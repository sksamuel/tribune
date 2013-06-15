package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

/**
 * @author Stephen Samuel
 */
public class RedisDriverTest {

    RiverName name = new RiverName("type", "name");
    Map<String, Object> map = new HashMap<String, Object>();
    RiverSettings settings = new RiverSettings(mock(Settings.class), map);
    Client client = mock(Client.class);
    RedisDriver driver = new RedisDriver(name, settings, "myindex", client);

    @Test
    public void settingsAreTakenFromSettingsRedisJsonObjectIfSet() {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> redis = new HashMap<String, Object>();
        map.put("redis", redis);
        redis.put("hostname", "myhost");
        redis.put("port", "12345");
        redis.put("channels", "a,b,c");
        redis.put("index", "superindex");
        redis.put("messageField", "mf");
        RiverSettings settings = new RiverSettings(mock(Settings.class), map);
        RedisDriver driver = new RedisDriver(name, settings, "myindex", client);
        assertEquals("myhost", driver.hostname);
        assertEquals(12345, driver.port);
        assertEquals("myindex", driver.riverIndexName);
        assertEquals("mf", driver.messageField);
    }

    @Test
    public void settingsAreDefaultsIfJsonObjectNotSet() {
        Map<String, Object> map = new HashMap<String, Object>();
        RiverSettings settings = new RiverSettings(mock(Settings.class), map);
        RedisDriver driver = new RedisDriver(name, settings, "myindex", client);
        assertEquals(RedisDriver.DEFAULT_REDIS_HOSTNAME, driver.hostname);
        assertEquals(RedisDriver.DEFAULT_REDIS_PORT, driver.port);
        assertEquals(RedisDriver.DEFAULT_REDIS_MESSAGE_FIELD, driver.messageField);
        assertArrayEquals(RedisDriver.DEFAULT_REDIS_CHANNELS, driver.channels);
        assertEquals("myindex", driver.riverIndexName);
    }

    @Test
    public void closeOnNullSubscriberDoesNotThrowException() {
        driver.subscriber = null;
        driver.close();
    }

    @Test
    public void closeOnUnsubscribedSubscriberDoesNotInvokeUnsub() {
        driver.subscriber = mock(ElasticRedisSubscriber.class);
        Mockito.when(driver.subscriber.isSubscribed()).thenReturn(false);
        driver.close();
        Mockito.verify(driver.subscriber, never()).unsubscribe();
    }

    @Test
    public void indexIsCreatedOnStartup() {
        AdminClient adminClient = mock(AdminClient.class);
        Mockito.when(client.admin()).thenReturn(adminClient);
        IndicesAdminClient indices = mock(IndicesAdminClient.class);
        Mockito.when(adminClient.indices()).thenReturn(indices);
        driver.start();
        Mockito.verify(indices).prepareCreate("myindex");
    }

    @Test
    public void whenAnExceptionIsThrownInIndexSetupThenRiverStops() {
        driver.start();
    }
}
