package orsc.util;

import java.io.*;
import java.net.URL;

public class GenUtil {
	private static URL streamChooserContext = null;
	private static long lastTimeCall;
	private static long timeOverflow;

	public static void close(Closeable c) {
		if (c == null) return;
		try {
			c.close();
		} catch (IOException e) {
			//log the exception
		}
	}
	
	public static InputStream chooseStreamFor(String file) throws IOException {
		try {
			InputStream stream;
			if (null != GenUtil.streamChooserContext) {
				URL var3 = new URL(GenUtil.streamChooserContext, file);
				stream = var3.openStream();
			} else {
				stream = new BufferedInputStream(new FileInputStream(file));
			}
			return stream;
		} catch (RuntimeException var4) {
			throw makeThrowable(var4, "nb.F(" + true + ',' + (file != null ? "{...}" : "null") + ')');
		}
	}

	public static int colorToResource(int r, int g, int b) {
		try {
			b >>= 3;
			r >>= 3;
			g >>= 3;
			return -(g << 5) - 1 - (r << 10) - b;
		} catch (RuntimeException var5) {
			throw makeThrowable(var5, "da.C(" + b + ',' + -66 + ',' + r + ',' + g + ')');
		}
	}

	public static synchronized long currentTimeMillis() {
		try {
			long time = System.currentTimeMillis();
			if (GenUtil.lastTimeCall > time) {
				GenUtil.timeOverflow += GenUtil.lastTimeCall - time;
			}

			GenUtil.lastTimeCall = time;
			return GenUtil.timeOverflow + time;
		} catch (RuntimeException var3) {
			throw makeThrowable(var3, "p.A(" + 0 + ')');
		}
	}

	public static String ipToString(int ip) {
		try {
			return (ip >> 24 & 255) + "." + (255 & ip >> 16) + "." + (ip >> 8 & 255) + "." + (ip & 255);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ba.C(" + "dummy" + ',' + ip + ')');
		}
	}


	public static RSRuntimeError makeThrowable(Throwable error, String msg) {
		try {
			RSRuntimeError var2;
			if (error instanceof RSRuntimeError) {
				var2 = (RSRuntimeError) error;
				var2.message = var2.message + ' ' + msg;
			} else {
				var2 = new RSRuntimeError(error, msg);
			}

			error.printStackTrace();

			return var2;
		} catch (RuntimeException var3) {
			throw var3;
		}
	}

	static void printMultiLineError(String err) {
		try {
			System.out.println("Error: " + StringUtil.stringFindReplace(true, "\n", "%0a", err));
		} catch (RuntimeException var3) {
			throw var3;
		}
	}

	/**
	 * @see chooseStreamFor
	 */
	public static void readFileFully(String file, byte[] dest, int limit) throws IOException {
		try {
			InputStream var4 = chooseStreamFor(file);
			DataInputStream stream = new DataInputStream(var4);

			try {
				stream.readFully(dest, 0, limit);
			} catch (EOFException ignored) {
				;
			}

			stream.close();
		} catch (RuntimeException var7) {
			throw makeThrowable(var7, "ta.A(" + (file != null ? "{...}" : "null") + ',' + "dummy" + ','
				+ (dest != null ? "{...}" : "null") + ',' + limit + ')');
		}
	}

	public static void sleep(long time) {
		try {
			try {
				Thread.sleep(time);
			} catch (InterruptedException ignored) {
				;
			}
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "u.A(" + "dummy" + ',' + time + ')');
		}
	}

	public static void sleepShadow(long time) {
		try {
			if (0L < time) {
				if (time % 10L == 0L) {
					sleep(time - 1);
					sleep(1L);
				} else {
					sleep(time);
				}

			}
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "mb.F(" + "dummy" + ',' + time + ')');
		}
	}

	public static int buildColor(int r, int g, int b) {
		try {
			return (r << 16) + (g << 8) + b;
		} catch (RuntimeException var5) {
			throw makeThrowable(var5, "o.A(" + r + ',' + 9570 + ',' + b + ',' + g + ')');
		}
	}

	public static int computeItemCost(int basePrice, int shopItemPrice, int shopBuyPriceMod, int var3,
									  boolean var4, int var5, int count, int shopPriceMultiplier) {
		try {
			int cost = 0;

			for (int k = 0; var5 > k; ++k) {
				int var10 = shopPriceMultiplier * (shopItemPrice + ((!var4 ? -k : k) - count));
				if (var10 >= -100) {
					if (var10 > 100) {
						var10 = 100;
					}
				} else {
					var10 = -100;
				}

				int scaling = shopBuyPriceMod + var10;
				if (scaling < 10) {
					scaling = 10;
				}

				cost += basePrice * scaling / 100;
			}

			return cost;
		} catch (RuntimeException var12) {
			throw makeThrowable(var12, "o.F(" + basePrice + ',' + shopItemPrice + ',' + shopBuyPriceMod + ',' + -30910
				+ ',' + var4 + ',' + var5 + ',' + count + ',' + shopPriceMultiplier + ')');
		}
	}

}
