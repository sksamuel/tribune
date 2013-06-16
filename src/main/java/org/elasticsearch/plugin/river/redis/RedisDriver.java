package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.river.AbstractRiverComponent;
import org.elasticsearch.river.River;
import org.elasticsearch.river.RiverIndexName;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Stephen Samuel
 */
public class RedisDriver extends AbstractRiverComponent implements River {

    static final int DEFAULT_REDIS_PORT = 6379;
    static final String DEFAULT_REDIS_INDEX = "redis-index";
    static final String DEFAULT_REDIS_MESSAGE_FIELD = "message";
    static final String DEFAULT_REDIS_CHANNELS = "elasticsearch";
    static final String DEFAULT_REDIS_HOSTNAME = "localhost";

    private static Logger logger = LoggerFactory.getLogger(RedisSubscriber.class);

    private final String hostname;
    private final String password;
    private final String index;
    private final String[] channels;
    private final int port;
    private final int database;
    private final String messageField;
    private final boolean json;
    private final RedisIndexer indexer;

    final RiverSettings settings;
    final Client client;
    RedisSubscriber subscriber;
    Thread thread;

    @Inject
    public RedisDriver(RiverName riverName, RiverSettings settings, @RiverIndexName final String riverIndexName, final Client client) {
        super(riverName, settings);
        this.settings = settings;
        this.client = client;

        hostname = XContentMapValues.nodeStringValue(settings.settings().get("redis.hostname"), DEFAULT_REDIS_HOSTNAME);
        port = XContentMapValues.nodeIntegerValue(settings.settings().get("redis.port"), DEFAULT_REDIS_PORT);
        channels = XContentMapValues.nodeStringValue(settings.settings().get("redis.channels"), DEFAULT_REDIS_CHANNELS).split(",");
        database = XContentMapValues.nodeIntegerValue(settings.settings().get("redis.database"), 0);
        password = XContentMapValues.nodeStringValue(settings.settings().get("redis.password"), null);
        messageField = XContentMapValues.nodeStringValue(settings.settings().get("redis.messageField"), DEFAULT_REDIS_MESSAGE_FIELD);
        json = XContentMapValues.nodeBooleanValue(settings.settings().get("redis.json"), false);
        index = XContentMapValues.nodeStringValue(settings.settings().get("index.name"), DEFAULT_REDIS_INDEX);

        logger.debug("Redis settings [hostname={}, port={}, channels={}, database={}]", new Object[]{hostname,
                port,
                channels,
                database});
        logger.debug("River settings [indexName={}, channels={}, messageField={}, json={}]", new Object[]{index,
                channels,
                messageField,
                json});

        indexer = new RedisIndexer(client, index, json, messageField);
    }

    @Override
    public void start() {
        logger.info("Starting redis subscriber");

        try {
            ensureIndexCreated();
        } catch (Exception e) {
            logger.debug("Could not create index. Disabling river");
            return;
        }

        try {

            subscriber = new RedisSubscriber(settings, indexer);
            startSubscriberThread(subscriber);

        } catch (Exception e) {
            logger.debug("Could not create redis pool. Disabling river");
        }
    }

    void startSubscriberThread(RedisSubscriber subscriber) {
        // this has to run on a separate thread because redis subscription method blocks
        final JedisPool pool = getJedisPool();
        final RedisSubscriptionTask task = new RedisSubscriptionTask(pool, subscriber, channels);
        thread = EsExecutors.daemonThreadFactory(settings.globalSettings(), "redis_subscription").newThread(task);
        thread.start();
    }

    JedisPool getJedisPool() {
        return new JedisPool(new JedisPoolConfig(), hostname, port, 0, password, database);
    }

    void ensureIndexCreated() {
        try {
            logger.debug("Creating index [{}]...", index);
            client.admin().indices().prepareCreate(index).execute().actionGet();
            logger.error("... created");
        } catch (Exception e) {
            //noinspection ThrowableResultOfMethodCallIgnored,StatementWithEmptyBody
            if (ExceptionsHelper.unwrapCause(e) instanceof IndexAlreadyExistsException) {
                logger.debug("... index already exists");
            } else {
                logger.error("... error {}", e);
                throw e;
            }
        }
    }

    @Override
    public void close() {
        if (subscriber != null && subscriber.isSubscribed())
            subscriber.unsubscribe();
    }

    public String getPassword() {
        return password;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public int getDatabase() {
        return database;
    }

    public String getMessageField() {
        return messageField;
    }

    public String[] getChannels() {
        return channels;
    }

    public String getIndex() {
        return index;
    }

    public boolean isJson() {
        return json;
    }
}

class RedisSubscriptionTask implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RedisSubscriptionTask.class);

    private final RedisSubscriber subscriber;
    private final JedisPool pool;
    private final String[] channels;

    public RedisSubscriptionTask(JedisPool pool, RedisSubscriber subscriber, String[] channels) {
        this.pool = pool;
        this.subscriber = subscriber;
        this.channels = channels;
    }

    @Override
    public void run() {
        try {
            Jedis jedis = pool.getResource();
            logger.debug("Subscribing to channels [{}]", channels);
            jedis.subscribe(subscriber, channels);
            logger.debug("Subscribe completed; closing down");
            pool.returnResource(jedis);
        } catch (Exception e) {
            logger.warn("Error running subscriber task {}", e);
        }
    }
}