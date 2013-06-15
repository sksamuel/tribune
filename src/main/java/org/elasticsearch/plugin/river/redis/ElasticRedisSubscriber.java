package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.river.RiverSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Stephen Samuel
 */
public class ElasticRedisSubscriber extends JedisPubSub {

    private static Logger logger = LoggerFactory.getLogger(ElasticRedisSubscriber.class);
    final RedisIndexer indexer;

    public ElasticRedisSubscriber(RiverSettings settings, Client client, String index, String messageField) {
        indexer = new RedisIndexer(client, index, messageField);
        EsExecutors.daemonThreadFactory(settings.globalSettings(), "redis_indexer").newThread(indexer);
    }

    public ElasticRedisSubscriber(RiverSettings settings, RedisIndexer indexer) {
        this.indexer = indexer;
        EsExecutors.daemonThreadFactory(settings.globalSettings(), "redis_indexer").newThread(indexer);
    }

    @Override
    public void onMessage(String channel, String message) {
        logger.debug("Message - [channel {} msg: {}]", channel, message);
        indexer.index(channel, message);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
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
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
    }
}

class RedisIndexer implements Runnable {

    private static final String[] POISON = new String[]{"jekkyl", "hyde"};
    private static Logger logger = LoggerFactory.getLogger(RedisIndexer.class);

    private final Client client;
    private final String index;
    private final String messageField;
    private BlockingQueue<String[]> queue = new LinkedBlockingQueue<String[]>();

    public RedisIndexer(Client client, String index, String messageField) {
        this.client = client;
        this.index = index;
        this.messageField = messageField;
    }

    public void index(String channel, String message) {
        queue.offer(new String[]{channel, message}); // pragmatically we're not bounded
    }

    public void shutdown() {
        queue.offer(POISON);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String[] msg = queue.take();
                if (msg == POISON)
                    return;
                try {
                    String json = String.format("{\"%s\" : \"%s\"}", messageField, msg[1]);
                    client.prepareIndex(index, msg[0]).setSource(json).execute().actionGet();
                } catch (Exception e) {
                    logger.warn("{}", e);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }
}