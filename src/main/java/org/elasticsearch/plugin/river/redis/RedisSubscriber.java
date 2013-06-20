package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.river.RiverSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

/**
 * @author Stephen Samuel
 */
public class RedisSubscriber extends JedisPubSub {

    private static Logger logger = LoggerFactory.getLogger(RedisSubscriber.class);
    final RedisIndexer indexer;

    public RedisSubscriber(RiverSettings settings, RedisIndexer indexer) {
        this.indexer = indexer;
        EsExecutors.daemonThreadFactory(settings.globalSettings(), "redis_indexer").newThread(indexer).start();
    }

    @Override
    public void onMessage(String channel, String message) {
        logger.debug("Message received [channel={} msg={}]", channel, message);
        indexer.index(channel, message);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        logger.debug("Message received [channel={} msg={}]", channel, message);
        indexer.index(channel, message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        indexer.shutdown();
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        indexer.shutdown();
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
    }
}

