package com.scalablecapital;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import static com.scalablecapital.functions.UrlExtractorFunctions.*;


@Slf4j
class JsCounter {

    @Getter
    private final ConcurrentHashMap<String, LongAdder> jsMapStorage;


    JsCounter(ConcurrentHashMap<String, LongAdder> concurrentHashMap) {
        jsMapStorage = concurrentHashMap;
    }


    void getAndCountJsLibs(String link, CloseableHttpAsyncClient client) throws IOException, GeneralSecurityException, InterruptedException {
        log.info("checking link = {}", link);
        PageDownloader pageDownloader = new PageDownloader();

        Optional<String> page = pageDownloader.downloadPage(link,client);
        if (!page.isPresent()) {
            log.info("noting to download for = {}", link);
            return;
        }
        Document document = Jsoup.parse(page.get());
        List<String> scripts = document.select("script").stream().map(s -> s.attr("src")).collect(Collectors.toList());
        for (String scriptSourse : scripts) {
            Optional.of(scriptSourse).map(getBeforeAmpersand).map(getBeforeQuestion).map(s -> {
                if (!s.isEmpty()) {
                    log.info("found {}", s);
                    jsMapStorage
                            .computeIfAbsent(Optional.of(s).
                                    map(getBeforejsExtention).
                                    orElseThrow(() -> new RuntimeException("Failed to compute"))
                                    , key -> new LongAdder()).increment();
                }
                return jsMapStorage;
            });
        }
    }

}


