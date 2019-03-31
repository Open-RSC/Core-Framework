package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnWallObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnWallObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.closeGenericObject;
import static com.openrsc.server.plugins.Functions.doDoor;
import static com.openrsc.server.plugins.Functions.doGate;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.incQuestReward;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.openGenericObject;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;

public class Waterfall_Quest implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener, ObjectActionListener,
	ObjectActionExecutiveListener, InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener, InvActionListener,
	InvActionExecutiveListener, WallObjectActionListener,
	WallObjectActionExecutiveListener, InvUseOnWallObjectListener,
	InvUseOnWallObjectExecutiveListener {

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
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("@gre@You haved gained 1 quest point!");
		p.message("you have completed the Baxtorian waterfall quest");
		for (int i = 473; i < 478; i++) {
			for (int y = 32; i < 34; i++) {
				if (p.getCache().hasKey("waterfall_" + i + "_" + y)) {
					p.getCache().remove("waterfall_" + i + "_" + y);
				}
			}
		}
		addItem(p, ItemId.MITHRIL_SEED.id(), 40);
		addItem(p, ItemId.GOLD_BAR.id(), 2);
		addItem(p, ItemId.DIAMOND.id(), 2);
		int[] questData = Quests.questData.get(Quests.WATERFALL_QUEST);
		//keep order kosher
		int[] skillIDs = {Skills.STRENGTH, Skills.ATTACK};
		for (int i = 0; i < skillIDs.length; i++) {
			questData[Quests.MAPIDX_SKILL] = skillIDs[i];
			incQuestReward(p, questData, i == (skillIDs.length - 1));
		}
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.ALMERA.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					playerTalk(p, n, "hello madam");
					npcTalk(p, n, "ah, hello there",
						"nice to see an outsider for a change",
						"are you busy young man?, i have a problem");
					int option = showMenu(p, n, "i'm afraid i'm in a rush",
						"how can i help?");
					if (option == 0) {
						npcTalk(p, n, "oh okay, never mind");
					} else if (option == 1) {
						npcTalk(p, n,
							"it's my son hudon, he's always getting into trouble",
							"the boy's convinced there's hidden treasure in the river",
							"and i'm a bit worried about his safety",
							"the poor lad can't even swim");
						playerTalk(p, n,
							"i could go and take a look for you if you like");
						npcTalk(p, n, "would you kind sir?",
							"you can use the small raft out back if you wish",
							"do be careful, the current down stream is very strong");
						p.updateQuestStage(this, 1);
					}
					break;
				case 1:
					playerTalk(p, n, "hello almera");
					npcTalk(p, n, "hello brave adventurer",
						"have you seen my boy yet?");
					playerTalk(p, n,
						"i'm afraid not, but i'm sure he hasn't gone far");
					npcTalk(p, n, "i do hope so",
						"you can't be too careful these days");
					break;
				case 2:
					npcTalk(p, n, "well hello, you're still around then");
					playerTalk(p, n,
						"i saw hudon by the river but he refused to come back with me");
					npcTalk(p, n, "yes he told me",
						"the foolish lad came in drenched to the bone",
						"he had fallen into the waterfall, lucky he wasn't killed",
						"now he can spend the rest of the summer in his room");
					playerTalk(p, n, "any ideas on what i could do while i'm here?");
					npcTalk(p, n,
						"why don't you visit the tourist centre south of the waterfall?");
					break;
				case 3:
					playerTalk(p, n, "hello again almera");
					npcTalk(p, n, "well hello again brave adventurer",
						"are you enjoying the tranquil scenery of these parts?");
					playerTalk(p, n, "yes, very relaxing");
					npcTalk(p, n, "well i'm glad to hear it",
						"the authorities wanted to dig up this whole area for a mine",
						"but the few locals who lived here wouldn't budge and they gave up");
					playerTalk(p, n, "good for you");
					npcTalk(p, n, "good for all of us");
					break;
				case 4:
				case -1:
					playerTalk(p, n, "hello almera");
					npcTalk(p, n, "hello adventurer",
						"how's your treasure hunt going?");
					playerTalk(p, n, "oh, i'm just sight seeing");
					npcTalk(p, n, "no adventurer stays here this long just to sight see",
						"but your business is yours alone",
						"if you need to use the raft go ahead",
						"but please try not crash it this time");
					playerTalk(p, n, "thanks almera");
					break;
			}
		} else if (n.getID() == NpcId.HUDON.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					playerTalk(p, n, "hello there");
					npcTalk(p, n, "what do you want?");
					playerTalk(p, n, "nothing, just passing by");
					break;
				case 1:
					playerTalk(p, n, "Hello hudon",
						"hello son, are you alright?");
					npcTalk(p, n, "don't play nice with me",
						"i know your looking for the treasure to");
					playerTalk(p, n, "your mother sent me to find you hudon");
					npcTalk(p, n, "i'll go home when i've found the treasure",
						"i'm going to be a rich rich man");
					playerTalk(p, n, "where is this treasure you talk of?");
					npcTalk(p, n, "just because i'm small doesn't mean i'm dumb",
						"if i told you, then you'd take it all for yourself");
					playerTalk(p, n, "maybe i could help?");
					npcTalk(p, n, "if you want to help go and tell my mother that i won't be back for a while");
					message(p, "hudon is refusing to leave the waterfall");
					playerTalk(p, n, "ok i'll leave you to it");
					p.updateQuestStage(this, 2);
					break;
				case 2:
					playerTalk(p, n, "so your still here");
					npcTalk(p, n, "i'll find that treasure soon",
						"just you wait and see");
					break;
				case 3:
					playerTalk(p, n, "hello hudon");
					npcTalk(p, n, "oh it's you",
						"trying to find my treasure again are you?");
					playerTalk(p, n, "i didn't know it belonged to you");
					npcTalk(p, n, "it will do when i find it",
						"i just need to get into this blasted waterfall",
						"i've been washed downstream three times already");
					break;
				case 4:
					playerTalk(p, n, "hello again");
					npcTalk(p, n, "not you still, why don't you give up?");
					playerTalk(p, n, "and miss all the fun!");
					npcTalk(p, n, "you do understand that anything you find you have to share it with me");
					playerTalk(p, n, "why's that?");
					npcTalk(p, n, "because i told you about the treasure");
					playerTalk(p, n, "well, i wouldn't count on it");
					npcTalk(p, n, "that's not fair");
					playerTalk(p, n, "neither is life kid");
					break;
				case -1:
					playerTalk(p, n, "hello again");
					npcTalk(p, n, "you stole my treasure i saw you");
					playerTalk(p, n, "i'll make sure it goes to a good cause");
					npcTalk(p, n, "hmmmm");
					break;
			}
		} else if (n.getID() == NpcId.GERALD.id()) {
			if (p.getQuestStage(this) == 0) {
				playerTalk(p, n, "hello there");
				npcTalk(p, n, "good day to you traveller",
					"are you here to fish or just looking around?",
					"i've caught some beauties down here");
				playerTalk(p, n, "really");
				npcTalk(p, n, "the last one was this big");
				message(p, "gerald stretches his arms out to full width");
			} else {
				playerTalk(p, n, "hello");
				npcTalk(p, n, "hello traveller",
					"are you here to fish or to hunt for treasure?");
				playerTalk(p, n, "why do you say that?");
				npcTalk(p, n, "adventurers pass through here every week",
					"they never find anything though");
			}
		} else if (n.getID() == NpcId.HADLEY.id()) {
			if (p.getQuestStage(this) == 0 || p.getQuestStage(this) == 1) {
				hadleyAltDialogue(p, n, HADLEY.ALL);
			} else {
				hadleyMainDialogue(p, n, HADLEY.ALL);
			}
		} else if (n.getID() == NpcId.GOLRIE.id()) {
			if (!hasItem(p, ItemId.GLARIALS_PEBBLE.id(), 1)) {
				playerTalk(p, n, "is your name golrie?");
				npcTalk(p, n, "that's me",
					"i've been stuck in here for weeks",
					"those goblins are trying to steal my families heirlooms",
					"my grandad gave me all sorts of old junk");
				playerTalk(p, n, "do you mind if i have a look?");
				npcTalk(p, n, "no, of course not");
				message(p, "mixed with the junk on the floor",
					"you find glarials pebble");
				addItem(p, ItemId.GLARIALS_PEBBLE.id(), 1);
				playerTalk(p, n, "could i take this old pebble?");
				npcTalk(p, n, "oh that, yes have it",
					"it's just some old elven junk i believe");
				removeItem(p, ItemId.LARGE_KEY.id(), 1);
				message(p, "you give golrie the key");
				npcTalk(p, n, "well thanks again for the key",
					"i think i'll wait in here until those goblins get bored and leave");
				playerTalk(p, n, "okay, take care golrie");
				if (!p.getCache().hasKey("golrie_key")) {
					p.getCache().store("golrie_key", true);
				}

			} else {
				playerTalk(p, n, "is your name golrie?");
				npcTalk(p, n, "that's me",
					"i've been stuck in here for weeks",
					"those goblins are trying to steal my families heirlooms",
					"my grandad gave me all sorts of old junk");
				playerTalk(p, n, "do you mind if i have a look?");
				npcTalk(p, n, "no, of course not");
				removeItem(p, ItemId.LARGE_KEY.id(), 1);
				message(p, "you find nothing of interest",
					"you give golrie the key");
				npcTalk(p, n, "thanks a lot for the key traveller",
					"i think i'll wait in here until those goblins get bored and leave");
				playerTalk(p, n, "okay, take care golrie");
				if (!p.getCache().hasKey("golrie_key")) {
					p.getCache().store("golrie_key", true);
				}
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.ALMERA.id(), NpcId.HUDON.id(),
				NpcId.HADLEY.id(), NpcId.GERALD.id(), NpcId.GOLRIE.id()}, n.getID());
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
									 Player player) {
		return DataConversions.inArray(new int[] {492, 486, 467, 469, BAXTORIAN_CUPBOARD_OPEN, BAXTORIAN_CUPBOARD_CLOSED, 481, 471, 479, 470, 480, 463, 462, 482, 464}, obj.getID());
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 464) {
			message(p, "you board the small raft", "and push off down stream",
				"the raft is pulled down stream by strong currents",
				"you crash into a small land mound");
			p.teleport(662, 463, false);
			Npc hudon = World.getWorld().getNpc(NpcId.HUDON.id(), 0, 2000, 0, 2000);
			if (hudon != null && p.getQuestStage(this) == 1) {
				playerTalk(p, hudon, "hello son, are you okay?");
				npcTalk(p, hudon, "it looks like you need the help");
				playerTalk(p, hudon, "your mum sent me to find you");
				npcTalk(p, hudon, "don't play nice with me");
				npcTalk(p, hudon, "i know your looking for the treasure");
				playerTalk(p, hudon, "where is this treasure you talk of?");
				npcTalk(p, hudon, "just because i'm small doesn't mean i'm dumb");
				npcTalk(p, hudon,
					"if i told you, you would take it all for yourself");
				playerTalk(p, hudon, "maybe i could help");
				npcTalk(p, hudon, "i'm fine alone");
				p.updateQuestStage(this, 2);
				message(p, "hudon is refusing to leave the waterfall");
			}
		} else if (obj.getID() == 463 || obj.getID() == 462
			|| obj.getID() == 482) {
			if (command.equals("jump to next")) {
				message(p, "the tree is too far off to jump to",
					"you need someway to pull yourself across");
			} else if (command.equals("jump off")) {
				message(p, "you jump into the wild rapids");
				p.teleport(654, 485, false);
				p.damage(DataConversions.random(4, 10));
				playerTalk(p, null, "ouch!");
				message(p, "you tumble over the water fall",
					"and are washed up by the river side");
			}
		} else if (obj.getID() == 469) {
			message(p, "you jump into the wild rapids below");
			p.teleport(654, 485, false);
			p.damage(DataConversions.random(4, 10));
			playerTalk(p, null, "ouch!");
			message(p, "you tumble over the water fall",
				"and are washed up by the river side");
		} else if (obj.getID() == 470) {
			message(p, "you search the bookcase");
			if (!p.getInventory().hasItemId(ItemId.BOOK_ON_BAXTORIAN.id())) {
				message(p, "and find a book named 'book on baxtorian'");
				addItem(p, ItemId.BOOK_ON_BAXTORIAN.id(), 1);
			} else
				message(p, "but find nothing of interest");
		} else if (obj.getID() == 481) {
			message(p, "you search the crate");
			if (!p.getInventory().hasItemId(ItemId.LARGE_KEY.id())) {
				message(p, "and find a large key");
				addItem(p, ItemId.LARGE_KEY.id(), 1);
			} else {
				p.message("but find nothing");
			}
		} else if (obj.getID() == 480) {
			Npc n = World.getWorld().getNpc(NpcId.GOLRIE.id(), 663, 668, 3520, 3529);
			if (p.getQuestStage(this) == 0) {
				npcTalk(p, n, "what are you doing down here",
					"leave before you get yourself into trouble");
				return;
			} else if (p.getLocation().getY() <= 3529) {
				doGate(p, obj);
				return;
			} else if (p.getLocation().getY() >= 3530 && p.getCache().hasKey("golrie_key") || p.getQuestStage(this) == -1) {
				p.message("golrie has locked himself in");
				return;
			}

			if (p.getLocation().getY() >= 3530) {
				if (n != null) {
					playerTalk(p, n, "are you ok?");
					npcTalk(p, n, "it's just those blasted hobgoblins",
						"i locked myself in here for protection",
						"but i've left the key somewhere",
						"and now i'm stuck");
					if (!p.getInventory().hasItemId(ItemId.LARGE_KEY.id())) {
						playerTalk(p, n, "okay, i'll have a look for a key");
					} else {
						playerTalk(p, n, "i found a key");
						npcTalk(p, n, "well don't wait all day",
							"give it a try");
					}

					return;
				}
			}

		} else if (obj.getID() == 479) {
			message(p, "the grave is covered in elven script",
				"some of the writing is in common tongue, it reads",
				"here lies glarial, wife of baxtorian",
				"true friend of nature in life and death",
				"may she now rest knowing",
				"only visitors with peaceful intent can enter");
		} else if (obj.getID() == BAXTORIAN_CUPBOARD_OPEN || obj.getID() == BAXTORIAN_CUPBOARD_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openGenericObject(obj, p, BAXTORIAN_CUPBOARD_OPEN, "you open the cupboard");
			} else if (command.equalsIgnoreCase("close")) {
				closeGenericObject(obj, p, BAXTORIAN_CUPBOARD_CLOSED, "you shut the cupboard");
			} else {
				message(p, "you search the cupboard");
				if (!hasItem(p, ItemId.GLARIALS_URN.id(), 1)) {
					p.message("and find a metel urn");
					addItem(p, ItemId.GLARIALS_URN.id(), 1);
				} else {
					p.message("it's empty");
				}
			}
		} else if (obj.getID() == 467) {
			message(p, "you search the coffin");
			if (!hasItem(p, ItemId.GLARIALS_AMULET.id())) {
				message(p, "inside you find a small amulet",
					"you take the amulet and close the coffin");
				addItem(p, ItemId.GLARIALS_AMULET.id(), 1);
			} else {
				message(p, "it's empty");
			}
		} else if (obj.getID() == 471) {
			message(p, "the doors begin to open");

			if (p.getInventory().wielding(ItemId.GLARIALS_AMULET.id())) {
				doGate(p, obj, 63);
				message(p, "You go through the door");
			} else {
				message(p, "suddenly the corridor floods",
					"flushing you back into the river");
				p.teleport(654, 485, false);
				p.damage(DataConversions.random(4, 10));
				playerTalk(p, null, "ouch!");
				message(p, "you tumble over the water fall");
			}
		} else if (obj.getID() == 492) {
			message(p, "you search the crate");
			if (!p.getInventory().hasItemId(ItemId.AN_OLD_KEY.id())) {
				message(p, "you find an old key");
				addItem(p, ItemId.AN_OLD_KEY.id(), 1);
			} else {
				p.message("it is empty");
			}
		} else if (obj.getID() == 135) {
			p.message("the door is locked");
		} else if (obj.getID() == 485) {
			message(p, "as you touch the chalice it tips over",
				"it falls to the floor", "you hear a gushing of water",
				"water floods into the cavern");
			p.damage(DataConversions.random(1, 10));
			p.teleport(654, 485, false);
			message(p, "ouch!", "you tumble over the water fall",
				"and are washed up by the river side");
		} else if (obj.getID() == 486) {
			p.message("you walk through the doorway");
			p.teleport(667, 3279, false);
		}
	}

	public void hadleyMainDialogue(final Player p, final Npc n, int cID) {
		if (cID == -1) {
			playerTalk(p, n, "hello there");
			npcTalk(p, n,
				"are you on holiday?, if so you've come to the right place",
				"i'm hadley the tourist guide, anything you need to know just ask me",
				"we have some of the most unspoilt wildlife and scenery in runescape",
				"people come from miles around to fish in the clear lakes",
				"or to wander the beautiful hill sides");
			playerTalk(p, n, "it is quite pretty");
			npcTalk(p, n, "surely pretty is an understatement kind sir",
				"beautiful, amazing or possibly life changing would be more suitable wording",
				"have your seen the baxtorian waterfall?",
				"it's named after the elf king who was buried beneath");
			cID = hadleyMainMenuOptions(p, n, HADLEY.ALL);
		}
		//can you tell me what happened to the elf king?
		if (cID == 0) {
			npcTalk(p, n, "there are many myths about baxtorian",
				"One popular story is this",
				"after defending his kingdom against the invading dark forces from the west",
				"baxtorian returned to find his wife glarial had been captured by the enemy",
				"this destroyed baxtorian, after years of searching he reclused",
				"to the secret home he had made for glarial under the waterfall",
				"he never came out and it is told that only glarial could enter");
			playerTalk(p, n, "what happened to him?");
			npcTalk(p, n, "oh, i don't know",
				"i believe we have some pages on him upstairs in our archives",
				"if you wish to look at them please be careful, they're all pretty delicate");
			cID = hadleyMainMenuOptions(p, n, HADLEY.WHAT_HAPPENED);
		}
		//where else is worth visiting around here?
		else if (cID == 1) {
			npcTalk(p, n,
				"there's a lovely spot for a picnic on the hill to the north east",
				"there lies a monument to the deceased elven queen glarial",
				"it really is quite pretty");
			playerTalk(p, n, "who was queen glarial?");
			npcTalk(p, n,
				"baxtorians wife, the only person who could also enter the waterfall",
				"she was queen when this land was inhabited by elven kind",
				"glarial was kidnapped while buxtorian was away",
				"but they eventually recovered her body and brought her home to rest");
			playerTalk(p, n, "that's sad");
			npcTalk(p, n,
				"true, i believe there's some information about her upstairs",
				"if you look at them please be careful");
			cID = hadleyMainMenuOptions(p, n, HADLEY.WHERE_ELSE);
		}
		//is there treasure under the waterfall?
		else if (cID == 2) {
			npcTalk(p, n, "ha ha, another treasure hunter",
				"well if there is no one's been able to get to it",
				"they've been searching that river for decades, all to no avail");
			cID = hadleyMainMenuOptions(p, n, HADLEY.IS_THERE_TREAS);
		}
		//thanks then, goodbye
		else if (cID == 3) {
			npcTalk(p, n, "enjoy your visit");
			return;
		}

		if (cID >= 0) {
			hadleyMainDialogue(p, n, cID);
		}
	}

	private int hadleyMainMenuOptions(Player p, Npc n, int discardOp) {
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
		choice = showMenu(p, n, menuOpts);
		if (discardOp != -1 && choice >= discardOp) {
			choice = choice + 1;
		}
		return choice;
	}

	public void hadleyAltDialogue(final Player p, final Npc n, int cID) {
		if (cID == -1) {
			playerTalk(p, n, "hello there");
			npcTalk(p, n,
				"well hello, come in, come in",
				"my names hadley, i'm head of tourism here in hemenster",
				"there's some of the most unspoilt wildlife and scenery in runescape here",
				"people come from miles around to fish in the clear lakes",
				"or to wander the beautiful hill sides");
			playerTalk(p, n, "it is quite pretty");
			npcTalk(p, n, "surely pretty is an understatement kind sir",
				"beautiful, amazing or possibly life changing would be more suitable wording",
				"have your seen the baxtorian waterfall",
				"it's quite a sight");
			cID = hadleyAltMenuOptions(p, n, HADLEY.ALL);
		}
		//what happened to the elf king?
		if (cID == 0) {
			npcTalk(p, n, "baxtorian, i guess he died a long long time ago",
				"it's quite sad really",
				"after defending his kingdom against the invading dark forces from the west",
				"baxtorian returned to find his beautiful wife glarial had been captured",
				"this destroyed baxtorian, after years of searching he became a recluse",
				"in the secret home he had made for glarial under the waterfall",
				"he never came out and to this day no one has managed to get in");
			playerTalk(p, n, "what happened to him?");
			npcTalk(p, n, "no one knows");
			cID = hadleyAltMenuOptions(p, n, HADLEY.WHAT_HAPPENED);
		}
		//where else is worth visiting around here?
		else if (cID == 1) {
			npcTalk(p, n,
				"well, there's a wide variety wildlife",
				"although unfortunately most of it's quite dangerous",
				"please don't feed the goblins");
			playerTalk(p, n, "ok");
			npcTalk(p, n,
				"there is a lovely spot for a picnic on the hill to the north east",
				"there's a monument to the deceased elven queen glarial",
				"it really is quite pretty");
			cID = hadleyAltMenuOptions(p, n, HADLEY.WHERE_ELSE);
		}
		//i don't like nature, it gives me a rash
		else if (cID == 2) {
			npcTalk(p, n, "that's just silly talk");
			return;
		}
		//thanks then, goodbye
		else if (cID == 3) {
			npcTalk(p, n, "enjoy your visit");
			return;
		}

		if (cID >= 0) {
			hadleyAltDialogue(p, n, cID);
		}
	}

	private int hadleyAltMenuOptions(Player p, Npc n, int discardOp) {
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
		choice = showMenu(p, n, menuOpts);
		if (discardOp != -1 && choice >= discardOp) {
			choice = choice + 1;
		}
		return choice;
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
									   Player player) {
		return (item.getID() == ItemId.LARGE_KEY.id() && obj.getID() == 480)
			|| item.getID() == ItemId.AN_OLD_KEY.id() && obj.getID() == 135
			|| (obj.getID() == 462 || obj.getID() == 463
			|| obj.getID() == 462 || obj.getID() == 482)
			&& item.getID() == ItemId.ROPE.id()
			|| (obj.getID() == 479 && item.getID() == ItemId.GLARIALS_PEBBLE.id())
			|| ((obj.getID() >= 473 && obj.getID() <= 478)
			&& (item.getID() == ItemId.WATER_RUNE.id() || item.getID() == ItemId.AIR_RUNE.id() || item.getID() == ItemId.EARTH_RUNE.id()))
			|| obj.getID() == 483 && item.getID() == ItemId.GLARIALS_AMULET.id()
			|| (obj.getID() == 485 && item.getID() == ItemId.GLARIALS_URN.id());
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 480 && item.getID() == ItemId.LARGE_KEY.id()) {
			if (hasItem(p, ItemId.LARGE_KEY.id(), 1)) {
				doGate(p, obj);
			}
		} else if (obj.getID() == 479 && item.getID() == ItemId.GLARIALS_PEBBLE.id()) {
			message(p, "you place the pebble in the gravestones small indent",
				"it fits perfectly");
			if (CANT_GO(p)) {
				message(p, "but nothing happens");
				return;
			} else {
				message(p, "You hear a loud creek",
					"the stone slab slides back revealing a ladder down",
					"you climb down to an underground passage");
				p.teleport(631, 3305, false);
				if (p.getQuestStage(this) == 3) {
					p.updateQuestStage(this, 4);
				}
				return;
			}
		} else if (obj.getID() == 462 || obj.getID() == 463
			|| obj.getID() == 462 || obj.getID() == 482
			&& item.getID() == ItemId.ROPE.id()) {
			message(p, "you tie one end of the rope around the tree",
				"you tie the other end into a loop",
				"and throw it towards the other dead tree");
			if (obj.getID() == 462) {
				message(p, "the rope loops around the tree",
					"you lower yourself into the rapidly flowing stream");
				p.teleport(662, 467, false);
				message(p, "you manage to pull yourself over to the land mound");
			} else if (obj.getID() == 463) {
				message(p, "the rope loops around the tree",
					"you lower yourself into the rapidly flowing stream");
				p.teleport(659, 471, false);
				message(p, "you manage to pull yourself over to the land mound");
			} else if (obj.getID() == 482) {
				message(p, "you gently drop to the rock below",
					"under the waterfall there is a secret passage");
				p.teleport(659, 3305, false);
			}
		} else if (obj.getID() == 135 && item.getID() == ItemId.AN_OLD_KEY.id()) {
			doDoor(obj, p);
		} else if ((obj.getID() >= 473 && obj.getID() <= 478)
			&& (item.getID() == ItemId.WATER_RUNE.id() || item.getID() == ItemId.AIR_RUNE.id() || item.getID() == ItemId.EARTH_RUNE.id())) {
			if (!p.getCache().hasKey(
				"waterfall_" + obj.getID() + "_" + item.getID())) {
				p.message("you place the "
					+ item.getDef().getName().toLowerCase()
					+ " on the stand");
				p.message("the rune stone crumbles into dust");
				p.getCache().store(
					"waterfall_" + obj.getID() + "_" + item.getID(), true);
				p.getInventory().remove(item.getID(), 1);

			} else {
				p.message("you have already placed " + article(item.getDef().getName()) + item.getDef().getName()
					+ " here");
			}
		} else if (obj.getID() == 483 && item.getID() == ItemId.GLARIALS_AMULET.id()) {
			boolean flag = false;
			for (int i = 473; i < 478; i++) {
				for (int y = 32; i < 34; i++) {
					if (!p.getCache().hasKey("waterfall_" + i + "_" + y)) {
						flag = true;
					}
				}
			}
			if (flag) {
				message(p, "you place the amulet around the statue",
					"nothing happens");
			} else {
				message(p, "you place the amulet around the statue",
					"you hear a loud rumble beneath you",
					"the ground raises up before you");
				p.teleport(647, 3267, false);
			}
		} else if (obj.getID() == 485 && item.getID() == ItemId.GLARIALS_URN.id()) {
			message(p, "you carefully poor the ashes in the chalice",
				"as you remove the baxtorian treasure",
				"the chalice remains standing",
				"inside you find a mithril case", "containing 40 seeds",
				"two diamond's and two gold bars");
			removeItem(p, ItemId.GLARIALS_URN.id(), 1);
			p.sendQuestComplete(getQuestId());
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
	public boolean blockInvAction(Item item, Player player) {
		return item.getID() == ItemId.BOOK_ON_BAXTORIAN.id() || item.getID() == ItemId.MITHRIL_SEED.id();
	}

	@Override
	public void onInvAction(Item i, Player p) {
		if (i.getID() == ItemId.MITHRIL_SEED.id()) {
			message(p, "you open the small mithril case");
			if (p.getViewArea().getGameObject(p.getLocation()) != null) {
				p.message("you can't plant a tree here");
				return;
			}
			removeItem(p, ItemId.MITHRIL_SEED.id(), 1);
			message(p, "and drop a seed by your feet");
			GameObject object = new GameObject(Point.location(p.getX(), p.getY()), 490, 0, 0);
			World.getWorld().registerGameObject(object);
			World.getWorld().delayedRemoveObject(object, 60000);
			p.message("a tree magically sprouts around you");
		}
		else if (i.getID() == ItemId.BOOK_ON_BAXTORIAN.id()) {
			message(p, "the book is old with many pages missing",
				"a few are translated from elven into common tongue");
			if (p.getQuestStage(this) == 2) {
				p.updateQuestStage(this, 3);
			}
			int menu = showMenu(p, "the missing relics",
				"the sonnet of baxtorian", "the power of nature",
				"ode to eternity");
			if (menu == 0) {
				ActionSender.sendBox(p,
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
				ActionSender.sendBox(p,
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
				ActionSender.sendBox(p,
					""
						+ "@yel@The Power of Nature@whi@"
						+ "%Glarial and Baxtorian were masters of nature. Trees would grow,"
						+ "%mountains form and rivers flood all to their command. Baxtorian"
						+ "%in particular had perfected rune lore. It was said that he could"
						+ "%use the stones to control the water, earth and air.",
					false);

			} else if (menu == 3) {
				ActionSender.sendBox(p,
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
	public boolean blockWallObjectAction(GameObject obj, Integer click,
										 Player player) {
		return obj.getID() == 135;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 135) {
			message(p, "the door is locked", "you need a key");
		}
	}

	@Override
	public boolean blockInvUseOnWallObject(GameObject obj, Item item,
										   Player player) {
		return obj.getID() == 135 && item.getID() == ItemId.AN_OLD_KEY.id();
	}

	@Override
	public void onInvUseOnWallObject(GameObject obj, Item item, Player player) {
		if (obj.getID() == 135 && item.getID() == ItemId.AN_OLD_KEY.id()) {
			message(player, "you open the door with the key");
			doDoor(obj, player);
			message(player, "You go through the door");
		}
	}

	private boolean CANT_GO(Player p) {
		for (Item item : p.getInventory().getItems()) {
			String name = item.getDef().getName().toLowerCase();
			if (name.contains("dagger") || name.contains("scimitar")
				|| name.contains("bow") || name.contains("mail")
				|| name.contains("plated") || item.getID() == ItemId.RUNE_SKIRT.id()
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

	class HADLEY {
		public static final int ALL = -1;
		public static final int WHAT_HAPPENED = 0;
		public static final int WHERE_ELSE = 1;
		public static final int IS_THERE_TREAS = 2;

	}
}
