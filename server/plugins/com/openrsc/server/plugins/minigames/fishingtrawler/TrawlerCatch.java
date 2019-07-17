package com.openrsc.server.plugins.minigames.fishingtrawler;

import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class TrawlerCatch implements ObjectActionListener, ObjectActionExecutiveListener {

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
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == TRAWLER_CATCH;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == TRAWLER_CATCH) {
			message(p, 1900, "you search the smelly net");
			showBubble(p, new Item(ItemId.NET.id()));
			if (p.getCache().hasKey("fishing_trawler_reward")) {
				p.message("you find...");
				int fishCaught = p.getCache().getInt("fishing_trawler_reward");
				boolean isFishRoll;
				for (int fishGiven = 0; fishGiven < fishCaught; fishGiven++) {
					isFishRoll = DataConversions.random(0,1) == 1;
					// roll for a fish
					if (isFishRoll) {
						if (catchFish(81, p.getSkills().getLevel(SKILLS.FISHING.id()))) {
							message(p, 1200, "..a manta ray!");
							addItem(p, ItemId.RAW_MANTA_RAY.id(), 1);
							p.incExp(SKILLS.FISHING.id(), 460, false);
						} else if (catchFish(79, p.getSkills().getLevel(SKILLS.FISHING.id()))) {
							message(p, 1200, "..a sea turtle!");
							addItem(p, ItemId.RAW_SEA_TURTLE.id(), 1);
							p.incExp(SKILLS.FISHING.id(), 380, false);
						} else if (catchFish(76, p.getSkills().getLevel(SKILLS.FISHING.id()))) {
							message(p, 1200, "..a shark!");
							addItem(p, ItemId.RAW_SHARK.id(), 1);
							p.incExp(SKILLS.FISHING.id(), 440, false);
						} else if (catchFish(50, p.getSkills().getLevel(SKILLS.FISHING.id()))) {
							message(p, 1200, "..a sword fish");
							addItem(p, ItemId.RAW_SWORDFISH.id(), 1);
							p.incExp(SKILLS.FISHING.id(), 400, false);
						} else if (catchFish(40, p.getSkills().getLevel(SKILLS.FISHING.id()))) {
							message(p, 1200, "..a lobster");
							addItem(p, ItemId.RAW_LOBSTER.id(), 1);
							p.incExp(SKILLS.FISHING.id(), 360, false);
						} else if (catchFish(30, p.getSkills().getLevel(SKILLS.FISHING.id()))) {
							message(p, 1200, "..some tuna");
							addItem(p, ItemId.RAW_TUNA.id(), 1);
							p.incExp(SKILLS.FISHING.id(), 320, false);
						} else if (catchFish(15, p.getSkills().getLevel(SKILLS.FISHING.id()))) {
							message(p, 1200, "..some anchovies");
							addItem(p, ItemId.RAW_ANCHOVIES.id(), 1);
							p.incExp(SKILLS.FISHING.id(), 160, false);
						} else if (catchFish(5, p.getSkills().getLevel(SKILLS.FISHING.id()))) {
							message(p, 1200, "..a sardine");
							addItem(p, ItemId.RAW_SARDINE.id(), 1);
							p.incExp(SKILLS.FISHING.id(), 80, false);
						} else {
							message(p, 1200, "..some shrimp");
							addItem(p, ItemId.RAW_SHRIMP.id(), 1);
							p.incExp(SKILLS.FISHING.id(), 40, false);
						}
					}
					 else {
						int randomJunkItem = JUNK_ITEMS[DataConversions.random(0, JUNK_ITEMS.length - 1)];
						if (randomJunkItem == ItemId.EDIBLE_SEAWEED.id()) { // Edible seaweed
							message(p, 1200, "..some seaweed");
							addItem(p, ItemId.EDIBLE_SEAWEED.id(), 1);
							p.incExp(SKILLS.FISHING.id(), 20, false);
						} else if (randomJunkItem == ItemId.OYSTER.id()) { // Oyster
							message(p, 1200, "..an oyster!");
							addItem(p, ItemId.OYSTER.id(), 1);
							p.incExp(SKILLS.FISHING.id(), 40, false);
						} else {
							// Broken glass, buttons, damaged armour, ceramic remains
							if (randomJunkItem == ItemId.BROKEN_GLASS_DIGSITE_LVL_2.id() || randomJunkItem == ItemId.BUTTONS.id()
								|| randomJunkItem == ItemId.DAMAGED_ARMOUR_1.id() || randomJunkItem == ItemId.DAMAGED_ARMOUR_2.id()
								|| randomJunkItem == ItemId.CERAMIC_REMAINS.id()) {
								message(p, 1200, "..some " + EntityHandler.getItemDef(randomJunkItem).getName());
							}
							// Old boot
							else if (randomJunkItem == ItemId.OLD_BOOT.id()) {
								message(p, 1200, "..an " + EntityHandler.getItemDef(randomJunkItem).getName());
							}
							// broken arrow, broken staff, Rusty sword, vase
							else {
								message(p, 1200, "..a " + EntityHandler.getItemDef(randomJunkItem).getName());
							}
							addItem(p, randomJunkItem, 1);
							p.incExp(SKILLS.FISHING.id(), 5, false);
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
		return Formulae.calcGatheringSuccessful(levelReq, level);
	}

}
