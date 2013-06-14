package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.river.River;

/**
 * @author Stephen Samuel
 */
public class RedisRiverModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(River.class).to(RedisDriver.class).asEagerSingleton();
    }
}