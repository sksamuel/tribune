package org.elasticsearch.plugin.river.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * @author Kyle Liu
 */
class RedisMonitor implements Runnable {
    private static String POISON_KEY = "elasticsearch:river:redis:poison:pill";
    private static String POISON_MSG = "shutdown";

    private static Logger logger = LoggerFactory.getLogger(RedisMonitor.class);

    private final RedisIndexer indexer;
    private final JedisPool pool;
    private final String[] keys;

    public RedisMonitor(JedisPool pool, RedisIndexer indexer, String[] keys) {
        this.pool = pool;
        this.indexer = indexer;
        this.keys = new String[keys.length + 1];
        System.arraycopy(keys, 0, this.keys, 0, keys.length);
        this.keys[keys.length] = POISON_KEY;
    }

    public void shutdown() {
        try {
            Jedis jedis = pool.getResource();
            jedis.lpush(POISON_KEY, POISON_MSG);
            pool.returnResource(jedis);
        } catch (Exception e) {
            // Do nothing!
        }
    }

    @Override
    public void run() {
        try {
            Jedis jedis = pool.getResource();
            logger.debug("Listen keys [{}]", keys);
            loop(jedis);
            logger.debug("Listen completed; closing down");
            pool.returnResource(jedis);
        } catch (Exception e) {
            logger.warn("Error running monitor task {}", e);
        }
    }

    private void loop(Jedis jedis) {
        while (true) {
            List<String> response = jedis.blpop(0, keys);
            if (response != null) {
                String key = response.get(0);
                String msg = response.get(1);
                if (key == POISON_KEY && msg == POISON_MSG) {
                    logger.debug("Poison pill eaten - shutdown edis monitor.");
                    break;
                } else {
                    indexer.index(key, msg);
                }
            }
        }
    }
}
