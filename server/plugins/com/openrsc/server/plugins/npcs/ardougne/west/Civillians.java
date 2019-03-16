package com.openrsc.server.plugins.npcs.ardougne.west;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Civillians implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		boolean hasCat = p.getInventory().hasItemId(ItemId.CAT.id());
		boolean hasKitten = p.getInventory().hasItemId(ItemId.KITTEN.id());
		boolean hasKardiasCat = p.getInventory().hasItemId(ItemId.KARDIA_CAT.id());
		boolean hasGertrudesCat = p.getInventory().hasItemId(ItemId.GERTRUDES_CAT.id());
		boolean hasAnyCat = hasCat || hasKitten || hasKardiasCat || hasGertrudesCat;
		switch(NpcId.getById(n.getID())) {
		case CIVILLIAN_APRON:
			playerTalk(p, n, "hi");
			npcTalk(p, n, "good day to you traveller");
			playerTalk(p, n, "what are you up to?");
			npcTalk(p, n, "chasing mice as usual...",
					"...it's all i seem to do");
			playerTalk(p, n, "you must waste alot of time");
			npcTalk(p, n, "yep, but what can you do?",
					"it's not like there's many cats around here");
			if (!hasAnyCat) {
				playerTalk(p, n, "no you're right, you don't see many around");
			} else {
				if (hasCat) civilianWantCatDialogue(p, n);
				else if (hasKitten) civilianShowKittenDialogue(p, n);
				else if (hasKardiasCat) civilianShowKardiasCatDialogue(p, n);
				else if (hasGertrudesCat) civilianShowGertrudesCatDialogue(p, n);
			}
			break;
		case CIVILLIAN_ATTACKABLE:
			playerTalk(p, n, "hello there");
			npcTalk(p, n, "oh hello, i'm sorry, i'm a bit worn out");
			playerTalk(p, n, "busy day?");
			npcTalk(p, n, "oh, it's those bleeding mice, they're everywhere",
					"what i really need is a cat, but they're hard to come by nowadays");
			if (!hasAnyCat) {
				playerTalk(p, n, "no, you're right, you don't see many around");
			} else {
				if (hasCat) civilianWantCatDialogue(p, n);
				else if (hasKitten) civilianShowKittenDialogue(p, n);
				else if (hasKardiasCat) civilianShowKardiasCatDialogue(p, n);
				else if (hasGertrudesCat) civilianShowGertrudesCatDialogue(p, n);
			}
			break;
		case CIVILLIAN_PICKPOCKET:
			playerTalk(p, n, "hello");
			npcTalk(p, n, "i'm a bit busy to talk, sorry");
			playerTalk(p, n, "what are you doing?");
			npcTalk(p, n, "i need to kill these blasted mice",
					"they're all over the place, i need a cat");
			if (!hasAnyCat) {
				playerTalk(p, n, "no you're right, you don't see many around");
			} else {
				if (hasCat) civilianWantCatDialogue(p, n);
				else if (hasKitten) civilianShowKittenDialogue(p, n);
				else if (hasKardiasCat) civilianShowKardiasCatDialogue(p, n);
				else if (hasGertrudesCat) civilianShowGertrudesCatDialogue(p, n);
			}
			break;
		default:
			break;
		}
	}
	
	private void civilianWantCatDialogue(Player p, Npc n) {
		int menu = showMenu(p, n, "i have a cat that i could sell", "nope, they're not easy to get hold of");
		if (menu == 0) {
			npcTalk(p, n, "you don't say, can i see it");
			p.message("you reveal the cat in your satchel");
			npcTalk(p, n, "hmmm, not bad, not bad at all",
					"looks like it's a lively one");
			playerTalk(p, n, "erm ...kind of!");
			npcTalk(p, n, "i don't have much in the way of money...",
					"but i do have these...");
			p.message("the peasent shows you a sack of death runes");
			npcTalk(p, n, "the dwarfs bring them from the mine for us",
					"tell you what, i'll give you 25 death runes for the cat");
			int sub_menu = showMenu(p, n, "nope, i'm not parting for that", "ok then, you've got a deal");
			if (sub_menu == 0) {
				npcTalk(p, n, "well, i'm not giving you anymore");
			} else if (sub_menu == 1) {
				p.message("you hand over the cat");
				removeItem(p, ItemId.CAT.id(), 1);
				p.message("you are given 25 death runes");
				addItem(p, ItemId.DEATH_RUNE.id(), 25);
				npcTalk(p, n, "great, thanks for that");
				playerTalk(p, n, "that's ok, take care");
			}
		} else if (menu == 1) {
			// nothing
		}
	}
	
	private void civilianShowKittenDialogue(Player p, Npc n) {
		int menu = showMenu(p, n, "i have a kitten that i could sell", "nope, they're not easy to get hold of");
		if (menu == 0) {
			npcTalk(p, n, "really, lets have a look");
			p.message("you reveal the kitten in your satchel");
			npcTalk(p, n, "hah, that little thing won't catch any mice",
					"i need a fully grown cat");
		} else if (menu == 1) {
			// nothing
		}
	}
	
	private void civilianShowKardiasCatDialogue(Player p, Npc n) {
		playerTalk(p, n, "i have a cat..look");
		npcTalk(p, n, "hmmm..doesn't look like it's seen daylight in years",
				"that's not going to catch any mice");
	}
	
	//no known method to obtain gertrudes cat
	private void civilianShowGertrudesCatDialogue(Player p, Npc n) {
		playerTalk(p, n, "i have a cat..look");
		npcTalk(p, n, "hmmm..doesn't look like it belongs to you",
				"i cannot buy it");
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return inArray(n.getID(), NpcId.CIVILLIAN_APRON.id(), NpcId.CIVILLIAN_ATTACKABLE.id(), NpcId.CIVILLIAN_PICKPOCKET.id());
	}

}
