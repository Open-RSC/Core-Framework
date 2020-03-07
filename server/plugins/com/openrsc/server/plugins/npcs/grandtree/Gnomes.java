package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Gnomes implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return inArray(n.getID(), NpcId.GNOME_LOCAL_RED.id(), NpcId.GNOME_LOCAL_PURPLE.id(), NpcId.GNOME_CHILD_GREEN_PURPLE.id(),
				NpcId.GNOME_CHILD_PURPLE_PINK.id(), NpcId.GNOME_CHILD_PINK_GREEN.id(), NpcId.GNOME_CHILD_CREAM_PURPLE.id());
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GNOME_LOCAL_RED.id()) {
			say(p, n, "hello");
			int chatRandom = DataConversions.getRandom().nextInt(4);
			switch (chatRandom) {
				case 0:
					npcsay(p, n, "can't stop sorry, busy, busy, busy");
					p.message("the gnome is too busy to talk");
					break;
				case 1:
					npcsay(p, n, "hello traveller",
						"are you enjoying your stay?");
					say(p, n, "it's a nice place");
					npcsay(p, n, "yes, we try to keep it that way");
					break;
				case 2:
					npcsay(p, n, "i don't think i can take much more");
					say(p, n, "what's wrong?");
					npcsay(p, n, "it's just the wife, she won't stop moaning");
					say(p, n, "maybe you should give her less to moan about");
					npcsay(p, n, "she'll always find something");
					break;
				case 3:
					npcsay(p, n, "how's life treating you");
					say(p, n, "not bad, not bad at all");
					npcsay(p, n, "it's good to see a human with a positive attitude");
					break;
			}
		}
		else if (n.getID() == NpcId.GNOME_LOCAL_PURPLE.id()) {
			say(p, n, "hello");
			int chatRandom = DataConversions.getRandom().nextInt(4);
			switch (chatRandom) {
				case 0:
					npcsay(p, n, "hello traveller",
						"are you eating properly?, you look tired");
					say(p, n, "i think so");
					npcsay(p, n, "here get this worm down you",
						"it'll do you the world of good");
					p.message("the gnome gives you a worm");
					give(p, ItemId.KING_WORM.id(), 1);
					say(p, n, "thanks!");
					break;
				case 1:
					say(p, n, "how are you?");
					npcsay(p, n, "not bad, a little worn out");
					say(p, n, "maybe you should have a lie down");
					npcsay(p, n, "with three kids to feed i've no time for naps");
					say(p, n, "sounds like hard work");
					npcsay(p, n, "it is but they're worth it");
					break;
				case 2:
					npcsay(p, n, "Some people grumble because roses have thorns",
						"I'm thankful that thorns have roses");
					say(p, n, "good attitude");
					break;
				// case 3 nothing but hello.
			}
		}
		else if (n.getID() == NpcId.GNOME_CHILD_GREEN_PURPLE.id() || n.getID() == NpcId.GNOME_CHILD_CREAM_PURPLE.id()) {
			say(p, n, "hi there");
			int chatRandom = DataConversions.getRandom().nextInt(6);
			switch (chatRandom) {
				case 0:
					npcsay(p, n, "hello, why aren't you green?");
					say(p, n, "i don't know");
					npcsay(p, n, "maybe you should eat more vegtables");
					break;
				case 1:
					npcsay(p, n, "she loves me");
					say(p, n, "really");
					npcsay(p, n, "she does i tell you",
						"she really loves me");
					break;
				// case 2 nothing by hi there.
				case 3:
					p.message("the gnome appears to be singing");
					npcsay(p, n, "oh baby, oh my sweet");
					say(p, n, "are you talking to me?");
					npcsay(p, n, "no, i'm just singing",
						"i'm gonna sweep you of your feet");
					break;
				case 4:
					npcsay(p, n, "hello, would you like a worm?");
					say(p, n, "erm ok");
					p.message("the gnome gives you a worm");
					give(p, ItemId.KING_WORM.id(), 1);
					say(p, n, "thanks");
					npcsay(p, n, "in the gnome village those who are needy..",
						"recieve what they need, and those who are able..",
						"... give what they can");
					break;
				case 5:
					npcsay(p, n, "low");
					say(p, n, "what?");
					npcsay(p, n, "when?");
					say(p, n, "cheeky");
					npcsay(p, n, "hee hee");
					break;
			}
		}
		else if (n.getID() == NpcId.GNOME_CHILD_PURPLE_PINK.id()) {
			say(p, n, "hello little man");
			int chatRandom = DataConversions.getRandom().nextInt(9);
			switch (chatRandom) {
				case 0:
					say(p, n, "how are you");
					npcsay(p, n, "a warning traveller, the new world..",
						"..will rise from the underground");
					say(p, n, "what do you mean underground?");
					npcsay(p, n, "just a warning");
					break;
				case 1:
					npcsay(p, n, "a little inaccuracy sometimes...",
						"..saves tons of explanation");
					say(p, n, "true");
					break;
				case 2:
					say(p, n, "you look happy");
					npcsay(p, n, "i'm always at peace with myself");
					say(p, n, "how do you manage that?");
					npcsay(p, n, "i know, therefore i am");
					break;
				case 3:
					npcsay(p, n, "hello, would you like a worm?");
					say(p, n, "erm ok");
					p.message("the gnome gives you a worm");
					give(p, ItemId.KING_WORM.id(), 1);
					say(p, n, "thanks");
					npcsay(p, n, "in the gnome village those who are needy..",
						"recieve what they need, and those who are able..",
						"... give what they can");
					break;
				case 4:
					npcsay(p, n, "some advice traveller",
						"we can walk, run, row or fly",
						"but never lose sight of the reason for the journey",
						"or miss the chance to see a rainbow on the way");
					say(p, n, "i like that");
					break;
				case 5:
					npcsay(p, n, "my mum says...",
						"A friendly look, a kindly smile",
						"one good act, and life's worthwhile!");
					say(p, n, "sweet");
					break;
				case 6:
					npcsay(p, n, "hello");
					say(p, n, "are you alright?");
					npcsay(p, n, "i just want something to happen");
					say(p, n, "what?");
					npcsay(p, n, "something, anything i don't know what");
					break;
				// case 7 hello little man
				case 8:
					Functions.mes(p, "the gnome is preying");
					npcsay(p, n, "guthix's angels fly so high as to be beyond our sight",
						"but they are always looking down upon us");
					say(p, n, "maybe");
					break;
			}
		}
		else if (n.getID() == NpcId.GNOME_CHILD_PINK_GREEN.id()) {
			say(p, n, "hello");
			int chatRandom = DataConversions.getRandom().nextInt(7);
			switch (chatRandom) {
				case 0:
					npcsay(p, n, "To be or not to be");
					say(p, n, "Hey I know that. Where's it from?");
					npcsay(p, n, "Existentialism for insects");
					break;
				case 1:
					npcsay(p, n, "The human mind is a tremendous thing");
					break;
				case 2:
					npcsay(p, n, "i have a riddle for you");
					say(p, n, "ok");
					npcsay(p, n, "I am the beginning of eternity and the end of time and space...",
						"I am the beginning of every end and the end of every place. What am i?");
					say(p, n, "?",
						"erm..not sure...annoying");
					npcsay(p, n, "i'm E, hee hee, do you get it");
					break;
				case 3:
					npcsay(p, n, "hardy ha ha",
						"hee hee hee");
					say(p, n, "are you ok?");
					npcsay(p, n, "i'm a little tree gnome",
						"that is me");
					say(p, n, "i've heard better");
					break;
				case 4:
					say(p, n, "hello there");
					npcsay(p, n, "bla bla bla");
					say(p, n, "what?");
					npcsay(p, n, "bla bla bla");
					p.message("rude little gnome");
					break;
				case 5:
					npcsay(p, n, "Nice weather we're having today",
						"But then it doesn't tend to rain much round here");
					break;
				// case 6 hello
			}
		}
	}
}
