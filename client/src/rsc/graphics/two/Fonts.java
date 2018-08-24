package rsc.graphics.two;

public class Fonts {

	static boolean[] fontAntiAliased = new boolean[] { false, false, false, false, false, false, false, false, false,
			false, false, false };
	public static byte[][] fontData = new byte[50][];
	private static int tmpFontDataHead = 0;
	public static int[] inputFilterCharFontAddr;
	public static String inputFilterChars;

	static {
		Fonts.inputFilterChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\u00a3$%^&*()-_=+[{]};:\'@#~,<.>/?\\| ";
		Fonts.inputFilterCharFontAddr = new int[256];
		for (int code = 0; code < 256; ++code) {
			int index = Fonts.inputFilterChars.indexOf(code);
			if (index == -1) {
				index = 74;
			}

			Fonts.inputFilterCharFontAddr[code] = index * 9;
		}
	}

	public static int addFont(byte bytes[]) {
		fontData[tmpFontDataHead] = bytes;
		return tmpFontDataHead++;
	}
}
