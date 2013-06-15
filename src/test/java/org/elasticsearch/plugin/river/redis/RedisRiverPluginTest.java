package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.river.RiversModule;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Stephen Samuel
 */
public class RedisRiverPluginTest {

    @Test
    public void thatModuleIsRegistered() {
        RiversModule module = mock(RiversModule.class);
        RedisRiverPlugin plugin = new RedisRiverPlugin();
        plugin.onModule(module);
        verify(module).registerRiver("redis", RedisRiverModule.class);
    }
}
