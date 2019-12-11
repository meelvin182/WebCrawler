package com.scalablecapital;

import org.junit.Test;
import org.mockito.Mockito;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;


public class BasicWebTest {

    ConcurrentHashMap<String, LongAdder> storage = new ConcurrentHashMap<>();

    protected HttpClient httpClientMock = Mockito.mock(HttpClient.class);
    protected PageDownloader pageDownloaderMock = Mockito.mock(PageDownloader.class);

    protected GoogleSearcher googleSearcherMock = Mockito.mock(GoogleSearcher.class);

    protected String getFileFromResourceFolder(String path) throws URISyntaxException {

        try {
            return new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(path).toURI())));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("file with name "+path+" not found");
        }
        return null;
    }

    @Test
    public void name() {
        System.out.println(pageDownloaderMock.downloadPages(Collections.singletonList("google.com")));

    }
}
