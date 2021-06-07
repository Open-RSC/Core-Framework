package com.openrsc.server.plugins.authentic.quests.members.shilovillage;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
	public int getQuestPoints() {
		return Quest.SHILO_VILLAGE.reward().getQuestPoints();
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
		final QuestReward reward = Quest.SHILO_VILLAGE.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == TOMB_DOLMEN;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == TOMB_DOLMEN) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				player.message("You find nothing on the Dolmen.");
				return;
			}
			if (command.equalsIgnoreCase("Look")) {
				mes("The Dolmen is intricately decorated with the family");
				delay(3);
				mes("symbol of two crossed palm trees .");
				delay(3);
				if (player.getQuestStage(Quests.SHILO_VILLAGE) == 8) {
					player.message("There is nothing on the Dolmen.");
					return;
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.SWORD_POMMEL.id(), Optional.empty())
					&& player.getCarriedItems().hasCatalogID(ItemId.LOCATING_CRYSTAL.id(), Optional.empty())) {
					player.message("There is nothing on the Dolmen.");
				} else if (player.getCarriedItems().hasCatalogID(ItemId.SWORD_POMMEL.id(), Optional.empty())
					|| player.getCarriedItems().hasCatalogID(ItemId.LOCATING_CRYSTAL.id(), Optional.empty())) {
					player.message("You can see an item on the Dolmen");
				} else {
					player.message("You can see that there are some items on the Dolmen.");
				}
			} else if (command.equalsIgnoreCase("Search")) {
				mes("The Dolmen is intricately decorated with the symbol of");
				delay(3);
				mes("two crossed palm trees. It might be the family crest?");
				delay(3);
				if (player.getCarriedItems().hasCatalogID( ItemId.SWORD_POMMEL.id(), Optional.empty())
					&& player.getCarriedItems().hasCatalogID(ItemId.LOCATING_CRYSTAL.id(), Optional.empty())) {
					mes("There is nothing on the Dolmen.");
					delay(3);
				} else if (player.getCarriedItems().hasCatalogID(ItemId.SWORD_POMMEL.id(), Optional.empty())
					|| player.getCarriedItems().hasCatalogID(ItemId.LOCATING_CRYSTAL.id(), Optional.empty())) {
					mes("You can see an item on the Dolmen");
					delay(3);
				} else {
					mes("You can see that there are some items on the Dolmen.");
					delay(3);
				}
				if (!player.getCarriedItems().hasCatalogID(ItemId.SWORD_POMMEL.id(), Optional.empty())
					&& !player.getCarriedItems().hasCatalogID(ItemId.BONE_BEADS.id(), Optional.empty())) { // SWORD POMMEL
					mes("You find a rusty sword with an ivory pommel.");
					delay(3);
					player.message("You take the pommel and place it into your inventory.");
					give(player, ItemId.SWORD_POMMEL.id(), 1);
					delay();
				}
				if (!player.getCarriedItems().hasCatalogID(ItemId.LOCATING_CRYSTAL.id(), Optional.empty())) { // crystal
					mes("You find a Crystal Sphere ");
					delay(3);
					give(player, ItemId.LOCATING_CRYSTAL.id(), 1);
				}
				mes("You find some writing on the dolmen,");
				delay(3);
				if (!player.getCarriedItems().hasCatalogID(ItemId.BERVIRIUS_TOMB_NOTES.id(), Optional.empty()) && player.getCache().hasKey("dropped_writing")) {
					mes("You would need some Papyrus and Charcoal");
					delay(3);
					player.message("to take more notes from this Dolmen!");
				} else if (!player.getCarriedItems().hasCatalogID(ItemId.BERVIRIUS_TOMB_NOTES.id(), Optional.empty()) && !player.getCache().hasKey("dropped_writing")) {
					mes("you grab some nearby scraps of delicate paper together ");
					delay(3);
					mes("and copy the text as best you can and collect");
					delay(3);
					player.message("them together as a scroll");
					give(player, ItemId.BERVIRIUS_TOMB_NOTES.id(), 1);
				}
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == TOMB_DOLMEN;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == TOMB_DOLMEN) {
			switch (ItemId.getById(item.getCatalogId())) {
				case PAPYRUS:
					if (player.getCarriedItems().hasCatalogID(ItemId.BERVIRIUS_TOMB_NOTES.id(), Optional.of(false))) {
						player.message("You already have Bervirius Tomb Notes in your inventory.");
					} else {
						mes("You try to take some new notes on the delicate papyrus.");
						delay(3);
						if (!player.getCarriedItems().hasCatalogID(ItemId.A_LUMP_OF_CHARCOAL.id(), Optional.of(false))) {
							player.message("You need some charcoal to make notes.");
							return;
						}
						mes("You use the charcoal and the Papyrus to make some new notes.");
						delay(3);
						player.message("You collect the notes together as a scroll.");
						player.getCarriedItems().remove(new Item(ItemId.PAPYRUS.id()));
						player.getCarriedItems().remove(new Item(ItemId.A_LUMP_OF_CHARCOAL.id()));
						give(player, ItemId.BERVIRIUS_TOMB_NOTES.id(), 1);
					}
					break;
				case RASHILIYA_CORPSE: // COMPLETE QUEST - RASHILIYIA CORPSE
					if (player.getQuestStage(Quests.SHILO_VILLAGE) == 8) {
						player.message("You carefully place Rashiliyia's remains on the Dolmen.");
						delay(2);
						player.message("You feel a strange vibration in the air.");
						Npc rash = addnpc(player.getWorld(), NpcId.RASHILIYIA.id(), player.getX(), player.getY(), (int) TimeUnit.SECONDS.toMillis(60));
						if (rash != null) {
							rash.teleport(rash.getX() + 1, rash.getY());
							npcsay(player, rash, "You have my gratitude for releasing my spirit.",
								"I have suffered a vengeful and evil existence.",
								"I was tricked by Zamorak. He returned my son to me as an undead Creature.",
								"My hatred and bitterness corrupted me.",
								"I tried too destroy all life...now I am released.",
								"And am grateful to contemplate eternal rest...");
							mes("Without warning the spirit of Rashiliyia disapears.");
							delay(3);
							rash.remove();
						}
						player.getCarriedItems().remove(new Item(ItemId.RASHILIYA_CORPSE.id()));
						player.sendQuestComplete(Quests.SHILO_VILLAGE);
					}
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
	}
}
