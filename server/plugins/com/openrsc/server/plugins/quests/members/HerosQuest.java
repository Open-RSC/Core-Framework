package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnWallObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerRangeNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnWallObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerRangeNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.closeCupboard;
import static com.openrsc.server.plugins.Functions.closeGenericObject;
import static com.openrsc.server.plugins.Functions.doDoor;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.incQuestReward;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.openCupboard;
import static com.openrsc.server.plugins.Functions.openGenericObject;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showBubble;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isBlackArmGang;

public class HerosQuest implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener, PickupExecutiveListener, WallObjectActionListener, WallObjectActionExecutiveListener, InvUseOnWallObjectListener, InvUseOnWallObjectExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener, PlayerAttackNpcExecutiveListener, PlayerAttackNpcListener, PlayerRangeNpcListener, PlayerMageNpcListener, PlayerRangeNpcExecutiveListener, PlayerMageNpcExecutiveListener,
	PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener{

	private static final int GRIPS_CUPBOARD_OPEN = 264;
	private static final int GRIPS_CUPBOARD_CLOSED = 263;
	private static final int CANDLESTICK_CHEST_OPEN = 265;
	private static final int CANDLESTICK_CHEST_CLOSED = 266;
	
	@Override
	public int getQuestId() {
		return Constants.Quests.HEROS_QUEST;
	}

	@Override
	public String getQuestName() {
		return "Hero's quest (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the hero guild entry quest");
		player.getCache().remove("talked_grip");
		player.getCache().remove("hq_impersonate");
		player.getCache().remove("talked_alf");
		player.getCache().remove("talked_grubor");
		player.getCache().remove("blackarm_mission");
		player.getCache().remove("garv_door");
		player.getCache().remove("armband");
		int[] questData = Quests.questData.get(Quests.HEROS_QUEST);
		//keep order kosher
		int[] skillIDs = {Skills.STRENGTH, Skills.DEFENSE, Skills.HITPOINTS,
			Skills.ATTACK, Skills.RANGED, Skills.HERBLAW,
			Skills.FISHING, Skills.COOKING, Skills.FIREMAKING, Skills.WOODCUT, Skills.MINING, Skills.SMITHING};
		for (int i = 0; i < skillIDs.length; i++) {
			questData[Quests.MAPIDX_SKILL] = skillIDs[i];
			incQuestReward(player, questData, i == (skillIDs.length - 1));
		}
		player.message("@gre@You haved gained 1 quest point!");

	}

	/**
	 * 457, 377
	 **/
	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.ACHETTIES.id(), NpcId.GRUBOR.id(),
				NpcId.TROBERT.id(), NpcId.GRIP.id()}, n.getID());
	}

	private void dutiesDialogue(Player p, Npc n) {
		npcTalk(p, n, "You'll have various guard duty shifts",
			"I may have specific tasks to give you as they come up",
			"If anything happens to me you need to take over as head guard",
			"You'll find Important keys to the treasure room and Pete's quarters",
			"Inside my jacket");
		int sub_menu2 = showMenu(p, n,
			"So can I guard the treasure room please",
			"Well I'd better sort my new room out",
			"Anything I can do now?");
		if (sub_menu2 == 0) {
			npcTalk(p, n, "Well I might post you outside it sometimes",
				"I prefer to be the only one allowed inside though",
				"There's some pretty valuable stuff in there",
				"Those keys stay only with the head guard and with Scarface Pete");
		} else if (sub_menu2 == 1) {
			npcTalk(p, n, "Yeah I'll give you time to settle in");
		} else if (sub_menu2 == 2) {
			if (!hasItem(p, ItemId.MISCELLANEOUS_KEY.id()) && p.getQuestStage(this) != -1) {
				npcTalk(p, n, "Hmm well you could find out what this key does",
					"Apparantly it's to something in this building",
					"Though I don't for the life of me know what");
				playerTalk(p, n, "Grip hands you a key");
				addItem(p, ItemId.MISCELLANEOUS_KEY.id(), 1);
			} else {
				npcTalk(p, n, "Can't think of anything right now");
			}
		}
	}

	private void treasureRoomDialogue(Player p, Npc n) {
		npcTalk(p, n, "Well I might post you outside it sometimes",
			"I prefer to be the only one allowed inside though",
			"There's some pretty valuable stuff in there",
			"Those keys stay only with the head guard and with Scarface Pete");
		int sub_menu = showMenu(p, n,
			"So what do my duties involve?",
			"Well I'd better sort my new room out");
		if (sub_menu == 0) {
			npcTalk(p, n, "You'll have various guard duty shifts",
				"I may have specific tasks to give you as they come up",
				"If anything happens to me you need to take over as head guard",
				"You'll find Important keys to the treasure room and Pete's quarters",
				"Inside my jacket");
			int sub_menu2 = showMenu(p, n,
				"So can I guard the treasure room please",
				"Well I'd better sort my new room out",
				"Anything I can do now?");
			if (sub_menu2 == 0) {
				npcTalk(p, n, "Well I might post you outside it sometimes",
					"I prefer to be the only one allowed inside though",
					"There's some pretty valuable stuff in there",
					"Those keys stay only with the head guard and with Scarface Pete");
			} else if (sub_menu2 == 1) {
				npcTalk(p, n, "Yeah I'll give you time to settle in");
			} else if (sub_menu2 == 2) {
				if (!hasItem(p, ItemId.MISCELLANEOUS_KEY.id()) && p.getQuestStage(this) != -1) {
					npcTalk(p, n, "Hmm well you could find out what this key does",
						"Apparantly it's to something in this building",
						"Though I don't for the life of me know what");
					playerTalk(p, n, "Grip hands you a key");
					addItem(p, ItemId.MISCELLANEOUS_KEY.id(), 1);
				} else {
					npcTalk(p, n, "Can't think of anything right now");
				}
			}
		} else if (sub_menu == 1) {
			npcTalk(p, n, "Yeah I'll give you time to settle in");
		}
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GRIP.id()) {
			if (p.getCache().hasKey("talked_grip") || p.getQuestStage(this) == -1) {
				int menu = showMenu(p, n,
					"So can I guard the treasure room please",
					"So what do my duties involve?",
					"Well I'd better sort my new room out");
				if (menu == 0) {
					treasureRoomDialogue(p, n);
				} else if (menu == 1) {
					dutiesDialogue(p, n);
				} else if (menu == 2) {
					npcTalk(p, n, "Yeah I'll give you time to settle in");
				}
				return;
			}
			playerTalk(p, n, "Hi I am Hartigen",
				"I've come to take the job as your deputy");
			npcTalk(p, n, "Ah good at last, you took you're time getting here",
				"Now let me see",
				"Your quarters will be that room nearest the sink",
				"I'll get your hours of duty sorted in a bit",
				"Oh and have you got your I.D paper",
				"Internal security is almost as important as external security for a guard");
			if (!hasItem(p, ItemId.ID_PAPER.id())) {
				playerTalk(p, n, "Oh dear I don't have that with me any more");
			} else {
				p.message("You hand your I.D paper to grip");
				removeItem(p, ItemId.ID_PAPER.id(), 1);
				p.getCache().store("talked_grip", true);
				int menu = showMenu(p, n,
					"So can I guard the treasure room please",
					"So what do my duties involve?",
					"Well I'd better sort my new room out");
				if (menu == 0) {
					treasureRoomDialogue(p, n);
				} else if (menu == 1) {
					dutiesDialogue(p, n);
				} else if (menu == 2) {
					npcTalk(p, n, "Yeah I'll give you time to settle in");
				}
			}
		}
		else if (n.getID() == NpcId.TROBERT.id()) {
			if (p.getQuestStage(this) == -1) {
				return;
			}
			if (p.getCache().hasKey("hq_impersonate")) {
				if (hasItem(p, ItemId.ID_PAPER.id())) {
					return;
				} else {
					playerTalk(p, n, "I have lost Hartigen's I.D paper");
					npcTalk(p, n, "That was careless",
						"He had a spare fortunatley",
						"Here it is");
					addItem(p, ItemId.ID_PAPER.id(), 1);
					npcTalk(p, n, "Be more careful this time");
				}
				return;
			}
			npcTalk(p, n, "Hi, welcome to our Brimhaven headquarters",
				"I'm Trobert and I'm in charge here");
			int menu = showMenu(p, n, false, //do not send over
				"So can you help me get Scarface Pete's candlesticks?",
				"pleased to meet you");
			if (menu == 0) {
				playerTalk(p, n, "So can you help me get Scarface Pete's candlesticks?");
				npcTalk(p, n, "Well we have made some progress there",
					"We know one of the keys to Pete's treasure room is carried by Grip the head guard",
					"So we thought it might be good to get close to the head guard",
					"Grip was taking on a new deputy called Hartigen",
					"Hartigen was an Asgarnian black knight",
					"However he was deserting the black knight fortress and seeking new employment",
					"We managed to waylay him on the way here",
					"We now have his i.d paper",
					"Next we need someone to impersonate the black knight");
				int sec_menu = showMenu(p, n,
					"I volunteer to undertake that mission",
					"Well good luck then");
				if (sec_menu == 0) {
					npcTalk(p, n, "Well here's the I.d");
					addItem(p, ItemId.ID_PAPER.id(), 1);
					p.getCache().store("hq_impersonate", true);
					npcTalk(p, n, "Take that to the guard room at Scarface Pete's mansion");
				}
			} else if (menu == 1) {
				playerTalk(p, n, "Pleased to meet you");
			}
		}
		else if (n.getID() == NpcId.GRUBOR.id()) {
			playerTalk(p, n, "Hi");
			npcTalk(p, n, "Hi, I'm a little busy right now");
		}
		else if (n.getID() == NpcId.ACHETTIES.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					npcTalk(p, n, "Greetings welcome to the hero's guild",
						"Only the foremost hero's of the land can enter here");
					int opt = showMenu(p, n,
						"I'm a hero, may I apply to join?",
						"Good for the foremost hero's of the land");
					if (opt == 0) {
						if ((p.getQuestStage(Constants.Quests.LOST_CITY) == -1 &&
							(p.getQuestStage(Constants.Quests.SHIELD_OF_ARRAV) == -1 ||
								p.getQuestStage(Constants.Quests.SHIELD_OF_ARRAV) == -2 ) &&
							p.getQuestStage(Constants.Quests.MERLINS_CRYSTAL) == -1 &&
							p.getQuestStage(Constants.Quests.DRAGON_SLAYER) == -1)
							&& p.getQuestPoints() >= 55) {
							npcTalk(p, n, "Ok you may begin the tasks for joining the hero's guild",
								"You need the feather of an Entrana firebird",
								"A master thief armband",
								"And a cooked lava eel");
							p.updateQuestStage(this, 1);
							int opt2 = showMenu(p, n, false, //do not send over
								"Any hints on getting the armband?",
								"Any hints on getting the feather?",
								"Any hints on getting the eel?",
								"I'll start looking for all those things then");
							if (opt2 == 0) {
								playerTalk(p, n, "Any hints on getting the thieves armband?");
								npcTalk(p, n, "I'm sure you have relevant contacts to find out about that");
							} else if (opt2 == 1) {
								playerTalk(p, n, "Any hints on getting the feather?");
								npcTalk(p, n, "Not really - Entrana firebirds live on Entrana");
							} else if (opt2 == 2) {
								playerTalk(p, n, "Any hints on getting the eel?");
								npcTalk(p, n, "Maybe go and find someone who knows a lot about fishing?");
							}
						} else {
							npcTalk(p, n, "You're a hero?, I've never heard of you");
							message(p, "You need to have 55 quest points to file for an application",
								"You also need to have completed the following quests",
								"The shield of arrav, the lost city",
								"Merlin's crystal and dragon slayer\"");
						}
					}
					break;
				case 1:
				case 2:
					npcTalk(p, n, "Greetings welcome to the hero's guild",
						"How goes thy quest?");
					if (hasItem(p, ItemId.MASTER_THIEF_ARMBAND.id()) && hasItem(p, ItemId.LAVA_EEL.id()) && hasItem(p, ItemId.RED_FIREBIRD_FEATHER.id())) {
						playerTalk(p, n, "I have all the things needed");
						removeItem(p, ItemId.MASTER_THIEF_ARMBAND.id(), 1);
						removeItem(p, ItemId.LAVA_EEL.id(), 1);
						removeItem(p, ItemId.RED_FIREBIRD_FEATHER.id(), 1);
						p.sendQuestComplete(Constants.Quests.HEROS_QUEST);
					} else {
						playerTalk(p, n, "It's tough, I've not done it yet");
						npcTalk(p, n, "Remember you need the feather of an Entrana firebird",
							"A master thief armband",
							"And a cooked lava eel");
						int opt2 = showMenu(p, n, false, //do not send over
							"Any hints on getting the armband?",
							"Any hints on getting the feather?",
							"Any hints on getting the eel?",
							"I'll start looking for all those things then");
						if (opt2 == 0) {
							playerTalk(p, n, "Any hints on getting the thieves armband?");
							npcTalk(p, n, "I'm sure you have relevant contacts to find out about that");
						} else if (opt2 == 1) {
							playerTalk(p, n, "Any hints on getting the feather?");
							npcTalk(p, n, "Not really - Entrana firebirds live on Entrana");
						} else if (opt2 == 2) {
							playerTalk(p, n, "Any hints on getting the eel?");
							npcTalk(p, n, "Maybe go and find someone who knows a lot about fishing?");
						}
					}
					break;
				case -1:
					npcTalk(p, n, "Greetings welcome to the hero's guild");
					break;

			}
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if (i.getID() == ItemId.RED_FIREBIRD_FEATHER.id()) {
			if (p.getInventory().wielding(ItemId.ICE_GLOVES.id())) {
				return false;
			} else {
				p.message("Ouch that is too hot to take");
				p.message("I need something cold to pick it up with");
				int damage = (int) Math.round((p.getSkills().getLevel(Skills.HITPOINTS)) * 0.15D);
				p.damage(damage);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		return (obj.getID() == 78 && obj.getX() == 448 && obj.getY() == 682)
				|| (obj.getID() == 76 && obj.getX() == 439 && obj.getY() == 694)
				|| (obj.getID() == 75 && obj.getX() == 463 && obj.getY() == 681)
				|| (obj.getID() == 77 && obj.getX() == 463 && obj.getY() == 676) || (obj.getID() == 79)
				|| (obj.getID() == 80 && obj.getX() == 459 && obj.getY() == 674)
				|| (obj.getID() == 81 && obj.getX() == 472 && obj.getY() == 674);
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 78 && obj.getX() == 448 && obj.getY() == 682) {
			if (p.getCache().hasKey("talked_alf") || p.getQuestStage(this) == -1) {
				p.message("you open the door");
				p.message("You go through the door");
				doDoor(obj, p);
			} else {
				Npc alf = getNearestNpc(p, NpcId.ALFONSE_THE_WAITER.id(), 10);
				if (alf != null) {
					npcTalk(p, alf, "Hey you can't go through there, that's private");
				}
			}
		}
		else if (obj.getID() == 76 && obj.getX() == 439 && obj.getY() == 694) {
			Npc grubor = getNearestNpc(p, NpcId.GRUBOR.id(), 10);
			if (p.getQuestStage(this) == -1) {
				npcTalk(p, grubor, "Yes? what do you want?");
				int mem = showMenu(p, grubor, false, //do not send over
					"Would you like to have your windows refitting?",
					"I want to come in",
					"Do you want to trade?");
				if (mem == 0) {
					playerTalk(p, grubor, "Would you like to have your windows refitting?");
					npcTalk(p, grubor, "Don't be daft, we don't have any windows");
				} else if (mem == 1) {
					playerTalk(p, grubor, "I want to come in");
					npcTalk(p, grubor, "No, go away");
				} else if (mem == 2) {
					playerTalk(p, grubor, "Do you want to trade");
					npcTalk(p, grubor, "No I'm busy");
				}
				return;
			}
			if (p.getCache().hasKey("blackarm_mission")) {
				if (p.getCache().hasKey("talked_grubor")) {
					p.message("you open the door");
					p.message("You go through the door");
					doDoor(obj, p);
				} else {
					if (grubor != null) {
						npcTalk(p, grubor, "Yes? what do you want?");
						int menu = showMenu(p, grubor, false, //do not send over
							"Rabbit's foot",
							"four leaved clover",
							"Lucky Horseshoe",
							"Black cat");
						if (menu == 1) {
							playerTalk(p, grubor, "Four leaved clover");
							npcTalk(p, grubor, "Oh you're one of the gang are you",
								"Just a second I'll let you in");
							p.message("You here the door being unbarred");
							p.getCache().store("talked_grubor", true);
							return;
						}
						if (menu == 0) {
							playerTalk(p, grubor, "Rabbit's foot");
						} else if (menu == 2) {
							playerTalk(p, grubor, "Lucky Horseshoe");
						} else if (menu == 3) {
							playerTalk(p, grubor, "Black cat");
						}
						npcTalk(p, grubor, "What are you on about",
							"Go away");
						return;
					}
				}
			} else {
				p.message("The door won't open");
			}
		}
		else if (obj.getID() == 75 && obj.getX() == 463 && obj.getY() == 681) {
			Npc garv = getNearestNpc(p, NpcId.GARV.id(), 12);
			if (p.getCache().hasKey("garv_door") || p.getQuestStage(this) == -1) {
				p.message("you open the door");
				p.message("You go through the door");
				doDoor(obj, p);
				return;
			}
			if (garv != null) {
				npcTalk(p, garv, "Where do you think you're going?");
				if (isBlackArmGang(p)) {
					playerTalk(p, garv, "Hi, I'm Hartigen",
						"I've come to work here");
					if (p.getInventory().wielding(ItemId.BLACK_PLATE_MAIL_LEGS.id())
							&& p.getInventory().wielding(ItemId.LARGE_BLACK_HELMET.id())&& p.getInventory().wielding(ItemId.BLACK_PLATE_MAIL_BODY.id())) {
						npcTalk(p, garv, "So have you got your i.d paper?");
						if (hasItem(p, ItemId.ID_PAPER.id())) {
							npcTalk(p, garv, "You had better come in then",
								"Grip will want to talk to you");
							p.getCache().store("garv_door", true);
						} else {
							playerTalk(p, garv, "No I must have left it in my other suit of armour");
						}
					} else {
						npcTalk(p, garv, "Hartigen the black knight?",
							"I don't think so - he doesn't dress like that");
					}
				}
			}
		}
		else if (obj.getID() == 77 && obj.getX() == 463 && obj.getY() == 676) {
			if (p.getCache().hasKey("talked_grip") || p.getQuestStage(this) == -1) {
				p.message("you open the door");
				p.message("You go through the door");
				doDoor(obj, p);
			} else {
				p.message("You can't get through the door");
				p.message("You need to speak to grip first");
			}
		}
		else if (obj.getID() == 79) { // strange panel - 11
			p.playSound("secretdoor");
			p.message("You just went through a secret door");
			doDoor(obj, p, 11);
		}
		else if (obj.getID() == 80 && obj.getX() == 459 && obj.getY() == 674) {
			p.message("The door is locked");
			playerTalk(p, null, "This room isn't a lot of use on it's own",
				"Maybe I can get extra help from the inside somehow",
				"I wonder if any of the other players have found a way in");
		}
		else if (obj.getID() == 81 && obj.getX() == 472 && obj.getY() == 674) {
			p.message("The door is locked");
		}
	}

	@Override
	public boolean blockInvUseOnWallObject(GameObject obj, Item item, Player player) {
		return obj.getID() == 80 || obj.getID() == 81;
	}

	@Override
	public void onInvUseOnWallObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 80) {
			if (item.getID() == ItemId.MISCELLANEOUS_KEY.id()) {
				showBubble(p, item);
				p.message("You unlock the door");
				p.message("You go through the door");
				doDoor(obj, p);
			}
		}
		else if (obj.getID() == 81) {
			if (item.getID() == ItemId.BUNCH_OF_KEYS.id()) {
				p.message("You open the door");
				p.message("You go through the door");
				doDoor(obj, p);
			}
		}

	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == GRIPS_CUPBOARD_OPEN || obj.getID() == GRIPS_CUPBOARD_CLOSED
				|| obj.getID() == CANDLESTICK_CHEST_OPEN || obj.getID() == CANDLESTICK_CHEST_CLOSED;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		Npc guard = getNearestNpc(p, NpcId.GUARD_PIRATE.id(), 10);
		Npc grip = getNearestNpc(p, NpcId.GRIP.id(), 15);
		if (obj.getID() == GRIPS_CUPBOARD_OPEN || obj.getID() == GRIPS_CUPBOARD_CLOSED) {
			if (command.equalsIgnoreCase("open") || command.equalsIgnoreCase("search")) {
				if (guard != null) {
					npcTalk(p, guard, "I don't think Mr Grip will like you opening that up",
						"That's his drinks cabinet");
					int menu = showMenu(p, guard,
						"He won't notice me having a quick look",
						"Ok I'll leave it");
					if (menu == 0) {
						if (grip != null) {
							grip.teleport(463, 673);
							npcTalk(p, grip, "Hey what are you doing there",
								"That's my drinks cabinet get away from it");
						} else {
							if (command.equalsIgnoreCase("open")) {
								openCupboard(obj, p, GRIPS_CUPBOARD_OPEN);
							} else {
								p.message("You find a bottle of whisky in the cupboard");
								addItem(p, ItemId.DRAYNOR_WHISKY.id(), 1);
							}
						}
					}
				} else {
					p.message("The guard is busy at the moment");
				}
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, p, GRIPS_CUPBOARD_CLOSED);
			}
		}
		else if (obj.getID() == CANDLESTICK_CHEST_OPEN || obj.getID() == CANDLESTICK_CHEST_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openGenericObject(obj, p, CANDLESTICK_CHEST_OPEN, "You open the chest");
			} else if (command.equalsIgnoreCase("close")) {
				closeGenericObject(obj, p, CANDLESTICK_CHEST_CLOSED, "You close the chest");
			} else {
				if (!hasItem(p, ItemId.CANDLESTICK.id())) {
					addItem(p, ItemId.CANDLESTICK.id(), 2);
					message(p, "You find two candlesticks in the chest",
						"So that will be one for you",
						"And one to the person who killed grip for you");
					if (p.getQuestStage(this) == 1) {
						p.updateQuestStage(this, 2);
					}
				} else {
					p.message("The chest is empty");
				}
			}
		}

	}
	
	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GRIP.id()) {
			World.getWorld().registerItem(
					new GroundItem(ItemId.BUNCH_OF_KEYS.id(), n.getX(), n.getY(), 1, (Player) null));
		}
		n.killedBy(p);
	}
	
	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == NpcId.GRIP.id();
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.GRIP.id();
	}

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		return n.getID() == NpcId.GRIP.id() && !p.getLocation().inHeroQuestRangeRoom();
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		return n.getID() == NpcId.GRIP.id() && !p.getLocation().inHeroQuestRangeRoom();
	}

	@Override
	public void onPlayerMageNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GRIP.id() && !p.getLocation().inHeroQuestRangeRoom()) {
			playerTalk(p, null, "I can't attack the head guard here",
					"There are too many witnesses to see me do it",
					"I'd have the whole of Brimhaven after me",
					"Besides if he dies I want to have the chance of being promoted");
			p.message("Maybe you need another player's help");
		}
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GRIP.id() && !p.getLocation().inHeroQuestRangeRoom()) {
			playerTalk(p, null, "I can't attack the head guard here",
				"There are too many witnesses to see me do it",
				"I'd have the whole of Brimhaven after me",
				"Besides if he dies I want to have the chance of being promoted");
			p.message("Maybe you need another player's help");
		}
	}

	@Override
	public void onPlayerAttackNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GRIP.id()) {
			if (!p.getLocation().inHeroQuestRangeRoom()) {
				playerTalk(p, null, "I can't attack the head guard here",
					"There are too many witnesses to see me do it",
					"I'd have the whole of Brimhaven after me",
					"Besides if he dies I want to have the chance of being promoted");
				p.message("Maybe you need another player's help");
			}
		}

	}

}
