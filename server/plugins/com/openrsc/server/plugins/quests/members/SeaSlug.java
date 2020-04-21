package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class SeaSlug implements QuestInterface, TalkNpcTrigger,
	TakeObjTrigger,
	OpLocTrigger,
	OpBoundTrigger {

	@Override
	public int getQuestId() {
		return Quests.SEA_SLUG;
	}

	@Override
	public String getQuestName() {
		return "Sea Slug (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("@gre@You haved gained 1 quest point!");
		incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.SEA_SLUG), true);
		p.message("well done, you have completed the sea slug quest");
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.CAROLINE.id(), NpcId.HOLGART_LAND.id(), NpcId.HOLGART_PLATFORM.id(), NpcId.HOLGART_ISLAND.id(),
				NpcId.KENNITH.id(), NpcId.KENT.id(), NpcId.PLATFORM_FISHERMAN_GOLDEN.id(), NpcId.PLATFORM_FISHERMAN_PURPLE.id(),
				NpcId.PLATFORM_FISHERMAN_GRAY.id(), NpcId.BAILEY.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KENT.id()) {
			switch (p.getQuestStage(this)) {
				case 4:
					npcsay(p, n, "oh thank Saradomin",
						"i thought i would be left out here forever");
					say(p, n,
						"your wife sent me out to find you and your boy",
						"kennith's fine he's on the platform");
					npcsay(p, n, "i knew the row boat wasn't sea worthy",
						"i couldn't risk bringing him along but you must get him of that platform");
					say(p, n, "what's going on on there?");
					npcsay(p,
						n,
						"five days ago we pulled in huge catch",
						"as well as fish we caught small slug like sea creatures, hundreds of them",
						"that's when the fishermen began to act strange",
						"it was the sea slugs, they attach themselves to your body",
						"and somehow take over the mind of the carrier",
						"i told Kennith to hide until i returned but i was washed up here",
						"please go back and get my boy",
						"you can send help for me later", "traveler wait!");
					mes(p, "kent reaches behind your neck", "slooop",
						"he pulls a sea slug from under your top");
					//kent drops slug at 511, 636 and stays for about 12s
					createGroundItemDelayedRemove(new GroundItem(p.getWorld(), ItemId.SEASLUG.id(), 511, 636, 1, p), 12000);
					npcsay(p, n,
						"a few more minutes and that thing would have full control you body");
					say(p, n, "yuck..thanks kent");
					p.updateQuestStage(getQuestId(), 5);
					break;
				case 5:
					say(p, n, "hello");
					npcsay(p, n, "oh my", "i must get back to shore");
					break;
			}
		}
		else if (n.getID() == NpcId.KENNITH.id()) {
			switch (p.getQuestStage(this)) {
				case 3:
					say(p, n, "are you okay young one?");
					npcsay(p, n, "no i want my daddy");
					say(p, n, "Where is your father?");
					npcsay(p,
						n,
						"he went to get help days ago",
						"the nasty fisher men tried to throw me and daddy into the sea",
						"so he told me to hide in here");
					say(p, n, "that's good advice",
						"you stay here and i'll go try and find your father");
					p.updateQuestStage(getQuestId(), 4);
					break;
				case 4:
					say(p, n, "are you okay?");
					npcsay(p, n, "i want to see daddy");
					say(p, n, "i'm working on it");
					break;
				case 5:
					if (p.getCache().hasKey("loose_panel")) {
						say(p, n,
							"kennith i've made an opening in the wall",
							"you can come out there");
						npcsay(p, n, "are their any sea slugs on the other side?");
						say(p, n, "not one");
						npcsay(p, n, "how will i get down stairs");
						say(p, n, "i'll figure that out in a moment");
						npcsay(p, n, "okay, when you have i'll come out");
						return;
					}
					say(p, n, "hello kennith",
						"are you okay?");
					npcsay(p, n, "no i want my daddy");
					say(p, n, "you'll be able to see him soon",
						"first we need to get you back to land",
						"come with me to the boat");
					npcsay(p, n, "no");
					say(p, n, "what, why not?");
					npcsay(p, n, "i'm scared of those nasty sea slugs",
						"i won't go near them");
					say(p, n,
						"okay, you wait here and i'll figure another way to get you out");
					break;
				case 6:
				case -1:
					mes(p, "He doesn't seem interested in talking");
			}
		}
		else if (n.getID() == NpcId.BAILEY.id()) {
			switch (p.getQuestStage(this)) {
				case 3:
				case 4:
					say(p, n, "hello");
					npcsay(p, n, "well hello there",
						"what are you doing here?");
					say(p, n, "i'm trying to find out what happened to a boy named kennith");
					npcsay(p, n, "oh, you mean kent's son",
						"he's around somewhere, probably hiding");
					say(p, n, "hiding from what?");
					npcsay(p, n, "haven't you seen all those things out there?");
					say(p, n, "the sea slugs?");
					npcsay(p, n, "ever since we pulled up that haul something strange has been going on",
						"the fishermen spend all day pulling in hauls of fish",
						"only to throw back the fish and keep those nasty sea slugs",
						"what am i supposed to do with those",
						"i haven't figured out how to kill one yet",
						"if i put them near the stove they squirm and jump away");
					say(p, n, "i doubt they would taste too good");
					break;
				case 5:
					if (!p.getCache().hasKey("lit_torch")) {
						say(p, n, "hello");
						npcsay(p, n, "oh thank god it's you",
							"they've all gone mad i tell you",
							"one of the fishermen tried to throw me into the sea");
						say(p, n,
							"they're all being controlled by the sea slugs");
						npcsay(p, n, "i figured as much");
						say(p, n,
							"i need to get kennith of this platform but i can't get past the fishermen");
						npcsay(p, n, "the sea slugs are scared of heat",
							"i figured that out when i tried to cook them");
						if (!p.getCarriedItems().hasCatalogID(ItemId.UNLIT_TORCH.id(), Optional.of(false))) {
							npcsay(p, n, "here");
							mes(p, "bailey gives you a torch");
							give(p, ItemId.UNLIT_TORCH.id(), 1);
							npcsay(p,
								n,
								"i doubt the fishermen will come near you if you can get this torch to light",
								"the only problem is all the wood and flint is damp",
								"i can't light a thing");
						} else {
							say(p, n,
								"i better figure a way to light this torch");
						}
					} else {
						if (p.getCarriedItems().hasCatalogID(ItemId.LIT_TORCH.id(), Optional.of(false))) {
							say(p, n, "i've managed to light the torch");
							npcsay(p, n, "well done traveler",
								"you better get kennith out of here soon",
								"the fishermen are becoming stranger by the minute",
								"and they keep pulling up those blasted sea slugs");
						} else if (p.getCarriedItems().hasCatalogID(ItemId.UNLIT_TORCH.id(), Optional.of(false))) {
							//nothing
						} else {
							say(p, n, "i've managed to lose my torch");
							npcsay(p, n, "that was silly, fortunately i have another",
								"here, take it");
							give(p, ItemId.UNLIT_TORCH.id(), 1);
						}
					}
					break;
				case 6:
					say(p, n, "hello bailey");
					npcsay(p, n, "hello again",
						"i saw you managed to get kennith of the platform",
						"well done, he wasn't safe around these slugs");
					say(p, n, "are you going to come back with us?");
					npcsay(p, n, "no, these fishermen are my friends",
						"i'm sure they can be saved",
						"i'm going to stay and try to get rid of all these slugs");
					say(p, n, "you're braver than most",
						"take care of yourself bailey");
					npcsay(p, n, "you to traveler");
					break;
				case -1:
					say(p, n, "hello bailey");
					npcsay(p, n, "well hello again traveler",
						"what brings you back out here");
					say(p, n, "just looking around");
					npcsay(p, n, "well don't go touching any of those blasted slugs");
					break;
			}
		}
		else if (n.getID() == NpcId.PLATFORM_FISHERMAN_PURPLE.id() || n.getID() == NpcId.PLATFORM_FISHERMAN_GRAY.id()) {
			say(p, n, "hello there");
			p.message("his eyes are fixated");
			p.message("starring at the sea");
			npcsay(p, n, "must find family");
			say(p, n, "what?");
			npcsay(p, n, "soon we'll all be together");
			say(p, n, "are you okay?");
			npcsay(p, n, "must find family", "they're all under the blue",
				"deep deep under the blue");
			say(p, n, "ermm..i'll leave you to it then");
		}
		else if (n.getID() == NpcId.PLATFORM_FISHERMAN_GOLDEN.id()) {
			say(p, n, "hello");
			p.message("his eyes are fixated");
			p.message("starring at the sea");
			npcsay(p, n, "keep away human", "leave or face the deep blue");
			say(p, n, "pardon?");
			npcsay(p, n, "you'll all end up in the blue",
				"deep deep under the blue");
		}
		else if (n.getID() == NpcId.CAROLINE.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					say(p, n, "hello there");
					npcsay(p, n, "is there any chance you could help me?");
					say(p, n, "what's wrong?");
					npcsay(p,
						n,
						"it's my husband, he works on a fishing platform",
						"once a month he takes our son kennith out with him",
						"they usually write to me regularly but i've heard nothing all week",
						"it's very strange");
					say(p, n, "maybe the post was lost!");
					npcsay(p,
						n,
						"maybe, but no one's heard from the other fishermen on the platform",
						"their families are becoming quite concerned",
						"is there any chance you could visit the platform and find out what's going on?");
					int firstMenu = multi(p, n,
						"i suppose so, how do i get there?",
						"i'm sorry i'm too busy");
					if (firstMenu == 0) {
						npcsay(p, n, "that's very good of you traveller",
							"my friend holgart will take you there");
						say(p, n, "okay i'll go and see if they're ok");
						npcsay(p, n, "i will reward you for your time",
							"and it'll give me great piece of mind",
							"to know kennith and my husband kent are safe");
						p.updateQuestStage(getQuestId(), 1);
					} else if (firstMenu == 1) {
						npcsay(p, n, "thats a shame");
						say(p, n, "bye");
						npcsay(p, n, "bye");
					}
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					say(p, n, "hello caroline");
					npcsay(p, n,
						"brave adventurer have you any news about my son and his father?");
					say(p, n, "i'm working on it now caroline");
					npcsay(p, n, "please bring them back safe and sound");
					say(p, n, "i'll do my best");
					break;
				case 6:
					say(p, n, "hello");
					npcsay(p,
						n,
						"brave adventurer you've returned",
						"kennith told me about the strange going ons on the platform",
						"i had no idea it was so serious",
						"i could have lost my son and my husband if it wasn't for you");
					say(p, n, "we found kent stranded on a island");
					npcsay(p, n,
						"yes, holgart told me and sent a rescue party out",
						"kent's back at home now, resting with kennith",
						"i don't think he'll be doing any fishing for a while",
						"here, take these oyster pearls as a reward",
						"they're worth a fair bit",
						"and can be used to make lethal crossbow bolts");
					p.sendQuestComplete(Quests.SEA_SLUG);
					say(p, n, "thanks");
					npcsay(p, n, "thank you", "take care of yourself adventurer");
					give(p, ItemId.QUEST_OYSTER_PEARLS.id(), 1);
					break;
				case -1:
					say(p, n, "hello again");
					npcsay(p, n, "hello traveler", "how are you?");
					say(p, n, "not bad thanks, yourself?");
					npcsay(p, n, "i'm good", "busy as always looking after kent and kennith but no complaints");
					break;
			}
		}
		else if (n.getID() == NpcId.HOLGART_LAND.id() || n.getID() == NpcId.HOLGART_PLATFORM.id() || n.getID() == NpcId.HOLGART_ISLAND.id()) { /* Holgart */
			switch (p.getQuestStage(this)) {
				case 0:
					say(p, n, "hello there");
					npcsay(p, n, "well hello m'laddy", "beautiful day isn't it");
					say(p, n, "not bad i suppose");
					npcsay(p, n, "just smell that sea air... beautiful");
					say(p, n, "hmm...lovely!");
					break;
				case 1:
					say(p, n, "hello");
					npcsay(p, n, "hello m'hearty");
					say(p, n,
						"i would like a ride on your boat to the fishing platform");
					npcsay(p, n,
						"i'm afraid it isn't sea worthy, it's full of holes",
						"to fill the holes i'll need some swamp paste");
					say(p, n, "swamp paste?");
					npcsay(p, n,
						"yes, swamp tar mixed with flour heated over a fire");
					say(p, n, "where can i find swamp tar?");
					npcsay(p,
						n,
						"unfortunately the only supply of swamp tar is in the swamps below lumbridge",
						"it's too far for an old man like me to travel",
						"if you can make me some swamp paste i will give you a ride on my boat");
					say(p, n, "i'll see what i can do");
					p.updateQuestStage(getQuestId(), 2);
					break;
				case 2:
					say(p, n, "hello holgart");
					npcsay(p, n, "hello m'hearty",
						"did you manage to make some swamp paste?");
					if (p.getCarriedItems().remove(new Item(ItemId.SWAMP_PASTE.id())) != -1) {
						say(p, n, "yes i have some here");
						p.message("you give holgart the swamp paste");
						npcsay(p, n, "superb, this looks great");
						p.message("holgart smears the paste over the under side of his boat");
						npcsay(p, n, "that's done the job, now we can go",
							"jump aboard");
						p.updateQuestStage(getQuestId(), 3);
						int boatMenu = multi(p, n, "i'll come back later",
							"okay, lets do it");
						if (boatMenu == 0) {
							npcsay(p, n, "okay then", "i'll wait here for you");
						} else if (boatMenu == 1) {
							npcsay(p, n, "hold on tight");
							mes(p, "you board the small row boat",
								"you arrive at the fishing platform");
							p.teleport(495, 618, false);
						}
					} else {
						say(p, n, "i'm afraid not");
						npcsay(p,
							n,
							"to make it you need swamp tar mixed with flour heated over a fire",
							"the only supply of swamp tar is in the swamps below lumbridge",
							"i can't fix the row boat without it");
						say(p, n, "ok, i'll try to find some");
					}
					break;
				case 3:
					if (p.getLocation().inArdougne()) {
						say(p, n, "hello holgart");
						npcsay(p, n, "hello again land lover",
							"there's some strange going's on, on that platform i tell you");
						int goMenu = multi(p, n, false, //do not send over
							"will you take me there?",
							"i'm keeping away from there");
						if (goMenu == 0) {
							say(p, n, "will you take me back there?");
							npcsay(p, n, "of course m'hearty",
								"if that's what you want");
							mes(p, "you board the small row boat");
							checkTorchCrossing(p);
							mes(p, "you arrive at the fishing platform");
							p.teleport(495, 618, false);
						} else if (goMenu == 1) {
							say(p, n, "i'm keeping away from there");
							npcsay(p, n, "fair enough m'hearty");
						}
					} else {
						say(p, n, "hey holgart");
						npcsay(p, n, "have you had enough of this place yet?",
							"it's scaring me");
						int goBack = multi(p, n,
							"no, i'm going to stay a while",
							"okay, lets go back");
						if (goBack == 0) {
							npcsay(p, n, "okay, you're the boss");
						} else if (goBack == 1) {
							npcsay(p, n, "okay m'hearty jump on");
							mes(p, "you arrive back on shore");
							p.teleport(515, 613, false);
						}
					}
					break;
				case 4:
					if (p.getLocation().inPlatformArea()) {
						say(p, n, "holgart, something strange is going on here");
						npcsay(p, n, "you're telling me",
							"none of the sailors seem to remember who i am");
						say(p, n,
							"apparently kenniths father left for help a couple of days ago");
						npcsay(p, n,
							"that's a worry, no ones heard from him on shore",
							"come on, we better go look for him");
						mes(p, "you board the row boat",
							"you arrive on a small island");
						p.teleport(512, 639, false);
					} else if (p.getLocation().inArdougne()) {
						say(p, n, "hello holgart");
						npcsay(p, n, "hello again land lover",
							"there's some strange going's on, on that platform i tell you");
						int goMenu = multi(p, n, false, //do not send over
							"will you take me there?",
							"i'm keeping away from there");
						if (goMenu == 0) {
							say(p, n, "will you take me back there?");
							npcsay(p, n, "of course m'hearty",
								"if that's what you want");
							mes(p, "you board the small row boat");
							checkTorchCrossing(p);
							mes(p, "you arrive at the fishing platform");
							p.teleport(495, 618, false);
						} else if (goMenu == 1) {
							say(p, n, "i'm keeping away from there");
							npcsay(p, n, "fair enough m'hearty");
						}
					} else { //kents island
						say(p, n, "where are we?");
						npcsay(p, n, "someway of mainland still",
							"you better see if old matey's okay");
					}
					break;
				case 5:
					if (p.getLocation().inPlatformArea()) {
						say(p, n, "hey holgart");
						npcsay(p, n, "have you had enough of this place yet?",
							"it's scaring me");
						int goBack = multi(p, n,
							"no, i'm going to stay a while",
							"okay, lets go back");
						if (goBack == 0) {
							npcsay(p, n, "okay, you're the boss");
						} else if (goBack == 1) {
							npcsay(p, n, "okay m'hearty jump on");
							mes(p, "you arrive back on shore");
							p.teleport(515, 613, false);
						}
					} else if (p.getLocation().inArdougne()) {
						say(p, n, "hello holgart");
						npcsay(p, n, "hello again land lover",
							"there's some strange going's on, on that platform i tell you");
						int goMenu = multi(p, n, false, //do not send over
							"will you take me there?",
							"i'm keeping away from there");
						if (goMenu == 0) {
							say(p, n, "will you take me back there?");
							npcsay(p, n, "of course m'hearty",
								"if that's what you want");
							mes(p, "you board the small row boat");
							checkTorchCrossing(p);
							mes(p, "you arrive at the fishing platform");
							p.teleport(495, 618, false);
						} else if (goMenu == 1) {
							say(p, n, "i'm keeping away from there");
							npcsay(p, n, "fair enough m'hearty");
						}
					} else { //kents island to fishing platform
						say(p, n, "we had better get back to the platform",
							"and see what's going on");
						npcsay(p, n, "you're right", "it all sounds pretty creepy");
						mes(p, "you arrive back at the fishing platform");
						p.teleport(495, 618, false);
					}
					break;
				case 6:
					if (p.getLocation().inPlatformArea()) {
						say(p, n, "did you get the kid back to shore?");
						npcsay(p, n, "yes, he's safe and sound with his parents",
							"your turn to return to land now adventurer");
						say(p, n, "looking forward to it");
						p.message("you board the small row boat");
						p.message("you arrive back on shore");
						p.teleport(515, 613, false);
					} else {
						say(p, n, "hello holgart");
						npcsay(p, n, "hello again land lover",
							"there's some strange going's on, on that platform i tell you");
						int goMenu = multi(p, n, false, //do not send over
							"will you take me there?",
							"i'm keeping away from there");
						if (goMenu == 0) {
							say(p, n, "will you take me back there?");
							npcsay(p, n, "of course m'hearty",
								"if that's what you want");
							mes(p, "you board the small row boat");
							checkTorchCrossing(p);
							mes(p, "you arrive at the fishing platform");
							p.teleport(495, 618, false);
						} else if (goMenu == 1) {
							say(p, n, "i'm keeping away from there");
							npcsay(p, n, "fair enough m'hearty");
						}
					}
					break;
				case -1:
					if (p.getLocation().inArdougne()) {
						say(p, n, "hello again holgart");
						npcsay(p, n, "well hello again m'hearty",
							"your land loving legs getting bored?",
							"fancy some cold and wet underfoot?");
						say(p, n, "pardon");
						npcsay(p, n, "fancy going out to sea?");
						int goMenu = multi(p, n, "i'll come back later",
							"okay lets do it");
						if (goMenu == 0) {
							npcsay(p, n, "okay then",
								"i'll wait here for you");
						}
						if (goMenu == 1) {
							npcsay(p, n, "hold on tight");
							mes(p, "you board the small row boat");
							checkTorchCrossing(p);
							mes(p, "you arrive at the fishing platform");
							p.teleport(495, 618, false);
						}
					} else {
						say(p, n, "hey holgart");
						npcsay(p, n, "have you had enough of this place yet?",
							"it's scaring me");
						int goBack = multi(p, n,
							"no, i'm going to stay a while",
							"okay, lets go back");
						if (goBack == 0) {
							npcsay(p, n, "okay, you're the boss");
						} else if (goBack == 1) {
							npcsay(p, n, "okay m'hearty jump on");
							mes(p, "you arrive back on shore");
							p.teleport(515, 613, false);
						}
					}
					break;
			}
		}
	}

	public void checkTorchCrossing(Player p) {
		if (p.getCarriedItems().hasCatalogID(ItemId.LIT_TORCH.id(), Optional.of(false))) {
			p.getCarriedItems().getInventory().replace(ItemId.LIT_TORCH.id(), ItemId.UNLIT_TORCH.id());
			mes(p, "your torch goes out on the crossing");
		}
	}

	@Override
	public boolean blockTakeObj(Player p, GroundItem i) {
		return i.getID() == ItemId.SEASLUG.id();
	}

	@Override
	public void onTakeObj(Player p, GroundItem i) {
		if (i.getID() == ItemId.SEASLUG.id()) {
			int damage = DataConversions.getRandom().nextInt(8) + 1;
			p.message("you pick up the seaslug");
			p.message("it sinks its teeth deep into you hand");
			p.damage(damage);
			say(p, null, "ouch");
			p.message("you drop the sea slug");
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return obj.getID() == 458 || obj.getID() == 453;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == 458) {
			if (p.getQuestStage(getQuestId()) < 5) {
				mes(p, "You climb up the ladder");
				p.teleport(494, 1561, false);
				return;
			}
			if (p.getQuestStage(getQuestId()) >= 5) {
				if (!p.getCarriedItems().hasCatalogID(ItemId.LIT_TORCH.id(), Optional.of(false))) {
					int damage = DataConversions.getRandom().nextInt(1) + 7;
					p.message("You attempt to climb up the ladder");
					p.message("the fishermen approach you");
					p.message("and throw you back down the ladder");
					p.damage(damage);
					say(p, null, "ouch");
				} else {
					mes(p, "You climb up the ladder");
					p.teleport(494, 1561, false);
					p.message("the fishermen seem afraid of your torch");
				}
			}
		}
		else if (obj.getID() == 453) {
			if (p.getQuestStage(getQuestId()) == 5) {
				mes(p, "you rotate the crane around", "to the far platform");
				GameObject firstRotation = new GameObject(obj.getWorld(), obj.getLocation(),
					453, 5, 0);
				p.getWorld().replaceGameObject(obj, firstRotation);
				delay(p.getWorld().getServer().getConfig().GAME_TICK);
				GameObject secondRotation = new GameObject(obj.getWorld(), obj.getLocation(),
					453, 6, 0);
				p.getWorld().replaceGameObject(obj, secondRotation);
				say(p, null, "jump on kennith!");
				p.message("kennith comes out through the broken panal");
				GameObject thirdRotation = new GameObject(obj.getWorld(), obj.getLocation(),
					453, 5, 0);
				p.getWorld().replaceGameObject(obj, thirdRotation);
				delay(p.getWorld().getServer().getConfig().GAME_TICK);
				GameObject fourthRotation = new GameObject(obj.getWorld(), obj.getLocation(),
					453, 4, 0);
				p.getWorld().replaceGameObject(obj, fourthRotation);
				mes(p, "he climbs onto the fishing net",
					"you rotate the crane back around",
					"and lower kennith to the row boat waiting below");
				p.updateQuestStage(getQuestId(), 6);
				p.getCache().remove("loose_panel");
				p.getCache().remove("lit_torch");
			} else if (p.getQuestStage(getQuestId()) > 0 && p.getQuestStage(getQuestId()) < 5) {
				mes(p, "you rotate the crane around");
			} else {
				p.message("Nothing interesting happens");
			}
		}
	}

	@Override
	public boolean blockOpBound(GameObject obj, Integer click,
								Player player) {
		return obj.getID() == 124;
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 124) {
			if (p.getQuestStage(getQuestId()) == 5) {
				mes(p, "you kick the loose panel",
					"the wood is rotten and crumbles away",
					"leaving an opening big enough for kennith to climb through");
				p.getCache().store("loose_panel", true);
			} else {
				mes(p, "you kick the loose panal",
					"nothing interesting happens");
			}
		}
	}
}
