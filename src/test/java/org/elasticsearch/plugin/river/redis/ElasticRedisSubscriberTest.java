package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.river.RiverSettings;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Stephen Samuel
 */
public class ElasticRedisSubscriberTest {

    Client client = mock(Client.class);
    Map<String, Object> map = new HashMap<String, Object>();
    RiverSettings settings = new RiverSettings(mock(Settings.class), map);
    RedisIndexer indexer = mock(RedisIndexer.class);
    ElasticRedisSubscriber subscriber = new ElasticRedisSubscriber(settings, indexer);

    @Test
    public void indexerThreadIsShutdownWhenUnsubscribed() {
        subscriber.onUnsubscribe(null, 0);
        verify(indexer).shutdown();
    }

    @Test
    public void indexerIsInvokedWhenMessageReceived() {
        subscriber.onMessage("channel4", "supermsg");
        verify(indexer).index("channel4", "supermsg");
    }
}
