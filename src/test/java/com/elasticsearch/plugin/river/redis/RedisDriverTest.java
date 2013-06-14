package com.elasticsearch.plugin.river.redis;

import org.elasticsearch.client.Client;
import org.elasticsearch.plugin.river.redis.RedisDriver;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Stephen Samuel
 */
public class RedisDriverTest {

    private RiverName name = new RiverName("type", "name");
    private Map<String, Object> map = new HashMap<String, Object>();
    RiverSettings settings = new RiverSettings(null, map);
    private Client client = Mockito.mock(Client.class);
    private RedisDriver driver = new RedisDriver(name, settings, "myindex", client);

    @Test
    public void closeOnUnsubscribedSubscriberDoesNotThrowException() {

    }

    @Test
    public void unspecifiedHostnameThrowsException() {

    }

    @Test
    public void unspecifiedChannelsThrowsException() {

    }
}
