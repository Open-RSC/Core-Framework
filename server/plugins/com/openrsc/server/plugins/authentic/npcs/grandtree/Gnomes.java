package com.openrsc.server.plugins.authentic.npcs.grandtree;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Gnomes implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return inArray(n.getID(), NpcId.GNOME_LOCAL_RED.id(), NpcId.GNOME_LOCAL_PURPLE.id(), NpcId.GNOME_CHILD_GREEN_PURPLE.id(),
				NpcId.GNOME_CHILD_PURPLE_PINK.id(), NpcId.GNOME_CHILD_PINK_GREEN.id(), NpcId.GNOME_CHILD_CREAM_PURPLE.id());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GNOME_LOCAL_RED.id()) {
			say(player, n, "hello");
			int chatRandom = DataConversions.getRandom().nextInt(4);
			switch (chatRandom) {
				case 0:
					npcsay(player, n, "can't stop sorry, busy, busy, busy");
					player.message("the gnome is too busy to talk");
					break;
				case 1:
					npcsay(player, n, "hello traveller",
						"are you enjoying your stay?");
					say(player, n, "it's a nice place");
					npcsay(player, n, "yes, we try to keep it that way");
					break;
				case 2:
					npcsay(player, n, "i don't think i can take much more");
					say(player, n, "what's wrong?");
					npcsay(player, n, "it's just the wife, she won't stop moaning");
					say(player, n, "maybe you should give her less to moan about");
					npcsay(player, n, "she'll always find something");
					break;
				case 3:
					npcsay(player, n, "how's life treating you");
					say(player, n, "not bad, not bad at all");
					npcsay(player, n, "it's good to see a human with a positive attitude");
					break;
			}
		}
		else if (n.getID() == NpcId.GNOME_LOCAL_PURPLE.id()) {
			say(player, n, "hello");
			int chatRandom = DataConversions.getRandom().nextInt(5);
			switch (chatRandom) {
				case 0:
					npcsay(player, n, "hello traveller",
						"are you eating properly?, you look tired");
					say(player, n, "i think so");
					npcsay(player, n, "here get this worm down you",
						"it'll do you the world of good");
					player.message("the gnome gives you a worm");
					give(player, ItemId.KING_WORM.id(), 1);
					say(player, n, "thanks!");
					break;
				case 1:
					say(player, n, "how are you?");
					npcsay(player, n, "not bad, a little worn out");
					say(player, n, "maybe you should have a lie down");
					npcsay(player, n, "with three kids to feed i've no time for naps");
					say(player, n, "sounds like hard work");
					npcsay(player, n, "it is but they're worth it");
					break;
				case 2:
					npcsay(player, n, "Some people grumble because roses have thorns",
						"I'm thankful that thorns have roses");
					say(player, n, "good attitude");
					break;
				// case 3 nothing but hello.
				case 4:
					npcsay(player, n, "well good day to you kind sir",
						"are you new to these parts?");
					say(player, n, "kind of");
					npcsay(player, n, "well if your looking for a good night out",
						"blurberrys cocktail bar's great");
					break;
			}
		}
		else if (n.getID() == NpcId.GNOME_CHILD_GREEN_PURPLE.id() || n.getID() == NpcId.GNOME_CHILD_CREAM_PURPLE.id()) {
			say(player, n, "hi there");
			int chatRandom = DataConversions.getRandom().nextInt(6);
			switch (chatRandom) {
				case 0:
					npcsay(player, n, "hello, why aren't you green?");
					say(player, n, "i don't know");
					npcsay(player, n, "maybe you should eat more vegtables");
					break;
				case 1:
					npcsay(player, n, "she loves me");
					say(player, n, "really");
					npcsay(player, n, "she does i tell you",
						"she really loves me");
					break;
				// case 2 nothing by hi there.
				case 3:
					player.message("the gnome appears to be singing");
					npcsay(player, n, "oh baby, oh my sweet");
					say(player, n, "are you talking to me?");
					npcsay(player, n, "no, i'm just singing",
						"i'm gonna sweep you of your feet");
					break;
				case 4:
					npcsay(player, n, "hello, would you like a worm?");
					say(player, n, "erm ok");
					player.message("the gnome gives you a worm");
					give(player, ItemId.KING_WORM.id(), 1);
					say(player, n, "thanks");
					npcsay(player, n, "in the gnome village those who are needy..",
						"recieve what they need, and those who are able..",
						"... give what they can");
					break;
				case 5:
					npcsay(player, n, "low");
					say(player, n, "what?");
					npcsay(player, n, "when?");
					say(player, n, "cheeky");
					npcsay(player, n, "hee hee");
					break;
			}
		}
		else if (n.getID() == NpcId.GNOME_CHILD_PURPLE_PINK.id()) {
			say(player, n, "hello little man");
			int chatRandom = DataConversions.getRandom().nextInt(9);
			switch (chatRandom) {
				case 0:
					say(player, n, "how are you");
					npcsay(player, n, "a warning traveller, the new world..",
						"..will rise from the underground");
					say(player, n, "what do you mean underground?");
					npcsay(player, n, "just a warning");
					break;
				case 1:
					npcsay(player, n, "a little inaccuracy sometimes...",
						"..saves tons of explanation");
					say(player, n, "true");
					break;
				case 2:
					say(player, n, "you look happy");
					npcsay(player, n, "i'm always at peace with myself");
					say(player, n, "how do you manage that?");
					npcsay(player, n, "i know, therefore i am");
					break;
				case 3:
					npcsay(player, n, "hello, would you like a worm?");
					say(player, n, "erm ok");
					player.message("the gnome gives you a worm");
					give(player, ItemId.KING_WORM.id(), 1);
					say(player, n, "thanks");
					npcsay(player, n, "in the gnome village those who are needy..",
						"recieve what they need, and those who are able..",
						"... give what they can");
					break;
				case 4:
					npcsay(player, n, "some advice traveller",
						"we can walk, run, row or fly",
						"but never lose sight of the reason for the journey",
						"or miss the chance to see a rainbow on the way");
					say(player, n, "i like that");
					break;
				case 5:
					npcsay(player, n, "my mum says...",
						"A friendly look, a kindly smile",
						"one good act, and life's worthwhile!");
					say(player, n, "sweet");
					break;
				case 6:
					npcsay(player, n, "hello");
					say(player, n, "are you alright?");
					npcsay(player, n, "i just want something to happen");
					say(player, n, "what?");
					npcsay(player, n, "something, anything i don't know what");
					break;
				// case 7 hello little man
				case 8:
					mes("the gnome is preying");
					delay(3);
					npcsay(player, n, "guthix's angels fly so high as to be beyond our sight",
						"but they are always looking down upon us");
					say(player, n, "maybe");
					break;
			}
		}
		else if (n.getID() == NpcId.GNOME_CHILD_PINK_GREEN.id()) {
			say(player, n, "hello");
			int chatRandom = DataConversions.getRandom().nextInt(9);
			switch (chatRandom) {
				case 0:
					npcsay(player, n, "To be or not to be");
					say(player, n, "Hey I know that. Where's it from?");
					npcsay(player, n, "Existentialism for insects");
					break;
				case 1:
					npcsay(player, n, "The human mind is a tremendous thing");
					break;
				case 2:
					npcsay(player, n, "i have a riddle for you");
					say(player, n, "ok");
					npcsay(player, n, "I am the beginning of eternity and the end of time and space...",
						"I am the beginning of every end and the end of every place. What am i?");
					say(player, n, "?",
						"erm..not sure...annoying");
					npcsay(player, n, "i'm E, hee hee, do you get it");
					break;
				case 3:
					npcsay(player, n, "hardy ha ha",
						"hee hee hee");
					say(player, n, "are you ok?");
					npcsay(player, n, "i'm a little tree gnome",
						"that is me");
					say(player, n, "i've heard better");
					break;
				case 4:
					say(player, n, "hello there");
					npcsay(player, n, "bla bla bla");
					say(player, n, "what?");
					npcsay(player, n, "bla bla bla");
					player.message("rude little gnome");
					break;
				case 5:
					npcsay(player, n, "Nice weather we're having today",
						"But then it doesn't tend to rain much round here");
					break;
				// case 6 hello
				case 7:
					npcsay(player, n, "i have a riddle for you");
					say(player, n, "ok");
					npcsay(player, n, "A tree which is planted on Monday and doubles in size each day...",
						"...is fully grown on the following sunday",
						"On what day is it half grown?");
					say(player, n, "Erm..i'm not sure");
					npcsay(player, n, "saturday",
						"you big folk really aren't the quickest");
					break;
				case 8:
					npcsay(player, n, "I worship Guthix, the god of balance",
						"He really does have exceptional co-ordination");
					break;
			}
		}
	}
}
