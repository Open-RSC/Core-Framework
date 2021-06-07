package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Waterfall_Quest implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	UseLocTrigger,
	OpInvTrigger,
	OpBoundTrigger,
	UseBoundTrigger {

	private static final int BAXTORIAN_CUPBOARD_OPEN = 507;
	private static final int BAXTORIAN_CUPBOARD_CLOSED = 506;

	@Override
	public int getQuestId() {
		return Quests.WATERFALL_QUEST;
	}

	@Override
	public String getQuestName() {
		return "Waterfall Quest (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.WATERFALL_QUEST.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		final QuestReward reward = Quest.WATERFALL_QUEST.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		player.message("you have completed the Baxtorian waterfall quest");
		for (int i = 473; i < 478; i++) {
			for (int y = 32; i < 34; i++) {
				if (player.getCache().hasKey("waterfall_" + i + "_" + y)) {
					player.getCache().remove("waterfall_" + i + "_" + y);
				}
			}
		}
		give(player, ItemId.MITHRIL_SEED.id(), 40);
		give(player, ItemId.GOLD_BAR.id(), 2);
		give(player, ItemId.DIAMOND.id(), 2);
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.ALMERA.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello madam");
					npcsay(player, n, "ah, hello there",
						"nice to see an outsider for a change",
						"are you busy young man?, i have a problem");
					int option = multi(player, n, "i'm afraid i'm in a rush",
						"how can i help?");
					if (option == 0) {
						npcsay(player, n, "oh okay, never mind");
					} else if (option == 1) {
						npcsay(player, n,
							"it's my son hudon, he's always getting into trouble",
							"the boy's convinced there's hidden treasure in the river",
							"and i'm a bit worried about his safety",
							"the poor lad can't even swim");
						say(player, n,
							"i could go and take a look for you if you like");
						npcsay(player, n, "would you kind sir?",
							"you can use the small raft out back if you wish",
							"do be careful, the current down stream is very strong");
						player.updateQuestStage(this, 1);
					}
					break;
				case 1:
					say(player, n, "hello almera");
					npcsay(player, n, "hello brave adventurer",
						"have you seen my boy yet?");
					say(player, n,
						"i'm afraid not, but i'm sure he hasn't gone far");
					npcsay(player, n, "i do hope so",
						"you can't be too careful these days");
					break;
				case 2:
					npcsay(player, n, "well hello, you're still around then");
					say(player, n,
						"i saw hudon by the river but he refused to come back with me");
					npcsay(player, n, "yes he told me",
						"the foolish lad came in drenched to the bone",
						"he had fallen into the waterfall, lucky he wasn't killed",
						"now he can spend the rest of the summer in his room");
					say(player, n, "any ideas on what i could do while i'm here?");
					npcsay(player, n,
						"why don't you visit the tourist centre south of the waterfall?");
					break;
				case 3:
					say(player, n, "hello again almera");
					npcsay(player, n, "well hello again brave adventurer",
						"are you enjoying the tranquil scenery of these parts?");
					say(player, n, "yes, very relaxing");
					npcsay(player, n, "well i'm glad to hear it",
						"the authorities wanted to dig up this whole area for a mine",
						"but the few locals who lived here wouldn't budge and they gave up");
					say(player, n, "good for you");
					npcsay(player, n, "good for all of us");
					break;
				case 4:
				case -1:
					say(player, n, "hello almera");
					npcsay(player, n, "hello adventurer",
						"how's your treasure hunt going?");
					say(player, n, "oh, i'm just sight seeing");
					npcsay(player, n, "no adventurer stays here this long just to sight see",
						"but your business is yours alone",
						"if you need to use the raft go ahead",
						"but please try not crash it this time");
					say(player, n, "thanks almera");
					break;
			}
		} else if (n.getID() == NpcId.HUDON.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello there");
					npcsay(player, n, "what do you want?");
					say(player, n, "nothing, just passing by");
					break;
				case 1:
					say(player, n, "Hello hudon",
						"hello son, are you alright?");
					npcsay(player, n, "don't play nice with me",
						"i know your looking for the treasure to");
					say(player, n, "your mother sent me to find you hudon");
					npcsay(player, n, "i'll go home when i've found the treasure",
						"i'm going to be a rich rich man");
					say(player, n, "where is this treasure you talk of?");
					npcsay(player, n, "just because i'm small doesn't mean i'm dumb",
						"if i told you, then you'd take it all for yourself");
					say(player, n, "maybe i could help?");
					npcsay(player, n, "if you want to help go and tell my mother that i won't be back for a while");
					mes("hudon is refusing to leave the waterfall");
					delay(3);
					say(player, n, "ok i'll leave you to it");
					player.updateQuestStage(this, 2);
					break;
				case 2:
					say(player, n, "so your still here");
					npcsay(player, n, "i'll find that treasure soon",
						"just you wait and see");
					break;
				case 3:
					say(player, n, "hello hudon");
					npcsay(player, n, "oh it's you",
						"trying to find my treasure again are you?");
					say(player, n, "i didn't know it belonged to you");
					npcsay(player, n, "it will do when i find it",
						"i just need to get into this blasted waterfall",
						"i've been washed downstream three times already");
					break;
				case 4:
					say(player, n, "hello again");
					npcsay(player, n, "not you still, why don't you give up?");
					say(player, n, "and miss all the fun!");
					npcsay(player, n, "you do understand that anything you find you have to share it with me");
					say(player, n, "why's that?");
					npcsay(player, n, "because i told you about the treasure");
					say(player, n, "well, i wouldn't count on it");
					npcsay(player, n, "that's not fair");
					say(player, n, "neither is life kid");
					break;
				case -1:
					say(player, n, "hello again");
					npcsay(player, n, "you stole my treasure i saw you");
					say(player, n, "i'll make sure it goes to a good cause");
					npcsay(player, n, "hmmmm");
					break;
			}
		} else if (n.getID() == NpcId.GERALD.id()) {
			if (player.getQuestStage(this) == 0) {
				say(player, n, "hello there");
				npcsay(player, n, "good day to you traveller",
					"are you here to fish or just looking around?",
					"i've caught some beauties down here");
				say(player, n, "really");
				npcsay(player, n, "the last one was this big");
				mes("gerald stretches his arms out to full width");
				delay(3);
			} else {
				say(player, n, "hello");
				npcsay(player, n, "hello traveller",
					"are you here to fish or to hunt for treasure?");
				say(player, n, "why do you say that?");
				npcsay(player, n, "adventurers pass through here every week",
					"they never find anything though");
			}
		} else if (n.getID() == NpcId.HADLEY.id()) {
			if (player.getQuestStage(this) == 0 || player.getQuestStage(this) == 1) {
				hadleyAltDialogue(player, n, HADLEY.ALL);
			} else if (player.getCarriedItems().hasCatalogID(ItemId.BOOK_ON_BAXTORIAN.id(), Optional.of(false))) {
				hadleyBookDialogue(player, n);
			} else {
				hadleyMainDialogue(player, n, HADLEY.ALL);
			}
		} else if (n.getID() == NpcId.GOLRIE.id()) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.GLARIALS_PEBBLE.id(), Optional.of(false))) {
				say(player, n, "is your name golrie?");
				npcsay(player, n, "that's me",
					"i've been stuck in here for weeks",
					"those goblins are trying to steal my families heirlooms",
					"my grandad gave me all sorts of old junk");
				say(player, n, "do you mind if i have a look?");
				npcsay(player, n, "no, of course not");
				mes("mixed with the junk on the floor");
				delay(3);
				mes("you find glarials pebble");
				delay(3);
				give(player, ItemId.GLARIALS_PEBBLE.id(), 1);
				say(player, n, "could i take this old pebble?");
				npcsay(player, n, "oh that, yes have it",
					"it's just some old elven junk i believe");
				player.getCarriedItems().remove(new Item(ItemId.LARGE_KEY.id()));
				mes("you give golrie the key");
				delay(3);
				npcsay(player, n, "well thanks again for the key",
					"i think i'll wait in here until those goblins get bored and leave");
				say(player, n, "okay, take care golrie");
				if (!player.getCache().hasKey("golrie_key")) {
					player.getCache().store("golrie_key", true);
				}

			} else {
				say(player, n, "is your name golrie?");
				npcsay(player, n, "that's me",
					"i've been stuck in here for weeks",
					"those goblins are trying to steal my families heirlooms",
					"my grandad gave me all sorts of old junk");
				say(player, n, "do you mind if i have a look?");
				npcsay(player, n, "no, of course not");
				player.getCarriedItems().remove(new Item(ItemId.LARGE_KEY.id()));
				mes("you find nothing of interest");
				delay(3);
				mes("you give golrie the key");
				delay(3);
				npcsay(player, n, "thanks a lot for the key traveller",
					"i think i'll wait in here until those goblins get bored and leave");
				say(player, n, "okay, take care golrie");
				if (!player.getCache().hasKey("golrie_key")) {
					player.getCache().store("golrie_key", true);
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.ALMERA.id(), NpcId.HUDON.id(),
				NpcId.HADLEY.id(), NpcId.GERALD.id(), NpcId.GOLRIE.id()}, n.getID());
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return DataConversions.inArray(new int[] {492, 486, 467, 469, BAXTORIAN_CUPBOARD_OPEN, BAXTORIAN_CUPBOARD_CLOSED, 481, 471, 479, 470, 480, 463, 462, 482, 464}, obj.getID());
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 464) {
			mes("you board the small raft");
			delay(3);
			mes("and push off down stream");
			delay(3);
			mes("the raft is pulled down stream by strong currents");
			delay(3);
			mes("you crash into a small land mound");
			delay(3);
			player.teleport(662, 463, false);
			Npc hudon = player.getWorld().getNpc(NpcId.HUDON.id(), 0, 2000, 0, 2000);
			if (hudon != null && player.getQuestStage(this) == 1) {
				say(player, hudon, "hello son, are you okay?");
				npcsay(player, hudon, "it looks like you need the help");
				say(player, hudon, "your mum sent me to find you");
				npcsay(player, hudon, "don't play nice with me");
				npcsay(player, hudon, "i know your looking for the treasure");
				say(player, hudon, "where is this treasure you talk of?");
				npcsay(player, hudon, "just because i'm small doesn't mean i'm dumb");
				npcsay(player, hudon,
					"if i told you, you would take it all for yourself");
				say(player, hudon, "maybe i could help");
				npcsay(player, hudon, "i'm fine alone");
				player.updateQuestStage(this, 2);
				mes("hudon is refusing to leave the waterfall");
				delay(3);
			}
		} else if (obj.getID() == 463 || obj.getID() == 462
			|| obj.getID() == 482) {
			if (command.equals("jump to next")) {
				mes("the tree is too far off to jump to");
				delay(3);
				mes("you need someway to pull yourself across");
				delay(3);
			} else if (command.equals("jump off")) {
				mes("you jump into the wild rapids");
				delay(3);
				player.teleport(654, 485, false);
				player.damage(DataConversions.random(4, 10));
				say(player, null, "ouch!");
				mes("you tumble over the water fall");
				delay(3);
				mes("and are washed up by the river side");
				delay(3);
			}
		} else if (obj.getID() == 469) {
			mes("you jump into the wild rapids below");
			delay(3);
			player.teleport(654, 485, false);
			player.damage(DataConversions.random(4, 10));
			say(player, null, "ouch!");
			mes("you tumble over the water fall");
			delay(3);
			mes("and are washed up by the river side");
			delay(3);
		} else if (obj.getID() == 470) {
			mes("you search the bookcase");
			delay(3);
			if (!player.getCarriedItems().hasCatalogID(ItemId.BOOK_ON_BAXTORIAN.id())) {
				mes("and find a book named 'book on baxtorian'");
				delay(3);
				give(player, ItemId.BOOK_ON_BAXTORIAN.id(), 1);
			} else
				mes("but find nothing of interest");
				delay(3);
		} else if (obj.getID() == 481) {
			if (player.getQuestStage(this) == 0) {
				player.message("the crate is empty");
				return;
			}
			mes("you search the crate");
			delay(3);
			if (!player.getCarriedItems().hasCatalogID(ItemId.LARGE_KEY.id())) {
				mes("and find a large key");
				delay(3);
				give(player, ItemId.LARGE_KEY.id(), 1);
			} else {
				player.message("but find nothing");
			}
		} else if (obj.getID() == 480) {
			Npc n = player.getWorld().getNpc(NpcId.GOLRIE.id(), 663, 668, 3520, 3529);
			if (player.getQuestStage(this) == 0) {
				npcsay(player, n, "what are you doing down here",
					"leave before you get yourself into trouble");
				return;
			} else if (player.getLocation().getY() <= 3529) {
				doGate(player, obj);
				return;
			} else if (player.getLocation().getY() >= 3530 && player.getCache().hasKey("golrie_key") || player.getQuestStage(this) == -1) {
				player.message("golrie has locked himself in");
				return;
			}

			if (player.getLocation().getY() >= 3530) {
				if (n != null) {
					say(player, n, "are you ok?");
					npcsay(player, n, "it's just those blasted hobgoblins",
						"i locked myself in here for protection",
						"but i've left the key somewhere",
						"and now i'm stuck");
					if (!player.getCarriedItems().hasCatalogID(ItemId.LARGE_KEY.id())) {
						say(player, n, "okay, i'll have a look for a key");
					} else {
						say(player, n, "i found a key");
						npcsay(player, n, "well don't wait all day",
							"give it a try");
					}

					return;
				}
			}

		} else if (obj.getID() == 479) {
			mes("the grave is covered in elven script");
			delay(3);
			mes("some of the writing is in common tongue, it reads");
			delay(3);
			mes("here lies glarial, wife of baxtorian");
			delay(3);
			mes("true friend of nature in life and death");
			delay(3);
			mes("may she now rest knowing");
			delay(3);
			mes("only visitors with peaceful intent can enter");
			delay(3);
		} else if (obj.getID() == BAXTORIAN_CUPBOARD_OPEN || obj.getID() == BAXTORIAN_CUPBOARD_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openGenericObject(obj, player, BAXTORIAN_CUPBOARD_OPEN, "you open the cupboard");
			} else if (command.equalsIgnoreCase("close")) {
				closeGenericObject(obj, player, BAXTORIAN_CUPBOARD_CLOSED, "you shut the cupboard");
			} else {
				mes("you search the cupboard");
				delay(3);
				if (!player.getCarriedItems().hasCatalogID(ItemId.GLARIALS_URN.id(), Optional.empty())) {
					player.message("and find a metel urn");
					give(player, ItemId.GLARIALS_URN.id(), 1);
				} else {
					player.message("it's empty");
				}
			}
		} else if (obj.getID() == 467) {
			mes("you search the coffin");
			delay(3);
			if (!player.getCarriedItems().hasCatalogID(ItemId.GLARIALS_AMULET.id(), Optional.empty())) {
				mes("inside you find a small amulet");
				delay(3);
				mes("you take the amulet and close the coffin");
				delay(3);
				give(player, ItemId.GLARIALS_AMULET.id(), 1);
			} else {
				mes("it's empty");
				delay(3);
			}
		} else if (obj.getID() == 471) {
			mes("the doors begin to open");
			delay(3);
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.GLARIALS_AMULET.id())) {
				doGate(player, obj, 63);
				mes("You go through the door");
				delay(3);
			} else {
				mes("suddenly the corridor floods");
				delay(3);
				mes("flushing you back into the river");
				delay(3);
				player.teleport(654, 485, false);
				player.damage(DataConversions.random(4, 10));
				say(player, null, "ouch!");
				mes("you tumble over the water fall");
				delay(3);
			}
		} else if (obj.getID() == 492) {
			mes("you search the crate");
			delay(3);
			if (!player.getCarriedItems().hasCatalogID(ItemId.AN_OLD_KEY.id())) {
				mes("you find an old key");
				delay(3);
				give(player, ItemId.AN_OLD_KEY.id(), 1);
			} else {
				player.message("it is empty");
			}
		} else if (obj.getID() == 135) {
			player.message("the door is locked");
		} else if (obj.getID() == 485) {
			if (player.getQuestStage(this) == -1) {
				mes("the chalice is empty");
				delay(3);
				mes("it will not move");
			} else {
				mes("as you touch the chalice it tips over");
				delay(3);
				mes("it falls to the floor");
				delay(3);
				mes("you hear a gushing of water");
				delay(3);
				mes("water floods into the cavern");
				delay(3);
				player.damage(DataConversions.random(1, 10));
				player.teleport(654, 485, false);
				mes("ouch!");
				delay(3);
				mes("you tumble over the water fall");
				delay(3);
				mes("and are washed up by the river side");
				delay(3);
			}
		} else if (obj.getID() == 486) {
			player.message("you walk through the doorway");
			player.teleport(667, 3279, false);
		}
	}

	public void hadleyBookDialogue(final Player player, final Npc n) {
		say(player, n, "hello there");
		npcsay(player, n, "i hope you're enjoying your stay",
			"there should be lots of useful infomation in that book",
			"places to go, people to see");
		int cID = hadleyMainMenuOptions(player, n, HADLEY.ALL);

		if (cID >= 0) {
			hadleyMainDialogue(player, n, cID);
		}
	}

	public void hadleyMainDialogue(final Player player, final Npc n, int cID) {
		if (cID == -1) {
			say(player, n, "hello there");
			npcsay(player, n,
				"are you on holiday?, if so you've come to the right place",
				"i'm hadley the tourist guide, anything you need to know just ask me",
				"we have some of the most unspoilt wildlife and scenery in runescape",
				"people come from miles around to fish in the clear lakes",
				"or to wander the beautiful hill sides");
			say(player, n, "it is quite pretty");
			npcsay(player, n, "surely pretty is an understatement kind sir",
				"beautiful, amazing or possibly life changing would be more suitable wording",
				"have your seen the baxtorian waterfall?",
				"it's named after the elf king who was buried beneath");
			cID = hadleyMainMenuOptions(player, n, HADLEY.ALL);
		}
		//can you tell me what happened to the elf king?
		if (cID == 0) {
			npcsay(player, n, "there are many myths about baxtorian",
				"One popular story is this",
				"after defending his kingdom against the invading dark forces from the west",
				"baxtorian returned to find his wife glarial had been captured by the enemy",
				"this destroyed baxtorian, after years of searching he reclused",
				"to the secret home he had made for glarial under the waterfall",
				"he never came out and it is told that only glarial could enter");
			say(player, n, "what happened to him?");
			npcsay(player, n, "oh, i don't know",
				"i believe we have some pages on him upstairs in our archives",
				"if you wish to look at them please be careful, they're all pretty delicate");
			cID = hadleyMainMenuOptions(player, n, HADLEY.WHAT_HAPPENED);
		}
		//where else is worth visiting around here?
		else if (cID == 1) {
			npcsay(player, n,
				"there's a lovely spot for a picnic on the hill to the north east",
				"there lies a monument to the deceased elven queen glarial",
				"it really is quite pretty");
			say(player, n, "who was queen glarial?");
			npcsay(player, n,
				"baxtorians wife, the only person who could also enter the waterfall",
				"she was queen when this land was inhabited by elven kind",
				"glarial was kidnapped while buxtorian was away",
				"but they eventually recovered her body and brought her home to rest");
			say(player, n, "that's sad");
			npcsay(player, n,
				"true, i believe there's some information about her upstairs",
				"if you look at them please be careful");
			cID = hadleyMainMenuOptions(player, n, HADLEY.WHERE_ELSE);
		}
		//is there treasure under the waterfall?
		else if (cID == 2) {
			npcsay(player, n, "ha ha, another treasure hunter",
				"well if there is no one's been able to get to it",
				"they've been searching that river for decades, all to no avail");
			cID = hadleyMainMenuOptions(player, n, HADLEY.IS_THERE_TREAS);
		}
		//thanks then, goodbye
		else if (cID == 3) {
			npcsay(player, n, "enjoy your visit");
			return;
		}

		if (cID >= 0) {
			hadleyMainDialogue(player, n, cID);
		}
	}

	private int hadleyMainMenuOptions(Player player, Npc n, int discardOp) {
		String menuOpts[];
		int choice;
		if (discardOp == 0) {
			menuOpts = new String[]{"where else is worth visiting around here?",
				"is there treasure under the waterfall?",
				"thanks then, goodbye"};
		} else if (discardOp == 1) {
			menuOpts = new String[]{"can you tell me what happened to the elf king?",
				"is there treasure under the waterfall?",
				"thanks then, goodbye"};
		} else if (discardOp == 2) {
			menuOpts = new String[]{"can you tell me what happened to the elf king?",
				"where else is worth visiting around here?",
				"thanks then, goodbye"};
		} else if (discardOp == 3) {
			menuOpts = new String[]{"can you tell me what happened to the elf king?",
				"where else is worth visiting around here?",
				"is there treasure under the waterfall?"};
		} else {
			menuOpts = new String[]{"can you tell me what happened to the elf king?",
				"where else is worth visiting around here?",
				"is there treasure under the waterfall?",
				"thanks then, goodbye"};
		}
		choice = multi(player, n, menuOpts);
		if (discardOp != -1 && choice >= discardOp) {
			choice = choice + 1;
		}
		return choice;
	}

	public void hadleyAltDialogue(final Player player, final Npc n, int cID) {
		if (cID == -1) {
			say(player, n, "hello there");
			npcsay(player, n,
				"well hello, come in, come in",
				"my names hadley, i'm head of tourism here in hemenster",
				"there's some of the most unspoilt wildlife and scenery in runescape here",
				"people come from miles around to fish in the clear lakes",
				"or to wander the beautiful hill sides");
			say(player, n, "it is quite pretty");
			npcsay(player, n, "surely pretty is an understatement kind sir",
				"beautiful, amazing or possibly life changing would be more suitable wording",
				"have your seen the baxtorian waterfall",
				"it's quite a sight",
				"named after the elf king who was buried beneath");
			cID = hadleyAltMenuOptions(player, n, HADLEY.ALL);
		}
		//what happened to the elf king?
		if (cID == 0) {
			npcsay(player, n, "baxtorian, i guess he died a long long time ago",
				"it's quite sad really",
				"after defending his kingdom against the invading dark forces from the west",
				"baxtorian returned to find his beautiful wife glarial had been captured",
				"this destroyed baxtorian, after years of searching he became a recluse",
				"in the secret home he had made for glarial under the waterfall",
				"he never came out and to this day no one has managed to get in");
			say(player, n, "what happened to him?");
			npcsay(player, n, "no one knows");
			cID = hadleyAltMenuOptions(player, n, HADLEY.WHAT_HAPPENED);
		}
		//where else is worth visiting around here?
		else if (cID == 1) {
			npcsay(player, n,
				"well, there's a wide variety wildlife",
				"although unfortunately most of it's quite dangerous",
				"please don't feed the goblins");
			say(player, n, "ok");
			npcsay(player, n,
				"there is a lovely spot for a picnic on the hill to the north east",
				"there's a monument to the deceased elven queen glarial",
				"it really is quite pretty");
			cID = hadleyAltMenuOptions(player, n, HADLEY.WHERE_ELSE);
		}
		//i don't like nature, it gives me a rash
		else if (cID == 2) {
			npcsay(player, n, "that's just silly talk");
			return;
		}
		//thanks then, goodbye
		else if (cID == 3) {
			npcsay(player, n, "enjoy your visit");
			return;
		}

		if (cID >= 0) {
			hadleyAltDialogue(player, n, cID);
		}
	}

	private int hadleyAltMenuOptions(Player player, Npc n, int discardOp) {
		String menuOpts[];
		int choice;
		if (discardOp == 0) {
			menuOpts = new String[]{"where else is worth visiting around here?",
				"i don't like nature, it gives me a rash",
				"thanks then, goodbye"};
		} else if (discardOp == 1) {
			menuOpts = new String[]{"what happened to the elf king?",
				"i don't like nature, it gives me a rash",
				"thanks then, goodbye"};
		} else if (discardOp == 2) {
			menuOpts = new String[]{"what happened to the elf king?",
				"where else is worth visiting around here?",
				"thanks then, goodbye"};
		} else if (discardOp == 3) {
			menuOpts = new String[]{"what happened to the elf king?",
				"where else is worth visiting around here?",
				"i don't like nature, it gives me a rash"};
		} else {
			menuOpts = new String[]{"what happened to the elf king?",
				"where else is worth visiting around here?",
				"i don't like nature, it gives me a rash",
				"thanks then, goodbye"};
		}
		choice = multi(player, n, menuOpts);
		if (discardOp != -1 && choice >= discardOp) {
			choice = choice + 1;
		}
		return choice;
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (item.getCatalogId() == ItemId.LARGE_KEY.id() && obj.getID() == 480)
			|| item.getCatalogId() == ItemId.AN_OLD_KEY.id() && obj.getID() == 135
			|| (obj.getID() == 462 || obj.getID() == 463
			|| obj.getID() == 462 || obj.getID() == 482)
			&& item.getCatalogId() == ItemId.ROPE.id()
			|| (obj.getID() == 479 && item.getCatalogId() == ItemId.GLARIALS_PEBBLE.id())
			|| ((obj.getID() >= 473 && obj.getID() <= 478)
			&& (item.getCatalogId() == ItemId.WATER_RUNE.id() || item.getCatalogId() == ItemId.AIR_RUNE.id() || item.getCatalogId() == ItemId.EARTH_RUNE.id()))
			|| obj.getID() == 483 && item.getCatalogId() == ItemId.GLARIALS_AMULET.id()
			|| (obj.getID() == 485 && item.getCatalogId() == ItemId.GLARIALS_URN.id());
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == 480 && item.getCatalogId() == ItemId.LARGE_KEY.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.LARGE_KEY.id(), Optional.of(false))) {
				doGate(player, obj);
			}
		} else if (obj.getID() == 479 && item.getCatalogId() == ItemId.GLARIALS_PEBBLE.id()) {
			mes("you place the pebble in the gravestones small indent");
			delay(3);
			mes("it fits perfectly");
			delay(3);
			if (CANT_GO(player)) {
				mes("but nothing happens");
				delay(3);
				return;
			} else {
				mes("You hear a loud creek");
				delay(3);
				mes("the stone slab slides back revealing a ladder down");
				delay(3);
				mes("you climb down to an underground passage");
				delay(3);
				player.teleport(631, 3305, false);
				if (player.getQuestStage(this) == 3) {
					player.updateQuestStage(this, 4);
				}
				return;
			}
		} else if (obj.getID() == 462 || obj.getID() == 463
			|| obj.getID() == 462 || obj.getID() == 482
			&& item.getCatalogId() == ItemId.ROPE.id()) {
			mes("you tie one end of the rope around the tree");
			delay(3);
			mes("you tie the other end into a loop");
			delay(3);
			mes("and throw it towards the other dead tree");
			delay(3);
			if (obj.getID() == 462) {
				mes("the rope loops around the tree");
				delay(3);
				mes("you lower yourself into the rapidly flowing stream");
				delay(3);
				player.teleport(662, 467, false);
				mes("you manage to pull yourself over to the land mound");
				delay(3);
			} else if (obj.getID() == 463) {
				mes("the rope loops around the tree");
				delay(3);
				mes("you lower yourself into the rapidly flowing stream");
				delay(3);
				player.teleport(659, 471, false);
				mes("you manage to pull yourself over to the land mound");
				delay(3);
			} else if (obj.getID() == 482) {
				mes("you gently drop to the rock below");
				delay(3);
				mes("under the waterfall there is a secret passage");
				delay(3);
				player.teleport(659, 3305, false);
			}
		} else if (obj.getID() == 135 && item.getCatalogId() == ItemId.AN_OLD_KEY.id()) {
			doDoor(obj, player);
		} else if ((obj.getID() >= 473 && obj.getID() <= 478)
			&& (item.getCatalogId() == ItemId.WATER_RUNE.id() || item.getCatalogId() == ItemId.AIR_RUNE.id() || item.getCatalogId() == ItemId.EARTH_RUNE.id())) {
			if (!player.getCache().hasKey(
				"waterfall_" + obj.getID() + "_" + item.getCatalogId())) {
				player.message("you place the "
					+ item.getDef(player.getWorld()).getName().toLowerCase()
					+ " on the stand");
				player.message("the rune stone crumbles into dust");
				player.getCache().store(
					"waterfall_" + obj.getID() + "_" + item.getCatalogId(), true);
				player.getCarriedItems().remove(new Item(item.getCatalogId()));

			} else {
				player.message("you have already placed " + article(item.getDef(player.getWorld()).getName()) + item.getDef(player.getWorld()).getName()
					+ " here");
			}
		} else if (obj.getID() == 483 && item.getCatalogId() == ItemId.GLARIALS_AMULET.id()) {
			boolean flag = false;
			for (int i = 473; i < 478; i++) {
				for (int y = 32; i < 34; i++) {
					if (!player.getCache().hasKey("waterfall_" + i + "_" + y)) {
						flag = true;
					}
				}
			}
			if (flag) {
				mes("you place the amulet around the statue");
				delay(3);
				mes("nothing happens");
				delay(3);
			} else {
				mes("you place the amulet around the statue");
				delay(3);
				mes("you hear a loud rumble beneath you");
				delay(3);
				mes("the ground raises up before you");
				delay(3);
				player.teleport(647, 3267, false);
			}
		} else if (obj.getID() == 485 && item.getCatalogId() == ItemId.GLARIALS_URN.id()) {
			if (player.getQuestStage(this) == -1) {
				// lost info, but possible message
				player.message("You have already completed this quest");
				return;
			}
			mes("you carefully poor the ashes in the chalice");
			player.getCarriedItems().remove(new Item(ItemId.GLARIALS_URN.id()));
			delay(3);
			give(player, ItemId.GLARIALS_URN_EMPTY.id(), 1);
			mes("as you remove the baxtorian treasure");
			delay(3);
			mes("the chalice remains standing");
			delay(3);
			mes("inside you find a mithril case");
			delay(3);
			mes("containing 40 seeds");
			delay(3);
			mes("two diamond's and two gold bars");
			delay(3);
			player.sendQuestComplete(getQuestId());
		}
	}

	private String article(String word) {
		char c = word.toLowerCase().charAt(0);
		if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
			return "an";
		} else {
			return "a";
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.BOOK_ON_BAXTORIAN.id() || item.getCatalogId() == ItemId.MITHRIL_SEED.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item i, String command) {
		if (i.getCatalogId() == ItemId.MITHRIL_SEED.id()) {
			mes("you open the small mithril case");
			delay(3);
			if (player.getViewArea().getGameObject(player.getLocation()) != null) {
				player.message("you can't plant a tree here");
				return;
			}
			player.getCarriedItems().remove(new Item(ItemId.MITHRIL_SEED.id()));
			mes("and drop a seed by your feet");
			delay(3);
			GameObject object = new GameObject(player.getWorld(), Point.location(player.getX(), player.getY()), 490, 0, 0);
			player.getWorld().registerGameObject(object);
			player.getWorld().delayedRemoveObject(object, 60000);
			player.message("a tree magically sprouts around you");
		}
		else if (i.getCatalogId() == ItemId.BOOK_ON_BAXTORIAN.id()) {
			mes("the book is old with many pages missing");
			delay(3);
			mes("a few are translated from elven into common tongue");
			delay(3);
			if (player.getQuestStage(this) == 2) {
				player.updateQuestStage(this, 3);
			}
			int menu = multi(player, "the missing relics",
				"the sonnet of baxtorian", "the power of nature",
				"ode to eternity");
			if (menu == 0) {
				ActionSender.sendBox(player,
					"@yel@The Missing Relics@whi@% %"
						+ "Many artifacts of elven history were lost after the second age. % "
						+ "The greatest loss to our collection of elf history were the hidden%"
						+ "treasures of Baxtorian."
						+ "% %Some believe these treasures are still unclaimed, but it is more"
						+ "%commonly believed that dwarf miners recovered the treasure at"
						+ "%the beginning of the third age. "
						+ "% %Another great loss was Glarial's pebble a key which allowed her"
						+ "% ancestors to visit her tomb. The stone was stolen by a gnome"
						+ "% family over a century ago."
						+ "% % It is believed that the gnomes ancestor Glorie still has the stone"
						+ "hidden in the caves of the gnome tree village.",
					true);
			} else if (menu == 1) {
				ActionSender.sendBox(player,
					"@yel@The Sonnet of Baxtorian@whi@"
						+ "% %The love between Baxtorian and Glarial was said to have lasted"
						+ "%over a century. They lived a peaceful life learning and teaching "
						+ "%the laws of nature."
						+ "% %When Baxtorian's kingdom was invaded by the dark forces he left"
						+ "%on a five year campaign. He returned to find his people"
						+ "%slaughtered and his wife taken by the enemy."
						+ "% %After years of searching for his love he finally gave up, he"
						+ "%returned to the home he made for himself and Glarial under the "
						+ "% baxtorian waterfall. Once he entered he never returned."
						+ "% % Only Glarial had the power to also enter the waterfall. Since"
						+ "%Baxtorian entered no one but her can follow him in, it's as if the"
						+ "%powers of nature still work to protect him.",
					true);
			} else if (menu == 2) {
				ActionSender.sendBox(player,
					""
						+ "@yel@The Power of Nature@whi@"
						+ "%Glarial and Baxtorian were masters of nature. Trees would grow,"
						+ "%mountains form and rivers flood all to their command. Baxtorian"
						+ "%in particular had perfected rune lore. It was said that he could"
						+ "%use the stones to control the water, earth and air.",
					false);

			} else if (menu == 3) {
				ActionSender.sendBox(player,
					"@yel@Ode to Eternity@whi@"
						+ "% %@yel@A Short Piece Written by Baxtorian himself@whi@"
						+ "% % What care I for this mortal coil, where treasures are yet so frail,"
						+ "%for it is you that is my life blood, the wine to my holy grail"
						+ "% %and if I see the judgement day, when the gods fill the air with"
						+ "% dust, I'll happily choke on your memory, as my kingdom turns to "
						+ "rust.", true);
			}
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == 135;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 135) {
			mes("the door is locked");
			delay(3);
			mes("you need a key");
			delay(3);
		}
	}

	@Override
	public boolean blockUseBound(Player player, GameObject obj, Item item) {
		return obj.getID() == 135 && item.getCatalogId() == ItemId.AN_OLD_KEY.id();
	}

	@Override
	public void onUseBound(Player player, GameObject obj, Item item) {
		if (obj.getID() == 135 && item.getCatalogId() == ItemId.AN_OLD_KEY.id()) {
			mes("you open the door with the key");
			doDoor(obj, player);
			mes("You go through the door");
			delay(3);
		}
	}

	private boolean CANT_GO(Player player) {
		synchronized(player.getCarriedItems().getInventory().getItems()) {
			for (Item item : player.getCarriedItems().getInventory().getItems()) {
				String name = item.getDef(player.getWorld()).getName().toLowerCase();
				if (name.contains("dagger") || name.contains("scimitar")
					|| name.contains("bow") || name.contains("mail")
					|| name.contains("plated") || item.getCatalogId() == ItemId.RUNE_SKIRT.id()
					|| name.contains("shield") || (name.contains("sword")
					&& !name.equalsIgnoreCase("Swordfish") && !name.equalsIgnoreCase("Burnt Swordfish") && !name.equalsIgnoreCase("Raw Swordfish"))
					|| name.contains("mace") || name.contains("helmet")
					|| name.contains("axe") || name.contains("throwing knife")
					|| name.contains("spear")) {
					return true;
				}
			}
			return false;
		}
	}

	class HADLEY {
		public static final int ALL = -1;
		public static final int WHAT_HAPPENED = 0;
		public static final int WHERE_ELSE = 1;
		public static final int IS_THERE_TREAS = 2;

	}
}
