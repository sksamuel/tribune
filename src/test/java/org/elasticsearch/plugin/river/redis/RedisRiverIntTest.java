package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.support.QuerySourceBuilder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.UUID;

import static org.elasticsearch.client.Requests.clusterHealthRequest;
import static org.elasticsearch.client.Requests.countRequest;
import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;
import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;
import static org.elasticsearch.index.query.QueryBuilders.queryString;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Stephen Samuel
 */
public class RedisRiverIntTest {

    private static Logger logger = LoggerFactory.getLogger(RedisRiverIntTest.class);

    private Node node;
    private final String river = "redis-river-" + UUID.randomUUID().toString();
    String index;
    String channel;
    private Jedis jedis;

    public void shutdown() {
        logger.debug("Shutdown elastic...");
        node.close();
        logger.debug("...goodbye elastic");
        logger.debug("Quitting jedis...");
        jedis.quit();
        logger.debug("...I'm a quitter");
    }

    @Before
    public void setupElasticAndRedis() throws Exception {

        Settings globalSettings = settingsBuilder().loadFromClasspath("settings.yml").build();
        String json = copyToStringFromClasspath("/simple-redis-river.json");
        Settings riverSettings = settingsBuilder().loadFromSource(json).build();

        index = riverSettings.get("index.name");
        channel = riverSettings.get("redis.channels").split(",")[0];

        String hostname = riverSettings.get("redis.hostname");
        int port = riverSettings.getAsInt("redis.port", 6379);
        logger.debug("Connecting to Redis [hostname={} port={}]...", hostname, port);
        jedis = new Jedis(hostname, port, 0);
        logger.debug("... connected");

        logger.debug("Starting local elastic...");
        node = nodeBuilder().local(true).settings(globalSettings).node();

        logger.info("Create river [{}]", river);
        node.client().prepareIndex("_river", river, "_meta").setSource(json).execute().actionGet();

        logger.debug("Running Cluster Health");
        ClusterHealthResponse clusterHealth = node.client().admin().cluster()
                .health(clusterHealthRequest().waitForGreenStatus())
                .actionGet();
        logger.info("Done Cluster Health, status " + clusterHealth.getStatus());

        GetResponse response = node.client().prepareGet("_river", river, "_meta").execute().actionGet();
        assertTrue(response.isExists());

        logger.debug("...elasticized ok");
    }

    @Test
    public void connectRiverAndSendMessages() throws InterruptedException {

        Thread.sleep(1000);

        String field = "content";
        String msg = "sammy";

        logger.debug("Publishing message [channel={}, msg={}]", channel, msg);
        jedis.publish(channel, msg);

        Thread.sleep(1000);
        refreshIndex();

        QuerySourceBuilder builder = new QuerySourceBuilder();
        builder.setQuery(queryString(field + ":" + msg));

        logger.debug("Counting [index={}, type={}, field={}, msg={}]", new Object[]{index, channel, field, msg});
        CountResponse resp = node.client().count(countRequest(index).types(channel).source(builder)).actionGet();
        assertEquals(1, resp.getCount());

        msg = "coldplay";

        logger.debug("Counting [index={}, type={}, field={}, msg={}]", new Object[]{index, channel, field, msg});
        resp = node.client().count(countRequest(index).types(channel).source(builder)).actionGet();
        assertEquals(0, resp.getCount());

        logger.debug("Publishing message [channel={}]", channel);
        jedis.publish(channel, msg);

        Thread.sleep(1000);
        refreshIndex();

        logger.debug("Counting [index={}, type={}, field={}, msg={}]", new Object[]{index, channel, field, msg});
        resp = node.client().count(countRequest(index).types(channel).source(builder)).actionGet();
        assertEquals(1, resp.getCount());

        shutdown();
    }

    private void refreshIndex() {
        node.client().admin().indices().prepareRefresh(index).execute().actionGet();
    }
}
