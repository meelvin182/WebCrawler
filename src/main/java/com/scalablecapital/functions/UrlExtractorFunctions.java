package com.scalablecapital.functions;

import java.util.function.Function;

public class UrlExtractorFunctions {

    public static Function<String, String> extractBeforeAmpersand = url -> {
        if (url.contains("&")) {
            return url.substring(0, url.indexOf("&"));
        }
        return url;
    };

}
