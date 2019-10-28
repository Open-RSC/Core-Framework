package com.openrsc.server.util.rsc;

import com.openrsc.server.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
	private static Font loadedFonts[];

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

	public static byte[] generateCaptcha(Player p) {
		final BufferedImage image = new BufferedImage(255 - 10, 40, BufferedImage.TYPE_INT_RGB);
		final Graphics2D gfx = image.createGraphics();
		final String captcha = words.get(DataConversions.random(0, words.size()));
		gfx.setColor(Color.BLACK);
		gfx.fillRect(0, 0, 255, 40);
		int currentX = 10;
		for (int i = 0; i <= captcha.length() - 1; i++) {
			gfx.setColor(colors.get(DataConversions.random(0, colors.size() - 1)));
			gfx.setFont(loadedFonts[DataConversions.random(0, loadedFonts.length - 1)]);
			gfx.drawString(String.valueOf(captcha.charAt(i)), currentX, DataConversions.random(25, 35));
			currentX += gfx.getFontMetrics().charWidth(captcha.charAt(i)) + (DataConversions.random(5, 10));
		}
		p.setSleepword(captcha);
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
