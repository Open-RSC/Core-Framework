package orsc.util;

public class FastMath {
	public static final int[] bitwiseMaskForShift = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095,
		8191, 16383, 32767, '\uffff', 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215,
		33554431, 67108863, 134217727, 268435455, 536870911, 1073741823, Integer.MAX_VALUE, -1};
	public static int[] trigTable256 = new int[512];
	public static int[] trigTable1024 = new int[2048];
	private static int[] trigTable_256 = new int[512];
	public static int[] trigTable_1024 = new int[2048];

	static {
		for (int i = 0; i < 256; ++i) {
			FastMath.trigTable256[i] = (int) (32768.0D * Math.sin(0.02454369D * (double) i));
			FastMath.trigTable256[256 + i] = (int) (32768.0D * Math.cos((double) i * 0.02454369D));
		}

		for (int i = 0; i < 1024; ++i) {
			FastMath.trigTable1024[i] = (int) (Math.sin((double) i * 0.00613592315D) * 32768.0D);
			FastMath.trigTable1024[i + 1024] = (int) (Math.cos((double) i * 0.00613592315D) * 32768.0D);
		}

		for (int i = 0; i < 256; ++i) {
			FastMath.trigTable_256[i] = (int) (Math.sin(0.02454369D * (double) i) * 32768.0D);
			FastMath.trigTable_256[256 + i] = (int) (Math.cos((double) i * 0.02454369D) * 32768.0D);
		}

		for (int i = 0; i < 1024; ++i) {
			FastMath.trigTable_1024[i] = (int) (Math.sin((double) i * 0.00613592315D) * 32768.0D);
			FastMath.trigTable_1024[1024 + i] = (int) (Math.cos((double) i * 0.00613592315D) * 32768.0D);
		}
	}

	public static int bitwiseAnd(int var0, int var1) {
		try {
			return var0 & var1;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ib.QA(" + var0 + ',' + var1 + ')');
		}
	}

	public static int bitwiseOr(int var0, int var1) {
		try {
			return var0 | var1;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "d.B(" + var0 + ',' + var1 + ')');
		}
	}

	public static int byteToUByte(byte val) {
		try {
			return val & 255;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "nb.G(" + "dummy" + ',' + val + ')');
		}
	}

	/**
	 * Returns 1 if var0 is a power of two. Otherwise returns the closest power
	 * of two.
	 */
	public static int nearestPowerOfTwo(int var0, byte var1) {
		try {
			var0 = (0x55555555 & var0 >>> 1) + (0x55555555 & var0);
			var0 = ((var0 & -858993460) >>> 2) + (0x33333333 & var0);
			var0 = var0 + (var0 >>> 4) & 0x0F0F0F0F;
			var0 += var0 >>> 8;
			var0 += var0 >>> 16;
			return 255 & var0;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ta.B(" + var0 + ',' + var1 + ')');
		}
	}

	public static int nextPowerOfTwo(int n) {
		try {
			--n;
			n |= n >>> 1;
			n |= n >>> 2;
			n |= n >>> 4;
			n |= n >>> 8;
			n |= n >>> 16;
			return n + 1;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "aa.C(" + n + ',' + false + ')');
		}
	}
}
