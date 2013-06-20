package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.util.concurrent.EsExecutors;
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

import static org.elasticsearch.common.xcontent.support.XContentMapValues.*;

/**
 * @author Stephen Samuel
 */
public class RedisDriver extends AbstractRiverComponent implements River {

    static final int DEFAULT_REDIS_PORT = 6379;
    static final String DEFAULT_REDIS_INDEX = "redis-index";
    static final String DEFAULT_REDIS_MESSAGE_FIELD = "message";
    static final String DEFAULT_REDIS_CHANNELS = "elasticsearch";
    static final String DEFAULT_REDIS_HOSTNAME = "localhost";
    static final String DEFAULT_REDIS_KEYS = "";

    private static Logger logger = LoggerFactory.getLogger(RedisSubscriber.class);

    private final String hostname;
    private final String password;
    private final String index;
    private final String[] channels;
    private final String[] keys;
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

        hostname = nodeStringValue(extractValue("redis.hostname", settings.settings()), DEFAULT_REDIS_HOSTNAME);
        port = nodeIntegerValue(extractValue("redis.port", settings.settings()), DEFAULT_REDIS_PORT);
        String k = nodeStringValue(extractValue("redis.keys", settings.settings()), null);
        keys = k == null ? new String[0] : k.split(",");
        channels = nodeStringValue(extractValue("redis.channels", settings.settings()), DEFAULT_REDIS_CHANNELS).split(",");
        database = nodeIntegerValue(extractValue("redis.database", settings.settings()), 0);
        password = nodeStringValue(extractValue("redis.password", settings.settings()), null);
        messageField = nodeStringValue(extractValue("redis.messageField", settings.settings()), DEFAULT_REDIS_MESSAGE_FIELD);
        json = nodeBooleanValue(extractValue("redis.json", settings.settings()), false);
        index = nodeStringValue(extractValue("index.name", settings.settings()), DEFAULT_REDIS_INDEX);

        logger.debug("Redis settings [hostname={}, port={}, database={}]", new Object[]{hostname, port, database});
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

    public String[] getKeys() {
        return keys;
    }

    public String getIndex() {
        return index;
    }

    public boolean isJson() {
        return json;
    }
}

