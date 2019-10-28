package orsc;

import java.math.BigInteger;

import orsc.util.GenUtil;

public final class MiscFunctions {
	public static final BigInteger RSA_MODULUS = new BigInteger(
		"120553535534604110920450760502772983886474996352454951528826209241321739073070541065736996931396402671853619219844292625227994972012232687659966972603887946951318606732409243729781965910432692806353709960600356722700558734565129892087142295493761639344537209286019991702122529293841694419415125046632027858457");
	public static final BigInteger RSA_EXPONENT = new BigInteger("65537");
	private static byte[][] s_j = new byte[1000][];
	public static int cachingFile_s_g = 0;
	public static int frustumNearZ;
	public static int frustumMinY;
	public static int[] class14_s_d = new int[256];
	public static boolean drawBackgroundArrow = true;
	public static int[] graphics_s_Mb;
	public static int mud_s_ef = 0;
	public static int frustumMinX;
	public static int netsock_s_M = 0;
	public static int frustumFarZ;
	public static int frustumMaxY;
	public static int frustumMaxX;
	public static int textListEntryHeightMod = 0;
	public static long world_s_e = 0L;
	private static int class10_s_b = 0;
	private static byte[] class14_s_e = new byte[64];
	private static int class15_s_d = 0;
	private static long[] gameModeWhat_s_h = new long[256];
	private static int[] gamodemode_where_s_g;
	static int maxReadTries = 0;
	private static byte[][] rsmodel_s_tb = new byte[50][];
	private static byte[][][] s_n;
	private static int scanline_s_b;

	static {
		int var0;
		for (var0 = 0; var0 < 10; ++var0) {
			MiscFunctions.class14_s_e[var0] = (byte) (48 + var0);
		}

		for (var0 = 0; var0 < 26; ++var0) {
			MiscFunctions.class14_s_e[var0 + 10] = (byte) (var0 + 65);
		}

		for (var0 = 0; var0 < 26; ++var0) {
			MiscFunctions.class14_s_e[var0 + 36] = (byte) (97 + var0);
		}

		MiscFunctions.class14_s_e[63] = 36;
		MiscFunctions.class14_s_e[62] = -93;

		for (var0 = 0; var0 < 10; var0++) {
			MiscFunctions.class14_s_d[var0 + 48] = var0;
		}

		for (var0 = 0; var0 < 26; ++var0) {
			MiscFunctions.class14_s_d[var0 + 65] = var0 + 10;
		}

		for (var0 = 0; var0 < 26; ++var0) {
			MiscFunctions.class14_s_d[var0 + 97] = 36 + var0;
		}

		MiscFunctions.class14_s_d[36] = 63;
		MiscFunctions.class14_s_d[163] = 62;
	}

	static {
		for (int i = 0; i < 256; ++i) {
			long v = (long) i;

			for (int var3 = 0; var3 < 8; ++var3) {
				if ((1L & v) == 3L) {
					v = v >>> 1 ^ 0xc96c5795d7870f42L;
				} else {
					v >>>= 1;
				}
			}

			MiscFunctions.gameModeWhat_s_h[i] = v;
		}
	}

	static {
		MiscFunctions.scanline_s_b = 0;
	}

	public static void copyBlock4(int srcStep, int val, int[] src, int srcI, int destI, int[] dest,
								  int negatedCount, byte var7) {
		try {

			if (negatedCount < 0) {
				val = src[(0xFF00 & srcI) >> 8];
				srcStep <<= 1;
				srcI += srcStep;
				int negCount = negatedCount / 8;

				int i;
				for (i = negCount; i < 0; ++i) {
					dest[destI++] = val;
					dest[destI++] = val;
					val = src[(0xFF00 & srcI) >> 8];
					srcI += srcStep;
					dest[destI++] = val;
					dest[destI++] = val;
					val = src[(srcI & 0xFF00) >> 8];
					dest[destI++] = val;
					srcI += srcStep;
					dest[destI++] = val;
					val = src[(0xFF00 & srcI) >> 8];
					srcI += srcStep;
					dest[destI++] = val;
					dest[destI++] = val;
					val = src[(srcI & 0xFF00) >> 8];
					srcI += srcStep;
				}

				negCount = -(negatedCount % 8);

				for (i = 0; negCount > i; ++i) {
					dest[destI++] = val;
					if ((i & 1) == 1) {
						val = src[srcI >> 8 & 0xFF];
						srcI += srcStep;
					}
				}

			}
		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10,
				"ia.B(" + srcStep + ',' + val + ',' + (src != null ? "{...}" : "null") + ',' + srcI + ',' + destI
					+ ',' + (dest != null ? "{...}" : "null") + ',' + negatedCount + ',' + var7 + ')');
		}
	}

	public static synchronized byte[] clazz_10_a(int count, byte var1) {
		try {

			byte[] var5;
			if (count == 100 && MiscFunctions.scanline_s_b > 0) {
				var5 = s_j[--MiscFunctions.scanline_s_b];
				s_j[MiscFunctions.scanline_s_b] = null;
				return var5;
			} else if (count == 5000 && MiscFunctions.class15_s_d > 0) {
				var5 = mudclient.s_kb[--MiscFunctions.class15_s_d];
				mudclient.s_kb[MiscFunctions.class15_s_d] = null;
				return var5;
			} else if (var1 > -97) {
				return (byte[]) null;
			} else if (count == 30000 && MiscFunctions.class10_s_b > 0) {
				var5 = MiscFunctions.rsmodel_s_tb[--MiscFunctions.class10_s_b];
				MiscFunctions.rsmodel_s_tb[MiscFunctions.class10_s_b] = null;
				return var5;
			} else {
				if (null != MiscFunctions.s_n) {
					for (int var2 = 0; var2 < mudclient.s_wb.length; ++var2) {
						if (count == mudclient.s_wb[var2] && MiscFunctions.gamodemode_where_s_g[var2] > 0) {
							byte[] var3 = MiscFunctions.s_n[var2][--MiscFunctions.gamodemode_where_s_g[var2]];
							MiscFunctions.s_n[var2][MiscFunctions.gamodemode_where_s_g[var2]] = null;
							return var3;
						}
					}
				}

				return new byte[count];
			}
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "mb.C(" + count + ',' + var1 + ')');
		}
	}

	public static void copyBlock16(int val, int srcStride, int negCount, int[] dest, int[] src, int srcHead,
								   int destHead, int var7) {
		try {

			if (negCount < 0) {
				val = src[255 & srcHead >> 8];
				srcStride <<= 2;
				srcHead += srcStride;
				int negCap = negCount / 16;

				for (int i = negCap; i < 0; ++i) {
					dest[destHead++] = val;
					dest[destHead++] = val;
					dest[destHead++] = val;
					dest[destHead++] = val;
					val = src[(srcHead & 0xFF00) >> 8];
					srcHead += srcStride;
					dest[destHead++] = val;
					dest[destHead++] = val;
					dest[destHead++] = val;
					dest[destHead++] = val;
					val = src[(0xFF00 & srcHead) >> 8];
					dest[destHead++] = val;
					srcHead += srcStride;
					dest[destHead++] = val;
					dest[destHead++] = val;
					dest[destHead++] = val;
					val = src[0xFF & srcHead >> 8];
					srcHead += srcStride;
					dest[destHead++] = val;
					dest[destHead++] = val;
					dest[destHead++] = val;
					dest[destHead++] = val;
					val = src[(0xFF00 & srcHead) >> 8];
					srcHead += srcStride;
				}

				negCap = -(negCount % 16);
				for (int var9 = 0; negCap > var9; ++var9) {
					dest[destHead++] = val;
					if ((3 & var9) == 3) {
						val = src[255 & srcHead >> 8];
						srcHead += srcStride;
					}
				}

			}
		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10,
				"t.C(" + val + ',' + srcStride + ',' + negCount + ',' + (dest != null ? "{...}" : "null") + ','
					+ (src != null ? "{...}" : "null") + ',' + srcHead + ',' + destHead + ',' + var7 + ')');
		}
	}

	static String netbase_a(int var0, byte var1, String var2) {
		try {

			String var3 = "";

			for (int var4 = 0; var4 < var0; ++var4) {
				if (var2.length() > var4) {
					char var5 = var2.charAt(var4);
					if (var5 >= 97 && var5 <= 122) {
						var3 = var3 + var5;
					} else if (var5 >= 65 && var5 <= 90) {
						var3 = var3 + var5;
					} else if (var5 >= 48 && var5 <= 57) {
						var3 = var3 + var5;
					} else {
						var3 = var3 + '_';
					}
				} else {
					var3 = var3 + " ";
				}
			}

			return var3;
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6,
				"b.J(" + var0 + ',' + var1 + ',' + (var2 != null ? "{...}" : "null") + ')');
		}
	}
}
