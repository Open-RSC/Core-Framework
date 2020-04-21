package com.openrsc.server.plugins.minigames.fishingtrawler;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class TrawlerCatch implements OpLocTrigger {

	private static final int TRAWLER_CATCH = 1106;
	private static final int[] JUNK_ITEMS = new int[]{
		ItemId.OLD_BOOT.id(),
		ItemId.DAMAGED_ARMOUR_1.id(),
		ItemId.DAMAGED_ARMOUR_2.id(),
		ItemId.RUSTY_SWORD.id(),
		ItemId.BROKEN_ARROW.id(),
		ItemId.BUTTONS.id(),
		ItemId.BROKEN_STAFF.id(),
		ItemId.VASE.id(),
		ItemId.CERAMIC_REMAINS.id(),
		ItemId.BROKEN_GLASS_DIGSITE_LVL_2.id(), // Broken glass
		ItemId.EDIBLE_SEAWEED.id(),
		ItemId.OYSTER.id()
	};

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return obj.getID() == TRAWLER_CATCH;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == TRAWLER_CATCH) {
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 3, "you search the smelly net");
			thinkbubble(p, new Item(ItemId.NET.id()));
			if (p.getCache().hasKey("fishing_trawler_reward")) {
				p.message("you find...");
				int fishCaught = p.getCache().getInt("fishing_trawler_reward");
				boolean isFishRoll;
				for (int fishGiven = 0; fishGiven < fishCaught; fishGiven++) {
					isFishRoll = DataConversions.random(0,1) == 1;
					// roll for a fish
					if (isFishRoll) {
						if (catchFish(81, p.getSkills().getLevel(Skills.FISHING))) {
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..a manta ray!");
							give(p, ItemId.RAW_MANTA_RAY.id(), 1);
							p.incExp(Skills.FISHING, 460, false);
						} else if (catchFish(79, p.getSkills().getLevel(Skills.FISHING))) {
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..a sea turtle!");
							give(p, ItemId.RAW_SEA_TURTLE.id(), 1);
							p.incExp(Skills.FISHING, 380, false);
						} else if (catchFish(76, p.getSkills().getLevel(Skills.FISHING))) {
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..a shark!");
							give(p, ItemId.RAW_SHARK.id(), 1);
							p.incExp(Skills.FISHING, 440, false);
						} else if (catchFish(50, p.getSkills().getLevel(Skills.FISHING))) {
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..a sword fish");
							give(p, ItemId.RAW_SWORDFISH.id(), 1);
							p.incExp(Skills.FISHING, 400, false);
						} else if (catchFish(40, p.getSkills().getLevel(Skills.FISHING))) {
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..a lobster");
							give(p, ItemId.RAW_LOBSTER.id(), 1);
							p.incExp(Skills.FISHING, 360, false);
						} else if (catchFish(30, p.getSkills().getLevel(Skills.FISHING))) {
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..some tuna");
							give(p, ItemId.RAW_TUNA.id(), 1);
							p.incExp(Skills.FISHING, 320, false);
						} else if (catchFish(15, p.getSkills().getLevel(Skills.FISHING))) {
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..some anchovies");
							give(p, ItemId.RAW_ANCHOVIES.id(), 1);
							p.incExp(Skills.FISHING, 160, false);
						} else if (catchFish(5, p.getSkills().getLevel(Skills.FISHING))) {
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..a sardine");
							give(p, ItemId.RAW_SARDINE.id(), 1);
							p.incExp(Skills.FISHING, 80, false);
						} else {
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..some shrimp");
							give(p, ItemId.RAW_SHRIMP.id(), 1);
							p.incExp(Skills.FISHING, 40, false);
						}
					}
					 else {
						int randomJunkItem = JUNK_ITEMS[DataConversions.random(0, JUNK_ITEMS.length - 1)];
						if (randomJunkItem == ItemId.EDIBLE_SEAWEED.id()) { // Edible seaweed
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..some seaweed");
							give(p, ItemId.EDIBLE_SEAWEED.id(), 1);
							p.incExp(Skills.FISHING, 20, false);
						} else if (randomJunkItem == ItemId.OYSTER.id()) { // Oyster
							mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..an oyster!");
							give(p, ItemId.OYSTER.id(), 1);
							p.incExp(Skills.FISHING, 40, false);
						} else {
							// Broken glass, buttons, damaged armour, ceramic remains
							if (randomJunkItem == ItemId.BROKEN_GLASS_DIGSITE_LVL_2.id() || randomJunkItem == ItemId.BUTTONS.id()
								|| randomJunkItem == ItemId.DAMAGED_ARMOUR_1.id() || randomJunkItem == ItemId.DAMAGED_ARMOUR_2.id()
								|| randomJunkItem == ItemId.CERAMIC_REMAINS.id()) {
								mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..some " + p.getWorld().getServer().getEntityHandler().getItemDef(randomJunkItem).getName());
							}
							// Old boot
							else if (randomJunkItem == ItemId.OLD_BOOT.id()) {
								mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..an " + p.getWorld().getServer().getEntityHandler().getItemDef(randomJunkItem).getName());
							}
							// broken arrow, broken staff, Rusty sword, vase
							else {
								mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "..a " + p.getWorld().getServer().getEntityHandler().getItemDef(randomJunkItem).getName());
							}
							give(p, randomJunkItem, 1);
							p.incExp(Skills.FISHING, 5, false);
						}
					}
				}
				p.getCache().remove("fishing_trawler_reward");
				p.message("that's the lot");
			} else {
				p.message("the smelly net is empty");
			}
		}
	}

	private boolean catchFish(int levelReq, int level) {
		return Formulae.calcGatheringSuccessful(levelReq, level, 18);
	}

}
