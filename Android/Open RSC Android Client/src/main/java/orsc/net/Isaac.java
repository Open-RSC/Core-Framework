package orsc.net;

import orsc.util.FastMath;
import orsc.util.GenUtil;

public final class Isaac {
	private static final int GOLDEN = -1640531527;
	private int gen;
	private int m_d;
	private int[] mem;
	private int[] rsl;
	private int m_m;
	private int m_f;

	Isaac(int[] seed) {
		try {
			this.rsl = new int[256];
			this.mem = new int[256];

			System.arraycopy(seed, 0, this.rsl, 0, seed.length);

			this.init();
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "o.<init>(" + "{...}" + ')');
		}
	}

	public final int next() {
		try {
			if (this.gen-- == 0) {
				this.generate();
				this.gen = 255;
			}

			return this.rsl[this.gen];
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "o.C(" + "dumb" + ')');
		}
	}

	private void generate() {
		try {
			this.m_d += ++this.m_f;

			for (int i = 0; i < 256; ++i) {
				int var3 = this.mem[i];
				int var5 = i & 3;
				if (var5 == 0) {
					this.m_m ^= this.m_m << 13;
				} else if (var5 != 1) {
					if (var5 == 2) {
						this.m_m ^= this.m_m << 2;
					} else {
						this.m_m ^= this.m_m >>> 16;
					}
				} else {
					this.m_m ^= this.m_m >>> 6;
				}

				this.m_m += this.mem[255 & 128 + i];
				int var4;
				this.mem[i] = var4 = this.m_m + this.mem[FastMath.bitwiseAnd(1020, var3) >> 2] + this.m_d;
				this.rsl[i] = this.m_d = this.mem[FastMath.bitwiseAnd(255, var4 >> 8 >> 2)] + var3;
			}

		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "o.D(" + -110 + ')');
		}
	}

	private void init() {
		try {
			int c = GOLDEN;
			int a = GOLDEN;
			int h = GOLDEN;
			int g = GOLDEN;
			int e = GOLDEN;

			int b = GOLDEN;
			int f = GOLDEN;
			int d = GOLDEN;

			for (int i = 0; i < 4; ++i) {
				a ^= b << 11;
				d += a;
				b += c;
				b ^= c >>> 2;
				e += b;
				c += d;
				c ^= d << 8;
				d += e;
				f += c;
				d ^= e >>> 16;
				e += f;
				g += d;
				e ^= f << 10;
				h += e;
				f += g;
				f ^= g >>> 4;
				g += h;
				a += f;
				g ^= h << 8;
				h += a;
				b += g;
				h ^= a >>> 9;
				a += b;
				c += h;
			}

			for (int i = 0; i < 256; i += 8) {
				a += this.rsl[i];
				e += this.rsl[4 + i];
				c += this.rsl[2 + i];
				h += this.rsl[i + 7];
				f += this.rsl[i + 5];
				d += this.rsl[3 + i];
				g += this.rsl[6 + i];
				b += this.rsl[i + 1];
				a ^= b << 11;
				d += a;
				b += c;
				b ^= c >>> 2;
				c += d;
				e += b;
				c ^= d << 8;
				d += e;
				f += c;
				d ^= e >>> 16;
				g += d;
				e += f;
				e ^= f << 10;
				h += e;
				f += g;
				f ^= g >>> 4;
				a += f;
				g += h;
				g ^= h << 8;
				b += g;
				h += a;
				h ^= a >>> 9;
				c += h;
				a += b;
				this.mem[i] = a;
				this.mem[i + 1] = b;
				this.mem[2 + i] = c;
				this.mem[3 + i] = d;
				this.mem[4 + i] = e;
				this.mem[i + 5] = f;
				this.mem[i + 6] = g;
				this.mem[i + 7] = h;
			}

			for (int i = 0; i < 256; i += 8) {
				g += this.mem[6 + i];
				b += this.mem[i + 1];
				h += this.mem[7 + i];
				e += this.mem[4 + i];
				f += this.mem[5 + i];
				c += this.mem[i + 2];
				d += this.mem[i + 3];
				a += this.mem[i];
				a ^= b << 11;
				b += c;
				d += a;
				b ^= c >>> 2;
				c += d;
				e += b;
				c ^= d << 8;
				f += c;
				d += e;
				d ^= e >>> 16;
				e += f;
				g += d;
				e ^= f << 10;
				f += g;
				h += e;
				f ^= g >>> 4;
				g += h;
				a += f;
				g ^= h << 8;
				h += a;
				b += g;
				h ^= a >>> 9;
				a += b;
				c += h;
				this.mem[i] = a;
				this.mem[i + 1] = b;
				this.mem[i + 2] = c;
				this.mem[3 + i] = d;
				this.mem[i + 4] = e;
				this.mem[i + 5] = f;
				this.mem[i + 6] = g;
				this.mem[7 + i] = h;
			}

			this.generate();
			this.gen = 256;
		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11, "o.E(" + -2 + ')');
		}
	}
}