package com.scalablecapital;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class GoogleSearcherWebTest extends TestSetUp {


    private GoogleSearcher googleSearcher = new GoogleSearcher(pageDownloaderMock);

    @Test
    public void testGoogleSearchParser() throws Exception {

        List<String> resultsForStackOverFlowGoogleList = Arrays.asList("https://ru.stackoverflow.com/",
                "https://ru.wikipedia.org/wiki/Stack_Overflow",
                "https://stackoverflow.blog/",
                "https://stackoverflow.blog/newsletter/",
                "https://twitter.com/stackoverflow",
                "https://www.linkedin.com/company/stack-overflow",
                "https://www.stackoverflowbusiness.com/");

        when(pageDownloaderMock.downloadPages(Collections.singletonList("http://www.google.com/search?&ie=utf-8&oe=utf-8&q=stackoverflow")))
                .thenReturn(Collections.singletonList(getFileFromResourceFolder("StackOverFlowSearchResult.html")));

        assertThat(googleSearcher.findMainResultLinks("stackoverflow"), hasSize(7));

        assertThat(googleSearcher.findMainResultLinks("stackoverflow"), equalTo(resultsForStackOverFlowGoogleList));

    }

}
