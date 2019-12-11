package com.scalablecapital;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class JsCounterTest extends BasicWebTest {

    private JsCounter jsCounter = new JsCounter(storage, pageDownloaderMock);

    @Test
    public void testDownloadAndCountJsLibs() throws URISyntaxException {

        List<String> htmlsForStackOverFlowGoogle = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            htmlsForStackOverFlowGoogle.add(getFileFromResourceFolder("StackOverFlowGoogle" + i + ".html"));
        }


        List<String> resultsForStackOverFlowGoogleList = Arrays.asList("https://ru.stackoverflow.com/",
                "https://ru.wikipedia.org/wiki/Stack_Overflow",
                "https://stackoverflow.blog/",
                "https://stackoverflow.blog/2019/07/18/building-community-inclusivity-stack-overflow/",
                "https://twitter.com/stackoverflow",
                "https://www.stackoverflowbusiness.com/advertising",
                "https://www.stackoverflowbusiness.com/");


        when(pageDownloaderMock.downloadPages(resultsForStackOverFlowGoogleList)).thenReturn(htmlsForStackOverFlowGoogle);

        jsCounter.downloadAndCountJsLibs(resultsForStackOverFlowGoogleList);

        System.out.println(jsCounter.getJsMapStorage());

        assertThat(jsCounter.getJsMapStorage(), aMapWithSize(25));

        assertThat(jsCounter.getTopFive(),hasSize(5));
    }
}