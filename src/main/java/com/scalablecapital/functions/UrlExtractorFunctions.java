package com.scalablecapital.functions;

import java.util.function.Function;

public class UrlExtractorFunctions {

    public static Function<String, String> removeUrlQ = url -> url.replace("/url?q=", "");

    public static Function<String, String> extractBeforeAmpersand = url -> {
        if (url.contains("&")) {
            return url.substring(0, url.indexOf("&"));
        }
        return url;
    };

    public static Function<String, String> extractBeforeQuestionMark = url -> {
        String tmp = url;
        if (url.contains("?")) {
            tmp = url.substring(0, url.indexOf("?"));
        }
        if (url.contains("%3F")) {
            tmp = url.substring(0, url.indexOf("%3F"));
        }
        return tmp;
    };

    public static Function<String, String> getBeforejsExtention = url -> {
        String tmp = url;
        if (url.contains(".js") && !url.endsWith(".js")) {
            tmp = url.substring(0, url.indexOf(".js") + 4);
        }
        return tmp;
    };


}
