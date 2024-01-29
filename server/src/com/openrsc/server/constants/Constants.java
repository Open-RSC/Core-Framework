package com.openrsc.server.constants;

import com.openrsc.server.Server;
import com.openrsc.server.model.container.Item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Constants {

	private final Server server;
	private final com.openrsc.server.constants.Skills skills;
	private final Poison poison;
	private final Retreats retreats;
	private final Minigames minigames;
	private final Quests quests;
	private final SpellDamages spellDamages;

	public Constants(Server server) {
		this.server = server;
		this.skills = new com.openrsc.server.constants.Skills(this);
		this.poison = new Poison(this);
		this.retreats = new Retreats(this);
		this.minigames = new Minigames(this);
		this.quests = new Quests(this);
		this.spellDamages = new SpellDamages();
	}

	public final Server getServer() {
		return server;
	}
	public final com.openrsc.server.constants.Skills getSkills() {
		return skills;
	}
	public final Poison getPoison() {
		return poison;
	}
	public final Retreats getRetreats() {
		return retreats;
	}
	public final Minigames getMinigames() {
		return minigames;
	}
	public final Quests getQuests() {
		return quests;
	}
	public final SpellDamages getSpellDamages() { return spellDamages; }

	public final int[] STARTER_ITEMS = {ItemId.BRONZE_AXE.id(), ItemId.TINDERBOX.id(), ItemId.COOKEDMEAT.id()};
	public final Item[] OPENPK_STARTER_ITEMS = {new Item(ItemId.IRON_2_HANDED_SWORD.id(), 1), new Item(ItemId.RUBY_AMULET_OF_STRENGTH.id(), 1), new Item(ItemId.LARGE_IRON_HELMET.id(), 1), new Item(ItemId.IRON_PLATE_MAIL_BODY.id(), 1), new Item(ItemId.IRON_CHAIN_MAIL_BODY.id(), 1), new Item(ItemId.IRON_PLATE_MAIL_LEGS.id(), 1), new Item(ItemId.AIR_RUNE.id(), 100, false), new Item(ItemId.MIND_RUNE.id(), 100, false), new Item(ItemId.WATER_RUNE.id(), 100, false), new Item(ItemId.EARTH_RUNE.id(), 100, false), new Item(ItemId.FIRE_RUNE.id(), 100, false), new Item(ItemId.SHORTBOW.id(), 100, false), new Item(ItemId.IRON_ARROWS.id(), 100, false), new Item(ItemId.LOBSTER.id(), 10000, true), new Item(ItemId.SHARK.id(), 10000, true), new Item(ItemId.FULL_SUPER_ATTACK_POTION.id(), 1000, true), new Item(ItemId.FULL_SUPER_STRENGTH_POTION.id(), 1000, true), new Item(ItemId.FULL_SUPER_DEFENSE_POTION.id(), 1000, true), new Item(ItemId.FULL_RESTORE_PRAYER_POTION.id(), 1000, true), new Item(ItemId.FULL_RANGING_POTION.id(), 1000, true)};

	/**
	 * Strikes, Bolts & Blast Spells.
	 * <p/>
	 * Remember, 30+ Magic damage gives you +1 damage, so these damages are
	 * -1 the absolute max. Level Requirement, Max Damage
	 */
	//public final int[][] SPELLS = {{1, 1}, {5, 2}, {9, 3}, {13, 4}, {17, 5}, {23, 5}, {29, 6}, {35, 6}, {41, 7}, {47, 7}, {53, 8}, {59, 8}, {62, 9}, {65, 9}, {70, 10}, {75, 10}};

	/**
	 * ID's of all Undead-type of NPC's. (Used for undead sounds)
	 */
	public static final int[] UNDEAD_NPCS = {15, 53, 80, 178, 664, 41, 52, 68, 180, 214, 319, 40, 45, 46, 50, 179, 195, 516, 542};
	/**
	 * ID's of all ARMOR type NPC's. (Used for armor hitting sounds)
	 */
	public static final int[] ARMOR_NPCS = {66, 102, 189, 277, 322, 401324, 323, 632, 633};
	/**
	 * Maximum hit for Crumble Undead (Magic) spell. (Against undead)
	 */
	public static final int CRUMBLE_UNDEAD_MAX = 8;
	/**
	 * Size of Regions in RegionManager
	 */
	public static final int REGION_SIZE = 48;
	/**
	 * Maximum number of clan members
	 */
	public static final int MAX_CLAN_SIZE = 200;
	/**
	 * Maximum number of users of a party
	 */
	public static final int MAX_PARTY_SIZE = 5;
	/**
	 * How far in the Wilderness can you teleport with Charged Dragonstone Amulet of Glory
	 */
	public static final int GLORY_TELEPORT_LIMIT = 30;
	/**
	 * Objects that block projectiles
	 */
	public static final String[] objectsProjectileClipAllowed = {"gravestone", "sign", "broken pillar", "bone",
		"animalskull", "skull", "egg", "eggs", "ladder", "torch", "rock", "treestump", "railing",
		"railings", "gate", "fence", "table", "smashed chair", "smashed table", "longtable", "fence",
		"wooden gate", "metal gate", "chair"};
	/**
	 * Maximum world height
	 */
	public static final int MAX_HEIGHT = 4032; // 3776
	/**
	 * Maximum world width
	 */
	public static final int MAX_WIDTH = 1008; // 944
	/**
	 * Size in width of RSC Player Avatars
	 */
	public final static int AVATAR_WIDTH = 64;
	/**
	 * Size in height of RSC Player Avatars
	 */
	public final static int AVATAR_HEIGHT = 102;
	/**
	 * PK Bot retreat levels.
	 * This is essentially a second set of "difficulty" levels beyond heal levels.
	 */
	public final static int PKBOT_EASY_RETREATS = 20, PKBOT_MEDIUM_RETREATS = 25,
	PKBOT_HARD_RETREATS = 30, PKBOT_EXPERT_RETREATS = 35, PKBOT_SUPER_EXPERT_RETREATS = 40;
	/**
	 * Allowable character skin colours
	 */
	public final static int characterSkinColours[] = {0xecded0, 0xccb366, 0xb38c40, 0x997326, 0x906020};
	/**
	 * Allowable character hair colours
	 */
	public final static int characterHairColours[] = {0xffc030, 0xffa040, 0x805030, 0x604020, 0x303030, 0xff6020, 0xff4000, 0xffffff, 65280, 65535};
	/**
	 * Allowable character top and bottom colours
	 */
	public final static int characterTopBottomColours[] = {0xff0000, 0xff8000, 0xffe000, 0xa0e000, 57344, 32768, 41088, 45311, 33023, 12528, 0xe000e0, 0x303030, 0x604000, 0x805000, 0xffffff};
	/**
	 * Character animation indices
	 */
	public final static int npcAnimationArray[][] =
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

	public static final Map<Integer, String> reportReasons = new HashMap<Integer, String>() {{
		// mudclient 205+ reasons
		put(1, "Buying or selling an account");
		put(2, "Encouraging rule-breaking");
		put(3, "Staff impersonation");
		put(4, "Macroing or use of bots");
		put(5, "Scamming");
		put(6, "Exploiting a bug");
		put(7, "Seriously offensive language");
		put(8, "Solicitation");
		put(9, "Disruptive behaviour");
		put(10, "Offensive account name");
		put(11, "Real-life threats");
		put(12, "Asking for or providing contact information");
		put(13, "Breaking real-world laws");
		put(14, "Advertising websites");

		// mudclient 182 to 204 reasons
		put(1 + 32, "Offensive language");
		put(2 + 32, "Item scamming");
		put(3 + 32, "Password scamming");
		put(4 + 32, "Bug abuse");
		put(5 + 32, "Jagex Staff impersonation");
		put(6 + 32, "Account sharing/trading");
		put(7 + 32, "Macroing");
		put(8 + 32, "Mutiple logging in"); // Lol mutiple {{sic}}
		put(9 + 32, "Encouraging others to break rules");
		put(10 + 32, "Misuse of customer support"); // How could this even happen in game
		put(11 + 32, "Advertising / website");
		put(12 + 32, "Real world item trading");

		// mudclient 153 to 181 reasons (includes 177!)
		put (0 + 64, "Non-vulgar insult such as 'idiot', 'noob', 'loser', etc...");
		put (1 + 64, "Swearing, profanity, or racial abuse directed at me personally");
		put (2 + 64, "I overheard the player being abusive to someone else");
		put (3 + 64, "Trade scam - lied about trade to steal items from me");
		put (4 + 64, "Asking players for their password / trying to steal accounts");
		put (5 + 64, "Attempting to buy/sell a RuneScape account");
		put (6 + 64, "Impersonating Jagex Staff");
		put (7 + 64, "Advertising scam website");
		put (8 + 64, "Other");
	}};

	public static final Map<Integer, String> reportDiscordColours = new HashMap<Integer, String>() {{
		// mudclient 205+ reasons
		put(1, "5069823"); // blue "Buying or selling an account"
		put(2, "16753433"); // yellow "Encouraging rule-breaking"
		put(3, "16776960"); // ff0 yellow "Staff impersonation"
		put(4, "8942042"); // purple "Macroing or use of bots"
		put(5, "5069823"); // blue "Scamming"
		put(6, "8942042"); // purple "Exploiting a bug"
		put(7, "10949120"); // report abuse red "Seriously offensive language"
		put(8, "16753433"); // yellow "Solicitation"
		put(9, "16753433"); // yellow "Disruptive behaviour"
		put(10, "10949120"); // report abuse red "Offensive account name"
		put(11, "16711680"); // red "Real-life threats"
		put(12, "1087508"); // green "Asking for or providing contact information"
		put(13, "16711680"); // red "Breaking real-world laws"
		put(14, "10949120"); // report abuse red "Advertising websites"

		// mudclient 182 to 204 reasons
		put(1 + 32, "10949120"); // report abuse red, "Offensive language"
		put(2 + 32, "5069823"); // blue, "Item scamming"
		put(3 + 32, "5069823"); // blue, "Password scamming"
		put(4 + 32, "8942042"); // purple, "Bug abuse"
		put(5 + 32, "16776960"); // ff0 yellow "Jagex Staff impersonation"
		put(6 + 32, "5069823"); // blue, "Account sharing/trading"
		put(7 + 32, "8942042"); // purple, "Macroing"
		put(8 + 32, "1087508"); // green, "Mutiple logging in";; NOT AGAINST OUR MODERN DAY RULES...!
		put(9 + 32, "15781888"); // yellow, "Encouraging others to break rules"
		put(10 + 32, "8942042"); // purple "Misuse of customer support"); // How could this even happen in game
		put(11 + 32, "10949120"); // report abuse red, "Advertising / website"
		put(12 + 32, "10949120"); // report abuse red, "Real world item trading"

		// mudclient 153 to 181 reasons (includes 177!)
		put (0 + 64, "1087508"); // green, "Non-vulgar insult such as 'idiot', 'noob', 'loser', etc..."
		put (1 + 64, "10949120"); // report abuse red "Swearing, profanity, or racial abuse directed at me personally"
		put (2 + 64, "15781888"); // yellow "I overheard the player being abusive to someone else"
		put (3 + 64, "5069823"); // blue "Trade scam - lied about trade to steal items from me"
		put (4 + 64, "5069823"); // blue "Asking players for their password / trying to steal accounts"
		put (5 + 64, "5069823"); // blue "Attempting to buy/sell a RS account"
		put (6 + 64, "16776960"); // ff0 yellow Impersonating Jagex Staff
		put (7 + 64, "10949120"); // report abuse red "Advertising scam website"
		put (8 + 64, "0"); // black "Other"
	}};

	public static final Map<Spells, Integer> spellMap = new HashMap<Spells, Integer>() {{
		put(Spells.WIND_STRIKE, 0);
		put(Spells.CONFUSE, 1);
		put(Spells.WATER_STRIKE, 2);
		put(Spells.ENCHANT_LVL1_AMULET, 3);
		put(Spells.EARTH_STRIKE, 4);
		put(Spells.WEAKEN, 5);
		put(Spells.FIRE_STRIKE, 6);
		put(Spells.BONES_TO_BANANAS, 7);
		put(Spells.WIND_BOLT, 8);
		put(Spells.CURSE, 9);
		put(Spells.LOW_LEVEL_ALCHEMY, 10);
		put(Spells.WATER_BOLT, 11);
		put(Spells.VARROCK_TELEPORT, 12);
		put(Spells.ENCHANT_LVL2_AMULET, 13);
		put(Spells.EARTH_BOLT, 14);
		put(Spells.LUMBRIDGE_TELEPORT, 15);
		put(Spells.TELEKINETIC_GRAB, 16);
		put(Spells.FIRE_BOLT, 17);
		put(Spells.FALADOR_TELEPORT, 18);
		put(Spells.CRUMBLE_UNDEAD, 19);
		put(Spells.WIND_BLAST, 20);
		put(Spells.SUPERHEAT_ITEM, 21);
		put(Spells.CAMELOT_TELEPORT, 22);
		put(Spells.WATER_BLAST, 23);
		put(Spells.ENCHANT_LVL3_AMULET, 24);
		put(Spells.IBAN_BLAST, 25);
		put(Spells.ARDOUGNE_TELEPORT, 26);
		put(Spells.EARTH_BLAST, 27);
		put(Spells.HIGH_LEVEL_ALCHEMY, 28);
		put(Spells.CHARGE_WATER_ORB, 29);
		put(Spells.ENCHANT_LVL4_AMULET, 30);
		put(Spells.WATCHTOWER_TELEPORT, 31);
		put(Spells.FIRE_BLAST, 32);
		put(Spells.CLAWS_OF_GUTHIX, 33);
		put(Spells.SARADOMIN_STRIKE, 34);
		put(Spells.FLAMES_OF_ZAMORAK, 35);
		put(Spells.CHARGE_EARTH_ORB, 36);
		put(Spells.WIND_WAVE, 37);
		put(Spells.CHARGE_FIRE_ORB, 38);
		put(Spells.WATER_WAVE, 39);
		put(Spells.CHARGE_AIR_ORB, 40);
		put(Spells.VULNERABILITY, 41);
		put(Spells.ENCHANT_LVL5_AMULET, 42);
		put(Spells.EARTH_WAVE, 43);
		put(Spells.ENFEEBLE, 44);
		put(Spells.FIRE_WAVE, 45);
		put(Spells.STUN, 46);
		put(Spells.CHARGE, 47);
	}};

	public static Spells spellToEnum(int id) {
		Iterator<Map.Entry<Spells, Integer>> itr = spellMap.entrySet().iterator();

		boolean found = false;
		Spells spell = null;
		while(itr.hasNext() && !found)
		{
			Map.Entry<Spells, Integer> entry = itr.next();
			if (entry.getValue() == id) {
				spell = entry.getKey();
				found = true;
			}
		}

		return spell;
	}
}
