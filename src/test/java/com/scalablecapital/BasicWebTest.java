package com.scalablecapital;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;


public class BasicWebTest {

    ConcurrentHashMap<String, LongAdder> storage = new ConcurrentHashMap<>();

    protected PageDownloader pageDownloader = Mockito.mock(PageDownloader.class);

    protected GoogleSearcher googleSearcher = Mockito.mock(GoogleSearcher.class);

    protected String getFileFromResourceFolder(String path) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(path).toURI())));
    }


    @Test
    public void name() throws ExecutionException, InterruptedException
    {
        List<String> results = CompletableFuture.supplyAsync(() -> {
            System.out.println("main");
            return "main + ";
        }).thenCompose(mainResult -> {
            List<CompletableFuture<String>> futures = new ArrayList<>();
            futures.add(CompletableFuture.supplyAsync(() -> {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
                System.out.println("finish foo");
                return mainResult + "foo";
            }));
            futures.add(CompletableFuture.supplyAsync(() -> {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
                System.out.println("bar");
                return mainResult + "bar";
            }));

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[2]))
                    .thenApply(ignore -> {
                        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
                    });
        }).get();

        System.out.println(results);
    }


}
