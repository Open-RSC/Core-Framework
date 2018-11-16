package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Waterfall_Quest implements QuestInterface,TalkToNpcListener,
		TalkToNpcExecutiveListener, ObjectActionListener,
		ObjectActionExecutiveListener, InvUseOnObjectListener,
		InvUseOnObjectExecutiveListener, InvActionListener,
		InvActionExecutiveListener, WallObjectActionListener,
		WallObjectActionExecutiveListener, InvUseOnWallObjectListener,
		InvUseOnWallObjectExecutiveListener {

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
		addItem(p, 796, 40);
		addItem(p, 172, 2);
		addItem(p, 161, 2);
		int[] questData = Quests.questData.get(Quests.WATERFALL_QUEST);
		//keep order kosher
		int[] skillIDs = {STRENGTH, ATTACK};
		for(int i=0; i<skillIDs.length; i++) {
			questData[Quests.MAPIDX_SKILL] = skillIDs[i];
			incQuestReward(p, questData, i==(skillIDs.length-1));
		}
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 470) {
			switch (p.getQuestStage(this)) {
			case 0:
				playerTalk(p, n, "hello madam");
				npcTalk(p, n, "ah, hello there");
				npcTalk(p, n, "nice to see an outsider for a change");
				npcTalk(p, n, "are you busy young man?, i have a problem");
				int option = showMenu(p, n, "i'm afraid i'm in a rush",
						"how can i help?");
				if (option == 0) {
					npcTalk(p, n, "oh okay, never mind");
				} else if (option == 1) {
					npcTalk(p, n,
							"it's my son hudon, he's always getting into trouble");
					npcTalk(p, n,
							"the boy's convinced there's hidden treasure in the river");
					npcTalk(p, n, "and i'm a bit worried about his safety");
					npcTalk(p, n, "the poor lad can't even swim");
					playerTalk(p, n,
							"i could go and take a look for you if you like");
					npcTalk(p, n, "would you kind sir?");
					npcTalk(p, n,
							"you can use the small raft out back if you wish");
					npcTalk(p, n,
							"do be careful, the current down stream is very strong");
					p.updateQuestStage(this, 1);
				}
				break;
			case 1:
				playerTalk(p, n, "hello almera");
				npcTalk(p, n, "hello brave adventurer");
				npcTalk(p, n, "have you seen my boy yet?");
				playerTalk(p, n,
						"i'm afraid not, but i'm sure he hasn't gone far");
				npcTalk(p, n, "i do hope so");
				npcTalk(p, n, "you can't be too careful these days");
				break;
			case 2:
				npcTalk(p, n, "well hello, you're still around then");
				playerTalk(p, n,
						"i saw hudon by the river but he refused to come back with me");
				npcTalk(p, n, "yes he told me");
				npcTalk(p, n, "the foolish lad came in drenched to the bone");
				npcTalk(p, n,
						"he had fallen into the waterfall, lucky he wasn't killed");
				npcTalk(p, n,
						"now he can spend the rest of the summer in his room");
				playerTalk(p, n, "any ideas on what i could do while i'm here?");
				npcTalk(p, n,
						"why don't you visit the tourist centre south of the waterfall?");

				break;
			}
		} else if (n.getID() == 471) {
			switch (p.getQuestStage(this)) {
			case 1:
				playerTalk(p, n, "hello son, are you okay?");
				npcTalk(p, n, "it looks like you need the help");
				playerTalk(p, n, "your mum sent me to find you");
				npcTalk(p, n, "don't play nice with me");
				npcTalk(p, n, "i know your looking for the treasure");
				playerTalk(p, n, "where is this treasure you talk of?");
				npcTalk(p, n, "just because i'm small doesn't mean i'm dumb");
				npcTalk(p, n,
						"if i told you, you would take it all for yourself");
				playerTalk(p, n, "maybe i could help");
				npcTalk(p, n, "i'm fine alone");
				p.updateQuestStage(this, 2);
				message(p, "hudon is refusing to leave the waterfall");
				break;
			}
		} else if (n.getDef().getName().equals("golrie")) {
			if (!hasItem(p, 787, 1)) {
				playerTalk(p, n, "is your name golrie?");
				npcTalk(p, n, " that's me");
				npcTalk(p, n, " i've been stuck in here for weeks");
				npcTalk(p, n,
						" those goblins are trying to steal my families heirlooms");
				npcTalk(p, n, " my grandad gave me all sorts of old junk");
				playerTalk(p, n, "do you mind if i have a look?");
				npcTalk(p, n, " no, of course not");
				message(p, "mixed with the junk on the floor",
						"you find glarials pebble");
				addItem(p, 787, 1);
				playerTalk(p, n, "could i take this old pebble?");
				npcTalk(p, n, " oh that, yes have it");
				npcTalk(p, n, " it's just some old elven junk i believe");
				removeItem(p, 789, 1);
				message(p, "you give golrie the key");
				npcTalk(p, n, " well thanks again for the key");
				npcTalk(p, n,
						" i think i'll wait in here until those goblins get bored and leave");
				playerTalk(p, n, "okay, take care golrie");
				p.getCache().store("golrie_key", true);
				if (!p.getCache().hasKey("golrie_key")) {
					p.getCache().store("golrie_key", true);
				}
									
			} else {
				playerTalk(p, n, "is your name golrie?");
				npcTalk(p, n, " that's me");
				npcTalk(p, n, " i've been stuck in here for weeks");
				npcTalk(p, n,
						" those goblins are trying to steal my families heirlooms");
				npcTalk(p, n, " my grandad gave me all sorts of old junk");
				playerTalk(p, n, "do you mind if i have a look?");
				npcTalk(p, n, " no, of course not");
				removeItem(p, 789, 1);
				message(p, "you find nothing of interest",
						"you give golrie the key");
				npcTalk(p, n, " thanks a lot for the key traveller");
				npcTalk(p, n,
						" i think i'll wait in here until those goblins get bored and leave");
				playerTalk(p, n, "okay, take care golrie");
				if (!p.getCache().hasKey("golrie_key")) {
					p.getCache().store("golrie_key", true);
				}
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getDef().getName().equals("Almera")
				|| n.getDef().getName().equals("Hudon")
				|| n.getDef().getName().equals("golrie");
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		return obj.getID() == 492 || obj.getID() == 486 || obj.getID() == 467
				|| obj.getID() == 507 || obj.getID() == 481
				|| obj.getID() == 471 || obj.getID() == 479
				|| obj.getID() == 470 || obj.getID() == 480
				|| obj.getID() == 463 || obj.getID() == 462
				|| obj.getID() == 482 || obj.getID() == 464;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 464) {
			message(p, "you board the small raft", "and push off down stream",
					"the raft is pulled down stream by strong currents",
					"you crash into a small land mound");
			p.teleport(662, 463, false);
			Npc hudon = World.getWorld().getNpc(471, 0, 2000, 0, 2000);
			if (hudon != null) {
				onTalkToNpc(p, hudon);
			}
		} else if (obj.getID() == 463 || obj.getID() == 462
				|| obj.getID() == 482) {
			if (command.equals("jump to next")) {
				message(p, "the tree is too far off to jump to",
						"you need some way to pull yourself across");
			} else if (command.equals("jump off")) {
				message(p, "you jump into the wild rapids");
				p.teleport(645, 485, false);
				p.damage(DataConversions.random(4, 10));
				playerTalk(p, null, "ouch!");
				message(p, "you tumble over the water fall",
						"and are washed up by the river side");
			}
		} else if (obj.getID() == 470) {
			message(p, "you search the bookcase");
			if (!p.getInventory().hasItemId(788)) {
				message(p, "and find a book named 'book on baxtorian'");
				addItem(p, 788, 1);
			} else
				message(p, "but find nothing of interest");
		} else if (obj.getID() == 481) {
			message(p, "you search the crate");
			if (!p.getInventory().hasItemId(789)) {
				message(p, "and find a large key");
				addItem(p, 789, 1);
			} else {
				p.message("but find nothing");
			}
		} else if (obj.getID() == 480) {
			Npc n = World.getWorld().getNpc(475, 663, 668, 3520, 3529);		
			if (p.getQuestStage(this) == 0) {
				npcTalk(p, n, "what are you doing down here",
				"leave before you get yourself into trouble");
				return;
				}
			else if (p.getLocation().getY() <= 3529) {
				doGate(p, obj);				
				return;
			}
					
			else if (p.getLocation().getY() >= 3530 && p.getCache().hasKey("golrie_key") || p.getQuestStage(this) == -1) {
				p.message("golrie has locked himself in");		
				return;
				}
			
			if (p.getLocation().getY() >= 3530 && !p.getInventory().hasItemId(789)) {					
						if (n != null) {
							playerTalk(p, n, "are you ok?");
							npcTalk(p, n, "it's just those blasted hobgoblins",
									"i locked myself in here for protection",
									"but i've left the key somewhere",
									"and now i'm stuck");
							playerTalk(p, n, "okay, i'll have a look for a key");
							return;
							}
					}
					else if (p.getLocation().getY() >= 3530 && p.getInventory().hasItemId(789)) {			
									if (n != null) {
									playerTalk(p, n, "are you ok?");
									npcTalk(p, n, "it's just those blasted hobgoblins",
											"i locked myself in here for protection",
											"but i've left the key somewhere",
											"and now i'm stuck");
									playerTalk(p, n, "i found a key");
									npcTalk(p, n, "well don't wait all day",
											"give it a try");	
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
		} else if (obj.getID() == 507) {
			message(p, "you search the cupboard");
			if (!hasItem(p, 805, 1)) {
				p.message("and find a metel urn");
				addItem(p, 805, 1);
			} else {
				p.message("and find nothing");
			}
		} else if (obj.getID() == 467) {
			message(p, "you search the coffin");
			if (!hasItem(p, 782)) {
				message(p, "inside you find a small amulet",
						"you take the amulet and close the coffin");
				addItem(p, 782, 1);
			} else {
				message(p, "it's empty");
			}
		} else if (obj.getID() == 471) {
			message(p, "the doors begin to open");
			
			if (p.getInventory().wielding(782)) {
				doGate(p, obj, 63);
				message(p, "You go through the door");
			} else {
				message(p, "when the corridor floods",
						"flushing you back into the river");
				p.teleport(645, 485, false);
				p.damage(DataConversions.random(4, 10));
				playerTalk(p, null, "ouch!");
				message(p, "you tumble over the water fall");
			}
		} else if (obj.getID() == 492) {
			message(p, "you search the crate");
			if (!p.getInventory().hasItemId(797)) {
				message(p, "and find an old key key");
				addItem(p, 797, 1);
			} else {
				p.message("but find nothing");
			}
		} else if (obj.getID() == 135) {
			p.message("the door is locked");
		} else if (obj.getID() == 485) {
			message(p, "as you touch the chalice it tips over",
					"it falls to the floor", "you hear a gushing of water",
					"water floods into the cavern");
			p.damage(DataConversions.random(1, 10));
			p.teleport(645, 485, false);
			message(p, "ouch!", "you tumble over the water fall",
					"and are washed up by the river side");
		} else if (obj.getID() == 486) {
			p.message("you walk through the doorway");
			p.teleport(667, 3279, false);
		}
	}

	public void handleHadley(final Player p, final Npc n) {
		playerTalk(p, n, "hello there");
		npcTalk(p, n,
				"are you on holiday?, if so you've come to the right place");
		npcTalk(p, n,
				"i'm hadley the tourist guide, anything you need to know just ask me");
		npcTalk(p, n,
				"we have some of the most unspoilt wildlife and scenery in runescape");
		npcTalk(p, n,
				"people come from miles around to fish in the clear lakes");
		npcTalk(p, n, "or to wander the beautiful hill sides");
		playerTalk(p, n, "it is quite pretty");
		npcTalk(p, n, "surely pretty is an understatement kind sir");
		npcTalk(p, n,
				"beautiful, amazing or possibly life changing would be more suitable wording");
		npcTalk(p, n, "have your seen the baxtorian waterfall?");
		npcTalk(p, n, "it's named after the elf king who was buried beneath");
		int opt = showMenu(p, n,
				"can you tell me what happened to the elf king?",
				"where else is worth visiting around here?",
				"is there treasure under the waterfall?",
				"thanks then, goodbye");
		if (opt == 0) {
			playerTalk(p, n, "can you tell me what happened to the elf king?");
			npcTalk(p, n, "there are many myths about baxtorian");
			npcTalk(p, n, "One popular story is this");
			npcTalk(p, n,
					"after defending his kingdom against the invading dark forces from the west");
			npcTalk(p, n,
					"baxtorian returned to find his wife glarial had been captured by the enemy");
			npcTalk(p, n,
					"this destroyed baxtorian, after years of searching he reclused");
			npcTalk(p, n,
					"to the secret home he had made for glarial under the waterfall");
			npcTalk(p, n,
					"he never came out and it is told that only glarial could enter");
			playerTalk(p, n, "what happened to him?");
			npcTalk(p, n, "oh, i don't know");
			npcTalk(p, n,
					"i believe we have some pages on him upstairs in our archives");
			npcTalk(p, n,
					"if you wish to look at them please be careful, they're all pretty delicate");
		} else if (opt == 1) {
			playerTalk(p, n, "where else is worth visiting around here?");
			npcTalk(p, n,
					"there's a lovely spot for a picnic on the hill to the north east");
			npcTalk(p, n,
					"there lies a monument to the deceased elven queen glarial");
			npcTalk(p, n, "it really is quite pretty");
			playerTalk(p, n, "who was queen glarial?");
			npcTalk(p, n,
					"baxtorians wife, the only person who could also enter the waterfall");
			npcTalk(p, n,
					"she was queen when this land was inhabited by elven kind");
			npcTalk(p, n, "glarial was kidnapped while buxtorian was away");
			npcTalk(p, n,
					"but they eventually recovered her body and brought her home to rest");
			playerTalk(p, n, "that's sad");
			npcTalk(p, n,
					"true, i believe there's some information about her upstairs");
			npcTalk(p, n, "if you look at them please be careful");
		} else if (opt == 2) {
			playerTalk(p, n, "is there treasure under the waterfall?");
			npcTalk(p, n, "ha ha, another treasure hunter");
			npcTalk(p, n, "well if there is no one's been able to get to it");
			npcTalk(p, n,
					"they've been searching that river for decades, all to no avail");
		} else if (opt == 3) {
			npcTalk(p, n, "enjoy your visit");
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		return (item.getID() == 789 && obj.getID() == 480)
				|| item.getID() == 797
				&& obj.getID() == 135
				|| (obj.getID() == 462 || obj.getID() == 463
						|| obj.getID() == 462 || obj.getID() == 482)
				&& item.getID() == 237
				|| (obj.getID() == 479 && item.getID() == 787)
				|| ((obj.getID() >= 473 && obj.getID() <= 478) && (item.getID() >= 32 && item
						.getID() <= 34)) || obj.getID() == 483
				&& item.getID() == 782
				|| (obj.getID() == 485 && item.getID() == 805);
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 480 && item.getID() == 789) {
			if (hasItem(p, 789, 1)) {
				doGate(p, obj);
			}
		} else if (obj.getID() == 479 && item.getID() == 787) {
			if(CAN_GO(p)) {
				p.message("you may only enter with peaceful intentions");
				return;
			}
			message(p, "you place the pebble in the gravestones small indent",
					"it fits perfectly", "You hear a loud creek",
					"the stone slab slides back revealing a ladder down",
					"you climb down to an underground passage");
			p.teleport(631, 3305, false);
		} else if (obj.getID() == 462 || obj.getID() == 463
				|| obj.getID() == 462 || obj.getID() == 482
				&& item.getID() == 237) {
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
		} else if (obj.getID() == 135 && item.getID() == 797) {
			doDoor(obj, p);
		} else if ((obj.getID() >= 473 && obj.getID() <= 478)
				&& (item.getID() >= 32 && item.getID() <= 34)) {
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
				p.message("you have already placed " + item.getDef().getName()
						+ " here");
			}
		} else if (obj.getID() == 483 && item.getID() == 782) {
			boolean flag = false;
			for (int i = 473; i < 478; i++) {
				for (int y = 32; i < 34; i++) {
					if (!p.getCache().hasKey("waterfall_" + i + "_" + y)) {
						flag = true;
					}
				}
			}
			if (flag) {
				message(p, "nothing happens.");
			} else {
				message(p, "you place the amulet around the statue",
						"you hear a loud rumble beneath you",
						"the ground raises up before you");
				p.teleport(647, 3267, false);
			}
		} else if (obj.getID() == 485 && item.getID() == 805) {
			message(p, "you carefully poor the ashes in the chalice",
					"as you remove the baxtorian treasure",
					"the chalice remains standing",
					"inside you find a mithril case", "containing 40 seeds",
					"two diamond's and two gold bars");
			removeItem(p, 805, 1);
			p.sendQuestComplete(getQuestId());
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player player) {
		return item.getID() == 788 || item.getID() == 796;
	}

	@Override
	public void onInvAction(Item i, Player p) {
		if (i.getID() == 796) {
			message(p, "you open the small mithril case");
			if(p.getViewArea().getGameObject(p.getLocation()) != null) {
				p.message("you can't plant a tree here");
				return;
			}
			removeItem(p, 796, 1);
			message(p, "and drop a seed by your feet");
			GameObject object = new GameObject(Point.location(p.getX(), p.getY()), 490, 0, 0);
			World.getWorld().registerGameObject(object);
			World.getWorld().delayedRemoveObject(object, 60000);
			p.message("a tree magically sprouts around you");
		}
		if (i.getID() == 788) {
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
			p.message("the door is locked");
		}
	}

	@Override
	public boolean blockInvUseOnWallObject(GameObject obj, Item item,
			Player player) {
		return obj.getID() == 135 && item.getID() == 797;
	}

	@Override
	public void onInvUseOnWallObject(GameObject obj, Item item, Player player) {
		if (obj.getID() == 135 && item.getID() == 797) {
			doDoor(obj, player);
		}
	}
	private boolean CAN_GO(Player p) {
		for (Item item : p.getInventory().getItems()) {
			String name = item.getDef().getName().toLowerCase();
			if (name.contains("dagger") || name.contains("scimitar")
					|| name.contains("bow") || name.contains("mail")
					|| (name.contains("sword")
					&& !name.equalsIgnoreCase("Swordfish") && !name.equalsIgnoreCase("Burnt Swordfish") && !name.equalsIgnoreCase("Raw Swordfish"))
					|| name.contains("mace") || name.contains("helmet")
					|| name.contains("axe")) {
				return true;
			}
		}
		return false;
	}
}
