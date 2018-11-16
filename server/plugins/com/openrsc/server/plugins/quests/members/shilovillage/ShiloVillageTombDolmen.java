package com.openrsc.server.plugins.quests.members.shilovillage;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class ShiloVillageTombDolmen implements QuestInterface, ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	public static final int TOMB_DOLMEN = 689;

	@Override
	public int getQuestId() {
		return Constants.Quests.SHILO_VILLAGE;
	}

	@Override
	public String getQuestName() {
		return "Shilo village (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		/* REMOVE CACHES AFTER QUEST COMPLETE THAT NO LONGER IS USED */
		if(player.getCache().hasKey("coins_shilo_cave")) {
			player.getCache().remove("coins_shilo_cave");
		}
		if(player.getCache().hasKey("can_chisel_bone")) {
			player.getCache().remove("can_chisel_bone");
		}
		if(player.getCache().hasKey("tomb_door_shilo")) {
			player.getCache().remove("tomb_door_shilo");
		}
		if(player.getCache().hasKey("SV_DIG_BUMP")) {
			player.getCache().remove("SV_DIG_BUMP");
		}
		if(player.getCache().hasKey("dolmen_zombie")) {
			player.getCache().remove("dolmen_zombie");
		}
		if(player.getCache().hasKey("dolmen_skeleton")) {
			player.getCache().remove("dolmen_skeleton");
		}
		if(player.getCache().hasKey("dolmen_ghost")) {
			player.getCache().remove("dolmen_ghost");
		}
		player.message("Well Done!");
		player.message("You have completed the Shilo Village Quest.");
		player.message("You gain some experience in crafting.");
		player.message("@gre@You haved gained 2 quest points!");
		incQuestReward(player, Quests.questData.get(Quests.SHILO_VILLAGE), true);
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == TOMB_DOLMEN) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == TOMB_DOLMEN) {
			if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == -1) {
				p.message("You find nothing on the Dolmen.");
				return;
			}
			if(command.equalsIgnoreCase("Look")) {
				message(p, "The Dolmen is intricately decorated with the family",
						"symbol of two crossed palm trees .");
				if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == 8) {
					p.message("There is nothing on the Dolmen.");
					return;
				}
				if(hasItem(p, 973) && hasItem(p, 972)) {
					p.message("There is nothing on the Dolmen.");
				} else if(hasItem(p, 973) || hasItem(p, 972)) {
					p.message("You can see an item on the Dolmen");
				} else {
					p.message("You can see that there are some items on the Dolmen.");
				}
			} else if(command.equalsIgnoreCase("Search")) {
				message(p, "The Dolmen is intricately decorated with the symbol of");
				message(p, "two crossed palm trees. It might be the family crest?");
				if(hasItem(p, 973) && hasItem(p, 972)) {
					message(p, "There is nothing on the Dolmen.");
				} else if(hasItem(p, 973) || hasItem(p, 972)) {
					message(p, "You can see an item on the Dolmen");
				} else {
					message(p, "You can see that there are some items on the Dolmen.");
				}
				if(!hasItem(p, 973) && !hasItem(p, 976)) { // SWORD POMMEL
					message(p, "You find a rusty sword with an ivory pommel.");
					p.message("You take the pommel and place it into your inventory.");
					addItem(p, 973, 1);
					sleep(500);
				}
				if(!hasItem(p, 972)) { // crystal
					message(p, "You find a Crystal Sphere ");
					addItem(p, 972, 1);
				}
				message(p, "You find some writing on the dolmen,");
				if(!hasItem(p, 961) && p.getCache().hasKey("dropped_writing")) {
					message(p, "You would need some Papyrus and Charcoal");
					p.message("to take more notes from this Dolmen!");
				} else if(!hasItem(p, 961) && !p.getCache().hasKey("dropped_writing")) {
					message(p, "you grab some nearby scraps of delicate paper together ",
							"and copy the text as best you can and collect");
					p.message("them together as a scroll");
					addItem(p, 961, 1);
				}
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == TOMB_DOLMEN) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == TOMB_DOLMEN) {
			switch(item.getID()) {
			case 982:
				if(hasItem(p, 961)) {
					p.message("You already have Bervirius Tomb Notes in your inventory.");
				} else {
					message(p, "You try to take some new notes on the delicate papyrus.");
					if(!hasItem(p, 983)) {
						p.message("You need some charcoal to make notes.");
						return;
					}
					message(p, "You use the charcoal and the Papyrus to make some new notes.");
					p.message("You collect the notes together as a scroll.");
					removeItem(p, 982, 1);
					removeItem(p, 983, 1);
					addItem(p, 961, 1);
				}
				break;
			case 977: // COMPLETE QUEST - RASHILIYIA CORPSE
				if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == 8) {
					p.setBusy(true);
					p.message("You carefully place Rashiliyia's remains on the Dolmen.");
					sleep(1200);
					p.message("You feel a strange vibration in the air.");
					Npc rash = spawnNpc(533, p.getX(), p.getY(), 60000);
					if(rash != null) {
						rash.teleport(rash.getX() + 1, rash.getY());
						npcTalk(p, rash, "You have my gratitude for releasing my spirit.",
								"I have suffered a vengeful and evil existence.",
								"I was tricked by Zamorak. He returned my son to me as an undead Creature.",
								"My hatred and bitterness corrupted me.",
								"I tried too destroy all life...now I am released.",
								"And am grateful to contemplate eternal rest...");
						message(p, "Without warning the spirit of Rashiliyia disapears.");
						rash.remove();
					}
					removeItem(p, 977, 1);
					p.sendQuestComplete(Constants.Quests.SHILO_VILLAGE);
					p.setBusy(false);
				}
				break;
			default:
				p.message("Nothing interesting happens");
				break;
			}
		}
	}
}
