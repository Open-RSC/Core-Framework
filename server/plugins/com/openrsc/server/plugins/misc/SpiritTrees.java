package com.openrsc.server.plugins.misc;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class SpiritTrees implements ObjectActionListener, ObjectActionExecutiveListener {

	public static int STRONGHOLD_SPIRIT_TREE = 661;
	public static int YOUNG_SPIRIT_TREES = 391;
	public static int TREE_GNOME_VILLAGE_SPIRIT_TREE = 390;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == YOUNG_SPIRIT_TREES || obj.getID() == TREE_GNOME_VILLAGE_SPIRIT_TREE || obj.getID() == STRONGHOLD_SPIRIT_TREE;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		/** Tree Gnome Stronghold tree 
		 *  NOTE: Only spirit tree that does not teleport you back to tree gnome village unless you have completed Grand Tree quest
		 *  NOTE: If you complete Grand Tree quest you can use all spirit trees without completing tree gnome village quest.
		 *  NOTE: If you have only completed tree gnome village quest you can still use all teleports except this one.
		 * **/
		if(obj.getID() == STRONGHOLD_SPIRIT_TREE) {
			if(p.getQuestStage(Constants.Quests.GRAND_TREE) == -1) {
				message(p, "The tree talks in an old tired voice...",
						"@yel@Spirit Tree: You friend of gnome people, you friend of mine",
						"@yel@Spirit Tree: Would you like me to take you somewhere?");
				int treeMenu = showMenu(p, "No thanks old tree",
						"Where can i go?");
				if (treeMenu == 0) {
					playerTalk(p, null, "no thanks old tree");
				} else if (treeMenu == 1) {
					playerTalk(p, null, "where can i go?");
					message(p, "The tree talks again..",
							"@yel@Spirit Tree: You can travel to the trees",
							"@yel@Spirit Tree: Which are related to myself");
					int travelMenu = showMenu(p,
							"Battlefield of Khazard",
							"Forest north of Varrock", "the gnome tree village");
					if (travelMenu == 0) {
						message(p,
								"You place your hands on the dry tough bark of the spirit tree",
								"and feel a surge of energy run through your veins");
						breakPlagueSample(p);
						p.teleport(629, 629, false);
					} else if (travelMenu == 1) {
						message(p,
								"You place your hands on the dry tough bark of the spirit tree",
								"and feel a surge of energy run through your veins");
						breakPlagueSample(p);
						p.teleport(161, 453, false);
					} else if (travelMenu == 2) {
						message(p,
								"You place your hands on the dry tough bark of the spirit tree",
								"and feel a surge of energy run through your veins");
						breakPlagueSample(p);
						p.teleport(656, 694, false);
					}
				}
			} else {
				p.message("the tree doesn't feel like talking");
			}
		}
		/** EDGEVILLE VARROCK tree **/
		/** Battle field of Khazard tree **/
		else if(obj.getID() == YOUNG_SPIRIT_TREES) {
			if (p.getQuestStage(Constants.Quests.TREE_GNOME_VILLAGE) == -1 || p.getQuestStage(Constants.Quests.GRAND_TREE) == -1) {
				p.message("the young spirit tree talks..");
				message(p, "@yel@Young Spirit Tree: Hello gnome friend",
						"@yel@Young Spirit Tree: Would you like to travel to the home of the tree gnomes?");
				int treeMenu = showMenu(p, "No thank you",
						"Yes please");
				if(treeMenu == 0) {
					playerTalk(p, null, "No thank you");
				} else if(treeMenu == 1) {
					playerTalk(p, null, "Yes please");
					message(p, "You place your hands on the dry tough bark of the spirit tree",
							"and feel a surge of energy run through your veins");
					breakPlagueSample(p);
					p.teleport(658, 695, false);
				}
			} else {
				p.message("The tree doesn't feel like talking");
			}
		}
		/** GRAND SPIRIT TREE - TREE GNOME VILLAGE **/
		else if(obj.getID() == TREE_GNOME_VILLAGE_SPIRIT_TREE) {
			if (p.getQuestStage(Constants.Quests.TREE_GNOME_VILLAGE) == -1 || p.getQuestStage(Constants.Quests.GRAND_TREE) == -1) {
				message(p,
						"The tree talks in an old tired voice...",
						"@yel@Spirit Tree: You friend of gnome people, you friend of mine",
						"@yel@Spirit Tree: Would you like me to take you somewhere?");
				int treeMenu = showMenu(p, "No thanks old tree",
						"Where can i go?");
				if (treeMenu == 0) {
					playerTalk(p, null, "no thanks old tree");
				} else if (treeMenu == 1) {
					playerTalk(p, null, "where can i go?");
					message(p, "The tree talks again..",
							"@yel@Spirit Tree: You can travel to the trees",
							"@yel@Spirit Tree: Which are related to myself");
					int travelMenu = showMenu(p,
							"Battlefield of Khazard",
							"Forest north of Varrock", "the gnome stronghold");
					if (travelMenu == 0) {
						message(p,
								"You place your hands on the dry tough bark of the spirit tree",
								"and feel a surge of energy run through your veins");
						breakPlagueSample(p);
						p.teleport(629, 629, false);
					} else if (travelMenu == 1) {
						message(p,
								"You place your hands on the dry tough bark of the spirit tree",
								"and feel a surge of energy run through your veins");
						breakPlagueSample(p);
						p.teleport(161, 453, false);
					} else if (travelMenu == 2) {
						message(p,
								"You place your hands on the dry tough bark of the spirit tree",
								"and feel a surge of energy run through your veins");
						breakPlagueSample(p);
						p.teleport(703, 487, false);
					}
				}
			} else {
				p.message("The tree doesn't feel like talking");
			}
		}
	}
	private void breakPlagueSample(Player p) {
		if(p.getInventory().hasItemId(812)) {
			p.message("the plague sample is too delicate...");
			p.message("it disintegrates in the crossing");
			while(p.getInventory().countId(812) > 0) {
				p.getInventory().remove(new Item(812));
			}
		}
	}
}
