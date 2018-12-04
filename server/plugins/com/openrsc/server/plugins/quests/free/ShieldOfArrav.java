package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;

import static com.openrsc.server.plugins.Functions.*;

public class ShieldOfArrav implements QuestInterface,InvUseOnWallObjectListener,
		InvUseOnWallObjectExecutiveListener, PlayerKilledNpcListener,
		PlayerKilledNpcExecutiveListener, InvActionListener,
		InvActionExecutiveListener, TalkToNpcListener,
		ObjectActionListener, ObjectActionExecutiveListener,
		TalkToNpcExecutiveListener, InvUseOnObjectListener,
		InvUseOnObjectExecutiveListener, WallObjectActionExecutiveListener,
		WallObjectActionListener {

	class QuestItems {
		public static final int BOOK = 30;// book
		public static final int COINS = 10;// ID: 10 coins
		public static final int KEY = 48; // ID: 48 key (phoenix gang key)
		public static final int SCROLL = 49; // ID: 49 scroll
		public static final int BLACKARM_BROKENSHIELD = 53;// ID: 53 broken
		public static final int PHOENIX_BROKENSHIELD = 54; // ID: 54 broken
		public static final int CERTIFICATE = 61;// certificate
		public static final int FUR = 146; // ID: 146 fur
		public static final int PHOENIX_CROSSBOW = 59;
	}

	class QuestObjects {
		public static final int BOOKCASE = 67; // ID: 67 bookcase - coords:
		// 132,455
		public static final int DOOR = 19; // ID: 19 door- coords 110,3370
		public static final int DOOR2 = 20; // ID: 20 door - coords: 103,532
	}

	class QuestNpcs {
		public static final int RELDO = 20; // 20 Reldo- coords: 131,457
		public static final int BARAEK = 26; // ID: 26 Baraek- coords 127,506
		public static final int MAN = 24; // ID: 24 Man- coords: 111,3367
		public static final int JONNY = 25; // ID: 25 Jonny the beard- coords:
		// 123,523
		public static final int KING = 42; // ID: 42 king- coords: 126,474
		public static final int CURATOR = 39; // ID: 39 Curator- coords: 101,488
		public static final int TRAMP = 28; // ID: 28 Tramp- coords 132, 527	
		public static final int THIEF = 64; // ID: 64 Thief- coords: 110,3375 (4
		// in phoenix gang building)
		public static final int KATRINE = 27; // ID: 27 Katrine- coords: 149,534
	}

	public static final int BLACK_ARM = 0;
	public static final int PHOENIX_GANG = 1;

	public static final int BLACK_ARM_COMPLETE = -2;
	public static final int PHOENIX_COMPLETE = -1;

	@Override
	public int getQuestId() {
		return Constants.Quests.SHIELD_OF_ARRAV;
	}

	@Override
	public String getQuestName() {
		return "Shield of Arrav";
	}

	@Override
	public void handleReward(Player p) {
		p.message("Well done, you have completed the shield of Arrav quest");
		incQuestReward(p, Quests.questData.get(Quests.SHIELD_OF_ARRAV), true);
		p.message("@gre@You haved gained 1 quest point!");
		addItem(p, 10, 600);
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == QuestNpcs.KATRINE;
		
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == 81) {
			return true;
		} else if (obj.getID() == 85) {
			return true;
		} else if (obj.getID() == 67) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		switch (obj.getID()) {
		case 67:
			if (player.getQuestStage(this) == 1) {
				playerTalk(player, null, "Aha the shield of Arrav");
				playerTalk(player, null, "That was what I was looking for");
				message(player, "You take the book from the bookcase");
				addItem(player, 30, 1);
			} else {
				player.message("A large collection of books");
			}
			break;
		case 81:
			if (player.getBank().contains(new Item(54))
					|| player.getInventory().contains(new Item(54))) {
				message(player, "You search the chest", "The chest is empty");
				return;
			} else if (isPhoenixGang(player)) {
			message(player, "You search the chest",
					"You find half a shield which you take");
			addItem(player, 54, 1);
			}
			else {
				message(player, "You search the chest", "The chest is empty");
			}
			break;
		case 85:
			if (player.getBank().contains(new Item(53))
					|| player.getInventory().contains(new Item(53))) {
				message(player, "You search the cupboard", "The cupboard is empty");
				return;
			} else if (isBlackArmGang(player)) {
			message(player, "You search the cupboard",
					"You find half a shield which you take");
			addItem(player, 53, 1);
			}
			else {
				message(player, "You search the cupboard", "The cupboard is empty");
			}
			break;			
		}
	}
	@Override
	public void onTalkToNpc(Player p, Npc n) {
		switch (n.getID()) {
		case QuestNpcs.KATRINE:
			katrineDialogue(p, n, -1);
			break;				
		}
	}
	public void katrineDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
			case 0:
				playerTalk(p, n, "What is this place?");
				npcTalk(p, n, "It's a private business", "Can I help you at all?");
				int choice = showMenu(p, n,
						"What sort of business?",
						"I'm looking for fame and riches");
				if (choice == 0) {
					npcTalk(p, n,
							"A small family business", "We give financial advice to other companies");
				} else if (choice == 1) {
					npcTalk(p, n,
							"And you expect to find it up the backstreets of Varrock?");
				}
				break;
			case 1:
			case 2:
			case 3:
				playerTalk(p, n, "What is this place?");
				npcTalk(p, n, "It's a private business", "Can I help you at all?");
				if(p.getCache().hasKey("spoken_tramp")) {
					choice = showMenu(p, n,
							"I've heard you're the blackarm gang",
							"What sort of business?",
							"I'm looking for fame and riches");
				}
				else {
					choice = showMenu(p, n,
							"What sort of business?",
							"I'm looking for fame and riches");
					if(choice >= 0) {
						choice += 1;
					}
				}
				if (choice == 0) {
					katrineDialogue(p, n, Katrine.BLACKARM);
				} else if (choice == 1) {
					npcTalk(p, n,
							"A small family business", "We give financial advice to other companies");
				} else if (choice == 2) {
					npcTalk(p, n,
							"And you expect to find it up the backstreets of Varrock?");
				}
				break;
			case 4:
				if (isBlackArmGang(p)) {
					if (hasItem(p, QuestItems.PHOENIX_CROSSBOW, 2)) {
						npcTalk(p, n, "Have you got those crossbows for me yet?");
						playerTalk(p, n, "Yes I have");
						p.message("You give the crossbows to katrine");
						p.getInventory().remove(59, 2);
						npcTalk(p, n,
								"Ok you can join our gang now",
								"Feel free to enter any the rooms of the ganghouse");
						p.updateQuestStage(this, 5);
					} else if (hasItem(p, QuestItems.PHOENIX_CROSSBOW, 1))  {
						npcTalk(p, n, "Have you got those crossbows for me yet?");
						playerTalk(p, n, "I have one");
						npcTalk(p, n, "I need two",
								"Come back when you have them");
					} else {
						npcTalk(p, n, "Have you got those crossbows for me yet?");
						playerTalk(p, n, "No I haven't found them yet");
						npcTalk(p,
								n,
								"I need two crossbows",
								"Stolen from the phoenix gang weapons stash",
								"which if you head east for a bit",
								"Is a building on the south side of the road");
					}
				} else {
					npcTalk(p, n, "You've got some guts coming here",
							"Phoenix guy");
					p.message("Katrine Spits");
					npcTalk(p, n, "Now go away",
							"Or I'll make sure you 'aven't got those guts anymore");
				}
				break;
			case 5:
			case -1:
			case -2:
				if (isBlackArmGang(p)) {
					if (p.getQuestStage(Constants.Quests.HEROS_QUEST) > 0) {
						if (!hasItem(p, 586) && p.getCache().hasKey("armband")) {
							playerTalk(p, n, "I have lost my master thief armband");
							npcTalk(p, n, "Well I have a spare", "Don't lose it again");
							addItem(p, 586, 1);
							return;
						}
						playerTalk(p, n, "Hey");
						npcTalk(p, n, "Hey");
						if (hasItem(p, 585) && !p.getCache().hasKey("armband")) {
							int choice3 = showMenu(p, n,
									"Who are all those people in there?",
									"I have a candlestick now");
							if (choice3 == 0) {
								npcTalk(p, n,
										"They're just various rogues and thieves");
								playerTalk(p, n, "They don't say a lot");
								npcTalk(p, n, "Nope");
							} else if (choice3 == 1) {
								npcTalk(p, n, "Wow is it really it?");
								p.message("Katrine takes hold of the candlestick and examines it");
								removeItem(p, 585, 1);
								npcTalk(p, n,
										"This really is a fine bit of thievery",
										"Thieves have been trying to get hold of this 1 for a while",
										"You wanted to be ranked as master thief didn't you?",
										"Well I guess this just about ranks as good enough");
								p.message("Katrine gives you a master thief armband");
								addItem(p, 586, 1);
								p.getCache().store("armband", true);
							}
							return;
						}
						int choice2 = showMenu(p, n, false, //do not send over
								"Who are all those people in there?",
								"Is there anyway I can get the rank of master thief?");
						if (choice2 == 0) {
							playerTalk(p, n, "Who are all those people in there?");
							npcTalk(p, n, "They're just various rogues and thieves");
							playerTalk(p, n, "They don't say a lot");
							npcTalk(p, n, "Nope");
						} else if (choice2 == 1) {
							playerTalk(p, n, "Is there any way I can get the rank of master thief?");
							npcTalk(p, n,
									"Master thief? We are the ambitious one aren't we?",
									"Well you're going to have do something pretty amazing");
							playerTalk(p, n, "Anything you can suggest?");
							npcTalk(p, n,
									"Well some of the most coveted prizes in thiefdom right now",
									"Are in the  pirate town of Brimhaven on Karamja",
									"The pirate leader Scarface Pete",
									"Has a pair of extremely rare valuable candlesticks",
									"His security is very good",
									"We of course have gang members in a town like Brimhaven",
									"They may be able to help you",
									"visit our hideout in the alleyway on palm street",
									"To get in you will need to tell them the word four leafed clover");
							if (!p.getCache().hasKey("blackarm_mission")) {
								p.getCache().store("blackarm_mission", true);
							}
						}
					} else if (p.getQuestStage(Constants.Quests.HEROS_QUEST) == -1) {
						playerTalk(p, n, "Hey");
						npcTalk(p, n, "Hey");
						int choice1 = showMenu(p, n,
										"Who are all those people in there?",
										"Teach me to be a top class criminal");
						if (choice1 == 0) {
							npcTalk(p, n,
											"They're just various rogues and thieves");
							playerTalk(p, n, "They don't say a lot");
							npcTalk(p, n, "Nope");
						} else if (choice1 == 1) {
							npcTalk(p, n, "Teach yourself");
						}
					}
				} else {
					npcTalk(p, n, "You've got some guts coming here",
							"Phoenix guy");
					p.message("Katrine Spits");
					npcTalk(p, n, "Now go away",
							"Or I'll make sure you 'aven't got those guts anymore");
				}
				break;
			}
			return;
		}
		switch (cID) {
		case Katrine.BLACKARM:
			npcTalk(p, n, "Who told you that?");
			int choice = showMenu(p, n, false, //do not send over
					"I'd rather not reveal my sources",
					"It was the tramp outside",
					"Everyone knows - its no great secret");
			if (choice == 0) {
				playerTalk(p, n, "I'd rather not reveal my sources");
				npcTalk(p, n,
						"Yes, I can understand that", "So what do you want with us?");
			} else if (choice == 1) {
				playerTalk(p, n, "It was the tramp outside");
				npcTalk(p,
						n,
						"Is that guy still out there?",
						"He's getting to be a nuisance",
						"Remind me to send someone to kill him",
						"So now you've found us", "What do you want?");
			} else if (choice == 2) {
				playerTalk(p, n, "Everyone knows", "It's no great secret");
				npcTalk(p, n, "I thought we were safe back here");
				playerTalk(p, n, "Oh no, not at all", "It's so obvious",
						"Even the town guard have caught on");
				npcTalk(p,
						n,
						"Wow we must be obvious",
						"I guess they'll be expecting bribes again soon in that case",
						"Thanks for the information",
						"Is there anything else you want to tell me?");
			}
			int choice1 = showMenu(p, n, false, //do not send over
					"I want to become a member of your gang",
					"I want some hints for becoming a thief",
					"I'm looking for the door out of here");
			if (choice1 == 0) {
				playerTalk(p, n, "I want to become a member of your gang");
				katrineDialogue(p, n, Katrine.MEMBER);
			} else if (choice1 == 1) {
				playerTalk(p, n, "I want some hints for becomming a thief");
				npcTalk(p, n,
						"Well I'm sorry luv", "I'm not giving away any of my secrets",
						"Not to none black arm members anyway");
			} else if (choice1 == 2) {
				playerTalk(p, n, "I'm looking for the door out of here");
				p.message("Katrine groans");
				npcTalk(p, n, "Try the one you just came in");
			}
			break;
		case Katrine.MEMBER:
			npcTalk(p, n,
					"How unusual",
					"Normally we recruit for our gang", 
					"By watching local thugs and thieves in action",
					"People don't normally waltz in here",
					"Saying 'hello can I play'",
					"How can I be sure you can be trusted?");
			int choice11 = showMenu(p, n,
					"Well you can give me a try, can't you?",
					"Well people tell me I have an honest face");
			if (choice11 == 0) {
				npcTalk(p, n, "I'm not so sure.");
			} else if (choice11 == 1) {
				npcTalk(p, n, "How unusual someone honest wanting to join a gang of thieves",
						"Excuse me if i remain unconvinced");
			}
			katrineDialogue(p, n, Katrine.GIVETRY);
			break;
		case Katrine.GIVETRY:
			npcTalk(p, n,
					"I think I may have a solution actually",
					"Our rival gang - the phoenix gang",
					"Has a weapons stash a little east of here",
					"We're fresh out of crossbows",
					"So if you could steal a couple of crossbows for us",
					"It would be very much appreciated",
					"Then I'll be happy to call you a black arm");
			int choice3 = showMenu(p, n, false, //do not send over 
					"Ok no problem",
					"Sounds a little tricky got anything easier?");
			if (choice3 == 0) {
				playerTalk(p, n, "Ok no problem");
				p.getCache().set("arrav_gang", BLACK_ARM);
				p.updateQuestStage(this, 4);
				p.getCache().remove("spoken_tramp");
			} else if (choice3 == 1) {
				playerTalk(p, n, "Sounds a little tricky", "Got anything easier?");
				npcTalk(p, n, "If you're not up to a little bit of danger",
						"I don't think you've got anything to offer our gang");
			}
			break;
		}
	}

	class Katrine {
		public static final int GIVETRY = 4;
		public static final int MEMBER = 3;
		public static final int BLACKARM = 0;
	}

	@Override
	public boolean blockInvAction(Item item, Player player) {
		return item.getID() == QuestItems.BOOK;
	}

	@Override
	public void onInvAction(Item item, Player player) {
		switch (item.getID()) {
		case QuestItems.BOOK:
			message(player,
					"The shield of Arrav",
					"By A.R.Wright",
					"Arrav is probably the best known hero of the 4th age.",
					"One surviving artifact from the 4th age is a fabulous shield.",
					"This shield is believed to have once belonged to Arrav",
					"And is now indeed known as the shield of Arrav.",
					"For 150 years it was the prize piece in the royal museum of Varrock.",
					"However in the year 143 of the 5th age",
					"A gang of thieves called the phoenix gang broke into the museum",
					"And stole the shield.",
					"King Roald the VII put a 1200 gold reward on the return on the shield.",
					"The thieves who stole the shield",
					"Have now become the most powerful crime gang in Varrock.",
					"The reward for the return of the shield still stands.");
			if(!player.getCache().hasKey("read_arrav")) {
				player.getCache().store("read_arrav", true);
			}
			break;
		}
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (p.getQuestStage(this) == 4) {
			World.getWorld().registerItem(
					new GroundItem(QuestItems.SCROLL, n.getX(), n.getY(), 1, p));
			n.killedBy(p);
		} else {
			n.killedBy(p);
		}
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 21 && obj.getY() == 533) {
			if (isBlackArmGang(p) && !(p.getQuestStage(this) >= 0 && p.getQuestStage(this) < 5)) {
				p.message("You hear the door being unbarred");
				p.message("You go through the door");
				if (p.getY() >= 533) {
					doDoor(obj, p);
					p.teleport(148, 532, false);
				} else {
					doDoor(obj, p);
					p.teleport(148, 533, false);
				}
			} else {
				p.message("The door won't open");
			}
		} else if (obj.getID() == 19 && obj.getY() == 3370) {
			Npc man = getNearestNpc(p, 24, 20);
			if (isPhoenixGang(p)) {
				if (p.getQuestStage(this) >= 0 && p.getQuestStage(this) < 5) {
					if (man != null) {
						man.initializeTalkScript(p);
					}
				} else {
					p.message("The door is opened for you");
					p.message("You go through the door");
					if (p.getY() <= 3369) {
						doDoor(obj, p);
						p.teleport(p.getX(), p.getY() + 1, false);
					} else {
						doDoor(obj, p);
						p.teleport(p.getX(), p.getY() - 1, false);
					}
				}
			} else if (isBlackArmGang(p)) {
				if (man != null) {
					npcTalk(p, man, "hey get away from there",
							"Black arm dog");
					man.setChasing(p);
				}
			} else {
				if(man != null) {
					man.initializeTalkScript(p);
				}
			}
		} else if (obj.getID() == 20 && obj.getY() == 532) {
			if (p.getY() <= 531 || p.getY() >= 531) {
				if (p.getInventory().hasItemId(48)) {
					p.message("The door is locked");
					p.message("You need to use your key to open it");
					return;
				}
				p.message("The door is locked");
			}
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
			Player player) {
		//door on phoenix gang entrance
		if (obj.getID() == 19 && obj.getY() == 3370) {
			return true;
		}
		//door on black arm gang entrance
		if (obj.getID() == 21 && obj.getY() == 533) {
			return true;
		}
		if (obj.getID() == 20 && obj.getY() == 532) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockInvUseOnWallObject(GameObject obj, Item item,
			Player player) {
		if (item.getID() == QuestItems.KEY && obj.getID() == 20
				&& obj.getY() == 532) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnWallObject(GameObject obj, Item item, Player player) {
		if (item.getID() == QuestItems.KEY && obj.getID() == 20
				&& obj.getY() == 532) {
			showBubble(player, item);
			if (player.getY() <= 531) {
				doDoor(obj, player);
				player.teleport(player.getX(), player.getY() + 1, false);
			} else {
				doDoor(obj, player);
				player.teleport(player.getX(), player.getY() - 1, false);
			}
		}

	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == QuestNpcs.JONNY)
			return true;

		return false;
	}

	public static boolean isBlackArmGang(Player p) {
		return (p.getCache().hasKey("arrav_gang") && p.getCache().getInt("arrav_gang") == BLACK_ARM)
					|| p.getQuestStage(Constants.Quests.SHIELD_OF_ARRAV) == BLACK_ARM_COMPLETE;
	}

	public static boolean isPhoenixGang(Player p) {
		return (p.getCache().hasKey("arrav_gang") && p.getCache().getInt("arrav_gang") == PHOENIX_GANG)
						|| p.getQuestStage(Constants.Quests.SHIELD_OF_ARRAV) == PHOENIX_COMPLETE;
	}
}
