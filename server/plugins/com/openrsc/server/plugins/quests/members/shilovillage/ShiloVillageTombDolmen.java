package com.openrsc.server.plugins.quests.members.shilovillage;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class ShiloVillageTombDolmen implements QuestInterface, OpLocTrigger, UseLocTrigger {

	private static final int TOMB_DOLMEN = 689;

	@Override
	public int getQuestId() {
		return Quests.SHILO_VILLAGE;
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
		if (player.getCache().hasKey("obtained_shilo_info")) {
			player.getCache().remove("obtained_shilo_info");
		}
		if (player.getCache().hasKey("coins_shilo_cave")) {
			player.getCache().remove("coins_shilo_cave");
		}
		if (player.getCache().hasKey("can_chisel_bone")) {
			player.getCache().remove("can_chisel_bone");
		}
		if (player.getCache().hasKey("tomb_door_shilo")) {
			player.getCache().remove("tomb_door_shilo");
		}
		if (player.getCache().hasKey("SV_DIG_LIT")) {
			player.getCache().remove("SV_DIG_LIT");
		}
		if (player.getCache().hasKey("SV_DIG_ROPE")) {
			player.getCache().remove("SV_DIG_ROPE");
		}
		if (player.getCache().hasKey("SV_DIG_BUMP")) {
			player.getCache().remove("SV_DIG_BUMP");
		}
		if (player.getCache().hasKey("dolmen_zombie")) {
			player.getCache().remove("dolmen_zombie");
		}
		if (player.getCache().hasKey("dolmen_skeleton")) {
			player.getCache().remove("dolmen_skeleton");
		}
		if (player.getCache().hasKey("dolmen_ghost")) {
			player.getCache().remove("dolmen_ghost");
		}
		player.message("Well Done!");
		player.message("You have completed the Shilo Village Quest.");
		player.message("You gain some experience in crafting.");
		player.message("@gre@You haved gained 2 quest points!");
		incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.SHILO_VILLAGE), true);
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return obj.getID() == TOMB_DOLMEN;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == TOMB_DOLMEN) {
			if (p.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				p.message("You find nothing on the Dolmen.");
				return;
			}
			if (command.equalsIgnoreCase("Look")) {
				mes(p, "The Dolmen is intricately decorated with the family",
					"symbol of two crossed palm trees .");
				if (p.getQuestStage(Quests.SHILO_VILLAGE) == 8) {
					p.message("There is nothing on the Dolmen.");
					return;
				}
				if (p.getCarriedItems().hasCatalogID(ItemId.SWORD_POMMEL.id(), Optional.empty())
					&& p.getCarriedItems().hasCatalogID(ItemId.LOCATING_CRYSTAL.id(), Optional.empty())) {
					p.message("There is nothing on the Dolmen.");
				} else if (p.getCarriedItems().hasCatalogID(ItemId.SWORD_POMMEL.id(), Optional.empty())
					|| p.getCarriedItems().hasCatalogID(ItemId.LOCATING_CRYSTAL.id(), Optional.empty())) {
					p.message("You can see an item on the Dolmen");
				} else {
					p.message("You can see that there are some items on the Dolmen.");
				}
			} else if (command.equalsIgnoreCase("Search")) {
				mes(p, "The Dolmen is intricately decorated with the symbol of");
				mes(p, "two crossed palm trees. It might be the family crest?");
				if (p.getCarriedItems().hasCatalogID( ItemId.SWORD_POMMEL.id(), Optional.empty())
					&& p.getCarriedItems().hasCatalogID(ItemId.LOCATING_CRYSTAL.id(), Optional.empty())) {
					mes(p, "There is nothing on the Dolmen.");
				} else if (p.getCarriedItems().hasCatalogID(ItemId.SWORD_POMMEL.id(), Optional.empty())
					|| p.getCarriedItems().hasCatalogID(ItemId.LOCATING_CRYSTAL.id(), Optional.empty())) {
					mes(p, "You can see an item on the Dolmen");
				} else {
					mes(p, "You can see that there are some items on the Dolmen.");
				}
				if (!p.getCarriedItems().hasCatalogID(ItemId.SWORD_POMMEL.id(), Optional.empty())
					&& !p.getCarriedItems().hasCatalogID(ItemId.BONE_BEADS.id(), Optional.empty())) { // SWORD POMMEL
					mes(p, "You find a rusty sword with an ivory pommel.");
					p.message("You take the pommel and place it into your inventory.");
					give(p, ItemId.SWORD_POMMEL.id(), 1);
					delay(p.getWorld().getServer().getConfig().GAME_TICK);
				}
				if (!p.getCarriedItems().hasCatalogID(ItemId.LOCATING_CRYSTAL.id(), Optional.empty())) { // crystal
					mes(p, "You find a Crystal Sphere ");
					give(p, ItemId.LOCATING_CRYSTAL.id(), 1);
				}
				mes(p, "You find some writing on the dolmen,");
				if (!p.getCarriedItems().hasCatalogID(ItemId.BERVIRIUS_TOMB_NOTES.id(), Optional.empty()) && p.getCache().hasKey("dropped_writing")) {
					mes(p, "You would need some Papyrus and Charcoal");
					p.message("to take more notes from this Dolmen!");
				} else if (!p.getCarriedItems().hasCatalogID(ItemId.BERVIRIUS_TOMB_NOTES.id(), Optional.empty()) && !p.getCache().hasKey("dropped_writing")) {
					mes(p, "you grab some nearby scraps of delicate paper together ",
						"and copy the text as best you can and collect");
					p.message("them together as a scroll");
					give(p, ItemId.BERVIRIUS_TOMB_NOTES.id(), 1);
				}
			}
		}
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
		return obj.getID() == TOMB_DOLMEN;
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == TOMB_DOLMEN) {
			switch (ItemId.getById(item.getCatalogId())) {
				case PAPYRUS:
					if (p.getCarriedItems().hasCatalogID(ItemId.BERVIRIUS_TOMB_NOTES.id(), Optional.of(false))) {
						p.message("You already have Bervirius Tomb Notes in your inventory.");
					} else {
						mes(p, "You try to take some new notes on the delicate papyrus.");
						if (!p.getCarriedItems().hasCatalogID(ItemId.A_LUMP_OF_CHARCOAL.id(), Optional.of(false))) {
							p.message("You need some charcoal to make notes.");
							return;
						}
						mes(p, "You use the charcoal and the Papyrus to make some new notes.");
						p.message("You collect the notes together as a scroll.");
						p.getCarriedItems().remove(new Item(ItemId.PAPYRUS.id()));
						p.getCarriedItems().remove(new Item(ItemId.A_LUMP_OF_CHARCOAL.id()));
						give(p, ItemId.BERVIRIUS_TOMB_NOTES.id(), 1);
					}
					break;
				case RASHILIYA_CORPSE: // COMPLETE QUEST - RASHILIYIA CORPSE
					if (p.getQuestStage(Quests.SHILO_VILLAGE) == 8) {
						p.setBusy(true);
						p.message("You carefully place Rashiliyia's remains on the Dolmen.");
						delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
						p.message("You feel a strange vibration in the air.");
						Npc rash = addnpc(p.getWorld(), NpcId.RASHILIYIA.id(), p.getX(), p.getY(), 60000);
						if (rash != null) {
							rash.teleport(rash.getX() + 1, rash.getY());
							npcsay(p, rash, "You have my gratitude for releasing my spirit.",
								"I have suffered a vengeful and evil existence.",
								"I was tricked by Zamorak. He returned my son to me as an undead Creature.",
								"My hatred and bitterness corrupted me.",
								"I tried too destroy all life...now I am released.",
								"And am grateful to contemplate eternal rest...");
							mes(p, "Without warning the spirit of Rashiliyia disapears.");
							rash.remove();
						}
						p.getCarriedItems().remove(new Item(ItemId.RASHILIYA_CORPSE.id()));
						p.sendQuestComplete(Quests.SHILO_VILLAGE);
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
