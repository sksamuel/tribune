package org.elasticsearch.plugin.river.redis;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Stephen Samuel
 */
public class RedisSubscriptionTaskTest {

    JedisPool pool = mock(JedisPool.class);
    RedisSubscriber subscriber = mock(RedisSubscriber.class);
    String[] channels = {"a", "b"};
    Jedis jedis = mock(Jedis.class);

    {
        when(pool.getResource()).thenReturn(jedis);
    }

    @Test
    public void whenRunningTheSubscriptionUsesTheGivenChannels() {
        RedisSubscriptionTask task = new RedisSubscriptionTask(pool, subscriber, channels);
        task.run();
        verify(jedis).subscribe(subscriber, channels);
    }

    @Test
    public void taskBlocksUntilSubscribeReturns() throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                latch.await();
                return null;
            }
        }).when(jedis).subscribe(subscriber, channels);

        RedisSubscriptionTask task = new RedisSubscriptionTask(pool, subscriber, channels);
        Thread thread = new Thread(task);
        thread.start();

        Thread.sleep(200);
        assertTrue(thread.isAlive());
        latch.countDown();
        Thread.sleep(200);
        assertFalse(thread.isAlive());
    }

    @Test
    public void resourceIsReturnedWhenSubscribeReturns() {
        RedisSubscriptionTask task = new RedisSubscriptionTask(pool, subscriber, channels);
        task.run();
        verify(pool).returnResource(jedis);
    }
}
