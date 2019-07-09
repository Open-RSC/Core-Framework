package orsc.graphics.two;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.ItemDef;
import com.openrsc.client.entityhandling.defs.SpriteDef;
import com.openrsc.client.entityhandling.defs.extras.AnimationDef;
import com.openrsc.client.model.Sprite;
import com.openrsc.data.DataConversions;
import orsc.Config;
import orsc.MiscFunctions;
import orsc.mudclient;
import orsc.util.FastMath;
import orsc.util.GenUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class GraphicsController {
	public enum SPRITE_LAYER {
		MINIMAP, WORLDMAP, SHOP
	}

	public boolean interlace = false;

	// public int[][] image2D_colorLookupTable;
	// public int[] image2D_width;
	// public int[] image2D_height;
	// private boolean[] image2D_hasAlpha;
	// private int[] image2D_xOffset;
	// private int[] image2D_yOffset;
	// public int[] image2D_setParam2;
	// public int[] image2D_setParam3;
	// public byte[][] spriteColours;
	public boolean loggedIn = false;
	public int height2;
	public int[] pixelData;
	public int width2;
	public Sprite[] sprites;
	public Sprite[] spriteVerts = new Sprite[3];
	public Sprite minimapSprite = new Sprite();

	private int clipTop = 0;
	private int iconSpriteIndex;
	private int clipLeft = 0;
	private int[] trigTable256;
	private int clipRight = 0;
	private int[] m_M;
	private int clipBottom = 0;
	private int[] m_t;
	private int[] m_tb;
	private int[] m_Tb;
	private int[] m_Wb;
	public Map<String, List<Sprite>> spriteTree = new HashMap<String, List<Sprite>>();
	public static Map<String, Integer> animationMap = new HashMap<>();

	// public int[][] image2D_pixels;
	private int[] m_Xb;
	private ZipFile spriteArchive;

	GraphicsController(int var1, int var2, int var3) {
		try {
			this.clipBottom = var2;
			this.clipRight = var1;
			this.pixelData = new int[var1 * var2];
			this.height2 = var2;
			this.width2 = var1;
			try {
				if (Config.S_WANT_CUSTOM_SPRITES) {
					spriteArchive = new ZipFile(Config.F_CACHE_DIR + File.separator + "Custom_Sprites.orsc");
				} else {
					spriteArchive = new ZipFile(Config.F_CACHE_DIR + File.separator + "Authentic_Sprites.orsc");
					sprites = new Sprite[var3];
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "ua.<init>(" + var1 + ',' + var2 + ',' + var3 + ')' + ')');
		}
	}

	public static void a(int var0, int[] var1, int var2, int[] var3, int var4, int var5, int var6, int var7) {
		try {

			if (var2 < 0) {
				var4 = var1[('\uffa7' & var0) >> 8];
				var5 <<= 2;
				var0 += var5;
				int var8 = var2 / 16;

				int var9;
				for (var9 = var8; var9 < 0; ++var9) {
					var3[var6++] = var4 + FastMath.bitwiseAnd(0x7F7F7F, var3[var6] >> 1);
					var3[var6++] = var4 + FastMath.bitwiseAnd(0x7F7F7F, var3[var6] >> 1);
					var3[var6++] = var4 + (FastMath.bitwiseAnd(var3[var6], 0xFEFEFF) >> 1);
					var3[var6++] = var4 + (FastMath.bitwiseAnd(0xFEFEFF, var3[var6]) >> 1);
					var4 = var1[255 & var0 >> 8];
					var0 += var5;
					var3[var6++] = FastMath.bitwiseAnd(var3[var6] >> 1, 0x7F7F7F) + var4;
					var3[var6++] = FastMath.bitwiseAnd(var3[var6] >> 1, 0x7F7F7F) + var4;
					var3[var6++] = (FastMath.bitwiseAnd(0xFEFEFF, var3[var6]) >> 1) + var4;
					var3[var6++] = FastMath.bitwiseAnd(var3[var6] >> 1, 0x7F7F7F) + var4;
					var4 = var1[var0 >> 8 & 255];
					var3[var6++] = (FastMath.bitwiseAnd(var3[var6], 0xFEFEFE) >> 1) + var4;
					var0 += var5;
					var3[var6++] = (FastMath.bitwiseAnd(0xFEFEFF, var3[var6]) >> 1) + var4;
					var3[var6++] = var4 + FastMath.bitwiseAnd(var3[var6] >> 1, 0x7F7F7F);
					var3[var6++] = var4 + FastMath.bitwiseAnd(0x7F7F7F, var3[var6] >> 1);
					var4 = var1[('\uff16' & var0) >> 8];
					var0 += var5;
					var3[var6++] = (FastMath.bitwiseAnd(var3[var6], 0xFEFEFF) >> 1) + var4;
					var3[var6++] = var4 + FastMath.bitwiseAnd(var3[var6] >> 1, 0x7F7F7F);
					var3[var6++] = var4 + (FastMath.bitwiseAnd(var3[var6], 0xFEFEFF) >> 1);
					var3[var6++] = var4 + (FastMath.bitwiseAnd(0xFEFEFF, var3[var6]) >> 1);
					var4 = var1[var0 >> 8 & 255];
					var0 += var5;
				}

				var8 = -(var2 % 16);

				for (var9 = var7; var8 > var9; ++var9) {
					var3[var6++] = FastMath.bitwiseAnd(var3[var6] >> 1, 0x7F7F7F) + var4;
					if ((3 & var9) == 3) {
						var4 = var1[(var0 & '\uff38') >> 8];
						var0 += var5;
						var0 += var5;
					}
				}

			}
		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10,
				"ua.S(" + var0 + ',' + (var1 != null ? "{...}" : "null") + ',' + var2 + ','
					+ (var3 != null ? "{...}" : "null") + ',' + var4 + ',' + var5 + ',' + var6 + ',' + var7
					+ ')');
		}
	}

	public void resize(int width, int height) {
		this.clipBottom = height;
		this.clipRight = width;
		this.pixelData = new int[width * height];
		this.height2 = height;
		this.width2 = width;
	}

	private void plotCharacter(boolean antiAliased, byte[] fontData, int x, int color, int indexAddr, int y) {
		try {

			int width = fontData[indexAddr + 3];
			int height = fontData[indexAddr + 4];
			int left = x + fontData[indexAddr + 5];
			int top = y - fontData[indexAddr + 6];
			int dataAddr = (fontData[indexAddr] << 14) + (fontData[indexAddr + 1] << 7) + fontData[indexAddr + 2];
			int startPixel = left + top * this.width2;

			int rowStride = this.width2 - width;
			if (top < this.clipTop) {
				int lost = this.clipTop - top;
				dataAddr += lost * width;
				startPixel += this.width2 * lost;
				height -= lost;
				top = this.clipTop;
			}

			if (top + height >= this.clipBottom) {
				height -= 1 + top + height - this.clipBottom;
			}

			int srcStride = 0;
			if (this.clipLeft > left) {
				int lost = this.clipLeft - left;
				srcStride += lost;
				width -= lost;
				dataAddr += lost;
				left = this.clipLeft;
				rowStride += lost;
				startPixel += lost;
			}

			if (this.clipRight <= width + left) {
				int lost = width + left - this.clipRight + 1;
				rowStride += lost;
				srcStride += lost;
				width -= lost;
			}

			if (width > 0 && height > 0) {
				if (antiAliased) {
					this.plotLetterAntialiased(fontData, color, width, startPixel, height, srcStride, rowStride,
						this.pixelData, dataAddr);
				} else {
					this.plotLetter(color, this.pixelData, startPixel, rowStride, height, width, dataAddr, fontData,
						srcStride);
				}
			}
		} catch (RuntimeException var17) {
			throw GenUtil.makeThrowable(var17, "ua.SA(" + "dummy" + ',' + antiAliased + ','
				+ (fontData != null ? "{...}" : "null") + ',' + x + ',' + color + ',' + indexAddr + ',' + y + ')');
		}
	}

	public final void clearClip() {
		try {
			this.clipRight = this.width2;
			this.clipLeft = 0;
			this.clipTop = 0;
			this.clipBottom = this.height2;

		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ua.JA(" + "dummy" + ')');
		}
	}

	public final void spriteClipping(Sprite sprite, byte var2, int height, int var4, int width, int var6, int var7) {
		try {


			try {
				int spriteWidth = sprite.getWidth();// this.image2D_width[sprite];
				int spriteHeight = sprite.getHeight();
				int var10 = 0;
				int var11 = 0;
				int scaleX = (spriteWidth << 16) / width;
				int scaleY = (spriteHeight << 16) / height;
				if (sprite.requiresShift()) {
					int var14 = sprite.getSomething1();
					int var15 = sprite.getSomething2();
					if (var14 == 0 || var15 == 0) {
						return;
					}

					scaleY = (var15 << 16) / height;
					var6 += (var15 + height * sprite.getYShift() - 1) / var15;
					var4 += (var14 + sprite.getXShift() * width - 1) / var14;
					scaleX = (var14 << 16) / width;
					if (width * sprite.getXShift() % var14 != 0) {
						var10 = (var14 - sprite.getXShift() * width % var14 << 16) / width;
					}

					if (sprite.getYShift() * height % var15 != 0) {
						var11 = (var15 - sprite.getYShift() * height % var15 << 16) / height;
					}

					width = width * (sprite.getWidth() - (var10 >> 16)) / var14;
					height = (sprite.getHeight() - (var11 >> 16)) * height / var15;
				}

				int var14 = var6 * this.width2 + var4;
				if (var2 > -121) {
					return;
				}

				int var16;
				if (this.clipTop > var6) {
					var16 = this.clipTop - var6;
					height -= var16;
					var6 = 0;
					var14 += this.width2 * var16;
					var11 += scaleY * var16;
				}

				int var15 = this.width2 - width;
				if (var4 < this.clipLeft) {
					var16 = this.clipLeft - var4;
					var4 = 0;
					var10 += var16 * scaleX;
					var14 += var16;
					width -= var16;
					var15 += var16;
				}

				if (var6 + height >= this.clipBottom) {
					height -= 1 + height + (var6 - this.clipBottom);
				}

				if (var4 + width >= this.clipRight) {
					var16 = 1 + var4 + (width - this.clipRight);
					var15 += var16;
					width -= var16;
				}

				byte var19 = 1;
				if (this.interlace) {
					scaleY += scaleY;
					var15 += this.width2;
					if ((var6 & 1) != 0) {
						var14 += this.width2;
						--height;
					}

					var19 = 2;
				}

				this.plot_tran_scale(var19, var11, width, (byte) -61, scaleY, spriteWidth, scaleX, height, var14,
					sprite.getPixels(), 0, var10, var15, var7, this.pixelData);
			} catch (Exception var17) {
				System.out.println("error in sprite clipping routine");
			}

		} catch (RuntimeException var18) {
			throw GenUtil.makeThrowable(var18, "ua.E(" + sprite.getID() + ',' + var2 + ',' + height + ',' + var4 + ',' + width
				+ ',' + var6 + ',' + var7 + ')');
		}
	}
	public Sprite spriteSelect(ItemDef item) {
		if (!Config.S_WANT_CUSTOM_SPRITES)
			return sprites[item.authenticSpriteID + mudclient.spriteItem];

		String[] location = item.getSpriteLocation().split(":");
		Sprite retVal = spriteTree.get(location[0]).get(Integer.parseInt(location[1]));
		return retVal;
	}

	public Sprite spriteSelect(AnimationDef animation, int offset) {
		if (!Config.S_WANT_CUSTOM_SPRITES)
			return sprites[animation.getNumber()+offset];

		Sprite sprite = spriteTree.get("animations").get(animationMap.get(animation.name) + offset);
		return sprite;
	}

	public Sprite spriteSelect(SpriteDef sprite) {
		if (!Config.S_WANT_CUSTOM_SPRITES)
			return sprites[sprite.getAuthenticSpriteID()];

		String[] location = sprite.getSpriteLocation().split(":");

		Sprite retVal = spriteTree.get(location[0]).get(Integer.parseInt(location[1]));
		return retVal;
	}

	public final void a(Sprite sprite, int var2, int var3, int var4, int var5) {
		try {
			if (sprite == null) {
				System.out.println("Sprite missing: ");
				return;
			}
			if (sprite.requiresShift()) {
				var3 += sprite.getXShift();
				var5 += sprite.getYShift();
			}


			int var6 = this.width2 * var5 + var3;
			int var7 = var2;
			int var8 = sprite.getHeight();
			int var9 = sprite.getWidth();
			int var10 = this.width2 - var9;
			int var11 = 0;
			int var12;
			if (var5 < this.clipTop) {
				var12 = this.clipTop - var5;
				var5 = this.clipTop;
				var8 -= var12;
				var7 = var2 + var12 * var9;
				var6 += var12 * this.width2;
			}

			if (var8 + var5 >= this.clipBottom) {
				var8 -= 1 + var8 + (var5 - this.clipBottom);
			}

			if (var3 < this.clipLeft) {
				var12 = this.clipLeft - var3;
				var10 += var12;
				var11 += var12;
				var6 += var12;
				var7 += var12;
				var3 = this.clipLeft;
				var9 -= var12;
			}

			if (this.clipRight <= var3 + var9) {
				var12 = var3 - (-var9 - 1) - this.clipRight;
				var10 += var12;
				var9 -= var12;
				var11 += var12;
			}

			if (var9 > 0 && var8 > 0) {
				byte var14 = 1;
				if (this.interlace) {
					var11 += sprite.getWidth();
					var14 = 2;
					if ((1 & var5) != 0) {
						--var8;
						var6 += this.width2;
					}

					var10 += this.width2;
				}
				// TODO:
				// if (this.image2D_pixels[sprite] == null) {
				// this.a(var7, var10, this.spriteColours[sprite], var14, false,
				// var8, var11, var4, var9, this.pixelData,
				// this.image2D_colorLookupTable[sprite], var6);
				// } else {
				this.a(var7, var8, var6, sprite.getPixels(), 0, var4, var14, this.pixelData, -107, var11,
					var10, var9);
				// }

			}
		} catch (RuntimeException var13) {
			throw GenUtil.makeThrowable(var13,
				"ua.T(" + sprite + ',' + var2 + ',' + var3 + ',' + var4 + ',' + var5 + ')');
		}
	}

	public final void spriteClip3(int var1, int var2, Sprite sprite, int var4, int var5, byte var6, int var7) {
		try {


			try {
				int spriteWidth = sprite.getWidth();
				int spriteHeight = sprite.getHeight();
				int var10 = 0;
				int var11 = 0;
				int var12 = (spriteWidth << 16) / var7;
				int var13 = (spriteHeight << 16) / var5;
				int var14;
				int var15;
				if (sprite.requiresShift()) {
					var14 = sprite.getSomething1();
					var15 = sprite.getSomething2();
					if (var14 == 0 || var15 == 0) {
						return;
					}

					if (sprite.getXShift() * var7 % var14 != 0) {
						var10 = (var14 - sprite.getXShift() * var7 % var14 << 16) / var7;
					}

					var1 += (var7 * sprite.getXShift() + var14 - 1) / var14;
					var12 = (var14 << 16) / var7;
					var4 += (var15 + var5 * sprite.getYShift() - 1) / var15;
					var13 = (var15 << 16) / var5;
					if (sprite.getYShift() * var5 % var15 != 0) {
						var11 = (var15 - var5 * sprite.getYShift() % var15 << 16) / var5;
					}

					var5 = var5 * (sprite.getHeight() - (var11 >> 16)) / var15;
					var7 = (sprite.getWidth() - (var10 >> 16)) * var7 / var14;
				}

				var14 = var1 + var4 * this.width2;
				var15 = this.width2 - var7;
				int var16;
				if (this.clipTop > var4) {
					var16 = this.clipTop - var4;
					var4 = 0;
					var5 -= var16;
					var14 += var16 * this.width2;
					var11 += var16 * var13;
				}

				if (this.clipLeft > var1) {
					var16 = this.clipLeft - var1;
					var7 -= var16;
					var15 += var16;
					var10 += var16 * var12;
					var14 += var16;
					var1 = 0;
				}

				if (this.clipBottom <= var4 + var5) {
					var5 -= var4 + var5 - (this.clipBottom - 1);
				}

				if (this.clipRight <= var1 + var7) {
					var16 = var1 + var7 + (1 - this.clipRight);
					var7 -= var16;
					var15 += var16;
				}

				byte var19 = 1;
				if (this.interlace) {
					var13 += var13;
					if ((1 & var4) != 0) {
						--var5;
						var14 += this.width2;
					}

					var19 = 2;
					var15 += this.width2;
				}

				this.a(var11, var12, var7, var10, var15, sprite.getPixels(), var14, this.pixelData, 0,
					spriteWidth, false, var13, var5, var2, var19);
			} catch (Exception var17) {
				System.out.println("error in sprite clipping routine");
			}

		} catch (RuntimeException var18) {
			throw GenUtil.makeThrowable(var18, "ua.FA(" + var1 + ',' + var2 + ',' + sprite.getID() + ',' + var4 + ',' + var5
				+ ',' + var6 + ',' + var7 + ')');
		}
	}

	public void drawEntity(int index, int x, int y, int width, int height, int var1, int var8) {
		try {
			Sprite sprite = sprites[index];
			this.drawSprite(sprite, x, y, width, height, 5924);

		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10, "ua.B(" + var1 + ',' + index + ',' + height + ',' + x + ',' + y + ','
				+ width + ',' + 29 + ',' + var8 + ')');
		}
	}

	public final void a(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
		try {
			for (int var8 = var4; var8 < var4 + var6; ++var8) {
				for (int var9 = var3; var3 + var1 > var9; ++var9) {
					int var10 = 0;
					int var11 = 0;
					int var12 = 0;
					int var13 = 0;

					for (int var14 = var8 - var7; var14 <= var7 + var8; ++var14) {
						if (var14 >= 0 && this.width2 > var14) {
							for (int var15 = var9 - var2; var15 <= var9 + var2; ++var15) {
								if (var15 >= 0 && this.height2 > var15) {
									int inLetter = this.pixelData[this.width2 * var15 + var14];
									var12 += 0xFF & inLetter;
									++var13;
									var11 += (inLetter & 0xFF81) >> 8;
									var10 += (inLetter & 0xFF64A6) >> 16;
								}
							}
						}
					}

					this.pixelData[var8 + var9 * this.width2] = var12 / var13 + (var10 / var13 << 16)
						+ (var11 / var13 << 8);
				}
			}


		} catch (RuntimeException var17) {
			throw GenUtil.makeThrowable(var17, "ua.VA(" + var1 + ',' + var2 + ',' + var3 + ',' + var4 + ',' + 16740352
				+ ',' + var6 + ',' + var7 + ')');
		}
	}

	private void a(int var1, int var2, int var3, int var4, int var5, int[] var6, int var7, int[] letterPlotTable,
				   int var9, int var10, boolean var11, int var12, int var13, int var14, int var15) {
		try {

			int var16 = (var14 & 16736117) >> 16;
			int var17 = 255 & var14 >> 8;
			int var18 = 255 & var14;

			try {
				int var19 = var4;
				if (var11) {
					this.m_Tb = (int[]) null;
				}

				for (int var20 = -var13; var20 < 0; var20 += var15) {
					int var21 = (var1 >> 16) * var10;

					for (int var22 = -var3; var22 < 0; ++var22) {
						var9 = var6[var21 + (var4 >> 16)];
						if (var9 != 0) {
							int var23 = var9 >> 16 & 255;
							int var24 = ('\uff60' & var9) >> 8;
							int var25 = 255 & var9;
							if (var23 == var24 && var25 == var24) {
								letterPlotTable[var7++] = (var23 * var16 >> 8 << 16) + (var17 * var24 >> 8 << 8)
									+ (var25 * var18 >> 8);
							} else {
								letterPlotTable[var7++] = var9;
							}
						} else {
							++var7;
						}

						var4 += var2;
					}

					var1 += var12;
					var7 += var5;
					var4 = var19;
				}
			} catch (Exception var26) {
				System.out.println("error in plot_scale");
			}

		} catch (RuntimeException var27) {
			throw GenUtil.makeThrowable(var27,
				"ua.EB(" + var1 + ',' + var2 + ',' + var3 + ',' + var4 + ',' + var5 + ','
					+ (var6 != null ? "{...}" : "null") + ',' + var7 + ','
					+ (letterPlotTable != null ? "{...}" : "null") + ',' + var9 + ',' + var10 + ',' + var11
					+ ',' + var12 + ',' + var13 + ',' + var14 + ',' + var15 + ')');
		}
	}

	/**
	 * @param negCount negated output pixel count
	 * @param srcWidth source data width
	 * @param dest     destination data
	 * @param srcStepX (source x per output pixel) << 17
	 * @param srcHeadY (start source y) << 17
	 * @param srcHeadX (start source x) << 17
	 * @param src      source pixel data
	 * @param srcStepY (source y per output pixel) << 17
	 * @param destHead starting pixel address in pixelData
	 */
	private void plot_horiz_line(int negCount, int srcWidth, int[] dest, int srcStepX, int srcHeadY, int srcHeadX,
								 int[] src, int srcStepY, int destHead) {
		try {
			for (int i = negCount; i < 0; ++i) {
				dest[destHead++] = src[(srcHeadY >> 17) * srcWidth + (srcHeadX >> 17)];
				srcHeadX += srcStepX;
				srcHeadY += srcStepY;
			}

		} catch (RuntimeException var13) {
			throw GenUtil.makeThrowable(var13,
				"ua.M(" + negCount + ',' + srcWidth + ',' + "dummy" + ',' + (dest != null ? "{...}" : "null") + ','
					+ srcStepX + ',' + srcHeadY + ',' + srcHeadX + ',' + (src != null ? "{...}" : "null") + ','
					+ srcStepY + ',' + destHead + ',' + "dummy" + ')');
		}
	}

	private void a(int var1, int var2, int var3, int[] var4, int var5, int var6, int var7, int[] var8, int var9,
				   int var10, int var11, int var12) {
		try {

			int var13 = 256 - var6;
			if (var9 <= -54) {
				for (int var14 = -var2; var14 < 0; var14 += var7) {
					for (int var15 = -var12; var15 < 0; ++var15) {
						var5 = var4[var1++];
						if (var5 == 0) {
							++var3;
						} else {
							int var16 = var8[var3];
							var8[var3++] = FastMath.bitwiseAnd(16711680,
								var6 * FastMath.bitwiseAnd(var5, '\uff00')
									+ var13 * FastMath.bitwiseAnd('\uff00', var16))
								+ FastMath.bitwiseAnd(var13 * FastMath.bitwiseAnd(var16, 16711935)
								+ FastMath.bitwiseAnd(var5, 16711935) * var6, -16711936) >> 8;
						}
					}

					var1 += var10;
					var3 += var11;
				}

			}
		} catch (RuntimeException var17) {
			throw GenUtil.makeThrowable(var17,
				"ua.TA(" + var1 + ',' + var2 + ',' + var3 + ',' + (var4 != null ? "{...}" : "null") + ',' + var5
					+ ',' + var6 + ',' + var7 + ',' + (var8 != null ? "{...}" : "null") + ',' + var9 + ','
					+ var10 + ',' + var11 + ',' + var12 + ')');
		}
	}

	/**
	 * @param negCount negated output pixel count
	 * @param srcWidth source data width
	 * @param dest     destination data
	 * @param srcStepX (source x per output pixel) << 17
	 * @param srcHeadY (start source y) << 17
	 * @param srcHeadX (start source x) << 17
	 * @param src      source pixel data
	 * @param srcStepY (source y per output pixel) << 17
	 * @param destHead starting pixel address in pixelData
	 */
	private void plot_trans_horiz_line(int srcStepY, int negCount, int srcHeadX, int[] src, int[] var5,
									   int srcHeadY, int destHead, int srcWidth, int srcStepX) {
		try {

			int i = negCount;
			while (i < 0) {
				int color = src[(srcHeadY >> 17) * srcWidth + (srcHeadX >> 17)];
				// System.out.println("color: " + color);
				if (color == 0) {
					// System.out.println("color is black");
					++destHead;
				} else {
					var5[destHead++] = color;
				}

				srcHeadX += srcStepX;
				srcHeadY += srcStepY;
				++i;
			}
		} catch (RuntimeException var13) {
			throw GenUtil.makeThrowable(var13,
				"ua.UA(" + srcStepY + ',' + negCount + ',' + srcHeadX + ',' + (src != null ? "{...}" : "null") + ','
					+ (var5 != null ? "{...}" : "null") + ',' + srcHeadY + ',' + destHead + ',' + srcWidth + ','
					+ srcStepX + ',' + "dummy" + ',' + "dummy" + ')');
		}
	}

	private void a(int var1, int var2, String var3, int var4, int var5, int var6, int var7) {
		try {
			this.drawColoredString(var1 - this.stringWidth(var6, var3), var2, var3, var6, var4, var7);

			if (var5 != -12200) {
				this.copyPixelDataToSurface(SPRITE_LAYER.SHOP, -128, -127, -124, 75);
			}

		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "ua.OA(" + var1 + ',' + var2 + ',' + (var3 != null ? "{...}" : "null")
				+ ',' + var4 + ',' + var5 + ',' + var6 + ',' + var7 + ')');
		}
	}

	private void a(int var1, int[] var2, int var3, int var4, int var5, int var6, byte var7, int var8, int[] var9,
				   int var10, int var11) {
		try {

			if (var7 <= 122) {
				this.drawBoxBorder(121, 54, -117, 67, -103);
			}

			int var12 = -(var1 >> 2);
			var1 = -(3 & var1);

			for (int var13 = -var4; var13 < 0; var13 += var3) {
				int var14;
				for (var14 = var12; var14 < 0; ++var14) {
					var5 = var9[var6++];
					if (var5 == 0) {
						++var8;
					} else {
						var2[var8++] = var5;
					}

					var5 = var9[var6++];
					if (var5 == 0) {
						++var8;
					} else {
						var2[var8++] = var5;
					}

					var5 = var9[var6++];
					if (var5 != 0) {
						var2[var8++] = var5;
					} else {
						++var8;
					}

					var5 = var9[var6++];
					if (var5 == 0) {
						++var8;
					} else {
						var2[var8++] = var5;
					}
				}

				for (var14 = var1; var14 < 0; ++var14) {
					var5 = var9[var6++];
					if (var5 == 0) {
						++var8;
					} else {
						var2[var8++] = var5;
					}
				}

				var6 += var11;
				var8 += var10;
			}

		} catch (RuntimeException var15) {
			throw GenUtil.makeThrowable(var15,
				"ua.JB(" + var1 + ',' + (var2 != null ? "{...}" : "null") + ',' + var3 + ',' + var4 + ',' + var5
					+ ',' + var6 + ',' + var7 + ',' + var8 + ',' + (var9 != null ? "{...}" : "null") + ','
					+ var10 + ',' + var11 + ')');
		}
	}

	public final void drawWrappedCenteredString(String str, int x, int y, int wrapWidth, int font, int color, boolean newLineOnPercent) {
		drawWrappedCenteredString(str, x, y, wrapWidth, font, color, newLineOnPercent, true);
	}

	public final void drawWrappedCenteredString(String str, int x, int y, int wrapWidth, int font, int color,
												boolean newLineOnPercent, boolean centered) {
		try {


			try {
				int width = 0;
				byte[] fontData = Fonts.fontData[font];

				int lastLineTerm = 0;
				int lastBreak = 0;

				for (int i = 0; str.length() > i; ++i) {
					if (str.charAt(i) == '@' && 4 + i < str.length() && str.charAt(i + 4) == '@') {
						i += 4;
					} else if (str.charAt(i) == '~' && str.length() > 4 + i && str.charAt(4 + i) == '~') {
						i += 4;
					} else {
						char c = str.charAt(i);
						if (c < 0 || c >= Fonts.inputFilterCharFontAddr.length) {
							c = ' ';
						}

						width += fontData[7 + Fonts.inputFilterCharFontAddr[c]];
					}

					if (str.charAt(i) == ' ') {
						lastBreak = i;
					}

					if (str.charAt(i) == '%' && newLineOnPercent) {
						width = 1000;
						lastBreak = i;
					}

					if (width > wrapWidth) {
						int lineEndsAt = lastBreak;
						if (lastBreak <= lastLineTerm) {
							lineEndsAt = lastBreak = i;
							lineEndsAt++;
						}

						StringBuilder colourCode    = new StringBuilder();

						if(Config.S_WANT_FIXED_OVERHEAD_CHAT) {
							StringBuilder regexBuilder = new StringBuilder(str.substring(0, lastLineTerm));
							String regexCheck = regexBuilder.reverse().toString();
							Pattern regex = Pattern.compile("(@.{3}@)");
							Matcher match = regex.matcher(regexCheck);

							if (match.find())
								colourCode = colourCode.append(match.group(0)).reverse();
						}

						if (centered) {
							this.drawColoredStringCentered(color, font, 0, colourCode + str.substring(lastLineTerm, lineEndsAt), x, y);
						} else {
							this.drawColoredString(x, y, colourCode + str.substring(lastLineTerm, lineEndsAt), font, color, 0);
						}
						lastLineTerm = i = 1 + lastBreak;
						y += this.fontHeight(font);
						width = 0;
					}
				}

				if (width > 0) {
					StringBuilder colourCode    = new StringBuilder();

					if(Config.S_WANT_FIXED_OVERHEAD_CHAT) {
						StringBuilder regexBuilder = new StringBuilder(str.substring(0, lastLineTerm));
						String regexCheck = regexBuilder.reverse().toString();
						Pattern regex = Pattern.compile("(@.{3}@)");
						Matcher match = regex.matcher(regexCheck);

						if(match.find())
							colourCode  = colourCode.append(match.group(0)).reverse();
					}

					if (centered) {
						this.drawColoredStringCentered(color, font, 0, colourCode + str.substring(lastLineTerm), x, y);
					} else {
						this.drawColoredString(x, y, colourCode + str.substring(lastLineTerm), font, color, 0);
					}
				}
			} catch (Exception var15) {
				System.out.println("centrepara: " + var15);
				var15.printStackTrace();
			}

		} catch (RuntimeException var16) {
			throw GenUtil.makeThrowable(var16, "ua.HB(" + wrapWidth + ',' + (str != null ? "{...}" : "null") + ',' + x
				+ ',' + "dummy" + ',' + font + ',' + y + ',' + newLineOnPercent + ',' + color + ')');
		}
	}

	/**
	 * destHeight is height / heightStep. Black is transparent.
	 *
	 * @param heightStep    destinationHeight / height
	 * @param srcStartY     First source row << 16
	 * @param destWidth     Destination width
	 * @param dummy1
	 * @param dummy2
	 * @param scaleY        (source rows per destination row) << 16
	 * @param spriteWidth   columns in source data
	 * @param scaleX        (source columns per destination column) << 16
	 * @param height        destination height * heightStep
	 * @param destHead      first destination pixel to output to
	 * @param src           source pixel data
	 * @param srcStartX     First source column << 16
	 * @param destRowStride Pixels to skip between rows of output
	 * @param dest          Destination pixel data
	 */
	private void plot_scale_black_mask(int[] src, int heightStep, int scaleX, int dummy1, int srcStartY,
									   int[] dest, byte dummy2, int scaleY, int destHeight, int srcStartX, int destRowStride, int destWidth,
									   int srcWidth, int destHead) {
		try {


			try {
				int firstColumn = srcStartX;

				for (int i = -destHeight; i < 0; i += heightStep) {
					int srcRowHead = (srcStartY >> 16) * srcWidth;
					srcStartY += scaleY;

					for (int j = -destWidth; j < 0; ++j) {
						int color = src[(srcStartX >> 16) + srcRowHead];
						srcStartX += scaleX;
						if (color != 0) {
							dest[destHead++] = color;
						} else {
							++destHead;
						}
					}

					destHead += destRowStride;
					srcStartX = firstColumn;
				}
			} catch (Exception var20) {
				System.out.println("error in plot_scale");
			}

		} catch (RuntimeException var21) {
			throw GenUtil.makeThrowable(var21,
				"ua.DA(" + (src != null ? "{...}" : "null") + ',' + heightStep + ',' + scaleX + ',' + dummy1 + ','
					+ srcStartY + ',' + (dest != null ? "{...}" : "null") + ',' + dummy2 + ',' + scaleY + ','
					+ destHeight + ',' + srcStartX + ',' + destRowStride + ',' + destWidth + ',' + srcWidth
					+ ',' + destHead + ')');
		}
	}

	/*
	 * public final void applyColorLookupTable(int imgID) { try {
	 *  if (null != this.spriteColours[imgID]) { int
	 * pixCount = this.image2D_width[imgID] * this.image2D_height[imgID]; byte[]
	 * pixels = this.spriteColours[imgID]; int[] colorLookupTable =
	 * this.image2D_colorLookupTable[imgID]; int[] outputPixels = new
	 * int[pixCount];
	 *
	 * for (int i = 0; pixCount > i; ++i) { int color = colorLookupTable[255 &
	 * pixels[i]]; if (color == 0) { color = 1; } else if (color == 16711935) {
	 * color = 0; }
	 *
	 * outputPixels[i] = color; }
	 *
	 * this.surfaceSetPixels[imgID] = outputPixels; this.spriteColours[imgID] =
	 * null; this.image2D_colorLookupTable[imgID] = null; } } catch
	 * (RuntimeException var9) { throw GenUtil.makeThrowable(var9, "ua.P(" +
	 * imgID + ',' + -342059728 + ')'); } }
	 */

	private void plot_trans_scale_with_2_masks(int[] dest, int[] src, int destColumnCount,
											   int destColumnSkewPerRow, int destFirstColumn, int dummy1, int dummy2, int mask2, int scaleY, int scaleX,
											   int srcStartX, int skipEveryOther, int srcStartY, int srcWidth, int mask1, int destHeight,
											   int destRowHead) {
		plot_trans_scale_with_2_masks(dest, src, destColumnCount, destColumnSkewPerRow, destFirstColumn, dummy1, dummy2, mask2, scaleY, scaleX, srcStartX, skipEveryOther, srcStartY, srcWidth, mask1, destHeight, destRowHead, 0xFFFFFFFF);
	}

	/**
	 * @param src                  source pixel data
	 * @param mask1                background color to show through when the source data is [x,
	 *                             x, x] (dest = background * source)
	 * @param mask2                background color to show through when the source data is [255,
	 *                             x, x] (dest = background * source)
	 * @param dummy1
	 * @param destColumnSkewPerRow increase in destination first column per destination row
	 * @param srcWidth             width of source data
	 * @param srcStartY            (source start row) << 16
	 * @param scaleY               (source rows per destination row) << 16
	 * @param destFirstColumn      first column to store to in the first destination row
	 * @param scaleX               (source columns per destination row) << 16
	 * @param dest                 destination pixel data
	 * @param skipEveryOther       if this is 0 or 1 the rasterizer skips every other destination
	 *                             pixel
	 * @param spritePixel
	 * @param srcStartX            (source start column) << 16
	 * @param destRowHead          pixel address of first column of the first row to store
	 * @param destColumnCount      destination column count
	 * @param destHeight           destination row count
	 * @param colourTransform      The colour and opacity with which to shade this sprite a uniform colour
	 */
	private void plot_trans_scale_with_2_masks(int[] dest, int[] src, int destColumnCount,
											   int destColumnSkewPerRow, int destFirstColumn, int dummy1, int spritePixel, int mask2, int scaleY, int scaleX,
											   int srcStartX, int skipEveryOther, int srcStartY, int srcWidth, int mask1, int destHeight,
											   int destRowHead, int colourTransform) {
		try {

			int mask1R = mask1 >> 16 & 0xFF;
			int mask1G = mask1 >> 8 & 0xFF;
			int mask1B = mask1 & 0xFF;
			int mask2R = mask2 >> 16 & 0xFF;
			int mask2G = mask2 >> 8 & 0xFF;
			int mask2B = mask2 & 0xFF;

			if (dummy1 != 1603920392) {
				this.clipBottom = 29;
			}

			try {
				int var27 = srcStartX;

				for (int var28 = -destHeight; var28 < 0; ++var28) {
					int var29 = (srcStartY >> 16) * srcWidth;
					int var30 = destFirstColumn >> 16;
					int var31 = destColumnCount;
					int var32;
					if (this.clipLeft > var30) {
						var32 = this.clipLeft - var30;
						var31 = destColumnCount - var32;
						srcStartX += var32 * scaleX;
						var30 = this.clipLeft;
					}

					if (this.clipRight <= var30 + var31) {
						var32 = var30 - this.clipRight + var31;
						var31 -= var32;
					}

					skipEveryOther = 1 - skipEveryOther;
					if (skipEveryOther != 0) {
						for (var32 = var30; var30 + var31 > var32; ++var32) {
							spritePixel = src[var29 + (srcStartX >> 16)];
							if (spritePixel != 0) {
								int spritePixelR = spritePixel >> 16 & 0xFF;
								int spritePixelG = spritePixel >> 8 & 0xFF;
								int spritePixelB = spritePixel & 0xFF;

								// Is the colour from the sprite gray?
								if (spritePixelR == spritePixelG && spritePixelG == spritePixelB) {
									spritePixelR = (spritePixelR * mask1R) >> 8;
									spritePixelG = (spritePixelG * mask1G) >> 8;
									spritePixelB = (spritePixelB * mask1B) >> 8;
								} else if (spritePixelR == 255 && spritePixelG == spritePixelB) { // Is sprite colour full white?
									spritePixelR = (spritePixelR * mask2R) >> 8;
									spritePixelG = (spritePixelG * mask2G) >> 8;
									spritePixelB = (spritePixelB * mask2B) >> 8;
								}

								int opacity = colourTransform >> 24 & 0xFF;
								int inverseOpacity = 0xFF - opacity;

								int transformR = (colourTransform >> 16) & 0xFF;
								int transformG = (colourTransform >> 8) & 0xFF;
								int transformB = colourTransform & 0xFF;

								int spriteR = ((spritePixelR * transformR) >> 8) * opacity;
								int spriteG = ((spritePixelG * transformG) >> 8) * opacity;
								int spriteB = ((spritePixelB * transformB) >> 8) * opacity;

								int canvasR = (dest[var32 + destRowHead] >> 16 & 0xff) * inverseOpacity;
								int canvasG = (dest[var32 + destRowHead] >> 8 & 0xff) * inverseOpacity;
								int canvasB = (dest[var32 + destRowHead] & 0xff) * inverseOpacity;

								int finalColour =
									(((spriteR + canvasR) >> 8) << 16) +
										(((spriteG + canvasG) >> 8) << 8) +
										((spriteB + canvasB) >> 8);
								dest[var32 + destRowHead] = finalColour;

								/*if (spritePixelR == spritePixelG && spritePixelB == spritePixelG) {
									dest[var32 + destRowHead] = (spritePixelB * mask1B >> 8) + (mask1G * spritePixelG >> 8 << 8)
											+ (spritePixelR * mask1R >> 8 << 16);
								} else if (spritePixelR == 255 && spritePixelG == spritePixelB) {
									dest[var32 + destRowHead] = (mask2B * spritePixelB >> 8) + (spritePixelR * mask2R >> 8 << 16)
											+ (spritePixelG * mask2G >> 8 << 8);
								} else {
									dest[var32 + destRowHead] = spritePixel;
								}*/
							}

							srcStartX += scaleX;
						}
					}

					srcStartY += scaleY;
					srcStartX = var27;
					destRowHead += this.width2;
					destFirstColumn += destColumnSkewPerRow;
				}
			} catch (Exception var33) {
				System.out.println("error in transparent sprite plot routine");
			}

		} catch (RuntimeException var34) {
			throw GenUtil.makeThrowable(var34,
				"ua.AA(" + (dest != null ? "{...}" : "null") + ',' + (src != null ? "{...}" : "null") + ','
					+ destColumnCount + ',' + destColumnSkewPerRow + ',' + destFirstColumn + ',' + dummy1 + ','
					+ spritePixel + ',' + mask2 + ',' + scaleY + ',' + scaleX + ',' + srcStartX + ','
					+ skipEveryOther + ',' + srcStartY + ',' + srcWidth + ',' + mask1 + ',' + destHeight + ','
					+ destRowHead + ')');
		}
	}

	public final void copyPixelDataToSurface(SPRITE_LAYER layer, int xOffset, int yOffset, int width, int height) {
		try {
			int[] pixels = new int[width * height];
			int pixel = 0;
			for (int x = xOffset; x < xOffset + width; x++) {
				for (int y = yOffset; y < yOffset + height; y++) {
					pixels[pixel++] = pixelData[x + y * width2];
				}
			}

			Sprite sprite = new Sprite(pixels, width, height);
			sprite.setShift(0, 0);
			sprite.setRequiresShift(false);
			sprite.setSomething(width, height);
			switch (layer) {
				case MINIMAP:
					minimapSprite = sprite;
					break;
				case WORLDMAP:
					//doesn't look like the worldmap is generated on hte fly
					//sprites[4500] = sprite;
					break;
				case SHOP:
					//sprites[49] = sprite;
					break;
			}



			/*
			 *  this.image2D_width[destLayer] = width;
			 * this.image2D_height[destLayer] = height;
			 * this.image2D_hasAlpha[destLayer] = false;
			 * this.image2D_xOffset[destLayer] = 0;
			 * this.image2D_yOffset[destLayer] = 0;
			 * this.image2D_setParam2[destLayer] = width;
			 * this.image2D_setParam3[destLayer] = height; int count = height *
			 * width; int destHead = 0; this.surfaceSetPixels[destLayer] = new
			 * int[count];
			 *
			 * for (int x = xOffset; xOffset + width > x; ++x) { for (int y =
			 * yOffset; y < height + yOffset; ++y) {
			 * this.surfaceSetPixels[destLayer][destHead++] = this.pixelData[x +
			 * this.width2 * y]; } }
			 */
		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11, "ua.BB(" + height + ',' + xOffset + ',' + yOffset + ',' + "dummy" + ','
				+ layer + ',' + width + ')');
		}
	}

	public final void drawVerticalGradient(int x, int y, int width, int height, int topColor, int bottomColor) {
		try {

			if (this.clipLeft > x) {
				width -= this.clipLeft - x;
				x = this.clipLeft;
			}

			if (this.clipRight < width + x) {
				width = this.clipRight - x;
			}

			int topR = (topColor & 0xFF0000) >> 16;
			int topG = topColor >> 8 & 255;
			int topB = topColor & 255;
			int btmR = (bottomColor & 0xFF0000) >> 16;
			int btmG = bottomColor >> 8 & 255;
			int btmB = bottomColor & 255;
			int rowStride = this.width2 - width;
			byte yStep = 1;
			if (this.interlace) {
				rowStride += this.width2;
				yStep = 2;
				if ((y & 1) != 0) {
					++y;
					--height;
				}
			}
			int pxHead = x + this.width2 * y;

			for (int yi = 0; height > yi; yi += yStep) {
				if (this.clipTop <= yi + y && y + yi < this.clipBottom) {
					int color = ((topG * yi + btmG * (height - yi)) / height << 8)
						+ ((btmR * (height - yi) + topR * yi) / height << 16)
						+ (yi * topB + btmB * (height - yi)) / height;

					for (int xi = -width; xi < 0; ++xi) {
						this.pixelData[pxHead++] = color;
					}

					pxHead += rowStride;
				} else {
					pxHead += this.width2;
				}
			}

		} catch (RuntimeException var20) {
			throw GenUtil.makeThrowable(var20, "ua.F(" + x + ',' + bottomColor + ',' + width + ',' + topColor + ','
				+ height + ',' + y + ',' + "dummy" + ')');
		}
	}

	public final void b(int var1, String var2, int var3, int var4, int var5, int var6) {
		try {

			this.a(var1, var3, var2, var4, -12200, (int) var6, 0);
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8, "ua.J(" + var1 + ',' + (var2 != null ? "{...}" : "null") + ',' + var3
				+ ',' + var4 + ',' + var5 + ',' + var6 + ')');
		}
	}

	public final void blackScreen(boolean var1) {
		try {

			int var2 = this.height2 * this.width2;
			if (var1 == !this.interlace) {
				for (int i = 0; var2 > i; ++i) {
					this.pixelData[i] = 0;
				}
			} else {
				int head = 0;

				for (int i = -this.height2; i < 0; i += 2) {
					for (int j = -this.width2; j < 0; ++j) {
						this.pixelData[head++] = 0;
					}

					head += this.width2;
				}
			}

		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "ua.H(" + var1 + ')');
		}
	}

	private int c(int var1, int var2) {
		try {

			if (var2 != 0) {
				if (var1 < 49) {
					this.a(-22, 77, 112, -35, -44, (int[]) null, -45, (int[]) null, -39, -33, false, 50, 61, 37, -7);
				}

				return Fonts.fontData[var2][8] - 1;
			} else {
				return Fonts.fontData[var2][8] - 2;
			}
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "ua.HA(" + var1 + ',' + var2 + ')');
		}
	}

	public final void setIconsStart(int var1, int var2) {
		try {
			this.iconSpriteIndex = var2;

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "ua.I(" + var1 + ',' + var2 + ')');
		}
	}

	public final void storeSpriteVert(int index, int startX, int startY, int width, int height) {
		int[] pixels = new int[width * height];
		int pixel = 0;
		for (int y = startY; y < startY + height; y++) {
			for (int x = startX; x < startX + width; x++) {
				pixels[pixel++] = pixelData[x + y * width2];
			}
		}

		Sprite sprite = new Sprite(pixels, width, height);
		sprite.setShift(0, 0);
		sprite.setRequiresShift(false);
		sprite.setSomething(width, height);

		spriteVerts[index] = sprite;
	}

	public final void drawBox(int xr, int yr, int widthh, int height, int color) {
		try {
			if (xr < this.clipLeft) {
				widthh -= this.clipLeft - xr;
				xr = this.clipLeft;
			}

			if (this.clipTop > yr) {
				height -= this.clipTop - yr;
				yr = this.clipTop;
			}


			if (this.clipBottom < yr + height) {
				height = this.clipBottom - yr;
			}

			if (this.clipRight < widthh + xr) {
				widthh = this.clipRight - xr;
			}

			int lineSkip = this.width2 - widthh;
			byte var8 = 1;
			if (this.interlace) {
				lineSkip += this.width2;
				if ((yr & 1) != 0) {
					--height;
					++yr;
				}

				var8 = 2;
			}

			int var10 = xr + this.width2 * yr;

			for (int yi = -height; yi < 0; yi += var8) {
				for (int xi = -widthh; xi < 0; ++xi) {
					this.pixelData[var10++] = color;
				}

				var10 += lineSkip;
			}

		} catch (RuntimeException var13) {
			throw GenUtil.makeThrowable(var13,
				"ua.LA(" + xr + ',' + "dummy" + ',' + color + ',' + yr + ',' + height + ',' + widthh + ')');
		}
	}

	public final void drawBoxAlpha(int x, int y, int width, int height, int color, int alpha) {
		try {

			if (y < this.clipTop) {
				height -= this.clipTop - y;
				y = this.clipTop;
			}

			if (this.clipLeft > x) {
				width -= this.clipLeft - x;
				x = this.clipLeft;
			}

			if (this.clipRight < x + width) {
				width = this.clipRight - x;
			}

			if (this.clipBottom < height + y) {
				height = this.clipBottom - y;
			}

			int mixOld = 256 - alpha;
			int n3 = alpha * (color >> 16 & 255);
			int n2 = ((color & '\uffc4') >> 8) * alpha;
			int n1 = alpha * (color & 255);
			int lineStride = this.width2 - width;
			byte var16 = 1;
			if (this.interlace) {
				if ((y & 1) != 0) {
					--height;
					++y;
				}

				lineStride += this.width2;
				var16 = 2;
			}

			int pxi = x + this.width2 * y;

			for (int yi = 0; yi < height; yi += var16) {
				for (int xi = -width; xi < 0; ++xi) {
					int o1 = mixOld * (this.pixelData[pxi] & 255);
					int o3 = mixOld * ((0xFF0000 & this.pixelData[pxi]) >> 16);
					int o2 = mixOld * ((0xFF00 & this.pixelData[pxi]) >> 8);
					int var20 = (o1 + n1 >> 8) + (o2 + n2 >> 8 << 8) + (n3 + o3 >> 8 << 16);
					this.pixelData[pxi++] = var20;
				}

				pxi += lineStride;
			}

		} catch (RuntimeException var21) {
			throw GenUtil.makeThrowable(var21, "ua.KB(" + alpha + ',' + x + ',' + height + ',' + "dummy" + ',' + y + ','
				+ width + ',' + color + ')');
		}
	}

	public final void drawBoxBorder(int x, int width, int y, int height, int color) {
		try {
			this.drawLineHoriz(x, y, width, color);

			this.drawLineHoriz(x, height - 1 + y, width, color);
			this.drawLineVert(x, y, color, height);
			this.drawLineVert(width + x - 1, y, color, height);
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8,
				"ua.U(" + x + ',' + width + ',' + y + ',' + "dummy" + ',' + height + ',' + color + ')');
		}
	}

	public final void drawCircle(int x, int y, int radius, int color, int alpha, int dummy) {
		try {

			int destAlpha = 256 - alpha;
			int srcR = alpha * (255 & color >> 16);
			int srcB = (255 & color >> 8) * alpha;
			int srcG = alpha * (color & 255);
			int startY = y - radius;
			if (startY < 0) {
				startY = 0;
			}

			int endY = y + radius;
			if (this.height2 <= endY) {
				endY = this.height2 - 1;
			}

			byte yStep = 1;
			if (this.interlace) {
				if ((1 & startY) != 0) {
					++startY;
				}

				yStep = 2;
			}

			for (int py = startY; endY >= py; py += yStep) {
				int dy = py - y;
				int horizHalf = (int) Math.sqrt((double) (radius * radius - dy * dy));
				int startX = x - horizHalf;
				if (startX < 0) {
					startX = 0;
				}

				int endX = x + horizHalf;
				if (this.width2 <= endX) {
					endX = this.width2 - 1;
				}

				int rowStart = startX + this.width2 * py;

				for (int px = startX; endX >= px; ++px) {
					int destG = (this.pixelData[rowStart] & 255) * destAlpha;
					int destB = destAlpha * ((0xFF00 & this.pixelData[rowStart]) >> 8);
					int destR = destAlpha * ((this.pixelData[rowStart] & 0xFF0000) >> 16);
					int finalColor = (destG + srcG >> 8) + (srcB + destB >> 8 << 8) + (srcR + destR >> 8 << 16);
					this.pixelData[rowStart++] = finalColor;
				}
			}

		} catch (RuntimeException var25) {
			throw GenUtil.makeThrowable(var25,
				"ua.WA(" + alpha + ',' + -1057205208 + ',' + radius + ',' + y + ',' + color + ',' + x + ')');
		}
	}

	public final void drawColoredString(int x, int y, String str, int font, int color, int spriteHeader) {
		try {
			try {
				if (spriteHeader > 0) {
					int iconSprite = (spriteHeader >> 24 & 0xFF) + this.iconSpriteIndex - 1;
					int spriteHeaderMask = (spriteHeader & 0x00FFFFFF);
					Sprite crown = spriteSelect(EntityHandler.crowns.get(iconSprite-3284));

					if (crown != null) {
						this.drawSpriteClipping(
							crown,
							x,
							y - crown.getHeight(),
							crown.getWidth(),
							crown.getHeight(),
							spriteHeaderMask,
							0,
							false,
							0,
							0
						);
						//this.drawSprite(var8, x, y - sprites[var8].getHeight());
						x += crown.getWidth() + 5;
					}
				}

				byte[] fontData = Fonts.fontData[font];

				for (int i = 0; str.length() > i; ++i) {
					if (str.charAt(i) == '@' && i + 4 < str.length() && str.charAt(i + 4) == '@') {
						final String key = str.substring(i + 1, i + 4);
						if (key.equalsIgnoreCase("red")) {
							color = 0xFF0000;
						} else if (key.equalsIgnoreCase("lre")) {
							color = 0xFF9040;
						} else if (key.equalsIgnoreCase("yel")) {
							color = 0xFFFF00;
						} else if (key.equalsIgnoreCase("gre")) {
							color = 0x00FF00;
						} else if (key.equalsIgnoreCase("blu")) {
							color = 0x0000FF;
						} else if (key.equalsIgnoreCase("cya")) {
							color = 0x00FFFF;
						} else if (key.equalsIgnoreCase("mag")) {
							color = 0xFF00FF;
						} else if (key.equalsIgnoreCase("whi")) {
							color = 0xFFFFFF;
						} else if (key.equalsIgnoreCase("bla")) {
							color = 0x000000;
						} else if (key.equalsIgnoreCase("dre")) {
							color = 0xC00000;
						} else if (key.equalsIgnoreCase("ora")) {
							color = 0xFF9040;
						} else if (key.equalsIgnoreCase("ran")) {
							color = (int) (0xFFFFFF * Math.random());
						} else if (key.equalsIgnoreCase("or1")) {
							color = 0xFFB000;
						} else if (key.equalsIgnoreCase("or2")) {
							color = 0xFF7000;
						} else if (key.equalsIgnoreCase("or3")) {
							color = 0xFF3000;
						} else if (key.equalsIgnoreCase("gr1")) {
							color = 0xC0FF00;
						} else if (key.equalsIgnoreCase("gr2")) {
							color = 0x80FF00;
						} else if (key.equalsIgnoreCase("gr3")) {
							color = 0x40FF00;
						} else if (key.equalsIgnoreCase("bl1")) {
							color = 0x4040ff;
						} else if (key.equalsIgnoreCase("bl2")) {
							color = 0x0040ff;
						} else if (key.equalsIgnoreCase("bl3")) {
							color = 0x4000ff;
						} else if (key.equalsIgnoreCase("dgr")) {
							color = 0x00c000;
						} else if (key.equalsIgnoreCase("dbl")) {
							color = 0x0000c0;
						} else if (key.equalsIgnoreCase("dcy")) {
							color = 0x00c0c0;
						} else if (key.equalsIgnoreCase("dor")) {
							color = 0xc06020;
						} else if (key.equalsIgnoreCase("sub")) {
							color = 0xEEDDDD;
						} else if (key.equalsIgnoreCase("eve")) {
							color = 0x4D33BD;
						} else if (key.equalsIgnoreCase("sil")) {
							color = 0xC0C0C0;
						} else if (key.equalsIgnoreCase("pre")) {
							color = 0x44eadf;
						} else if (key.equalsIgnoreCase("cla")) {
							color = 0x7CADDA;
						}
						i += 4;
					} else {
						if (str.charAt(i) == '~' && 4 + i < str.length() && str.charAt(4 + i) == '~') {
							char a = str.charAt(i + 1);
							char b = str.charAt(i + 2);
							char c = str.charAt(i + 3);
							if (a >= '0' && a <= '9' && b >= '0' && b <= '9' && c >= '0' && c <= '9') {
								x = Integer.parseInt(str.substring(i + 1, i + 4));
							}

							i += 4;
						} else if (str.charAt(i) == '~' && i + 5 < str.length() && str.charAt(i + 5) == '~') {
							char c = str.charAt(i + 1);
							char c1 = str.charAt(i + 2);
							char c2 = str.charAt(i + 3);
							char c3 = str.charAt(i + 4);
							if (c >= '0' && c <= '9' && c1 >= '0' && c1 <= '9' && c2 >= '0' && c2 <= '9' && c3 >= '0'
								&& c3 <= '9')
								x = Integer.parseInt(str.substring(i + 1, i + 5));
							i += 5;
						} else if (false && Config.S_WANT_CUSTOM_RANK_DISPLAY && str.charAt(i) == '#' && i + 4 < str.length() && str.charAt(i + 4) == '#' && str.substring(i + 1, i + 4).equalsIgnoreCase("adm")) {
							this.drawSpriteClipping(
								spriteSelect(EntityHandler.GUIparts.get(0)),
								x - 1,
								y - sprites[this.iconSpriteIndex].getHeight(),
								sprites[this.iconSpriteIndex].getWidth(),
								sprites[this.iconSpriteIndex].getHeight(),
								0x00FF00,
								0,
								false,
								0,
								0
							);
							x += sprites[this.iconSpriteIndex].getWidth() + 5;
							i += 4;
						} else if (false && Config.S_WANT_CUSTOM_RANK_DISPLAY && str.charAt(i) == '#' && i + 4 < str.length() && str.charAt(i + 4) == '#' && str.substring(i + 1, i + 4).equalsIgnoreCase("mod")) {
							this.drawSpriteClipping(
								spriteSelect(EntityHandler.crowns.get(0)),
								x - 1,
								y - sprites[this.iconSpriteIndex].getHeight(),
								sprites[this.iconSpriteIndex].getWidth(),
								sprites[this.iconSpriteIndex].getHeight(),
								0x0000FF,
								0,
								false,
								0,
								0
							);
							x += sprites[this.iconSpriteIndex].getWidth() + 5;
							i += 4;
						} else if (false && Config.S_WANT_CUSTOM_RANK_DISPLAY && str.charAt(i) == '#' && i + 4 < str.length() && str.charAt(i + 4) == '#' && str.substring(i + 1, i + 4).equalsIgnoreCase("dev")) {
							this.drawSpriteClipping(
								spriteSelect(EntityHandler.crowns.get(0)),
								x - 1,
								y - sprites[this.iconSpriteIndex].getHeight(),
								sprites[this.iconSpriteIndex].getWidth(),
								sprites[this.iconSpriteIndex].getHeight(),
								0xFF0000,
								0,
								false,
								0,
								0
							);
							x += sprites[this.iconSpriteIndex].getWidth() + 5;
							i += 4;
						} else if (false && Config.S_WANT_CUSTOM_RANK_DISPLAY && str.charAt(i) == '#' && i + 4 < str.length() && str.charAt(i + 4) == '#' && str.substring(i + 1, i + 4).equalsIgnoreCase("eve")) {
							this.drawSpriteClipping(
								spriteSelect(EntityHandler.crowns.get(0)),
								x - 1,
								y - sprites[this.iconSpriteIndex].getHeight(),
								sprites[this.iconSpriteIndex].getWidth(),
								sprites[this.iconSpriteIndex].getHeight(),
								0x4D33BD,
								0,
								false,
								0,
								0
							);
							x += sprites[this.iconSpriteIndex].getWidth() + 5;
							i += 4;
						} else {
							char here = str.charAt(i);
							if (here == 160) {
								here = ' ';
							}

							if (here < 0 || here >= Fonts.inputFilterCharFontAddr.length) {
								here = ' ';
							}

							int addr = Fonts.inputFilterCharFontAddr[here];
							if (this.loggedIn && !Fonts.fontAntiAliased[font] && color != 0) {
								this.plotCharacter(Fonts.fontAntiAliased[font], fontData, 1 + x, 0, addr, y);
							}

							if (this.loggedIn && !Fonts.fontAntiAliased[font] && color != 0) {
								this.plotCharacter(Fonts.fontAntiAliased[font], fontData, x, 0, addr, y + 1);
							}

							this.plotCharacter(Fonts.fontAntiAliased[font], fontData, x, color, addr, y);
							x += fontData[addr + 7];
						}
					}
				}
			} catch (Exception var14) {
				System.out.println("drawstring: " + var14);
				var14.printStackTrace();
			}


		} catch (

			RuntimeException var15) {
			throw GenUtil.makeThrowable(var15, "ua.FB(" + spriteHeader + ',' + y + ','
				+ (str != null ? "{...}" : "null") + ',' + x + ',' + color + ',' + "dummy" + ',' + font + ')');
		}

	}

	private void drawColoredStringCentered(int color, int font, int spriteHeader, String str, int x, int y) {
		try {
			this.drawColoredString(x - this.stringWidth(font, str) / 2, y, str, font, color, spriteHeader);

		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "ua.G(" + 11815 + ',' + color + ',' + font + ',' + spriteHeader + ','
				+ (str != null ? "{...}" : "null") + ',' + x + ',' + y + ')');
		}
	}

	public final void drawColoredStringCentered(int x, String str, int color, int spriteHeader, int font, int y) {
		try {
			this.drawColoredStringCentered(color, font, spriteHeader, str, x, y);

		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8, "ua.N(" + x + ',' + (str != null ? "{...}" : "null") + ',' + color + ','
				+ spriteHeader + ',' + font + ',' + y + ')');
		}
	}

	public final void drawLineHoriz(int x, int y, int width, int color) {
		try {

			if (this.clipTop <= y && y < this.clipBottom) {
				if (this.clipLeft > x) {
					width -= this.clipLeft - x;
					x = this.clipLeft;
				}

				if (x + width > this.clipRight) {
					width = this.clipRight - x;
				}

				if (width > 0) {
					int offset = x + this.width2 * y;

					for (int xi = 0; width > xi; ++xi) {
						this.pixelData[offset + xi] = color;
					}

				}
			}
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "ua.LB(" + width + ',' + color + ',' + x + ',' + y + ',' + "dummy" + ')');
		}
	}

	public final void drawLineVert(int x, int y, int color, int height) {

		if (this.clipLeft <= x && x < this.clipRight) {
			if (y < this.clipTop) {
				height -= this.clipTop - y;
				y = this.clipTop;
			}

			if (y + height > this.clipBottom) {
				height = this.clipBottom - y;
			}

			if (height > 0) {
				int pxOffset = x + this.width2 * y;

				for (int i = 0; height > i; ++i) {
					this.pixelData[pxOffset + this.width2 * i] = color;
				}
			}
		}

	}

	public final void drawMinimapSprite(Sprite sprite, int var2, int var3, int var4, int var5, int var6) {
		try {

			int var7 = this.width2;
			int var8 = this.height2;
			int var9;
			if (this.trigTable256 == null) {
				this.trigTable256 = new int[512];

				for (var9 = 0; var9 < 256; ++var9) {
					this.trigTable256[var9] = (int) (Math.sin((double) var9 * 0.02454369D) * 32768.0D);
					this.trigTable256[256 + var9] = (int) (Math.cos((double) var9 * 0.02454369D) * 32768.0D);
				}
			}

			var9 = -sprite.getSomething1() / 2;
			int var10 = -sprite.getSomething2() / 2;
			if (sprite.requiresShift()) {
				var9 += sprite.getXShift();
				var10 += sprite.getYShift();
			}

			int var11 = sprite.getWidth() + var9;
			int var12 = sprite.getHeight() + var10;
			var6 &= 255;
			int var17 = this.trigTable256[var6] * var5;
			int var18 = this.trigTable256[var6 + 256] * var5;
			int var19 = var3 + (var18 * var9 + var10 * var17 >> 22);
			int var20 = var2 + (var10 * var18 - var9 * var17 >> 22);
			int var21 = (var11 * var18 + var10 * var17 >> 22) + var3;
			int var22 = var2 + (var18 * var10 - var11 * var17 >> 22);
			int var23 = (var17 * var12 + var18 * var11 >> 22) + var3;
			int var24 = (var18 * var12 - var17 * var11 >> 22) + var2;
			int var25 = (var9 * var18 + var17 * var12 >> 22) + var3;
			int var26 = var2 + (var12 * var18 - var9 * var17 >> 22);
			if (var5 == 192 && (63 & var6) == (63 & MiscFunctions.mud_s_ef)) {
				++MiscFunctions.cachingFile_s_g;
			} else if (var5 == 128) {
				MiscFunctions.mud_s_ef = var6;
			} else {
				++MiscFunctions.netsock_s_M;
			}

			int var27 = var20;
			int var28 = var20;
			if (var20 > var22) {
				var27 = var22;
			} else if (var22 > var20) {
				var28 = var22;
			}

			if (var27 > var24) {
				var27 = var24;
			} else if (var24 > var28) {
				var28 = var24;
			}

			if (var27 > var26) {
				var27 = var26;
			} else if (var26 > var28) {
				var28 = var26;
			}

			if (this.clipTop > var27) {
				var27 = this.clipTop;
			}

			if (this.m_Xb == null || var8 + 1 != this.m_Xb.length) {
				this.m_tb = new int[var8 + 1];
				this.m_M = new int[1 + var8];
				this.m_t = new int[var8 + 1];
				this.m_Tb = new int[1 + var8];
				this.m_Wb = new int[1 + var8];
				this.m_Xb = new int[var8 + 1];
			}

			if (var28 > this.clipBottom) {
				var28 = this.clipBottom;
			}

			int var29;
			for (var29 = var27; var29 <= var28; ++var29) {
				this.m_Xb[var29] = 99999999;
				this.m_t[var29] = -99999999;
			}

			int var32 = 0;
			int var34 = 0;
			int var36 = 0;
			int var37 = sprite.getWidth();
			var11 = var37 - 1;
			int var38 = sprite.getHeight();
			int var13 = var37 - 1;
			byte var49 = 0;
			byte var50 = 0;
			byte var14 = 0;
			byte var15 = 0;
			var12 = var38 - 1;
			int var16 = var38 - 1;
			int var30;
			int var31;
			int var35;
			if (var20 > var26) {
				var30 = var20;
				var35 = var16 << 8;
				var29 = var26;
				var31 = var25 << 8;
			} else {
				var30 = var26;
				var29 = var20;
				var35 = var50 << 8;
				var31 = var19 << 8;
			}

			if (var20 != var26) {
				var36 = (var16 - var50 << 8) / (var26 - var20);
				var32 = (var25 - var19 << 8) / (var26 - var20);
			}

			if (var29 < 0) {
				var31 -= var32 * var29;
				var35 -= var29 * var36;
				var29 = 0;
			}

			if (var8 - 1 < var30) {
				var30 = var8 - 1;
			}

			int var39;
			int[] var40;
			for (var39 = var29; var30 >= var39; ++var39) {
				this.m_Xb[var39] = this.m_t[var39] = var31;
				var31 += var32;
				var40 = this.m_M;
				this.m_Tb[var39] = 0;
				var40[var39] = 0;
				this.m_tb[var39] = this.m_Wb[var39] = var35;
				var35 += var36;
			}

			if (var20 != var22) {
				var32 = (var21 - var19 << 8) / (var22 - var20);
				var34 = (var13 - var49 << 8) / (var22 - var20);
			}

			int var33;
			if (var22 < var20) {
				var33 = var13 << 8;
				var30 = var20;
				var29 = var22;
				var31 = var21 << 8;
			} else {
				var31 = var19 << 8;
				var29 = var20;
				var33 = var49 << 8;
				var30 = var22;
			}

			if (var8 - 1 < var30) {
				var30 = var8 - 1;
			}

			if (var29 < 0) {
				var33 -= var34 * var29;
				var31 -= var32 * var29;
				var29 = 0;
			}

			for (var39 = var29; var30 >= var39; ++var39) {
				if (this.m_Xb[var39] > var31) {
					this.m_Xb[var39] = var31;
					this.m_M[var39] = var33;
					this.m_tb[var39] = 0;
				}

				if (var31 > this.m_t[var39]) {
					this.m_t[var39] = var31;
					this.m_Tb[var39] = var33;
					this.m_Wb[var39] = 0;
				}

				var31 += var32;
				var33 += var34;
			}

			if (var24 >= var22) {
				var33 = var13 << 8;
				var35 = var14 << 8;
				var29 = var22;
				var31 = var21 << 8;
				var30 = var24;
			} else {
				var33 = var11 << 8;
				var30 = var22;
				var35 = var12 << 8;
				var31 = var23 << 8;
				var29 = var24;
			}

			if (var22 != var24) {
				var36 = (var12 - var14 << 8) / (var24 - var22);
				var32 = (var23 - var21 << 8) / (var24 - var22);
			}

			if (var30 > var8 - 1) {
				var30 = var8 - 1;
			}

			if (var29 < 0) {
				var31 -= var32 * var29;
				var35 -= var36 * var29;
				var29 = 0;
			}

			for (var39 = var29; var39 <= var30; ++var39) {
				if (this.m_Xb[var39] > var31) {
					this.m_Xb[var39] = var31;
					this.m_M[var39] = var33;
					this.m_tb[var39] = var35;
				}

				if (var31 > this.m_t[var39]) {
					this.m_t[var39] = var31;
					this.m_Tb[var39] = var33;
					this.m_Wb[var39] = var35;
				}

				var31 += var32;
				var35 += var36;
			}

			if (var26 != var24) {
				var32 = (var25 - var23 << 8) / (var26 - var24);
				var34 = (var15 - var11 << 8) / (var26 - var24);
			}

			if (var26 < var24) {
				var33 = var15 << 8;
				var30 = var24;
				var29 = var26;
				var35 = var16 << 8;
				var31 = var25 << 8;
			} else {
				var29 = var24;
				var35 = var12 << 8;
				var31 = var23 << 8;
				var33 = var11 << 8;
				var30 = var26;
			}

			if (var29 < 0) {
				var31 -= var29 * var32;
				var33 -= var29 * var34;
				var29 = 0;
			}

			if (var8 - 1 < var30) {
				var30 = var8 - 1;
			}

			for (var39 = var29; var30 >= var39; ++var39) {
				if (var31 < this.m_Xb[var39]) {
					this.m_Xb[var39] = var31;
					this.m_M[var39] = var33;
					this.m_tb[var39] = var35;
				}

				if (var31 > this.m_t[var39]) {
					this.m_t[var39] = var31;
					this.m_Tb[var39] = var33;
					this.m_Wb[var39] = var35;
				}

				var31 += var32;
				var33 += var34;
			}

			var39 = var27 * var7;
			var40 = sprite.getPixels();

			for (int var41 = var27; var41 < var28; ++var41) {
				int var42 = this.m_Xb[var41] >> 8;
				int var43 = this.m_t[var41] >> 8;
				if (var43 - var42 > 0) {
					int var44 = this.m_M[var41] << 9;
					int var45 = ((this.m_Tb[var41] << 9) - var44) / (var43 - var42);
					int var46 = this.m_tb[var41] << 9;
					int var47 = ((this.m_Wb[var41] << 9) - var46) / (var43 - var42);
					if (var43 > this.clipRight) {
						var43 = this.clipRight;
					}

					if (var42 < this.clipLeft) {
						var46 += (this.clipLeft - var42) * var47;
						var44 += (this.clipLeft - var42) * var45;
						var42 = this.clipLeft;
					}

					if (!this.interlace || (var41 & 1) == 0) {
						if (sprite.requiresShift()) {
							this.plot_trans_horiz_line(var47, var42 - var43, var44, var40, this.pixelData, var46,
								var42 + var39, var37, var45);
						} else {
							this.plot_horiz_line(var42 - var43, var37, this.pixelData, var45, var46, var44, var40,
								var47, var42 + var39);
						}
					}

					var39 += var7;
				} else {
					var39 += var7;
				}
			}
		} catch (RuntimeException var48) {
			throw GenUtil.makeThrowable(var48,
				"ua.O(" + sprite + ',' + var2 + ',' + var3 + ',' + 842218000 + ',' + var5 + ',' + var6 + ')');
		}
	}

	public final void drawSprite(Sprite sprite, int x, int y) {
		try {

			if (sprite == null) {
				System.out.println("sprite missing:" );
				return;
			}
			if (sprite.requiresShift()) {
				x += sprite.getXShift();
				y += sprite.getYShift();
			}

			int var5 = y * this.width2 + x;
			int var6 = 0;
			int var7 = sprite.getHeight();
			int var8 = sprite.getWidth();
			int var9 = this.width2 - var8;
			int var10 = 0;
			int var11;
			if (this.clipTop > y) {
				var11 = this.clipTop - y;
				var7 -= var11;
				y = this.clipTop;
				var5 += this.width2 * var11;
				var6 += var11 * var8;
			}

			if (y + var7 >= this.clipBottom) {
				var7 -= 1 + (var7 + y - this.clipBottom);
			}

			if (x < this.clipLeft) {
				var11 = this.clipLeft - x;
				var6 += var11;
				var9 += var11;
				var8 -= var11;
				var10 += var11;
				x = this.clipLeft;
				var5 += var11;
			}

			if (x + var8 >= this.clipRight) {
				var11 = x + var8 - this.clipRight + 1;
				var8 -= var11;
				var10 += var11;
				var9 += var11;
			}

			if (var8 > 0 && var7 > 0) {
				byte var13 = 1;
				if (this.interlace) {
					var9 += this.width2;
					if ((1 & y) != 0) {
						var5 += this.width2;
						--var7;
					}

					var13 = 2;
					var10 += sprite.getWidth();
				}

				this.a(var8, this.pixelData, var13, var7, 0, var6, (byte) 123, var5, sprite.getPixels(), var9,
					var10);

			}
		} catch (RuntimeException var12) {
			throw GenUtil.makeThrowable(var12, "ua.Q(" + -1 + ',' + sprite + ',' + y + ',' + x + ')');
		}
	}

	/*
	 * public final void drawWorld(boolean var1, int var2) { try {
	 *  int var3 = this.image2D_height[var2] *
	 * this.image2D_width[var2]; int[] var4 = this.image2D_pixels[var2]; int[]
	 * var5 = new int['\u8000'];
	 *
	 * for (int var6 = 0; var6 < var3; ++var6) { int var7 = var4[var6];
	 * ++var5[(31 & var7 >> 3) + (var7 >> 9 & 31744) + ((var7 & '\uf800') >>
	 * 6)]; }
	 *
	 * int[] var24 = new int[256]; var24[0] = 16711935; int[] var25 = new
	 * int[256];
	 *
	 * int var9; int var10; int var11; for (int var8 = 0; var8 < '\u8000';
	 * ++var8) { var9 = var5[var8]; if (var25[255] < var9) { for (var10 = 1;
	 * var10 < 256; ++var10) { if (var25[var10] < var9) { for (var11 = 255;
	 * var10 < var11; --var11) { var24[var11] = var24[var11 - 1]; var25[var11] =
	 * var25[var11 - 1]; }
	 *
	 * var24[var10] = 0x040404 + (FastMath.bitwiseAnd(0x1F, var8) << 3) +
	 * FastMath.bitwiseAnd(0xF800, var8 << 6) + FastMath.bitwiseAnd(0xF80000,
	 * var8 << 9); var25[var10] = var9; break; } } }
	 *
	 * var5[var8] = -1; }
	 *
	 * byte[] var26 = new byte[var3];
	 *
	 * for (var9 = 0; var9 < var3; ++var9) { var10 = var4[var9]; var11 = (var10
	 * >> 3 & 31) + ((var10 & '\uf800') >> 6) + ((16252928 & var10) >> 9); int
	 * var12 = var5[var11]; if (var12 == -1) { int var13 = 999999999; int var14
	 * = 255 & var10 >> 16; int var15 = 255 & var10 >> 8; int var16 = var10 &
	 * 255;
	 *
	 * for (int var17 = 0; var17 < 256; ++var17) { int var18 = var24[var17]; int
	 * var19 = (0xFF1E98 & var18) >> 16; int var20 = var18 >> 8 & 0xFF; int
	 * var21 = 0xFF & var18; int var22 = (var16 - var21) * (var16 - var21) +
	 * (var14 - var19) * (var14 - var19) + (var15 - var20) * (var15 - var20); if
	 * (var22 < var13) { var12 = var17; var13 = var22; } }
	 *
	 * var5[var11] = var12; }
	 *
	 * var26[var9] = (byte) var12; }
	 *
	 * this.spriteColours[var2] = var26; this.image2D_colorLookupTable[var2] =
	 * var24; this.image2D_pixels[var2] = null; } catch (RuntimeException var23)
	 * { throw GenUtil.makeThrowable(var23, "ua.DB(" + false + ',' + var2 +
	 * ')'); } }
	 */

	public final void drawShadowText(String text, int x, int y, int textColor, int fontSize, boolean center) {
		int textX = x;
		int textY = y;
		if (center) {
			int textWidth = stringWidth(fontSize, text);
			int textHeight = fontHeight(fontSize);
			textX -= (textWidth / 2);
			textY += (textHeight / 2);
		}
		drawString(text, textX - 1, textY, 0x0F0F0F, fontSize);
		drawString(text, textX, textY - 1, 0x0F0F0F, fontSize);

		drawString(text, textX, textY, textColor, fontSize);
	}

	public final void drawString(String str, int x, int y, int color, int font) {
		this.drawColoredString(x, y, str, font, color, 0);
	}

	public final void drawSprite(Sprite sprite, int x, int y, int destWidth, int destHeight, int var5) {
		try {


			try {
				int spriteWidth = sprite.getWidth();
				int spriteHeight = sprite.getHeight();
				int srcStartX = 0;
				int srcStartY = 0;
				int scaleX = (spriteWidth << 16) / destWidth;
				int scaleY = (spriteHeight << 16) / destHeight;
				int destHead;
				int destRowStride;
				if (sprite.requiresShift()) {
					destHead = sprite.getSomething1();
					destRowStride = sprite.getSomething2();
					if (destHead == 0 || destRowStride == 0) {
						return;
					}

					if (sprite.getYShift() * destHeight % destRowStride != 0) {
						srcStartY = (destRowStride - destHeight * sprite.getYShift() % destRowStride << 16)
							/ destHeight;
					}

					scaleX = (destHead << 16) / destWidth;
					if (sprite.getXShift() * destWidth % destHead != 0) {
						srcStartX = (destHead - sprite.getXShift() * destWidth % destHead << 16) / destWidth;
					}

					x += (destWidth * sprite.getXShift() + destHead - 1) / destHead;
					scaleY = (destRowStride << 16) / destHeight;
					y += (destRowStride + destHeight * sprite.getYShift() - 1) / destRowStride;
					destHeight = (sprite.getHeight() - (srcStartY >> 16)) * destHeight / destRowStride;
					destWidth = destWidth * (sprite.getWidth() - (srcStartX >> 16)) / destHead;
				}

				destHead = x + this.width2 * y;
				if (y < this.clipTop) {
					int lost = this.clipTop - y;
					srcStartY += scaleY * lost;
					destHeight -= lost;
					destHead += this.width2 * lost;
					y = 0;
				}

				destRowStride = this.width2 - destWidth;
				if (y + destHeight >= this.clipBottom) {
					destHeight -= y - this.clipBottom + destHeight + 1;
				}

				if (x < this.clipLeft) {
					int lost = this.clipLeft - x;
					destWidth -= lost;
					destRowStride += lost;
					destHead += lost;
					x = 0;
					srcStartX += scaleX * lost;
				}

				if (this.clipRight <= x + destWidth) {
					int lost = 1 + x + (destWidth - this.clipRight);
					destRowStride += lost;
					destWidth -= lost;
				}

				byte heightStep = 1;
				if (this.interlace) {
					if ((y & 1) != 0) {
						--destHeight;
						destHead += this.width2;
					}

					destRowStride += this.width2;
					heightStep = 2;
					scaleY += scaleY;
				}

				this.plot_scale_black_mask(sprite.getPixels(), heightStep, scaleX, 0, srcStartY,
					this.pixelData, (byte) 78, scaleY, destHeight, srcStartX, destRowStride, destWidth, spriteWidth,
					destHead);
			} catch (Exception var16) {
				System.out.println("error in sprite clipping routine");
			}

		} catch (RuntimeException var17) {
			throw GenUtil.makeThrowable(var17,
				"ua.D(" + x + ',' + y + ',' + destHeight + ',' + destWidth + ',' + 5924 + ',' + sprite + ')');
		}
	}

	public final void fade2black(int var1) {
		try {

			int var4 = this.height2 * this.width2;

			for (int i = 0; var4 > i; ++i) {
				int var2 = 0xFFFFFF & this.pixelData[i];
				this.pixelData[i] = FastMath.bitwiseAnd(var2 >>> 4, 0x0F0F0F)
					+ (FastMath.bitwiseAnd(var2, 0xF8F8F9) >>> 3) + (FastMath.bitwiseAnd(0xFEFEFF, var2) >>> 1)
					+ FastMath.bitwiseAnd(-2143338689, var2 >>> 2);
			}

		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "ua.V(" + 16316665 + ')');
		}
	}

	public final int fontHeight(int font) {
		try {

			return font != 0
				? (font != 1
				? (font == 2 ? 14
				: (font == 3 ? 15
				: (font != 4 ? (font != 5
				? (font == 6 ? 24 : (font != 7 ? this.c(60, font) : 29)) : 19)
				: 15)))
				: 14)
				: 12;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "ua.QA(" + "dummy" + ',' + font + ')');
		}
	}

	private void plotLetter(int color, int[] dest, int destHead, int rowStride, int height, int width,
							int srcHead, byte[] src, int srcStride) {
		try {


			int count2;
			try {
				count2 = -(width >> 2);
				width = -(width & 3);

				for (int i = -height; i < 0; ++i) {
					for (int j = count2; j < 0; ++j) {
						if (src[srcHead++] != 0) {
							dest[destHead++] = color;
						} else {
							++destHead;
						}

						if (src[srcHead++] == 0) {
							++destHead;
						} else {
							dest[destHead++] = color;
						}

						if (src[srcHead++] == 0) {
							++destHead;
						} else {
							dest[destHead++] = color;
						}

						if (src[srcHead++] != 0) {
							dest[destHead++] = color;
						} else {
							++destHead;
						}
					}

					for (int j = width; j < 0; ++j) {
						if (src[srcHead++] == 0) {
							++destHead;
						} else {
							dest[destHead++] = color;
						}
					}

					srcHead += srcStride;
					destHead += rowStride;
				}
			} catch (Exception var14) {
				System.out.println("plotletter: " + var14);
				var14.printStackTrace();
			}

			// var11 = 82 % ((-45 - var4) / 48);
		} catch (RuntimeException var15) {
			throw GenUtil.makeThrowable(var15,
				"ua.PA(" + color + ',' + (dest != null ? "{...}" : "null") + ',' + destHead + ',' + "dummy" + ','
					+ rowStride + ',' + height + ',' + width + ',' + srcHead + ','
					+ (src != null ? "{...}" : "null") + ',' + srcStride + ')');
		}
	}

	private void plotLetterAntialiased(byte[] src, int color, int width, int destHead, int height, int srcStride,
									   int rowStride, int[] dest, int srcHead) {
		try {

			for (int i = -height; i < 0; ++i) {
				for (int j = -width; j < 0; ++j) {
					int alpha = 0xFF & src[srcHead++];
					if (alpha <= 30) {
						++destHead;
					} else if (alpha < 230) {
						int invAlpha = (256 - alpha);
						int destColor = dest[destHead];
						dest[destHead++] = (FastMath.bitwiseAnd(0xFF00FF00,
							FastMath.bitwiseAnd(0xFF00FF, color) * alpha
								+ FastMath.bitwiseAnd(destColor, 0xFF00FF) * invAlpha)
							+ FastMath.bitwiseAnd(invAlpha * FastMath.bitwiseAnd(0xFF00, destColor)
							+ alpha * FastMath.bitwiseAnd(0xFF00, color), 0xFF0000)) >> 8;
					} else {
						dest[destHead++] = color;
					}
				}

				srcHead += srcStride;
				destHead += rowStride;
			}

		} catch (RuntimeException var15) {
			throw GenUtil.makeThrowable(var15,
				"ua.MA(" + (src != null ? "{...}" : "null") + ',' + color + ',' + width + ',' + destHead + ','
					+ height + ',' + srcStride + ',' + "dummy" + ',' + rowStride + ','
					+ (dest != null ? "{...}" : "null") + ',' + srcHead + ')');
		}
	}

	public final void createCaptchaSprite(Sprite s) {
		try {

			spriteVerts[2] = s;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final void setClip(int clipLeft, int clipRight, int clipBottom, int clipTop) {
		try {
			if (this.height2 < clipBottom) {
				clipBottom = this.height2;
			}

			if (clipTop < 0) {
				clipTop = 0;
			}

			if (clipLeft < 0) {
				clipLeft = 0;
			}


			if (clipRight > this.width2) {
				clipRight = this.width2;
			}

			this.clipTop = clipTop;
			this.clipBottom = clipBottom;
			this.clipRight = clipRight;
			this.clipLeft = clipLeft;
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7,
				"ua.NA(" + clipLeft + ',' + clipRight + ',' + clipBottom + ',' + clipTop + ',' + "dummy" + ')');
		}
	}

	public final void setPixel(int x, int y, int val) {
		try {

			if (this.clipLeft <= x && this.clipTop <= y && this.clipRight > x && this.clipBottom > y) {
				this.pixelData[x + this.width2 * y] = val;
			}
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "ua.CB(" + y + ',' + x + ',' + "dummy" + ',' + val + ')');
		}
	}

	public final void drawSpriteClipping(Sprite sprite, int x, int y, int width, int height, int colorMask, int colorMask2,
										 boolean mirrorX, int topPixelSkew, int dummy) {
		drawSpriteClipping(sprite, x, y, width, height, colorMask, colorMask2, mirrorX, topPixelSkew, dummy, 0xFFFFFFFF);
	}

	public final void drawSpriteClipping(Sprite e, int x, int y, int width, int height, int colorMask, int colorMask2,
										 boolean mirrorX, int topPixelSkew, int dummy, int colourTransform) {
		try {
			try {
				if (colorMask2 == 0) {
					colorMask2 = 0xFFFFFF;
				}

				if (colorMask == 0) {
					colorMask = 0xFFFFFF;
				}

				int spriteWidth = e.getWidth();
				int spriteHeight = e.getHeight();
				int srcStartX = 0;
				int srcStartY = 0;
				int destFirstColumn = topPixelSkew << 16;
				int scaleX = (spriteWidth << 16) / width;
				int scaleY = (spriteHeight << 16) / height;
				int destColumnSkewPerRow = -(topPixelSkew << 16) / height;
				int destRowHead;
				int skipEveryOther;
				if (e.requiresShift()) {
					destRowHead = e.getSomething1();
					skipEveryOther = e.getSomething2();
					if (destRowHead == 0 || skipEveryOther == 0) {
						return;
					}

					scaleX = (destRowHead << 16) / width;
					scaleY = (skipEveryOther << 16) / height;
					int var21 = e.getXShift();
					if (mirrorX) {
						var21 = destRowHead - e.getWidth() - var21;
					}

					int var22 = e.getYShift();
					x += (destRowHead + var21 * width - 1) / destRowHead;
					int var23 = (var22 * height + skipEveryOther - 1) / skipEveryOther;
					if (var21 * width % destRowHead != 0) {
						srcStartX = (destRowHead - width * var21 % destRowHead << 16) / width;
					}

					y += var23;
					destFirstColumn += var23 * destColumnSkewPerRow;
					if (var22 * height % skipEveryOther != 0) {
						srcStartY = (skipEveryOther - height * var22 % skipEveryOther << 16) / height;
					}

					width = (scaleX + ((e.getWidth() << 16) - (srcStartX + 1))) / scaleX;
					height = ((e.getHeight() << 16) - srcStartY - (1 - scaleY)) / scaleY;
				}

				destRowHead = this.width2 * y;
				destFirstColumn += x << 16;
				if (y < this.clipTop) {
					skipEveryOther = this.clipTop - y;
					destFirstColumn += destColumnSkewPerRow * skipEveryOther;
					height -= skipEveryOther;
					srcStartY += skipEveryOther * scaleY;
					destRowHead += this.width2 * skipEveryOther;
					y = this.clipTop;
				}

				if (y + height >= this.clipBottom) {
					height -= 1 + y + height - this.clipBottom;
				}

				skipEveryOther = destRowHead / this.width2 & dummy;
				if (!this.interlace) {
					skipEveryOther = 2;
				}
				// TODO:Make sure this works.
				if (colorMask2 == 0xFFFFFF) {
					if (null != e.getPixels()) {
						if (mirrorX) {
							this.plot_tran_scale_with_mask(dummy ^ 74, e.getPixels(), scaleY, 0,
								srcStartY, (e.getWidth() << 16) - (srcStartX + 1), width,
								this.pixelData, height, destColumnSkewPerRow, destRowHead, -scaleX, destFirstColumn,
								spriteWidth, skipEveryOther, colorMask, colourTransform);
						} else {
							this.plot_tran_scale_with_mask(dummy + 89, e.getPixels(), scaleY, 0,
								srcStartY, srcStartX, width, this.pixelData, height, destColumnSkewPerRow,
								destRowHead, scaleX, destFirstColumn, spriteWidth, skipEveryOther, colorMask, colourTransform);
						}
					}
				} else if (mirrorX) {
					this.plot_trans_scale_with_2_masks(this.pixelData, e.getPixels(), width,
						destColumnSkewPerRow, destFirstColumn, dummy + 1603920391, 0, colorMask2, scaleY, -scaleX,
						(e.getWidth() << 16) - srcStartX - 1, skipEveryOther, srcStartY, spriteWidth,
						colorMask, height, destRowHead, colourTransform);
				} else {
					this.plot_trans_scale_with_2_masks(this.pixelData, e.getPixels(), width,
						destColumnSkewPerRow, destFirstColumn, 1603920392, 0, colorMask2, scaleY, scaleX, srcStartX,
						skipEveryOther, srcStartY, spriteWidth, colorMask, height, destRowHead, colourTransform);
				}
			} catch (Exception var24) {
				System.out.println("error in sprite clipping routine");
			}

		} catch (RuntimeException var25) {
			throw GenUtil.makeThrowable(var25, "ua.AB(" + y + ',' + colorMask + ',' + colorMask2 + ',' + mirrorX + ','
				+ topPixelSkew + ',' + e.getID() + ',' + height + ',' + width + ',' + x + ',' + dummy + ')');
		}
	}

	public final int stringWidth(int font, String str) {
		try {

			int width = 0;

			byte[] fontData = Fonts.fontData[font];

			for (int i = 0; i < str.length(); ++i) {
				if (str.charAt(i) == '@' && 4 + i < str.length() && str.charAt(i + 4) == '@') {
					i += 4;
				} else if (str.charAt(i) == '~' && i + 4 < str.length() && str.charAt(i + 4) == '~') {
					i += 4;
				} else {
					char c = str.charAt(i);
					if (c < 0 || c >= Fonts.inputFilterCharFontAddr.length) {
						c = ' ';
					}
					width += fontData[Fonts.inputFilterCharFontAddr[c] + 7];
				}
			}

			return width;
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8,
				"ua.K(" + font + ',' + "dummy" + ',' + (str != null ? "{...}" : "null") + ')');
		}
	}

	/**
	 * destHeight is height / heightStep
	 *
	 * @param heightStep    destinationHeight / height
	 * @param srcStartY     First source row << 16
	 * @param destWidth     Destination width
	 * @param dummy1
	 * @param scaleY        (source rows per destination row) << 16
	 * @param spriteWidth   columns in source data
	 * @param scaleX        (source columns per destination column) << 16
	 * @param height        destination height * heightStep
	 * @param destHead      first destination pixel to output to
	 * @param src           source pixel data
	 * @param dummy2
	 * @param srcStartX     First source column << 16
	 * @param destRowStride Pixels to skip between rows of output
	 * @param alpha         Alpha value [0-256]
	 * @param dest          Destination pixel data
	 */
	private void plot_tran_scale(int heightStep, int srcStartY, int destWidth, byte dummy1, int scaleY,
								 int spriteWidth, int scaleX, int height, int destHead, int[] src, int dummy2, int srcStartX,
								 int destRowStride, int alpha, int[] dest) {
		try {

			int alphaInverse = 256 - alpha;

			try {
				int firstColumn = srcStartX;
				// destHeight = height / heightStep
				for (int i = -height; i < 0; i += heightStep) {
					int rowOffset = spriteWidth * (srcStartY >> 16);
					srcStartY += scaleY;

					for (int j = -destWidth; j < 0; ++j) {
						int newColor = src[rowOffset + (srcStartX >> 16)];
						srcStartX += scaleX;
						if (newColor == 0) {
							++destHead;
						} else {
							int oldColor = dest[destHead];
							dest[destHead++] = FastMath
								.bitwiseAnd(FastMath.bitwiseAnd(0xFF00, oldColor) * alphaInverse
									+ FastMath.bitwiseAnd(0xFF00, newColor) * alpha, 0xFF0000)
								+ FastMath.bitwiseAnd(
								FastMath.bitwiseAnd(newColor, 0xFF00FF) * alpha
									+ alphaInverse * FastMath.bitwiseAnd(0xFF00FF, oldColor),
								-16711936) >> 8;
						}
					}

					destHead += destRowStride;
					srcStartX = firstColumn;
				}
			} catch (Exception var22) {
				System.out.println("error in tran_scale");
			}

		} catch (RuntimeException var23) {
			throw GenUtil.makeThrowable(var23,
				"ua.EA(" + heightStep + ',' + srcStartY + ',' + destWidth + ',' + dummy1 + ',' + scaleY + ','
					+ spriteWidth + ',' + scaleX + ',' + height + ',' + destHead + ','
					+ (src != null ? "{...}" : "null") + ',' + dummy2 + ',' + srcStartX + ',' + destRowStride
					+ ',' + alpha + ',' + (dest != null ? "{...}" : "null") + ')');
		}
	}

	private void plot_tran_scale_with_mask(int dummy2, int[] src, int scaleY, int dummy1, int srcStartY,
										   int srcStartX, int destColumnCount, int[] dest, int destHeight, int destColumnSkewPerRow, int destRowHead,
										   int scaleX, int destFirstColumn, int srcWidth, int skipEveryOther, int background) {
		plot_tran_scale_with_mask(dummy2, src, scaleY, dummy1, srcStartY, srcStartX, destColumnCount, dest, destHeight, destColumnSkewPerRow, destRowHead, scaleX, destFirstColumn, srcWidth, skipEveryOther, background, 0xFFFFFFFF);
	}

	/**
	 * @param dummy2
	 * @param src                  source pixel data
	 * @param scaleY               (source rows per destination row) << 16
	 * @param dummy1
	 * @param srcStartY            (source start row) << 16
	 * @param srcStartX            (source start column) << 16
	 * @param destColumnCount      destination column count
	 * @param dest                 destination pixel data
	 * @param destHeight           destination row count
	 * @param destColumnSkewPerRow increase in destination first column per destination row
	 * @param destRowHead          pixel address of first column of the first row to store
	 * @param scaleX               (source columns per destination row) << 16
	 * @param destFirstColumn      first column to store to in the first destination row
	 * @param srcWidth             width of source data
	 * @param skipEveryOther       if this is 0 or 1 the rasterizer skips every other destination
	 *                             pixel
	 * @param spritePixel          background color to show through when the source data is grey
	 *                             (dest = background * source)
	 * @param colourTransform      The colour and opacity with which to shade this sprite a uniform colour
	 */
	private void plot_tran_scale_with_mask(int dummy2, int[] src, int scaleY, int dummy1, int srcStartY,
										   int srcStartX, int destColumnCount, int[] dest, int destHeight, int destColumnSkewPerRow, int destRowHead,
										   int scaleX, int destFirstColumn, int srcWidth, int skipEveryOther, int spritePixel, int colourTransform) {
		try {

			int spritePixelR = spritePixel >> 16 & 0xFF;
			int spritePixelG = spritePixel >> 8 & 0xFF;
			int spritePixelB = spritePixel & 0xFF;

			try {
				int firstColumn = srcStartX;

				for (int i = -destHeight; i < 0; ++i) {
					int srcRowHead = (srcStartY >> 16) * srcWidth;
					int duFirstColumn = destFirstColumn >> 16;
					int duColumnCount = destColumnCount;
					if (duFirstColumn < this.clipLeft) {
						int lost = this.clipLeft - duFirstColumn;
						duFirstColumn = this.clipLeft;
						duColumnCount = destColumnCount - lost;
						srcStartX += scaleX * lost;
					}

					skipEveryOther = 1 - skipEveryOther;
					if (duFirstColumn + duColumnCount >= this.clipRight) {
						int lost = duColumnCount + duFirstColumn - this.clipRight;
						duColumnCount -= lost;
					}

					if (skipEveryOther != 0) {
						for (int j = duFirstColumn; j < duColumnCount + duFirstColumn; ++j) {
							int newColor = src[srcRowHead + (srcStartX >> 16)];
							if (newColor != 0) {
								int opacity = colourTransform >> 24 & 0xFF;
								int inverseOpacity = 256 - opacity;

								int transformR = colourTransform >> 16 & 0xFF;
								int transformG = colourTransform >> 8 & 0xFF;
								int transformB = colourTransform & 0xFF;

								int newR = newColor >> 16 & 0xFF;
								int newG = newColor >> 8 & 0xFF;
								int newB = newColor & 0xFF;

								// Is the colour from the sprite gray?
								if (newR == newG && newG == newB) {
									newR = (spritePixelR * newR) >> 8;
									newG = (spritePixelG * newG) >> 8;
									newB = (spritePixelB * newB) >> 8;
								}

								int spriteR = ((newR * transformR) >> 8) * opacity;
								int spriteG = ((newG * transformG) >> 8) * opacity;
								int spriteB = ((newB * transformB) >> 8) * opacity;

								int canvasR = (dest[destRowHead + j] >> 16 & 0xff) * inverseOpacity;
								int canvasG = (dest[destRowHead + j] >> 8 & 0xff) * inverseOpacity;
								int canvasB = (dest[destRowHead + j] & 0xff) * inverseOpacity;

								int finalColour =
									(((spriteR + canvasR) >> 8) << 16) +
										(((spriteG + canvasG) >> 8) << 8) +
										((spriteB + canvasB) >> 8);
								dest[destRowHead + j] = finalColour;

								/*// Are we a grey?
								if (newR == newG && newB == newG) {
									dest[destRowHead + j] = (newB * backgroundB >> 8) + ((backgroundG * newG >> 8) << 8)
											+ ((backgroundR * newR >> 8) << 16);
								} else {
									dest[destRowHead + j] = newColor;
								}*/
							}

							srcStartX += scaleX;
						}
					}

					srcStartY += scaleY;
					srcStartX = firstColumn;
					destFirstColumn += destColumnSkewPerRow;
					destRowHead += this.width2;
				}
			} catch (Exception var29) {
				System.out.println("error in transparent sprite plot routine");
			}

			if (dummy2 < 20) {
				this.m_t = (int[]) null;
			}

		} catch (RuntimeException var30) {
			throw GenUtil.makeThrowable(var30,
				"ua.GA(" + dummy2 + ',' + (src != null ? "{...}" : "null") + ',' + scaleY + ',' + dummy1 + ','
					+ srcStartY + ',' + srcStartX + ',' + destColumnCount + ','
					+ (dest != null ? "{...}" : "null") + ',' + destHeight + ',' + destColumnSkewPerRow + ','
					+ destRowHead + ',' + scaleX + ',' + destFirstColumn + ',' + srcWidth + ',' + skipEveryOther
					+ ',' + spritePixel + ')');
		}
	}

	/**
	 * @param src                  source pixel data
	 * @param background           background color to show through when the source data is grey
	 *                             (dest = background * source)
	 * @param dummy1
	 * @param destColumnSkewPerRow increase in destination first column per destination row
	 * @param lookupTable          mapping from source value to color
	 * @param srcWidth             width of source data
	 * @param srcStartY            (source start row) << 16
	 * @param scaleY               (source rows per destination row) << 16
	 * @param destFirstColumn      first column to store to in the first destination row
	 * @param scaleX               (source columns per destination row) << 16
	 * @param dest                 destination pixel data
	 * @param skipEveryOther       if this is 0 or 1 the rasterizer skips every other destination
	 *                             pixel
	 * @param dummy2
	 * @param srcStartX            (source start column) << 16
	 * @param destRowHead          pixel address of first column of the first row to store
	 * @param destColumnCount      destination column count
	 * @param destHeight           destination row count
	 */
	final void plot_trans_scale_lookup_with_mask(byte[] src, int background, int dummy1, int destColumnSkewPerRow,
												 int[] lookupTable, int srcWidth, int srcStartY, int scaleY, int destFirstColumn, int scaleX, int[] dest,
												 int skipEveryOther, int dummy2, int srcStartX, int destRowHead, int destColumnCount, int destHeight) {
		try {

			int backR = (0xFF0000 & background) >> 16;
			int backG = (0xFF00 & background) >> 8;
			int backB = background & 255;

			try {
				int firstColumn = srcStartX;

				for (int i = -destHeight; i < 0; ++i) {
					int srcRowHead = (srcStartY >> 16) * srcWidth;
					int duStartCol = destFirstColumn >> 16;
					int duColCount = destColumnCount;
					if (duStartCol < this.clipLeft) {
						int tmp = this.clipLeft - duStartCol;
						duStartCol = this.clipLeft;
						duColCount = destColumnCount - tmp;
						srcStartX += tmp * scaleX;
					}

					if (duColCount + duStartCol >= this.clipRight) {
						int lost = duStartCol + (duColCount - this.clipRight);
						duColCount -= lost;
					}

					skipEveryOther = 1 - skipEveryOther;
					srcStartY += scaleY;
					if (skipEveryOther != 0) {
						for (int j = duStartCol; j < duColCount + duStartCol; ++j) {
							int newColor = src[(srcStartX >> 16) + srcRowHead] & 255;
							if (newColor != 0) {
								newColor = lookupTable[newColor];
								int newB = newColor & 255;
								int newR = newColor >> 16 & 255;
								int newG = newColor >> 8 & 255;
								// Is grey
								if (newR == newG && newB == newG) {
									dest[j + destRowHead] = (backG * newG >> 8 << 8) + (backR * newR >> 8 << 16)
										+ (backB * newB >> 8);
								} else {
									dest[destRowHead + j] = newColor;
								}
							}

							srcStartX += scaleX;
						}
					}

					destFirstColumn += destColumnSkewPerRow;
					destRowHead += this.width2;
					srcStartX = firstColumn;
				}
			} catch (Exception var31) {
				System.out.println("error in transparent sprite plot routine");
			}

		} catch (RuntimeException var32) {
			throw GenUtil.makeThrowable(var32,
				"ua.IB(" + (src != null ? "{...}" : "null") + ',' + background + ',' + dummy1 + ','
					+ destColumnSkewPerRow + ',' + (lookupTable != null ? "{...}" : "null") + ',' + srcWidth
					+ ',' + srcStartY + ',' + scaleY + ',' + destFirstColumn + ',' + scaleX + ','
					+ (dest != null ? "{...}" : "null") + ',' + skipEveryOther + ',' + dummy2 + ',' + srcStartX
					+ ',' + destRowHead + ',' + destColumnCount + ',' + destHeight + ')');
		}
	}
	public boolean fillSpriteTree() {
		Enumeration<? extends ZipEntry> entries = spriteArchive.entries();
		//Loop through each spritesheet
		try {
			while (entries.hasMoreElements()) {
				List<Sprite> spriteGroup = new ArrayList<Sprite>();
				ZipEntry entry = entries.nextElement();
				//ZipInputStream entryStream = new ZipInputStream(spriteArchive.getInputStream(entry));
				//InputStream in = spriteArchive.getInputStream(entry);

				//ByteBuffer buffer = streamToBuffer(in);
				spriteGroup = unpackSpriteData(spriteArchive, entry);
				spriteTree.put(entry.getName(), spriteGroup);
			}
		} catch (IOException a) {
			a.printStackTrace();
		}

		return true;
	}

	public static ArrayList<Sprite> unpackSpriteData(ZipFile ioe, ZipEntry zipEntry) throws IOException {
		ArrayList<Sprite> sprites = new ArrayList<Sprite>();

		try {
			InputStream fileIn = ioe.getInputStream(zipEntry);
			ByteArrayOutputStream fileBytesBuffer = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int readByte;
			while ((readByte = fileIn.read(buffer)) != -1) {
				fileBytesBuffer.write(buffer,0, readByte);
			}

			fileBytesBuffer.close();
			fileIn.close();

			byte[] fileBytes = fileBytesBuffer.toByteArray();
			ByteBuffer fileByteBuffer = ByteBuffer.wrap(fileBytes);
			sprites = unpackSpriteNew(fileByteBuffer);

		}
		catch (IOException a) {
			a.printStackTrace();
		}
		return sprites;
	}

	public static ArrayList<Sprite> unpackSpriteNew(ByteBuffer in) {
		ArrayList<Sprite> spriteArray = new ArrayList<>();


		while (in.hasRemaining()) {

			int id = in.getShort();
			int width = in.getShort();
			int height = in.getShort();

			boolean requiresShift = in.get() == 1;
			int xShift = in.getShort();
			int yShift = in.getShort();

			int width2 = in.getShort();
			int height2 = in.getShort();

			int[] pixels = new int[width * height];

			//if (in.remaining() < (pixels.length * 4))
			//   throw new IOException("Provided buffer too short - Pixels missing");

			for (int pixel = 0; pixel < pixels.length; pixel++)
				pixels[pixel] = Integer.valueOf(in.getInt());

			//if (in.remaining() <= 0)
			//  e.name = "Missing";
			//else

			Sprite e = new Sprite(pixels, width, height);
			e.setPackageName(readString(in));

			//e.data = rgbTo8bit(pixels,width,height);
			e.setID(id);
			e.setSomething(width2, height2);

			e.setXShift(xShift);
			e.setYShift(yShift);

			e.setRequiresShift(requiresShift);
			spriteArray.add(e);
		}
		//if (in.remaining() < 15)
		//   throw new IOException("Provided buffer too short - Headers missing");


		return spriteArray;
	}

	private static final String readString(ByteBuffer buffer) {
		StringBuilder bldr = new StringBuilder();

		byte b;
		while ((b = buffer.get()) != 10) {
			bldr.append((char) b);
		}

		return bldr.toString();
	}


	public boolean loadSprite(int id, String packageName) {
		try {
			ZipEntry e = spriteArchive.getEntry(String.valueOf(id));
			if (e == null) {
				System.err.println("Missing sprite: " + id);
				return false;
			}
			ByteBuffer data = DataConversions.streamToBuffer(new BufferedInputStream(spriteArchive.getInputStream(e)));
			sprites[id] = Sprite.unpack(data);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public void mapAnimations() {

		List<Sprite> animationList = spriteTree.get("animations");

		for (int i = 0; i < EntityHandler.animationCount(); i++)
		{
			AnimationDef animation = EntityHandler.getAnimationDef(i);
			if (!animationMap.containsKey(animation.getName())) {
				int p = 0;
				for (Sprite sprite : animationList) {
					if (animation.getName().equalsIgnoreCase(sprite.getPackageName())) {
						animationMap.put(animation.getName(), p);
						break;
					}
					p++;
				}
			}
		}
	}

}
