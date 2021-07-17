package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
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
	public int getQuestPoints() {
		return Quest.SEA_SLUG.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		final QuestReward reward = Quest.SEA_SLUG.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		player.message("well done, you have completed the sea slug quest");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.CAROLINE.id(), NpcId.HOLGART_LAND.id(), NpcId.HOLGART_PLATFORM.id(), NpcId.HOLGART_ISLAND.id(),
				NpcId.KENNITH.id(), NpcId.KENT.id(), NpcId.PLATFORM_FISHERMAN_GOLDEN.id(), NpcId.PLATFORM_FISHERMAN_PURPLE.id(),
				NpcId.PLATFORM_FISHERMAN_GRAY.id(), NpcId.BAILEY.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.KENT.id()) {
			switch (player.getQuestStage(this)) {
				case 4:
					npcsay(player, n, "oh thank Saradomin",
						"i thought i would be left out here forever");
					say(player, n,
						"your wife sent me out to find you and your boy",
						"kennith's fine he's on the platform");
					npcsay(player, n, "i knew the row boat wasn't sea worthy",
						"i couldn't risk bringing him along but you must get him of that platform");
					say(player, n, "what's going on on there?");
					npcsay(player,
						n,
						"five days ago we pulled in huge catch",
						"as well as fish we caught small slug like sea creatures, hundreds of them",
						"that's when the fishermen began to act strange",
						"it was the sea slugs, they attach themselves to your body",
						"and somehow take over the mind of the carrier",
						"i told Kennith to hide until i returned but i was washed up here",
						"please go back and get my boy",
						"you can send help for me later", "traveler wait!");
					mes("kent reaches behind your neck");
					delay(3);
					mes("slooop");
					delay(3);
					mes("he pulls a sea slug from under your top");
					delay(3);
					//kent drops slug at 511, 636 and stays for about 12s
					createGroundItemDelayedRemove(new GroundItem(player.getWorld(), ItemId.SEASLUG.id(), 511, 636, 1, player), 12000);
					npcsay(player, n,
						"a few more minutes and that thing would have full control you body");
					say(player, n, "yuck..thanks kent");
					player.updateQuestStage(getQuestId(), 5);
					break;
				case 5:
					say(player, n, "hello");
					npcsay(player, n, "oh my", "i must get back to shore");
					break;
			}
		}
		else if (n.getID() == NpcId.KENNITH.id()) {
			switch (player.getQuestStage(this)) {
				case 3:
					say(player, n, "are you okay young one?");
					npcsay(player, n, "no i want my daddy");
					say(player, n, "Where is your father?");
					npcsay(player,
						n,
						"he went to get help days ago",
						"the nasty fisher men tried to throw me and daddy into the sea",
						"so he told me to hide in here");
					say(player, n, "that's good advice",
						"you stay here and i'll go try and find your father");
					player.updateQuestStage(getQuestId(), 4);
					break;
				case 4:
					say(player, n, "are you okay?");
					npcsay(player, n, "i want to see daddy");
					say(player, n, "i'm working on it");
					break;
				case 5:
					if (player.getCache().hasKey("loose_panel")) {
						say(player, n,
							"kennith i've made an opening in the wall",
							"you can come out there");
						npcsay(player, n, "are their any sea slugs on the other side?");
						say(player, n, "not one");
						npcsay(player, n, "how will i get down stairs");
						say(player, n, "i'll figure that out in a moment");
						npcsay(player, n, "okay, when you have i'll come out");
						return;
					}
					say(player, n, "hello kennith",
						"are you okay?");
					npcsay(player, n, "no i want my daddy");
					say(player, n, "you'll be able to see him soon",
						"first we need to get you back to land",
						"come with me to the boat");
					npcsay(player, n, "no");
					say(player, n, "what, why not?");
					npcsay(player, n, "i'm scared of those nasty sea slugs",
						"i won't go near them");
					say(player, n,
						"okay, you wait here and i'll figure another way to get you out");
					break;
				case 6:
				case -1:
					mes("He doesn't seem interested in talking");
					delay(3);
			}
		}
		else if (n.getID() == NpcId.BAILEY.id()) {
			switch (player.getQuestStage(this)) {
				case 3:
				case 4:
					say(player, n, "hello");
					npcsay(player, n, "well hello there",
						"what are you doing here?");
					say(player, n, "i'm trying to find out what happened to a boy named kennith");
					npcsay(player, n, "oh, you mean kent's son",
						"he's around somewhere, probably hiding");
					say(player, n, "hiding from what?");
					npcsay(player, n, "haven't you seen all those things out there?");
					say(player, n, "the sea slugs?");
					npcsay(player, n, "ever since we pulled up that haul something strange has been going on",
						"the fishermen spend all day pulling in hauls of fish",
						"only to throw back the fish and keep those nasty sea slugs",
						"what am i supposed to do with those",
						"i haven't figured out how to kill one yet",
						"if i put them near the stove they squirm and jump away");
					say(player, n, "i doubt they would taste too good");
					break;
				case 5:
					if (!player.getCache().hasKey("lit_torch")) {
						say(player, n, "hello");
						npcsay(player, n, "oh thank god it's you",
							"they've all gone mad i tell you",
							"one of the fishermen tried to throw me into the sea");
						say(player, n,
							"they're all being controlled by the sea slugs");
						npcsay(player, n, "i figured as much");
						say(player, n,
							"i need to get kennith of this platform but i can't get past the fishermen");
						npcsay(player, n, "the sea slugs are scared of heat",
							"i figured that out when i tried to cook them");
						if (!player.getCarriedItems().hasCatalogID(ItemId.UNLIT_TORCH.id(), Optional.of(false))) {
							npcsay(player, n, "here");
							mes("bailey gives you a torch");
							delay(3);
							give(player, ItemId.UNLIT_TORCH.id(), 1);
							npcsay(player,
								n,
								"i doubt the fishermen will come near you if you can get this torch to light",
								"the only problem is all the wood and flint is damp",
								"i can't light a thing");
						} else {
							say(player, n,
								"i better figure a way to light this torch");
						}
					} else {
						if (player.getCarriedItems().hasCatalogID(ItemId.LIT_TORCH.id(), Optional.of(false))) {
							say(player, n, "i've managed to light the torch");
							npcsay(player, n, "well done traveler",
								"you better get kennith out of here soon",
								"the fishermen are becoming stranger by the minute",
								"and they keep pulling up those blasted sea slugs");
						} else if (player.getCarriedItems().hasCatalogID(ItemId.UNLIT_TORCH.id(), Optional.of(false))) {
							//nothing
						} else {
							say(player, n, "i've managed to lose my torch");
							npcsay(player, n, "that was silly, fortunately i have another",
								"here, take it");
							give(player, ItemId.UNLIT_TORCH.id(), 1);
						}
					}
					break;
				case 6:
					say(player, n, "hello bailey");
					npcsay(player, n, "hello again",
						"i saw you managed to get kennith of the platform",
						"well done, he wasn't safe around these slugs");
					say(player, n, "are you going to come back with us?");
					npcsay(player, n, "no, these fishermen are my friends",
						"i'm sure they can be saved",
						"i'm going to stay and try to get rid of all these slugs");
					say(player, n, "you're braver than most",
						"take care of yourself bailey");
					npcsay(player, n, "you to traveler");
					break;
				case -1:
					say(player, n, "hello bailey");
					npcsay(player, n, "well hello again traveler",
						"what brings you back out here");
					say(player, n, "just looking around");
					npcsay(player, n, "well don't go touching any of those blasted slugs");
					break;
			}
		}
		else if (n.getID() == NpcId.PLATFORM_FISHERMAN_PURPLE.id() || n.getID() == NpcId.PLATFORM_FISHERMAN_GRAY.id()) {
			say(player, n, "hello there");
			player.message("his eyes are fixated");
			player.message("starring at the sea");
			npcsay(player, n, "must find family");
			say(player, n, "what?");
			npcsay(player, n, "soon we'll all be together");
			say(player, n, "are you okay?");
			npcsay(player, n, "must find family", "they're all under the blue",
				"deep deep under the blue");
			say(player, n, "ermm..i'll leave you to it then");
		}
		else if (n.getID() == NpcId.PLATFORM_FISHERMAN_GOLDEN.id()) {
			say(player, n, "hello");
			player.message("his eyes are fixated");
			player.message("starring at the sea");
			npcsay(player, n, "keep away human", "leave or face the deep blue");
			say(player, n, "pardon?");
			npcsay(player, n, "you'll all end up in the blue",
				"deep deep under the blue");
		}
		else if (n.getID() == NpcId.CAROLINE.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello there");
					npcsay(player, n, "is there any chance you could help me?");
					say(player, n, "what's wrong?");
					npcsay(player,
						n,
						"it's my husband, he works on a fishing platform",
						"once a month he takes our son kennith out with him",
						"they usually write to me regularly but i've heard nothing all week",
						"it's very strange");
					say(player, n, "maybe the post was lost!");
					npcsay(player,
						n,
						"maybe, but no one's heard from the other fishermen on the platform",
						"their families are becoming quite concerned",
						"is there any chance you could visit the platform and find out what's going on?");
					int firstMenu = multi(player, n,
						"i suppose so, how do i get there?",
						"i'm sorry i'm too busy");
					if (firstMenu == 0) {
						npcsay(player, n, "that's very good of you traveller",
							"my friend holgart will take you there");
						say(player, n, "okay i'll go and see if they're ok");
						npcsay(player, n, "i will reward you for your time",
							"and it'll give me great piece of mind",
							"to know kennith and my husband kent are safe");
						player.updateQuestStage(getQuestId(), 1);
					} else if (firstMenu == 1) {
						npcsay(player, n, "thats a shame");
						say(player, n, "bye");
						npcsay(player, n, "bye");
					}
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					say(player, n, "hello caroline");
					npcsay(player, n,
						"brave adventurer have you any news about my son and his father?");
					say(player, n, "i'm working on it now caroline");
					npcsay(player, n, "please bring them back safe and sound");
					say(player, n, "i'll do my best");
					break;
				case 6:
					say(player, n, "hello");
					npcsay(player,
						n,
						"brave adventurer you've returned",
						"kennith told me about the strange going ons on the platform",
						"i had no idea it was so serious",
						"i could have lost my son and my husband if it wasn't for you");
					say(player, n, "we found kent stranded on a island");
					npcsay(player, n,
						"yes, holgart told me and sent a rescue party out",
						"kent's back at home now, resting with kennith",
						"i don't think he'll be doing any fishing for a while",
						"here, take these oyster pearls as a reward",
						"they're worth a fair bit",
						"and can be used to make lethal crossbow bolts");
					player.sendQuestComplete(Quests.SEA_SLUG);
					say(player, n, "thanks");
					npcsay(player, n, "thank you", "take care of yourself adventurer");
					give(player, ItemId.QUEST_OYSTER_PEARLS.id(), 1);
					break;
				case -1:
					say(player, n, "hello again");
					npcsay(player, n, "hello traveler", "how are you?");
					say(player, n, "not bad thanks, yourself?");
					npcsay(player, n, "i'm good", "busy as always looking after kent and kennith but no complaints");
					break;
			}
		}
		else if (n.getID() == NpcId.HOLGART_LAND.id() || n.getID() == NpcId.HOLGART_PLATFORM.id() || n.getID() == NpcId.HOLGART_ISLAND.id()) { /* Holgart */
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello there");
					npcsay(player, n, "well hello m'laddy", "beautiful day isn't it");
					say(player, n, "not bad i suppose");
					npcsay(player, n, "just smell that sea air... beautiful");
					say(player, n, "hmm...lovely!");
					break;
				case 1:
					say(player, n, "hello");
					npcsay(player, n, "hello m'hearty");
					say(player, n,
						"i would like a ride on your boat to the fishing platform");
					npcsay(player, n,
						"i'm afraid it isn't sea worthy, it's full of holes",
						"to fill the holes i'll need some swamp paste");
					say(player, n, "swamp paste?");
					npcsay(player, n,
						"yes, swamp tar mixed with flour heated over a fire");
					say(player, n, "where can i find swamp tar?");
					npcsay(player,
						n,
						"unfortunately the only supply of swamp tar is in the swamps below lumbridge",
						"it's too far for an old man like me to travel",
						"if you can make me some swamp paste i will give you a ride on my boat");
					say(player, n, "i'll see what i can do");
					player.updateQuestStage(getQuestId(), 2);
					break;
				case 2:
					say(player, n, "hello holgart");
					npcsay(player, n, "hello m'hearty",
						"did you manage to make some swamp paste?");
					if (player.getCarriedItems().remove(new Item(ItemId.SWAMP_PASTE.id())) != -1) {
						say(player, n, "yes i have some here");
						player.message("you give holgart the swamp paste");
						npcsay(player, n, "superb, this looks great");
						player.message("holgart smears the paste over the under side of his boat");
						npcsay(player, n, "that's done the job, now we can go",
							"jump aboard");
						player.updateQuestStage(getQuestId(), 3);
						int boatMenu = multi(player, n, "i'll come back later",
							"okay, lets do it");
						if (boatMenu == 0) {
							npcsay(player, n, "okay then", "i'll wait here for you");
						} else if (boatMenu == 1) {
							npcsay(player, n, "hold on tight");
							mes("you board the small row boat");
							delay(3);
							mes("you arrive at the fishing platform");
							delay(3);
							player.teleport(495, 618, false);
						}
					} else {
						say(player, n, "i'm afraid not");
						npcsay(player,
							n,
							"to make it you need swamp tar mixed with flour heated over a fire",
							"the only supply of swamp tar is in the swamps below lumbridge",
							"i can't fix the row boat without it");
						say(player, n, "ok, i'll try to find some");
					}
					break;
				case 3:
					if (player.getLocation().inArdougne()) {
						say(player, n, "hello holgart");
						npcsay(player, n, "hello again land lover",
							"there's some strange going's on, on that platform i tell you");
						int goMenu = multi(player, n, false, //do not send over
							"will you take me there?",
							"i'm keeping away from there");
						if (goMenu == 0) {
							say(player, n, "will you take me back there?");
							npcsay(player, n, "of course m'hearty",
								"if that's what you want");
							mes("you board the small row boat");
							delay(3);
							checkTorchCrossing(player);
							mes("you arrive at the fishing platform");
							delay(3);
							player.teleport(495, 618, false);
						} else if (goMenu == 1) {
							say(player, n, "i'm keeping away from there");
							npcsay(player, n, "fair enough m'hearty");
						}
					} else {
						say(player, n, "hey holgart");
						npcsay(player, n, "have you had enough of this place yet?",
							"it's scaring me");
						int goBack = multi(player, n,
							"no, i'm going to stay a while",
							"okay, lets go back");
						if (goBack == 0) {
							npcsay(player, n, "okay, you're the boss");
						} else if (goBack == 1) {
							npcsay(player, n, "okay m'hearty jump on");
							mes("you arrive back on shore");
							delay(3);
							player.teleport(515, 613, false);
						}
					}
					break;
				case 4:
					if (player.getLocation().inPlatformArea()) {
						say(player, n, "holgart, something strange is going on here");
						npcsay(player, n, "you're telling me",
							"none of the sailors seem to remember who i am");
						say(player, n,
							"apparently kenniths father left for help a couple of days ago");
						npcsay(player, n,
							"that's a worry, no ones heard from him on shore",
							"come on, we better go look for him");
						mes("you board the row boat");
						delay(3);
						mes("you arrive on a small island");
						delay(3);
						player.teleport(512, 639, false);
					} else if (player.getLocation().inArdougne()) {
						say(player, n, "hello holgart");
						npcsay(player, n, "hello again land lover",
							"there's some strange going's on, on that platform i tell you");
						int goMenu = multi(player, n, false, //do not send over
							"will you take me there?",
							"i'm keeping away from there");
						if (goMenu == 0) {
							say(player, n, "will you take me back there?");
							npcsay(player, n, "of course m'hearty",
								"if that's what you want");
							mes("you board the small row boat");
							delay(3);
							checkTorchCrossing(player);
							mes("you arrive at the fishing platform");
							delay(3);
							player.teleport(495, 618, false);
						} else if (goMenu == 1) {
							say(player, n, "i'm keeping away from there");
							npcsay(player, n, "fair enough m'hearty");
						}
					} else { //kents island
						say(player, n, "where are we?");
						npcsay(player, n, "someway of mainland still",
							"you better see if old matey's okay");
					}
					break;
				case 5:
					if (player.getLocation().inPlatformArea()) {
						say(player, n, "hey holgart");
						npcsay(player, n, "have you had enough of this place yet?",
							"it's scaring me");
						int goBack = multi(player, n,
							"no, i'm going to stay a while",
							"okay, lets go back");
						if (goBack == 0) {
							npcsay(player, n, "okay, you're the boss");
						} else if (goBack == 1) {
							npcsay(player, n, "okay m'hearty jump on");
							mes("you arrive back on shore");
							delay(3);
							player.teleport(515, 613, false);
						}
					} else if (player.getLocation().inArdougne()) {
						say(player, n, "hello holgart");
						npcsay(player, n, "hello again land lover",
							"there's some strange going's on, on that platform i tell you");
						int goMenu = multi(player, n, false, //do not send over
							"will you take me there?",
							"i'm keeping away from there");
						if (goMenu == 0) {
							say(player, n, "will you take me back there?");
							npcsay(player, n, "of course m'hearty",
								"if that's what you want");
							mes("you board the small row boat");
							delay(3);
							checkTorchCrossing(player);
							mes("you arrive at the fishing platform");
							delay(3);
							player.teleport(495, 618, false);
						} else if (goMenu == 1) {
							say(player, n, "i'm keeping away from there");
							npcsay(player, n, "fair enough m'hearty");
						}
					} else { //kents island to fishing platform
						say(player, n, "we had better get back to the platform",
							"and see what's going on");
						npcsay(player, n, "you're right", "it all sounds pretty creepy");
						mes("you arrive back at the fishing platform");
						delay(3);
						player.teleport(495, 618, false);
					}
					break;
				case 6:
					if (player.getLocation().inPlatformArea()) {
						say(player, n, "did you get the kid back to shore?");
						npcsay(player, n, "yes, he's safe and sound with his parents",
							"your turn to return to land now adventurer");
						say(player, n, "looking forward to it");
						player.message("you board the small row boat");
						player.message("you arrive back on shore");
						player.teleport(515, 613, false);
					} else {
						say(player, n, "hello holgart");
						npcsay(player, n, "hello again land lover",
							"there's some strange going's on, on that platform i tell you");
						int goMenu = multi(player, n, false, //do not send over
							"will you take me there?",
							"i'm keeping away from there");
						if (goMenu == 0) {
							say(player, n, "will you take me back there?");
							npcsay(player, n, "of course m'hearty",
								"if that's what you want");
							mes("you board the small row boat");
							delay(3);
							checkTorchCrossing(player);
							mes("you arrive at the fishing platform");
							delay(3);
							player.teleport(495, 618, false);
						} else if (goMenu == 1) {
							say(player, n, "i'm keeping away from there");
							npcsay(player, n, "fair enough m'hearty");
						}
					}
					break;
				case -1:
					if (player.getLocation().inArdougne()) {
						say(player, n, "hello again holgart");
						npcsay(player, n, "well hello again m'hearty",
							"your land loving legs getting bored?",
							"fancy some cold and wet underfoot?");
						say(player, n, "pardon");
						npcsay(player, n, "fancy going out to sea?");
						int goMenu = multi(player, n, "i'll come back later",
							"okay lets do it");
						if (goMenu == 0) {
							npcsay(player, n, "okay then",
								"i'll wait here for you");
						}
						if (goMenu == 1) {
							npcsay(player, n, "hold on tight");
							mes("you board the small row boat");
							delay(3);
							checkTorchCrossing(player);
							mes("you arrive at the fishing platform");
							delay(3);
							player.teleport(495, 618, false);
						}
					} else {
						say(player, n, "hey holgart");
						npcsay(player, n, "have you had enough of this place yet?",
							"it's scaring me");
						int goBack = multi(player, n,
							"no, i'm going to stay a while",
							"okay, lets go back");
						if (goBack == 0) {
							npcsay(player, n, "okay, you're the boss");
						} else if (goBack == 1) {
							npcsay(player, n, "okay m'hearty jump on");
							mes("you arrive back on shore");
							delay(3);
							player.teleport(515, 613, false);
						}
					}
					break;
			}
		}
	}

	public void checkTorchCrossing(Player player) {
		if (player.getCarriedItems().hasCatalogID(ItemId.LIT_TORCH.id(), Optional.of(false))) {
			player.getCarriedItems().remove(new Item(ItemId.LIT_TORCH.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.UNLIT_TORCH.id()));
			mes("your torch goes out on the crossing");
			delay(3);
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return i.getID() == ItemId.SEASLUG.id();
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.SEASLUG.id()) {
			int damage = DataConversions.getRandom().nextInt(8) + 1;
			player.message("you pick up the seaslug");
			player.message("it sinks its teeth deep into you hand");
			player.damage(damage);
			say(player, null, "ouch");
			player.message("you drop the sea slug");
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 458 || obj.getID() == 453;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 458) {
			if (player.getQuestStage(getQuestId()) < 5) {
				mes("You climb up the ladder");
				delay(3);
				player.teleport(494, 1561, false);
				return;
			}
			if (player.getQuestStage(getQuestId()) >= 5) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.LIT_TORCH.id(), Optional.of(false))) {
					int damage = DataConversions.getRandom().nextInt(1) + 7;
					player.message("You attempt to climb up the ladder");
					player.message("the fishermen approach you");
					player.message("and throw you back down the ladder");
					player.damage(damage);
					say(player, null, "ouch");
				} else {
					mes("You climb up the ladder");
					delay(3);
					player.teleport(494, 1561, false);
					player.message("the fishermen seem afraid of your torch");
				}
			}
		}
		else if (obj.getID() == 453) {
			if (player.getQuestStage(getQuestId()) == 5) {
				mes("you rotate the crane around");
				delay(3);
				mes("to the far platform");
				delay(3);
				GameObject firstRotation = new GameObject(obj.getWorld(), obj.getLocation(),
					453, 5, 0);
				player.getWorld().replaceGameObject(obj, firstRotation);
				delay();
				GameObject secondRotation = new GameObject(obj.getWorld(), obj.getLocation(),
					453, 6, 0);
				player.getWorld().replaceGameObject(obj, secondRotation);
				say(player, null, "jump on kennith!");
				player.message("kennith comes out through the broken panal");
				GameObject thirdRotation = new GameObject(obj.getWorld(), obj.getLocation(),
					453, 5, 0);
				player.getWorld().replaceGameObject(obj, thirdRotation);
				delay();
				GameObject fourthRotation = new GameObject(obj.getWorld(), obj.getLocation(),
					453, 4, 0);
				player.getWorld().replaceGameObject(obj, fourthRotation);
				mes("he climbs onto the fishing net");
				delay(3);
				mes("you rotate the crane back around");
				delay(3);
				mes("and lower kennith to the row boat waiting below");
				delay(3);
				player.updateQuestStage(getQuestId(), 6);
				player.getCache().remove("loose_panel");
				player.getCache().remove("lit_torch");
			} else if (player.getQuestStage(getQuestId()) > 0 && player.getQuestStage(getQuestId()) < 5) {
				mes("you rotate the crane around");
				delay(3);
			} else {
				player.message("Nothing interesting happens");
			}
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == 124;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 124) {
			if (player.getQuestStage(getQuestId()) == 5) {
				mes("you kick the loose panel");
				delay(3);
				mes("the wood is rotten and crumbles away");
				delay(3);
				mes("leaving an opening big enough for kennith to climb through");
				delay(3);
				player.getCache().store("loose_panel", true);
			} else {
				mes("you kick the loose panal");
				delay(3);
				mes("nothing interesting happens");
				delay(3);
			}
		}
	}
}
