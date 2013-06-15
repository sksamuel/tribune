package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.river.RiversModule;

/**
 * @author Stephen Samuel
 */
public class RedisRiverPlugin extends AbstractPlugin {

    @Inject
    public RedisRiverPlugin() {
    }

    @Override
    public String name() {
        return "river-redis";
    }

    @Override
    public String description() {
        return "River Redis Plugin";
    }

    public void onModule(RiversModule module) {
        module.registerRiver("redis", RedisRiverModule.class);
    }
}
