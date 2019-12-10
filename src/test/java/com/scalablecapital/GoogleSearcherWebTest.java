package com.scalablecapital;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collection;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpClientHolder.class)
@PowerMockIgnore("javax.net.ssl.*")
public class GoogleSearcherWebTest extends BasicWebTest {

    @Test
    public void testGoogleSearch() throws Exception {

        GoogleSearcher googleSearcher = new GoogleSearcher();
        when(pageDownloader.downloadPage("http://www.google.com/search?&ie=utf-8&oe=utf-8&q=stackoverflow", HttpClientHolder.getInstance().getHttpAsyncClient())).thenReturn(Optional.of(getFileFromResourceFolder("stackoverflowgoogleresult.html")));
        Collection<String> resultLinks = googleSearcher.findMainResultLinks("stackoverflow");
        // the result from mocked page should be
        // [https://ru.stackoverflow.com/,
        // https://ru.wikipedia.org/wiki/Stack_Overflow,
        // https://stackoverflow.blog/,
        // https://stackoverflow.blog/2019/07/18/building-community-inclusivity-stack-overflow/,
        // https://twitter.com/stackoverflow,
        // https://www.stackoverflowbusiness.com/advertising,
        // https://www.stackoverflowbusiness.com/]
        assertThat(resultLinks, hasSize(7));
        assertThat(resultLinks, hasItems("https://stackoverflow.blog/",
                "https://ru.wikipedia.org/wiki/Stack_Overflow",
                "https://stackoverflow.blog/",
                "https://stackoverflow.blog/2019/07/18/building-community-inclusivity-stack-overflow/",
                "https://twitter.com/stackoverflow",
                "https://www.stackoverflowbusiness.com/advertising",
                "https://www.stackoverflowbusiness.com/"));
    }

}
