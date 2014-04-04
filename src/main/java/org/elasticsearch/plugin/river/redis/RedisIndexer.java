package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Stephen Samuel
 */
class RedisIndexer implements Runnable {

    private static final String[] POISON = new String[]{"jekkyl", "hyde"};
    private static Logger logger = LoggerFactory.getLogger(RedisIndexer.class);

    private final Client client;
    private final String index;
    private final boolean json;
    private final String messageField;
    private BlockingQueue<String[]> queue = new LinkedBlockingQueue<>();

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
                    logger.debug("Indexing... [index={}, type={}, source={}]", new String[]{index, type, source});
                    client.prepareIndex(index, type).setSource(source).execute().actionGet();
                    logger.debug("...indexed");
                } catch (Exception e) {
                    logger.warn("{}", e);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    String getSource(String msg) throws IOException {
        if (json)
            return msg;
        else
            return XContentFactory.jsonBuilder().startObject().field(messageField, msg).field("timestamp",
                    System.currentTimeMillis()).endObject().string();
    }
}
