package com.scalablecapital.functions;

import java.util.function.Function;

public class UrlExtractorFunctions {

    public static String extractBeforeAmpersand(String url) {
        if (url.contains("&")) {
            return url.substring(0, url.indexOf("&"));
        }
        return url;
    }

    public static String extractBeforejsExtention(String libName) {
        String tmp = libName;
        if (libName.contains(".js") && !libName.endsWith(".js")) {
            tmp = libName.substring(0, libName.indexOf(".js") + 4);
        }
        return tmp;
    }

}
