package com.scalablecapital;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpClientHolder.class)
@PowerMockIgnore("javax.net.ssl.*")
public class JsCounterTest extends BasicWebTest {

    private JsCounter jsCounter = new JsCounter(storage);

    @Test
    public void testGetAndCountJsLibs() throws Exception {
        when(pageDownloader.downloadPage("http://www.google.com/search?&ie=utf-8&oe=utf-8&q=stackoverflow", HttpClientHolder.getInstance().getHttpAsyncClient())).thenReturn(Optional.of(getFileFromResourceFolder("stackoverflowgoogleresult.html")));
        when(googleSearcher.findMainResultLinks("http://www.google.com/search?&ie=utf-8&oe=utf-8&q=stackoverflow")).thenReturn(Collections.singleton(getFileFromResourceFolder("stackoverflowmain.html")));
        jsCounter.getAndCountJsLibs("https://ru.stackoverflow.com/",HttpClientHolder.getInstance().getHttpAsyncClient());
        assertThat(jsCounter.getTopFive(),hasSize(2));
    }
}