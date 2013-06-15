package org.elasticsearch.plugin.river.redis;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Stephen Samuel
 */
public class RedisIndexerTest {

    Client client = mock(Client.class);

    @Test
    public void messageIsIndexedUsingTheChannelAndMessageBody() throws InterruptedException {

        RedisIndexer indexer = new RedisIndexer(client, "myindex", false, "mf");

        IndexRequestBuilder builder = mock(IndexRequestBuilder.class);
        Mockito.when(client.prepareIndex("myindex", "chan4")).thenReturn(builder);
        Mockito.when(builder.setSource(Matchers.anyString())).thenReturn(builder);

        Thread thread = new Thread(indexer);
        thread.start();
        indexer.index("chan4", "msg");
        indexer.shutdown();
        thread.join();

        verify(client).prepareIndex("myindex", "chan4");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(builder).setSource(captor.capture());
        assertTrue(captor.getValue().matches("\\{\"mf\":\"msg\",\"timestamp\":\\d+\\}"));
    }

    @Test
    public void messageIsIndexedAsJsonWhenSpecified() throws InterruptedException {

        RedisIndexer indexer = new RedisIndexer(client, "myindex", true, "mf");

        IndexRequestBuilder builder = mock(IndexRequestBuilder.class);
        Mockito.when(client.prepareIndex("myindex", "chan4")).thenReturn(builder);
        Mockito.when(builder.setSource(Matchers.anyString())).thenReturn(builder);

        Thread thread = new Thread(indexer);
        thread.start();
        indexer.index("chan4", "{ some raw message }");
        indexer.shutdown();
        thread.join();

        verify(client).prepareIndex("myindex", "chan4");
        verify(builder).setSource("{ some raw message }");
    }

    @Test
    public void shutdownKillsThread() throws InterruptedException {
        RedisIndexer indexer = new RedisIndexer(client, "myindex", false, "mf");
        Thread thread = new Thread(indexer);
        thread.start();
        indexer.shutdown();
        thread.join();
    }
}
