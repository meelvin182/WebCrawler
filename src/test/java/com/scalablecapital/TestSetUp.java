package com.scalablecapital;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class TestSetUp {

    TopNStorage<String> storage = new TopNStorage<>(5);

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
