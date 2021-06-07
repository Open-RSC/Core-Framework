package com.openrsc.server.plugins.authentic.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.authentic.npcs.varrock.ManPhoenix;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class ShieldOfArrav implements QuestInterface, UseBoundTrigger,
	KillNpcTrigger,
	OpInvTrigger,
	TalkNpcTrigger,
	OpLocTrigger,
	OpBoundTrigger {

	public static final int BLACK_ARM = 0;
	public static final int PHOENIX_GANG = 1;
	public static final int BLACK_ARM_COMPLETE = -2;
	public static final int PHOENIX_COMPLETE = -1;

	public static final int BLACKARM_MISSION = 1;
	public static final int PHOENIX_MISSION = 2;
	public static final int ANY_MISSION = 3;

	//84 & 85 black arm
	private static final int PHOENIX_CHEST_OPEN = 81;
	private static final int PHOENIX_CHEST_CLOSED = 82;
	private static final int BARM_CUPBOARD_OPEN = 85;
	private static final int BARM_CUPBOARD_CLOSED = 84;

	@Override
	public int getQuestId() {
		return Quests.SHIELD_OF_ARRAV;
	}

	@Override
	public String getQuestName() {
		return "Shield of Arrav";
	}

	@Override
	public int getQuestPoints() {
		return Quest.SHIELD_OF_ARRAV.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done, you have completed the shield of Arrav quest");
		final QuestReward reward = Quest.SHIELD_OF_ARRAV.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		give(player, ItemId.COINS.id(), 600);
	}

	public static boolean isBlackArmGang(Player player) {
		return (player.getCache().hasKey("arrav_gang") && player.getCache().getInt("arrav_gang") == BLACK_ARM)
			|| player.getQuestStage(Quests.SHIELD_OF_ARRAV) == BLACK_ARM_COMPLETE;
	}

	public static boolean isPhoenixGang(Player player) {
		return (player.getCache().hasKey("arrav_gang") && player.getCache().getInt("arrav_gang") == PHOENIX_GANG)
			|| player.getQuestStage(Quests.SHIELD_OF_ARRAV) == PHOENIX_COMPLETE;
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.KATRINE.id();
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == PHOENIX_CHEST_OPEN || obj.getID() == PHOENIX_CHEST_CLOSED) {
			return true;
		} else if (obj.getID() == BARM_CUPBOARD_OPEN || obj.getID() == BARM_CUPBOARD_CLOSED) {
			return true;
		} else if (obj.getID() == 67) {
			return true;
		}
		return false;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		switch (obj.getID()) {
			case 67:
				if (player.getQuestStage(this) == 1) {
					say(player, null, "Aha the shield of Arrav");
					say(player, null, "That was what I was looking for");
					mes("You take the book from the bookcase");
					delay(3);
					give(player, ItemId.BOOK.id(), 1);
					if (!player.getCache().hasKey("read_arrav")) {
						player.getCache().store("read_arrav", true);
					}
				} else {
					player.message("A large collection of books");
				}
				break;
			case PHOENIX_CHEST_OPEN:
			case PHOENIX_CHEST_CLOSED:
				if (command.equalsIgnoreCase("open")) {
					openGenericObject(obj, player, PHOENIX_CHEST_OPEN, "You open the chest");
				} else if (command.equalsIgnoreCase("close")) {
					closeGenericObject(obj, player, PHOENIX_CHEST_CLOSED, "You close the chest");
				} else {
					if (player.getBank().contains(new Item(ItemId.BROKEN_SHIELD_ARRAV_1.id()))
							|| player.getCarriedItems().getInventory().contains(new Item(ItemId.BROKEN_SHIELD_ARRAV_1.id()))) {
							mes("You search the chest");
							delay(3);
							mes("The chest is empty");
							delay(3);
							return;
					} else if (isPhoenixGang(player)) {
						mes("You search the chest");
						delay(3);
						mes("You find half a shield which you take");
						delay(3);
						give(player, ItemId.BROKEN_SHIELD_ARRAV_1.id(), 1);
					} else {
						mes("You search the chest");
						delay(3);
						mes("The chest is empty");
						delay(3);
					}
				}
				break;
			case BARM_CUPBOARD_OPEN:
			case BARM_CUPBOARD_CLOSED:
				if (command.equalsIgnoreCase("open")) {
					openCupboard(obj, player, BARM_CUPBOARD_OPEN);
				} else if (command.equalsIgnoreCase("close")) {
					closeCupboard(obj, player, BARM_CUPBOARD_CLOSED);
				} else {
					if (player.getBank().contains(new Item(ItemId.BROKEN_SHIELD_ARRAV_2.id()))
						|| player.getCarriedItems().getInventory().contains(new Item(ItemId.BROKEN_SHIELD_ARRAV_2.id()))) {
						mes("You search the cupboard");
						delay(3);
						mes("The cupboard is empty");
						delay(3);
						return;
					} else if (isBlackArmGang(player)) {
						mes("You search the cupboard");
						delay(3);
						mes("You find half a shield which you take");
						delay(3);
						give(player, ItemId.BROKEN_SHIELD_ARRAV_2.id(), 1);
					} else {
						mes("You search the cupboard");
						delay(3);
						mes("The cupboard is empty");
						delay(3);
					}
				}
				break;
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		switch (NpcId.getById(n.getID())) {
			case KATRINE:
				katrineDialogue(player, n, -1);
				break;
			default:
				break;
		}
	}

	public void katrineDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			int choice;
			int stage = player.getQuestStage(this);
			if ((stage == 4 && isBlackArmGang(player)) ||
					(player.getCache().hasKey("arrav_mission") && (player.getCache().getInt("arrav_mission") & 1) == BLACKARM_MISSION)) {
				if (ifheld(player, ItemId.PHOENIX_CROSSBOW.id(), 2)) {
					npcsay(player, n, "Have you got those crossbows for me yet?");
					say(player, n, "Yes I have");
					player.message("You give the crossbows to katrine");
					for (int i = 0; i < 2; i++) {
						player.getCarriedItems().remove(new Item(ItemId.PHOENIX_CROSSBOW.id()));
					}
					npcsay(player, n,
						"Ok you can join our gang now",
						"Feel free to enter any the rooms of the ganghouse");
					player.updateQuestStage(this, 5);
					if (!player.getCache().hasKey("arrav_gang")) {
						// player got traded the crossbows or had them before starting mission
						player.getCache().set("arrav_gang", BLACK_ARM);
					}
					if (player.getCache().hasKey("arrav_mission")) {
						player.getCache().remove("arrav_mission");
					}
					if (player.getCache().hasKey("spoken_tramp")) {
						player.getCache().remove("spoken_tramp");
					}
				} else if (player.getCarriedItems().hasCatalogID(ItemId.PHOENIX_CROSSBOW.id(), Optional.of(false))) {
					npcsay(player, n, "Have you got those crossbows for me yet?");
					say(player, n, "I have one");
					npcsay(player, n, "I need two",
						"Come back when you have them");
				} else {
					npcsay(player, n, "Have you got those crossbows for me yet?");
					say(player, n, "No I haven't found them yet");
					npcsay(player,
						n,
						"I need two crossbows",
						"Stolen from the phoenix gang weapons stash",
						"which if you head east for a bit",
						"Is a building on the south side of the road");
				}
			} else if ((player.getQuestStage(Quests.SHIELD_OF_ARRAV) >= 5 || player.getQuestStage(Quests.SHIELD_OF_ARRAV) < 0) && isBlackArmGang(player)) {
				if (player.getQuestStage(Quests.HEROS_QUEST) > 0) {
					if (!player.getCarriedItems().hasCatalogID(ItemId.MASTER_THIEF_ARMBAND.id(), Optional.empty()) && player.getCache().hasKey("armband")) {
						say(player, n, "I have lost my master thief armband");
						npcsay(player, n, "Well I have a spare", "Don't lose it again");
						give(player, ItemId.MASTER_THIEF_ARMBAND.id(), 1);
						return;
					}
					say(player, n, "Hey");
					npcsay(player, n, "Hey");
					if (player.getCarriedItems().hasCatalogID(ItemId.CANDLESTICK.id(), Optional.of(false)) && !player.getCache().hasKey("armband")) {
						int choice3 = multi(player, n,
							"Who are all those people in there?",
							"I have a candlestick now");
						if (choice3 == 0) {
							npcsay(player, n,
								"They're just various rogues and thieves");
							say(player, n, "They don't say a lot");
							npcsay(player, n, "Nope");
						} else if (choice3 == 1) {
							npcsay(player, n, "Wow is it really it?");
							player.message("Katrine takes hold of the candlestick and examines it");
							player.getCarriedItems().remove(new Item(ItemId.CANDLESTICK.id()));
							npcsay(player, n,
								"This really is a fine bit of thievery",
								"Thieves have been trying to get hold of this 1 for a while",
								"You wanted to be ranked as master thief didn't you?",
								"Well I guess this just about ranks as good enough");
							player.message("Katrine gives you a master thief armband");
							give(player, ItemId.MASTER_THIEF_ARMBAND.id(), 1);
							player.getCache().store("armband", true);
						}
						return;
					}
					int choice2 = multi(player, n, false, //do not send over
						"Who are all those people in there?",
						"Is there anyway I can get the rank of master thief?");
					if (choice2 == 0) {
						say(player, n, "Who are all those people in there?");
						npcsay(player, n, "They're just various rogues and thieves");
						say(player, n, "They don't say a lot");
						npcsay(player, n, "Nope");
					} else if (choice2 == 1) {
						say(player, n, "Is there any way I can get the rank of master thief?");
						npcsay(player, n,
							"Master thief? We are the ambitious one aren't we?",
							"Well you're going to have do something pretty amazing");
						say(player, n, "Anything you can suggest?");
						npcsay(player, n,
							"Well some of the most coveted prizes in thiefdom right now",
							"Are in the  pirate town of Brimhaven on Karamja",
							"The pirate leader Scarface Pete",
							"Has a pair of extremely rare valuable candlesticks",
							"His security is very good",
							"We of course have gang members in a town like Brimhaven",
							"They may be able to help you",
							"visit our hideout in the alleyway on palm street",
							"To get in you will need to tell them the word four leafed clover");
						if (!player.getCache().hasKey("blackarm_mission")) {
							player.getCache().store("blackarm_mission", true);
						}
					}
				} else {
					say(player, n, "Hey");
					npcsay(player, n, "Hey");
					int choice1 = multi(player, n,
						"Who are all those people in there?",
						"Teach me to be a top class criminal");
					if (choice1 == 0) {
						npcsay(player, n,
							"They're just various rogues and thieves");
						say(player, n, "They don't say a lot");
						npcsay(player, n, "Nope");
					} else if (choice1 == 1) {
						npcsay(player, n, "Teach yourself");
					}
				}
			} else if (stage == 0) {
				say(player, n, "What is this place?");
				npcsay(player, n, "It's a private business", "Can I help you at all?");
				choice = multi(player, n,
					"What sort of business?",
					"I'm looking for fame and riches");
				if (choice == 0) {
					npcsay(player, n,
						"A small family business", "We give financial advice to other companies");
				} else if (choice == 1) {
					npcsay(player, n,
						"And you expect to find it up the backstreets of Varrock?");
				}
			} else if (stage >= 1 && stage <= 3) {
				say(player, n, "What is this place?");
				npcsay(player, n, "It's a private business", "Can I help you at all?");
				if (player.getCache().hasKey("spoken_tramp")) {
					choice = multi(player, n,
						"I've heard you're the blackarm gang",
						"What sort of business?",
						"I'm looking for fame and riches");
				} else {
					choice = multi(player, n,
						"What sort of business?",
						"I'm looking for fame and riches");
					if (choice >= 0) {
						choice += 1;
					}
				}
				if (choice == 0) {
					katrineDialogue(player, n, Katrine.BLACKARM);
				} else if (choice == 1) {
					npcsay(player, n,
						"A small family business", "We give financial advice to other companies");
				} else if (choice == 2) {
					npcsay(player, n,
						"And you expect to find it up the backstreets of Varrock?");
				}
			} else {
				npcsay(player, n, "You've got some guts coming here",
						"Phoenix guy");
				player.message("Katrine Spits");
				npcsay(player, n, "Now go away",
						"Or I'll make sure you 'aven't got those guts anymore");
			}
			return;
		}
		switch (cID) {
			case Katrine.BLACKARM:
				npcsay(player, n, "Who told you that?");
				int choice = multi(player, n, false, //do not send over
					"I'd rather not reveal my sources",
					"It was the tramp outside",
					"Everyone knows - its no great secret");
				if (choice == 0) {
					say(player, n, "I'd rather not reveal my sources");
					npcsay(player, n,
						"Yes, I can understand that", "So what do you want with us?");
				} else if (choice == 1) {
					say(player, n, "It was the tramp outside");
					npcsay(player,
						n,
						"Is that guy still out there?",
						"He's getting to be a nuisance",
						"Remind me to send someone to kill him",
						"So now you've found us", "What do you want?");
				} else if (choice == 2) {
					say(player, n, "Everyone knows", "It's no great secret");
					npcsay(player, n, "I thought we were safe back here");
					say(player, n, "Oh no, not at all", "It's so obvious",
						"Even the town guard have caught on");
					npcsay(player,
						n,
						"Wow we must be obvious",
						"I guess they'll be expecting bribes again soon in that case",
						"Thanks for the information",
						"Is there anything else you want to tell me?");
				}
				int choice1 = multi(player, n, false, //do not send over
					"I want to become a member of your gang",
					"I want some hints for becoming a thief",
					"I'm looking for the door out of here");
				if (choice1 == 0) {
					say(player, n, "I want to become a member of your gang");
					katrineDialogue(player, n, Katrine.MEMBER);
				} else if (choice1 == 1) {
					say(player, n, "I want some hints for becomming a thief");
					npcsay(player, n,
						"Well I'm sorry luv", "I'm not giving away any of my secrets",
						"Not to none black arm members anyway");
				} else if (choice1 == 2) {
					say(player, n, "I'm looking for the door out of here");
					player.message("Katrine groans");
					npcsay(player, n, "Try the one you just came in");
				}
				break;
			case Katrine.MEMBER:
				npcsay(player, n,
					"How unusual",
					"Normally we recruit for our gang",
					"By watching local thugs and thieves in action",
					"People don't normally waltz in here",
					"Saying 'hello can I play' ",
					"How can I be sure you can be trusted?");
				int choice11 = multi(player, n,
					"Well you can give me a try, can't you?",
					"Well people tell me I have an honest face");
				if (choice11 == 0) {
					npcsay(player, n, "I'm not so sure.");
				} else if (choice11 == 1) {
					npcsay(player, n, "How unusual someone honest wanting to join a gang of thieves",
						"Excuse me if i remain unconvinced");
				}
				katrineDialogue(player, n, Katrine.GIVETRY);
				break;
			case Katrine.GIVETRY:
				npcsay(player, n,
					"I think I may have a solution actually",
					"Our rival gang - the phoenix gang",
					"Has a weapons stash a little east of here",
					"We're fresh out of crossbows",
					"So if you could steal a couple of crossbows for us",
					"It would be very much appreciated",
					"Then I'll be happy to call you a black arm");
				int choice3 = multi(player, n, false, //do not send over
					"Ok no problem",
					"Sounds a little tricky got anything easier?");
				if (choice3 == 0) {
					say(player, n, "Ok no problem");
					if (player.getCache().hasKey("arrav_mission") && ((player.getCache().getInt("arrav_mission") & 1) != BLACKARM_MISSION)) {
						player.getCache().set("arrav_mission", ANY_MISSION);
					} else if (!player.getCache().hasKey("arrav_mission")) {
						player.getCache().set("arrav_mission", BLACKARM_MISSION);
					}
				} else if (choice3 == 1) {
					say(player, n, "Sounds a little tricky", "Got anything easier?");
					npcsay(player, n, "If you're not up to a little bit of danger",
						"I don't think you've got anything to offer our gang");
				}
				break;
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.BOOK.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		switch (ItemId.getById(item.getCatalogId())) {
			case BOOK:
				mes("The shield of Arrav");
				delay(3);
				mes("By A.R.Wright");
				delay(3);
				mes("Arrav is probably the best known hero of the 4th age.");
				delay(3);
				mes("One surviving artifact from the 4th age is a fabulous shield.");
				delay(3);
				mes("This shield is believed to have once belonged to Arrav");
				delay(3);
				mes("And is now indeed known as the shield of Arrav.");
				delay(3);
				mes("For 150 years it was the prize piece in the royal museum of Varrock.");
				delay(3);
				mes("However in the year 143 of the 5th age");
				delay(3);
				mes("A gang of thieves called the phoenix gang broke into the museum");
				delay(3);
				mes("And stole the shield.");
				delay(3);
				mes("King Roald the VII put a 1200 gold reward on the return on the shield.");
				delay(3);
				mes("The thieves who stole the shield");
				delay(3);
				mes("Have now become the most powerful crime gang in Varrock.");
				delay(3);
				mes("The reward for the return of the shield still stands.");
				delay(3);
				break;
			default:
				break;
		}
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.JONNY_THE_BEARD.id()) {
			if (player.getCache().hasKey("arrav_mission") && (player.getCache().getInt("arrav_mission") & 2) == PHOENIX_MISSION) {
				player.getCache().set("arrav_gang", PHOENIX_GANG);
				player.updateQuestStage(Quests.SHIELD_OF_ARRAV, 4);
				player.getCache().remove("arrav_mission");
				player.getCache().remove("spoken_tramp");
			}
		}
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 21 && obj.getY() == 533) {
			if (isBlackArmGang(player) && !(player.getQuestStage(this) >= 0 && player.getQuestStage(this) < 5)) {
				player.message("You hear the door being unbarred");
				player.message("You go through the door");
				if (player.getY() >= 533) {
					doDoor(obj, player);
					player.teleport(148, 532, false);
				} else {
					doDoor(obj, player);
					player.teleport(148, 533, false);
				}
			} else {
				player.message("The door won't open");
			}
		} else if (obj.getID() == 19 && obj.getY() == 3370) {
			Npc man = ifnearvisnpc(player, NpcId.STRAVEN.id(), 20);
			if (isPhoenixGang(player)) {
				if (player.getQuestStage(this) >= 0 && player.getQuestStage(this) < 5) {
					if (man != null) {
						ManPhoenix.indirectTalkToStraven(player, man);
					}
				} else {
					if (!player.getConfig().OLD_QUEST_MECHANICS) {
						player.message("The door is opened for you");
						player.message("You go through the door");
						if (player.getY() <= 3369) {
							doDoor(obj, player);
							player.teleport(player.getX(), player.getY() + 1, false);
						} else {
							doDoor(obj, player);
							player.teleport(player.getX(), player.getY() - 1, false);
						}
					} else {
						if (player.getY() <= 3369) {
							player.message("The door is locked");
							if (player.getCarriedItems().hasCatalogID(ItemId.PHOENIX_GANG_KEY.id())) {
								player.message("You need to use your key to open it");
								return;
							}
						} else {
							doDoor(obj, player);
							player.teleport(player.getX(), player.getY() - 1, false);
						}
					}
				}
			} else if (isBlackArmGang(player)) {
				if (man != null) {
					npcsay(player, man, "hey get away from there",
						"Black arm dog");
					man.setChasing(player);
				}
			} else {
				if (man != null) {
					ManPhoenix.indirectTalkToStraven(player, man);
				}
			}
		} else if (obj.getID() == 20 && obj.getY() == 532) {
			player.message("The door is locked");
			if (player.getCarriedItems().hasCatalogID(ItemId.PHOENIX_GANG_WEAPON_KEY.id())) {
				player.message("You need to use your key to open it");
				return;
			}
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
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
	public boolean blockUseBound(Player player, GameObject obj, Item item) {
		return (item.getCatalogId() == ItemId.PHOENIX_GANG_WEAPON_KEY.id() && obj.getID() == 20
				&& obj.getY() == 532)
			|| (item.getCatalogId() == ItemId.PHOENIX_GANG_KEY.id() && obj.getID() == 19
			&& obj.getY() == 3370);
	}

	@Override
	public void onUseBound(Player player, GameObject obj, Item item) {
		if (item.getCatalogId() == ItemId.PHOENIX_GANG_WEAPON_KEY.id() && obj.getID() == 20
			&& obj.getY() == 532) {
			thinkbubble(item);
			mes("You unlock the door");
			delay(3);
			doDoor(obj, player);
			mes("You go through the door");
			delay(3);
		} else if (item.getCatalogId() == ItemId.PHOENIX_GANG_KEY.id() && obj.getID() == 19
			&& obj.getY() == 3370) {
			// Retro RSC mechanic - had to use key on door to get in
			thinkbubble(item);
			mes("You unlock the door");
			delay(3);
			doDoor(obj, player);
			mes("You go through the door");
			delay(3);
		}

	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.JONNY_THE_BEARD.id();
	}

	class Katrine {
		public static final int GIVETRY = 4;
		public static final int MEMBER = 3;
		public static final int BLACKARM = 0;
	}
}
