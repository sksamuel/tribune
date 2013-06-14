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
        return "river-rabbitmq";
    }

    @Override
    public String description() {
        return "River RabbitMQ Plugin";
    }

    public void onModule(RiversModule module) {
        module.registerRiver("rabbitmq", RedisRiverModule.class);
    }
}
