package com.scalablecapital;

import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author sokolov
 */
@Slf4j
public class Main {


    public static void main(String[] args) throws Exception {
        Optional<String> param = Arrays.stream(args).findFirst();
        if (param.isEmpty()) {
            Scanner sc = new Scanner(System.in);
            param = Optional.of(sc.nextLine());
        }
        String googleQuery = param.orElseThrow(() -> new RuntimeException("You have not entered any string to google"));
        log.info("You have passed = {}", googleQuery);

        HttpClient client = HttpClient.newHttpClient();
        // basically all the code in main method could be in one line
        // 1) Get the CF for google query
        // 2) Then parse and get the main result links using supply async
        // 3) CF::allOf for google results
        // 4) CF::combine with the root
        Map<String, LongAdder> storage = new ConcurrentHashMap<>();
        GoogleSearcher googleSearcher = new GoogleSearcher(client);
        PageDownloader pageDownloader = new PageDownloader(client);
        List<String> mainResultLinks = googleSearcher.findMainResultLinks(googleQuery);

        JsCounter jsCounter = new JsCounter(storage,pageDownloader);

        jsCounter.getAndCountJsLibs(mainResultLinks);

        System.out.println(storage);


    }


}
