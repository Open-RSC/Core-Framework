package org.openrsc.client.entityhandling;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openrsc.client.Config;

import org.openrsc.client.Resources;
import org.openrsc.client.entityhandling.defs.DoorDef;
import org.openrsc.client.entityhandling.defs.ElevationDef;
import org.openrsc.client.entityhandling.defs.GameObjectDef;
import org.openrsc.client.entityhandling.defs.ItemDef;
import org.openrsc.client.entityhandling.defs.NPCDef;
import org.openrsc.client.entityhandling.defs.PrayerDef;
import org.openrsc.client.entityhandling.defs.SpellDef;
import org.openrsc.client.entityhandling.defs.TileDef;
import org.openrsc.client.entityhandling.defs.extras.AnimationDef;
import org.openrsc.client.entityhandling.defs.extras.TextureDef;
import org.openrsc.client.util.Pair;

public final class EntityHandler {

    private static ArrayList<TextureDef> textures = new ArrayList<TextureDef>();
    private static ArrayList<AnimationDef> animations = new ArrayList<AnimationDef>();
    private static ArrayList<SpellDef> spells = new ArrayList<SpellDef>();
    private static ArrayList<PrayerDef> prayers = new ArrayList<PrayerDef>();
    private static ArrayList<TileDef> tiles = new ArrayList<TileDef>();
    private static ArrayList<DoorDef> doors = new ArrayList<DoorDef>();
    private static ArrayList<ElevationDef> elevation = new ArrayList<ElevationDef>();
    private static ArrayList<String> models = new ArrayList<String>();
	
    private static NPCDef[] npcs = new NPCDef[0];
    private static HashMap<Integer, ItemDef> items = new HashMap<Integer, ItemDef>();
    private static GameObjectDef[] objects = new GameObjectDef[0];
    
    private static int invPictureCount = 0;
    
	public static ArrayList<Pair<ItemDef, Integer>> searchItems(String search) {
		search = search.toLowerCase();
		ArrayList<Pair<ItemDef, Integer>> matches = new ArrayList<Pair<ItemDef, Integer>>();
		
		try {
			Pattern pattern = Pattern.compile("^" + search);
			for (Map.Entry<Integer, ItemDef> entry : items.entrySet()) {
				ItemDef def = entry.getValue();
				if (!def.tradable)
					continue;
				Matcher matcher = pattern.matcher(def.getName().toLowerCase());
				if (matcher.find()) {
					matches.add(new Pair<ItemDef, Integer>(def, entry.getKey()));
				}
			}
			Collections.sort(matches, new Comparator<Pair<ItemDef, Integer>>() {
				@Override
				public int compare(Pair<ItemDef, Integer> o1, Pair<ItemDef, Integer> o2) {
					return o1.getFirst().getName().compareToIgnoreCase(o2.getFirst().getName());
				}
			});
		} catch (Exception e) {
			
		}
		return matches;
	}
	
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
        return npcs.length;
    }

    public static NPCDef getNpcDef(int id) {
        if (id < 0 || id >= npcs.length) {
            return null;
        }
        return npcs[id];
    }

    public static int itemCount() {
        return items.size();
    }

    public static ItemDef getItemDef(int id) {
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
        	System.out.println("Bad Tile ID");
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
        return objects.length;
    }

    public static GameObjectDef getObjectDef(int id) {
        if (id < 0 || id >= objects.length) {
            return null;
        }
        return objects[id];
    }

	private static void loadPrayerDefinitions() {
		prayers.add(new PrayerDef(1, 15, "Thick skin", "Increases your defense by 5%"));
		prayers.add(new PrayerDef(4, 15, "Burst of strength", "Increases your strength by 5%"));
		prayers.add(new PrayerDef(7, 15, "Clarity of thought", "Increases your attack by 5%"));
		prayers.add(new PrayerDef(10, 30, "Rock skin", "Increases your defense by 10%"));
		prayers.add(new PrayerDef(13, 30, "Superhuman strength", "Increases your strength by 10%"));
		prayers.add(new PrayerDef(16, 30, "Improved reflexes", "Increases your attack by 10%"));
		prayers.add(new PrayerDef(19, 5, "Rapid restore", "2x restore rate for all stats except hits"));
		prayers.add(new PrayerDef(22, 10, "Rapid heal", "2x restore rate for hitpoints stat"));
		prayers.add(new PrayerDef(25, 10, "Protect items", "Keep 1 extra item if you die"));
		prayers.add(new PrayerDef(28, 60, "Steel skin", "Increases your defense by 15%"));
		prayers.add(new PrayerDef(31, 60, "Ultimate strength", "Increases your strength by 15%"));
		prayers.add(new PrayerDef(34, 60, "Incredible reflexes", "Increases your attack by 15%"));
		prayers.add(new PrayerDef(37, 60, "Paralyze monster", "Stops monsters from fighting back"));
		prayers.add(new PrayerDef(40, 60, "Protect from missiles", "100% protection from ranged attacks"));
	}
	
	private static void loadTileDefinitions() { //GOOD
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
	
	private static void loadElevationDefinitions() { //GOOD
		elevation.add(new ElevationDef(64, 6));
		elevation.add(new ElevationDef(64, 3));
		elevation.add(new ElevationDef(96, 2));
		elevation.add(new ElevationDef(80, 33));
		elevation.add(new ElevationDef(80, 15));
		elevation.add(new ElevationDef(90, 49));
	}
	
	private static void loadTextureDefinitions() {
		textures.add(new TextureDef("wall","door"));
		textures.add(new TextureDef("water", ""));
		textures.add(new TextureDef("wall", ""));
		 textures.add(new TextureDef("planks", ""));
		 textures.add(new TextureDef("wall", "doorway"));
		 textures.add(new TextureDef("wall","window"));
		 textures.add(new TextureDef("roof",""));
		 textures.add(new TextureDef("wall","arrowslit"));
		 textures.add(new TextureDef("leafytree",""));
		 textures.add(new TextureDef("treestump",""));
		 textures.add(new TextureDef("fence",""));
		 textures.add(new TextureDef("mossy",""));
		 textures.add(new TextureDef("railings",""));
		 textures.add(new TextureDef("painting1",""));
		 textures.add(new TextureDef("painting2",""));
		textures.add(new TextureDef("marble",""));
		textures.add(new TextureDef("deadtree",""));
		textures.add(new TextureDef("fountain",""));
		 textures.add(new TextureDef("wall","stainedglass"));
		 textures.add(new TextureDef("target",""));
		 textures.add(new TextureDef("books",""));
		 textures.add(new TextureDef("timbered",""));
		textures.add(new TextureDef("timbered","timberwindow"));
		textures.add(new TextureDef("mossybricks",""));
		textures.add(new TextureDef("growingwheat",""));
		textures.add(new TextureDef("gungywater",""));
		textures.add(new TextureDef("web",""));
		textures.add(new TextureDef("wall","desertwindow"));
		textures.add(new TextureDef("wall","crumbled"));
		textures.add(new TextureDef("cavern",""));
		textures.add(new TextureDef("cavern2",""));
		textures.add(new TextureDef("lava",""));
		textures.add(new TextureDef("pentagram",""));
		textures.add(new TextureDef("mapletree",""));
		textures.add(new TextureDef("yewtree",""));
		textures.add(new TextureDef("helmet",""));
		textures.add(new TextureDef("canvas","tentbottom"));
		textures.add(new TextureDef("Chainmail2",""));
		textures.add(new TextureDef("mummy",""));
		textures.add(new TextureDef("jungleleaf",""));
		textures.add(new TextureDef("jungleleaf3",""));
		textures.add(new TextureDef("jungleleaf4",""));
		textures.add(new TextureDef("jungleleaf5",""));
		textures.add(new TextureDef("jungleleaf6",""));
		textures.add(new TextureDef("mossybricks","arrowslit"));
		textures.add(new TextureDef("planks","window"));
		textures.add(new TextureDef("planks","junglewindow"));
		textures.add(new TextureDef("cargonet",""));
		textures.add(new TextureDef("bark",""));
		textures.add(new TextureDef("canvas",""));
		textures.add(new TextureDef("canvas","tentdoor"));
		textures.add(new TextureDef("wall","lowcrumbled"));
		textures.add(new TextureDef("cavern","crumbled"));
		textures.add(new TextureDef("cavern2","crumbled"));
		textures.add(new TextureDef("lava","flames"));
	}

	private static void loadAnimationDefinitions() { //GOOD
		animations.add(new AnimationDef("head1", 1, 13, true, false, 0));  
		animations.add(new AnimationDef("body1", 2, 6, true, false, 0));  
		animations.add(new AnimationDef("legs1", 3, 15, true, false, 0));  
		animations.add(new AnimationDef("fhead1", 1, 13, true, false, 0));  
		animations.add(new AnimationDef("fbody1", 2, 10, true, false, 0));  
		animations.add(new AnimationDef("head2", 1, 13, true, false, 0));  
		animations.add(new AnimationDef("head3", 1, 5, true, false, 0));  
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
		animations.add(new AnimationDef("partyhat", 65535, 0, true, false, 0));  
		animations.add(new AnimationDef("dragon", 16711680, 0, true, false, 0));  
		animations.add(new AnimationDef("hatchet", 16737817, 0, true, false, 0));  
		animations.add(new AnimationDef("hatchet", 15654365, 0, true, false, 0));  
		animations.add(new AnimationDef("hatchet", 15658734, 0, true, false, 0));  
		animations.add(new AnimationDef("hatchet", 10072780, 0, true, false, 0));  
		animations.add(new AnimationDef("hatchet", 11717785, 0, true, false, 0));  
		animations.add(new AnimationDef("hatchet", 65535, 0, true, false, 0));  
		animations.add(new AnimationDef("hatchet", 3158064, 0, true, false, 0));  
}
	
	
	@SuppressWarnings("unchecked")
	private static void loadSpellDefinitions() {
		HashMap<Integer, Integer> runes = new HashMap<Integer, Integer>();
		runes.put(35, 1);
		runes.put(33, 1);
		spells.add(new SpellDef("Wind strike", "A strength 1 missile attack", 1, 2, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 2);
		runes.put(32, 3);
		runes.put(36, 1);
		spells.add(new SpellDef("Confuse", "Reduces your opponents attack by 5%", 3, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(35, 1);
		runes.put(32, 1);
		runes.put(33, 1);
		spells.add(new SpellDef("Water Strike", "A strength 2 missile attack", 5, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(32, 1);
		runes.put(46, 1);
		spells.add(new SpellDef("Enchant lvl-1 amulet", "For use on sapphire amulets", 7, 3, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 2);
		runes.put(35, 1);
		runes.put(33, 1);
		spells.add(new SpellDef("Earth Strike", "A strength 3 missile attack", 9, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 2);
		runes.put(32, 3);
		runes.put(36, 1);
		spells.add(new SpellDef("Weaken", "Reduces your opponents strength by 5%", 11, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(35, 1);
		runes.put(33, 2);
		runes.put(31, 3);
		spells.add(new SpellDef("Fire Strike", "A strength 4 missile attack", 13, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 2);
		runes.put(32, 2);
		runes.put(40, 1);
		spells.add(new SpellDef("Bones to bananas", "Changes all held bones into bananas!", 15, 0, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 2);
		runes.put(41, 1);
		spells.add(new SpellDef("Wind Bolt", "A strength 5 missile attack", 17, 2, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 3);
		runes.put(32, 2);
		runes.put(36, 1);
		spells.add(new SpellDef("Curse", "Reduces your opponents defense by 5%", 19, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(40, 1);
		runes.put(31, 3);
		spells.add(new SpellDef("Low level alchemy", "Converts an item into gold", 21, 3, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(32, 2);
		runes.put(33, 2);
		runes.put(41, 1);
		spells.add(new SpellDef("Water bolt", "A strength 6 missle attack", 23, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 3);
		runes.put(42, 1);
		runes.put(31, 1);
		spells.add(new SpellDef("Varrock teleport", "Teleports you to Varrock", 25, 0, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 3);
		runes.put(46, 1);
		spells.add(new SpellDef("Enchant lvl-2 amulet", "For use on emerald amulets", 27, 3, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 3);
		runes.put(33, 2);
		runes.put(41, 1);
		spells.add(new SpellDef("Earth bolt", "A strength 7 missile attack", 29, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 1);
		runes.put(33, 3);
		runes.put(42, 1);
		spells.add(new SpellDef("Lumbridge teleport", "Teleports you to Lumbridge", 31, 0, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 1);
		runes.put(42, 1);
		spells.add(new SpellDef("Telekinetic grab", "Take an item that you can see but can't reach", 33, 3, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 3);
		runes.put(41, 1);
		runes.put(31, 4);
		spells.add(new SpellDef("Fire bolt", "A strength 8 missile attack", 35, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(32, 1);
		runes.put(33, 3);
		runes.put(42, 1);
		spells.add(new SpellDef("Falador teleport", "Teleports you to Falador", 37, 0, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 2);
		runes.put(33, 2);
		runes.put(41, 1);
		spells.add(new SpellDef("Crumble undead", "Hits skeletons, ghosts, & zombies hard!", 39, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 3);
		runes.put(38, 1);
		spells.add(new SpellDef("Wind blast", "A strength 9 missile attack", 41, 2, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(40, 1);
		runes.put(31, 4);
		spells.add(new SpellDef("Superheat item", "Smelt 1 ore without a furnace", 43, 3, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 5);
		runes.put(42, 1);
		spells.add(new SpellDef("Camelot teleport", "Teleports you to Camelot", 45, 0, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(32, 3);
		runes.put(33, 3);
		runes.put(38, 1);
		spells.add(new SpellDef("Water blast", "A strength 10 missile attack", 47, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(46, 1);
		runes.put(31, 5);
		spells.add(new SpellDef("Enchant lvl-3 amulet", "For use on ruby amulets", 49, 3, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(38, 1);
		runes.put(31, 5);
		spells.add(new SpellDef("Iban blast", "A strength 25 missile attack!", 50, 2, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(32, 2);
		runes.put(42, 2);
		spells.add(new SpellDef("Ardougne teleport", "Teleports you to Ardougne", 51, 0, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 4);
		runes.put(33, 3);
		runes.put(38, 1);
		spells.add(new SpellDef("Earth blast", "A strength 11 missile attack", 53, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(40, 1);
		runes.put(31, 5);
		spells.add(new SpellDef("High level alchemy", "Convert an item into more gold", 55, 3, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(611, 1);
		runes.put(32, 30);
		runes.put(46, 3);
		spells.add(new SpellDef("Charge Water Orb", "Needs to be cast on a water obelisk", 56, 5, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 10);
		runes.put(46, 1);
		spells.add(new SpellDef("Enchant lvl-4 amulet", "For use on diamond amulets", 57, 3, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 2);
		runes.put(42, 2);
		spells.add(new SpellDef("Watchtower teleport", "Teleports you to the watchtower", 58, 0, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 4);
		runes.put(38, 1);
		runes.put(31, 5);
		spells.add(new SpellDef("Fire blast", "A strength 12 missile attack", 59, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 4);
		runes.put(619, 2);
		runes.put(31, 1);
		spells.add(new SpellDef("Claws of Guthix", "Summons the power of Guthix", 60, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 4);
		runes.put(619, 2);
		runes.put(31, 2);
		spells.add(new SpellDef("Saradomin strike", "Summons the power of Saradomin", 60, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 1);
		runes.put(619, 2);
		runes.put(31, 4);
		spells.add(new SpellDef("Flames of Zamorak", "Summons the power of Zamorak", 60, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 30);
		runes.put(611, 1);
		runes.put(46, 3);
		spells.add(new SpellDef("Charge earth Orb", "Needs to be cast on an earth obelisk", 60, 5, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(31, 2);
		runes.put(42, 2);
		spells.add(new SpellDef("Lost City Teleport", "Teleports you to the Lost City", 61, 0, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 5);
		runes.put(619, 1);
		spells.add(new SpellDef("Wind wave", "A strength 13 missile attack", 62, 2, 2, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(611, 1);
		runes.put(46, 3);
		runes.put(31, 30);
		spells.add(new SpellDef("Charge Fire Orb", "Needs to be cast on a fire obelisk", 63, 5, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(32, 7);
		runes.put(33, 5);
		runes.put(619, 1);
		spells.add(new SpellDef("Water wave", "A strength 14 missile attack", 65, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(611, 1);
		runes.put(33, 30);
		runes.put(46, 3);
		spells.add(new SpellDef("Charge air Orb", "Needs to be cast on an air obelisk", 66, 5, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 5);
		runes.put(32, 5);
		runes.put(825, 1);
		spells.add(new SpellDef("Vulnerability", "Reduces your opponents defense by 10%", 66, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 15);
		runes.put(32, 15);
		runes.put(46, 1);
		spells.add(new SpellDef("Enchant lvl-5 amulet", "For use on dragonstone amulets", 68, 3, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 7);
		runes.put(33, 5);
		runes.put(619, 1);
		spells.add(new SpellDef("Earth wave", "A strength 15 missile attack", 70, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 8);
		runes.put(32, 8);
		runes.put(825, 1);
		spells.add(new SpellDef("Enfeeble", "Reduces your opponents strength by 10%", 73, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 5);
		runes.put(619, 1);
		runes.put(31, 7);
		spells.add(new SpellDef("Fire wave", "A strength 16 missile atack", 75, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(34, 12);
		runes.put(32, 12);
		runes.put(825, 1);
		spells.add(new SpellDef("Stun", "Reduces your opponents attack by 10%", 80, 2, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes.clear();
		runes.put(33, 3);
		runes.put(619, 3);
		runes.put(31, 3);
		spells.add(new SpellDef("Charge", "Increases your mage arena spells damage", 80, 0, 3, (HashMap<Integer, Integer>)runes.clone()));
		runes = null;
	}

	private static void loadDoorDefinitions() {
		int i = 0;
		doors.add(new DoorDef("Wall", "", "WalkTo", "Examine", 1, 0, 192, 2, 2, i++));
		doors.add(new DoorDef("Doorframe", "", "WalkTo", "Close", 0, 1, 192, 4, 4, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Window", "", "WalkTo", "Examine", 1, 0, 192, 5, 5, i++));
		doors.add(new DoorDef("Fence", "", "WalkTo", "Examine", 1, 0, 192, 10, 10, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "Examine", 1, 0, 192, 12, 12, i++));
		doors.add(new DoorDef("Stained glass window", "", "WalkTo", "Examine", 1, 0, 192, 18, 18, i++));
		doors.add(new DoorDef("Highwall", "", "WalkTo", "Examine", 1, 0, 275, 2, 2, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 275, 0, 0, i++));
		doors.add(new DoorDef("Doorframe", "", "WalkTo", "Close", 0, 1, 275, 4, 4, i++));
		doors.add(new DoorDef("battlement", "", "WalkTo", "Examine", 1, 0, 70, 2, 2, i++));
		doors.add(new DoorDef("Doorframe", "", "WalkTo", "Examine", 1, 0, 192, 4, 4, i++));
		doors.add(new DoorDef("snowwall", "", "WalkTo", "Examine", 1, 0, 192, -31711, -31711, i++));
		doors.add(new DoorDef("arrowslit", "", "WalkTo", "Examine", 1, 0, 192, 7, 7, i++));
		doors.add(new DoorDef("timberwall", "", "WalkTo", "Examine", 1, 0, 192, 21, 21, i++));
		doors.add(new DoorDef("timberwindow", "", "WalkTo", "Examine", 1, 0, 192, 22, 22, i++));
		doors.add(new DoorDef("blank", "", "WalkTo", "Examine", 0, 0, 192, 12345678, 12345678, i++));
		doors.add(new DoorDef("highblank", "", "WalkTo", "Examine", 0, 0, 275, 12345678, 12345678, i++));
		doors.add(new DoorDef("mossybricks", "", "WalkTo", "Examine", 1, 0, 192, 23, 23, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Odd looking wall", "This wall doesn't look quite right", "Push", "Examine", 1, 1, 192, 2, 2, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("web", "A spider's web", "WalkTo", "Examine", 1, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Window", "", "WalkTo", "Examine", 1, 0, 192, 27, 27, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Crumbled", "", "WalkTo", "Examine", 1, 0, 192, 28, 28, i++));
		doors.add(new DoorDef("Cavern", "", "WalkTo", "Examine", 1, 0, 192, 29, 29, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("cavern2", "", "WalkTo", "Examine", 1, 0, 192, 30, 30, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Wall", "", "WalkTo", "Examine", 1, 0, 192, 3, 3, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Strange looking wall", "This wall doesn't look quite right", "Push", "Examine", 1, 1, 192, 29, 29, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("memberrailings", "", "WalkTo", "Examine", 1, 0, 192, 12, 12, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Magic Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Strange Panel", "This wall doesn't look quite right", "Push", "Examine", 1, 1, 192, 21, 21, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("blockblank", "", "WalkTo", "Examine", 1, 0, 192, 12345678, 12345678, i++));
		doors.add(new DoorDef("unusual looking wall", "This wall doesn't look quite right", "Push", "Examine", 1, 1, 192, 2, 2, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick lock", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick lock", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick lock", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick lock", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick lock", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick Lock", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick lock", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Fence with loose pannels", "I wonder if I could get through this", "push", "Examine", 1, 1, 192, 10, 10, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("rat cage", "The rat's have damaged the bars", "search", "Examine", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("arrowslit", "", "WalkTo", "Examine", 1, 0, 192, 44, 44, i++));
		doors.add(new DoorDef("solidblank", "", "WalkTo", "Examine", 1, 0, 192, 12345678, 12345678, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("loose panel", "The panel has worn with age", "break", "Examine", 1, 1, 192, 3, 3, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("plankswindow", "", "WalkTo", "Examine", 1, 0, 192, 45, 45, i++));
		doors.add(new DoorDef("Low Fence", "", "WalkTo", "Examine", 1, 0, 96, 10, 10, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Cooking pot", "Smells good!", "WalkTo", "Examine", 1, 1, 96, 10, 10, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("plankstimber", "", "WalkTo", "Examine", 1, 0, 192, 46, 46, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("magic portal", "", "enter", "Examine", 1, 1, 192, 17, 17, i++));
		doors.add(new DoorDef("magic portal", "", "enter", "Examine", 1, 1, 192, 17, 17, i++));
		doors.add(new DoorDef("magic portal", "", "enter", "Examine", 1, 1, 192, 17, 17, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Cavern wall", "It looks as if it is covered in some fungus.", "WalkTo", "search", 1, 1, 192, 29, 29, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "the door is shut", "walk through", "Examine", 1, 1, 192, 3, 3, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "walk through", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Pick Lock", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Low wall", "a low wall", "", "Examine", 1, 1, 70, 2, 2, i++));
		doors.add(new DoorDef("Low wall", "a low wall", "", "Examine", 1, 1, 70, 2, 2, i++));
		doors.add(new DoorDef("Blacksmiths Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("railings", "", "pick lock", "Examine", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "pick lock", "Examine", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "pick lock", "search", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "pick lock", "search", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "pick lock", "search", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "look through", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "knock on", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Doorframe", "", "WalkTo", "Close", 1, 1, 192, 4, 4, i++));
		doors.add(new DoorDef("Tent", "", "WalkTo", "Examine", 1, 0, 192, 36, 36, i++));
		doors.add(new DoorDef("Jail Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Jail Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Window", "A barred window", "WalkTo", "Search", 1, 1, 192, 27, 27, i++));
		doors.add(new DoorDef("magic portal", "A magical barrier shimmers with power", "WalkTo", "Examine", 1, 1, 192, 17, 17, i++));
		doors.add(new DoorDef("Jail Door", "A solid iron gate", "Open", "Examine", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("Cave exit", "The way out", "Leave", "Examine", 0, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("Cave exit", "The way out", "Leave", "Examine", 0, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("Cave exit", "The way out", "Leave", "Examine", 0, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("Cave exit", "The way out", "Leave", "Examine", 0, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("Cave exit", "The way out", "Leave", "Examine", 0, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("Cave exit", "The way out", "Leave", "Examine", 0, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("railings", "", "WalkTo", "search", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("battlement", "This is blocking your path", "Climb-over", "Examine", 1, 1, 70, 2, 2, i++));
		doors.add(new DoorDef("Tent Door", "An entrance into the tent", "Go through", "Examine", 1, 1, 192, 50, 50, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Tent Door", "An entrance into the tent", "Go through", "Examine", 1, 1, 192, 50, 50, i++));
		doors.add(new DoorDef("Low Fence", "A damaged wooden fence", "search", "Examine", 1, 1, 96, 10, 10, i++));
		doors.add(new DoorDef("Sturdy Iron Gate", "A solid iron gate", "Open", "Examine", 1, 1, 192, 12, 12, i++));
		doors.add(new DoorDef("battlement", "this low wall blocks your path", "climb over", "Examine", 1, 1, 70, 2, 2, i++));
		doors.add(new DoorDef("Water", "My waterfall boundary!", "WalkTo", "Examine", 1, 0, 192, 25, 25, i++));
		doors.add(new DoorDef("Wheat", "Test Boundary!", "WalkTo", "Examine", 1, 0, 192, 24, 24, i++));
		doors.add(new DoorDef("Jungle", "Thick inpenetrable jungle", "Chop", "Examine", 1, 1, 192, 8, 8, i++));
		doors.add(new DoorDef("Window", "you can see a vicious looking guard dog right outside", "Investigate", "Examine", 1, 1, 192, 5, 5, i++));
		doors.add(new DoorDef("Rut", "Looks like a small rut carved into the ground.", "WalkTo", "Search", 1, 0, 96, 51, 51, i++));
		doors.add(new DoorDef("Crumbled Cavern 1", "", "WalkTo", "Examine", 0, 0, 192, 52, 52, i++));
		doors.add(new DoorDef("Crumbled Cavern 2", "", "WalkTo", "Examine", 0, 0, 192, 53, 53, i++));
		doors.add(new DoorDef("cavernhole", "", "WalkTo", "Examine", 1, 0, 192, 54, 54, i++));
		doors.add(new DoorDef("flamewall", "A supernatural fire of incredible intensity", "Touch", "Investigate", 1, 1, 192, 54, 54, i++));
		doors.add(new DoorDef("Ruined wall", "Some ancient wall structure - it doesn't look too high.", "WalkTo", "Jump", 1, 1, 192, 28, 28, i++));
		doors.add(new DoorDef("Ancient Wall", "An ancient - slightly higher wall with some strange markings on it", "Use", "Search", 1, 1, 275, 2, 2, i++));
		doors.add(new DoorDef("Door", "The door is shut", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "This door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "This door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "This door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "This door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("web", "A spider's web", "WalkTo", "Examine", 1, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("web", "A spider's web", "WalkTo", "Examine", 1, 1, 192, 26, 26, i++));
		doors.add(new DoorDef("Anti-Nigger Forcefield", "A magical barrier that screams white power", "WalkTo", "Examine", 1, 1, 192, 17, 17, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
		doors.add(new DoorDef("Door", "The door is locked", "Open", "Examine", 1, 1, 192, 0, 0, i++));
	}
	
	private static void loadGameObjects() {
		try {
			DataInputStream in = new DataInputStream(Resources.load("/objects.dat"));
			objects = new GameObjectDef[in.readInt()];
			String[] strs = new String[5];
			int[] ints = new int[4];
			
			for (int i = 0; i < objects.length; i++) {
				for (int j = 0; j < strs.length; j++) {
					int len = in.readByte();
					byte[] str = new byte[len];
					in.readFully(str);
					strs[j] = new String(str);
				}
				for (int j = 0; j < ints.length; j++) {
					ints[j] = in.readInt();
				}
				objects[i] = new GameObjectDef(strs[0], strs[1], strs[3], strs[4],
						ints[0], ints[1], ints[2],
						ints[3], strs[2], i);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void loadItems() { 
		try {
			DataInputStream in = new DataInputStream(Resources.load("/items.dat"));
			//items = new ItemDef[in.readInt()];
			String[] strs = new String[3];
			int[] ints = new int[4];
			boolean[] bools = new boolean[3];
            int numberItems = in.readInt();
			
			for (int i = 0; i < numberItems; i++) {
				for (int j = 0; j < strs.length; j++) {
					int len = in.readByte();
					byte[] str = new byte[len];
					in.readFully(str);
					strs[j] = new String(str);
				}
				for (int j = 0; j < ints.length; j++) {
					ints[j] = in.readInt();
				}
				for (int j = 0; j < bools.length; j++) {
					bools[j] = in.readBoolean();
				}
				items.put(
                    i,
                    new ItemDef(strs[0], strs[1], strs[2],
                                ints[0], ints[1], ints[2], bools[0], bools[1],
                                ints[3], bools[2], i)
                );
			}
            
            HashMap<Integer, ItemDef> notedItems = new HashMap<Integer, ItemDef>();
            for (Map.Entry<Integer, ItemDef> entry : items.entrySet()) {
				ItemDef item = entry.getValue();
				if(!item.isStackable())  {
                    int newID = entry.getKey()+Config.NOTE_ITEM_ID_BASE;
                    notedItems.put(newID, new ItemDef(item,newID));
				}
			}
            items.putAll(notedItems);
            
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadNpcs() { 
		try {
			DataInputStream in = new DataInputStream(Resources.load("/npcs.dat"));
			npcs = new NPCDef[in.readInt()];
			String[] strs = new String[3];
			int[] ints = new int[13];
			boolean[] bools = new boolean[1];
			int[][] inta = new int[1][];
			
			for (int i = 0; i < npcs.length; i++) {
				for (int j = 0; j < strs.length; j++) {
					int len = in.readByte();
					byte[] str = new byte[len];
					in.readFully(str);
					strs[j] = new String(str);
				}
				for (int j = 0; j < ints.length; j++) {
					ints[j] = in.readInt();
				}
				for (int j = 0; j < bools.length; j++) {
					bools[j] = in.readBoolean();
				}
				for (int j = 0; j < inta.length; j++) {
					int len = in.readInt();
					inta[j] = new int[len];
					for (int k = 0; k < len; k++)
						inta[j][k] = in.readInt();
				}

				npcs[i] = new NPCDef(strs[0], strs[1], strs[2],
						ints[0], ints[1], ints[2], ints[3],
						bools[0], inta[0], ints[4], ints[5],
						ints[6], ints[7], ints[8], ints[9],
						ints[10], ints[11], ints[12], i);

			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean loaded = false;
	
	public static void load() {
		if(!loaded)
		{
	        loadTextureDefinitions();
	        loadAnimationDefinitions();
	        loadSpellDefinitions();
			loadPrayerDefinitions();
	        loadTileDefinitions();
	        loadDoorDefinitions();
	        loadElevationDefinitions();

	        loadNpcs();
	        loadItems();
	        loadGameObjects();
            for (Map.Entry<Integer, ItemDef> entry : items.entrySet()) {
	            if (entry.getValue().getSprite() + 1 > invPictureCount) {
	                invPictureCount = entry.getValue().getSprite() + 1;
	            }
	        }

	        for (int id = 0; id < objects.length; id++) {
	            objects[id].modelID = storeModel(objects[id].getObjectModel());
	        }
	        loaded = true;
		}
    }

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
