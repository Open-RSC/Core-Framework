package com.openrsc.server.plugins.authentic.minigames.fishingtrawler;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
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
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == TRAWLER_CATCH;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == TRAWLER_CATCH) {
			mes("you search the smelly net");
			delay(3);
			thinkbubble(new Item(ItemId.NET.id()));
			if (player.getCache().hasKey("fishing_trawler_reward")) {
				player.message("you find...");
				int fishCaught = player.getCache().getInt("fishing_trawler_reward");

				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.FISHING_CAPE.id())) {
					fishCaught *= 1.5;
				}

				boolean isFishRoll;
				for (int fishGiven = 0; fishGiven < fishCaught; fishGiven++) {
					isFishRoll = DataConversions.random(0,1) == 1;
					// roll for a fish
					if (isFishRoll) {
						if (catchFish(81, player.getSkills().getLevel(Skill.FISHING.id()))) {
							mes("..a manta ray!");
							delay(2);
							give(player, ItemId.RAW_MANTA_RAY.id(), 1);
							player.incExp(Skill.FISHING.id(), 460, false);
						} else if (catchFish(79, player.getSkills().getLevel(Skill.FISHING.id()))) {
							mes("..a sea turtle!");
							delay(2);
							give(player, ItemId.RAW_SEA_TURTLE.id(), 1);
							player.incExp(Skill.FISHING.id(), 380, false);
						} else if (catchFish(76, player.getSkills().getLevel(Skill.FISHING.id()))) {
							mes("..a shark!");
							delay(2);
							give(player, ItemId.RAW_SHARK.id(), 1);
							player.incExp(Skill.FISHING.id(), 440, false);
						} else if (catchFish(50, player.getSkills().getLevel(Skill.FISHING.id()))) {
							mes("..a sword fish");
							delay(2);
							give(player, ItemId.RAW_SWORDFISH.id(), 1);
							player.incExp(Skill.FISHING.id(), 400, false);
						} else if (catchFish(40, player.getSkills().getLevel(Skill.FISHING.id()))) {
							mes("..a lobster");
							delay(2);
							give(player, ItemId.RAW_LOBSTER.id(), 1);
							player.incExp(Skill.FISHING.id(), 360, false);
						} else if (catchFish(30, player.getSkills().getLevel(Skill.FISHING.id()))) {
							mes("..some tuna");
							delay(2);
							give(player, ItemId.RAW_TUNA.id(), 1);
							player.incExp(Skill.FISHING.id(), 320, false);
						} else if (catchFish(15, player.getSkills().getLevel(Skill.FISHING.id()))) {
							mes("..some anchovies");
							delay(2);
							give(player, ItemId.RAW_ANCHOVIES.id(), 1);
							player.incExp(Skill.FISHING.id(), 160, false);
						} else if (catchFish(5, player.getSkills().getLevel(Skill.FISHING.id()))) {
							mes("..a sardine");
							delay(2);
							give(player, ItemId.RAW_SARDINE.id(), 1);
							player.incExp(Skill.FISHING.id(), 80, false);
						} else {
							mes("..some shrimp");
							delay(2);
							give(player, ItemId.RAW_SHRIMP.id(), 1);
							player.incExp(Skill.FISHING.id(), 40, false);
						}
					}
					 else {
						int randomJunkItem = JUNK_ITEMS[DataConversions.random(0, JUNK_ITEMS.length - 1)];
						if (randomJunkItem == ItemId.EDIBLE_SEAWEED.id()) { // Edible seaweed
							mes("..some seaweed");
							delay(2);
							give(player, ItemId.EDIBLE_SEAWEED.id(), 1);
							player.incExp(Skill.FISHING.id(), 20, false);
						} else if (randomJunkItem == ItemId.OYSTER.id()) { // Oyster
							mes("..an oyster!");
							delay(2);
							give(player, ItemId.OYSTER.id(), 1);
							player.incExp(Skill.FISHING.id(), 40, false);
						} else {
							// Broken glass, buttons, damaged armour, ceramic remains
							if (randomJunkItem == ItemId.BROKEN_GLASS_DIGSITE_LVL_2.id() || randomJunkItem == ItemId.BUTTONS.id()
								|| randomJunkItem == ItemId.DAMAGED_ARMOUR_1.id() || randomJunkItem == ItemId.DAMAGED_ARMOUR_2.id()
								|| randomJunkItem == ItemId.CERAMIC_REMAINS.id()) {
								mes("..some " + player.getWorld().getServer().getEntityHandler().getItemDef(randomJunkItem).getName());
								delay(2);
							}
							// Old boot
							else if (randomJunkItem == ItemId.OLD_BOOT.id()) {
								mes("..an " + player.getWorld().getServer().getEntityHandler().getItemDef(randomJunkItem).getName());
								delay(2);
							}
							// broken arrow, broken staff, Rusty sword, vase
							else {
								mes("..a " + player.getWorld().getServer().getEntityHandler().getItemDef(randomJunkItem).getName());
								delay(2);
							}
							give(player, randomJunkItem, 1);
							player.incExp(Skill.FISHING.id(), 5, false);
						}
					}
				}
				player.getCache().remove("fishing_trawler_reward");
				player.message("that's the lot");
			} else {
				player.message("the smelly net is empty");
			}
		}
	}

	private boolean catchFish(int levelReq, int level) {
		return Formulae.calcGatheringSuccessfulLegacy(levelReq, level, 18);
	}

}
