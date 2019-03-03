package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class Gnomes implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return inArray(n.getID(), NpcId.GNOME_LOCAL_RED.id(), NpcId.GNOME_LOCAL_PURPLE.id(), NpcId.GNOME_CHILD_GREEN_PURPLE.id(),
				NpcId.GNOME_CHILD_PURPLE_PINK.id(), NpcId.GNOME_CHILD_PINK_GREEN.id(), NpcId.GNOME_CHILD_CREAM_PURPLE.id());
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GNOME_LOCAL_RED.id()) {
			playerTalk(p, n, "hello");
			int chatRandom = p.getRandom().nextInt(4);
			switch (chatRandom) {
				case 0:
					npcTalk(p, n, "can't stop sorry, busy, busy, busy");
					p.message("the gnome is too busy to talk");
					break;
				case 1:
					npcTalk(p, n, "hello traveller",
						"are you enjoying your stay?");
					playerTalk(p, n, "it's a nice place");
					npcTalk(p, n, "yes, we try to keep it that way");
					break;
				case 2:
					npcTalk(p, n, "i don't think i can take much more");
					playerTalk(p, n, "what's wrong?");
					npcTalk(p, n, "it's just the wife, she won't stop moaning");
					playerTalk(p, n, "maybe you should give her less to moan about");
					npcTalk(p, n, "she'll always find something");
					break;
				case 3:
					npcTalk(p, n, "how's life treating you");
					playerTalk(p, n, "not bad, not bad at all");
					npcTalk(p, n, "it's good to see a human with a positive attitude");
					break;
			}
		}
		else if (n.getID() == NpcId.GNOME_LOCAL_PURPLE.id()) {
			playerTalk(p, n, "hello");
			int chatRandom = p.getRandom().nextInt(4);
			switch (chatRandom) {
				case 0:
					npcTalk(p, n, "hello traveller",
						"are you eating properly?, you look tired");
					playerTalk(p, n, "i think so");
					npcTalk(p, n, "here get this worm down you",
						"it'll do you the world of good");
					p.message("the gnome gives you a worm");
					addItem(p, ItemId.KING_WORM.id(), 1);
					playerTalk(p, n, "thanks!");
					break;
				case 1:
					playerTalk(p, n, "how are you?");
					npcTalk(p, n, "not bad, a little worn out");
					playerTalk(p, n, "maybe you should have a lie down");
					npcTalk(p, n, "with three kids to feed i've no time for naps");
					playerTalk(p, n, "sounds like hard work");
					npcTalk(p, n, "it is but they're worth it");
					break;
				case 2:
					npcTalk(p, n, "Some people grumble because roses have thorns",
						"I'm thankful that thorns have roses");
					playerTalk(p, n, "good attitude");
					break;
				// case 3 nothing but hello.
			}
		}
		else if (n.getID() == NpcId.GNOME_CHILD_GREEN_PURPLE.id() || n.getID() == NpcId.GNOME_CHILD_CREAM_PURPLE.id()) {
			playerTalk(p, n, "hi there");
			int chatRandom = p.getRandom().nextInt(6);
			switch (chatRandom) {
				case 0:
					npcTalk(p, n, "hello, why aren't you green?");
					playerTalk(p, n, "i don't know");
					npcTalk(p, n, "maybe you should eat more vegtables");
					break;
				case 1:
					npcTalk(p, n, "she loves me");
					playerTalk(p, n, "really");
					npcTalk(p, n, "she does i tell you",
						"she really loves me");
					break;
				// case 2 nothing by hi there.
				case 3:
					p.message("the gnome appears to be singing");
					npcTalk(p, n, "oh baby, oh my sweet");
					playerTalk(p, n, "are you talking to me?");
					npcTalk(p, n, "no, i'm just singing",
						"i'm gonna sweep you of your feet");
					break;
				case 4:
					npcTalk(p, n, "hello, would you like a worm?");
					playerTalk(p, n, "erm ok");
					p.message("the gnome gives you a worm");
					addItem(p, ItemId.KING_WORM.id(), 1);
					playerTalk(p, n, "thanks");
					npcTalk(p, n, "in the gnome village those who are needy..",
						"recieve what they need, and those who are able..",
						"... give what they can");
					break;
				case 5:
					npcTalk(p, n, "low");
					playerTalk(p, n, "what?");
					npcTalk(p, n, "when?");
					playerTalk(p, n, "cheeky");
					npcTalk(p, n, "hee hee");
					break;
			}
		}
		else if (n.getID() == NpcId.GNOME_CHILD_PURPLE_PINK.id()) {
			playerTalk(p, n, "hello little man");
			int chatRandom = p.getRandom().nextInt(9);
			switch (chatRandom) {
				case 0:
					playerTalk(p, n, "how are you");
					npcTalk(p, n, "a warning traveller, the new world..",
						"..will rise from the underground");
					playerTalk(p, n, "what do you mean underground?");
					npcTalk(p, n, "just a warning");
					break;
				case 1:
					npcTalk(p, n, "a little inaccuracy sometimes...",
						"..saves tons of explanation");
					playerTalk(p, n, "true");
					break;
				case 2:
					playerTalk(p, n, "you look happy");
					npcTalk(p, n, "i'm always at peace with myself");
					playerTalk(p, n, "how do you manage that?");
					npcTalk(p, n, "i know, therefore i am");
					break;
				case 3:
					npcTalk(p, n, "hello, would you like a worm?");
					playerTalk(p, n, "erm ok");
					p.message("the gnome gives you a worm");
					addItem(p, ItemId.KING_WORM.id(), 1);
					playerTalk(p, n, "thanks");
					npcTalk(p, n, "in the gnome village those who are needy..",
						"recieve what they need, and those who are able..",
						"... give what they can");
					break;
				case 4:
					npcTalk(p, n, "some advice traveller",
						"we can walk, run, row or fly",
						"but never lose sight of the reason for the journey",
						"or miss the chance to see a rainbow on the way");
					playerTalk(p, n, "i like that");
					break;
				case 5:
					npcTalk(p, n, "my mum says...",
						"A friendly look, a kindly smile",
						"one good act, and life's worthwhile!");
					playerTalk(p, n, "sweet");
					break;
				case 6:
					npcTalk(p, n, "hello");
					playerTalk(p, n, "are you alright?");
					npcTalk(p, n, "i just want something to happen");
					playerTalk(p, n, "what?");
					npcTalk(p, n, "something, anything i don't know what");
					break;
				// case 7 hello little man
				case 8:
					message(p, "the gnome is preying");
					npcTalk(p, n, "guthix's angels fly so high as to be beyond our sight",
						"but they are always looking down upon us");
					playerTalk(p, n, "maybe");
					break;
			}
		}
		else if (n.getID() == NpcId.GNOME_CHILD_PINK_GREEN.id()) {
			playerTalk(p, n, "hello");
			int chatRandom = p.getRandom().nextInt(7);
			switch (chatRandom) {
				case 0:
					npcTalk(p, n, "To be or not to be");
					playerTalk(p, n, "Hey I know that. Where's it from?");
					npcTalk(p, n, "Existentialism for insects");
					break;
				case 1:
					npcTalk(p, n, "The human mind is a tremendous thing");
					break;
				case 2:
					npcTalk(p, n, "i have a riddle for you");
					playerTalk(p, n, "ok");
					npcTalk(p, n, "I am the beginning of eternity and the end of time and space...",
						"I am the beginning of every end and the end of every place. What am i?");
					playerTalk(p, n, "?",
						"erm..not sure...annoying");
					npcTalk(p, n, "i'm E, hee hee, do you get it");
					break;
				case 3:
					npcTalk(p, n, "hardy ha ha",
						"hee hee hee");
					playerTalk(p, n, "are you ok?");
					npcTalk(p, n, "i'm a little tree gnome",
						"that is me");
					playerTalk(p, n, "i've heard better");
					break;
				case 4:
					playerTalk(p, n, "hello there");
					npcTalk(p, n, "bla bla bla");
					playerTalk(p, n, "what?");
					npcTalk(p, n, "bla bla bla");
					p.message("rude little gnome");
					break;
				case 5:
					npcTalk(p, n, "Nice weather we're having today",
						"But then it doesn't tend to rain much round here");
					break;
				// case 6 hello
			}
		}
	}
}
