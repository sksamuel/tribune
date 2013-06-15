package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;
import org.junit.Test;
import org.mockito.Mockito;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Stephen Samuel
 */
public class RedisDriverTest {

    RiverName name = new RiverName("type", "name");
    Map<String, Object> map = new HashMap<String, Object>();
    RiverSettings settings = new RiverSettings(mock(Settings.class), map);
    Client client = mock(Client.class);
    RedisDriver driver = new RedisDriver(name, settings, "myindex", client);
    Jedis jedis = mock(Jedis.class);

    @Test
    public void settingsAreTakenFromSettingsRedisJsonObjectIfSet() {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> redis = new HashMap<String, Object>();
        map.put("redis", redis);
        redis.put("hostname", "myhost");
        redis.put("port", "12345");
        redis.put("channels", "a,b,c");
        redis.put("index", "myindex");
        redis.put("messageField", "mf");
        redis.put("password", "letmein");
        redis.put("database", "3");
        redis.put("json", "true");
        RiverSettings settings = new RiverSettings(mock(Settings.class), map);
        RedisDriver driver = new RedisDriver(name, settings, "_river", client);
        assertEquals("myhost", driver.getHostname());
        assertEquals(12345, driver.getPort());
        assertEquals("myindex", driver.getIndex());
        assertEquals("mf", driver.getMessageField());
        assertArrayEquals(new String[]{"a", "b", "c"}, driver.getChannels());
        assertEquals("letmein", driver.getPassword());
        assertEquals(3, driver.getDatabase());
        assertEquals(true, driver.isJson());
    }

    @Test
    public void settingsAreDefaultsIfJsonObjectNotSet() {
        Map<String, Object> map = new HashMap<String, Object>();
        RiverSettings settings = new RiverSettings(mock(Settings.class), map);
        RedisDriver driver = new RedisDriver(name, settings, "myindex", client);
        assertEquals(RedisDriver.DEFAULT_REDIS_HOSTNAME, driver.getHostname());
        assertEquals(RedisDriver.DEFAULT_REDIS_PORT, driver.getPort());
        assertEquals(RedisDriver.DEFAULT_REDIS_MESSAGE_FIELD, driver.getMessageField());
        assertArrayEquals(new String[]{RedisDriver.DEFAULT_REDIS_CHANNELS}, driver.getChannels());
        assertEquals(RedisDriver.DEFAULT_REDIS_INDEX, driver.getIndex());
        assertEquals(0, driver.getDatabase());
        assertEquals(false, driver.isJson());
    }

    @Test
    public void closeOnNullSubscriberDoesNotThrowException() {
        driver.subscriber = null;
        driver.close();
    }

    @Test
    public void closeOnUnsubscribedSubscriberDoesNotInvokeUnsub() {
        driver.subscriber = mock(RedisSubscriber.class);
        when(driver.subscriber.isSubscribed()).thenReturn(false);
        driver.close();
        Mockito.verify(driver.subscriber, never()).unsubscribe();
    }

    @Test
    public void indexIsCreatedOnStartup() {
        AdminClient adminClient = mock(AdminClient.class);
        when(client.admin()).thenReturn(adminClient);
        IndicesAdminClient indices = mock(IndicesAdminClient.class);
        when(adminClient.indices()).thenReturn(indices);
        driver.start();
        Mockito.verify(indices).prepareCreate("redis-index");
    }

    @Test
    public void whenAnExceptionIsThrownInIndexSetupThenRiverStops() {
        when(client.admin()).thenThrow(new RuntimeException());
        driver.start();
        assertNull(driver.subscriber);
    }

    @Test
    public void whenStartingTheSubscriptionThreadIsStarted() {

        AdminClient adminClient = mock(AdminClient.class);
        when(client.admin()).thenReturn(adminClient);
        IndicesAdminClient indices = mock(IndicesAdminClient.class);
        when(adminClient.indices()).thenReturn(indices);
        CreateIndexRequestBuilder builder = mock(CreateIndexRequestBuilder.class);
        when(indices.prepareCreate("redis-index")).thenReturn(builder);
        ListenableActionFuture future = mock(ListenableActionFuture.class);
        when(builder.execute()).thenReturn(future);

        driver.subscriber = null;
        driver.start();
        assertNotNull(driver.subscriber);
        assertNotNull(driver.thread);
    }
}
