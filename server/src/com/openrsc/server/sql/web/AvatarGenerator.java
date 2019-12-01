package com.openrsc.server.sql.web;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.world.World;

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

	private final World world;

	public AvatarGenerator(World world) {
		this.world = world;
	}

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

		new AvatarTransaction(world, playerID, appearance, wornItems);
	}

	public World getWorld() {
		return world;
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
				new AnimationDef("dragonmedhelm", 0, 0, true, false, 0), //245
				new AnimationDef("armorskirt", 0xBB4B12, 0, true, false, 0), //246
				new AnimationDef("armorskirt", 0xAFA2A2, 0, true, false, 0),
				new AnimationDef("armorskirt", 0xAFAFAF, 0, true, false, 0),
				new AnimationDef("armorskirt", 0x708396, 0, true, false, 0),
				new AnimationDef("armorskirt", 0x839670, 0, true, false, 0),
				new AnimationDef("armorskirt", 48059, 0, true, false, 0),
				new AnimationDef("armorskirt", 0x232323, 0, true, false, 0),
				new AnimationDef("longbow", 8537122, 0, true, false, 0), //253
				new AnimationDef("longbow", 11300689, 0, true, false, 0),
				new AnimationDef("longbow", 8941897, 0, true, false, 0),
				new AnimationDef("longbow", 9132849, 0, true, false, 0),
				new AnimationDef("longbow", 10310656, 0, true, false, 0),
				new AnimationDef("longbow", 37281, 0, true, false, 0),
				new AnimationDef("shortsword", 16737817, 0, true, false, 0), //259
				new AnimationDef("shortsword", 15654365, 0, true, false, 0),
				new AnimationDef("shortsword", 15658734, 0, true, false, 0),
				new AnimationDef("shortsword", 10072780, 0, true, false, 0),
				new AnimationDef("shortsword", 11717785, 0, true, false, 0),
				new AnimationDef("shortsword", 65535, 0, true, false, 0),
				new AnimationDef("shortsword", 3158064, 0, true, false, 0),
				new AnimationDef("dagger", 16737817, 0, true, false, 0), //266
				new AnimationDef("dagger", 15654365, 0, true, false, 0),
				new AnimationDef("dagger", 15658734, 0, true, false, 0),
				new AnimationDef("dagger", 10072780, 0, true, false, 0),
				new AnimationDef("dagger", 11717785, 0, true, false, 0),
				new AnimationDef("dagger", 65535, 0, true, false, 0),
				new AnimationDef("dagger", 3158064, 0, true, false, 0),
				new AnimationDef("poisoneddagger", 16737817, 0, true, false, 0), //273
				new AnimationDef("poisoneddagger", 15654365, 0, true, false, 0),
				new AnimationDef("poisoneddagger", 15658734, 0, true, false, 0),
				new AnimationDef("poisoneddagger", 10072780, 0, true, false, 0),
				new AnimationDef("poisoneddagger", 11717785, 0, true, false, 0),
				new AnimationDef("poisoneddagger", 65535, 0, true, false, 0),
				new AnimationDef("poisoneddagger", 3158064, 0, true, false, 0),
				new AnimationDef("2hander", 16737817, 0, true, false, 0), //280
				new AnimationDef("2hander", 15654365, 0, true, false, 0),
				new AnimationDef("2hander", 15658734, 0, true, false, 0),
				new AnimationDef("2hander", 10072780, 0, true, false, 0),
				new AnimationDef("2hander", 11717785, 0, true, false, 0),
				new AnimationDef("2hander", 65535, 0, true, false, 0),
				new AnimationDef("2hander", 3158064, 0, true, false, 0),

				// Unicorn masks
				new AnimationDef("unicornmask", 16777215, 0, true, false, 0), //287 - white unicorn mask
				new AnimationDef("unicornmask", 10878976, 0, true, false, 0), //288 - blood unicorn mask
				new AnimationDef("unicornmask", 1513239, 0, true, false, 0), //289 - black unicorn mask
				new AnimationDef("unicornmask", 16759039, 0, true, false, 0), //290 - pink unicorn mask

				// Wolf masks
				new AnimationDef("wolfmask", 16777215, 0, true, false, 0), //291 - white wolf mask
				new AnimationDef("wolfmask", 10878976, 0, true, false, 0), //292 - blood wolf mask
				new AnimationDef("wolfmask", 1513239, 0, true, false, 0), //293 - black wolf mask
				new AnimationDef("wolfmask", 16759039, 0, true, false, 0), //294 - pink wolf mask

				// Dragon items
				new AnimationDef("dragonfullhelm", 11189164, 0, true, false, 0), //295 - dragon large
				new AnimationDef("dragonbody", 11189164, 0, true, false, 0), //296 - dragon plate
				new AnimationDef("dragonlegs", 11189164, 0, true, false, 0), //297 - dragon legs
				new AnimationDef("fullhelm", 16768685, 0, true, false, 0), //298 -
				new AnimationDef("fdragontop", 16768685, 0, true, false, 0), //299 -
				new AnimationDef("dragonskirt", 16768685, 0, true, false, 0), //300 -
				new AnimationDef("fullhelm", 10027084, 0, true, false, 0), //301 -
				new AnimationDef("platemailtop", 10027084, 0, true, false, 0), //302 -
				new AnimationDef("hatchet", 0, 0, true, false, 0), // 303 -

				// Pumpkin head masks (missing, using wolf instead)
				new AnimationDef("wolf", 2039583, 0, true, false, 0), //304 - orange pumpkin head (missing, was using charColour 0)
				new AnimationDef("wolf", 2039583, 0, true, false, 0), //305 - red pumpkin head (missing, was 1513239)
				new AnimationDef("wolf", 2039583, 0, true, false, 0), //306 - yellow pumpkin head (missing, was 16776960)
				new AnimationDef("wolf", 255, 0, true, false, 0), //307 - blue pumpkin head (missing)
				new AnimationDef("wolf", 11141375, 0, true, false, 0), //308 - purple pumpkin head (missing)
				new AnimationDef("wolf", 65280, 0, true, false, 0), //309 - green pumpkin head (missing)

				// Skill capes and hoods
				new AnimationDef("fishingcape", 0, 0, true, false, 0), //310 - fishing cape
				new AnimationDef("cookingcape", 0, 0, true, false, 0), //311 - cooking cape
				new AnimationDef("hood1", 0, 0, true, false, 0), //312 - fishing hood
				new AnimationDef("warriorcape", 0, 0, true, false, 0), //313 - warrior cape
				new AnimationDef("spottedcape", 7692086, 0, true, false, 0), //314 - spotted cape
				new AnimationDef("attackcape", 0, 0, true, false, 0), //317 - attack cape

				// Easter basket (missing, using peppermintstick instead) and Gaia NPC (missing, using evilhoodie instead)
				new AnimationDef("evilhoodie", 0, 0, true, false, 0), //316 - NPC Gaia (missing)
				new AnimationDef("peppermintstick", 0, 0, true, false, 0), //317 - easter basket (missing)

				// Ironman items
				new AnimationDef("fullhelm", 11189164, 0, true, false, 0), //318 - ironman helm
				new AnimationDef("platemailtop", 11189164, 0, true, false, 0), //319 - ironman plate
				new AnimationDef("platemaillegs", 11189164, 0, true, false, 0), //320 - ironman legs
				new AnimationDef("fullhelm", 16768685, 0, true, false, 0), //321 - ultimate ironman helm
				new AnimationDef("platemailtop", 16768685, 0, true, false, 0), //322 - ultimate ironman plate
				new AnimationDef("platemaillegs", 16768685, 0, true, false, 0), //323 - ultimate ironman legs
				new AnimationDef("fullhelm", 10027084, 0, true, false, 0), //324 - hc ironman helm
				new AnimationDef("platemailtop", 10027084, 0, true, false, 0), //325 - hc ironman plate
				new AnimationDef("platemaillegs", 10027084, 0, true, false, 0), //326 - hc ironman legs

				// Orange feather helms
				new AnimationDef("fullhelmorange", 16737817, 0, true, false, 0), //327 - bronze helm orange
				new AnimationDef("fullhelmorange", 15654365, 0, true, false, 0), //328 - iron helm orange
				new AnimationDef("fullhelmorange", 15658734, 0, true, false, 0), //329 - steel helm orange
				new AnimationDef("fullhelmorange", 3158064, 0, true, false, 0), //330 - black helm orange
				new AnimationDef("fullhelmorange", 10072780, 0, true, false, 0), //331 - mith helm orange
				new AnimationDef("fullhelmorange", 11717785, 0, true, false, 0), //332 - addy helm orange
				new AnimationDef("fullhelmorange", 65535, 0, true, false, 0), //333 - rune helm orange

				// Blue feather helms
				new AnimationDef("fullhelmblue", 16737817, 0, true, false, 0), //334 - bronze helm blue
				new AnimationDef("fullhelmblue", 15654365, 0, true, false, 0), //335 - iron helm blue
				new AnimationDef("fullhelmblue", 15658734, 0, true, false, 0), //336 - steel helm blue
				new AnimationDef("fullhelmblue", 3158064, 0, true, false, 0), //337 - black helm blue
				new AnimationDef("fullhelmblue", 10072780, 0, true, false, 0), //338 - mith helm blue
				new AnimationDef("fullhelmblue", 11717785, 0, true, false, 0), //339 - addy helm blue
				new AnimationDef("fullhelmblue", 65535, 0, true, false, 0), //340 - rune helm blue

				// Purple feather helms
				new AnimationDef("fullhelmpurple", 16737817, 0, true, false, 0), //341 - bronze helm purple
				new AnimationDef("fullhelmpurple", 15654365, 0, true, false, 0), //342 - iron helm purple
				new AnimationDef("fullhelmpurple", 15658734, 0, true, false, 0), //343 - steel helm purple
				new AnimationDef("fullhelmpurple", 3158064, 0, true, false, 0), //344 - black helm purple
				new AnimationDef("fullhelmpurple", 10072780, 0, true, false, 0), //345 - mith helm purple
				new AnimationDef("fullhelmpurple", 11717785, 0, true, false, 0), //346 - addy helm purple
				new AnimationDef("fullhelmpurple", 65535, 0, true, false, 0), //347 - rune helm purple

				// Yellow feather helms
				new AnimationDef("fullhelmyellow", 16737817, 0, true, false, 0), //348 - bronze helm yellow
				new AnimationDef("fullhelmyellow", 15654365, 0, true, false, 0), //349 - iron helm yellow
				new AnimationDef("fullhelmyellow", 15658734, 0, true, false, 0), //350 - steel helm yellow
				new AnimationDef("fullhelmyellow", 3158064, 0, true, false, 0), //351 - black helm yellow
				new AnimationDef("fullhelmyellow", 10072780, 0, true, false, 0), //352 - mith helm yellow
				new AnimationDef("fullhelmyellow", 11717785, 0, true, false, 0), //353 - addy helm yellow
				new AnimationDef("fullhelmyellow", 65535, 0, true, false, 0), //354 - rune helm yellow

				// Green feather helms
				new AnimationDef("fullhelmgreen", 16737817, 0, true, false, 0), //355 - bronze helm green
				new AnimationDef("fullhelmgreen", 15654365, 0, true, false, 0), //356 - iron helm green
				new AnimationDef("fullhelmgreen", 15658734, 0, true, false, 0), //357 - steel helm green
				new AnimationDef("fullhelmgreen", 3158064, 0, true, false, 0), //358 - black helm green
				new AnimationDef("fullhelmgreen", 10072780, 0, true, false, 0), //359 - mith helm green
				new AnimationDef("fullhelmgreen", 11717785, 0, true, false, 0), //360 - addy helm green
				new AnimationDef("fullhelmgreen", 65535, 0, true, false, 0), //361 - rune helm green

				// Grey feather helms
				new AnimationDef("fullhelmgrey", 16737817, 0, true, false, 0), //362 - bronze helm grey
				new AnimationDef("fullhelmgrey", 15654365, 0, true, false, 0), //363 - iron helm grey
				new AnimationDef("fullhelmgrey", 15658734, 0, true, false, 0), //364 - steel helm grey
				new AnimationDef("fullhelmgrey", 3158064, 0, true, false, 0), //365 - black helm grey
				new AnimationDef("fullhelmgrey", 10072780, 0, true, false, 0), //366 - mith helm grey
				new AnimationDef("fullhelmgrey", 11717785, 0, true, false, 0), //367 - addy helm grey
				new AnimationDef("fullhelmgrey", 65535, 0, true, false, 0), //368 - rune helm grey

				// Black feather helms
				new AnimationDef("fullhelmblack", 16737817, 0, true, false, 0), //369 - bronze helm black
				new AnimationDef("fullhelmblack", 15654365, 0, true, false, 0), //370 - iron helm black
				new AnimationDef("fullhelmblack", 15658734, 0, true, false, 0), //371 - steel helm black
				new AnimationDef("fullhelmblack", 3158064, 0, true, false, 0), //372 - black helm black
				new AnimationDef("fullhelmblack", 10072780, 0, true, false, 0), //373 - mith helm black
				new AnimationDef("fullhelmblack", 11717785, 0, true, false, 0), //374 - addy helm black
				new AnimationDef("fullhelmblack", 65535, 0, true, false, 0), //375 - rune helm black

				// White feather helms
				new AnimationDef("fullhelmwhite", 16737817, 0, true, false, 0), //376 - bronze helm white
				new AnimationDef("fullhelmwhite", 15654365, 0, true, false, 0), //377 - iron helm white
				new AnimationDef("fullhelmwhite", 15658734, 0, true, false, 0), //378 - steel helm white
				new AnimationDef("fullhelmwhite", 3158064, 0, true, false, 0), //379 - black helm white
				new AnimationDef("fullhelmwhite", 10072780, 0, true, false, 0), //380 - mith helm white
				new AnimationDef("fullhelmwhite", 11717785, 0, true, false, 0), //381 - addy helm white
				new AnimationDef("fullhelmwhite", 65535, 0, true, false, 0), //382 - rune helm white

				// Greatwood NPC (missing, using evilhoodie instead) and skill capes
				new AnimationDef("evilhoodie", 5453066, 0, true, false, 0), //383 NPC Greatwood tree boss (missing)
				new AnimationDef("smithingcape", 0, 0, true, false, 0), //384 smithing cape
				new AnimationDef("strengthcape", 0, 0, true, false, 0), //385 strength cape
				new AnimationDef("hitscape", 0, 0, true, false, 0), //386 hits cape

				// Fox mask
				new AnimationDef("wolfmask", 16730368, 0, true, false, 0), //387 - fox mask

				// Recolored spears
				new AnimationDef("spear", 0xBB4B12, 0, true, false, 0), //388 - bronze spear
				new AnimationDef("spear", 0xAFA2A2, 0, true, false, 0), //389 - iron spear
				new AnimationDef("spear", 0xAFAFAF, 0, true, false, 0), //390 - steel spear
				new AnimationDef("spear", 0x708396, 0, true, false, 0), //391 - mith spear
				new AnimationDef("spear", 0x839670, 0, true, false, 0), //392 - addy spear
				new AnimationDef("spear", 48059, 0, true, false, 0) //393 - rune spear
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
					if (animationNumber == 3705) {
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
		/// World Reference used to find the appropriate avatar directory for this world.
		private final World world;

		/// Sole Constructor
		AvatarTransaction(World world, int playerID, PlayerAppearance appearance, int[] wornItems) throws IOException {
			this.world = world;
			this.appearance = appearance;
			this.wornItems = wornItems;
			this.pixels = new int[Constants.AVATAR_WIDTH * Constants.AVATAR_HEIGHT];

			drawPlayer(0, 0, Constants.AVATAR_WIDTH - 1, Constants.AVATAR_HEIGHT - 12, 0);

			BufferedImage img = new BufferedImage(Constants.AVATAR_WIDTH, Constants.AVATAR_HEIGHT, BufferedImage.TYPE_INT_ARGB);

			for (int i = 0; i < pixels.length; ++i) {
				if (pixels[i] != 0) {
					img.setRGB(i % Constants.AVATAR_WIDTH, i / Constants.AVATAR_WIDTH, pixels[i] | 0xFF000000);
				}
			}
			ImageIO.write(img, "png", new File(getWorld().getServer().getConfig().AVATAR_DIR + playerID + ".png"));
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
			int j4 = j * Constants.AVATAR_WIDTH;
			i3 += i << 16;
			if (j < 0) {
				int l4 = 0 - j;
				l -= l4;
				j = 0;
				j4 += l4 * Constants.AVATAR_WIDTH;
				l2 += k3 * l4;
				i3 += l3 * l4;
			}
			if (j + l >= Constants.AVATAR_HEIGHT) {
				l -= ((j + l) - Constants.AVATAR_HEIGHT) + 1;
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
		private void spritePlotTransparent(int[] ai, int[] ai1, int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int overlay, int k2, int l2, int i3) {
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
				if (k5 + l5 >= Constants.AVATAR_WIDTH) {
					int j6 = (k5 + l5) - Constants.AVATAR_WIDTH;
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
				l += Constants.AVATAR_WIDTH;
				k2 += l2;
			}
		}

		/// A helper function for rendering
		private void spritePlotTransparent(int[] ai, int[] ai1, int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int overlay, int k2, int l2, int i3, int j3) {
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
				if (k6 + l6 >= Constants.AVATAR_WIDTH) {
					int j7 = (k6 + l6) - Constants.AVATAR_WIDTH;
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
				l += Constants.AVATAR_WIDTH;
				l2 += i3;
			}
		}

		/// A helper function for rendering
		private void drawPlayer(int x, int y, int scaleX, int scaleY, int unknown) {
			for (int k2 = 0; k2 < 12; k2++) {
				int l2 = Constants.npcAnimationArray[0][k2];
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
					int skinColour = Constants.characterSkinColours[appearance.getSkinColour()];
					if (colour == 1)
						colour = Constants.characterHairColours[appearance.getHairColour()];
					else if (colour == 2)
						colour = Constants.characterTopBottomColours[appearance.getTopColour()];
					else if (colour == 3)
						colour = Constants.characterTopBottomColours[appearance.getTrouserColour()];
					spriteClip4(x + k4, y + i5, l5, scaleY, k5, colour, skinColour, unknown, false);
				}
			}
		}

		public World getWorld() {
			return world;
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
