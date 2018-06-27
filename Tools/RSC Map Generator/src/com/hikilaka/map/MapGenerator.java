package com.hikilaka.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.hikilaka.file.JagArchiveLoader;
import com.hikilaka.file.jag.JagArchive;

public final class MapGenerator {

	public static void main(String[] args) {
		new MapGenerator().begin();
	}

	private final JagArchiveLoader jagLoader = new JagArchiveLoader();

	private final JagArchive freeLand, memLand, mapsFree, mapsMem;

	protected final byte[] tileGroundElevation = new byte[2304];

	protected final byte[] tileRoofType = new byte[2304];

	protected final byte[] tileGroundTexture = new byte[2304];

	protected final int[] tileDiagonalWall = new int[2304];

	protected final byte[] tileGroundOverlay = new byte[2304];

	protected final byte[] tileObjectRotation = new byte[2304];

	protected final byte[] tileHorizontalWall = new byte[2304];

	protected final byte[] tileVerticalWall = new byte[2304];

	protected final int[][] tiles = new int[96][96];

	protected int height;

	private final int[] tileColors = new int[256];

	public MapGenerator() {
		freeLand = jagLoader.loadArchive(Config.INPUT_DIRECTORY + "land.jag");
		memLand = jagLoader.loadArchive(Config.INPUT_DIRECTORY + "land.mem");
		mapsFree = jagLoader.loadArchive(Config.INPUT_DIRECTORY + "maps.jag");
		mapsMem = jagLoader.loadArchive(Config.INPUT_DIRECTORY + "maps.mem");

		for (int i = 0; i < 64; i++) {
			tileColors[i] = decodeColor(255 - i * 4, 255 - (int) ((double) i * 1.75D), 255 - i * 4);
		}
		for (int i = 0; i < 64; i++) {
			tileColors[i + 64] = decodeColor(i * 3, 144, 0);
		}
		for (int i = 0; i < 64; i++) {
			tileColors[i + 128] = decodeColor(192 - (int) ((double) i * 1.5D), 144 - (int) ((double) i * 1.5D), 0);
		}
		for (int i = 0; i < 64; i++) {
			tileColors[i + 192] = decodeColor(96 - (int) ((double) i * 1.5D), 48 + (int) ((double) i * 1.5D), 0);
		}
	}

	public void begin() {
		BufferedImage finalImage = new BufferedImage(Config.FINAL_IMAGE_WIDTH, Config.FINAL_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
		File f = new File("map." + Config.IMAGE_EXTENSION);

		for (int z = 0; z < Config.MAX_Z_SECTOR; ++z) {
			for (int y = 0; y < (Config.MAX_Y_SECTOR - 37); ++y) {
				for (int x = 0; x < (Config.MAX_X_SECTOR - 48); ++x) {
					String file = "m" + z + (x + 48) / 10 + (x + 48) % 10 + (y + 37) / 10 + (y + 37) % 10;
					byte[] data = freeLand.load(file + ".hei");
					BufferedImage image = null; 
					if (data != null && data.length > 0) {
						unpackTiles(file, data, z);
						image = generateSectorImage();
						if (z == 0) finalImage.getGraphics().drawImage(image, Config.SECTOR_IMAGE_WIDTH * x, Config.SECTOR_IMAGE_HEIGHT * y, null);
						continue;
					}
					data = memLand.load(file + ".hei");
					if (data != null && data.length > 0) {
						unpackTiles(file, data, z);
						image = generateSectorImage();
						if (z == 0) finalImage.getGraphics().drawImage(image, Config.SECTOR_IMAGE_WIDTH * x, Config.SECTOR_IMAGE_HEIGHT * y, null);
					}
					
					if (z == 0) {
						finalImage.getGraphics().drawImage(image, Config.SECTOR_IMAGE_WIDTH * x, Config.SECTOR_IMAGE_HEIGHT * y, null);
					}
				}	
			}
		}
		finalImage = flipXAxis(finalImage);
		
		try {
			ImageIO.write(finalImage, Config.IMAGE_EXTENSION, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private BufferedImage flipXAxis(BufferedImage image) {
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-image.getWidth(), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
		return image;
	}

	private BufferedImage generateSectorImage() {
		BufferedImage image = new BufferedImage(Config.SECTOR_IMAGE_WIDTH, Config.SECTOR_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
		applySector(image);
		flipXAxis(image);
		return image;
	}

	private void applySector(BufferedImage image) {	
		Graphics gfx = image.getGraphics();
		for (int x = 0; x < Config.SECTOR_IMAGE_WIDTH - 1; ++x) {
			for (int y = 0; y < Config.SECTOR_IMAGE_HEIGHT - 1; ++y) {
				int texture = getGroundTexture(x, y);
				int color1 = tileColors[texture];
				int color2 = color1;
				int underlay = color1;
				int renderType = 0;

				if (height == 1 || height == 2) {
					color1 = 0xbc614e;
					color2 = 0xbc614e;
					underlay = 0xbc614e;
				}
				if (getGroundTexturesOverlay(x, y) > 0) {
					int overlay = getGroundTexturesOverlay(x, y);

					if (overlay - 1 >= Texture.TILE_UNKNOWN.length) continue; //err fixed a weird npe exception

					int l5 = Texture.TILE_UNKNOWN[overlay - 1];
					int i19 = method427(x, y);
					color1 = color2 = Texture.TILE_COLORS[overlay - 1];
					if (l5 == 4) {
						color1 = 1;
						color2 = 1;
						if (overlay == 12) {
							color1 = 31;
							color2 = 31;
						}
					}
					if (l5 == 5) {
						if (getDiagonalWall(x, y) > 0 && getDiagonalWall(x, y) < 24000)
							if (getOverlayIfRequired(x - 1, y, underlay) != 0xbc614e && getOverlayIfRequired(x, y - 1, underlay) != 0xbc614e) {
								color1 = getOverlayIfRequired(x - 1, y, underlay);
								renderType = 0;
							} else if (getOverlayIfRequired(x + 1, y, underlay) != 0xbc614e && getOverlayIfRequired(x, y + 1, underlay) != 0xbc614e) {
								color2 = getOverlayIfRequired(x + 1, y, underlay);
								renderType = 0;
							} else if (getOverlayIfRequired(x + 1, y, underlay) != 0xbc614e && getOverlayIfRequired(x, y - 1, underlay) != 0xbc614e) {
								color2 = getOverlayIfRequired(x + 1, y, underlay);
								renderType = 1;
							} else if (getOverlayIfRequired(x - 1, y, underlay) != 0xbc614e && getOverlayIfRequired(x, y + 1, underlay) != 0xbc614e) {
								color1 = getOverlayIfRequired(x - 1, y, underlay);
								renderType = 1;
							}
					} else if (l5 != 2 || getDiagonalWall(x, y) > 0 && getDiagonalWall(x, y) < 24000)
						if (method427(x - 1, y) != i19 && method427(x, y - 1) != i19) {
							color1 = underlay;
							renderType = 0;
						} else if (method427(x + 1, y) != i19 && method427(x, y + 1) != i19) {
							color2 = underlay;
							renderType = 0;
						} else if (method427(x + 1, y) != i19 && method427(x, y - 1) != i19) {
							color2 = underlay;
							renderType = 1;
						} else if (method427(x - 1, y) != i19 && method427(x, y + 1) != i19) {
							color1 = underlay;
							renderType = 1;
						}
					if (Texture.TILE_OBJECT[overlay - 1] != 0) {
						tiles[x][y] |= 0x40;
					}
					if (Texture.TILE_UNKNOWN[overlay - 1] == 2) {
						tiles[x][y] |= 0x80;
					}
				}
				drawTile(gfx, x, y, renderType, color1, color2);
			}
		}

		int color = 0x606060;

		for (int x = 0; x < Config.SECTOR_IMAGE_WIDTH - 1; x++) {
			for (int y = 0; y < Config.SECTOR_IMAGE_HEIGHT - 1; y++) {
				int tileValue = getHorizontalWall(x, y);

				if (tileValue > 0 && Texture.DOOR_UNKNOWN[tileValue - 1] == 0) {
					if (Texture.DOOR_TYPE[tileValue - 1] != 0) {
						tiles[x][y] |= 2;
						if (x > 0) {
							orWalkable(x - 1, y, 8);
						}
					}
					drawLineY(gfx, x * 3, y * 3, 3, color);
				}

				tileValue = getVerticalWall(x, y);
				if (tileValue > 0 && Texture.DOOR_UNKNOWN[tileValue - 1] == 0) {
					if (Texture.DOOR_TYPE[tileValue - 1] != 0) {
						tiles[x][y] |= 1;
						if (y > 0) {
							orWalkable(x, y - 1, 4);
						}
					}
					drawLineX(gfx, x * 3, y * 3, 3, color);
				}

				tileValue = getDiagonalWall(x, y);
				if (tileValue > 0 && tileValue < 12000 && Texture.DOOR_UNKNOWN[tileValue - 1] == 0) {
					if (Texture.DOOR_TYPE[tileValue - 1] != 0) {
						tiles[x][y] |= 0x20;
					}
					setPixelColour(gfx, x * 3, y * 3, color);
					setPixelColour(gfx, x * 3 + 1, y * 3 + 1, color);
					setPixelColour(gfx, x * 3 + 2, y * 3 + 2, color);
				}

				if (tileValue > 12000 && tileValue < 24000 && Texture.DOOR_UNKNOWN[tileValue - 12001] == 0) {
					if (Texture.DOOR_TYPE[tileValue - 12001] != 0) {
						tiles[x][y] |= 0x10;
					}
					setPixelColour(gfx, x * 3 + 2, y * 3, color);
					setPixelColour(gfx, x * 3 + 1, y * 3 + 1, color);
					setPixelColour(gfx, x * 3, y * 3 + 2, color);
				}
			}
		}
	}

	public void drawTile(Graphics gfx, int x, int y, int type, int base1, int base2) {
		int xx = x * 3;
		int yy = y * 3;
		int color1 = Texture.decodeColor(base1);
		int color2 = Texture.decodeColor(base2);
		color1 = color1 >> 1 & 0x7f7f7f;
				color2 = color2 >> 1 & 0x7f7f7f;

		if (type == 0) {
			drawLineX(gfx, xx, yy, 3, color1);
			drawLineX(gfx, xx, yy + 1, 2, color1);
			drawLineX(gfx, xx, yy + 2, 1, color1);
			drawLineX(gfx, xx + 2, yy + 1, 1, color2);
			drawLineX(gfx, xx + 1, yy + 2, 2, color2);
		} else if (type == 1) {
			drawLineX(gfx, xx, yy, 3, color2);
			drawLineX(gfx, xx + 1, yy + 1, 2, color2);
			drawLineX(gfx, xx + 2, yy + 2, 1, color2);
			drawLineX(gfx, xx, yy + 1, 1, color1);
			drawLineX(gfx, xx, yy + 2, 2, color1);
		}
	}

	private void drawLineX(Graphics gfx, int x, int y, int length, int color) {
		gfx.setColor(convertLongToRGB(color));
		gfx.drawLine(x, y, x + length, y);
	}

	private void drawLineY(Graphics gfx, int x, int y, int length, int color) {
		gfx.setColor(convertLongToRGB(color));
		gfx.drawLine(x, y, x, y + length);
	}

	private void setPixelColour(Graphics gfx, int x, int y, int color) {
		gfx.setColor(convertLongToRGB(color));
		gfx.drawLine(x, y, x, y);
	}

	public Color convertLongToRGB(int value) {
		return new Color((value >> 16) & 0xff, (value >> 8) & 0xff, value & 0xff);
	}

	private void unpackTiles(String fileName, byte[] data, int height) {
		int off = 0;
		int lastValue = 0;

		for (int tile = 0; tile < 2304;) {
			int value = data[off++] & 0xff;

			if (value < 128) {
				tileGroundElevation[tile++] = (byte)value;
				lastValue = value;
			}
			if (value >= 128) {
				for (int i = 0; i < value - 128; i++) {
					tileGroundElevation[tile++] = (byte) lastValue;
				}
			}
		}

		lastValue = 64;
		for (int w = 0; w < 48; w++) {
			for (int h = 0; h < 48; h++) {
				lastValue = tileGroundElevation[h * 48 + w] + lastValue & 0x7f;
				tileGroundElevation[h * 48 + w] = (byte)(lastValue * 2);
			}

		}

		lastValue = 0;
		for (int tile = 0; tile < 2304;) {
			int value = data[off++] & 0xff;
			if (value < 128) {
				tileGroundTexture[tile++] = (byte) value;
				lastValue = value;
			}
			if (value >= 128) {
				for (int i = 0; i < value - 128; i++) {
					tileGroundTexture[tile++] = (byte)lastValue;
				}
			}
		}

		lastValue = 35;
		for (int w = 0; w < 48; w++) {
			for (int h = 0; h < 48; h++) {
				lastValue = tileGroundTexture[h * 48 + w] + lastValue & 0x7f;
				tileGroundTexture[h * 48 + w] = (byte)(lastValue * 2);
			}
		}

		data = mapsFree.load(fileName + ".dat");
		off = 0;

		if(data == null) {
			data = mapsMem.load(fileName + ".dat");
		}

		for(int tile = 0; tile < 2304; tile++) {
			tileHorizontalWall[tile] = data[off++];
		}

		for(int tile = 0; tile < 2304; tile++) {
			tileVerticalWall[tile] = data[off++];
		}

		for(int tile = 0; tile < 2304; tile++)
			tileDiagonalWall[tile] = data[off++] & 0xff;

		for(int tile = 0; tile < 2304; tile++) {
			int value = data[off++] & 0xff;
			if(value > 0) {
				tileDiagonalWall[tile] = value + 12000;
			}
		}

		for(int tile = 0; tile < 2304;) {
			int value = data[off++] & 0xff;
			if(value < 128) {
				tileRoofType[tile++] = (byte)value;
			} else {
				for(int i = 0; i < value - 128; i++) {
					tileRoofType[tile++] = 0;
				}
			}
		}

		lastValue = 0;
		for(int tile = 0; tile < 2304;) {
			int value = data[off++] & 0xff;
			if(value < 128) {
				tileGroundOverlay[tile++] = (byte) value;
				lastValue = value;
			} else {
				for(int i = 0; i < value - 128; i++) {
					tileGroundOverlay[tile++] = (byte) lastValue;
				}
			}
		}

		for(int tile = 0; tile < 2304;) {
			int value = data[off++] & 0xff;
			if(value < 128) {
				tileObjectRotation[tile++] = (byte)value;
			} else {
				for(int l10 = 0; l10 < value - 128; l10++) {
					tileObjectRotation[tile++] = 0;
				}
			}
		}

		data = mapsFree.load(fileName + ".loc");

		if(data != null && data.length > 0) {
			int index = 0;
			for(int tile = 0; tile < 2304;) {
				int value = data[index++] & 0xff;
				if(value < 128)
					tileDiagonalWall[tile++] = value + 48000;
				else
					tile += value - 128;
			}
			return;
		}
	}

	public static int decodeColor(int r, int g, int b) {
		return -1 - (r / 8) * 1024 - (g / 8) * 32 - b / 8;
	}

	public int getGroundTexture(int x, int y) {
		if (x < 0 || x >= 96 || y < 0 || y >= 96) {
			return 0;
		}
		if (x >= 48 && y < 48) {
			x -= 48;
		} else if (x < 48 && y >= 48) {
			y -= 48;
		} else if (x >= 48 && y >= 48) {
			x -= 48;
			y -= 48;
		}
		return tileGroundTexture[x * 48 + y] & 0xff;
	}

	public int getGroundTexturesOverlay(int x, int y) {
		if (x < 0 || x >= 96 || y < 0 || y >= 96) {
			return 0;
		}
		if (x >= 48 && y < 48) {
			x -= 48;
		} else if (x < 48 && y >= 48) {
			y -= 48;
		} else if (x >= 48 && y >= 48) {
			x -= 48;
			y -= 48;
		}
		return tileGroundOverlay[x * 48 + y] & 0xff;
	}

	public int getDiagonalWall(int x, int y) {
		if (x < 0 || x >= 96 || y < 0 || y >= 96) {
			return 0;
		}
		if (x >= 48 && y < 48) {
			x -= 48;
		} else if (x < 48 && y >= 48) {
			y -= 48;
		} else if (x >= 48 && y >= 48) {
			x -= 48;
			y -= 48;
		}
		return tileDiagonalWall[x * 48 + y];
	}

	public int getVerticalWall(int x, int y) {
		if (x < 0 || x >= 96 || y < 0 || y >= 96) {
			return 0;
		}
		if (x >= 48 && y < 48) {
			x -= 48;
		} else if (x < 48 && y >= 48) {
			y -= 48;
		} else if (x >= 48 && y >= 48) {
			x -= 48;
			y -= 48;
		}
		return tileVerticalWall[x * 48 + y] & 0xff;
	}

	public int getHorizontalWall(int x, int y) {
		if (x < 0 || x >= 96 || y < 0 || y >= 96) {
			return 0;
		}
		if (x >= 48 && y < 48) {
			x -= 48;
		} else if (x < 48 && y >= 48) {
			y -= 48;
		} else if (x >= 48 && y >= 48) {
			x -= 48;
			y -= 48;
		}
		return tileHorizontalWall[x * 48 + y] & 0xff;
	}

	public int getOverlayIfRequired(int x, int y, int underlay) {
		int texture = getGroundTexturesOverlay(x, y);
		if (texture == 0) {
			return underlay;
		}
		return Texture.TILE_COLORS[texture - 1];
	}

	public int method427(int x, int y) {
		int texture = getGroundTexturesOverlay(x, y);
		if (texture == 0) {
			return -1;
		}
		return Texture.TILE_UNKNOWN[texture - 1] != 2 ? 0 : 1;
	}

	public void orWalkable(int x, int y, int or) {
		tiles[x][y] |= or;
	}

}