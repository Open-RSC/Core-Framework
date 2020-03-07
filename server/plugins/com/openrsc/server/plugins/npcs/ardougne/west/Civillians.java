package com.openrsc.server.plugins.npcs.ardougne.west;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Civillians implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player p, Npc n) {
		boolean hasCat = p.getCarriedItems().hasCatalogID(ItemId.CAT.id());
		boolean hasKitten = p.getCarriedItems().hasCatalogID(ItemId.KITTEN.id());
		boolean hasKardiasCat = p.getCarriedItems().hasCatalogID(ItemId.KARDIA_CAT.id());
		boolean hasGertrudesCat = p.getCarriedItems().hasCatalogID(ItemId.GERTRUDES_CAT.id());
		boolean hasFluffsKittens = p.getCarriedItems().hasCatalogID(ItemId.KITTENS.id());
		boolean hasAnyCat = hasCat || hasKitten || hasKardiasCat || hasGertrudesCat
				|| (hasFluffsKittens && p.getWorld().getServer().getConfig().WANT_SHOW_KITTENS_CIVILLIAN);
		switch(NpcId.getById(n.getID())) {
		case CIVILLIAN_APRON:
			say(p, n, "hi");
			npcsay(p, n, "good day to you traveller");
			say(p, n, "what are you up to?");
			npcsay(p, n, "chasing mice as usual...",
					"...it's all i seem to do");
			say(p, n, "you must waste alot of time");
			npcsay(p, n, "yep, but what can you do?",
					"it's not like there's many cats around here");
			if (!hasAnyCat) {
				say(p, n, "no you're right, you don't see many around");
			} else {
				if (hasCat) civilianWantCatDialogue(p, n);
				else if (hasKitten) civilianShowKittenDialogue(p, n);
				else if (hasKardiasCat) civilianShowKardiasCatDialogue(p, n);
				else if (hasGertrudesCat) civilianShowGertrudesCatDialogue(p, n);
				else if (hasFluffsKittens && p.getWorld().getServer().getConfig().WANT_SHOW_KITTENS_CIVILLIAN)
					civilianShowFluffsKittensDialogue(p, n);
			}
			break;
		case CIVILLIAN_ATTACKABLE:
			say(p, n, "hello there");
			npcsay(p, n, "oh hello, i'm sorry, i'm a bit worn out");
			say(p, n, "busy day?");
			npcsay(p, n, "oh, it's those bleeding mice, they're everywhere",
					"what i really need is a cat, but they're hard to come by nowadays");
			if (!hasAnyCat) {
				say(p, n, "no, you're right, you don't see many around");
			} else {
				if (hasCat) civilianWantCatDialogue(p, n);
				else if (hasKitten) civilianShowKittenDialogue(p, n);
				else if (hasKardiasCat) civilianShowKardiasCatDialogue(p, n);
				else if (hasGertrudesCat) civilianShowGertrudesCatDialogue(p, n);
				else if (hasFluffsKittens && p.getWorld().getServer().getConfig().WANT_SHOW_KITTENS_CIVILLIAN)
					civilianShowFluffsKittensDialogue(p, n);
			}
			break;
		case CIVILLIAN_PICKPOCKET:
			say(p, n, "hello");
			npcsay(p, n, "i'm a bit busy to talk, sorry");
			say(p, n, "what are you doing?");
			npcsay(p, n, "i need to kill these blasted mice",
					"they're all over the place, i need a cat");
			if (!hasAnyCat) {
				say(p, n, "no you're right, you don't see many around");
			} else {
				if (hasCat) civilianWantCatDialogue(p, n);
				else if (hasKitten) civilianShowKittenDialogue(p, n);
				else if (hasKardiasCat) civilianShowKardiasCatDialogue(p, n);
				else if (hasGertrudesCat) civilianShowGertrudesCatDialogue(p, n);
				else if (hasFluffsKittens && p.getWorld().getServer().getConfig().WANT_SHOW_KITTENS_CIVILLIAN)
					civilianShowFluffsKittensDialogue(p, n);
			}
			break;
		default:
			break;
		}
	}

	private void civilianWantCatDialogue(Player p, Npc n) {
		int menu = multi(p, n, "i have a cat that i could sell", "nope, they're not easy to get hold of");
		if (menu == 0) {
			npcsay(p, n, "you don't say, can i see it");
			p.message("you reveal the cat in your satchel");
			npcsay(p, n, "hmmm, not bad, not bad at all",
					"looks like it's a lively one");
			say(p, n, "erm ...kind of!");
			npcsay(p, n, "i don't have much in the way of money...",
					"but i do have these...");
			p.message("the peasent shows you a sack of death runes");
			npcsay(p, n, "the dwarfs bring them from the mine for us",
					"tell you what, i'll give you 25 death runes for the cat");
			int sub_menu = multi(p, n, "nope, i'm not parting for that", "ok then, you've got a deal");
			if (sub_menu == 0) {
				npcsay(p, n, "well, i'm not giving you anymore");
			} else if (sub_menu == 1) {
				p.message("you hand over the cat");
				remove(p, ItemId.CAT.id(), 1);
				p.message("you are given 25 death runes");
				give(p, ItemId.DEATH_RUNE.id(), 25);
				npcsay(p, n, "great, thanks for that");
				say(p, n, "that's ok, take care");
			}
		} else if (menu == 1) {
			// nothing
		}
	}

	private void civilianShowKittenDialogue(Player p, Npc n) {
		int menu = multi(p, n, "i have a kitten that i could sell", "nope, they're not easy to get hold of");
		if (menu == 0) {
			npcsay(p, n, "really, lets have a look");
			p.message("you reveal the kitten in your satchel");
			npcsay(p, n, "hah, that little thing won't catch any mice",
					"i need a fully grown cat");
		} else if (menu == 1) {
			// nothing
		}
	}

	private void civilianShowKardiasCatDialogue(Player p, Npc n) {
		say(p, n, "i have a cat..look");
		npcsay(p, n, "hmmm..doesn't look like it's seen daylight in years",
				"that's not going to catch any mice");
	}

	//no known method to obtain gertrudes cat
	private void civilianShowGertrudesCatDialogue(Player p, Npc n) {
		say(p, n, "i have a cat..look");
		npcsay(p, n, "hmmm..doesn't look like it belongs to you",
				"i cannot buy it");
	}

	//very likely did not trigger something, it does not appear to trigger dialogue in OSRS
	//and kardias cat is wikified to have dialogue in OSRS
	private void civilianShowFluffsKittensDialogue(Player p, Npc n) {
		say(p, n, "i have some kittens..look");
		npcsay(p, n, "hmmm..doesn't look like they are happy",
				"better return them where they were");
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return inArray(n.getID(), NpcId.CIVILLIAN_APRON.id(), NpcId.CIVILLIAN_ATTACKABLE.id(), NpcId.CIVILLIAN_PICKPOCKET.id());
	}

}
