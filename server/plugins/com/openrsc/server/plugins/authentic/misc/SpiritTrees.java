package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class SpiritTrees implements OpLocTrigger {

	private static int STRONGHOLD_SPIRIT_TREE = 661;
	private static int TREE_GNOME_VILLAGE_SPIRIT_TREE = 390;
	private static int YOUNG_SPIRIT_TREES = 391;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == YOUNG_SPIRIT_TREES || obj.getID() == TREE_GNOME_VILLAGE_SPIRIT_TREE || obj.getID() == STRONGHOLD_SPIRIT_TREE;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		/** STRONGHOLD SPIRIT TREE
		 *  NOTE: does not teleport you back to tree gnome village unless you have completed Grand Tree quest
		 * **/
		if (obj.getID() == STRONGHOLD_SPIRIT_TREE) {
			if (player.getQuestStage(Quests.GRAND_TREE) == -1) {
				mes("The tree talks in an old tired voice...");
				delay(3);
				mes("@yel@Spirit Tree: You friend of gnome people, you friend of mine");
				delay(3);
				mes("@yel@Spirit Tree: Would you like me to take you somewhere?");
				delay(3);
				int treeMenu = multi(player, "No thanks old tree",
					"Where can i go?");
				if (treeMenu == 0) {
					say(player, null, "no thanks old tree");
				} else if (treeMenu == 1) {
					say(player, null, "where can i go?");
					mes("The tree talks again..");
					delay(3);
					mes("@yel@Spirit Tree: You can travel to the trees");
					delay(3);
					mes("@yel@Spirit Tree: Which are related to myself");
					delay(3);
					int travelMenu = multi(player,
						"Battlefield of Khazard",
						"Forest north of Varrock", "the gnome tree village");
					if (travelMenu >= 0 && travelMenu <= 2) {
						mes("You place your hands on the dry tough bark of the spirit tree");
						delay(3);
						mes("and feel a surge of energy run through your veins");
						delay(3);
						breakPlagueSample(player);
						if (travelMenu == 0) player.teleport(629, 629, false);
						else if (travelMenu == 1) player.teleport(161, 453, false);
						else if (travelMenu == 2) player.teleport(656, 694, false);
					}
				}
			} else {
				player.message("the tree doesn't feel like talking");
			}
		}
		/** GRAND SPIRIT TREE - TREE GNOME VILLAGE
		 *  NOTE: may be reached by completing Grand Tree but does not teleport you back there unless you have completed Tree Gnome Village quest
		 *  **/
		else if (obj.getID() == TREE_GNOME_VILLAGE_SPIRIT_TREE) {
			if (player.getQuestStage(Quests.TREE_GNOME_VILLAGE) == -1) {
				mes("The tree talks in an old tired voice...");
				delay(3);
				mes("@yel@Spirit Tree: You friend of gnome people, you friend of mine");
				delay(3);
				mes("@yel@Spirit Tree: Would you like me to take you somewhere?");
				delay(3);
				int treeMenu = multi(player, "No thanks old tree",
					"Where can i go?");
				if (treeMenu == 0) {
					say(player, null, "no thanks old tree");
				} else if (treeMenu == 1) {
					say(player, null, "where can i go?");
					mes("The tree talks again..");
					delay(3);
					mes("@yel@Spirit Tree: You can travel to the trees");
					delay(3);
					mes("@yel@Spirit Tree: Which are related to myself");
					delay(3);
					int travelMenu = multi(player,
						"Battlefield of Khazard",
						"Forest north of Varrock", "the gnome stronghold");
					if (travelMenu >= 0 && travelMenu <= 2) {
						mes("You place your hands on the dry tough bark of the spirit tree");
						delay(3);
						mes("and feel a surge of energy run through your veins");
						delay(3);
						breakPlagueSample(player);
						if (travelMenu == 0) player.teleport(629, 629, false);
						else if (travelMenu == 1) player.teleport(161, 453, false);
						else if (travelMenu == 2) player.teleport(703, 487, false);
					}
				}
			} else {
				player.message("The tree doesn't feel like talking");
			}
		}
		/** EDGEVILLE VARROCK tree **/
		/** Battle field of Khazard tree **/
		else if (obj.getID() == YOUNG_SPIRIT_TREES) {
			if (player.getQuestStage(Quests.TREE_GNOME_VILLAGE) == -1) {
				player.message("The young spirit tree talks..");
				mes("@yel@Young Spirit Tree: Hello gnome friend");
				delay(3);
				mes("@yel@Young Spirit Tree: Would you like to travel to the home of the tree gnomes?");
				delay(3);
				int treeMenu = multi(player, "No thank you",
					"Yes please");
				if (treeMenu == 0) {
					say(player, null, "No thank you");
				} else if (treeMenu == 1) {
					say(player, null, "Yes please");
					mes("You place your hands on the dry tough bark of the spirit tree");
					delay(3);
					mes("and feel a surge of energy run through your veins");
					delay(3);
					breakPlagueSample(player);
					player.teleport(658, 695, false);
				}
			} else {
				player.message("The tree doesn't feel like talking");
			}
		}
	}

	private void breakPlagueSample(Player player) {
		if (player.getCarriedItems().hasCatalogID(ItemId.PLAGUE_SAMPLE.id())) {
			player.message("the plague sample is too delicate...");
			player.message("it disintegrates in the crossing");
			while (player.getCarriedItems().getInventory().countId(ItemId.PLAGUE_SAMPLE.id()) > 0) {
				player.getCarriedItems().remove(new Item(ItemId.PLAGUE_SAMPLE.id()));
			}
		}
	}
}
