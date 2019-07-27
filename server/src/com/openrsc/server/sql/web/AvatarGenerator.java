package com.openrsc.server.sql.web;

import com.openrsc.server.Constants;
import com.openrsc.server.model.PlayerAppearance;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class AvatarGenerator {

	/// The width of the avatar to generate
	private final static int AVATAR_WIDTH = 65;

	/// The height of the avatar to generate
	private final static int AVATAR_HEIGHT = 115;

	public void generateAvatar(int playerID, PlayerAppearance appearance, int[] wornItems) throws IOException {
		if (appearance == null) {
			throw new NullPointerException("The provided appearance may not be null!");
		}
		if (wornItems == null) {
			throw new NullPointerException("The provided worn items array may not be null!");
		}
		if (wornItems.length != 12) {
			throw new IllegalArgumentException("The provided worn items array is invalid!");
		}

		new AvatarTransaction(playerID, appearance, wornItems);
	}

	/// An internal transaction type
	private final static class AvatarTransaction {
		/// An array of unpacked sprites
		private final static Sprite[] sprites = new Sprite[4000];
		/// The animations
		private final static AnimationDef[] animations =
			{
				new AnimationDef("head1", 1, 13, true, false, 0),
				new AnimationDef("body1", 2, 6, true, false, 0),
				new AnimationDef("legs1", 3, 15, true, false, 0),
				new AnimationDef("fhead1", 1, 13, true, false, 0),
				new AnimationDef("fbody1", 2, 10, true, false, 0),
				new AnimationDef("head2", 1, 13, true, false, 0),
				new AnimationDef("head3", 1, 13, true, false, 0), // allow shemales.
				new AnimationDef("head4", 1, 13, true, false, 0),
				new AnimationDef("chefshat", 16777215, 0, true, false, 0),
				new AnimationDef("apron", 16777215, 0, true, false, 0),
				new AnimationDef("apron", 9789488, 0, true, false, 0),
				new AnimationDef("boots", 5592405, 0, true, false, 0),
				new AnimationDef("fullhelm", 16737817, 0, true, false, 0),
				new AnimationDef("fullhelm", 15654365, 0, true, false, 0),
				new AnimationDef("fullhelm", 15658734, 0, true, false, 0),
				new AnimationDef("fullhelm", 10072780, 0, true, false, 0),
				new AnimationDef("fullhelm", 11717785, 0, true, false, 0),
				new AnimationDef("fullhelm", 65535, 0, true, false, 0),
				new AnimationDef("fullhelm", 3158064, 0, true, false, 0),
				new AnimationDef("fullhelm", 16777215, 0, true, false, 0),
				new AnimationDef("chainmail", 16737817, 0, true, false, 0),
				new AnimationDef("chainmail", 15654365, 0, true, false, 0),
				new AnimationDef("chainmail", 15658734, 0, true, false, 0),
				new AnimationDef("chainmail", 10072780, 0, true, false, 0),
				new AnimationDef("chainmail", 11717785, 0, true, false, 0),
				new AnimationDef("chainmail", 65535, 0, true, false, 0),
				new AnimationDef("chainmail", 3158064, 0, true, false, 0),
				new AnimationDef("platemailtop", 16737817, 0, true, false, 0),
				new AnimationDef("platemailtop", 15654365, 0, true, false, 0),
				new AnimationDef("platemailtop", 15658734, 0, true, false, 0),
				new AnimationDef("platemailtop", 10072780, 0, true, false, 0),
				new AnimationDef("platemailtop", 11717785, 0, true, false, 0),
				new AnimationDef("platemailtop", 3158064, 0, true, false, 0),
				new AnimationDef("platemailtop", 65535, 0, true, false, 0),
				new AnimationDef("platemailtop", 16777215, 0, true, false, 0),
				new AnimationDef("platemailtop", 10083839, 0, true, false, 0),
				new AnimationDef("platemaillegs", 16737817, 0, true, false, 0),
				new AnimationDef("platemaillegs", 15654365, 0, true, false, 0),
				new AnimationDef("platemaillegs", 15658734, 0, true, false, 0),
				new AnimationDef("platemaillegs", 10072780, 0, true, false, 0),
				new AnimationDef("platemaillegs", 11717785, 0, true, false, 0),
				new AnimationDef("platemaillegs", 65535, 0, true, false, 0),
				new AnimationDef("platemaillegs", 4210752, 0, true, false, 0),
				new AnimationDef("platemaillegs", 16777215, 0, true, false, 0),
				new AnimationDef("platemaillegs", 10083839, 0, true, false, 0),
				new AnimationDef("leatherarmour", 0, 0, true, false, 0),
				new AnimationDef("leathergloves", 0, 0, true, false, 0),
				new AnimationDef("sword", 16737817, 0, true, false, 0),
				new AnimationDef("sword", 15654365, 0, true, false, 0),
				new AnimationDef("sword", 15658734, 0, true, false, 0),
				new AnimationDef("sword", 10072780, 0, true, false, 0),
				new AnimationDef("sword", 11717785, 0, true, false, 0),
				new AnimationDef("sword", 65535, 0, true, false, 0),
				new AnimationDef("sword", 3158064, 0, true, false, 0),
				new AnimationDef("fplatemailtop", 16737817, 0, true, false, 0),
				new AnimationDef("fplatemailtop", 15654365, 0, true, false, 0),
				new AnimationDef("fplatemailtop", 15658734, 0, true, false, 0),
				new AnimationDef("fplatemailtop", 10072780, 0, true, false, 0),
				new AnimationDef("fplatemailtop", 11717785, 0, true, false, 0),
				new AnimationDef("fplatemailtop", 65535, 0, true, false, 0),
				new AnimationDef("fplatemailtop", 3158064, 0, true, false, 0),
				new AnimationDef("apron", 16777215, 0, true, false, 0),
				new AnimationDef("cape", 16711680, 0, true, false, 0),
				new AnimationDef("cape", 2434341, 0, true, false, 0),
				new AnimationDef("cape", 4210926, 0, true, false, 0),
				new AnimationDef("cape", 4246592, 0, true, false, 0),
				new AnimationDef("cape", 15658560, 0, true, false, 0),
				new AnimationDef("cape", 15636736, 0, true, false, 0),
				new AnimationDef("cape", 11141341, 0, true, false, 0),
				new AnimationDef("mediumhelm", 16737817, 0, true, false, 0),
				new AnimationDef("mediumhelm", 15654365, 0, true, false, 0),
				new AnimationDef("mediumhelm", 15658734, 0, true, false, 0),
				new AnimationDef("mediumhelm", 10072780, 0, true, false, 0),
				new AnimationDef("mediumhelm", 11717785, 0, true, false, 0),
				new AnimationDef("mediumhelm", 65535, 0, true, false, 0),
				new AnimationDef("mediumhelm", 3158064, 0, true, false, 0),
				new AnimationDef("wizardsrobe", 255, 0, true, false, 0),
				new AnimationDef("wizardshat", 255, 0, true, false, 0),
				new AnimationDef("wizardshat", 4210752, 0, true, false, 0),
				new AnimationDef("necklace", 15658734, 0, true, false, 0),
				new AnimationDef("necklace", 16763980, 0, true, false, 0),
				new AnimationDef("skirt", 255, 0, true, false, 0),
				new AnimationDef("wizardsrobe", 4210752, 0, true, false, 0),
				new AnimationDef("Wizardsrobe", 10510400, 0, true, false, 0),
				new AnimationDef("wizardsrobe", 15609904, 0, true, false, 0),
				new AnimationDef("Wizardsrobe", 16777215, 0, true, false, 0),
				new AnimationDef("skirt", 16777215, 0, true, false, 0),
				new AnimationDef("skirt", 10510400, 0, true, false, 0),
				new AnimationDef("skirt", 4210752, 0, true, false, 0),
				new AnimationDef("skirt", 16036851, 0, true, false, 0),
				new AnimationDef("skirt", 15609904, 0, true, false, 0),
				new AnimationDef("Skirt", 8400921, 0, true, false, 0),
				new AnimationDef("skirt", 7824998, 0, true, false, 0),
				new AnimationDef("skirt", 7829367, 0, true, false, 0),
				new AnimationDef("skirt", 2245205, 0, true, false, 0),
				new AnimationDef("skirt", 4347170, 0, true, false, 0),
				new AnimationDef("skirt", 26214, 0, true, false, 0),
				new AnimationDef("squareshield", 16737817, 0, true, false, 0),
				new AnimationDef("squareshield", 15654365, 0, true, false, 0),
				new AnimationDef("squareshield", 15658734, 0, true, false, 0),
				new AnimationDef("squareshield", 10072780, 0, true, false, 0),
				new AnimationDef("squareshield", 11717785, 0, true, false, 0),
				new AnimationDef("squareshield", 56797, 0, true, false, 0),
				new AnimationDef("squareshield", 3158064, 0, true, false, 0),
				new AnimationDef("squareshield", 16750896, 0, true, false, 0),
				new AnimationDef("squareshield", 11363121, 0, true, false, 0),
				new AnimationDef("crossbow", 0, 0, false, false, 0),
				new AnimationDef("longbow", 0, 0, false, false, 0),
				new AnimationDef("battleaxe", 16737817, 0, true, false, 0),
				new AnimationDef("battleaxe", 15654365, 0, true, false, 0),
				new AnimationDef("battleaxe", 15658734, 0, true, false, 0),
				new AnimationDef("battleaxe", 10072780, 0, true, false, 0),
				new AnimationDef("battleaxe", 11717785, 0, true, false, 0),
				new AnimationDef("battleaxe", 65535, 0, true, false, 0),
				new AnimationDef("battleaxe", 3158064, 0, true, false, 0),
				new AnimationDef("mace", 16737817, 0, true, false, 0),
				new AnimationDef("mace", 15654365, 0, true, false, 0),
				new AnimationDef("mace", 15658734, 0, true, false, 0),
				new AnimationDef("mace", 10072780, 0, true, false, 0),
				new AnimationDef("mace", 11717785, 0, true, false, 0),
				new AnimationDef("mace", 65535, 0, true, false, 0),
				new AnimationDef("mace", 3158064, 0, true, false, 0),
				new AnimationDef("staff", 0, 0, true, false, 0),
				new AnimationDef("rat", 4805259, 0, true, false, 0),
				new AnimationDef("demon", 16384000, 0, true, false, 0),
				new AnimationDef("spider", 13408576, 0, true, false, 0),
				new AnimationDef("spider", 16728144, 0, true, false, 0),
				new AnimationDef("camel", 0, 0, true, false, 0),
				new AnimationDef("cow", 0, 0, true, false, 0),
				new AnimationDef("sheep", 0, 0, false, false, 0),
				new AnimationDef("unicorn", 0, 0, true, false, 0),
				new AnimationDef("bear", 0, 0, true, false, 0),
				new AnimationDef("chicken", 0, 0, true, false, 0),
				new AnimationDef("skeleton", 0, 0, true, false, 0),
				new AnimationDef("skelweap", 0, 0, true, true, 0),
				new AnimationDef("zombie", 0, 0, true, false, 0),
				new AnimationDef("zombweap", 0, 0, true, true, 0),
				new AnimationDef("ghost", 0, 0, true, false, 0),
				new AnimationDef("bat", 0, 0, true, false, 0),
				new AnimationDef("goblin", 8969727, 0, true, false, 0),
				new AnimationDef("goblin", 16711680, 0, true, false, 0),
				new AnimationDef("goblin", 47872, 0, true, false, 0),
				new AnimationDef("gobweap", 65535, 0, true, true, 0),
				new AnimationDef("scorpion", 0, 0, true, false, 0),
				new AnimationDef("dragon", 65280, 0, true, false, 0),
				new AnimationDef("dragon", 16711680, 0, true, false, 0),
				new AnimationDef("dragon", 21981, 0, true, false, 0),
				new AnimationDef("Wolf", 0, 0, true, false, 0),
				new AnimationDef("Wolf", 10066329, 0, true, false, 0),
				new AnimationDef("partyhat", 16711680, 0, true, false, 0),
				new AnimationDef("partyhat", 16776960, 0, true, false, 0),
				new AnimationDef("partyhat", 255, 0, true, false, 0),
				new AnimationDef("partyhat", 65280, 0, true, false, 0),
				new AnimationDef("partyhat", 16711935, 0, true, false, 0),
				new AnimationDef("partyhat", 16777215, 0, true, false, 0),
				new AnimationDef("leathergloves", 11202303, 0, true, false, 0),
				new AnimationDef("chicken", 16711680, 0, true, false, 0),
				new AnimationDef("fplatemailtop", 10083839, 0, true, false, 0),
				new AnimationDef("skirt", 1118481, 0, true, false, 0),
				new AnimationDef("Wolf", 9789488, 0, true, false, 0),
				new AnimationDef("spider", 65535, 0, true, false, 0),
				new AnimationDef("battleaxe", 16711748, 0, true, false, 0),
				new AnimationDef("sword", 16711748, 0, true, false, 0),
				new AnimationDef("eyepatch", 0, 0, true, true, 0),
				new AnimationDef("demon", 3158064, 0, true, false, 0),
				new AnimationDef("dragon", 3158064, 0, true, false, 0),
				new AnimationDef("spider", 14535680, 0, true, false, 0),
				new AnimationDef("Wolf", 2236962, 0, true, false, 0),
				new AnimationDef("unicorn", 2236962, 0, true, false, 0),
				new AnimationDef("demon", 6291456, 0, true, false, 0),
				new AnimationDef("spider", 2236962, 0, true, false, 0),
				new AnimationDef("necklace", 3158064, 0, true, false, 0),
				new AnimationDef("rat", 11184810, 0, true, false, 0),
				new AnimationDef("mediumhelm", 11250603, 0, true, false, 0),
				new AnimationDef("chainmail", 11250603, 0, true, false, 0),
				new AnimationDef("wizardshat", 16711680, 0, true, false, 0),
				new AnimationDef("legs1", 9785408, 0, true, false, 0),
				new AnimationDef("gasmask", 0, 0, true, false, 0),
				new AnimationDef("mediumhelm", 16711748, 0, true, false, 0),
				new AnimationDef("spider", 3852326, 0, true, false, 0),
				new AnimationDef("spear", 0, 0, true, false, 0),
				new AnimationDef("halloweenmask", 52224, 0, true, false, 0),
				new AnimationDef("wizardsrobe", 1052688, 0, true, false, 0),
				new AnimationDef("skirt", 1052688, 0, true, false, 0),
				new AnimationDef("halloweenmask", 16711680, 0, true, false, 0),
				new AnimationDef("halloweenmask", 255, 0, true, false, 0),
				new AnimationDef("skirt", 16755370, 15, true, false, 0),
				new AnimationDef("skirt", 11206570, 15, true, false, 0),
				new AnimationDef("skirt", 11184895, 15, true, false, 0),
				new AnimationDef("skirt", 16777164, 15, true, false, 0),
				new AnimationDef("skirt", 13434879, 15, true, false, 0),
				new AnimationDef("wizardshat", 16755370, 0, true, false, 0),
				new AnimationDef("wizardshat", 11206570, 0, true, false, 0),
				new AnimationDef("wizardshat", 11184895, 0, true, false, 0),
				new AnimationDef("wizardshat", 16777164, 0, true, false, 0),
				new AnimationDef("wizardshat", 13434879, 0, true, false, 0),
				new AnimationDef("wizardsrobe", 16755370, 0, true, false, 0),
				new AnimationDef("wizardsrobe", 11206570, 0, true, false, 0),
				new AnimationDef("wizardsrobe", 11184895, 0, true, false, 0),
				new AnimationDef("wizardsrobe", 16777164, 0, true, false, 0),
				new AnimationDef("wizardsrobe", 13434879, 0, true, false, 0),
				new AnimationDef("Wizardsrobe", 3978097, 0, true, false, 0),
				new AnimationDef("skirt", 3978097, 0, true, false, 0),
				new AnimationDef("boots", 16755370, 0, true, false, 0),
				new AnimationDef("boots", 11206570, 0, true, false, 0),
				new AnimationDef("boots", 11184895, 0, true, false, 0),
				new AnimationDef("boots", 16777164, 0, true, false, 0),
				new AnimationDef("boots", 13434879, 0, true, false, 0),
				new AnimationDef("santahat", 0, 0, true, false, 0),
				new AnimationDef("ibanstaff", 0, 0, true, false, 0),
				new AnimationDef("souless", 0, 0, true, false, 0),
				new AnimationDef("boots", 16777215, 0, true, false, 0),
				new AnimationDef("legs1", 16777215, 0, true, false, 0),
				new AnimationDef("Wizardsrobe", 8421376, 0, true, false, 0),
				new AnimationDef("skirt", 8421376, 0, true, false, 0),
				new AnimationDef("cape", 16777215, 0, true, false, 0),
				new AnimationDef("Wolf", 13420580, 0, true, false, 0),
				new AnimationDef("bunnyears", 0, 0, true, false, 0),
				new AnimationDef("saradominstaff", 0, 0, true, false, 0),
				new AnimationDef("spear", 56797, 0, true, false, 0),
				new AnimationDef("skirt", 1392384, 0, true, false, 0),
				new AnimationDef("wizardsrobe", 1392384, 0, true, false, 0),
				new AnimationDef("wolf", 5585408, 0, true, false, 0),
				new AnimationDef("chicken", 6893315, 0, true, false, 0),
				new AnimationDef("squareshield", 13500416, 0, true, false, 0),
				new AnimationDef("cape", 16777215, 0, true, false, 0),
				new AnimationDef("boots", 1118481, 0, true, false, 0),
				new AnimationDef("wizardsrobe", 1118481, 0, true, false, 0),
				new AnimationDef("Scythe", 0, 0, true, false, 0),
				/**
				 * Add custom animation below.
				*/
				new AnimationDef("hatchet", 16737817, 0, true, false, 0), //230
			new AnimationDef("hatchet", 15654365, 0, true, false, 0),
			new AnimationDef("hatchet", 15658734, 0, true, false, 0),
			new AnimationDef("hatchet", 10072780, 0, true, false, 0),
			new AnimationDef("hatchet", 11717785, 0, true, false, 0),
			new AnimationDef("hatchet", 65535, 0, true, false, 0),
			new AnimationDef("hatchet", 3158064, 0, true, false, 0),
			new AnimationDef("kiteshield", 0xBB4B12, 0, true, false, 0), //237
			new AnimationDef("kiteshield", 0xAFA2A2, 0, true, false, 0),
			new AnimationDef("kiteshield", 0xAFAFAF, 0, true, false, 0),
			new AnimationDef("kiteshield", 0x708396, 0, true, false, 0),
			new AnimationDef("kiteshield", 0x839670, 0, true, false, 0),
			new AnimationDef("kiteshield", 48059, 0, true, false, 0),
			new AnimationDef("kiteshield", 0x232323, 0, true, false, 0),
			new AnimationDef("dragonshield", 0, 0, true, false, 0), //244
			new AnimationDef("dragonmedhelm", 0, 0, true, false, 0) //245
			};
		/// A helper array for rendering
		private final static int characterSkinColours[] = {0xecded0, 0xccb366, 0xb38c40, 0x997326, 0x906020};
		/// A helper array for rendering
		private final static int characterHairColours[] = {0xffc030, 0xffa040, 0x805030, 0x604020, 0x303030, 0xff6020, 0xff4000, 0xffffff, 65280, 65535};
		/// A helper array for rendering
		private final static int characterTopBottomColours[] = {0xff0000, 0xff8000, 0xffe000, 0xa0e000, 57344, 32768, 41088, 45311, 33023, 12528, 0xe000e0, 0x303030, 0x604000, 0x805000, 0xffffff};
		private final static int npcAnimationArray[][] =
			{
				{11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3, 4},
				{11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3, 4},
				{11, 3, 2, 9, 7, 1, 6, 10, 0, 5, 8, 4},
				{3, 4, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5},
				{3, 4, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5},
				{4, 3, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5},
				{11, 4, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3},
				{11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 4, 3}
			};

		/// Load the sprites
		static {
			try {
				ZipFile spritesArchive = new ZipFile("conf" + File.separator + "server" + File.separator + "data" + File.separator + "Sprites.rscd");
				int animationNumber = 0;
				label0:
				for (int animationIndex = 0; animationIndex < animations.length; animationIndex++) {
					String s = animations[animationIndex].getName();
					for (int nextAnimationIndex = 0; nextAnimationIndex < animationIndex; nextAnimationIndex++) {
						if (!animations[nextAnimationIndex].getName().equalsIgnoreCase(s)) {
							continue;
						}
						animations[animationIndex].number = animations[nextAnimationIndex].getNumber();
						continue label0;
					}

					loadSprite(spritesArchive, animationNumber, 15);
					if (animations[animationIndex].hasA()) {
						loadSprite(spritesArchive, animationNumber + 15, 3);
					}
					if (animations[animationIndex].hasF()) {
						loadSprite(spritesArchive, animationNumber + 18, 9);
					}
					animations[animationIndex].number = animationNumber;
					animationNumber += 27;
					if (animationNumber == 1998) {
						animationNumber = 3300;
					}
				}
			} catch (IOException ioe) {
				throw new ExceptionInInitializerError();
			}
		}

		/// The appearance of the player to generate an avatar of
		private final PlayerAppearance appearance;
		/// The worn items of the player to generate an avatar of
		private final int[] wornItems;
		/// The pixel array for rendering to
		private final int[] pixels;

		/// Sole Constructor
		AvatarTransaction(int playerID, PlayerAppearance appearance, int[] wornItems) throws IOException {
			this.appearance = appearance;
			this.wornItems = wornItems;
			this.pixels = new int[AVATAR_WIDTH * AVATAR_HEIGHT];

			drawPlayer(0, 0, AVATAR_WIDTH - 1, AVATAR_HEIGHT - 12, 0);

			BufferedImage img = new BufferedImage(AVATAR_WIDTH, AVATAR_HEIGHT, BufferedImage.TYPE_INT_ARGB);

			for (int i = 0; i < pixels.length; ++i) {
				if (pixels[i] != 0) {
					img.setRGB(i % AVATAR_WIDTH, i / AVATAR_WIDTH, pixels[i] | 0xFF000000);
				}
			}
			ImageIO.write(img, "png", new File(Constants.GameServer.AVATAR_DIR + playerID + ".png"));
		}

		/// A helper function to load a single sprite
		private static void loadSprite(ZipFile spritesArchive, int id) throws IOException {
			ZipEntry e = spritesArchive.getEntry(String.valueOf(id));
			if (e == null) {
				System.err.println("Missing sprite: " + id);
			}
			InputStream bis = new BufferedInputStream(spritesArchive.getInputStream(e));
			byte[] buffer = new byte[bis.available()];
			bis.read(buffer, 0, buffer.length);
			ByteBuffer data = ByteBuffer.wrap(buffer);
			sprites[id] = Sprite.unpack(data);
		}

		/// A helper function to load a number of sprites
		private static void loadSprite(ZipFile archive, int id, int count) throws IOException {
			for (int i = id; i < id + count; i++) {
				loadSprite(archive, i);
			}
		}

		/// A helper function for rendering
		void spriteClip4(int i, int j, int k, int l, int i1, int overlay, int k1, int l1, boolean flag) {
			if (overlay == 0) {
				overlay = 0xffffff;
			}
			if (k1 == 0) {
				k1 = 0xffffff;
			}
			int i2 = sprites[i1].getWidth();
			int j2 = sprites[i1].getHeight();
			int k2 = 0;
			int l2 = 0;
			int i3 = l1 << 16;
			int j3 = (i2 << 16) / k;
			int k3 = (j2 << 16) / l;
			int l3 = -(l1 << 16) / l;
			if (sprites[i1].requiresShift()) {
				int i4 = sprites[i1].getSomething1();
				int k4 = sprites[i1].getSomething2();
				j3 = (i4 << 16) / k;
				k3 = (k4 << 16) / l;
				int j5 = sprites[i1].getXShift();
				int k5 = sprites[i1].getYShift();
				if (flag) {
					j5 = i4 - sprites[i1].getWidth() - j5;
				}
				i += ((j5 * k + i4) - 1) / i4;
				int l5 = ((k5 * l + k4) - 1) / k4;
				j += l5;
				i3 += l5 * l3;
				if ((j5 * k) % i4 != 0) {
					k2 = (i4 - (j5 * k) % i4 << 16) / k;
				}
				if ((k5 * l) % k4 != 0) {
					l2 = (k4 - (k5 * l) % k4 << 16) / l;
				}
				k = ((((sprites[i1].getWidth() << 16) - k2) + j3) - 1) / j3;
				l = ((((sprites[i1].getHeight() << 16) - l2) + k3) - 1) / k3;
			}
			int j4 = j * AVATAR_WIDTH;
			i3 += i << 16;
			if (j < 0) {
				int l4 = 0 - j;
				l -= l4;
				j = 0;
				j4 += l4 * AVATAR_WIDTH;
				l2 += k3 * l4;
				i3 += l3 * l4;
			}
			if (j + l >= AVATAR_HEIGHT) {
				l -= ((j + l) - AVATAR_HEIGHT) + 1;
			}
			int i5 = 2;
			if (k1 == 0xffffff) {
				if (!flag) {
					spritePlotTransparent(pixels, sprites[i1].getPixels(), 0, k2, l2, j4, k, l, j3, k3, i2, overlay, i3, l3, i5);
					return;
				}
				spritePlotTransparent(pixels, sprites[i1].getPixels(), 0, (sprites[i1].getWidth() << 16) - k2 - 1, l2, j4, k, l, -j3, k3, i2, overlay, i3, l3, i5);
				return;
			}
			if (!flag) {
				spritePlotTransparent(pixels, sprites[i1].getPixels(), 0, k2, l2, j4, k, l, j3, k3, i2, overlay, k1, i3, l3, i5);
				return;
			}
			spritePlotTransparent(pixels, sprites[i1].getPixels(), 0, (sprites[i1].getWidth() << 16) - k2 - 1, l2, j4, k, l, -j3, k3, i2, overlay, k1, i3, l3, i5);
		}

		/// A helper function for rendering
		private void spritePlotTransparent(int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int overlay, int k2, int l2, int i3) {
			int i4 = overlay >> 16 & 0xff; //R
			int j4 = overlay >> 8 & 0xff;  //G
			int k4 = overlay & 0xff;       //B
			int l4 = j;
			for (int i5 = -j1; i5 < 0; i5++) {
				int j5 = (k >> 16) * i2;
				int k5 = k2 >> 16;
				int l5 = i1;
				if (k5 < 0) {
					int i6 = 0 - k5;
					l5 -= i6;
					k5 = 0;
					j += k1 * i6;
				}
				if (k5 + l5 >= AVATAR_WIDTH) {
					int j6 = (k5 + l5) - AVATAR_WIDTH;
					l5 -= j6;
				}
				i3 = 1 - i3;
				if (i3 != 0) {
					for (int k6 = k5; k6 < k5 + l5; k6++) {
						i = ai1[(j >> 16) + j5];
						if (i != 0) {
							int j3 = i >> 16 & 0xff;
							int k3 = i >> 8 & 0xff;
							int l3 = i & 0xff;
							if (j3 == k3 && k3 == l3) {
								ai[k6 + l] = ((j3 * i4 >> 8) << 16) + ((k3 * j4 >> 8) << 8) + (l3 * k4 >> 8);
							} else {
								ai[k6 + l] = i;
							}
						}
						j += k1;
					}

				}
				k += l1;
				j = l4;
				l += AVATAR_WIDTH;
				k2 += l2;
			}
		}

		/// A helper function for rendering
		private void spritePlotTransparent(int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int overlay, int k2, int l2, int i3, int j3) {
			int j4 = overlay >> 16 & 0xff;
			int k4 = overlay >> 8 & 0xff;
			int l4 = overlay & 0xff;
			int i5 = k2 >> 16 & 0xff;
			int j5 = k2 >> 8 & 0xff;
			int k5 = k2 & 0xff;
			int l5 = j;
			for (int i6 = -j1; i6 < 0; i6++) {
				int j6 = (k >> 16) * i2;
				int k6 = l2 >> 16;
				int l6 = i1;
				if (k6 < 0) {
					int i7 = 0 - k6;
					l6 -= i7;
					k6 = 0;
					j += k1 * i7;
				}
				if (k6 + l6 >= AVATAR_WIDTH) {
					int j7 = (k6 + l6) - AVATAR_WIDTH;
					l6 -= j7;
				}
				j3 = 1 - j3;
				if (j3 != 0) {
					for (int k7 = k6; k7 < k6 + l6; k7++) {
						i = ai1[(j >> 16) + j6];
						if (i != 0) {
							int k3 = i >> 16 & 0xff;
							int l3 = i >> 8 & 0xff;
							int i4 = i & 0xff;
							if (k3 == l3 && l3 == i4) {
								ai[k7 + l] = ((k3 * j4 >> 8) << 16) + ((l3 * k4 >> 8) << 8) + (i4 * l4 >> 8);
							} else if (k3 == 255 && l3 == i4) {
								ai[k7 + l] = ((k3 * i5 >> 8) << 16) + ((l3 * j5 >> 8) << 8) + (i4 * k5 >> 8);
							} else {
								ai[k7 + l] = i;
							}
						}
						j += k1;
					}
				}
				k += l1;
				j = l5;
				l += AVATAR_WIDTH;
				l2 += i3;
			}
		}

		/// A helper function for rendering
		private void drawPlayer(int x, int y, int scaleX, int scaleY, int unknown) {
			for (int k2 = 0; k2 < 12; k2++) {
				int l2 = npcAnimationArray[0][k2];
				int animationIndex = wornItems[l2] - 1;
				if (animationIndex >= 0) {
					int k4 = 0;
					int i5 = 0;
					int ANGLE = 1;
					int k5 = ANGLE + animations[animationIndex].getNumber();
					k4 = (k4 * scaleX) / sprites[k5].getSomething1();
					i5 = (i5 * scaleY) / sprites[k5].getSomething2();
					int l5 = (scaleX * sprites[k5].getSomething1()) / sprites[animations[animationIndex].getNumber()].getSomething1();
					k4 -= (l5 - scaleX) / 2;
					int colour = animations[animationIndex].getCharColour();
					int skinColour = characterSkinColours[appearance.getSkinColour()];
					if (colour == 1)
						colour = characterHairColours[appearance.getHairColour()];
					else if (colour == 2)
						colour = characterTopBottomColours[appearance.getTopColour()];
					else if (colour == 3)
						colour = characterTopBottomColours[appearance.getTrouserColour()];
					spriteClip4(x + k4, y + i5, l5, scaleY, k5, colour, skinColour, unknown, false);
				}
			}
		}

		/// An internal helper class
		private final static class Sprite {
			private int[] pixels;
			private int width;
			private int height;
			private boolean requiresShift;
			private int xShift;
			private int yShift;
			private int something1;
			private int something2;

			Sprite(int[] pixels, int width, int height) {
				this.pixels = pixels;
				this.width = width;
				this.height = height;
			}

			static Sprite unpack(ByteBuffer in)
				throws
				IOException {
				if (in.remaining() < 25) {
					throw new IOException("Provided buffer too short - Headers missing");
				}
				int width = in.getInt();
				int height = in.getInt();

				boolean requiresShift = in.get() == 1;
				int xShift = in.getInt();
				int yShift = in.getInt();

				int something1 = in.getInt();
				int something2 = in.getInt();

				int[] pixels = new int[width * height];
				if (in.remaining() < (pixels.length * 4)) {
					throw new IOException("Provided buffer too short - Pixels missing");
				}
				for (int c = 0; c < pixels.length; c++) {
					pixels[c] = in.getInt();
				}
				Sprite sprite = new Sprite(pixels, width, height);
				sprite.requiresShift = requiresShift;
				sprite.xShift = xShift;
				sprite.yShift = yShift;
				sprite.something1 = something1;
				sprite.something2 = something2;
				return sprite;
			}

			int getSomething1() {
				return something1;
			}

			int getSomething2() {
				return something2;
			}

			boolean requiresShift() {
				return requiresShift;
			}

			int getXShift() {
				return xShift;
			}

			int getYShift() {
				return yShift;
			}

			int[] getPixels() {
				return pixels;
			}

			int getWidth() {
				return width;
			}

			public int getHeight() {
				return height;
			}
		}

		/// A helper class for describing animation offsets
		private final static class AnimationDef {
			public String name;
			int charColour;
			boolean hasA;
			boolean hasF;
			public int number;

			AnimationDef(String name, int charColour, int genderModel, boolean hasA, boolean hasF, int number) {
				this.name = name;
				this.charColour = charColour;
				this.hasA = hasA;
				this.hasF = hasF;
				this.number = number;
			}

			public String getName() {
				return name;
			}

			int getCharColour() {
				return charColour;
			}

			boolean hasA() {
				return hasA;
			}

			boolean hasF() {
				return hasF;
			}

			public int getNumber() {
				return number;
			}
		}
	}
}
