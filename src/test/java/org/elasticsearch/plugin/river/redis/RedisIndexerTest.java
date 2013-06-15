package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Stephen Samuel
 */
public class RedisIndexerTest {

    Client client = mock(Client.class);

    @Test
    public void messageIsIndexedUsingTheChannelAndMessageBody() throws InterruptedException {

        RedisIndexer indexer = new RedisIndexer(client, "myindex", "mf");

        IndexRequestBuilder builder = mock(IndexRequestBuilder.class);
        Mockito.when(client.prepareIndex("myindex", "chan4")).thenReturn(builder);
        Mockito.when(builder.setSource(Matchers.anyString())).thenReturn(builder);

        Thread thread = new Thread(indexer);
        thread.start();
        indexer.index("chan4", "msg");
        indexer.shutdown();
        thread.join();

        verify(client).prepareIndex("myindex", "chan4");
        verify(builder).setSource("{\"mf\" : \"msg\"}");
    }

    @Test
    public void shutdownKillsThread() throws InterruptedException {
        RedisIndexer indexer = new RedisIndexer(client, "myindex", "mf");
        Thread thread = new Thread(indexer);
        thread.start();
        indexer.shutdown();
        thread.join();
    }
}
