package com.openrsc.server.plugins.authentic.npcs.portsarim;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Klarense implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (!player.getCache().hasKey("owns_ship")) {
			defaultDialogue(player, n);
		} else {
			ownsShipDialogue(player, n);
		}
	}

	private void ownsShipDialogue(final Player player, final Npc n) {
		int choice = multi(player, n, new String[]{
			"So would you like to sail this ship to Crandor Isle for me?",
			"So what needs fixing on this ship?",
			"What are you going to do now you don't have a ship?"
		});
		if (choice == 0) {
			npcsay(player, n, "No not me, I'm frightened of dragons");
		} else if (choice == 1) {
			npcsay(player, n,
				"Well the big gaping hole in the hold is the main problem");
			npcsay(player, n, "you'll need a few planks");
			npcsay(player, n, "Hammered in with steel nails");
		} else if (choice == 2) {
			npcsay(player, n, "Oh I'll be fine");
			npcsay(player, n, "I've got work as Port Sarim's first life guard");
		}
	}

	private void defaultDialogue(final Player player, final Npc n) {
		npcsay(player, n,
			"You're interested in a trip on the Lumbridge Lady are you?");
		npcsay(player, n,
			"I admit she looks fine, but she isn't seaworthy right now");
		String[] menu = new String[]{
			"Do you know when she will be seaworthy",
			"Ah well, nevermind"
		};

		if (player.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
			menu = new String[]{
				"Do you know when she will be seaworthy",
				"Would you take me to Crandor Isle when it's ready?",
				"I don't suppose I could buy it",
				"Ah well, nevermind"
			};

			int choice = multi(player, n, false, //do not send over
				menu);
			if (choice == 0) {
				say(player, n, "Do you know when she will be seaworthy?");
			} else if (choice == 1) {
				say(player, n, "Would you take me to Crandor Isle when it's ready?");
			} else if (choice == 2) {
				say(player, n, "I don't suppose I could buy it");
			} else if (choice == 3) {
				say(player, n, "Ah well never mind");
			}
			travel(player, n, choice);
		} else {
			int choice = multi(player, n, false, //do not send over
				menu);
			if (choice == 0) {
				say(player, n, "Do you know when she will be seaworthy?");
				travel(player, n, choice);
			} else if (choice == 1) {
				say(player, n, "Ah well never mind");
			}
		}
	}

	public void travel(Player player, Npc n, int option) {
		if (option == 0) {
			npcsay(player, n, "No not really",
				"Port Sarim's shipbuilders aren't very efficient",
				"So it could be quite a while"
			);
		} else if (option == 1) {
			npcsay(player, n, "Well even if I knew how to get there",
				"I wouldn't like to risk it",
				"Especially after to goin to all the effort of fixing the old girl up"
			);
		} else if (option == 2) {
			npcsay(player, n, "I guess you could",
				"I'm sure the work needed to do on it wouldn't be too expensive",
				"How does 2000 gold sound for a price?"
			);
			int choice = multi(player, n, "Yep sounds good",
				"I'm not paying that much for a broken boat");
			if (choice == 0) {
				if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 2000) {
					npcsay(player, n,
						"Ok she's all yours");
					player.getCache().store("owns_ship", true);
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 2000));
				} else {
					say(player, n, "Except I don't have that much money on me");
				}
			} else if (choice == 1) {
				npcsay(player, n, "That's Ok, I didn't particularly want to sell anyway");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.KLARENSE.id();
	}

}
