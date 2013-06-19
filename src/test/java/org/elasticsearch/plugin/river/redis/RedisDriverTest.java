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

import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;
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

    @Test
    public void settingsAreTakenFromSettingsRedisJsonObjectIfSet() {

        Settings globalSettings = settingsBuilder().loadFromClasspath("settings.yml").build();
        Map map = settingsBuilder().loadFromClasspath("dummy-river.json").build().getAsMap();
        RiverSettings riverSettings = new RiverSettings(globalSettings, map);
        RedisDriver driver = new RedisDriver(name, riverSettings, "myindex", client);

        assertEquals("superhost", driver.getHostname());
        assertEquals(9865, driver.getPort());
        assertEquals("reddyredindex", driver.getIndex());
        assertArrayEquals(new String[]{"key1", "key2*"}, driver.getKeys());
        assertEquals("putithere", driver.getMessageField());
        assertArrayEquals(new String[]{"c1", "c2"}, driver.getChannels());
        assertEquals("letmein", driver.getPassword());
        assertEquals(16, driver.getDatabase());
        assertEquals(true, driver.isJson());
    }

    @Test
    public void settingsAreDefaultsIfJsonObjectNotSet() {

        Map<String, Object> map = new HashMap<String, Object>();
        RiverSettings settings = new RiverSettings(mock(Settings.class), map);
        RedisDriver driver = new RedisDriver(name, settings, "myindex", client);

        assertEquals(RedisDriver.DEFAULT_REDIS_HOSTNAME, driver.getHostname());
        assertEquals(0, driver.getKeys().length);
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
