package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.river.RiverSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Stephen Samuel
 */
public class ElasticRedisSubscriber extends JedisPubSub {

    private static Logger logger = LoggerFactory.getLogger(ElasticRedisSubscriber.class);
    private final RedisIndexer indexer;

    public ElasticRedisSubscriber(RiverSettings settings, Client client, String index, String messageField) {
        indexer = new RedisIndexer(client, index, messageField);
        EsExecutors.daemonThreadFactory(settings.globalSettings(), "redis_indexer").newThread(indexer);
    }

    @Override
    public void onMessage(String channel, String message) {
        logger.debug("Message - [channel {} msg: {}]", channel, message);
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
        indexer.shutdown();
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {

    }
}

class RedisIndexer implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RedisIndexer.class);

    private final Client client;
    private final String index;
    private final String messageField;
    private volatile boolean running = true;
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
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                String[] msg = queue.take();
                try {
                    XContentBuilder src = XContentFactory.jsonBuilder().startObject().field(messageField, msg[1]).endObject();
                    client.prepareIndex(index, msg[0]).setSource(src).execute().actionGet();
                } catch (IOException e) {
                    logger.warn("{}", e);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }
}