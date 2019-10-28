package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Klarense implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if (!p.getCache().hasKey("owns_ship")) {
			defaultDialogue(p, n);
		} else {
			ownsShipDialogue(p, n);
		}
	}

	private void ownsShipDialogue(final Player p, final Npc n) {
		int choice = showMenu(p, n, new String[]{
			"So would you like to sail this ship to Crandor Isle for me?",
			"So what needs fixing on this ship?",
			"What are you going to do now you don't have a ship?"
		});
		if (choice == 0) {
			npcTalk(p, n, "No not me, I'm frightened of dragons");
		} else if (choice == 1) {
			npcTalk(p, n,
				"Well the big gaping hole in the hold is the main problem");
			npcTalk(p, n, "you'll need a few planks");
			npcTalk(p, n, "Hammered in with steel nails");
		} else if (choice == 2) {
			npcTalk(p, n, "Oh I'll be fine");
			npcTalk(p, n, "I've got work as Port Sarim's first life guard");
		}
	}

	private void defaultDialogue(final Player p, final Npc n) {
		npcTalk(p, n,
			"You're interested in a trip on the Lumbridge Lady are you?");
		npcTalk(p, n,
			"I admit she looks fine, but she isn't seaworthy right now");
		String[] menu = new String[]{
			"Do you know when she will be seaworthy",
			"Ah well, nevermind"
		};

		if (p.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
			menu = new String[]{
				"Do you know when she will be seaworthy",
				"Would you take me to Crandor Isle when it's ready?",
				"I don't suppose I could buy it",
				"Ah well, nevermind"
			};

			int choice = showMenu(p, n, menu);
			travel(p, n, choice);
		} else {
			int choice = showMenu(p, n, menu);
			if (choice == 0)
				travel(p, n, choice);
		}
	}

	public void travel(Player p, Npc n, int option) {
		if (option == 0) {
			npcTalk(p, n, "No not really",
				"Port Sarim's shipbuilders aren't very efficient",
				"So it could be quite a while"
			);
		} else if (option == 1) {
			npcTalk(p, n, "Well even if I knew how to get there",
				"I wouldn't like to risk it",
				"Especially after to goin to all the effort of fixing the old girl up"
			);
		} else if (option == 2) {
			npcTalk(p, n, "I guess you could",
				"I'm sure the work needed to do on it wouldn't be too expensive",
				"How does 2000 gold sound for a price?"
			);
			int choice = showMenu(p, n, new String[]{
				"Yep sounds good",
				"I'm not paying that much for a broken boat"
			});
			if (choice == 0) {
				if (p.getInventory().countId(ItemId.COINS.id()) >= 2000) {
					npcTalk(p, n,
						"Ok she's all yours");
					p.getCache().store("owns_ship", true);
					p.getInventory().remove(ItemId.COINS.id(), 2000);
				} else {
					playerTalk(p, n, "Except I don't have that much money on me");
				}
			} else if (choice == 1) {
				npcTalk(p, n, "That's Ok, I didn't particularly want to sell anyway");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.KLARENSE.id();
	}

}
