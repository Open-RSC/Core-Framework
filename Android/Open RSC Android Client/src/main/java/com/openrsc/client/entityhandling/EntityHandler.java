package com.openrsc.client.entityhandling;

import com.openrsc.client.entityhandling.defs.DoorDef;
import com.openrsc.client.entityhandling.defs.ElevationDef;
import com.openrsc.client.entityhandling.defs.GameObjectDef;
import com.openrsc.client.entityhandling.defs.ItemDef;
import com.openrsc.client.entityhandling.defs.NPCDef;
import com.openrsc.client.entityhandling.defs.PrayerDef;
import com.openrsc.client.entityhandling.defs.SpellDef;
import com.openrsc.client.entityhandling.defs.SpriteDef;
import com.openrsc.client.entityhandling.defs.TileDef;
import com.openrsc.client.entityhandling.defs.extras.AnimationDef;
import com.openrsc.client.entityhandling.defs.extras.TextureDef;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import orsc.Config;
import orsc.mudclient;

public class EntityHandler {

	public static ArrayList<NPCDef> npcs = new ArrayList<>();
	private static ArrayList<ItemDef> items = new ArrayList<>();
	//public static ArrayList<ItemDef> specificSprites = new ArrayList<ItemDef>();
	private static ArrayList<TextureDef> textures = new ArrayList<>();
	private static ArrayList<AnimationDef> animations = new ArrayList<>();
	public static ArrayList<SpriteDef> projectiles = new ArrayList<>();
	public static ArrayList<SpriteDef> GUIparts = new ArrayList<>();
	public static ArrayList<SpriteDef> crowns = new ArrayList<>();
	private static ArrayList<SpellDef> spells = new ArrayList<>();
	private static ArrayList<PrayerDef> prayers = new ArrayList<>();
	private static ArrayList<TileDef> tiles = new ArrayList<>();
	private static ArrayList<DoorDef> doors = new ArrayList<>();
	private static ArrayList<ElevationDef> elevation = new ArrayList<>();
	private static ArrayList<GameObjectDef> objects = new ArrayList<>();
	private static ArrayList<String> models = new ArrayList<>();

	private static int invPictureCount = 0;

	public static int getModelCount() {
		return models.size();
	}

	public static String getModelName(int id) {
		if (id < 0 || id >= models.size()) {
			return null;
		}
		return models.get(id);
	}

	public static int invPictureCount() {
		return invPictureCount;
	}

	public static int npcCount() {
		return npcs.size();
	}

	public static NPCDef getNpcDef(int id) {
		if (id < 0 || id >= npcs.size()) {
			return null;
		}
		return npcs.get(id);
	}

	public static int itemCount() {
		return items.size();
	}

	public static ItemDef getItemDef(int id) {
		if (id < 0 || id >= items.size()) {
			return null;
		}
		return items.get(id);
	}

	public static int textureCount() {
		return textures.size();
	}

	public static int animationCount() {
		return animations.size();
	}

	public static AnimationDef getAnimationDef(int id) {
		if (id < 0 || id >= animations.size()) {
			return null;
		}
		return animations.get(id);
	}

	public static int spellCount() {
		return spells.size();
	}

	public static SpellDef getSpellDef(int id) {
		if (id < 0 || id >= spells.size()) {
			return null;
		}
		return spells.get(id);
	}

	public static int prayerCount() {
		return prayers.size();
	}

	public static PrayerDef getPrayerDef(int id) {
		if (id < 0 || id >= prayers.size()) {
			return null;
		}
		return prayers.get(id);
	}

	public static int tileCount() {
		return tiles.size();
	}

	public static TileDef getTileDef(int id) {
		if (id < 0 || id >= tiles.size()) {
			return null;
		}
		return tiles.get(id);
	}

	public static int doorCount() {
		return doors.size();
	}

	public static DoorDef getDoorDef(int id) {
		if (id < 0 || id >= doors.size()) {
			return null;
		}
		return doors.get(id);
	}

	public static int elevationCount() {
		return elevation.size();
	}

	public static ElevationDef getElevationDef(int id) {
		if (id < 0 || id >= elevation.size()) {
			return null;
		}
		return elevation.get(id);
	}

	public static int objectCount() {
		return objects.size();
	}

	public static GameObjectDef getObjectDef(int id) {
		if (id < 0 || id >= objects.size()) {
			return null;
		}
		return objects.get(id);
	}

	private static void loadPrayerDefinitions() {
		prayers.add(new PrayerDef(1, 15, "Thick skin",
				"Increases your defense by 5%"));
		prayers.add(new PrayerDef(4, 15, "Burst of strength",
				"Increases your strength by 5%"));
		prayers.add(new PrayerDef(7, 15, "Clarity of thought",
				"Increases your attack by 5%"));
		prayers.add(new PrayerDef(10, 30, "Rock skin",
				"Increases your defense by 10%"));
		prayers.add(new PrayerDef(13, 30, "Superhuman strength",
				"Increases your strength by 10%"));
		prayers.add(new PrayerDef(16, 30, "Improved reflexes",
				"Increases your attack by 10%"));
		prayers.add(new PrayerDef(19, 5, "Rapid restore",
				"2x restore rate for all stats except hits"));
		prayers.add(new PrayerDef(22, 10, "Rapid heal",
				"2x restore rate for hitpoints stat"));
		prayers.add(new PrayerDef(25, 10, "Protect items",
				"Keep 1 extra item if you die"));
		prayers.add(new PrayerDef(28, 60, "Steel skin",
				"Increases your defense by 15%"));
		prayers.add(new PrayerDef(31, 60, "Ultimate strength",
				"Increases your strength by 15%"));
		prayers.add(new PrayerDef(34, 60, "Incredible reflexes",
				"Increases your attack by 15%"));
		prayers.add(new PrayerDef(37, 60, "Paralyze monster",
				"Stops monsters from fighting back"));
		prayers.add(new PrayerDef(40, 60, "Protect from missiles",
				"100% protection from ranged attacks"));
	}

	private static void loadTileDefinitions() {
		tiles.add(new TileDef(-16913, 1, 0));
		tiles.add(new TileDef(1, 3, 1));
		tiles.add(new TileDef(3, 2, 0));
		tiles.add(new TileDef(3, 4, 0));
		tiles.add(new TileDef(-16913, 2, 0));
		tiles.add(new TileDef(-27685, 2, 0));
		tiles.add(new TileDef(25, 3, 1));
		tiles.add(new TileDef(12345678, 5, 1));
		tiles.add(new TileDef(-26426, 1, 1));
		tiles.add(new TileDef(-1, 5, 1));
		tiles.add(new TileDef(31, 3, 1));
		tiles.add(new TileDef(3, 4, 0));
		tiles.add(new TileDef(-4534, 2, 0));
		tiles.add(new TileDef(32, 2, 0));
		tiles.add(new TileDef(-9225, 2, 0));
		tiles.add(new TileDef(-3172, 2, 0));
		tiles.add(new TileDef(15, 2, 0));
		tiles.add(new TileDef(-2, 2, 0));
		tiles.add(new TileDef(-1, 3, 1));
		tiles.add(new TileDef(-2, 4, 0));
		tiles.add(new TileDef(-2, 4, 1));
		tiles.add(new TileDef(-2, 0, 0));
		tiles.add(new TileDef(-17793, 2, 0));
		tiles.add(new TileDef(-14594, 1, 1));
		tiles.add(new TileDef(1, 3, 0));
	}

	private static void loadElevationDefinitions() {
		elevation.add(new ElevationDef(64, 6));
		elevation.add(new ElevationDef(64, 3));
		elevation.add(new ElevationDef(96, 2));
		elevation.add(new ElevationDef(80, 33));
		elevation.add(new ElevationDef(80, 15));
		elevation.add(new ElevationDef(90, 49));
	}

	private static void loadTextureDefinitions() {
		textures.add(new TextureDef("wall", "door"));
		textures.add(new TextureDef("water", ""));
		textures.add(new TextureDef("wall", ""));
		textures.add(new TextureDef("planks", ""));
		textures.add(new TextureDef("wall", "doorway"));
		textures.add(new TextureDef("wall", "window"));
		textures.add(new TextureDef("roof", ""));
		textures.add(new TextureDef("wall", "arrowslit"));
		textures.add(new TextureDef("leafytree", ""));
		textures.add(new TextureDef("treestump", ""));
		textures.add(new TextureDef("fence", ""));
		textures.add(new TextureDef("mossy", ""));
		textures.add(new TextureDef("railings", ""));
		textures.add(new TextureDef("painting1", ""));
		textures.add(new TextureDef("painting2", ""));
		textures.add(new TextureDef("marble", ""));
		textures.add(new TextureDef("deadtree", ""));
		textures.add(new TextureDef("fountain", ""));
		textures.add(new TextureDef("wall", "stainedglass"));
		textures.add(new TextureDef("target", ""));
		textures.add(new TextureDef("books", ""));
		textures.add(new TextureDef("timbered", ""));
		textures.add(new TextureDef("timbered", "timberwindow"));
		textures.add(new TextureDef("mossybricks", ""));
		textures.add(new TextureDef("growingwheat", ""));
		textures.add(new TextureDef("gungywater", ""));
		textures.add(new TextureDef("web", ""));
		textures.add(new TextureDef("wall", "desertwindow"));
		textures.add(new TextureDef("wall", "crumbled"));
		textures.add(new TextureDef("cavern", ""));
		textures.add(new TextureDef("cavern2", ""));
		textures.add(new TextureDef("lava", ""));
		textures.add(new TextureDef("pentagram", ""));
		textures.add(new TextureDef("mapletree", ""));
		textures.add(new TextureDef("yewtree", ""));
		textures.add(new TextureDef("helmet", ""));
		textures.add(new TextureDef("canvas", "tentbottom"));
		textures.add(new TextureDef("Chainmail2", ""));
		textures.add(new TextureDef("mummy", ""));
		textures.add(new TextureDef("jungleleaf", ""));
		textures.add(new TextureDef("jungleleaf3", ""));
		textures.add(new TextureDef("jungleleaf4", ""));
		textures.add(new TextureDef("jungleleaf5", ""));
		textures.add(new TextureDef("jungleleaf6", ""));
		textures.add(new TextureDef("mossybricks", "arrowslit"));
		textures.add(new TextureDef("planks", "window"));
		textures.add(new TextureDef("planks", "junglewindow"));
		textures.add(new TextureDef("cargonet", ""));
		textures.add(new TextureDef("bark", ""));
		textures.add(new TextureDef("canvas", ""));
		textures.add(new TextureDef("canvas", "tentdoor"));
		textures.add(new TextureDef("wall", "lowcrumbled"));
		textures.add(new TextureDef("cavern", "crumbled"));
		textures.add(new TextureDef("cavern2", "crumbled"));
		textures.add(new TextureDef("lava", "flames"));
	}

	public enum PROJECTILE_TYPES {
		ORB(0),
		MAGIC(1),
		RANGED(2),
		GNOMEBALL(3),
		SKULL(4),
		SPIKEBALL(5),
		BLANK(6);//not sure if this is even used for anything

		private final int value;

		PROJECTILE_TYPES(int value) {
			this.value = value;
		}

		public int id() {
			return value;
		}
	}
	private static void loadProjectiles() {
		projectiles.add(new SpriteDef("orb projectile", mudclient.spriteProjectile,"projectiles:0",0));
		projectiles.add(new SpriteDef("magic projectile", mudclient.spriteProjectile + 1,"projectiles:1",1));
		projectiles.add(new SpriteDef("ranged projectile", mudclient.spriteProjectile + 2,"projectiles:2",2));
		projectiles.add(new SpriteDef("gnomeball projectile", mudclient.spriteProjectile + 3,"projectiles:3",3));
		projectiles.add(new SpriteDef("skull projectile", mudclient.spriteProjectile + 4,"projectiles:4",4));
		projectiles.add(new SpriteDef("spiked ball projectile", mudclient.spriteProjectile + 5,"projectiles:5",5));
		projectiles.add(new SpriteDef("blank projectile", mudclient.spriteProjectile + 6,"projectiles:6",6));
	}
	public enum GUIPARTS {
		MAINLOGO(0),
		BLUEBAR(1),
		ACCEPTBUTTON(2),
		DECLINEBUTTON(3),
		SKULL(4),
		DAMAGETAKEN(5),
		DAMAGEGIVEN(6),
		MENUBAR(7),
		MENUSOCIAL(8),
		MENUSPELLS(9),
		MINIMAPTAB(10),
		SETTINGSTAB(11),
		SKILLSTAB(12),
		BAGTAB(13),
		CLIPPING(14),
		CHECKMARK(15),
		XMARK(16),
		CHATTABS(17),
		CHATTABSCLAN(18),
		COMPASS(19),
		UPARROW(20),
		DOWNARROW(21),
		RIGHTARROW(22),
		LEFTARROW(23),
		MINIARROWUP(24),
		MINIARROWDOWN(25),
		DECORATEDBOXUL(26),
		DECORATEDBOXUR(27),
		DECORATEDBOXLL(28),
		DECORATEDBOXLR(29),
		YELLOWX1(30),
		YELLOWX2(31),
		YELLOWX3(32),
		YELLOWX4(33),
		REDX1(34),
		REDX2(35),
		REDX3(36),
		REDX4(37);

		private final int value;

		GUIPARTS(int value) {
			this.value = value;
		}

		public int id() {
			return value;
		}
	}
	private static void loadGUIParts() {
		GUIparts.add(new SpriteDef("main logo", mudclient.spriteMedia + 10,"GUI:7",0));
		GUIparts.add(new SpriteDef("bluebar", mudclient.spriteMedia + 22,"GUI:19",1));
		GUIparts.add(new SpriteDef("accept button", mudclient.spriteMedia + 25,"GUI:22",2));
		GUIparts.add(new SpriteDef("decline button", mudclient.spriteMedia + 26,"GUI:23",3));
		GUIparts.add(new SpriteDef("skull", mudclient.spriteMedia + 13,"GUI:10",4));
		GUIparts.add(new SpriteDef("blue damage taken bubble", mudclient.spriteMedia + 12,"GUI:9",5));
		GUIparts.add(new SpriteDef("red damage taken bubble", mudclient.spriteMedia  + 11,"GUI:8",6));
		GUIparts.add(new SpriteDef("menu bar", mudclient.spriteMedia,"GUI:0",7));
		GUIparts.add(new SpriteDef("social tab", mudclient.spriteMedia + 5,"GUI:5",8));
		GUIparts.add(new SpriteDef("spell tab", mudclient.spriteMedia + 4,"GUI:4",9));
		GUIparts.add(new SpriteDef("minimap tab", mudclient.spriteMedia + 2,"GUI:2",10));
		GUIparts.add(new SpriteDef("settings tab", mudclient.spriteMedia + 6,"GUI:6",11));
		GUIparts.add(new SpriteDef("skills tab", mudclient.spriteMedia + 3,"GUI:3",12));
		GUIparts.add(new SpriteDef("bag tab", mudclient.spriteMedia + 1,"GUI:1",13));
		GUIparts.add(new SpriteDef("clipping sprite", mudclient.spriteMedia + 9,"clipping:0",14));
		GUIparts.add(new SpriteDef("check mark", mudclient.spriteMedia + 27,"GUI:24",15));
		GUIparts.add(new SpriteDef("x mark", mudclient.spriteMedia  + 28,"GUI:25",16));
		GUIparts.add(new SpriteDef("chat tabs", mudclient.spriteMedia + 30,"GUI:27",17));
		GUIparts.add(new SpriteDef("chat tabs clan", mudclient.spriteMedia + 23,"GUI:20",18));
		GUIparts.add(new SpriteDef("compass", mudclient.spriteMedia + 24,"GUI:21",19));
		GUIparts.add(new SpriteDef("up arrow", mudclient.spriteUtil+ 8,"GUIutil:8",20));
		GUIparts.add(new SpriteDef("down arrow", mudclient.spriteUtil + 9,"GUIutil:9",21));
		GUIparts.add(new SpriteDef("right arrow", mudclient.spriteUtil + 6,"GUIutil:6",22));
		GUIparts.add(new SpriteDef("left arrow", mudclient.spriteUtil + 7,"GUIutil:7",23));
		GUIparts.add(new SpriteDef("mini up arrow", mudclient.spriteUtil,"GUIutil:0",24));
		GUIparts.add(new SpriteDef("mini down arrow", mudclient.spriteUtil + 1,"GUIutil:1",25));
		GUIparts.add(new SpriteDef("decorated box upper left", mudclient.spriteUtil + 2,"GUIutil:2",26));
		GUIparts.add(new SpriteDef("decorated box upper right", mudclient.spriteUtil + 3,"GUIutil:3",27));
		GUIparts.add(new SpriteDef("decorated box lower left", mudclient.spriteUtil + 4,"GUIutil:4",28));
		GUIparts.add(new SpriteDef("decorated box lower right", mudclient.spriteUtil + 5,"GUIutil:5",29));
		GUIparts.add(new SpriteDef("yellow cross 1", mudclient.spriteMedia + 14,"GUI:11",30));
		GUIparts.add(new SpriteDef("yellow cross 2", mudclient.spriteMedia + 15,"GUI:12",31));
		GUIparts.add(new SpriteDef("yellow cross 3", mudclient.spriteMedia + 16,"GUI:13",32));
		GUIparts.add(new SpriteDef("yellow cross 4", mudclient.spriteMedia + 17,"GUI:14",33));
		GUIparts.add(new SpriteDef("red cross 1", mudclient.spriteMedia + 18,"GUI:15",34));
		GUIparts.add(new SpriteDef("red cross 2", mudclient.spriteMedia + 19,"GUI:16",35));
		GUIparts.add(new SpriteDef("red cross 3", mudclient.spriteMedia + 20,"GUI:17",36));
		GUIparts.add(new SpriteDef("red cross 4", mudclient.spriteMedia + 21,"GUI:18",37));

	}

	public enum CROWN_TYPES {
		GREY_MOD(0),
		GOLD_MOD(1),
		DARKGREY_MOD(2),
		STAR(3),
		KEY(4);

		private final int value;

		CROWN_TYPES(int value) {
			this.value = value;
		}

		public int id() {
			return value;
		}
	}
	private static void loadCrowns() {
		crowns.add(new SpriteDef("grey mod crown", 3284, "crowns:0", 0));
		crowns.add(new SpriteDef("gold mod crown", 3285, "crowns:1", 1));
		crowns.add(new SpriteDef("dark grey mod crown", 3286, "crowns:2", 2));
		crowns.add(new SpriteDef("star", 3287, "crowns:3", 3));
		crowns.add(new SpriteDef("key", 3288, "crowns:4", 4));

	}

	private static void loadNpcDefinitionsA() {
		int i = 0;
		int[] sprites;

		/* Configurable NPC Data */
		String bankerOption1 = Config.S_RIGHT_CLICK_BANK ? "Bank" : ""; // Banker right click bank
		String bankerOption2 = Config.S_SPAWN_AUCTION_NPCS ? "Collect" : null; // Banker collect for auctions

		sprites = new int[]{130, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Unicorn", "It's a unicorn", "", 21, 23, 19, 23, true, sprites, 0, 0, 0, 0, 201, 230, 6, 6, 7, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bob", "An axe seller", "", 2, 2, 3, 2, false, sprites, 3158064, 16711680, 16711680, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{129, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Sheep", "A very wooly sheep", "", 0, 0, 3, 0, false, sprites, 0, 0, 0, 0, 170, 124, 6, 6, 5, i++));
		sprites = new int[]{132, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Chicken", "Yep definitely a chicken", "", 3, 4, 3, 4, true, sprites, 0, 0, 0, 0, 70, 62, 6, 6, 5, i++));
		sprites = new int[]{142, 139, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Goblin", "An ugly green creature", "", 16, 14, 12, 13, true, sprites, 0, 0, 0, 0, 219, 206, 9, 8, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Hans", "A castle servant", "", 3, 3, 3, 3, true, sprites, 1, 16711680, 65280, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{128, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("cow", "It's a multi purpose cow", "", 9, 8, 8, 9, true, sprites, 0, 0, 0, 0, 327, 240, 6, 6, 45, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 8, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("cook", "The head cook of Lumbridge castle", "", 20, 20, 3, 20, false, sprites, 1, 16777215, 16711680, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{131, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bear", "Eek! A bear!", "", 25, 23, 25, 26, true, sprites, 0, 0, 0, 0, 262, 247, 6, 9, 30, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Priest", "A priest of Saradomin", "", 0, 0, 3, 0, false, sprites, 1, 2105376, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Urhney", "He looks a little grumpy", "", 10, 10, 3, 10, false, sprites, 1, 2105376, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Man", "One of runescapes many citizens", "pickpocket", 11, 8, 7, 11, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bartender", "I could get a beer off him", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Camel", "Oh its a camel", "", 0, 0, 3, 0, false, sprites, 0, 0, 0, 0, 208, 208, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Gypsy", "An old gypsy lady", "", 0, 0, 3, 0, false, sprites, 15921906, 255, 65280, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{137, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ghost", "Ooh spooky", "", 15, 15, 5, 15, false, sprites, 0, 0, 0, 0, 201, 243, 9, 9, 5, i++));
		sprites = new int[]{13, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Sir Prysin", "One of the king's knights", "", 30, 60, 50, 20, false, sprites, 1, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 77, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Traiborn the wizard", "An old wizard", "", 20, 15, 3, 10, false, sprites, 16777215, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 28, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Captain Rovin", "The head of the palace guard", "", 40, 70, 65, 30, false, sprites, 11167296, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{123, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Rat", "Overgrown vermin", "", 10, 10, 5, 10, true, sprites, 0, 0, 0, 0, 346, 136, 7, 7, 45, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Reldo", "I think he's the librarian", "", 20, 15, 3, 10, false, sprites, 1, 2, 65280, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("mugger", "He jumps out and attacks people", "", 15, 10, 8, 8, true, sprites, 1, 16711680, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{124, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Lesser Demon", "Lesser but still pretty big", "", 78, 79, 79, 80, true, sprites, 0, 0, 0, 0, 275, 262, 11, 11, 30, i++));
		sprites = new int[]{125, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Giant Spider", "I think this spider has been genetically modified", "", 10, 10, 5, 10, true, sprites, 0, 0, 0, 0, 120, 104, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Man", "A shifty looking man", "", 30, 30, 30, 30, false, sprites, 1, 16711680, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Jonny the beard", "I can see why he's called the beard", "", 10, 20, 8, 5, true, sprites, 1, 2, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Baraek", "A fur trader", "", 30, 30, 30, 30, false, sprites, 1, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Katrine", "She doesn't look to friendly", "", 35, 25, 10, 30, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Tramp", "A scruffy looking chap", "", 9, 8, 5, 7, false, sprites, 16711680, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{123, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Rat", "A small muddy rat", "", 3, 4, 2, 2, true, sprites, 0, 0, 0, 0, 115, 45, 7, 7, 10, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Romeo", "He looks mildly confused", "", 20, 60, 60, 40, false, sprites, 16761440, 255, 8409120, 15523536, 125, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 89, -1, -1, -1, -1};
		npcs.add(new NPCDef("Juliet", "She looks a little stressed", "", 2, 4, 3, 2, false, sprites, 15645552, 16036851, 16036851, 15523536, 125, 225, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Father Lawrence", "A kindly looking priest", "", 0, 0, 3, 0, false, sprites, 11167296, 2105376, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Apothecary", "I wonder if he has any good potions", "", 10, 5, 7, 5, false, sprites, 11167296, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{125, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("spider", "Incey wincey", "", 5, 2, 2, 1, true, sprites, 0, 0, 0, 0, 40, 35, 6, 6, 5, i++));
		sprites = new int[]{124, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Delrith", "A freshly summoned demon", "", 42, 35, 7, 37, true, sprites, 0, 0, 0, 0, 275, 262, 11, 11, 30, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Veronica", "She doesn't look too happy", "", 1, 1, 5, 1, false, sprites, 15643488, 255, 3, 15523536, 140, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, 48, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Weaponsmaster", "The phoenix gang quartermaster", "", 35, 20, 20, 28, true, sprites, 1, 16711680, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Professor Oddenstein", "A mad scientist if I ever saw one", "", 3, 3, 7, 3, false, sprites, 16777215, 16777215, 16777215, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Curator", "He looks like he's daydreaming", "", 3, 2, 3, 2, false, sprites, 16777215, 2, 8409120, 15523536, 145, 200, 6, 6, 5, i++));
		sprites = new int[]{133, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("skeleton", "It rattles as it walks", "", 24, 20, 17, 23, true, sprites, 0, 0, 0, 0, 216, 234, 11, 11, 5, i++));
		sprites = new int[]{135, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("zombie", "The living dead", "", 23, 28, 24, 23, true, sprites, 0, 0, 0, 0, 174, 259, 12, 12, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 80, 62};
		npcs.add(new NPCDef("king", "King Roald the VIII", "", 15, 60, 30, 15, false, sprites, 1, 16711680, 16777215, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{138, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Giant bat", "An angry flying rodent", "", 32, 32, 32, 32, true, sprites, 0, 0, 0, 0, 225, 195, 5, 3, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bartender", "A friendly barman", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 15523536, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{134, 133, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("skeleton", "It rattles as it walks", "", 32, 30, 29, 35, true, sprites, 0, 0, 0, 0, 216, 234, 11, 11, 5, i++));
		sprites = new int[]{133, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("skeleton", "It rattles as it walks", "", 27, 24, 24, 28, true, sprites, 0, 0, 0, 0, 216, 234, 11, 11, 5, i++));
		sprites = new int[]{123, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Rat", "overgrown vermin", "", 16, 12, 10, 15, true, sprites, 0, 0, 0, 0, 346, 136, 7, 7, 45, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Horvik the Armourer", "He looks strong", "", 15, 22, 22, 6, false, sprites, 16761440, 2, 8409120, 15523536, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{131, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bear", "A  bear", "", 0, 0, 3, 0, false, sprites, 0, 0, 0, 0, 262, 247, 6, 9, 30, i++));
		sprites = new int[]{133, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("skeleton", "It rattles when it walks", "", 20, 18, 18, 21, true, sprites, 0, 0, 0, 0, 216, 234, 11, 11, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shopkeeper", "Maybe he'd like to buy some of my junk", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{135, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("zombie", "The living dead", "", 18, 20, 22, 19, true, sprites, 0, 0, 0, 0, 174, 259, 12, 12, 5, i++));
		sprites = new int[]{137, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ghost", "Ooh spooky", "", 23, 30, 25, 23, true, sprites, 0, 0, 0, 0, 201, 243, 9, 9, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Aubury", "I think he might be a shop keeper", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shopkeeper", "I wonder what he's got for sale", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("shopkeeper", "I can buy swords off him", "", 0, 0, 3, 0, false, sprites, 16761440, 2, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 82, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Darkwizard", "He works evil magic", "", 15, 15, 12, 12, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("lowe", "The owner of the archery store", "", 0, 0, 3, 0, false, sprites, 16761440, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Thessalia", "A young shop assistant", "", 0, 0, 3, 0, false, sprites, 1, 16036851, 3, 15523536, 130, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 82, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Darkwizard", "He works evil magic", "", 27, 24, 24, 27, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Giant", "A very large foe", "", 37, 36, 35, 40, true, sprites, 1, 2, 8409120, 15523536, 218, 330, 6, 6, 5, i++));
		sprites = new int[]{139, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Goblin", "An ugly green creature", "", 8, 9, 5, 9, true, sprites, 0, 0, 0, 0, 219, 206, 9, 8, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("farmer", "He grows the crops in this area", "pickpocket", 15, 16, 12, 18, true, sprites, 11167296, 8409120, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Thief", "He'll take anything that isn't nailed down", "", 24, 22, 17, 23, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 69, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "He tries to keep order around here", "pickpocket", 31, 30, 22, 31, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{18, 32, 42, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Black Knight", "A sinister looking knight", "", 45, 50, 42, 48, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{139, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Hobgoblin", "A large ugly green creature", "", 32, 34, 29, 34, true, sprites, 0, 0, 0, 0, 285, 268, 9, 8, 7, i++));
		sprites = new int[]{136, 135, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("zombie", "The living dead", "", 32, 31, 30, 35, true, sprites, 0, 0, 0, 0, 174, 259, 12, 12, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Zaff", "He trades in staffs", "", 0, 0, 3, 0, false, sprites, 3158064, 2, 3, 10056486, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{143, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Scorpion", "An extremely vicious scorpion", "", 21, 24, 17, 22, true, sprites, 0, 0, 0, 0, 362, 208, 7, 7, 45, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("silk trader", "He sells silk", "", 0, 0, 3, 0, false, sprites, 3158064, 16724172, 16724172, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Man", "One of Runescapes many citizens", "pickpocket", 11, 8, 7, 11, true, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guide", "He gives hints to new adventurers", "", 0, 0, 7, 0, false, sprites, 1, 32768, 8388863, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{125, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Giant Spider", "I think this spider has been genetically modified", "", 30, 31, 32, 34, true, sprites, 0, 0, 0, 0, 180, 156, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 70, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Peksa", "A helmet salesman", "", 11, 8, 7, 11, false, sprites, 15645552, 2, 3, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Barbarian", "Not civilised looking", "", 18, 15, 14, 18, true, sprites, 15645552, 8409120, 8409120, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Fred the farmer", "An old farmer", "", 11, 8, 7, 11, false, sprites, 15921906, 8409120, 8409136, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Gunthor the Brave", "The barbarians fearless leader", "", 37, 40, 35, 38, true, sprites, 15645552, 16732192, 8409120, 15523536, 165, 245, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 78, -1, 88, -1, -1, -1, 63};
		npcs.add(new NPCDef("Witch", "She's got warts", "", 35, 25, 10, 30, true, sprites, 1, 2, 3, 15523536, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{137, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ghost", "Ooh spooky", "", 23, 30, 25, 23, true, sprites, 0, 0, 0, 0, 201, 243, 9, 9, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 77, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Wizard", "An old wizard", "", 18, 15, 14, 18, true, sprites, 16777215, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shop Assistant", "Maybe he'd like to buy some of my junk", "", 0, 0, 3, 0, false, sprites, 6307872, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shop Assistant", "Maybe he'd like to buy some of my junk", "", 0, 0, 3, 0, false, sprites, 6307872, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 70, -1, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Zeke", "He sells Scimitars", "", 0, 0, 3, 0, false, sprites, 3158064, 16763952, 15609986, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 37, -1, -1, -1, -1, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Louie Legs", "He might want to sell something", "", 0, 0, 3, 0, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 98, 48, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Warrior", "A member of Al Kharid's military", "pickpocket", 20, 17, 19, 18, true, sprites, 1, 13385932, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shopkeeper", "I wonder what he's got for sale", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 13415270, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shop Assistant", "Maybe she'd like to buy some of my junk", "", 0, 0, 3, 0, false, sprites, 6307872, 8409120, 8409120, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, 47, -1, -1, -1, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Highwayman", "He holds up passers by", "", 14, 15, 13, 13, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Kebab Seller", "A seller of strange food", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 13415270, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{132, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Chicken", "Yep definitely a chicken", "", 3, 4, 3, 4, false, sprites, 0, 0, 0, 0, 70, 62, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ernest", "A former chicken", "", 3, 3, 3, 3, false, sprites, 16761440, 65535, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Monk", "A Peaceful monk", "", 12, 13, 15, 12, true, sprites, 16761440, 65535, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Dwarf", "A short angry guy", "", 20, 17, 16, 20, true, sprites, 7360576, 8409120, 8409120, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Banker", "He can look after my money", bankerOption1, bankerOption2, 11, 8, 7, 11, false, sprites, 15921906, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Count Draynor", "A vicious vampire", "", 40, 65, 35, 35, true, sprites, 1, 2, 3, 16576224, 140, 230, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Morgan", "A frigtened villager", "", 11, 8, 7, 11, false, sprites, 1, 16267666, 10464124, 15523536, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Dr Harlow", "His nose is very red", "", 11, 8, 7, 11, false, sprites, 15921906, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{126, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Deadly Red spider", "I think this spider has been genetically modified", "", 40, 36, 35, 35, true, sprites, 0, 0, 0, 0, 120, 104, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 69, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "He's here to guard this fortress", "pickpocket", 31, 30, 22, 31, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Cassie", "She sells shields", "", 35, 25, 10, 30, false, sprites, 16753488, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{19, 34, 43, -1, 49, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("White Knight", "A chivalrous knight", "", 55, 60, 52, 58, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 93, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ranael", "A shopkeeper of some sort", "", 35, 25, 10, 30, false, sprites, 16753488, 14518442, 3, 13415270, 145, 235, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Moss Giant", "his beard seems to have a life of its own", "", 62, 61, 60, 65, true, sprites, 7838054, 8409120, 8409120, 14483408, 261, 396, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shopkeeper", "I wonder what he's got for sale", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shop Assistant", "Maybe he'd like to buy some of my junk", "", 0, 0, 3, 0, false, sprites, 6307872, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 78, -1, 88, -1, -1, -1, 63};
		npcs.add(new NPCDef("Witch", "She's got warts", "", 35, 25, 10, 30, false, sprites, 1, 2, 3, 15523536, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{18, 32, 42, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Black Knight", "A sinister looking knight", "", 45, 50, 42, 48, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{139, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Greldo", "A small green warty creature", "", 8, 9, 5, 9, true, sprites, 0, 0, 0, 0, 219, 206, 9, 8, 5, i++));
		sprites = new int[]{19, 34, 43, -1, 49, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Sir Amik Varze", "The leader of the white knights", "", 55, 60, 52, 58, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 29, 38, 48, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guildmaster", "He's in charge of this place", "", 40, 40, 40, 40, false, sprites, 1, 13385932, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Valaine", "She runs the champion's store", "", 35, 25, 10, 30, false, sprites, 16753488, 3211263, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Drogo", "He runs a mining store", "", 20, 17, 16, 20, false, sprites, 7360576, 9465888, 9465888, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{124, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Imp", "A cheeky little imp", "", 4, 4, 8, 5, true, sprites, 0, 0, 0, 0, 74, 70, 11, 11, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Flynn", "The mace salesman", "", 15, 22, 22, 6, false, sprites, 16761440, 2, 8409120, 15523536, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Wyson the gardener", "An old gardener", "", 10, 8, 7, 8, false, sprites, 16777215, 8947848, 8409120, 15523536, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 77, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Wizard Mizgog", "An old wizard", "", 20, 15, 3, 10, false, sprites, 14535816, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("Prince Ali", "A young prince", "", 20, 20, 20, 20, false, sprites, 1, 15618286, 15658576, 13415270, 140, 215, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, -1, -1, 79, 62};
		npcs.add(new NPCDef("Hassan", "the Chancellor to the emir", "", 20, 20, 20, 20, false, sprites, 1, 16777215, 16777215, 13415270, 150, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 82, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Osman", "He looks a little shifty", "", 20, 20, 20, 20, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 116, -1, 22, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Joe", "Lady Keli's head guard", "", 40, 40, 40, 40, false, sprites, 1, 2, 3, 15523536, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Leela", "She comes from Al Kharid", "", 20, 20, 20, 20, false, sprites, 1, 12285781, 3, 13415270, 140, 215, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 89, -1, -1, -1, -1};
		npcs.add(new NPCDef("Lady Keli", "An Infamous bandit", "", 20, 20, 20, 20, false, sprites, 16763992, 15618286, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ned", "An old sailor", "", 20, 20, 20, 20, false, sprites, 16777215, 255, 3, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 78, -1, 88, -1, -1, -1, 63};
		npcs.add(new NPCDef("Aggie", "A witch", "", 35, 25, 10, 30, false, sprites, 1, 16711680, 3, 15523536, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 89, -1, -1, -1, -1};
		npcs.add(new NPCDef("Prince Ali", "That is an effective disguise", "", 10, 10, 10, 10, false, sprites, 16763992, 15618286, 3, 15523536, 140, 215, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Jailguard", "I wonder what he's guarding", "", 34, 34, 32, 36, true, sprites, 16763992, 2, 3, 15523536, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 48, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Redbeard Frank", "A pirate", "", 35, 25, 10, 30, false, sprites, 15630384, 2, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Wydin", "A grocer", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("shop assistant", "I can buy swords off him", "", 0, 0, 3, 0, false, sprites, 16761440, 2, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Brian", "An axe seller", "", 0, 0, 3, 0, false, sprites, 16761440, 2, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("squire", "A young squire", "", 0, 0, 3, 0, false, sprites, 14535800, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 8, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Head chef", "He looks after the chef's guild", "", 20, 20, 3, 20, false, sprites, 1, 16777215, 16711680, 15523536, 150, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Thurgo", "A short angry guy", "", 20, 17, 16, 20, false, sprites, 15658734, 8409200, 8409120, 13415270, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ice Giant", "He's got icicles in his beard", "", 67, 66, 70, 70, true, sprites, 6724027, 8425710, 8409120, 5623807, 261, 396, 6, 6, 5, i++));
		sprites = new int[]{143, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("King Scorpion", "Wow scorpions shouldn't grow that big", "", 40, 38, 30, 39, true, sprites, 0, 0, 0, 0, 543, 312, 7, 7, 45, i++));
		sprites = new int[]{6, 1, 2, -1, 48, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Pirate", "A vicious pirate", "", 35, 25, 20, 30, true, sprites, 1, 15658615, 14483456, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{19, 34, 43, -1, 49, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Sir Vyvin", "One of the white knights of Falador", "", 55, 60, 52, 58, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 116, -1, 84, 90, -1, -1, -1, -1};
		npcs.add(new NPCDef("Monk of Zamorak", "An evil cleric", "", 28, 32, 30, 28, true, sprites, 16761440, 65535, 255, 15392466, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 84, 90, -1, -1, -1, -1};
		npcs.add(new NPCDef("Monk of Zamorak", "An evil cleric", "", 18, 22, 20, 18, true, sprites, 16761440, 65535, 255, 15392466, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Wayne", "An armourer", "", 15, 22, 22, 6, false, sprites, 16761440, 2, 8409120, 15523536, 140, 210, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Barmaid", "a pretty barmaid", "", 35, 25, 10, 30, false, sprites, 16753488, 16777008, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Dwarven shopkeeper", "I wonder if he wants to buy any of my junk", "", 20, 17, 16, 20, false, sprites, 7360576, 9465888, 9465888, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Doric", "A dwarven smith", "", 20, 17, 16, 20, false, sprites, 16753488, 9465888, 9465888, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shopkeeper", "I wonder what he's got for sale", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shop Assistant", "Maybe he'd like to buy some of my junk", "", 0, 0, 3, 0, false, sprites, 6307872, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guide", "She gives hints to new adventurers", "", 0, 0, 7, 0, false, sprites, 1, 32768, 8388863, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 78, -1, 88, -1, -1, -1, 63};
		npcs.add(new NPCDef("Hetty", "A witch", "", 35, 25, 10, 30, false, sprites, 3182640, 16711680, 3, 15531728, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 78, -1, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Betty", "A witch", "", 35, 25, 10, 30, false, sprites, 1, 16711680, 3, 15523536, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bartender", "I could get a beer off him", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{142, 141, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("General wartface", "An ugly green creature", "", 16, 14, 12, 13, false, sprites, 0, 0, 0, 0, 264, 250, 9, 8, 5, i++));
		sprites = new int[]{142, 140, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("General Bentnoze", "An ugly green creature", "", 16, 14, 12, 13, false, sprites, 0, 0, 0, 0, 264, 250, 9, 8, 5, i++));
		sprites = new int[]{142, 140, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Goblin", "An ugly green creature", "", 16, 14, 12, 13, true, sprites, 0, 0, 0, 0, 219, 206, 9, 8, 5, i++));
		sprites = new int[]{142, 141, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Goblin", "An ugly green creature", "", 16, 14, 12, 13, true, sprites, 0, 0, 0, 0, 219, 206, 9, 8, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Herquin", "A gem merchant", "", 0, 0, 3, 0, false, sprites, 16753488, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Rommik", "The owner of the crafting shop", "", 0, 0, 3, 0, false, sprites, 16753488, 16732192, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Grum", "Grum the goldsmith", "", 0, 0, 3, 0, false, sprites, 16753488, 7368816, 7368816, 15523536, 130, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 44, 100, 118, -1, 35, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ice warrior", "A strange inhuman warrior", "", 57, 56, 59, 59, true, sprites, 6724027, 8425710, 8425710, 5623807, 150, 250, 6, 6, 5, i++));
		sprites = new int[]{3, 56, 38, -1, 109, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Warrior", "A skilled fighter", "pickpocket", 35, 25, 20, 30, true, sprites, 16753488, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Thrander", "A smith of some sort", "", 15, 22, 22, 6, false, sprites, 16761440, 2, 8409120, 15523536, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 48, -1, 70, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Border Guard", "a guard from Al Kharid", "", 20, 17, 19, 18, false, sprites, 1, 13385881, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 48, -1, 70, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Border Guard", "a guard from Al Kharid", "", 20, 17, 19, 18, false, sprites, 1, 13385881, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 4, 38, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Customs Officer", "She is here to stop smugglers", "", 23, 12, 15, 14, false, sprites, 1, 2, 3, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Luthas", "The owner of the banana plantation", "", 23, 12, 15, 14, false, sprites, 1, 2, 3, 9461792, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Zambo", "He will sell me exotic rum", "", 23, 12, 15, 14, false, sprites, 13398064, 3198139, 3, 9461792, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Captain Tobias", "An old sailor", "", 20, 20, 20, 20, false, sprites, 16777215, 255, 255, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Gerrant", "I wonder what he's got for sale", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shopkeeper", "I wonder what he's got for sale", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 9461792, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shop Assistant", "Maybe he'd like to buy some of my junk", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Seaman Lorris", "A young sailor", "", 20, 20, 20, 20, false, sprites, 16752704, 255, 255, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Seaman Thresnor", "A young sailor", "", 20, 20, 20, 20, false, sprites, 16752704, 255, 255, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Tanner", "He makes leather", "", 20, 60, 60, 40, false, sprites, 16761440, 8409120, 8409120, 13415270, 125, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Dommik", "The owner of the crafting shop", "", 0, 0, 3, 0, false, sprites, 16753488, 16732192, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Abbot Langley", "A Peaceful monk", "", 12, 13, 15, 12, false, sprites, 16761440, 65535, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Thordur", "He runs a a tourist attraction", "", 20, 17, 16, 20, false, sprites, 7360576, 9465888, 9465888, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Brother Jered", "human", "", 12, 13, 15, 12, false, sprites, 16761440, 65535, 255, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{123, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Rat", "Overgrown vermin", "", 16, 12, 10, 15, true, sprites, 0, 0, 0, 0, 346, 136, 7, 7, 45, i++));
		sprites = new int[]{137, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ghost", "Ooh spooky", "", 23, 30, 25, 23, true, sprites, 0, 0, 0, 0, 201, 243, 9, 9, 5, i++));
		sprites = new int[]{134, 133, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("skeleton", "it rattles when it walks", "", 32, 30, 29, 35, true, sprites, 0, 0, 0, 0, 216, 234, 11, 11, 5, i++));
		sprites = new int[]{136, 135, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("zombie", "the living dead", "", 32, 31, 30, 35, true, sprites, 0, 0, 0, 0, 174, 259, 12, 12, 5, i++));
		sprites = new int[]{124, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Lesser Demon", "Lesser but still very big", "", 78, 79, 79, 80, true, sprites, 0, 0, 0, 0, 275, 262, 11, 11, 30, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 82, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Melzar the mad", "He looks totally insane", "", 47, 44, 44, 47, true, sprites, 1, 2, 3, 16776944, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Scavvo", "He has lopsided eyes", "", 10, 10, 10, 10, false, sprites, 15921906, 7356480, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{124, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Greater Demon", "big red and incredibly evil", "", 86, 87, 87, 88, true, sprites, 0, 0, 0, 0, 358, 341, 11, 11, 30, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shopkeeper", "I wonder what he's got for sale", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shop Assistant", "Maybe he'd like to buy some of my junk", "", 0, 0, 3, 0, false, sprites, 6307872, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Oziach", "A strange little man", "", 0, 0, 3, 0, false, sprites, 6307872, 8440864, 8440864, 15523536, 145, 205, 6, 6, 5, i++));
		sprites = new int[]{131, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bear", "Eek! A bear!", "", 27, 25, 27, 28, true, sprites, 0, 0, 0, 0, 262, 247, 6, 9, 30, i++));
		sprites = new int[]{18, 32, 42, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Black Knight", "An armoured follower of Zamorak", "", 45, 50, 42, 48, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 110, 71, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("chaos Dwarf", "a dwarf gone bad", "", 58, 59, 61, 60, true, sprites, 14495808, 14495808, 14495808, 14495808, 135, 185, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("dwarf", "A dwarf who looks after the mining guild", "", 20, 17, 16, 20, false, sprites, 7360576, 9465888, 9465888, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{139, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Wormbrain", "Dumb even by goblin standards", "", 8, 9, 5, 9, true, sprites, 0, 0, 0, 0, 219, 206, 9, 8, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, 46, -1, -1, -1, -1};
		npcs.add(new NPCDef("Klarense", "A young sailor", "", 20, 20, 20, 20, false, sprites, 16752704, 221, 4, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ned", "An old sailor", "", 20, 20, 20, 20, false, sprites, 16777215, 255, 3, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{134, 133, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("skeleton", "A Taller than normal skeleton", "", 52, 50, 59, 55, true, sprites, 0, 0, 0, 0, 259, 281, 11, 11, 12, i++));
		sprites = new int[]{144, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Dragon", "A powerful and ancient dragon", "", 110, 110, 110, 110, true, sprites, 0, 0, 0, 0, 452, 326, 10, 7, 70, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Oracle", "A mystic of unknown race", "", 57, 56, 59, 59, false, sprites, 6724027, 14544622, 14544622, 5619694, 110, 180, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, 80, 64};
		npcs.add(new NPCDef("Duke of Lumbridge", "Duke Horacio of Lumbridge", "", 15, 60, 30, 15, false, sprites, 1, 16711680, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 32, 42, -1, 114, 75, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Dark Warrior", "A warrior touched by chaos", "", 20, 25, 17, 23, true, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 85, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("Druid", "A worshipper of Guthix", "", 28, 32, 30, 28, true, sprites, 16777215, 65535, 255, 15392466, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{145, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Red Dragon", "A big powerful dragon", "", 140, 140, 140, 140, true, sprites, 0, 0, 0, 0, 452, 326, 10, 7, 70, i++));
		sprites = new int[]{146, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Blue Dragon", "A mother dragon", "", 105, 105, 105, 105, true, sprites, 0, 0, 0, 0, 452, 326, 10, 7, 70, i++));
		sprites = new int[]{146, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Baby Blue Dragon", "Young but still dangerous", "", 50, 50, 50, 50, true, sprites, 0, 0, 0, 0, 226, 163, 10, 7, 30, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 85, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("Kaqemeex", "A wise druid", "", 28, 32, 30, 28, false, sprites, 14540253, 65535, 255, 15392466, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 85, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("Sanfew", "An old druid", "", 28, 32, 30, 28, false, sprites, 16777215, 65535, 255, 15392466, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{-1, 28, 37, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Suit of armour", "A dusty old suit of armour", "", 30, 30, 29, 28, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Adventurer", "A cleric", "", 12, 13, 15, 12, false, sprites, 16753248, 65535, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 122, -1, 77, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Adventurer", "A wizard", "", 20, 15, 3, 10, false, sprites, 1, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 28, 37, -1, 110, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Adventurer", "A Warrior", "", 55, 60, 52, 58, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, 107, -1, -1, -1, 46, -1, 80, -1};
		npcs.add(new NPCDef("Adventurer", "An archer", "", 39, 39, 39, 39, true, sprites, 16753488, 15645504, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Leprechaun", "A funny little man who lives in a tree", "", 20, 17, 16, 20, false, sprites, 5271616, 5286432, 5286432, 15523536, 103, 141, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Monk of entrana", "A Peaceful monk", "", 12, 13, 15, 12, false, sprites, 16761440, 65535, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Monk of entrana", "A Peaceful monk", "", 12, 13, 15, 12, false, sprites, 16761440, 65535, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{136, 135, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("zombie", "The living dead", "", 32, 31, 30, 35, true, sprites, 0, 0, 0, 0, 174, 259, 12, 12, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Monk of entrana", "A Peaceful monk", "", 12, 13, 15, 12, false, sprites, 16761440, 65535, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{137, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("tree spirit", "Ooh spooky", "", 100, 90, 85, 105, true, sprites, 0, 0, 0, 0, 241, 292, 9, 9, 5, i++));
		sprites = new int[]{128, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("cow", "It's a dairy cow", "", 9, 8, 8, 9, false, sprites, 0, 0, 0, 0, 327, 240, 6, 6, 45, i++));
		sprites = new int[]{78, 82, 88, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Irksol", "Is he invisible or just a set of floating clothes?", "", 2, 2, 3, 2, false, sprites, 3158064, 16711680, 16711680, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Fairy Lunderwin", "A fairy merchant", "", 2, 2, 3, 2, false, sprites, 3158064, 16711680, 16711680, 9461792, 94, 143, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Jakut", "An unusual looking merchant", "", 2, 2, 3, 2, false, sprites, 3180748, 65280, 65280, 9461792, 145, 260, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 37, -1, 110, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Doorman", "He guards the entrance to the faerie market", "", 55, 60, 52, 58, false, sprites, 3189418, 3170508, 3206894, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Fairy Shopkeeper", "I wonder what he's got for sale", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 94, 143, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Fairy Shop Assistant", "Maybe he'd like to buy some of my junk", "", 0, 0, 3, 0, false, sprites, 6307872, 8409120, 8409120, 15523536, 94, 143, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Fairy banker", "He can look after my money", bankerOption1, bankerOption2, 11, 8, 7, 11, false, sprites, 15921906, 2, 3, 15523536, 94, 143, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Giles", "He runs an ore exchange store", "", 30, 30, 30, 30, false, sprites, 1, 255, 14508096, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Miles", "He runs a bar exchange store", "", 30, 30, 30, 30, false, sprites, 16772761, 255, 14508096, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Niles", "He runs a fish exchange store", "", 30, 30, 30, 30, false, sprites, 15921906, 255, 14508096, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Gaius", "he sells very big swords", "", 15, 22, 22, 6, false, sprites, 16761440, 2, 8409120, 15523536, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Fairy Ladder attendant", "A worker in the faerie market", "", 0, 0, 3, 0, false, sprites, 16761440, 8409120, 8409120, 15523536, 94, 143, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 85, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Jatix", "A hard working druid", "", 28, 32, 30, 28, false, sprites, 11184810, 65535, 16777215, 15392466, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Master Crafter", "The man in charge of the crafter's guild", "", 0, 0, 3, 0, false, sprites, 16753488, 16732192, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, 49, -1, -1, -1, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Bandit", "He's ready for a fight", "", 32, 33, 27, 26, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 117, -1, -1, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("Noterazzo", "A bandit shopkeeper", "", 32, 33, 27, 26, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, 49, -1, -1, -1, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Bandit", "A wilderness outlaw", "", 32, 33, 27, 26, true, sprites, 1, 221, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 8, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Fat Tony", "A Gourmet Pizza chef", "", 20, 20, 3, 20, false, sprites, 1, 16777215, 16711680, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 4, 2, -1, 52, -1, -1, -1, -1, -1, 80, -1};
		npcs.add(new NPCDef("Hunter", "A bandit leader", "", 42, 43, 37, 36, true, sprites, 16752704, 8060928, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, 109, 98, -1, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("Black Heather", "A bandit leader", "", 42, 43, 37, 36, true, sprites, 1, 8060928, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, 49, -1, -1, 22, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("Speedy Keith", "A bandit leader", "", 42, 43, 37, 36, true, sprites, 16752704, 8060928, 3, 15523536, 150, 230, 6, 6, 5, i++));
		sprites = new int[]{147, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("White wolf sentry", "A vicious mountain wolf", "", 30, 32, 34, 31, true, sprites, 0, 0, 0, 0, 260, 198, 6, 10, 30, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Boy", "He doesn't seem very happy", "", 42, 43, 37, 36, false, sprites, 16752704, 8060928, 3, 15523536, 100, 147, 6, 6, 5, i++));
		sprites = new int[]{123, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Rat", "He seems to live here", "", 3, 4, 2, 2, false, sprites, 0, 0, 0, 0, 115, 45, 7, 7, 10, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 78, -1, 88, -1, -1, -1, 63};
		npcs.add(new NPCDef("Nora T Hag", "She's got warts", "", 35, 25, 10, 30, false, sprites, 1, 2, 3, 15527632, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{148, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Grey wolf", "A sinister looking wolf", "", 60, 62, 69, 65, true, sprites, 0, 0, 0, 0, 260, 198, 6, 10, 30, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 84, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("shapeshifter", "I've not seen anyone like this before", "", 28, 29, 21, 20, true, sprites, 14495808, 14495808, 14495808, 14495808, 150, 185, 6, 6, 5, i++));
		sprites = new int[]{125, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("shapeshifter", "I think this spider has been genetically modified", "", 38, 39, 31, 30, false, sprites, 0, 0, 0, 0, 120, 104, 6, 6, 5, i++));
		sprites = new int[]{131, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("shapeshifter", "Eek! A bear!", "", 48, 49, 41, 40, false, sprites, 0, 0, 0, 0, 262, 247, 6, 9, 30, i++));
		sprites = new int[]{148, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("shapeshifter", "A sinister looking wolf", "", 58, 59, 51, 50, false, sprites, 0, 0, 0, 0, 260, 198, 6, 10, 30, i++));
		sprites = new int[]{147, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("White wolf", "A vicious mountain wolf", "", 40, 42, 44, 41, true, sprites, 0, 0, 0, 0, 260, 198, 6, 10, 30, i++));
		sprites = new int[]{147, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Pack leader", "A vicious mountain wolf", "", 70, 72, 74, 71, true, sprites, 0, 0, 0, 0, 312, 238, 6, 10, 30, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Harry", "I wonder what he's got for sale", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Thug", "He likes hitting things", "", 19, 20, 18, 17, true, sprites, 1, 2, 255, 15523536, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{156, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Firebird", "Probably not a chicken", "", 6, 7, 5, 7, true, sprites, 0, 0, 0, 0, 70, 62, 6, 6, 5, i++));
		sprites = new int[]{3, 59, 41, 102, 113, 74, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Achetties", "One of Asgarnia's greatest heros", "", 45, 50, 42, 48, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 4, 44, 100, 118, -1, -1, -1, 155, -1, -1, 64};
		npcs.add(new NPCDef("Ice queen", "The leader of the ice warriors", "", 105, 101, 104, 104, true, sprites, 6724027, 8425710, 8425710, 5623807, 150, 250, 6, 6, 5, i++));
		//loadNpcDefinitionsB();
		//`sprites1`='" + npc.sprites[0] + "',`sprites2`='" + npc.sprites[1] + "',`sprites3`='" + npc.sprites[2] + "',`sprites4`='" + npc.sprites[3] + "',`sprites5`='" + npc.sprites[4] + "',`sprites6`='" + npc.sprites[5] + "',`sprites7`='" + npc.sprites[6] + "',`sprites8`='" + npc.sprites[7] + "',`sprites9`='" + npc.sprites[8] + "',`sprites10`='" + npc.sprites[9] + "',`sprites11`='" + npc.sprites[10] + "',`sprites12`='" + npc.sprites[11] + "'
		/*try {
			PrintWriter printWriter = new PrintWriter(new FileOutputStream("newNpcDef.txt"), true);
			int count = 0;
			for(NPCDef npc : npcs) {
				int npcCombat = (npc.getStr()
						+ npc.getAtt()
						+ npc.getDef()
						+ npc.getHits()) / 4;
				printWriter.println("UPDATE `openrsc_npcdef` SET `name`='" + npc.getName().replace("'", "''") + "',`description`='" + npc.getDescription().replace("'", "''") + "', " + (npc.getCommand().isEmpty() ? "" : "`command`='" + npc.getCommand() + "',") + "`attack`='" + npc.getAtt() + "',`strength`='" + npc.getStr() + "',`hits`='" + npc.getHits() + "',`defense`='" + npc.getDef() + "',`combatlvl`='" + npcCombat + "',`attackable`=" + (npc.isAttackable() ? "'1'" : "'0'") + ", `sprites1`='" + npc.sprites[0] + "',`sprites2`='" + npc.sprites[1] + "',`sprites3`='" + npc.sprites[2] + "',`sprites4`='" + npc.sprites[3] + "',`sprites5`='" + npc.sprites[4] + "',`sprites6`='" + npc.sprites[5] + "',`sprites7`='" + npc.sprites[6] + "',`sprites8`='" + npc.sprites[7] + "',`sprites9`='" + npc.sprites[8] + "',`sprites10`='" + npc.sprites[9] + "',`sprites11`='" + npc.sprites[10] + "',`sprites12`='" + npc.sprites[11] + "', `hairColour`='" + npc.getHairColour() + "',`topColour`='" + npc.getTopColour() + "', `bottomColour`='" + npc.bottomColour + "',`skinColour`='" + npc.getSkinColour() + "',`camera1`='" + npc.getCamera1() + "',`camera2`='" + npc.getCamera2() + "',`walkModel`='" + npc.getWalkModel() + "',`combatModel`='" + npc.getCombatModel() + "',`combatSprite`='" + npc.getCombatSprite() + "' WHERE `id`='" + npc.id + "';");

				printWriter.flush();
				count++;
			}
			printWriter.close();
			System.out.println("NPCS TOTAL: " + count);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
	}


	private static void loadNpcDefinitionsB() {
		int[] sprites;
		int i = npcs.size() - 1;

		/* Configurable NPC Data */
		String bankerOption1 = Config.S_RIGHT_CLICK_BANK ? "Bank" : ""; // Banker right click bank
		String bankerOption2 = Config.S_SPAWN_AUCTION_NPCS ? "Collect" : null; // Auction collect banker

		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Grubor", "A rough looking thief", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 15523536, 150, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, 51, -1, -1, -1, -1, -1, -1, 79, 67};
		npcs.add(new NPCDef("Trobert", "A well dressed thief", "", 14, 15, 13, 13, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, 110, -1, 75, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Garv", "A diligent guard", "", 31, 30, 22, 31, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 48, 163, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("guard", "A vicious pirate", "", 35, 25, 20, 30, false, sprites, 1, 15658615, 14483456, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 42, 110, -1, 75, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Grip", "Scar face petes head guard", "", 31, 60, 62, 31, true, sprites, 1, 2, 3, 15523536, 152, 231, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Alfonse the waiter", "He should get a clean apron", "", 11, 8, 7, 11, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 8, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Charlie the cook", "Head cook of the Shrimp and parrot", "", 20, 20, 3, 20, false, sprites, 1, 16777215, 15641122, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{159, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard Dog", "He doesn't seem pleased to see me", "", 45, 47, 49, 46, true, sprites, 0, 0, 0, 0, 247, 188, 6, 10, 30, i++));
		sprites = new int[]{160, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ice spider", "I think this spider has been genetically modified", "", 60, 66, 65, 65, true, sprites, 0, 0, 0, 0, 132, 114, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 48, 163, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Pirate", "A vicious pirate", "", 38, 28, 23, 33, true, sprites, 1, 7829248, 6684672, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 32, 42, -1, 114, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Jailer", "Guards prisoners for the black knights", "", 50, 55, 47, 53, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 32, 42, 103, 53, -1, -1, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Lord Darquarius", "A black knight commander", "", 75, 80, 72, 78, true, sprites, 1, 2, 3, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Seth", "He runs a fish exchange store", "", 30, 30, 30, 30, false, sprites, 15921906, 255, 14508096, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Banker", "He can look after my money", bankerOption1, bankerOption2, 11, 8, 7, 11, false, sprites, 15921906, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 33, 41, 102, 52, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Helemos", "A retired hero", "", 45, 50, 42, 48, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 84, 90, -1, -1, -1, -1};
		npcs.add(new NPCDef("Chaos Druid", "A crazy evil druid", "", 18, 22, 20, 18, true, sprites, 16777215, 65535, 255, 15392466, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{143, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Poison Scorpion", "It has a very vicious looking tail", "", 26, 29, 23, 27, true, sprites, 0, 0, 0, 0, 362, 208, 7, 7, 45, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Velrak the explorer", "he looks cold and hungry", "", 3, 3, 3, 3, false, sprites, 1, 16711680, 8952166, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 34, 43, 101, 49, -1, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("Sir Lancelot", "A knight of the round table", "", 55, 60, 52, 58, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 34, 43, 100, 49, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Sir Gawain", "A knight of the round table", "", 55, 60, 52, 58, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 34, 43, -1, 49, 150, -1, -1, -1, -1, -1, 68};
		npcs.add(new NPCDef("King Arthur", "A wise old king", "", 55, 60, 52, 58, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{18, 32, 42, 103, 53, -1, -1, -1, -1, -1, -1, 64};
		npcs.add(new NPCDef("Sir Mordred", "An evil knight", "", 57, 62, 54, 60, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{18, 32, 42, -1, 53, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Renegade knight", "He isn't very friendly", "", 50, 55, 48, 53, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 48, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Davon", "An amulet trader", "", 35, 25, 20, 30, false, sprites, 1, 15658615, 10289152, 11312784, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, 163, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bartender", "I could get some grog off him", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Arhein", "A merchant", "", 0, 0, 3, 0, false, sprites, 3158064, 13381836, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, 122, -1, -1, 88, -1, -1, -1, 63};
		npcs.add(new NPCDef("Morgan le faye", "An evil sorceress", "", 35, 25, 10, 30, false, sprites, 1, 2, 3, 15527632, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Candlemaker", "He makes and sells candles", "", 15, 22, 22, 6, false, sprites, 16761440, 2, 8409120, 15523536, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("lady", "She has a hint of magic about her", "", 0, 0, 3, 0, false, sprites, 15921906, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("lady", "She has a hint of magic about her", "", 0, 0, 3, 0, false, sprites, 15921906, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("lady", "She has a hint of magic about her", "", 0, 0, 3, 0, false, sprites, 15921906, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Beggar", "A scruffy looking chap", "", 9, 8, 5, 7, false, sprites, 16768256, 2, 3, 15523536, 135, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, 77, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Merlin", "An old wizard", "", 20, 15, 3, 10, false, sprites, 16777215, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{124, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Thrantax", "A freshly summoned demon", "", 90, 90, 90, 90, false, sprites, 0, 0, 0, 0, 358, 341, 11, 11, 30, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Hickton", "The owner of the archery store", "", 0, 0, 3, 0, false, sprites, 8409136, 14483456, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{164, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Black Demon", "A big scary jet black demon", "", 155, 157, 157, 158, true, sprites, 0, 0, 0, 0, 358, 341, 11, 11, 30, i++));
		sprites = new int[]{165, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Black Dragon", "A fierce dragon with black scales!", "", 210, 190, 190, 210, true, sprites, 0, 0, 0, 0, 542, 391, 10, 7, 84, i++));
		sprites = new int[]{166, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Poison Spider", "I think this spider has been genetically modified", "", 60, 62, 64, 68, true, sprites, 0, 0, 0, 0, 180, 156, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 122, -1, 84, 90, -1, -1, -1, -1};
		npcs.add(new NPCDef("Monk of Zamorak", "An evil cleric", "", 48, 52, 40, 48, true, sprites, 16761440, 65535, 255, 15392466, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{167, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Hellhound", "Hello nice doggy", "", 115, 112, 116, 114, true, sprites, 0, 0, 0, 0, 312, 237, 6, 10, 36, i++));
		sprites = new int[]{-1, -1, -1, -1, 109, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Animated axe", "a magic axe with a mind of it's own", "", 48, 45, 44, 48, true, sprites, 15645552, 8409120, 8409120, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{168, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Black Unicorn", "It's a sort of unicorn", "", 31, 33, 29, 33, true, sprites, 0, 0, 0, 0, 201, 230, 6, 6, 7, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Frincos", "A Peaceful monk", "", 12, 13, 15, 12, false, sprites, 16761440, 65535, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{78, 82, 88, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Otherworldly being", "Is he invisible or just a set of floating clothes?", "", 66, 66, 66, 66, true, sprites, 3158064, 16711680, 16711680, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Owen", "He runs a fish exchange store", "", 30, 30, 30, 30, false, sprites, 16772761, 255, 14508096, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 82, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Thormac the sorceror", "A powerful sorcerrer", "", 27, 24, 24, 27, false, sprites, 1, 2, 3, 10056486, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 76, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Seer", "An old wizard", "", 18, 15, 14, 18, false, sprites, 16777215, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{143, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Kharid Scorpion", "a smaller less dangerous scorpion", "", 21, 24, 17, 22, false, sprites, 0, 0, 0, 0, 121, 69, 7, 7, 45, i++));
		sprites = new int[]{143, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Kharid Scorpion", "a smaller less dangerous scorpion", "", 21, 24, 17, 22, false, sprites, 0, 0, 0, 0, 121, 69, 7, 7, 45, i++));
		sprites = new int[]{143, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Kharid Scorpion", "a smaller less dangerous scorpion", "", 21, 24, 17, 22, false, sprites, 0, 0, 0, 0, 121, 69, 7, 7, 45, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 28, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Barbarian guard", "Not very civilised", "", 18, 15, 14, 18, false, sprites, 15645552, 8409120, 8409120, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bartender", "I could get a beer off him", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 79, 66};
		npcs.add(new NPCDef("man", "A well dressed nobleman", "", 11, 8, 7, 11, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("gem trader", "He sells gems", "", 0, 0, 3, 0, false, sprites, 3158064, 3211212, 3211212, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, 79, 62};
		npcs.add(new NPCDef("Dimintheis", "A well dressed nobleman", "", 11, 8, 7, 11, false, sprites, 1, 7811157, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 8, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("chef", "A busy looking chef", "", 20, 20, 3, 20, false, sprites, 1, 16777215, 14540032, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{142, 140, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Hobgoblin", "An ugly green creature", "", 49, 47, 49, 48, true, sprites, 0, 0, 0, 0, 314, 295, 9, 8, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 117, -1, 45, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre", "A large dim looking humanoid", "", 72, 33, 60, 70, true, sprites, 11550752, 6299664, 6299664, 10056486, 212, 280, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 20, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Boot the Dwarf", "A short angry guy", "", 20, 17, 16, 20, false, sprites, 7360576, 10502176, 8409120, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{3, 1, 2, -1, 122, -1, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Wizard", "A young wizard", "", 18, 15, 14, 18, false, sprites, 16777215, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{169, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Chronozon", "Chronozon the blood demon", "", 183, 60, 60, 182, true, sprites, 0, 0, 0, 0, 358, 341, 11, 11, 30, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Captain Barnaby", "An old sailor", "", 20, 20, 20, 20, false, sprites, 16777215, 255, 255, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 4, 38, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Customs Official", "She's here to stop smugglers", "", 23, 12, 15, 14, false, sprites, 1, 2, 3, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Man", "One of Runescape's citizens", "pickpocket", 11, 8, 7, 11, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("farmer", "An humble peasant", "pickpocket", 15, 16, 12, 18, true, sprites, 11167296, 8409120, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 56, 38, -1, 109, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Warrior", "A skilled fighter", "pickpocket", 35, 25, 20, 30, true, sprites, 16753488, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 69, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "He tries to keep the law and order around here", "pickpocket", 31, 30, 22, 31, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{19, 34, 43, -1, 49, -1, -1, -1, -1, -1, -1, 64};
		npcs.add(new NPCDef("Knight", "A knight of Ardougne", "pickpocket", 55, 60, 52, 58, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 34, 43, 101, 119, -1, -1, -1, -1, -1, -1, 66};
		npcs.add(new NPCDef("Paladin", "A paladin of Ardougne", "pickpocket", 85, 55, 57, 88, true, sprites, 16760880, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 33, 41, 102, 52, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Hero", "A Hero of Ardougne", "pickpocket", 85, 80, 82, 88, true, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Baker", "He sells hot baked bread", "", 20, 20, 3, 20, false, sprites, 1, 16777215, 8912896, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("silk merchant", "He buys silk", "", 0, 0, 3, 0, false, sprites, 3158064, 16724172, 16724172, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, 67};
		npcs.add(new NPCDef("Fur trader", "A buyer and seller of animal furs", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, 66};
		npcs.add(new NPCDef("silver merchant", "He deals in silver", "", 0, 0, 3, 0, false, sprites, 3158064, 16764108, 16764108, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("spice merchant", "He sells exotic spices", "", 20, 20, 3, 20, false, sprites, 1, 16777215, 8912896, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("gem merchant", "He sells gems", "", 0, 0, 3, 0, false, sprites, 3158064, 3211212, 3211212, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, 56, 93, -1, -1, -1, -1};
		npcs.add(new NPCDef("Zenesha", "A shopkeeper of some sort", "", 35, 25, 10, 30, false, sprites, 16753488, 14518442, 3, 15523536, 145, 235, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Kangai Mau", "A tribesman", "", 0, 0, 3, 0, false, sprites, 1, 9461792, 16724016, 9461792, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 122, -1, 77, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Wizard Cromperty", "An old wizard", "", 20, 15, 3, 10, false, sprites, 16777215, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("RPDT employee", "A delivery man", "", 12, 12, 13, 12, false, sprites, 3158064, 170, 170, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Horacio", "An old gardener", "", 10, 8, 7, 8, false, sprites, 16777215, 8947848, 8409120, 15523536, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Aemad", "He helps run the adventurers store", "", 15, 22, 22, 6, false, sprites, 16761440, 2, 8409120, 15523536, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Kortan", "He helps run the adventurers store", "", 15, 22, 22, 6, false, sprites, 16761440, 2, 8409120, 15523536, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("zoo keeper", "He looks after Ardougne city zoo", "", 20, 20, 20, 20, true, sprites, 16752704, 187, 187, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, -1, 84, -1, -1, -1, -1, 68};
		npcs.add(new NPCDef("Make over mage", "He can change how I look", "", 0, 0, 3, 0, false, sprites, 3158064, 16763952, 15609986, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bartender", "I could get a beer off him", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("chuck", "A wood merchant", "", 0, 0, 3, 0, false, sprites, 16753488, 16732192, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Rogue", "He needs a shave", "pickpocket", 24, 22, 17, 23, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{170, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Shadow spider", "Is it a spider or is it a shadow", "", 54, 51, 55, 52, true, sprites, 0, 0, 0, 0, 132, 114, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Fire Giant", "A big guy with red glowing skin", "", 110, 112, 111, 105, true, sprites, 12255232, 16742195, 16742195, 16724787, 261, 396, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Grandpa Jack", "A wistful old man", "", 20, 20, 20, 20, false, sprites, 15658734, 12277060, 3, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Sinister stranger", "not your average fisherman", "", 40, 65, 35, 35, false, sprites, 1, 2, 3, 16576224, 140, 240, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bonzo", "Fishing competition organiser", "", 30, 30, 30, 30, false, sprites, 1, 16711680, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 107, -1, 45, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Forester", "He looks after McGrubor's wood", "", 24, 22, 17, 23, true, sprites, 1, 56576, 43520, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Morris", "Fishing competition organiser", "", 30, 30, 30, 30, false, sprites, 1, 16711680, 3, 15523536, 150, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Brother Omad", "A Peaceful monk", "", 12, 13, 15, 12, false, sprites, 16761440, 65535, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, 48, -1, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Thief", "A dastardly blanket thief", "", 24, 22, 17, 23, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 50, -1, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Head Thief", "A dastardly blanket thief", "", 34, 32, 37, 33, true, sprites, 1, 2, 3, 15523536, 150, 230, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Big Dave", "A well built fisherman", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 15523536, 165, 242, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Joshua", "A grumpy fisherman", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 15523536, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Mountain Dwarf", "A short angry guy", "", 20, 17, 16, 20, false, sprites, 1, 7360544, 7360544, 15523536, 130, 180, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Mountain Dwarf", "A short angry guy", "", 30, 27, 26, 30, true, sprites, 1, 7360544, 7360544, 15523536, 130, 180, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Brother Cedric", "A Peaceful monk", "", 12, 13, 15, 12, false, sprites, 16761440, 65535, 255, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 84, 90, -1, -1, -1, -1};
		npcs.add(new NPCDef("Necromancer", "A crazy evil necromancer", "", 28, 42, 40, 28, true, sprites, 16777215, 65535, 255, 16768722, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{135, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("zombie", "The living dead", "", 23, 28, 24, 23, true, sprites, 0, 0, 0, 0, 174, 259, 12, 12, 5, i++));
		sprites = new int[]{18, 1, 2, -1, -1, -1, 82, 88, 46, 11, -1, -1};
		npcs.add(new NPCDef("Lucien", "He walks with a slight limp", "", 24, 22, 17, 23, false, sprites, 1, 2, 3, 4, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 36, 97, 108, -1, 20, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("The Fire warrior of lesarkus", "A strange red humanoid", "", 72, 50, 59, 72, true, sprites, 16750950, 15634560, 15634560, 16752469, 150, 250, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 41, -1, -1, -1, 85, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("guardian of Armadyl", "A worshipper of Armadyl", "", 58, 52, 50, 58, false, sprites, 16772778, 65535, 255, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 41, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("guardian of Armadyl", "A worshipper of Armadyl", "", 58, 52, 50, 58, false, sprites, 16772778, 16777215, 255, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{18, 1, 2, -1, -1, -1, 82, 88, 46, 11, -1, -1};
		npcs.add(new NPCDef("Lucien", "He walks with a limp", "", 24, 22, 17, 23, true, sprites, 1, 2, 3, 4, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 78, -1, 88, -1, -1, -1, 63};
		npcs.add(new NPCDef("winelda", "A witch", "", 35, 25, 10, 30, false, sprites, 1, 16711680, 3, 15531984, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Brother Kojo", "A Peaceful monk", "", 12, 13, 15, 12, false, sprites, 16761440, 65535, 255, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{172, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Dungeon Rat", "Overgrown vermin", "", 20, 10, 12, 22, true, sprites, 0, 0, 0, 0, 346, 136, 7, 7, 45, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Master fisher", "The man in charge of the fishing guild", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Orven", "He runs a fish exchange store", "", 30, 30, 30, 30, false, sprites, 16772761, 16711680, 14508096, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Padik", "He runs a fish exchange store", "", 30, 30, 30, 30, false, sprites, 16772761, 16711680, 14508096, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shopkeeper", "He smells of fish", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Lady servil", "She look's wealthy", "", 1, 1, 5, 1, false, sprites, 15643488, 255, 3, 15523536, 140, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 69, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "It's one of General Khazard's guard's", "", 31, 30, 22, 31, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 173, 174, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "It's one of General Khazard's guard's", "", 31, 30, 22, 31, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 173, 174, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "It's one of General Khazard's guard's", "", 31, 30, 22, 31, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, 117, 173, 174, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "It's one of General Khazard's guard's", "", 31, 30, 22, 31, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Jeremy Servil", "A young squire", "", 0, 0, 3, 0, false, sprites, 14535800, 2, 3, 15523536, 120, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Justin Servil", "Jeremy servil's father", "", 0, 0, 3, 0, false, sprites, 12307576, 2, 3, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("fightslave joe", "He look's mistreated and weak", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("fightslave kelvin", "He look's mistreated and weak", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("local", "A scruffy looking chap", "", 9, 8, 5, 7, false, sprites, 16768256, 2, 3, 15523536, 135, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Khazard Bartender", "A tough looking barman", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 15523536, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{18, 32, 42, 103, 53, -1, -1, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("General Khazard", "He look's real nasty", "", 75, 80, 170, 78, true, sprites, 1, 2, 3, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 117, -1, 45, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Khazard Ogre", "Khazard's strongest ogre warrior", "", 72, 33, 60, 70, true, sprites, 11550752, 6299664, 6299664, 10056486, 212, 280, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, 117, 173, 174, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "It's one of General Khazard's guard's", "", 31, 30, 22, 31, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{143, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Khazard Scorpion", "A large angry scorpion", "", 50, 48, 40, 49, true, sprites, 0, 0, 0, 0, 543, 312, 7, 7, 45, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("hengrad", "He look's mistreated and weak", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{167, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bouncer", "Hello nice doggy", "", 130, 112, 116, 130, true, sprites, 0, 0, 0, 0, 312, 237, 6, 10, 36, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Stankers", "A cheerful looking fellow", "", 0, 0, 3, 0, false, sprites, 1, 8453920, 8409120, 14467993, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Docky", "An old sailor", "", 20, 20, 20, 20, false, sprites, 16777215, 255, 3, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Shopkeeper", "Maybe he'd like to buy some of my junk", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 150, -1, 86, -1, -1, -1, 66};
		npcs.add(new NPCDef("Fairy queen", "A very little queen", "", 2, 2, 3, 2, false, sprites, 16765040, 16777215, 16777215, 9461792, 94, 143, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, 77, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Merlin", "An old wizard", "", 20, 15, 3, 10, false, sprites, 16777215, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, 122, -1, 82, 88, -1, -1, -1, 63};
		npcs.add(new NPCDef("Crone", "A strange old lady", "", 35, 25, 10, 30, false, sprites, 5255248, 2, 3, 15523536, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 83, 87, -1, -1, 79, -1};
		npcs.add(new NPCDef("High priest of entrana", "A Peaceful monk", "", 12, 13, 15, 12, false, sprites, 3158064, 65535, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 175, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("elkoy", "It's a tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 16711680, 14535850, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 175, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("remsai", "It's a tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 16711680, 14535850, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 175, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("bolkoy", "It's a tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 16711680, 8965256, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 175, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("local gnome", "It's a young tree gnome", "", 3, 3, 3, 3, true, sprites, 1, 16711680, 8973960, 36864, 90, 110, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 150, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("bolren", "It's a gnome he look's important", "", 3, 3, 3, 3, false, sprites, 1, 16776960, 2280584, 36864, 100, 150, 6, 6, 5, i++));
		sprites = new int[]{18, 32, 42, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Black Knight titan", "He is blocking the way", "", 145, 150, 142, 148, true, sprites, 1, 2, 3, 15523536, 209, 314, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 175, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("kalron", "he look's lost", "", 3, 3, 3, 3, true, sprites, 1, 16711680, 8973824, 36864, 90, 110, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, 62};
		npcs.add(new NPCDef("brother Galahad", "A Peaceful monk", "", 12, 13, 15, 12, false, sprites, 16761440, 65535, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 175, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("tracker 1", "It's a tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 16711680, 8965256, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 175, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("tracker 2", "It's a tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 16711680, 8965256, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 175, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("tracker 3", "It's a tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 16711680, 8965256, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, 98, 117, 173, 174, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Khazard troop", "It's one of General Khazard's warrior's", "", 31, 30, 22, 31, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, 108, 175, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("commander montai", "It's a tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 255, 8965256, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, 107, 175, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("gnome troop", "It's a tree gnome trooper", "", 3, 3, 3, 3, true, sprites, 1, 16711680, 4508808, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{0, 32, 42, 103, 53, -1, -1, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("khazard warlord", "He look's real nasty", "", 75, 80, 170, 78, true, sprites, 1, 2, 3, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{19, 34, 43, 101, 49, -1, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("Sir Percival", "He's covered in pieces of straw", "", 55, 60, 52, 58, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, 154, -1, -1, -1, -1, 80, 62};
		npcs.add(new NPCDef("Fisher king", "an old king", "", 15, 60, 30, 15, false, sprites, 15658734, 16711680, 16777215, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("maiden", "She has a far away look in her eyes", "", 2, 4, 3, 2, false, sprites, 15645552, 16777215, 16777215, 15523536, 125, 225, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Fisherman", "an old fisherman", "", 15, 60, 30, 15, false, sprites, 15658734, 15636787, 11184810, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 34, 43, 101, 49, 154, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("King Percival", "The new fisher king", "", 55, 60, 52, 58, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("unhappy peasant", "He looks tired and hungry", "", 25, 26, 22, 28, true, sprites, 11167296, 8409120, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("happy peasant", "He looks well fed and full of energy", "", 25, 26, 22, 28, true, sprites, 11167296, 8409120, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("ceril", "It's Sir ceril carnillean a local noblemen", "", 11, 8, 7, 11, false, sprites, 16777215, 255, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("butler", "It's the carnillean family butler", "", 11, 8, 7, 11, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, 117, 70, 21, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("carnillean guard", "It's a carnillean family guard", "", 31, 30, 22, 31, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, 97, 48, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Tribesman", "A primative warrior", "", 38, 39, 39, 40, true, sprites, 1, 9461792, 9461792, 7360528, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("henryeta", "It's a wealthy looking woman", "", 2, 4, 3, 2, false, sprites, 15645552, 16777215, 16777215, 15523536, 125, 225, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("philipe", "It's a young well dressed boy", "", 0, 0, 3, 0, false, sprites, 14535800, 2, 3, 15523536, 120, 140, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("clivet", "A strange looking man in black ", "", 20, 20, 20, 20, false, sprites, 1, 2, 3, 13415270, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("cult member", "An suspicous looking man in black ", "", 20, 20, 20, 20, true, sprites, 1, 2, 3, 13415270, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{18, 32, 42, 103, 53, -1, -1, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Lord hazeel", "He could do with some sun", "", 75, 80, 170, 78, true, sprites, 1, 2, 3, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 110, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("alomone", "A musculer looking man in black ", "", 48, 46, 20, 56, false, sprites, 3, 2, 3, 13415270, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 98, 110, 173, 174, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Khazard commander", "It's one of General Khazard's commander's", "", 50, 50, 22, 45, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 8, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("claus", "the carnillean family cook", "", 20, 20, 3, 20, false, sprites, 1, 16777215, 16711680, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{129, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("1st plague sheep", "The sheep has the plague", "", 0, 0, 3, 0, false, sprites, 0, 0, 0, 0, 170, 124, 6, 6, 5, i++));
		sprites = new int[]{129, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("2nd plague sheep", "The sheep has the plague", "", 0, 0, 3, 0, false, sprites, 0, 0, 0, 0, 170, 124, 6, 6, 5, i++));
		sprites = new int[]{129, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("3rd plague sheep", "The sheep has the plague", "", 0, 0, 3, 0, false, sprites, 0, 0, 0, 0, 170, 124, 6, 6, 5, i++));
		sprites = new int[]{129, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("4th plague sheep", "The sheep has the plague", "", 0, 0, 3, 0, false, sprites, 0, 0, 0, 0, 170, 124, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Farmer brumty", "He looks after livestock in this area", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Doctor orbon", "A local doctor", "", 20, 20, 3, 20, false, sprites, 1, 16777215, 16711680, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Councillor Halgrive", "A town counceller", "", 20, 20, 20, 20, false, sprites, 16777215, 255, 255, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Edmond", "A local civilian", "", 20, 20, 20, 20, false, sprites, 1, 12255487, 16777215, 9461792, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Citizen", "He look's tired", "", 12, 11, 13, 10, true, sprites, 16760880, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Citizen", "He look's frightened", "", 10, 10, 13, 8, true, sprites, 16760880, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Citizen", "She look's frustrated", "", 11, 10, 13, 14, true, sprites, 1, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Citizen", "He look's angry", "", 20, 20, 23, 18, true, sprites, 16728064, 12603424, 5263392, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Citizen", "He look's disillusioned", "", 18, 12, 10, 20, true, sprites, 6307872, 10506320, 3174432, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Jethick", "A cynical old man", "", 18, 12, 10, 20, false, sprites, 16777215, 5263488, 3158048, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 177, 83, 176, 46, 11, 45, -1};
		npcs.add(new NPCDef("Mourner", "A mourner or plague healer", "", 2, 2, 3, 2, false, sprites, 3158064, 16711680, 16711680, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 1, 2, -1, -1, 177, 83, 176, 46, 11, 45, -1};
		npcs.add(new NPCDef("Mourner", "A mourner or plague healer", "", 2, 2, 3, 2, false, sprites, 3158064, 16711680, 16711680, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ted Rehnison", "The head of the Rehnison family", "", 11, 8, 7, 11, false, sprites, 15921906, 8409120, 8409136, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Martha Rehnison", "A fairly poor looking woman", "", 11, 10, 13, 14, false, sprites, 1, 8409120, 8409120, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Billy Rehnison", "The Rehnisons eldest son", "", 20, 60, 60, 40, false, sprites, 16761440, 8388688, 8409120, 15523536, 125, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Milli Rehnison", "She doesn't seem very happy", "", 42, 43, 37, 36, false, sprites, 16752704, 8060928, 3, 15523536, 112, 198, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Alrena", "She look's concerned", "", 1, 1, 5, 1, false, sprites, 15643488, 255, 3, 15523536, 140, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 177, 83, 176, 46, 11, 45, -1};
		npcs.add(new NPCDef("Mourner", "A mourner or plague healer", "", 2, 2, 3, 2, false, sprites, 3158064, 16711680, 16711680, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Clerk", "A bueracratic administrator", "", 2, 4, 3, 2, false, sprites, 16759632, 16021427, 12303291, 14470816, 138, 205, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Carla", "She look's upset", "", 1, 1, 5, 1, false, sprites, 15643488, 255, 3, 15523536, 140, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, 79, 64};
		npcs.add(new NPCDef("Bravek", "The city warder of West Ardougne", "", 15, 60, 30, 15, false, sprites, 1, 16711680, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Caroline", "A well dressed middle aged lady", "", 1, 1, 5, 1, false, sprites, 15643488, 255, 3, 15523536, 140, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Holgart", "An old sailor", "", 20, 20, 20, 20, false, sprites, 16777215, 255, 3, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Holgart", "An old sailor", "", 20, 20, 20, 20, false, sprites, 16777215, 255, 3, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Holgart", "An old sailor", "", 20, 20, 20, 20, false, sprites, 16777215, 255, 3, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("kent", "caroline's husband", "", 20, 60, 60, 40, false, sprites, 16761440, 8388688, 8409120, 15523536, 125, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 8, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("bailey", "the fishing platform cook", "", 20, 20, 3, 20, false, sprites, 1, 16777215, 16711680, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("kennith", "A young scared looking boy", "", 0, 0, 3, 0, false, sprites, 14535800, 11193464, 3, 15523536, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Platform Fisherman", "an emotionless fisherman", "", 15, 60, 30, 15, true, sprites, 15658734, 16763904, 16711680, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Platform Fisherman", "an emotionless fisherman", "", 15, 60, 30, 15, true, sprites, 15658734, 12255453, 14531532, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Platform Fisherman", "an emotionless fisherman", "", 15, 60, 30, 15, true, sprites, 15658734, 13426124, 15641275, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("Elena", "She doesn't look too happy", "", 1, 1, 5, 1, false, sprites, 13542224, 13408767, 3, 15523536, 140, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("jinno", "He doesn't seem to mind his lack of legs", "", 30, 30, 30, 30, false, sprites, 1, 255, 14508096, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Watto", "He doesn't seem to mind his lack of legs", "", 30, 30, 30, 30, false, sprites, 16772761, 255, 14508096, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 28, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Recruiter", "A member of the Ardougne royal army", "", 40, 70, 65, 30, false, sprites, 15643488, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 1, 2, -1, -1, 177, 83, 176, 46, 11, 45, 63};
		npcs.add(new NPCDef("Head mourner", "In charge of people with silly outfits", "", 2, 2, 3, 2, false, sprites, 3158064, 16711680, 16711680, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Almera", "A woman of the wilderness", "", 1, 1, 5, 1, false, sprites, 15643488, 255, 3, 15523536, 140, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("hudon", "A young boisterous looking lad", "", 0, 0, 3, 0, false, sprites, 14535800, 11193464, 3, 15523536, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("hadley", "A happy looking fellow", "", 15, 60, 30, 15, false, sprites, 15658734, 13426124, 15641275, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{123, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Rat", "Overgrown vermin", "", 15, 2, 3, 8, true, sprites, 0, 0, 0, 0, 346, 136, 7, 7, 45, i++));
		sprites = new int[]{0, 28, 2, -1, -1, -1, -1, -1, -1, -1, -1, 64};
		npcs.add(new NPCDef("Combat instructor", "He will tell me how to fight", "", 40, 70, 65, 30, false, sprites, 11167296, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 175, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("golrie", "It's a tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 13417420, 14535850, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, 122, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guide", "She gives hints to new adventurers", "", 0, 0, 7, 0, false, sprites, 1, 32768, 8388863, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{165, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("King Black Dragon", "The biggest meanest dragon around", "", 250, 240, 240, 250, true, sprites, 0, 0, 0, 0, 542, 391, 10, 7, 84, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 8, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("cooking instructor", "Talk to him to learn about runescape food", "", 20, 20, 3, 20, false, sprites, 1, 16777215, 16777215, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("fishing instructor", "He smells of fish", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("financial advisor", "He knows about money", "", 0, 0, 3, 0, false, sprites, 16753488, 7368816, 7368816, 15523536, 130, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("gerald", "An old fisherman", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 69, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("mining instructor", "A short angry guy", "", 20, 17, 16, 20, false, sprites, 7360576, 8409120, 8409120, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("Elena", "She looks concerned", "", 1, 1, 5, 1, false, sprites, 13542224, 13408767, 3, 15523536, 140, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Omart", "A nervous looking fellow", "", 15, 60, 30, 15, false, sprites, 15658734, 13426124, 15641275, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bank assistant", "She can look after my stuff", bankerOption1, bankerOption2, 11, 8, 7, 11, false, sprites, 15921906, 7368816, 7368816, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Jerico", "He looks friendly enough", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Kilron", "He looks shifty", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guidor's wife", "She looks rather concerned", "", 1, 1, 5, 1, false, sprites, 15643488, 255, 3, 15523536, 140, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 28, 37, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Quest advisor", "I wonder what advise he has to impart", "", 40, 70, 65, 30, false, sprites, 11167296, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("chemist", "human", "", 3, 3, 7, 3, false, sprites, 16777215, 16777215, 16777215, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 177, 83, 176, 46, 11, 45, -1};
		npcs.add(new NPCDef("Mourner", "A mourner or plague healer", "", 2, 2, 3, 2, false, sprites, 3158064, 16711680, 16711680, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 177, 83, 176, 46, 11, 45, -1};
		npcs.add(new NPCDef("Mourner", "A mourner or plague healer", "", 2, 2, 3, 2, false, sprites, 3158064, 16711680, 16711680, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 49, -1, -1, -1, -1, -1, -1, -1, 64};
		npcs.add(new NPCDef("Wilderness guide", "He's ready for a fight", "", 32, 33, 27, 26, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 77, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Magic Instructor", "An old wizard", "", 20, 15, 3, 10, false, sprites, 16777215, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 177, 83, 176, 46, 11, 45, -1};
		npcs.add(new NPCDef("Mourner", "A mourner or plague healer", "", 20, 20, 19, 30, true, sprites, 3158064, 16711680, 16711680, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("Community instructor", "This is the last advisor - honest", "", 2, 4, 3, 2, false, sprites, 16746544, 16021427, 12303291, 14470816, 138, 205, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, 46, -1, 45, -1};
		npcs.add(new NPCDef("boatman", "An old sailor", "", 20, 20, 20, 20, false, sprites, 6710886, 255, 3, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{133, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("skeleton mage", "It rattles as it walks", "", 24, 20, 17, 23, true, sprites, 0, 0, 0, 0, 216, 234, 11, 11, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("controls guide", "He's ready for a fight", "", 32, 33, 27, 26, false, sprites, 16746544, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("nurse sarah", "She's quite a looker", "", 1, 1, 5, 1, false, sprites, 15643488, 16777215, 16777215, 15523536, 140, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("Tailor", "He's ready for a party", "", 32, 33, 27, 26, false, sprites, 16746544, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 177, 83, 176, 46, 11, 45, -1};
		npcs.add(new NPCDef("Mourner", "A mourner or plague healer", "", 30, 20, 25, 25, true, sprites, 3158064, 16711680, 16711680, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 69, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "He tries to keep order around here", "", 31, 30, 22, 31, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Chemist", "He looks clever enough", "", 32, 33, 27, 26, false, sprites, 16746544, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, 83, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Chancy", "He's ready for a bet", "", 32, 33, 27, 26, false, sprites, 16746544, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Hops", "He's drunk", "", 32, 33, 27, 26, false, sprites, 16746544, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 83, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("DeVinci", "He has a colourful personality", "", 32, 33, 27, 26, false, sprites, 16746544, 11189213, 11189213, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 83, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("Guidor", "He's not that ill", "", 32, 33, 27, 26, false, sprites, 16746544, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, 83, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Chancy", "He's ready for a bet", "", 32, 33, 27, 26, false, sprites, 16746544, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Hops", "He's drunk", "", 32, 33, 27, 26, false, sprites, 16746544, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 83, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("DeVinci", "He has a colourful personality", "", 32, 33, 27, 26, false, sprites, 16746544, 11189213, 11189213, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 80, 62};
		npcs.add(new NPCDef("king Lathas", "King Lanthas of east ardounge", "", 15, 60, 30, 15, false, sprites, 1, 16711680, 16777215, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 77, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Head wizard", "He runs the wizards guild", "", 20, 15, 3, 10, false, sprites, 1, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, 77, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Magic store owner", "An old wizard", "", 20, 15, 3, 10, false, sprites, 16777215, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 77, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Wizard Frumscone", "A confused looking wizard", "", 20, 15, 3, 10, false, sprites, 1, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{135, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("target practice zombie", "The living dead", "", 23, 28, 24, 23, true, sprites, 0, 0, 0, 0, 174, 259, 12, 12, 5, i++));
		sprites = new int[]{6, 1, 2, 122, -1, -1, 83, 87, -1, -1, -1, -1};
		npcs.add(new NPCDef("Trufitus", "A wise old witch doctor", "", 10, 5, 7, 5, false, sprites, 12632256, 7296823, 7296823, 7296823, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 28, 37, -1, 109, -1, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("Colonel Radick", "A soldier of the town of Yanille", "", 40, 70, 65, 30, true, sprites, 11167296, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, 48, 70, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Soldier", "A soldier of the town of Yanille", "", 31, 30, 22, 31, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bartender", "I could get a beer off him", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{179, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Jungle Spider", "A venomous deadly spider", "", 45, 46, 50, 47, true, sprites, 0, 0, 0, 0, 120, 104, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, 83, 87, -1, -1, -1, 10};
		npcs.add(new NPCDef("Jiminua", "She looks very interested in selling some of her wares.", "", 0, 0, 3, 0, false, sprites, 10, 8409136, 7296823, 7296823, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 180, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Jogre", "An aggressive humanoid", "", 72, 33, 60, 70, true, sprites, 3852326, 3329330, 37633, 3978097, 212, 280, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 69, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "He tries to keep order around here", "", 31, 30, 22, 31, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 117, -1, 45, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre", "Useful for ranged training", "", 72, 33, 60, 70, true, sprites, 11550752, 6299664, 6299664, 10056486, 212, 280, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 69, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "He tries to keep order around here", "", 31, 30, 22, 31, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 69, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "He tries to keep order around here", "", 31, 30, 22, 31, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("shop keeper", "he sells weapons", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bartender", "I could get a beer off him", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Frenita", "runs a cookery shop", "", 0, 0, 3, 0, false, sprites, 16752704, 8409120, 8409120, 15523536, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 119, -1, 22, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre chieftan", "A slightly bigger uglier ogre", "", 92, 53, 80, 90, true, sprites, 11550752, 6299664, 6299664, 10056486, 222, 294, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 192, 197, 187, -1, 204, -1, -1};
		npcs.add(new NPCDef("rometti", "It's a well dressed tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 16711680, 14535850, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, 201, 202, -1, -1, -1, 65};
		npcs.add(new NPCDef("Rashiliyia", "A willowy ethereal being who floats above the ground", "", 80, 80, 80, 80, false, sprites, 1, 2, 3, 3978097, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, 193, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Blurberry", "It's a red faced tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 11184895, 14535850, 36864, 110, 140, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, 195, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Heckel funch", "It's another jolly tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 16711680, 14535850, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, 8, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Aluft Gianne", "It's a tree gnome chef", "", 3, 3, 3, 3, false, sprites, 1, 13434879, 14535901, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, 195, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Hudo glenfad", "It's another jolly tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 16711680, 14535850, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 88, -1, -1, -1, -1};
		npcs.add(new NPCDef("Irena", "human", "", 0, 0, 0, 0, false, sprites, 1, 12285781, 3, 13415270, 140, 215, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, 180, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Mosol", "A jungle warrior", "", 0, 0, 3, 0, false, sprites, 1, 9461792, 9461792, 9461792, 160, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Gnome banker", "It's tree gnome banker", bankerOption1, bankerOption2, 3, 3, 3, 3, false, sprites, 16777215, 1052688, 1052688, 36864, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 150, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("King Narnode Shareen", "It's a gnome he look's important", "", 3, 3, 3, 3, false, sprites, 1, 16776960, 16711424, 36864, 100, 150, 6, 6, 5, i++));
		sprites = new int[]{135, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("UndeadOne", "One of Rashaliyas Minions", "", 80, 59, 59, 50, true, sprites, 0, 0, 0, 0, 174, 259, 12, 12, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 82, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Drucas", "engraver", "", 20, 20, 20, 20, false, sprites, 1, 2, 3, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("tourist", "human", "", 32, 33, 27, 26, false, sprites, 16746544, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 150, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("King Narnode Shareen", "It's a gnome he look's important", "", 3, 3, 3, 3, false, sprites, 1, 16776960, 16711424, 36864, 100, 150, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 192, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Hazelmere", "An ancient looking gnome", "", 3, 3, 3, 3, false, sprites, 1, 16776960, 2280584, 36864, 100, 150, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 116, 69, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Glough", "An rough looking gnome", "", 3, 3, 3, 3, false, sprites, 1, 3158064, 3158064, 36864, 100, 150, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Shar", "Concerned about the economy", "b38c40", 0, 0, 3, 0, false, sprites, 16761440, 2, 8409120, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Shantay", "human", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 11766848, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("charlie", "Poor guy?", "", 0, 0, 3, 0, true, sprites, 1, 8409120, 8409120, 11766848, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 98, 180, 175, 22, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome guard", "A tree gnome guard", "", 31, 31, 31, 31, true, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 193, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome pilot", "He can fly the glider", "", 3, 3, 3, 3, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Mehman", "local", "805030", 32, 33, 27, 26, false, sprites, 16746544, 2, 3, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 1, 2, -1, -1, -1, 213, 214, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ana", "This lady doesn't look as if she belongs here.", "", 17, 15, 16, 18, false, sprites, 16760880, 8409120, 8409120, 10056486, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, -1, 84, 42, -1, -1, -1, -1};
		npcs.add(new NPCDef("Chaos Druid warrior", "A crazy evil druid", "", 48, 42, 40, 48, true, sprites, 1, 65535, 255, 15392466, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 193, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome pilot", "He can fly the glider", "", 3, 3, 3, 3, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Shipyard worker", "He look's busy", "", 48, 42, 40, 48, true, sprites, 3158064, 8409120, 8409120, 11766848, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Shipyard worker", "He look's busy", "", 48, 42, 40, 48, true, sprites, 3158064, 8409120, 8409120, 11766848, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Shipyard worker", "He look's busy", "", 48, 42, 40, 48, true, sprites, 3158064, 8409120, 8409120, 7360528, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 22, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Shipyard foreman", "He look's busy", "", 60, 60, 59, 69, false, sprites, 3158064, 8409120, 8409120, 7360528, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 22, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Shipyard foreman", "He look's busy", "", 60, 60, 59, 69, false, sprites, 3158064, 8409120, 8409120, 7360528, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 98, 180, 175, 22, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome guard", "A tree gnome guard", "", 23, 23, 23, 23, true, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 175, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Femi", "It's a little tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 175, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Femi", "It's a little tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 193, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Anita", "It's a little tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 116, 69, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Glough", "An rough looking gnome", "", 3, 3, 3, 3, false, sprites, 1, 3158064, 3158064, 36864, 100, 150, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 84, 90, -1, -1, -1, -1};
		npcs.add(new NPCDef("Salarin the twisted", "A crazy evil druid", "", 68, 72, 70, 68, true, sprites, 14483456, 65535, 255, 15392466, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{164, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Black Demon", "A big scary jet black demon", "", 195, 168, 160, 178, true, sprites, 0, 0, 0, 0, 398, 401, 11, 11, 30, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 193, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome pilot", "He can fly the glider", "", 3, 3, 3, 3, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 193, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome pilot", "He can fly the glider", "", 3, 3, 3, 3, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 193, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome pilot", "He can fly the glider", "", 3, 3, 3, 3, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 193, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome pilot", "He can fly the glider", "", 3, 3, 3, 3, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{5, 28, 37, -1, 110, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Sigbert the Adventurer", "A Warrior", "", 55, 60, 52, 58, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 70, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Yanille Watchman", "He watches out for invading ogres", "pickpocket", 41, 30, 22, 41, true, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 70, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Tower guard", "He stops people going up the tower", "", 41, 30, 22, 41, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 175, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Trainer", "He can advise on training", "", 11, 11, 11, 11, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 175, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Trainer", "He can advise on training", "", 11, 11, 11, 11, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 175, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Trainer", "He can advise on training", "", 11, 11, 11, 11, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 175, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Trainer", "He can advise on training", "pickpocket", 11, 11, 11, 11, false, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 175, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Blurberry barman", "He serves cocktails", "pickpocket", 3, 3, 3, 3, false, sprites, 1, 16776960, 16711424, 36864, 90, 120, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 194, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Gnome waiter", "He can serve you gnome food", "pickpocket", 3, 3, 3, 3, false, sprites, 1, 16777164, 3158064, 36864, 90, 120, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 98, 106, 175, 22, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome guard", "A tree gnome guard", "pickpocket", 31, 31, 31, 17, true, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 192, 198, 189, -1, 204, -1, -1};
		npcs.add(new NPCDef("Gnome child", "that's a little gnome", "pickpocket", 3, 3, 3, 3, true, sprites, 1, 16776960, 16711680, 36864, 80, 100, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, 97, 47, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Earth warrior", "A strange inhuman warrior", "", 52, 51, 54, 54, true, sprites, 6724027, 7356448, 7356448, 9461792, 150, 250, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 191, 200, 186, -1, 207, -1, -1};
		npcs.add(new NPCDef("Gnome child", "He's a little fellow", "pickpocket", 3, 3, 3, 3, true, sprites, 1, 16711935, 65280, 36864, 80, 100, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 193, 196, 188, -1, 203, -1, -1};
		npcs.add(new NPCDef("Gnome child", "hello little gnome", "pickpocket", 3, 3, 3, 3, true, sprites, 1, 16711884, 255, 36864, 80, 100, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 116, 116, 69, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Gulluck", "He sells weapons", "", 10, 11, 11, 11, false, sprites, 1, 3158064, 3158064, 36864, 100, 150, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gunnjorn", "Not civilised looking", "", 18, 15, 14, 18, false, sprites, 16768384, 8409120, 8409120, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{137, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Zadimus", "Ghostly Visage of the dead Zadimus", "", 0, 0, 0, 0, false, sprites, 0, 0, 0, 0, 201, 243, 9, 9, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 192, -1, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("Brimstail", "An ancient looking gnome", "", 3, 3, 3, 3, false, sprites, 1, 16776960, 2280584, 36864, 100, 150, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 195, 198, 190, -1, 205, -1, -1};
		npcs.add(new NPCDef("Gnome child", "He's a little fellow", "pickpocket", 3, 3, 3, 3, false, sprites, 1, 16711884, 255, 36864, 80, 100, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 175, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome local", "A tree gnome villager", "pickpocket", 9, 9, 9, 9, true, sprites, 1, 8409120, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 193, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome local", "A tree gnome villager", "pickpocket", 3, 3, 3, 3, true, sprites, 1, 8409120, 8409120, 36864, 90, 120, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Moss Giant", "his beard seems to have a life of its own", "", 62, 61, 60, 65, true, sprites, 7838054, 8409120, 8409120, 14483408, 261, 396, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 175, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "A tree gnome ball player", "tackle", 70, 70, 70, 70, true, sprites, 1, 7798801, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 191, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Goalie", "A gnome ball goal catcher", "", 70, 70, 70, 70, false, sprites, 1, 16728064, 16728064, 36864, 100, 120, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 175, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "A tree gnome ball player", "tackle", 70, 70, 70, 70, true, sprites, 1, 10027110, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 191, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "A tree gnome ball player", "tackle", 70, 70, 70, 70, true, sprites, 1, 10027110, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 192, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "A tree gnome ball player", "tackle", 70, 70, 70, 70, true, sprites, 1, 10027110, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 193, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "A tree gnome ball player", "tackle", 70, 70, 70, 70, true, sprites, 1, 10027025, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 116, 69, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Referee", "He controls the game", "", 3, 3, 3, 3, false, sprites, 1, 3158064, 3158064, 36864, 100, 150, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 194, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "A tree gnome ball player", "tackle", 70, 70, 70, 70, true, sprites, 1, 10027025, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 194, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "A tree gnome ball player", "tackle", 70, 70, 70, 70, true, sprites, 1, 16752704, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 194, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "A tree gnome ball player", "tackle", 70, 70, 70, 70, true, sprites, 1, 16752704, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 194, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "A tree gnome ball player", "tackle", 70, 70, 70, 70, true, sprites, 1, 16760880, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 194, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "A tree gnome ball player", "tackle", 70, 70, 70, 70, true, sprites, 1, 16760880, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 194, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "A tree gnome ball player", "tackle", 70, 70, 70, 70, true, sprites, 1, 16760880, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 194, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "A tree gnome ball player", "tackle", 70, 70, 70, 70, true, sprites, 1, 16760880, 8409120, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 194, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "He's on your team", "pass to", 70, 70, 70, 70, false, sprites, 1, 16728064, 16728064, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 194, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gnome Baller", "He's on your team", "pass to", 70, 70, 70, 70, false, sprites, 1, 16728064, 16728064, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 175, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Cheerleader", "It's a little tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 16728064, 16728064, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 175, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Cheerleader", "It's a little tree gnome", "", 3, 3, 3, 3, false, sprites, 1, 16728064, 16728064, 36864, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{135, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Nazastarool Zombie", "One of Rashaliyas Minions", "", 95, 70, 80, 90, true, sprites, 0, 0, 0, 0, 261, 388, 12, 12, 5, i++));
		sprites = new int[]{134, 133, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Nazastarool Skeleton", "One of Rashaliyas Minions", "", 95, 70, 80, 90, true, sprites, 0, 0, 0, 0, 259, 281, 11, 11, 12, i++));
		sprites = new int[]{137, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Nazastarool Ghost", "One of Rashaliyas Minions", "", 95, 70, 80, 90, true, sprites, 0, 0, 0, 0, 302, 365, 9, 9, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, 46, -1, 10, -1};
		npcs.add(new NPCDef("Fernahei", "An enthusiastic fishing shop owner", "", 10, 5, 7, 5, false, sprites, 12632256, 7296823, 7296823, 7296823, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 201, -1, -1, -1, 10, -1};
		npcs.add(new NPCDef("Jungle Banker", "He can look after my money", bankerOption1, bankerOption2, 11, 8, 7, 11, false, sprites, 12632256, 7296823, 7296823, 7296823, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Cart Driver", "He drives the cart", "", 15, 16, 12, 18, false, sprites, 16760880, 16777215, 16777215, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Cart Driver", "He drives the cart", "", 15, 16, 12, 18, false, sprites, 3158064, 16777215, 16777215, 7296823, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 83, 87, -1, -1, -1, 10};
		npcs.add(new NPCDef("Obli", "An intelligent looking shop owner", "", 0, 0, 3, 0, false, sprites, 10, 3158064, 7296823, 7296823, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 83, -1, -1, -1, -1, 10};
		npcs.add(new NPCDef("Kaleb", "This is Kaleb Paramaya - a warm and friendly inn owner", "", 0, 0, 3, 0, false, sprites, 10, 16752704, 16777215, 7296823, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 83, -1, -1, -1, -1, 10};
		npcs.add(new NPCDef("Yohnus", "This is Yohnus - he runs the local blacksmiths", "", 0, 0, 3, 0, false, sprites, 10, 16752704, 16777215, 7296823, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Serevel", "This is Serevel - he sells tickets for the 'Lady of the Waves'", "", 0, 0, 3, 0, false, sprites, 10, 16752704, 16777215, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, 83, -1, -1, -1, -1, 10};
		npcs.add(new NPCDef("Yanni", "Yanni Salika - He buys and sells antiques.", "", 0, 0, 3, 0, false, sprites, 10, 16777215, 16711680, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Official", "He helps the referee", "", 3, 3, 3, 3, false, sprites, 10, 1, 0, 0, 100, 130, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Koftik", "The kings top tracker", "", 18, 15, 14, 18, false, sprites, 16768384, 8409120, 8409120, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Koftik", "The kings top tracker", "", 18, 15, 14, 18, false, sprites, 16768384, 8409120, 8409120, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Koftik", "The kings top tracker", "", 18, 15, 14, 18, false, sprites, 16768384, 8409120, 8409120, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Koftik", "The kings top tracker", "", 18, 15, 14, 18, false, sprites, 16768384, 8409120, 8409120, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{123, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Blessed Vermen", "A undead servent of iban", "", 15, 7, 30, 7, true, sprites, 0, 0, 0, 0, 115, 45, 7, 7, 10, i++));
		sprites = new int[]{125, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Blessed Spider", "One of iban's eight legged friends", "", 45, 31, 32, 34, true, sprites, 0, 0, 0, 0, 180, 156, 6, 6, 5, i++));
		sprites = new int[]{6, 34, 43, 101, 119, -1, -1, -1, -1, -1, -1, 66};
		npcs.add(new NPCDef("Paladin", "A paladin of Ardougne", "", 85, 55, 57, 88, true, sprites, 16760880, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 34, 43, 101, 119, -1, -1, -1, -1, -1, -1, 66};
		npcs.add(new NPCDef("Paladin", "A paladin of Ardougne", "", 85, 55, 57, 88, true, sprites, 16760880, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("slave", "He seems possessed", "", 17, 15, 16, 18, true, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("slave", "He seems possessed", "", 17, 15, 16, 18, true, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("slave", "He seems to have been here a while", "", 17, 15, 16, 18, true, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("slave", "He seems possessed", "", 17, 15, 16, 18, true, sprites, 16760880, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("slave", "He seems to have been here a while", "", 17, 15, 16, 18, true, sprites, 16752704, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("slave", "He seems possessed", "", 17, 15, 16, 18, true, sprites, 16760880, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("slave", "He seems to have been here a while", "", 17, 15, 16, 18, true, sprites, 16777130, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{125, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Kalrag", "I think this is one of Ibans pets", "", 88, 69, 78, 78, true, sprites, 0, 0, 0, 0, 420, 404, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Niloof", "A short angry guy", "", 20, 17, 16, 20, false, sprites, 7360576, 8409120, 8409120, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 78, -1, 88, -1, -1, -1, 63};
		npcs.add(new NPCDef("Kardia the Witch", "She's got warts", "", 35, 25, 10, 30, false, sprites, 1, 2, 3, 15523536, 155, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Souless", "He seems an empty shell", "", 17, 15, 16, 18, true, sprites, 16777215, 8409120, 8409120, 16777215, 100, 200, 6, 6, 5, i++));
		sprites = new int[]{124, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Othainian", "big red and incredibly evil", "", 78, 78, 78, 78, true, sprites, 0, 0, 0, 0, 358, 341, 11, 11, 30, i++));
		sprites = new int[]{164, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Doomion", "A big scary jet black demon", "", 98, 98, 98, 98, true, sprites, 0, 0, 0, 0, 358, 341, 11, 11, 30, i++));
		sprites = new int[]{124, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Holthion", "big red and incredibly evil", "", 78, 78, 78, 78, true, sprites, 0, 0, 0, 0, 358, 341, 11, 11, 30, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Klank", "A short angry guy", "", 20, 17, 16, 20, false, sprites, 7360576, 8409120, 8409120, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{18, 1, 2, -1, -1, -1, 82, 88, 46, 11, -1, -1};
		npcs.add(new NPCDef("Iban", "You feel terror just looking at him", "", 24, 22, 17, 23, false, sprites, 1, 2, 3, 4, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Koftik", "The kings top tracker", "", 18, 15, 14, 18, false, sprites, 16768384, 8409120, 8409120, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{142, 139, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Goblin guard", "An imposing green creature", "", 48, 51, 43, 51, true, sprites, 0, 0, 0, 0, 285, 268, 9, 8, 7, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, 46, -1, 9, -1};
		npcs.add(new NPCDef("Observatory Professor", "He works in the observatory", "", 3, 3, 7, 3, false, sprites, 16777215, 16777215, 16777215, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ugthanki", "A dangerous type of spitting camel that can temporarily blind an opponent.", "", 45, 45, 45, 45, true, sprites, 0, 0, 0, 0, 208, 208, 6, 6, 25, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Observatory assistant", "The Professor's assistant", "", 3, 3, 7, 3, false, sprites, 16777215, 16777215, 16777215, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{210, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Souless", "A servent to zamorak", "", 23, 28, 24, 23, true, sprites, 0, 0, 0, 0, 174, 259, 12, 12, 5, i++));
		sprites = new int[]{166, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Dungeon spider", "A nasty poisonous arachnid", "", 25, 20, 35, 10, true, sprites, 0, 0, 0, 0, 90, 78, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Kamen", "A short angry guy", "", 20, 17, 16, 20, false, sprites, 7360576, 8409120, 8409120, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 84, 90, -1, -1, -1, -1};
		npcs.add(new NPCDef("Iban disciple", "An evil follower of Iban", "", 18, 22, 20, 18, true, sprites, 16761440, 65535, 255, 15392466, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Koftik", "The kings top tracker", "", 18, 15, 14, 18, false, sprites, 16768384, 8409120, 8409120, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{142, 139, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Goblin", "These goblins have grown strong", "", 24, 20, 16, 18, true, sprites, 0, 0, 0, 0, 219, 206, 9, 8, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, 46, -1, 10, -1};
		npcs.add(new NPCDef("Chadwell", "A sturdy looking gent", "", 18, 15, 14, 18, false, sprites, 16768384, 8409120, 8409120, 15523536, 160, 230, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, -1, -1, 46, -1, 9, -1};
		npcs.add(new NPCDef("Professor", "The owner of the observatory", "", 3, 3, 7, 3, false, sprites, 16777215, 16777215, 16777215, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{-1, -1, -1, 99, 49, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("San Tojalon", "The animated spirit of San Tojalon", "", 120, 120, 120, 120, true, sprites, 16760880, 8409120, 8409120, 10056486, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{137, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ghost", "A doomed victim of zamorak", "", 33, 33, 20, 30, true, sprites, 0, 0, 0, 0, 201, 243, 9, 9, 5, i++));
		sprites = new int[]{137, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Spirit of Scorpius", "The undead spirit of the follower of Zamorak", "", 100, 100, 100, 100, false, sprites, 0, 0, 0, 0, 201, 243, 9, 9, 5, i++));
		sprites = new int[]{143, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Scorpion", "There are nasty scorpions around this grave", "", 21, 24, 17, 22, false, sprites, 0, 0, 0, 0, 121, 69, 7, 7, 45, i++));
		sprites = new int[]{6, 1, 2, -1, 122, -1, 84, -1, -1, -1, -1, 68};
		npcs.add(new NPCDef("Dark Mage", "He works in the ways of dark magic", "", 0, 0, 3, 0, false, sprites, 3158064, 16763952, 15609986, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 49, 70, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Mercenary", "He seems to be guarding an area", "", 39, 39, 39, 39, true, sprites, 1, 11379585, 14858776, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 51, 71, 22, -1, -1, -1, -1, 62};
		npcs.add(new NPCDef("Mercenary Captain", "He's in control of the local guards.", "watch", 48, 80, 80, 48, true, sprites, 1, 11379585, 14858776, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 49, 70, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Mercenary", "He seems to be guarding an area", "", 48, 30, 48, 32, true, sprites, 1, 11379585, 14858776, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, 213, 214, -1, -1, -1, -1};
		npcs.add(new NPCDef("Mining Slave", "A chained slave forced to mine rocks.", "", 17, 15, 16, 18, true, sprites, 16777130, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, 77, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("Watchtower wizard", "A learned man", "", 20, 15, 3, 10, false, sprites, 16777215, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 122, 75, 21, -1, -1, 11, 79, 83};
		npcs.add(new NPCDef("Ogre Shaman", "An intelligent form of ogre", "", 100, 100, 100, 100, false, sprites, 3381504, 10027263, 26367, 26367, 222, 294, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 117, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Skavid", "Servant race to the ogres", "", 3, 3, 3, 3, false, sprites, 629145, 10079385, 10066329, 10066329, 96, 176, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 114, -1, 22, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre guard", "These ogres protect the city", "", 92, 53, 80, 90, false, sprites, 11550752, 6299664, 6299664, 10056486, 222, 294, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 114, -1, 22, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre guard", "These ogres protect the city", "", 92, 53, 80, 90, false, sprites, 11550752, 6299664, 6299664, 10056486, 222, 294, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 119, -1, 22, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre guard", "These ogres protect the city", "", 92, 53, 80, 90, false, sprites, 11550752, 6299664, 6299664, 10056486, 222, 294, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 117, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Skavid", "Servant race to the ogres", "", 3, 3, 3, 3, false, sprites, 629145, 10066329, 10066329, 10066329, 96, 176, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 117, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Skavid", "Servant race to the ogres", "", 3, 3, 3, 3, false, sprites, 629145, 10066329, 10066329, 10066329, 96, 176, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 119, -1, 22, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Og", "The chieftan of this ogre tribe", "", 92, 53, 80, 90, false, sprites, 11550752, 6299664, 6299664, 10056486, 222, 294, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 119, -1, 22, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Grew", "The chieftan of this ogre tribe", "", 92, 53, 80, 90, false, sprites, 11550752, 6299664, 6299664, 10056486, 222, 294, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 119, -1, 22, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Toban", "The chieftan of this ogre tribe", "", 92, 53, 80, 90, false, sprites, 11550752, 6299664, 6299664, 10056486, 222, 294, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 119, 70, 22, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Gorad", "A high ranking ogre official", "", 92, 53, 80, 90, true, sprites, 11550752, 6299664, 6299664, 10056486, 222, 294, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 114, -1, 24, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre guard", "this creature looks very tough", "", 98, 99, 99, 90, true, sprites, 11550752, 6299664, 6299664, 10056486, 233, 320, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 70, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Yanille Watchman", "A captured guard of Yanille", "", 41, 30, 22, 41, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 45, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre merchant", "He sells ogre-inspired items", "", 72, 33, 60, 70, false, sprites, 11550752, 6299664, 6299664, 10056486, 212, 280, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 45, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre trader", "He trades in metals", "", 72, 33, 60, 70, false, sprites, 11550752, 6299664, 6299664, 10056486, 212, 280, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 45, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre trader", "He trades in food", "", 72, 33, 60, 70, false, sprites, 11550752, 6299664, 6299664, 10056486, 212, 280, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 45, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre trader", "He trades in food", "", 72, 33, 60, 70, false, sprites, 11550752, 6299664, 6299664, 10056486, 212, 280, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 49, 70, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Mercenary", "He seems to be guarding an area", "", 48, 30, 48, 32, true, sprites, 1, 11379585, 14858776, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 119, 70, 22, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("City Guard", "high ranking ogre guards", "", 92, 53, 80, 90, false, sprites, 11550752, 6299664, 6299664, 10056486, 222, 294, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 49, 70, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Mercenary", "He seems to be guarding this area", "", 48, 30, 48, 32, true, sprites, 1, 11379585, 14858776, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Lawgof", "He guards the mines", "", 20, 17, 16, 20, false, sprites, 7360576, 3158064, 3158064, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Dwarf", "A short angry guy", "", 20, 17, 16, 20, true, sprites, 7360576, 3158064, 3158064, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("lollk", "He looks scared", "", 20, 17, 16, 20, false, sprites, 7360576, 3158064, 3158064, 15523536, 90, 130, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 117, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Skavid", "Servant race to the ogres", "", 3, 3, 3, 3, false, sprites, 629145, 10066329, 10066329, 10066329, 96, 176, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 119, -1, 22, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre guard", "These ogres protect the city", "", 92, 53, 80, 90, true, sprites, 11550752, 6299664, 6299664, 10056486, 222, 294, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Nulodion", "He's the head of black guard weapon development", "", 20, 17, 16, 20, false, sprites, 7360576, 3158064, 3158064, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 109, 70, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Dwarf", "A short angry guy", "", 20, 17, 16, 20, true, sprites, 7360576, 3158064, 3158064, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, 154, 85, 86, 155, 211, 80, 215};
		npcs.add(new NPCDef("Al Shabim", "The leader of a nomadic Bedabin desert people - sometimes referred to as the 'Tenti's'", "", 0, 0, 3, 0, false, sprites, 1, 2105376, 3, 6307872, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, 85, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bedabin Nomad", "A Bedabin nomad - they live in the harshest extremes in the desert", "", 0, 0, 3, 0, false, sprites, 1, 2105376, 3, 6307872, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 51, 150, 22, -1, -1, -1, -1, 68};
		npcs.add(new NPCDef("Captain Siad", "He's in control of the whole mining camp.", "", 48, 48, 48, 48, true, sprites, 1, 16777215, 14858776, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 48, -1, 85, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bedabin Nomad Guard", "A Bedabin nomad guard - he's protecting something important", "", 70, 70, 70, 70, true, sprites, 1, 2105376, 3, 6307872, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 117, -1, 45, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre citizen", "A denizen of Gu'Tanoth", "", 72, 33, 60, 70, true, sprites, 11550752, 6299664, 6299664, 10056486, 212, 280, 6, 6, 5, i++));
		sprites = new int[]{124, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Rock of ages", "A huge boulder", "", 150, 150, 150, 150, true, sprites, 0, 0, 0, 0, 74, 70, 11, 11, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 117, -1, 45, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Ogre", "A large dim looking humanoid", "", 72, 33, 60, 70, true, sprites, 11550752, 6299664, 6299664, 10056486, 212, 280, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 117, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Skavid", "Servant race to the ogres", "", 3, 3, 3, 3, false, sprites, 629145, 10066329, 10066329, 10066329, 96, 176, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 117, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Skavid", "Servant race to the ogres", "", 3, 3, 3, 3, false, sprites, 629145, 10066329, 10066329, 10066329, 96, 176, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, 117, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Skavid", "Servant race to the ogres", "", 3, 3, 3, 3, false, sprites, 629145, 10066329, 10066329, 10066329, 96, 176, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 49, 70, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Draft Mercenary Guard", "He's quickly drafted in to deal with trouble makers", "", 48, 60, 60, 32, true, sprites, 1, 11379585, 14858776, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, 85, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("Mining Cart Driver", "He drives the mining cart", "", 15, 16, 12, 18, false, sprites, 16760880, 16777215, 16777215, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, 70, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("kolodion", "He runs the mage arena", "", 20, 15, 3, 10, false, sprites, 1, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, 70, 76, 81, -1, -1, -1, -1};
		npcs.add(new NPCDef("kolodion", "He runs the mage arena", "", 20, 15, 3, 10, true, sprites, 1, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 89, -1, -1, -1, -1};
		npcs.add(new NPCDef("Gertrude", "A busy housewife", "", 20, 20, 20, 20, false, sprites, 16763992, 15618286, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Shilop", "A young boisterous looking lad", "", 0, 0, 3, 0, false, sprites, 14535800, 11193464, 3, 15523536, 100, 140, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 49, 70, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Rowdy Guard", "He looks as if he's spoiling for trouble", "", 48, 60, 60, 32, true, sprites, 1, 11379585, 14858776, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 49, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Shantay Pass Guard", "He seems to be guarding the Shantay Pass", "", 32, 32, 32, 32, true, sprites, 1, 11379585, 8421376, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, 213, 214, -1, -1, -1, -1};
		npcs.add(new NPCDef("Rowdy Slave", "A slave who's looking for trouble.", "", 17, 15, 16, 18, true, sprites, 16777130, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 49, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Shantay Pass Guard", "He seems to be guarding the Shantay Pass", "", 32, 32, 32, 32, false, sprites, 1, 11379585, 8421376, 11766848, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Assistant", "He is an assistant to Shantay and helps him to run the pass.", "", 0, 0, 3, 0, false, sprites, 1, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{216, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Desert Wolf", "A vicious Desert wolf", "", 30, 32, 34, 31, true, sprites, 0, 0, 0, 0, 260, 198, 6, 10, 30, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, 46, 11, 10, -1};
		npcs.add(new NPCDef("Workman", "This person is working on the site", "pickpocket", 11, 8, 7, 11, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Examiner", "As you examine the examiner you examine that she is indeed an examiner!!", "", 1, 1, 5, 1, false, sprites, 15643488, 255, 3, 15523536, 140, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 88, 46, 11, -1, -1};
		npcs.add(new NPCDef("Student", "A student busily digging!", "", 0, 0, 3, 0, false, sprites, 1, 16036851, 3, 15523536, 130, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, 46, 11, -1, -1};
		npcs.add(new NPCDef("Student", "A student busily digging!", "", 20, 20, 20, 20, false, sprites, 1, 52224, 15658576, 13415270, 140, 215, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, 46, 11, -1, -1};
		npcs.add(new NPCDef("Guide", "This person specialises in panning for gold", "", 20, 15, 3, 10, false, sprites, 1, 10053120, 6697728, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, 46, 11, -1, -1};
		npcs.add(new NPCDef("Student", "A student busily digging!", "", 20, 17, 19, 18, false, sprites, 1, 16737792, 3, 10053171, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Archaeological expert", "An expert on archaeology!", "", 20, 20, 3, 20, false, sprites, 1, 16777215, 16711680, 6697728, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, 46, 11, -1, -1};
		npcs.add(new NPCDef("civillian", "He looks aggitated!", "", 20, 17, 19, 18, true, sprites, 1, 16737792, 3, 10053171, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 88, 46, 11, -1, -1};
		npcs.add(new NPCDef("civillian", "She looks aggitated!", "", 0, 0, 3, 0, false, sprites, 1, 16036851, 3, 15523536, 130, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("civillian", "She looks aggitated!", "", 0, 0, 3, 0, false, sprites, 16777215, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("civillian", "He looks aggitated!", "pickpocket", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Murphy", "The man in charge of the fishing trawler", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Murphy", "The man in charge of the fishing trawler", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 41, -1, 52, 74, 25, -1, -1, -1, 80, 68};
		npcs.add(new NPCDef("Sir Radimus Erkle", "A huge muscular man in charge of the Legends Guild", "", 10, 20, 8, 5, false, sprites, 16777215, 13415270, 13415270, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 180, 72, 23, 94, -1, -1, -1, 215};
		npcs.add(new NPCDef("Legends Guild Guard", "This guard is protecting the entrance to the Legends Guild.", "", 50, 50, 50, 50, false, sprites, 6307872, 13415270, 13415270, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, 85, 86, -1, 211, -1, -1};
		npcs.add(new NPCDef("Escaping Mining Slave", "An emancipated slave with cool Desert Clothes.", "", 17, 15, 16, 18, false, sprites, 16777130, 8409120, 8409120, 15523536, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, 46, 11, 10, -1};
		npcs.add(new NPCDef("Workman", "This person is working in the mine", "pickpocket", 11, 8, 7, 11, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Murphy", "The man in charge of the fishing trawler", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{137, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Echned Zekin", "An evil spirit of the underworld.", "", 50, 50, 50, 50, false, sprites, 0, 0, 0, 0, 201, 243, 9, 9, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Donovan the Handyman", "It's the family odd jobs man", "", 11, 8, 7, 11, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Pierre the Dog Handler", "It's the guy who looks after the family guard dog", "", 11, 8, 7, 11, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Hobbes the Butler", "It's the family butler", "", 11, 8, 7, 11, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, 8, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Louisa The Cook", "It's the family cook", "", 0, 0, 3, 0, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, 9, -1};
		npcs.add(new NPCDef("Mary The Maid", "The family maid", "", 35, 25, 10, 30, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Stanford The Gardener", "It's the family Gardener", "", 10, 8, 7, 8, false, sprites, 1, 2, 3, 13415270, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, 71, 22, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "He looks like he's in over his head here", "", 31, 30, 22, 31, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{159, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard Dog", "He doesn't seem pleased to see me", "", 45, 47, 49, 46, false, sprites, 0, 0, 0, 0, 247, 188, 6, 10, 30, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Guard", "***EMPTY PLEASE USE OR REPLACE***", "", 10, 8, 7, 8, false, sprites, 1, 2, 3, 13415270, 155, 230, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Man", "A thirsty looking man", "", 11, 8, 7, 11, false, sprites, 1, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Anna Sinclair", "The first child of the late Lord Sinclair", "", 11, 8, 7, 11, false, sprites, 1, 16711680, 65280, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Bob Sinclair", "The second child of the late Lord Sinclair", "", 11, 8, 7, 11, false, sprites, 1, 16711680, 16711680, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Carol Sinclair", "The third child of the late Lord Sinclair", "", 11, 8, 7, 11, false, sprites, 1, 255, 16711680, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("David Sinclair", "The fourth child of the late Lord Sinclair", "", 11, 8, 7, 11, false, sprites, 1, 65280, 65280, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Elizabeth Sinclair", "The fifth child of the late Lord Sinclair", "", 11, 8, 7, 11, false, sprites, 1, 65280, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Frank Sinclair", "The sixth child of the late Lord Sinclair", "", 11, 8, 7, 11, false, sprites, 1, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, -1, -1, -1, 45, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("kolodion", "He's a shape shifter", "", 72, 55, 65, 70, true, sprites, 11550752, 6299664, 6299664, 10056486, 212, 280, 6, 6, 5, i++));
		sprites = new int[]{125, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("kolodion", "He's a shape shifter", "", 47, 69, 78, 78, true, sprites, 0, 0, 0, 0, 420, 404, 6, 6, 5, i++));
		sprites = new int[]{210, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("kolodion", "He's a shape shifter", "", 58, 28, 78, 23, true, sprites, 0, 0, 0, 0, 270, 390, 12, 12, 5, i++));
		sprites = new int[]{164, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("kolodion", "He's a shape shifter", "", 105, 85, 107, 98, true, sprites, 0, 0, 0, 0, 358, 341, 11, 11, 30, i++));
		sprites = new int[]{-1, -1, -1, 100, 50, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Irvig Senay", "The animated spirit of Irvig Senay", "", 125, 125, 125, 125, true, sprites, 16760880, 8409120, 8409120, 10056486, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{-1, -1, -1, 102, 52, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ranalph Devere", "The animated spirit of Ranalph Devere", "", 130, 130, 130, 130, true, sprites, 16760880, 8409120, 8409120, 10056486, 120, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Poison Salesman", "Peter Potter - Poison Purveyor", "", 9, 8, 5, 7, false, sprites, 16711680, 2, 3, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{7, 1, 2, 97, 219, -1, 221, 220, -1, -1, -1, -1};
		npcs.add(new NPCDef("Gujuo", "A tall charismatic looking jungle native - he approaches with confidence", "", 60, 60, 60, 60, false, sprites, 0, 9461792, 9461792, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, 108, -1, -1, -1, 46, -1, -1, 65};
		npcs.add(new NPCDef("Jungle Forester", "A woodsman who specialises in large and exotic timber", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 122, -1, -1, 85, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ungadulu", "An ancient looking Shaman", "", 75, 75, 75, 75, true, sprites, 12632256, 7296823, 7296823, 7296823, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, 180, -1, -1, 85, 86, -1, -1, -1, -1};
		npcs.add(new NPCDef("Ungadulu", "An ancient looking Shaman - he looks very strange with glowing red eyes...", "", 75, 75, 75, 75, true, sprites, 16728064, 7296823, 7296823, 7296823, 148, 224, 6, 6, 5, i++));
		sprites = new int[]{138, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Death Wing", "A supernatural creature of the underworld", "", 80, 80, 80, 80, true, sprites, 0, 0, 0, 0, 225, 195, 5, 3, 5, i++));
		sprites = new int[]{164, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Nezikchened", "An ancient powerful Demon of the Underworld...", "", 175, 177, 160, 178, true, sprites, 0, 0, 0, 0, 358, 341, 11, 11, 30, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Dwarf Cannon engineer", "He's the head of black guard weapon development", "", 20, 17, 16, 20, false, sprites, 7360576, 3158064, 3158064, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, 63};
		npcs.add(new NPCDef("Dwarf commander", "He guards the mines", "", 20, 17, 16, 20, false, sprites, 7360576, 3158064, 3158064, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{-1, 1, 2, 122, -1, 77, 76, 81, 155, -1, -1, 64};
		npcs.add(new NPCDef("Viyeldi", "The spirit of a dead sorcerer", "", 80, 80, 80, 80, true, sprites, 1, 255, 255, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Nurmof", "He sells pickaxes", "", 20, 17, 16, 20, false, sprites, 7360576, 9465888, 13393952, 15523536, 121, 176, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Fatigue expert", "He looks wide awake", "", 10, 10, 13, 8, false, sprites, 16760880, 8409120, 8409120, 15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{222, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Karamja Wolf", "A hungry", "", 61, 61, 61, 61, true, sprites, 0, 0, 0, 0, 260, 198, 6, 10, 30, i++));
		sprites = new int[]{5, 1, 2, -1, 180, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Jungle Savage", "A savage and fearless Jungle warrior", "", 100, 60, 90, 100, true, sprites, 1, 9461792, 9461792, 7360528, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{223, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Oomlie Bird", "A variety of flightless jungle fowl - it has a sharp beak and a bad temper.", "", 50, 20, 40, 20, true, sprites, 0, 0, 0, 0, 70, 62, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Sidney Smith", "Sidney Smith - Certification clerk", "", 30, 30, 30, 30, false, sprites, 0, 16711935, 16744703, 8404992, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Siegfried Erkle", "An eccentric shop keeper - related to the Grand Vizier of the Legends Guild", "", 35, 25, 10, 30, false, sprites, 16753488, 14518442, 3, 13415270, 145, 235, 6, 6, 5, i++));
		sprites = new int[]{5, 1, 2, -1, -1, -1, -1, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Tea seller", "He has delicious tea to buy", "", 11, 8, 7, 11, false, sprites, 1, 2, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Wilough", "A young son of gertrudes", "", 0, 0, 3, 0, false, sprites, 14535800, 11193464, 3, 15523536, 100, 120, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Philop", "Gertrudes youngest", "", 0, 0, 3, 0, false, sprites, 14535800, 11193464, 3, 15523536, 80, 100, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, 45, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Kanel", "Gertrudes youngest's twin brother", "", 0, 0, 3, 0, false, sprites, 14535800, 11193464, 3, 15523536, 80, 100, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("chamber guardian", "He hasn't seen much sun latley", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 41, -1, 52, 74, 25, -1, -1, -1, 80, 68};
		npcs.add(new NPCDef("Sir Radimus Erkle", "A huge muscular man in charge of the Legends Guild", "", 10, 20, 8, 5, false, sprites, 16777215, 13415270, 13415270, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{143, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		npcs.add(new NPCDef("Pit Scorpion", "Very vicious little scorpions", "", 33, 30, 32, 48, true, sprites, 0, 0, 0, 0, 121, 69, 7, 7, 45, i++));
		sprites = new int[]{3, 1, 2, 53, -1, -1, 227, -1, -1, 226, -1, -1};
		npcs.add(new NPCDef("Shadow Warrior", "A sinsistar shadowy figure", "", 61, 68, 67, 61, true, sprites, 1, 2, 3, 4, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, 186, -1, 207, -1, -1};
		npcs.add(new NPCDef("Fionella", "She runs the legend's general store", "", 35, 25, 10, 30, false, sprites, 16752704, 3211263, 3, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 122, 192, -1, -1, -1, -1, -1, 65};
		npcs.add(new NPCDef("Battle mage", "He kills in the name of guthix", "", 0, 90, 120, 0, true, sprites, 1, 8413216, 8409120, 36864, 100, 150, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 209, -1, 82, 88, -1, -1, -1, 62};
		npcs.add(new NPCDef("Battle mage", "He kills in the name of zamarok", "", 0, 90, 120, 0, true, sprites, 1, 2, 3, 16776944, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, 218, -1, 85, 86, -1, -1, -1, 64};
		npcs.add(new NPCDef("Battle mage", "He kills in the name of Saradomin", "", 0, 90, 120, 0, true, sprites, 3158064, 16763952, 15609986, 9461792, 145, 220, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Gundai", "He must get lonely out here", bankerOption1, bankerOption2, 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{6, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Lundail", "He sells rune stones", "", 15, 16, 12, 18, false, sprites, 11167296, 8409120, 3, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{0, 1, 2, -1, -1, -1, -1, -1, 46, -1, -1, -1};
		npcs.add(new NPCDef("Auctioneer", "He gives access to auction house", "Auction", 0, 0, 3, 0, false, sprites, 16761440, 2, 8409120, 13415270, 145, 230, 6, 6, 5, i++));
		sprites = new int[]{3, 4, 2, -1, -1, -1, -1, -1, -1, 11, -1, -1};
		npcs.add(new NPCDef("Auction Clerk", "There to help me make my auctions", "Auction", "Teleport", 15, 16, 12, 18, false, sprites, 11167296, 11141375, 11141375, 14415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[] { 3, 4, 2, -1, -1, 77, -1, -1, -1, -1, -1, -1 };
		npcs.add(new NPCDef("Subscription Vendor", "Exchange your subscription token to subscription time", "", 0, 0, 3, 0, false, sprites, 16711680, 143190, 143190,15523536, 145, 220, 6, 6, 5, i++));
		sprites = new int[] { 0, 1, 2, -1, -1, 77, -1, -1, -1, -1, -1, -1 };
		npcs.add(new NPCDef("Subscription Vendor", "Exchange your subscription token to subscription time", "", 0, 0, 3, 0, false, sprites, 16761440, 143190, 143190,15523536, 145, 230, 6, 6, 5, i++));
		sprites = new int[] { 241, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		npcs.add(new NPCDef("Gaia","The earth queen with a rotten heart", "", 78, 79, 79, 80, true, sprites, 0, 0, 0, 0, 275, 262, 11, 11, 30, i++));
		sprites = new int[] { 0, 245, 246, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		npcs.add(new NPCDef("Iron Man","An Iron Man", "Armour", 0, 0, 0, 0, false, sprites, 6751590, 0, 14, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[] { 0, 248, 249, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		npcs.add(new NPCDef("Ultimate Iron Man","An Ultimate Iron Man", "Armour", 0, 0, 0, 0, false, sprites, 11167296, 8, 14, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[] { 250, 251, 252, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		npcs.add(new NPCDef("Hardcore Iron Man","A Hardcore Iron Man", "Armour", 0, 0, 0, 0, false, sprites, 11167296, 8, 14, 13415270, 145, 220, 6, 6, 5, i++));
		sprites = new int[] { 309, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		npcs.add(new NPCDef("Greatwood","A scary hard slamming tree", "", 255, 245, 400, 300, true, sprites, 0, 0, 0, 0, 345, 410, 11, 11, 30, i++));

		/*try {
			PrintWriter printWriter = new PrintWriter(new FileOutputStream("NpcType.txt"), true);
			int count = 0;
			for(NPCDef npc : npcs) {
				//printWriter.println("UPDATE `openrsc_npcdef` SET `name`='" + npc.getName().replace("'", "''") + "',`description`='" + npc.getDescription().replace("'", "''") + "', " + (npc.getCommand().isEmpty() ? "" : "`command`='" + npc.getCommand() + "',") + "`attack`='" + npc.getAtt() + "',`strength`='" + npc.getStr() + "',`hits`='" + npc.getHits() + "',`defense`='" + npc.getDef() + "',`combatlvl`='" + npcCombat + "',`attackable`=" + (npc.isAttackable() ? "'1'" : "'0'") + ", `sprites1`='" + npc.sprites[0] + "',`sprites2`='" + npc.sprites[1] + "',`sprites3`='" + npc.sprites[2] + "',`sprites4`='" + npc.sprites[3] + "',`sprites5`='" + npc.sprites[4] + "',`sprites6`='" + npc.sprites[5] + "',`sprites7`='" + npc.sprites[6] + "',`sprites8`='" + npc.sprites[7] + "',`sprites9`='" + npc.sprites[8] + "',`sprites10`='" + npc.sprites[9] + "',`sprites11`='" + npc.sprites[10] + "',`sprites12`='" + npc.sprites[11] + "', `hairColour`='" + npc.getHairColour() + "',`topColour`='" + npc.getTopColour() + "', `bottomColour`='" + npc.bottomColour + "',`skinColour`='" + npc.getSkinColour() + "',`camera1`='" + npc.getCamera1() + "',`camera2`='" + npc.getCamera2() + "',`walkModel`='" + npc.getWalkModel() + "',`combatModel`='" + npc.getCombatModel() + "',`combatSprite`='" + npc.getCombatSprite() + "' WHERE `id`='" + count + "';");
				printWriter.println("NPC: " + npc.getName() + " | ID: " + count);
				printWriter.flush();
				printWriter.println("UPDATE `openrsc_npcdef` SET `isMembers`='?' WHERE `id`='" + count + "';");
				printWriter.flush();
				count++;
			}
			printWriter.close();
			System.out.println("NPCS TOTAL: " + count);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
	}

	private static void loadItemDefinitions() {
		items.add(new ItemDef("Iron Mace", "A spiky mace", "", 63, 0, "items:0", false, true, 16, 15654365, false, false, 1290, -1, 0));
		items.add(new ItemDef("Iron Short Sword", "A razor sharp sword", "", 91, 1, "items:1", false, true, 16, 15654365, false, false, 1291, -1, 1));
		items.add(new ItemDef("Iron Kite Shield", "A large metal shield", "", 238, 2, "items:2", false, true, 8, 15654365, false, false, 1292, -1, 2));
		items.add(new ItemDef("Iron Square Shield", "A medium metal shield", "", 168, 3, "items:3", false, true, 8, 15654365, false, false, 1293, -1, 3));
		items.add(new ItemDef("Wooden Shield", "A solid wooden shield", "", 20, 4, "items:4", false, true, 8, 0, false, false, 1294, -1, 4));
		items.add(new ItemDef("Medium Iron Helmet", "A medium sized helmet", "", 84, 5, "items:5", false, true, 32, 15654365, false, false, 1295, -1, 5));
		items.add(new ItemDef("Large Iron Helmet", "A full face helmet", "", 154, 6, "items:6", false, true, 33, 15654365, false, false, 1296, -1, 6));
		items.add(new ItemDef("Iron Chain Mail Body", "A series of connected metal rings", "", 210, 7, "items:7", false, true, 64, 15654365, false, false, 1297, -1, 7));
		items.add(new ItemDef("Iron Plate Mail Body", "Provides excellent protection", "", 560, 8, "items:8", false, true, 322, 15654365, false, false, 1298, -1, 8));
		items.add(new ItemDef("Iron Plate Mail Legs", "These look pretty heavy", "", 280, 9, "items:9", false, true, 644, 15654365, false, false, 1299, -1, 9));
		items.add(new ItemDef("Coins", "Lovely money!", "", 1, 10, "items:10", true, false, 0, 0, false, false, -1, -1, 10));
		items.add(new ItemDef("Bronze Arrows", "Arrows with bronze heads", "", 2, 11, "items:11", true, false, 0, 16737817, false, false, -1, -1, 11));
		items.add(new ItemDef("Iron Axe", "A woodcutters axe", "", 56, 12, "items:12", false, true, 16, 15654365, false, false, 1300, -1, 12));

		items.add(new ItemDef("Knife", "A dangerous looking knife", "", 6, 13, "items:13", false, false, 0, 0, false, false, 1301, -1, 13));
		items.add(new ItemDef("Logs", "A number of wooden logs", "", 4, 14, "items:14", false, false, 0, 0, false, false, 1302, -1, 14));
		items.add(new ItemDef("Leather Armour", "Better than no armour!", "", 21, 15, "items:15", false, true, 64, 0, false, false, 1303, -1, 15));
		items.add(new ItemDef("Leather Gloves", "These will keep my hands warm!", "", 6, 17, "items:17", false, true, 256, 0, false, false, 1304, -1, 16));
		items.add(new ItemDef("Boots", "Comfortable leather boots", "", 6, 16, "items:16", false, true, 512, 0, false, false, 1305, -1, 17));
		items.add(new ItemDef("Cabbage", "Yuck I don't like cabbage", "Eat", 1, 18, "items:18", false, false, 0, 0, false, false, 1306, -1, 18));
		items.add(new ItemDef("Egg", "A nice fresh egg", "", 4, 19, "items:19", false, false, 0, 0, false, false, 1307, -1, 19));
		items.add(new ItemDef("Bones", "Ew it's a pile of bones", "Bury", 1, 20, "items:20", false, false, 0, 0, false, false, 1308, -1, 20));
		items.add(new ItemDef("Bucket", "It's a wooden bucket", "", 2, 22, "items:22", false, false, 0, 1052688, false, false, 1309, -1, 21));
		items.add(new ItemDef("Milk", "It's a bucket of milk", "", 6, 22, "items:22", false, false, 0, 0, false, false, 1310, -1, 22));
		items.add(new ItemDef("Flour", "A little heap of flour", "", 2, 23, "items:23", false, false, 0, 0, false, true, -1, -1, 23));
		items.add(new ItemDef("Amulet of GhostSpeak", "It lets me talk to ghosts", "", 35, 24, "items:24", false, true, 1024, 0, false, true, -1, -1, 24));
		items.add(new ItemDef("Silverlight key 1", "A key given to me by Wizard Traiborn", "", 1, 25, "items:25", false, false, 0, 14540253, false, true, -1, -1, 25));
		items.add(new ItemDef("Silverlight key 2", "A key given to me by Captain Rovin", "", 1, 25, "items:25", false, false, 0, 14540253, false, true, -1, -1, 26));
		items.add(new ItemDef("skull", "A spooky looking skull", "", 1, 26, "items:26", false, false, 0, 0, false, true, -1, -1, 27));
		items.add(new ItemDef("Iron dagger", "Short but pointy", "", 35, 80, "items:80", false, true, 16, 15654365, false, false, 1311, -1, 28));
		items.add(new ItemDef("grain", "Some wheat heads", "", 2, 27, "items:27", false, false, 0, 0, false, false, 1312, -1, 29));
		items.add(new ItemDef("Book", "", "read", 1, 28, "items:28", false, false, 0, 16755370, false, true, -1, -1, 30));
		items.add(new ItemDef("Fire-Rune", "One of the 4 basic elemental runes", "", 4, 30, "items:30", true, false, 0, 0, false, false, -1, -1, 31));
		items.add(new ItemDef("Water-Rune", "One of the 4 basic elemental runes", "", 4, 31, "items:31", true, false, 0, 0, false, false, -1, -1, 32));
		items.add(new ItemDef("Air-Rune", "One of the 4 basic elemental runes", "", 4, 32, "items:32", true, false, 0, 0, false, false, -1, -1, 33));
		items.add(new ItemDef("Earth-Rune", "One of the 4 basic elemental runes", "", 4, 33, "items:33", true, false, 0, 0, false, false, -1, -1, 34));
		items.add(new ItemDef("Mind-Rune", "Used for low level missile spells", "", 3, 34, "items:34", true, false, 0, 0, false, false, -1, -1, 35));
		items.add(new ItemDef("Body-Rune", "Used for curse spells", "", 3, 35, "items:35", true, false, 0, 0, false, false, -1, -1, 36));
		items.add(new ItemDef("Life-Rune", "Used for summon spells", "", 1, 36, "items:36", true, false, 0, 0, true, false, -1, -1, 37));
		items.add(new ItemDef("Death-Rune", "Used for high level missile spells", "", 20, 37, "items:37", true, false, 0, 0, false, false, -1, -1, 38));
		items.add(new ItemDef("Needle", "Used with a thread to make clothes", "", 1, 38, "items:38", true, false, 0, 0, false, false, -1, -1, 39));
		items.add(new ItemDef("Nature-Rune", "Used for alchemy spells", "", 7, 39, "items:39", true, false, 0, 0, false, false, -1, -1, 40));
		items.add(new ItemDef("Chaos-Rune", "Used for mid level missile spells", "", 10, 40, "items:40", true, false, 0, 0, false, false, -1, -1, 41));
		items.add(new ItemDef("Law-Rune", "Used for teleport spells", "", 12, 41, "items:41", true, false, 0, 0, false, false, -1, -1, 42));
		items.add(new ItemDef("Thread", "Used with a needle to make clothes", "", 1, 42, "items:42", true, false, 0, 0, false, false, -1, -1, 43));
		items.add(new ItemDef("Holy Symbol of saradomin", "This needs a string putting on it", "", 200, 43, "items:43", false, false, 0, 0, false, false, 1313, -1, 44));
		items.add(new ItemDef("Unblessed Holy Symbol", "This needs blessing", "", 200, 44, "items:44", false, true, 1024, 0, false, false, 1314, -1, 45));
		items.add(new ItemDef("Cosmic-Rune", "Used for enchant spells", "", 15, 45, "items:45", true, false, 0, 0, false, false, -1, -1, 46));
		items.add(new ItemDef("key", "The key to get into the phoenix gang", "", 1, 25, "items:25", false, false, 0, 15636736, false, true, -1, -1, 47));
		items.add(new ItemDef("key", "The key to the phoenix gang's weapons store", "", 1, 25, "items:25", false, false, 0, 15636736, false, false, 1315, -1, 48));
		items.add(new ItemDef("scroll", "An intelligence Report", "", 5, 29, "items:29", false, false, 0, 0, false, false, 1316, -1, 49));
		items.add(new ItemDef("Water", "It's a bucket of water", "", 6, 22, "items:22", false, false, 0, 5724145, false, false, 1317, -1, 50));
		items.add(new ItemDef("Silverlight key 3", "A key I found in a drain", "", 1, 25, "items:25", false, false, 0, 14540253, false, true, -1, -1, 51));
		items.add(new ItemDef("Silverlight", "A magic sword", "", 50, 81, "items:81", false, true, 16, 0, false, false, 1318, -1, 52));
		items.add(new ItemDef("Broken shield", "Half of the shield of Arrav", "", 1, 46, "items:46", false, false, 0, 0, false, false, 1319, -1, 53));
		items.add(new ItemDef("Broken shield", "Half of the shield of Arrav", "", 1, 47, "items:47", false, false, 0, 0, false, false, 1320, -1, 54));
		items.add(new ItemDef("Cadavaberries", "Poisonous berries", "", 1, 21, "items:21", false, false, 0, 15161265, false, false, 1321, -1, 55));
		items.add(new ItemDef("message", "A message from Juliet to Romeo", "", 1, 29, "items:29", false, false, 0, 0, false, false, 1322, -1, 56));
		items.add(new ItemDef("Cadava", "I'm meant to give this to Juliet", "", 1, 48, "items:48", false, false, 0, 11620466, false, true, -1, -1, 57));
		items.add(new ItemDef("potion", "this is meant to be good for spots", "", 1, 48, "items:48", false, false, 0, 5289585, false, false, 1323, -1, 58));
		items.add(new ItemDef("Phoenix Crossbow", "Former property of the phoenix gang", "", 4, 49, "items:49", false, true, 16, 0, false, false, 1324, -1, 59));
		items.add(new ItemDef("Crossbow", "This fires crossbow bolts", "", 70, 49, "items:49", false, true, 16, 0, false, false, 1325, -1, 60));
		items.add(new ItemDef("Certificate", "I can use this to claim a reward from the king", "", 1, 29, "items:29", false, false, 0, 0, false, false, 1326, -1, 61));
		items.add(new ItemDef("bronze dagger", "Short but pointy", "", 10, 80, "items:80", false, true, 16, 16737817, false, false, 1327, -1, 62));
		items.add(new ItemDef("Steel dagger", "Short but pointy", "", 125, 80, "items:80", false, true, 16, 15658734, false, false, 1328, -1, 63));
		items.add(new ItemDef("Mithril dagger", "Short but pointy", "", 325, 80, "items:80", false, true, 16, 10072780, false, false, 1329, -1, 64));
		items.add(new ItemDef("Adamantite dagger", "Short but pointy", "", 800, 80, "items:80", false, true, 16, 11717785, false, false, 1330, -1, 65));
		items.add(new ItemDef("Bronze Short Sword", "A razor sharp sword", "", 26, 1, "items:1", false, true, 16, 16737817, false, false, 1331, -1, 66));
		items.add(new ItemDef("Steel Short Sword", "A razor sharp sword", "", 325, 1, "items:1", false, true, 16, 15658734, false, false, 1332, -1, 67));
		items.add(new ItemDef("Mithril Short Sword", "A razor sharp sword", "", 845, 1, "items:1", false, true, 16, 10072780, false, false, 1333, -1, 68));
		items.add(new ItemDef("Adamantite Short Sword", "A razor sharp sword", "", 2080, 1, "items:1", false, true, 16, 11717785, false, false, 1334, -1, 69));
		items.add(new ItemDef("Bronze Long Sword", "A razor sharp sword", "", 40, 81, "items:81", false, true, 16, 16737817, false, false, 1335, -1, 70));
		items.add(new ItemDef("Iron Long Sword", "A razor sharp sword", "", 140, 81, "items:81", false, true, 16, 15654365, false, false, 1336, -1, 71));
		items.add(new ItemDef("Steel Long Sword", "A razor sharp sword", "", 500, 81, "items:81", false, true, 16, 15658734, false, false, 1337, -1, 72));
		items.add(new ItemDef("Mithril Long Sword", "A razor sharp sword", "", 1300, 81, "items:81", false, true, 16, 10072780, false, false, 1338, -1, 73));
		items.add(new ItemDef("Adamantite Long Sword", "A razor sharp sword", "", 3200, 81, "items:81", false, true, 16, 11717785, false, false, 1339, -1, 74));
		items.add(new ItemDef("Rune long sword", "A razor sharp sword", "", 32000, 81, "items:81", false, true, 16, 65535, false, false, 1340, -1, 75));
		items.add(new ItemDef("Bronze 2-handed Sword", "A very large sword", "", 80, 82, "items:82", false, true, 8216, 16737817, false, false, 1341, -1, 76));
		items.add(new ItemDef("Iron 2-handed Sword", "A very large sword", "", 280, 82, "items:82", false, true, 8216, 15654365, false, false, 1342, -1, 77));
		items.add(new ItemDef("Steel 2-handed Sword", "A very large sword", "", 1000, 82, "items:82", false, true, 8216, 15658734, false, false, 1343, -1, 78));
		items.add(new ItemDef("Mithril 2-handed Sword", "A very large sword", "", 2600, 82, "items:82", false, true, 8216, 10072780, false, false, 1344, -1, 79));
		items.add(new ItemDef("Adamantite 2-handed Sword", "A very large sword", "", 6400, 82, "items:82", false, true, 8216, 11717785, false, false, 1345, -1, 80));
		items.add(new ItemDef("rune 2-handed Sword", "A very large sword", "", 64000, 82, "items:82", false, true, 8216, 65535, false, false, 1346, -1, 81));
		items.add(new ItemDef("Bronze Scimitar", "A vicious curved sword", "", 32, 83, "items:83", false, true, 16, 16737817, false, false, 1347, -1, 82));
		items.add(new ItemDef("Iron Scimitar", "A vicious curved sword", "", 112, 83, "items:83", false, true, 16, 15654365, false, false, 1348, -1, 83));
		items.add(new ItemDef("Steel Scimitar", "A vicious curved sword", "", 400, 83, "items:83", false, true, 16, 15658734, false, false, 1349, -1, 84));
		items.add(new ItemDef("Mithril Scimitar", "A vicious curved sword", "", 1040, 83, "items:83", false, true, 16, 10072780, false, false, 1350, -1, 85));
		items.add(new ItemDef("Adamantite Scimitar", "A vicious curved sword", "", 2560, 83, "items:83", false, true, 16, 11717785, false, false, 1351, -1, 86));
		items.add(new ItemDef("bronze Axe", "A woodcutters axe", "", 16, 12, "items:12", false, true, 16, 16737817, false, false, 1352, -1, 87));
		items.add(new ItemDef("Steel Axe", "A woodcutters axe", "", 200, 12, "items:12", false, true, 16, 15658734, false, false, 1353, -1, 88));
		items.add(new ItemDef("Iron battle Axe", "A vicious looking axe", "", 182, 84, "items:84", false, true, 16, 15654365, false, false, 1354, -1, 89));
		items.add(new ItemDef("Steel battle Axe", "A vicious looking axe", "", 650, 84, "items:84", false, true, 16, 15658734, false, false, 1355, -1, 90));
		items.add(new ItemDef("Mithril battle Axe", "A vicious looking axe", "", 1690, 84, "items:84", false, true, 16, 10072780, false, false, 1356, -1, 91));
		items.add(new ItemDef("Adamantite battle Axe", "A vicious looking axe", "", 4160, 84, "items:84", false, true, 16, 11717785, false, false, 1357, -1, 92));
		items.add(new ItemDef("Rune battle Axe", "A vicious looking axe", "", 41600, 84, "items:84", false, true, 16, 65535, false, false, 1358, -1, 93));
		items.add(new ItemDef("Bronze Mace", "A spiky mace", "", 18, 0, "items:0", false, true, 16, 16737817, false, false, 1359, -1, 94));
		items.add(new ItemDef("Steel Mace", "A spiky mace", "", 225, 0, "items:0", false, true, 16, 15658734, false, false, 1360, -1, 95));
		items.add(new ItemDef("Mithril Mace", "A spiky mace", "", 585, 0, "items:0", false, true, 16, 10072780, false, false, 1361, -1, 96));
		items.add(new ItemDef("Adamantite Mace", "A spiky mace", "", 1440, 0, "items:0", false, true, 16, 11717785, false, false, 1362, -1, 97));
		items.add(new ItemDef("Rune Mace", "A spiky mace", "", 14400, 0, "items:0", false, true, 16, 65535, false, false, 1363, -1, 98));
		items.add(new ItemDef("Brass key", "I wonder what this is the key to", "", 1, 25, "items:25", false, false, 0, 16750848, false, false, 1364, -1, 99));
		items.add(new ItemDef("staff", "It's a slightly magical stick", "", 15, 85, "items:85", false, true, 16, 10072780, false, false, 1365, -1, 100));
		items.add(new ItemDef("Staff of Air", "A Magical staff", "", 1500, 91, "items:91", false, true, 16, 65535, false, false, 1366, -1, 101));
		items.add(new ItemDef("Staff of water", "A Magical staff", "", 1500, 91, "items:91", false, true, 16, 255, false, false, 1367, -1, 102));
		items.add(new ItemDef("Staff of earth", "A Magical staff", "", 1500, 91, "items:91", false, true, 16, 7353600, false, false, 1368, -1, 103));
		items.add(new ItemDef("Medium Bronze Helmet", "A medium sized helmet", "", 24, 5, "items:5", false, true, 32, 16737817, false, false, 1369, -1, 104));
		items.add(new ItemDef("Medium Steel Helmet", "A medium sized helmet", "", 300, 5, "items:5", false, true, 32, 15658734, false, false, 1370, -1, 105));
		items.add(new ItemDef("Medium Mithril Helmet", "A medium sized helmet", "", 780, 5, "items:5", false, true, 32, 10072780, false, false, 1371, -1, 106));
		items.add(new ItemDef("Medium Adamantite Helmet", "A medium sized helmet", "", 1920, 5, "items:5", false, true, 32, 11717785, false, false, 1372, -1, 107));
		items.add(new ItemDef("Large Bronze Helmet", "A full face helmet", "", 44, 6, "items:6", false, true, 33, 16737817, false, false, 1373, -1, 108));
		items.add(new ItemDef("Large Steel Helmet", "A full face helmet", "", 550, 6, "items:6", false, true, 33, 15658734, false, false, 1374, -1, 109));
		items.add(new ItemDef("Large Mithril Helmet", "A full face helmet", "", 1430, 6, "items:6", false, true, 33, 10072780, false, false, 1375, -1, 110));
		items.add(new ItemDef("Large Adamantite Helmet", "A full face helmet", "", 3520, 6, "items:6", false, true, 33, 11717785, false, false, 1376, -1, 111));
		items.add(new ItemDef("Large Rune Helmet", "A full face helmet", "", 35200, 6, "items:6", false, true, 33, 65535, false, false, 1377, -1, 112));
		items.add(new ItemDef("Bronze Chain Mail Body", "A series of connected metal rings", "", 60, 7, "items:7", false, true, 64, 16737817, false, false, 1378, -1, 113));
		items.add(new ItemDef("Steel Chain Mail Body", "A series of connected metal rings", "", 750, 7, "items:7", false, true, 64, 15658734, false, false, 1379, -1, 114));
		items.add(new ItemDef("Mithril Chain Mail Body", "A series of connected metal rings", "", 1950, 7, "items:7", false, true, 64, 10072780, false, false, 1380, -1, 115));
		items.add(new ItemDef("Adamantite Chain Mail Body", "A series of connected metal rings", "", 4800, 7, "items:7", false, true, 64, 11717785, false, false, 1381, -1, 116));
		items.add(new ItemDef("Bronze Plate Mail Body", "Provides excellent protection", "", 160, 8, "items:8", false, true, 322, 16737817, false, false, 1382, -1, 117));
		items.add(new ItemDef("Steel Plate Mail Body", "Provides excellent protection", "", 2000, 8, "items:8", false, true, 322, 15658734, false, false, 1383, -1, 118));
		items.add(new ItemDef("Mithril Plate Mail Body", "Provides excellent protection", "", 5200, 8, "items:8", false, true, 322, 10072780, false, false, 1384, -1, 119));
		items.add(new ItemDef("Adamantite Plate Mail Body", "Provides excellent protection", "", 12800, 8, "items:8", false, true, 322, 11717785, false, false, 1385, -1, 120));
		items.add(new ItemDef("Steel Plate Mail Legs", "These look pretty heavy", "", 1000, 9, "items:9", false, true, 644, 15658734, false, false, 1386, -1, 121));
		items.add(new ItemDef("Mithril Plate Mail Legs", "These look pretty heavy", "", 2600, 9, "items:9", false, true, 644, 10072780, false, false, 1387, -1, 122));
		items.add(new ItemDef("Adamantite Plate Mail Legs", "These look pretty heavy", "", 6400, 9, "items:9", false, true, 644, 11717785, false, false, 1388, -1, 123));
		items.add(new ItemDef("Bronze Square Shield", "A medium metal shield", "", 48, 3, "items:3", false, true, 8, 16737817, false, false, 1389, -1, 124));
		items.add(new ItemDef("Steel Square Shield", "A medium metal shield", "", 600, 3, "items:3", false, true, 8, 15658734, false, false, 1390, -1, 125));
		items.add(new ItemDef("Mithril Square Shield", "A medium metal shield", "", 1560, 3, "items:3", false, true, 8, 10072780, false, false, 1391, -1, 126));
		items.add(new ItemDef("Adamantite Square Shield", "A medium metal shield", "", 3840, 3, "items:3", false, true, 8, 11717785, false, false, 1392, -1, 127));
		items.add(new ItemDef("Bronze Kite Shield", "A large metal shield", "", 68, 2, "items:2", false, true, 8, 16737817, false, false, 1393, -1, 128));
		items.add(new ItemDef("Steel Kite Shield", "A large metal shield", "", 850, 2, "items:2", false, true, 8, 15658734, false, false, 1394, -1, 129));
		items.add(new ItemDef("Mithril Kite Shield", "A large metal shield", "", 2210, 2, "items:2", false, true, 8, 10072780, false, false, 1395, -1, 130));
		items.add(new ItemDef("Adamantite Kite Shield", "A large metal shield", "", 5440, 2, "items:2", false, true, 8, 11717785, false, false, 1396, -1, 131));
		items.add(new ItemDef("cookedmeat", "Mmm this looks tasty", "Eat", 4, 60, "items:60", false, false, 0, 13395507, false, false, 1397, -1, 132));
		items.add(new ItemDef("raw chicken", "I need to cook this first", "", 1, 60, "items:60", false, false, 0, 16747571, false, false, 1398, -1, 133));
		items.add(new ItemDef("burntmeat", "Oh dear", "", 1, 60, "items:60", false, false, 0, 5000268, false, false, 1399, -1, 134));
		items.add(new ItemDef("pot", "This pot is empty", "", 1, 61, "items:61", false, false, 0, 16748885, false, false, 1400, -1, 135));
		items.add(new ItemDef("flour", "There is flour in this pot", "", 10, 62, "items:62", false, false, 0, 0, false, false, 1401, -1, 136));
		items.add(new ItemDef("bread dough", "Some uncooked dough", "", 1, 63, "items:63", false, false, 0, 0, false, false, 1402, -1, 137));
		items.add(new ItemDef("bread", "Nice crispy bread", "Eat", 12, 64, "items:64", false, false, 0, 16739379, false, false, 1403, -1, 138));
		items.add(new ItemDef("burntbread", "This bread is ruined!", "", 1, 64, "items:64", false, false, 0, 5000268, false, false, 1404, -1, 139));
		items.add(new ItemDef("jug", "This jug is empty", "", 1, 65, "items:65", false, false, 0, 65856, false, false, 1405, -1, 140));
		items.add(new ItemDef("water", "It's full of water", "", 1, 65, "items:65", false, false, 0, 12632319, false, false, 1406, -1, 141));
		items.add(new ItemDef("wine", "It's full of wine", "Drink", 1, 65, "items:65", false, false, 0, 12851224, false, false, 1407, -1, 142));
		items.add(new ItemDef("grapes", "Good grapes for wine making", "", 1, 21, "items:21", false, false, 0, 9386967, false, false, 1408, -1, 143));
		items.add(new ItemDef("shears", "For shearing sheep", "", 1, 66, "items:66", false, false, 0, 0, false, false, 1409, -1, 144));
		items.add(new ItemDef("wool", "I think this came from a sheep", "", 1, 67, "items:67", false, false, 0, 0, false, false, 1410, -1, 145));
		items.add(new ItemDef("fur", "This would make warm clothing", "", 10, 68, "items:68", false, false, 0, 12288534, false, false, 1411, -1, 146));
		items.add(new ItemDef("cow hide", "I should take this to the tannery", "", 1, 69, "items:69", false, false, 0, 0, false, false, 1412, -1, 147));
		items.add(new ItemDef("leather", "It's a piece of leather", "", 1, 69, "items:69", false, false, 0, 16757299, false, false, 1413, -1, 148));
		items.add(new ItemDef("clay", "Some hard dry clay", "", 1, 70, "items:70", false, false, 0, 15046937, false, false, 1414, -1, 149));
		items.add(new ItemDef("copper ore", "this needs refining", "", 3, 70, "items:70", false, false, 0, 16737817, false, false, 1415, -1, 150));
		items.add(new ItemDef("iron ore", "this needs refining", "", 17, 70, "items:70", false, false, 0, 11704729, false, false, 1416, -1, 151));
		items.add(new ItemDef("gold", "this needs refining", "", 150, 73, "items:73", false, false, 0, 16763980, false, false, 1417, -1, 152));
		items.add(new ItemDef("mithril ore", "this needs refining", "", 162, 70, "items:70", false, false, 0, 10072780, false, false, 1418, -1, 153));
		items.add(new ItemDef("adamantite ore", "this needs refining", "", 400, 70, "items:70", false, false, 0, 11717785, false, false, 1419, -1, 154));
		items.add(new ItemDef("coal", "hmm a non-renewable energy source!", "", 45, 71, "items:71", false, false, 0, 0, false, false, 1420, -1, 155));
		items.add(new ItemDef("Bronze Pickaxe", "Used for mining", "", 1, 72, "items:72", false, false, 0, 16737817, false, false, 1421, -1, 156));
		items.add(new ItemDef("uncut diamond", "this would be worth more cut", "", 200, 73, "items:73", false, false, 0, 0, false, false, 1422, -1, 157));
		items.add(new ItemDef("uncut ruby", "this would be worth more cut", "", 100, 73, "items:73", false, false, 0, 16724736, false, false, 1423, -1, 158));
		items.add(new ItemDef("uncut emerald", "this would be worth more cut", "", 50, 73, "items:73", false, false, 0, 3394611, false, false, 1424, -1, 159));
		items.add(new ItemDef("uncut sapphire", "this would be worth more cut", "", 25, 73, "items:73", false, false, 0, 19711, false, false, 1425, -1, 160));
		items.add(new ItemDef("diamond", "this looks valuable", "", 2000, 74, "items:74", false, false, 0, 0, false, false, 1426, -1, 161));
		items.add(new ItemDef("ruby", "this looks valuable", "", 1000, 74, "items:74", false, false, 0, 16724736, false, false, 1427, -1, 162));
		items.add(new ItemDef("emerald", "this looks valuable", "", 500, 74, "items:74", false, false, 0, 3394611, false, false, 1428, -1, 163));
		items.add(new ItemDef("sapphire", "this looks valuable", "", 250, 74, "items:74", false, false, 0, 19711, false, false, 1429, -1, 164));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, false, 1430, -1, 165));
		items.add(new ItemDef("tinderbox", "useful for lighting a fire", "", 1, 76, "items:76", false, false, 0, 0, false, false, 1431, -1, 166));
		items.add(new ItemDef("chisel", "good for detailed crafting", "", 1, 77, "items:77", false, false, 0, 0, false, false, 1432, -1, 167));
		items.add(new ItemDef("hammer", "good for hitting things!", "", 1, 78, "items:78", false, false, 0, 0, false, false, 1433, -1, 168));
		items.add(new ItemDef("bronze bar", "it's a bar of bronze", "", 8, 79, "items:79", false, false, 0, 16737817, false, false, 1434, -1, 169));
		items.add(new ItemDef("iron bar", "it's a bar of iron", "", 28, 79, "items:79", false, false, 0, 15654365, false, false, 1435, -1, 170));
		items.add(new ItemDef("steel bar", "it's a bar of steel", "", 100, 79, "items:79", false, false, 0, 15658734, false, false, 1436, -1, 171));
		items.add(new ItemDef("gold bar", "this looks valuable", "", 300, 79, "items:79", false, false, 0, 16763980, false, false, 1437, -1, 172));
		items.add(new ItemDef("mithril bar", "it's a bar of mithril", "", 300, 79, "items:79", false, false, 0, 10072780, false, false, 1438, -1, 173));
		items.add(new ItemDef("adamantite bar", "it's a bar of adamantite", "", 640, 79, "items:79", false, false, 0, 11717785, false, false, 1439, -1, 174));
		items.add(new ItemDef("Pressure gauge", "It looks like part of a machine", "", 1, 50, "items:50", false, false, 0, 0, false, true, -1, -1, 175));
		items.add(new ItemDef("Fish Food", "Keeps  your pet fish strong and healthy", "", 1, 51, "items:51", false, false, 0, 0, false, false, 1440, -1, 176));
		items.add(new ItemDef("Poison", "This stuff looks nasty", "", 1, 52, "items:52", false, false, 0, 0, false, false, 1441, -1, 177));
		items.add(new ItemDef("Poisoned fish food", "Doesn't seem very nice to the poor fishes", "", 1, 51, "items:51", false, false, 0, 0, false, true, -1, -1, 178));
		items.add(new ItemDef("spinach roll", "A home made spinach thing", "Eat", 1, 53, "items:53", false, false, 0, 0, false, false, 1442, -1, 179));
		items.add(new ItemDef("Bad wine", "Oh dear", "Drink", 1, 65, "items:65", false, false, 0, 12851224, false, false, 1443, -1, 180));
		items.add(new ItemDef("Ashes", "A heap of ashes", "", 2, 23, "items:23", false, false, 0, 11184810, false, false, 1444, -1, 181));
		items.add(new ItemDef("Apron", "A mostly clean apron", "", 2, 58, "items:58", false, true, 1024, 0, false, false, 1445, -1, 182));
		items.add(new ItemDef("Cape", "A bright red cape", "", 2, 59, "items:59", false, true, 2048, 16711680, false, false, 1446, -1, 183));
		items.add(new ItemDef("Wizards robe", "I can do magic better in this", "", 15, 87, "items:87", false, true, 64, 255, false, false, 1447, -1, 184));
		items.add(new ItemDef("wizardshat", "A silly pointed hat", "", 2, 86, "items:86", false, true, 32, 255, false, false, 1448, -1, 185));
		items.add(new ItemDef("Brass necklace", "I'd prefer a gold one", "", 30, 57, "items:57", false, true, 1024, 0, false, false, 1449, -1, 186));
		items.add(new ItemDef("skirt", "A ladies skirt", "", 2, 88, "items:88", false, true, 128, 255, false, false, 1450, -1, 187));
		items.add(new ItemDef("Longbow", "A Nice sturdy bow", "", 80, 54, "items:54", false, true, 24, 65280, false, false, 1451, -1, 188));
		items.add(new ItemDef("Shortbow", "Short but effective", "", 50, 55, "items:55", false, true, 24, 65280, false, false, 1452, -1, 189));
		items.add(new ItemDef("Crossbow bolts", "Good if you have a crossbow!", "", 3, 56, "items:56", true, false, 0, 0, false, false, -1, -1, 190));
		items.add(new ItemDef("Apron", "this will help keep my clothes clean", "", 2, 58, "items:58", false, true, 1024, 9789488, false, false, 1453, -1, 191));
		items.add(new ItemDef("Chef's hat", "What a silly hat", "", 2, 89, "items:89", false, true, 32, 0, false, false, 1454, -1, 192));
		items.add(new ItemDef("Beer", "A glass of frothy ale", "drink", 2, 90, "items:90", false, false, 0, 0, false, false, 1455, -1, 193));
		items.add(new ItemDef("skirt", "A ladies skirt", "", 2, 88, "items:88", false, true, 128, 16036851, false, false, 1456, -1, 194));
		items.add(new ItemDef("skirt", "A ladies skirt", "", 2, 88, "items:88", false, true, 128, 4210752, false, false, 1457, -1, 195));
		items.add(new ItemDef("Black Plate Mail Body", "Provides excellent protection", "", 3840, 8, "items:8", false, true, 322, 3158064, false, false, 1458, -1, 196));
		items.add(new ItemDef("Staff of fire", "A Magical staff", "", 1500, 91, "items:91", false, true, 16, 16711680, false, false, 1459, -1, 197));
		items.add(new ItemDef("Magic Staff", "A Magical staff", "", 200, 91, "items:91", false, true, 16, 16777215, false, false, 1460, -1, 198));
		items.add(new ItemDef("wizardshat", "A silly pointed hat", "", 2, 86, "items:86", false, true, 32, 4210752, false, false, 1461, -1, 199));
		items.add(new ItemDef("silk", "It's a sheet of silk", "", 30, 92, "items:92", false, false, 0, 16724172, false, false, 1462, -1, 200));
		items.add(new ItemDef("flier", "Get your axes from Bob's axes", "", 1, 29, "items:29", false, false, 0, 0, false, false, 1463, -1, 201));
		items.add(new ItemDef("tin ore", "this needs refining", "", 3, 70, "items:70", false, false, 0, 13810105, false, false, 1464, -1, 202));
		items.add(new ItemDef("Mithril Axe", "A powerful axe", "", 520, 12, "items:12", false, true, 16, 10072780, false, false, 1465, -1, 203));
		items.add(new ItemDef("Adamantite Axe", "A powerful axe", "", 1280, 12, "items:12", false, true, 16, 11717785, false, false, 1466, -1, 204));
		items.add(new ItemDef("bronze battle Axe", "A vicious looking axe", "", 52, 84, "items:84", false, true, 16, 16737817, false, false, 1467, -1, 205));
		items.add(new ItemDef("Bronze Plate Mail Legs", "These look pretty heavy", "", 80, 9, "items:9", false, true, 644, 16737817, false, false, 1468, -1, 206));
		items.add(new ItemDef("Ball of wool", "Spun from sheeps wool", "", 2, 93, "items:93", false, false, 0, 0, false, false, 1469, -1, 207));
		items.add(new ItemDef("Oil can", "Its pretty full", "", 3, 94, "items:94", false, false, 0, 0, false, true, -1, -1, 208));
		items.add(new ItemDef("Cape", "A warm black cape", "", 7, 59, "items:59", false, true, 2048, 2434341, false, false, 1470, -1, 209));
		items.add(new ItemDef("Kebab", "A meaty Kebab", "eat", 3, 95, "items:95", false, false, 0, 0, false, false, 1471, -1, 210));
		items.add(new ItemDef("Spade", "A fairly small spade", "Dig", 3, 96, "items:96", false, false, 0, 0, false, false, 1472, -1, 211));
		items.add(new ItemDef("Closet Key", "A slightly smelly key", "", 1, 25, "items:25", false, false, 0, 16772608, false, true, -1, -1, 212));
		items.add(new ItemDef("rubber tube", "Its slightly charred", "", 3, 97, "items:97", false, false, 0, 0, false, true, -1, -1, 213));
		items.add(new ItemDef("Bronze Plated Skirt", "Designer leg protection", "", 80, 88, "items:88", false, true, 640, 8400921, false, false, 1473, -1, 214));
		items.add(new ItemDef("Iron Plated Skirt", "Designer leg protection", "", 280, 88, "items:88", false, true, 640, 7824998, false, false, 1474, -1, 215));
		items.add(new ItemDef("Black robe", "I can do magic better in this", "", 13, 87, "items:87", false, true, 64, 4210752, false, false, 1475, -1, 216));
		items.add(new ItemDef("stake", "A very pointy stick", "", 8, 98, "items:98", false, true, 16, 16737817, false, true, -1, -1, 217));
		items.add(new ItemDef("Garlic", "A clove of garlic", "", 3, 99, "items:99", false, false, 0, 0, false, false, 1476, -1, 218));
		items.add(new ItemDef("Red spiders eggs", "eewww", "", 7, 100, "items:100", false, false, 0, 0, false, false, 1477, -1, 219));
		items.add(new ItemDef("Limpwurt root", "the root of a limpwurt plant", "", 7, 101, "items:101", false, false, 0, 0, false, false, 1478, -1, 220));
		items.add(new ItemDef("Strength Potion", "4 doses of strength potion", "Drink", 14, 48, "items:48", false, false, 0, 15658544, false, false, 1479, -1, 221));
		items.add(new ItemDef("Strength Potion", "3 doses of strength potion", "Drink", 13, 48, "items:48", false, false, 0, 15658544, false, false, 1480, -1, 222));
		items.add(new ItemDef("Strength Potion", "2 doses of strength potion", "Drink", 13, 436, "items:436", false, false, 0, 15658544, false, false, 1481, -1, 223));
		items.add(new ItemDef("Strength Potion", "1 dose of strength potion", "Drink", 11, 437, "items:437", false, false, 0, 15658544, false, false, 1482, -1, 224));
		items.add(new ItemDef("Steel Plated skirt", "designer leg protection", "", 1000, 88, "items:88", false, true, 640, 7829367, false, false, 1483, -1, 225));
		items.add(new ItemDef("Mithril Plated skirt", "Designer Leg protection", "", 2600, 88, "items:88", false, true, 640, 2245205, false, false, 1484, -1, 226));
		items.add(new ItemDef("Adamantite Plated skirt", "Designer leg protection", "", 6400, 88, "items:88", false, true, 640, 4347170, false, false, 1485, -1, 227));
		items.add(new ItemDef("Cabbage", "Yuck I don't like cabbage", "Eat", 1, 18, "items:18", false, false, 0, 0, false, false, 1486, -1, 228));
		items.add(new ItemDef("Cape", "A thick blue cape", "", 32, 59, "items:59", false, true, 2048, 4210926, false, false, 1487, -1, 229));
		items.add(new ItemDef("Large Black Helmet", "A full face helmet", "", 1056, 6, "items:6", false, true, 33, 4210752, false, false, 1488, -1, 230));
		items.add(new ItemDef("Red Bead", "A small round red bead", "", 4, 102, "items:102", false, false, 0, 16711680, false, false, 1489, -1, 231));
		items.add(new ItemDef("Yellow Bead", "A small round yellow bead", "", 4, 102, "items:102", false, false, 0, 16776960, false, false, 1490, -1, 232));
		items.add(new ItemDef("Black Bead", "A small round black bead", "", 4, 102, "items:102", false, false, 0, 4210752, false, false, 1491, -1, 233));
		items.add(new ItemDef("White Bead", "A small round white bead", "", 4, 102, "items:102", false, false, 0, 16777215, false, false, 1492, -1, 234));
		items.add(new ItemDef("Amulet of accuracy", "It increases my aim", "", 100, 24, "items:24", false, true, 1024, 0, false, false, 1493, -1, 235));
		items.add(new ItemDef("Redberries", "Very bright red berries", "", 3, 21, "items:21", false, false, 0, 16711680, false, false, 1494, -1, 236));
		items.add(new ItemDef("Rope", "A Coil of rope", "", 18, 103, "items:103", false, false, 0, 0, false, false, 1495, -1, 237));
		items.add(new ItemDef("Reddye", "A little bottle of dye", "", 5, 104, "items:104", false, false, 0, 16711680, false, false, 1496, -1, 238));
		items.add(new ItemDef("Yellowdye", "A little bottle of dye", "", 5, 104, "items:104", false, false, 0, 16776960, false, false, 1497, -1, 239));
		items.add(new ItemDef("Paste", "A bottle off skin coloured paste", "", 5, 104, "items:104", false, false, 0, 15523008, false, true, -1, -1, 240));
		items.add(new ItemDef("Onion", "A strong smelling onion", "", 3, 99, "items:99", false, false, 0, 15641190, false, false, 1498, -1, 241));
		items.add(new ItemDef("Bronze key", "A heavy key", "", 1, 25, "items:25", false, false, 0, 16737817, false, true, -1, -1, 242));
		items.add(new ItemDef("Soft Clay", "Clay that's ready to be used", "", 2, 105, "items:105", false, false, 0, 0, false, false, 1499, -1, 243));
		items.add(new ItemDef("wig", "A blonde wig", "", 2, 106, "items:106", false, false, 0, 16763992, false, true, -1, -1, 244));
		items.add(new ItemDef("wig", "A wig made from wool", "", 2, 106, "items:106", false, false, 0, 0, false, true, -1, -1, 245));
		items.add(new ItemDef("Half full wine jug", "It's half full of wine", "Drink", 1, 65, "items:65", false, false, 0, 12851224, false, false, 1500, -1, 246));
		items.add(new ItemDef("Keyprint", "An imprint of a key in a lump of clay", "", 2, 107, "items:107", false, false, 0, 0, false, true, -1, -1, 247));
		items.add(new ItemDef("Black Plate Mail Legs", "These look pretty heavy", "", 1920, 9, "items:9", false, true, 644, 4210752, false, false, 1501, -1, 248));
		items.add(new ItemDef("banana", "Mmm this looks tasty", "Eat", 2, 108, "items:108", false, false, 0, 0, false, false, 1502, -1, 249));
		items.add(new ItemDef("pastry dough", "Some uncooked dough", "", 1, 63, "items:63", false, false, 0, 0, false, false, 1503, -1, 250));
		items.add(new ItemDef("Pie dish", "For making pies in", "", 3, 110, "items:110", false, false, 0, 15634261, false, false, 1504, -1, 251));
		items.add(new ItemDef("cooking apple", "I wonder what i can make with this", "", 1, 109, "items:109", false, false, 0, 0, false, false, 1505, -1, 252));
		items.add(new ItemDef("pie shell", "I need to find a filling for this pie", "", 1, 111, "items:111", false, false, 0, 0, false, false, 1506, -1, 253));
		items.add(new ItemDef("Uncooked apple pie", "I need to cook this first", "", 1, 112, "items:112", false, false, 0, 16633518, false, false, 1507, -1, 254));
		items.add(new ItemDef("Uncooked meat pie", "I need to cook this first", "", 1, 112, "items:112", false, false, 0, 16633518, false, false, 1508, -1, 255));
		items.add(new ItemDef("Uncooked redberry pie", "I need to cook this first", "", 1, 112, "items:112", false, false, 0, 16633518, false, false, 1509, -1, 256));
		items.add(new ItemDef("apple pie", "Mmm Apple pie", "eat", 30, 112, "items:112", false, false, 0, 11168819, false, false, 1510, -1, 257));
		items.add(new ItemDef("Redberry pie", "Looks tasty", "eat", 12, 112, "items:112", false, false, 0, 11168819, false, false, 1511, -1, 258));
		items.add(new ItemDef("meat pie", "Mighty and meaty", "eat", 15, 112, "items:112", false, false, 0, 11168819, false, false, 1512, -1, 259));
		items.add(new ItemDef("burntpie", "Oops", "empty dish", 1, 112, "items:112", false, false, 0, 5000268, false, false, 1513, -1, 260));
		items.add(new ItemDef("Half a meat pie", "Mighty and meaty", "eat", 10, 113, "items:113", false, false, 0, 11168819, false, false, 1514, -1, 261));
		items.add(new ItemDef("Half a Redberry pie", "Looks tasty", "eat", 4, 113, "items:113", false, false, 0, 11168819, false, false, 1515, -1, 262));
		items.add(new ItemDef("Half an apple pie", "Mmm Apple pie", "eat", 5, 113, "items:113", false, false, 0, 11168819, false, false, 1516, -1, 263));
		items.add(new ItemDef("Portrait", "It's a picture of a knight", "", 3, 114, "items:114", false, false, 0, 0, false, true, -1, -1, 264));
		items.add(new ItemDef("Faladian Knight's sword", "A razor sharp sword", "", 200, 115, "items:115", false, true, 16, 15654365, false, true, -1, -1, 265));
		items.add(new ItemDef("blurite ore", "What Strange stuff", "", 3, 70, "items:70", false, false, 0, 5263598, false, true, -1, -1, 266));
		items.add(new ItemDef("Asgarnian Ale", "A glass of frothy ale", "drink", 2, 90, "items:90", false, false, 0, 0, false, false, 1517, -1, 267));
		items.add(new ItemDef("Wizard's Mind Bomb", "It's got strange bubbles in it", "drink", 2, 90, "items:90", false, false, 0, 0, false, false, 1518, -1, 268));
		items.add(new ItemDef("Dwarven Stout", "A Pint of thick dark beer", "drink", 2, 90, "items:90", false, false, 0, 0, false, false, 1519, -1, 269));
		items.add(new ItemDef("Eye of newt", "It seems to be looking at me", "", 3, 116, "items:116", false, false, 0, 0, false, false, 1520, -1, 270));
		items.add(new ItemDef("Rat's tail", "A bit of rat", "", 3, 117, "items:117", false, false, 0, 0, false, true, -1, -1, 271));
		items.add(new ItemDef("Bluedye", "A little bottle of dye", "", 5, 104, "items:104", false, false, 0, 255, false, false, 1521, -1, 272));
		items.add(new ItemDef("Goblin Armour", "Armour Designed to fit Goblins", "", 40, 118, "items:118", false, false, 0, 65535, false, false, 1522, -1, 273));
		items.add(new ItemDef("Goblin Armour", "Armour Designed to fit Goblins", "", 40, 118, "items:118", false, false, 0, 16750912, false, true, -1, -1, 274));
		items.add(new ItemDef("Goblin Armour", "Armour Designed to fit Goblins", "", 40, 118, "items:118", false, false, 0, 255, false, true, -1, -1, 275));
		items.add(new ItemDef("unstrung Longbow", "I need to find a string for this", "", 60, 119, "items:119", false, false, 0, 65280, true, false, 1523, -1, 276));
		items.add(new ItemDef("unstrung shortbow", "I need to find a string for this", "", 23, 120, "items:120", false, false, 0, 65280, true, false, 1524, -1, 277));
		items.add(new ItemDef("Unfired Pie dish", "I need to put this in a pottery oven", "", 3, 110, "items:110", false, false, 0, 15632503, false, false, 1525, -1, 278));
		items.add(new ItemDef("unfired pot", "I need to put this in a pottery oven", "", 1, 61, "items:61", false, false, 0, 15632503, false, false, 1526, -1, 279));
		items.add(new ItemDef("arrow shafts", "I need to attach feathers to these", "", 1, 121, "items:121", true, false, 0, 0, true, false, -1, -1, 280));
		items.add(new ItemDef("Woad Leaf", "slightly bluish leaves", "", 1, 122, "items:122", true, false, 0, 0, false, false, -1, -1, 281));
		items.add(new ItemDef("Orangedye", "A little bottle of dye", "", 5, 104, "items:104", false, false, 0, 16755200, false, false, 1527, -1, 282));
		items.add(new ItemDef("Gold ring", "A valuable ring", "", 350, 123, "items:123", false, false, 0, 16763980, false, false, 1528, -1, 283));
		items.add(new ItemDef("Sapphire ring", "A valuable ring", "", 900, 123, "items:123", false, false, 0, 19711, false, false, 1529, -1, 284));
		items.add(new ItemDef("Emerald ring", "A valuable ring", "", 1275, 123, "items:123", false, false, 0, 3394611, false, false, 1530, -1, 285));
		items.add(new ItemDef("Ruby ring", "A valuable ring", "", 2025, 123, "items:123", false, false, 0, 16724736, false, false, 1531, -1, 286));
		items.add(new ItemDef("Diamond ring", "A valuable ring", "", 3525, 123, "items:123", false, false, 0, 0, false, false, 1532, -1, 287));
		items.add(new ItemDef("Gold necklace", "I wonder if this is valuable", "", 450, 57, "items:57", false, true, 1024, 16763980, false, false, 1533, -1, 288));
		items.add(new ItemDef("Sapphire necklace", "I wonder if this is valuable", "", 1050, 57, "items:57", false, true, 1024, 19711, false, false, 1534, -1, 289));
		items.add(new ItemDef("Emerald necklace", "I wonder if this is valuable", "", 1425, 57, "items:57", false, true, 1024, 3394611, false, false, 1535, -1, 290));
		items.add(new ItemDef("Ruby necklace", "I wonder if this is valuable", "", 2175, 57, "items:57", false, true, 1024, 16724736, false, false, 1536, -1, 291));
		items.add(new ItemDef("Diamond necklace", "I wonder if this is valuable", "", 3675, 57, "items:57", false, true, 1024, 0, false, false, 1537, -1, 292));
		items.add(new ItemDef("ring mould", "Used to make gold rings", "", 5, 127, "items:127", false, false, 0, 0, false, false, 1538, -1, 293));
		items.add(new ItemDef("Amulet mould", "Used to make gold amulets", "", 5, 128, "items:128", false, false, 0, 0, false, false, 1539, -1, 294));
		items.add(new ItemDef("Necklace mould", "Used to make gold necklaces", "", 5, 129, "items:129", false, false, 0, 0, false, false, 1540, -1, 295));
		items.add(new ItemDef("Gold Amulet", "It needs a string so I can wear it", "", 350, 126, "items:126", false, false, 0, 16763980, false, false, 1541, -1, 296));
		items.add(new ItemDef("Sapphire Amulet", "It needs a string so I can wear it", "", 900, 126, "items:126", false, false, 0, 19711, false, false, 1542, -1, 297));
		items.add(new ItemDef("Emerald Amulet", "It needs a string so I can wear it", "", 1275, 126, "items:126", false, false, 0, 3394611, false, false, 1543, -1, 298));
		items.add(new ItemDef("Ruby Amulet", "It needs a string so I can make wear it", "", 2025, 126, "items:126", false, false, 0, 16724736, false, false, 1544, -1, 299));
		items.add(new ItemDef("Diamond Amulet", "It needs a string so I can wear it", "", 3525, 126, "items:126", false, false, 0, 0, false, false, 1545, -1, 300));
		items.add(new ItemDef("Gold Amulet", "I wonder if I can get this enchanted", "", 350, 125, "items:125", false, true, 1024, 16763980, false, false, 1546, -1, 301));
		items.add(new ItemDef("Sapphire Amulet", "I wonder if I can get this enchanted", "", 900, 125, "items:125", false, true, 1024, 19711, false, false, 1547, -1, 302));
		items.add(new ItemDef("Emerald Amulet", "I wonder if I can get this enchanted", "", 1275, 125, "items:125", false, true, 1024, 3394611, false, false, 1548, -1, 303));
		items.add(new ItemDef("Ruby Amulet", "I wonder if I can get this enchanted", "", 2025, 125, "items:125", false, true, 1024, 16724736, false, false, 1549, -1, 304));
		items.add(new ItemDef("Diamond Amulet", "I wonder if I can get this enchanted", "", 3525, 125, "items:125", false, true, 1024, 0, false, false, 1550, -1, 305));
		items.add(new ItemDef("superchisel", "I wonder if I can get this enchanted", "twiddle", 3525, 126, "items:126", false, false, 0, 0, true, false, 1551, -1, 306));
		items.add(new ItemDef("Mace of Zamorak", "This mace gives me the creeps", "", 4500, 0, "items:0", false, true, 16, 13408690, false, false, 1552, -1, 307));
		items.add(new ItemDef("Bronze Plate Mail top", "Armour designed for females", "", 160, 130, "items:130", false, true, 322, 16737817, false, false, 1553, -1, 308));
		items.add(new ItemDef("Steel Plate Mail top", "Armour designed for females", "", 2000, 130, "items:130", false, true, 322, 15658734, false, false, 1554, -1, 309));
		items.add(new ItemDef("Mithril Plate Mail top", "Armour designed for females", "", 5200, 130, "items:130", false, true, 322, 10072780, false, false, 1555, -1, 310));
		items.add(new ItemDef("Adamantite Plate Mail top", "Armour designed for females", "", 12800, 130, "items:130", false, true, 322, 11717785, false, false, 1556, -1, 311));
		items.add(new ItemDef("Iron Plate Mail top", "Armour designed for females", "", 560, 130, "items:130", false, true, 322, 15654365, false, false, 1557, -1, 312));
		items.add(new ItemDef("Black Plate Mail top", "Armour designed for females", "", 3840, 130, "items:130", false, true, 322, 3158064, false, false, 1558, -1, 313));
		items.add(new ItemDef("Sapphire Amulet of magic", "It improves my magic", "", 900, 125, "items:125", false, true, 1024, 19711, false, false, 1559, -1, 314));
		items.add(new ItemDef("Emerald Amulet of protection", "It improves my defense", "", 1275, 125, "items:125", false, true, 1024, 3394611, false, false, 1560, -1, 315));
		items.add(new ItemDef("Ruby Amulet of strength", "It improves my damage", "", 2025, 125, "items:125", false, true, 1024, 16724736, false, false, 1561, -1, 316));
		items.add(new ItemDef("Diamond Amulet of power", "A powerful amulet", "", 3525, 125, "items:125", false, true, 1024, 0, false, false, 1562, -1, 317));
		items.add(new ItemDef("Karamja Rum", "A very strong spirit brewed in Karamja", "", 30, 131, "items:131", false, false, 0, 0, false, true, -1, -1, 318));
		items.add(new ItemDef("Cheese", "It's got holes in it", "Eat", 4, 150, "items:150", false, false, 0, 0, false, false, 1563, -1, 319));
		items.add(new ItemDef("Tomato", "This would make good ketchup", "Eat", 4, 151, "items:151", false, false, 0, 0, false, false, 1564, -1, 320));
		items.add(new ItemDef("Pizza Base", "I need to add some tomato next", "", 4, 152, "items:152", false, false, 0, 16768184, false, false, 1565, -1, 321));
		items.add(new ItemDef("Burnt Pizza", "Oh dear!", "", 1, 152, "items:152", false, false, 0, 4210752, false, false, 1566, -1, 322));
		items.add(new ItemDef("Incomplete Pizza", "I need to add some cheese next", "", 10, 153, "items:153", false, false, 0, 0, false, false, 1567, -1, 323));
		items.add(new ItemDef("Uncooked Pizza", "This needs cooking", "", 25, 154, "items:154", false, false, 0, 0, false, false, 1568, -1, 324));
		items.add(new ItemDef("Plain Pizza", "A cheese and tomato pizza", "Eat", 40, 154, "items:154", false, false, 0, 0, false, false, 1569, -1, 325));
		items.add(new ItemDef("Meat Pizza", "A pizza with bits of meat on it", "Eat", 50, 155, "items:155", false, false, 0, 16756316, false, false, 1570, -1, 326));
		items.add(new ItemDef("Anchovie Pizza", "A Pizza with Anchovies", "Eat", 60, 155, "items:155", false, false, 0, 11447982, false, false, 1571, -1, 327));
		items.add(new ItemDef("Half Meat Pizza", "Half of this pizza has been eaten", "Eat", 25, 156, "items:156", false, false, 0, 16756316, false, false, 1572, -1, 328));
		items.add(new ItemDef("Half Anchovie Pizza", "Half of this pizza has been eaten", "Eat", 30, 156, "items:156", false, false, 0, 11447982, false, false, 1573, -1, 329));
		items.add(new ItemDef("Cake", "A plain sponge cake", "Eat", 50, 157, "items:157", false, false, 0, 16763289, false, false, 1574, -1, 330));
		items.add(new ItemDef("Burnt Cake", "Argh what a mess!", "", 1, 157, "items:157", false, false, 0, 4210752, false, false, 1575, -1, 331));
		items.add(new ItemDef("Chocolate Cake", "This looks very tasty!", "Eat", 70, 157, "items:157", false, false, 0, 16744524, false, false, 1576, -1, 332));
		items.add(new ItemDef("Partial Cake", "Someone has eaten a big chunk of this cake", "Eat", 30, 158, "items:158", false, false, 0, 16763289, false, false, 1577, -1, 333));
		items.add(new ItemDef("Partial Chocolate Cake", "Someone has eaten a big chunk of this cake", "Eat", 50, 158, "items:158", false, false, 0, 16744524, false, false, 1578, -1, 334));
		items.add(new ItemDef("Slice of Cake", "I'd rather have a whole cake!", "Eat", 10, 159, "items:159", false, false, 0, 16763289, false, false, 1579, -1, 335));
		items.add(new ItemDef("Chocolate Slice", "A slice of chocolate cake", "Eat", 30, 159, "items:159", false, false, 0, 16744524, false, false, 1580, -1, 336));
		items.add(new ItemDef("Chocolate Bar", "It's a bar of chocolate", "Eat", 10, 160, "items:160", false, false, 0, 0, false, false, 1581, -1, 337));
		items.add(new ItemDef("Cake Tin", "Useful for baking cakes", "", 10, 177, "items:177", false, false, 0, 0, false, false, 1582, -1, 338));
		items.add(new ItemDef("Uncooked cake", "Now all I need to do is cook it", "", 20, 178, "items:178", false, false, 0, 16769248, false, false, 1583, -1, 339));
		items.add(new ItemDef("Unfired bowl", "I need to put this in a pottery oven", "", 2, 161, "items:161", false, false, 0, 15632503, false, false, 1584, -1, 340));
		items.add(new ItemDef("Bowl", "Useful for mixing things", "", 4, 161, "items:161", false, false, 0, 16757606, false, false, 1585, -1, 341));
		items.add(new ItemDef("Bowl of water", "It's a bowl of water", "", 3, 162, "items:162", false, false, 0, 255, false, false, 1586, -1, 342));
		items.add(new ItemDef("Incomplete stew", "I need to add some meat too", "", 4, 162, "items:162", false, false, 0, 10066355, false, false, 1587, -1, 343));
		items.add(new ItemDef("Incomplete stew", "I need to add some potato too", "", 4, 162, "items:162", false, false, 0, 10066355, false, false, 1588, -1, 344));
		items.add(new ItemDef("Uncooked stew", "I need to cook this", "", 10, 162, "items:162", false, false, 0, 13415270, false, false, 1589, -1, 345));
		items.add(new ItemDef("Stew", "It's a meat and potato stew", "Eat", 20, 162, "items:162", false, false, 0, 10046464, false, false, 1590, -1, 346));
		items.add(new ItemDef("Burnt Stew", "Eew it's horribly burnt", "Empty", 1, 162, "items:162", false, false, 0, 3158064, false, false, 1591, -1, 347));
		items.add(new ItemDef("Potato", "Can be used to make stew", "", 1, 163, "items:163", false, false, 0, 0, false, false, 1592, -1, 348));
		items.add(new ItemDef("Raw Shrimp", "I should try cooking this", "", 5, 164, "items:164", false, false, 0, 16752800, false, false, 1593, -1, 349));
		items.add(new ItemDef("Shrimp", "Some nicely cooked fish", "Eat", 5, 164, "items:164", false, false, 0, 16740464, false, false, 1594, -1, 350));
		items.add(new ItemDef("Raw Anchovies", "I should try cooking this", "", 15, 164, "items:164", false, false, 0, 10526975, false, false, 1595, -1, 351));
		items.add(new ItemDef("Anchovies", "Some nicely cooked fish", "Eat", 15, 164, "items:164", false, false, 0, 7368959, false, false, 1596, -1, 352));
		items.add(new ItemDef("Burnt fish", "Oops!", "", 1, 164, "items:164", false, false, 0, 4210752, false, false, 1597, -1, 353));
		items.add(new ItemDef("Raw Sardine", "I should try cooking this", "", 10, 165, "items:165", false, false, 0, 10551200, false, false, 1598, -1, 354));
		items.add(new ItemDef("Sardine", "Some nicely cooked fish", "Eat", 10, 165, "items:165", false, false, 0, 7405424, false, false, 1599, -1, 355));
		items.add(new ItemDef("Raw Salmon", "I should try cooking this", "", 50, 165, "items:165", false, false, 0, 0, false, false, 1600, -1, 356));
		items.add(new ItemDef("Salmon", "Some nicely cooked fish", "Eat", 50, 165, "items:165", false, false, 0, 12619920, false, false, 1601, -1, 357));
		items.add(new ItemDef("Raw Trout", "I should try cooking this", "", 20, 165, "items:165", false, false, 0, 16752800, false, false, 1602, -1, 358));
		items.add(new ItemDef("Trout", "Some nicely cooked fish", "Eat", 20, 165, "items:165", false, false, 0, 16740464, false, false, 1603, -1, 359));
		items.add(new ItemDef("Burnt fish", "Oops!", "", 1, 165, "items:165", false, false, 0, 4210752, false, false, 1604, -1, 360));
		items.add(new ItemDef("Raw Herring", "I should try cooking this", "", 15, 166, "items:166", false, false, 0, 0, false, false, 1605, -1, 361));
		items.add(new ItemDef("Herring", "Some nicely cooked fish", "Eat", 15, 166, "items:166", false, false, 0, 12619920, false, false, 1606, -1, 362));
		items.add(new ItemDef("Raw Pike", "I should try cooking this", "", 25, 166, "items:166", false, false, 0, 10526975, false, false, 1607, -1, 363));
		items.add(new ItemDef("Pike", "Some nicely cooked fish", "Eat", 25, 166, "items:166", false, false, 0, 7368959, false, false, 1608, -1, 364));
		items.add(new ItemDef("Burnt fish", "Oops!", "", 1, 166, "items:166", false, false, 0, 4210752, false, false, 1609, -1, 365));
		items.add(new ItemDef("Raw Tuna", "I should try cooking this", "", 100, 167, "items:167", false, false, 0, 0, false, false, 1610, -1, 366));
		items.add(new ItemDef("Tuna", "Wow this is a big fish", "Eat", 100, 167, "items:167", false, false, 0, 12619920, false, false, 1611, -1, 367));
		items.add(new ItemDef("Burnt fish", "Oops!", "", 1, 167, "items:167", false, false, 0, 4210752, false, false, 1612, -1, 368));
		items.add(new ItemDef("Raw Swordfish", "I should try cooking this", "", 200, 168, "items:168", false, false, 0, 16752895, false, false, 1613, -1, 369));
		items.add(new ItemDef("Swordfish", "I'd better be careful eating this!", "Eat", 200, 168, "items:168", false, false, 0, 12611776, false, false, 1614, -1, 370));
		items.add(new ItemDef("Burnt Swordfish", "Oops!", "", 1, 168, "items:168", false, false, 0, 4210752, false, false, 1615, -1, 371));
		items.add(new ItemDef("Raw Lobster", "I should try cooking this", "", 150, 169, "items:169", false, false, 0, 16711680, false, false, 1616, -1, 372));
		items.add(new ItemDef("Lobster", "This looks tricky to eat", "Eat", 150, 169, "items:169", false, false, 0, 11558912, false, false, 1617, -1, 373));
		items.add(new ItemDef("Burnt Lobster", "Oops!", "", 1, 169, "items:169", false, false, 0, 4210752, false, false, 1618, -1, 374));
		items.add(new ItemDef("Lobster Pot", "Useful for catching lobsters", "", 20, 170, "items:170", false, false, 0, 0, false, false, 1619, -1, 375));
		items.add(new ItemDef("Net", "Useful for catching small fish", "", 5, 171, "items:171", false, false, 0, 0, false, false, 1620, -1, 376));
		items.add(new ItemDef("Fishing Rod", "Useful for catching sardine or herring", "", 5, 172, "items:172", false, false, 0, 0, false, false, 1621, -1, 377));
		items.add(new ItemDef("Fly Fishing Rod", "Useful for catching salmon or trout", "", 5, 173, "items:173", false, false, 0, 0, false, false, 1622, -1, 378));
		items.add(new ItemDef("Harpoon", "Useful for catching really big fish", "", 5, 174, "items:174", false, false, 0, 0, false, false, 1623, -1, 379));
		items.add(new ItemDef("Fishing Bait", "For use with a fishing rod", "", 3, 175, "items:175", true, false, 0, 0, false, false, -1, -1, 380));
		items.add(new ItemDef("Feather", "Used for fly-fishing", "", 2, 176, "items:176", true, false, 0, 0, false, false, -1, -1, 381));
		items.add(new ItemDef("Chest key", "A key to One eyed Hector's chest", "", 1, 25, "items:25", false, false, 0, 14540253, false, true, -1, -1, 382));
		items.add(new ItemDef("Silver", "this needs refining", "", 75, 134, "items:134", false, false, 0, 0, false, false, 1624, -1, 383));
		items.add(new ItemDef("silver bar", "this looks valuable", "", 150, 79, "items:79", false, false, 0, 0, false, false, 1625, -1, 384));
		items.add(new ItemDef("Holy Symbol of saradomin", "This improves my prayer", "", 300, 44, "items:44", false, true, 1024, 0, false, false, 1626, -1, 385));
		items.add(new ItemDef("Holy symbol mould", "Used to make Holy Symbols", "", 5, 132, "items:132", false, false, 0, 0, false, false, 1627, -1, 386));
		items.add(new ItemDef("Disk of Returning", "Used to get out of Thordur's blackhole", "spin", 12, 133, "items:133", false, false, 0, 0, false, false, 1628, -1, 387));
		items.add(new ItemDef("Monks robe", "I feel closer to the God's when I am wearing this", "", 40, 87, "items:87", false, true, 64, 10510400, false, false, 1629, -1, 388));
		items.add(new ItemDef("Monks robe", "Keeps a monk's legs nice and warm", "", 30, 88, "items:88", false, true, 128, 10510400, false, false, 1630, -1, 389));
		items.add(new ItemDef("Red key", "A painted key", "", 1, 25, "items:25", false, false, 0, 16711680, false, true, -1, -1, 390));
		items.add(new ItemDef("Orange Key", "A painted key", "", 1, 25, "items:25", false, false, 0, 16755200, false, true, -1, -1, 391));
		items.add(new ItemDef("yellow key", "A painted key", "", 1, 25, "items:25", false, false, 0, 16777045, false, true, -1, -1, 392));
		items.add(new ItemDef("Blue key", "A painted key", "", 1, 25, "items:25", false, false, 0, 255, false, true, -1, -1, 393));
		items.add(new ItemDef("Magenta key", "A painted key", "", 1, 25, "items:25", false, false, 0, 16711935, false, true, -1, -1, 394));
		items.add(new ItemDef("black key", "A painted key", "", 1, 25, "items:25", false, false, 0, 4210752, false, true, -1, -1, 395));
		items.add(new ItemDef("rune dagger", "Short but pointy", "", 8000, 80, "items:80", false, true, 16, 65535, false, false, 1631, -1, 396));
		items.add(new ItemDef("Rune short sword", "A razor sharp sword", "", 20800, 1, "items:1", false, true, 16, 65535, false, false, 1632, -1, 397));
		items.add(new ItemDef("rune Scimitar", "A vicious curved sword", "", 25600, 83, "items:83", false, true, 16, 65535, false, false, 1633, -1, 398));
		items.add(new ItemDef("Medium Rune Helmet", "A medium sized helmet", "", 19200, 5, "items:5", false, true, 32, 65535, false, false, 1634, -1, 399));
		items.add(new ItemDef("Rune Chain Mail Body", "A series of connected metal rings", "", 50000, 7, "items:7", false, true, 64, 65535, false, false, 1635, -1, 400));
		items.add(new ItemDef("Rune Plate Mail Body", "Provides excellent protection", "", 65000, 8, "items:8", false, true, 322, 65535, false, false, 1636, -1, 401));
		items.add(new ItemDef("Rune Plate Mail Legs", "These look pretty heavy", "", 64000, 9, "items:9", false, true, 644, 65535, false, false, 1637, -1, 402));
		items.add(new ItemDef("Rune Square Shield", "A medium metal shield", "", 38400, 3, "items:3", false, true, 8, 56797, false, false, 1638, -1, 403));
		items.add(new ItemDef("Rune Kite Shield", "A large metal shield", "", 54400, 2, "items:2", false, true, 8, 56797, false, false, 1639, -1, 404));
		items.add(new ItemDef("rune Axe", "A powerful axe", "", 12800, 12, "items:12", false, true, 16, 65535, false, false, 1640, -1, 405));
		items.add(new ItemDef("Rune skirt", "Designer leg protection", "", 64000, 88, "items:88", false, true, 640, 26214, false, false, 1641, -1, 406));
		items.add(new ItemDef("Rune Plate Mail top", "Armour designed for females", "", 65000, 130, "items:130", false, true, 322, 65535, false, false, 1642, -1, 407));
		items.add(new ItemDef("Runite bar", "it's a bar of runite", "", 5000, 79, "items:79", false, false, 0, 56797, false, false, 1643, -1, 408));
		items.add(new ItemDef("runite ore", "this needs refining", "", 3200, 70, "items:70", false, false, 0, 56797, false, false, 1644, -1, 409));
		items.add(new ItemDef("Plank", "This doesn't look very useful", "", 1, 135, "items:135", false, false, 0, 0, false, false, 1645, -1, 410));
		items.add(new ItemDef("Tile", "This doesn't look very useful", "", 1, 136, "items:136", false, false, 0, 0, false, false, 1646, -1, 411));
		items.add(new ItemDef("skull", "A spooky looking skull", "", 1, 26, "items:26", false, false, 0, 0, false, false, 1647, -1, 412));
		items.add(new ItemDef("Big Bones", "Ew it's a pile of bones", "Bury", 1, 137, "items:137", false, false, 0, 0, false, false, 1648, -1, 413));
		items.add(new ItemDef("Muddy key", "It looks like a key to a chest", "", 1, 25, "items:25", false, false, 0, 15636736, false, false, 1649, -1, 414));
		items.add(new ItemDef("Map", "A map showing the way to the Isle of Crandor", "", 1, 138, "items:138", false, false, 0, 0, false, true, -1, -1, 415));
		items.add(new ItemDef("Map Piece", "I need some more of the map for this to be useful", "", 1, 139, "items:139", false, false, 0, 0, false, true, -1, -1, 416));
		items.add(new ItemDef("Map Piece", "I need some more of the map for this to be useful", "", 1, 140, "items:140", false, false, 0, 0, false, true, -1, -1, 417));
		items.add(new ItemDef("Map Piece", "I need some more of the map for this to be useful", "", 1, 141, "items:141", false, false, 0, 0, false, true, -1, -1, 418));
		items.add(new ItemDef("Nails", "Nails made from steel", "", 3, 142, "items:142", true, false, 0, 0, false, false, -1, -1, 419));
		items.add(new ItemDef("Anti dragon breath Shield", "Helps prevent damage from dragons", "", 20, 143, "items:143", false, true, 8, 0, false, false, 1650, -1, 420));
		items.add(new ItemDef("Maze key", "The key to the entrance of Melzar's maze", "", 1, 25, "items:25", false, false, 0, 14540253, false, false, 1651, -1, 421));
		items.add(new ItemDef("Pumpkin", "Happy halloween", "eat", 30, 149, "items:149", false, false, 0, 0, false, false, 1652, -1, 422));
		items.add(new ItemDef("Black dagger", "Short but pointy", "", 240, 80, "items:80", false, true, 16, 3158064, false, false, 1653, -1, 423));
		items.add(new ItemDef("Black Short Sword", "A razor sharp sword", "", 624, 1, "items:1", false, true, 16, 3158064, false, false, 1654, -1, 424));
		items.add(new ItemDef("Black Long Sword", "A razor sharp sword", "", 960, 81, "items:81", false, true, 16, 3158064, false, false, 1655, -1, 425));
		items.add(new ItemDef("Black 2-handed Sword", "A very large sword", "", 1920, 82, "items:82", false, true, 8216, 3158064, false, false, 1656, -1, 426));
		items.add(new ItemDef("Black Scimitar", "A vicious curved sword", "", 768, 83, "items:83", false, true, 16, 3158064, false, false, 1657, -1, 427));
		items.add(new ItemDef("Black Axe", "A sinister looking axe", "", 384, 12, "items:12", false, true, 16, 3158064, false, false, 1658, -1, 428));
		items.add(new ItemDef("Black battle Axe", "A vicious looking axe", "", 1248, 84, "items:84", false, true, 16, 3158064, false, false, 1659, -1, 429));
		items.add(new ItemDef("Black Mace", "A spikey mace", "", 432, 0, "items:0", false, true, 16, 3158064, false, false, 1660, -1, 430));
		items.add(new ItemDef("Black Chain Mail Body", "A series of connected metal rings", "", 1440, 7, "items:7", false, true, 64, 3158064, false, false, 1661, -1, 431));
		items.add(new ItemDef("Black Square Shield", "A medium metal shield", "", 1152, 3, "items:3", false, true, 8, 3158064, false, false, 1662, -1, 432));
		items.add(new ItemDef("Black Kite Shield", "A large metal shield", "", 1632, 2, "items:2", false, true, 8, 3158064, false, false, 1663, -1, 433));
		items.add(new ItemDef("Black Plated skirt", "designer leg protection", "", 1920, 88, "items:88", false, true, 640, 1118481, false, false, 1664, -1, 434));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, false, 1665, -1, 435));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, false, 1666, -1, 436));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, false, 1667, -1, 437));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, false, 1668, -1, 438));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, false, 1669, -1, 439));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, false, 1670, -1, 440));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, false, 1671, -1, 441));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, false, 1672, -1, 442));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, false, 1673, -1, 443));
		items.add(new ItemDef("Guam leaf", "A herb used in attack potion making", "", 3, 75, "items:75", false, false, 0, 0, true, false, 1674, -1, 444));
		items.add(new ItemDef("Marrentill", "A herb used in poison cures", "", 5, 75, "items:75", false, false, 0, 0, true, false, 1675, -1, 445));
		items.add(new ItemDef("Tarromin", "A useful herb", "", 11, 75, "items:75", false, false, 0, 0, true, false, 1676, -1, 446));
		items.add(new ItemDef("Harralander", "A useful herb", "", 20, 75, "items:75", false, false, 0, 0, true, false, 1677, -1, 447));
		items.add(new ItemDef("Ranarr Weed", "A useful herb", "", 25, 75, "items:75", false, false, 0, 0, true, false, 1678, -1, 448));
		items.add(new ItemDef("Irit Leaf", "A useful herb", "", 40, 75, "items:75", false, false, 0, 0, true, false, 1679, -1, 449));
		items.add(new ItemDef("Avantoe", "A useful herb", "", 48, 75, "items:75", false, false, 0, 0, true, false, 1680, -1, 450));
		items.add(new ItemDef("Kwuarm", "A powerful herb", "", 54, 75, "items:75", false, false, 0, 0, true, false, 1681, -1, 451));
		items.add(new ItemDef("Cadantine", "A powerful herb", "", 65, 75, "items:75", false, false, 0, 0, true, false, 1682, -1, 452));
		items.add(new ItemDef("Dwarf Weed", "A powerful herb", "", 70, 75, "items:75", false, false, 0, 0, true, false, 1683, -1, 453));
		items.add(new ItemDef("Unfinished potion", "I need another ingredient to finish this Guam potion", "", 3, 48, "items:48", false, false, 0, 10073782, true, false, 1684, -1, 454));
		items.add(new ItemDef("Unfinished potion", "I need another ingredient to finish this Marrentill potion", "", 5, 48, "items:48", false, false, 0, 11966902, true, false, 1685, -1, 455));
		items.add(new ItemDef("Unfinished potion", "I need another ingredient to finish this Tarromin potion", "", 11, 48, "items:48", false, false, 0, 11974297, true, false, 1686, -1, 456));
		items.add(new ItemDef("Unfinished potion", "I need another ingredient to finish this Harralander potion", "", 20, 48, "items:48", false, false, 0, 11966873, true, false, 1687, -1, 457));
		items.add(new ItemDef("Unfinished potion", "I need another ingredient to finish this Ranarr potion", "", 25, 48, "items:48", false, false, 0, 10073753, true, false, 1688, -1, 458));
		items.add(new ItemDef("Unfinished potion", "I need another ingredient to finish this Irit potion", "", 40, 48, "items:48", false, false, 0, 10066358, true, false, 1689, -1, 459));
		items.add(new ItemDef("Unfinished potion", "I need another ingredient to finish this Avantoe potion", "", 48, 48, "items:48", false, false, 0, 10066329, true, false, 1690, -1, 460));
		items.add(new ItemDef("Unfinished potion", "I need another ingredient to finish this Kwuarm potion", "", 54, 48, "items:48", false, false, 0, 11974326, true, false, 1691, -1, 461));
		items.add(new ItemDef("Unfinished potion", "I need another ingredient to finish this Cadantine potion", "", 65, 48, "items:48", false, false, 0, 13743769, true, false, 1692, -1, 462));
		items.add(new ItemDef("Unfinished potion", "I need another ingredient to finish this Dwarfweed potion", "", 70, 48, "items:48", false, false, 0, 10073809, true, false, 1693, -1, 463));
		items.add(new ItemDef("Vial", "It's full of water", "", 2, 48, "items:48", false, false, 0, 11197951, false, false, 1694, -1, 464));
		items.add(new ItemDef("Vial", "This vial is empty", "", 2, 144, "items:144", false, false, 0, 0, false, false, 1695, -1, 465));
		items.add(new ItemDef("Unicorn horn", "Poor unicorn", "", 20, 145, "items:145", false, false, 0, 0, true, false, 1696, -1, 466));
		items.add(new ItemDef("Blue dragon scale", "A large shiny scale", "", 50, 146, "items:146", false, false, 0, 0, true, false, 1697, -1, 467));
		items.add(new ItemDef("Pestle and mortar", "I can grind things for potions in this", "", 4, 147, "items:147", false, false, 0, 0, true, false, 1698, -1, 468));
		items.add(new ItemDef("Snape grass", "Strange spikey grass", "", 10, 148, "items:148", false, false, 0, 0, true, false, 1699, -1, 469));
		items.add(new ItemDef("Medium black Helmet", "A medium sized helmet", "", 576, 5, "items:5", false, true, 32, 3158064, false, false, 1700, -1, 470));
		items.add(new ItemDef("White berries", "Poisonous berries", "", 10, 21, "items:21", false, false, 0, 0, true, false, 1701, -1, 471));
		items.add(new ItemDef("Ground blue dragon scale", "This stuff isn't good for you", "", 40, 23, "items:23", false, false, 0, 35071, true, false, 1702, -1, 472));
		items.add(new ItemDef("Ground unicorn horn", "A useful potion ingredient", "", 20, 23, "items:23", false, false, 0, 15645520, true, false, 1703, -1, 473));
		items.add(new ItemDef("attack Potion", "3 doses of attack potion", "Drink", 12, 48, "items:48", false, false, 0, 3206894, true, false, 1704, -1, 474));
		items.add(new ItemDef("attack Potion", "2 doses of attack potion", "Drink", 9, 436, "items:436", false, false, 0, 3206894, true, false, 1705, -1, 475));
		items.add(new ItemDef("attack Potion", "1 dose of attack potion", "Drink", 6, 437, "items:437", false, false, 0, 3206894, true, false, 1706, -1, 476));
		items.add(new ItemDef("stat restoration Potion", "3 doses of stat restoration potion", "Drink", 88, 48, "items:48", false, false, 0, 15609904, true, false, 1707, -1, 477));
		items.add(new ItemDef("stat restoration Potion", "2 doses of stat restoration potion", "Drink", 66, 436, "items:436", false, false, 0, 15609904, true, false, 1708, -1, 478));
		items.add(new ItemDef("stat restoration Potion", "1 dose of stat restoration potion", "Drink", 44, 437, "items:437", false, false, 0, 15609904, true, false, 1709, -1, 479));
		items.add(new ItemDef("defense Potion", "3 doses of defense potion", "Drink", 120, 48, "items:48", false, false, 0, 3206704, true, false, 1710, -1, 480));
		items.add(new ItemDef("defense Potion", "2 doses of defense potion", "Drink", 90, 436, "items:436", false, false, 0, 3206704, true, false, 1711, -1, 481));
		items.add(new ItemDef("defense Potion", "1 dose of defense potion", "Drink", 60, 437, "items:437", false, false, 0, 3206704, true, false, 1712, -1, 482));
		items.add(new ItemDef("restore prayer Potion", "3 doses of restore prayer potion", "Drink", 152, 48, "items:48", false, false, 0, 3206809, true, false, 1713, -1, 483));
		items.add(new ItemDef("restore prayer Potion", "2 doses of restore prayer potion", "Drink", 114, 436, "items:436", false, false, 0, 3206809, true, false, 1714, -1, 484));
		items.add(new ItemDef("restore prayer Potion", "1 dose of restore prayer potion", "Drink", 76, 437, "items:437", false, false, 0, 3206809, true, false, 1715, -1, 485));
		items.add(new ItemDef("Super attack Potion", "3 doses of attack potion", "Drink", 180, 48, "items:48", false, false, 0, 3158254, true, false, 1716, -1, 486));
		items.add(new ItemDef("Super attack Potion", "2 doses of attack potion", "Drink", 135, 436, "items:436", false, false, 0, 3158254, true, false, 1717, -1, 487));
		items.add(new ItemDef("Super attack Potion", "1 dose of attack potion", "Drink", 90, 437, "items:437", false, false, 0, 3158254, true, false, 1718, -1, 488));
		items.add(new ItemDef("fishing Potion", "3 doses of fishing potion", "Drink", 200, 48, "items:48", false, false, 0, 3158064, true, false, 1719, -1, 489));
		items.add(new ItemDef("fishing Potion", "2 doses of fishing potion", "Drink", 150, 436, "items:436", false, false, 0, 3158064, true, false, 1720, -1, 490));
		items.add(new ItemDef("fishing Potion", "1 dose of fishing potion", "Drink", 100, 437, "items:437", false, false, 0, 3158064, true, false, 1721, -1, 491));
		items.add(new ItemDef("Super strength Potion", "3 doses of strength potion", "Drink", 220, 48, "items:48", false, false, 0, 15658734, true, false, 1722, -1, 492));
		items.add(new ItemDef("Super strength Potion", "2 doses of strength potion", "Drink", 165, 436, "items:436", false, false, 0, 15658734, true, false, 1723, -1, 493));
		items.add(new ItemDef("Super strength Potion", "1 dose of strength potion", "Drink", 110, 437, "items:437", false, false, 0, 15658734, true, false, 1724, -1, 494));
		items.add(new ItemDef("Super defense Potion", "3 doses of defense potion", "Drink", 264, 48, "items:48", false, false, 0, 15644208, true, false, 1725, -1, 495));
		items.add(new ItemDef("Super defense Potion", "2 doses of defense potion", "Drink", 198, 436, "items:436", false, false, 0, 15644208, true, false, 1726, -1, 496));
		items.add(new ItemDef("Super defense Potion", "1 dose of defense potion", "Drink", 132, 437, "items:437", false, false, 0, 15644208, true, false, 1727, -1, 497));
		items.add(new ItemDef("ranging Potion", "3 doses of ranging potion", "Drink", 288, 48, "items:48", false, false, 0, 3192558, true, false, 1728, -1, 498));
		items.add(new ItemDef("ranging Potion", "2 doses of ranging potion", "Drink", 216, 436, "items:436", false, false, 0, 3192558, true, false, 1729, -1, 499));
		items.add(new ItemDef("ranging Potion", "1 dose of ranging potion", "Drink", 144, 437, "items:437", false, false, 0, 3192558, true, false, 1730, -1, 500));
		items.add(new ItemDef("wine of Zamorak", "It's full of wine", "Drink", 1, 65, "items:65", false, false, 0, 12851224, false, false, 1731, -1, 501));
		items.add(new ItemDef("raw bear meat", "I need to cook this first", "", 1, 60, "items:60", false, false, 0, 16747571, false, false, 1732, -1, 502));
		items.add(new ItemDef("raw rat meat", "I need to cook this first", "", 1, 60, "items:60", false, false, 0, 16747571, false, false, 1733, -1, 503));
		items.add(new ItemDef("raw beef", "I need to cook this first", "", 1, 60, "items:60", false, false, 0, 16747571, false, false, 1734, -1, 504));
		items.add(new ItemDef("enchanted bear meat", "I don't fancy eating this now", "", 1, 60, "items:60", false, false, 0, 13495347, true, true, -1, -1, 505));
		items.add(new ItemDef("enchanted rat meat", "I don't fancy eating this now", "", 1, 60, "items:60", false, false, 0, 13495347, true, true, -1, -1, 506));
		items.add(new ItemDef("enchanted beef", "I don't fancy eating this now", "", 1, 60, "items:60", false, false, 0, 13495347, true, true, -1, -1, 507));
		items.add(new ItemDef("enchanted chicken meat", "I don't fancy eating this now", "", 1, 60, "items:60", false, false, 0, 13495347, true, true, -1, -1, 508));
		items.add(new ItemDef("Dramen Staff", "A magical staff cut from the dramen tree", "", 15, 85, "items:85", false, true, 16, 10072780, true, true, -1, -1, 509));
		items.add(new ItemDef("Dramen Branch", "I need to make this into a staff", "", 15, 179, "items:179", false, false, 0, 10072780, true, true, -1, -1, 510));
		items.add(new ItemDef("Cape", "A thick Green cape", "", 32, 59, "items:59", false, true, 2048, 4246592, false, false, 1735, -1, 511));
		items.add(new ItemDef("Cape", "A thick yellow cape", "", 32, 59, "items:59", false, true, 2048, 15658560, false, false, 1736, -1, 512));
		items.add(new ItemDef("Cape", "A thick Orange cape", "", 32, 59, "items:59", false, true, 2048, 15636736, false, false, 1737, -1, 513));
		items.add(new ItemDef("Cape", "A thick purple cape", "", 32, 59, "items:59", false, true, 2048, 11141341, false, false, 1738, -1, 514));
		items.add(new ItemDef("Greendye", "A little bottle of dye", "", 5, 104, "items:104", false, false, 0, 65280, false, false, 1739, -1, 515));
		items.add(new ItemDef("Purpledye", "A little bottle of dye", "", 5, 104, "items:104", false, false, 0, 11141375, false, false, 1740, -1, 516));
		items.add(new ItemDef("Iron ore certificate", "Each certificate exchangable at draynor market for 5 iron ore", "", 10, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 517));
		items.add(new ItemDef("Coal certificate", "Each certificate exchangable at draynor market for 5 coal", "", 20, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 518));
		items.add(new ItemDef("Mithril ore certificate", "Each certificate exchangable at draynor market for 5 mithril ore", "", 30, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 519));
		items.add(new ItemDef("silver certificate", "Each certificate exchangable at draynor market for 5 silver nuggets", "", 15, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 520));
		items.add(new ItemDef("Gold certificate", "Each certificate exchangable at draynor market for 5 gold nuggets", "", 25, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 521));
		items.add(new ItemDef("Dragonstone Amulet", "A very powerful amulet", "", 17625, 125, "items:125", false, true, 1024, 12255487, true, false, 1741, -1, 522));
		items.add(new ItemDef("Dragonstone", "This looks very valuable", "", 10000, 74, "items:74", false, false, 0, 12255487, true, false, 1742, -1, 523));
		items.add(new ItemDef("Dragonstone Amulet", "It needs a string so I can wear it", "", 17625, 126, "items:126", false, false, 0, 12255487, true, false, 1743, -1, 524));
		items.add(new ItemDef("Crystal key", "A very shiny key", "", 1, 25, "items:25", false, false, 0, 15663103, true, false, 1744, -1, 525));
		items.add(new ItemDef("Half of a key", "A very shiny key", "", 1, 181, "items:181", false, false, 0, 15663103, true, false, 1745, -1, 526));
		items.add(new ItemDef("Half of a key", "A very shiny key", "", 1, 182, "items:182", false, false, 0, 15663103, true, false, 1746, -1, 527));
		items.add(new ItemDef("Iron bar certificate", "Each certificate exchangable at draynor market for 5 iron bars", "", 10, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 528));
		items.add(new ItemDef("steel bar certificate", "Each certificate exchangable at draynor market for 5 steel bars", "", 20, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 529));
		items.add(new ItemDef("Mithril bar certificate", "Each certificate exchangable at draynor market for 5 mithril bars", "", 30, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 530));
		items.add(new ItemDef("silver bar certificate", "Each certificate exchangable at draynor market for 5 silver bars", "", 15, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 531));
		items.add(new ItemDef("Gold bar certificate", "Each certificate exchangable at draynor market for 5 gold bars", "", 25, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 532));
		items.add(new ItemDef("Lobster certificate", "Each certificate exchangable at draynor market for 5 lobsters", "", 10, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 533));
		items.add(new ItemDef("Raw lobster certificate", "Each certificate exchangable at draynor market for 5 raw lobsters", "", 10, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 534));
		items.add(new ItemDef("Swordfish certificate", "Each certificate exchangable at draynor market for 5 swordfish", "", 10, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 535));
		items.add(new ItemDef("Raw swordfish certificate", "Each certificate exchangable at draynor market for 5 raw swordfish", "", 10, 180, "items:180", true, false, 0, 0, false, false, -1, -1, 536));
		items.add(new ItemDef("Diary", "Property of Nora.T.Hag", "read", 1, 28, "items:28", false, false, 0, 11206570, true, false, 1747, -1, 537));
		items.add(new ItemDef("Front door key", "A house key", "", 1, 25, "items:25", false, false, 0, 15636736, true, true, -1, -1, 538));
		items.add(new ItemDef("Ball", "A child's ball", "", 1, 183, "items:183", false, false, 0, 0, true, true, -1, -1, 539));
		items.add(new ItemDef("magnet", "A very attractive magnet", "", 3, 184, "items:184", false, false, 0, 0, true, true, -1, -1, 540));
		items.add(new ItemDef("Grey wolf fur", "This would make warm clothing", "", 50, 68, "items:68", false, false, 0, 15658734, true, false, 1748, -1, 541));
		items.add(new ItemDef("uncut dragonstone", "this would be worth more cut", "", 1000, 73, "items:73", false, false, 0, 12255487, true, false, 1749, -1, 542));
		items.add(new ItemDef("Dragonstone ring", "A valuable ring", "", 17625, 123, "items:123", false, false, 0, 12255487, true, false, 1750, -1, 543));
		items.add(new ItemDef("Dragonstone necklace", "I wonder if this is valuable", "", 18375, 57, "items:57", false, true, 1024, 12255487, true, false, 1751, -1, 544));
		items.add(new ItemDef("Raw Shark", "I should try cooking this", "", 300, 185, "items:185", false, false, 0, 5263488, true, false, 1752, -1, 545));
		items.add(new ItemDef("Shark", "I'd better be careful eating this!", "Eat", 300, 185, "items:185", false, false, 0, 11558912, true, false, 1753, -1, 546));
		items.add(new ItemDef("Burnt Shark", "Oops!", "", 1, 185, "items:185", false, false, 0, 4210752, true, false, 1754, -1, 547));
		items.add(new ItemDef("Big Net", "Useful for catching lots of fish", "", 20, 186, "items:186", false, false, 0, 0, true, false, 1755, -1, 548));
		items.add(new ItemDef("Casket", "I hope there is treasure in it", "open", 50, 187, "items:187", false, false, 0, 0, true, false, 1756, -1, 549));
		items.add(new ItemDef("Raw cod", "I should try cooking this", "", 25, 165, "items:165", false, false, 0, 10526924, true, false, 1757, -1, 550));
		items.add(new ItemDef("Cod", "Some nicely cooked fish", "Eat", 25, 165, "items:165", false, false, 0, 7368908, true, false, 1758, -1, 551));
		items.add(new ItemDef("Raw Mackerel", "I should try cooking this", "", 17, 166, "items:166", false, false, 0, 13421728, true, false, 1759, -1, 552));
		items.add(new ItemDef("Mackerel", "Some nicely cooked fish", "Eat", 17, 166, "items:166", false, false, 0, 13421680, true, false, 1760, -1, 553));
		items.add(new ItemDef("Raw Bass", "I should try cooking this", "", 120, 167, "items:167", false, false, 0, 16752800, true, false, 1761, -1, 554));
		items.add(new ItemDef("Bass", "Wow this is a big fish", "Eat", 120, 167, "items:167", false, false, 0, 16740464, true, false, 1762, -1, 555));
		items.add(new ItemDef("Ice Gloves", "These will keep my hands cold!", "", 6, 17, "items:17", false, true, 256, 11202303, true, true, -1, -1, 556));
		items.add(new ItemDef("Firebird Feather", "A red hot feather", "", 2, 176, "items:176", false, false, 0, 16711680, true, true, -1, -1, 557));
		items.add(new ItemDef("Firebird Feather", "This is cool enough to hold now", "", 2, 176, "items:176", false, false, 0, 16768256, true, true, -1, -1, 558));
		items.add(new ItemDef("Poisoned Iron dagger", "Short but pointy", "", 35, 80, "items:80", false, true, 16, 15654365, true, false, 1763, -1, 559));
		items.add(new ItemDef("Poisoned bronze dagger", "Short but pointy", "", 10, 80, "items:80", false, true, 16, 16737817, true, false, 1764, -1, 560));
		items.add(new ItemDef("Poisoned Steel dagger", "Short but pointy", "", 125, 80, "items:80", false, true, 16, 15658734, true, false, 1765, -1, 561));
		items.add(new ItemDef("Poisoned Mithril dagger", "Short but pointy", "", 325, 80, "items:80", false, true, 16, 10072780, true, false, 1766, -1, 562));
		items.add(new ItemDef("Poisoned Rune dagger", "Short but pointy", "", 8000, 80, "items:80", false, true, 16, 65535, true, false, 1767, -1, 563));
		items.add(new ItemDef("Poisoned Adamantite dagger", "Short but pointy", "", 800, 80, "items:80", false, true, 16, 11717785, true, false, 1768, -1, 564));
		items.add(new ItemDef("Poisoned Black dagger", "Short but pointy", "", 240, 80, "items:80", false, true, 16, 3158064, true, false, 1769, -1, 565));
		items.add(new ItemDef("Cure poison Potion", "3 doses of cure poison potion", "Drink", 288, 48, "items:48", false, false, 0, 6749969, true, false, 1770, -1, 566));
		items.add(new ItemDef("Cure poison Potion", "2 doses of cure poison potion", "Drink", 216, 436, "items:436", false, false, 0, 6749969, true, false, 1771, -1, 567));
		items.add(new ItemDef("Cure poison Potion", "1 dose of cure poison potion", "Drink", 144, 437, "items:437", false, false, 0, 6749969, true, false, 1772, -1, 568));
		items.add(new ItemDef("Poison antidote", "3 doses of anti poison potion", "Drink", 288, 48, "items:48", false, false, 0, 16716134, true, false, 1773, -1, 569));
		items.add(new ItemDef("Poison antidote", "2 doses of anti poison potion", "Drink", 216, 436, "items:436", false, false, 0, 16716134, true, false, 1774, -1, 570));
		items.add(new ItemDef("Poison antidote", "1 dose of anti poison potion", "Drink", 144, 437, "items:437", false, false, 0, 16716134, true, false, 1775, -1, 571));
		items.add(new ItemDef("weapon poison", "For use on daggers and arrows", "", 144, 48, "items:48", false, false, 0, 1140479, true, false, 1776, -1, 572));
		items.add(new ItemDef("ID Paper", "ID of Hartigen the black knight", "", 1, 29, "items:29", false, false, 0, 0, true, false, 1777, -1, 573));
		items.add(new ItemDef("Poison Bronze Arrows", "Venomous looking arrows", "", 2, 206, "items:206", true, false, 0, 16737817, true, false, -1, -1, 574));
		items.add(new ItemDef("Christmas cracker", "Use on another player to pull it", "", 1, 188, "items:188", false, false, 0, 16711680, false, false, 1778, -1, 575));
		items.add(new ItemDef("Party Hat", "Party!!!", "", 2, 189, "items:189", false, true, 32, 16711680, false, false, 1779, -1, 576));
		items.add(new ItemDef("Party Hat", "Party!!!", "", 2, 189, "items:189", false, true, 32, 16776960, false, false, 1780, -1, 577));
		items.add(new ItemDef("Party Hat", "Party!!!", "", 2, 189, "items:189", false, true, 32, 255, false, false, 1781, -1, 578));
		items.add(new ItemDef("Party Hat", "Party!!!", "", 2, 189, "items:189", false, true, 32, 65280, false, false, 1782, -1, 579));
		items.add(new ItemDef("Party Hat", "Party!!!", "", 2, 189, "items:189", false, true, 32, 16711935, false, false, 1783, -1, 580));
		items.add(new ItemDef("Party Hat", "Party!!!", "", 2, 189, "items:189", false, true, 32, 0, false, false, 1784, -1, 581));
		items.add(new ItemDef("Miscellaneous key", "I wonder what this unlocks", "", 1, 25, "items:25", false, false, 0, 14509670, true, false, 1785, -1, 582));
		items.add(new ItemDef("Bunch of keys", "Some keys on a keyring", "", 2, 190, "items:190", false, false, 0, 0, true, false, 1786, -1, 583));
		items.add(new ItemDef("Whisky", "A bottle of Draynor Malt", "drink", 5, 191, "items:191", false, false, 0, 16755200, true, false, 1787, -1, 584));
		items.add(new ItemDef("Candlestick", "A valuable candlestick", "", 5, 192, "items:192", false, false, 0, 0, true, false, 1788, -1, 585));
		items.add(new ItemDef("Master thief armband", "This denotes a great act of thievery", "", 2, 193, "items:193", false, false, 0, 0, true, true, -1, -1, 586));
		items.add(new ItemDef("Blamish snail slime", "Yuck", "", 5, 104, "items:104", false, false, 0, 15663086, true, true, -1, -1, 587));
		items.add(new ItemDef("Blamish oil", "made from the finest snail slime", "", 10, 48, "items:48", false, false, 0, 15663086, true, true, -1, -1, 588));
		items.add(new ItemDef("Oily Fishing Rod", "A rod covered in Blamish oil", "", 15, 172, "items:172", false, false, 0, 0, true, true, -1, -1, 589));
		items.add(new ItemDef("lava eel", "Strange it looks cooler now it's been cooked", "eat", 150, 194, "items:194", false, false, 0, 11558912, true, true, -1, -1, 590));
		items.add(new ItemDef("Raw lava eel", "A very strange eel", "", 150, 194, "items:194", false, false, 0, 16711680, true, true, -1, -1, 591));
		items.add(new ItemDef("Poison Crossbow bolts", "Good if you have a crossbow!", "", 3, 56, "items:56", true, false, 0, 0, true, false, -1, -1, 592));
		items.add(new ItemDef("Dragon sword", "A Razor sharp sword", "", 100000, 273, "items:273", false, true, 16, 16711748, true, false, 1789, -1, 593));
		items.add(new ItemDef("Dragon axe", "A vicious looking axe", "", 200000, 272, "items:272", false, true, 16, 16711748, true, false, 1790, -1, 594));
		items.add(new ItemDef("Jail keys", "Keys to the black knight jail", "", 2, 190, "items:190", false, false, 0, 0, true, true, -1, -1, 595));
		items.add(new ItemDef("Dusty Key", "A key given to me by Velrak", "", 1, 25, "items:25", false, false, 0, 12303291, true, true, -1, -1, 596));
		items.add(new ItemDef("Charged Dragonstone Amulet", "A very powerful amulet", "rub", 17625, 125, "items:125", false, true, 1024, 12255487, true, false, 1791, -1, 597));
		items.add(new ItemDef("Grog", "A murky glass of some sort of drink", "drink", 3, 90, "items:90", false, false, 0, 0, true, false, 1792, -1, 598));
		items.add(new ItemDef("Candle", "An unlit candle", "", 3, 195, "items:195", false, false, 0, 0, true, true, -1, -1, 599));
		items.add(new ItemDef("black Candle", "A spooky but unlit candle", "", 3, 195, "items:195", false, false, 0, 2105376, true, true, -1, -1, 600));
		items.add(new ItemDef("Candle", "A small slowly burning candle", "", 3, 196, "items:196", false, false, 0, 0, true, true, -1, -1, 601));
		items.add(new ItemDef("black Candle", "A spooky candle", "", 3, 196, "items:196", false, false, 0, 2105376, true, true, -1, -1, 602));
		items.add(new ItemDef("insect repellant", "Drives away all known 6 legged creatures", "", 3, 197, "items:197", false, false, 0, 0, true, true, -1, -1, 603));
		items.add(new ItemDef("Bat bones", "Ew it's a pile of bones", "Bury", 1, 20, "items:20", false, false, 0, 0, true, false, 1793, -1, 604));
		items.add(new ItemDef("wax Bucket", "It's a wooden bucket", "", 2, 22, "items:22", false, false, 0, 16777181, true, true, -1, -1, 605));
		items.add(new ItemDef("Excalibur", "This used to belong to king Arthur", "", 200, 115, "items:115", false, true, 16, 10072780, true, true, -1, -1, 606));
		items.add(new ItemDef("Druids robe", "I feel closer to the Gods when I am wearing this", "", 40, 87, "items:87", false, true, 64, 16777215, true, false, 1794, -1, 607));
		items.add(new ItemDef("Druids robe", "Keeps a druids's knees nice and warm", "", 30, 88, "items:88", false, true, 128, 16777215, true, false, 1795, -1, 608));
		items.add(new ItemDef("Eye patch", "It makes me look very piratical", "", 2, 198, "items:198", false, true, 32, 0, true, false, 1796, -1, 609));
		items.add(new ItemDef("Unenchanted Dragonstone Amulet", "I wonder if I can get this enchanted", "", 17625, 125, "items:125", false, true, 1024, 12255487, true, false, 1797, -1, 610));
		items.add(new ItemDef("Unpowered orb", "I'd prefer it if it was powered", "", 100, 199, "items:199", false, false, 0, 0, true, false, 1798, -1, 611));
		items.add(new ItemDef("Fire orb", "A magic glowing orb", "", 300, 199, "items:199", false, false, 0, 16711680, true, false, 1799, -1, 612));
		items.add(new ItemDef("Water orb", "A magic glowing orb", "", 300, 199, "items:199", false, false, 0, 255, true, false, 1800, -1, 613));
		items.add(new ItemDef("Battlestaff", "It's a slightly magical stick", "", 7000, 85, "items:85", false, true, 16, 10072780, true, false, 1801, -1, 614));
		items.add(new ItemDef("Battlestaff of fire", "A Magical staff", "", 15500, 91, "items:91", false, true, 16, 16711680, true, false, 1802, -1, 615));
		items.add(new ItemDef("Battlestaff of water", "A Magical staff", "", 15500, 91, "items:91", false, true, 16, 255, true, false, 1803, -1, 616));
		items.add(new ItemDef("Battlestaff of air", "A Magical staff", "", 15500, 91, "items:91", false, true, 16, 65535, true, false, 1804, -1, 617));
		items.add(new ItemDef("Battlestaff of earth", "A Magical staff", "", 15500, 91, "items:91", false, true, 16, 7353600, true, false, 1805, -1, 618));
		items.add(new ItemDef("Blood-Rune", "Used for high level missile spells", "", 25, 200, "items:200", true, false, 0, 0, true, false, -1, -1, 619));
		items.add(new ItemDef("Beer glass", "I need to fill this with beer", "", 2, 201, "items:201", false, false, 0, 0, false, false, 1806, -1, 620));
		items.add(new ItemDef("glassblowing pipe", "Use on molten glass to make things", "", 2, 202, "items:202", false, false, 0, 0, true, false, 1807, -1, 621));
		items.add(new ItemDef("seaweed", "slightly damp seaweed", "", 2, 203, "items:203", false, false, 0, 0, true, false, 1808, -1, 622));
		items.add(new ItemDef("molten glass", "hot glass ready to be blown", "", 2, 204, "items:204", false, false, 0, 0, true, false, 1809, -1, 623));
		items.add(new ItemDef("soda ash", "one of the ingredients for making glass", "", 2, 23, "items:23", false, false, 0, 0, true, false, 1810, -1, 624));
		items.add(new ItemDef("sand", "one of the ingredients for making glass", "", 2, 22, "items:22", false, false, 0, 16763904, true, false, 1811, -1, 625));
		items.add(new ItemDef("air orb", "A magic glowing orb", "", 300, 199, "items:199", false, false, 0, 65535, true, false, 1812, -1, 626));
		items.add(new ItemDef("earth orb", "A magic glowing orb", "", 300, 199, "items:199", false, false, 0, 7353600, true, false, 1813, -1, 627));
		items.add(new ItemDef("bass certificate", "Each certificate exchangable at Catherby for 5 bass", "", 10, 180, "items:180", true, false, 0, 0, true, false, -1, -1, 628));
		items.add(new ItemDef("Raw bass certificate", "Each certificate exchangable at Catherby for 5 raw bass", "", 10, 180, "items:180", true, false, 0, 0, true, false, -1, -1, 629));
		items.add(new ItemDef("shark certificate", "Each certificate exchangable at Catherby for 5 shark", "", 10, 180, "items:180", true, false, 0, 0, true, false, -1, -1, 630));
		items.add(new ItemDef("Raw shark certificate", "Each certificate exchangable at Catherby for 5 raw shark", "", 10, 180, "items:180", true, false, 0, 0, true, false, -1, -1, 631));
		items.add(new ItemDef("Oak Logs", "Logs cut from an oak tree", "", 20, 14, "items:14", false, false, 0, 0, true, false, 1814, -1, 632));
		items.add(new ItemDef("Willow Logs", "Logs cut from a willow tree", "", 40, 14, "items:14", false, false, 0, 0, true, false, 1815, -1, 633));
		items.add(new ItemDef("Maple Logs", "Logs cut from a maple tree", "", 80, 14, "items:14", false, false, 0, 0, true, false, 1816, -1, 634));
		items.add(new ItemDef("Yew Logs", "Logs cut from a yew tree", "", 160, 14, "items:14", false, false, 0, 0, true, false, 1817, -1, 635));
		items.add(new ItemDef("Magic Logs", "Logs made from magical wood", "", 320, 14, "items:14", false, false, 0, 0, true, false, 1818, -1, 636));
		items.add(new ItemDef("Headless Arrows", "I need to attach arrow heads to these", "", 1, 205, "items:205", true, false, 0, 0, true, false, -1, -1, 637));
		items.add(new ItemDef("Iron Arrows", "Arrows with iron heads", "", 6, 11, "items:11", true, false, 0, 15654365, true, false, -1, -1, 638));
		items.add(new ItemDef("Poison Iron Arrows", "Venomous looking arrows", "", 6, 206, "items:206", true, false, 0, 15654365, true, false, -1, -1, 639));
		items.add(new ItemDef("Steel Arrows", "Arrows with steel heads", "", 24, 11, "items:11", true, false, 0, 15658734, true, false, -1, -1, 640));
		items.add(new ItemDef("Poison Steel Arrows", "Venomous looking arrows", "", 24, 206, "items:206", true, false, 0, 15658734, true, false, -1, -1, 641));
		items.add(new ItemDef("Mithril Arrows", "Arrows with mithril heads", "", 64, 11, "items:11", true, false, 0, 9614028, true, false, -1, -1, 642));
		items.add(new ItemDef("Poison Mithril Arrows", "Venomous looking arrows", "", 64, 206, "items:206", true, false, 0, 9614028, true, false, -1, -1, 643));
		items.add(new ItemDef("Adamantite Arrows", "Arrows with adamantite heads", "", 160, 11, "items:11", true, false, 0, 11717785, true, false, -1, -1, 644));
		items.add(new ItemDef("Poison Adamantite Arrows", "Venomous looking arrows", "", 160, 206, "items:206", true, false, 0, 11717785, true, false, -1, -1, 645));
		items.add(new ItemDef("Rune Arrows", "Arrows with rune heads", "", 800, 11, "items:11", true, false, 0, 65535, true, false, -1, -1, 646));
		items.add(new ItemDef("Poison Rune Arrows", "Venomous looking arrows", "", 800, 206, "items:206", true, false, 0, 65535, true, false, -1, -1, 647));
		items.add(new ItemDef("Oak Longbow", "A Nice sturdy bow", "", 160, 54, "items:54", false, true, 24, 255, true, false, 1819, -1, 648));
		items.add(new ItemDef("Oak Shortbow", "Short but effective", "", 100, 55, "items:55", false, true, 24, 255, true, false, 1820, -1, 649));
		items.add(new ItemDef("Willow Longbow", "A Nice sturdy bow", "", 320, 54, "items:54", false, true, 24, 16776960, true, false, 1821, -1, 650));
		items.add(new ItemDef("Willow Shortbow", "Short but effective", "", 200, 55, "items:55", false, true, 24, 16776960, true, false, 1822, -1, 651));
		items.add(new ItemDef("Maple Longbow", "A Nice sturdy bow", "", 640, 54, "items:54", false, true, 24, 16746496, true, false, 1823, -1, 652));
		items.add(new ItemDef("Maple Shortbow", "Short but effective", "", 400, 55, "items:55", false, true, 24, 16746496, true, false, 1824, -1, 653));
		items.add(new ItemDef("Yew Longbow", "A Nice sturdy bow", "", 1280, 54, "items:54", false, true, 24, 16711680, true, false, 1825, -1, 654));
		items.add(new ItemDef("Yew Shortbow", "Short but effective", "", 800, 55, "items:55", false, true, 24, 16711680, true, false, 1826, -1, 655));
		items.add(new ItemDef("Magic Longbow", "A Nice sturdy bow", "", 2560, 54, "items:54", false, true, 24, 4210752, true, false, 1827, -1, 656));
		items.add(new ItemDef("Magic Shortbow", "Short but effective", "", 1600, 55, "items:55", false, true, 24, 4210752, true, false, 1828, -1, 657));
		items.add(new ItemDef("unstrung Oak Longbow", "I need to find a string for this", "", 80, 119, "items:119", false, false, 0, 255, true, false, 1829, -1, 658));
		items.add(new ItemDef("unstrung Oak Shortbow", "I need to find a string for this", "", 50, 120, "items:120", false, false, 0, 255, true, false, 1830, -1, 659));
		items.add(new ItemDef("unstrung Willow Longbow", "I need to find a string for this", "", 160, 119, "items:119", false, false, 0, 16776960, true, false, 1831, -1, 660));
		items.add(new ItemDef("unstrung Willow Shortbow", "I need to find a string for this", "", 100, 120, "items:120", false, false, 0, 16776960, true, false, 1832, -1, 661));
		items.add(new ItemDef("unstrung Maple Longbow", "I need to find a string for this", "", 320, 119, "items:119", false, false, 0, 16744448, true, false, 1833, -1, 662));
		items.add(new ItemDef("unstrung Maple Shortbow", "I need to find a string for this", "", 200, 120, "items:120", false, false, 0, 16744448, true, false, 1834, -1, 663));
		items.add(new ItemDef("unstrung Yew Longbow", "I need to find a string for this", "", 640, 119, "items:119", false, false, 0, 16711680, true, false, 1835, -1, 664));
		items.add(new ItemDef("unstrung Yew Shortbow", "I need to find a string for this", "", 400, 120, "items:120", false, false, 0, 16711680, true, false, 1836, -1, 665));
		items.add(new ItemDef("unstrung Magic Longbow", "I need to find a string for this", "", 1280, 119, "items:119", false, false, 0, 4210752, true, false, 1837, -1, 666));
		items.add(new ItemDef("unstrung Magic Shortbow", "I need to find a string for this", "", 800, 120, "items:120", false, false, 0, 4210752, true, false, 1838, -1, 667));
		items.add(new ItemDef("barcrawl card", "The official Alfred Grimhand barcrawl", "read", 10, 180, "items:180", false, false, 0, 0, true, true, -1, -1, 668));
		items.add(new ItemDef("bronze arrow heads", "Not much use without the rest of the arrow!", "", 1, 207, "items:207", true, false, 0, 16737817, true, false, -1, -1, 669));
		items.add(new ItemDef("iron arrow heads", "Not much use without the rest of the arrow!", "", 3, 207, "items:207", true, false, 0, 15658717, true, false, -1, -1, 670));
		items.add(new ItemDef("steel arrow heads", "Not much use without the rest of the arrow!", "", 12, 207, "items:207", true, false, 0, 15658734, true, false, -1, -1, 671));
		items.add(new ItemDef("mithril arrow heads", "Not much use without the rest of the arrow!", "", 32, 207, "items:207", true, false, 0, 10072780, true, false, -1, -1, 672));
		items.add(new ItemDef("adamantite arrow heads", "Not much use without the rest of the arrow!", "", 80, 207, "items:207", true, false, 0, 11717785, true, false, -1, -1, 673));
		items.add(new ItemDef("rune arrow heads", "Not much use without the rest of the arrow!", "", 400, 207, "items:207", true, false, 0, 65535, true, false, -1, -1, 674));
		items.add(new ItemDef("flax", "I should use this with a spinning wheel", "", 5, 209, "items:209", false, false, 0, 0, true, false, 1839, -1, 675));
		items.add(new ItemDef("bow string", "I need a bow handle to attach this too", "", 10, 208, "items:208", false, false, 0, 0, true, false, 1840, -1, 676));
		items.add(new ItemDef("Easter egg", "Happy Easter", "eat", 10, 210, "items:210", false, false, 0, 0, false, false, 1841, -1, 677));
		items.add(new ItemDef("scorpion cage", "I need to catch some scorpions in this", "", 10, 211, "items:211", false, false, 0, 0, true, true, -1, -1, 678));
		items.add(new ItemDef("scorpion cage", "It has 1 scorpion in it", "", 10, 212, "items:212", false, false, 0, 0, true, true, -1, -1, 679));
		items.add(new ItemDef("scorpion cage", "It has 2 scorpions in it", "", 10, 212, "items:212", false, false, 0, 0, true, true, -1, -1, 680));
		items.add(new ItemDef("scorpion cage", "It has 3 scorpions in it", "", 10, 212, "items:212", false, false, 0, 0, true, true, -1, -1, 681));
		items.add(new ItemDef("Enchanted Battlestaff of fire", "A Magical staff", "", 42500, 91, "items:91", false, true, 16, 16711680, true, false, 1842, -1, 682));
		items.add(new ItemDef("Enchanted Battlestaff of water", "A Magical staff", "", 42500, 91, "items:91", false, true, 16, 255, true, false, 1843, -1, 683));
		items.add(new ItemDef("Enchanted Battlestaff of air", "A Magical staff", "", 42500, 91, "items:91", false, true, 16, 65535, true, false, 1844, -1, 684));
		items.add(new ItemDef("Enchanted Battlestaff of earth", "A Magical staff", "", 42500, 91, "items:91", false, true, 16, 7353600, true, false, 1845, -1, 685));
		items.add(new ItemDef("scorpion cage", "It has 1 scorpion in it", "", 10, 212, "items:212", false, false, 0, 0, true, true, -1, -1, 686));
		items.add(new ItemDef("scorpion cage", "It has 1 scorpion in it", "", 10, 212, "items:212", false, false, 0, 0, true, true, -1, -1, 687));
		items.add(new ItemDef("scorpion cage", "It has 2 scorpions in it", "", 10, 212, "items:212", false, false, 0, 0, true, true, -1, -1, 688));
		items.add(new ItemDef("scorpion cage", "It has 2 scorpions in it", "", 10, 212, "items:212", false, false, 0, 0, true, true, -1, -1, 689));
		items.add(new ItemDef("gold", "this needs refining", "", 150, 73, "items:73", false, false, 0, 16763980, true, true, -1, -1, 690));
		items.add(new ItemDef("gold bar", "this looks valuable", "", 300, 79, "items:79", false, false, 0, 16763980, true, true, -1, -1, 691));
		items.add(new ItemDef("Ruby ring", "A valuable ring", "", 2025, 123, "items:123", false, false, 0, 16724736, true, true, -1, -1, 692));
		items.add(new ItemDef("Ruby necklace", "I wonder if this is valuable", "", 2175, 57, "items:57", false, true, 1024, 16724736, true, true, -1, -1, 693));
		items.add(new ItemDef("Family crest", "The crest of a varrocian noble family", "", 10, 213, "items:213", false, false, 0, 0, true, true, -1, -1, 694));
		items.add(new ItemDef("Crest fragment", "Part of the Fitzharmon family crest", "", 10, 214, "items:214", false, false, 0, 0, true, true, -1, -1, 695));
		items.add(new ItemDef("Crest fragment", "Part of the Fitzharmon family crest", "", 10, 215, "items:215", false, false, 0, 0, true, true, -1, -1, 696));
		items.add(new ItemDef("Crest fragment", "Part of the Fitzharmon family crest", "", 10, 216, "items:216", false, false, 0, 0, true, true, -1, -1, 697));
		items.add(new ItemDef("Steel gauntlets", "Very handy armour", "", 6, 217, "items:217", false, true, 256, 12303291, true, true, -1, -1, 698));
		items.add(new ItemDef("gauntlets of goldsmithing", "metal gloves for gold making", "", 6, 217, "items:217", false, true, 256, 16777130, true, true, -1, -1, 699));
		items.add(new ItemDef("gauntlets of cooking", "Used for cooking fish", "", 6, 217, "items:217", false, true, 256, 14540253, true, true, -1, -1, 700));
		items.add(new ItemDef("gauntlets of chaos", "improves bolt spells", "", 6, 217, "items:217", false, true, 256, 16755370, true, true, -1, -1, 701));
		items.add(new ItemDef("robe of Zamorak", "A robe worn by worshippers of Zamorak", "", 40, 87, "items:87", false, true, 64, 16711680, true, false, 1846, -1, 702));
		items.add(new ItemDef("robe of Zamorak", "A robe worn by worshippers of Zamorak", "", 30, 88, "items:88", false, true, 128, 16711680, true, false, 1847, -1, 703));
		items.add(new ItemDef("Address Label", "To lord Handelmort- Handelmort mansion", "", 10, 218, "items:218", false, false, 0, 0, true, true, -1, -1, 704));
		items.add(new ItemDef("Tribal totem", "It represents some sort of tribal god", "", 10, 219, "items:219", false, false, 0, 0, true, true, -1, -1, 705));
		items.add(new ItemDef("tourist guide", "Your definitive guide to Ardougne", "read", 1, 28, "items:28", false, false, 0, 11184895, true, false, 1848, -1, 706));
		items.add(new ItemDef("spice", "Put it in uncooked stew to make curry", "", 230, 62, "items:62", false, false, 0, 16711680, true, false, 1849, -1, 707));
		items.add(new ItemDef("Uncooked curry", "I need to cook this", "", 10, 162, "items:162", false, false, 0, 15643494, true, false, 1850, -1, 708));
		items.add(new ItemDef("curry", "It's a spicey hot curry", "Eat", 20, 162, "items:162", false, false, 0, 12274688, true, false, 1851, -1, 709));
		items.add(new ItemDef("Burnt curry", "Eew it's horribly burnt", "Empty", 1, 162, "items:162", false, false, 0, 5255216, true, false, 1852, -1, 710));
		items.add(new ItemDef("yew logs certificate", "Each certificate exchangable at Ardougne for 5 yew logs", "", 10, 180, "items:180", true, false, 0, 0, true, false, -1, -1, 711));
		items.add(new ItemDef("maple logs certificate", "Each certificate exchangable at Ardougne for 5 maple logs", "", 20, 180, "items:180", true, false, 0, 0, true, false, -1, -1, 712));
		items.add(new ItemDef("willow logs certificate", "Each certificate exchangable at Ardougne for 5 willow logs", "", 30, 180, "items:180", true, false, 0, 0, true, false, -1, -1, 713));
		items.add(new ItemDef("lockpick", "It makes picking some locks easier", "", 20, 220, "items:220", false, false, 0, 0, true, false, 1853, -1, 714));
		items.add(new ItemDef("Red vine worms", "Strange little red worms", "", 3, 175, "items:175", true, false, 0, 16711680, true, true, -1, -1, 715));
		items.add(new ItemDef("Blanket", "A child's blanket", "", 5, 92, "items:92", false, false, 0, 56831, true, true, -1, -1, 716));
		items.add(new ItemDef("Raw giant carp", "I should try cooking this", "", 50, 165, "items:165", false, false, 0, 80, true, true, -1, -1, 717));
		items.add(new ItemDef("giant Carp", "Some nicely cooked fish", "Eat", 50, 165, "items:165", false, false, 0, 12619984, true, true, -1, -1, 718));
		items.add(new ItemDef("Fishing competition Pass", "Admits one to the Hemenster fishing competition", "", 10, 218, "items:218", false, false, 0, 0, true, true, -1, -1, 719));
		items.add(new ItemDef("Hemenster fishing trophy", "Hurrah you won a fishing competition", "", 20, 221, "items:221", false, false, 0, 16763980, true, true, -1, -1, 720));
		items.add(new ItemDef("Pendant of Lucien", "Gets me through the chamber of fear", "", 12, 222, "items:222", false, true, 1024, 3158064, true, true, -1, -1, 721));
		items.add(new ItemDef("Boots of lightfootedness", "Wearing these makes me feel like I am floating", "", 6, 223, "items:223", false, true, 512, 16742144, true, true, -1, -1, 722));
		items.add(new ItemDef("Ice Arrows", "Can only be fired with yew or magic bows", "", 2, 11, "items:11", true, false, 0, 11206655, true, true, -1, -1, 723));
		items.add(new ItemDef("Lever", "This was once attached to something", "", 20, 224, "items:224", false, false, 0, 0, true, true, -1, -1, 724));
		items.add(new ItemDef("Staff of Armadyl", "A Magical staff", "", 15, 91, "items:91", false, true, 16, 16776960, true, true, -1, -1, 725));
		items.add(new ItemDef("Pendant of Armadyl", "Allows me to fight Lucien", "", 12, 222, "items:222", false, true, 1024, 0, true, true, -1, -1, 726));
		items.add(new ItemDef("Large cog", " A large old cog", "", 10, 241, "items:241", false, false, 0, 255, true, true, -1, -1, 727));
		items.add(new ItemDef("Large cog", " A large old cog", "", 10, 240, "items:240", false, false, 0, 0, true, true, -1, -1, 728));
		items.add(new ItemDef("Large cog", " A large old cog", "", 10, 241, "items:241", false, false, 0, 16711680, true, true, -1, -1, 729));
		items.add(new ItemDef("Large cog", " A large old cog", "", 10, 241, "items:241", false, false, 0, 13369548, true, true, -1, -1, 730));
		items.add(new ItemDef("Rat Poison", "This stuff looks nasty", "", 1, 52, "items:52", false, false, 0, 0, true, false, 1854, -1, 731));
		items.add(new ItemDef("shiny Key", "Quite a small key", "", 1, 25, "items:25", false, false, 0, 12303291, true, true, -1, -1, 732));
		items.add(new ItemDef("khazard Helmet", "A medium sized helmet", "", 10, 5, "items:5", false, true, 32, 11250603, true, true, -1, -1, 733));
		items.add(new ItemDef("khazard chainmail", "A series of connected metal rings", "", 10, 7, "items:7", false, true, 64, 11250603, true, true, -1, -1, 734));
		items.add(new ItemDef("khali brew", "A bottle of khazard's worst brew", "drink", 5, 191, "items:191", false, false, 0, 16755200, true, false, 1855, -1, 735));
		items.add(new ItemDef("khazard cell keys", "Keys for General Khazard's cells", "", 1, 25, "items:25", false, false, 0, 14540253, true, true, -1, -1, 736));
		items.add(new ItemDef("Poison chalice", "A strange looking drink", "drink", 20, 225, "items:225", false, false, 0, 11206400, true, true, -1, -1, 737));
		items.add(new ItemDef("magic whistle", "A small tin whistle", "blow", 10, 226, "items:226", false, false, 0, 0, true, true, -1, -1, 738));
		items.add(new ItemDef("Cup of tea", "A nice cup of tea", "drink", 10, 227, "items:227", false, false, 0, 0, true, false, 1856, -1, 739));
		items.add(new ItemDef("orb of protection", "a strange glowing green orb", "", 1, 242, "items:242", false, false, 0, 14540253, true, true, -1, -1, 740));
		items.add(new ItemDef("orbs of protection", "two strange glowing green orbs", "", 1, 243, "items:243", false, false, 0, 14540253, true, true, -1, -1, 741));
		items.add(new ItemDef("Holy table napkin", "a cloth given to me by sir Galahad", "", 10, 92, "items:92", false, false, 0, 0, true, true, -1, -1, 742));
		items.add(new ItemDef("bell", "I wonder what happens when i ring it", "ring", 1, 228, "items:228", false, false, 0, 0, true, true, -1, -1, 743));
		items.add(new ItemDef("Gnome Emerald Amulet of protection", "It improves my defense", "", 0, 125, "items:125", false, true, 1024, 3394611, true, true, -1, -1, 744));
		items.add(new ItemDef("magic golden feather", "It will point the way for me", "blow on", 2, 176, "items:176", false, false, 0, 16776960, true, true, -1, -1, 745));
		items.add(new ItemDef("Holy grail", "A holy and powerful artifact", "", 1, 229, "items:229", false, false, 0, 0, true, true, -1, -1, 746));
		items.add(new ItemDef("Script of Hazeel", "An old scroll with strange ancient text", "", 1, 244, "items:244", false, false, 0, 14540253, true, true, -1, -1, 747));
		items.add(new ItemDef("Pineapple", "It can be cut up with a knife", "", 1, 124, "items:124", false, false, 0, 0, true, false, 1857, -1, 748));
		items.add(new ItemDef("Pineapple ring", "Exotic fruit", "eat", 1, 230, "items:230", false, false, 0, 0, true, false, 1858, -1, 749));
		items.add(new ItemDef("Pineapple Pizza", "A tropicana pizza", "Eat", 100, 155, "items:155", false, false, 0, 16777079, true, false, 1859, -1, 750));
		items.add(new ItemDef("Half pineapple Pizza", "Half of this pizza has been eaten", "Eat", 50, 156, "items:156", false, false, 0, 16777079, true, false, 1860, -1, 751));
		items.add(new ItemDef("Magic scroll", "Maybe I should read it", "read", 1, 244, "items:244", false, false, 0, 0, true, true, -1, -1, 752));
		items.add(new ItemDef("Mark of Hazeel", "A large metal amulet", "", 0, 245, "items:245", false, false, 0, 14540253, true, true, -1, -1, 753));
		items.add(new ItemDef("bloody axe of zamorak", "A vicious looking axe", "", 5000, 246, "items:246", false, true, 16, 15658734, true, true, -1, -1, 754));
		items.add(new ItemDef("carnillean armour", "the carnillean family armour", "", 65, 247, "items:247", false, false, 0, 15658734, true, true, -1, -1, 755));
		items.add(new ItemDef("Carnillean Key", "An old rusty key", "", 1, 25, "items:25", false, false, 0, 16772608, true, true, -1, -1, 756));
		items.add(new ItemDef("Cattle prod", "An old cattle prod", "", 15, 248, "items:248", false, false, 0, 16772608, true, true, -1, -1, 757));
		items.add(new ItemDef("Plagued sheep remains", "These sheep remains are infected", "", 0, 20, "items:20", false, false, 0, 14540253, true, true, -1, -1, 758));
		items.add(new ItemDef("Poisoned animal feed", "This looks nasty", "", 0, 250, "items:250", false, false, 0, 14540253, true, true, -1, -1, 759));
		items.add(new ItemDef("Protective jacket", "A thick heavy leather top", "", 50, 251, "items:251", false, true, 64, 14540253, true, true, -1, -1, 760));
		items.add(new ItemDef("Protective trousers", "A thick pair of leather trousers", "", 50, 252, "items:252", false, true, 644, 15654365, true, true, -1, -1, 761));
		items.add(new ItemDef("Plagued sheep remains", "These sheep remains are infected", "", 0, 20, "items:20", false, false, 0, 14540253, true, true, -1, -1, 762));
		items.add(new ItemDef("Plagued sheep remains", "These sheep remains are infected", "", 0, 20, "items:20", false, false, 0, 14540253, true, true, -1, -1, 763));
		items.add(new ItemDef("Plagued sheep remains", "These sheep remains are infected", "", 0, 20, "items:20", false, false, 0, 14540253, true, true, -1, -1, 764));
		items.add(new ItemDef("dwellberries", "some rather pretty blue berries", "eat", 4, 253, "items:253", false, false, 0, 0, true, false, 1861, -1, 765));
		items.add(new ItemDef("Gasmask", "Stops me breathing nasty stuff", "", 2, 232, "items:232", false, true, 32, 0, true, true, -1, -1, 766));
		items.add(new ItemDef("picture", "A picture of a lady called Elena", "", 2, 233, "items:233", false, false, 0, 0, true, true, -1, -1, 767));
		items.add(new ItemDef("Book", "Turnip growing for beginners", "read", 1, 28, "items:28", false, false, 0, 16755455, true, true, -1, -1, 768));
		items.add(new ItemDef("Seaslug", "a rather nasty looking crustacean", "", 4, 254, "items:254", false, false, 0, 0, true, true, -1, -1, 769));
		items.add(new ItemDef("chocolaty milk", "Milk with chocolate in it", "drink", 2, 22, "items:22", false, false, 0, 9785408, true, true, -1, -1, 770));
		items.add(new ItemDef("Hangover cure", "It doesn't look very tasty", "", 2, 22, "items:22", false, false, 0, 8757312, true, true, -1, -1, 771));
		items.add(new ItemDef("Chocolate dust", "I prefer it in a bar shape", "", 2, 23, "items:23", false, false, 0, 9461792, true, false, 1862, -1, 772));
		items.add(new ItemDef("Torch", "A unlit home made torch", "", 4, 255, "items:255", false, false, 0, 0, true, true, -1, -1, 773));
		items.add(new ItemDef("Torch", "A lit home made torch", "", 4, 256, "items:256", false, false, 0, 0, true, true, -1, -1, 774));
		items.add(new ItemDef("warrant", "A search warrant for a house in Ardougne", "", 5, 29, "items:29", false, false, 0, 0, true, true, -1, -1, 775));
		items.add(new ItemDef("Damp sticks", "Some damp wooden sticks", "", 0, 257, "items:257", false, false, 0, 0, true, true, -1, -1, 776));
		items.add(new ItemDef("Dry sticks", "Some dry wooden sticks", "rub together", 0, 258, "items:258", false, false, 0, 0, true, true, -1, -1, 777));
		items.add(new ItemDef("Broken glass", "Glass from a broken window pane", "", 0, 259, "items:259", false, false, 0, 0, true, true, -1, -1, 778));
		items.add(new ItemDef("oyster pearls", "I could work wonders with these and a chisel", "", 1400, 260, "items:260", false, false, 0, 0, true, false, 1863, -1, 779));
		items.add(new ItemDef("little key", "Quite a small key", "", 1, 25, "items:25", false, false, 0, 12303291, true, true, -1, -1, 780));
		items.add(new ItemDef("Scruffy note", "It seems to say hongorer lure", "read", 2, 234, "items:234", false, false, 0, 0, true, false, 1864, -1, 781));
		items.add(new ItemDef("Glarial's amulet", "A bright green gem set in a necklace", "", 1, 261, "items:261", false, true, 1024, 12303291, true, true, -1, -1, 782));
		items.add(new ItemDef("Swamp tar", "A foul smelling thick tar like substance", "", 1, 262, "items:262", true, false, 0, 12303291, true, false, -1, -1, 783));
		items.add(new ItemDef("Uncooked Swamp paste", "A thick tar like substance mixed with flour", "", 1, 263, "items:263", true, false, 0, 12303291, true, false, -1, -1, 784));
		items.add(new ItemDef("Swamp paste", "A tar like substance mixed with flour and warmed", "", 30, 263, "items:263", true, false, 0, 12303291, true, false, -1, -1, 785));
		items.add(new ItemDef("Oyster pearl bolts", "Great if you have a crossbow!", "", 110, 266, "items:266", true, false, 0, 0, true, false, -1, -1, 786));
		items.add(new ItemDef("Glarials pebble", "A small pebble with elven inscription", "", 1, 264, "items:264", false, false, 0, 12303291, true, true, -1, -1, 787));
		items.add(new ItemDef("book on baxtorian", "A book on elven history in north runescape", "read", 2, 28, "items:28", false, false, 0, 0, true, true, -1, -1, 788));
		items.add(new ItemDef("large key", "I wonder what this is the key to", "", 1, 25, "items:25", false, false, 0, 16750848, true, true, -1, -1, 789));
		items.add(new ItemDef("Oyster pearl bolt tips", "Can be used to improve crossbow bolts", "", 56, 265, "items:265", true, false, 0, 12303291, true, false, -1, -1, 790));
		items.add(new ItemDef("oyster", "It's empty", "", 5, 267, "items:267", false, false, 0, 0, true, false, 1865, -1, 791));
		items.add(new ItemDef("oyster pearls", "I could work wonders with these and a chisel", "", 112, 268, "items:268", false, false, 0, 0, true, false, 1866, -1, 792));
		items.add(new ItemDef("oyster", "It's a rare oyster", "open", 200, 269, "items:269", false, false, 0, 0, true, false, 1867, -1, 793));
		items.add(new ItemDef("Soil", "It's a bucket of fine soil", "", 2, 22, "items:22", false, false, 0, 12285815, true, true, -1, -1, 794));
		items.add(new ItemDef("Dragon medium Helmet", "A medium sized helmet", "", 100000, 271, "items:271", false, true, 32, 16711748, true, false, 1868, -1, 795));
		items.add(new ItemDef("Mithril seed", "Magical seeds in a mithril case", "open", 200, 270, "items:270", true, false, 0, 0, true, true, -1, -1, 796));
		items.add(new ItemDef("An old key", "A door key", "", 1, 25, "items:25", false, false, 0, 15636736, true, true, -1, -1, 797));
		items.add(new ItemDef("pigeon cage", "It's for holding pigeons", "", 1, 274, "items:274", false, false, 0, 15636736, true, true, -1, -1, 798));
		items.add(new ItemDef("Messenger pigeons", "some very plump birds", "release", 1, 275, "items:275", false, false, 0, 15636736, true, true, -1, -1, 799));
		items.add(new ItemDef("Bird feed", "A selection of mixed seeds", "", 1, 276, "items:276", false, false, 0, 15636736, true, true, -1, -1, 800));
		items.add(new ItemDef("Rotten apples", "Yuck!", "eat", 1, 277, "items:277", false, false, 0, 15636736, true, true, -1, -1, 801));
		items.add(new ItemDef("Doctors gown", "I do feel clever wearing this", "", 40, 87, "items:87", false, true, 64, 16777215, true, true, -1, -1, 802));
		items.add(new ItemDef("Bronze key", "A heavy key", "", 1, 25, "items:25", false, false, 0, 16737817, true, true, -1, -1, 803));
		items.add(new ItemDef("Distillator", "It's for seperating compounds", "", 1, 278, "items:278", false, false, 0, 16737817, true, true, -1, -1, 804));
		items.add(new ItemDef("Glarial's urn", "An urn containing glarials ashes", "", 1, 279, "items:279", false, false, 0, 0, false, true, -1, -1, 805));
		items.add(new ItemDef("Glarial's urn", "An empty metal urn", "", 1, 280, "items:280", false, false, 0, 0, false, true, -1, -1, 806));
		items.add(new ItemDef("Priest robe", "I feel closer to saradomin in this", "", 5, 87, "items:87", false, true, 64, 1052688, false, false, 1869, -1, 807));
		items.add(new ItemDef("Priest gown", "I feel closer to saradomin in this", "", 5, 88, "items:88", false, true, 128, 1052688, false, false, 1870, -1, 808));
		items.add(new ItemDef("Liquid Honey", "This isn't worth much", "", 0, 48, "items:48", false, false, 0, 16776960, true, true, -1, -1, 809));
		items.add(new ItemDef("Ethenea", "An expensive colourless liquid", "", 10, 48, "items:48", false, false, 0, 11184827, true, true, -1, -1, 810));
		items.add(new ItemDef("Sulphuric Broline", "it's highly poisonous", "", 1, 48, "items:48", false, false, 0, 11966902, true, true, -1, -1, 811));
		items.add(new ItemDef("Plague sample", "An air tight tin container", "", 1, 281, "items:281", false, false, 0, 0, true, true, -1, -1, 812));
		items.add(new ItemDef("Touch paper", "For scientific testing", "", 1, 282, "items:282", false, false, 0, 0, true, true, -1, -1, 813));
		items.add(new ItemDef("Dragon Bones", "Ew it's a pile of bones", "Bury", 1, 137, "items:137", false, false, 0, 0, true, false, 1871, -1, 814));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, true, -1, -1, 815));
		items.add(new ItemDef("Snake Weed", "A very rare jungle herb", "", 5, 75, "items:75", false, false, 0, 0, true, true, -1, -1, 816));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, true, -1, -1, 817));
		items.add(new ItemDef("Ardrigal", "An interesting", "", 5, 75, "items:75", false, false, 0, 0, true, true, -1, -1, 818));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, true, -1, -1, 819));
		items.add(new ItemDef("Sito Foil", "An rare species of jungle herb", "", 5, 75, "items:75", false, false, 0, 0, true, true, -1, -1, 820));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, true, -1, -1, 821));
		items.add(new ItemDef("Volencia Moss", "A very rare species of jungle herb", "", 5, 75, "items:75", false, false, 0, 0, true, true, -1, -1, 822));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, true, -1, -1, 823));
		items.add(new ItemDef("Rogues Purse", " A rare species of jungle herb", "", 5, 75, "items:75", false, false, 0, 0, true, true, -1, -1, 824));
		items.add(new ItemDef("Soul-Rune", "Used for high level curse spells", "", 2500, 235, "items:235", true, false, 0, 0, true, false, -1, -1, 825));
		items.add(new ItemDef("king lathas Amulet", "The amulet is red", "", 10, 125, "items:125", false, true, 1024, 13382451, true, true, -1, -1, 826));
		items.add(new ItemDef("Bronze Spear", "A bronze tipped spear", "", 4, 283, "items:283", false, true, 16, 16737817, true, false, 1872, -1, 827));
		items.add(new ItemDef("halloween mask", "aaaarrrghhh ... i'm a monster", "", 15, 284, "items:284", false, true, 32, 65280, false, false, 1873, -1, 828));
		items.add(new ItemDef("Dragon bitter", "A glass of frothy ale", "drink", 2, 90, "items:90", false, false, 0, 0, true, false, 1874, -1, 829));
		items.add(new ItemDef("Greenmans ale", "A glass of frothy ale", "drink", 2, 90, "items:90", false, false, 0, 0, true, false, 1875, -1, 830));
		items.add(new ItemDef("halloween mask", "aaaarrrghhh ... i'm a monster", "", 15, 284, "items:284", false, true, 32, 16711680, false, false, 1876, -1, 831));
		items.add(new ItemDef("halloween mask", "aaaarrrghhh ... i'm a monster", "", 15, 284, "items:284", false, true, 32, 255, false, false, 1877, -1, 832));
		items.add(new ItemDef("cocktail glass", "For sipping cocktails", "", 0, 285, "items:285", false, false, 0, 0, true, false, 1878, -1, 833));
		items.add(new ItemDef("cocktail shaker", "For mixing cocktails", "pour", 2, 286, "items:286", false, false, 0, 0, true, false, 1879, -1, 834));
		items.add(new ItemDef("Bone Key", "A key delicately carved key made from a single piece of bone", "Look", 1, 25, "items:25", false, false, 0, 16777215, true, true, -1, -1, 835));
		items.add(new ItemDef("gnome robe", "A high fashion robe", "", 180, 88, "items:88", false, true, 128, 16755370, true, false, 1880, -1, 836));
		items.add(new ItemDef("gnome robe", "A high fashion robe", "", 180, 88, "items:88", false, true, 128, 11206570, true, false, 1881, -1, 837));
		items.add(new ItemDef("gnome robe", "A high fashion robe", "", 180, 88, "items:88", false, true, 128, 11184895, true, false, 1882, -1, 838));
		items.add(new ItemDef("gnome robe", "A high fashion robe", "", 180, 88, "items:88", false, true, 128, 16777164, true, false, 1883, -1, 839));
		items.add(new ItemDef("gnome robe", "A high fashion robe", "", 180, 88, "items:88", false, true, 128, 13434879, true, false, 1884, -1, 840));
		items.add(new ItemDef("gnomeshat", "A silly pointed hat", "", 160, 86, "items:86", false, true, 32, 16755370, true, false, 1885, -1, 841));
		items.add(new ItemDef("gnomeshat", "A silly pointed hat", "", 160, 86, "items:86", false, true, 32, 11206570, true, false, 1886, -1, 842));
		items.add(new ItemDef("gnomeshat", "A silly pointed hat", "", 160, 86, "items:86", false, true, 32, 11184895, true, false, 1887, -1, 843));
		items.add(new ItemDef("gnomeshat", "A silly pointed hat", "", 160, 86, "items:86", false, true, 32, 16777164, true, false, 1888, -1, 844));
		items.add(new ItemDef("gnomeshat", "A silly pointed hat", "", 160, 86, "items:86", false, true, 32, 13434879, true, false, 1889, -1, 845));
		items.add(new ItemDef("gnome top", "rometti - the ultimate in gnome design", "", 180, 87, "items:87", false, true, 64, 16755370, true, false, 1890, -1, 846));
		items.add(new ItemDef("gnome top", "rometti - the only name in gnome fashion!", "", 180, 87, "items:87", false, true, 64, 11206570, true, false, 1891, -1, 847));
		items.add(new ItemDef("gnome top", "rometti - the only name in gnome fashion!", "", 180, 87, "items:87", false, true, 64, 11184895, true, false, 1892, -1, 848));
		items.add(new ItemDef("gnome top", "rometti - the only name in gnome fashion!", "", 180, 87, "items:87", false, true, 64, 16777164, true, false, 1893, -1, 849));
		items.add(new ItemDef("gnome top", "rometti - the only name in gnome fashion!", "", 180, 87, "items:87", false, true, 64, 13434879, true, false, 1894, -1, 850));
		items.add(new ItemDef("gnome cocktail guide", "A book on tree gnome cocktails", "read", 2, 299, "items:299", false, false, 0, 0, true, false, 1895, -1, 851));
		items.add(new ItemDef("Beads of the dead", "A curious looking neck ornament", "", 35, 24, "items:24", false, true, 1024, 16737817, true, true, -1, -1, 852));
		items.add(new ItemDef("cocktail glass", "For sipping cocktails", "drink", 2, 288, "items:288", false, false, 0, 0, true, false, 1896, -1, 853));
		items.add(new ItemDef("cocktail glass", "For sipping cocktails", "drink", 2, 289, "items:289", false, false, 0, 0, true, false, 1897, -1, 854));
		items.add(new ItemDef("lemon", "It's very fresh", "eat", 2, 290, "items:290", false, false, 0, 0, true, false, 1898, -1, 855));
		items.add(new ItemDef("lemon slices", "It's very fresh", "eat", 2, 291, "items:291", false, false, 0, 16763952, true, false, 1899, -1, 856));
		items.add(new ItemDef("orange", "It's very fresh", "eat", 2, 292, "items:292", false, false, 0, 0, true, false, 1900, -1, 857));
		items.add(new ItemDef("orange slices", "It's very fresh", "eat", 2, 291, "items:291", false, false, 0, 16760880, true, false, 1901, -1, 858));
		items.add(new ItemDef("Diced orange", "Fresh chunks of orange", "eat", 2, 293, "items:293", false, false, 0, 16760880, true, false, 1902, -1, 859));
		items.add(new ItemDef("Diced lemon", "Fresh chunks of lemon", "eat", 2, 293, "items:293", false, false, 0, 16763952, true, false, 1903, -1, 860));
		items.add(new ItemDef("Fresh Pineapple", "It can be cut up with a knife", "eat", 1, 124, "items:124", false, false, 0, 0, true, false, 1904, -1, 861));
		items.add(new ItemDef("Pineapple chunks", "Fresh chunks of pineapple", "eat", 1, 293, "items:293", false, false, 0, 16760880, true, false, 1905, -1, 862));
		items.add(new ItemDef("lime", "It's very fresh", "eat", 2, 294, "items:294", false, false, 0, 0, true, false, 1906, -1, 863));
		items.add(new ItemDef("lime chunks", "Fresh chunks of lime", "eat", 1, 293, "items:293", false, false, 0, 65280, true, false, 1907, -1, 864));
		items.add(new ItemDef("lime slices", "It's very fresh", "eat", 2, 291, "items:291", false, false, 0, 65280, true, false, 1908, -1, 865));
		items.add(new ItemDef("fruit blast", "A cool refreshing fruit mix", "drink", 2, 295, "items:295", false, false, 0, 0, true, false, 1909, -1, 866));
		items.add(new ItemDef("odd looking cocktail", "A cool refreshing mix", "drink", 2, 289, "items:289", false, false, 0, 0, true, false, 1910, -1, 867));
		items.add(new ItemDef("Whisky", "A locally brewed Malt", "drink", 5, 191, "items:191", false, false, 0, 16755200, true, false, 1911, -1, 868));
		items.add(new ItemDef("vodka", "A strong spirit", "drink", 5, 191, "items:191", false, false, 0, 16755200, true, false, 1912, -1, 869));
		items.add(new ItemDef("gin", "A strong spirit", "drink", 5, 191, "items:191", false, false, 0, 16755200, true, false, 1913, -1, 870));
		items.add(new ItemDef("cream", "Fresh cream", "eat", 2, 296, "items:296", false, false, 0, 0, true, false, 1914, -1, 871));
		items.add(new ItemDef("Drunk dragon", "A warm creamy alcoholic beverage", "drink", 2, 297, "items:297", false, false, 0, 0, true, false, 1915, -1, 872));
		items.add(new ItemDef("Equa leaves", "Small sweet smelling leaves", "eat", 2, 298, "items:298", false, false, 0, 0, true, false, 1916, -1, 873));
		items.add(new ItemDef("SGG", "A short green guy..looks good", "drink", 2, 295, "items:295", false, false, 0, 0, true, false, 1917, -1, 874));
		items.add(new ItemDef("Chocolate saturday", "A warm creamy alcoholic beverage", "drink", 2, 297, "items:297", false, false, 0, 0, true, false, 1918, -1, 875));
		items.add(new ItemDef("brandy", "A strong spirit", "drink", 5, 191, "items:191", false, false, 0, 16755200, true, false, 1919, -1, 876));
		items.add(new ItemDef("blurberry special", "Looks good..smells strong", "drink", 2, 295, "items:295", false, false, 0, 0, true, false, 1920, -1, 877));
		items.add(new ItemDef("wizard blizzard", "Looks like a strange mix", "drink", 2, 295, "items:295", false, false, 0, 0, true, false, 1921, -1, 878));
		items.add(new ItemDef("pineapple punch", "A fresh healthy fruit mix", "drink", 2, 295, "items:295", false, false, 0, 0, true, false, 1922, -1, 879));
		items.add(new ItemDef("gnomebatta dough", "Dough formed into a base", "", 2, 300, "items:300", false, false, 0, 0, true, false, 1923, -1, 880));
		items.add(new ItemDef("gianne dough", "It's made from a secret recipe", "mould", 2, 301, "items:301", false, false, 0, 0, true, false, 1924, -1, 881));
		items.add(new ItemDef("gnomebowl dough", "Dough formed into a bowl shape", "", 2, 302, "items:302", false, false, 0, 0, true, false, 1925, -1, 882));
		items.add(new ItemDef("gnomecrunchie dough", "Dough formed into cookie shapes", "", 2, 303, "items:303", false, false, 0, 0, true, false, 1926, -1, 883));
		items.add(new ItemDef("gnomebatta", "A baked dough base", "", 2, 300, "items:300", false, false, 0, 0, true, false, 1927, -1, 884));
		items.add(new ItemDef("gnomebowl", "A baked dough bowl", "eat", 2, 302, "items:302", false, false, 0, 0, true, false, 1928, -1, 885));
		items.add(new ItemDef("gnomebatta", "It's burnt to a sinder", "", 2, 304, "items:304", false, false, 0, 0, true, false, 1929, -1, 886));
		items.add(new ItemDef("gnomecrunchie", "They're burnt to a sinder", "", 2, 306, "items:306", false, false, 0, 0, true, false, 1930, -1, 887));
		items.add(new ItemDef("gnomebowl", "It's burnt to a sinder", "", 2, 305, "items:305", false, false, 0, 0, true, false, 1931, -1, 888));
		items.add(new ItemDef("Uncut Red Topaz", "A semi precious stone", "", 40, 73, "items:73", false, false, 0, 16525133, true, false, 1932, -1, 889));
		items.add(new ItemDef("Uncut Jade", "A semi precious stone", "", 30, 73, "items:73", false, false, 0, 10025880, true, false, 1933, -1, 890));
		items.add(new ItemDef("Uncut Opal", "A semi precious stone", "", 20, 73, "items:73", false, false, 0, 16777124, true, false, 1934, -1, 891));
		items.add(new ItemDef("Red Topaz", "A semi precious stone", "", 200, 74, "items:74", false, false, 0, 16525133, true, false, 1935, -1, 892));
		items.add(new ItemDef("Jade", "A semi precious stone", "", 150, 74, "items:74", false, false, 0, 10025880, true, false, 1936, -1, 893));
		items.add(new ItemDef("Opal", "A semi precious stone", "", 100, 74, "items:74", false, false, 0, 16777124, true, false, 1937, -1, 894));
		items.add(new ItemDef("Swamp Toad", "Slippery little blighters", "remove legs", 2, 307, "items:307", false, false, 0, 0, true, false, 1938, -1, 895));
		items.add(new ItemDef("Toad legs", "Gnome delicacy apparently", "eat", 2, 308, "items:308", false, false, 0, 0, true, false, 1939, -1, 896));
		items.add(new ItemDef("King worm", "Gnome delicacy apparently", "eat", 2, 309, "items:309", false, false, 0, 0, true, false, 1940, -1, 897));
		items.add(new ItemDef("Gnome spice", "Aluft Giannes secret reciepe", "", 2, 310, "items:310", false, false, 0, 0, true, false, 1941, -1, 898));
		items.add(new ItemDef("gianne cook book", "Aluft Giannes favorite dishes", "read", 2, 299, "items:299", false, false, 0, 0, true, false, 1942, -1, 899));
		items.add(new ItemDef("gnomecrunchie", "yum ... smells good", "eat", 2, 311, "items:311", false, false, 0, 0, true, false, 1943, -1, 900));
		items.add(new ItemDef("cheese and tomato batta", "Smells really good", "eat", 2, 312, "items:312", false, false, 0, 0, true, false, 1944, -1, 901));
		items.add(new ItemDef("toad batta", "actually smells quite good", "eat", 2, 312, "items:312", false, false, 0, 0, true, false, 1945, -1, 902));
		items.add(new ItemDef("gnome batta", "smells like pants", "eat", 2, 312, "items:312", false, false, 0, 0, true, false, 1946, -1, 903));
		items.add(new ItemDef("worm batta", "actually smells quite good", "eat", 2, 312, "items:312", false, false, 0, 0, true, false, 1947, -1, 904));
		items.add(new ItemDef("fruit batta", "actually smells quite good", "eat", 2, 312, "items:312", false, false, 0, 0, true, false, 1948, -1, 905));
		items.add(new ItemDef("Veg batta", "well..it looks healthy", "eat", 2, 312, "items:312", false, false, 0, 0, true, false, 1949, -1, 906));
		items.add(new ItemDef("Chocolate bomb", "Looks great", "eat", 2, 313, "items:313", false, false, 0, 0, true, false, 1950, -1, 907));
		items.add(new ItemDef("Vegball", "Looks pretty healthy", "eat", 2, 314, "items:314", false, false, 0, 0, true, false, 1951, -1, 908));
		items.add(new ItemDef("worm hole", "actually smells quite good", "eat", 2, 315, "items:315", false, false, 0, 0, true, false, 1952, -1, 909));
		items.add(new ItemDef("Tangled toads legs", "actually smells quite good", "eat", 2, 316, "items:316", false, false, 0, 0, true, false, 1953, -1, 910));
		items.add(new ItemDef("Choc crunchies", "yum ... smells good", "eat", 2, 311, "items:311", false, false, 0, 0, true, false, 1954, -1, 911));
		items.add(new ItemDef("Worm crunchies", "actually smells quite good", "eat", 2, 311, "items:311", false, false, 0, 0, true, false, 1955, -1, 912));
		items.add(new ItemDef("Toad crunchies", "actually smells quite good", "eat", 2, 311, "items:311", false, false, 0, 0, true, false, 1956, -1, 913));
		items.add(new ItemDef("Spice crunchies", "yum ... smells good", "eat", 2, 311, "items:311", false, false, 0, 0, true, false, 1957, -1, 914));
		items.add(new ItemDef("Crushed Gemstone", "A gemstone that has been smashed", "", 2, 23, "items:23", false, false, 0, 16777215, true, false, 1958, -1, 915));
		items.add(new ItemDef("Blurberry badge", "an official cocktail maker", "", 2, 317, "items:317", false, false, 0, 16711680, true, false, 1959, -1, 916));
		items.add(new ItemDef("Gianne badge", "an official gianne chef", "", 2, 317, "items:317", false, false, 0, 65280, true, false, 1960, -1, 917));
		items.add(new ItemDef("tree gnome translation", "Translate the old gnome tounge", "read", 2, 299, "items:299", false, false, 0, 0, true, false, 1961, -1, 918));
		items.add(new ItemDef("Bark sample", "A sample from the grand tree", "", 2, 318, "items:318", false, false, 0, 0, true, true, -1, -1, 919));
		items.add(new ItemDef("War ship", "A model of a karamja warship", "play with", 2, 319, "items:319", false, false, 0, 0, true, false, 1962, -1, 920));
		items.add(new ItemDef("gloughs journal", "Glough's private notes", "read", 2, 299, "items:299", false, false, 0, 0, true, true, -1, -1, 921));
		items.add(new ItemDef("invoice", "A note with foreman's timber order", "read", 2, 234, "items:234", false, false, 0, 0, true, true, -1, -1, 922));
		items.add(new ItemDef("Ugthanki Kebab", "A strange smelling Kebab made from Ugthanki meat - it doesn't look too good", "eat", 20, 95, "items:95", false, false, 0, 0, true, false, 1963, -1, 923));
		items.add(new ItemDef("special curry", "It's a spicy hot curry", "Eat", 20, 162, "items:162", false, false, 0, 12274688, true, false, 1964, -1, 924));
		items.add(new ItemDef("glough's key", "Glough left this at anita's", "", 1, 25, "items:25", false, false, 0, 14540253, true, true, -1, -1, 925));
		items.add(new ItemDef("glough's notes", "Scribbled notes and diagrams", "read", 2, 234, "items:234", false, false, 0, 0, true, true, -1, -1, 926));
		items.add(new ItemDef("Pebble", "The pebble has an inscription", "", 2, 321, "items:321", false, false, 0, 0, true, true, -1, -1, 927));
		items.add(new ItemDef("Pebble", "The pebble has an inscription", "", 2, 322, "items:322", false, false, 0, 0, true, true, -1, -1, 928));
		items.add(new ItemDef("Pebble", "The pebble has an inscription", "", 2, 323, "items:323", false, false, 0, 0, true, true, -1, -1, 929));
		items.add(new ItemDef("Pebble", "The pebble has an inscription", "", 2, 324, "items:324", false, false, 0, 0, true, true, -1, -1, 930));
		items.add(new ItemDef("Daconia rock", "A magicaly crafted stone", "", 40, 73, "items:73", false, false, 0, 14540253, true, true, -1, -1, 931));
		items.add(new ItemDef("Sinister key", "You get a sense of dread from this key", "", 1, 25, "items:25", false, false, 0, 1118481, true, false, 1965, -1, 932));
		items.add(new ItemDef("Herb", "I need a closer look to identify this", "Identify", 1, 75, "items:75", false, false, 0, 0, true, false, 1966, -1, 933));
		items.add(new ItemDef("Torstol", "A useful herb", "", 25, 75, "items:75", false, false, 0, 0, true, false, 1967, -1, 934));
		items.add(new ItemDef("Unfinished potion", "I need Jangerberries to finish this Torstol potion", "", 25, 48, "items:48", false, false, 0, 12285696, true, false, 1968, -1, 935));
		items.add(new ItemDef("Jangerberries", "They don't look very ripe", "eat", 1, 21, "items:21", false, false, 0, 4497408, true, false, 1969, -1, 936));
		items.add(new ItemDef("fruit blast", "A cool refreshing fruit mix", "drink", 30, 295, "items:295", false, false, 0, 0, true, false, 1970, -1, 937));
		items.add(new ItemDef("blurberry special", "Looks good..smells strong", "drink", 30, 295, "items:295", false, false, 0, 0, true, false, 1971, -1, 938));
		items.add(new ItemDef("wizard blizzard", "Looks like a strange mix", "drink", 30, 295, "items:295", false, false, 0, 0, true, false, 1972, -1, 939));
		items.add(new ItemDef("pineapple punch", "A fresh healthy fruit mix", "drink", 30, 295, "items:295", false, false, 0, 0, true, false, 1973, -1, 940));
		items.add(new ItemDef("SGG", "A short green guy..looks good", "drink", 30, 295, "items:295", false, false, 0, 0, true, false, 1974, -1, 941));
		items.add(new ItemDef("Chocolate saturday", "A warm creamy alcoholic beverage", "drink", 30, 297, "items:297", false, false, 0, 0, true, false, 1975, -1, 942));
		items.add(new ItemDef("Drunk dragon", "A warm creamy alcoholic beverage", "drink", 30, 297, "items:297", false, false, 0, 0, true, false, 1976, -1, 943));
		items.add(new ItemDef("cheese and tomato batta", "Smells really good", "eat", 120, 312, "items:312", false, false, 0, 0, true, false, 1977, -1, 944));
		items.add(new ItemDef("toad batta", "actually smells quite good", "eat", 120, 312, "items:312", false, false, 0, 0, true, false, 1978, -1, 945));
		items.add(new ItemDef("gnome batta", "smells like pants", "eat", 120, 312, "items:312", false, false, 0, 0, true, false, 1979, -1, 946));
		items.add(new ItemDef("worm batta", "actually smells quite good", "eat", 120, 312, "items:312", false, false, 0, 0, true, false, 1980, -1, 947));
		items.add(new ItemDef("fruit batta", "actually smells quite good", "eat", 120, 312, "items:312", false, false, 0, 0, true, false, 1981, -1, 948));
		items.add(new ItemDef("Veg batta", "well..it looks healthy", "eat", 120, 312, "items:312", false, false, 0, 0, true, false, 1982, -1, 949));
		items.add(new ItemDef("Chocolate bomb", "Looks great", "eat", 160, 313, "items:313", false, false, 0, 0, true, false, 1983, -1, 950));
		items.add(new ItemDef("Vegball", "Looks pretty healthy", "eat", 150, 314, "items:314", false, false, 0, 0, true, false, 1984, -1, 951));
		items.add(new ItemDef("worm hole", "actually smells quite good", "eat", 150, 315, "items:315", false, false, 0, 0, true, false, 1985, -1, 952));
		items.add(new ItemDef("Tangled toads legs", "actually smells quite good", "eat", 160, 316, "items:316", false, false, 0, 0, true, false, 1986, -1, 953));
		items.add(new ItemDef("Choc crunchies", "yum ... smells good", "eat", 85, 311, "items:311", false, false, 0, 0, true, false, 1987, -1, 954));
		items.add(new ItemDef("Worm crunchies", "actually smells quite good", "eat", 85, 311, "items:311", false, false, 0, 0, true, false, 1988, -1, 955));
		items.add(new ItemDef("Toad crunchies", "actually smells quite good", "eat", 85, 311, "items:311", false, false, 0, 0, true, false, 1989, -1, 956));
		items.add(new ItemDef("Spice crunchies", "yum ... smells good", "eat", 85, 311, "items:311", false, false, 0, 0, true, false, 1990, -1, 957));
		items.add(new ItemDef("Stone-Plaque", "A stone plaque with carved letters in it", "Read", 5, 236, "items:236", false, false, 0, 0, true, true, -1, -1, 958));
		items.add(new ItemDef("Tattered Scroll", "An ancient tattered scroll", "Read", 5, 29, "items:29", false, false, 0, 255, true, true, -1, -1, 959));
		items.add(new ItemDef("Crumpled Scroll", "An ancient crumpled scroll", "Read", 5, 29, "items:29", false, false, 0, 12648448, true, true, -1, -1, 960));
		items.add(new ItemDef("Bervirius Tomb Notes", "Notes taken from the tomb of Bervirius", "Read", 5, 29, "items:29", false, false, 0, 16776960, true, true, -1, -1, 961));
		items.add(new ItemDef("Zadimus Corpse", "The remains of Zadimus", "Bury", 1, 237, "items:237", false, false, 0, 15400622, true, true, -1, -1, 962));
		items.add(new ItemDef("Potion of Zamorak", "It looks scary", "drink", 25, 48, "items:48", false, false, 0, 15636736, true, false, 1991, -1, 963));
		items.add(new ItemDef("Potion of Zamorak", "It looks scary", "drink", 25, 436, "items:436", false, false, 0, 15636736, true, false, 1992, -1, 964));
		items.add(new ItemDef("Potion of Zamorak", "It looks scary", "drink", 25, 437, "items:437", false, false, 0, 15636736, true, false, 1993, -1, 965));
		items.add(new ItemDef("Boots", "They're soft and silky", "", 200, 223, "items:223", false, true, 512, 16755370, true, false, 1994, -1, 966));
		items.add(new ItemDef("Boots", "They're soft and silky", "", 200, 223, "items:223", false, true, 512, 11206570, true, false, 1995, -1, 967));
		items.add(new ItemDef("Boots", "They're soft and silky", "", 200, 223, "items:223", false, true, 512, 11184895, true, false, 1996, -1, 968));
		items.add(new ItemDef("Boots", "They're soft and silky", "", 200, 223, "items:223", false, true, 512, 16777164, true, false, 1997, -1, 969));
		items.add(new ItemDef("Boots", "They're soft and silky", "", 200, 223, "items:223", false, true, 512, 13434879, true, false, 1998, -1, 970));
		items.add(new ItemDef("Santa's hat", "It's a santa claus' hat", "", 160, 325, "items:325", false, true, 32, 0, false, false, 1999, -1, 971));
		items.add(new ItemDef("Locating Crystal", "A magical crystal sphere", "Activate", 100, 199, "items:199", false, false, 0, 12648447, true, true, -1, -1, 972));
		items.add(new ItemDef("Sword Pommel", "An ivory sword pommel", "", 100, 334, "items:334", false, false, 0, 16777088, true, true, -1, -1, 973));
		items.add(new ItemDef("Bone Shard", "A slender piece of bone", "Look", 1, 238, "items:238", false, false, 0, 0, true, true, -1, -1, 974));
		items.add(new ItemDef("Steel Wire", "Useful for crafting items", "", 200, 326, "items:326", false, false, 0, 0, true, false, 2000, -1, 975));
		items.add(new ItemDef("Bone Beads", "Beads carved out of bone", "", 1, 239, "items:239", false, false, 0, 16777152, true, true, -1, -1, 976));
		items.add(new ItemDef("Rashiliya Corpse", "The remains of the Zombie Queen", "Bury", 1, 237, "items:237", false, false, 0, 16744576, true, true, -1, -1, 977));
		items.add(new ItemDef("ResetCrystal", "Helps reset things in game", "Activate", 100, 199, "items:199", false, false, 0, 182474, true, false, 2001, -1, 978));
		items.add(new ItemDef("Bronze Wire", "Useful for crafting items", "", 20, 326, "items:326", false, false, 0, 16737817, true, false, 2002, -1, 979));
		items.add(new ItemDef("Present", "Click to use this on a friend", "open", 160, 330, "items:330", false, false, 0, 0, false, false, 2003, -1, 980));
		items.add(new ItemDef("Gnome Ball", "Lets play", "shoot", 10, 327, "items:327", false, false, 0, 0, true, true, -1, -1, 981));
		items.add(new ItemDef("Papyrus", "Used for making notes", "", 9, 282, "items:282", false, false, 0, 0, true, false, 2004, -1, 982));
		items.add(new ItemDef("A lump of Charcoal", "a lump of cooked coal good for making marks.", "", 45, 73, "items:73", false, false, 0, 2105376, true, false, 2005, -1, 983));
		items.add(new ItemDef("Arrow", "linen wrapped around an arrow head", "", 10, 328, "items:328", false, false, 0, 0, true, true, -1, -1, 984));
		items.add(new ItemDef("Lit Arrow", "A flamming arrow", "", 10, 329, "items:329", true, false, 0, 0, true, true, -1, -1, 985));
		items.add(new ItemDef("Rocks", "A few Large rocks", "", 10, 331, "items:331", false, false, 0, 0, true, true, -1, -1, 986));
		items.add(new ItemDef("Paramaya Rest Ticket", "Allows you to rest in the luxurius Paramaya Inn", "", 5, 218, "items:218", false, false, 0, 0, true, true, -1, -1, 987));
		items.add(new ItemDef("Ship Ticket", "Allows you passage on the 'Lady of the Waves' ship.", "", 5, 218, "items:218", false, false, 0, 8454143, true, true, -1, -1, 988));
		items.add(new ItemDef("Damp cloth", "It smells as if it's been doused in alcohol", "", 10, 92, "items:92", false, false, 0, 0, true, true, -1, -1, 989));
		items.add(new ItemDef("Desert Boots", "Boots made specially for the desert", "", 20, 223, "items:223", false, true, 512, 16777215, true, false, 2006, -1, 990));
		items.add(new ItemDef("Orb of light", "The orb gives you a safe peaceful feeling", "", 10, 333, "items:333", false, false, 0, 0, true, true, -1, -1, 991));
		items.add(new ItemDef("Orb of light", "The orb gives you a safe peaceful feeling", "", 10, 345, "items:345", false, false, 0, 0, true, true, -1, -1, 992));
		items.add(new ItemDef("Orb of light", "The orb gives you a safe peaceful feeling", "", 10, 346, "items:346", false, false, 0, 0, true, true, -1, -1, 993));
		items.add(new ItemDef("Orb of light", "The orb gives you a safe peaceful feeling", "", 10, 347, "items:347", false, false, 0, 0, true, true, -1, -1, 994));
		items.add(new ItemDef("Railing", "A broken metal rod", "", 10, 335, "items:335", false, false, 0, 0, true, true, -1, -1, 995));
		items.add(new ItemDef("Randas's journal", "An old journal with several pages missing", "read", 1, 28, "items:28", false, false, 0, 15641258, true, true, -1, -1, 996));
		items.add(new ItemDef("Unicorn horn", "Poor unicorn went splat!", "", 20, 145, "items:145", false, false, 0, 0, true, true, -1, -1, 997));
		items.add(new ItemDef("Coat of Arms", "A symbol of truth and all that is good", "", 10, 348, "items:348", false, false, 0, 0, true, true, -1, -1, 998));
		items.add(new ItemDef("Coat of Arms", "A symbol of truth and all that is good", "", 10, 336, "items:336", false, false, 0, 0, true, true, -1, -1, 999));
		items.add(new ItemDef("Staff of Iban", "It's a slightly magical stick", "", 15, 337, "items:337", false, true, 8216, 0, true, true, -1, -1, 1000));
		items.add(new ItemDef("Dwarf brew", "It's a bucket of home made brew", "", 2, 22, "items:22", false, false, 0, 12285815, true, true, -1, -1, 1001));
		items.add(new ItemDef("Ibans Ashes", "A heap of ashes", "", 2, 23, "items:23", false, false, 0, 11184810, true, true, -1, -1, 1002));
		items.add(new ItemDef("Cat", "She's sleeping..i think!", "", 2, 338, "items:338", false, false, 0, 11184810, true, true, -1, -1, 1003));
		items.add(new ItemDef("A Doll of Iban", "A strange doll made from sticks and cloth", "search", 2, 339, "items:339", false, false, 0, 11184810, true, true, -1, -1, 1004));
		items.add(new ItemDef("Old Journal", "I wonder who wrote this!", "read", 1, 28, "items:28", false, false, 0, 16755370, true, true, -1, -1, 1005));
		items.add(new ItemDef("Klank's gauntlets", "Heavy hand protection", "", 6, 217, "items:217", false, true, 256, 12303291, true, true, -1, -1, 1006));
		items.add(new ItemDef("Iban's shadow", "A dark mystical liquid", "", 2, 340, "items:340", false, false, 0, 11184810, true, true, -1, -1, 1007));
		items.add(new ItemDef("Iban's conscience", "The remains of a dove that died long ago", "", 2, 341, "items:341", false, false, 0, 11184810, true, true, -1, -1, 1008));
		items.add(new ItemDef("Amulet of Othainian", "A strange looking amulet", "", 0, 125, "items:125", false, true, 1024, 12255487, true, true, -1, -1, 1009));
		items.add(new ItemDef("Amulet of Doomion", "A strange looking amulet", "", 0, 125, "items:125", false, true, 1024, 12255487, true, true, -1, -1, 1010));
		items.add(new ItemDef("Amulet of Holthion", "A strange looking amulet", "", 0, 125, "items:125", false, true, 1024, 12255487, true, true, -1, -1, 1011));
		items.add(new ItemDef("keep key", "A small prison key", "", 1, 25, "items:25", false, false, 0, 12303291, true, true, -1, -1, 1012));
		items.add(new ItemDef("Bronze Throwing Dart", "A deadly throwing dart with a bronze tip.", "", 2, 231, "items:231", true, true, 16, 16737817, true, false, -1, -1, 1013));
		items.add(new ItemDef("Prototype Throwing Dart", "A proto type of a deadly throwing dart.", "", 70, 231, "items:231", true, false, 0, 16737817, true, true, -1, -1, 1014));
		items.add(new ItemDef("Iron Throwing Dart", "A deadly throwing dart with an iron tip.", "", 5, 231, "items:231", true, true, 16, 15654365, true, false, -1, -1, 1015));
		items.add(new ItemDef("Full Water Skin", "A skinful of water", "", 30, 343, "items:343", false, false, 0, 8404992, true, false, 2007, -1, 1016));
		items.add(new ItemDef("Lens mould", "A peculiar mould in the shape of a disc", "", 10, 342, "items:342", false, false, 0, 0, true, true, -1, -1, 1017));
		items.add(new ItemDef("Lens", "A perfectly formed glass disc", "", 10, 344, "items:344", false, false, 0, 0, true, true, -1, -1, 1018));
		items.add(new ItemDef("Desert Robe", "Cool light robe to wear in the desert", "", 40, 88, "items:88", false, true, 128, 16777215, true, false, 2008, -1, 1019));
		items.add(new ItemDef("Desert Shirt", "A light cool shirt to wear in the desert", "", 40, 87, "items:87", false, true, 64, 16777215, true, false, 2009, -1, 1020));
		items.add(new ItemDef("Metal Key", "A large metalic key.", "", 1, 25, "items:25", false, false, 0, 12632256, true, true, -1, -1, 1021));
		items.add(new ItemDef("Slaves Robe Bottom", "A dirty desert skirt", "", 40, 88, "items:88", false, true, 128, 8421376, true, false, 2010, -1, 1022));
		items.add(new ItemDef("Slaves Robe Top", "A dirty desert shirt", "", 40, 87, "items:87", false, true, 64, 8421376, true, false, 2011, -1, 1023));
		items.add(new ItemDef("Steel Throwing Dart", "A deadly throwing dart with a steel tip.", "", 20, 231, "items:231", true, true, 16, 15658734, true, false, -1, -1, 1024));
		items.add(new ItemDef("Astrology Book", "A book on Astrology in runescape", "Read", 2, 28, "items:28", false, false, 0, 0, true, true, -1, -1, 1025));
		items.add(new ItemDef("Unholy Symbol mould", "use this with silver in a furnace", "", 200, 349, "items:349", false, false, 0, 0, true, true, -1, -1, 1026));
		items.add(new ItemDef("Unholy Symbol of Zamorak", "this needs stringing", "", 200, 350, "items:350", false, false, 0, 0, true, true, -1, -1, 1027));
		items.add(new ItemDef("Unblessed Unholy Symbol of Zamorak", "this needs blessing", "", 200, 351, "items:351", false, true, 1024, 0, true, true, -1, -1, 1028));
		items.add(new ItemDef("Unholy Symbol of Zamorak", "a symbol indicating allegiance to Zamorak", "", 200, 351, "items:351", false, true, 1024, 0, true, true, -1, -1, 1029));
		items.add(new ItemDef("Shantay Desert Pass", "Allows you into the desert through the Shantay pass worth 5 gold.", "", 5, 218, "items:218", true, false, 0, 13083169, true, true, -1, -1, 30));
		items.add(new ItemDef("Staff of Iban", "The staff is damaged", "wield", 15, 337, "items:337", false, false, 0, 0, true, true, -1, -1, 1031));
		items.add(new ItemDef("Dwarf cannon base", "bang", "set down", 200000, 352, "items:352", false, false, 0, 0, true, false, 2012, -1, 1032));
		items.add(new ItemDef("Dwarf cannon stand", "bang", "", 200000, 353, "items:353", false, false, 0, 0, true, false, 2013, -1, 1033));
		items.add(new ItemDef("Dwarf cannon barrels", "bang", "", 200000, 354, "items:354", false, false, 0, 0, true, false, 2014, -1, 1034));
		items.add(new ItemDef("Dwarf cannon furnace", "bang", "", 200000, 355, "items:355", false, false, 0, 0, true, false, 2015, -1, 1035));
		items.add(new ItemDef("Fingernails", "Ugh gross!", "", 0, 356, "items:356", false, false, 0, 0, true, true, -1, -1, 1036));
		items.add(new ItemDef("Powering crystal1", "An intricately cut gemstone", "", 0, 357, "items:357", false, false, 0, 16777011, true, true, -1, -1, 1037));
		items.add(new ItemDef("Mining Barrel", "A roughly constructed barrel for carrying rock.", "", 100, 358, "items:358", false, false, 0, 65280, true, true, -1, -1, 1038));
		items.add(new ItemDef("Ana in a Barrel", "A roughly constructed barrel with an Ana in it!", "Look", 100, 359, "items:359", false, false, 0, 16711680, true, true, -1, -1, 1039));
		items.add(new ItemDef("Stolen gold", "I wish I could spend it", "", 300, 79, "items:79", false, false, 0, 16763980, true, true, -1, -1, 1040));
		items.add(new ItemDef("multi cannon ball", "A heavy metal spiked ball", "", 10, 332, "items:332", true, false, 0, 0, true, false, -1, -1, 1041));
		items.add(new ItemDef("Railing", "A metal railing replacement", "", 10, 335, "items:335", false, false, 0, 0, true, true, -1, -1, 1042));
		items.add(new ItemDef("Ogre tooth", "big sharp and nasty", "", 0, 360, "items:360", false, false, 0, 0, true, true, -1, -1, 1043));
		items.add(new ItemDef("Ogre relic", "A grotesque symbol of the ogres", "", 0, 361, "items:361", false, false, 0, 0, true, true, -1, -1, 1044));
		items.add(new ItemDef("Skavid map", "A map of cave locations", "", 0, 362, "items:362", false, false, 0, 0, true, true, -1, -1, 1045));
		items.add(new ItemDef("dwarf remains", "The remains of a dwarf savaged by goblins", "", 1, 237, "items:237", false, false, 0, 16744576, true, true, -1, -1, 1046));
		items.add(new ItemDef("Key", "A key for a chest", "", 1, 25, "items:25", false, false, 0, 16750848, true, true, -1, -1, 1047));
		items.add(new ItemDef("Ogre relic part", "A piece of a statue", "", 0, 363, "items:363", false, false, 0, 0, true, true, -1, -1, 1048));
		items.add(new ItemDef("Ogre relic part", "A piece of a statue", "", 0, 364, "items:364", false, false, 0, 0, true, true, -1, -1, 1049));
		items.add(new ItemDef("Ogre relic part", "A piece of a statue", "", 0, 365, "items:365", false, false, 0, 0, true, true, -1, -1, 1050));
		items.add(new ItemDef("Ground bat bones", "The ground bones of a bat", "", 20, 23, "items:23", false, false, 0, 15645520, true, true, -1, -1, 1051));
		items.add(new ItemDef("Unfinished potion", "I need another ingredient to finish the shaman potion", "", 3, 48, "items:48", false, false, 0, 56576, true, true, -1, -1, 1052));
		items.add(new ItemDef("Ogre potion", "A strange liquid", "", 120, 48, "items:48", false, false, 0, 13434726, true, true, -1, -1, 1053));
		items.add(new ItemDef("Magic ogre potion", "A strange liquid that bubbles with power", "", 120, 48, "items:48", false, false, 0, 6750156, true, true, -1, -1, 1054));
		items.add(new ItemDef("Tool kit", "These could be handy!", "", 120, 366, "items:366", false, false, 0, 15654365, true, true, -1, -1, 1055));
		items.add(new ItemDef("Nulodion's notes", "Construction notes for dwarf cannon ammo", "read", 1, 234, "items:234", false, false, 0, 0, true, true, -1, -1, 1056));
		items.add(new ItemDef("cannon ammo mould", "Used to make cannon ammo", "", 5, 367, "items:367", false, false, 0, 0, true, false, 2016, -1, 1057));
		items.add(new ItemDef("Tenti Pineapple", "The most delicious in the whole of Kharid", "", 1, 124, "items:124", false, false, 0, 0, true, true, -1, -1, 1058));
		items.add(new ItemDef("Bedobin Copy Key", "A copy of a key for the captains of the mining camps chest", "", 20, 25, "items:25", false, false, 0, 4194304, true, true, -1, -1, 1059));
		items.add(new ItemDef("Technical Plans", "Very technical looking plans for making a thrown weapon of some sort", "Read", 500, 218, "items:218", false, false, 0, 12632256, true, true, -1, -1, 1060));
		items.add(new ItemDef("Rock cake", "Yum... I think!", "eat", 0, 368, "items:368", false, false, 0, 0, true, true, -1, -1, 1061));
		items.add(new ItemDef("Bronze dart tips", "Dangerous looking dart tips - need feathers for flight", "", 1, 369, "items:369", true, false, 0, 16737817, true, false, -1, -1, 1062));
		items.add(new ItemDef("Iron dart tips", "Dangerous looking dart tips - need feathers for flight", "", 3, 369, "items:369", true, false, 0, 15654365, true, false, -1, -1, 1063));
		items.add(new ItemDef("Steel dart tips", "Dangerous looking dart tips - need feathers for flight", "", 9, 369, "items:369", true, false, 0, 15658734, true, false, -1, -1, 1064));
		items.add(new ItemDef("Mithril dart tips", "Dangerous looking dart tips - need feathers for flight", "", 25, 369, "items:369", true, false, 0, 10072780, true, false, -1, -1, 1065));
		items.add(new ItemDef("Adamantite dart tips", "Dangerous looking dart tips - need feathers for flight", "", 65, 369, "items:369", true, false, 0, 11717785, true, false, -1, -1, 1066));
		items.add(new ItemDef("Rune dart tips", "Dangerous looking dart tips - need feathers for flight", "", 350, 369, "items:369", true, false, 0, 65535, true, false, -1, -1, 1067));
		items.add(new ItemDef("Mithril Throwing Dart", "A deadly throwing dart with a mithril tip.", "", 50, 231, "items:231", true, true, 16, 10072780, true, false, -1, -1, 1068));
		items.add(new ItemDef("Adamantite Throwing Dart", "A deadly throwing dart with an adamantite tip.", "", 130, 231, "items:231", true, true, 16, 11717785, true, false, -1, -1, 1069));
		items.add(new ItemDef("Rune Throwing Dart", "A deadly throwing dart with a runite tip.", "", 700, 231, "items:231", true, true, 16, 65535, true, false, -1, -1, 1070));
		items.add(new ItemDef("Prototype dart tip", "Dangerous looking dart tip - needs feathers for flight", "", 1, 207, "items:207", true, false, 0, 16737817, true, true, -1, -1, 1071));
		items.add(new ItemDef("info document", "read to access variable choices", "read", 2, 234, "items:234", false, false, 0, 0, true, true, -1, -1, 1072));
		items.add(new ItemDef("Instruction manual", "An old note book", "read", 1, 28, "items:28", false, false, 0, 16755370, true, true, -1, -1, 1073));
		items.add(new ItemDef("Unfinished potion", "I need another ingredient to finish this potion", "", 3, 48, "items:48", false, false, 0, 6750054, true, true, -1, -1, 1074));
		items.add(new ItemDef("Iron throwing knife", "A finely balanced knife", "", 6, 80, "items:80", false, true, 16, 15654365, true, false, 2017, -1, 1075));
		items.add(new ItemDef("Bronze throwing knife", "A finely balanced knife", "", 2, 80, "items:80", false, true, 16, 16737817, true, false, 2018, -1, 1076));
		items.add(new ItemDef("Steel throwing knife", "A finely balanced knife", "", 21, 80, "items:80", false, true, 16, 15658734, true, false, 2019, -1, 1077));
		items.add(new ItemDef("Mithril throwing knife", "A finely balanced knife", "", 54, 80, "items:80", false, true, 16, 10072780, true, false, 2020, -1, 1078));
		items.add(new ItemDef("Adamantite throwing knife", "A finely balanced knife", "", 133, 80, "items:80", false, true, 16, 11717785, true, false, 2021, -1, 1079));
		items.add(new ItemDef("Rune throwing knife", "A finely balanced knife", "", 333, 80, "items:80", false, true, 16, 65535, true, false, 2022, -1, 1080));
		items.add(new ItemDef("Black throwing knife", "A finely balanced knife", "", 37, 80, "items:80", false, true, 16, 3158064, true, false, 2023, -1, 1081));
		items.add(new ItemDef("Water Skin mostly full", "A half full skin of water", "", 27, 343, "items:343", false, false, 0, 8404992, true, false, 2024, -1, 1082));
		items.add(new ItemDef("Water Skin mostly empty", "A half empty skin of water", "", 24, 343, "items:343", false, false, 0, 8404992, true, false, 2025, -1, 1083));
		items.add(new ItemDef("Water Skin mouthful left", "A waterskin with a mouthful of water left", "", 18, 343, "items:343", false, false, 0, 8404992, true, false, 2026, -1, 1084));
		items.add(new ItemDef("Empty Water Skin", "A completely empty waterskin", "", 15, 343, "items:343", false, false, 0, 8404992, true, false, 2027, -1, 1085));
		items.add(new ItemDef("nightshade", "Deadly!", "eat", 30, 370, "items:370", false, false, 0, 0, true, true, -1, -1, 1086));
		items.add(new ItemDef("Shaman robe", "This has been left by one of the dead ogre shaman", "search", 40, 87, "items:87", false, false, 0, 10510400, true, true, -1, -1, 1087));
		items.add(new ItemDef("Iron Spear", "An iron tipped spear", "", 13, 283, "items:283", false, true, 16, 15654365, true, false, 2028, -1, 1088));
		items.add(new ItemDef("Steel Spear", "A steel tipped spear", "", 46, 283, "items:283", false, true, 16, 15658734, true, false, 2029, -1, 1089));
		items.add(new ItemDef("Mithril Spear", "A mithril tipped spear", "", 119, 283, "items:283", false, true, 16, 10072780, true, false, 2030, -1, 1090));
		items.add(new ItemDef("Adamantite Spear", "An adamantite tipped spear", "", 293, 283, "items:283", false, true, 16, 11717785, true, false, 2031, -1, 1091));
		items.add(new ItemDef("Rune Spear", "A rune tipped spear", "", 1000, 283, "items:283", false, true, 16, 56797, true, false, 2032, -1, 1092));
		items.add(new ItemDef("Cat", "it's fluffs", "Stroke", 2, 338, "items:338", false, false, 0, 11184810, true, true, -1, -1, 1093));
		items.add(new ItemDef("Seasoned Sardine", "They don't smell any better", "", 10, 165, "items:165", false, false, 0, 10551200, true, false, 2033, -1, 1094));
		items.add(new ItemDef("Kittens", "purrr", "", 2, 372, "items:372", false, false, 0, 11184810, true, true, -1, -1, 1095));
		items.add(new ItemDef("Kitten", "purrr", "stroke", 2, 371, "items:371", false, false, 0, 11184810, true, true, -1, -1, 1096));
		items.add(new ItemDef("Wrought iron key", "This key clears unlocks a very sturdy gate of some sort.", "", 1, 25, "items:25", false, false, 0, 14540253, true, true, -1, -1, 1097));
		items.add(new ItemDef("Cell Door Key", "A roughly hewn key", "", 1, 25, "items:25", false, false, 0, 16384, true, true, -1, -1, 1098));
		items.add(new ItemDef("A free Shantay Disclaimer", "Very important information.", "Read", 1, 218, "items:218", false, false, 0, 16711680, true, false, 2034, -1, 1099));
		items.add(new ItemDef("Doogle leaves", "Small sweet smelling leaves", "", 2, 298, "items:298", false, false, 0, 0, true, false, 2035, -1, 1100));
		items.add(new ItemDef("Raw Ugthanki Meat", "I need to cook this first", "", 2, 60, "items:60", false, false, 0, 16744640, true, false, 2036, -1, 1101));
		items.add(new ItemDef("Tasty Ugthanki Kebab", "A fresh Kebab made from Ugthanki meat", "eat", 20, 320, "items:320", false, false, 0, 0, true, false, 2037, -1, 1102));
		items.add(new ItemDef("Cooked Ugthanki Meat", "Freshly cooked Ugthanki meat", "Eat", 5, 60, "items:60", false, false, 0, 13395507, true, false, 2038, -1, 1103));
		items.add(new ItemDef("Uncooked Pitta Bread", "I need to cook this.", "", 4, 152, "items:152", false, false, 0, 0, true, false, 2039, -1, 1104));
		items.add(new ItemDef("Pitta Bread", "Mmmm I need to add some other ingredients yet.", "", 10, 152, "items:152", false, false, 0, 16768184, true, false, 2040, -1, 1105));
		items.add(new ItemDef("Tomato Mixture", "A mixture of tomatoes in a bowl", "", 3, 162, "items:162", false, false, 0, 16711680, true, false, 2041, -1, 1106));
		items.add(new ItemDef("Onion Mixture", "A mixture of onions in a bowl", "", 3, 162, "items:162", false, false, 0, 16776960, true, false, 2042, -1, 1107));
		items.add(new ItemDef("Onion and Tomato Mixture", "A mixture of onions and tomatoes in a bowl", "", 3, 162, "items:162", false, false, 0, 16744512, true, false, 2043, -1, 1108));
		items.add(new ItemDef("Onion and Tomato and Ugthanki Mix", "A mixture of onions and tomatoes and Ugthanki meat in a bowl", "", 3, 162, "items:162", false, false, 0, 5977890, true, false, 2044, -1, 1109));
		items.add(new ItemDef("Burnt Pitta Bread", "Urgh - it's all burnt", "", 1, 152, "items:152", false, false, 0, 4194304, true, false, 2045, -1, 1110));
		items.add(new ItemDef("Panning tray", "used for panning gold", "search", 1, 373, "items:373", false, false, 0, 4194304, true, true, -1, -1, 1111));
		items.add(new ItemDef("Panning tray", "this tray contains gold nuggets", "take gold", 1, 374, "items:374", false, false, 0, 4194304, true, true, -1, -1, 1112));
		items.add(new ItemDef("Panning tray", "this tray contains mud", "search", 1, 375, "items:375", false, false, 0, 4194304, true, true, -1, -1, 1113));
		items.add(new ItemDef("Rock pick", "a sharp pick for cracking rocks", "", 1, 376, "items:376", false, false, 0, 4194304, true, true, -1, -1, 1114));
		items.add(new ItemDef("Specimen brush", "stiff brush for cleaning specimens", "", 1, 377, "items:377", false, false, 0, 4194304, true, true, -1, -1, 1115));
		items.add(new ItemDef("Specimen jar", "a jar for holding soil samples", "", 1, 378, "items:378", false, false, 0, 4194304, true, true, -1, -1, 1116));
		items.add(new ItemDef("Rock Sample", "A rock sample", "", 1, 379, "items:379", false, false, 0, 4194304, true, true, -1, -1, 1117));
		items.add(new ItemDef("gold Nuggets", "Real gold pieces!", "", 1, 380, "items:380", true, false, 0, 4194304, true, true, -1, -1, 1118));
		items.add(new ItemDef("cat", "looks like a healthy one", "stroke", 1, 381, "items:381", false, false, 0, 4194304, true, false, 2046, -1, 1119));
		items.add(new ItemDef("Scrumpled piece of paper", "A piece of paper with barely legible writing - looks like a recipe!", "Read", 10, 218, "items:218", false, false, 0, 16317080, true, false, 2047, -1, 1120));
		items.add(new ItemDef("Digsite info", "IAN ONLY", "read", 63, 382, "items:382", false, false, 0, 0, true, true, -1, -1, 1121));
		items.add(new ItemDef("Poisoned Bronze Throwing Dart", "A venomous throwing dart with a bronze tip.", "", 2, 384, "items:384", true, true, 16, 16737817, true, false, -1, -1, 1122));
		items.add(new ItemDef("Poisoned Iron Throwing Dart", "A venomous throwing dart with an iron tip.", "", 5, 384, "items:384", true, true, 16, 16737817, true, false, -1, -1, 1123));
		items.add(new ItemDef("Poisoned Steel Throwing Dart", "A venomous throwing dart with a steel tip.", "", 20, 384, "items:384", true, true, 16, 15658734, true, false, -1, -1, 1124));
		items.add(new ItemDef("Poisoned Mithril Throwing Dart", "A venomous throwing dart with a mithril tip.", "", 50, 384, "items:384", true, true, 16, 10072780, true, false, -1, -1, 1125));
		items.add(new ItemDef("Poisoned Adamantite Throwing Dart", "A venomous throwing dart with an adamantite tip.", "", 130, 384, "items:384", true, true, 16, 11717785, true, false, -1, -1, 1126));
		items.add(new ItemDef("Poisoned Rune Throwing Dart", "A deadly venomous dart with a runite tip.", "", 700, 384, "items:384", true, true, 16, 65535, true, false, -1, -1, 1127));
		items.add(new ItemDef("Poisoned Bronze throwing knife", "A finely balanced knife with a coating of venom", "", 2, 385, "items:385", false, true, 16, 16737817, true, false, 2048, -1, 1128));
		items.add(new ItemDef("Poisoned Iron throwing knife", "A finely balanced knife with a coating of venom", "", 6, 385, "items:385", false, true, 16, 15654365, true, false, 2049, -1, 1129));
		items.add(new ItemDef("Poisoned Steel throwing knife", "A finely balanced knife with a coating of venom", "", 21, 385, "items:385", false, true, 16, 15658734, true, false, 2050, -1, 1130));
		items.add(new ItemDef("Poisoned Mithril throwing knife", "A finely balanced knife with a coating of venom", "", 54, 385, "items:385", false, true, 16, 10072780, true, false, 2051, -1, 1131));
		items.add(new ItemDef("Poisoned Black throwing knife", "A finely balanced knife with a coating of venom", "", 37, 385, "items:385", false, true, 16, 3158064, true, false, 2052, -1, 1132));
		items.add(new ItemDef("Poisoned Adamantite throwing knife", "A finely balanced knife with a coating of venom", "", 133, 385, "items:385", false, true, 16, 11717785, true, false, 2053, -1, 1133));
		items.add(new ItemDef("Poisoned Rune throwing knife", "A finely balanced knife with a coating of venom", "", 333, 385, "items:385", false, true, 16, 65535, true, false, 2054, -1, 1134));
		items.add(new ItemDef("Poisoned Bronze Spear", "A bronze tipped spear with added venom ", "", 4, 383, "items:383", false, true, 16, 16737817, true, false, 2055, -1, 1135));
		items.add(new ItemDef("Poisoned Iron Spear", "An iron tipped spear with added venom", "", 13, 383, "items:383", false, true, 16, 15654365, true, false, 2056, -1, 1136));
		items.add(new ItemDef("Poisoned Steel Spear", "A steel tipped spear with added venom", "", 46, 383, "items:383", false, true, 16, 15658734, true, false, 2057, -1, 1137));
		items.add(new ItemDef("Poisoned Mithril Spear", "A mithril tipped spear with added venom", "", 119, 383, "items:383", false, true, 16, 10072780, true, false, 2058, -1, 1138));
		items.add(new ItemDef("Poisoned Adamantite Spear", "An adamantite tipped spear with added venom", "", 293, 383, "items:383", false, true, 16, 11717785, true, false, 2059, -1, 1139));
		items.add(new ItemDef("Poisoned Rune Spear", "A rune tipped spear with added venom", "", 1000, 383, "items:383", false, true, 16, 56797, true, false, 2060, -1, 1140));
		items.add(new ItemDef("Book of experimental chemistry", "A book on experiments with volatile chemicals", "read", 1, 28, "items:28", false, false, 0, 16755370, true, true, -1, -1, 1141));
		items.add(new ItemDef("Level 1 Certificate", "A Certificate of education", "read", 1, 29, "items:29", false, false, 0, 0, true, true, -1, -1, 1142));
		items.add(new ItemDef("Level 2 Certificate", "A Certificate of education", "read", 1, 29, "items:29", false, false, 0, 0, true, true, -1, -1, 1143));
		items.add(new ItemDef("Level 3 Certificate", "A Certificate of education", "read", 1, 29, "items:29", false, false, 0, 0, true, true, -1, -1, 1144));
		items.add(new ItemDef("Trowel", "A small device for digging", "", 1, 386, "items:386", false, false, 0, 0, true, true, -1, -1, 1145));
		items.add(new ItemDef("Stamped letter of recommendation", "A stamped scroll with a recommendation on it", "", 1, 402, "items:402", false, false, 0, 0, true, true, -1, -1, 1146));
		items.add(new ItemDef("Unstamped letter of recommendation", "I hereby recommend this student to undertake the Varrock City earth sciences exams", "", 5, 29, "items:29", false, false, 0, 0, true, true, -1, -1, 1147));
		items.add(new ItemDef("Rock Sample", "A rock sample", "", 1, 388, "items:388", false, false, 0, 4194304, true, true, -1, -1, 1148));
		items.add(new ItemDef("Rock Sample", "A rock sample", "", 1, 389, "items:389", false, false, 0, 4194304, true, true, -1, -1, 1149));
		items.add(new ItemDef("Cracked rock Sample", "It's been cracked open", "", 1, 387, "items:387", false, false, 0, 4194304, true, true, -1, -1, 1150));
		items.add(new ItemDef("Belt buckle", "been here some time", "", 1, 390, "items:390", false, false, 0, 4194304, true, true, -1, -1, 1151));
		items.add(new ItemDef("Powering crystal2", "An intricately cut gemstone", "", 0, 357, "items:357", false, false, 0, 16738047, true, true, -1, -1, 1152));
		items.add(new ItemDef("Powering crystal3", "An intricately cut gemstone", "", 0, 357, "items:357", false, false, 0, 6750207, true, true, -1, -1, 1153));
		items.add(new ItemDef("Powering crystal4", "An intricately cut gemstone", "", 0, 357, "items:357", false, false, 0, 3407667, true, true, -1, -1, 1154));
		items.add(new ItemDef("Old boot", "that's been here some time", "", 1, 391, "items:391", false, false, 0, 4194304, true, true, -1, -1, 1155));
		items.add(new ItemDef("Bunny ears", "Get another from the clothes shop if you die", "", 1, 392, "items:392", false, true, 32, 4194304, false, true, 2133, -1, 1156));
		items.add(new ItemDef("Damaged armour", "that's been here some time", "", 1, 393, "items:393", false, false, 0, 4194304, true, true, -1, -1, 1157));
		items.add(new ItemDef("Damaged armour", "that's been here some time", "", 1, 394, "items:394", false, false, 0, 4194304, true, true, -1, -1, 1158));
		items.add(new ItemDef("Rusty sword", "that's been here some time", "", 1, 395, "items:395", false, false, 0, 4194304, true, true, -1, -1, 1159));
		items.add(new ItemDef("Ammonium Nitrate", "An acrid chemical", "", 20, 23, "items:23", false, false, 0, 16777164, true, true, -1, -1, 1160));
		items.add(new ItemDef("Nitroglycerin", "A strong acidic formula", "", 2, 48, "items:48", false, false, 0, 16750848, true, true, -1, -1, 1161));
		items.add(new ItemDef("Old tooth", "a large single tooth", "", 0, 360, "items:360", false, false, 0, 0, true, true, -1, -1, 1162));
		items.add(new ItemDef("Radimus Scrolls", "Scrolls that Radimus gave you", "Read Scrolls", 5, 29, "items:29", false, false, 0, 8421504, true, true, -1, -1, 1163));
		items.add(new ItemDef("chest key", "A small key for a chest", "", 1, 25, "items:25", false, false, 0, 16763904, true, true, -1, -1, 1164));
		items.add(new ItemDef("broken arrow", "that's been here some time", "", 1, 396, "items:396", false, false, 0, 4194304, true, true, -1, -1, 1165));
		items.add(new ItemDef("buttons", "they've been here some time", "", 1, 397, "items:397", false, false, 0, 4194304, true, true, -1, -1, 1166));
		items.add(new ItemDef("broken staff", "that's been here some time", "", 1, 398, "items:398", false, false, 0, 4194304, true, true, -1, -1, 1167));
		items.add(new ItemDef("vase", "An old vase", "", 1, 279, "items:279", false, false, 0, 0, true, true, -1, -1, 1168));
		items.add(new ItemDef("ceramic remains", "some ancient pottery", "", 1, 399, "items:399", false, false, 0, 4194304, true, true, -1, -1, 1169));
		items.add(new ItemDef("Broken glass", "smashed glass", "", 0, 259, "items:259", false, false, 0, 0, true, true, -1, -1, 1170));
		items.add(new ItemDef("Unidentified powder", "who knows what this is for?", "", 20, 23, "items:23", false, false, 0, 16777164, true, true, -1, -1, 1171));
		items.add(new ItemDef("Machette", "A purpose built tool for cutting through thick jungle.", "", 40, 432, "items:432", false, true, 16, 8421504, true, false, 2061, -1, 1172));
		items.add(new ItemDef("Scroll", "A letter written by the expert", "read", 5, 29, "items:29", false, false, 0, 0, true, true, -1, -1, 1173));
		items.add(new ItemDef("stone tablet", "some ancient script is engraved on here", "read", 1, 400, "items:400", false, false, 0, 4194304, true, true, -1, -1, 1174));
		items.add(new ItemDef("Talisman of Zaros", "an ancient item", "", 1, 401, "items:401", false, false, 0, 4194304, true, true, -1, -1, 1175));
		items.add(new ItemDef("Explosive compound", "A dark mystical powder", "", 2, 48, "items:48", false, false, 0, 51, true, true, -1, -1, 1176));
		items.add(new ItemDef("Bull Roarer", "A sound producing instrument - it may attract attention", "Swing", 1, 418, "items:418", false, false, 0, 7552262, true, true, -1, -1, 1177));
		items.add(new ItemDef("Mixed chemicals", "A pungent mix of 2 chemicals", "", 2, 48, "items:48", false, false, 0, 16777113, true, true, -1, -1, 1178));
		items.add(new ItemDef("Ground charcoal", "Powdered charcoal!", "", 20, 23, "items:23", false, false, 0, 2236962, true, false, 2062, -1, 1179));
		items.add(new ItemDef("Mixed chemicals", "A pungent mix of 3 chemicals", "", 2, 48, "items:48", false, false, 0, 13408512, true, true, -1, -1, 1180));
		items.add(new ItemDef("Spell scroll", "A magical scroll", "read", 5, 29, "items:29", false, false, 0, 0, true, true, -1, -1, 1181));
		items.add(new ItemDef("Yommi tree seed", "A magical seed that grows into a Yommi tree - these need to be germinated", "Inspect", 200, 270, "items:270", true, false, 0, 65280, true, true, -1, -1, 1182));
		items.add(new ItemDef("Totem Pole", "A well crafted totem pole", "", 500, 403, "items:403", false, false, 0, 65280, true, true, -1, -1, 1183));
		items.add(new ItemDef("Dwarf cannon base", "bang", "set down", 200000, 352, "items:352", false, false, 0, 0, true, false, 2063, -1, 1184));
		items.add(new ItemDef("Dwarf cannon stand", "bang", "", 200000, 353, "items:353", false, false, 0, 0, true, false, 2064, -1, 1185));
		items.add(new ItemDef("Dwarf cannon barrels", "bang", "", 200000, 354, "items:354", false, false, 0, 0, true, false, 2065, -1, 1186));
		items.add(new ItemDef("Dwarf cannon furnace", "bang", "", 150000, 355, "items:355", false, false, 0, 0, true, false, 2066, -1, 1187));
		items.add(new ItemDef("Golden Bowl", "A specially made bowl constructed out of pure gold", "", 1000, 404, "items:404", false, false, 0, 0, true, true, -1, -1, 1188));
		items.add(new ItemDef("Golden Bowl with pure water", "A golden bowl filled with pure water", "", 1000, 405, "items:405", false, false, 0, 8454143, true, true, -1, -1, 1189));
		items.add(new ItemDef("Raw Manta ray", "A rare catch!", "", 500, 406, "items:406", false, false, 0, 255, true, false, 2067, -1, 1190));
		items.add(new ItemDef("Manta ray", "A rare catch!", "eat", 500, 407, "items:407", false, false, 0, 255, true, false, 2068, -1, 1191));
		items.add(new ItemDef("Raw Sea turtle", "A rare catch!", "", 500, 408, "items:408", false, false, 0, 255, true, false, 2069, -1, 1192));
		items.add(new ItemDef("Sea turtle", "Tasty!", "eat", 500, 409, "items:409", false, false, 0, 255, true, false, 2070, -1, 1193));
		items.add(new ItemDef("Annas Silver Necklace", "A necklace coated with silver", "", 1, 24, "items:24", false, true, 1024, 0, true, true, -1, -1, 1194));
		items.add(new ItemDef("Bobs Silver Teacup", "A tea cup coated with silver", "", 1, 227, "items:227", false, false, 0, 0, true, true, -1, -1, 1195));
		items.add(new ItemDef("Carols Silver Bottle", "A little bottle coated with silver", "", 1, 104, "items:104", false, false, 0, 0, true, true, -1, -1, 1196));
		items.add(new ItemDef("Davids Silver Book", "An ornamental book coated with silver", "", 1, 28, "items:28", false, false, 0, 0, true, true, -1, -1, 1197));
		items.add(new ItemDef("Elizabeths Silver Needle", "An ornamental needle coated with silver", "", 1, 38, "items:38", false, false, 0, 0, true, true, -1, -1, 1198));
		items.add(new ItemDef("Franks Silver Pot", "A small pot coated with silver", "", 1, 61, "items:61", false, false, 0, 0, true, true, -1, -1, 1199));
		items.add(new ItemDef("Thread", "A piece of red thread discovered at the scene of the crime", "", 1, 208, "items:208", false, false, 0, 16711680, true, true, -1, -1, 1200));
		items.add(new ItemDef("Thread", "A piece of green thread discovered at the scene of the crime", "", 1, 208, "items:208", false, false, 0, 65280, true, true, -1, -1, 1201));
		items.add(new ItemDef("Thread", "A piece of blue thread discovered at the scene of the crime", "", 1, 208, "items:208", false, false, 0, 255, true, true, -1, -1, 1202));
		items.add(new ItemDef("Flypaper", "Sticky paper for catching flies", "", 1, 415, "items:415", false, false, 0, 14540253, true, true, -1, -1, 1203));
		items.add(new ItemDef("Murder Scene Pot", "The pot has a sickly smell of poison mixed with wine", "", 1, 61, "items:61", false, false, 0, 16711680, true, true, -1, -1, 1204));
		items.add(new ItemDef("A Silver Dagger", "Dagger Found at crime scene", "", 1, 80, "items:80", false, true, 16, 0, true, true, -1, -1, 1205));
		items.add(new ItemDef("Murderers fingerprint", "An impression of the murderers fingerprint", "", 1, 416, "items:416", false, false, 0, 14540253, true, true, -1, -1, 1206));
		items.add(new ItemDef("Annas fingerprint", "An impression of Annas fingerprint", "", 1, 416, "items:416", false, false, 0, 14540253, true, true, -1, -1, 1207));
		items.add(new ItemDef("Bobs fingerprint", "An impression of Bobs fingerprint", "", 1, 416, "items:416", false, false, 0, 14540253, true, true, -1, -1, 1208));
		items.add(new ItemDef("Carols fingerprint", "An impression of Carols fingerprint", "", 1, 416, "items:416", false, false, 0, 14540253, true, true, -1, -1, 1209));
		items.add(new ItemDef("Davids fingerprint", "An impression of Davids fingerprint", "", 1, 416, "items:416", false, false, 0, 14540253, true, true, -1, -1, 1210));
		items.add(new ItemDef("Elizabeths fingerprint", "An impression of Elizabeths fingerprint", "", 1, 416, "items:416", false, false, 0, 14540253, true, true, -1, -1, 1211));
		items.add(new ItemDef("Franks fingerprint", "An impression of Franks fingerprint", "", 1, 416, "items:416", false, false, 0, 14540253, true, true, -1, -1, 1212));
		items.add(new ItemDef("Zamorak Cape", "A cape from the almighty zamorak", "", 100, 59, "items:59", false, true, 2048, 16711680, true, true, -1, -1, 1213));
		items.add(new ItemDef("Saradomin Cape", "A cape from the almighty saradomin", "", 100, 59, "items:59", false, true, 2048, 4210926, true, true, -1, -1, 1214));
		items.add(new ItemDef("Guthix Cape", "A cape from the almighty guthix", "", 100, 59, "items:59", false, true, 2048, 4246592, true, true, -1, -1, 1215));
		items.add(new ItemDef("Staff of zamorak", "It's a stick of the gods", "", 80000, 337, "items:337", false, true, 16, 0, true, true, -1, -1, 1216));
		items.add(new ItemDef("Staff of guthix", "It's a stick of the gods", "", 80000, 85, "items:85", false, true, 16, 10072780, true, true, -1, -1, 1217));
		items.add(new ItemDef("Staff of Saradomin", "It's a stick of the gods", "", 80000, 414, "items:414", false, true, 16, 10072780, true, true, -1, -1, 1218));
		items.add(new ItemDef("A chunk of crystal", "A reddish crystal fragment - it looks like it formed a shape at one time.", "", 2000, 410, "items:410", false, false, 0, 0, true, true, -1, -1, 1219));
		items.add(new ItemDef("A lump of crystal", "A reddish crystal fragment - it looks like it formed a shape at one time.", "", 2000, 411, "items:411", false, false, 0, 0, true, true, -1, -1, 1220));
		items.add(new ItemDef("A hunk of crystal", "A reddish crystal fragment - it looks like it formed a shape at one time.", "", 2000, 412, "items:412", false, false, 0, 0, true, true, -1, -1, 1221));
		items.add(new ItemDef("A red crystal", "A heart shaped red crystal ", "Inspect", 2000, 413, "items:413", false, false, 0, 0, true, true, -1, -1, 1222));
		items.add(new ItemDef("Unidentified fingerprint", "An impression of the murderers fingerprint", "", 1, 416, "items:416", false, false, 0, 14540253, true, true, -1, -1, 1223));
		items.add(new ItemDef("Annas Silver Necklace", "A silver necklace coated with flour", "", 1, 24, "items:24", false, true, 1024, 0, true, true, -1, -1, 1224));
		items.add(new ItemDef("Bobs Silver Teacup", "A silver tea cup coated with flour", "", 1, 227, "items:227", false, false, 0, 0, true, true, -1, -1, 1225));
		items.add(new ItemDef("Carols Silver Bottle", "A little silver bottle coated with flour", "", 1, 104, "items:104", false, false, 0, 0, true, true, -1, -1, 1226));
		items.add(new ItemDef("Davids Silver Book", "An ornamental silver book coated with flour", "", 1, 28, "items:28", false, false, 0, 0, true, true, -1, -1, 1227));
		items.add(new ItemDef("Elizabeths Silver Needle", "An ornamental silver needle coated with flour", "", 1, 38, "items:38", false, false, 0, 0, true, true, -1, -1, 1228));
		items.add(new ItemDef("Franks Silver Pot", "A small silver pot coated with flour", "", 1, 61, "items:61", false, false, 0, 0, true, true, -1, -1, 1229));
		items.add(new ItemDef("A Silver Dagger", "Dagger Found at crime scene coated with flour", "", 1, 80, "items:80", false, true, 16, 0, true, true, -1, -1, 1230));
		items.add(new ItemDef("A glowing red crystal", "A glowing heart shaped red crystal - great magic must be present in this item", "", 2000, 419, "items:419", false, false, 0, 0, true, true, -1, -1, 1231));
		items.add(new ItemDef("Unidentified liquid", "A strong acidic formula", "", 2, 48, "items:48", false, false, 0, 16750848, true, true, -1, -1, 1232));
		items.add(new ItemDef("Radimus Scrolls", "Mission briefing and the completed map of Karamja - Sir Radimus will be pleased...", "Read Scrolls", 5, 29, "items:29", false, false, 0, 8421504, true, true, -1, -1, 1233));
		items.add(new ItemDef("Robe", "A worn robe", "", 15, 87, "items:87", false, true, 64, 255, true, true, -1, -1, 1234));
		items.add(new ItemDef("Armour", "An unusually red armour", "", 40, 118, "items:118", false, false, 0, 13369344, true, true, -1, -1, 1235));
		items.add(new ItemDef("Dagger", "Short but pointy", "", 35, 80, "items:80", false, true, 16, 15654365, true, true, -1, -1, 1236));
		items.add(new ItemDef("eye patch", "It makes me look very piratical", "", 2, 198, "items:198", false, true, 32, 0, true, true, -1, -1, 1237));
		items.add(new ItemDef("Booking of Binding", "An ancient tome on Demonology", "read", 1, 28, "items:28", false, false, 0, 15641258, true, true, -1, -1, 1238));
		items.add(new ItemDef("Holy Water Vial", "A deadly potion against evil kin", "Throw", 3, 48, "items:48", false, true, 16, 10073782, true, true, -1, -1, 1239));
		items.add(new ItemDef("Enchanted Vial", "This enchanted vial is empty - but is ready for magical liquids.", "", 200, 144, "items:144", false, false, 0, 16646109, true, true, -1, -1, 1240));
		items.add(new ItemDef("Scribbled notes", "It looks like a page ripped from a book", "Read", 20, 427, "items:427", false, false, 0, 8421376, true, true, -1, -1, 1241));
		items.add(new ItemDef("Scrawled notes", "It looks like a page ripped from a book", "Read", 20, 427, "items:427", false, false, 0, 14066524, true, true, -1, -1, 1242));
		items.add(new ItemDef("Scatched notes", "It looks like a page ripped from a book", "Read", 20, 427, "items:427", false, false, 0, 11909701, true, true, -1, -1, 1243));
		items.add(new ItemDef("Shamans Tome", "An ancient tome on various subjects...", "read", 1, 299, "items:299", false, false, 0, 15641258, true, true, -1, -1, 1244));
		items.add(new ItemDef("Edible seaweed", "slightly damp seaweed", "eat", 2, 203, "items:203", false, false, 0, 0, true, false, 2071, -1, 1245));
		items.add(new ItemDef("Rough Sketch of a bowl", "A roughly sketched picture of a bowl made from metal", "Read", 5, 29, "items:29", false, false, 0, 0, true, true, -1, -1, 1246));
		items.add(new ItemDef("Burnt Manta ray", "oops!", "", 500, 430, "items:430", false, false, 0, 255, true, false, 2072, -1, 1247));
		items.add(new ItemDef("Burnt Sea turtle", "oops!", "", 500, 431, "items:431", false, false, 0, 255, true, false, 2073, -1, 1248));
		items.add(new ItemDef("Cut reed plant", "A narrow long tube - it might be useful for something", "", 2, 202, "items:202", false, false, 0, 65280, true, false, 2074, -1, 1249));
		items.add(new ItemDef("Magical Fire Pass", "A pass which allows you to cross the flaming walls into the Flaming Octagon", "", 1, 29, "items:29", false, false, 0, 16711680, true, true, -1, -1, 1250));
		items.add(new ItemDef("Snakes Weed Solution", "Snakes weed in water - part of a potion", "", 1, 48, "items:48", false, false, 0, 8454016, true, true, -1, -1, 1251));
		items.add(new ItemDef("Ardrigal Solution", "Ardrigal herb in water - part of a potion", "", 1, 48, "items:48", false, false, 0, 8388608, true, true, -1, -1, 1252));
		items.add(new ItemDef("Gujuo Potion", "A potion to help against fear of the supernatural", "Drink", 1, 48, "items:48", false, false, 0, 8405056, true, true, -1, -1, 1253));
		items.add(new ItemDef("Germinated Yommi tree seed", "A magical seed that grows into a Yommi tree - these have been germinated.", "Inspect", 200, 270, "items:270", true, false, 0, 65280, true, true, -1, -1, 1254));
		items.add(new ItemDef("Dark Dagger", "An unusual looking dagger made of dark shiny obsidian", "", 91, 420, "items:420", false, true, 16, 0, true, true, -1, -1, 1255));
		items.add(new ItemDef("Glowing Dark Dagger", "An unusual looking dagger made of dark shiny obsidian - it has an unnatural glow .", "", 91, 421, "items:421", false, true, 16, 0, true, true, -1, -1, 1256));
		items.add(new ItemDef("Holy Force Spell", "A powerful incantation - it affects spirits of the underworld", "Cast", 1, 423, "items:423", false, false, 0, 0, true, true, -1, -1, 1257));
		items.add(new ItemDef("Iron Pickaxe", "Used for mining", "", 140, 72, "items:72", false, false, 0, 15654365, false, false, 2075, -1, 1258));
		items.add(new ItemDef("Steel Pickaxe", "Requires level 6 mining to use", "", 500, 72, "items:72", false, false, 0, 15658734, false, false, 2076, -1, 1259));
		items.add(new ItemDef("Mithril Pickaxe", "Requires level 21 mining to use", "", 1300, 72, "items:72", false, false, 0, 10072780, false, false, 2077, -1, 1260));
		items.add(new ItemDef("Adamantite Pickaxe", "Requires level 31 mining to use", "", 3200, 72, "items:72", false, false, 0, 11717785, false, false, 2078, -1, 1261));
		items.add(new ItemDef("Rune Pickaxe", "Requires level 41 mining to use", "", 32000, 72, "items:72", false, false, 0, 65535, false, false, 2079, -1, 1262));
		items.add(new ItemDef("Sleeping Bag", "Not as comfy as a bed but better than nothing", "sleep", 30, 422, "items:422", false, false, 0, 0, false, false, 2080, -1, 1263));
		items.add(new ItemDef("A blue wizards hat", "An ancient wizards hat.", "", 2, 86, "items:86", false, true, 32, 255, true, true, -1, -1, 1264));
		items.add(new ItemDef("Gilded Totem Pole", "A well crafted totem pole - given to you as a gift from Gujuo", "Inspect", 20, 403, "items:403", false, false, 0, 65280, true, true, -1, -1, 1265));
		items.add(new ItemDef("Blessed Golden Bowl", "A specially made bowl constructed out of pure gold - it looks magical somehow", "", 1000, 404, "items:404", false, false, 0, 0, true, true, -1, -1, 1266));
		items.add(new ItemDef("Blessed Golden Bowl with Pure Water", "A golden bowl filled with pure water - it looks magical somehow", "", 1000, 405, "items:405", false, false, 0, 8454143, true, true, -1, -1, 1267));
		items.add(new ItemDef("Raw Oomlie Meat", "Raw meat from the Oomlie bird", "", 10, 60, "items:60", false, false, 0, 16747571, true, false, 2081, -1, 1268));
		items.add(new ItemDef("Cooked Oomlie meat Parcel", "Deliciously cooked Oomlie meat in a palm leaf pouch.", "eat", 35, 433, "items:433", false, false, 0, 13395507, true, false, 2082, -1, 1269));
		items.add(new ItemDef("Dragon Bone Certificate", "Each certificate exchangable at Yanille for 5 Dragon Bones", "", 10, 180, "items:180", true, false, 0, 0, true, false, -1, -1, 1270));
		items.add(new ItemDef("Limpwurt Root Certificate", "Each certificate exchangable at Yanille for 5 Limpwort roots", "", 10, 180, "items:180", true, false, 0, 16384, true, false, -1, -1, 1271));
		items.add(new ItemDef("Prayer Potion Certificate", "Each certificate exchangable at Yanille for 5 prayer potions", "", 10, 180, "items:180", true, false, 0, 3206809, true, false, -1, -1, 1272));
		items.add(new ItemDef("Super Attack Potion Certificate", "Exchangable at Yanille for 5", "", 10, 180, "items:180", true, false, 0, 3158254, true, false, -1, -1, 1273));
		items.add(new ItemDef("Super Defense Potion Certificate", "Exchangable at Yanille for 5", "", 10, 180, "items:180", true, false, 0, 15644208, true, false, -1, -1, 1274));
		items.add(new ItemDef("Super Strength Potion Certificate", "Exchangable at Yanille for 5", "", 10, 180, "items:180", true, false, 0, 15658734, true, false, -1, -1, 1275));
		items.add(new ItemDef("Half Dragon Square Shield", "The Right Half of an ancient and powerful looking Dragon Square shield.", "", 500000, 425, "items:425", false, false, 0, 15654365, true, false, 2083, -1, 1276));
		items.add(new ItemDef("Half Dragon Square Shield", "Left Half of an ancient and powerful looking Dragon Square shield.", "", 110000, 424, "items:424", false, false, 0, 15654365, true, false, 2084, -1, 1277));
		items.add(new ItemDef("Dragon Square Shield", "An ancient and powerful looking Dragon Square shield.", "", 500000, 426, "items:426", false, true, 8, 13500416, true, false, 2085, -1, 1278));
		items.add(new ItemDef("Palm tree leaf", "A thick green plam leaf - natives use this to cook meat in", "", 5, 428, "items:428", false, false, 0, 0, true, false, 2086, -1, 1279));
		items.add(new ItemDef("Raw Oomlie Meat Parcel", "Oomlie meat in a palm leaf pouch - just needs to be cooked.", "", 16, 429, "items:429", false, false, 0, 16747571, true, false, 2087, -1, 1280));
		items.add(new ItemDef("Burnt Oomlie Meat parcel", "Oomlie meat in a palm leaf pouch - it's burnt.", "", 1, 429, "items:429", false, false, 0, 4194304, true, false, 2088, -1, 1281));
		items.add(new ItemDef("Bailing Bucket", "It's a water tight bucket", "bail with ", 10, 22, "items:22", false, false, 0, 1052688, true, false, 2089, -1, 1282));
		items.add(new ItemDef("Plank", "Damaged remains of the ship", "", 1, 135, "items:135", false, false, 0, 0, true, false, 2090, -1, 1283));
		items.add(new ItemDef("Arcenia root", "the root of an arcenia plant", "", 7, 101, "items:101", false, false, 0, 0, true, true, -1, -1, 1284));
		items.add(new ItemDef("display tea", "A nice cup of tea - for display only", "", 10, 227, "items:227", false, false, 0, 0, true, false, 2091, -1, 1285));
		items.add(new ItemDef("Blessed Golden Bowl with plain water", "A golden bowl filled with plain water", "Empty", 1000, 405, "items:405", false, false, 0, 8454143, true, true, -1, -1, 1286));
		items.add(new ItemDef("Golden Bowl with plain water", "A golden bowl filled with plain water", "Empty", 1000, 405, "items:405", false, false, 0, 8454143, true, true, -1, -1, 1287));
		items.add(new ItemDef("Cape of legends", "Shows I am a member of the legends guild", "", 450, 59, "items:59", false, true, 2048, 16777215, true, true, -1, -1, 1288));
		items.add(new ItemDef("Scythe", "Get another from the clothes shop if you die", "", 15, 434, "items:434", false, true, 8216, 0, false, true, 2134, -1, 1289));
		loadNoteDefinitions();

		//Load custom sprites
		if (Config.S_WANT_CUSTOM_SPRITES)
			loadCustomItemAndNoteDefinitions();

		if (Config.S_SHOW_UNIDENTIFIED_HERB_NAMES) {
			items.get(165).name = "Muddy Guam";
			items.get(435).name = "Muddy Marrentill";
			items.get(436).name = "Muddy Tarromin";
			items.get(437).name = "Muddy Harralander";
			items.get(438).name = "Muddy Ranarr Weed";
			items.get(439).name = "Muddy Irit Leaf";
			items.get(440).name = "Muddy Avantoe";
			items.get(441).name = "Muddy Kwuarm";
			items.get(442).name = "Muddy Cadantine";
			items.get(443).name = "Muddy Dwarf Weed";
			items.get(815).name = "Muddy Snake Weed";
			items.get(817).name = "Muddy Ardrigal";
			items.get(819).name = "Muddy Sito Foil";
			items.get(821).name = "Muddy Volencia Moss";
			items.get(823).name = "Muddy Rogues Purse";
			items.get(933).name = "Muddy Torstol";
		}

		/*try {
		PrintWriter printWriter = new PrintWriter(new FileOutputStream("newItemDef.txt"), true);


		/*for(ItemDef item : items) {
			if(item.id <= 1289) {
				printWriter.println("UPDATE `openrsc_itemdef` SET `bankNoteID`=" + item.getNotedForm() + ",`originalItemID`='-1' WHERE id='" + item.id + "';");
				printWriter.flush();
			}
			else if(item.id >= 0) {
				printWriter.println("UPDATE `openrsc_itemdef` SET `name`='" + item.getName().replace("'", "''") + "',`description`='" + item.getDescription().replace("'", "''") + "', " + (item.getCommand().isEmpty() ? "" : "`command`='" + item.getCommand() + "',") + "`isStackable`=" + (item.isStackable() ? "'1'" : "'0'") + ",`isUntradable`=" + (item.quest ? "'1'" : "'0'") + ",`isWearable`=" + (item.isWieldable() ? "'1'" : "'0'") + ",`wearableID`='" + item.wearableID + "',`basePrice`='" + item.getBasePrice() + "',`isMembersOnly`=" + (item.membersItem ? "'1'" : "'0'") + " WHERE id='" + item.id + "';");
				printWriter.flush();
			}
			else {
				printWriter.println("INSERT INTO `openrsc_itemdef`(`id`, `bankNoteID`, `originalItemID`, `name`, `description`, `command`, `isFemaleOnly`, `isMembersOnly`, `isStackable`, `isUntradable`, `isWearable`, `appearanceID`, `wearableID`, `wearSlot`, `requiredLevel`, `requiredSkillID`, `armourBonus`, `weaponAimBonus`, `weaponPowerBonus`, `magicBonus`, `prayerBonus`, `basePrice`)"
						+ " VALUES (\""+ item.id +"\", \"-1\", \"" + item.getNotedFormOf() + "\", \"" + item.getName() + "\" ,\""+ item.getDescription()+"\",\""+ item.getCommand() +"\",\"0\",\"" + (item.membersItem ? 1 : 0) + "\",\""+ (item.isStackable() ? 1 : 0) +"\", \"0\", \"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"" + item.basePrice + "\");");
				printWriter.flush();
			}
		}

		for(ItemDef item : (ArrayList<ItemDef>) items.clone()) {
			if(item.isStackable() || item.quest) {
				continue;
			}
			items.add(new ItemDef(itemCount(), item));
		}
		for(ItemDef item : items) {
			if(item.getNotedFormOf() >= 0) {
				ItemDef original = getItemDef(item.getNotedFormOf());
				original.setNotedForm(item.id);
			}
		}
			for(ItemDef item : items) {
				printWriter.println("items.add(new ItemDef(\""+ item.getName() +"\",\"" + item.getDescription() + "\", \"" + item.command + "\", " + item.getBasePrice() + ", " + item.getSprite() + ", " + item.stackable + ", "+ item.wieldable +", "+ item.wearableID +", "+ item.getPictureMask()+", "+ item.membersItem+", "+item.quest+", " + item.getNotedForm() +", " + item.getNotedFormOf() + ", " + item.id +"));");
				printWriter.flush();
			}
		printWriter.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}*/

	}

	private static void loadCustomItemAndNoteDefinitions() {
		/**
		 * These are the custom item and note definitions.
		 * Sampled as the following format for ease:
		 * Non note item -> note of same item (IF note should be added of said item).
		 * ------------------------------------------------------------------------------
		 * After id 1289 - Scythe from real RSC.
		 * After id 2091 - Display Tea from regular RSC as the last real RSC item in note.
		 */

		items.add(new ItemDef("Bunny ears", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, true, -1, 1156, 2133));
		items.add(new ItemDef("Scythe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, true, -1, 1289, 2134));

		/*
		items.add(new ItemDef("Ironman helm", "For just a rather very independent scaper.", "", 154, 6, false, true, 33, 11189164, false, true, -1, -1, 2135));
		items.add(new ItemDef("Ironman platebody", "Take it off and what are you?", "", 560, 8, false, true, 322, 11189164, false, true, -1, -1, 2136));
		items.add(new ItemDef("Ironman platelegs", "Take it off and what are you?", "", 280, 9, false, true, 644, 11189164, false, true, -1, -1, 2137));

		items.add(new ItemDef("Ultimate ironman helm", "For Just A Rather Very Independent Scaper.", "", 154, 6, false, true, 33, 16768685, false, true, -1, -1, 2138));
		items.add(new ItemDef("Ultimate ironman platebody", "Take it off and what are you?", "", 560, 8, false, true, 322, 16768685, false, true, -1, -1, 2139));
		items.add(new ItemDef("Ultimate ironman platelegs", "Take it off and what are you?", "", 280, 9, false, true, 644, 16768685, false, true, -1, -1, 2140));

		items.add(new ItemDef("Hardcore ironman helm", "For those who stand alone.", "", 154, 6, false, true, 33, 10027084, false, true, -1, -1, 2141));
		items.add(new ItemDef("Hardcore ironman platebody", "Take it off and what are you?", "", 560, 8, false, true, 322, 10027084, false, true, -1, -1, 2142));
		items.add(new ItemDef("Hardcore ironman platelegs", "Take it off and what are you?", "", 280, 9, false, true, 644, 10027084, false, true, -1, -1, 2143));
		*/

	}

	private static void loadNoteDefinitions() {
		items.add(new ItemDef("Iron Mace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 0, 1290));
		items.add(new ItemDef("Iron Short Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 1, 1291));
		items.add(new ItemDef("Iron Kite Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 2, 1292));
		items.add(new ItemDef("Iron Square Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 3, 1293));
		items.add(new ItemDef("Wooden Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 4, 1294));
		items.add(new ItemDef("Medium Iron Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 5, 1295));
		items.add(new ItemDef("Large Iron Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 6, 1296));
		items.add(new ItemDef("Iron Chain Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 7, 1297));
		items.add(new ItemDef("Iron Plate Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 8, 1298));
		items.add(new ItemDef("Iron Plate Mail Legs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 9, 1299));
		items.add(new ItemDef("Iron Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 12, 1300));
		items.add(new ItemDef("Knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 13, 1301));
		items.add(new ItemDef("Logs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 14, 1302));
		items.add(new ItemDef("Leather Armour", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 15, 1303));
		items.add(new ItemDef("Leather Gloves", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 16, 1304));
		items.add(new ItemDef("Boots", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 17, 1305));
		items.add(new ItemDef("Cabbage", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 18, 1306));
		items.add(new ItemDef("Egg", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 19, 1307));
		items.add(new ItemDef("Bones", "Swap this note at any bank for the equivalent item.", "Bury", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 20, 1308));
		items.add(new ItemDef("Bucket", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 21, 1309));
		items.add(new ItemDef("Milk", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 22, 1310));
		items.add(new ItemDef("Iron dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 28, 1311));
		items.add(new ItemDef("grain", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 29, 1312));
		items.add(new ItemDef("Holy Symbol of saradomin", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 44, 1313));
		items.add(new ItemDef("Unblessed Holy Symbol", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 45, 1314));
		items.add(new ItemDef("key", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 48, 1315));
		items.add(new ItemDef("scroll", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 49, 1316));
		items.add(new ItemDef("Water", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 50, 1317));
		items.add(new ItemDef("Silverlight", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 52, 1318));
		items.add(new ItemDef("Broken shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 53, 1319));
		items.add(new ItemDef("Broken shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 54, 1320));
		items.add(new ItemDef("Cadavaberries", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 55, 1321));
		items.add(new ItemDef("message", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 56, 1322));
		items.add(new ItemDef("potion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 58, 1323));
		items.add(new ItemDef("Phoenix Crossbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 59, 1324));
		items.add(new ItemDef("Crossbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 60, 1325));
		items.add(new ItemDef("Certificate", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 61, 1326));
		items.add(new ItemDef("bronze dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 62, 1327));
		items.add(new ItemDef("Steel dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 63, 1328));
		items.add(new ItemDef("Mithril dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 64, 1329));
		items.add(new ItemDef("Adamantite dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 65, 1330));
		items.add(new ItemDef("Bronze Short Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 66, 1331));
		items.add(new ItemDef("Steel Short Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 67, 1332));
		items.add(new ItemDef("Mithril Short Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 68, 1333));
		items.add(new ItemDef("Adamantite Short Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 69, 1334));
		items.add(new ItemDef("Bronze Long Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 70, 1335));
		items.add(new ItemDef("Iron Long Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 71, 1336));
		items.add(new ItemDef("Steel Long Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 72, 1337));
		items.add(new ItemDef("Mithril Long Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 73, 1338));
		items.add(new ItemDef("Adamantite Long Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 74, 1339));
		items.add(new ItemDef("Rune long sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 75, 1340));
		items.add(new ItemDef("Bronze 2-handed Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 76, 1341));
		items.add(new ItemDef("Iron 2-handed Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 77, 1342));
		items.add(new ItemDef("Steel 2-handed Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 78, 1343));
		items.add(new ItemDef("Mithril 2-handed Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 79, 1344));
		items.add(new ItemDef("Adamantite 2-handed Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 80, 1345));
		items.add(new ItemDef("rune 2-handed Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 81, 1346));
		items.add(new ItemDef("Bronze Scimitar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 82, 1347));
		items.add(new ItemDef("Iron Scimitar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 83, 1348));
		items.add(new ItemDef("Steel Scimitar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 84, 1349));
		items.add(new ItemDef("Mithril Scimitar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 85, 1350));
		items.add(new ItemDef("Adamantite Scimitar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 86, 1351));
		items.add(new ItemDef("bronze Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 87, 1352));
		items.add(new ItemDef("Steel Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 88, 1353));
		items.add(new ItemDef("Iron battle Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 89, 1354));
		items.add(new ItemDef("Steel battle Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 90, 1355));
		items.add(new ItemDef("Mithril battle Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 91, 1356));
		items.add(new ItemDef("Adamantite battle Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 92, 1357));
		items.add(new ItemDef("Rune battle Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 93, 1358));
		items.add(new ItemDef("Bronze Mace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 94, 1359));
		items.add(new ItemDef("Steel Mace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 95, 1360));
		items.add(new ItemDef("Mithril Mace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 96, 1361));
		items.add(new ItemDef("Adamantite Mace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 97, 1362));
		items.add(new ItemDef("Rune Mace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 98, 1363));
		items.add(new ItemDef("Brass key", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 99, 1364));
		items.add(new ItemDef("staff", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 100, 1365));
		items.add(new ItemDef("Staff of Air", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 101, 1366));
		items.add(new ItemDef("Staff of water", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 102, 1367));
		items.add(new ItemDef("Staff of earth", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 103, 1368));
		items.add(new ItemDef("Medium Bronze Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 104, 1369));
		items.add(new ItemDef("Medium Steel Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 105, 1370));
		items.add(new ItemDef("Medium Mithril Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 106, 1371));
		items.add(new ItemDef("Medium Adamantite Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 107, 1372));
		items.add(new ItemDef("Large Bronze Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 108, 1373));
		items.add(new ItemDef("Large Steel Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 109, 1374));
		items.add(new ItemDef("Large Mithril Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 110, 1375));
		items.add(new ItemDef("Large Adamantite Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 111, 1376));
		items.add(new ItemDef("Large Rune Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 112, 1377));
		items.add(new ItemDef("Bronze Chain Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 113, 1378));
		items.add(new ItemDef("Steel Chain Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 114, 1379));
		items.add(new ItemDef("Mithril Chain Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 115, 1380));
		items.add(new ItemDef("Adamantite Chain Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 116, 1381));
		items.add(new ItemDef("Bronze Plate Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 117, 1382));
		items.add(new ItemDef("Steel Plate Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 118, 1383));
		items.add(new ItemDef("Mithril Plate Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 119, 1384));
		items.add(new ItemDef("Adamantite Plate Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 120, 1385));
		items.add(new ItemDef("Steel Plate Mail Legs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 121, 1386));
		items.add(new ItemDef("Mithril Plate Mail Legs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 122, 1387));
		items.add(new ItemDef("Adamantite Plate Mail Legs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 123, 1388));
		items.add(new ItemDef("Bronze Square Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 124, 1389));
		items.add(new ItemDef("Steel Square Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 125, 1390));
		items.add(new ItemDef("Mithril Square Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 126, 1391));
		items.add(new ItemDef("Adamantite Square Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 127, 1392));
		items.add(new ItemDef("Bronze Kite Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 128, 1393));
		items.add(new ItemDef("Steel Kite Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 129, 1394));
		items.add(new ItemDef("Mithril Kite Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 130, 1395));
		items.add(new ItemDef("Adamantite Kite Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 131, 1396));
		items.add(new ItemDef("cookedmeat", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 132, 1397));
		items.add(new ItemDef("raw chicken", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 133, 1398));
		items.add(new ItemDef("burntmeat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 134, 1399));
		items.add(new ItemDef("pot", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 135, 1400));
		items.add(new ItemDef("flour", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 136, 1401));
		items.add(new ItemDef("bread dough", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 137, 1402));
		items.add(new ItemDef("bread", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 138, 1403));
		items.add(new ItemDef("burntbread", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 139, 1404));
		items.add(new ItemDef("jug", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 140, 1405));
		items.add(new ItemDef("water", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 141, 1406));
		items.add(new ItemDef("wine", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 142, 1407));
		items.add(new ItemDef("grapes", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 143, 1408));
		items.add(new ItemDef("shears", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 144, 1409));
		items.add(new ItemDef("wool", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 145, 1410));
		items.add(new ItemDef("fur", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 146, 1411));
		items.add(new ItemDef("cow hide", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 147, 1412));
		items.add(new ItemDef("leather", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 148, 1413));
		items.add(new ItemDef("clay", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 149, 1414));
		items.add(new ItemDef("copper ore", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 150, 1415));
		items.add(new ItemDef("iron ore", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 151, 1416));
		items.add(new ItemDef("gold", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 152, 1417));
		items.add(new ItemDef("mithril ore", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 153, 1418));
		items.add(new ItemDef("adamantite ore", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 154, 1419));
		items.add(new ItemDef("coal", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 155, 1420));
		items.add(new ItemDef("Bronze Pickaxe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 156, 1421));
		items.add(new ItemDef("uncut diamond", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 157, 1422));
		items.add(new ItemDef("uncut ruby", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 158, 1423));
		items.add(new ItemDef("uncut emerald", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 159, 1424));
		items.add(new ItemDef("uncut sapphire", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 160, 1425));
		items.add(new ItemDef("diamond", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 161, 1426));
		items.add(new ItemDef("ruby", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 162, 1427));
		items.add(new ItemDef("emerald", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 163, 1428));
		items.add(new ItemDef("sapphire", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 164, 1429));
		items.add(new ItemDef("Herb", "Swap this note at any bank for the equivalent item.", "Identify", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 165, 1430));
		items.add(new ItemDef("tinderbox", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 166, 1431));
		items.add(new ItemDef("chisel", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 167, 1432));
		items.add(new ItemDef("hammer", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 168, 1433));
		items.add(new ItemDef("bronze bar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 169, 1434));
		items.add(new ItemDef("iron bar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 170, 1435));
		items.add(new ItemDef("steel bar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 171, 1436));
		items.add(new ItemDef("gold bar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 172, 1437));
		items.add(new ItemDef("mithril bar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 173, 1438));
		items.add(new ItemDef("adamantite bar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 174, 1439));
		items.add(new ItemDef("Fish Food", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 176, 1440));
		items.add(new ItemDef("Poison", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 177, 1441));
		items.add(new ItemDef("spinach roll", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 179, 1442));
		items.add(new ItemDef("Bad wine", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 180, 1443));
		items.add(new ItemDef("Ashes", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 181, 1444));
		items.add(new ItemDef("Apron", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 182, 1445));
		items.add(new ItemDef("Cape", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 183, 1446));
		items.add(new ItemDef("Wizards robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 184, 1447));
		items.add(new ItemDef("wizardshat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 185, 1448));
		items.add(new ItemDef("Brass necklace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 186, 1449));
		items.add(new ItemDef("skirt", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 187, 1450));
		items.add(new ItemDef("Longbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 188, 1451));
		items.add(new ItemDef("Shortbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 189, 1452));
		items.add(new ItemDef("Apron", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 191, 1453));
		items.add(new ItemDef("Chef's hat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 192, 1454));
		items.add(new ItemDef("Beer", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 193, 1455));
		items.add(new ItemDef("skirt", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 194, 1456));
		items.add(new ItemDef("skirt", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 195, 1457));
		items.add(new ItemDef("Black Plate Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 196, 1458));
		items.add(new ItemDef("Staff of fire", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 197, 1459));
		items.add(new ItemDef("Magic Staff", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 198, 1460));
		items.add(new ItemDef("wizardshat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 199, 1461));
		items.add(new ItemDef("silk", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 200, 1462));
		items.add(new ItemDef("flier", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 201, 1463));
		items.add(new ItemDef("tin ore", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 202, 1464));
		items.add(new ItemDef("Mithril Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 203, 1465));
		items.add(new ItemDef("Adamantite Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 204, 1466));
		items.add(new ItemDef("bronze battle Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 205, 1467));
		items.add(new ItemDef("Bronze Plate Mail Legs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 206, 1468));
		items.add(new ItemDef("Ball of wool", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 207, 1469));
		items.add(new ItemDef("Cape", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 209, 1470));
		items.add(new ItemDef("Kebab", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 210, 1471));
		items.add(new ItemDef("Spade", "Swap this note at any bank for the equivalent item.", "Dig", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 211, 1472));
		items.add(new ItemDef("Bronze Plated Skirt", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 214, 1473));
		items.add(new ItemDef("Iron Plated Skirt", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 215, 1474));
		items.add(new ItemDef("Black robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 216, 1475));
		items.add(new ItemDef("Garlic", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 218, 1476));
		items.add(new ItemDef("Red spiders eggs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 219, 1477));
		items.add(new ItemDef("Limpwurt root", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 220, 1478));
		items.add(new ItemDef("Strength Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 221, 1479));
		items.add(new ItemDef("Strength Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 222, 1480));
		items.add(new ItemDef("Strength Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 223, 1481));
		items.add(new ItemDef("Strength Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 224, 1482));
		items.add(new ItemDef("Steel Plated skirt", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 225, 1483));
		items.add(new ItemDef("Mithril Plated skirt", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 226, 1484));
		items.add(new ItemDef("Adamantite Plated skirt", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 227, 1485));
		items.add(new ItemDef("Cabbage", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 228, 1486));
		items.add(new ItemDef("Cape", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 229, 1487));
		items.add(new ItemDef("Large Black Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 230, 1488));
		items.add(new ItemDef("Red Bead", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 231, 1489));
		items.add(new ItemDef("Yellow Bead", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 232, 1490));
		items.add(new ItemDef("Black Bead", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 233, 1491));
		items.add(new ItemDef("White Bead", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 234, 1492));
		items.add(new ItemDef("Amulet of accuracy", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 235, 1493));
		items.add(new ItemDef("Redberries", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 236, 1494));
		items.add(new ItemDef("Rope", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 237, 1495));
		items.add(new ItemDef("Reddye", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 238, 1496));
		items.add(new ItemDef("Yellowdye", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 239, 1497));
		items.add(new ItemDef("Onion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 241, 1498));
		items.add(new ItemDef("Soft Clay", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 243, 1499));
		items.add(new ItemDef("Half full wine jug", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 246, 1500));
		items.add(new ItemDef("Black Plate Mail Legs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 248, 1501));
		items.add(new ItemDef("banana", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 249, 1502));
		items.add(new ItemDef("pastry dough", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 250, 1503));
		items.add(new ItemDef("Pie dish", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 251, 1504));
		items.add(new ItemDef("cooking apple", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 252, 1505));
		items.add(new ItemDef("pie shell", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 253, 1506));
		items.add(new ItemDef("Uncooked apple pie", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 254, 1507));
		items.add(new ItemDef("Uncooked meat pie", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 255, 1508));
		items.add(new ItemDef("Uncooked redberry pie", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 256, 1509));
		items.add(new ItemDef("apple pie", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 257, 1510));
		items.add(new ItemDef("Redberry pie", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 258, 1511));
		items.add(new ItemDef("meat pie", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 259, 1512));
		items.add(new ItemDef("burntpie", "Swap this note at any bank for the equivalent item.", "empty dish", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 260, 1513));
		items.add(new ItemDef("Half a meat pie", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 261, 1514));
		items.add(new ItemDef("Half a Redberry pie", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 262, 1515));
		items.add(new ItemDef("Half an apple pie", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 263, 1516));
		items.add(new ItemDef("Asgarnian Ale", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 267, 1517));
		items.add(new ItemDef("Wizard's Mind Bomb", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 268, 1518));
		items.add(new ItemDef("Dwarven Stout", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 269, 1519));
		items.add(new ItemDef("Eye of newt", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 270, 1520));
		items.add(new ItemDef("Bluedye", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 272, 1521));
		items.add(new ItemDef("Goblin Armour", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 273, 1522));
		items.add(new ItemDef("unstrung Longbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 276, 1523));
		items.add(new ItemDef("unstrung shortbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 277, 1524));
		items.add(new ItemDef("Unfired Pie dish", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 278, 1525));
		items.add(new ItemDef("unfired pot", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 279, 1526));
		items.add(new ItemDef("Orangedye", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 282, 1527));
		items.add(new ItemDef("Gold ring", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 283, 1528));
		items.add(new ItemDef("Sapphire ring", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 284, 1529));
		items.add(new ItemDef("Emerald ring", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 285, 1530));
		items.add(new ItemDef("Ruby ring", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 286, 1531));
		items.add(new ItemDef("Diamond ring", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 287, 1532));
		items.add(new ItemDef("Gold necklace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 288, 1533));
		items.add(new ItemDef("Sapphire necklace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 289, 1534));
		items.add(new ItemDef("Emerald necklace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 290, 1535));
		items.add(new ItemDef("Ruby necklace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 291, 1536));
		items.add(new ItemDef("Diamond necklace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 292, 1537));
		items.add(new ItemDef("ring mould", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 293, 1538));
		items.add(new ItemDef("Amulet mould", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 294, 1539));
		items.add(new ItemDef("Necklace mould", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 295, 1540));
		items.add(new ItemDef("Gold Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 296, 1541));
		items.add(new ItemDef("Sapphire Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 297, 1542));
		items.add(new ItemDef("Emerald Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 298, 1543));
		items.add(new ItemDef("Ruby Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 299, 1544));
		items.add(new ItemDef("Diamond Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 300, 1545));
		items.add(new ItemDef("Gold Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 301, 1546));
		items.add(new ItemDef("Sapphire Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 302, 1547));
		items.add(new ItemDef("Emerald Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 303, 1548));
		items.add(new ItemDef("Ruby Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 304, 1549));
		items.add(new ItemDef("Diamond Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 305, 1550));
		items.add(new ItemDef("superchisel", "Swap this note at any bank for the equivalent item.", "twiddle", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 306, 1551));
		items.add(new ItemDef("Mace of Zamorak", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 307, 1552));
		items.add(new ItemDef("Bronze Plate Mail top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 308, 1553));
		items.add(new ItemDef("Steel Plate Mail top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 309, 1554));
		items.add(new ItemDef("Mithril Plate Mail top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 310, 1555));
		items.add(new ItemDef("Adamantite Plate Mail top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 311, 1556));
		items.add(new ItemDef("Iron Plate Mail top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 312, 1557));
		items.add(new ItemDef("Black Plate Mail top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 313, 1558));
		items.add(new ItemDef("Sapphire Amulet of magic", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 314, 1559));
		items.add(new ItemDef("Emerald Amulet of protection", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 315, 1560));
		items.add(new ItemDef("Ruby Amulet of strength", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 316, 1561));
		items.add(new ItemDef("Diamond Amulet of power", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 317, 1562));
		items.add(new ItemDef("Cheese", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 319, 1563));
		items.add(new ItemDef("Tomato", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 320, 1564));
		items.add(new ItemDef("Pizza Base", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 321, 1565));
		items.add(new ItemDef("Burnt Pizza", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 322, 1566));
		items.add(new ItemDef("Incomplete Pizza", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 323, 1567));
		items.add(new ItemDef("Uncooked Pizza", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 324, 1568));
		items.add(new ItemDef("Plain Pizza", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 325, 1569));
		items.add(new ItemDef("Meat Pizza", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 326, 1570));
		items.add(new ItemDef("Anchovie Pizza", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 327, 1571));
		items.add(new ItemDef("Half Meat Pizza", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 328, 1572));
		items.add(new ItemDef("Half Anchovie Pizza", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 329, 1573));
		items.add(new ItemDef("Cake", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 330, 1574));
		items.add(new ItemDef("Burnt Cake", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 331, 1575));
		items.add(new ItemDef("Chocolate Cake", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 332, 1576));
		items.add(new ItemDef("Partial Cake", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 333, 1577));
		items.add(new ItemDef("Partial Chocolate Cake", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 334, 1578));
		items.add(new ItemDef("Slice of Cake", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 335, 1579));
		items.add(new ItemDef("Chocolate Slice", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 336, 1580));
		items.add(new ItemDef("Chocolate Bar", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 337, 1581));
		items.add(new ItemDef("Cake Tin", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 338, 1582));
		items.add(new ItemDef("Uncooked cake", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 339, 1583));
		items.add(new ItemDef("Unfired bowl", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 340, 1584));
		items.add(new ItemDef("Bowl", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 341, 1585));
		items.add(new ItemDef("Bowl of water", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 342, 1586));
		items.add(new ItemDef("Incomplete stew", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 343, 1587));
		items.add(new ItemDef("Incomplete stew", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 344, 1588));
		items.add(new ItemDef("Uncooked stew", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 345, 1589));
		items.add(new ItemDef("Stew", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 346, 1590));
		items.add(new ItemDef("Burnt Stew", "Swap this note at any bank for the equivalent item.", "Empty", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 347, 1591));
		items.add(new ItemDef("Potato", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 348, 1592));
		items.add(new ItemDef("Raw Shrimp", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 349, 1593));
		items.add(new ItemDef("Shrimp", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 350, 1594));
		items.add(new ItemDef("Raw Anchovies", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 351, 1595));
		items.add(new ItemDef("Anchovies", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 352, 1596));
		items.add(new ItemDef("Burnt fish", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 353, 1597));
		items.add(new ItemDef("Raw Sardine", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 354, 1598));
		items.add(new ItemDef("Sardine", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 355, 1599));
		items.add(new ItemDef("Raw Salmon", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 356, 1600));
		items.add(new ItemDef("Salmon", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 357, 1601));
		items.add(new ItemDef("Raw Trout", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 358, 1602));
		items.add(new ItemDef("Trout", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 359, 1603));
		items.add(new ItemDef("Burnt fish", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 360, 1604));
		items.add(new ItemDef("Raw Herring", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 361, 1605));
		items.add(new ItemDef("Herring", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 362, 1606));
		items.add(new ItemDef("Raw Pike", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 363, 1607));
		items.add(new ItemDef("Pike", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 364, 1608));
		items.add(new ItemDef("Burnt fish", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 365, 1609));
		items.add(new ItemDef("Raw Tuna", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 366, 1610));
		items.add(new ItemDef("Tuna", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 367, 1611));
		items.add(new ItemDef("Burnt fish", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 368, 1612));
		items.add(new ItemDef("Raw Swordfish", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 369, 1613));
		items.add(new ItemDef("Swordfish", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 370, 1614));
		items.add(new ItemDef("Burnt Swordfish", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 371, 1615));
		items.add(new ItemDef("Raw Lobster", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 372, 1616));
		items.add(new ItemDef("Lobster", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 373, 1617));
		items.add(new ItemDef("Burnt Lobster", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 374, 1618));
		items.add(new ItemDef("Lobster Pot", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 375, 1619));
		items.add(new ItemDef("Net", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 376, 1620));
		items.add(new ItemDef("Fishing Rod", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 377, 1621));
		items.add(new ItemDef("Fly Fishing Rod", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 378, 1622));
		items.add(new ItemDef("Harpoon", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 379, 1623));
		items.add(new ItemDef("Silver", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 383, 1624));
		items.add(new ItemDef("silver bar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 384, 1625));
		items.add(new ItemDef("Holy Symbol of saradomin", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 385, 1626));
		items.add(new ItemDef("Holy symbol mould", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 386, 1627));
		items.add(new ItemDef("Disk of Returning", "Swap this note at any bank for the equivalent item.", "spin", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 387, 1628));
		items.add(new ItemDef("Monks robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 388, 1629));
		items.add(new ItemDef("Monks robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 389, 1630));
		items.add(new ItemDef("rune dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 396, 1631));
		items.add(new ItemDef("Rune short sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 397, 1632));
		items.add(new ItemDef("rune Scimitar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 398, 1633));
		items.add(new ItemDef("Medium Rune Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 399, 1634));
		items.add(new ItemDef("Rune Chain Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 400, 1635));
		items.add(new ItemDef("Rune Plate Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 401, 1636));
		items.add(new ItemDef("Rune Plate Mail Legs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 402, 1637));
		items.add(new ItemDef("Rune Square Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 403, 1638));
		items.add(new ItemDef("Rune Kite Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 404, 1639));
		items.add(new ItemDef("rune Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 405, 1640));
		items.add(new ItemDef("Rune skirt", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 406, 1641));
		items.add(new ItemDef("Rune Plate Mail top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 407, 1642));
		items.add(new ItemDef("Runite bar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 408, 1643));
		items.add(new ItemDef("runite ore", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 409, 1644));
		items.add(new ItemDef("Plank", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 410, 1645));
		items.add(new ItemDef("Tile", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 411, 1646));
		items.add(new ItemDef("skull", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 412, 1647));
		items.add(new ItemDef("Big Bones", "Swap this note at any bank for the equivalent item.", "Bury", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 413, 1648));
		items.add(new ItemDef("Muddy key", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 414, 1649));
		items.add(new ItemDef("Anti dragon breath Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 420, 1650));
		items.add(new ItemDef("Maze key", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 421, 1651));
		items.add(new ItemDef("Pumpkin", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 422, 1652));
		items.add(new ItemDef("Black dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 423, 1653));
		items.add(new ItemDef("Black Short Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 424, 1654));
		items.add(new ItemDef("Black Long Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 425, 1655));
		items.add(new ItemDef("Black 2-handed Sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 426, 1656));
		items.add(new ItemDef("Black Scimitar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 427, 1657));
		items.add(new ItemDef("Black Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 428, 1658));
		items.add(new ItemDef("Black battle Axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 429, 1659));
		items.add(new ItemDef("Black Mace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 430, 1660));
		items.add(new ItemDef("Black Chain Mail Body", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 431, 1661));
		items.add(new ItemDef("Black Square Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 432, 1662));
		items.add(new ItemDef("Black Kite Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 433, 1663));
		items.add(new ItemDef("Black Plated skirt", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 434, 1664));
		items.add(new ItemDef("Herb", "Swap this note at any bank for the equivalent item.", "Identify", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 435, 1665));
		items.add(new ItemDef("Herb", "Swap this note at any bank for the equivalent item.", "Identify", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 436, 1666));
		items.add(new ItemDef("Herb", "Swap this note at any bank for the equivalent item.", "Identify", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 437, 1667));
		items.add(new ItemDef("Herb", "Swap this note at any bank for the equivalent item.", "Identify", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 438, 1668));
		items.add(new ItemDef("Herb", "Swap this note at any bank for the equivalent item.", "Identify", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 439, 1669));
		items.add(new ItemDef("Herb", "Swap this note at any bank for the equivalent item.", "Identify", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 440, 1670));
		items.add(new ItemDef("Herb", "Swap this note at any bank for the equivalent item.", "Identify", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 441, 1671));
		items.add(new ItemDef("Herb", "Swap this note at any bank for the equivalent item.", "Identify", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 442, 1672));
		items.add(new ItemDef("Herb", "Swap this note at any bank for the equivalent item.", "Identify", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 443, 1673));
		items.add(new ItemDef("Guam leaf", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 444, 1674));
		items.add(new ItemDef("Marrentill", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 445, 1675));
		items.add(new ItemDef("Tarromin", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 446, 1676));
		items.add(new ItemDef("Harralander", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 447, 1677));
		items.add(new ItemDef("Ranarr Weed", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 448, 1678));
		items.add(new ItemDef("Irit Leaf", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 449, 1679));
		items.add(new ItemDef("Avantoe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 450, 1680));
		items.add(new ItemDef("Kwuarm", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 451, 1681));
		items.add(new ItemDef("Cadantine", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 452, 1682));
		items.add(new ItemDef("Dwarf Weed", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 453, 1683));
		items.add(new ItemDef("Unfinished potion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 454, 1684));
		items.add(new ItemDef("Unfinished potion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 455, 1685));
		items.add(new ItemDef("Unfinished potion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 456, 1686));
		items.add(new ItemDef("Unfinished potion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 457, 1687));
		items.add(new ItemDef("Unfinished potion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 458, 1688));
		items.add(new ItemDef("Unfinished potion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 459, 1689));
		items.add(new ItemDef("Unfinished potion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 460, 1690));
		items.add(new ItemDef("Unfinished potion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 461, 1691));
		items.add(new ItemDef("Unfinished potion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 462, 1692));
		items.add(new ItemDef("Unfinished potion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 463, 1693));
		items.add(new ItemDef("Vial", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 464, 1694));
		items.add(new ItemDef("Vial", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 465, 1695));
		items.add(new ItemDef("Unicorn horn", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 466, 1696));
		items.add(new ItemDef("Blue dragon scale", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 467, 1697));
		items.add(new ItemDef("Pestle and mortar", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 468, 1698));
		items.add(new ItemDef("Snape grass", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 469, 1699));
		items.add(new ItemDef("Medium black Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 470, 1700));
		items.add(new ItemDef("White berries", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 471, 1701));
		items.add(new ItemDef("Ground blue dragon scale", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 472, 1702));
		items.add(new ItemDef("Ground unicorn horn", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 473, 1703));
		items.add(new ItemDef("attack Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 474, 1704));
		items.add(new ItemDef("attack Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 475, 1705));
		items.add(new ItemDef("attack Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 476, 1706));
		items.add(new ItemDef("stat restoration Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 477, 1707));
		items.add(new ItemDef("stat restoration Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 478, 1708));
		items.add(new ItemDef("stat restoration Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 479, 1709));
		items.add(new ItemDef("defense Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 480, 1710));
		items.add(new ItemDef("defense Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 481, 1711));
		items.add(new ItemDef("defense Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 482, 1712));
		items.add(new ItemDef("restore prayer Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 483, 1713));
		items.add(new ItemDef("restore prayer Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 484, 1714));
		items.add(new ItemDef("restore prayer Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 485, 1715));
		items.add(new ItemDef("Super attack Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 486, 1716));
		items.add(new ItemDef("Super attack Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 487, 1717));
		items.add(new ItemDef("Super attack Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 488, 1718));
		items.add(new ItemDef("fishing Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 489, 1719));
		items.add(new ItemDef("fishing Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 490, 1720));
		items.add(new ItemDef("fishing Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 491, 1721));
		items.add(new ItemDef("Super strength Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 492, 1722));
		items.add(new ItemDef("Super strength Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 493, 1723));
		items.add(new ItemDef("Super strength Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 494, 1724));
		items.add(new ItemDef("Super defense Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 495, 1725));
		items.add(new ItemDef("Super defense Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 496, 1726));
		items.add(new ItemDef("Super defense Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 497, 1727));
		items.add(new ItemDef("ranging Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 498, 1728));
		items.add(new ItemDef("ranging Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 499, 1729));
		items.add(new ItemDef("ranging Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 500, 1730));
		items.add(new ItemDef("wine of Zamorak", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 501, 1731));
		items.add(new ItemDef("raw bear meat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 502, 1732));
		items.add(new ItemDef("raw rat meat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 503, 1733));
		items.add(new ItemDef("raw beef", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 504, 1734));
		items.add(new ItemDef("Cape", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 511, 1735));
		items.add(new ItemDef("Cape", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 512, 1736));
		items.add(new ItemDef("Cape", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 513, 1737));
		items.add(new ItemDef("Cape", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 514, 1738));
		items.add(new ItemDef("Greendye", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 515, 1739));
		items.add(new ItemDef("Purpledye", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 516, 1740));
		items.add(new ItemDef("Dragonstone Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 522, 1741));
		items.add(new ItemDef("Dragonstone", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 523, 1742));
		items.add(new ItemDef("Dragonstone Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 524, 1743));
		items.add(new ItemDef("Crystal key", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 525, 1744));
		items.add(new ItemDef("Half of a key", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 526, 1745));
		items.add(new ItemDef("Half of a key", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 527, 1746));
		items.add(new ItemDef("Diary", "Swap this note at any bank for the equivalent item.", "read", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 537, 1747));
		items.add(new ItemDef("Grey wolf fur", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 541, 1748));
		items.add(new ItemDef("uncut dragonstone", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 542, 1749));
		items.add(new ItemDef("Dragonstone ring", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 543, 1750));
		items.add(new ItemDef("Dragonstone necklace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 544, 1751));
		items.add(new ItemDef("Raw Shark", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 545, 1752));
		items.add(new ItemDef("Shark", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 546, 1753));
		items.add(new ItemDef("Burnt Shark", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 547, 1754));
		items.add(new ItemDef("Big Net", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 548, 1755));
		items.add(new ItemDef("Casket", "Swap this note at any bank for the equivalent item.", "open", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 549, 1756));
		items.add(new ItemDef("Raw cod", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 550, 1757));
		items.add(new ItemDef("Cod", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 551, 1758));
		items.add(new ItemDef("Raw Mackerel", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 552, 1759));
		items.add(new ItemDef("Mackerel", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 553, 1760));
		items.add(new ItemDef("Raw Bass", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 554, 1761));
		items.add(new ItemDef("Bass", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 555, 1762));
		items.add(new ItemDef("Poisoned Iron dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 559, 1763));
		items.add(new ItemDef("Poisoned bronze dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 560, 1764));
		items.add(new ItemDef("Poisoned Steel dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 561, 1765));
		items.add(new ItemDef("Poisoned Mithril dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 562, 1766));
		items.add(new ItemDef("Poisoned Rune dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 563, 1767));
		items.add(new ItemDef("Poisoned Adamantite dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 564, 1768));
		items.add(new ItemDef("Poisoned Black dagger", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 565, 1769));
		items.add(new ItemDef("Cure poison Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 566, 1770));
		items.add(new ItemDef("Cure poison Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 567, 1771));
		items.add(new ItemDef("Cure poison Potion", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 568, 1772));
		items.add(new ItemDef("Poison antidote", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 569, 1773));
		items.add(new ItemDef("Poison antidote", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 570, 1774));
		items.add(new ItemDef("Poison antidote", "Swap this note at any bank for the equivalent item.", "Drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 571, 1775));
		items.add(new ItemDef("weapon poison", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 572, 1776));
		items.add(new ItemDef("ID Paper", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 573, 1777));
		items.add(new ItemDef("Christmas cracker", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 575, 1778));
		items.add(new ItemDef("Party Hat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 576, 1779));
		items.add(new ItemDef("Party Hat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 577, 1780));
		items.add(new ItemDef("Party Hat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 578, 1781));
		items.add(new ItemDef("Party Hat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 579, 1782));
		items.add(new ItemDef("Party Hat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 580, 1783));
		items.add(new ItemDef("Party Hat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 581, 1784));
		items.add(new ItemDef("Miscellaneous key", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 582, 1785));
		items.add(new ItemDef("Bunch of keys", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 583, 1786));
		items.add(new ItemDef("Whisky", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 584, 1787));
		items.add(new ItemDef("Candlestick", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 585, 1788));
		items.add(new ItemDef("Dragon sword", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 593, 1789));
		items.add(new ItemDef("Dragon axe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 594, 1790));
		items.add(new ItemDef("Charged Dragonstone Amulet", "Swap this note at any bank for the equivalent item.", "rub", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 597, 1791));
		items.add(new ItemDef("Grog", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 598, 1792));
		items.add(new ItemDef("Bat bones", "Swap this note at any bank for the equivalent item.", "Bury", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 604, 1793));
		items.add(new ItemDef("Druids robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 607, 1794));
		items.add(new ItemDef("Druids robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 608, 1795));
		items.add(new ItemDef("Eye patch", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 609, 1796));
		items.add(new ItemDef("Unenchanted Dragonstone Amulet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 610, 1797));
		items.add(new ItemDef("Unpowered orb", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 611, 1798));
		items.add(new ItemDef("Fire orb", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 612, 1799));
		items.add(new ItemDef("Water orb", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 613, 1800));
		items.add(new ItemDef("Battlestaff", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 614, 1801));
		items.add(new ItemDef("Battlestaff of fire", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 615, 1802));
		items.add(new ItemDef("Battlestaff of water", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 616, 1803));
		items.add(new ItemDef("Battlestaff of air", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 617, 1804));
		items.add(new ItemDef("Battlestaff of earth", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 618, 1805));
		items.add(new ItemDef("Beer glass", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 620, 1806));
		items.add(new ItemDef("glassblowing pipe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 621, 1807));
		items.add(new ItemDef("seaweed", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 622, 1808));
		items.add(new ItemDef("molten glass", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 623, 1809));
		items.add(new ItemDef("soda ash", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 624, 1810));
		items.add(new ItemDef("sand", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 625, 1811));
		items.add(new ItemDef("air orb", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 626, 1812));
		items.add(new ItemDef("earth orb", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 627, 1813));
		items.add(new ItemDef("Oak Logs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 632, 1814));
		items.add(new ItemDef("Willow Logs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 633, 1815));
		items.add(new ItemDef("Maple Logs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 634, 1816));
		items.add(new ItemDef("Yew Logs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 635, 1817));
		items.add(new ItemDef("Magic Logs", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 636, 1818));
		items.add(new ItemDef("Oak Longbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 648, 1819));
		items.add(new ItemDef("Oak Shortbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 649, 1820));
		items.add(new ItemDef("Willow Longbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 650, 1821));
		items.add(new ItemDef("Willow Shortbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 651, 1822));
		items.add(new ItemDef("Maple Longbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 652, 1823));
		items.add(new ItemDef("Maple Shortbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 653, 1824));
		items.add(new ItemDef("Yew Longbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 654, 1825));
		items.add(new ItemDef("Yew Shortbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 655, 1826));
		items.add(new ItemDef("Magic Longbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 656, 1827));
		items.add(new ItemDef("Magic Shortbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 657, 1828));
		items.add(new ItemDef("unstrung Oak Longbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 658, 1829));
		items.add(new ItemDef("unstrung Oak Shortbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 659, 1830));
		items.add(new ItemDef("unstrung Willow Longbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 660, 1831));
		items.add(new ItemDef("unstrung Willow Shortbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 661, 1832));
		items.add(new ItemDef("unstrung Maple Longbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 662, 1833));
		items.add(new ItemDef("unstrung Maple Shortbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 663, 1834));
		items.add(new ItemDef("unstrung Yew Longbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 664, 1835));
		items.add(new ItemDef("unstrung Yew Shortbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 665, 1836));
		items.add(new ItemDef("unstrung Magic Longbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 666, 1837));
		items.add(new ItemDef("unstrung Magic Shortbow", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 667, 1838));
		items.add(new ItemDef("flax", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 675, 1839));
		items.add(new ItemDef("bow string", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 676, 1840));
		items.add(new ItemDef("Easter egg", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 677, 1841));
		items.add(new ItemDef("Enchanted Battlestaff of fire", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 682, 1842));
		items.add(new ItemDef("Enchanted Battlestaff of water", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 683, 1843));
		items.add(new ItemDef("Enchanted Battlestaff of air", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 684, 1844));
		items.add(new ItemDef("Enchanted Battlestaff of earth", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 685, 1845));
		items.add(new ItemDef("robe of Zamorak", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 702, 1846));
		items.add(new ItemDef("robe of Zamorak", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 703, 1847));
		items.add(new ItemDef("tourist guide", "Swap this note at any bank for the equivalent item.", "read", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 706, 1848));
		items.add(new ItemDef("spice", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 707, 1849));
		items.add(new ItemDef("Uncooked curry", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 708, 1850));
		items.add(new ItemDef("curry", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 709, 1851));
		items.add(new ItemDef("Burnt curry", "Swap this note at any bank for the equivalent item.", "Empty", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 710, 1852));
		items.add(new ItemDef("lockpick", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 714, 1853));
		items.add(new ItemDef("Rat Poison", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 731, 1854));
		items.add(new ItemDef("khali brew", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 735, 1855));
		items.add(new ItemDef("Cup of tea", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 739, 1856));
		items.add(new ItemDef("Pineapple", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 748, 1857));
		items.add(new ItemDef("Pineapple ring", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 749, 1858));
		items.add(new ItemDef("Pineapple Pizza", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 750, 1859));
		items.add(new ItemDef("Half pineapple Pizza", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 751, 1860));
		items.add(new ItemDef("dwellberries", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 765, 1861));
		items.add(new ItemDef("Chocolate dust", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 772, 1862));
		items.add(new ItemDef("oyster pearls", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 779, 1863));
		items.add(new ItemDef("Scruffy note", "Swap this note at any bank for the equivalent item.", "read", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 781, 1864));
		items.add(new ItemDef("oyster", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 791, 1865));
		items.add(new ItemDef("oyster pearls", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 792, 1866));
		items.add(new ItemDef("oyster", "Swap this note at any bank for the equivalent item.", "open", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 793, 1867));
		items.add(new ItemDef("Dragon medium Helmet", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 795, 1868));
		items.add(new ItemDef("Priest robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 807, 1869));
		items.add(new ItemDef("Priest gown", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 808, 1870));
		items.add(new ItemDef("Dragon Bones", "Swap this note at any bank for the equivalent item.", "Bury", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 814, 1871));
		items.add(new ItemDef("Bronze Spear", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 827, 1872));
		items.add(new ItemDef("halloween mask", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 828, 1873));
		items.add(new ItemDef("Dragon bitter", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 829, 1874));
		items.add(new ItemDef("Greenmans ale", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 830, 1875));
		items.add(new ItemDef("halloween mask", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 831, 1876));
		items.add(new ItemDef("halloween mask", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 832, 1877));
		items.add(new ItemDef("cocktail glass", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 833, 1878));
		items.add(new ItemDef("cocktail shaker", "Swap this note at any bank for the equivalent item.", "pour", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 834, 1879));
		items.add(new ItemDef("gnome robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 836, 1880));
		items.add(new ItemDef("gnome robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 837, 1881));
		items.add(new ItemDef("gnome robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 838, 1882));
		items.add(new ItemDef("gnome robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 839, 1883));
		items.add(new ItemDef("gnome robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 840, 1884));
		items.add(new ItemDef("gnomeshat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 841, 1885));
		items.add(new ItemDef("gnomeshat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 842, 1886));
		items.add(new ItemDef("gnomeshat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 843, 1887));
		items.add(new ItemDef("gnomeshat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 844, 1888));
		items.add(new ItemDef("gnomeshat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 845, 1889));
		items.add(new ItemDef("gnome top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 846, 1890));
		items.add(new ItemDef("gnome top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 847, 1891));
		items.add(new ItemDef("gnome top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 848, 1892));
		items.add(new ItemDef("gnome top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 849, 1893));
		items.add(new ItemDef("gnome top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 850, 1894));
		items.add(new ItemDef("gnome cocktail guide", "Swap this note at any bank for the equivalent item.", "read", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 851, 1895));
		items.add(new ItemDef("cocktail glass", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 853, 1896));
		items.add(new ItemDef("cocktail glass", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 854, 1897));
		items.add(new ItemDef("lemon", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 855, 1898));
		items.add(new ItemDef("lemon slices", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 856, 1899));
		items.add(new ItemDef("orange", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 857, 1900));
		items.add(new ItemDef("orange slices", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 858, 1901));
		items.add(new ItemDef("Diced orange", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 859, 1902));
		items.add(new ItemDef("Diced lemon", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 860, 1903));
		items.add(new ItemDef("Fresh Pineapple", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 861, 1904));
		items.add(new ItemDef("Pineapple chunks", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 862, 1905));
		items.add(new ItemDef("lime", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 863, 1906));
		items.add(new ItemDef("lime chunks", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 864, 1907));
		items.add(new ItemDef("lime slices", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 865, 1908));
		items.add(new ItemDef("fruit blast", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 866, 1909));
		items.add(new ItemDef("odd looking cocktail", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 867, 1910));
		items.add(new ItemDef("Whisky", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 868, 1911));
		items.add(new ItemDef("vodka", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 869, 1912));
		items.add(new ItemDef("gin", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 870, 1913));
		items.add(new ItemDef("cream", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 871, 1914));
		items.add(new ItemDef("Drunk dragon", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 872, 1915));
		items.add(new ItemDef("Equa leaves", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 873, 1916));
		items.add(new ItemDef("SGG", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 874, 1917));
		items.add(new ItemDef("Chocolate saturday", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 875, 1918));
		items.add(new ItemDef("brandy", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 876, 1919));
		items.add(new ItemDef("blurberry special", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 877, 1920));
		items.add(new ItemDef("wizard blizzard", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 878, 1921));
		items.add(new ItemDef("pineapple punch", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 879, 1922));
		items.add(new ItemDef("gnomebatta dough", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 880, 1923));
		items.add(new ItemDef("gianne dough", "Swap this note at any bank for the equivalent item.", "mould", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 881, 1924));
		items.add(new ItemDef("gnomebowl dough", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 882, 1925));
		items.add(new ItemDef("gnomecrunchie dough", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 883, 1926));
		items.add(new ItemDef("gnomebatta", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 884, 1927));
		items.add(new ItemDef("gnomebowl", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 885, 1928));
		items.add(new ItemDef("gnomebatta", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 886, 1929));
		items.add(new ItemDef("gnomecrunchie", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 887, 1930));
		items.add(new ItemDef("gnomebowl", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 888, 1931));
		items.add(new ItemDef("Uncut Red Topaz", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 889, 1932));
		items.add(new ItemDef("Uncut Jade", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 890, 1933));
		items.add(new ItemDef("Uncut Opal", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 891, 1934));
		items.add(new ItemDef("Red Topaz", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 892, 1935));
		items.add(new ItemDef("Jade", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 893, 1936));
		items.add(new ItemDef("Opal", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 894, 1937));
		items.add(new ItemDef("Swamp Toad", "Swap this note at any bank for the equivalent item.", "remove legs", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 895, 1938));
		items.add(new ItemDef("Toad legs", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 896, 1939));
		items.add(new ItemDef("King worm", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 897, 1940));
		items.add(new ItemDef("Gnome spice", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 898, 1941));
		items.add(new ItemDef("gianne cook book", "Swap this note at any bank for the equivalent item.", "read", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 899, 1942));
		items.add(new ItemDef("gnomecrunchie", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 900, 1943));
		items.add(new ItemDef("cheese and tomato batta", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 901, 1944));
		items.add(new ItemDef("toad batta", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 902, 1945));
		items.add(new ItemDef("gnome batta", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 903, 1946));
		items.add(new ItemDef("worm batta", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 904, 1947));
		items.add(new ItemDef("fruit batta", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 905, 1948));
		items.add(new ItemDef("Veg batta", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 906, 1949));
		items.add(new ItemDef("Chocolate bomb", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 907, 1950));
		items.add(new ItemDef("Vegball", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 908, 1951));
		items.add(new ItemDef("worm hole", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 909, 1952));
		items.add(new ItemDef("Tangled toads legs", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 910, 1953));
		items.add(new ItemDef("Choc crunchies", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 911, 1954));
		items.add(new ItemDef("Worm crunchies", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 912, 1955));
		items.add(new ItemDef("Toad crunchies", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 913, 1956));
		items.add(new ItemDef("Spice crunchies", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 914, 1957));
		items.add(new ItemDef("Crushed Gemstone", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 915, 1958));
		items.add(new ItemDef("Blurberry badge", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 916, 1959));
		items.add(new ItemDef("Gianne badge", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 917, 1960));
		items.add(new ItemDef("tree gnome translation", "Swap this note at any bank for the equivalent item.", "read", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 918, 1961));
		items.add(new ItemDef("War ship", "Swap this note at any bank for the equivalent item.", "play with", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 920, 1962));
		items.add(new ItemDef("Ugthanki Kebab", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 923, 1963));
		items.add(new ItemDef("special curry", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 924, 1964));
		items.add(new ItemDef("Sinister key", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 932, 1965));
		items.add(new ItemDef("Herb", "Swap this note at any bank for the equivalent item.", "Identify", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 933, 1966));
		items.add(new ItemDef("Torstol", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 934, 1967));
		items.add(new ItemDef("Unfinished potion", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 935, 1968));
		items.add(new ItemDef("Jangerberries", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 936, 1969));
		items.add(new ItemDef("fruit blast", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 937, 1970));
		items.add(new ItemDef("blurberry special", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 938, 1971));
		items.add(new ItemDef("wizard blizzard", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 939, 1972));
		items.add(new ItemDef("pineapple punch", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 940, 1973));
		items.add(new ItemDef("SGG", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 941, 1974));
		items.add(new ItemDef("Chocolate saturday", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 942, 1975));
		items.add(new ItemDef("Drunk dragon", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 943, 1976));
		items.add(new ItemDef("cheese and tomato batta", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 944, 1977));
		items.add(new ItemDef("toad batta", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 945, 1978));
		items.add(new ItemDef("gnome batta", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 946, 1979));
		items.add(new ItemDef("worm batta", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 947, 1980));
		items.add(new ItemDef("fruit batta", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 948, 1981));
		items.add(new ItemDef("Veg batta", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 949, 1982));
		items.add(new ItemDef("Chocolate bomb", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 950, 1983));
		items.add(new ItemDef("Vegball", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 951, 1984));
		items.add(new ItemDef("worm hole", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 952, 1985));
		items.add(new ItemDef("Tangled toads legs", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 953, 1986));
		items.add(new ItemDef("Choc crunchies", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 954, 1987));
		items.add(new ItemDef("Worm crunchies", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 955, 1988));
		items.add(new ItemDef("Toad crunchies", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 956, 1989));
		items.add(new ItemDef("Spice crunchies", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 957, 1990));
		items.add(new ItemDef("Potion of Zamorak", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 963, 1991));
		items.add(new ItemDef("Potion of Zamorak", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 964, 1992));
		items.add(new ItemDef("Potion of Zamorak", "Swap this note at any bank for the equivalent item.", "drink", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 965, 1993));
		items.add(new ItemDef("Boots", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 966, 1994));
		items.add(new ItemDef("Boots", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 967, 1995));
		items.add(new ItemDef("Boots", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 968, 1996));
		items.add(new ItemDef("Boots", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 969, 1997));
		items.add(new ItemDef("Boots", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 970, 1998));
		items.add(new ItemDef("Santa's hat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 971, 1999));
		items.add(new ItemDef("Steel Wire", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 975, 2000));
		items.add(new ItemDef("ResetCrystal", "Swap this note at any bank for the equivalent item.", "Activate", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 978, 2001));
		items.add(new ItemDef("Bronze Wire", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 979, 2002));
		items.add(new ItemDef("Present", "Swap this note at any bank for the equivalent item.", "open", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 980, 2003));
		items.add(new ItemDef("Papyrus", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 982, 2004));
		items.add(new ItemDef("A lump of Charcoal", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 983, 2005));
		items.add(new ItemDef("Desert Boots", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 990, 2006));
		items.add(new ItemDef("Full Water Skin", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1016, 2007));
		items.add(new ItemDef("Desert Robe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1019, 2008));
		items.add(new ItemDef("Desert Shirt", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1020, 2009));
		items.add(new ItemDef("Slaves Robe Bottom", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1022, 2010));
		items.add(new ItemDef("Slaves Robe Top", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1023, 2011));
		items.add(new ItemDef("Dwarf cannon base", "Swap this note at any bank for the equivalent item.", "set down", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1032, 2012));
		items.add(new ItemDef("Dwarf cannon stand", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1033, 2013));
		items.add(new ItemDef("Dwarf cannon barrels", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1034, 2014));
		items.add(new ItemDef("Dwarf cannon furnace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1035, 2015));
		items.add(new ItemDef("cannon ammo mould", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1057, 2016));
		items.add(new ItemDef("Iron throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1075, 2017));
		items.add(new ItemDef("Bronze throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1076, 2018));
		items.add(new ItemDef("Steel throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1077, 2019));
		items.add(new ItemDef("Mithril throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1078, 2020));
		items.add(new ItemDef("Adamantite throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1079, 2021));
		items.add(new ItemDef("Rune throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1080, 2022));
		items.add(new ItemDef("Black throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1081, 2023));
		items.add(new ItemDef("Water Skin mostly full", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1082, 2024));
		items.add(new ItemDef("Water Skin mostly empty", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1083, 2025));
		items.add(new ItemDef("Water Skin mouthful left", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1084, 2026));
		items.add(new ItemDef("Empty Water Skin", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1085, 2027));
		items.add(new ItemDef("Iron Spear", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1088, 2028));
		items.add(new ItemDef("Steel Spear", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1089, 2029));
		items.add(new ItemDef("Mithril Spear", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1090, 2030));
		items.add(new ItemDef("Adamantite Spear", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1091, 2031));
		items.add(new ItemDef("Rune Spear", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1092, 2032));
		items.add(new ItemDef("Seasoned Sardine", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1094, 2033));
		items.add(new ItemDef("A free Shantay Disclaimer", "Swap this note at any bank for the equivalent item.", "Read", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1099, 2034));
		items.add(new ItemDef("Doogle leaves", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1100, 2035));
		items.add(new ItemDef("Raw Ugthanki Meat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1101, 2036));
		items.add(new ItemDef("Tasty Ugthanki Kebab", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1102, 2037));
		items.add(new ItemDef("Cooked Ugthanki Meat", "Swap this note at any bank for the equivalent item.", "Eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1103, 2038));
		items.add(new ItemDef("Uncooked Pitta Bread", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1104, 2039));
		items.add(new ItemDef("Pitta Bread", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1105, 2040));
		items.add(new ItemDef("Tomato Mixture", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1106, 2041));
		items.add(new ItemDef("Onion Mixture", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1107, 2042));
		items.add(new ItemDef("Onion and Tomato Mixture", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1108, 2043));
		items.add(new ItemDef("Onion and Tomato and Ugthanki Mix", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1109, 2044));
		items.add(new ItemDef("Burnt Pitta Bread", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1110, 2045));
		items.add(new ItemDef("cat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1119, 2046));
		items.add(new ItemDef("Scrumpled piece of paper", "Swap this note at any bank for the equivalent item.", "Read", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1120, 2047));
		items.add(new ItemDef("Poisoned Bronze throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1128, 2048));
		items.add(new ItemDef("Poisoned Iron throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1129, 2049));
		items.add(new ItemDef("Poisoned Steel throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1130, 2050));
		items.add(new ItemDef("Poisoned Mithril throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1131, 2051));
		items.add(new ItemDef("Poisoned Black throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1132, 2052));
		items.add(new ItemDef("Poisoned Adamantite throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1133, 2053));
		items.add(new ItemDef("Poisoned Rune throwing knife", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1134, 2054));
		items.add(new ItemDef("Poisoned Bronze Spear", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1135, 2055));
		items.add(new ItemDef("Poisoned Iron Spear", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1136, 2056));
		items.add(new ItemDef("Poisoned Steel Spear", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1137, 2057));
		items.add(new ItemDef("Poisoned Mithril Spear", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1138, 2058));
		items.add(new ItemDef("Poisoned Adamantite Spear", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1139, 2059));
		items.add(new ItemDef("Poisoned Rune Spear", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1140, 2060));
		items.add(new ItemDef("Machette", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1172, 2061));
		items.add(new ItemDef("Ground charcoal", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1179, 2062));
		items.add(new ItemDef("Dwarf cannon base", "Swap this note at any bank for the equivalent item.", "set down", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1184, 2063));
		items.add(new ItemDef("Dwarf cannon stand", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1185, 2064));
		items.add(new ItemDef("Dwarf cannon barrels", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1186, 2065));
		items.add(new ItemDef("Dwarf cannon furnace", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1187, 2066));
		items.add(new ItemDef("Raw Manta ray", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1190, 2067));
		items.add(new ItemDef("Manta ray", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1191, 2068));
		items.add(new ItemDef("Raw Sea turtle", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1192, 2069));
		items.add(new ItemDef("Sea turtle", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1193, 2070));
		items.add(new ItemDef("Edible seaweed", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1245, 2071));
		items.add(new ItemDef("Burnt Manta ray", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1247, 2072));
		items.add(new ItemDef("Burnt Sea turtle", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1248, 2073));
		items.add(new ItemDef("Cut reed plant", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1249, 2074));
		items.add(new ItemDef("Iron Pickaxe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 1258, 2075));
		items.add(new ItemDef("Steel Pickaxe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 1259, 2076));
		items.add(new ItemDef("Mithril Pickaxe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 1260, 2077));
		items.add(new ItemDef("Adamantite Pickaxe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 1261, 2078));
		items.add(new ItemDef("Rune Pickaxe", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 1262, 2079));
		items.add(new ItemDef("Sleeping Bag", "Swap this note at any bank for the equivalent item.", "sleep", 0, 438, "items:438", true, false, 0, 0, false, false, -1, 1263, 2080));
		items.add(new ItemDef("Raw Oomlie Meat", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1268, 2081));
		items.add(new ItemDef("Cooked Oomlie meat Parcel", "Swap this note at any bank for the equivalent item.", "eat", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1269, 2082));
		items.add(new ItemDef("Half Dragon Square Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1276, 2083));
		items.add(new ItemDef("Half Dragon Square Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1277, 2084));
		items.add(new ItemDef("Dragon Square Shield", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1278, 2085));
		items.add(new ItemDef("Palm tree leaf", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1279, 2086));
		items.add(new ItemDef("Raw Oomlie Meat Parcel", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1280, 2087));
		items.add(new ItemDef("Burnt Oomlie Meat parcel", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1281, 2088));
		items.add(new ItemDef("Bailing Bucket", "Swap this note at any bank for the equivalent item.", "bail with ", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1282, 2089));
		items.add(new ItemDef("Plank", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1283, 2090));
		items.add(new ItemDef("display tea", "Swap this note at any bank for the equivalent item.", "", 0, 438, "items:438", true, false, 0, 0, true, false, -1, 1285, 2091));


	}

	private static void loadAnimationDefinitions() {
		animations.add(new AnimationDef("head1", 1, 13, true, false, 0));
		animations.add(new AnimationDef("body1", 2, 6, true, false, 0));
		animations.add(new AnimationDef("legs1", 3, 15, true, false, 0));
		animations.add(new AnimationDef("fhead1", 1, 13, true, false, 0));
		animations.add(new AnimationDef("fbody1", 2, 10, true, false, 0));
		animations.add(new AnimationDef("head2", 1, 13, true, false, 0));
		animations.add(new AnimationDef("head3", 1, 13, true, false, 0)); // allow shemales.
		animations.add(new AnimationDef("head4", 1, 13, true, false, 0));
		animations.add(new AnimationDef("chefshat", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("apron", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("apron", 9789488, 0, true, false, 0));
		animations.add(new AnimationDef("boots", 5592405, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("fullhelm", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("platemailtop", 10083839, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", 4210752, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("platemaillegs", 10083839, 0, true, false, 0));
		animations.add(new AnimationDef("leatherarmour", 0, 0, true, false, 0));
		animations.add(new AnimationDef("leathergloves", 0, 0, true, false, 0));
		animations.add(new AnimationDef("sword", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("sword", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("sword", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("sword", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("sword", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("sword", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("sword", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("apron", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("cape", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("cape", 2434341, 0, true, false, 0));
		animations.add(new AnimationDef("cape", 4210926, 0, true, false, 0));
		animations.add(new AnimationDef("cape", 4246592, 0, true, false, 0));
		animations.add(new AnimationDef("cape", 15658560, 0, true, false, 0));
		animations.add(new AnimationDef("cape", 15636736, 0, true, false, 0));
		animations.add(new AnimationDef("cape", 11141341, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", 255, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", 255, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", 4210752, 0, true, false, 0));
		animations.add(new AnimationDef("necklace", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("necklace", 16763980, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 255, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", 4210752, 0, true, false, 0));
		animations.add(new AnimationDef("Wizardsrobe", 10510400, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", 15609904, 0, true, false, 0));
		animations.add(new AnimationDef("Wizardsrobe", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 10510400, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 4210752, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 16036851, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 15609904, 0, true, false, 0));
		animations.add(new AnimationDef("Skirt", 8400921, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 7824998, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 7829367, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 2245205, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 4347170, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 26214, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", 56797, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", 16750896, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", 11363121, 0, true, false, 0));
		animations.add(new AnimationDef("crossbow", 0, 0, false, false, 0));
		animations.add(new AnimationDef("longbow", 0, 0, false, false, 0));
		animations.add(new AnimationDef("battleaxe", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("mace", 16737817, 0, true, false, 0));
		animations.add(new AnimationDef("mace", 15654365, 0, true, false, 0));
		animations.add(new AnimationDef("mace", 15658734, 0, true, false, 0));
		animations.add(new AnimationDef("mace", 10072780, 0, true, false, 0));
		animations.add(new AnimationDef("mace", 11717785, 0, true, false, 0));
		animations.add(new AnimationDef("mace", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("mace", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("staff", 0, 0, true, false, 0));
		animations.add(new AnimationDef("rat", 4805259, 0, true, false, 0));
		animations.add(new AnimationDef("demon", 16384000, 0, true, false, 0));
		animations.add(new AnimationDef("spider", 13408576, 0, true, false, 0));
		animations.add(new AnimationDef("spider", 16728144, 0, true, false, 0));
		animations.add(new AnimationDef("camel", 0, 0, true, false, 0));
		animations.add(new AnimationDef("cow", 0, 0, true, false, 0));
		animations.add(new AnimationDef("sheep", 0, 0, false, false, 0));
		animations.add(new AnimationDef("unicorn", 0, 0, true, false, 0));
		animations.add(new AnimationDef("bear", 0, 0, true, false, 0));
		animations.add(new AnimationDef("chicken", 0, 0, true, false, 0));
		animations.add(new AnimationDef("skeleton", 0, 0, true, false, 0));
		animations.add(new AnimationDef("skelweap", 0, 0, true, true, 0));
		animations.add(new AnimationDef("zombie", 0, 0, true, false, 0));
		animations.add(new AnimationDef("zombweap", 0, 0, true, true, 0));
		animations.add(new AnimationDef("ghost", 0, 0, true, false, 0));
		animations.add(new AnimationDef("bat", 0, 0, true, false, 0));
		animations.add(new AnimationDef("goblin", 8969727, 0, true, false, 0));
		animations.add(new AnimationDef("goblin", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("goblin", 47872, 0, true, false, 0));
		animations.add(new AnimationDef("gobweap", 65535, 0, true, true, 0));
		animations.add(new AnimationDef("scorpion", 0, 0, true, false, 0));
		animations.add(new AnimationDef("dragon", 65280, 0, true, false, 0));
		animations.add(new AnimationDef("dragon", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("dragon", 21981, 0, true, false, 0));
		animations.add(new AnimationDef("Wolf", 0, 0, true, false, 0));
		animations.add(new AnimationDef("Wolf", 10066329, 0, true, false, 0));
		animations.add(new AnimationDef("partyhat", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("partyhat", 16776960, 0, true, false, 0));
		animations.add(new AnimationDef("partyhat", 255, 0, true, false, 0));
		animations.add(new AnimationDef("partyhat", 65280, 0, true, false, 0));
		animations.add(new AnimationDef("partyhat", 16711935, 0, true, false, 0));
		animations.add(new AnimationDef("partyhat", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("leathergloves", 11202303, 0, true, false, 0));
		animations.add(new AnimationDef("chicken", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("fplatemailtop", 10083839, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 1118481, 0, true, false, 0));
		animations.add(new AnimationDef("Wolf", 9789488, 0, true, false, 0));
		animations.add(new AnimationDef("spider", 65535, 0, true, false, 0));
		animations.add(new AnimationDef("battleaxe", 16711748, 0, true, false, 0));
		animations.add(new AnimationDef("sword", 16711748, 0, true, false, 0));
		animations.add(new AnimationDef("eyepatch", 0, 0, true, true, 0));
		animations.add(new AnimationDef("demon", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("dragon", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("spider", 14535680, 0, true, false, 0));
		animations.add(new AnimationDef("Wolf", 2236962, 0, true, false, 0));
		animations.add(new AnimationDef("unicorn", 2236962, 0, true, false, 0));
		animations.add(new AnimationDef("demon", 6291456, 0, true, false, 0));
		animations.add(new AnimationDef("spider", 2236962, 0, true, false, 0));
		animations.add(new AnimationDef("necklace", 3158064, 0, true, false, 0));
		animations.add(new AnimationDef("rat", 11184810, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", 11250603, 0, true, false, 0));
		animations.add(new AnimationDef("chainmail", 11250603, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("legs1", 9785408, 0, true, false, 0));
		animations.add(new AnimationDef("gasmask", 0, 0, true, false, 0));
		animations.add(new AnimationDef("mediumhelm", 16711748, 0, true, false, 0));
		animations.add(new AnimationDef("spider", 3852326, 0, true, false, 0));
		animations.add(new AnimationDef("spear", 0, 0, true, false, 0));
		animations.add(new AnimationDef("halloweenmask", 52224, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", 1052688, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 1052688, 0, true, false, 0));
		animations.add(new AnimationDef("halloweenmask", 16711680, 0, true, false, 0));
		animations.add(new AnimationDef("halloweenmask", 255, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 16755370, 15, true, false, 0));
		animations.add(new AnimationDef("skirt", 11206570, 15, true, false, 0));
		animations.add(new AnimationDef("skirt", 11184895, 15, true, false, 0));
		animations.add(new AnimationDef("skirt", 16777164, 15, true, false, 0));
		animations.add(new AnimationDef("skirt", 13434879, 15, true, false, 0));
		animations.add(new AnimationDef("wizardshat", 16755370, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", 11206570, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", 11184895, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", 16777164, 0, true, false, 0));
		animations.add(new AnimationDef("wizardshat", 13434879, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", 16755370, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", 11206570, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", 11184895, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", 16777164, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", 13434879, 0, true, false, 0));
		animations.add(new AnimationDef("Wizardsrobe", 3978097, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 3978097, 0, true, false, 0));
		animations.add(new AnimationDef("boots", 16755370, 0, true, false, 0));
		animations.add(new AnimationDef("boots", 11206570, 0, true, false, 0));
		animations.add(new AnimationDef("boots", 11184895, 0, true, false, 0));
		animations.add(new AnimationDef("boots", 16777164, 0, true, false, 0));
		animations.add(new AnimationDef("boots", 13434879, 0, true, false, 0));
		animations.add(new AnimationDef("santahat", 0, 0, true, false, 0));
		animations.add(new AnimationDef("ibanstaff", 0, 0, true, false, 0));
		animations.add(new AnimationDef("souless", 0, 0, true, false, 0));
		animations.add(new AnimationDef("boots", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("legs1", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("Wizardsrobe", 8421376, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 8421376, 0, true, false, 0));
		animations.add(new AnimationDef("cape", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("Wolf", 13420580, 0, true, false, 0));
		animations.add(new AnimationDef("bunnyears", 0, 0, true, false, 0));
		animations.add(new AnimationDef("saradominstaff", 0, 0, true, false, 0));
		animations.add(new AnimationDef("spear", 56797, 0, true, false, 0));
		animations.add(new AnimationDef("skirt", 1392384, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", 1392384, 0, true, false, 0));
		animations.add(new AnimationDef("wolf", 5585408, 0, true, false, 0));
		animations.add(new AnimationDef("chicken", 6893315, 0, true, false, 0));
		animations.add(new AnimationDef("squareshield", 13500416, 0, true, false, 0));
		animations.add(new AnimationDef("cape", 16777215, 0, true, false, 0));
		animations.add(new AnimationDef("boots", 1118481, 0, true, false, 0));
		animations.add(new AnimationDef("wizardsrobe", 1118481, 0, true, false, 0));
		animations.add(new AnimationDef("Scythe", 0, 0, true, false, 0));

		/*
		  Add custom animation below.
		 */

		if (Config.S_WANT_CUSTOM_SPRITES) {
			animations.add(new AnimationDef("hatchet", 0, 0, true, false, 0)); // 231 appearanceID.
			animations.add(new AnimationDef("fullhelm", 11189164, 0, true, false, 0));
			animations.add(new AnimationDef("platemailtop", 11189164, 0, true, false, 0));
			animations.add(new AnimationDef("platemaillegs", 11189164, 0, true, false, 0));
			animations.add(new AnimationDef("fullhelm", 16768685, 0, true, false, 0));
			animations.add(new AnimationDef("platemailtop", 16768685, 0, true, false, 0));
			animations.add(new AnimationDef("platemaillegs", 16768685, 0, true, false, 0));
			animations.add(new AnimationDef("fullhelm", 10027084, 0, true, false, 0));
			animations.add(new AnimationDef("platemailtop", 10027084, 0, true, false, 0));
			animations.add(new AnimationDef("platemaillegs", 10027084, 0, true, false, 0));
		}
	}

	@SuppressWarnings("unchecked")
	private static void loadSpellDefinitions() {
		LinkedHashMap<Integer, Integer> runes = new LinkedHashMap<>();
		runes.put(33, 1);
		runes.put(35, 1);
		spells.add(new SpellDef("Wind strike", "A strength 1 missile attack",
				1, 2, 2, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(32, 3);
		runes.put(34, 2);
		runes.put(36, 1);
		spells.add(new SpellDef("Confuse",
				"Reduces your opponents attack by 5%", 3, 2, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(32, 1);
		runes.put(33, 1);
		runes.put(35, 1);
		spells.add(new SpellDef("Water Strike", "A strength 2 missile attack",
				5, 2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(32, 1);
		runes.put(46, 1);
		spells.add(new SpellDef("Enchant lvl-1 amulet",
				"For use on sapphire amulets", 7, 3, 2,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 2);
		runes.put(33, 1);
		runes.put(35, 1);
		spells.add(new SpellDef("Earth Strike", "A strength 3 missile attack",
				9, 2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(32, 3);
		runes.put(34, 2);
		runes.put(36, 1);
		spells.add(new SpellDef("Weaken",
				"Reduces your opponents strength by 5%", 11, 2, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 3);
		runes.put(33, 2);
		runes.put(35, 1);
		spells.add(new SpellDef("Fire Strike", "A strength 4 missile attack",
				13, 2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 2);
		runes.put(32, 2);
		runes.put(40, 1);
		spells.add(new SpellDef("Bones to bananas",
				"Changes all held bones into bananas!", 15, 0, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(33, 2);
		runes.put(41, 1);
		spells.add(new SpellDef("Wind Bolt", "A strength 5 missile attack", 17,
				2, 2, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(32, 2);
		runes.put(34, 3);
		runes.put(36, 1);
		spells.add(new SpellDef("Curse",
				"Reduces your opponents defense by 5%", 19, 2, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 3);
		runes.put(40, 1);
		spells.add(new SpellDef("Low level alchemy",
				"Converts an item into gold", 21, 3, 2,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(32, 2);
		runes.put(33, 2);
		runes.put(41, 1);
		spells.add(new SpellDef("Water bolt", "A strength 6 missle attack", 23,
				2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 1);
		runes.put(33, 3);
		runes.put(42, 1);
		spells.add(new SpellDef("Varrock teleport", "Teleports you to Varrock",
				25, 0, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(33, 3);
		runes.put(46, 1);
		spells.add(new SpellDef("Enchant lvl-2 amulet",
				"For use on emerald amulets", 27, 3, 2,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 3);
		runes.put(33, 2);
		runes.put(41, 1);
		spells.add(new SpellDef("Earth bolt", "A strength 7 missile attack",
				29, 2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 1);
		runes.put(33, 3);
		runes.put(42, 1);
		spells.add(new SpellDef("Lumbridge teleport",
				"Teleports you to Lumbridge", 31, 0, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(33, 1);
		runes.put(42, 1);
		spells.add(new SpellDef("Telekinetic grab",
				"Take an item you can see but can't reach", 33, 3, 2,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 4);
		runes.put(33, 3);
		runes.put(41, 1);
		spells.add(new SpellDef("Fire bolt", "A strength 8 missile attack", 35,
				2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(32, 1);
		runes.put(33, 3);
		runes.put(42, 1);
		spells.add(new SpellDef("Falador teleport", "Teleports you to Falador",
				37, 0, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 2);
		runes.put(33, 2);
		runes.put(41, 1);
		spells.add(new SpellDef("Crumble undead",
				"Hits skeleton, ghosts & zombies hard!", 39, 2, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(33, 3);
		runes.put(38, 1);
		spells.add(new SpellDef("Wind blast", "A strength 9 missile attack",
				41, 2, 2, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 4);
		runes.put(40, 1);
		spells.add(new SpellDef("Superheat item",
				"Smelt 1 ore without a furnace", 43, 3, 2,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(33, 5);
		runes.put(42, 1);
		spells.add(new SpellDef("Camelot teleport", "Teleports you to Camelot",
				45, 0, 2, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(32, 3);
		runes.put(33, 3);
		runes.put(38, 1);
		spells.add(new SpellDef("Water blast", "A strength 10 missile attack",
				47, 2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 5);
		runes.put(46, 1);
		spells.add(new SpellDef("Enchant lvl-3 amulet",
				"For use on ruby amulets", 49, 3, 2,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 5);
		runes.put(38, 1);
		spells.add(new SpellDef("Iban blast", "A strength 25 missile attack!",
				50, 2, 2, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(32, 2);
		runes.put(42, 2);
		spells.add(new SpellDef("Ardougne teleport",
				"Teleports you to Ardougne", 51, 0, 2,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 4);
		runes.put(33, 3);
		runes.put(38, 1);
		spells.add(new SpellDef("Earth blast", "A strength 11 missile attack",
				53, 2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 5);
		runes.put(40, 1);
		spells.add(new SpellDef("High level alchemy",
				"Convert an item into more gold", 55, 3, 2,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(32, 30);
		runes.put(46, 3);
		runes.put(611, 1);
		spells.add(new SpellDef("Charge Water Orb",
				"Needs to be cast on a water obelisk", 56, 5, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 10);
		runes.put(46, 1);
		spells.add(new SpellDef("Enchant lvl-4 amulet",
				"For use on diamond amulets", 57, 3, 2,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 2);
		runes.put(42, 2);
		spells.add(new SpellDef("Watchtower teleport",
				"Teleports you to the watchtower", 58, 0, 2,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 5);
		runes.put(33, 4);
		runes.put(38, 1);
		spells.add(new SpellDef("Fire blast", "A strength 12 missile attack",
				59, 2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 1);
		runes.put(33, 4);
		runes.put(619, 2);
		spells.add(new SpellDef("Claws of Guthix",
				"Summons the power of Guthix", 60, 2, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 2);
		runes.put(33, 4);
		runes.put(619, 2);
		spells.add(new SpellDef("Saradomin strike",
				"Summons the power of Saradomin", 60, 2, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 4);
		runes.put(33, 1);
		runes.put(619, 2);
		spells.add(new SpellDef("Flames of Zamorak",
				"Summons the power of Zamorak", 60, 2, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 30);
		runes.put(46, 3);
		runes.put(611, 1);
		spells.add(new SpellDef("Charge earth Orb",
				"Needs to be cast on an earth obelisk", 60, 5, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(33, 5);
		runes.put(619, 1);
		spells.add(new SpellDef("Wind wave", "A strength 13 missile attack",
				62, 2, 2, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 30);
		runes.put(46, 3);
		runes.put(611, 1);
		spells.add(new SpellDef("Charge Fire Orb",
				"Needs to be cast on a fire obelisk", 63, 5, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(32, 7);
		runes.put(33, 5);
		runes.put(619, 1);
		spells.add(new SpellDef("Water wave", "A strength 14 missile attack",
				65, 2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(33, 30);
		runes.put(46, 3);
		runes.put(611, 1);
		spells.add(new SpellDef("Charge air Orb",
				"Needs to be cast on an air obelisk", 66, 5, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 5);
		runes.put(32, 5);
		runes.put(825, 1);
		spells.add(new SpellDef("Vulnerability",
				"Reduces your opponents defense by 10%", 66, 2, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(32, 15);
		runes.put(34, 15);
		runes.put(46, 1);
		spells.add(new SpellDef("Enchant lvl-5 amulet",
				"For use on dragonstone amulets", 68, 3, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 7);
		runes.put(33, 5);
		runes.put(619, 1);
		spells.add(new SpellDef("Earth wave", "A strength 15 missile attack",
				70, 2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 8);
		runes.put(32, 8);
		runes.put(825, 1);
		spells.add(new SpellDef("Enfeeble",
				"Reduces your opponents strength by 10%", 73, 2, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 7);
		runes.put(33, 5);
		runes.put(619, 1);
		spells.add(new SpellDef("Fire wave", "A strength 16 missile attack", 75,
				2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(34, 12);
		runes.put(32, 12);
		runes.put(825, 1);
		spells.add(new SpellDef("Stun", "Reduces your opponents attack by 10%",
				80, 2, 3, (LinkedHashMap<Integer, Integer>) runes.clone()));
		runes.clear();
		runes.put(31, 3);
		runes.put(33, 3);
		runes.put(619, 3);
		spells.add(new SpellDef("Charge",
				"Increase your mage arena spells damage", 80, 0, 3,
				(LinkedHashMap<Integer, Integer>) runes.clone()));
		runes = null;
	}

	private static void loadDoorDefinitions() {
		int i = 0;
		doors.add(new DoorDef("Wall", "", "WalkTo", "Examine", 1, 0, 192, 2, 2,
				i++));
		doors.add(new DoorDef("Doorframe", "", "WalkTo", "Close", 0, 1, 192, 4,
				4, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Window", "", "WalkTo", "Examine", 1, 0, 192, 5,
				5, i++));
		doors.add(new DoorDef("Fence", "", "WalkTo", "Examine", 1, 0, 192, 10,
				10, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "Examine", 1, 0, 192,
				12, 12, i++));
		doors.add(new DoorDef("Stained glass window", "", "WalkTo", "Examine",
				1, 0, 192, 18, 18, i++));
		doors.add(new DoorDef("Highwall", "", "WalkTo", "Examine", 1, 0, 275,
				2, 2, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 275, 0, 0, i++));
		doors.add(new DoorDef("Doorframe", "", "WalkTo", "Close", 0, 1, 275, 4,
				4, i++));
		doors.add(new DoorDef("battlement", "", "WalkTo", "Examine", 1, 0, 70,
				2, 2, i++));
		doors.add(new DoorDef("Doorframe", "", "WalkTo", "Examine", 1, 0, 192,
				4, 4, i++));
		doors.add(new DoorDef("snowwall", "", "WalkTo", "Examine", 1, 0, 192,
				-31711, -31711, i++));
		doors.add(new DoorDef("arrowslit", "", "WalkTo", "Examine", 1, 0, 192,
				7, 7, i++));
		doors.add(new DoorDef("timberwall", "", "WalkTo", "Examine", 1, 0, 192,
				21, 21, i++));
		doors.add(new DoorDef("timberwindow", "", "WalkTo", "Examine", 1, 0,
				192, 22, 22, i++));
		doors.add(new DoorDef("blank", "", "WalkTo", "Examine", 0, 0, 192,
				12345678, 12345678, i++));
		doors.add(new DoorDef("highblank", "", "WalkTo", "Examine", 0, 0, 275,
				12345678, 12345678, i++));
		doors.add(new DoorDef("mossybricks", "", "WalkTo", "Examine", 1, 0,
				192, 23, 23, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Odd looking wall",
				"This wall doesn't look quite right", "Push", "Examine", 1, 1,
				192, 2, 2, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("web", "A spider's web", "WalkTo", "Examine", 1,
				1, 192, 26, 26, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Window", "", "WalkTo", "Examine", 1, 0, 192, 27,
				27, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Crumbled", "", "WalkTo", "Examine", 1, 0, 192,
				28, 28, i++));
		doors.add(new DoorDef("Cavern", "", "WalkTo", "Examine", 1, 0, 192, 29,
				29, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("cavern2", "", "WalkTo", "Examine", 1, 0, 192,
				30, 30, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Wall", "", "WalkTo", "Examine", 1, 0, 192, 3, 3,
				i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Strange looking wall",
				"This wall doesn't look quite right", "Push", "Examine", 1, 1,
				192, 29, 29, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("memberrailings", "", "WalkTo", "Examine", 1, 0,
				192, 12, 12, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Magic Door", "The door is shut", "Open",
				"Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Strange Panel",
				"This wall doesn't look quite right", "Push", "Examine", 1, 1,
				192, 21, 21, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("blockblank", "", "WalkTo", "Examine", 1, 0, 192,
				12345678, 12345678, i++));
		doors.add(new DoorDef("unusual looking wall",
				"This wall doesn't look quite right", "Push", "Examine", 1, 1,
				192, 2, 2, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick lock",
				1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick lock",
				1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick lock",
				1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick lock",
				1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick lock",
				1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick Lock",
				1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick lock",
				1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Fence with loose pannels",
				"I wonder if I could get through this", "push", "Examine", 1,
				1, 192, 10, 10, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("rat cage", "The rat's have damaged the bars",
				"search", "Examine", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("arrowslit", "", "WalkTo", "Examine", 1, 0, 192,
				44, 44, i++));
		doors.add(new DoorDef("solidblank", "", "WalkTo", "Examine", 1, 0, 192,
				12345678, 12345678, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("loose panel", "The panel has worn with age",
				"break", "Examine", 1, 1, 192, 3, 3, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("plankswindow", "", "WalkTo", "Examine", 1, 0,
				192, 45, 45, i++));
		doors.add(new DoorDef("Low Fence", "", "WalkTo", "Examine", 1, 0, 96,
				10, 10, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Cooking pot", "Smells good!", "WalkTo",
				"Examine", 1, 1, 96, 10, 10, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("plankstimber", "", "WalkTo", "Examine", 1, 0,
				192, 46, 46, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("magic portal", "", "enter", "Examine", 1, 1,
				192, 17, 17, i++));
		doors.add(new DoorDef("magic portal", "", "enter", "Examine", 1, 1,
				192, 17, 17, i++));
		doors.add(new DoorDef("magic portal", "", "enter", "Examine", 1, 1,
				192, 17, 17, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Cavern wall",
				"It looks as if it is covered in some fungus.", "WalkTo",
				"search", 1, 1, 192, 29, 29, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "the door is shut", "walk through",
				"Examine", 1, 1, 192, 3, 3, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through",
				"Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through",
				"Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through",
				"Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through",
				"Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through",
				"Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through",
				"Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through",
				"Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick Lock",
				1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Low wall", "a low wall", "jump", "Examine", 1,
				1, 70, 2, 2, i++));
		doors.add(new DoorDef("Low wall", "a low wall", "jump", "Examine", 1,
				1, 70, 2, 2, i++));
		doors.add(new DoorDef("Blacksmiths Door", "The door is shut", "Open",
				"Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("railings", "", "pick lock", "Examine", 1, 1,
				192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "pick lock", "Examine", 1, 1,
				192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "pick lock", "search", 1, 1, 192,
				12, 12, i++));
		doors.add(new DoorDef("railings", "", "pick lock", "search", 1, 1, 192,
				12, 12, i++));
		doors.add(new DoorDef("railings", "", "pick lock", "search", 1, 1, 192,
				12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192,
				12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "look through", 1, 1,
				192, 12, 12, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "knock on",
				1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Doorframe", "", "WalkTo", "Close", 1, 1, 192, 4,
				4, i++));
		doors.add(new DoorDef("Tent", "", "WalkTo", "Examine", 1, 0, 192, 36,
				36, i++));
		doors.add(new DoorDef("Jail Door", "The door is shut", "Open",
				"Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Jail Door", "The door is shut", "Open",
				"Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Window", "A barred window", "WalkTo", "Search",
				1, 1, 192, 27, 27, i++));
		doors.add(new DoorDef("magic portal",
				"A magical barrier shimmers with power", "WalkTo", "Examine",
				1, 1, 192, 17, 17, i++));
		doors.add(new DoorDef("Jail Door", "A solid iron gate", "Open",
				"Examine", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192,
				12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192,
				12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192,
				12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192,
				12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192,
				12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192,
				12, 12, i++));
		doors.add(new DoorDef("Cave exit", "The way out", "Leave", "Examine",
				0, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("Cave exit", "The way out", "Leave", "Examine",
				0, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("Cave exit", "The way out", "Leave", "Examine",
				0, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("Cave exit", "The way out", "Leave", "Examine",
				0, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("Cave exit", "The way out", "Leave", "Examine",
				0, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("Cave exit", "The way out", "Leave", "Examine",
				0, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192,
				12, 12, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("battlement", "This is blocking your path",
				"Climb-over", "Examine", 1, 1, 70, 2, 2, i++));
		doors.add(new DoorDef("Tent Door", "An entrance into the tent",
				"Go through", "Examine", 1, 1, 192, 50, 50, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
		doors.add(new DoorDef("Tent Door", "An entrance into the tent",
				"Go through", "Examine", 1, 1, 192, 50, 50, i++));
		doors.add(new DoorDef("Low Fence", "A damaged wooden fence", "search",
				"Examine", 1, 1, 96, 10, 10, i++));
		doors.add(new DoorDef("Sturdy Iron Gate", "A solid iron gate", "Open",
				"Examine", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("battlement", "this low wall blocks your path",
				"climb over", "Examine", 1, 1, 70, 2, 2, i++));
		doors.add(new DoorDef("Water", "My waterfall boundary!", "WalkTo",
				"Examine", 1, 0, 192, 25, 25, i++));
		doors.add(new DoorDef("Wheat", "Test Boundary!", "WalkTo", "Examine",
				1, 0, 192, 24, 24, i++));
		doors.add(new DoorDef("Jungle", "Thick inpenetrable jungle", "Chop",
				"Examine", 1, 1, 192, 8, 8, i++));
		doors.add(new DoorDef("Window",
				"you can see a vicious looking guard dog right outside",
				"Investigate", "Examine", 1, 1, 192, 5, 5, i++));
		doors.add(new DoorDef("Rut",
				"Looks like a small rut carved into the ground.", "WalkTo",
				"Search", 1, 0, 96, 51, 51, i++));
		doors.add(new DoorDef("Crumbled Cavern 1", "", "WalkTo", "Examine", 0,
				0, 192, 52, 52, i++));
		doors.add(new DoorDef("Crumbled Cavern 2", "", "WalkTo", "Examine", 0,
				0, 192, 53, 53, i++));
		doors.add(new DoorDef("cavernhole", "", "WalkTo", "Examine", 1, 0, 192,
				54, 54, i++));
		doors.add(new DoorDef("flamewall",
				"A supernatural fire of incredible intensity", "Touch",
				"Investigate", 1, 1, 192, 54, 54, i++));
		doors.add(new DoorDef("Ruined wall",
				"Some ancient wall structure - it doesn't look too high.",
				"WalkTo", "Jump", 1, 1, 192, 28, 28, i++));
		doors.add(new DoorDef(
				"Ancient Wall",
				"An ancient - slightly higher wall with some strange markings on it",
				"Use", "Search", 1, 1, 275, 2, 2, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1,
				1, 192, 0, 0, i++));
	}

	private static void loadGameObjectDefinitionsA() { // GOOD
		int i = 0;
		objects.add(new GameObjectDef("Tree", "A pointy tree", "Chop", "Examine", 1, 1, 1, 0, "tree2", i++));
		objects.add(new GameObjectDef("Tree", "A leafy tree", "Chop", "Examine", 1, 1, 1, 0, "tree", i++));
		objects.add(new GameObjectDef("Well", "The bucket is missing", "WalkTo", "Examine", 1, 2, 2, 0, "well", i++));
		objects.add(new GameObjectDef("Table", "A mighty fine table", "WalkTo", "Examine", 1, 1, 1, 96, "table", i++));
		objects.add(new GameObjectDef("Treestump", "Someone has chopped this tree down!", "WalkTo", "Examine", 1, 1, 1, 0, "treestump", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading downwards", "Climb-Down", "Examine", 1, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("Chair", "A sturdy looking chair", "WalkTo", "Examine", 1, 1, 1, 0, "chair", i++));
		objects.add(new GameObjectDef("logs", "A pile of logs", "WalkTo", "Examine", 1, 1, 1, 0, "logpile", i++));
		objects.add(new GameObjectDef("Longtable", "It has nice candles", "WalkTo", "Examine", 1, 4, 1, 0, "longtable", i++));
		objects.add(new GameObjectDef("Throne", "It looks fancy and expensive", "WalkTo", "Examine", 1, 1, 1, 0, "throne", i++));
		objects.add(new GameObjectDef("Range", "A hot well stoked range", "WalkTo", "Examine", 1, 1, 2, 0, "range", i++));
		objects.add(new GameObjectDef("Gravestone", "R I P", "WalkTo", "Examine", 1, 1, 1, 0, "gravestone1", i++));
		objects.add(new GameObjectDef("Gravestone", "Its covered in moss", "WalkTo", "Examine", 1, 1, 1, 0, "gravestone2", i++));
		objects.add(new GameObjectDef("Bed", "Ooh nice blankets", "rest", "Examine", 1, 2, 3, 0, "Bigbed", i++));
		objects.add(new GameObjectDef("Bed", "Its a bed - wow", "rest", "Examine", 1, 2, 2, 0, "bed", i++));
		objects.add(new GameObjectDef("bar", "Mmm beer", "WalkTo", "Examine", 1, 1, 1, 0, "barpumps", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Altar", "Its an Altar", "Recharge at", "Examine", 1, 2, 1, 0, "altar", i++));
		objects.add(new GameObjectDef("Post", "What am I examining posts for", "WalkTo", "Examine", 1, 1, 1, 0, "wallpost", i++));
		objects.add(new GameObjectDef("Support", "A wooden pole", "WalkTo", "Examine", 0, 1, 1, 0, "supportnw", i++));
		objects.add(new GameObjectDef("barrel", "Its empty", "WalkTo", "Examine", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("Bench", "It doesn't look very comfy", "WalkTo", "Examine", 1, 2, 1, 0, "bench", i++));
		objects.add(new GameObjectDef("Portrait", "A painting of our beloved king", "WalkTo", "Examine", 0, 1, 1, 0, "portrait", i++));
		objects.add(new GameObjectDef("candles", "Candles on a fancy candlestick", "WalkTo", "Examine", 1, 1, 1, 0, "candles", i++));
		objects.add(new GameObjectDef("fountain", "The water looks fairly clean", "WalkTo", "Examine", 1, 2, 2, 0, "fountain", i++));
		objects.add(new GameObjectDef("landscape", "An oil painting", "WalkTo", "Examine", 0, 1, 1, 0, "landscape", i++));
		objects.add(new GameObjectDef("Millstones", "You can use these to make flour", "WalkTo", "Examine", 1, 3, 3, 0, "mill", i++));
		objects.add(new GameObjectDef("Counter", "It's the shop counter", "WalkTo", "Examine", 1, 2, 1, 120, "counter", i++));
		objects.add(new GameObjectDef("Stall", "A market stall", "WalkTo", "Examine", 1, 2, 2, 112, "market", i++));
		objects.add(new GameObjectDef("Target", "Coming soon archery practice", "Practice", "Examine", 1, 1, 1, 0, "target", i++));
		objects.add(new GameObjectDef("PalmTree", "A nice palm tree", "WalkTo", "Examine", 1, 1, 1, 0, "palm2", i++));
		objects.add(new GameObjectDef("PalmTree", "A shady palm tree", "WalkTo", "Examine", 1, 1, 1, 0, "palm", i++));
		objects.add(new GameObjectDef("Fern", "A leafy plant", "WalkTo", "Examine", 0, 1, 1, 0, "fern", i++));
		objects.add(new GameObjectDef("Cactus", "It looks very spikey", "WalkTo", "Examine", 1, 1, 1, 0, "cactus", i++));
		objects.add(new GameObjectDef("Bullrushes", "I wonder why it's called a bullrush", "WalkTo", "Examine", 0, 1, 1, 0, "bullrushes", i++));
		objects.add(new GameObjectDef("Flower", "Ooh thats pretty", "WalkTo", "Examine", 0, 1, 1, 0, "flower", i++));
		objects.add(new GameObjectDef("Mushroom", "I think it's a poisonous one", "WalkTo", "Examine", 0, 1, 1, 0, "mushroom", i++));
		objects.add(new GameObjectDef("Coffin", "This coffin is closed", "Open", "Examine", 1, 2, 2, 0, "coffin", i++));
		objects.add(new GameObjectDef("Coffin", "This coffin is open", "Search", "Close", 1, 2, 2, 0, "coffin2", i++));
		objects.add(new GameObjectDef("stairs", "These lead upstairs", "Go up", "Examine", 1, 2, 3, 0, "woodenstairs", i++));
		objects.add(new GameObjectDef("stairs", "These lead downstairs", "Go down", "Examine", 1, 2, 3, 0, "woodenstairsdown", i++));
		objects.add(new GameObjectDef("stairs", "These lead upstairs", "Go up", "Examine", 1, 2, 3, 0, "stonestairs", i++));
		objects.add(new GameObjectDef("stairs", "These lead downstairs", "Go down", "Examine", 1, 2, 3, 0, "stonestairsdown", i++));
		objects.add(new GameObjectDef("railing", "nice safety measure", "WalkTo", "Examine", 1, 1, 1, 0, "woodenrailing", i++));
		objects.add(new GameObjectDef("pillar", "An ornate pillar", "WalkTo", "Examine", 1, 1, 1, 0, "marblepillar", i++));
		objects.add(new GameObjectDef("Bookcase", "A large collection of books", "WalkTo", "Examine", 1, 1, 2, 0, "bookcase", i++));
		objects.add(new GameObjectDef("Sink", "Its fairly dirty", "WalkTo", "Examine", 1, 1, 2, 0, "sink", i++));
		objects.add(new GameObjectDef("Dummy", "I can practice my fighting here", "hit", "Examine", 1, 1, 1, 0, "sworddummy", i++));
		objects.add(new GameObjectDef("anvil", "heavy metal", "WalkTo", "Examine", 1, 1, 1, 0, "anvil", i++));
		objects.add(new GameObjectDef("Torch", "It would be very dark without this", "WalkTo", "Examine", 0, 1, 1, 0, "torcha1", i++));
		objects.add(new GameObjectDef("hopper", "You put grain in here", "operate", "Examine", 1, 2, 2, 0, "milltop", i++));
		objects.add(new GameObjectDef("chute", "Flour comes out here", "WalkTo", "Examine", 1, 2, 2, 40, "millbase", i++));
		objects.add(new GameObjectDef("cart", "A farm cart", "WalkTo", "Examine", 1, 2, 3, 0, "cart", i++));
		objects.add(new GameObjectDef("sacks", "Yep they're sacks", "WalkTo", "Examine", 1, 1, 1, 0, "sacks", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("Gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("gate", "The gate is open", "WalkTo", "close", 3, 1, 2, 0, "metalgateopen", i++));
		objects.add(new GameObjectDef("gate", "The gate is open", "WalkTo", "close", 3, 1, 2, 0, "woodengateopen", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "woodengateclosed", i++));
		objects.add(new GameObjectDef("signpost", "To Varrock", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("signpost", "To the tower of wizards", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("doors", "The doors are open", "WalkTo", "Close", 3, 1, 2, 0, "doubledoorsopen", i++));
		objects.add(new GameObjectDef("doors", "The doors are shut", "Open", "Examine", 2, 1, 2, 0, "doubledoorsclosed", i++));
		objects.add(new GameObjectDef("signpost", "To player owned houses", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("signpost", "To Lumbridge Castle", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("bookcase", "It's a bookcase", "WalkTo", "Search", 1, 1, 2, 0, "bookcase", i++));
		objects.add(new GameObjectDef("henge", "these look impressive", "WalkTo", "Examine", 1, 2, 2, 0, "henge", i++));
		objects.add(new GameObjectDef("Dolmen", "A sort of ancient altar thingy", "WalkTo", "Examine", 1, 2, 2, 0, "dolmen", i++));
		objects.add(new GameObjectDef("Tree", "This tree doesn't look too healthy", "WalkTo", "Chop", 1, 1, 1, 0, "deadtree1", i++));
		objects.add(new GameObjectDef("cupboard", "Perhaps I should search it", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("Wheat", "nice ripe looking wheat", "WalkTo", "pick", 0, 1, 1, 0, "wheat", i++));
		objects.add(new GameObjectDef("sign", "The blue moon inn", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("sails", "The windmill's sails", "WalkTo", "Examine", 0, 1, 3, 0, "windmillsail", i++));
		objects.add(new GameObjectDef("sign", "estate agent", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("sign", "The Jolly boar inn", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("Drain", "This drainpipe runs from the kitchen to the sewers", "WalkTo", "Search", 0, 1, 1, 0, "pipe&drain", i++));
		objects.add(new GameObjectDef("manhole", "A manhole cover", "open", "Examine", 0, 1, 1, 0, "manholeclosed", i++));
		objects.add(new GameObjectDef("manhole", "How dangerous - this manhole has been left open", "climb down", "close", 0, 1, 1, 0, "manholeopen", i++));
		objects.add(new GameObjectDef("pipe", "a dirty sewer pipe", "WalkTo", "Examine", 1, 1, 1, 0, "wallpipe", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("barrel", "It seems to be full of newt's eyes", "WalkTo", "Examine", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("cupboard", "Perhaps I should search it", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("fountain", "I think I see something in the fountain", "WalkTo", "Search", 1, 2, 2, 0, "fountain", i++));
		objects.add(new GameObjectDef("signpost", "To Draynor Manor", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("Tree", "This tree doesn't look too healthy", "Approach", "Search", 1, 1, 1, 0, "deadtree1", i++));
		objects.add(new GameObjectDef("sign", "General Store", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("sign", "Lowe's Archery store", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("sign", "The Clothes Shop", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("sign", "Varrock Swords", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("sign", "Bob's axes", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("sign", "The staff shop", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("fire", "A strongly burning fire", "WalkTo", "Examine", 0, 1, 1, 0, "firea1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks2", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "copperrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "copperrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "ironrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "ironrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "tinrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "tinrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "mithrilrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "mithrilrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "adamiterock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "adamiterock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "coalrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "coalrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "goldrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "goldrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "clayrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "clayrock1", i++));
		objects.add(new GameObjectDef("web", "A spider's web", "WalkTo", "Examine", 0, 1, 1, 0, "ceilingweb", i++));
		objects.add(new GameObjectDef("web", "A spider's web", "WalkTo", "Examine", 0, 1, 1, 0, "floorweb", i++));
		objects.add(new GameObjectDef("furnace", "A red hot furnace", "WalkTo", "Examine", 1, 2, 2, 0, "furnace", i++));
		objects.add(new GameObjectDef("Cook's Range", "A hot well stoked range", "WalkTo", "Examine", 1, 1, 2, 0, "range", i++));
		objects.add(new GameObjectDef("Machine", "I wonder what it's supposed to do", "WalkTo", "Examine", 1, 2, 2, 0, "madmachine", i++));
		objects.add(new GameObjectDef("Spinning wheel", "I can spin wool on this", "WalkTo", "Examine", 1, 1, 1, 0, "spinningwheel", i++));
		objects.add(new GameObjectDef("Lever", "The lever is up", "WalkTo", "Examine", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("Lever", "The lever is down", "WalkTo", "Examine", 0, 1, 1, 0, "leverdown", i++));
		objects.add(new GameObjectDef("LeverA", "It's a lever", "Pull", "Inspect", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("LeverB", "It's a lever", "Pull", "Inspect", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("LeverC", "It's a lever", "Pull", "Inspect", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("LeverD", "It's a lever", "Pull", "Inspect", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("LeverE", "It's a lever", "Pull", "Inspect", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("LeverF", "It's a lever", "Pull", "Inspect", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("signpost", "To the forge", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("signpost", "To the Barbarian's  Village", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("signpost", "To Al Kharid", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("Compost Heap", "A smelly pile of compost", "WalkTo", "Search", 1, 2, 2, 0, "compost", i++));
		objects.add(new GameObjectDef("Coffin", "This coffin is closed", "Open", "Examine", 1, 2, 2, 0, "coffin", i++));
		objects.add(new GameObjectDef("Coffin", "This coffin is open", "Search", "Close", 1, 2, 2, 0, "coffin2", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("sign", "The Bank of runescape", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("cupboard", "Perhaps I should search it", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("doors", "The doors are shut", "Open", "Examine", 2, 1, 2, 0, "doubledoorsclosed", i++));
		objects.add(new GameObjectDef("torch", "A scary torch", "WalkTo", "Examine", 0, 1, 1, 0, "skulltorcha1", i++));
		objects.add(new GameObjectDef("Altar", "An altar to the evil God Zamorak", "Recharge at", "Examine", 1, 2, 1, 0, "chaosaltar", i++));
		objects.add(new GameObjectDef("Shield", "A display shield", "WalkTo", "Examine", 0, 1, 1, 0, "wallshield", i++));
		objects.add(new GameObjectDef("Grill", "some sort of ventilation", "WalkTo", "Examine", 0, 1, 1, 0, "wallgrill", i++));
		objects.add(new GameObjectDef("Cauldron", "A very large pot", "WalkTo", "drink from", 1, 1, 1, 0, "cauldron", i++));
		objects.add(new GameObjectDef("Grill", "some sort of ventilation", "Listen", "Examine", 0, 1, 1, 0, "wallgrill", i++));
		objects.add(new GameObjectDef("Mine Cart", "It's empty", "WalkTo", "Examine", 1, 1, 1, 0, "minecart", i++));
		objects.add(new GameObjectDef("Buffers", "Stop the carts falling off the end", "WalkTo", "Examine", 1, 1, 1, 0, "trackbuffer", i++));
		objects.add(new GameObjectDef("Track", "Train track", "WalkTo", "Examine", 0, 2, 2, 0, "trackcurve", i++));
		objects.add(new GameObjectDef("Track", "Train track", "WalkTo", "Examine", 0, 2, 2, 0, "trackpoints", i++));
		objects.add(new GameObjectDef("Track", "Train track", "WalkTo", "Examine", 0, 1, 1, 0, "trackstraight", i++));
		objects.add(new GameObjectDef("Hole", "I can see a witches cauldron directly below it", "WalkTo", "Examine", 1, 1, 1, 0, "hole", i++));
		objects.add(new GameObjectDef("ship", "A ship to Karamja", "board", "Examine", 0, 5, 3, 0, "Shipfront", i++));
		objects.add(new GameObjectDef("ship", "A ship to Karamja", "board", "Examine", 0, 2, 3, 0, "Shipmiddle", i++));
		objects.add(new GameObjectDef("ship", "A ship to Karamja", "board", "Examine", 0, 5, 3, 0, "Shipback", i++));
		objects.add(new GameObjectDef("Emergency escape ladder", "it's a ladder leading downwards", "Climb-Down", "Examine", 1, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("sign", "Wydin's grocery", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("sign", "The Rusty Anchor", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("ship", "A ship to Port Sarim", "board", "Examine", 0, 5, 3, 0, "Shipfront", i++));
		objects.add(new GameObjectDef("ship", "A ship to Port Sarim", "board", "Examine", 0, 2, 3, 0, "Shipmiddle", i++));
		objects.add(new GameObjectDef("ship", "A ship to Port Sarim", "board", "Examine", 0, 5, 3, 0, "Shipback", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("hopper", "You put grain in here", "operate", "Examine", 1, 2, 2, 0, "milltop", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("cupboard", "Perhaps I should search it", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "bluriterock1", i++));
		objects.add(new GameObjectDef("Doric's anvil", "Property of Doric the dwarf", "WalkTo", "Examine", 1, 1, 1, 0, "anvil", i++));
		objects.add(new GameObjectDef("pottery oven", "I can fire clay pots in this", "WalkTo", "Examine", 1, 2, 2, 0, "potteryoven", i++));
		objects.add(new GameObjectDef("potter's wheel", "I can make clay pots using this", "WalkTo", "Examine", 1, 1, 1, 0, "potterywheel", i++));
		objects.add(new GameObjectDef("gate", "A gate from Lumbridge to Al Kharid", "Open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("gate", "This gate is open", "WalkTo", "Examine", 2, 1, 2, 0, "metalgateopen", i++));
		objects.add(new GameObjectDef("crate", "A crate used for storing bananas", "WalkTo", "Search", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("Banana tree", "A tree with nice ripe bananas growing on it", "WalkTo", "Pick Banana", 1, 1, 1, 0, "palm2", i++));
		objects.add(new GameObjectDef("Banana tree", "There are no bananas left on the tree", "WalkTo", "Pick Banana", 1, 1, 1, 0, "palm2", i++));
		objects.add(new GameObjectDef("crate", "A crate used for storing bananas", "WalkTo", "Search", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("Chest", "A battered old chest", "WalkTo", "Examine", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Flower", "Ooh thats pretty", "WalkTo", "Examine", 0, 1, 1, 0, "flower", i++));
		objects.add(new GameObjectDef("sign", "Fishing Supplies", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("sign", "Jewellers", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("Potato", "A potato plant", "WalkTo", "pick", 0, 1, 1, 0, "potato", i++));
		objects.add(new GameObjectDef("fish", "I can see fish swimming in the water", "Lure", "Bait", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("fish", "I can see fish swimming in the water", "Net", "Bait", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("fish", "I can see fish swimming in the water", "Harpoon", "Cage", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "silverrock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "silverrock1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading downwards", "Climb-Down", "Examine", 1, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("Monks Altar", "Its an Altar", "Recharge at", "Examine", 1, 2, 1, 0, "altar", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading downwards", "Climb-Down", "Examine", 1, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("Coffin", "This coffin is closed", "Open", "Examine", 1, 2, 2, 0, "coffin", i++));
		objects.add(new GameObjectDef("Coffin", "This coffin is open", "Search", "Close", 1, 2, 2, 0, "coffin2", i++));
		objects.add(new GameObjectDef("Smashed table", "This table has seen better days", "WalkTo", "Examine", 1, 1, 1, 0, "smashedtable", i++));
		objects.add(new GameObjectDef("Fungus", "A creepy looking fungus", "WalkTo", "Examine", 0, 1, 1, 0, "nastyfungus", i++));
		objects.add(new GameObjectDef("Smashed chair", "This chair is broken", "WalkTo", "Examine", 1, 1, 1, 0, "smashedchair", i++));
		objects.add(new GameObjectDef("Broken pillar", "The remains of a pillar", "WalkTo", "Examine", 1, 1, 1, 0, "brokenpillar", i++));
		objects.add(new GameObjectDef("Fallen tree", "A fallen tree", "WalkTo", "Examine", 1, 3, 2, 0, "fallentree", i++));
		objects.add(new GameObjectDef("Danger Sign", "Danger!", "WalkTo", "Examine", 1, 1, 1, 0, "dangersign", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "runiterock1", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "runiteruck1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Gravestone", "A big impressive gravestone", "WalkTo", "Examine", 1, 2, 2, 0, "largegrave", i++));
		objects.add(new GameObjectDef("bone", "Eep!", "WalkTo", "Examine", 1, 1, 1, 0, "curvedbone", i++));
		objects.add(new GameObjectDef("bone", "This would feed a dog for a month", "WalkTo", "Examine", 1, 1, 1, 0, "largebone", i++));
		objects.add(new GameObjectDef("carcass", "I think it's dead", "WalkTo", "Examine", 1, 2, 2, 0, "carcass", i++));
		objects.add(new GameObjectDef("animalskull", "I wouldn't like to meet a live one", "WalkTo", "Examine", 1, 1, 1, 0, "animalskull", i++));
		objects.add(new GameObjectDef("Vine", "A creepy creeper", "WalkTo", "Examine", 0, 1, 1, 0, "vine", i++));
		objects.add(new GameObjectDef("Vine", "A creepy creeper", "WalkTo", "Examine", 0, 1, 1, 0, "vinecorner", i++));
		objects.add(new GameObjectDef("Vine", "A creepy creeper", "WalkTo", "Examine", 0, 1, 1, 0, "vinejunction", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "WalkTo", "Examine", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading downwards", "Climb-Down", "Examine", 1, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("ship", "The Lumbridge Lady", "board", "Examine", 0, 5, 3, 0, "Shipfront", i++));
		objects.add(new GameObjectDef("ship", "The Lumbridge Lady", "board", "Examine", 0, 5, 3, 0, "Shipback", i++));
		objects.add(new GameObjectDef("hole", "This ship isn't much use with that there", "WalkTo", "Examine", 2, 1, 1, 0, "brokenwall", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("hole", "This ship isn't much use with that there", "WalkTo", "Examine", 2, 1, 1, 0, "brokenwall", i++));
		objects.add(new GameObjectDef("ship", "The Lumbridge Lady", "board", "Examine", 0, 5, 3, 0, "Shipfront", i++));
		objects.add(new GameObjectDef("ship", "The Lumbridge Lady", "board", "Examine", 0, 5, 3, 0, "Shipback", i++));
		objects.add(new GameObjectDef("Altar of Guthix", "A sort of ancient altar thingy", "Recharge at", "Examine", 1, 2, 2, 0, "dolmen", i++));
		objects.add(new GameObjectDef("The Cauldron of Thunder", "A very large pot", "WalkTo", "Examine", 1, 1, 1, 0, "cauldron", i++));
		objects.add(new GameObjectDef("Tree", "A leafy tree", "Search", "Examine", 1, 1, 1, 0, "tree", i++));
		objects.add(new GameObjectDef("ship", "A ship to Entrana", "board", "Examine", 0, 5, 3, 0, "Shipfront", i++));
		objects.add(new GameObjectDef("ship", "A ship to Entrana", "board", "Examine", 0, 2, 3, 0, "Shipmiddle", i++));
		objects.add(new GameObjectDef("ship", "A ship to Entrana", "board", "Examine", 0, 5, 3, 0, "Shipback", i++));
		objects.add(new GameObjectDef("ship", "A ship to Port Sarim", "board", "Examine", 0, 5, 3, 0, "Shipfront", i++));
		objects.add(new GameObjectDef("ship", "A ship to Port Sarim", "board", "Examine", 0, 2, 3, 0, "Shipmiddle", i++));
		objects.add(new GameObjectDef("ship", "A ship to Port Sarim", "board", "Examine", 0, 5, 3, 0, "Shipback", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading downwards", "Climb-Down", "Examine", 1, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("Dramen Tree", "This tree doesn't look too healthy", "Chop", "Examine", 1, 1, 1, 0, "dramentree", i++));
		objects.add(new GameObjectDef("hopper", "You put grain in here", "operate", "Examine", 1, 2, 2, 0, "milltop", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "WalkTo", "Examine", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("sign", "2-handed swords sold here", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("sign", "ye olde herbalist", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Door mat", "If I ever get my boots muddy I know where to come", "search", "Examine", 0, 1, 1, 0, "Doormat", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Cauldron", "A very large pot", "WalkTo", "Examine", 1, 1, 1, 0, "cauldron", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("cupboard", "Perhaps I should search it", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("gate", "The bank vault gate", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("fish", "I can see fish swimming in the water", "Net", "Harpoon", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("sign", "Harry's fishing shack", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("cupboard", "Perhaps I should search it", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("sign", "The shrimp and parrot", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("signpost", "Palm Street", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("Rockslide", "A pile of rocks blocks your path", "Mine", "Prospect", 1, 1, 1, 0, "rock3", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("fish", "I can see fish swimming in the lava!", "Bait", "Examine", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("barrel", "Its got ale in it", "WalkTo", "Examine", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("table", "It's a sturdy table", "WalkTo", "Examine", 1, 2, 1, 96, "bigtable", i++));
		objects.add(new GameObjectDef("Fireplace", "It would be very cold without this", "WalkTo", "Examine", 1, 2, 1, 0, "fireplacea1", i++));
		objects.add(new GameObjectDef("Egg", "Thats one big egg!", "WalkTo", "Examine", 1, 1, 1, 0, "bigegg", i++));
		objects.add(new GameObjectDef("Eggs", "They'd make an impressive omlette", "WalkTo", "Examine", 1, 1, 1, 0, "eggs", i++));
		objects.add(new GameObjectDef("Stalagmites", "Hmm pointy", "WalkTo", "Examine", 1, 1, 1, 0, "stalagmites", i++));
		objects.add(new GameObjectDef("Stool", "A simple three legged stool", "WalkTo", "Examine", 1, 1, 1, 0, "stool", i++));
		objects.add(new GameObjectDef("Bench", "It doesn't look to comfortable", "WalkTo", "Examine", 1, 1, 1, 0, "wallbench", i++));
		objects.add(new GameObjectDef("table", "A round table ideal for knights", "WalkTo", "Examine", 1, 2, 2, 0, "bigroundtable", i++));
		objects.add(new GameObjectDef("table", "A handy little table", "WalkTo", "Examine", 1, 1, 1, 96, "roundtable", i++));
		objects.add(new GameObjectDef("fountain of heros", "Use a dragonstone gem here to increase it's abilties", "WalkTo", "Examine", 1, 2, 2, 0, "fountain", i++));
		objects.add(new GameObjectDef("bush", "A leafy bush", "WalkTo", "Examine", 1, 1, 1, 0, "bush1", i++));
		objects.add(new GameObjectDef("hedge", "A carefully trimmed hedge", "WalkTo", "Examine", 1, 1, 1, 0, "bush2", i++));
		objects.add(new GameObjectDef("flower", "A nice colourful flower", "WalkTo", "Examine", 1, 1, 1, 0, "blueflower", i++));
		objects.add(new GameObjectDef("plant", "Hmm leafy", "WalkTo", "Examine", 1, 1, 1, 0, "smallfern", i++));
		objects.add(new GameObjectDef("Giant crystal", "How unusual a crystal with a wizard trapped in it", "WalkTo", "Examine", 1, 3, 3, 0, "giantcrystal", i++));
		objects.add(new GameObjectDef("sign", "The dead man's chest", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("sign", "The rising sun", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("crate", "A large wooden storage box", "WalkTo", "Search", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("crate", "A large wooden storage box", "WalkTo", "Search", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("ship", "A merchant ship", "stow away", "Examine", 0, 5, 3, 0, "Shipfront", i++));
		objects.add(new GameObjectDef("ship", "A merchant ship", "stow away", "Examine", 0, 5, 3, 0, "Shipback", i++));
		objects.add(new GameObjectDef("beehive", "It's guarded by angry looking bees", "WalkTo", "Examine", 1, 1, 1, 0, "beehive", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("Altar", "An altar to the evil God Zamorak", "Recharge at", "Search", 1, 2, 1, 0, "chaosaltar", i++));
		objects.add(new GameObjectDef("sign", "Hickton's Archery store", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("signpost", "To Camelot", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("Archway", "A decorative marble arch", "WalkTo", "Examine", 1, 2, 1, 0, "marblearch", i++));
		objects.add(new GameObjectDef("Obelisk of water", "It doesn't look very wet", "WalkTo", "Examine", 1, 1, 1, 0, "obelisk", i++));
		objects.add(new GameObjectDef("Obelisk of fire", "It doesn't look very hot", "WalkTo", "Examine", 1, 1, 1, 0, "obelisk", i++));
		objects.add(new GameObjectDef("sand pit", "I can use a bucket to get sand from here", "WalkTo", "Search", 1, 2, 2, 0, "sandpit", i++));
		objects.add(new GameObjectDef("Obelisk of air", "A tall stone pointy thing", "WalkTo", "Examine", 1, 1, 1, 0, "obelisk", i++));
		objects.add(new GameObjectDef("Obelisk of earth", "A tall stone pointy thing", "WalkTo", "Examine", 1, 1, 1, 0, "obelisk", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Oak Tree", "A grand old oak tree", "Chop", "Examine", 1, 2, 2, 0, "oaktree", i++));
		objects.add(new GameObjectDef("Willow Tree", "A weeping willow", "Chop", "Examine", 1, 2, 2, 0, "willowtree", i++));
		objects.add(new GameObjectDef("Maple Tree", "It's got nice shaped leaves", "Chop", "Examine", 1, 2, 2, 0, "mapletree", i++));
		objects.add(new GameObjectDef("Yew Tree", "A tough looking yew tree", "Chop", "Examine", 1, 2, 2, 0, "yewtree", i++));
		objects.add(new GameObjectDef("Tree", "A magical tree", "Chop", "Examine", 1, 1, 1, 0, "magictree", i++));
		objects.add(new GameObjectDef("gate", "A gate guarded by a fierce barbarian", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("sign", "The forester's arms", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("flax", "A flax plant", Config.S_BATCH_PROGRESSION ? "pick" : "WalkTo", Config.S_BATCH_PROGRESSION ? "Examine" : "pick", 0, 1, 1, 0, "flax", i++));
		objects.add(new GameObjectDef("Large treestump", "Someone has chopped this tree down!", "WalkTo", "Examine", 1, 2, 2, 0, "treestump", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "goldrock1", i++));
		objects.add(new GameObjectDef("Lever", "It's a lever", "Pull", "Inspect", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("Lever", "It's a lever", "Pull", "Inspect", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("Lever", "It's a lever", "Pull", "Inspect", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("ship", "A ship bound for Ardougne", "board", "Examine", 0, 5, 3, 0, "Shipfront", i++));
		objects.add(new GameObjectDef("ship", "A ship bound for Ardougne", "board", "Examine", 0, 5, 3, 0, "Shipback", i++));
		objects.add(new GameObjectDef("Bakers Stall", "A market stall", "WalkTo", "steal from", 1, 2, 2, 112, "market", i++));
		objects.add(new GameObjectDef("Silk Stall", "A market stall", "WalkTo", "steal from", 1, 2, 2, 112, "market", i++));
		objects.add(new GameObjectDef("Fur Stall", "A market stall", "WalkTo", "steal from", 1, 2, 2, 112, "market", i++));
		objects.add(new GameObjectDef("Silver Stall", "A market stall", "WalkTo", "steal from", 1, 2, 2, 112, "market", i++));
		objects.add(new GameObjectDef("Spices Stall", "A market stall", "WalkTo", "steal from", 1, 2, 2, 112, "market", i++));
		objects.add(new GameObjectDef("gems Stall", "A market stall", "WalkTo", "steal from", 1, 2, 2, 112, "market", i++));
		objects.add(new GameObjectDef("crate", "A large heavy sealed crate", "WalkTo", "Search", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("crate", "A large heavy sealed crate", "WalkTo", "Search", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("sign", "RPDT depot", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("stairs", "These lead upstairs", "Go up", "Search for traps", 1, 2, 3, 0, "stonestairs", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Search for traps", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Search for traps", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Search for traps", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Search for traps", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Search for traps", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "someone is stealing something from it", "WalkTo", "Examine", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Search for traps", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("empty stall", "A market stall", "WalkTo", "Examine", 1, 2, 2, 112, "market", i++));
		objects.add(new GameObjectDef("stairs", "These lead upstairs", "Go up", "Examine", 1, 2, 3, 0, "stonestairs", i++));
		objects.add(new GameObjectDef("hopper", "You put grain in here", "operate", "Examine", 1, 2, 2, 0, "milltop", i++));
		objects.add(new GameObjectDef("signpost", "Ardougne city zoo", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("sign", "The flying horse", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Lever", "The lever is up", "pull", "Examine", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("Lever", "The lever is up", "pull", "Examine", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("pipe", "a dirty sewer pipe", "WalkTo", "Examine", 1, 1, 1, 0, "wallpipe", i++));
		objects.add(new GameObjectDef("fish", "I can see fish swimming in the water", "Bait", "Examine", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("fish", "I can see fish swimming in the water", "Bait", "Examine", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("fish", "I can see fish swimming in the water", "Bait", "Examine", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("fish", "I can see fish swimming in the water", "Bait", "Examine", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("Vine", "A creepy creeper", "WalkTo", "Examine", 0, 1, 1, 0, "vine", i++));
		objects.add(new GameObjectDef("gate", "The main entrance to McGrubor's wood", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("gate", "The gate is open", "WalkTo", "Examine", 2, 1, 2, 0, "woodengateopen", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "woodengateclosed", i++));
		objects.add(new GameObjectDef("stairs", "These lead downstairs", "Go down", "Examine", 1, 2, 3, 0, "stonestairsdown", i++));
		objects.add(new GameObjectDef("broken cart", "A farm cart", "WalkTo", "Examine", 1, 2, 3, 0, "brokencart", i++));
		objects.add(new GameObjectDef("Lever", "It's a lever", "Pull", "Searchfortraps", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("clock pole blue", "A pole - a pole to put cog's on", "inspect", "Examine", 0, 1, 1, 0, "clockpoleblue", i++));
		objects.add(new GameObjectDef("clock pole red", "A pole - a pole to put cog's on", "inspect", "Examine", 0, 1, 1, 0, "clockpolered", i++));
		objects.add(new GameObjectDef("clock pole purple", "A pole - a pole to put cog's on", "inspect", "Examine", 0, 1, 1, 0, "clockpolepurple", i++));
		objects.add(new GameObjectDef("clock pole black", "A pole - a pole to put cog's on", "inspect", "Examine", 0, 1, 1, 0, "clockpoleblack", i++));
		objects.add(new GameObjectDef("wallclockface", "It's a large clock face", "WalkTo", "Examine", 1, 2, 2, 0, "wallclockface", i++));
		objects.add(new GameObjectDef("Lever Bracket", "Theres something missing here", "WalkTo", "Examine", 0, 1, 1, 0, "leverbracket", i++));
		objects.add(new GameObjectDef("Lever", "It's a lever", "Pull", "Examine", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("stairs", "These lead upstairs", "Go up", "Examine", 1, 2, 3, 0, "woodenstairs", i++));
		objects.add(new GameObjectDef("stairs", "These lead downstairs", "Go down", "Examine", 1, 2, 3, 0, "woodenstairsdown", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "metalgateclosed2", i++));
		objects.add(new GameObjectDef("gate", "The gate is open", "close", "Examine", 3, 1, 2, 0, "metalgateopen2", i++));
		objects.add(new GameObjectDef("Lever", "The lever is up", "pull", "Examine", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("Lever", "The lever is up", "push", "Examine", 0, 1, 1, 0, "leverdown", i++));
		objects.add(new GameObjectDef("Foodtrough", "It's for feeding the rat's", "WalkTo", "Examine", 1, 2, 1, 0, "Foodtrough", i++));
		objects.add(new GameObjectDef("fish", "I can see fish swimming in the water", "cage", "harpoon", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("spearwall", "It's a defensive battlement", "WalkTo", "Examine", 1, 2, 1, 0, "spearwall", i++));
		objects.add(new GameObjectDef("hornedskull", "A horned dragon skull", "WalkTo", "Examine", 1, 2, 2, 0, "hornedskull", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "picklock", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "picklock", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("guardscupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("guardscupboard", "Perhaps I should search it", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("Coal truck", "I can use this to transport coal", "get coal from", "Examine", 1, 1, 1, 0, "minecart", i++));
		objects.add(new GameObjectDef("ship", "A ship to Port Birmhaven", "board", "Examine", 0, 5, 3, 0, "Shipfront", i++));
		objects.add(new GameObjectDef("ship", "A ship to Port Birmhaven", "board", "Examine", 0, 2, 3, 0, "Shipmiddle", i++));
		objects.add(new GameObjectDef("ship", "A ship to Port Birmhaven", "board", "Examine", 0, 5, 3, 0, "Shipback", i++));
		objects.add(new GameObjectDef("Tree", "It's a tree house", "WalkTo", "Examine", 1, 1, 1, 0, "toplesstree", i++));
		objects.add(new GameObjectDef("Ballista", "It's a war machine", "fire", "Examine", 1, 4, 1, 0, "catabow", i++));
		objects.add(new GameObjectDef("largespear", "", "WalkTo", "Examine", 1, 2, 1, 0, "catabowarrow", i++));
		objects.add(new GameObjectDef("spirit tree", "A grand old spirit tree", "talk to", "Examine", 1, 2, 2, 0, "ent", i++));
		objects.add(new GameObjectDef("young spirit Tree", "Ancestor of the spirit tree", "talk to", "Examine", 1, 1, 1, 0, "tree2", i++));
	}

	private static void loadGameObjectDefinitionsB() {
		int i = objects.size() - 1;
		objects.add(new GameObjectDef("gate", "The gate is closed", "talk through", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("wall", "A damaged wall", "climb", "Examine", 1, 3, 1, 0, "khazardwall", i++));
		objects.add(new GameObjectDef("tree", "An exotic looking tree", "WalkTo", "Examine", 1, 1, 1, 0, "jungle tree 2", i++));
		objects.add(new GameObjectDef("tree", "An exotic looking tree", "WalkTo", "Examine", 1, 1, 1, 0, "jungle tree 1", i++));
		objects.add(new GameObjectDef("Fern", "An exotic leafy plant", "WalkTo", "Examine", 0, 1, 1, 0, "jungle fern textured", i++));
		objects.add(new GameObjectDef("Fern", "An exotic leafy plant", "WalkTo", "Examine", 0, 1, 1, 0, "jungle fern textured 2", i++));
		objects.add(new GameObjectDef("Fern", "An exotic leafy plant", "WalkTo", "Examine", 0, 1, 1, 0, "jungle fern textured 3", i++));
		objects.add(new GameObjectDef("Fern", "An exotic leafy plant", "WalkTo", "Examine", 0, 1, 1, 0, "jungle fern textured 4", i++));
		objects.add(new GameObjectDef("fly trap", "A small carnivourous plant", "approach", "Search", 0, 1, 1, 0, "jungle fly trap", i++));
		objects.add(new GameObjectDef("Fern", "An exotic leafy plant", "WalkTo", "Examine", 0, 1, 1, 0, "jungle fern", i++));
		objects.add(new GameObjectDef("Fern", "An exotic spikey plant", "WalkTo", "Examine", 0, 1, 1, 0, "jungle spikey fern", i++));
		objects.add(new GameObjectDef("plant", "What an unusual plant", "WalkTo", "Examine", 0, 1, 1, 0, "jungle strange plant", i++));
		objects.add(new GameObjectDef("plant", "An odd looking plant", "WalkTo", "Examine", 1, 1, 1, 0, "jungle strange plant 2", i++));
		objects.add(new GameObjectDef("plant", "some nice jungle foliage", "WalkTo", "Examine", 1, 1, 1, 0, "jungle medium size plant", i++));
		objects.add(new GameObjectDef("stone head", "It looks like it's been here some time", "WalkTo", "Examine", 1, 2, 2, 0, "jungle statue", i++));
		objects.add(new GameObjectDef("dead Tree", "A rotting tree", "WalkTo", "Examine", 1, 1, 1, 0, "deadtree2", i++));
		objects.add(new GameObjectDef("sacks", "Yep they're sacks", "WalkTo", "prod", 1, 1, 1, 0, "sacks", i++));
		objects.add(new GameObjectDef("khazard open Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("khazard shut Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("doorframe", "It's a stone doorframe", "WalkTo", "Examine", 3, 1, 2, 0, "doubledoorframe", i++));
		objects.add(new GameObjectDef("Sewer valve", "It changes the water flow of the sewer's", "turn left", "turn right", 1, 1, 1, 0, "sewervalve", i++));
		objects.add(new GameObjectDef("Sewer valve 2", "It changes the water flow of the sewer's", "turn left", "turn right", 1, 1, 1, 0, "sewervalve", i++));
		objects.add(new GameObjectDef("Sewer valve 3", "It changes the water flow of the sewer's", "turn left", "turn right", 1, 1, 1, 0, "sewervalve", i++));
		objects.add(new GameObjectDef("Sewer valve 4", "It changes the water flow of the sewer's", "turn left", "turn right", 1, 1, 1, 0, "sewervalve", i++));
		objects.add(new GameObjectDef("Sewer valve 5", "It changes the water flow of the sewer's", "turn left", "turn right", 1, 1, 1, 0, "sewervalve", i++));
		objects.add(new GameObjectDef("Cave entrance", "I wonder what is inside...", "enter", "Examine", 1, 2, 2, 0, "caveentrance", i++));
		objects.add(new GameObjectDef("Log bridge", "A tree gnome construction", "WalkTo", "Examine", 0, 1, 1, 0, "logbridgelow", i++));
		objects.add(new GameObjectDef("Log bridge", "A tree gnome construction", "WalkTo", "Examine", 0, 1, 1, 0, "logbridgehigh", i++));
		objects.add(new GameObjectDef("tree platform", "A tree gnome construction", "WalkTo", "Examine", 0, 1, 1, 0, "treeplatformhigh", i++));
		objects.add(new GameObjectDef("tree platform", "A tree gnome construction", "WalkTo", "Examine", 0, 1, 1, 0, "treeplatformlow", i++));
		objects.add(new GameObjectDef("gate", "The gate is open", "close", "Examine", 2, 1, 2, 0, "metalgateopen2", i++));
		objects.add(new GameObjectDef("tree platform", "A tree gnome construction", "WalkTo", "Examine", 0, 1, 1, 0, "largetreeplatformlow", i++));
		objects.add(new GameObjectDef("tree platform", "A tree gnome construction", "WalkTo", "Examine", 0, 1, 1, 0, "largetreeplatformhigh", i++));
		objects.add(new GameObjectDef("Log bridge", "A tree gnome construction", "WalkTo", "Examine", 0, 1, 1, 0, "logbridgecurvedhigh", i++));
		objects.add(new GameObjectDef("Log bridge", "A tree gnome construction", "WalkTo", "Examine", 0, 1, 1, 0, "logbridgecurvedlow", i++));
		objects.add(new GameObjectDef("tree platform", "A tree gnome construction", "WalkTo", "Examine", 0, 1, 1, 0, "treeplatformlow2", i++));
		objects.add(new GameObjectDef("tree platform", "A tree gnome construction", "WalkTo", "Examine", 0, 1, 1, 0, "treeplatformhigh2", i++));
		objects.add(new GameObjectDef("Tribal brew", "A very large pot", "WalkTo", "drink", 1, 1, 1, 0, "cauldron", i++));
		objects.add(new GameObjectDef("Pineapple tree", "A tree with nice ripe pineapples growing on it", "WalkTo", "Pick pineapple", 1, 1, 1, 0, "palm2", i++));
		objects.add(new GameObjectDef("Pineapple tree", "There are no pineapples left on the tree", "WalkTo", "Pick pineapple", 1, 1, 1, 0, "palm2", i++));
		objects.add(new GameObjectDef("log raft", "A mighty fine raft", "board", "Examine", 0, 1, 1, 96, "lograft", i++));
		objects.add(new GameObjectDef("log raft", "A mighty fine raft", "board", "Examine", 0, 1, 1, 96, "lograft", i++));
		objects.add(new GameObjectDef("Tomb of hazeel", "A clay shrine to lord hazeel", "WalkTo", "Examine", 1, 1, 2, 96, "hazeeltomb", i++));
		objects.add(new GameObjectDef("range", "A pot of soup slowly cooking", "WalkTo", "Examine", 1, 1, 2, 0, "range", i++));
		objects.add(new GameObjectDef("Bookcase", "A large collection of books", "Search", "Examine", 1, 1, 2, 0, "bookcase", i++));
		objects.add(new GameObjectDef("Carnillean Chest", "Perhaps I should search it", "WalkTo", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Carnillean Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("crate", "A crate used for storing food", "Search", "Examine", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("Butlers cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("Butlers cupboard", "The cupboard is open", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("gate", "The gate is open", "WalkTo", "Examine", 2, 1, 2, 0, "woodengateopen", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "woodengateclosed", i++));
		objects.add(new GameObjectDef("Cattle furnace", "A red hot furnace", "WalkTo", "Examine", 1, 2, 2, 0, "furnace", i++));
		objects.add(new GameObjectDef("Ardounge wall", "A huge wall seperating east and west ardounge", "WalkTo", "Examine", 1, 1, 3, 0, "ardoungewall", i++));
		objects.add(new GameObjectDef("Ardounge wall corner", "A huge wall seperating east and west ardounge", "WalkTo", "Examine", 1, 1, 1, 0, "ardoungewallcorner", i++));
		objects.add(new GameObjectDef("Dug up soil", "A freshly dug pile of mud", "WalkTo", "Examine", 0, 1, 1, 0, "mudpatch", i++));
		objects.add(new GameObjectDef("Pile of mud", "Mud caved in from above", "climb", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("large Sewer pipe", "a dirty sewer pipe", "enter", "Examine", 1, 1, 1, 0, "largesewerpipe", i++));
		objects.add(new GameObjectDef("Ardounge wall gateway", "A huge set of heavy wooden doors", "open", "Examine", 2, 1, 2, 0, "doubledoorsclosed", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is open", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("Fishing crane", "For hauling in large catches of fish", "operate", "Examine", 1, 1, 2, 0, "fishingcrane", i++));
		objects.add(new GameObjectDef("Rowboat", "A reasonably sea worthy two man boat", "WalkTo", "Examine", 1, 2, 2, 0, "rowboat", i++));
		objects.add(new GameObjectDef("Damaged Rowboat", "A not so sea worthy two man boat", "WalkTo", "Examine", 1, 2, 2, 0, "rowboatsinking", i++));
		objects.add(new GameObjectDef("barrel", "I wonder what's inside", "WalkTo", "Search", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("Fishing crane", "For hauling in large catches of fish", "operate", "Examine", 1, 1, 1, 0, "fishingcranerot1", i++));
		objects.add(new GameObjectDef("Fishing crane", "For hauling in large catches of fish", "operate", "Examine", 1, 1, 1, 0, "fishingcranerot2", i++));
		objects.add(new GameObjectDef("Waterfall", "it's a waterfall", "WalkTo", "Examine", 2, 1, 2, 0, "waterfall", i++));
		objects.add(new GameObjectDef("leaflessTree", "A pointy tree", "jump off", "jump to next", 1, 1, 1, 0, "deadtree2base", i++));
		objects.add(new GameObjectDef("leaflessTree", "A pointy tree", "jump off", "jump to next", 1, 1, 1, 0, "deadtree2base", i++));
		objects.add(new GameObjectDef("log raft", "A mighty fine raft", "board", "Examine", 0, 1, 1, 96, "lograft", i++));
		objects.add(new GameObjectDef("doors", "The doors are shut", "Open", "Examine", 2, 1, 2, 0, "doubledoorsclosed", i++));
		objects.add(new GameObjectDef("Well", "An oddly placed well", "operate", "Examine", 1, 2, 2, 0, "well", i++));
		objects.add(new GameObjectDef("Tomb of glarial", "A stone tomb surrounded by flowers", "Search", "Examine", 1, 2, 4, 96, "elventomb", i++));
		objects.add(new GameObjectDef("Waterfall", "it's a fast flowing waterfall", "jump off", "Examine", 2, 1, 2, 0, "waterfalllev1", i++));
		objects.add(new GameObjectDef("Waterfall", "it's a fast flowing waterfall", "jump off", "Examine", 0, 1, 2, 0, "waterfalllev2", i++));
		objects.add(new GameObjectDef("Bookcase", "A large collection of books", "Search", "Examine", 1, 1, 2, 0, "bookcase", i++));
		objects.add(new GameObjectDef("doors", "The doors are shut", "Open", "Examine", 2, 1, 2, 0, "doubledoorsclosed", i++));
		objects.add(new GameObjectDef("doors", "The doors are shut", "Open", "Examine", 2, 1, 2, 0, "doubledoorsclosed", i++));
		objects.add(new GameObjectDef("Stone stand", "On top is an indent the size of a rune stone", "WalkTo", "Examine", 1, 1, 1, 0, "stonestand", i++));
		objects.add(new GameObjectDef("Stone stand", "On top is an indent the size of a rune stone", "WalkTo", "Examine", 1, 1, 1, 0, "stonestand", i++));
		objects.add(new GameObjectDef("Stone stand", "On top is an indent the size of a rune stone", "WalkTo", "Examine", 1, 1, 1, 0, "stonestand", i++));
		objects.add(new GameObjectDef("Stone stand", "On top is an indent the size of a rune stone", "WalkTo", "Examine", 1, 1, 1, 0, "stonestand", i++));
		objects.add(new GameObjectDef("Stone stand", "On top is an indent the size of a rune stone", "WalkTo", "Examine", 1, 1, 1, 0, "stonestand", i++));
		objects.add(new GameObjectDef("Stone stand", "On top is an indent the size of a rune stone", "WalkTo", "Examine", 1, 1, 1, 0, "stonestand", i++));
		objects.add(new GameObjectDef("Glarial's Gravestone", "There is an indent the size of a pebble in the stone's center", "read", "Examine", 1, 1, 1, 0, "gravestone1", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("crate", "It's a crate", "Search", "Examine", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("leaflessTree", "A pointy tree", "jump off", "Examine", 1, 1, 1, 0, "deadtree2base", i++));
		objects.add(new GameObjectDef("Statue of glarial", "A statue of queen glarial - something's missing", "WalkTo", "Examine", 1, 1, 1, 0, "glarialsstatue", i++));
		objects.add(new GameObjectDef("Chalice of eternity", "A magically elevated chalice full of treasure", "WalkTo", "Examine", 1, 1, 1, 0, "baxtorianchalice", i++));
		objects.add(new GameObjectDef("Chalice of eternity", "A magically elevated chalice full of treasure", "empty", "Examine", 1, 1, 1, 0, "baxtorianchalicelow", i++));
		objects.add(new GameObjectDef("doors", "The doors are shut", "Open", "Examine", 2, 1, 2, 0, "doubledoorsclosed", i++));
		objects.add(new GameObjectDef("Lever", "The lever is up", "pull", "Examine", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("Lever", "The lever is up", "pull", "Examine", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("log raft remains", "oops!", "WalkTo", "Examine", 0, 1, 1, 96, "brokenlograft", i++));
		objects.add(new GameObjectDef("Tree", "A pointy tree", "WalkTo", "Examine", 0, 1, 1, 0, "tree2", i++));
		objects.add(new GameObjectDef(" Range", "A hot well stoked range", "WalkTo", "Examine", 1, 1, 2, 0, "range", i++));
		objects.add(new GameObjectDef("crate", "It's an old crate", "WalkTo", "Search", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("fish", "I can see fish swimming in the water", "Net", "Examine", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("Watch tower", "They're always watching", "approach", "Examine", 1, 2, 2, 0, "watchtower", i++));
		objects.add(new GameObjectDef("signpost", "Tourist infomation", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("doors", "The doors are open", "WalkTo", "Examine", 2, 1, 2, 0, "doubledoorsopen", i++));
		objects.add(new GameObjectDef("Rope ladder", "A hand made ladder", "WalkTo", "Examine", 1, 1, 1, 0, "ropeladder", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("cupboard", "Perhaps I should search it", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("Rope ladder", "A hand made ladder", "WalkTo", "Examine", 1, 1, 1, 0, "ropeladder", i++));
		objects.add(new GameObjectDef("Cooking pot", "the mourners are busy enjoying this stew", "WalkTo", "Examine", 1, 1, 1, 0, "cauldron", i++));
		objects.add(new GameObjectDef("Gallow", "Best not hang about!", "WalkTo", "Examine", 1, 2, 2, 0, "gallows", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("crate", "A crate used for storing confiscated goods", "WalkTo", "Search", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("cupboard", "Perhaps I should search it", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("cupboard", "Perhaps I should search it", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("sign", "Tailors fancy dress", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("grand tree", "the grand tree", "WalkTo", "Examine", 0, 1, 1, 0, "grand tree-lev 0", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "woodengateclosed", i++));
		objects.add(new GameObjectDef("gate", "The gate is open", "WalkTo", "close", 2, 1, 2, 0, "woodengateopen", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Log bridge", "A tree gnome construction", "WalkTo", "Examine", 0, 1, 1, 0, "logbridge lev0", i++));
		objects.add(new GameObjectDef("Watch tower", "A tree gnome construction", "WalkTo", "Examine", 1, 1, 1, 0, "gnomewatchtower lev0", i++));
		objects.add(new GameObjectDef("Log bridge", "A tree gnome construction", "WalkTo", "Examine", 0, 0, 0, 0, "logbridgejunction lev0", i++));
		objects.add(new GameObjectDef("climbing rocks", "I wonder if I can climb up these", "climb", "Examine", 0, 1, 1, 0, "climbing_rocks", i++));
		objects.add(new GameObjectDef("Ledge", "It looks rather thin", "balance on", "Examine", 0, 1, 0, 0, "corner_ledge", i++));
		objects.add(new GameObjectDef("Ledge", "It looks rather thin", "balance on", "Examine", 0, 1, 1, 0, "straight_ledge", i++));
		objects.add(new GameObjectDef("log", "It looks slippery", "WalkTo", "Examine", 0, 1, 1, 0, "log_balance1", i++));
		objects.add(new GameObjectDef("log", "It looks slippery", "WalkTo", "Examine", 0, 1, 1, 0, "log_balance2", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("PalmTree", "A shady palm tree", "WalkTo", "Search", 1, 1, 1, 0, "palm", i++));
		objects.add(new GameObjectDef("Scorched Earth", "An area of burnt soil", "WalkTo", "Search", 0, 1, 1, 0, "mudpatch", i++));
		objects.add(new GameObjectDef("Rocks", "A moss covered rock", "Mine", "Search", 1, 1, 1, 0, "mossyrock", i++));
		objects.add(new GameObjectDef("sign", "The dancing donkey inn", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("fish", "I can see fish swimming in the water", "harpoon", "cage", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("Rocky Walkway", "A precarious rocky walkway", "Balance", "Examine", 1, 1, 1, 0, "rocktile", i++));
		objects.add(new GameObjectDef("Rocky Walkway", "A precarious rocky walkway", "Balance", "Examine", 1, 1, 1, 0, "rocktile", i++));
		objects.add(new GameObjectDef("Rocky Walkway", "A precarious rocky walkway", "Balance", "Examine", 1, 1, 1, 0, "rocktile", i++));
		objects.add(new GameObjectDef("Rocky Walkway", "A precarious rocky walkway", "Balance", "Examine", 1, 1, 1, 0, "rocktile", i++));
		objects.add(new GameObjectDef("fight Dummy", "I can practice my fighting here", "hit", "Examine", 1, 1, 1, 0, "sworddummy", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Jungle Vine", "A deep jungle Vine", "WalkTo", "Search", 0, 1, 1, 0, "vine", i++));
		objects.add(new GameObjectDef("statue", "hand carved", "WalkTo", "Examine", 1, 1, 1, 0, "tribalstature", i++));
		objects.add(new GameObjectDef("sign", "Ye Olde Dragon Inn", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("grand tree", "the grand tree", "WalkTo", "Examine", 0, 1, 1, 0, "grand treeinside-lev 0", i++));
		objects.add(new GameObjectDef("grand tree", "the grand tree", "WalkTo", "Examine", 0, 1, 1, 0, "grand treeinside-lev 1", i++));
		objects.add(new GameObjectDef("grand tree", "the grand tree", "WalkTo", "Examine", 0, 1, 1, 0, "grand treeinside-lev 2", i++));
		objects.add(new GameObjectDef("grand tree", "the grand tree", "WalkTo", "Examine", 0, 1, 1, 0, "grand tree-lev 1", i++));
		objects.add(new GameObjectDef("grand tree", "the grand tree", "WalkTo", "Examine", 0, 1, 1, 0, "grand tree-lev 2", i++));
		objects.add(new GameObjectDef("Hillside Entrance", "Large doors that seem to lead into the hillside", "Open", "Search", 2, 1, 2, 0, "hillsidedoor", i++));
		objects.add(new GameObjectDef("tree", "A large exotic looking tree", "WalkTo", "Search", 1, 1, 1, 0, "jungle medium size plant", i++));
		objects.add(new GameObjectDef("Log bridge", "A tree gnome construction", "WalkTo", "Examine", 0, 0, 0, 0, "logbridgejunction lev1", i++));
		objects.add(new GameObjectDef("Tree platform", "A tree gnome construction", "WalkTo", "Examine", 0, 0, 0, 0, "fourwayplatform-lev 0", i++));
		objects.add(new GameObjectDef("Tree platform", "A tree gnome construction", "WalkTo", "Examine", 0, 0, 0, 0, "fourwayplatform-lev 1", i++));
		objects.add(new GameObjectDef("Metalic Dungeon Gate", "It seems to be closed", "Open", "Search", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Log bridge", "A tree gnome construction", "WalkTo", "Examine", 0, 0, 0, 0, "logbridge lev1", i++));
		objects.add(new GameObjectDef("Log bridge", "A tree gnome construction", "WalkTo", "Examine", 0, 0, 0, 0, "logbridge lev2", i++));
		objects.add(new GameObjectDef("Watch tower", "A tree gnome construction", "WalkTo", "Examine", 1, 0, 0, 0, "gnomewatchtower lev1", i++));
		objects.add(new GameObjectDef("Watch tower", "A tree gnome construction", "WalkTo", "Examine", 1, 0, 0, 0, "gnomewatchtower lev2", i++));
		objects.add(new GameObjectDef("Shallow water", "A small opening in the ground with some spots of water", "WalkTo", "Investigate", 1, 2, 2, 0, "rockpoolwater", i++));
		objects.add(new GameObjectDef("Doors", "Perhaps you should give them a push", "Open", "Search", 2, 1, 2, 0, "hillsidedoor", i++));
		objects.add(new GameObjectDef("grand tree", "the grand tree", "WalkTo", "Examine", 0, 1, 1, 0, "grand tree-lev 3", i++));
		objects.add(new GameObjectDef("Tree Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 0, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("Tree Ladder", "it's a ladder leading downwards", "Climb-down", "Examine", 0, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("blurberrys cocktail bar", "the gnome social hot spot", "WalkTo", "Examine", 0, 1, 1, 0, "blurberrybar", i++));
		objects.add(new GameObjectDef("Gem Rocks", "A rocky outcrop with a vein of semi precious stones", "Mine", "Prospect", 1, 1, 1, 0, "gemrock", i++));
		objects.add(new GameObjectDef("Giannes place", "Eat green eat gnome cruisine", "WalkTo", "Examine", 0, 1, 1, 0, "blurberrybar", i++));
		objects.add(new GameObjectDef("ropeswing", "A good place to train agility", "WalkTo", "Examine", 0, 1, 1, 0, "obstical_ropeswing", i++));
		objects.add(new GameObjectDef("net", "A good place to train agility", "WalkTo", "Examine", 1, 2, 1, 0, "obstical_net", i++));
		objects.add(new GameObjectDef("Frame", "A good place to train agility", "WalkTo", "Examine", 1, 1, 1, 0, "obstical_frame", i++));
		objects.add(new GameObjectDef("Tree", "It has a branch ideal for tying ropes to", "WalkTo", "Examine", 1, 2, 2, 0, "tree_for_rope", i++));
		objects.add(new GameObjectDef("Tree", "I wonder who put that rope there", "WalkTo", "Examine", 1, 2, 2, 0, "tree_with_rope", i++));
		objects.add(new GameObjectDef("Tree", "they look fun to swing on", "WalkTo", "Examine", 1, 2, 2, 0, "tree_with_vines", i++));
		objects.add(new GameObjectDef("cart", "A farm cart", "WalkTo", "Search", 1, 2, 3, 0, "cart", i++));
		objects.add(new GameObjectDef("fence", "it doesn't look too strong", "WalkTo", "Examine", 1, 1, 1, 0, "gnomefence", i++));
		objects.add(new GameObjectDef("beam", "A plank of wood", "WalkTo", "Examine", 0, 1, 1, 0, "beam", i++));
		objects.add(new GameObjectDef("Sign", "read me", "WalkTo", "Examine", 1, 1, 1, 0, "gnomesign", i++));
		objects.add(new GameObjectDef("Sign", "Blurberry's cocktail bar", "WalkTo", "Examine", 1, 1, 1, 0, "gnomesign", i++));
		objects.add(new GameObjectDef("Sign", "Giannes tree gnome cuisine", "WalkTo", "Examine", 1, 1, 1, 0, "gnomesign", i++));
		objects.add(new GameObjectDef("Sign", "Heckel funch's grocery store", "WalkTo", "Examine", 1, 1, 1, 0, "gnomesign", i++));
		objects.add(new GameObjectDef("Sign", "Hudo glenfad's grocery store", "WalkTo", "Examine", 1, 1, 1, 0, "gnomesign", i++));
		objects.add(new GameObjectDef("Sign", "Rometti's fashion outlet", "WalkTo", "Examine", 1, 1, 1, 0, "gnomesign", i++));
		objects.add(new GameObjectDef("Sign", "Tree gnome bank and rometti's fashion outlet", "WalkTo", "Examine", 1, 1, 1, 0, "gnomesign", i++));
		objects.add(new GameObjectDef("Sign", "Tree gnome local swamp", "WalkTo", "Examine", 1, 1, 1, 0, "gnomesign", i++));
		objects.add(new GameObjectDef("Sign", "Agility training course", "WalkTo", "Examine", 1, 1, 1, 0, "gnomesign", i++));
		objects.add(new GameObjectDef("Sign", "To the grand tree", "WalkTo", "Examine", 1, 1, 1, 0, "gnomesign", i++));
		objects.add(new GameObjectDef("Root", "To the grand tree", "search", "Examine", 1, 1, 1, 0, "treeroot1", i++));
		objects.add(new GameObjectDef("Root", "To the grand tree", "search", "Examine", 1, 1, 1, 0, "treeroot2", i++));
		objects.add(new GameObjectDef("Metal Gate", "The gate is closed", "Open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Metal Gate", "The gate is open", "WalkTo", "close", 2, 1, 2, 0, "metalgateopen", i++));
		objects.add(new GameObjectDef("A farm cart", "It is blocking the entrance to the village", "Examine", "Search", 1, 2, 3, 0, "cart", i++));
		objects.add(new GameObjectDef("Ledge", "It looks rather thin", "balance on", "Examine", 0, 1, 1, 0, "straight_ledge", i++));
		objects.add(new GameObjectDef("Ledge", "It looks rather thin", "balance on", "Examine", 0, 1, 1, 0, "straight_ledge", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("cage", "i don't like the look of that", "open", "Examine", 0, 1, 1, 0, "gnomecage", i++));
		objects.add(new GameObjectDef("glider", "i wonder if it flys", "fly", "Examine", 1, 1, 1, 0, "gnomeglider", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("cupboard", "Perhaps I should search it", "Search", "close", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("stairs", "These lead upstairs", "Go up", "Examine", 1, 2, 3, 0, "woodenstairs", i++));
		objects.add(new GameObjectDef("glider", "i wonder if it flys", "WalkTo", "Examine", 1, 1, 1, 0, "gnomeglidercrashed", i++));
		objects.add(new GameObjectDef("gate", "The gate is open", "WalkTo", "close", 1, 1, 2, 0, "woodengateopen", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 1, 1, 2, 0, "woodengateclosed", i++));
		objects.add(new GameObjectDef("chaos altar", "An altar to the evil God Zamorak", "Recharge at", "Examine", 1, 2, 1, 0, "chaosaltar", i++));
		objects.add(new GameObjectDef("Gnome stronghold gate", "The gate is closed", "Open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("ropeswing", "A good place to train agility", "Swing", "Examine", 0, 1, 1, 0, "obstical_ropeswing", i++));
		objects.add(new GameObjectDef("ropeswing", "A good place to train agility", "Swing", "Examine", 0, 1, 1, 0, "obstical_ropeswing", i++));
		objects.add(new GameObjectDef("stairs", "These lead upstairs", "Go up", "Examine", 1, 2, 3, 0, "woodenstairs", i++));
		objects.add(new GameObjectDef("stairs", "These lead downstairs", "Go down", "Examine", 1, 2, 3, 0, "woodenstairsdown", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Pile of rubble", "What a mess", "climb", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("Stone stand", "On top our four indents from left to right", "WalkTo", "push down", 1, 1, 1, 0, "stonestand", i++));
		objects.add(new GameObjectDef("Watch tower", "A tree gnome construction", "climb up", "Examine", 1, 1, 1, 0, "gnomewatchtower lev1", i++));
		objects.add(new GameObjectDef("Pile of rubble", "What a mess", "climb", "Examine", 1, 2, 1, 0, "mudpiledown", i++));
		objects.add(new GameObjectDef("Root", "To the grand tree", "search", "Examine", 1, 1, 1, 0, "treeroot2", i++));
		objects.add(new GameObjectDef("Root", "To the grand tree", "push", "Examine", 1, 1, 1, 0, "treeroot2", i++));
		objects.add(new GameObjectDef("Root", "To the grand tree", "push", "Examine", 1, 1, 1, 0, "treeroot2", i++));
		objects.add(new GameObjectDef("Sign", "Home to the Head tree guardian", "WalkTo", "Examine", 1, 1, 1, 0, "gnomesign", i++));
		objects.add(new GameObjectDef("Hammock", "They've got to sleep somewhere", "lie in", "Examine", 1, 1, 2, 0, "gnomehamek", i++));
		objects.add(new GameObjectDef("Goal", "You're supposed to throw the ball here", "WalkTo", "Examine", 0, 1, 1, 0, "gnomegoal", i++));
		objects.add(new GameObjectDef("stone tile", "It looks as if it might move", "twist", "Examine", 1, 1, 1, 0, "stonedisc", i++));
		objects.add(new GameObjectDef("Chest", "You get a sense of dread from the chest", "WalkTo", "Examine", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "You get a sense of dread from the chest", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Watch tower", "A tree gnome construction", "WalkTo", "climb down", 0, 1, 1, 0, "gnomewatchtower lev2", i++));
		objects.add(new GameObjectDef("net", "A good place to train agility", "climb", "Examine", 1, 2, 1, 0, "obstical_net", i++));
		objects.add(new GameObjectDef("Watch tower", "A tree gnome construction", "climb up", "Examine", 1, 1, 1, 0, "gnomewatchtower lev1", i++));
		objects.add(new GameObjectDef("Watch tower", "A tree gnome construction", "climb down", "Examine", 1, 1, 1, 0, "gnomewatchtower lev2", i++));
		objects.add(new GameObjectDef("ropeswing", "A good place to train agility", "grab hold of", "Examine", 0, 1, 1, 0, "obstical_ropeswing", i++));
		objects.add(new GameObjectDef("Bumpy Dirt", "Some disturbed earth", "Look", "Search", 0, 1, 1, 0, "mudpatch", i++));
		objects.add(new GameObjectDef("pipe", "a dirty sewer pipe", "WalkTo", "Examine", 1, 1, 1, 0, "obstical_pipe", i++));
		objects.add(new GameObjectDef("net", "A good place to train agility", "climb", "Examine", 1, 2, 1, 0, "obstical_net", i++));
		objects.add(new GameObjectDef("pipe", "a dirty sewer pipe", "enter", "Examine", 1, 1, 1, 0, "obstical_pipe", i++));
		objects.add(new GameObjectDef("log", "It looks slippery", "balance on", "Examine", 0, 1, 1, 0, "log_balance1", i++));
		objects.add(new GameObjectDef("pipe", "a dirty sewer pipe", "enter", "Examine", 1, 1, 1, 0, "obstical_pipe", i++));
		objects.add(new GameObjectDef("pipe", "a dirty sewer pipe", "enter", "Examine", 0, 1, 1, 0, "obstical_pipe", i++));
		objects.add(new GameObjectDef("Handholds", "I wonder if I can climb up these", "climb", "Examine", 0, 1, 1, 0, "climbing_rocks", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "woodengateclosed", i++));
		objects.add(new GameObjectDef("stronghold spirit Tree", "Ancestor of the spirit tree", "talk to", "Examine", 1, 1, 1, 0, "tree2", i++));
		objects.add(new GameObjectDef("Tree", "It has a branch ideal for tying ropes to", "WalkTo", "Examine", 1, 2, 2, 0, "tree_for_rope", i++));
		objects.add(new GameObjectDef("Tree", "I wonder who put that rope there", "swing on", "Examine", 1, 2, 2, 0, "tree_with_rope", i++));
		objects.add(new GameObjectDef("Tree", "I wonder who put that rope there", "swing on", "Examine", 1, 2, 2, 0, "tree_with_rope", i++));
		objects.add(new GameObjectDef("Spiked pit", "I don't want to go down there", "WalkTo", "Examine", 1, 2, 2, 0, "spikedpit-low", i++));
		objects.add(new GameObjectDef("Spiked pit", "I don't want to go down there", "WalkTo", "Examine", 1, 2, 2, 0, "spikedpit", i++));
		objects.add(new GameObjectDef("Cave", "I wonder what is inside...", "enter", "Examine", 1, 2, 2, 0, "caveentrance", i++));
		objects.add(new GameObjectDef("stone pebble", "Looks like a stone", "WalkTo", "Examine", 1, 1, 1, 0, "stonedisc", i++));
		objects.add(new GameObjectDef("Pile of rubble", "Rocks that have caved in", "WalkTo", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("Pile of rubble", "Rocks that have caved in", "WalkTo", "Search", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("pipe", "I might be able to fit through this", "enter", "Examine", 1, 1, 1, 0, "obstical_pipe", i++));
		objects.add(new GameObjectDef("pipe", "2", "enter", "Examine", 1, 1, 1, 0, "obstical_pipe", i++));
		objects.add(new GameObjectDef("Stone", "Looks like a stone", "WalkTo", "Examine", 1, 1, 1, 0, "stonedisc", i++));
		objects.add(new GameObjectDef("Stone", "Looks like a stone", "Look Closer", "Investigate", 1, 1, 1, 0, "stonedisc", i++));
		objects.add(new GameObjectDef("ropeswing", "A good place to train agility", "Swing", "Examine", 0, 1, 1, 0, "obstical_ropeswing", i++));
		objects.add(new GameObjectDef("log", "It looks slippery", "balance on", "Examine", 0, 1, 1, 0, "log_balance1", i++));
		objects.add(new GameObjectDef("net", "A good place to train agility", "Climb Up", "Examine", 1, 2, 1, 0, "obstical_net", i++));
		objects.add(new GameObjectDef("Ledge", "It looks rather thin", "balance on", "Examine", 0, 1, 1, 0, "straight_ledge", i++));
		objects.add(new GameObjectDef("Handholds", "I wonder if I can climb up these", "climb", "Examine", 0, 1, 1, 0, "climbing_rocks", i++));
		objects.add(new GameObjectDef("log", "It looks slippery", "balance on", "Examine", 0, 1, 1, 0, "log_balance1", i++));
		objects.add(new GameObjectDef("log", "It looks slippery", "balance on", "Examine", 0, 1, 1, 0, "log_balance1", i++));
		objects.add(new GameObjectDef("Rotten Gallows", "A human corpse hangs from the noose", "Look", "Search", 1, 2, 2, 0, "gallows", i++));
		objects.add(new GameObjectDef("Pile of rubble", "Rocks that have caved in", "WalkTo", "Search", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("ropeswing", "I wonder what's over here", "Swing", "Examine", 0, 1, 1, 0, "obstical_ropeswing", i++));
		objects.add(new GameObjectDef("ropeswing", "I wonder what's over here", "Swing", "Examine", 0, 1, 1, 0, "obstical_ropeswing", i++));
		objects.add(new GameObjectDef("ocks", "A moss covered rock", "balance", "Examine", 1, 1, 1, 0, "mossyrock", i++));
		objects.add(new GameObjectDef("Tree", "This tree doesn't look too healthy", "WalkTo", "balance", 1, 1, 1, 0, "deadtree1", i++));
		objects.add(new GameObjectDef("Well stacked rocks", "Rocks that have been stacked at regular intervals", "Investigate", "Search", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Tomb Dolmen", "An ancient construct for displaying the bones of the deceased", "Look", "Search", 1, 2, 2, 0, "dolmen", i++));
		objects.add(new GameObjectDef("Handholds", "I wonder if I can climb up these", "Climb", "Examine", 0, 1, 1, 0, "climbing_rocks", i++));
		objects.add(new GameObjectDef("Bridge Blockade", "A crudely constructed fence to stop you going further", "Investigate", "Jump", 1, 1, 1, 0, "gnomefence", i++));
		objects.add(new GameObjectDef("Log Bridge", "A slippery log that is a make-do bridge", "Balance On", "Examine", 0, 1, 1, 0, "log_balance2", i++));
		objects.add(new GameObjectDef("Handholds", "I wonder if I can climb up these", "climb", "Examine", 0, 1, 1, 0, "climbing_rocks", i++));
		objects.add(new GameObjectDef("Tree", "they look fun to swing on", "Swing on", "Examine", 1, 2, 2, 0, "tree_with_vines", i++));
		objects.add(new GameObjectDef("Tree", "they look fun to swing on", "Swing on", "Examine", 1, 2, 2, 0, "tree_with_vines", i++));
		objects.add(new GameObjectDef("Wet rocks", "A rocky outcrop", "Look", "Search", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Smashed table", "This table has seen better days", "Examine", "Craft", 1, 1, 1, 0, "smashedtable", i++));
		objects.add(new GameObjectDef("Crude Raft", "A crudely constructed raft", "Disembark", "Examine", 0, 1, 1, 96, "lograft", i++));
		objects.add(new GameObjectDef("Daconia rock", "Piles of daconia rock", "Mine", "Prospect", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("statue", "A statue to mark Taie Bwo Wannai sacred grounds", "WalkTo", "Examine", 1, 1, 1, 0, "tribalstature", i++));
		objects.add(new GameObjectDef("Stepping stones", "A rocky outcrop", "Balance", "Jump onto", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "woodengateclosed", i++));
		objects.add(new GameObjectDef("gate", "Enter to balance into an agility area", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("gate", "Enter to balance into an agility area", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("pipe", "It looks a tight squeeze", "enter", "Examine", 1, 1, 1, 0, "obstical_pipe", i++));
		objects.add(new GameObjectDef("ropeswing", "A good place to train agility", "Swing", "Examine", 0, 1, 1, 0, "obstical_ropeswing", i++));
		objects.add(new GameObjectDef("Stone", "Looks like a stone", "balance on", "Examine", 1, 1, 1, 0, "stonedisc", i++));
		objects.add(new GameObjectDef("Ledge", "It doesn't look stable", "balance on", "Examine", 0, 1, 1, 0, "straight_ledge", i++));
		objects.add(new GameObjectDef("Vine", "A creepy creeper", "climb up", "Examine", 0, 1, 1, 0, "vinecorner", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "WalkTo", "Climb", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Wooden Gate", "The gate is open", "Close", "Close", 2, 1, 2, 0, "woodengateopen", i++));
		objects.add(new GameObjectDef("Wooden Gate", "The gate is closed", "Open", "Examine", 2, 1, 2, 0, "woodengateclosed", i++));
		objects.add(new GameObjectDef("Stone bridge", "An ancient stone construction", "WalkTo", "Examine", 0, 1, 1, 0, "bridge section 1", i++));
		objects.add(new GameObjectDef("Stone bridge", "An ancient stone construction", "WalkTo", "Examine", 0, 1, 1, 0, "bridge section 2", i++));
		objects.add(new GameObjectDef("Stone bridge", "An ancient stone construction", "WalkTo", "Examine", 0, 1, 1, 0, "bridge section 3", i++));
		objects.add(new GameObjectDef("Stone bridge", "An ancient stone construction", "WalkTo", "Examine", 0, 1, 1, 0, "cave bridge support", i++));
		objects.add(new GameObjectDef("Stone platform", "An ancient stone construction", "WalkTo", "Examine", 0, 1, 1, 0, "cave platform small", i++));
		objects.add(new GameObjectDef("fence", "it doesn't look too strong", "WalkTo", "Examine", 1, 1, 1, 0, "gnomefence2", i++));
		objects.add(new GameObjectDef("Rocks", "A rocky outcrop", "Climb", "Climb", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "cross", "Examine", 1, 3, 1, 0, "bridge section collapsed", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "cross", "Examine", 1, 3, 1, 0, "bridge section collapsed2", i++));
		objects.add(new GameObjectDef("Gate of Iban", "It doesn't look very inviting", "open", "Examine", 1, 3, 1, 0, "rams skull door", i++));
		objects.add(new GameObjectDef("Wooden Door", "It doesn't look very inviting", "cross", "Examine", 1, 3, 1, 0, "rams skull dooropen", i++));
		objects.add(new GameObjectDef("Tomb Dolmen", "An ancient construct for displaying the bones of the deceased", "Look", "Search", 1, 2, 2, 0, "dolmen", i++));
		objects.add(new GameObjectDef("Cave entrance", "It doesn't look very inviting", "enter", "Examine", 1, 3, 1, 0, "caveentrance2", i++));
		objects.add(new GameObjectDef("Old bridge", "That's been there a while", "WalkTo", "Examine", 1, 3, 1, 0, "cave old bridge", i++));
		objects.add(new GameObjectDef("Old bridge", "That's been there a while", "cross", "Examine", 1, 3, 1, 0, "cave old bridgedown", i++));
		objects.add(new GameObjectDef("Crumbled rock", "climb up to above ground", "climb", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("stalagmite", "Formed over thousands of years", "WalkTo", "Examine", 1, 1, 1, 0, "cave large stagamite", i++));
		objects.add(new GameObjectDef("stalagmite", "Formed over thousands of years", "WalkTo", "Examine", 1, 1, 1, 0, "cave small stagamite", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "climb up", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Lever", "Seems to be some sort of winch", "pull", "Examine", 1, 1, 1, 0, "cave lever", i++));
		objects.add(new GameObjectDef("stalactite", "Formed over thousands of years", "WalkTo", "Examine", 0, 1, 1, 0, "cave large stagatite", i++));
		objects.add(new GameObjectDef("stalactite", "Formed over thousands of years", "WalkTo", "Examine", 0, 1, 1, 0, "cave small stagatite", i++));
		objects.add(new GameObjectDef("stalactite", "Formed over thousands of years", "climb", "Examine", 0, 1, 1, 0, "cave extra large stagatite", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "climb up", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "climb up", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "climb up", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "climb up", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Swamp", "That smells horrid", "step over", "Examine", 1, 1, 1, 0, "cave swampbubbles", i++));
		objects.add(new GameObjectDef("Swamp", "That smells horrid", "WalkTo", "Examine", 1, 1, 1, 0, "cave swampbubbles", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Pile of mud", "Mud caved in from above", "climb", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("Travel Cart", "A sturdy cart for travelling in", "Board", "Look", 1, 2, 3, 0, "cart", i++));
		objects.add(new GameObjectDef("Travel Cart", "A sturdy cart for travelling in", "Board", "Look", 1, 2, 3, 0, "cart", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "mine", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("stalactite", "Formed over thousands of years", "WalkTo", "Examine", 0, 1, 1, 0, "cave large stagatite", i++));
		objects.add(new GameObjectDef("Rocks", "You should be able to move these", "clear", "Examine", 1, 2, 2, 0, "cave rocktrap1", i++));
		objects.add(new GameObjectDef("Rocks", "You should be able to move these", "WalkTo", "Examine", 1, 2, 2, 0, "cave rocktrap1a", i++));
		objects.add(new GameObjectDef("Rocks", "You should be able to move these", "WalkTo", "Examine", 1, 1, 3, 0, "cave swamprocks", i++));
		objects.add(new GameObjectDef("sign", "The Paramaya Hostel", "WalkTo", "Examine", 0, 1, 1, 0, "shopsign", i++));
		objects.add(new GameObjectDef("Ladder", "A ladder that leads to the dormitory - a ticket is needed", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("Grill", "They looks suspicious", "walk over", "Examine", 1, 1, 1, 0, "cave grilltrap", i++));
		objects.add(new GameObjectDef("Spiked pit", "They looks suspicious", "walk over", "Examine", 1, 1, 1, 0, "cave grilltrapa", i++));
		objects.add(new GameObjectDef("signpost", "To the Furnace", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("Ship", "A sea faring ship called 'Lady Of The Waves'", "board", "Examine", 0, 5, 3, 0, "Shipfront", i++));
		objects.add(new GameObjectDef("Ship", "A sea faring ship called 'Lady Of The Waves'", "board", "Examine", 0, 5, 3, 0, "Shipback", i++));
		objects.add(new GameObjectDef("Grill", "They looks suspicious", "walk over", "Examine", 1, 1, 1, 0, "cave grilltrap", i++));
		objects.add(new GameObjectDef("sacks", "Yep they're sacks", "WalkTo", "Search", 1, 1, 1, 0, "sacks", i++));
		objects.add(new GameObjectDef("Zamorakian Temple", "Scary!", "WalkTo", "Examine", 0, 1, 1, 0, "cave temple", i++));
		objects.add(new GameObjectDef("Grill", "They looks suspicious", "walk over", "Examine", 1, 1, 1, 0, "cave grilltrap", i++));
		objects.add(new GameObjectDef("Grill", "They looks suspicious", "walk over", "Examine", 1, 1, 1, 0, "cave grilltrap", i++));
		objects.add(new GameObjectDef("Grill", "They looks suspicious", "walk over", "Examine", 1, 1, 1, 0, "cave grilltrap", i++));
		objects.add(new GameObjectDef("Grill", "They looks suspicious", "walk over", "Examine", 1, 1, 1, 0, "cave grilltrap", i++));
		objects.add(new GameObjectDef("Grill", "They looks suspicious", "walk over", "Examine", 1, 1, 1, 0, "cave grilltrap", i++));
		objects.add(new GameObjectDef("Grill", "They looks suspicious", "walk over", "Examine", 1, 1, 1, 0, "cave grilltrap", i++));
		objects.add(new GameObjectDef("Grill", "They looks suspicious", "walk over", "Examine", 1, 1, 1, 0, "cave grilltrap", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "walk here", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "walk here", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Tomb Doors", "Ornately carved wooden doors depicting skeletal warriors", "Open", "Search", 2, 1, 2, 0, "doubledoorsclosed", i++));
		objects.add(new GameObjectDef("Swamp", "That smells horrid", "step over", "Examine", 1, 1, 1, 0, "cave swampbubbles", i++));
		objects.add(new GameObjectDef("Rocks", "You should be able to move these", "clear", "Examine", 1, 2, 1, 0, "cave rocktrap1", i++));
		objects.add(new GameObjectDef("Rocks", "You should be able to move these", "clear", "Examine", 1, 2, 1, 0, "cave rocktrap1", i++));
		objects.add(new GameObjectDef("stalactite", "Formed over thousands of years", "WalkTo", "Examine", 0, 1, 1, 0, "cave extra large stagatite", i++));
		objects.add(new GameObjectDef("stalactite", "Formed over thousands of years", "WalkTo", "Examine", 0, 1, 1, 0, "cave extra large stagatite", i++));
		objects.add(new GameObjectDef("Spiked pit", "They looks suspicious", "walk over", "Examine", 1, 1, 1, 0, "cave grilltrapa up", i++));
		objects.add(new GameObjectDef("Lever", "Seems to be some sort of winch", "pull", "Examine", 1, 1, 1, 0, "cave lever", i++));
		objects.add(new GameObjectDef("Cage", "Seems to be mechanical ", "WalkTo", "Examine", 1, 1, 1, 0, "cave grillcage", i++));
		objects.add(new GameObjectDef("Cage", "Seems to be mechanical ", "WalkTo", "Examine", 1, 1, 1, 0, "cave grillcageup", i++));
		objects.add(new GameObjectDef("Rocks", "More rocks!", "step over", "search for traps", 1, 1, 1, 0, "cave speartrap", i++));
		objects.add(new GameObjectDef("Spear trap", "Ouch!", "WalkTo", "Examine", 1, 1, 1, 0, "cave speartrapa", i++));
		objects.add(new GameObjectDef("Rocks", "More rocks!", "step over", "search", 1, 1, 1, 0, "cave speartrap", i++));
		objects.add(new GameObjectDef("Rocks", "More rocks!", "step over", "search", 1, 1, 1, 0, "cave speartrap", i++));
		objects.add(new GameObjectDef("Rocks", "More rocks!", "step over", "search", 1, 1, 1, 0, "cave speartrap", i++));
		objects.add(new GameObjectDef("Rocks", "More rocks!", "step over", "search", 1, 1, 1, 0, "cave speartrap", i++));
		objects.add(new GameObjectDef("Rocks", "More rocks!", "step over", "search", 1, 1, 1, 0, "cave speartrap", i++));
		objects.add(new GameObjectDef("Rocks", "More rocks!", "step over", "search", 1, 1, 1, 0, "cave speartrap", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "drop down", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Furnace", "Charred bones are slowly burning inside", "WalkTo", "Examine", 1, 1, 1, 0, "cave furnace", i++));
		objects.add(new GameObjectDef("Well", "The remains of a warrior slump over the strange construction", "drop down", "Examine", 1, 1, 1, 0, "cave well", i++));
		objects.add(new GameObjectDef("Passage", "A strange metal grill covers the passage", "walk down", "Examine", 1, 2, 1, 0, "cave tubetrap", i++));
		objects.add(new GameObjectDef("Passage", "The passage way has swung down to a vertical position", "climb up", "Examine", 1, 2, 1, 0, "cave tubetrapa", i++));
		objects.add(new GameObjectDef("Passage", "The passage way has swung down to a vertical position", "climb up rope", "Examine", 1, 2, 1, 0, "cave tubetrapa rope", i++));
		objects.add(new GameObjectDef("stalagmite", "Formed over thousands of years", "search", "Examine", 1, 1, 1, 0, "cave large stagamite", i++));
		objects.add(new GameObjectDef("Rocks", "You should be able to move these", "clear", "search", 1, 2, 2, 0, "cave rocktrap1", i++));
		objects.add(new GameObjectDef("Rocks", "You should be able to move these", "clear", "search", 1, 2, 2, 0, "cave rocktrap1", i++));
		objects.add(new GameObjectDef("Rocks", "You should be able to move these", "clear", "search", 1, 2, 2, 0, "cave rocktrap1", i++));
		objects.add(new GameObjectDef("Rocks", "You should be able to move these", "clear", "search", 1, 2, 2, 0, "cave rocktrap1", i++));
		objects.add(new GameObjectDef("Rocks", "You should be able to move these", "clear", "search", 1, 2, 2, 0, "cave rocktrap1", i++));
		objects.add(new GameObjectDef("Rocks", "You should be able to move these", "clear", "search", 1, 2, 2, 0, "cave rocktrap1", i++));
		objects.add(new GameObjectDef("Passage", "Looks suspicous!", "Walk here", "search", 1, 1, 1, 0, "cave snaptrap", i++));
		objects.add(new GameObjectDef("snap trap", "aaaarghh", "WalkTo", "Examine", 1, 1, 1, 0, "cave snaptrapa", i++));
		objects.add(new GameObjectDef("Wooden planks", "You can walk across these", "WalkTo", "Examine", 1, 1, 1, 0, "cave planks", i++));
		objects.add(new GameObjectDef("Passage", "Looks suspicous!", "Walk here", "search", 1, 1, 1, 0, "cave snaptrap", i++));
		objects.add(new GameObjectDef("Passage", "Looks suspicous!", "Walk here", "search", 1, 1, 1, 0, "cave snaptrap", i++));
		objects.add(new GameObjectDef("Flames of zamorak", "Careful", "search", "Examine", 1, 2, 2, 0, "cave bloodwell", i++));
		objects.add(new GameObjectDef("Platform", "An ancient construction", "WalkTo", "Examine", 1, 1, 1, 0, "cave platform verysmall", i++));
		objects.add(new GameObjectDef("Rock", "Scripture has been carved into the rock", "read", "Examine", 1, 1, 1, 0, "cave carvings", i++));
		objects.add(new GameObjectDef("Rock", "Scripture has been carved into the rock", "read", "Examine", 1, 1, 1, 0, "cave carvings", i++));
		objects.add(new GameObjectDef("Rock", "Scripture has been carved into the rock", "read", "Examine", 1, 1, 1, 0, "cave carvings", i++));
		objects.add(new GameObjectDef("Rock", "Scripture has been carved into the rock", "read", "Examine", 1, 1, 1, 0, "cave carvings", i++));
		objects.add(new GameObjectDef("wall grill", "It seems to filter the rotten air through the caverns", "climb up", "Examine", 1, 1, 1, 0, "cave wallgrill", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to make to the other side", "jump off", "climb up", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("wall grill", "It seems to filter the rotten air through the caverns", "climb up", "Examine", 1, 1, 1, 0, "cave wallgrill", i++));
		objects.add(new GameObjectDef("Dug up soil", "A freshly dug pile of mud", "search", "Examine", 0, 1, 1, 0, "mudpatch", i++));
		objects.add(new GameObjectDef("Dug up soil", "A freshly dug pile of mud", "search", "Examine", 0, 1, 1, 0, "mudpatch", i++));
		objects.add(new GameObjectDef("Pile of mud", "Mud caved in from above", "climb", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("stalagmite", "Formed over thousands of years", "WalkTo", "Examine", 0, 1, 1, 0, "cave small stagamite", i++));
		objects.add(new GameObjectDef("Pile of mud", "Mud and rocks piled up", "climb", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("Pile of mud", "Mud and rocks piled up", "climb", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("Pile of mud", "Mud and rocks piled up", "climb", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("Pile of mud", "Mud and rocks piled up", "climb", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("Pile of mud", "Mud and rocks piled up", "climb", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("Spiked pit", "I don't want to go down there", "WalkTo", "Examine", 0, 1, 1, 0, "spikedpit", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "climb up", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "climb up", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "climb up", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "climb up", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "climb up", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "climb up", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Boulder", "Could be dangerous!", "WalkTo", "Examine", 1, 2, 2, 0, "cave bolder", i++));
		objects.add(new GameObjectDef("crate", "Someone or something has been here before us", "WalkTo", "Search", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("Door", "Spooky!", "open", "Examine", 1, 1, 1, 0, "cave templedoor", i++));
		objects.add(new GameObjectDef("Platform", "An ancient construction", "WalkTo", "Examine", 1, 1, 1, 0, "cave platform small2", i++));
		objects.add(new GameObjectDef("Cage remains", "Poor unicorn!", "WalkTo", "Search", 1, 1, 1, 0, "cave smashedcage", i++));
		objects.add(new GameObjectDef("Ledge", "I might be able to climb that", "climb up", "Examine", 1, 1, 1, 0, "cave ledge", i++));
		objects.add(new GameObjectDef("Passage", "Looks suspicous!", "Walk here", "Examine", 1, 1, 1, 0, "cave snaptrap", i++));
		objects.add(new GameObjectDef("Passage", "Looks suspicous!", "Walk here", "Examine", 1, 1, 1, 0, "cave snaptrap", i++));
		objects.add(new GameObjectDef("Gate of Zamorak", "It doesn't look very inviting", "open", "Examine", 1, 3, 1, 0, "rams skull door", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Bridge support", "An ancient construction", "WalkTo", "Examine", 1, 1, 1, 0, "cave bridge supportbase", i++));
		objects.add(new GameObjectDef("Tomb of Iban", "A clay shrine to lord iban", "Open", "Examine", 1, 1, 2, 96, "hazeeltomb", i++));
		objects.add(new GameObjectDef("Claws of Iban", "claws of iban", "WalkTo", "Examine", 1, 1, 1, 96, "clawsofiban", i++));
		objects.add(new GameObjectDef("barrel", "Its stinks of alcohol", "empty", "Examine", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("Rock", "Scripture has been carved into the rock", "read", "Examine", 1, 1, 1, 0, "cave carvings", i++));
		objects.add(new GameObjectDef("Rocks", "More rocks", "step over", "search for traps", 1, 1, 1, 0, "cave speartrap", i++));
		objects.add(new GameObjectDef("Rocks", "More rocks", "step over", "search for traps", 1, 1, 1, 0, "cave speartrap", i++));
		objects.add(new GameObjectDef("Swamp", "That smells horrid", "WalkTo", "Examine", 1, 1, 1, 0, "cave swampbubbles", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Examine", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Stone bridge", "An ancient stone construction", "WalkTo", "Examine", 0, 1, 1, 0, "bridge section corner", i++));
		objects.add(new GameObjectDef("cage", "That's no way to live", "search", "Examine", 0, 1, 1, 0, "gnomecage", i++));
		objects.add(new GameObjectDef("cage", "That's no way to live", "search", "Examine", 0, 1, 1, 0, "gnomecage", i++));
		objects.add(new GameObjectDef("Stone steps", "They lead into the darkness", "walk down", "Examine", 0, 1, 1, 0, "cave bridge stairs", i++));
		objects.add(new GameObjectDef("Pile of mud", "Mud and rocks piled up", "climb", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "cross", "Examine", 1, 1, 3, 0, "bridge section collapsed", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "jump over", "Examine", 1, 1, 3, 0, "bridge section collapsed2", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "cross", "Examine", 1, 1, 3, 0, "bridge section collapsed", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "jump over", "Examine", 1, 1, 3, 0, "bridge section collapsed2", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "cross", "Examine", 1, 1, 3, 0, "bridge section collapsed", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "jump over", "Examine", 1, 1, 3, 0, "bridge section collapsed2", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "cross", "Examine", 1, 1, 3, 0, "bridge section collapsed", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "jump over", "Examine", 1, 1, 3, 0, "bridge section collapsed2", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "cross", "Examine", 1, 1, 3, 0, "bridge section collapsed", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "jump over", "Examine", 1, 1, 3, 0, "bridge section collapsed2", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "cross", "Examine", 1, 1, 3, 0, "bridge section collapsed", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "jump over", "Examine", 1, 1, 3, 0, "bridge section collapsed2", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "cross", "Examine", 1, 1, 3, 0, "bridge section collapsed", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "jump over", "Examine", 1, 1, 3, 0, "bridge section collapsed2", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "cross", "Examine", 1, 1, 3, 0, "bridge section collapsed", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "jump over", "Examine", 1, 1, 3, 0, "bridge section collapsed2", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "cross", "Examine", 1, 1, 3, 0, "bridge section collapsed", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "jump over", "Examine", 1, 1, 3, 0, "bridge section collapsed2", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "cross", "Examine", 1, 1, 3, 0, "bridge section collapsed", i++));
		objects.add(new GameObjectDef("Stone bridge", "The bridge has partly collapsed", "jump over", "Examine", 1, 1, 3, 0, "bridge section collapsed2", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Pit of the Damned", "The son of zamoracks alter...", "WalkTo", "Examine", 1, 1, 1, 0, "cave temple alter", i++));
		objects.add(new GameObjectDef("Open Door", "Spooky!", "open", "Examine", 1, 1, 1, 0, "cave templedooropen", i++));
		objects.add(new GameObjectDef("signpost", "Observatory reception", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("Stone Gate", "A mystical looking object", "Go through", "Look", 1, 2, 2, 0, "henge", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps there is something inside", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Zodiac", "A map of the twelve signs of the zodiac", "WalkTo", "Examine", 0, 3, 3, 0, "zodiac", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("Stone steps", "They lead into the darkness", "walk down", "Examine", 0, 1, 1, 0, "cave bridge stairs", i++));
		objects.add(new GameObjectDef("Rock", "Scripture has been carved into the rock", "read", "Examine", 1, 1, 1, 0, "cave carvings", i++));
		objects.add(new GameObjectDef("Rock", "Scripture has been carved into the rock", "read", "Examine", 1, 1, 1, 0, "cave carvings", i++));
		objects.add(new GameObjectDef("Rock", "Scripture has been carved into the rock", "read", "Examine", 1, 1, 1, 0, "cave carvings", i++));
		objects.add(new GameObjectDef("Telescope", "A device for viewing the heavens", "Use", "Examine", 1, 1, 1, 0, "telescope", i++));
		objects.add(new GameObjectDef("Gate", "The entrance to the dungeon jail", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("sacks", "These sacks feels lumpy!", "Search", "Examine", 1, 1, 1, 0, "sacks", i++));
		objects.add(new GameObjectDef("Ladder", "the ladder goes down into a dark area", "Climb-Down", "Examine", 1, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("Chest", "All these chests look the same!", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "All these chests look the same!", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Bookcase", "A very roughly constructed bookcase.", "WalkTo", "Search", 1, 1, 2, 0, "bookcase", i++));
		objects.add(new GameObjectDef("Iron Gate", "A well wrought iron gate - it's locked.", "Open", "Search", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Ladder", "the ladder down to the cavern", "Climb-Down", "Examine", 1, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps there is something inside", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "All these chests look the same!", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps there is something inside", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "All these chests look the same!", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Rockslide", "A pile of rocks blocks your path", "WalkTo", "Examine", 1, 1, 1, 0, "rock3", i++));
		objects.add(new GameObjectDef("Altar", "An altar to the evil God Zamorak", "Recharge at", "Examine", 1, 2, 1, 0, "chaosaltar", i++));
		objects.add(new GameObjectDef("column", "Formed over thousands of years", "WalkTo", "Examine", 1, 1, 1, 0, "cave pillar", i++));
		objects.add(new GameObjectDef("Grave of Scorpius", "Here lies Scorpius: dread follower of zamorak", "Read", "Examine", 1, 1, 3, 0, "gravestone1", i++));
		objects.add(new GameObjectDef("Bank Chest", "Allows you to access your bank.", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("dwarf multicannon", "fires metal balls", "fire", "pick up", 0, 1, 1, 0, "dwarf multicannon", i++));
		objects.add(new GameObjectDef("Disturbed sand", "Footprints in the sand show signs of a struggle", "Look", "Search", 0, 1, 1, 0, "sandyfootsteps", i++));
		objects.add(new GameObjectDef("Disturbed sand", "Footprints in the sand show signs of a struggle", "Look", "Search", 0, 1, 1, 0, "sandyfootsteps", i++));
		objects.add(new GameObjectDef("dwarf multicannon base", "bang", "pick up", "Examine", 0, 1, 1, 0, "dwarf multicannon part1", i++));
		objects.add(new GameObjectDef("dwarf multicannon stand", "bang", "pick up", "Examine", 0, 1, 1, 0, "dwarf multicannon part2", i++));
		objects.add(new GameObjectDef("dwarf multicannon barrels", "bang", "pick up", "Examine", 0, 1, 1, 0, "dwarf multicannon part3", i++));
		objects.add(new GameObjectDef("Cave", "I wonder what's inside!", "enter", "Examine", 1, 1, 1, 0, "small caveentrance2", i++));
		objects.add(new GameObjectDef("Cave", "I wonder what's inside!", "enter", "Examine", 1, 1, 1, 0, "small caveentrance2", i++));
		objects.add(new GameObjectDef("fence", "These bridges seem hastily put up", "WalkTo", "Examine", 0, 1, 1, 0, "gnomefence", i++));
		objects.add(new GameObjectDef("signpost", "a signpost", "read", "Examine", 1, 1, 1, 0, "signpost2", i++));
		objects.add(new GameObjectDef("Rocks", "I wonder if I can climb up these", "climb", "Examine", 0, 1, 1, 0, "brownclimbingrocks", i++));
		objects.add(new GameObjectDef("Rocks", "I wonder if I can climb up these", "climb", "Examine", 0, 1, 1, 0, "brownclimbingrocks", i++));
		objects.add(new GameObjectDef("Cave entrance", "A noxious smell emanates from the cave...", "enter", "Examine", 1, 3, 1, 0, "caveentrance2", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wouldn't like to think where the owner is now", "Search", "Close", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Wooden Doors", "Large oak doors constantly watched by guards", "Open", "Watch", 2, 1, 2, 0, "hillsidedoor", i++));
		objects.add(new GameObjectDef("Pedestal", "something fits on here", "WalkTo", "Examine", 1, 1, 1, 96, "stonestand", i++));
		objects.add(new GameObjectDef("bush", "A leafy bush", "Search", "Examine", 1, 1, 1, 0, "bush1", i++));
		objects.add(new GameObjectDef("bush", "A leafy bush", "Search", "Examine", 1, 1, 1, 0, "bush1", i++));
		objects.add(new GameObjectDef("Standard", "A standard with a human skull on it", "WalkTo", "Examine", 1, 1, 1, 0, "ogre standard", i++));
		objects.add(new GameObjectDef("Mining Cave", "A gaping hole that leads to another section of the mine", "enter", "Examine", 1, 3, 1, 0, "caveentrance2", i++));
		objects.add(new GameObjectDef("Mining Cave", "A gaping hole that leads to another section of the mine", "enter", "Examine", 1, 3, 1, 0, "caveentrance2", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "WalkTo", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Lift", "To brings mined rocks to the surface", "Operate", "Examine", 1, 1, 2, 0, "liftwinch", i++));
		objects.add(new GameObjectDef("Mining Barrel", "For loading up mined stone from below ground", "WalkTo", "Search", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("Hole", "I wonder where this leads...", "enter", "Examine", 1, 1, 1, 0, "hole", i++));
		objects.add(new GameObjectDef("Hole", "I wonder where this leads...", "enter", "Examine", 1, 1, 1, 0, "hole", i++));
		objects.add(new GameObjectDef("Cave", "I wonder what's inside!", "enter", "Examine", 1, 1, 1, 0, "small caveentrance2", i++));
		objects.add(new GameObjectDef("Cave", "I wonder what's inside!", "enter", "Examine", 1, 1, 1, 0, "small caveentrance2", i++));
		objects.add(new GameObjectDef("Cave", "I wonder what's inside!", "enter", "Examine", 1, 1, 1, 0, "small caveentrance2", i++));
		objects.add(new GameObjectDef("Counter", "An ogre is selling items here", "Steal from", "Examine", 1, 1, 1, 0, "rockcounter", i++));
		objects.add(new GameObjectDef("Track", "Train track", "Look", "Examine", 1, 2, 2, 0, "trackcurve", i++));
		objects.add(new GameObjectDef("Cave", "I wonder what's inside!", "enter", "Examine", 1, 1, 1, 0, "small caveentrance2", i++));
		objects.add(new GameObjectDef("Mine Cart", "A heavily constructed and often used mining cart.", "Look", "Search", 1, 1, 1, 0, "minecart", i++));
		objects.add(new GameObjectDef("Lift Platform", "A wooden lift that is operated from the surface.", "Use", "Search", 1, 1, 1, 0, "liftbed", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Close", "Examine", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Watch tower", "Constructed by the dwarven black guard", "WalkTo", "Examine", 0, 2, 2, 0, "watchtower", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("Cave entrance", "I wonder what is inside...", "enter", "Examine", 1, 2, 2, 0, "caveentrance", i++));
		objects.add(new GameObjectDef("Pile of mud", "Mud caved in from above", "climb", "Examine", 1, 2, 1, 0, "mudpile", i++));
		objects.add(new GameObjectDef("Cave", "I wonder what's inside!", "enter", "Examine", 1, 1, 1, 0, "small caveentrance2", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading downwards", "Climb-Down", "Examine", 1, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("crate", "A crate", "search", "Examine", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("crate", "A crate", "search", "Examine", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("Gate", "This gate barrs your way into gu'tanoth", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Gate", "This gate barrs your way into gu'tanoth", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("bush", "A leafy bush", "Search", "Examine", 1, 1, 1, 0, "bush1", i++));
		objects.add(new GameObjectDef("bush", "A leafy bush", "Search", "Examine", 1, 1, 1, 0, "bush1", i++));
		objects.add(new GameObjectDef("bush", "A leafy bush", "Search", "Examine", 1, 1, 1, 0, "bush1", i++));
		objects.add(new GameObjectDef("bush", "A leafy bush", "Search", "Examine", 1, 1, 1, 0, "bush1", i++));
		objects.add(new GameObjectDef("multicannon", "fires metal balls", "inspect", "Examine", 1, 1, 1, 0, "dwarf multicannon", i++));
		objects.add(new GameObjectDef("Rocks", "Some rocks are close to the egde", "jump over", "look at", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "Some rocks are close to the edge", "jump over", "look at", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading downwards", "Climb-down", "Examine", 0, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("Cave entrance", "I wonder what is inside...", "enter", "Examine", 1, 1, 1, 0, "small caveentrance2", i++));
		objects.add(new GameObjectDef("Counter", "An ogre is selling cakes here", "Steal from", "Examine", 1, 1, 1, 0, "rock cake counter", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Bookcase", "A large collection of books", "Look", "Search", 1, 1, 2, 0, "bookcase", i++));
		objects.add(new GameObjectDef("Captains Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Experimental Anvil", "An experimental anvil - for developing new techniques in forging", "Use", "Examine", 1, 1, 1, 0, "anvil", i++));
		objects.add(new GameObjectDef("Rocks", "A small pile of stones", "Search", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "Search", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("Column", "Created by ancient mages", "WalkTo", "Examine", 1, 1, 1, 0, "magearena colomn", i++));
		objects.add(new GameObjectDef("Wall", "Created by ancient mages", "WalkTo", "Examine", 1, 1, 1, 0, "magearena wall", i++));
		objects.add(new GameObjectDef("Wall", "Created by ancient mages", "WalkTo", "Examine", 1, 1, 1, 0, "magearena corner", i++));
		objects.add(new GameObjectDef("Wall", "Created by ancient mages", "WalkTo", "Examine", 1, 1, 1, 0, "magearena tallwall", i++));
		objects.add(new GameObjectDef("Wall", "Created by ancient mages", "WalkTo", "Examine", 1, 1, 1, 0, "magearena cornerfill", i++));
		objects.add(new GameObjectDef("Lever", "The lever is up", "pull", "Examine", 0, 1, 1, 0, "leverup", i++));
		objects.add(new GameObjectDef("Lever", "The lever is down", "pull", "Examine", 0, 1, 1, 0, "leverdown", i++));
		objects.add(new GameObjectDef("Wall", "Created by ancient mages", "WalkTo", "Examine", 1, 1, 1, 0, "magearena tallcorner", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading downwards", "Climb-Down", "Examine", 1, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("Wall", "Created by ancient mages", "WalkTo", "Examine", 1, 1, 1, 0, "magearena plain wall", i++));
		objects.add(new GameObjectDef("Gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Gate", "The gate is closed", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("shock", "cosmic energy", "WalkTo", "Examine", 1, 1, 1, 0, "spellshock", i++));
		objects.add(new GameObjectDef("Desk", "A very strong looking table with some locked drawers.", "WalkTo", "Search", 1, 2, 1, 120, "counter", i++));
		objects.add(new GameObjectDef("Cave", "I wonder what's inside!", "enter", "Examine", 1, 1, 1, 0, "small caveentrance2", i++));
		objects.add(new GameObjectDef("Mining Cart", "A sturdy well built mining cart with barrels full of rock on the back.", "WalkTo", "Search", 1, 2, 3, 0, "cart", i++));
		objects.add(new GameObjectDef("Rock of Dalgroth", "A mysterious boulder of the ogres", "mine", "prospect", 1, 2, 2, 0, "cave bolder", i++));
		objects.add(new GameObjectDef("entrance", "Created by ancient mages", "walk through", "Examine", 1, 1, 1, 0, "magearena door", i++));
		objects.add(new GameObjectDef("Dried Cactus", "It looks very spikey", "WalkTo", "Examine", 1, 1, 1, 0, "cactuswatered", i++));
		objects.add(new GameObjectDef("climbing rocks", "I wonder if I can climb up these", "climb", "Examine", 0, 1, 1, 0, "climbing_rocks", i++));
		objects.add(new GameObjectDef("Rocks", "Strange rocks - who knows why they're wanted?", "Mine", "Prospect", 1, 1, 1, 0, "tinrock1", i++));
		objects.add(new GameObjectDef("lightning", "blimey!", "WalkTo", "Examine", 1, 1, 1, 0, "lightning1", i++));
		objects.add(new GameObjectDef("Crude Desk", "A very roughly constructed desk", "WalkTo", "Search", 1, 2, 1, 120, "counter", i++));
		objects.add(new GameObjectDef("Heavy Metal Gate", "This is an immense and very heavy looking gate made out of thick wrought metal", "Look", "Push", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Counter", "An ogre is selling cakes here", "Steal from", "Examine", 1, 1, 1, 0, "rock cake counter", i++));
		objects.add(new GameObjectDef("Crude bed", "A flea infested sleeping experience", "rest", "Examine", 1, 1, 2, 0, "poorbed", i++));
		objects.add(new GameObjectDef("flames", "looks hot!", "WalkTo", "Examine", 1, 1, 1, 0, "firespell1", i++));
		objects.add(new GameObjectDef("Carved Rock", "An ornately carved rock with a pointed recepticle", "WalkTo", "Search", 1, 1, 1, 120, "cave small stagamite", i++));
		objects.add(new GameObjectDef("USE", "FREE SLOT PLEASE USE", "WalkTo", "Examine", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("crate", "A crate used for storing materials", "search", "Examine", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("crate", "A crate used for storing materials", "search", "Examine", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("barrel", "Its shut", "search", "Examine", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("Brick", "A stone brick", "WalkTo", "Examine", 1, 1, 1, 0, "1-1light", i++));
		objects.add(new GameObjectDef("Brick", "A stone brick", "WalkTo", "Examine", 1, 1, 1, 0, "1-1dark", i++));
		objects.add(new GameObjectDef("Brick", "A stone brick", "WalkTo", "Examine", 1, 3, 1, 0, "1-3light", i++));
		objects.add(new GameObjectDef("Brick", "A stone brick", "WalkTo", "Examine", 1, 3, 1, 0, "1-3dark", i++));
		objects.add(new GameObjectDef("Brick", "A stone brick", "WalkTo", "Examine", 1, 2, 2, 0, "2-2light", i++));
		objects.add(new GameObjectDef("Brick", "A stone brick", "WalkTo", "Examine", 1, 1, 1, 0, "2-2dark", i++));
		objects.add(new GameObjectDef("Barrier", "this section is roped off", "WalkTo", "Examine", 1, 1, 1, 0, "barrier1", i++));
		objects.add(new GameObjectDef("buried skeleton", "I hope I don't meet any of these", "search", "Examine", 1, 1, 1, 0, "halfburiedskeleton", i++));
		objects.add(new GameObjectDef("Brick", "A stone brick", "WalkTo", "Examine", 1, 1, 1, 0, "2-1light", i++));
		objects.add(new GameObjectDef("Brick", "A stone brick", "WalkTo", "Examine", 1, 1, 1, 0, "2-1light", i++));
		objects.add(new GameObjectDef("Specimen tray", "A pile of sifted earth", "WalkTo", "Search", 1, 2, 2, 0, "compost", i++));
		objects.add(new GameObjectDef("winch", "This winches earth from the dig hole", "Operate", "Examine", 1, 1, 2, 0, "liftwinch", i++));
		objects.add(new GameObjectDef("crate", "A crate", "search", "Examine", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("crate", "A crate", "search", "Examine", 1, 1, 1, 0, "crate", i++));
		objects.add(new GameObjectDef("Urn", "A large ornamental urn", "WalkTo", "Examine", 1, 1, 1, 0, "largeurn", i++));
		objects.add(new GameObjectDef("buried skeleton", "I'm glad this isn't around now", "search", "Examine", 1, 1, 1, 0, "halfburiedskeleton2", i++));
		objects.add(new GameObjectDef("panning point", "a shallow where I can pan for gold", "look", "Examine", 0, 1, 1, 0, "fishing", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "WalkTo", "Examine", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("signpost", "a signpost", "read", "Examine", 1, 1, 1, 0, "signpost2", i++));
		objects.add(new GameObjectDef("signpost", "a signpost", "read", "Examine", 1, 1, 1, 0, "signpost2", i++));
		objects.add(new GameObjectDef("signpost", "a signpost", "read", "Examine", 1, 1, 1, 0, "signpost2", i++));
		objects.add(new GameObjectDef("signpost", "a signpost", "read", "Examine", 1, 1, 1, 0, "signpost2", i++));
		objects.add(new GameObjectDef("signpost", "Digsite educational centre", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("soil", "soil", "search", "Examine", 0, 1, 1, 0, "dugupsoil1", i++));
		objects.add(new GameObjectDef("soil", "soil", "search", "Examine", 0, 1, 1, 0, "dugupsoil2", i++));
		objects.add(new GameObjectDef("soil", "soil", "search", "Examine", 0, 1, 1, 0, "dugupsoil3", i++));
		objects.add(new GameObjectDef("Gate", "The gate has closed", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("ship", "The ship is sinking", "WalkTo", "Examine", 2, 1, 2, 0, "sinkingshipfront", i++));
		objects.add(new GameObjectDef("barrel", "The ship is sinking", "climb on", "Examine", 2, 1, 2, 0, "sinkingbarrel", i++));
		objects.add(new GameObjectDef("Leak", "The ship is sinking", "fill", "Examine", 0, 1, 1, 0, "shipleak", i++));
		objects.add(new GameObjectDef("bush", "A leafy bush", "search", "Examine", 1, 1, 1, 0, "bush1", i++));
		objects.add(new GameObjectDef("bush", "A leafy bush", "search", "Examine", 1, 1, 1, 0, "bush1", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("sacks", "Yep they're sacks", "search", "Examine", 1, 1, 1, 0, "sacks", i++));
		objects.add(new GameObjectDef("sacks", "Yep they're sacks", "search", "Examine", 1, 1, 1, 0, "sacks", i++));
		objects.add(new GameObjectDef("Leak", "The ship is sinking", "fill", "Examine", 0, 1, 1, 0, "shipleak2", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "search", "Examine", 1, 1, 2, 0, "cupboardopen", i++));
		objects.add(new GameObjectDef("Wrought Mithril Gates", "Magnificent wrought mithril gates giving access to the Legends Guild", "open", "Search", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Legends Hall Doors", "Solid Oak doors leading to the Hall of Legends", "Open", "Search", 2, 1, 2, 0, "doubledoorsclosed", i++));
		objects.add(new GameObjectDef("Camp bed", "Not comfortable but useful nonetheless", "WalkTo", "Examine", 1, 1, 2, 0, "poorbed", i++));
		objects.add(new GameObjectDef("barrel", "It has a lid on it - I need something to lever it off", "WalkTo", "Examine", 1, 1, 1, 0, "barrelredcross", i++));
		objects.add(new GameObjectDef("barrel", "I wonder what is inside...", "search", "Examine", 1, 1, 1, 0, "barrelredcross", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside...", "Open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Dense Jungle Tree", "Thick vegetation", "Chop", "Examine", 1, 1, 1, 0, "jungle medium size plant", i++));
		objects.add(new GameObjectDef("Jungle tree stump", "A chopped down jungle tree", "Walk", "Examine", 1, 1, 1, 0, "treestump", i++));
		objects.add(new GameObjectDef("signpost", "To the digsite", "WalkTo", "Examine", 1, 1, 1, 0, "signpost", i++));
		objects.add(new GameObjectDef("gate", "You can pass through this on the members server", "open", "Examine", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Bookcase", "A large collection of books", "search", "Examine", 1, 1, 2, 0, "bookcase", i++));
		objects.add(new GameObjectDef("Dense Jungle Tree", "An exotic looking tree", "Chop", "Examine", 1, 1, 1, 0, "jungle tree 2", i++));
		objects.add(new GameObjectDef("Dense Jungle Tree", "An exotic looking tree", "Chop", "Examine", 1, 1, 1, 0, "jungle tree 1", i++));
		objects.add(new GameObjectDef("Spray", "There's a strong wind", "WalkTo", "Examine", 1, 1, 1, 0, "shipspray1", i++));
		objects.add(new GameObjectDef("Spray", "There's a strong wind", "WalkTo", "Examine", 1, 1, 1, 0, "shipspray2", i++));
		objects.add(new GameObjectDef("winch", "This winches earth from the dig hole", "Operate", "Examine", 1, 1, 2, 0, "liftwinch", i++));
		objects.add(new GameObjectDef("Brick", "It seems these were put here deliberately", "search", "Examine", 1, 1, 1, 0, "1-1light", i++));
		objects.add(new GameObjectDef("Rope", "it's a rope leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ropeforclimbingbot", i++));
		objects.add(new GameObjectDef("Rope", "it's a rope leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ropeforclimbingbot", i++));
		objects.add(new GameObjectDef("Dense Jungle Palm", "A hardy palm tree with dense wood", "Chop", "Examine", 1, 1, 1, 0, "palm2", i++));
		objects.add(new GameObjectDef("Dense Jungle Palm", "A hardy palm tree with dense wood", "Chop", "Examine", 1, 1, 1, 0, "palm", i++));
		objects.add(new GameObjectDef("Trawler net", "A huge net to catch little fish", "inspect", "Examine", 1, 1, 1, 0, "trawlernet-l", i++));
		objects.add(new GameObjectDef("Trawler net", "A huge net to catch little fish", "inspect", "Examine", 1, 1, 1, 0, "trawlernet-r", i++));
		objects.add(new GameObjectDef("Brick", "The bricks are covered in the strange compound", "WalkTo", "Examine", 1, 1, 1, 0, "1-1light", i++));
		objects.add(new GameObjectDef("Chest", "I wonder what is inside ?", "open", "Examine", 1, 1, 1, 0, "ChestClosed", i++));
		objects.add(new GameObjectDef("Chest", "Perhaps I should search it", "Search", "Close", 1, 1, 1, 0, "ChestOpen", i++));
		objects.add(new GameObjectDef("Trawler catch", "Smells like fish!", "Search", "Examine", 1, 1, 1, 0, "trawlernet", i++));
		objects.add(new GameObjectDef("Yommi Tree", "An adolescent rare and mystical looking tree in", "WalkTo", "Examine", 1, 2, 2, 0, "totemtree1", i++));
		objects.add(new GameObjectDef("Grown Yommi Tree", "A fully grown rare and mystical looking tree", "WalkTo", "Examine", 1, 2, 2, 0, "totemtree2", i++));
		objects.add(new GameObjectDef("Chopped Yommi Tree", "A mystical looking tree that has recently been felled", "WalkTo", "Examine", 1, 2, 2, 0, "totemtree3", i++));
		objects.add(new GameObjectDef("Trimmed Yommi Tree", "The trunk of the yommi tree.", "WalkTo", "Examine", 1, 2, 2, 0, "totemtree4", i++));
		objects.add(new GameObjectDef("Totem Pole", "A nicely crafted wooden totem pole.", "Lift", "Examine", 1, 2, 2, 0, "totemtree5", i++));
		objects.add(new GameObjectDef("Baby Yommi Tree", "A baby Yommi tree - with a mystical aura", "WalkTo", "Examine", 1, 2, 2, 0, "smallfern", i++));
		objects.add(new GameObjectDef("Fertile earth", "A very fertile patch of earth", "WalkTo", "Examine", 0, 2, 2, 0, "dugupsoil1", i++));
		objects.add(new GameObjectDef("Rock Hewn Stairs", "steps cut out of the living rock", "Climb", "Examine", 1, 2, 3, 0, "rocksteps", i++));
		objects.add(new GameObjectDef("Hanging rope", "A rope hangs from the ceiling", "WalkTo", "Examine", 1, 1, 1, 0, "ropeladder", i++));
		objects.add(new GameObjectDef("Rocks", "A large boulder blocking the stream", "Move", "Examine", 1, 2, 2, 0, "cave bolder", i++));
		objects.add(new GameObjectDef("Boulder", "A large boulder blocking the way", "WalkTo", "Smash to pieces", 1, 2, 2, 0, "cave bolder", i++));
		objects.add(new GameObjectDef("dwarf multicannon", "fires metal balls", "fire", "pick up", 1, 1, 1, 0, "dwarf multicannon", i++));
		objects.add(new GameObjectDef("dwarf multicannon base", "bang", "pick up", "Examine", 1, 1, 1, 0, "dwarf multicannon part1", i++));
		objects.add(new GameObjectDef("dwarf multicannon stand", "bang", "pick up", "Examine", 1, 1, 1, 0, "dwarf multicannon part2", i++));
		objects.add(new GameObjectDef("dwarf multicannon barrels", "bang", "pick up", "Examine", 1, 1, 1, 0, "dwarf multicannon part3", i++));
		objects.add(new GameObjectDef("rock", "A rocky outcrop", "climb over", "Examine", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Rock Hewn Stairs", "steps cut out of the living rock", "Climb", "Examine", 1, 2, 3, 0, "rocksteps", i++));
		objects.add(new GameObjectDef("Rock Hewn Stairs", "steps cut out of the living rock", "Climb", "Examine", 1, 2, 3, 0, "rocksteps", i++));
		objects.add(new GameObjectDef("Rock Hewn Stairs", "steps cut out of the living rock", "Climb", "Examine", 1, 2, 3, 0, "rocksteps", i++));
		objects.add(new GameObjectDef("Compost Heap", "The family gardeners' compost heap", "WalkTo", "Investigate", 1, 2, 2, 0, "compost", i++));
		objects.add(new GameObjectDef("beehive", "An old looking beehive", "WalkTo", "Investigate", 1, 1, 1, 0, "beehive", i++));
		objects.add(new GameObjectDef("Drain", "This drainpipe runs from the kitchen to the sewers", "WalkTo", "Investigate", 0, 1, 1, 0, "pipe&drain", i++));
		objects.add(new GameObjectDef("web", "An old thick spider's web", "WalkTo", "Investigate", 0, 1, 1, 0, "floorweb", i++));
		objects.add(new GameObjectDef("fountain", "There seems to be a lot of insects here", "WalkTo", "Investigate", 1, 2, 2, 0, "fountain", i++));
		objects.add(new GameObjectDef("Sinclair Crest", "The Sinclair family crest", "WalkTo", "Investigate", 0, 1, 1, 0, "wallshield", i++));
		objects.add(new GameObjectDef("barrel", "Annas stuff - There seems to be something shiny at the bottom", "WalkTo", "Search", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("barrel", "Bobs things - There seems to be something shiny at the bottom", "WalkTo", "Search", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("barrel", "Carols belongings - there seems to be something shiny at the bottom", "WalkTo", "Search", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("barrel", "Davids equipment - there seems to be something shiny at the bottom", "WalkTo", "Search", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("barrel", "Elizabeths clothes - theres something shiny at the bottom", "WalkTo", "Search", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("barrel", "Franks barrel seems to have something shiny at the bottom", "WalkTo", "Search", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("Flour Barrel", "Its full of flour", "WalkTo", "Take From", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("sacks", "Full of various gardening tools", "WalkTo", "investigate", 1, 1, 1, 0, "sacks", i++));
		objects.add(new GameObjectDef("gate", "A sturdy and secure wooden gate", "WalkTo", "Investigate", 2, 1, 2, 0, "woodengateclosed", i++));
		objects.add(new GameObjectDef("Dead Yommi Tree", "A dead Yommi Tree - it looks like a tough axe will be needed to fell this", "WalkTo", "Inspect", 1, 2, 2, 0, "deadtree2", i++));
		objects.add(new GameObjectDef("clawspell", "forces of guthix", "WalkTo", "Examine", 1, 1, 1, 0, "clawspell1", i++));
		objects.add(new GameObjectDef("Rocks", "The remains of a large rock", "WalkTo", "Examine", 1, 2, 2, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("crate", "A crate of some kind", "WalkTo", "Search", 1, 1, 1, 70, "crate", i++));
		objects.add(new GameObjectDef("Cavernous Opening", "A dark and mysterious cavern", "enter", "search", 1, 3, 1, 0, "caveentrance2", i++));
		objects.add(new GameObjectDef("Ancient Lava Furnace", "A badly damaged furnace fueled by red hot Lava - it looks ancient", "Look", "Search", 1, 2, 2, 0, "furnace", i++));
		objects.add(new GameObjectDef("Spellcharge", "forces of guthix", "WalkTo", "Examine", 1, 1, 1, 0, "spellcharge1", i++));
		objects.add(new GameObjectDef("Rocks", "A small rocky outcrop", "WalkTo", "Search", 1, 1, 1, 0, "cave rock1", i++));
		objects.add(new GameObjectDef("cupboard", "The cupboard is shut", "open", "Examine", 1, 1, 2, 0, "cupboard", i++));
		objects.add(new GameObjectDef("sacks", "Yep they're sacks", "search", "Examine", 1, 1, 1, 0, "sacks", i++));
		objects.add(new GameObjectDef("Rock", "A rocky outcrop", "WalkTo", "Search", 1, 1, 1, 0, "rocks1", i++));
		objects.add(new GameObjectDef("Saradomin stone", "A faith stone", "chant to", "Examine", 1, 1, 1, 0, "saradominstone", i++));
		objects.add(new GameObjectDef("Guthix stone", "A faith stone", "chant to", "Examine", 1, 1, 1, 0, "guthixstone", i++));
		objects.add(new GameObjectDef("Zamorak stone", "A faith stone", "chant to", "Examine", 1, 1, 1, 0, "zamorakstone", i++));
		objects.add(new GameObjectDef("Magical pool", "A cosmic portal", "step into", "Examine", 1, 2, 2, 0, "rockpool", i++));
		objects.add(new GameObjectDef("Wooden Beam", "Some sort of support - perhaps used with ropes to lower people over the hole", "WalkTo", "Search", 0, 1, 1, 0, "Scaffoldsupport", i++));
		objects.add(new GameObjectDef("Rope down into darkness", "A scarey downwards trip into possible doom.", "WalkTo", "Use", 0, 1, 1, 0, "ScaffoldsupportRope", i++));
		objects.add(new GameObjectDef("Cave entrance", "A dark cave entrance leading to the surface.", "Enter", "Examine", 1, 3, 1, 0, "caveentrance2", i++));
		objects.add(new GameObjectDef("Cave entrance", "A small tunnel that leads to a large room beyond.", "enter", "Examine", 1, 2, 2, 0, "Shamancave", i++));
		objects.add(new GameObjectDef("Ancient Wooden Doors", "The doors are locked shut", "Open", "Pick Lock", 2, 1, 2, 0, "doubledoorsclosed", i++));
		objects.add(new GameObjectDef("Table", "An old rickety table", "WalkTo", "search", 1, 1, 1, 96, "table", i++));
		objects.add(new GameObjectDef("Crude bed", "Barely a bed at all", "Rest", "Search", 1, 1, 2, 0, "poorbed", i++));
		objects.add(new GameObjectDef("Tall Reeds", "A tall plant with a tube for a stem.", "WalkTo", "Search", 0, 1, 1, 0, "bullrushes", i++));
		objects.add(new GameObjectDef("Goblin foot prints", "They seem to be heading south east", "WalkTo", "Examine", 0, 1, 1, 0, "sandyfootsteps", i++));
		objects.add(new GameObjectDef("Dark Metal Gate", "A dark metalic gate which seems to be fused with the rock", "Open", "Search", 2, 1, 2, 0, "metalgateclosed", i++));
		objects.add(new GameObjectDef("Magical pool", "A cosmic portal", "step into", "Examine", 1, 2, 2, 0, "rockpool", i++));
		objects.add(new GameObjectDef("Rope Up", "A welcome rope back up and out of this dark place.", "Climb", "Examine", 0, 1, 1, 0, "obstical_ropeswing", i++));
		objects.add(new GameObjectDef("Half buried remains", "Some poor unfortunate soul", "WalkTo", "Search", 1, 1, 1, 0, "skeletonwithbag", i++));
		objects.add(new GameObjectDef("Totem Pole", "A carved and decorated totem pole", "Look", "Examine", 1, 1, 1, 0, "totemtreeevil", i++));
		objects.add(new GameObjectDef("Totem Pole", "A carved and decorated totem pole", "Look", "Examine", 1, 1, 1, 0, "totemtreegood", i++));
		objects.add(new GameObjectDef("Comfy bed", "Its a bed - wow", "rest", "Examine", 1, 2, 2, 0, "bed", i++));
		objects.add(new GameObjectDef("Rotten Yommi Tree", "A decomposing fully grown Yommi Tree", "WalkTo", "Inspect", 1, 2, 2, 0, "totemtreerotten2", i++));
		objects.add(new GameObjectDef("Rotten Yommi Tree", "A decomposing felled Yommi Tree", "WalkTo", "Inspect", 1, 2, 2, 0, "totemtreerotten3", i++));
		objects.add(new GameObjectDef("Rotten Yommi Tree", "A decomposing Yommi Tree Trunk", "WalkTo", "Inspect", 1, 2, 2, 0, "totemtreerotten4", i++));
		objects.add(new GameObjectDef("Rotten Totem Pole", "A decomposing Totem Pole", "WalkTo", "Inspect", 1, 2, 2, 0, "totemtreerotten5", i++));
		objects.add(new GameObjectDef("Leafy Palm Tree", "A shady palm tree", "WalkTo", "Shake", 1, 1, 1, 0, "palm", i++));
		objects.add(new GameObjectDef("Grand Viziers Desk", "A very elegant desk - you could knock it to get the Grand Viziers attention.", "WalkTo", "Knock on table", 1, 2, 1, 120, "counter", i++));
		objects.add(new GameObjectDef("Strange Barrel", "It might have something inside of it.", "Smash", "Examine", 1, 1, 1, 0, "barrel", i++));
		objects.add(new GameObjectDef("ship", "A sturdy sailing ship", "WalkTo", "Examine", 0, 5, 3, 0, "Shipfront", i++));
		objects.add(new GameObjectDef("ship", "A sturdy sailing ship", "WalkTo", "Examine", 0, 2, 3, 0, "Shipmiddle", i++));
		objects.add(new GameObjectDef("ship", "A sturdy sailing ship", "WalkTo", "Examine", 0, 5, 3, 0, "Shipback", i++));
		objects.add(new GameObjectDef("digsite bed", "Not comfortable but useful nonetheless", "sleep", "Examine", 1, 1, 2, 0, "poorbed", i++));
		objects.add(new GameObjectDef("Tea stall", "A stall selling oriental infusions", "WalkTo", "Steal from", 1, 2, 2, 112, "market", i++));
		objects.add(new GameObjectDef("Boulder", "A large boulder blocking the way", "WalkTo", "Smash to pieces", 1, 2, 2, 0, "cave bolder", i++));
		objects.add(new GameObjectDef("Boulder", "A large boulder blocking the way", "WalkTo", "Smash to pieces", 1, 2, 2, 0, "cave bolder", i++));
		objects.add(new GameObjectDef("Damaged Earth", "Disturbed earth - it will heal itself in time", "WalkTo", "Examine", 0, 1, 1, 0, "dugupsoil1", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading upwards", "Climb-Up", "Examine", 1, 1, 1, 0, "ladder", i++));
		objects.add(new GameObjectDef("Ladder", "it's a ladder leading downwards", "Climb-Down", "Examine", 1, 1, 1, 0, "ladderdown", i++));
		objects.add(new GameObjectDef("Vine", "A creepy creeper", "Grab", "Examine", 0, 1, 1, 0, "vinejunction", i++));

		if (Config.S_PROPER_MAGIC_TREE_NAME) {
			objects.get(310).name = "Magic Tree";
		}
	}

	public static void load(boolean loadMembers) {
		loadNpcDefinitionsA();
		loadNpcDefinitionsB();
		loadItemDefinitions();
		loadTextureDefinitions();
		loadAnimationDefinitions();
		loadSpellDefinitions();
		loadPrayerDefinitions();
		loadTileDefinitions();
		loadDoorDefinitions();
		loadElevationDefinitions();
		loadGameObjectDefinitionsA();
		loadGameObjectDefinitionsB();
		loadProjectiles();
		loadGUIParts();
		loadCrowns();
		if (!Config.S_WANT_CUSTOM_SPRITES) {
			for (ItemDef item : items) {
				if (item.getAuthenticSpriteID() + 1 > invPictureCount) {
					invPictureCount = item.getAuthenticSpriteID() + 1;
				}
				if (item.membersItem && !loadMembers) {
					item.name = "Members object";
					item.description = "You need to be a member to use this object";
					item.basePrice = 0;
					item.command = "";
					item.wieldable = false;
					item.wearableID = 0;
					item.quest = true;
				}
			}
		}

		for (GameObjectDef object : objects) {
			object.modelID = storeModel(object
					.getObjectModel());
		}

	}

	/*private static void dumpItems() throws IOException {
		FileWriter fWriter =  new FileWriter(new File("dump.txt"));
		BufferedWriter buffer = new BufferedWriter(fWriter);
		for(int i = 0; i < items.size();i++) {
			buffer.write("{");
			buffer.newLine();
			ItemDef def = items.get(i);
			buffer.write(" \"id\": " + i + ",");
			buffer.newLine();
			buffer.write(" \"name\": \""+ def.getName() + (def.getNotedFormOf() >= 0 ? "(Noted)" : "") +"\",");
			buffer.newLine();
			buffer.write(" \"stackable\": " + (def.isStackable() ? "true" : "false"));
			buffer.newLine();
			buffer.write("},");
			buffer.newLine();
		}
		buffer.close();
		fWriter.close();
	}*/


	public static int storeModel(String name) {
		if (name.equalsIgnoreCase("na")) {
			return 0;
		}
		int index = models.indexOf(name);
		if (index < 0) {
			models.add(name);
			return models.size() - 1;
		}
		return index;
	}
}
