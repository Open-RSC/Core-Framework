package com.aposbot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HTTPClient {

    private static final String USER_AGENT;

    static {
        final StringBuilder b = new StringBuilder("Mozilla/5.0 (compatible; Windows NT 6.1");
        boolean x64 = true;
        try {
            x64 = !System.getProperty("sun.arch.data.model").contains("32");
        } catch (final Throwable t) {
        }
        if (x64) {
            b.append("; WOW64) like Gecko");
        } else {
            b.append(") like Gecko");
        }
        USER_AGENT = b.toString();
    }

    private HTTPClient() {
    }

    public static byte[] load(String dest,
                              String via, boolean fakeUserAgent) throws IOException {
        final URL url = new URL(dest);
        final URLConnection connect = url.openConnection();
        connect.addRequestProperty("Accept",
                "text/html, application/xhtml+xml, */*");
        if (via != null) {
            connect.addRequestProperty("Referer", via);
        }
        connect.addRequestProperty("Accept-Language", "en-US");
        connect.addRequestProperty("User-Agent",
                fakeUserAgent ? USER_AGENT : "APOS");
        connect.setConnectTimeout(5000);
        InputStream in = null;
        try {
            in = connect.getInputStream();
            int read = 0;
            final int block_size = 4096;
            byte[] b = new byte[block_size];
            for (; ; ) {
                final int r = in.read(b, read, block_size);
                if (r == -1)
                    break;
                read += r;
                b = Arrays.copyOf(b, b.length + block_size);
            }
            return Arrays.copyOf(b, read);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final Throwable t) {
                }
            }
        }
    }

    public static Map<String, String> getParameters(String document) {
        final String PATTERN =
                "<param name=([^\\s]+)\\s+value=([^>]*)>";
        final Map<String, String> params = new HashMap<>();
        final Pattern regex = Pattern.compile(PATTERN,
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        final Matcher matcher = regex.matcher(document);
        while (matcher.find()) {
            final String key = matcher.group(1).replace("\"", "");
            final String value = matcher.group(2).replace("\"", "");
            if (!params.containsKey(key)) {
                params.put(key, value);
            }
        }
        if (params.containsKey("haveie6")) {
            params.put("haveie6", "false");
        }
        for (final String str : params.keySet()) {
            System.out.println(str + "=" + params.get(str));
        }
        return params;
    }

    public static String getArchive(String document) {
        final String PATTERN = "archive=([^\\s]+)";
        final Pattern regex = Pattern.compile(PATTERN,
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        final Matcher matcher = regex.matcher(document);
        if (matcher.find()) {
            return matcher.group(1).replace("\"", "");
        }
        return Constants.DEFAULT_JAR;
    }
}
