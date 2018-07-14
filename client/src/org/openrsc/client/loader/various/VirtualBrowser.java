package org.openrsc.client.loader.various;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class VirtualBrowser {
	private HttpURLConnection createConnection(URL url, String requestMethod) {
		try {
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(requestMethod);
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3");
			connection.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
			connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
			connection.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			connection.addRequestProperty("Keep-Alive", "300");
			connection.addRequestProperty("Connection", "keep-alive");
			return connection;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] download(HttpURLConnection connection, ProgressCallback callback) throws IOException {
		connection.getContentLength();
		final int len = connection.getContentLength();
		InputStream is = new BufferedInputStream(connection.getInputStream());

		final String compression = connection.getHeaderField("Content-Encoding");
		if (compression != null && compression.toLowerCase().contains("gzip"))
			is = new GZIPInputStream(is);

		final ByteArrayOutputStream bAOut = new ByteArrayOutputStream();
		int c = 0;
		int off = 0;
		try {
			while ((c = is.read()) != -1) {
				bAOut.write(c);
				off++;
				if (callback != null)
					callback.update(off, len);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		if (callback != null)
			callback.onComplete(bAOut.toByteArray());
		return bAOut.toByteArray();
	}

	public String get(URL url) {
		return new String(getRaw(url, null));
	}

	public byte[] getRaw(URL url, ProgressCallback callback) {
		final HttpURLConnection connection = createConnection(url, "GET");
		try {
			return download(connection, callback);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String post(URL url, String urlParameters, boolean encryption) {
		return new String(postRaw(url, urlParameters));
	}

	public byte[] postRaw(URL url, String urlParameters) {
		HttpURLConnection connection = null;
		try {
			connection = createConnection(url, "POST");
			connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			final DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			final byte[] arrayOfByte = download(connection, null);
			return arrayOfByte;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null)
				connection.disconnect();
		}

	}
}