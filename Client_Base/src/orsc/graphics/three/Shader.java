package orsc.graphics.three;

import orsc.util.FastMath;
import orsc.util.GenUtil;

class Shader {

	static void shadeScanline(int var0, int var1, int var2, int var3, int[] var4, int var5, int var6,
							  int var7, int var8, int var9, int var10, int var11, int var12, int var13, int[] var14, int var15) {
		try {

			if (var15 > 0) {
				if (var1 != 10) {
					shadeScanline(-30, -28, 22, 0, (int[]) null, -78, 109, 44, -120, 67, 27, 2, 107, -113, (int[]) null,
						56);
				}

				int var16 = 0;
				int var17 = 0;
				var11 <<= 2;
				if (var5 != 0) {
					var17 = var8 / var5 << 7;
					var16 = var7 / var5 << 7;
				}

				if (var16 < 0) {
					var16 = 0;
				} else if (var16 > 0x3F80) {
					var16 = 0x3F80;
				}

				for (int var20 = var15; var20 > 0; var20 -= 16) {
					var7 += var13;
					var3 = var17;
					var5 += var10;
					var2 = var16;
					var8 += var0;
					if (var5 != 0) {
						var16 = var7 / var5 << 7;
						var17 = var8 / var5 << 7;
					}

					if (var16 >= 0) {
						if (var16 > 0x3F80) {
							var16 = 0x3F80;
						}
					} else {
						var16 = 0;
					}

					int var18 = var16 - var2 >> 4;
					int var19 = var17 - var3 >> 4;
					int var21 = var6 >> 23;
					var2 += 6291456 & var6;
					var6 += var11;
					if (var20 < 16) {
						for (int var22 = 0; var20 > var22; ++var22) {
							if ((var12 = var14[(var3 & 0x3F80) + (var2 >> 7)] >>> var21) != 0) {
								var4[var9] = var12;
							}

							++var9;
							var2 += var18;
							var3 += var19;
							if ((var22 & 3) == 3) {
								var2 = (var6 & 6291456) + (16383 & var2);
								var21 = var6 >> 23;
								var6 += var11;
							}
						}
					} else {
						if ((var12 = var14[(var3 & 0x3F80) + (var2 >> 7)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						var2 += var18;
						++var9;
						var3 += var19;
						if ((var12 = var14[(var2 >> 7) + (0x3F80 & var3)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						++var9;
						var3 += var19;
						var2 += var18;
						if ((var12 = var14[(var2 >> 7) + (0x3F80 & var3)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						++var9;
						var3 += var19;
						var2 += var18;
						if ((var12 = var14[(var2 >> 7) + (var3 & 0x3F80)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						var2 += var18;
						var3 += var19;
						++var9;
						var21 = var6 >> 23;
						var2 = (var6 & 6291456) + (16383 & var2);
						var6 += var11;
						if ((var12 = var14[(var3 & 0x3F80) + (var2 >> 7)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						var2 += var18;
						++var9;
						var3 += var19;
						if ((var12 = var14[(0x3F80 & var3) + (var2 >> 7)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						++var9;
						var2 += var18;
						var3 += var19;
						if ((var12 = var14[(var2 >> 7) + (0x3F80 & var3)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						var2 += var18;
						var3 += var19;
						++var9;
						if ((var12 = var14[(var2 >> 7) + (var3 & 0x3F80)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						var3 += var19;
						++var9;
						var2 += var18;
						var21 = var6 >> 23;
						var2 = (var2 & 16383) + (6291456 & var6);
						if ((var12 = var14[(var2 >> 7) + (var3 & 0x3F80)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						var6 += var11;
						++var9;
						var2 += var18;
						var3 += var19;
						if ((var12 = var14[(var2 >> 7) + (var3 & 0x3F80)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						var3 += var19;
						++var9;
						var2 += var18;
						if ((var12 = var14[(0x3F80 & var3) + (var2 >> 7)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						var3 += var19;
						var2 += var18;
						++var9;
						if ((var12 = var14[(var2 >> 7) + (var3 & 0x3F80)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						var2 += var18;
						var3 += var19;
						++var9;
						var2 = (var2 & 16383) + (var6 & 6291456);
						var21 = var6 >> 23;
						if ((var12 = var14[(var3 & 0x3F80) + (var2 >> 7)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						var6 += var11;
						var3 += var19;
						++var9;
						var2 += var18;
						if ((var12 = var14[(var2 >> 7) + (var3 & 0x3F80)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						++var9;
						var2 += var18;
						var3 += var19;
						if ((var12 = var14[(var2 >> 7) + (var3 & 0x3F80)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						var3 += var19;
						var2 += var18;
						++var9;
						if ((var12 = var14[(0x3F80 & var3) + (var2 >> 7)] >>> var21) != 0) {
							var4[var9] = var12;
						}

						++var9;
					}
				}

			}
		} catch (RuntimeException var23) {
			throw GenUtil.makeThrowable(var23,
				"wb.S(" + var0 + ',' + var1 + ',' + var2 + ',' + var3 + ',' + (var4 != null ? "{...}" : "null")
					+ ',' + var5 + ',' + var6 + ',' + var7 + ',' + var8 + ',' + var9 + ',' + var10 + ',' + var11
					+ ',' + var12 + ',' + var13 + ',' + (var14 != null ? "{...}" : "null") + ',' + var15 + ')');
		}
	}

	static void shadeScanline(int var0, int var1, byte var2, int var3, int val, int valStep, int[] src,
							  int dH, int var8, int var9, int high, int low, int[] dest, int var13, int var14) {
		try {

			if (var14 > 0) {
				int var15 = 0;
				int var16 = 0;
				if (var3 != 0) {
					low = var8 / var3 << 7;
					high = var0 / var3 << 7;
				}

				int shift = 0;
				if (low < 0) {
					low = 0;
				} else if (low > 0x3F80) {
					low = 0x3F80;
				}

				if (var2 == 50) {
					var3 += var9;
					var0 += var13;
					var8 += var1;
					if (var3 != 0) {
						var16 = var0 / var3 << 7;
						var15 = var8 / var3 << 7;
					}

					if (var15 >= 0) {
						if (var15 > 0x3F80) {
							var15 = 0x3F80;
						}
					} else {
						var15 = 0;
					}

					int lowStep = var15 - low >> 4;
					int highStep = var16 - high >> 4;

					int var20;
					for (var20 = var14 >> 4; var20 > 0; --var20) {
						low += val & 6291456;
						shift = val >> 23;
						dest[dH++] = src[FastMath.bitwiseAnd(0x3F80, high) + (low >> 7)] >>> shift;
						val += valStep;
						low += lowStep;
						high += highStep;
						dest[dH++] = src[(low >> 7) + FastMath.bitwiseAnd(0x3F80, high)] >>> shift;
						high += highStep;
						low += lowStep;
						dest[dH++] = src[(low >> 7) + FastMath.bitwiseAnd(0x3F80, high)] >>> shift;
						high += highStep;
						low += lowStep;
						dest[dH++] = src[(low >> 7) + FastMath.bitwiseAnd(0x3F80, high)] >>> shift;
						high += highStep;
						low += lowStep;
						low = (6291456 & val) + (16383 & low);
						shift = val >> 23;
						dest[dH++] = src[(low >> 7) + FastMath.bitwiseAnd(high, 0x3F80)] >>> shift;
						val += valStep;
						low += lowStep;
						high += highStep;
						dest[dH++] = src[FastMath.bitwiseAnd(0x3F80, high) + (low >> 7)] >>> shift;
						low += lowStep;
						high += highStep;
						dest[dH++] = src[(low >> 7) + FastMath.bitwiseAnd(0x3F80, high)] >>> shift;
						high += highStep;
						low += lowStep;
						dest[dH++] = src[FastMath.bitwiseAnd(0x3F80, high) + (low >> 7)] >>> shift;
						high += highStep;
						low += lowStep;
						low = (val & 6291456) + (16383 & low);
						shift = val >> 23;
						val += valStep;
						dest[dH++] = src[FastMath.bitwiseAnd(high, 0x3F80) + (low >> 7)] >>> shift;
						low += lowStep;
						high += highStep;
						dest[dH++] = src[FastMath.bitwiseAnd(0x3F80, high) + (low >> 7)] >>> shift;
						low += lowStep;
						high += highStep;
						dest[dH++] = src[(low >> 7) + FastMath.bitwiseAnd(0x3F80, high)] >>> shift;
						high += highStep;
						low += lowStep;
						dest[dH++] = src[(low >> 7) + FastMath.bitwiseAnd(high, 0x3F80)] >>> shift;
						low += lowStep;
						high += highStep;
						low = (16383 & low) + (6291456 & val);
						shift = val >> 23;
						dest[dH++] = src[(low >> 7) + FastMath.bitwiseAnd(0x3F80, high)] >>> shift;
						val += valStep;
						low += lowStep;
						high += highStep;
						dest[dH++] = src[(low >> 7) + FastMath.bitwiseAnd(high, 0x3F80)] >>> shift;
						low += lowStep;
						high += highStep;
						dest[dH++] = src[(low >> 7) + FastMath.bitwiseAnd(0x3F80, high)] >>> shift;
						high += highStep;
						low += lowStep;
						dest[dH++] = src[(low >> 7) + FastMath.bitwiseAnd(0x3F80, high)] >>> shift;
						low = var15;
						high = var16;
						var0 += var13;
						var3 += var9;
						var8 += var1;
						if (var3 != 0) {
							var16 = var0 / var3 << 7;
							var15 = var8 / var3 << 7;
						}

						if (var15 >= 0) {
							if (var15 > 0x3F80) {
								var15 = 0x3F80;
							}
						} else {
							var15 = 0;
						}

						highStep = var16 - high >> 4;
						lowStep = var15 - low >> 4;
					}

					for (var20 = 0; (15 & var14) > var20; ++var20) {
						if ((var20 & 3) == 0) {
							shift = val >> 23;
							low = (val & 6291456) + (16383 & low);
							val += valStep;
						}

						dest[dH++] = src[(low >> 7) + FastMath.bitwiseAnd(high, 0x3F80)] >>> shift;
						high += highStep;
						low += lowStep;
					}

				}
			}
		} catch (RuntimeException var21) {
			throw GenUtil.makeThrowable(var21,
				"gb.E(" + var0 + ',' + var1 + ',' + var2 + ',' + var3 + ',' + val + ',' + valStep + ','
					+ (src != null ? "{...}" : "null") + ',' + dH + ',' + var8 + ',' + var9 + ',' + high + ','
					+ low + ',' + (dest != null ? "{...}" : "null") + ',' + var13 + ',' + var14 + ')');
		}
	}

	static void shadeScanline(int var0, int var1, int var2, int var3, int var4, int var5, int var6,
							  int var7, int var8, int var9, int[] var10, int var11, int var12, int[] var13, byte var14) {
		try {

			if (var11 > 0) {
				int var15 = 0;
				int var16 = 0;
				if (var14 <= 97) {
					Shader.shadeScanline(-65, -47, -42, (byte) -16, 62, 50, -59, -91, (int[]) null, (int[]) null, 71,
						-91, -16, -29, 110, 81);
				}

				int var19 = 0;
				if (var7 != 0) {
					var3 = var1 / var7 << 7;
					var6 = var2 / var7 << 7;
				}

				var7 += var12;
				if (var6 >= 0) {
					if (var6 > 0x3F80) {
						var6 = 0x3F80;
					}
				} else {
					var6 = 0;
				}

				var1 += var5;
				var2 += var8;
				if (var7 != 0) {
					var15 = var2 / var7 << 7;
					var16 = var1 / var7 << 7;
				}

				if (var15 >= 0) {
					if (var15 > 0x3F80) {
						var15 = 0x3F80;
					}
				} else {
					var15 = 0;
				}

				int var17 = var15 - var6 >> 4;
				int var18 = var16 - var3 >> 4;

				int var20;
				for (var20 = var11 >> 4; var20 > 0; --var20) {
					var19 = var4 >> 23;
					var6 += var4 & 6291456;
					var4 += var9;
					var13[var0++] = FastMath.bitwiseAnd(var13[var0] >> 1, 8355711)
						+ (var10[(var6 >> 7) + FastMath.bitwiseAnd(var3, 0x3F80)] >>> var19);
					var3 += var18;
					var6 += var17;
					var13[var0++] = FastMath.bitwiseAnd(var13[var0] >> 1, 8355711)
						+ (var10[(var6 >> 7) + FastMath.bitwiseAnd(0x3F80, var3)] >>> var19);
					var6 += var17;
					var3 += var18;
					var13[var0++] = (var10[FastMath.bitwiseAnd(0x3F80, var3) + (var6 >> 7)] >>> var19)
						+ (FastMath.bitwiseAnd(16711422, var13[var0]) >> 1);
					var3 += var18;
					var6 += var17;
					var13[var0++] = (FastMath.bitwiseAnd(16711422, var13[var0]) >> 1)
						+ (var10[FastMath.bitwiseAnd(var3, 0x3F80) + (var6 >> 7)] >>> var19);
					var3 += var18;
					var6 += var17;
					var19 = var4 >> 23;
					var6 = (var6 & 16383) + (var4 & 6291456);
					var4 += var9;
					var13[var0++] = FastMath.bitwiseAnd(var13[var0] >> 1, 8355711)
						+ (var10[FastMath.bitwiseAnd(0x3F80, var3) + (var6 >> 7)] >>> var19);
					var3 += var18;
					var6 += var17;
					var13[var0++] = (var10[(var6 >> 7) + FastMath.bitwiseAnd(var3, 0x3F80)] >>> var19)
						+ FastMath.bitwiseAnd(var13[var0] >> 1, 8355711);
					var3 += var18;
					var6 += var17;
					var13[var0++] = (FastMath.bitwiseAnd(var13[var0], 16711423) >> 1)
						+ (var10[FastMath.bitwiseAnd(var3, 0x3F80) + (var6 >> 7)] >>> var19);
					var6 += var17;
					var3 += var18;
					var13[var0++] = (var10[(var6 >> 7) + FastMath.bitwiseAnd(0x3F80, var3)] >>> var19)
						+ (FastMath.bitwiseAnd(var13[var0], 16711423) >> 1);
					var6 += var17;
					var3 += var18;
					var6 = (16383 & var6) + (var4 & 6291456);
					var19 = var4 >> 23;
					var13[var0++] = (FastMath.bitwiseAnd(16711423, var13[var0]) >> 1)
						+ (var10[(var6 >> 7) + FastMath.bitwiseAnd(var3, 0x3F80)] >>> var19);
					var4 += var9;
					var3 += var18;
					var6 += var17;
					var13[var0++] = FastMath.bitwiseAnd(var13[var0] >> 1, 8355711)
						+ (var10[(var6 >> 7) + FastMath.bitwiseAnd(0x3F80, var3)] >>> var19);
					var6 += var17;
					var3 += var18;
					var13[var0++] = (var10[FastMath.bitwiseAnd(var3, 0x3F80) + (var6 >> 7)] >>> var19)
						+ FastMath.bitwiseAnd(8355711, var13[var0] >> 1);
					var6 += var17;
					var3 += var18;
					var13[var0++] = (FastMath.bitwiseAnd(16711423, var13[var0]) >> 1)
						+ (var10[FastMath.bitwiseAnd(0x3F80, var3) + (var6 >> 7)] >>> var19);
					var3 += var18;
					var6 += var17;
					var6 = (var6 & 16383) + (var4 & 6291456);
					var19 = var4 >> 23;
					var13[var0++] = FastMath.bitwiseAnd(8355711, var13[var0] >> 1)
						+ (var10[(var6 >> 7) + FastMath.bitwiseAnd(var3, 0x3F80)] >>> var19);
					var4 += var9;
					var6 += var17;
					var3 += var18;
					var13[var0++] = FastMath.bitwiseAnd(var13[var0] >> 1, 8355711)
						+ (var10[(var6 >> 7) + FastMath.bitwiseAnd(0x3F80, var3)] >>> var19);
					var6 += var17;
					var3 += var18;
					var13[var0++] = (var10[FastMath.bitwiseAnd(var3, 0x3F80) + (var6 >> 7)] >>> var19)
						+ FastMath.bitwiseAnd(var13[var0] >> 1, 8355711);
					var6 += var17;
					var3 += var18;
					var13[var0++] = FastMath.bitwiseAnd(var13[var0] >> 1, 8355711)
						+ (var10[(var6 >> 7) + FastMath.bitwiseAnd(0x3F80, var3)] >>> var19);
					var7 += var12;
					var1 += var5;
					var2 += var8;
					var3 = var16;
					var6 = var15;
					if (var7 != 0) {
						var16 = var1 / var7 << 7;
						var15 = var2 / var7 << 7;
					}

					if (var15 >= 0) {
						if (var15 > 0x3F80) {
							var15 = 0x3F80;
						}
					} else {
						var15 = 0;
					}

					var18 = var16 - var3 >> 4;
					var17 = var15 - var6 >> 4;
				}

				for (var20 = 0; (var11 & 15) > var20; ++var20) {
					if ((var20 & 3) == 0) {
						var6 = (var4 & 6291456) + (var6 & 16383);
						var19 = var4 >> 23;
						var4 += var9;
					}

					var13[var0++] = (var10[FastMath.bitwiseAnd(var3, 0x3F80) + (var6 >> 7)] >>> var19)
						+ (FastMath.bitwiseAnd(var13[var0], 16711422) >> 1);
					var6 += var17;
					var3 += var18;
				}

			}
		} catch (RuntimeException var21) {
			throw GenUtil.makeThrowable(var21,
				"cb.E(" + var0 + ',' + var1 + ',' + var2 + ',' + var3 + ',' + var4 + ',' + var5 + ',' + var6 + ','
					+ var7 + ',' + var8 + ',' + var9 + ',' + (var10 != null ? "{...}" : "null") + ',' + var11
					+ ',' + var12 + ',' + (var13 != null ? "{...}" : "null") + ',' + var14 + ')');
		}
	}

	static void shadeScanline(int[] var0, int var1, int var2, int var3, int var4, int var5, int var6,
							  int var7, int var8, int var9, int[] var10, boolean var11, int var12, int var13, int var14) {
		try {

			if (var7 > 0) {
				int var15 = 0;
				int var16 = 0;
				if (var3 != 0) {
					var16 = var13 / var3 << 6;
					var15 = var8 / var3 << 6;
				}

				var4 <<= 2;
				if (var15 < 0) {
					var15 = 0;
				} else if (var15 > 0xFC0) {
					var15 = 0xFC0;
				}

				for (int var19 = var7; var19 > 0; var19 -= 16) {
					var3 += var2;
					var14 = var15;
					var8 += var12;
					var9 = var16;
					var13 += var1;
					if (var3 != 0) {
						var15 = var8 / var3 << 6;
						var16 = var13 / var3 << 6;
					}

					if (var15 >= 0) {
						if (var15 > 0xFC0) {
							var15 = 0xFC0;
						}
					} else {
						var15 = 0;
					}

					int var18 = var16 - var9 >> 4;
					int var17 = var15 - var14 >> 4;
					int var20 = var5 >> 20;
					var14 += var5 & 786432;
					var5 += var4;
					if (var19 >= 16) {
						var0[var6++] = FastMath.bitwiseAnd(var0[var6] >> 1, 0x7f7f7f)
							+ (var10[FastMath.bitwiseAnd(0xFC0, var9) + (var14 >> 6)] >>> var20);
						var14 += var17;
						var9 += var18;
						var0[var6++] = (FastMath.bitwiseAnd(var0[var6], 0xfefeff) >> 1)
							+ (var10[FastMath.bitwiseAnd(0xFC0, var9) + (var14 >> 6)] >>> var20);
						var9 += var18;
						var14 += var17;
						var0[var6++] = (FastMath.bitwiseAnd(0xfefeff, var0[var6]) >> 1)
							+ (var10[FastMath.bitwiseAnd(var9, 0xFC0) + (var14 >> 6)] >>> var20);
						var9 += var18;
						var14 += var17;
						var0[var6++] = (FastMath.bitwiseAnd(var0[var6], 0xfefeff) >> 1)
							+ (var10[(var14 >> 6) + FastMath.bitwiseAnd(0xFC0, var9)] >>> var20);
						var14 += var17;
						var9 += var18;
						var14 = (var5 & 786432) + (4095 & var14);
						var20 = var5 >> 20;
						var0[var6++] = (var10[FastMath.bitwiseAnd(var9, 0xFC0) + (var14 >> 6)] >>> var20)
							+ (FastMath.bitwiseAnd(var0[var6], 0xFEFEFE) >> 1);
						var5 += var4;
						var14 += var17;
						var9 += var18;
						var0[var6++] = (var10[(var14 >> 6) + FastMath.bitwiseAnd(0xFC0, var9)] >>> var20)
							+ (FastMath.bitwiseAnd(var0[var6], 0xfefeff) >> 1);
						var14 += var17;
						var9 += var18;
						var0[var6++] = (var10[FastMath.bitwiseAnd(0xFC0, var9) + (var14 >> 6)] >>> var20)
							+ (FastMath.bitwiseAnd(var0[var6], 0xfefeff) >> 1);
						var9 += var18;
						var14 += var17;
						var0[var6++] = (var10[FastMath.bitwiseAnd(0xFC0, var9) + (var14 >> 6)] >>> var20)
							+ (FastMath.bitwiseAnd(0xfefeff, var0[var6]) >> 1);
						var14 += var17;
						var9 += var18;
						var14 = (786432 & var5) + (4095 & var14);
						var20 = var5 >> 20;
						var0[var6++] = (var10[FastMath.bitwiseAnd(0xFC0, var9) + (var14 >> 6)] >>> var20)
							+ (FastMath.bitwiseAnd(var0[var6], 0xFEFEFE) >> 1);
						var5 += var4;
						var14 += var17;
						var9 += var18;
						var0[var6++] = (var10[FastMath.bitwiseAnd(var9, 0xFC0) + (var14 >> 6)] >>> var20)
							+ FastMath.bitwiseAnd(var0[var6] >> 1, 0x7f7f7f);
						var9 += var18;
						var14 += var17;
						var0[var6++] = (var10[FastMath.bitwiseAnd(0xFC0, var9) + (var14 >> 6)] >>> var20)
							+ (FastMath.bitwiseAnd(var0[var6], 0xfefeff) >> 1);
						var14 += var17;
						var9 += var18;
						var0[var6++] = (FastMath.bitwiseAnd(0xFEFEFE, var0[var6]) >> 1)
							+ (var10[(var14 >> 6) + FastMath.bitwiseAnd(var9, 0xFC0)] >>> var20);
						var14 += var17;
						var9 += var18;
						var14 = (var5 & 786432) + (var14 & 4095);
						var20 = var5 >> 20;
						var0[var6++] = (var10[FastMath.bitwiseAnd(var9, 0xFC0) + (var14 >> 6)] >>> var20)
							+ (FastMath.bitwiseAnd(0xFEFEFE, var0[var6]) >> 1);
						var5 += var4;
						var14 += var17;
						var9 += var18;
						var0[var6++] = (FastMath.bitwiseAnd(var0[var6], 0xfefeff) >> 1)
							+ (var10[FastMath.bitwiseAnd(var9, 0xFC0) + (var14 >> 6)] >>> var20);
						var14 += var17;
						var9 += var18;
						var0[var6++] = (var10[FastMath.bitwiseAnd(0xFC0, var9) + (var14 >> 6)] >>> var20)
							+ FastMath.bitwiseAnd(var0[var6] >> 1, 0x7f7f7f);
						var9 += var18;
						var14 += var17;
						var0[var6++] = (var10[(var14 >> 6) + FastMath.bitwiseAnd(0xFC0, var9)] >>> var20)
							+ (FastMath.bitwiseAnd(var0[var6], 0xfefeff) >> 1);
					} else {
						for (int var21 = 0; var19 > var21; ++var21) {
							var0[var6++] = (var10[(var14 >> 6) + FastMath.bitwiseAnd(var9, 0xFC0)] >>> var20)
								+ (FastMath.bitwiseAnd(0xFEFEFE, var0[var6]) >> 1);
							var9 += var18;
							var14 += var17;
							if ((var21 & 3) == 3) {
								var20 = var5 >> 20;
								var14 = (var14 & 4095) + (786432 & var5);
								var5 += var4;
							}
						}
					}
				}

			}
		} catch (RuntimeException var22) {
			throw GenUtil.makeThrowable(var22, "jb.D(" + (var0 != null ? "{...}" : "null") + ',' + var1 + ',' + var2
				+ ',' + var3 + ',' + var4 + ',' + var5 + ',' + var6 + ',' + var7 + ',' + var8 + ',' + var9 + ','
				+ (var10 != null ? "{...}" : "null") + ',' + false + ',' + var12 + ',' + var13 + ',' + var14 + ')');
		}
	}

	static void shadeScanline(int var0, int var1, int var2, int var3, int var4, int[] src, int var6,
							  int var7, int var8, int var9, int[] dest, int var11, int var12, int var13, int var14) {
		try {

			if (var14 > 0) {
				int var15 = 0;
				int var16 = 0;
				if (var12 != 0) {
					var16 = var3 / var12 << 6;
					var15 = var8 / var12 << 6;
				}

				var0 <<= 2;
				if (var15 >= 0) {
					if (var15 > 4032) {
						var15 = 4032;
					}
				} else {
					var15 = 0;
				}

				if (var1 != 1121159302) {
					shadeScanline(-69, 127, -20, -29, -78, (int[]) null, 16, 2, -77, -5, (int[]) null, 113, -57, 68,
						-87);
				}

				for (int var19 = var14; var19 > 0; var19 -= 16) {
					var12 += var13;
					var8 += var4;
					var3 += var2;
					var9 = var15;
					var7 = var16;
					if (var12 != 0) {
						var15 = var8 / var12 << 6;
						var16 = var3 / var12 << 6;
					}

					if (var15 < 0) {
						var15 = 0;
					} else if (var15 > 4032) {
						var15 = 4032;
					}

					int var18 = var16 - var7 >> 4;
					int var17 = var15 - var9 >> 4;
					int var20 = var6 >> 20;
					var9 += 786432 & var6;
					var6 += var0;
					if (var19 >= 16) {
						dest[var11++] = src[FastMath.bitwiseAnd(var7, 4032) + (var9 >> 6)] >>> var20;
						var7 += var18;
						var9 += var17;
						dest[var11++] = src[(var9 >> 6) + FastMath.bitwiseAnd(var7, 4032)] >>> var20;
						var7 += var18;
						var9 += var17;
						dest[var11++] = src[(var9 >> 6) + FastMath.bitwiseAnd(4032, var7)] >>> var20;
						var9 += var17;
						var7 += var18;
						dest[var11++] = src[(var9 >> 6) + FastMath.bitwiseAnd(4032, var7)] >>> var20;
						var9 += var17;
						var7 += var18;
						var20 = var6 >> 20;
						var9 = (var6 & 786432) + (4095 & var9);
						var6 += var0;
						dest[var11++] = src[FastMath.bitwiseAnd(4032, var7) + (var9 >> 6)] >>> var20;
						var7 += var18;
						var9 += var17;
						dest[var11++] = src[FastMath.bitwiseAnd(var7, 4032) + (var9 >> 6)] >>> var20;
						var9 += var17;
						var7 += var18;
						dest[var11++] = src[FastMath.bitwiseAnd(var7, 4032) + (var9 >> 6)] >>> var20;
						var7 += var18;
						var9 += var17;
						dest[var11++] = src[(var9 >> 6) + FastMath.bitwiseAnd(4032, var7)] >>> var20;
						var9 += var17;
						var7 += var18;
						var20 = var6 >> 20;
						var9 = (786432 & var6) + (4095 & var9);
						var6 += var0;
						dest[var11++] = src[FastMath.bitwiseAnd(var7, 4032) + (var9 >> 6)] >>> var20;
						var7 += var18;
						var9 += var17;
						dest[var11++] = src[(var9 >> 6) + FastMath.bitwiseAnd(var7, 4032)] >>> var20;
						var7 += var18;
						var9 += var17;
						dest[var11++] = src[FastMath.bitwiseAnd(var7, 4032) + (var9 >> 6)] >>> var20;
						var7 += var18;
						var9 += var17;
						dest[var11++] = src[FastMath.bitwiseAnd(4032, var7) + (var9 >> 6)] >>> var20;
						var7 += var18;
						var9 += var17;
						var20 = var6 >> 20;
						var9 = (4095 & var9) + (var6 & 786432);
						var6 += var0;
						dest[var11++] = src[FastMath.bitwiseAnd(var7, 4032) + (var9 >> 6)] >>> var20;
						var7 += var18;
						var9 += var17;
						dest[var11++] = src[(var9 >> 6) + FastMath.bitwiseAnd(var7, 4032)] >>> var20;
						var7 += var18;
						var9 += var17;
						dest[var11++] = src[(var9 >> 6) + FastMath.bitwiseAnd(var7, 4032)] >>> var20;
						var9 += var17;
						var7 += var18;
						dest[var11++] = src[FastMath.bitwiseAnd(4032, var7) + (var9 >> 6)] >>> var20;
					} else {
						for (int var21 = 0; var21 < var19; ++var21) {
							dest[var11++] = src[(var9 >> 6) + FastMath.bitwiseAnd(4032, var7)] >>> var20;
							var7 += var18;
							var9 += var17;
							if ((3 & var21) == 3) {
								var20 = var6 >> 20;
								var9 = (var6 & 786432) + (4095 & var9);
								var6 += var0;
							}
						}
					}
				}

			}
		} catch (RuntimeException var22) {
			throw GenUtil.makeThrowable(var22, "p.B(" + var0 + ',' + var1 + ',' + var2 + ',' + var3 + ',' + var4 + ','
				+ (src != null ? "{...}" : "null") + ',' + var6 + ',' + var7 + ',' + var8 + ',' + var9 + ','
				+ (dest != null ? "{...}" : "null") + ',' + var11 + ',' + var12 + ',' + var13 + ',' + var14 + ')');
		}
	}

	static void shadeScanline(int var0, int var1, int var2, byte var3, int var4, int var5, int var6,
							  int var7, int[] var8, int[] var9, int var10, int var11, int var12, int var13, int var14, int var15) {
		try {

			if (var0 > 0) {
				int var16 = 0;
				int var17 = 0;
				if (var1 != 0) {
					var16 = var11 / var1 << 6;
					var17 = var15 / var1 << 6;
				}

				var7 <<= 2;
				if (var16 >= 0) {
					if (var16 > 4032) {
						var16 = 4032;
					}
				} else {
					var16 = 0;
				}

				if (var3 == 25) {
					for (int var20 = var0; var20 > 0; var20 -= 16) {
						var4 = var17;
						var12 = var16;
						var11 += var5;
						var1 += var6;
						var15 += var13;
						if (var1 != 0) {
							var17 = var15 / var1 << 6;
							var16 = var11 / var1 << 6;
						}

						if (var16 < 0) {
							var16 = 0;
						} else if (var16 > 4032) {
							var16 = 4032;
						}

						int var19 = var17 - var4 >> 4;
						int var18 = var16 - var12 >> 4;
						var12 += 786432 & var14;
						int var21 = var14 >> 20;
						var14 += var7;
						if (var20 >= 16) {
							if ((var2 = var8[(var12 >> 6) + (4032 & var4)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							++var10;
							var4 += var19;
							var12 += var18;
							if ((var2 = var8[(var12 >> 6) + (var4 & 4032)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							var4 += var19;
							++var10;
							var12 += var18;
							if ((var2 = var8[(var12 >> 6) + (4032 & var4)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							var4 += var19;
							++var10;
							var12 += var18;
							if ((var2 = var8[(4032 & var4) + (var12 >> 6)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							++var10;
							var12 += var18;
							var4 += var19;
							var21 = var14 >> 20;
							var12 = (786432 & var14) + (4095 & var12);
							var14 += var7;
							if ((var2 = var8[(var12 >> 6) + (4032 & var4)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							++var10;
							var12 += var18;
							var4 += var19;
							if ((var2 = var8[(var4 & 4032) + (var12 >> 6)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							++var10;
							var12 += var18;
							var4 += var19;
							if ((var2 = var8[(var4 & 4032) + (var12 >> 6)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							++var10;
							var12 += var18;
							var4 += var19;
							if ((var2 = var8[(var4 & 4032) + (var12 >> 6)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							var4 += var19;
							++var10;
							var12 += var18;
							var12 = (var12 & 4095) + (var14 & 786432);
							var21 = var14 >> 20;
							if ((var2 = var8[(var12 >> 6) + (var4 & 4032)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							var14 += var7;
							++var10;
							var12 += var18;
							var4 += var19;
							if ((var2 = var8[(var12 >> 6) + (4032 & var4)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							var4 += var19;
							var12 += var18;
							++var10;
							if ((var2 = var8[(var12 >> 6) + (4032 & var4)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							var12 += var18;
							var4 += var19;
							++var10;
							if ((var2 = var8[(var4 & 4032) + (var12 >> 6)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							++var10;
							var12 += var18;
							var4 += var19;
							var21 = var14 >> 20;
							var12 = (var14 & 786432) + (var12 & 4095);
							var14 += var7;
							if ((var2 = var8[(var4 & 4032) + (var12 >> 6)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							var4 += var19;
							var12 += var18;
							++var10;
							if ((var2 = var8[(var4 & 4032) + (var12 >> 6)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							++var10;
							var12 += var18;
							var4 += var19;
							if ((var2 = var8[(var4 & 4032) + (var12 >> 6)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							var4 += var19;
							++var10;
							var12 += var18;
							if ((var2 = var8[(4032 & var4) + (var12 >> 6)] >>> var21) != 0) {
								var9[var10] = var2;
							}

							++var10;
						} else {
							for (int var22 = 0; var20 > var22; ++var22) {
								if ((var2 = var8[(var12 >> 6) + (4032 & var4)] >>> var21) != 0) {
									var9[var10] = var2;
								}

								++var10;
								var12 += var18;
								var4 += var19;
								if ((3 & var22) == 3) {
									var21 = var14 >> 20;
									var12 = (4095 & var12) + (var14 & 786432);
									var14 += var7;
								}
							}
						}
					}

				}
			}
		} catch (RuntimeException var23) {
			throw GenUtil.makeThrowable(var23,
				"cb.B(" + var0 + ',' + var1 + ',' + var2 + ',' + var3 + ',' + var4 + ',' + var5 + ',' + var6 + ','
					+ var7 + ',' + (var8 != null ? "{...}" : "null") + ',' + (var9 != null ? "{...}" : "null")
					+ ',' + var10 + ',' + var11 + ',' + var12 + ',' + var13 + ',' + var14 + ',' + var15 + ')');
		}
	}

}
