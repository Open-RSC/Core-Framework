package com.openrsc.server.plugins.authentic.npcs.portsarim;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.authentic.quests.free.DragonSlayer;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class NedInShip implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		//2 cases: ned in portsarim side
		//ned in crandor side
		if (player.getCache().hasKey("lumb_lady") && player.getCache().getInt("lumb_lady") == DragonSlayer.CRANDOR) {
			int menu = multi(player, n, "Is the ship ready to sail back?",
				"So are you enjoying this exotic island vacation?");

			if (menu == 0) {
				npcsay(player, n, "Well when we arrived the ship took a nasty jar from those rocks",
					"We may be stranded");
			} else if (menu == 1) {
				npcsay(player, n, "Well it would have been better if I'd bought my sun lotion",
					"Oh and the skeletons which won't let me leave the ship",
					"Probably aren't helping either");
			}
			return;
		} else {
			if (player.getQuestStage(Quests.DRAGON_SLAYER) == 3 || player.getQuestStage(Quests.DRAGON_SLAYER) == -1) {
				npcsay(player, n, player.getText("NedInShipHelloAgain"));
				int menu = multi(player, n, false, //do not send over
					"Can you take me back to Crandor again",
					"How did you get back?");
				if (menu == 0) {
					say(player, n, "Can you take me back to Crandor again?");
					if (player.getCache().hasKey("ship_fixed")) {
						npcsay(player, n, "Okie Dokie");
						mes("You feel the ship begin to move");
						delay(3);
						mes("You are out at sea");
						delay(3);
						mes("The ship is sailing");
						delay(3);
						mes("The ship is sailing");
						delay(3);
						mes("You feel a crunch");
						delay(3);
						player.teleport(281, 3472, false);
						player.getCache().remove("ship_fixed");
						npcsay(player, n, "Aha we've arrived");
						player.getCache().set("lumb_lady", DragonSlayer.CRANDOR);
					} else {
						npcsay(player, n, "Well I would, but the last adventure",
							"Hasn't left this tub in the best of shapes",
							"You'll have to fix it again");
					}
				} else if (menu == 1) {
					say(player, n, "How did you get back?");
					npcsay(player, n, "I got towed back by a passing friendly whale");
				}
				return;
			} else {
				npcsay(player, n, player.getText("NedInShipHelloThere"));
				int opt = multi(player, n,
					"So are you going to take me to Crandor Island now then?",
					"So are you still up to sailing this ship?");
				if (opt == 0) {
					npcsay(player, n, "Ok show me the map and we'll set sail now");
					boolean gave_map = false;
					if (player.getCarriedItems().hasCatalogID(ItemId.MAP.id(), Optional.of(false))) {
						mes("You give the map to ned");
						delay(3);
						say(player, n, "Here it is");
						player.getCarriedItems().remove(new Item(ItemId.MAP.id()));
						gave_map = true;
					} else if (player.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_1.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_2.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_3.id(), Optional.of(false))) {
						mes("You give the parts of the map to ned");
						delay(3);
						say(player, n, "Here it is");
						player.getCarriedItems().remove(new Item(ItemId.MAP_PIECE_1.id()));
						player.getCarriedItems().remove(new Item(ItemId.MAP_PIECE_2.id()));
						player.getCarriedItems().remove(new Item(ItemId.MAP_PIECE_3.id()));
						gave_map = true;
					}
					if (gave_map) {
						player.message("You feel the ship begin to move");
						delay(3);
						player.message("You are out at sea");
						delay(3);
						player.message("The ship is sailing");
						delay(3);
						player.message("The ship is sailing");
						delay(3);
						player.message("You feel a crunch");
						delay(3);
						player.teleport(281, 3472, false);
						player.getCache().remove("ship_fixed");
						npcsay(player, n, "Aha we've arrived");
						player.getCache().set("lumb_lady", DragonSlayer.CRANDOR);
						player.updateQuestStage(Quests.DRAGON_SLAYER, 3);
						if (player.getCache().hasKey("dwarven_unlocked")) {
							player.getCache().remove("dwarven_unlocked");
						}
						if (player.getCache().hasKey("melzar_unlocked")) {
							player.getCache().remove("melzar_unlocked");
						}
					}
				} else if (opt == 1) {
					npcsay(player, n, "Well I am a tad rusty",
						"I'm sure it'll all come back to me, once I get into action",
						"I hope...");
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.NED_BOAT.id();
	}
}
