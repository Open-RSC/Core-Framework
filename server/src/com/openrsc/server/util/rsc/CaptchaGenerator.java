package com.openrsc.server.util.rsc;

import com.openrsc.server.model.entity.player.Player;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CaptchaGenerator {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private static List<Color> colors = new ArrayList<>();
	private static List<String> words = new ArrayList<>();
	private static String fontFolder = "." + File.separator + "conf" + File.separator + "server" + File.separator + "fonts" + File.separator;
    private static String sleepwordsFolder = "." + File.separator + "conf" + File.separator + "server" + File.separator + "data" + File.separator + "sleepwords" + File.separator;
	private static String specialSleepwordsFolder = "." + File.separator + "conf" + File.separator + "server" + File.separator + "data" + File.separator + "specialsleepwords" + File.separator;
	private static Font loadedFonts[];
	public static int prerenderedSleepwordsSize = 0;
	public static int prerenderedSleepwordsSpecialSize = 0;
	public static boolean usingPrerenderedSleepwords = false;
	public static boolean usingPrerenderedSleepwordsSpecial = false;
	public static List<PrerenderedSleepword> prerenderedSleepwords = new ArrayList<PrerenderedSleepword>();
	public static List<PrerenderedSleepword> prerenderedSleepwordsSpecial = new ArrayList<PrerenderedSleepword>();

	// used for inauthentic RSCL style captchas
	static {
		loadFonts();
		colors.clear();
		colors.add(Color.GREEN);
		colors.add(Color.WHITE);
		colors.add(Color.RED);
		colors.add(Color.PINK);
		colors.add(Color.CYAN);
		colors.add(Color.MAGENTA);
		colors.add(Color.YELLOW);
	}

	public static byte[] generateCaptcha(Player player) {
		boolean queuedSleepword = player.queuedSleepword != null;
	    if (usingPrerenderedSleepwords || (queuedSleepword && usingPrerenderedSleepwordsSpecial)) {
	    	if (queuedSleepword) {
	    		// moderator has sent player a specific sleepword to fill out
	    		try {
	    			player.queuedSleepwordSender.message(player.getUsername() + " is now seeing your queued sleepword...");
				} catch (Exception ex) {} // probably the moderator logged off or something

				if (!player.isUsingCustomClient()) {
					return player.queuedSleepword.rleData;
				} else {
					return player.queuedSleepword.pngData;
				}
			} else {
	    		// normal sleep word generation
				int rand = DataConversions.random(0, prerenderedSleepwordsSize - 1);
				player.setSleepword(rand);
				if (!player.isUsingCustomClient()) {
					return prerenderedSleepwords.get(rand).rleData;
				} else {
					return prerenderedSleepwords.get(rand).pngData;
				}
	    	}
        }
	    return generateRSCLCaptcha(player);
	}

	private static byte[] generateRSCLCaptcha(Player player) {
        if (!player.isUsingCustomClient()) {
            // fallback to pre-rendered image of word "ASLEEP"
            player.setSleepword("asleep");
            return new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x2A, (byte)0x00, (byte)0x00, (byte)0x40, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x6D, (byte)0x29, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x12, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x6A, (byte)0x2C, (byte)0x05, (byte)0x00, (byte)0x10, (byte)0x40, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x69, (byte)0x28, (byte)0x02, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0F, (byte)0x00, (byte)0x00, (byte)0x2A, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x06, (byte)0x08, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x68, (byte)0x15, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0E, (byte)0x02, (byte)0x07, (byte)0x00, (byte)0x0A, (byte)0x00, (byte)0x00, (byte)0x16, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0E, (byte)0x1B, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x6C, (byte)0x45, (byte)0x19, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0F, (byte)0x1D, (byte)0x6B, (byte)0x14, (byte)0x04, (byte)0x24, (byte)0x06, (byte)0xB9, (byte)0x13, (byte)0x02, (byte)0x02, (byte)0x0D, (byte)0x1C, (byte)0x15, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xA0, (byte)0x11, (byte)0x00, (byte)0x17, (byte)0x14, (byte)0x00, (byte)0x03, (byte)0x1A, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x99, (byte)0x14, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x0D, (byte)0x03, (byte)0x00, (byte)0x14, (byte)0x34, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x02, (byte)0x09, (byte)0x6B, (byte)0x16, (byte)0x02, (byte)0x0D, (byte)0x05, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x44, (byte)0x08, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x68, (byte)0x0F, (byte)0x00, (byte)0x02, (byte)0x05, (byte)0x0E, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x46, (byte)0x17, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x06, (byte)0x69, (byte)0x12, (byte)0x03, (byte)0x02, (byte)0x12, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x10, (byte)0x02, (byte)0x10, (byte)0x02, (byte)0x1D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x15, (byte)0x6A, (byte)0x0D, (byte)0x00, (byte)0x07, (byte)0x18, (byte)0x1B, (byte)0x0D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x18, (byte)0x18, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x6B, (byte)0x11, (byte)0x20, (byte)0x18, (byte)0x00, (byte)0x11, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x26, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x70, (byte)0x0B, (byte)0x00, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x18, (byte)0x02, (byte)0x10, (byte)0x02, (byte)0x03, (byte)0x00, (byte)0x3B, (byte)0x02, (byte)0x74, (byte)0x14, (byte)0x00, (byte)0x31, (byte)0x00, (byte)0x00, (byte)0x0F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x14, (byte)0x8C, (byte)0x09, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x15, (byte)0x07, (byte)0x14, (byte)0x09, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x9B, (byte)0x0C, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x14, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x16, (byte)0x0A, (byte)0x28, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x81, (byte)0x13, (byte)0x00, (byte)0x00, (byte)0x16, (byte)0x00, (byte)0x03, (byte)0x10, (byte)0x07, (byte)0x00, (byte)0x00, (byte)0xB2, (byte)0x07, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x0C, (byte)0x0D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x00, (byte)0x16, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0x07, (byte)0x10, (byte)0x12, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x14, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x81, (byte)0x08, (byte)0x00, (byte)0x0C, (byte)0x5C, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x11, (byte)0x02, (byte)0x73, (byte)0x16, (byte)0x42, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x14, (byte)0x16, (byte)0x00, (byte)0x00, (byte)0x73, (byte)0x5E, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x99, (byte)0xFF, (byte)0x56, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xA1, (byte)0x16, (byte)0x47, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x99 };
        } else {
            final String captcha = words.get(DataConversions.random(0, words.size()));
            player.setSleepword(captcha);

            return makeColourfulRSCLCaptcha(captcha);
        }
    }

	public static void loadPrerenderedCaptchas() {
	    if (prerenderedSleepwordsSize > 0) {
	        return; // currently don't support loading more than once
        }

	    File sleepwordDataDir = new File(sleepwordsFolder);
        File[] sleepwordFiles = sleepwordDataDir.listFiles();
        if (sleepwordFiles == null) {
            // server owner doesn't have a sleepwords directory
            return;
        }
        for (File fname : sleepwordFiles) {
            String correctWord = "-null-";
            boolean knowTheWord = false;

            int endOfWordIndex = fname.getName().indexOf('_', 6);
            if (endOfWordIndex < 6) {
                // filename parsed is not of the expected format. assuming all characters except last 4 are the correct word.
                knowTheWord = true;
                correctWord = fname.getName().substring(0, fname.getName().length() - 4); // remove 4 character file extension
            } else {
                // example filenames:
                // sleep_!INCORRECT!instar__flying sno_flying sno (redacted chat) replays_bot204_dist_bot204_replay_penuslarge1_06-18-2018 06.23.47_455.png
                // sleep_crushing_Logg_Tylerbeg_06-13-2018 20.09.59 high alch from 55 to 60 and I got a dmed lol_70.png
                // see: https://github.com/2003scape/rsc-captcha-archives/tree/master/rscplus-replay-sleepwords

                String candidateWord = fname.getName().substring(6, fname.getName().indexOf('_', 6));
                knowTheWord = !(candidateWord.contains("!INCORRECT!") ||
                    candidateWord.contains("!SUDDENLY-AWOKE!"));
                if (knowTheWord) {
                    correctWord = candidateWord;
                }
            }

            prerenderedSleepwords.add(
                new PrerenderedSleepword(
                    fname.getName(),
                    correctWord,
                    knowTheWord,
                    readFull(fname),
                    imageFileToRLE(fname)
                )
            );
        }

        prerenderedSleepwordsSize = prerenderedSleepwords.size();

		if (prerenderedSleepwordsSize > 0) {
			usingPrerenderedSleepwords = true;
		}
    }

	public static void loadSpecialPrerenderedCaptchas() {
		if (prerenderedSleepwordsSpecialSize > 0) {
			return; // currently don't support loading more than once
		}

		File sleepwordDataDir = new File(specialSleepwordsFolder);
		File[] sleepwordFiles = sleepwordDataDir.listFiles();
		if (sleepwordFiles == null) {
			// server owner doesn't have a specialsleepwords directory
			return;
		}
		for (File fname : sleepwordFiles) {
			String correctWord = "-null-";
			boolean knowTheWord;

			int endOfWordIndex = fname.getName().indexOf('_', 6);
			if (endOfWordIndex < 6) {
				// filename parsed is not of the expected format. assuming all characters except last 4 are the correct word.
				knowTheWord = true;
				correctWord = fname.getName().substring(0, fname.getName().length() - 4); // remove 4 character file extension
			} else {
				// example filename:
				// sleep_!ACCEPTANY!how_many_planes__special.png
				// sleep_thirteen__special.png
				String candidateWord = fname.getName().substring(6, fname.getName().indexOf('_', 6));
				knowTheWord = !candidateWord.contains("!ACCEPTANY!");
				if (knowTheWord) {
					correctWord = candidateWord;
				}
			}

			prerenderedSleepwordsSpecial.add(
				new PrerenderedSleepword(
					fname.getName(),
					correctWord,
					knowTheWord,
					readFull(fname),
					imageFileToRLE(fname)
				)
			);
		}

		prerenderedSleepwordsSpecialSize = prerenderedSleepwordsSpecial.size();

		if (prerenderedSleepwordsSpecialSize > 0) {
			usingPrerenderedSleepwordsSpecial = true;
		}
	}

    private static byte[] readFull(File f) {
        try {
            return Files.readAllBytes(f.toPath());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Reads a compatible image format into one that can be displayed by
     * the authentic RSC235 client. PNG and BMP are known compatible.
     *
     * Images will be resized from any arbitrary resolution and
     * colour depth will be reduced to 1 bit.
     *
     * @param fname filename
     * @return run length encoded image byte data
     */
    private static byte[] imageFileToRLE(File fname) {
	    return booleansToRLE(loadImageFileToBooleanImage(fname));
    }

    /**
     * @param fname filename
     * @return ██ █ ██ █ ██ █ █
     *         █ █ █  █ █ ██ ██ 255 x 40
     *          ███ ██ ███ ██ █ boolean style image
     */
    private static boolean[][] loadImageFileToBooleanImage(File fname) {
        int WIDTH = 255;
        int HEIGHT = 40;
        boolean[][] imageArray = new boolean[WIDTH][HEIGHT];

        try {
            BufferedImage image = resizeSanitize(ImageIO.read(fname));
            int imgHeight = image.getHeight();
            int imgWidth = image.getWidth();

            boolean drawToConsole = false; // fun option, but obviously not necessary. :-)

            for (int y = 0; y < imgHeight && y < HEIGHT; y += 1) {
                for (int x = 0; x < imgWidth && x < WIDTH; x += 1) {
                    imageArray[x][y] = image.getRGB(x, y) > -5000;

                    // disabled by default
                    if (drawToConsole) {
                        if (imageArray[x][y]) {
                            System.out.print("█");
                        } else {
                            System.out.print(" ");
                        }
                    }
                }
                if (drawToConsole) System.out.println("/");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageArray;
    }

    /**
     * @param imageData in "custom" boolean[][] format
     * @return bytes that are safe to send to authentic client as image data
     */
    private static byte[] booleansToRLE(boolean[][] imageData) {
        int WIDTH = 255; // Warning: don't try to use the last column, it is buggy in authentic client
        int HEIGHT = 40;
        int x = 0;
        int y = 0;
        boolean lastColour = false;
        int length = 0;
        ArrayList<Byte> image = new ArrayList<Byte>();

        // first row uses RLE horizontally
        // whatever colour is in the last pixel of the row will be used for that entire column
        for (; x < WIDTH; x += 1) {
            if (imageData[x][y] == lastColour) {
                length += 1;
            } else {
                image.add((byte)length);
                length = 1;
                lastColour = !lastColour;
            }
        }
        image.add((byte)length);

        // subsequent rows look at the pixel above and use RLE vertically
        for (y = 1; y < HEIGHT; y += 1) {
            length = 0;
            for(x = 0; x < WIDTH; x += 1) {
                if (imageData[x][y] == imageData[x][y - 1]) {
                    length += 1;
                } else {
                    image.add((byte) length);
                    length = 0;
                }
            }
            image.add((byte)length);
        }

        Byte[] imageBytes = new Byte[image.size()];
        imageBytes = image.toArray(imageBytes);
        return ArrayUtils.toPrimitive(imageBytes);
    }

    // ensure image can be displayed at 255x40 or fall back to 254x40 scaled
    private static BufferedImage resizeSanitize(BufferedImage img) {
        int imgHeight = img.getHeight();
        int imgWidth = img.getWidth();
        int buggyColumn = 254;

        // if image is approximately the correct size, won't scale image & will just truncate to top left corner
        // but we must correct column 255 (the last column) so it doesn't change in value from top row
        if (imgWidth <= 260 && imgHeight <= 45) {
            if (imgWidth <= buggyColumn) {
                return img;
            }
    		int topRowColour = img.getRGB(buggyColumn, 0);
    		for (int y = 1; y < imgHeight; y++) {
                img.setRGB(buggyColumn, y, topRowColour);
			}
			return img;
		} else {
            // image is grossly large & we would like to scale/stretch it down.
            int newWidth = buggyColumn; // limits image to before the buggy column
            int newHeight = 40;

            Image tmp = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage dimg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = dimg.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();

            return dimg;
        }
    }

    /**
     * Makes inauthentic & bizarre colourful RSCL captcha.
     * Returns image bytes in PNG format.
     * @param captcha
     * @return
     */
	private static byte[] makeColourfulRSCLCaptcha(String captcha) {
        final BufferedImage image = new BufferedImage(255 - 10, 40, BufferedImage.TYPE_INT_RGB);
        final Graphics2D gfx = image.createGraphics();

        gfx.setColor(Color.BLACK);
        gfx.fillRect(0, 0, 255, 40);
        int currentX = 10;
        for (int i = 0; i <= captcha.length() - 1; i++) {
            gfx.setColor(colors.get(DataConversions.random(0, colors.size() - 1)));
            gfx.setFont(loadedFonts[DataConversions.random(0, loadedFonts.length - 1)]);
            gfx.drawString(String.valueOf(captcha.charAt(i)), currentX, DataConversions.random(25, 35));
            currentX += gfx.getFontMetrics().charWidth(captcha.charAt(i)) + (DataConversions.random(5, 10));
        }
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.setUseCache(false);
            ImageIO.write(image, "PNG", baos);
            return baos.toByteArray();
        } catch (final IOException e) {
            LOGGER.catching(e);
        } finally {
            gfx.dispose();
            image.flush();
        }
        return null;
    }

	/**
	 * Loads fonts from a folder to a font array
     * This is used later in makeColourfulRSCLCaptcha()
	 */
	private static void loadFonts() {
		words.clear();
		try (final BufferedReader br = new BufferedReader(new FileReader(
			new File(System.getProperty("user.dir") + File.separator + "conf" + File.separator + "server" + File.separator + "words.list")))) {
			for (String line; (line = br.readLine()) != null; )
				words.add(line);
		} catch (final IOException e1) {
			LOGGER.catching(e1);
		}
		final File fontFolderFile = new File(fontFolder);
		final String[] fonts = fontFolderFile.list();
		loadedFonts = new Font[fonts.length];
		for (int i = 0; i < fonts.length; i++)
			try (final FileInputStream fontStream = new FileInputStream(fontFolder + fonts[i])) {
				final Font temp = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontStream);
				loadedFonts[i] = temp.deriveFont(Float.valueOf(DataConversions.random(25, 35)));
			} catch (final Exception e) {
				LOGGER.catching(e);
			}
		LOGGER.info("Loaded " + fonts.length + " fonts.");
	}
}
