package com.openrsc.server.plugins.npcs.edgeville;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class BrotherJered implements TalkToNpcExecutiveListener,
	TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		int option = showMenu(p, n, "What can you do to help a bold adventurer like myself?", "Praise be to Saradomin");
		if (option == 0) {
			if (!hasItem(p, ItemId.UNBLESSED_HOLY_SYMBOL.id()) && !hasItem(p, ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id())) {
				npcTalk(p, n, "If you have a silver star",
						"Which is the holy symbol of Saradomin",
						"Then I can bless it",
						"Then if you are wearing it",
						"It will help you when you are praying");
			} else if (hasItem(p, ItemId.UNBLESSED_HOLY_SYMBOL.id())) {
				npcTalk(p, n, "Well I can bless that star of Saradomin you have");
				int sub_option = showMenu(p, n, false, //do not send over
						"Yes Please", "No thankyou");
				if (sub_option == 0) {
					removeItem(p, ItemId.UNBLESSED_HOLY_SYMBOL.id(), 1);
					npcTalk(p, n, "Yes Please");
					message(p, "You give Jered the symbol",
							"Jered closes his eyes and places his hand on the symbol",
							"He softly chants",
							"Jered passes you the holy symbol");
					addItem(p, ItemId.HOLY_SYMBOL_OF_SARADOMIN.id(), 1);
				} else if (sub_option == 1) {
					npcTalk(p, n, "No Thankyou");
				}
			} else if (hasItem(p, ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id())) {
				npcTalk(p, n, "Well if you put a string on that holy symbol",
						"I can bless it for you\"");
			}
		} else if (option == 1) {
			npcTalk(p, n, "Yes praise he who brings life to this world");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.BROTHER_JERED.id();
	}

}
