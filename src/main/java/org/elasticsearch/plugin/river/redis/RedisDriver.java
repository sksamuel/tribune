package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.client.Client;
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

import java.util.Map;

/**
 * @author Stephen Samuel
 */
public class RedisDriver extends AbstractRiverComponent implements River {

    static final int DEFAULT_REDIS_PORT = 6379;
    static final String DEFAULT_REDIS_INDEX = "redisindex";
    static final String DEFAULT_REDIS_MESSAGE_FIELD = "message";
    static final String[] DEFAULT_REDIS_CHANNELS = {"*"};
    static final String DEFAULT_REDIS_HOSTNAME = "localhost";

    private static Logger logger = LoggerFactory.getLogger(ElasticRedisSubscriber.class);

    final String hostname;
    final String password;
    final String[] channels;
    final int port;
    final int database;
    final String messageField;
    final RiverSettings settings;
    final String riverIndexName;
    Client client;
    ElasticRedisSubscriber subscriber;

    public RedisDriver(RiverName riverName, RiverSettings settings, @RiverIndexName final String riverIndexName, final Client client) {
        super(riverName, settings);
        this.settings = settings;
        this.riverIndexName = riverIndexName;
        this.client = client;

        if (settings.settings().containsKey("redis")) {
            Map<String, Object> redisSettings = (Map<String, Object>) settings.settings().get("redis");
            hostname = XContentMapValues.nodeStringValue(redisSettings.get("hostname"), DEFAULT_REDIS_HOSTNAME);
            port = XContentMapValues.nodeIntegerValue(redisSettings.get("port"), DEFAULT_REDIS_PORT);
            channels = XContentMapValues.nodeStringValue(redisSettings.get("channels"), "*").split(",");
            database = XContentMapValues.nodeIntegerValue(redisSettings.get("database"), 0);
            password = XContentMapValues.nodeStringValue(redisSettings.get("password"), null);
            messageField = XContentMapValues.nodeStringValue(redisSettings.get("messageField"), DEFAULT_REDIS_MESSAGE_FIELD);
        } else {
            hostname = DEFAULT_REDIS_HOSTNAME;
            port = DEFAULT_REDIS_PORT;
            channels = DEFAULT_REDIS_CHANNELS;
            database = 0;
            password = null;
            messageField = DEFAULT_REDIS_MESSAGE_FIELD;
        }

        logger.debug("Redis settings [hostname={}, port={}, channels={}, database={}]", new Object[]{hostname,
                port,
                channels,
                database});
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

            final JedisPool pool = getJedisPool();
            subscriber = new ElasticRedisSubscriber(settings, client, riverIndexName, messageField);
            final RedisSubscriptionThread subscriptionThread = new RedisSubscriptionThread(pool, subscriber, channels);
            Thread thread = EsExecutors.daemonThreadFactory(settings.globalSettings(), "redis_subscription").newThread(subscriptionThread);
            thread.start();

        } catch (Exception e) {
            logger.debug("Could not create redis pool. Disabling river");
        }
    }

    JedisPool getJedisPool() {
        return new JedisPool(new JedisPoolConfig(), hostname, port, 0, password, database);
    }

    void ensureIndexCreated() {
        try {
            client.admin().indices().prepareCreate(riverIndexName).execute().actionGet();
        } catch (Exception e) {
            //noinspection ThrowableResultOfMethodCallIgnored,StatementWithEmptyBody
            if (ExceptionsHelper.unwrapCause(e) instanceof IndexAlreadyExistsException) {
                // lucky us
            } else {
                throw e;
            }
        }
    }

    @Override
    public void close() {
        if (subscriber != null && subscriber.isSubscribed())
            subscriber.unsubscribe();
    }
}

class RedisSubscriptionThread implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RedisSubscriptionThread.class);

    private final ElasticRedisSubscriber subscriber;
    private final JedisPool pool;
    private final String[] channels;

    public RedisSubscriptionThread(JedisPool pool, ElasticRedisSubscriber subscriber, String[] channels) {
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
            logger.debug(">>> OH NOES Sub - " + e.getMessage());
        }
    }
}