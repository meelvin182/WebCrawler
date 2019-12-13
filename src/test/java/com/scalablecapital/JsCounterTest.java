package com.scalablecapital;

import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class JsCounterTest extends TestSetUp {

    private JsCounter jsCounter = new JsCounter(storage, pageDownloaderMock);

    private List<String> resultsForStackOverFlowGoogleList;

    @Before
    public void init() throws URISyntaxException {

        List<String> htmlsForStackOverFlowGoogle = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            htmlsForStackOverFlowGoogle.add(getFileFromResourceFolder("StackOverFlowGoogle" + i + ".html"));
        }

        resultsForStackOverFlowGoogleList = Arrays.asList("https://ru.stackoverflow.com/",
                "https://ru.wikipedia.org/wiki/Stack_Overflow",
                "https://stackoverflow.blog/",
                "https://stackoverflow.blog/2019/07/18/building-community-inclusivity-stack-overflow/",
                "https://twitter.com/stackoverflow",
                "https://www.stackoverflowbusiness.com/advertising",
                "https://www.stackoverflowbusiness.com/");

        when(pageDownloaderMock.downloadPages(resultsForStackOverFlowGoogleList)).thenReturn(htmlsForStackOverFlowGoogle);

    }

    @Test
    public void testDownloadAndCountJsLibs() throws URISyntaxException {

        jsCounter.downloadAndCountJsLibs(resultsForStackOverFlowGoogleList);

        System.out.println(jsCounter.getJsMapStorage());

        assertThat(jsCounter.getJsMapStorage().getStore(), aMapWithSize(30));
    }

    @Test
    public void testThatTop5ReturnsFive() {
        jsCounter.downloadAndCountJsLibs(resultsForStackOverFlowGoogleList);
        assertThat(jsCounter.getJsMapStorage().getTop(),hasSize(5));
        assertThat(jsCounter.getJsMapStorage().getTop(), hasItem("js='https://stats.wp.com/e-201950.js\t=\t2\n"));
    }
}