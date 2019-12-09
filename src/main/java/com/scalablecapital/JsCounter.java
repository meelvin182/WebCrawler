package com.scalablecapital;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

import static com.scalablecapital.functions.UrlExtractorFunctions.getBeforeAmpersand;
import static com.scalablecapital.functions.UrlExtractorFunctions.getBeforeQuestion;


@Slf4j
class JsCounter {

    @Getter
    private final ConcurrentHashMap<String, LongAdder> jsMapStorage;


    JsCounter(ConcurrentHashMap<String, LongAdder> concurrentHashMap) {
        jsMapStorage = concurrentHashMap;
    }


    void getAndCount(String link) throws IOException, GeneralSecurityException, ExecutionException, InterruptedException {
        log.info("checking link = {}", link);
        PageDownloader pageDownloader = new PageDownloader();
        Optional<String> page = pageDownloader.downloadPage(link);
        if (!page.isPresent()) {
            log.info("noting to download for = {}", page);
            return;
        }
        Document document = Jsoup.parse(page.get());
        List<String> scripts = document.select("script").stream().map(s -> s.attr("src")).collect(Collectors.toList());
        for (String scriptSourse : scripts) {
            Optional.of(scriptSourse).map(getBeforeAmpersand).map(getBeforeQuestion).map(s -> {
                if (!s.isEmpty()) {
                    log.info("found {}", s);
                    jsMapStorage.computeIfAbsent(s, key -> new LongAdder()).increment();
                }
                return jsMapStorage;
            });
        }
    }

}
