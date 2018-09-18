package rsc.buffers;

import rsc.util.FastMath;
import rsc.util.GenUtil;

public final class StringEncryption {
	private int[] m_g;
	private byte[] m_i;
	private int[] m_j;

	final int encryptString(int right, byte[] dest, int destOffset, byte[] src, int left, int dummy) {
		try {
			if (dummy < 99) {
				this.encryptString(-58, (byte[]) null, 69, (byte[]) null, -39, 22);
			}

			int var7 = 0;
			right += left;

			int var8;
			for (var8 = destOffset << 3; left < right; ++left) {
				int var9 = src[left] & 255;
				int var10 = this.m_g[var9];
				byte var11 = this.m_i[var9];
				if (var11 == 0) {
					throw new RuntimeException("" + var9);
				}

				int var12 = var8 >> 3;
				int var13 = var8 & 7;
				var7 &= -var13 >> 31;
				int var14 = var12 + (var11 + var13 - 1 >> 3);
				var8 += var11;
				var13 += 24;
				dest[var12] = (byte) (var7 = FastMath.bitwiseOr(var7, var10 >>> var13));
				if (var12 < var14) {
					++var12;
					var13 -= 8;
					dest[var12] = (byte) (var7 = var10 >>> var13);
					if (var14 > var12) {
						++var12;
						var13 -= 8;
						dest[var12] = (byte) (var7 = var10 >>> var13);
						if (var12 < var14) {
							++var12;
							var13 -= 8;
							dest[var12] = (byte) (var7 = var10 >>> var13);
							if (var12 < var14) {
								++var12;
								var13 -= 8;
								dest[var12] = (byte) (var7 = var10 << -var13);
							}
						}
					}
				}
			}

			return (var8 + 7 >> 3) - destOffset;
		} catch (RuntimeException var15) {
			throw GenUtil.makeThrowable(var15, "aa.B(" + right + ',' + (dest != null ? "{...}" : "null") + ',' + destOffset
					+ ',' + (src != null ? "{...}" : "null") + ',' + left + ',' + dummy + ')');
		}
	}

	final int decryptString(byte[] src, byte[] dest, int destOffset, int srcOffset, int dummy, int count) {
		try {
			if (count == 0) {
				return 0;
			} else {
				int var7 = 0;
				count += destOffset;
				if (dummy != -1) {
					this.encryptString(105, (byte[]) null, 82, (byte[]) null, 125, -45);
				}

				int var8 = srcOffset;

				while (true) {
					byte var9 = src[var8];
					if (var9 >= 0) {
						++var7;
					} else {
						var7 = this.m_j[var7];
					}

					int var10;
					if ((var10 = this.m_j[var7]) < 0) {
						dest[destOffset++] = (byte) (~var10);
						if (destOffset >= count) {
							break;
						}

						var7 = 0;
					}

					if ((64 & var9) != 0) {
						var7 = this.m_j[var7];
					} else {
						++var7;
					}

					if ((var10 = this.m_j[var7]) < 0) {
						dest[destOffset++] = (byte) (~var10);
						if (count <= destOffset) {
							break;
						}

						var7 = 0;
					}

					if ((var9 & 32) == 0) {
						++var7;
					} else {
						var7 = this.m_j[var7];
					}

					if ((var10 = this.m_j[var7]) < 0) {
						dest[destOffset++] = (byte) (~var10);
						if (count <= destOffset) {
							break;
						}

						var7 = 0;
					}

					if ((16 & var9) != 0) {
						var7 = this.m_j[var7];
					} else {
						++var7;
					}

					if ((var10 = this.m_j[var7]) < 0) {
						dest[destOffset++] = (byte) (~var10);
						if (destOffset >= count) {
							break;
						}

						var7 = 0;
					}

					if ((var9 & 8) != 0) {
						var7 = this.m_j[var7];
					} else {
						++var7;
					}

					if ((var10 = this.m_j[var7]) < 0) {
						dest[destOffset++] = (byte) (~var10);
						if (destOffset >= count) {
							break;
						}

						var7 = 0;
					}

					if ((4 & var9) != 0) {
						var7 = this.m_j[var7];
					} else {
						++var7;
					}

					if ((var10 = this.m_j[var7]) < 0) {
						dest[destOffset++] = (byte) (~var10);
						if (destOffset >= count) {
							break;
						}

						var7 = 0;
					}

					if ((2 & var9) == 0) {
						++var7;
					} else {
						var7 = this.m_j[var7];
					}

					if ((var10 = this.m_j[var7]) < 0) {
						dest[destOffset++] = (byte) (~var10);
						if (destOffset >= count) {
							break;
						}

						var7 = 0;
					}

					if ((1 & var9) != 0) {
						var7 = this.m_j[var7];
					} else {
						++var7;
					}

					if ((var10 = this.m_j[var7]) < 0) {
						dest[destOffset++] = (byte) (~var10);
						if (destOffset >= count) {
							break;
						}

						var7 = 0;
					}

					++var8;
				}

				return 1 - srcOffset + var8;
			}
		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11, "aa.A(" + (src != null ? "{...}" : "null") + ','
					+ (dest != null ? "{...}" : "null") + ',' + destOffset + ',' + srcOffset + ',' + dummy + ',' + count + ')');
		}
	}

	public static byte[] asByte(int... is) {
		byte[] res = new byte[is.length];
		for (int i = 0; i < res.length; i++)
			res[i] = (byte) is[i];
		return res;
	}

	public StringEncryption(byte[] var1) {
		try {
			int var2 = var1.length;
			this.m_g = new int[var2];
			this.m_i = var1;
			this.m_j = new int[8];
			int[] var3 = new int[33];
			int var4 = 0;

			for (int var5 = 0; var2 > var5; ++var5) {
				byte var6 = var1[var5];
				if (var6 != 0) {
					int var7 = 1 << 32 - var6;
					int var8 = var3[var6];
					this.m_g[var5] = var8;
					int var9;
					int var10;
					int var11;
					int var12;
					if ((var7 & var8) != 0) {
						var9 = var3[var6 - 1];
					} else {
						for (var10 = var6 - 1; var10 >= 1; --var10) {
							var11 = var3[var10];
							if (var8 != var11) {
								break;
							}

							var12 = 1 << 32 - var10;
							if ((var11 & var12) != 0) {
								var3[var10] = var3[var10 - 1];
								break;
							}

							var3[var10] = FastMath.bitwiseOr(var12, var11);
						}

						var9 = var8 | var7;
					}

					var3[var6] = var9;

					for (var10 = var6 + 1; var10 <= 32; ++var10) {
						if (var3[var10] == var8) {
							var3[var10] = var9;
						}
					}

					var10 = 0;

					for (var11 = 0; var6 > var11; ++var11) {
						var12 = Integer.MIN_VALUE >>> var11;
						if ((var12 & var8) == 0) {
							++var10;
						} else {
							if (this.m_j[var10] == 0) {
								this.m_j[var10] = var4;
							}

							var10 = this.m_j[var10];
						}

						if (this.m_j.length <= var10) {
							int[] var13 = new int[this.m_j.length * 2];

							for (int var14 = 0; var14 < this.m_j.length; ++var14) {
								var13[var14] = this.m_j[var14];
							}

							this.m_j = var13;
						}

						var12 >>>= 1;
					}

					if (var10 >= var4) {
						var4 = var10 + 1;
					}

					this.m_j[var10] = ~var5;
				}
			}

		} catch (RuntimeException var15) {
			throw GenUtil.makeThrowable(var15, "aa.<init>(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}
}
