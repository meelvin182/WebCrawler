package com.scalablecapital;

import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;


public class BasicWebTest {

    ConcurrentHashMap<String, LongAdder> storage = new ConcurrentHashMap<>();

    protected PageDownloader pageDownloader = Mockito.mock(PageDownloader.class);

    protected GoogleSearcher googleSearcher = Mockito.mock(GoogleSearcher.class);

    protected String getFileFromResourceFolder(String path) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(path).toURI())));
    }

}
