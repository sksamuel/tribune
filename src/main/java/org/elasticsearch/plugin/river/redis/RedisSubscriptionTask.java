package org.elasticsearch.plugin.river.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author Stephen Samuel
 */
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
