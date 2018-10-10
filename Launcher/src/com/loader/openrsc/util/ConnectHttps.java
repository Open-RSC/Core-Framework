package com.loader.openrsc.util;

import javax.net.ssl.*;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import static com.loader.openrsc.Constants.base_url;

public class ConnectHttps
{
    public static void main(final String[] args) throws Exception {
        final TrustManager[] trustAllCerts = { new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                
                @Override
                public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                }
                
                @Override
                public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                }
            } };
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        final HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        final URL url = new URL(base_url);
        final URLConnection con = url.openConnection();
        final Reader reader = new InputStreamReader(con.getInputStream());
        while (true) {
            final int ch = reader.read();
            if (ch == -1) {
                break;
            }
            System.out.print((char)ch);
        }
    }
}
