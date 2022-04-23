package com.openrsc.server.plugins.authentic.npcs.ardougne.west;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class SpiritOfScorpius implements TalkNpcTrigger, UseNpcTrigger, OpLocTrigger {

	public int GRAVE_OF_SCORPIUS = 941;

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SPIRIT_OF_SCORPIUS.id() || n.getID() == NpcId.GHOST_SCORPIUS.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SPIRIT_OF_SCORPIUS.id()) {
			if (player.getQuestStage(Quests.OBSERVATORY_QUEST) != -1) {
				npcsay(player, n, "How dare you disturb me!");
			} else {
				if (player.getCache().hasKey("scorpius_mould")) {
					int option;
					if (config().WANT_CUSTOM_QUESTS) {
						option = multi(player, n, false, //do not send over
							"I have come to seek a blessing",
							"I need another unholy symbol mould",
							"I have come to kill you",
							"About mould drops");
					} else {
						option = multi(player, n, false, //do not send over
							"I have come to seek a blessing",
							"I need another unholy symbol mould",
							"I have come to kill you");
					}

					if (option == 0) {
						say(player, n, "I have come to seek a blessing");
						if (player.getCarriedItems().hasCatalogID(ItemId.UNHOLY_SYMBOL_OF_ZAMORAK.id(), Optional.of(false))) {
							npcsay(player, n, "I see you have the unholy symbol of our Lord",
								"It is blessed with the Lord Zamorak's power",
								"Come to me when your faith weakens");
						} else if (player.getCarriedItems().hasCatalogID(ItemId.UNBLESSED_UNHOLY_SYMBOL_OF_ZAMORAK.id(), Optional.of(false))) {
							npcsay(player, n, "I see you have the unholy symbol of our Lord",
								"I will bless it for you");
							player.message("The ghost mutters in a strange voice");
							player.getCarriedItems().remove(new Item(ItemId.UNBLESSED_UNHOLY_SYMBOL_OF_ZAMORAK.id()));
							player.getCarriedItems().getInventory().add(new Item(ItemId.UNHOLY_SYMBOL_OF_ZAMORAK.id()));
							mes("The unholy symbol throbs with power");
							delay(3);
							npcsay(player, n, "The symbol of our lord has been blessed with power!",
								"My master calls...");
						} else {
							npcsay(player, n, "No blessings will be given to those",
								"Who have no symbol of our Lord's love!");
						}
					} else if (option == 1) {
						say(player, n, "I need another mould for the unholy symbol");
						if (player.getCarriedItems().hasCatalogID(ItemId.UNHOLY_SYMBOL_MOULD.id(), Optional.of(false))) {
							npcsay(player, n, "One you already have, another is not needed",
								"Leave me be!");
						} else {
							npcsay(player, n, "To lose an object is easy to replace",
								"To lose the affections of our lord is impossible to forgive...");
							player.message("The ghost hands you another mould");
							give(player, ItemId.UNHOLY_SYMBOL_MOULD.id(), 1);
						}
					} else if (option == 2) {
						say(player, n, "I have come to kill you");
						npcsay(player, n, "The might of mortals to me is as the dust is to the sea!");
					} else if (option == 3) {
						say(player, n, "About mould drops");
						if (!player.getCache().hasKey("want_unholy_symbol_drops"))
							player.getCache().store("want_unholy_symbol_drops", true);

						boolean wantDrops = player.getCache().getBoolean("want_unholy_symbol_drops");
						final String words = wantDrops ? "are" : "are not";
						npcsay(player, n, "I see you " + words + " seeking the unholy symbol.",
							"Do you wish to change your mind?");
						int option2 = multi(player, n, "Yes", "No");
						if (option2 == 0) {
							player.getCache().store("want_unholy_symbol_drops", !wantDrops);
							npcsay(player, n, "Very well");
						} else {
							npcsay(player, n, "How dare you disturb me!");
						}
					}
					return;
				}
				int menu = multi(player, n,
					"I seek your wisdom",
					"I have come to kill you");
				if (menu == 0) {
					npcsay(player, n, "Indeed, I feel you have beheld the far places in the heavens",
						"My Lord instructs me to help you",
						"Here is a mould to make a token for our Lord",
						"A mould for the unholy symbol of Zamorak");
					player.message("The ghost gives you a casting mould");
					give(player, ItemId.UNHOLY_SYMBOL_MOULD.id(), 1);
					if (!player.getCache().hasKey("scorpius_mould")) {
						player.getCache().store("scorpius_mould", true);
					}
				} else if (menu == 1) {
					npcsay(player, n, "The might of mortals to me is as the dust is to the sea!");
				}
			}
		} else if (n.getID() == NpcId.GHOST_SCORPIUS.id()) {
			npcsay(player, n, "We are waiting for you");
			n.startCombat(player);
		}
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (item.getCatalogId() == ItemId.CROWN_OF_THE_OCCULT.id()) {
			if (!player.getCache().hasKey("occultcrown")) {
				npcsay(player, npc, "I see you have an uncharged crown",
					"Capable of cremating bones to the underground",
					"I will charge it for you");
				player.getCache().set("occultcrown", 0);
			} else {
				npcsay(player, npc, "Your crown already holds charges");
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.SPIRIT_OF_SCORPIUS.id() && item.getCatalogId() == ItemId.CROWN_OF_THE_OCCULT.id();
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == GRAVE_OF_SCORPIUS;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == GRAVE_OF_SCORPIUS) {
			player.message("Here lies Scorpius:");
			player.message("Only those who have seen beyond the stars");
			player.message("may seek his counsel");
		}
	}
}
