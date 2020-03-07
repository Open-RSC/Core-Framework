package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isBlackArmGang;

public class HerosQuest implements QuestInterface, TalkNpcTrigger,
	OpBoundTrigger, UseBoundTrigger, OpLocTrigger, AttackNpcTrigger, PlayerRangeNpcTrigger, SpellNpcTrigger,
	KillNpcTrigger, TakeObjTrigger {

	private static final int GRIPS_CUPBOARD_OPEN = 264;
	private static final int GRIPS_CUPBOARD_CLOSED = 263;
	private static final int CANDLESTICK_CHEST_OPEN = 265;
	private static final int CANDLESTICK_CHEST_CLOSED = 266;

	private static final AtomicReference<SingleEvent> gripReturnEvent = new AtomicReference<>();

	@Override
	public int getQuestId() {
		return Quests.HEROS_QUEST;
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
		int[] questData = player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.HEROS_QUEST);
		//keep order kosher
		int[] skillIDs = {Skills.STRENGTH, Skills.DEFENSE, Skills.HITS,
			Skills.ATTACK, Skills.RANGED, Skills.HERBLAW,
			Skills.FISHING, Skills.COOKING, Skills.FIREMAKING,
			Skills.WOODCUT, Skills.MINING, Skills.SMITHING};
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
	public boolean blockTalkNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.ACHETTIES.id(), NpcId.GRUBOR.id(),
				NpcId.TROBERT.id(), NpcId.GRIP.id()}, n.getID());
	}

	private void dutiesDialogue(Player p, Npc n) {
		npcsay(p, n, "You'll have various guard duty shifts",
			"I may have specific tasks to give you as they come up",
			"If anything happens to me you need to take over as head guard",
			"You'll find Important keys to the treasure room and Pete's quarters",
			"Inside my jacket");
		int sub_menu2 = multi(p, n,
			"So can I guard the treasure room please",
			"Well I'd better sort my new room out",
			"Anything I can do now?");
		if (sub_menu2 == 0) {
			npcsay(p, n, "Well I might post you outside it sometimes",
				"I prefer to be the only one allowed inside though",
				"There's some pretty valuable stuff in there",
				"Those keys stay only with the head guard and with Scarface Pete");
		} else if (sub_menu2 == 1) {
			npcsay(p, n, "Yeah I'll give you time to settle in");
		} else if (sub_menu2 == 2) {
			if (!p.getCarriedItems().hasCatalogID(ItemId.MISCELLANEOUS_KEY.id(), Optional.empty()) ) {
				npcsay(p, n, "Hmm well you could find out what this key does",
					"Apparantly it's to something in this building",
					"Though I don't for the life of me know what");
				say(p, n, "Grip hands you a key");
				give(p, ItemId.MISCELLANEOUS_KEY.id(), 1);
			} else {
				npcsay(p, n, "Can't think of anything right now");
			}
		}
	}

	private void treasureRoomDialogue(Player p, Npc n) {
		npcsay(p, n, "Well I might post you outside it sometimes",
			"I prefer to be the only one allowed inside though",
			"There's some pretty valuable stuff in there",
			"Those keys stay only with the head guard and with Scarface Pete");
		int sub_menu = multi(p, n,
			"So what do my duties involve?",
			"Well I'd better sort my new room out");
		if (sub_menu == 0) {
			npcsay(p, n, "You'll have various guard duty shifts",
				"I may have specific tasks to give you as they come up",
				"If anything happens to me you need to take over as head guard",
				"You'll find Important keys to the treasure room and Pete's quarters",
				"Inside my jacket");
			int sub_menu2 = multi(p, n,
				"So can I guard the treasure room please",
				"Well I'd better sort my new room out",
				"Anything I can do now?");
			if (sub_menu2 == 0) {
				npcsay(p, n, "Well I might post you outside it sometimes",
					"I prefer to be the only one allowed inside though",
					"There's some pretty valuable stuff in there",
					"Those keys stay only with the head guard and with Scarface Pete");
			} else if (sub_menu2 == 1) {
				npcsay(p, n, "Yeah I'll give you time to settle in");
			} else if (sub_menu2 == 2) {
				if (!p.getCarriedItems().hasCatalogID(ItemId.MISCELLANEOUS_KEY.id(), Optional.empty()) ) {
					npcsay(p, n, "Hmm well you could find out what this key does",
						"Apparantly it's to something in this building",
						"Though I don't for the life of me know what");
					say(p, n, "Grip hands you a key");
					give(p, ItemId.MISCELLANEOUS_KEY.id(), 1);
				} else {
					npcsay(p, n, "Can't think of anything right now");
				}
			}
		} else if (sub_menu == 1) {
			npcsay(p, n, "Yeah I'll give you time to settle in");
		}
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GRIP.id()) {
			if (p.getCache().hasKey("talked_grip") || p.getQuestStage(this) == -1) {
				int menu = multi(p, n,
					"So can I guard the treasure room please",
					"So what do my duties involve?",
					"Well I'd better sort my new room out");
				if (menu == 0) {
					treasureRoomDialogue(p, n);
				} else if (menu == 1) {
					dutiesDialogue(p, n);
				} else if (menu == 2) {
					npcsay(p, n, "Yeah I'll give you time to settle in");
				}
				return;
			}
			say(p, n, "Hi I am Hartigen",
				"I've come to take the job as your deputy");
			npcsay(p, n, "Ah good at last, you took you're time getting here",
				"Now let me see",
				"Your quarters will be that room nearest the sink",
				"I'll get your hours of duty sorted in a bit",
				"Oh and have you got your I.D paper",
				"Internal security is almost as important as external security for a guard");
			if (!p.getCarriedItems().hasCatalogID(ItemId.ID_PAPER.id(), Optional.of(false))) {
				say(p, n, "Oh dear I don't have that with me any more");
			} else {
				p.message("You hand your I.D paper to grip");
				remove(p, ItemId.ID_PAPER.id(), 1);
				p.getCache().store("talked_grip", true);
				int menu = multi(p, n,
					"So can I guard the treasure room please",
					"So what do my duties involve?",
					"Well I'd better sort my new room out");
				if (menu == 0) {
					treasureRoomDialogue(p, n);
				} else if (menu == 1) {
					dutiesDialogue(p, n);
				} else if (menu == 2) {
					npcsay(p, n, "Yeah I'll give you time to settle in");
				}
			}
		}
		else if (n.getID() == NpcId.TROBERT.id()) {
			if (p.getQuestStage(this) == -1) {
				return;
			}
			if (p.getCache().hasKey("hq_impersonate")) {
				if (p.getCarriedItems().hasCatalogID(ItemId.ID_PAPER.id(), Optional.empty())) {
					return;
				} else {
					say(p, n, "I have lost Hartigen's I.D paper");
					npcsay(p, n, "That was careless",
						"He had a spare fortunatley",
						"Here it is");
					give(p, ItemId.ID_PAPER.id(), 1);
					npcsay(p, n, "Be more careful this time");
				}
				return;
			}
			npcsay(p, n, "Hi, welcome to our Brimhaven headquarters",
				"I'm Trobert and I'm in charge here");
			int menu = multi(p, n, false, //do not send over
				"So can you help me get Scarface Pete's candlesticks?",
				"pleased to meet you");
			if (menu == 0) {
				say(p, n, "So can you help me get Scarface Pete's candlesticks?");
				npcsay(p, n, "Well we have made some progress there",
					"We know one of the keys to Pete's treasure room is carried by Grip the head guard",
					"So we thought it might be good to get close to the head guard",
					"Grip was taking on a new deputy called Hartigen",
					"Hartigen was an Asgarnian black knight",
					"However he was deserting the black knight fortress and seeking new employment",
					"We managed to waylay him on the way here",
					"We now have his i.d paper",
					"Next we need someone to impersonate the black knight");
				int sec_menu = multi(p, n,
					"I volunteer to undertake that mission",
					"Well good luck then");
				if (sec_menu == 0) {
					npcsay(p, n, "Well here's the I.d");
					give(p, ItemId.ID_PAPER.id(), 1);
					p.getCache().store("hq_impersonate", true);
					npcsay(p, n, "Take that to the guard room at Scarface Pete's mansion");
				}
			} else if (menu == 1) {
				say(p, n, "Pleased to meet you");
			}
		}
		else if (n.getID() == NpcId.GRUBOR.id()) {
			say(p, n, "Hi");
			npcsay(p, n, "Hi, I'm a little busy right now");
		}
		else if (n.getID() == NpcId.ACHETTIES.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					npcsay(p, n, "Greetings welcome to the hero's guild",
						"Only the foremost hero's of the land can enter here");
					int opt = multi(p, n,
						"I'm a hero, may I apply to join?",
						"Good for the foremost hero's of the land");
					if (opt == 0) {
						if ((p.getQuestStage(Quests.LOST_CITY) == -1 &&
							(p.getQuestStage(Quests.SHIELD_OF_ARRAV) == -1 ||
								p.getQuestStage(Quests.SHIELD_OF_ARRAV) == -2 ) &&
							p.getQuestStage(Quests.MERLINS_CRYSTAL) == -1 &&
							p.getQuestStage(Quests.DRAGON_SLAYER) == -1)
							&& p.getQuestPoints() >= 55) {
							npcsay(p, n, "Ok you may begin the tasks for joining the hero's guild",
								"You need the feather of an Entrana firebird",
								"A master thief armband",
								"And a cooked lava eel");
							p.updateQuestStage(this, 1);
							int opt2 = multi(p, n, false, //do not send over
								"Any hints on getting the armband?",
								"Any hints on getting the feather?",
								"Any hints on getting the eel?",
								"I'll start looking for all those things then");
							if (opt2 == 0) {
								say(p, n, "Any hints on getting the thieves armband?");
								npcsay(p, n, "I'm sure you have relevant contacts to find out about that");
							} else if (opt2 == 1) {
								say(p, n, "Any hints on getting the feather?");
								npcsay(p, n, "Not really - Entrana firebirds live on Entrana");
							} else if (opt2 == 2) {
								say(p, n, "Any hints on getting the eel?");
								npcsay(p, n, "Maybe go and find someone who knows a lot about fishing?");
							}
						} else {
							npcsay(p, n, "You're a hero?, I've never heard of you");
							Functions.mes(p, "You need to have 55 quest points to file for an application",
								"You also need to have completed the following quests",
								"The shield of arrav, the lost city",
								"Merlin's crystal and dragon slayer\"");
						}
					}
					break;
				case 1:
				case 2:
					npcsay(p, n, "Greetings welcome to the hero's guild",
						"How goes thy quest?");
					if (p.getCarriedItems().hasCatalogID(ItemId.MASTER_THIEF_ARMBAND.id(), Optional.of(false))
						&& p.getCarriedItems().hasCatalogID(ItemId.LAVA_EEL.id(), Optional.of(false))
						&& p.getCarriedItems().hasCatalogID(ItemId.RED_FIREBIRD_FEATHER.id(), Optional.of(false))) {
						say(p, n, "I have all the things needed");
						remove(p, ItemId.MASTER_THIEF_ARMBAND.id(), 1);
						remove(p, ItemId.LAVA_EEL.id(), 1);
						remove(p, ItemId.RED_FIREBIRD_FEATHER.id(), 1);
						p.sendQuestComplete(Quests.HEROS_QUEST);
					} else {
						say(p, n, "It's tough, I've not done it yet");
						npcsay(p, n, "Remember you need the feather of an Entrana firebird",
							"A master thief armband",
							"And a cooked lava eel");
						int opt2 = multi(p, n, false, //do not send over
							"Any hints on getting the armband?",
							"Any hints on getting the feather?",
							"Any hints on getting the eel?",
							"I'll start looking for all those things then");
						if (opt2 == 0) {
							say(p, n, "Any hints on getting the thieves armband?");
							npcsay(p, n, "I'm sure you have relevant contacts to find out about that");
						} else if (opt2 == 1) {
							say(p, n, "Any hints on getting the feather?");
							npcsay(p, n, "Not really - Entrana firebirds live on Entrana");
						} else if (opt2 == 2) {
							say(p, n, "Any hints on getting the eel?");
							npcsay(p, n, "Maybe go and find someone who knows a lot about fishing?");
						}
					}
					break;
				case -1:
					npcsay(p, n, "Greetings welcome to the hero's guild");
					break;

			}
		}
	}

	@Override
	public void onTakeObj(Player p, GroundItem i) {
		if (i.getID() == ItemId.RED_FIREBIRD_FEATHER.id()) {
			if (!p.getCarriedItems().getEquipment().hasEquipped(ItemId.ICE_GLOVES.id())) {
				p.message("Ouch that is too hot to take");
				p.message("I need something cold to pick it up with");
				int damage = (int) Math.round((p.getSkills().getLevel(Skills.HITS)) * 0.15D);
				p.damage(damage);
			}
		}
	}

	@Override
	public boolean blockTakeObj(Player p, GroundItem i) {
		if (i.getID() == ItemId.RED_FIREBIRD_FEATHER.id()) {
			if (!p.getCarriedItems().getEquipment().hasEquipped(ItemId.ICE_GLOVES.id())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockOpBound(GameObject obj, Integer click, Player player) {
		return (obj.getID() == 78 && obj.getX() == 448 && obj.getY() == 682)
				|| (obj.getID() == 76 && obj.getX() == 439 && obj.getY() == 694)
				|| (obj.getID() == 75 && obj.getX() == 463 && obj.getY() == 681)
				|| (obj.getID() == 77 && obj.getX() == 463 && obj.getY() == 676) || (obj.getID() == 79)
				|| (obj.getID() == 80 && obj.getX() == 459 && obj.getY() == 674)
				|| (obj.getID() == 81 && obj.getX() == 472 && obj.getY() == 674);
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 78 && obj.getX() == 448 && obj.getY() == 682) {
			if (p.getCache().hasKey("talked_alf") || p.getQuestStage(this) == -1) {
				p.message("you open the door");
				p.message("You go through the door");
				doDoor(obj, p);
			} else {
				Npc alf = ifnearvisnpc(p, NpcId.ALFONSE_THE_WAITER.id(), 10);
				if (alf != null) {
					npcsay(p, alf, "Hey you can't go through there, that's private");
				}
			}
		}
		else if (obj.getID() == 76 && obj.getX() == 439 && obj.getY() == 694) {
			Npc grubor = ifnearvisnpc(p, NpcId.GRUBOR.id(), 10);
			if (p.getQuestStage(this) == -1) {
				npcsay(p, grubor, "Yes? what do you want?");
				int mem = multi(p, grubor, false, //do not send over
					"Would you like to have your windows refitting?",
					"I want to come in",
					"Do you want to trade?");
				if (mem == 0) {
					say(p, grubor, "Would you like to have your windows refitting?");
					npcsay(p, grubor, "Don't be daft, we don't have any windows");
				} else if (mem == 1) {
					say(p, grubor, "I want to come in");
					npcsay(p, grubor, "No, go away");
				} else if (mem == 2) {
					say(p, grubor, "Do you want to trade");
					npcsay(p, grubor, "No I'm busy");
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
						npcsay(p, grubor, "Yes? what do you want?");
						int menu = multi(p, grubor, false, //do not send over
							"Rabbit's foot",
							"four leaved clover",
							"Lucky Horseshoe",
							"Black cat");
						if (menu == 1) {
							say(p, grubor, "Four leaved clover");
							npcsay(p, grubor, "Oh you're one of the gang are you",
								"Just a second I'll let you in");
							p.message("You here the door being unbarred");
							p.getCache().store("talked_grubor", true);
							return;
						}
						if (menu == 0) {
							say(p, grubor, "Rabbit's foot");
						} else if (menu == 2) {
							say(p, grubor, "Lucky Horseshoe");
						} else if (menu == 3) {
							say(p, grubor, "Black cat");
						}
						npcsay(p, grubor, "What are you on about",
							"Go away");
						return;
					}
				}
			} else {
				p.message("The door won't open");
			}
		}
		else if (obj.getID() == 75 && obj.getX() == 463 && obj.getY() == 681) {
			Npc garv = ifnearvisnpc(p, NpcId.GARV.id(), 12);
			if (p.getCache().hasKey("garv_door") || p.getQuestStage(this) == -1) {
				p.message("you open the door");
				p.message("You go through the door");
				doDoor(obj, p);
				return;
			}
			if (garv != null) {
				npcsay(p, garv, "Where do you think you're going?");
				if (isBlackArmGang(p)) {
					say(p, garv, "Hi, I'm Hartigen",
						"I've come to work here");
					if (p.getCarriedItems().getEquipment().hasEquipped(ItemId.BLACK_PLATE_MAIL_LEGS.id())
							&& p.getCarriedItems().getEquipment().hasEquipped(ItemId.LARGE_BLACK_HELMET.id())&& p.getCarriedItems().getEquipment().hasEquipped(ItemId.BLACK_PLATE_MAIL_BODY.id())) {
						npcsay(p, garv, "So have you got your i.d paper?");
						if (p.getCarriedItems().hasCatalogID(ItemId.ID_PAPER.id(), Optional.of(false))) {
							npcsay(p, garv, "You had better come in then",
								"Grip will want to talk to you");
							p.getCache().store("garv_door", true);
						} else {
							say(p, garv, "No I must have left it in my other suit of armour");
						}
					} else {
						npcsay(p, garv, "Hartigen the black knight?",
							"I don't think so - he doesn't dress like that");
					}
				}
			}
		}
		else if (obj.getID() == 77 && obj.getX() == 463 && obj.getY() == 676) {
			if (p.getCache().hasKey("talked_grip") || p.getQuestStage(this) == -1) {
				Npc grip = ifnearvisnpc(p, NpcId.GRIP.id(), 15);
				// grip exits if he is not in combat and player opens from inside the room
				boolean moveGrip = (p.getY() <= 675 && grip != null && grip.getY() <= 675 && !grip.isChasing());
				p.message("you open the door");
				p.message("You go through the door");
				if (moveGrip) {
					grip.walk(463, 675);
				}
				doDoor(obj, p);
				if (moveGrip) {
					removeReturnEventIfPresent(p);
					p.getWorld().getServer().getGameEventHandler().add(new SingleEvent(p.getWorld(), null, 1000, "Heroes Quest Grip through door", true) {
						@Override
						public void action() {
							grip.teleport(463, 676);
						}
					});
				}
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
			say(p, null, "This room isn't a lot of use on it's own",
				"Maybe I can get extra help from the inside somehow",
				"I wonder if any of the other players have found a way in");
		}
		else if (obj.getID() == 81 && obj.getX() == 472 && obj.getY() == 674) {
			p.message("The door is locked");
		}
	}

	@Override
	public boolean blockUseBound(GameObject obj, Item item, Player player) {
		return obj.getID() == 80 || obj.getID() == 81;
	}

	@Override
	public void onUseBound(GameObject obj, Item item, Player p) {
		if (obj.getID() == 80) {
			if (item.getCatalogId() == ItemId.MISCELLANEOUS_KEY.id()) {
				thinkbubble(p, item);
				p.message("You unlock the door");
				p.message("You go through the door");
				doDoor(obj, p);
			}
		}
		else if (obj.getID() == 81) {
			if (item.getCatalogId() == ItemId.BUNCH_OF_KEYS.id()) {
				p.message("You open the door");
				p.message("You go through the door");
				doDoor(obj, p);
			}
		}

	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return obj.getID() == GRIPS_CUPBOARD_OPEN || obj.getID() == GRIPS_CUPBOARD_CLOSED
				|| obj.getID() == CANDLESTICK_CHEST_OPEN || obj.getID() == CANDLESTICK_CHEST_CLOSED;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		Npc guard = ifnearvisnpc(p, NpcId.GUARD_PIRATE.id(), 10);
		Npc grip = ifnearvisnpc(p, NpcId.GRIP.id(), 15);
		if (obj.getID() == GRIPS_CUPBOARD_OPEN || obj.getID() == GRIPS_CUPBOARD_CLOSED) {
			if (command.equalsIgnoreCase("open") || command.equalsIgnoreCase("search")) {
				if (guard != null) {
					npcsay(p, guard, "I don't think Mr Grip will like you opening that up",
						"That's his drinks cabinet");
					int menu = multi(p, guard,
						"He won't notice me having a quick look",
						"Ok I'll leave it");
					if (menu == 0) {
						if (grip != null) {
							if (grip.getY() <= 675) {
								grip.walk(463, 673);
							}
							else {
								grip.teleport(463, 673);
								// delayed event to prevent grip being trapped if player had invoked him
								gripReturnEvent.set(new SingleEvent(p.getWorld(), null, 60000 * 10, "Heroes Quest Delayed Return Grip", true) {
									@Override
									public void action() {
										if (grip != null && grip.getY() <= 675)
											grip.teleport(463, 677);
									}
								});
								p.getWorld().getServer().getGameEventHandler().add(gripReturnEvent.get());
							}
							npcsay(p, grip, "Hey what are you doing there",
								"That's my drinks cabinet get away from it");
						} else {
							if (command.equalsIgnoreCase("open")) {
								openCupboard(obj, p, GRIPS_CUPBOARD_OPEN);
							} else {
								p.message("You find a bottle of whisky in the cupboard");
								give(p, ItemId.DRAYNOR_WHISKY.id(), 1);
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
				if (!p.getCarriedItems().hasCatalogID(ItemId.CANDLESTICK.id(), Optional.empty())) {
					give(p, ItemId.CANDLESTICK.id(), 2);
					Functions.mes(p, "You find two candlesticks in the chest",
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

	private void removeReturnEventIfPresent(Player p) {
		if (gripReturnEvent.get() != null) {
			p.getWorld().getServer().getGameEventHandler().remove(gripReturnEvent.get());
			gripReturnEvent.set(null);
		}
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GRIP.id()) {
			p.getWorld().registerItem(
					new GroundItem(p.getWorld(), ItemId.BUNCH_OF_KEYS.id(), n.getX(), n.getY(), 1, (Player) null));
			removeReturnEventIfPresent(p);
		}
		n.killedBy(p);
	}

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return n.getID() == NpcId.GRIP.id();
	}

	@Override
	public boolean blockAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.GRIP.id();
	}

	@Override
	public boolean blockSpellNpc(Player p, Npc n) {
		return n.getID() == NpcId.GRIP.id() && !p.getLocation().inHeroQuestRangeRoom();
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		return n.getID() == NpcId.GRIP.id() && !p.getLocation().inHeroQuestRangeRoom();
	}

	@Override
	public void onSpellNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GRIP.id() && !p.getLocation().inHeroQuestRangeRoom()) {
			say(p, null, "I can't attack the head guard here",
					"There are too many witnesses to see me do it",
					"I'd have the whole of Brimhaven after me",
					"Besides if he dies I want to have the chance of being promoted");
			p.message("Maybe you need another player's help");
		}
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GRIP.id() && !p.getLocation().inHeroQuestRangeRoom()) {
			say(p, null, "I can't attack the head guard here",
				"There are too many witnesses to see me do it",
				"I'd have the whole of Brimhaven after me",
				"Besides if he dies I want to have the chance of being promoted");
			p.message("Maybe you need another player's help");
		}
	}

	@Override
	public void onAttackNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GRIP.id()) {
			if (!p.getLocation().inHeroQuestRangeRoom()) {
				say(p, null, "I can't attack the head guard here",
					"There are too many witnesses to see me do it",
					"I'd have the whole of Brimhaven after me",
					"Besides if he dies I want to have the chance of being promoted");
				p.message("Maybe you need another player's help");
			}
		}

	}

}
