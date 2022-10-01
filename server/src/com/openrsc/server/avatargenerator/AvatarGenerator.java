package com.openrsc.server.avatargenerator;

import com.openrsc.server.avatargenerator.AvatarFormat.*;
import com.openrsc.server.constants.Constants;
import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public final class AvatarGenerator {

	private final static Logger LOGGER = LogManager.getLogger();

	/// A map of unpacked sprites
	private final static HashMap<String, Map<String, Entry>> spriteTree = new HashMap<>();
	private final static Sprite[] sprites = new Sprite[4000];
	/// The animations
	private final static ArrayList<AnimationDef> animations = new ArrayList<>();

	private final World world;

	public AvatarGenerator(final World world) {
		this.world = world;
	}

	static {
		animations.add(new AnimationDef("head1", "player", 1, 13, true, false, 0));
		animations.add(new AnimationDef("body1", "player", 2, 6, true, false, 0));
		animations.add(new AnimationDef("legs1", "player", 3, 15, true, false, 0));
		animations.add(new AnimationDef("fhead1", "player", 1, 13, true, false, 0));
		animations.add(new AnimationDef("fbody1", "player", 2, 10, true, false, 0));
		animations.add(new AnimationDef("head2", "player", 1, 13, true, false, 0));
		animations.add(new AnimationDef("head3", "player", 1, 13, true, false, 0)); // allow shemales.
		animations.add(new AnimationDef("head4", "player", 1, 13, true, false, 0));
		animations.add(new AnimationDef("chefshat", "equipment", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("apron", "equipment", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("apron", "equipment", 9789488, 0, true, false, 0));
		animations.add(new AnimationDef("boots", "equipment", 5592405, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", "equipment", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", "equipment", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", "equipment", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", "equipment", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", "equipment", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", "equipment", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", "equipment", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", "equipment", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", "equipment", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", "equipment", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", "equipment", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", "equipment", 10083839, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", "equipment", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", "equipment", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", "equipment", 4210752, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", "equipment", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", "equipment", 10083839, 0, true, false, 0));
		animations.add(new AnimationDef("leatherarmour", "equipment", 0, 0, true, false, 0));
		animations.add(new AnimationDef("leathergloves", "equipment", 0, 0, true, false, 0));
		animations.add(new AnimationDef("sword", "equipment", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("sword", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("sword", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("sword", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("sword", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("sword", "equipment", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("sword", "equipment", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", "equipment", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", "equipment", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", "equipment", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("apron", "equipment", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("cape", "equipment", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("cape", "equipment", 2434341, 0, true, false, 0));
		animations.add(new AnimationDef("cape", "equipment", 4210926, 0, true, false, 0));
		animations.add(new AnimationDef("cape", "equipment", 4246592, 0, true, false, 0));
		animations.add(new AnimationDef("cape", "equipment", 15658560, 0, true, false, 0));
		animations.add(new AnimationDef("cape", "equipment", 15636736, 0, true, false, 0));
		animations.add(new AnimationDef("cape", "equipment", 11141341, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", "equipment", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", "equipment", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", "equipment", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 255, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", "equipment", 255, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", "equipment", 4210752, 0, true, false, 0));
		animations.add(new AnimationDef("necklace", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("necklace", "equipment", 16763980, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 255, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 4210752, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 10510400, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 15609904, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 10510400, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 4210752, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 16036851, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 15609904, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 8400921, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 7824998, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 7829367, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 2245205, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 4347170, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 26214, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", "equipment", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", "equipment", 56797, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", "equipment", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", "equipment", 16750896, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", "equipment", 11363121, 0, true, false, 0));
		animations.add(new AnimationDef("crossbow", "equipment", 0, 0, false, false, 0));
		animations.add(new AnimationDef("longbow", "equipment", 0, 0, false, false, 0));
		animations.add(new AnimationDef("battleaxe", "equipment", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", "equipment", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", "equipment", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("mace", "equipment", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("mace", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("mace", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("mace", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("mace", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("mace", "equipment", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("mace", "equipment", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("staff", "equipment", 0, 0, true, false, 0));
		animations.add(new AnimationDef("rat", "npc", 4805259, 0, true, false, 0));
		animations.add(new AnimationDef("demon", "npc", 16384000, 0, true, false, 0));
		animations.add(new AnimationDef("spider", "npc", 13408576, 0, true, false, 0));
		animations.add(new AnimationDef("spider", "npc", 16728144, 0, true, false, 0));
		animations.add(new AnimationDef("camel", "npc", 0, 0, true, false, 0));
		animations.add(new AnimationDef("cow", "npc", 0, 0, true, false, 0));
		animations.add(new AnimationDef("sheep", "npc", 0, 0, false, false, 0));
		animations.add(new AnimationDef("unicorn", "npc", 0, 0, true, false, 0));
		animations.add(new AnimationDef("bear", "npc", 0, 0, true, false, 0));
		animations.add(new AnimationDef("chicken", "npc", 0, 0, true, false, 0));
		animations.add(new AnimationDef("skeleton", "npc", 0, 0, true, false, 0));
		animations.add(new AnimationDef("skelweap", "npc", 0, 0, true, true, 0));
		animations.add(new AnimationDef("zombie", "npc", 0, 0, true, false, 0));
		animations.add(new AnimationDef("zombweap", "npc", 0, 0, true, true, 0));
		animations.add(new AnimationDef("ghost", "npc", 0, 0, true, false, 0));
		animations.add(new AnimationDef("bat", "npc", 0, 0, true, false, 0));
		animations.add(new AnimationDef("goblin", "npc", 8969727, 0, true, false, 0));
		animations.add(new AnimationDef("goblin", "npc", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("goblin", "npc", 47872, 0, true, false, 0));
		animations.add(new AnimationDef("gobweap", "npc", 65535, 0, true, true, 0));
		animations.add(new AnimationDef("scorpion", "npc", 0, 0, true, false, 0));
		animations.add(new AnimationDef("dragon", "npc", 65280, 0, true, false, 0));
		animations.add(new AnimationDef("dragon", "npc", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("dragon", "npc", 21981, 0, true, false, 0));
		animations.add(new AnimationDef("wolf", "npc", 0, 0, true, false, 0));
		animations.add(new AnimationDef("wolf", "npc", 10066329, 0, true, false, 0));
		animations.add(new AnimationDef("partyhat", "equipment", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("partyhat", "equipment", 16776960, 0, true, false, 0));
		animations.add(new AnimationDef("partyhat", "equipment", 255, 0, true, false, 0));
		animations.add(new AnimationDef("partyhat", "equipment", 65280, 0, true, false, 0));
		animations.add(new AnimationDef("partyhat", "equipment", 16711935, 0, true, false, 0));
		animations.add(new AnimationDef("partyhat", "equipment", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("leathergloves", "equipment", 11202303, 0, true, false, 0));
		animations.add(new AnimationDef("chicken", "npc", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", "equipment", 10083839, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 1118481, 0, true, false, 0));
		animations.add(new AnimationDef("wolf", "npc", 9789488, 0, true, false, 0));
		animations.add(new AnimationDef("spider", "npc", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", "equipment", 16711748, 0, true, false, 0));
		animations.add(new AnimationDef("sword", "equipment", 16711748, 0, true, false, 0));
		animations.add(new AnimationDef("eyepatch", "equipment", 0, 0, true, true, 0));
		animations.add(new AnimationDef("demon", "npc", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("dragon", "npc", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("spider", "npc", 14535680, 0, true, false, 0));
		animations.add(new AnimationDef("wolf", "npc", 2236962, 0, true, false, 0));
		animations.add(new AnimationDef("unicorn", "npc", 2236962, 0, true, false, 0));
		animations.add(new AnimationDef("demon", "npc", 6291456, 0, true, false, 0));
		animations.add(new AnimationDef("spider", "npc", 2236962, 0, true, false, 0));
		animations.add(new AnimationDef("necklace", "equipment", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("rat", "npc", 11184810, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", "equipment", 11250603, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", "equipment", 11250603, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", "equipment", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("legs1", "player", 9785408, 0, true, false, 0));
		animations.add(new AnimationDef("gasmask", "equipment", 0, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", "equipment", 16711748, 0, true, false, 0));
		animations.add(new AnimationDef("spider", "npc", 3852326, 0, true, false, 0));
		animations.add(new AnimationDef("spear", "equipment", 0, 0, true, false, 0));
		animations.add(new AnimationDef("halloweenmask", "equipment", 52224, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 1052688, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 1052688, 0, true, false, 0));
		animations.add(new AnimationDef("halloweenmask", "equipment", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("halloweenmask", "equipment", 255, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 16755370, 15, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 11206570, 15, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 11184895, 15, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 16777164, 15, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 13434879, 15, true, false, 0));
		animations.add(new AnimationDef("wizardshat", "equipment", 16755370, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", "equipment", 11206570, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", "equipment", 11184895, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", "equipment", 16777164, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", "equipment", 13434879, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 16755370, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 11206570, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 11184895, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 16777164, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 13434879, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 3978097, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 3978097, 0, true, false, 0));
		animations.add(new AnimationDef("boots", "equipment", 16755370, 0, true, false, 0));
		animations.add(new AnimationDef("boots", "equipment", 11206570, 0, true, false, 0));
		animations.add(new AnimationDef("boots", "equipment", 11184895, 0, true, false, 0));
		animations.add(new AnimationDef("boots", "equipment", 16777164, 0, true, false, 0));
		animations.add(new AnimationDef("boots", "equipment", 13434879, 0, true, false, 0));
		animations.add(new AnimationDef("santahat", "equipment", 0, 0, true, false, 0));
		animations.add(new AnimationDef("ibanstaff", "equipment", 0, 0, true, false, 0));
		animations.add(new AnimationDef("souless", "npc", 0, 0, true, false, 0));
		animations.add(new AnimationDef("boots", "equipment", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("legs1", "player", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 8421376, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 8421376, 0, true, false, 0));
		animations.add(new AnimationDef("cape", "equipment", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("wolf", "npc", 13420580, 0, true, false, 0));
		animations.add(new AnimationDef("bunnyears", "equipment", 0, 0, true, false, 0));
		animations.add(new AnimationDef("saradominstaff", "equipment", 0, 0, true, false, 0));
		animations.add(new AnimationDef("spear", "equipment", 56797, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", "equipment", 1392384, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 1392384, 0, true, false, 0));
		animations.add(new AnimationDef("wolf", "npc", 5585408, 0, true, false, 0));
		animations.add(new AnimationDef("chicken", "npc", 6893315, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", "equipment", 13500416, 0, true, false, 0));
		animations.add(new AnimationDef("cape", "equipment", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("boots", "equipment", 1118481, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", "equipment", 1118481, 0, true, false, 0));
		animations.add(new AnimationDef("scythe", "equipment", 0, 0, true, false, 0));
		/*
		  Add custom animation below.
		 */

		// Hatchets
		animations.add(new AnimationDef("hatchet", "equipment", 16737817, 0, true, false, 0)); //230 - bronze hatchet
		animations.add(new AnimationDef("hatchet", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("hatchet", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("hatchet", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("hatchet", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("hatchet", "equipment", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("hatchet", "equipment", 3158064, 0, true, false, 0));

		// Kite shields
		animations.add(new AnimationDef("kiteshield", "equipment", 0xBB4B12, 0, true, false, 0)); //237 - bronze kite
		animations.add(new AnimationDef("kiteshield", "equipment", 0xAFA2A2, 0, true, false, 0));
		animations.add(new AnimationDef("kiteshield", "equipment", 0xAFAFAF, 0, true, false, 0));
		animations.add(new AnimationDef("kiteshield", "equipment", 0x708396, 0, true, false, 0));
		animations.add(new AnimationDef("kiteshield", "equipment", 0x839670, 0, true, false, 0));
		animations.add(new AnimationDef("kiteshield", "equipment", 48059, 0, true, false, 0));
		animations.add(new AnimationDef("kiteshield", "equipment", 0x232323, 0, true, false, 0));

		// Dragon items
		animations.add(new AnimationDef("dragonshield", "equipment", 0, 0, true, false, 0)); //244 - dragon square
		animations.add(new AnimationDef("dragonmedhelm", "equipment", 0, 0, true, false, 0)); //245 - dragon med

		// Plate skirts
		animations.add(new AnimationDef("armorskirt", "equipment", 0xBB4B12, 0, true, false, 0)); //246 - bronze plate skirt
		animations.add(new AnimationDef("armorskirt", "equipment", 0xAFA2A2, 0, true, false, 0));
		animations.add(new AnimationDef("armorskirt", "equipment", 0xAFAFAF, 0, true, false, 0));
		animations.add(new AnimationDef("armorskirt", "equipment", 0x708396, 0, true, false, 0));
		animations.add(new AnimationDef("armorskirt", "equipment", 0x839670, 0, true, false, 0));
		animations.add(new AnimationDef("armorskirt", "equipment", 48059, 0, true, false, 0));
		animations.add(new AnimationDef("armorskirt", "equipment", 0x232323, 0, true, false, 0));

		// Longbows
		animations.add(new AnimationDef("longbow", "equipment", 8537122, 0, true, false, 0)); //253 - wooden longbow
		animations.add(new AnimationDef("longbow", "equipment", 11300689, 0, true, false, 0));
		animations.add(new AnimationDef("longbow", "equipment", 8941897, 0, true, false, 0));
		animations.add(new AnimationDef("longbow", "equipment", 9132849, 0, true, false, 0));
		animations.add(new AnimationDef("longbow", "equipment", 10310656, 0, true, false, 0));
		animations.add(new AnimationDef("longbow", "equipment", 37281, 0, true, false, 0));

		// Short swords
		animations.add(new AnimationDef("shortsword", "equipment", 16737817, 0, true, false, 0)); //259 - bronze short sword
		animations.add(new AnimationDef("shortsword", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("shortsword", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("shortsword", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("shortsword", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("shortsword", "equipment", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("shortsword", "equipment", 3158064, 0, true, false, 0));

		// Daggers
		animations.add(new AnimationDef("dagger", "equipment", 16737817, 0, true, false, 0)); //266 - bronze dagger
		animations.add(new AnimationDef("dagger", "equipment", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("dagger", "equipment", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("dagger", "equipment", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("dagger", "equipment", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("dagger", "equipment", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("dagger", "equipment", 3158064, 0, true, false, 0));

		// Poison daggers
		animations.add(new AnimationDef("poisoneddagger", "equipment", 16737817, 0, true, false, 0)); //273 - bronze p dagger
		animations.add(new AnimationDef("poisoneddagger", "equipment", 15654365, 0, true, false, 0)); //274 - iron p dagger
		animations.add(new AnimationDef("poisoneddagger", "equipment", 15658734, 0, true, false, 0)); //275 - steel p dagger
		animations.add(new AnimationDef("poisoneddagger", "equipment", 10072780, 0, true, false, 0)); //276 - black p dagger
		animations.add(new AnimationDef("poisoneddagger", "equipment", 11717785, 0, true, false, 0)); //277 - mith p dagger
		animations.add(new AnimationDef("poisoneddagger", "equipment", 65535, 0, true, false, 0)); //278 - addy p dagger
		animations.add(new AnimationDef("poisoneddagger", "equipment", 3158064, 0, true, false, 0)); //279 - rune p dagger

		// 2-handed swords
		animations.add(new AnimationDef("2hander", "equipment", 16737817, 0, true, false, 0)); //280 - bronze 2h
		animations.add(new AnimationDef("2hander", "equipment", 15654365, 0, true, false, 0)); //281 - iron 2h
		animations.add(new AnimationDef("2hander", "equipment", 15658734, 0, true, false, 0)); //282 - steel 2h
		animations.add(new AnimationDef("2hander", "equipment", 10072780, 0, true, false, 0)); //283 - black 2h
		animations.add(new AnimationDef("2hander", "equipment", 11717785, 0, true, false, 0)); //284 - mith 2h
		animations.add(new AnimationDef("2hander", "equipment", 65535, 0, true, false, 0)); //285 - addy 2h
		animations.add(new AnimationDef("2hander", "equipment", 3158064, 0, true, false, 0)); //286 - rune 2h

		// Unicorn masks
		animations.add(new AnimationDef("unicornmask", "equipment", 16777215, 16777215, 0, true, false, 0)); //287 - white unicorn mask
		animations.add(new AnimationDef("unicornmask", "equipment", 10878976, 1513239, 0, true, false, 0)); //288 - blood unicorn mask
		animations.add(new AnimationDef("unicornmask", "equipment", 1513239, 10878976, 0, true, false, 0)); //289 - black unicorn mask
		animations.add(new AnimationDef("unicornmask", "equipment", 16759039, 16777215, 0, true, false, 0)); //290 - pink unicorn mask

		// Wolf masks
		animations.add(new AnimationDef("wolfmask", "equipment", 16777215, 16777215, 0, true, false, 0)); //291 - white wolf mask
		animations.add(new AnimationDef("wolfmask", "equipment", 10878976, 1513239, 0, true, false, 0)); //292 - blood wolf mask
		animations.add(new AnimationDef("wolfmask", "equipment", 1513239, 10878976, 0, true, false, 0)); //293 - black wolf mask
		animations.add(new AnimationDef("wolfmask", "equipment", 16759039, 16777215, 0, true, false, 0)); //294 - pink wolf mask

		// Dragon items
		animations.add(new AnimationDef("dragonfullhelm", "equipment", 11189164, 0, true, false, 0)); //295 - dragon large
		animations.add(new AnimationDef("dragonbody", "equipment", 11189164, 0, true, false, 0)); //296 - dragon plate
		animations.add(new AnimationDef("dragonlegs", "equipment", 11189164, 0, true, false, 0)); //297 - dragon legs
		animations.add(new AnimationDef("fullhelm", "equipment", 16768685, 0, true, false, 0)); //298 -
		animations.add(new AnimationDef("fdragontop", "equipment", 16768685, 0, true, false, 0)); //299 -
		animations.add(new AnimationDef("dragonskirt", "equipment", 16768685, 0, true, false, 0)); //300 -
		animations.add(new AnimationDef("fullhelm", "equipment", 10027084, 0, true, false, 0)); //301 -
		animations.add(new AnimationDef("platemailtop", "equipment", 10027084, 0, true, false, 0)); //302 -
		animations.add(new AnimationDef("hatchet", "equipment", 0, 0, true, false, 0)); // 303 -

		// Pumpkin head masks (missing, using wolf instead)
		animations.add(new AnimationDef("wolf", "npc", 2039583, 0, true, false, 0)); //304 - orange pumpkin head (missing, was using charColour 0)
		animations.add(new AnimationDef("wolf", "npc", 2039583, 0, true, false, 0)); //305 - red pumpkin head (missing, was 1513239)
		animations.add(new AnimationDef("wolf", "npc", 2039583, 0, true, false, 0)); //306 - yellow pumpkin head (missing, was 16776960)
		animations.add(new AnimationDef("wolf", "npc", 255, 0, true, false, 0)); //307 - blue pumpkin head (missing)
		animations.add(new AnimationDef("wolf", "npc", 11141375, 0, true, false, 0)); //308 - purple pumpkin head (missing)
		animations.add(new AnimationDef("wolf", "npc", 65280, 0, true, false, 0)); //309 - green pumpkin head (missing)

		// Skill capes and hoods
		animations.add(new AnimationDef("fishingcape", "equipment", 0, 0, true, false, 0)); //310 - fishing cape
		animations.add(new AnimationDef("cookingcape", "equipment", 0, 0, true, false, 0)); //311 - cooking cape
		animations.add(new AnimationDef("hood1", "equipment", 0, 0, true, false, 0)); //312 - fishing hood
		animations.add(new AnimationDef("warriorcape", "equipment", 0, 0, true, false, 0)); //313 - warrior cape
		animations.add(new AnimationDef("spottedcape", "equipment", 7692086, 0, true, false, 0)); //314 - spotted cape
		animations.add(new AnimationDef("attackcape", "equipment", 0, 0, true, false, 0)); //315 - attack cape

		// Easter basket (missing, using peppermintstick instead) and Gaia NPC (missing, using evilhoodie instead)
		animations.add(new AnimationDef("evilhoodie", "equipment", 0, 0, true, false, 0)); //316 - NPC Gaia (missing)
		animations.add(new AnimationDef("peppermintstick", "equipment", 0, 0, true, false, 0)); //317 - easter basket (missing)

		// Ironman items
		animations.add(new AnimationDef("fullhelm", "equipment", 11189164, 0, true, false, 0)); //318 - ironman helm
		animations.add(new AnimationDef("platemailtop", "equipment", 11189164, 0, true, false, 0)); //319 - ironman plate
		animations.add(new AnimationDef("platemaillegs", "equipment", 11189164, 0, true, false, 0)); //320 - ironman legs
		animations.add(new AnimationDef("fullhelm", "equipment", 16768685, 0, true, false, 0)); //321 - ultimate ironman helm
		animations.add(new AnimationDef("platemailtop", "equipment", 16768685, 0, true, false, 0)); //322 - ultimate ironman plate
		animations.add(new AnimationDef("platemaillegs", "equipment", 16768685, 0, true, false, 0)); //323 - ultimate ironman legs
		animations.add(new AnimationDef("fullhelm", "equipment", 10027084, 0, true, false, 0)); //324 - hc ironman helm
		animations.add(new AnimationDef("platemailtop", "equipment", 10027084, 0, true, false, 0)); //325 - hc ironman plate
		animations.add(new AnimationDef("platemaillegs", "equipment", 10027084, 0, true, false, 0)); //326 - hc ironman legs

		// Orange feather helms
		animations.add(new AnimationDef("fullhelmorange", "equipment", 16737817, 0, true, false, 0)); //327 - bronze helm orange
		animations.add(new AnimationDef("fullhelmorange", "equipment", 15654365, 0, true, false, 0)); //328 - iron helm orange
		animations.add(new AnimationDef("fullhelmorange", "equipment", 15658734, 0, true, false, 0)); //329 - steel helm orange
		animations.add(new AnimationDef("fullhelmorange", "equipment", 3158064, 0, true, false, 0)); //330 - black helm orange
		animations.add(new AnimationDef("fullhelmorange", "equipment", 10072780, 0, true, false, 0)); //331 - mith helm orange
		animations.add(new AnimationDef("fullhelmorange", "equipment", 11717785, 0, true, false, 0)); //332 - addy helm orange
		animations.add(new AnimationDef("fullhelmorange", "equipment", 65535, 0, true, false, 0)); //333 - rune helm orange

		// Blue feather helms
		animations.add(new AnimationDef("fullhelmblue", "equipment", 16737817, 0, true, false, 0)); //334 - bronze helm blue
		animations.add(new AnimationDef("fullhelmblue", "equipment", 15654365, 0, true, false, 0)); //335 - iron helm blue
		animations.add(new AnimationDef("fullhelmblue", "equipment", 15658734, 0, true, false, 0)); //336 - steel helm blue
		animations.add(new AnimationDef("fullhelmblue", "equipment", 3158064, 0, true, false, 0)); //337 - black helm blue
		animations.add(new AnimationDef("fullhelmblue", "equipment", 10072780, 0, true, false, 0)); //338 - mith helm blue
		animations.add(new AnimationDef("fullhelmblue", "equipment", 11717785, 0, true, false, 0)); //339 - addy helm blue
		animations.add(new AnimationDef("fullhelmblue", "equipment", 65535, 0, true, false, 0)); //340 - rune helm blue

		// Purple feather helms
		animations.add(new AnimationDef("fullhelmpurple", "equipment", 16737817, 0, true, false, 0)); //341 - bronze helm purple
		animations.add(new AnimationDef("fullhelmpurple", "equipment", 15654365, 0, true, false, 0)); //342 - iron helm purple
		animations.add(new AnimationDef("fullhelmpurple", "equipment", 15658734, 0, true, false, 0)); //343 - steel helm purple
		animations.add(new AnimationDef("fullhelmpurple", "equipment", 3158064, 0, true, false, 0)); //344 - black helm purple
		animations.add(new AnimationDef("fullhelmpurple", "equipment", 10072780, 0, true, false, 0)); //345 - mith helm purple
		animations.add(new AnimationDef("fullhelmpurple", "equipment", 11717785, 0, true, false, 0)); //346 - addy helm purple
		animations.add(new AnimationDef("fullhelmpurple", "equipment", 65535, 0, true, false, 0)); //347 - rune helm purple

		// Yellow feather helms
		animations.add(new AnimationDef("fullhelmyellow", "equipment", 16737817, 0, true, false, 0)); //348 - bronze helm yellow
		animations.add(new AnimationDef("fullhelmyellow", "equipment", 15654365, 0, true, false, 0)); //349 - iron helm yellow
		animations.add(new AnimationDef("fullhelmyellow", "equipment", 15658734, 0, true, false, 0)); //350 - steel helm yellow
		animations.add(new AnimationDef("fullhelmyellow", "equipment", 3158064, 0, true, false, 0)); //351 - black helm yellow
		animations.add(new AnimationDef("fullhelmyellow", "equipment", 10072780, 0, true, false, 0)); //352 - mith helm yellow
		animations.add(new AnimationDef("fullhelmyellow", "equipment", 11717785, 0, true, false, 0)); //353 - addy helm yellow
		animations.add(new AnimationDef("fullhelmyellow", "equipment", 65535, 0, true, false, 0)); //354 - rune helm yellow

		// Green feather helms
		animations.add(new AnimationDef("fullhelmgreen", "equipment", 16737817, 0, true, false, 0)); //355 - bronze helm green
		animations.add(new AnimationDef("fullhelmgreen", "equipment", 15654365, 0, true, false, 0)); //356 - iron helm green
		animations.add(new AnimationDef("fullhelmgreen", "equipment", 15658734, 0, true, false, 0)); //357 - steel helm green
		animations.add(new AnimationDef("fullhelmgreen", "equipment", 3158064, 0, true, false, 0)); //358 - black helm green
		animations.add(new AnimationDef("fullhelmgreen", "equipment", 10072780, 0, true, false, 0)); //359 - mith helm green
		animations.add(new AnimationDef("fullhelmgreen", "equipment", 11717785, 0, true, false, 0)); //360 - addy helm green
		animations.add(new AnimationDef("fullhelmgreen", "equipment", 65535, 0, true, false, 0)); //361 - rune helm green

		// Grey feather helms
		animations.add(new AnimationDef("fullhelmgrey", "equipment", 16737817, 0, true, false, 0)); //362 - bronze helm grey
		animations.add(new AnimationDef("fullhelmgrey", "equipment", 15654365, 0, true, false, 0)); //363 - iron helm grey
		animations.add(new AnimationDef("fullhelmgrey", "equipment", 15658734, 0, true, false, 0)); //364 - steel helm grey
		animations.add(new AnimationDef("fullhelmgrey", "equipment", 3158064, 0, true, false, 0)); //365 - black helm grey
		animations.add(new AnimationDef("fullhelmgrey", "equipment", 10072780, 0, true, false, 0)); //366 - mith helm grey
		animations.add(new AnimationDef("fullhelmgrey", "equipment", 11717785, 0, true, false, 0)); //367 - addy helm grey
		animations.add(new AnimationDef("fullhelmgrey", "equipment", 65535, 0, true, false, 0)); //368 - rune helm grey

		// Black feather helms
		animations.add(new AnimationDef("fullhelmblack", "equipment", 16737817, 0, true, false, 0)); //369 - bronze helm black
		animations.add(new AnimationDef("fullhelmblack", "equipment", 15654365, 0, true, false, 0)); //370 - iron helm black
		animations.add(new AnimationDef("fullhelmblack", "equipment", 15658734, 0, true, false, 0)); //371 - steel helm black
		animations.add(new AnimationDef("fullhelmblack", "equipment", 3158064, 0, true, false, 0)); //372 - black helm black
		animations.add(new AnimationDef("fullhelmblack", "equipment", 10072780, 0, true, false, 0)); //373 - mith helm black
		animations.add(new AnimationDef("fullhelmblack", "equipment", 11717785, 0, true, false, 0)); //374 - addy helm black
		animations.add(new AnimationDef("fullhelmblack", "equipment", 65535, 0, true, false, 0)); //375 - rune helm black

		// White feather helms
		animations.add(new AnimationDef("fullhelmwhite", "equipment", 16737817, 0, true, false, 0)); //376 - bronze helm white
		animations.add(new AnimationDef("fullhelmwhite", "equipment", 15654365, 0, true, false, 0)); //377 - iron helm white
		animations.add(new AnimationDef("fullhelmwhite", "equipment", 15658734, 0, true, false, 0)); //378 - steel helm white
		animations.add(new AnimationDef("fullhelmwhite", "equipment", 3158064, 0, true, false, 0)); //379 - black helm white
		animations.add(new AnimationDef("fullhelmwhite", "equipment", 10072780, 0, true, false, 0)); //380 - mith helm white
		animations.add(new AnimationDef("fullhelmwhite", "equipment", 11717785, 0, true, false, 0)); //381 - addy helm white
		animations.add(new AnimationDef("fullhelmwhite", "equipment", 65535, 0, true, false, 0)); //382 - rune helm white

		// Greatwood NPC (missing, using evilhoodie instead) and skill capes
		animations.add(new AnimationDef("evilhoodie", "equipment", 5453066, 0, true, false, 0)); //383 NPC Greatwood tree boss (missing)
		animations.add(new AnimationDef("smithingcape", "equipment", 0, 0, true, false, 0)); //384 smithing cape
		animations.add(new AnimationDef("strengthcape", "equipment", 0, 0, true, false, 0)); //385 strength cape
		animations.add(new AnimationDef("hitscape", "equipment", 0, 0, true, false, 0)); //386 hits cape

		// Fox mask
		animations.add(new AnimationDef("wolfmask", "equipment", 16730368, 16446686, 0, true, false, 0)); //387 - fox mask

		// Spears
		animations.add(new AnimationDef("spear", "equipment", 0xBB4B12, 0, true, false, 0)); //388 - bronze spear
		animations.add(new AnimationDef("spear", "equipment", 0xAFA2A2, 0, true, false, 0)); //389 - iron spear
		animations.add(new AnimationDef("spear", "equipment", 0xAFAFAF, 0, true, false, 0)); //390 - steel spear
		animations.add(new AnimationDef("spear", "equipment", 0x708396, 0, true, false, 0)); //391 - mith spear
		animations.add(new AnimationDef("spear", "equipment", 0x839670, 0, true, false, 0)); //392 - addy spear
		animations.add(new AnimationDef("spear", "equipment", 48059, 0, true, false, 0)); //393 - rune spear

		// Xmas
		animations.add(new AnimationDef("xmasapron", "equipment",0, 0, true, false, 0)); //394
		animations.add(new AnimationDef("xmascape", "equipment",0, 0, true, false, 0)); //395
		animations.add(new AnimationDef("santabody", "equipment",0, 0, true, false, 0)); //396
		animations.add(new AnimationDef("santalegs", "equipment",0, 0, true, false, 0)); //397
		animations.add(new AnimationDef("santahat2", "equipment",0, 0, true, false, 0)); //398
		animations.add(new AnimationDef("santamittens", "equipment",0, 0, true, false, 0)); //399
		animations.add(new AnimationDef("satansgloveswht", "equipment",0, 0, true, false, 0)); //400
		animations.add(new AnimationDef("greensantahat", "equipment",0, 0, true, false, 0)); //401
		animations.add(new AnimationDef("antlers", "equipment",0, 0, true, false, 0)); //402

		//Dragon 2H
		animations.add(new AnimationDef("2hander", "equipment", 16711748, 0, true, false, 0)); //403 d2h

		//Dragon Scale Mail
		animations.add(new AnimationDef("dragonscalemail", "equipment",0, 0, true, false, 0));//404

		//Updated Necklaces and Amulets
		//Sapphire
		animations.add(new AnimationDef("necklace2", "equipment", 16763980, 19711, 0, true, false, 0)); //405
		animations.add(new AnimationDef("amulet", "equipment", 16763980, 19711, 0, true, false, 0)); //406
		//Emerald
		animations.add(new AnimationDef("necklace2", "equipment", 16763980, 3394611, 0, true, false, 0)); //407
		animations.add(new AnimationDef("amulet", "equipment", 16763980, 3394611, 0, true, false, 0)); //408
		//Ruby
		animations.add(new AnimationDef("necklace2", "equipment", 16763980, 16724736, 0, true, false, 0)); //409
		animations.add(new AnimationDef("amulet", "equipment", 16763980, 16724736, 0, true, false, 0)); //410
		//Diamond
		animations.add(new AnimationDef("necklace2", "equipment", 16763980, 16184564, 0, true, false, 0)); //411
		animations.add(new AnimationDef("amulet", "equipment", 16763980, 16184564, 0, true, false, 0)); //412
		//Dragonstone
		animations.add(new AnimationDef("necklace2", "equipment", 16763980, 12255487, 0, true, false, 0)); //413
		animations.add(new AnimationDef("amulet", "equipment", 16763980, 12255487, 0, true, false, 0)); //414
		//Annas, Accuracy, Ghostspeak
		animations.add(new AnimationDef("amulet2", "equipment", 0, 0, 0, true, false, 0)); //415
		//Beads of the dead
		animations.add(new AnimationDef("amulet2", "equipment", 16737817, 0, 0, true, false, 0)); //416
		//Lucien / Armadyl
		animations.add(new AnimationDef("lucians", "equipment", 3158064, 12750123, 0, true, false, 0)); //417
		animations.add(new AnimationDef("lucians", "equipment", 0, 12750123, 0, true, false, 0)); //418
		//Glarial
		animations.add(new AnimationDef("necklace2", "equipment", 0, 3394611, 0, true, false, 0)); //419
		//Symbols
		animations.add(new AnimationDef("sarasymbol", "equipment", 0, 0, 0, true, false, 0)); //420
		animations.add(new AnimationDef("zammysymbol", "equipment", 0, 0, 0, true, false, 0)); //421

		//Elemental Staves
		//air
		animations.add(new AnimationDef("elementalstaff", "equipment", 0x0AE5E4, 0, 0, true, false, 0)); //422
		//water
		animations.add(new AnimationDef("elementalstaff", "equipment", 0x0401DC, 0, 0, true, false, 0)); //423
		//earth
		animations.add(new AnimationDef("elementalstaff", "equipment", 0x642E01, 0, 0, true, false, 0)); //424
		//fire
		animations.add(new AnimationDef("elementalstaff", "equipment", 0xD40203, 0, 0, true, false, 0)); //425

		//New Leather Items
		//Chaps
		animations.add(new AnimationDef("leatherchaps", "equipment", 0, 0, 0, true, false, 0)); //426
		//Female Top
		animations.add(new AnimationDef("fleatherbody", "equipment", 0, 0, 0, true, false, 0)); //427
		//Female Skirt
		animations.add(new AnimationDef("leatherskirt", "equipment", 0, 0, 0, true, false, 0)); //428

		//Skill Cape Batch One
		//animations.add(new AnimationDef("attackcape", "equipment", 0, 0, true, false, 0)); //315
		//animations.add(new AnimationDef("cookingcape", "equipment", 0, 0, true, false, 0)); //311
		animations.add(new AnimationDef("thievingcape", "equipment", 0, 0, 0, true, false, 0)); //429
		animations.add(new AnimationDef("fletchingcape", "equipment", 0, 0, 0, true, false, 0)); //430
		animations.add(new AnimationDef("miningcape", "equipment", 0, 0, 0, true, false, 0)); //431

		// April Fools Items
		animations.add(new AnimationDef("plaguemask", "equipment", 0, 0, 0, true, false, 0)); // 432
		animations.add(new AnimationDef("rubberchicken", "equipment", 0, 0, 0, true, false, 0)); // 433

		// Pickaxe
		animations.add(new AnimationDef("pickaxe", "equipment", 16737817, 0, true, false, 0)); // bronze 434
		animations.add(new AnimationDef("pickaxe", "equipment", 15654365, 0, true, false, 0)); // iron 435
		animations.add(new AnimationDef("pickaxe", "equipment", 15658734, 0, true, false, 0)); // steel 436
		animations.add(new AnimationDef("pickaxe", "equipment", 11717785, 0, true, false, 0)); // mithril 437
		animations.add(new AnimationDef("pickaxe", "equipment", 65535, 0, true, false, 0)); // adamant 438
		animations.add(new AnimationDef("pickaxe", "equipment", 3158064, 0, true, false, 0)); // rune 439

		// More skill capes (batch 2)
		// animations.add(new AnimationDef("fishingcape", "equipment", 0, 0, true, false, 0)); //310 - fishing cape
		// animations.add(new AnimationDef("strengthcape", "equipment", 0, 0, true, false, 0)); //385 strength cape
		// animations.add(new AnimationDef("smithingcape", "equipment", 0, 0, true, false, 0)); //384 smithing cape
		animations.add(new AnimationDef("magiccape", "equipment", 0, 0, true, false, 0)); // 440
		animations.add(new AnimationDef("craftingcape", "equipment", 0, 0, true, false, 0)); // 441

		// Chainmail leg
		animations.add(new AnimationDef("chainmaillegs", "equipment", 16737817, 0, true, false, 0)); // bronze 442
		animations.add(new AnimationDef("chainmaillegs", "equipment", 15654365, 0, true, false, 0)); // iron 443
		animations.add(new AnimationDef("chainmaillegs", "equipment", 15658734, 0, true, false, 0)); // steel 444
		animations.add(new AnimationDef("chainmaillegs", "equipment", 10072780, 0, true, false, 0)); // mithril 445
		animations.add(new AnimationDef("chainmaillegs", "equipment", 11717785, 0, true, false, 0)); // adamant 446
		animations.add(new AnimationDef("chainmaillegs", "equipment", 65535, 0, true, false, 0)); // rune 447
		animations.add(new AnimationDef("chainmaillegs", "equipment", 3158064, 0, true, false, 0)); //black 448

		// Additional dragon items
		animations.add(new AnimationDef("dragonkiteshield", "equipment", 0, 0, true, false, 0)); //449 - dragon kite shield

		// CTF
		animations.add(new AnimationDef("ctfflag", "equipment", 0, 0, true, false, 0)); //450 - white ctf flag
		animations.add(new AnimationDef("ctfflag", "equipment", 4246592, 0, true, false, 0)); //451 - guthix ctf flag
		animations.add(new AnimationDef("ctfflag", "equipment", 4210926, 0, true, false, 0)); //452 - saradomin ctf flag
		animations.add(new AnimationDef("ctfflag", "equipment", 16711680, 0, true, false, 0)); //453 - zamorak ctf flag
		animations.add(new AnimationDef("wings", "equipment", 0, 0, true, false, 0)); //454 - white wings
		animations.add(new AnimationDef("mvalkyriehelm", "equipment", 0, 0, true, false, 0)); //455 - medium valkyrie helmet
		animations.add(new AnimationDef("mvalkyriehelm", "equipment", 4246592, 0, true, false, 0)); //456 - medium guthix valkyrie helmet
		animations.add(new AnimationDef("mvalkyriehelm", "equipment", 4210926, 0, true, false, 0)); //457 - medium saradomin valkyrie helmet
		animations.add(new AnimationDef("mvalkyriehelm", "equipment", 16711680, 0, true, false, 0)); //458 - medium zamorak valkyrie helmet
		animations.add(new AnimationDef("valkyriehelm", "equipment", 0, 0, true, false, 0)); //459 - large valkyrie helmet
		animations.add(new AnimationDef("valkyriehelm", "equipment", 4246592, 0, true, false, 0)); //460 - large guthix valkyrie helmet
		animations.add(new AnimationDef("valkyriehelm", "equipment", 4210926, 0, true, false, 0)); //461 - large saradomin valkyrie helmet
		animations.add(new AnimationDef("valkyriehelm", "equipment", 16711680, 0, true, false, 0)); //462 - large zamorak valkyrie helmet
		animations.add(new AnimationDef("guthixcape", "equipment", 0, 0, true, false, 0)); //463 - guthix cape
		animations.add(new AnimationDef("saracape", "equipment", 0, 0, true, false, 0)); //464 - saradomin cape
		animations.add(new AnimationDef("zammycape", "equipment", 0, 0, true, false, 0)); //465 - zamorak cape
		animations.add(new AnimationDef("wings", "equipment", 4246592, 1513239, 0, true, false, 0)); //466 - guthix wings
		animations.add(new AnimationDef("wings", "equipment", 4210926, 1513239, 0, true, false, 0)); //467 - saradomin wings
		animations.add(new AnimationDef("wings", "equipment", 16711680, 1513239, 0, true, false, 0)); //468 - zamorak wings
		animations.add(new AnimationDef("dagger", "equipment", 16711748, 0, true, false, 0)); //469 - dragon dagger
		animations.add(new AnimationDef("poisoneddagger", "equipment", 16711748, 0, true, false, 0)); //470 - poison dragon dagger
		animations.add(new AnimationDef("crossbow", "equipment", 16711748, 0, true, false, 0)); //471 - dragon crossbow
		animations.add(new AnimationDef("longbow", "equipment", 16711748, 0, false, false, 0)); //472 - dragon longbow

		// Easter 2021
		animations.add(new AnimationDef("bunny", "npc", 16777215, 0, true, false, 0)); //472
		animations.add(new AnimationDef("duck", "npc", 16777215, 0, true, false, 0));//473
		animations.add(new AnimationDef("bunnymorph", "npc", 16777215, 0, true, false, 0));//474
		animations.add(new AnimationDef("eggmorph", "npc", 16777215, 0, true, false, 0));//475

		// Scimitar Animation
		animations.add(new AnimationDef("scimitar", "equipment", 16737817, 0, true, false, 0)); // 476 Bronze Scimitar
		animations.add(new AnimationDef("scimitar", "equipment", 15654365, 0, true, false, 0)); // 477 Iron Scimitar
		animations.add(new AnimationDef("scimitar", "equipment", 15658734, 0, true, false, 0)); // 478 Steel Scimitar
		animations.add(new AnimationDef("scimitar", "equipment", 3158064, 0, true, false, 0)); // 479 Black Scimitar
		animations.add(new AnimationDef("scimitar", "equipment", 10072780, 0, true, false, 0)); // 480 Mithril Scimitar
		animations.add(new AnimationDef("scimitar", "equipment", 11717785, 0, true, false, 0)); // 481 Adamantite Scimitar
		animations.add(new AnimationDef("scimitar", "equipment", 65535, 0, true, false, 0)); // 482 Rune Scimitar

		// Halloween 2021
		animations.add(new AnimationDef("deathmask", "equipment", 0, 0, true, false, 0)); // 483 death mask

		// Christmas 2021
		animations.add(new AnimationDef("yoyo", "equipment", 0, 0, true, false, 0)); // 484 yoyo in hand
		animations.add(new AnimationDef("yoyo_anim_1", "equipment", 0, 0, true, false, 0)); // 485 yoyo up-down 1
		animations.add(new AnimationDef("yoyo_anim_2", "equipment", 0, 0, true, false, 0)); // 486 yoyo up-down 2
		animations.add(new AnimationDef("yoyo_anim_3", "equipment", 0, 0, true, false, 0)); // 487 yoyo up-down 3
		animations.add(new AnimationDef("yoyo_anim_4", "equipment", 0, 0, true, false, 0)); // 488 yoyo up-down 4
		animations.add(new AnimationDef("yoyo_anim_crazy_1", "equipment", 0, 0, true, false, 0)); // 489 yoyo crazy 1'oclock
		animations.add(new AnimationDef("yoyo_anim_crazy_2", "equipment", 0, 0, true, false, 0)); // 490 yoyo crazy 2'oclock
		animations.add(new AnimationDef("yoyo_anim_crazy_3", "equipment", 0, 0, true, false, 0)); // 491 yoyo crazy 3'oclock
		animations.add(new AnimationDef("yoyo_anim_crazy_4", "equipment", 0, 0, true, false, 0)); // 492 yoyo crazy 4'oclock
		animations.add(new AnimationDef("yoyo_anim_crazy_5", "equipment", 0, 0, true, false, 0)); // 493 yoyo crazy 5'oclock
		// for 6'oclock, just use yoyo_anim_4
		animations.add(new AnimationDef("yoyo_anim_crazy_7", "equipment", 0, 0, true, false, 0)); // 494 yoyo crazy 7'oclock
		animations.add(new AnimationDef("yoyo_anim_crazy_8", "equipment", 0, 0, true, false, 0)); // 495 yoyo crazy 8'oclock
		animations.add(new AnimationDef("yoyo_anim_crazy_9", "equipment", 0, 0, true, false, 0)); // 496 yoyo crazy 9'oclock
		animations.add(new AnimationDef("yoyo_anim_crazy_10", "equipment", 0, 0, true, false, 0)); // 497 yoyo crazy 10'oclock
		animations.add(new AnimationDef("yoyo_anim_crazy_11", "equipment", 0, 0, true, false, 0)); // 498 yoyo crazy 11'oclock
		animations.add(new AnimationDef("yoyo_anim_crazy_12", "equipment", 0, 0, true, false, 0)); // 499 yoyo crazy 12'oclock

		// Easter 2022 aka Peeling the Onion
		animations.add(new AnimationDef("ogreears", "equipment", 0xb5ff1d, 0, true, false, 0)); // 500 ogre ears
		animations.add(new AnimationDef("leathervest", "equipment", 0, 0, true, false, 0)); // 501 leather vest

		//Crowns
		//Gold
		animations.add(new AnimationDef("crown", "equipment", 16763980,0, true, false, 0));//502
		//Sapphire
		animations.add(new AnimationDef("crown", "equipment", 19711, 0, true, false, 0)); //503
		//Emerald
		animations.add(new AnimationDef("crown", "equipment", 3394611,0, true, false, 0)); //504
		//Ruby
		animations.add(new AnimationDef("crown", "equipment", 16724736,0, true, false, 0)); //505
		//Diamond
		animations.add(new AnimationDef("crown", "equipment", 16184564,0, true, false, 0)); //506
		//Dragonstone
		animations.add(new AnimationDef("crown", "equipment", 12255487,0, true, false, 0)); //507

		LOGGER.info("Unpacking sprite archive");
		File workspaceFile = new File("conf" + File.separator + "server" + File.separator + "data" + File.separator + "Custom_Sprites.osar");
		if (workspaceFile.exists()) {
			Unpacker unpacker = new Unpacker();
			Workspace workspace = unpacker.unpackArchive(workspaceFile);

			for (Subspace subspace : workspace.getSubspaces()) {
				Map<String, Entry> entries = new HashMap<>();
				for (Entry entry : subspace.getEntryList())
					entries.put(entry.getID(), entry);
				spriteTree.put(subspace.getName(), entries);
			}
		}

		try {
			ZipFile spritesArchive = new ZipFile("conf" + File.separator + "server" + File.separator + "data" + File.separator + "Authentic_Sprites.orsc");
			int animationNumber = 0;
			label0:
			for (int animationIndex = 0; animationIndex < animations.size(); animationIndex++) {
				String s = animations.get(animationIndex).getName();
				for (int nextAnimationIndex = 0; nextAnimationIndex < animationIndex; nextAnimationIndex++) {
					if (!animations.get(nextAnimationIndex).getName().equalsIgnoreCase(s)) {
						continue;
					}
					animations.get(animationIndex).number = animations.get(nextAnimationIndex).getNumber();
					continue label0;
				}

				loadSprite(spritesArchive, animationNumber, 15);
				if (animations.get(animationIndex).hasA()) {
					loadSprite(spritesArchive, animationNumber + 15, 3);
				}
				if (animations.get(animationIndex).hasF()) {
					loadSprite(spritesArchive, animationNumber + 18, 9);
				}
				animations.get(animationIndex).number = animationNumber;
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


		/// Load the sprites


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

			if (this.world.getServer().getConfig().WANT_CUSTOM_SPRITES)
				drawPlayer();
			else
				drawPlayer(0, 0, Constants.AVATAR_WIDTH, Constants.AVATAR_HEIGHT, 0);

			BufferedImage img = new BufferedImage(Constants.AVATAR_WIDTH, Constants.AVATAR_HEIGHT, BufferedImage.TYPE_INT_ARGB);

			for (int i = 0; i < pixels.length; ++i) {
				if (pixels[i] != 0) {
					img.setRGB(i % Constants.AVATAR_WIDTH, i / Constants.AVATAR_WIDTH, pixels[i] | 0xFF000000);
				}
			}
			ImageIO.write(img, "png", new File(getWorld().getServer().getConfig().AVATAR_DIR + getWorld().getServer().getConfig().DB_NAME + "+" + playerID + ".png"));
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
					int k5 = ANGLE + animations.get(animationIndex).getNumber();
					k4 = (k4 * scaleX) / sprites[k5].getSomething1();
					i5 = (i5 * scaleY) / sprites[k5].getSomething2();
					int l5 = (scaleX * sprites[k5].getSomething1()) / sprites[animations.get(animationIndex).getNumber()].getSomething1();
					k4 -= (l5 - scaleX) / 2;
					int colour = animations.get(animationIndex).getGrayMask();
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

		public void drawSpriteClipping(Sprite e, int x, int y, int width, int height, int colorMask, int colorMask2, int blueMask,
									   boolean mirrorX, int topPixelSkew, int dummy, int colourTransform) {
			try {
				try {

					if (colorMask2 == 0) {
						colorMask2 = 0xFFFFFF;
					}

					if (colorMask == 0) {
						colorMask = 0xFFFFFF;
					}

					if (blueMask == 0)
						blueMask = 0xFFFFFF;

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

					destRowHead = e.getSomething1() * y;
					destFirstColumn += x << 16;
					if (y < 0) {
						skipEveryOther = 0 - y;
						destFirstColumn += destColumnSkewPerRow * skipEveryOther;
						height -= skipEveryOther;
						srcStartY += skipEveryOther * scaleY;
						destRowHead += e.getSomething1() * skipEveryOther;
						y = 0;
					}

					if (y + height >= e.getSomething2()) {
						height -= 1 + y + height - e.getSomething2();
					}

					//skipEveryOther = destRowHead / e.getSomething1() & dummy;

					skipEveryOther = 2;

					if (colorMask2 == 0xFFFFFF) {
						if (null != e.getPixels()) {

							this.plot_tran_scale_with_mask(dummy + 89, e.getPixels(), scaleY, 0,
								srcStartY, srcStartX, width, this.pixels, height, destColumnSkewPerRow,
								destRowHead, scaleX, destFirstColumn, spriteWidth, skipEveryOther, colorMask, colourTransform, blueMask, e);

						}
					} else {
						this.plot_trans_scale_with_2_masks(this.pixels, e.getPixels(), width,
							destColumnSkewPerRow, destFirstColumn, 1603920392, 0, colorMask2, scaleY, scaleX, srcStartX,
							skipEveryOther, srcStartY, spriteWidth, colorMask, height, destRowHead, colourTransform, blueMask, e);
					}
				} catch (Exception var24) {
					var24.printStackTrace();
				}

			} catch (RuntimeException var25) {
				var25.printStackTrace();
			}
		}

		private void plot_trans_scale_with_2_masks(int[] dest, int[] src, int destColumnCount,
												   int destColumnSkewPerRow, int destFirstColumn, int dummy1, int spritePixel, int mask2, int scaleY, int scaleX,
												   int srcStartX, int skipEveryOther, int srcStartY, int srcWidth, int mask1, int destHeight,
												   int destRowHead, int colourTransform, int blueMask, Sprite e) {
			try {

				int mask1R = mask1 >> 16 & 0xFF;
				int mask1G = mask1 >> 8 & 0xFF;
				int mask1B = mask1 & 0xFF;
				int mask2R = mask2 >> 16 & 0xFF;
				int mask2G = mask2 >> 8 & 0xFF;
				int mask2B = mask2 & 0xFF;

				if (blueMask == 0)
					blueMask = 0xFFFFFF;

				try {
					int var27 = srcStartX;

					for (int var28 = -destHeight; var28 < 0; ++var28) {
						int var29 = (srcStartY >> 16) * srcWidth;
						int var30 = destFirstColumn >> 16;
						int var31 = destColumnCount;
						int var32;
						if (0 > var30) {
							var32 = 0 - var30;
							var31 = destColumnCount - var32;
							srcStartX += var32 * scaleX;
							var30 = 0;
						}

						if (e.getSomething1() <= var30 + var31) {
							var32 = var30 - e.getSomething1() + var31;
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
									} else if (blueMask != 0xFFFFFF && spritePixelR == spritePixelG && spritePixelB != spritePixelG) {
										int blueMaskR = blueMask >> 16 & 0xFF;
										int blueMaskG = blueMask >> 8 & 0xFF;
										int blueMaskB = blueMask & 0xFF;
										int shifter = spritePixelR * spritePixelB;
										spritePixelR = (blueMaskR * shifter) >> 16;
										spritePixelG = (blueMaskG * shifter) >> 16;
										spritePixelB = (blueMaskB * shifter) >> 16;
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

									int finalColour = opacity << 24;
									finalColour |= (((spriteR + canvasR) >> 8) << 16);
									finalColour |= (((spriteG + canvasG) >> 8) << 8);
									finalColour |= ((spriteB + canvasB) >> 8);

									dest[var32 + destRowHead] = finalColour;

								}

								srcStartX += scaleX;
							}
						}

						srcStartY += scaleY;
						srcStartX = var27;
						destRowHead += e.getSomething1();
						destFirstColumn += destColumnSkewPerRow;
					}
				} catch (Exception var33) {
					var33.printStackTrace();
				}

			} catch (RuntimeException var34) {
				var34.printStackTrace();
			}
		}

		private void plot_tran_scale_with_mask(int dummy2, int[] src, int scaleY, int dummy1, int srcStartY,
											   int srcStartX, int destColumnCount, int[] dest, int destHeight, int destColumnSkewPerRow, int destRowHead,
											   int scaleX, int destFirstColumn, int srcWidth, int skipEveryOther, int spritePixel, int colourTransform, int blueMask, Sprite e) {
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
						if (duFirstColumn < 0) {
							int lost = 0 - duFirstColumn;
							duFirstColumn = 0;
							duColumnCount = destColumnCount - lost;
							srcStartX += scaleX * lost;
						}

						skipEveryOther = 1 - skipEveryOther;
						if (duFirstColumn + duColumnCount >= e.getSomething1()) {
							int lost = duColumnCount + duFirstColumn - e.getSomething1();
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
									} else if (blueMask != 0xFFFFFF && newR == newG && newB != newR) {//blue mask?
										int blueMaskR = blueMask >> 16 & 0xFF;
										int blueMaskG = blueMask >> 8 & 0xFF;
										int blueMaskB = blueMask & 0xFF;
										int shifter = newR * newB;
										newR = (blueMaskR * shifter) >> 16;
										newG = (blueMaskG * shifter) >> 16;
										newB = (blueMaskB * shifter) >> 16;
									}

									int spriteR = ((newR * transformR) >> 8) * opacity;
									int spriteG = ((newG * transformG) >> 8) * opacity;
									int spriteB = ((newB * transformB) >> 8) * opacity;

									int canvasR = (dest[destRowHead + j] >> 16 & 0xff) * inverseOpacity;
									int canvasG = (dest[destRowHead + j] >> 8 & 0xff) * inverseOpacity;
									int canvasB = (dest[destRowHead + j] & 0xff) * inverseOpacity;

									int finalColour = opacity << 24;
									finalColour |= (((spriteR + canvasR) >> 8) << 16);
									finalColour |= (((spriteG + canvasG) >> 8) << 8);
									finalColour |= ((spriteB + canvasB) >> 8);
									dest[destRowHead + j] = finalColour;
								}

								srcStartX += scaleX;
							}
						}

						srcStartY += scaleY;
						srcStartX = firstColumn;
						destFirstColumn += destColumnSkewPerRow;
						destRowHead += e.getSomething1();
					}
				} catch (Exception var29) {
					var29.printStackTrace();
				}

			} catch (RuntimeException var30) {
				var30.printStackTrace();
			}
		}

		public final void drawPlayer() {
			try {
				for (int lay = 0; lay < 12; ++lay) {
					int mappedLayer = Constants.npcAnimationArray[0][lay];
					int animationIndex = wornItems[mappedLayer] - 1;

					if (animationIndex >= 0) {
						AnimationDef animationDef = animations.get(animationIndex);
						Sprite sprite = spriteTree.get(animationDef.getCategory()).get(animationDef.getName()).getFrames()[1].getSprite();

						int something1 = sprite.getSomething1();
						int something2 = sprite.getSomething2();
						int grayScaleColor = animationDef.getGrayMask();
						if (something1 != 0 && something2 != 0) {
							int skinColour = Constants.characterSkinColours[appearance.getSkinColour()];
							if (grayScaleColor == 1)
								grayScaleColor = Constants.characterHairColours[appearance.getHairColour()];
							else if (grayScaleColor == 2)
								grayScaleColor = Constants.characterTopBottomColours[appearance.getTopColour()];
							else if (grayScaleColor == 3)
								grayScaleColor = Constants.characterTopBottomColours[appearance.getTrouserColour()];
							drawSpriteClipping(sprite, 0, 0, something1,
								something2, grayScaleColor, skinColour, animationDef.getBlueMask(), false, 0, 1, 0xFFFFFFFF);
						}
					}
				}

			} catch (RuntimeException a) {
				a.printStackTrace();
			}
		}

		public World getWorld() {
			return world;
		}
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

}
