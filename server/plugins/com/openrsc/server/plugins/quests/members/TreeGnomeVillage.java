package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class TreeGnomeVillage implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener, WallObjectActionListener,
	WallObjectActionExecutiveListener, ObjectActionListener,
	ObjectActionExecutiveListener, PlayerKilledNpcListener,
	PlayerKilledNpcExecutiveListener {

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
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("Well done you have completed the treequest");
		p.message("@gre@You haved gained 2 quest points!");
		incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.TREE_GNOME_VILLAGE), true);
		addItem(p, ItemId.GNOME_EMERALD_AMULET_OF_PROTECTION.id(), 1);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.ELKOY.id(), NpcId.LOCAL_GNOME.id(), NpcId.REMSAI.id(), NpcId.BOLREN.id(),
				NpcId.GNOME_TROOP.id(), NpcId.COMMANDER_MONTAI.id(), NpcId.TRACKER_3.id(), NpcId.TRACKER_1.id(),
				NpcId.KHAZARD_WARLORD.id(), NpcId.KALRON.id()}, n.getID());
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KHAZARD_WARLORD.id()) {
			if (p.getQuestStage(getQuestId()) == 6
				|| p.getQuestStage(getQuestId()) == -1) {
				if (hasItem(p, ItemId.ORBS_OF_PROTECTION.id()) || p.getQuestStage(getQuestId()) == -1) {
					playerTalk(p, n, "i thought i killed you?");
					npcTalk(p, n,
						"fool.. warriors blessed by khazard don't die",
						"you can't kill that which is already dead",
						"however i can kill you");
					n.startCombat(p);
				} else {
					playerTalk(p, n, "you there, stop!");
					npcTalk(p, n, "go back to your pesky little green friends");
					playerTalk(p, n, "i've come for the orbs");
					npcTalk(p, n, "you're out of your depth traveller",
						"these orbs are part of a much larger picture");
					playerTalk(p, n, "they're stolen goods",
						"now give them here");
					npcTalk(p, n,
						"hee hee you really think you stand a chance?",
						"i'll crush you!");
					sleep(800);
					n.startCombat(p);
				}
			}
		}
		else if (n.getID() == NpcId.TRACKER_1.id()) {
			switch (p.getQuestStage(getQuestId())) {
				case 0:
				case 1:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "i can't talk now",
						"can't you see we're trying to win a battle here?");
					break;
				case 2:
					playerTalk(p, n, "hi there");
					npcTalk(p, n, "we're trying to hold them back",
						"but without more wood we won't be able to last long");
					playerTalk(p, n, "hang in there little man");
					break;
				case 3:
				case 4:
					playerTalk(p, n, "do you know the coordinates",
						"of the khazard stronghold?");
					npcTalk(p, n, "i managed to get one although it wasn't easy",
						"the height coordinate is 4");
					playerTalk(p, n, "well done");
					npcTalk(p, n, "the other two tracker gnomes",
						"should have the other coordinates",
						"if they're still alive");
					playerTalk(p, n, "ok, take care");
					break;
				case 5:
					if (hasItem(p, ItemId.ORB_OF_PROTECTION.id())) {
						playerTalk(p, n, "how are you tracker?");
						npcTalk(p, n, "now we have the globe i'm much better",
							"they won't stand a chance without it");
						return;
					}
					playerTalk(p, n, "hello again\"");
					npcTalk(p, n, "well done, you've broken down there defenses",
						"this battle must be ours");
					break;
				case 6:
				case -1:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "when will this battle end?",
						"i feel like i've been locked up my whole life");
					break;
			}
		}
		else if (n.getID() == NpcId.TRACKER_3.id()) {
			switch (p.getQuestStage(getQuestId())) {
				case 0:
				case 1:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "i can't talk now",
						"can't you see we're trying to win a battle here?");
					break;
				case 2:
					playerTalk(p, n, "hi there");
					npcTalk(p, n, "i can't stand this war",
						"the misery, the pain, it's driving me crazy",
						"when will it end?");
					p.message("He doesn't seem to be dealing with the battle very well");
					break;
				case 3:
				case 4:
					playerTalk(p, n, "are you ok?");
					p.message("The gnome looks dilerious");
					npcTalk(p, n, "ok? who's ok? not me", "hee hee");
					playerTalk(p, n, "what's wrong?");
					npcTalk(p, n, "you can't see me, no one can",
						"monsters, demons, they're all around me");
					playerTalk(p, n, "what do you mean?");
					npcTalk(p, n, "they're dancing, all of them hee hee");
					p.message("He's clearly lost the plot");
					playerTalk(p, n,
						"do you have the x coordinate for the khazard stronghold?");
					npcTalk(p, n, "who holds the stronghold?");
					playerTalk(p, n, "what?");
					npcTalk(p, n, "more than me", "less than our feet");
					playerTalk(p, n, "you're mad");
					npcTalk(p, n, "more than we", "and khazard's men are beat");
					p.message("The toll of war has affected his mind");
					playerTalk(p, n, "i'll pray for you little man");
					npcTalk(p, n, "all day we pray in the hay", "hee hee");
					p.message("The poor gnome has gone mad");
					break;
				case 5:
					if (hasItem(p, ItemId.ORB_OF_PROTECTION.id())) {
						playerTalk(p, n, "hello again\"");
						npcTalk(p, n, "don't talk to me, you can't see me",
							"no one can just the demons");
						p.message("The poor gnome has gone mad");
						return;
					}
					playerTalk(p, n, "hello again");
					npcTalk(p, n, "don't talk to me, you can't see me",
						"no one can just the demons");
					p.message("The poor gnome has gone mad");
					break;
				case 6:
				case -1:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "i feel dizzy, where am i?",
						"oh dear, oh dear i need some rest");
					playerTalk(p, n, "I think you do");
					break;
			}
		}
		else if (n.getID() == NpcId.COMMANDER_MONTAI.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "i can't talk now",
						"can't you see we're trying to win a battle here?",
						"if we can't hold back khazard's men",
						"we're all doomed");
					break;
				case 1:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "hello traveller",
						"are you here to help or just to watch?");
					playerTalk(p, n, "I've been sent by king Bolren",
						"to retrieve the orb of protection");
					npcTalk(p, n, "excellent we need all the help we can get",
						"i'm commander montai",
						"the orb is in the khazard stronghold to the north",
						"but until we weaken their defences",
						"we can't get close");
					playerTalk(p, n, "what can i do?");
					npcTalk(p, n, "first we need to strengthen our own defences",
						"we desperately need wood to make more battlements",
						"six loads of logs should do it",
						"once the battlements are gone it's all over");
					int firstOrb = showMenu(p, n, false, //do not send over
						"Ok, i'll gather some wood",
						"Sorry i no longer want to be involved");
					if (firstOrb == 0) {
						playerTalk(p, n, "ok, i'll gather some wood");
						npcTalk(p, n, "please be as quick as you can",
							"i don't know how much longer we can hold out");
						p.updateQuestStage(getQuestId(), 2);
					} else if (firstOrb == 1) {
						playerTalk(p, n, "sorry i no longer want to be involved");
						npcTalk(p, n, "that's a shame we could",
							"have done with your help");
					}
					break;
				case 2:
					playerTalk(p, n, "hello");
					npcTalk(p, n,
						"hello again, we're still desperate for wood soldier");
					if (hasItem(p, ItemId.LOGS.id(), 6)) {
						p.getInventory().remove(ItemId.LOGS.id(), 6);
						playerTalk(p, n, "i have some here");
						p.message("you give some wood to the commander");
						npcTalk(p,
							n,
							"that's excellent now we can make more defensive battlements",
							"give me a moment to organise the troops",
							"and then come speak to me",
							"i'll inform you of our next phase of attack");
						p.updateQuestStage(getQuestId(), 3);
					} else {
						npcTalk(p, n, "we need at least six loads of logs");
						playerTalk(p, n, "i'll see what i can do");
						npcTalk(p, n, "thankyou");
					}
					break;
				case 3:
					playerTalk(p, n, "how are you doing montai?");
					npcTalk(p, n, "we're hanging in there soldier",
						"for the next phase of the attack",
						"we need to breech their stronghold",
						"the ballista can break through the stronghold wall",
						"and then we can advance and seize back the orb");
					playerTalk(p, n, "so what's the problem?");
					npcTalk(p,
						n,
						"from this distance we can't get an accurate shot away",
						"we need the correct coordinates of the stronghold",
						"for a direct hit",
						"i've sent out three tracker gnomes to gather them");
					playerTalk(p, n, "have they returned?");
					npcTalk(p, n, "i'm afraid not and we're running out of time",
						"I need you to go into the heart of the battlefield",
						"find the trackers and bring back the coordinates.",
						"Do you think you can do it?");
					int phasetwo = showMenu(p, n, false, //do not send over
						"No, i've had enough of your battle",
						"I'll try my best");
					if (phasetwo == 0) {
						playerTalk(p, n, "no, i've had enough of your battle");
						npcTalk(p, n, "i understand, this isn't your fight");
					} else if (phasetwo == 1) {
						playerTalk(p, n, "i'll try my best");
						npcTalk(p,
							n,
							"thankyou, you're braver than most",
							"i don't know how long i will be able to hold out",
							"once you have the coordinates",
							"come back and fire the ballista",
							"right into those monsters",
							"if you can retrieve the orb and bring safety back to my people",
							"none of the blood spilled on this field will be in vain");
						p.updateQuestStage(getQuestId(), 4);
					}
					break;
				case 4:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "hello warrior we need the coordinates",
						"for a direct hit from the ballista",
						"once you have a direct hit you will be able",
						"to enter the stronghold and retrieve the orb");
					break;
				case 5:
					if (hasItem(p, ItemId.ORB_OF_PROTECTION.id())) {
						playerTalk(p, n, "i have the orb of protection");
						npcTalk(p, n, "incredible, for a human",
							"you really are something");
						playerTalk(p, n, "thanks... i think!");
						npcTalk(p, n, "I'll stay here with my troops",
							"and try and hold khazard's men back",
							"you return the orb to the gnome village",
							"go as quick as you can",
							"the village is still unprotected");
						return;
					}
					playerTalk(p, n, "i've breeched the stronghold");
					npcTalk(p, n, "i saw, that was a beautiful sight",
						"the khazard troops didn't know what hit them",
						"now is the time to retrieve the orb",
						"it's all in your hands", "i'll be praying for you");
					break;
				case 6:
				case -1:
					playerTalk(p, n, "hello montai, how are you?");
					npcTalk(p, n, "i'm ok, this battle is going",
						"to take longer to win than i expected",
						"the khazard troops won't give up even without the orb");
					playerTalk(p, n, "hang in there");
					break;
			}
		}
		else if (n.getID() == NpcId.GNOME_TROOP.id()) {
			if (p.getQuestStage(getQuestId()) == 5
				|| p.getQuestStage(getQuestId()) == -1) {
				playerTalk(p, n, "hi");
				npcTalk(p, n, "draw your sword warrior",
					"and fight along side us!");
				return;
			} else if (p.getQuestStage(getQuestId()) == 0
				|| p.getQuestStage(getQuestId()) >= 2
				|| p.getQuestStage(getQuestId()) <= 4) {
				playerTalk(p, n, "hello");
				npcTalk(p, n, "i can't talk now", "can't you see we're trying",
					"to win a battle here?");
				return;
			} else {
				playerTalk(p, n, "hello");
				npcTalk(p, n, "death to khazard and all who serve him!");
				return;
			}
		}
		else if (n.getID() == NpcId.BOLREN.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "well hello stranger",
						"my name's bolren, i'm the king of the tree gnomes",
						"i'm surprised you made it in",
						"maybe i made the maze too easy");
					playerTalk(p, n, "maybe");
					npcTalk(p, n,
						"i'm afraid i have more serious concerns at the moment",
						"very serious");
					int first = showMenu(p, n, false, //do not send over
						"I'll leave you to it then",
						"Can i help at all?");
					if (first == 0) {
						playerTalk(p, n, "i'll leave you too it then");
						npcTalk(p, n, "ok take care");
					} else if (first == 1) {
						playerTalk(p, n, "can i help at all?");
						npcTalk(p, n, "i'm glad you asked",
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
						playerTalk(p, n, "how can i help?");
						npcTalk(p, n, "you would be a huge benefit on the battlefield",
							"if you would go there and try and retrieve the orb",
							"my people and i will be forever grateful");
						int second = showMenu(p, n, false, //do not send over
							"I would be glad to help",
							"I'm sorry but i won't be involved");
						if (second == 0) {
							playerTalk(p, n, "i would be glad to help");
							npcTalk(p,
								n,
								"thank you",
								"the battlefield is to the north of the maze",
								"commander montai will inform you of their current situation",
								"that's if he's still alive",
								"my assistant shall guide you out",
								"try your best to return the orb",
								"good luck friend");
							p.message("A gnome guides you out of the maze");
							p.teleport(624, 675, false);
							if (p.getQuestStage(getQuestId()) == 0) {
								p.updateQuestStage(getQuestId(), 1);
							}
						} else if (second == 1) {
							playerTalk(p, n, "i'm sorry but i won't be involved");
							npcTalk(p, n, "ok then, travel safe");
						}
					}
					break;
				case 1:
					playerTalk(p, n, "hello bolren");
					npcTalk(p, n, "hello traveller, we must retrieve the orb",
						"it's being held by khazard troops",
						"to the west of the maze",
						"above the khazard fight arena");
					playerTalk(p, n, "ok i'll try my best");
					break;
				case 2:
				case 3:
				case 4:
					playerTalk(p, n, "hello bolren");
					npcTalk(p, n, "the orb is being held at the battlefield",
						"to the north of the maze",
						"above the khazard fight arena");
					break;
				case 5:
					playerTalk(p, n, "king bolren are you ok?");
					if (hasItem(p, ItemId.ORB_OF_PROTECTION.id())) {
						playerTalk(p, n, "i have the orb");
						npcTalk(p, n, "thank you traveller, but it's too late",
							"we're all doomed", "oh my the misery, the horror");
						playerTalk(p, n, "what happened?");
						npcTalk(p, n, "they came in the night",
							"i don't how many, enough");
						playerTalk(p, n, "who?");
						npcTalk(p, n, "khazard troops",
							"they slaughtered anyone who got in their way",
							"women, children, my wife");
						playerTalk(p, n, "i'm sorry");
						npcTalk(p, n, "they took the other orbs",
							"now we're defenseless");
						playerTalk(p, n, "where did they take them?");
						npcTalk(p, n, "they headed north of the",
							"battlefields to the dead valleys",
							"a warlord carries the orbs");
						int newOrbs = showMenu(p, n, false, //do not send over
							"I will find the warlord and bring back the orbs",
							"I'm sorry but i can't help");
						if (newOrbs == 0) {
							playerTalk(p, n, "i will find the warlord and bring back the orbs");
							npcTalk(p, n, "you are brave",
								"but this task will be tough even for you,",
								"i wish you the best of luck traveller",
								"once again you are our only hope",
								"i will safeguard this orb",
								"and pray for your safe return",
								"my assistant will guide you out");
							p.message("A gnome guides you out of the maze");
							p.teleport(624, 675, false);
							p.updateQuestStage(getQuestId(), 6);
							removeItem(p, ItemId.ORB_OF_PROTECTION.id(), 1);
						} else if (newOrbs == 1) {
							playerTalk(p, n, "i'm sorry but i can't help");
							npcTalk(p, n, "i understand, this isn't your battle");
						}
					} else {
						npcTalk(p, n, "do you have the orb?");
						playerTalk(p, n, "no, i'm afraid not");
						npcTalk(p, n, "please, we must have the orb",
							"if we are to survive");
					}
					break;
				case 6:
					if (hasItem(p, ItemId.ORBS_OF_PROTECTION.id())) {
						playerTalk(p, n, "bolren, i have returned");
						npcTalk(p, n, "you made it back", "do you have the orbs?");
						playerTalk(p, n, "i have them here");
						npcTalk(p,
							n,
							"hooray, you're amazing",
							"i didn't think it was possible",
							"but you've saved us",
							"once the orbs are replaced we will be safe once more",
							"come with me and we shall begin the ceremony");
						p.teleport(658, 696, true);
						playerTalk(p, n, "what now?");
						npcTalk(p, n,
							"the spirit tree has looked over us for centuries",
							"now we must pay our respects");
						message(p, "bolren takes the orbs",
							"the gnomes begin to chant", "Su tana, en tania",
							"They continue to chant",
							"As the king gnome climbs the tree",
							"placing the two Orbs at the peak of the spirit tree");
						removeItem(p, ItemId.ORBS_OF_PROTECTION.id(), 1);
						n.displayNpcTeleportBubble(656, 695);
						sleep(1000);
						n.displayNpcTeleportBubble(656, 695);
						sleep(1000);
						n.displayNpcTeleportBubble(656, 695);
						npcTalk(p, n, "now at last my people are safe once more",
							"and can live in peace");
						playerTalk(p, n, "i'm pleased i could help");
						npcTalk(p,
							n,
							"you are modest brave traveller",
							"please for your efforts take this amulet",
							"it's made from the same sacred stone as the orbs of protection",
							"it will help keep you safe on your journeys");
						playerTalk(p, n, "thank you king bolren");
						npcTalk(p, n, "the tree has many other powers",
							"some of which i cannot reveal",
							"however as a friend of the gnome",
							"people you can now use the tree's",
							"magic to teleport to other trees",
							"grown from related seeds");
						p.getCache().remove("looted_orbs_protect");
						p.sendQuestComplete(Quests.TREE_GNOME_VILLAGE);
					} else if (p.getCache().hasKey("looted_orbs_protect")) {
						playerTalk(p, n, "bolren, i have returned");
						npcTalk(p, n, "you made it back", "do you have the orbs?");
						playerTalk(p, n, "no, i'm afraid not");
						npcTalk(p, n, "please, we must have the orbs",
							"if we are to survive");
					} else {
						playerTalk(p, n, "hello bolren");
						npcTalk(p,
							n,
							"the orbs are gone",
							"taken north of the battlefield by a khazard warlord",
							"we're all doomed");
					}
					break;
				case -1:
					playerTalk(p, n, "hello again bolren");
					npcTalk(p, n, "well hello, it's good to see you again");
					if (!hasItem(p, ItemId.GNOME_EMERALD_AMULET_OF_PROTECTION.id())) {
						playerTalk(p, n, "i've lost my amulet");
						npcTalk(p, n, "oh dear", "here take another");
						addItem(p, ItemId.GNOME_EMERALD_AMULET_OF_PROTECTION.id(), 1);
					} else {
						playerTalk(p, n, "good to see you");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.REMSAI.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "well done, well done",
						"not many find their way in here",
						"i'm remsai, a tree gnome",
						"we live in this maze for our protection",
						"have a look around and enjoy");
					break;
				case 1:
					npcTalk(p, n, "oh my, oh my");
					playerTalk(p, n, "what's wrong?");
					npcTalk(p, n, "the orb, they have the orb",
						"it must be returned",
						"i'm remsai, a tree gnome",
						"or we're doomed");
					break;
				case 2:
				case 3:
				case 4:
					npcTalk(p, n, "the orb, they have the orb",
						"if it's not returned we're doomed");
					break;
				case 5:
					playerTalk(p, n, "hello remsai");
					npcTalk(p, n, "hello, did you find the orb?");
					if (hasItem(p, ItemId.ORB_OF_PROTECTION.id())) {
						playerTalk(p, n, "i have it here");
						npcTalk(p, n, "you're our saviour");
					} else {
						playerTalk(p, n, "no, i'm afraid not");
						npcTalk(p, n, "please we must have the orb",
							"if we are to survive");
					}
					break;
				case 6:
					if (hasItem(p, ItemId.ORBS_OF_PROTECTION.id())) {
						playerTalk(p, n, "i've returned");
						npcTalk(p, n, "you're back, well done brave adventurer",
							"now the orbs are safe",
							"we can perform the ritual for the orb tree",
							"and we can live in peace once again");
					} else {
						playerTalk(p, n, "are you ok?");
						npcTalk(p, n, "Khazard's men came",
							"without the orb we were defenseless",
							"they killed many", "and then took our last hope",
							"the other orbs", "now surely we're all doomed",
							"without them the spirit tree is useless");
					}
					break;
				case -1:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "hi there traveller",
						"you're a legend around these parts");
					playerTalk(p, n, "thanks remsai");
					break;
			}
		}
		else if (n.getID() == NpcId.LOCAL_GNOME.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "lardi dee, lardi da");
					playerTalk(p, n, "are you alright?");
					npcTalk(p, n, "hee hee, lardi da, lardi dee");
					p.message("The gnome appears to be singing");
					break;
				case 5:
					playerTalk(p, n, "hello little man");
					npcTalk(p, n, "little man stronger than big man",
						"hee hee", "lardi dee, lardi da");
					p.message("Cheeky little gnome");
					break;
				case 6:
					if (p.getCache().hasKey("looted_orbs_protect")) {
						playerTalk(p, n, "hello gnome");
						npcTalk(p, n, "soon we're gonna have the sacred ceremony",
							"and boy am i going to party",
							"lock up your daughters", "hee hee");
					} else {
						playerTalk(p, n, "hi");
						npcTalk(p, n,
							"must save the orbs and kill the khazard warlord",
							"that will be fun", "hee hee");
					}
					break;
				case -1:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "you're the best");
					playerTalk(p, n, "thanks");
					npcTalk(p, n, "well, i'm better", "hee hee");
					break;
			}
		}
		else if (n.getID() == NpcId.ELKOY.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					playerTalk(p, n, "hello there");
					npcTalk(p, n, "hello, welcome to our maze",
						"i'm elkoy the tree gnome");
					playerTalk(p, n, "i haven't heard of your sort");
					npcTalk(p, n, "there's not many of us left",
						"once you could find tree gnomes",
						"anywhere in the world, now we hide",
						"in small groups to avoid capture");
					playerTalk(p, n, "capture by whom?");
					npcTalk(p, n, "tree gnomes have been hunted",
						"for so called 'fun' since i",
						"can remember, our main threat",
						"nowadays are General Khazard's troops",
						"they know no mercy, but are also",
						"very dense, they'll never find",
						"their way through our maze", "have fun");
					break;
				case 1:
					playerTalk(p, n, "hello elkoy");
					npcTalk(p, n, "oh my, oh my");
					playerTalk(p, n, "what's wrong?");
					npcTalk(p, n, "the orb, they have the orb", "we're doomed");
					break;
				case 2:
				case 3:
				case 4:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "you must retrieve the orb",
						"or the gnome village is doomed");
					break;
				case 5:
					playerTalk(p, n, "hello elkoy");
					npcTalk(p, n, "you're back! and the orb?");
					if (hasItem(p, ItemId.ORB_OF_PROTECTION.id())) {
						playerTalk(p, n, "i have it here");
						npcTalk(p,
							n,
							"you're our saviour",
							"please return it to the village and we are all saved",
							"would you like me to show you the way to the village?");
						int gotOrb = showMenu(p, n, false, //do not send over
							"Yes please", "No thanks Elkoy");
						if (gotOrb == 0) {
							playerTalk(p, n, "yes please");
							npcTalk(p, n, "ok then follow me");
							p.message("elkoy leads you to the gnome village");
							p.teleport(644, 697, false);
						} else if (gotOrb == 1) {
							playerTalk(p, n, "no thanks elkoy");
							npcTalk(p, n, "ok then take care");
						}
					} else {
						playerTalk(p, n, "no, i'm afraid not");
						npcTalk(p, n, "please, we must have the orb",
							"if we are to survive");
					}
					break;
				case 6:
					playerTalk(p, n, "hello elkoy");
					if (p.getCache().hasKey("looted_orbs_protect")) {
						npcTalk(p, n, "you truly are a hero");
						playerTalk(p, n, "thanks");
						npcTalk(p, n, "you saved us by", "returning the orbs of\"",
							"protection, i'm humbled", "and wish you well",
							"would you like me to show",
							"you the way to the village?");
						int finaleMenu = showMenu(p, n, false, //do not send over
							"Yes please", "No thanks elkoy");
						if (finaleMenu == 0) {
							playerTalk(p, n, "yes please");
							npcTalk(p, n, "ok then follow me");
							p.message("elkoy leads you to the gnome village");
							p.teleport(644, 697, false);
						} else if (finaleMenu == 1) {
							playerTalk(p, n, "no thanks elkoy");
							npcTalk(p, n, "ok then take care");
						}
					} else {
						npcTalk(p, n, "did you hear? khazard's men",
							"have pillaged the village!",
							"they slaughtered many", "and took the other orbs",
							"in an attempt to lead us", "all out of the maze",
							"when will the misery end?",
							"would you like me to show",
							"you the way to the village?");
						int menu = showMenu(p, n, false, //do not send over
							"Yes please", "No thanks elkoy");
						if (menu == 0) {
							playerTalk(p, n, "yes please");
							npcTalk(p, n, "ok then follow me");
							p.message("elkoy leads you to the gnome village");
							p.teleport(644, 697, false);
						} else if (menu == 1) {
							playerTalk(p, n, "no thanks elkoy");
							npcTalk(p, n, "ok then take care");
						}
					}
					break;
				case -1:
					playerTalk(p, n, "hello little man");
					npcTalk(p, n, "hi there, hope life", "is treating you well",
						"would you like me to show",
						"you the way to the village?");
					int elkoy = showMenu(p, n, false, //do not send over
						"Yes please", "No thanks elkoy");
					if (elkoy == 0) {
						playerTalk(p, n, "yes please");
						npcTalk(p, n, "ok then follow me");
						p.message("elkoy leads you to the gnome village");
						p.teleport(644, 697, false);
					} else if (elkoy == 1) {
						playerTalk(p, n, "no thanks elkoy");
						npcTalk(p, n, "ok then take care");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.KALRON.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					playerTalk(p, n, "hello");
					npcTalk(p, n, "gotta find a way out",
						"we built this maze for protection",
						"but i can't get used to it",
						"i'm always getting lost");
					break;
				case 5:
					playerTalk(p, n, "hello there");
					npcTalk(p, n, "oh my, oh my",
						"the village has been",
						"and i'm still lost",
						"oh dear");
					break;
				case 6:
					if (p.getCache().hasKey("looted_orbs_protect")) {
						playerTalk(p, n, "hello little man");
						npcTalk(p, n, "hello i hope they come out and find me soon,",
							"it's getting cold");
					} else {
						playerTalk(p, n, "hello, how are you?");
						npcTalk(p, n,
							"oh my i'll never find my way back",
							"before khazard's men come and hunt me down");
					}
					break;
				case -1:
					npcTalk(p, n, "are you trying to be funny?");
					playerTalk(p, n, "no");
					npcTalk(p, n, "hmmm");
					break;
			}
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
										 Player player) {
		return (obj.getID() == 101 && obj.getY() == 705) || (obj.getID() == 101 && obj.getX() == 540 && obj.getY() == 445);
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 101 && obj.getY() == 705) {
			p.message("You push your way through the fence");
			doDoor(obj, p, 16);
		}
		else if (obj.getID() == 101 && obj.getX() == 540 && obj.getY() == 445) {
			p.message("You push your way through the fence");
			doDoor(obj, p, 16);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
									 Player player) {
		return DataConversions.inArray(new int[] {392, 388, 393, KHAZARD_CHEST_OPEN, KHAZARD_CHEST_CLOSED}, obj.getID());
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 392) {
			Npc trackerTwo = getNearestNpc(p, NpcId.TRACKER_2.id(), 5);
			switch (p.getQuestStage(getQuestId())) {
				case 0:
				case 1:
					playerTalk(p, trackerTwo, "hello");
					npcTalk(p, trackerTwo, "i can't talk now",
						"if the guards catch me i'll be dead gnome meat");
					break;
				case 2:
					playerTalk(p, trackerTwo, "hi there");
					npcTalk(p, trackerTwo, "the battle is far from over",
						"if you have a pure heart you will help us win");
					break;
				case 3:
				case 4:
					p.message("The gnome looks beaten and weak");
					npcTalk(p, trackerTwo, "they caught me spying on the stronghold..",
						"they beat and tortured me",
						"but i didn't crack, i told them nothing",
						"they can't break me");
					playerTalk(p, trackerTwo, "i'm sorry little man");
					npcTalk(p, trackerTwo, "don't be, i have the position of the stronghold",
						"the y coordinate is 5");
					playerTalk(p, trackerTwo, "well done");
					npcTalk(p, trackerTwo, "now leave before they find you and all is lost");
					playerTalk(p, trackerTwo, "hang in there");
					npcTalk(p, trackerTwo, "go");
					break;
				case 5:
					if (hasItem(p, ItemId.ORB_OF_PROTECTION.id())) {
						playerTalk(p, trackerTwo, "how are you tracker?");
						npcTalk(p, trackerTwo, "now we have the globe 'm much better",
							"soon my comrades will come and free me");
						return;
					}
					playerTalk(p, trackerTwo, "hello again");
					npcTalk(p, trackerTwo, "well done you've broken down there defenses",
						"this battle must be ours");
					break;
				case 6:
				case -1:
					playerTalk(p, trackerTwo, "hello");
					npcTalk(p, trackerTwo, "when will this battle end?",
						"i feel like i've been locked up my whole life");
					break;
			}
		}
		else if (obj.getID() == 388) {
			if (p.getQuestStage(getQuestId()) >= 5 || p.getQuestStage(getQuestId()) == -1) {
				p.message("The ballista has been damaged, it is out of use");
				return;
			} else if (p.getQuestStage(getQuestId()) < 4) {
				message(p, "The ballista is damaged",
					"It cannot be used until the gnomes have finished their repairs");
			} else if (p.getQuestStage(getQuestId()) == 4) {
				fireBallistaMenu(p, obj);
			}
		}
		else if (obj.getID() == 393) {
			if (p.getQuestStage(getQuestId()) >= 5 || p.getQuestStage(getQuestId()) == -1) {
				message(p, "The wall is reduced to",
					"Rubble, you manage to climb over");
				if (p.getY() >= 633) {
					p.teleport(659, 632, false);
					if (!p.getCache().hasKey("over_gnomefield_wall")) {
						Npc commander = getNearestNpc(p, NpcId.KHAZARD_COMMANDER.id(), 12);
						if (commander != null) {
							npcTalk(p, commander,
								"what?! how did you manage to get in here?");
							playerTalk(p, commander, "i've come for the orb");
							npcTalk(p, commander, "i'll never let you take it!");
							commander.startCombat(p);
						}
						p.getCache().store("over_gnomefield_wall", true);
					}
				} else {
					p.teleport(659, 633, false);
				}
			} else {
				message(p, "The wall is damaged",
					"But not enough to climb through");
			}
		}
		else if (obj.getID() == KHAZARD_CHEST_OPEN || obj.getID() == KHAZARD_CHEST_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openGenericObject(obj, p, KHAZARD_CHEST_OPEN, "You open the chest");
			} else if (command.equalsIgnoreCase("close")) {
				closeGenericObject(obj, p, KHAZARD_CHEST_OPEN, "You close the chest");
			} else {
				if (!hasItem(p, ItemId.ORB_OF_PROTECTION.id())) {
					p.message("You search the chest");
					p.message("And find the orb of protection");
					addItem(p, ItemId.ORB_OF_PROTECTION.id(), 1);
				} else {
					p.message("You search the chest, but find nothing");
				}
			}
		}
	}

	private void fireBallistaMenu(Player p, GameObject obj) {
		boolean firstOption = false;
		boolean secondOption = false;
		boolean thirdOption = false;
		message(p, "To fire the ballista you Must first set the coordinates",
			"Set the height coordinate to");
		int MenuBallistaOne = showMenu(p, "coord 1", "coord 2",
			"coord 3", "coord 4", "coord 5");
		if (MenuBallistaOne >= 0 || MenuBallistaOne <= 4) {
			p.message("Set the x coordinate to");
			if (MenuBallistaOne == 3) {
				firstOption = true;
			}
			int MenuBallistaTwo = showMenu(p, "coord 1", "coord 2",
				"coord 3", "coord 4", "coord 5");
			if (MenuBallistaTwo >= 0 || MenuBallistaTwo <= 4) {
				p.message("Set the y coordinate to");
				if (MenuBallistaTwo == 2) {
					secondOption = true;
				}
				int MenuBallistaThree = showMenu(p, "coord 1", "coord 2",
					"coord 3", "coord 4", "coord 5");
				if (MenuBallistaThree == 4) {
					thirdOption = true;
				}
			}
		}
		p.message("You fire the ballista");
		message(p, "The huge spear flies through the air");
		if (firstOption && secondOption && thirdOption) {
			message(p, "And screams down directly into the Khazard stronghold",
				"A deafening crash echoes over the battlefield",
				"The front entrance is reduced to rubble");
			p.updateQuestStage(getQuestId(), 5);
		} else {
			message(p, "Straight over the khazard stronghold",
				"Into the valleys behond", "You've missed the target");
		}
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == NpcId.KHAZARD_WARLORD.id();
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KHAZARD_WARLORD.id()) {
			n.resetCombatEvent();
			n.killedBy(p);
			if (p.getQuestStage(getQuestId()) == 6) {
				message(p,
					"As he falls to the ground...",
					"A ghostly vapour floats upwards from his battle worn armour",
					"Out of sight, you hear a shrill scream in the still air of the valley");
				if (!hasItem(p, ItemId.ORBS_OF_PROTECTION.id())) {
					p.message("You search his satchel and find the orbs of protection");
					addItem(p, ItemId.ORBS_OF_PROTECTION.id(), 1);
					if (!p.getCache().hasKey("looted_orbs_protect")) {
						p.getCache().store("looted_orbs_protect", true);
					}
				}
			}
		}
	}
}
