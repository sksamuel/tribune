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
    private final boolean json;
    private final String messageField;
    private BlockingQueue<String[]> queue = new LinkedBlockingQueue<String[]>();

    public RedisIndexer(Client client, String index, boolean json, String messageField) {
        this.client = client;
        this.index = index;
        this.json = json;
        this.messageField = messageField;
    }

    public void index(String channel, String message) {
        logger.debug("Queuing... [channel={}, message={}]", channel, message);
        queue.offer(new String[]{channel, message}); // pragmatically we're not bounded
        logger.debug("... {} now queued", queue.size());
    }

    public void shutdown() {
        queue.offer(POISON);
    }

    @Override
    public void run() {
        logger.debug("Starting indexer");
        while (true) {
            try {
                String[] msg = queue.take();
                if (msg == POISON) {
                    logger.info("Poison pill eaten - shutting down subscriber thread");
                    logger.debug("Indexer shutdown");
                    return;
                }
                try {
                    String type = msg[0];
                    String source = getSource(msg[1]);
                    logger.debug("Preparing index... [index={}, type={}, source={}]", new String[]{index, type, source});
                    client.prepareIndex(index, type).setSource(source).execute().actionGet();
                    logger.debug("...indexed");
                } catch (Exception e) {
                    logger.warn("{}", e);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    String getSource(String msg) {
        if (json)
            return msg;
        else
            return String.format("{\"%s\" : \"%s\"}", messageField, msg);
    }
}