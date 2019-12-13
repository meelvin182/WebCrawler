package com.scalablecapital;


import com.scalablecapital.functions.UrlExtractorFunctions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import static com.scalablecapital.functions.UrlExtractorFunctions.*;


@Slf4j
@AllArgsConstructor
class JsCounter {

    @Getter
    private final TopNStorage<String> jsMapStorage;

    private final PageDownloader pageDownloader;


    /**
     * This method parses the html page and stores the js libs inthe to storage
     *
     * @param links From what link we should crawl the libs
     */
    void downloadAndCountJsLibs(List<String> links) {
        log.debug("checking link = {}", links);
        List<String> pages = pageDownloader.downloadPages(links);
        if (pages.isEmpty()) {
            log.error("noting to download for = {}", links);
            return;
        }

        for (String page : pages) {
            Document document = Jsoup.parse(page);
            List<String> scripts = document.select("script").stream().map(s -> s.attr("src")).collect(Collectors.toList());
            for (String scriptSource : scripts) {
                Optional.of(scriptSource)
                        .map(UrlExtractorFunctions::extractBeforeAmpersand)
                        .map(UrlExtractorFunctions::extractBeforejsExtention)
                        .map(script -> {
                            if (!script.isEmpty()) {
                                log.debug("found {}", script);
                                jsMapStorage.add(script);
                            }
                            return jsMapStorage;
                        });
            }
        }
    }
}


