package com.openrsc.server.plugins.authentic.npcs.ardougne.west;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Civillians implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		boolean hasCat = player.getCarriedItems().hasCatalogID(ItemId.CAT.id());
		boolean hasKitten = player.getCarriedItems().hasCatalogID(ItemId.KITTEN.id());
		boolean hasKardiasCat = player.getCarriedItems().hasCatalogID(ItemId.KARDIA_CAT.id());
		boolean hasGertrudesCat = player.getCarriedItems().hasCatalogID(ItemId.GERTRUDES_CAT.id());
		boolean hasFluffsKittens = player.getCarriedItems().hasCatalogID(ItemId.KITTENS.id());
		boolean hasAnyCat = hasCat || hasKitten || hasKardiasCat || hasGertrudesCat
				|| (hasFluffsKittens && config().WANT_EXTENDED_CATS_BEHAVIOR);
		switch(NpcId.getById(n.getID())) {
		case CIVILLIAN_APRON:
			say(player, n, "hi");
			npcsay(player, n, "good day to you traveller");
			say(player, n, "what are you up to?");
			npcsay(player, n, "chasing mice as usual...",
					"...it's all i seem to do");
			say(player, n, "you must waste alot of time");
			npcsay(player, n, "yep, but what can you do?",
					"it's not like there's many cats around here");
			if (!hasAnyCat) {
				say(player, n, "no you're right, you don't see many around");
			} else {
				if (hasCat) civilianWantCatDialogue(player, n);
				else if (hasKitten) civilianShowKittenDialogue(player, n);
				else if (hasKardiasCat) civilianShowKardiasCatDialogue(player, n, 1);
				else if (hasGertrudesCat) civilianShowGertrudesCatDialogue(player, n, 1);
				else if (hasFluffsKittens && config().WANT_EXTENDED_CATS_BEHAVIOR)
					civilianShowFluffsKittensDialogue(player, n, 1);
			}
			break;
		case CIVILLIAN_ATTACKABLE:
			say(player, n, "hello there");
			npcsay(player, n, "oh hello, i'm sorry, i'm a bit worn out");
			say(player, n, "busy day?");
			npcsay(player, n, "oh, it's those bleeding mice, they're everywhere",
					"what i really need is a cat, but they're hard to come by nowadays");
			if (!hasAnyCat) {
				say(player, n, "no, you're right, you don't see many around");
			} else {
				if (hasCat) civilianWantCatDialogue(player, n);
				else if (hasKitten) civilianShowKittenDialogue(player, n);
				else if (hasKardiasCat) civilianShowKardiasCatDialogue(player, n, 0);
				else if (hasGertrudesCat) civilianShowGertrudesCatDialogue(player, n, 0);
				else if (hasFluffsKittens && config().WANT_EXTENDED_CATS_BEHAVIOR)
					civilianShowFluffsKittensDialogue(player, n, 0);
			}
			break;
		case CIVILLIAN_PICKPOCKET:
			say(player, n, "hello");
			npcsay(player, n, "i'm a bit busy to talk, sorry");
			say(player, n, "what are you doing?");
			npcsay(player, n, "i need to kill these blasted mice",
					"they're all over the place, i need a cat");
			if (!hasAnyCat) {
				say(player, n, "no you're right, you don't see many around");
			} else {
				if (hasCat) civilianWantCatDialogue(player, n);
				else if (hasKitten) civilianShowKittenDialogue(player, n);
				else if (hasKardiasCat) civilianShowKardiasCatDialogue(player, n, 0);
				else if (hasGertrudesCat) civilianShowGertrudesCatDialogue(player, n, 0);
				else if (hasFluffsKittens && config().WANT_EXTENDED_CATS_BEHAVIOR)
					civilianShowFluffsKittensDialogue(player, n, 0);
			}
			break;
		default:
			break;
		}
	}

	private void civilianWantCatDialogue(Player player, Npc n) {
		int menu = multi(player, n, "i have a cat that i could sell", "nope, they're not easy to get hold of");
		if (menu == 0) {
			npcsay(player, n, "you don't say, can i see it");
			player.message("you reveal the cat in your satchel");
			npcsay(player, n, "hmmm, not bad, not bad at all",
					"looks like it's a lively one");
			say(player, n, "erm ...kind of!");
			npcsay(player, n, "i don't have much in the way of money...",
					"but i do have these...");
			player.message("the peasent shows you a sack of death runes");
			npcsay(player, n, "the dwarfs bring them from the mine for us",
					"tell you what, i'll give you 25 death runes for the cat");
			int sub_menu = multi(player, n, "nope, i'm not parting for that", "ok then, you've got a deal");
			if (sub_menu == 0) {
				npcsay(player, n, "well, i'm not giving you anymore");
			} else if (sub_menu == 1) {
				player.message("you hand over the cat");
				if (player.getCarriedItems().remove(new Item(ItemId.CAT.id())) != -1) {
					player.message("you are given 25 death runes");
					give(player, ItemId.DEATH_RUNE.id(), 25);
					npcsay(player, n, "great, thanks for that");
					say(player, n, "that's ok, take care");
				}
			}
		} else if (menu == 1) {
			// nothing
		}
	}

	private void civilianShowKittenDialogue(Player player, Npc n) {
		int menu = multi(player, n, "i have a kitten that i could sell", "nope, they're not easy to get hold of");
		if (menu == 0) {
			npcsay(player, n, "really, lets have a look");
			player.message("you reveal the kitten in your satchel");
			npcsay(player, n, "hah, that little thing won't catch any mice",
					"i need a fully grown cat");
		} else if (menu == 1) {
			// nothing
		}
	}

	private void civilianShowKardiasCatDialogue(Player player, Npc n, int path) {
		say(player, n, "i have a cat..look" + (path == 1 ? "!" : ""));
		npcsay(player, n, "hmmm..doesn't look like it's seen daylight in years",
				"that's not going to catch any mice");
	}

	//no known method to obtain gertrudes cat
	private void civilianShowGertrudesCatDialogue(Player player, Npc n, int path) {
		say(player, n, "i have a cat..look" + (path == 1 ? "!" : ""));
		npcsay(player, n, "hmmm..doesn't look like it belongs to you",
				"i cannot buy it");
	}

	//very likely did not trigger something, it does not appear to trigger dialogue in OSRS
	//and kardias cat is wikified to have dialogue in OSRS
	private void civilianShowFluffsKittensDialogue(Player player, Npc n, int path) {
		say(player, n, "i have some kittens..look" + (path == 1 ? "!" : ""));
		npcsay(player, n, "hmmm..doesn't look like they are happy",
				"better return them where they were");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return inArray(n.getID(), NpcId.CIVILLIAN_APRON.id(), NpcId.CIVILLIAN_ATTACKABLE.id(), NpcId.CIVILLIAN_PICKPOCKET.id());
	}

}
