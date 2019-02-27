package orsc.buffers;

import java.math.BigInteger;

import orsc.MiscFunctions;
import orsc.util.GenUtil;

public class RSBuffer extends RSBuffer_Base {
	public byte[] dataBuffer;
	public int packetEnd;

	public RSBuffer(int size) {
		try {
			this.dataBuffer = MiscFunctions.clazz_10_a(size, (byte) -104);
			this.packetEnd = 0;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "tb.<init>(" + size + ')');
		}
	}

	public RSBuffer(byte[] var1) {
		try {
			this.dataBuffer = var1;
			this.packetEnd = 0;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "tb.<init>(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	public final void putNullThenString(String str, int var2) {
		try {

			int var3 = str.indexOf(0);
			if (var3 < 0) {
				this.dataBuffer[this.packetEnd++] = 0;
				this.packetEnd += RSBufferUtils.putStringIntoBytes(str, 0, str.length(), this.dataBuffer,
					this.packetEnd);
				this.dataBuffer[this.packetEnd++] = 0;
			} else {
				throw new IllegalArgumentException("");
			}
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "tb.O(" + (str != null ? "{...}" : "null") + ',' + var2 + ')');
		}
	}

	public void putLong(long l) {
		putInt((int) (l >> 32));
		putInt((int) (l));
	}

	public final void put24(int val) {
		try {
			this.dataBuffer[this.packetEnd++] = (byte) (val >> 16);

			this.dataBuffer[this.packetEnd++] = (byte) (val >> 8);
			this.dataBuffer[this.packetEnd++] = (byte) val;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "tb.I(" + val + ',' + "dummy" + ')');
		}
	}

	public final boolean isCRCValid() {
		try {
			this.packetEnd -= 4;

			int crcActual = RSBufferUtils.computeCRC(this.packetEnd, 107, this.dataBuffer, 0);
			int crcWanted = this.get32();
			return crcWanted == crcActual;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "tb.BA(" + -422797528 + ')');
		}
	}

	private void readBytes(int count, byte[] out) {
		try {
			for (int i = 0; i < count; ++i) {
				out[i] = this.dataBuffer[this.packetEnd++];
			}

		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6,
				"tb.N(" + "dummy" + ',' + 0 + ',' + count + ',' + (out != null ? "{...}" : "null") + ')');
		}
	}

	private void writeBytes(byte[] src, int count) {
		try {
			for (int i = 0; i < count; ++i) {
				this.dataBuffer[this.packetEnd++] = src[i];
			}

		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6,
				"tb.AA(" + 0 + ',' + -123 + ',' + count + ',' + (src != null ? "{...}" : "null") + ')');
		}
	}

	public final void putByte(int var1) {
		try {
			this.dataBuffer[this.packetEnd++] = (byte) var1;

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "tb.K(" + var1 + ',' + "dummy" + ')');
		}
	}

	public final void putString(String var2) {
		try {

			byte[] stringBytes = var2.getBytes();
			for (byte b : stringBytes)
				putByte(b);
			putByte(10);
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "tb.J(" + -39 + ',' + (var2 != null ? "{...}" : "null") + ')');
		}
	}

	public final String readString() {

		StringBuilder bldr = new StringBuilder();
		byte b;
		while ((b = dataBuffer[this.packetEnd++]) != 10) {
			bldr.append((char) b);
		}
		return bldr.toString();

	}

	public final void putShort(int val) {
		try {

			this.dataBuffer[this.packetEnd++] = (byte) (val >> 8);
			this.dataBuffer[this.packetEnd++] = (byte) val;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "tb.F(" + "dummy" + ',' + val + ')');
		}
	}

	public final byte getByte() {
		try {

			return this.dataBuffer[this.packetEnd++];
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "tb.L(" + "dummy" + ')');
		}
	}

	public final int getUnsignedByte() {
		try {

			return this.dataBuffer[this.packetEnd++] & 255;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "tb.E(" + 104 + ')');
		}
	}

	public final void a(int var2, int[] var3, int var4) {
		try {

			int var5 = this.packetEnd;

			this.packetEnd = var2;
			int var6 = (var4 - var2) / 8;

			for (int var7 = 0; var7 < var6; ++var7) {
				int var8 = this.get32();
				int var9 = this.get32();
				int var10 = 0;
				int var11 = -1640531527;

				for (int var12 = 32; var12-- > 0; var9 += var8 + (var8 >>> 5 ^ var8 << 4)
					^ var10 + var3[(7145 & var10) >>> 11]) {
					var8 += (var9 << 4 ^ var9 >>> 5) + var9 ^ var10 + var3[var10 & 3];
					var10 += var11;
				}

				this.packetEnd -= 8;
				this.putInt(var8);
				this.putInt(var9);

				this.packetEnd = var5;
			}
		} catch (RuntimeException var13) {
			var13.printStackTrace();
		}
	}

	public final void put16_Offset(int offset) {
		try {
			this.dataBuffer[this.packetEnd - offset - 2] = (byte) (offset >> 8);

			this.dataBuffer[this.packetEnd - offset - 1] = (byte) offset;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "tb.R(" + offset + ',' + "dummy" + ')');
		}
	}

	final int getSmart08_16() {
		try {

			int var2 = 255 & this.dataBuffer[this.packetEnd];
			return var2 < 128 ? this.getUnsignedByte() : this.getShort() - '\u8000';
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "tb.P(" + "dummy" + ')');
		}
	}

	public final int get16_V2() {
		try {
			this.packetEnd += 2;

			int strBegin = (255 & this.dataBuffer[this.packetEnd - 1])
				+ (this.dataBuffer[this.packetEnd - 2] << 8 & '\uff00');
			if (strBegin > 32767) {
				strBegin -= 65536;
			}

			return strBegin;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "tb.DA(" + false + ')');
		}
	}

	public final int getSmart16_32() {
		try {

			return (this.dataBuffer[this.packetEnd] < 0 ? Integer.MAX_VALUE & this.get32() : this.getShort());
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "tb.W(" + "dummy" + ')');
		}
	}

	final void putSmart08_16(int val) {
		try {

			if (val >= 0 && val < 128) {
				this.putByte(val);
			} else if (val >= 0 && val < '\u8000') {
				this.putShort('\u8000' + val);
			} else {
				throw new IllegalArgumentException();
			}
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "tb.H(" + val + ',' + "dummy" + ')');
		}
	}

	public final void putInt(int val) {
		try {

			this.dataBuffer[this.packetEnd++] = (byte) (val >> 24);
			this.dataBuffer[this.packetEnd++] = (byte) (val >> 16);
			this.dataBuffer[this.packetEnd++] = (byte) (val >> 8);
			this.dataBuffer[this.packetEnd++] = (byte) val;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "tb.Q(" + "dummy" + ',' + val + ')');
		}
	}

	public final int getShort() {
		try {

			this.packetEnd += 2;
			return ((this.dataBuffer[this.packetEnd - 2] & 255) << 8)
				+ (255 & this.dataBuffer[this.packetEnd - 1]);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "tb.G(" + "dummy" + ')');
		}
	}

	public final int get32() {
		try {
			this.packetEnd += 4;

			return (this.dataBuffer[this.packetEnd - 3] << 16 & 16711680)
				+ (this.dataBuffer[this.packetEnd - 4] << 24 & -16777216)
				+ (0xFF00 & this.dataBuffer[this.packetEnd - 2] << 8)
				+ (this.dataBuffer[this.packetEnd - 1] & 255);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "tb.U(" + -129 + ')');
		}
	}

	public final long getLong(int var1) {
		try {
			if (var1 != 0) {
				return -13L;
			} else {

				long var2 = (long) this.get32() & 4294967295L;
				long var4 = (long) this.get32() & 4294967295L;
				return (var2 << 1382465952) - -var4;
			}
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "tb.S(" + var1 + ')');
		}
	}

	public final byte[] toByteArray(int ignore) {
		try {

			byte[] bites = new byte[this.packetEnd];

			if (this.packetEnd - ignore >= 0)
				System.arraycopy(this.dataBuffer, ignore, bites, ignore, this.packetEnd - ignore);

			return bites;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "tb.T(" + ignore + ')');
		}
	}

	public final void encodeWithRSA(BigInteger var1, BigInteger var3) {
		try {

			int pointerPosition = this.packetEnd;
			this.packetEnd = 0;

			byte[] encodedBuffer = new byte[pointerPosition];

			this.readBytes(pointerPosition, encodedBuffer);
			BigInteger var7 = new BigInteger(encodedBuffer);
			BigInteger var8 = var7.modPow(var3, var1);
			byte[] var9 = var8.toByteArray();
			this.packetEnd = 0;
			this.putShort(var9.length);
			this.writeBytes(var9, var9.length);
		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10,
				"tb.V(" + (var1 != null ? "{...}" : "null") + ',' + (var3 != null ? "{...}" : "null") + ')');
		}
	}
}
