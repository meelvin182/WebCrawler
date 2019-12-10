package com.scalablecapital;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author sokolov
 */
@Slf4j
public class Main {


    public static void main(String[] args) throws IOException, GeneralSecurityException {
        Optional<String> param = Arrays.stream(args).findFirst();
        if (!param.isPresent()) {
            Scanner sc = new Scanner(System.in);
            param = Optional.of(sc.nextLine());
        }
        String googleQuery = param.orElseThrow(() -> new RuntimeException("You have not entered any string to google"));
        log.info("You have passed = {}", googleQuery);
        GoogleSearcher googleSearcher = new GoogleSearcher();
        Collection<String> googleMainResultLinks;
        try {
            googleMainResultLinks = googleSearcher.findMainResultLinks(googleQuery);
        } catch (Exception e) {
            log.error("Cannot get the main result link because or {}", e.getMessage());
            return;
        }

        log.info("Found main results = {}", googleMainResultLinks);

        ConcurrentHashMap<String, LongAdder> storage = new ConcurrentHashMap<>();
        final JsCounter jsCounter = new JsCounter(storage);
        //do not live this solution with passing httpclient to getAndCountJsLibs, but thus I can give 1 client
        try (CloseableHttpAsyncClient closeableHttpAsyncClient = HttpClientHolder.getInstance().getHttpAsyncClient()) {
            googleMainResultLinks.stream().forEach(link -> {
                try {
                    jsCounter.getAndCountJsLibs(link, closeableHttpAsyncClient);
                } catch (Exception e) {
                    log.error("getAndCountJsLibs failed with error = {}", e.getMessage());
                }

            });
        }

        System.out.println(jsCounter.getTopFive());

    }


}
