package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class TreeGnomeVillage implements QuestInterface, TalkNpcTrigger,
	OpBoundTrigger,
	OpLocTrigger,
	KillNpcTrigger {

	private static final int KHAZARD_CHEST_OPEN = 409;
	private static final int KHAZARD_CHEST_CLOSED = 410;

	@Override
	public int getQuestId() {
		return Quests.TREE_GNOME_VILLAGE;
	}

	@Override
	public String getQuestName() {
		return "Tree Gnome Village (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.TREE_GNOME_VILLAGE.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the treequest");
		final QuestReward reward = Quest.TREE_GNOME_VILLAGE.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		give(player, ItemId.GNOME_EMERALD_AMULET_OF_PROTECTION.id(), 1);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.ELKOY.id(), NpcId.LOCAL_GNOME.id(), NpcId.REMSAI.id(), NpcId.BOLREN.id(),
				NpcId.GNOME_TROOP.id(), NpcId.COMMANDER_MONTAI.id(), NpcId.TRACKER_3.id(), NpcId.TRACKER_1.id(),
				NpcId.KHAZARD_WARLORD.id(), NpcId.KALRON.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.KHAZARD_WARLORD.id()) {
			switch (player.getQuestStage(getQuestId())) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					say(player, n, "hello, how are you?");
					npcsay(player, n, "don't speak to me you insignificant wretch!",
						"die, in the name of khazard!");
					n.startCombat(player);
					break;
				case 5:
					say(player, n, "hello there");
					npcsay(player, n, "you think you're so clever",
						"you know nothing!");
					say(player, n, "what?");
					npcsay(player, n, "i'll crush you and those pesky little green men!");
					n.startCombat(player);
					break;
				case 6:
				case -1:
					if ((player.getQuestStage(getQuestId()) == 6 && player.getCache().hasKey("looted_orbs_protect"))
						|| player.getQuestStage(getQuestId()) == -1) {
						say(player, n, "i thought i killed you?");
						npcsay(player, n,
							"fool.. warriors blessed by khazard don't die",
							"you can't kill that which is already dead",
							"however i can kill you");
						n.startCombat(player);
					} else {
						say(player, n, "you there, stop!");
						npcsay(player, n, "go back to your pesky little green friends");
						say(player, n, "i've come for the orbs");
						npcsay(player, n, "you're out of your depth traveller",
							"these orbs are part of a much larger picture");
						say(player, n, "they're stolen goods",
							"now give them here");
						npcsay(player, n,
							"hee hee you really think you stand a chance?",
							"i'll crush you!");
						delay(2);
						n.startCombat(player);
					}
					break;
			}
		}
		else if (n.getID() == NpcId.TRACKER_1.id()) {
			switch (player.getQuestStage(getQuestId())) {
				case 0:
				case 1:
					say(player, n, "hello");
					npcsay(player, n, "i can't talk now",
						"can't you see we're trying to win a battle here?");
					break;
				case 2:
					say(player, n, "hi there");
					npcsay(player, n, "we're trying to hold them back",
						"but without more wood we won't be able to last long");
					say(player, n, "hang in there little man");
					break;
				case 3:
				case 4:
					say(player, n, "do you know the coordinates",
						"of the khazard stronghold?");
					npcsay(player, n, "i managed to get one although it wasn't easy",
						"the height coordinate is 4");
					say(player, n, "well done");
					npcsay(player, n, "the other two tracker gnomes",
						"should have the other coordinates",
						"if they're still alive");
					say(player, n, "ok, take care");
					break;
				case 5:
					if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_PROTECTION.id(), Optional.of(false))) {
						say(player, n, "how are you tracker?");
						npcsay(player, n, "now we have the globe i'm much better",
							"they won't stand a chance without it");
						return;
					}
					say(player, n, "hello again\"");
					npcsay(player, n, "well done, you've broken down there defenses",
						"this battle must be ours");
					break;
				case 6:
				case -1:
					say(player, n, "hello");
					npcsay(player, n, "when will this battle end?",
						"i feel like i've been fighting forever");
					break;
			}
		}
		else if (n.getID() == NpcId.TRACKER_3.id()) {
			switch (player.getQuestStage(getQuestId())) {
				case 0:
				case 1:
					say(player, n, "hello");
					npcsay(player, n, "i can't talk now",
						"can't you see we're trying to win a battle here?");
					break;
				case 2:
					say(player, n, "hi there");
					npcsay(player, n, "i can't stand this war",
						"the misery, the pain, it's driving me crazy",
						"when will it end?");
					player.message("He doesn't seem to be dealing with the battle very well");
					break;
				case 3:
				case 4:
					say(player, n, "are you ok?");
					player.message("The gnome looks dilerious");
					npcsay(player, n, "ok? who's ok? not me", "hee hee");
					say(player, n, "what's wrong?");
					npcsay(player, n, "you can't see me, no one can",
						"monsters, demons, they're all around me");
					say(player, n, "what do you mean?");
					npcsay(player, n, "they're dancing, all of them hee hee");
					player.message("He's clearly lost the plot");
					say(player, n,
						"do you have the x coordinate for the khazard stronghold?");
					npcsay(player, n, "who holds the stronghold?");
					say(player, n, "what?");
					npcsay(player, n, "more than me", "less than our feet");
					say(player, n, "you're mad");
					npcsay(player, n, "more than we", "and khazard's men are beat");
					player.message("The toll of war has affected his mind");
					say(player, n, "i'll pray for you little man");
					npcsay(player, n, "all day we pray in the hay", "hee hee");
					player.message("The poor gnome has gone mad");
					break;
				case 5:
					if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_PROTECTION.id(), Optional.of(false))) {
						say(player, n, "hello again\"");
						npcsay(player, n, "don't talk to me, you can't see me",
							"no one can just the demons");
						player.message("The poor gnome has gone mad");
						return;
					}
					say(player, n, "hello again");
					npcsay(player, n, "don't talk to me, you can't see me",
						"no one can just the demons");
					player.message("The poor gnome has gone mad");
					break;
				case 6:
				case -1:
					say(player, n, "hello");
					npcsay(player, n, "i feel dizzy, where am i?",
						"oh dear, oh dear i need some rest");
					say(player, n, "I think you do");
					break;
			}
		}
		else if (n.getID() == NpcId.COMMANDER_MONTAI.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello");
					npcsay(player, n, "i can't talk now",
						"can't you see we're trying to win a battle here?",
						"if we can't hold back khazard's men",
						"we're all doomed");
					break;
				case 1:
					say(player, n, "hello");
					npcsay(player, n, "hello traveller",
						"are you here to help or just to watch?");
					say(player, n, "I've been sent by king Bolren",
						"to retrieve the orb of protection");
					npcsay(player, n, "excellent we need all the help we can get",
						"i'm commander montai",
						"the orb is in the khazard stronghold to the north",
						"but until we weaken their defences",
						"we can't get close");
					say(player, n, "what can i do?");
					npcsay(player, n, "first we need to strengthen our own defences",
						"we desperately need wood to make more battlements",
						"six loads of logs should do it",
						"once the battlements are gone it's all over");
					int firstOrb = multi(player, n, false, //do not send over
						"Ok, i'll gather some wood",
						"Sorry i no longer want to be involved");
					if (firstOrb == 0) {
						say(player, n, "ok, i'll gather some wood");
						npcsay(player, n, "please be as quick as you can",
							"i don't know how much longer we can hold out");
						player.updateQuestStage(getQuestId(), 2);
					} else if (firstOrb == 1) {
						say(player, n, "sorry i no longer want to be involved");
						npcsay(player, n, "that's a shame we could",
							"have done with your help");
					}
					break;
				case 2:
					say(player, n, "hello");
					npcsay(player, n,
						"hello again, we're still desperate for wood soldier");
					if (ifheld(player, ItemId.LOGS.id(), 6)) {
						for (int i = 0; i < 6; i++) {
							player.getCarriedItems().remove(new Item(ItemId.LOGS.id()));
						}
						say(player, n, "i have some here");
						player.message("you give some wood to the commander");
						npcsay(player,
							n,
							"that's excellent now we can make more defensive battlements",
							"give me a moment to organise the troops",
							"and then come speak to me",
							"i'll inform you of our next phase of attack");
						player.updateQuestStage(getQuestId(), 3);
					} else {
						npcsay(player, n, "we need at least six loads of logs");
						say(player, n, "i'll see what i can do");
						npcsay(player, n, "thankyou");
					}
					break;
				case 3:
					say(player, n, "how are you doing montai?");
					npcsay(player, n, "we're hanging in there soldier",
						"for the next phase of the attack",
						"we need to breech their stronghold",
						"the ballista can break through the stronghold wall",
						"and then we can advance and seize back the orb");
					say(player, n, "so what's the problem?");
					npcsay(player,
						n,
						"from this distance we can't get an accurate shot away",
						"we need the correct coordinates of the stronghold",
						"for a direct hit",
						"i've sent out three tracker gnomes to gather them");
					say(player, n, "have they returned?");
					npcsay(player, n, "i'm afraid not and we're running out of time",
						"I need you to go into the heart of the battlefield",
						"find the trackers and bring back the coordinates.",
						"Do you think you can do it?");
					int phasetwo = multi(player, n, false, //do not send over
						"No, i've had enough of your battle",
						"I'll try my best");
					if (phasetwo == 0) {
						say(player, n, "no, i've had enough of your battle");
						npcsay(player, n, "i understand, this isn't your fight");
					} else if (phasetwo == 1) {
						say(player, n, "i'll try my best");
						npcsay(player,
							n,
							"thankyou, you're braver than most",
							"i don't know how long i will be able to hold out",
							"once you have the coordinates",
							"come back and fire the ballista",
							"right into those monsters",
							"if you can retrieve the orb and bring safety back to my people",
							"none of the blood spilled on this field will be in vain");
						player.updateQuestStage(getQuestId(), 4);
					}
					break;
				case 4:
					say(player, n, "hello");
					npcsay(player, n, "hello warrior we need the coordinates",
						"for a direct hit from the ballista",
						"once you have a direct hit you will be able",
						"to enter the stronghold and retrieve the orb");
					break;
				case 5:
					if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_PROTECTION.id(), Optional.of(false))) {
						say(player, n, "i have the orb of protection");
						npcsay(player, n, "incredible, for a human",
							"you really are something");
						say(player, n, "thanks... i think!");
						npcsay(player, n, "I'll stay here with my troops",
							"and try and hold khazard's men back",
							"you return the orb to the gnome village",
							"go as quick as you can",
							"the village is still unprotected");
						return;
					}
					say(player, n, "i've breeched the stronghold");
					npcsay(player, n, "i saw, that was a beautiful sight",
						"the khazard troops didn't know what hit them",
						"now is the time to retrieve the orb",
						"it's all in your hands", "i'll be praying for you");
					break;
				case 6:
				case -1:
					say(player, n, "hello montai, how are you?");
					npcsay(player, n, "i'm ok, this battle is going",
						"to take longer to win than i expected",
						"the khazard troops won't give up even without the orb");
					say(player, n, "hang in there");
					break;
			}
		}
		else if (n.getID() == NpcId.GNOME_TROOP.id()) {
			if (player.getQuestStage(getQuestId()) == 5
				|| player.getQuestStage(getQuestId()) == -1) {
				say(player, n, "hi");
				npcsay(player, n, "draw your sword warrior",
					"and fight along side us!");
				return;
			} else if (player.getQuestStage(getQuestId()) == 0
				|| player.getQuestStage(getQuestId()) >= 2
				|| player.getQuestStage(getQuestId()) <= 4) {
				say(player, n, "hello");
				npcsay(player, n, "i can't talk now", "can't you see we're trying",
					"to win a battle here?");
				return;
			} else {
				say(player, n, "hello");
				npcsay(player, n, "death to khazard and all who serve him!");
				return;
			}
		}
		else if (n.getID() == NpcId.BOLREN.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello");
					npcsay(player, n, "well hello stranger",
						"my name's bolren, i'm the king of the tree gnomes",
						"i'm surprised you made it in",
						"maybe i made the maze too easy");
					say(player, n, "maybe");
					npcsay(player, n,
						"i'm afraid i have more serious concerns at the moment",
						"very serious");
					int first = multi(player, n, false, //do not send over
						"I'll leave you to it then",
						"Can i help at all?");
					if (first == 0) {
						say(player, n, "i'll leave you too it then");
						npcsay(player, n, "ok take care");
					} else if (first == 1) {
						say(player, n, "can i help at all?");
						npcsay(player, n, "i'm glad you asked",
							"the truth is my people are in grave danger",
							"we have always been protected by the spirit tree",
							"no creature dark of heart can harm us",
							"while its three orbs are in place.",
							"We are not a violent race",
							"but we fight when we must", "many gnomes have fallen",
							"battling the dark forces of khazard to the north",
							"we became desperate",
							"so we took one orb of protection to the battlefield",
							"it was a foolish move",
							"khazard troops siezed the orb",
							"and now we are completely defenseless");
						say(player, n, "how can i help?");
						npcsay(player, n, "you would be a huge benefit on the battlefield",
							"if you would go there and try and retrieve the orb",
							"my people and i will be forever grateful");
						int second = multi(player, n, false, //do not send over
							"I would be glad to help",
							"I'm sorry but i won't be involved");
						if (second == 0) {
							say(player, n, "i would be glad to help");
							npcsay(player,
								n,
								"thank you",
								"the battlefield is to the north of the maze",
								"commander montai will inform you of their current situation",
								"that's if he's still alive",
								"my assistant shall guide you out",
								"try your best to return the orb",
								"good luck friend");
							player.message("A gnome guides you out of the maze");
							player.teleport(624, 675, false);
							if (player.getQuestStage(getQuestId()) == 0) {
								player.updateQuestStage(getQuestId(), 1);
							}
						} else if (second == 1) {
							say(player, n, "i'm sorry but i won't be involved");
							npcsay(player, n, "ok then, travel safe");
						}
					}
					break;
				case 1:
					say(player, n, "hello bolren");
					npcsay(player, n, "hello traveller, we must retrieve the orb",
						"it's being held by khazard troops",
						"to the west of the maze",
						"above the khazard fight arena");
					say(player, n, "ok i'll try my best");
					break;
				case 2:
				case 3:
				case 4:
					say(player, n, "hello bolren");
					npcsay(player, n, "the orb is being held at the battlefield",
						"to the north of the maze",
						"above the khazard fight arena");
					break;
				case 5:
					say(player, n, "king bolren are you ok?");
					if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_PROTECTION.id(), Optional.of(false))) {
						say(player, n, "i have the orb");
						npcsay(player, n, "thank you traveller, but it's too late",
							"we're all doomed", "oh my the misery, the horror");
						say(player, n, "what happened?");
						npcsay(player, n, "they came in the night",
							"i don't how many, enough");
						say(player, n, "who?");
						npcsay(player, n, "khazard troops",
							"they slaughtered anyone who got in their way",
							"women, children, my wife");
						say(player, n, "i'm sorry");
						npcsay(player, n, "they took the other orbs",
							"now we're defenseless");
						say(player, n, "where did they take them?");
						npcsay(player, n, "they headed north of the",
							"battlefields to the dead valleys",
							"a warlord carries the orbs");
						int newOrbs = multi(player, n, false, //do not send over
							"I will find the warlord and bring back the orbs",
							"I'm sorry but i can't help");
						if (newOrbs == 0) {
							say(player, n, "i will find the warlord and bring back the orbs");
							npcsay(player, n, "you are brave",
								"but this task will be tough even for you,",
								"i wish you the best of luck traveller",
								"once again you are our only hope",
								"i will safeguard this orb",
								"and pray for your safe return",
								"my assistant will guide you out");
							player.message("A gnome guides you out of the maze");
							player.teleport(624, 675, false);
							player.updateQuestStage(getQuestId(), 6);
							player.getCarriedItems().remove(new Item(ItemId.ORB_OF_PROTECTION.id()));
						} else if (newOrbs == 1) {
							say(player, n, "i'm sorry but i can't help");
							npcsay(player, n, "i understand, this isn't your battle");
						}
					} else {
						npcsay(player, n, "do you have the orb?");
						say(player, n, "no, i'm afraid not");
						npcsay(player, n, "please, we must have the orb",
							"if we are to survive");
					}
					break;
				case 6:
					if (player.getCarriedItems().hasCatalogID(ItemId.ORBS_OF_PROTECTION.id(), Optional.of(false))) {
						say(player, n, "bolren, i have returned");
						npcsay(player, n, "you made it back", "do you have the orbs?");
						say(player, n, "i have them here");
						npcsay(player,
							n,
							"hooray, you're amazing",
							"i didn't think it was possible",
							"but you've saved us",
							"once the orbs are replaced we will be safe once more",
							"come with me and we shall begin the ceremony");
						player.teleport(658, 696, true);
						say(player, n, "what now?");
						npcsay(player, n,
							"the spirit tree has looked over us for centuries",
							"now we must pay our respects");
						mes("bolren takes the orbs");
						delay(3);
						mes("the gnomes begin to chant");
						delay(3);
						mes("Su tana, en tania");
						delay(3);
						mes("They continue to chant");
						delay(3);
						mes("As the king gnome climbs the tree");
						delay(3);
						mes("placing the two Orbs at the peak of the spirit tree");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.ORBS_OF_PROTECTION.id()));
						n.displayNpcTeleportBubble(656, 695);
						delay(2);
						n.displayNpcTeleportBubble(656, 695);
						delay(2);
						n.displayNpcTeleportBubble(656, 695);
						npcsay(player, n, "now at last my people are safe once more",
							"and can live in peace");
						say(player, n, "i'm pleased i could help");
						npcsay(player,
							n,
							"you are modest brave traveller",
							"please for your efforts take this amulet",
							"it's made from the same sacred stone as the orbs of protection",
							"it will help keep you safe on your journeys");
						say(player, n, "thank you king bolren");
						npcsay(player, n, "the tree has many other powers",
							"some of which i cannot reveal",
							"however as a friend of the gnome",
							"people you can now use the tree's",
							"magic to teleport to other trees",
							"grown from related seeds");
						player.getCache().remove("looted_orbs_protect");
						player.sendQuestComplete(Quests.TREE_GNOME_VILLAGE);
					} else if (player.getCache().hasKey("looted_orbs_protect")) {
						say(player, n, "bolren, i have returned");
						npcsay(player, n, "you made it back", "do you have the orbs?");
						say(player, n, "no, i'm afraid not");
						npcsay(player, n, "please, we must have the orbs",
							"if we are to survive");
					} else {
						say(player, n, "hello bolren");
						npcsay(player,
							n,
							"the orbs are gone",
							"taken north of the battlefield by a khazard warlord",
							"we're all doomed");
					}
					break;
				case -1:
					say(player, n, "hello again bolren");
					npcsay(player, n, "well hello, it's good to see you again");
					if (!player.getCarriedItems().hasCatalogID(ItemId.GNOME_EMERALD_AMULET_OF_PROTECTION.id(), Optional.empty())) {
						say(player, n, "i've lost my amulet");
						npcsay(player, n, "oh dear", "here take another");
						give(player, ItemId.GNOME_EMERALD_AMULET_OF_PROTECTION.id(), 1);
					} else {
						say(player, n, "good to see you");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.REMSAI.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello");
					npcsay(player, n, "well done, well done",
						"not many find their way in here",
						"i'm remsai, a tree gnome",
						"we live in this maze for our protection",
						"have a look around and enjoy");
					break;
				case 1:
					npcsay(player, n, "oh my, oh my");
					say(player, n, "what's wrong?");
					npcsay(player, n, "the orb, they have the orb",
						"it must be returned",
						"i'm remsai, a tree gnome",
						"or we're doomed");
					break;
				case 2:
				case 3:
				case 4:
					npcsay(player, n, "the orb, they have the orb",
						"if it's not returned we're doomed");
					break;
				case 5:
					say(player, n, "hello remsai");
					npcsay(player, n, "hello, did you find the orb?");
					if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_PROTECTION.id(), Optional.of(false))) {
						say(player, n, "i have it here");
						npcsay(player, n, "you're our saviour");
					} else {
						say(player, n, "no, i'm afraid not");
						npcsay(player, n, "please we must have the orb",
							"if we are to survive");
					}
					break;
				case 6:
					if (player.getCarriedItems().hasCatalogID(ItemId.ORBS_OF_PROTECTION.id(), Optional.of(false))) {
						say(player, n, "i've returned");
						npcsay(player, n, "you're back, well done brave adventurer",
							"now the orbs are safe",
							"we can perform the ritual for the orb tree",
							"and we can live in peace once again");
					} else {
						say(player, n, "are you ok?");
						npcsay(player, n, "Khazard's men came",
							"without the orb we were defenseless",
							"they killed many", "and then took our last hope",
							"the other orbs", "now surely we're all doomed",
							"without them the spirit tree is useless");
					}
					break;
				case -1:
					say(player, n, "hello");
					npcsay(player, n, "hi there traveller",
						"you're a legend around these parts");
					say(player, n, "thanks remsai");
					break;
			}
		}
		else if (n.getID() == NpcId.LOCAL_GNOME.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					say(player, n, "hello");
					npcsay(player, n, "lardi dee, lardi da");
					say(player, n, "are you alright?");
					npcsay(player, n, "hee hee, lardi da, lardi dee");
					player.message("The gnome appears to be singing");
					break;
				case 5:
					say(player, n, "hello little man");
					npcsay(player, n, "little man stronger than big man",
						"hee hee", "lardi dee, lardi da");
					player.message("Cheeky little gnome");
					break;
				case 6:
					if (player.getCache().hasKey("looted_orbs_protect")) {
						say(player, n, "hello gnome");
						npcsay(player, n, "soon we're gonna have the sacred ceremony",
							"and boy am i going to party",
							"lock up your daughters", "hee hee");
					} else {
						say(player, n, "hi");
						npcsay(player, n,
							"must save the orbs and kill the khazard warlord",
							"that will be fun", "hee hee");
					}
					break;
				case -1:
					say(player, n, "hello");
					npcsay(player, n, "you're the best");
					say(player, n, "thanks");
					npcsay(player, n, "well, i'm better", "hee hee");
					break;
			}
		}
		else if (n.getID() == NpcId.ELKOY.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello there");
					npcsay(player, n, "hello, welcome to our maze",
						"i'm elkoy the tree gnome");
					say(player, n, "i haven't heard of your sort");
					npcsay(player, n, "there's not many of us left",
						"once you could find tree gnomes",
						"anywhere in the world, now we hide",
						"in small groups to avoid capture");
					say(player, n, "capture by whom?");
					npcsay(player, n, "tree gnomes have been hunted",
						"for so called 'fun' since i",
						"can remember, our main threat",
						"nowadays are General Khazard's troops",
						"they know no mercy, but are also",
						"very dense, they'll never find",
						"their way through our maze", "have fun");
					break;
				case 1:
					say(player, n, "hello elkoy");
					npcsay(player, n, "oh my, oh my");
					say(player, n, "what's wrong?");
					npcsay(player, n, "the orb, they have the orb", "we're doomed");
					break;
				case 2:
				case 3:
				case 4:
					say(player, n, "hello");
					npcsay(player, n, "you must retrieve the orb",
						"or the gnome village is doomed");
					break;
				case 5:
					say(player, n, "hello elkoy");
					npcsay(player, n, "you're back! and the orb?");
					if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_PROTECTION.id(), Optional.of(false))) {
						say(player, n, "i have it here");
						npcsay(player,
							n,
							"you're our saviour",
							"please return it to the village and we are all saved",
							"would you like me to show you the way to the village?");
						int gotOrb = multi(player, n, false, //do not send over
							"Yes please", "No thanks Elkoy");
						if (gotOrb == 0) {
							say(player, n, "yes please");
							npcsay(player, n, "ok then follow me");
							player.message("elkoy leads you to the gnome village");
							player.teleport(644, 697, false);
						} else if (gotOrb == 1) {
							say(player, n, "no thanks elkoy");
							npcsay(player, n, "ok then take care");
						}
					} else {
						say(player, n, "no, i'm afraid not");
						npcsay(player, n, "please, we must have the orb",
							"if we are to survive");
					}
					break;
				case 6:
					say(player, n, "hello elkoy");
					if (player.getCache().hasKey("looted_orbs_protect")) {
						npcsay(player, n, "you truly are a hero");
						say(player, n, "thanks");
						npcsay(player, n, "you saved us by", "returning the orbs of\"",
							"protection, i'm humbled", "and wish you well",
							"would you like me to show",
							"you the way to the village?");
						int finaleMenu = multi(player, n, false, //do not send over
							"Yes please", "No thanks elkoy");
						if (finaleMenu == 0) {
							say(player, n, "yes please");
							npcsay(player, n, "ok then follow me");
							player.message("elkoy leads you to the gnome village");
							player.teleport(644, 697, false);
						} else if (finaleMenu == 1) {
							say(player, n, "no thanks elkoy");
							npcsay(player, n, "ok then take care");
						}
					} else {
						npcsay(player, n, "did you hear? khazard's men",
							"have pillaged the village!",
							"they slaughtered many", "and took the other orbs",
							"in an attempt to lead us", "all out of the maze",
							"when will the misery end?",
							"would you like me to show",
							"you the way to the village?");
						int menu = multi(player, n, false, //do not send over
							"Yes please", "No thanks elkoy");
						if (menu == 0) {
							say(player, n, "yes please");
							npcsay(player, n, "ok then follow me");
							player.message("elkoy leads you to the gnome village");
							player.teleport(644, 697, false);
						} else if (menu == 1) {
							say(player, n, "no thanks elkoy");
							npcsay(player, n, "ok then take care");
						}
					}
					break;
				case -1:
					say(player, n, "hello little man");
					npcsay(player, n, "hi there, hope life", "is treating you well",
						"would you like me to show",
						"you the way to the village?");
					int elkoy = multi(player, n, false, //do not send over
						"Yes please", "No thanks elkoy");
					if (elkoy == 0) {
						say(player, n, "yes please");
						npcsay(player, n, "ok then follow me");
						player.message("elkoy leads you to the gnome village");
						player.teleport(644, 697, false);
					} else if (elkoy == 1) {
						say(player, n, "no thanks elkoy");
						npcsay(player, n, "ok then take care");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.KALRON.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					say(player, n, "hello");
					npcsay(player, n, "gotta find a way out",
						"we built this maze for protection",
						"but i can't get used to it",
						"i'm always getting lost");
					break;
				case 5:
					say(player, n, "hello there");
					npcsay(player, n, "oh my, oh my",
						"the village has been",
						"and i'm still lost",
						"oh dear");
					break;
				case 6:
					if (player.getCache().hasKey("looted_orbs_protect")) {
						say(player, n, "hello little man");
						npcsay(player, n, "hello i hope they come out and find me soon,",
							"it's getting cold");
					} else {
						say(player, n, "hello, how are you?");
						npcsay(player, n,
							"oh my i'll never find my way back",
							"before khazard's men come and hunt me down");
					}
					break;
				case -1:
					say(player, n, "hello there, you look lost");
					npcsay(player, n, "are you trying to be funny?");
					say(player, n, "no");
					npcsay(player, n, "hmmm");
					break;
			}
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return (obj.getID() == 101 && obj.getY() == 705) || (obj.getID() == 101 && obj.getX() == 540 && obj.getY() == 445);
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 101 && obj.getY() == 705) {
			player.message("You push your way through the fence");
			doDoor(obj, player, 16);
		}
		else if (obj.getID() == 101 && obj.getX() == 540 && obj.getY() == 445) {
			player.message("You push your way through the fence");
			doDoor(obj, player, 16);
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return DataConversions.inArray(new int[] {392, 388, 393, KHAZARD_CHEST_OPEN, KHAZARD_CHEST_CLOSED}, obj.getID());
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 392) {
			Npc trackerTwo = ifnearvisnpc(player, NpcId.TRACKER_2.id(), 5);
			if (trackerTwo == null) return;
			switch (player.getQuestStage(getQuestId())) {
				case 0:
				case 1:
					say(player, trackerTwo, "hello");
					npcsay(player, trackerTwo, "i can't talk now",
						"if the guards catch me i'll be dead gnome meat");
					break;
				case 2:
					say(player, trackerTwo, "hi there");
					npcsay(player, trackerTwo, "the battle is far from over",
						"if you have a pure heart you will help us win");
					break;
				case 3:
				case 4:
					player.message("The gnome looks beaten and weak");
					npcsay(player, trackerTwo, "they caught me spying on the stronghold..",
						"they beat and tortured me",
						"but i didn't crack, i told them nothing",
						"they can't break me");
					say(player, trackerTwo, "i'm sorry little man");
					npcsay(player, trackerTwo, "don't be, i have the position of the stronghold",
						"the y coordinate is 5");
					say(player, trackerTwo, "well done");
					npcsay(player, trackerTwo, "now leave before they find you and all is lost");
					say(player, trackerTwo, "hang in there");
					npcsay(player, trackerTwo, "go");
					break;
				case 5:
					if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_PROTECTION.id(), Optional.of(false))) {
						say(player, trackerTwo, "how are you tracker?");
						npcsay(player, trackerTwo, "now we have the globe 'm much better",
							"soon my comrades will come and free me");
						return;
					}
					say(player, trackerTwo, "hello again");
					npcsay(player, trackerTwo, "well done you've broken down there defenses",
						"this battle must be ours");
					break;
				case 6:
				case -1:
					say(player, trackerTwo, "hello");
					npcsay(player, trackerTwo, "when will this battle end?",
						"i feel like i've been locked up my whole life");
					break;
			}
		}
		else if (obj.getID() == 388) {
			if (player.getQuestStage(getQuestId()) >= 5 || player.getQuestStage(getQuestId()) == -1) {
				player.message("The ballista has been damaged, it is out of use");
				return;
			} else if (player.getQuestStage(getQuestId()) < 4) {
				mes("The ballista is damaged");
				delay(3);
				mes("It cannot be used until the gnomes have finished their repairs");
				delay(3);
			} else if (player.getQuestStage(getQuestId()) == 4) {
				fireBallistaMenu(player, obj);
			}
		}
		else if (obj.getID() == 393) {
			if (player.getQuestStage(getQuestId()) >= 5 || player.getQuestStage(getQuestId()) == -1) {
				mes("The wall is reduced to");
				delay(3);
				mes("Rubble, you manage to climb over");
				delay(3);
				if (player.getY() >= 633) {
					player.teleport(659, 632, false);
					if (!player.getCache().hasKey("over_gnomefield_wall")) {
						Npc commander = ifnearvisnpc(player, NpcId.KHAZARD_COMMANDER.id(), 12);
						if (commander != null) {
							npcsay(player, commander,
								"what?! how did you manage to get in here?");
							say(player, commander, "i've come for the orb");
							npcsay(player, commander, "i'll never let you take it!");
							commander.startCombat(player);
						}
						player.getCache().store("over_gnomefield_wall", true);
					}
				} else {
					player.teleport(659, 633, false);
				}
			} else {
				mes("The wall is damaged");
				delay(3);
				mes("But not enough to climb through");
				delay(3);
			}
		}
		else if (obj.getID() == KHAZARD_CHEST_OPEN || obj.getID() == KHAZARD_CHEST_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openGenericObject(obj, player, KHAZARD_CHEST_OPEN, "You open the chest");
			} else if (command.equalsIgnoreCase("close")) {
				closeGenericObject(obj, player, KHAZARD_CHEST_OPEN, "You close the chest");
			} else {
				if (!player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_PROTECTION.id(), Optional.empty())) {
					player.message("You search the chest");
					player.message("And find the orb of protection");
					give(player, ItemId.ORB_OF_PROTECTION.id(), 1);
				} else {
					player.message("You search the chest, but find nothing");
				}
			}
		}
	}

	private void fireBallistaMenu(Player player, GameObject obj) {
		boolean firstOption = false;
		boolean secondOption = false;
		boolean thirdOption = false;
		mes("To fire the ballista you Must first set the coordinates");
		delay(3);
		mes("Set the height coordinate to");
		delay(3);
		int MenuBallistaOne = multi(player, "coord 1", "coord 2",
			"coord 3", "coord 4", "coord 5");
		if (MenuBallistaOne >= 0 || MenuBallistaOne <= 4) {
			player.message("Set the x coordinate to");
			if (MenuBallistaOne == 3) {
				firstOption = true;
			}
			int MenuBallistaTwo = multi(player, "coord 1", "coord 2",
				"coord 3", "coord 4", "coord 5");
			if (MenuBallistaTwo >= 0 || MenuBallistaTwo <= 4) {
				player.message("Set the y coordinate to");
				if (MenuBallistaTwo == 2) {
					secondOption = true;
				}
				int MenuBallistaThree = multi(player, "coord 1", "coord 2",
					"coord 3", "coord 4", "coord 5");
				if (MenuBallistaThree == 4) {
					thirdOption = true;
				}
			}
		}
		player.message("You fire the ballista");
		mes("The huge spear flies through the air");
		delay(3);
		if (firstOption && secondOption && thirdOption) {
			mes("And screams down directly into the Khazard stronghold");
			delay(3);
			mes("A deafening crash echoes over the battlefield");
			delay(3);
			mes("The front entrance is reduced to rubble");
			delay(3);
			player.updateQuestStage(getQuestId(), 5);
		} else {
			mes("Straight over the khazard stronghold");
			delay(3);
			mes("Into the valleys behond");
			delay(3);
			mes("You've missed the target");
			delay(3);
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.KHAZARD_WARLORD.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.KHAZARD_WARLORD.id()) {
			if (player.getQuestStage(getQuestId()) == 6) {
				mes("As he falls to the ground...");
				delay(3);
				mes("A ghostly vapour floats upwards from his battle worn armour");
				delay(3);
				mes("Out of sight, you hear a shrill scream in the still air of the valley");
				delay(3);
				if (!player.getCarriedItems().hasCatalogID(ItemId.ORBS_OF_PROTECTION.id(), Optional.empty())) {
					player.message("You search his satchel and find the orbs of protection");
					give(player, ItemId.ORBS_OF_PROTECTION.id(), 1);
					if (!player.getCache().hasKey("looted_orbs_protect")) {
						player.getCache().store("looted_orbs_protect", true);
					}
				}
			}
		}
	}
}
