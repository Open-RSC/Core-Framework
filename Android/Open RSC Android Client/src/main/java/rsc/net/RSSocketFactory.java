package rsc.net;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import rsc.util.GenUtil;

final class RSSocketFactory extends RSSocketFactory_Base {
	private ProxySelector proxySelector;

	RSSocketFactory() {
		try {
			this.proxySelector = ProxySelector.getDefault();
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "gb.<init>()");
		}
	}

	private final Socket openConnect(int tunnelIP, int vsar2, String contentQuery, String tunnelHost) throws IOException {
		try {
			Socket sock = new Socket(tunnelHost, tunnelIP);
			sock.setSoTimeout(10000);
			OutputStream var6 = sock.getOutputStream();
			if (contentQuery != null) {
				var6.write(("CONNECT " + this.socketHost + ":" + this.socketPort + " HTTP/1.0\n" + contentQuery + "\n\n")
						.getBytes(Charset.forName("ISO-8859-1")));
			} else {
				var6.write(("CONNECT " + this.socketHost + ":" + this.socketPort + " HTTP/1.0\n\n")
						.getBytes(Charset.forName("ISO-8859-1")));
			}

			var6.flush();
			BufferedReader read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			String resp = read.readLine();
			if (resp != null) {
				if (resp.startsWith("HTTP/1.0 200") || resp.startsWith("HTTP/1.1 200")) {
					return sock;
				}

				if (resp.startsWith("HTTP/1.0 407") || resp.startsWith("HTTP/1.1 407")) {
					int lines = 0;
					String begin = "proxy-authenticate: ";

					for (resp = read.readLine(); resp != null && lines < 50; resp = read.readLine()) {
						if (resp.toLowerCase().startsWith(begin)) {
							resp = resp.substring(begin.length()).trim();
							int auth = resp.indexOf(32);
							if (auth != -1) {
								resp = resp.substring(0, auth);
							}

							throw new SocketProxyFailure(resp);
						}

						++lines;
					}

					throw new SocketProxyFailure("");
				}
			}

			var6.close();
			read.close();
			sock.close();
			return null;
		} catch (RuntimeException var12) {
			throw GenUtil.makeThrowable(var12, "gb.G(" + tunnelIP + ',' + "dummy" + ','
					+ (contentQuery != null ? "{...}" : "null") + ',' + (tunnelHost != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final Socket open() throws IOException {
		try {
			boolean var2 = Boolean.parseBoolean(System.getProperty("java.net.useSystemProxies"));
			if (!var2) {
				System.setProperty("java.net.useSystemProxies", "true");
			}

			boolean var5 = this.socketPort == 443;

			List<Proxy> primary;
			List<Proxy> secondary;
			try {
				primary = this.proxySelector.select(new URI((var5 ? "https" : "http") + "://" + this.socketHost));
				secondary = this.proxySelector.select(new URI((!var5 ? "https" : "http") + "://" + this.socketHost));
			} catch (URISyntaxException var15) {
				return this.openRaw();
			}

			primary.addAll(secondary);
			Proxy[] var6 = primary.toArray(new Proxy[primary.size()]);
			SocketProxyFailure var7 = null;
			Proxy[] var8 = var6;
			for (int var9 = 0; var9 < var8.length; ++var9) {
				Object var10 = var8[var9];
				Proxy var11 = (Proxy) var10;

				try {
					Socket var12 = this.open(var11);
					if (var12 != null) {
						return var12;
					}
				} catch (SocketProxyFailure var13) {
					var7 = var13;
				} catch (IOException var14) {
					;
				}
			}

			if (null == var7) {
				return this.openRaw();
			} else {
				throw var7;
			}
		} catch (RuntimeException var16) {
			throw GenUtil.makeThrowable(var16, "gb.D(" + "dummy" + ')');
		}
	}

	private final Socket open(Proxy proxy) throws IOException {
		try {
			if (proxy.type() != Type.DIRECT) {
				SocketAddress var3 = proxy.address();
				if (var3 instanceof InetSocketAddress) {
					InetSocketAddress var4 = (InetSocketAddress) var3;
					if (proxy.type() == Type.HTTP) {
						String var16 = null;

						try {
							Class<?> var6 = Class.forName("sun.net.www.protocol.http.AuthenticationInfo");
							Method var7 = var6.getDeclaredMethod("getProxyAuth",
									new Class[] { String.class, Integer.TYPE });
							var7.setAccessible(true);
							Object var8 = var7.invoke((Object) null,
									new Object[] { var4.getHostName(), new Integer(var4.getPort()) });
							if (null != var8) {
								Method var9 = var6.getDeclaredMethod("supportsPreemptiveAuthorization", new Class[0]);
								var9.setAccessible(true);
								if (((Boolean) var9.invoke(var8, new Object[0])).booleanValue()) {
									Method var10 = var6.getDeclaredMethod("getHeaderName", new Class[0]);
									var10.setAccessible(true);
									Method var11 = var6.getDeclaredMethod("getHeaderValue",
											new Class[] { URL.class, String.class });
									var11.setAccessible(true);
									String var12 = (String) var10.invoke(var8, new Object[0]);
									String var13 = (String) var11.invoke(var8,
											new Object[] { new URL("https://" + this.socketHost + "/"), "https" });
									var16 = var12 + ": " + var13;
								}
							}
						} catch (Exception var14) {
							;
						}

						return this.openConnect(var4.getPort(), 1514, var16, var4.getHostName());
					} else if (proxy.type() != Type.SOCKS) {
						return null;
					} else {
						Socket var5 = new Socket(proxy);
						var5.connect(new InetSocketAddress(this.socketHost, this.socketPort));
						return var5;
					}
				} else {
					return null;
				}
			} else {
				return this.openRaw();
			}
		} catch (RuntimeException var15) {
			throw GenUtil.makeThrowable(var15,
					"gb.F(" + (proxy != null ? "{...}" : "null") + ',' + "dummy" + ')');
		}
	}
}
