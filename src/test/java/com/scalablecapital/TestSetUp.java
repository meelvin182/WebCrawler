package com.scalablecapital;

import org.mockito.Mockito;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;


public class TestSetUp {

    protected ConcurrentHashMap<String, LongAdder> storage = new ConcurrentHashMap<>();

    protected PageDownloader pageDownloaderMock = Mockito.mock(PageDownloader.class);


    protected String getFileFromResourceFolder(String path) throws URISyntaxException {

        try {
            return new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(path).toURI())));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("file with name "+path+" not found");
        }
        return null;
    }

}
