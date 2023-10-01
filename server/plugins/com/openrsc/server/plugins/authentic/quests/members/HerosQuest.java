package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.custom.minigames.CombatOdyssey;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.authentic.quests.free.ShieldOfArrav.isBlackArmGang;

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
	public int getQuestPoints() {
		return Quest.HEROS_QUEST.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the hero guild entry quest");
		player.getCache().remove("talked_grip");
		player.getCache().remove("killed_grip"); //phoenix gang did their part to kill grip
		player.getCache().remove("looted_grip"); //black arm gang did their part to loot grip's chest
		player.getCache().remove("grip_keys");
		player.getCache().remove("hq_impersonate");
		player.getCache().remove("talked_alf");
		player.getCache().remove("talked_grubor");
		player.getCache().remove("blackarm_mission");
		player.getCache().remove("garv_door");
		player.getCache().remove("armband");
		final QuestReward reward = Quest.HEROS_QUEST.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	/**
	 * 457, 377
	 **/
	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.ACHETTIES.id(), NpcId.GRUBOR.id(),
				NpcId.TROBERT.id(), NpcId.GRIP.id(), NpcId.GARV.id()}, n.getID());
	}

	private void dutiesDialogue(Player player, Npc n) {
		npcsay(player, n, "You'll have various guard duty shifts",
			"I may have specific tasks to give you as they come up",
			"If anything happens to me you need to take over as head guard",
			"You'll find Important keys to the treasure room and Pete's quarters",
			"Inside my jacket");
		int sub_menu2 = multi(player, n,
			"So can I guard the treasure room please",
			"Well I'd better sort my new room out",
			"Anything I can do now?");
		if (sub_menu2 == 0) {
			npcsay(player, n, "Well I might post you outside it sometimes",
				"I prefer to be the only one allowed inside though",
				"There's some pretty valuable stuff in there",
				"Those keys stay only with the head guard and with Scarface Pete");
		} else if (sub_menu2 == 1) {
			npcsay(player, n, "Yeah I'll give you time to settle in");
		} else if (sub_menu2 == 2) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.MISCELLANEOUS_KEY.id(), Optional.empty()) ) {
				npcsay(player, n, "Hmm well you could find out what this key does",
					"Apparantly it's to something in this building",
					"Though I don't for the life of me know what");
				say(player, n, "Grip hands you a key");
				give(player, ItemId.MISCELLANEOUS_KEY.id(), 1);
			} else {
				npcsay(player, n, "Can't think of anything right now");
			}
		}
	}

	private void treasureRoomDialogue(Player player, Npc n) {
		npcsay(player, n, "Well I might post you outside it sometimes",
			"I prefer to be the only one allowed inside though",
			"There's some pretty valuable stuff in there",
			"Those keys stay only with the head guard and with Scarface Pete");
		int sub_menu = multi(player, n,
			"So what do my duties involve?",
			"Well I'd better sort my new room out");
		if (sub_menu == 0) {
			npcsay(player, n, "You'll have various guard duty shifts",
				"I may have specific tasks to give you as they come up",
				"If anything happens to me you need to take over as head guard",
				"You'll find Important keys to the treasure room and Pete's quarters",
				"Inside my jacket");
			int sub_menu2 = multi(player, n,
				"So can I guard the treasure room please",
				"Well I'd better sort my new room out",
				"Anything I can do now?");
			if (sub_menu2 == 0) {
				npcsay(player, n, "Well I might post you outside it sometimes",
					"I prefer to be the only one allowed inside though",
					"There's some pretty valuable stuff in there",
					"Those keys stay only with the head guard and with Scarface Pete");
			} else if (sub_menu2 == 1) {
				npcsay(player, n, "Yeah I'll give you time to settle in");
			} else if (sub_menu2 == 2) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.MISCELLANEOUS_KEY.id(), Optional.empty()) ) {
					npcsay(player, n, "Hmm well you could find out what this key does",
						"Apparantly it's to something in this building",
						"Though I don't for the life of me know what");
					say(player, n, "Grip hands you a key");
					give(player, ItemId.MISCELLANEOUS_KEY.id(), 1);
				} else {
					npcsay(player, n, "Can't think of anything right now");
				}
			}
		} else if (sub_menu == 1) {
			npcsay(player, n, "Yeah I'll give you time to settle in");
		}
	}

	private void garvInspectDialogue(Player player, Npc n) {
		say(player, n, "Hi, I'm Hartigen",
			"I've come to work here");
		if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.BLACK_PLATE_MAIL_LEGS.id())
			&& player.getCarriedItems().getEquipment().hasEquipped(ItemId.LARGE_BLACK_HELMET.id())&& player.getCarriedItems().getEquipment().hasEquipped(ItemId.BLACK_PLATE_MAIL_BODY.id())) {
			npcsay(player, n, "So have you got your i.d paper?");
			if (player.getCarriedItems().hasCatalogID(ItemId.ID_PAPER.id(), Optional.of(false))) {
				npcsay(player, n, "You had better come in then",
					"Grip will want to talk to you");
				player.getCache().store("garv_door", true);
			} else {
				say(player, n, "No I must have left it in my other suit of armour");
			}
		} else {
			npcsay(player, n, "Hartigen the black knight?",
				"I don't think so - he doesn't dress like that");
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GARV.id()) {
			npcsay(player, n, "Hello, what do you want?");
			if (isBlackArmGang(player) && player.getCache().hasKey("hq_impersonate") && !player.getCache().hasKey("garv_door")) {
				garvInspectDialogue(player, n);
			} else {
				int opts = multi(player, n, "Can I go in there?", "I want for nothing");
				if (opts == 0) {
					npcsay(player, n, "No in there is private");
				} else if (opts == 1) {
					npcsay(player, n, "You're one of a very lucky few then");
				}
			}
		}
		else if (n.getID() == NpcId.GRIP.id()) {
			if (player.getCache().hasKey("talked_grip") || player.getQuestStage(this) == -1) {
				int menu = multi(player, n,
					"So can I guard the treasure room please",
					"So what do my duties involve?",
					"Well I'd better sort my new room out");
				if (menu == 0) {
					treasureRoomDialogue(player, n);
				} else if (menu == 1) {
					dutiesDialogue(player, n);
				} else if (menu == 2) {
					npcsay(player, n, "Yeah I'll give you time to settle in");
				}
				return;
			}
			say(player, n, "Hi I am Hartigen",
				"I've come to take the job as your deputy");
			npcsay(player, n, "Ah good at last, you took you're time getting here",
				"Now let me see",
				"Your quarters will be that room nearest the sink",
				"I'll get your hours of duty sorted in a bit",
				"Oh and have you got your I.D paper",
				"Internal security is almost as important as external security for a guard");
			if (!player.getCarriedItems().hasCatalogID(ItemId.ID_PAPER.id(), Optional.of(false))) {
				say(player, n, "Oh dear I don't have that with me any more");
			} else {
				player.message("You hand your I.D paper to grip");
				player.getCarriedItems().remove(new Item(ItemId.ID_PAPER.id()));
				player.getCache().store("talked_grip", true);
				int menu = multi(player, n,
					"So can I guard the treasure room please",
					"So what do my duties involve?",
					"Well I'd better sort my new room out");
				if (menu == 0) {
					treasureRoomDialogue(player, n);
				} else if (menu == 1) {
					dutiesDialogue(player, n);
				} else if (menu == 2) {
					npcsay(player, n, "Yeah I'll give you time to settle in");
				}
			}
		}
		else if (n.getID() == NpcId.TROBERT.id()) {
			if (player.getQuestStage(this) == -1) {
				return;
			}
			if (player.getCache().hasKey("hq_impersonate")) {
				if (player.getCarriedItems().hasCatalogID(ItemId.ID_PAPER.id(), Optional.empty())) {
					return;
				} else {
					say(player, n, "I have lost Hartigen's I.D paper");
					npcsay(player, n, "That was careless",
						"He had a spare fortunatley",
						"Here it is");
					give(player, ItemId.ID_PAPER.id(), 1);
					npcsay(player, n, "Be more careful this time");
				}
				return;
			}
			npcsay(player, n, "Hi, welcome to our Brimhaven headquarters",
				"I'm Trobert and I'm in charge here");
			int menu = multi(player, n, false, //do not send over
				"So can you help me get Scarface Pete's candlesticks?",
				"pleased to meet you");
			if (menu == 0) {
				say(player, n, "So can you help me get Scarface Pete's candlesticks?");
				npcsay(player, n, "Well we have made some progress there",
					"We know one of the keys to Pete's treasure room is carried by Grip the head guard",
					"So we thought it might be good to get close to the head guard",
					"Grip was taking on a new deputy called Hartigen",
					"Hartigen was an Asgarnian black knight",
					"However he was deserting the black knight fortress and seeking new employment",
					"We managed to waylay him on the way here",
					"We now have his i.d paper",
					"Next we need someone to impersonate the black knight");
				int sec_menu = multi(player, n,
					"I volunteer to undertake that mission",
					"Well good luck then");
				if (sec_menu == 0) {
					npcsay(player, n, "Well here's the I.d");
					give(player, ItemId.ID_PAPER.id(), 1);
					player.getCache().store("hq_impersonate", true);
					npcsay(player, n, "Take that to the guard room at Scarface Pete's mansion");
				}
			} else if (menu == 1) {
				say(player, n, "Pleased to meet you");
			}
		}
		else if (n.getID() == NpcId.GRUBOR.id()) {
			say(player, n, "Hi");
			npcsay(player, n, "Hi, I'm a little busy right now");
		}
		else if (n.getID() == NpcId.ACHETTIES.id()) {
			ArrayList<String> choices = new ArrayList<>();
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay(player, n, "Greetings welcome to the hero's guild",
						"Only the foremost hero's of the land can enter here");
					choices.add("I'm a hero, may I apply to join?");
					choices.add("Good for the foremost hero's of the land");
					if (canBuyCape(player)) {
						choices.add("Is your cape also only for the foremost hero's of the land?");
					}
					int opt = multi(player, n, choices.toArray(new String[choices.size()]));
					if (opt == 0) {
						if ((player.getQuestStage(Quests.LOST_CITY) == -1 &&
							(player.getQuestStage(Quests.SHIELD_OF_ARRAV) == -1 ||
								player.getQuestStage(Quests.SHIELD_OF_ARRAV) == -2 ) &&
							player.getQuestStage(Quests.MERLINS_CRYSTAL) == -1 &&
							player.getQuestStage(Quests.DRAGON_SLAYER) == -1)
							&& (player.getConfig().INFLUENCE_INSTEAD_QP || player.getQuestPoints() >= 55)) {
							npcsay(player, n, "Ok you may begin the tasks for joining the hero's guild",
								"You need the feather of an Entrana firebird",
								"A master thief armband",
								"And a cooked lava eel");
							player.updateQuestStage(this, 1);
							int opt2 = multi(player, n, false, //do not send over
								"Any hints on getting the armband?",
								"Any hints on getting the feather?",
								"Any hints on getting the eel?",
								"I'll start looking for all those things then");
							if (opt2 == 0) {
								say(player, n, "Any hints on getting the thieves armband?");
								npcsay(player, n, "I'm sure you have relevant contacts to find out about that");
							} else if (opt2 == 1) {
								say(player, n, "Any hints on getting the feather?");
								npcsay(player, n, "Not really - Entrana firebirds live on Entrana");
							} else if (opt2 == 2) {
								say(player, n, "Any hints on getting the eel?");
								npcsay(player, n, "Maybe go and find someone who knows a lot about fishing?");
							}
						} else {
							npcsay(player, n, "You're a hero?, I've never heard of you");
							mes("You need to have 55 quest points to file for an application");
							delay(3);
							mes("You also need to have completed the following quests");
							delay(3);
							mes("The shield of arrav, the lost city");
							delay(3);
							mes("Merlin's crystal and dragon slayer\"");
							delay(3);
						}
					} else if (canBuyCape(player) && opt == 2) {
						skillcape(player, n);
					}
					break;
				case 1:
				case 2:
					npcsay(player, n, "Greetings welcome to the hero's guild",
						"How goes thy quest?");
					if (player.getCarriedItems().hasCatalogID(ItemId.MASTER_THIEF_ARMBAND.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.LAVA_EEL.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.RED_FIREBIRD_FEATHER.id(), Optional.of(false))) {

						say(player, n, "I have all the things needed");
						player.getCarriedItems().remove(new Item(ItemId.MASTER_THIEF_ARMBAND.id()));
						player.getCarriedItems().remove(new Item(ItemId.LAVA_EEL.id()));
						player.getCarriedItems().remove(new Item(ItemId.RED_FIREBIRD_FEATHER.id()));
						player.sendQuestComplete(Quests.HEROS_QUEST);
					} else {
						say(player, n, "It's tough, I've not done it yet");
						npcsay(player, n, "Remember you need the feather of an Entrana firebird",
							"A master thief armband",
							"And a cooked lava eel");

						choices.add("Any hints on getting the armband?");
						choices.add("Any hints on getting the feather?");
						choices.add("Any hints on getting the eel?");

						String capeDialog = "Any hints on getting a cape like yours?";
						if (canBuyCape(player)) {
							choices.add(capeDialog);
						}
						choices.add("I'll start looking for all those things then");

						int opt2 = multi(player, n, false, //do not send over
							choices.toArray(new String[choices.size()]));
						if (opt2 == 0) {
							say(player, n, "Any hints on getting the thieves armband?");
							npcsay(player, n, "I'm sure you have relevant contacts to find out about that");
						} else if (opt2 == 1) {
							say(player, n, "Any hints on getting the feather?");
							npcsay(player, n, "Not really - Entrana firebirds live on Entrana");
						} else if (opt2 == 2) {
							say(player, n, "Any hints on getting the eel?");
							npcsay(player, n, "Maybe go and find someone who knows a lot about fishing?");
						} else if (canBuyCape(player) && choices.get(opt2).equalsIgnoreCase(capeDialog)) {
							say(player, n, capeDialog);
							skillcape(player, n);
						}
					}
					break;
				case -1:
					if (config().WANT_COMBAT_ODYSSEY
						&& CombatOdyssey.getCurrentTier(player) == 8
						&& CombatOdyssey.isTierCompleted(player)) {
						if (CombatOdyssey.biggumMissing()) return;
						int newTier = 9;
						CombatOdyssey.assignNewTier(player, newTier);
						say(player, n, "Sigbert sent me here for Radimus' quest");
						npcsay(player, n, "Yes he asked me to give you this");
						CombatOdyssey.giveRewards(player, n);
						npcsay(player, n, "For me you have to kill the following");
						npcsay(player, n, player.getWorld().getCombatOdyssey().getTier(newTier).getTasksAndCounts());
						npcsay(player, n, "If you manage to do that then go speak to Radimus himself");
						return;
					}
					npcsay(player, n, "Greetings welcome to the hero's guild");
					if (canBuyCape(player)) {
						if (multi(player, n, "Could I get a heroic cape like yours?",
							"Thank you") == 0) {

							skillcape(player, n);
						}
					}
					break;

			}
		}
	}

	private boolean canBuyCape(Player player) {
		if (config().WANT_CUSTOM_SPRITES
			&& getMaxLevel(player, Skill.STRENGTH.id()) >= 99) { return true; }
		return false;
	}

	private void skillcape(Player player, Npc n) {
		npcsay(player, n, "Yes indeed",
			"I see that you have the strength of a hero",
			"I'd be willing to sell you a cape for 99,000 gold");
		if (multi(player, n, "Alright then", "No thank you") == 0) {
			if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
				mes("Achetties takes your coins");
				delay(3);
				if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
					mes("And hands you a Strength cape");
					delay(3);
					give(player, ItemId.STRENGTH_CAPE.id(), 1);
					npcsay(player, n, "Here you go",
						"This cape will help increase your combat efficiency",
						"By allowing you to perform critical hits");
				}
			} else {
				npcsay(player, n, "Heroes usually have more coins than that",
					"You don't have enough");
			}
		}
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.RED_FIREBIRD_FEATHER.id()) {
			if (player.getQuestStage(this) <= 0) {
				say(player, null, "It looks dangerously hot");
				say(player, null, "And I have no reason to take it");
			} else if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.ICE_GLOVES.id())) {
				player.message("Ouch that is too hot to take");
				player.message("I need something cold to pick it up with");
				int damage = (int) Math.round((player.getSkills().getLevel(Skill.HITS.id())) * 0.15D);
				player.damage(damage);
			}
		} else if (i.getID() == ItemId.BUNCH_OF_KEYS.id()) {
			if (i.getAttribute("fromGrip", false)) {
				if (!player.getCache().hasKey("grip_keys") && player.getQuestStage(this) >= 1) {
					player.getCache().store("grip_keys", true);
				}
			}
			player.getWorld().unregisterItem(i);
			give(player, ItemId.BUNCH_OF_KEYS.id(), 1);
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.RED_FIREBIRD_FEATHER.id()) {
			if (player.getQuestStage(this) <= 0 || !player.getCarriedItems().getEquipment().hasEquipped(ItemId.ICE_GLOVES.id())) {
				return true;
			}
		}
		else if (i.getID() == ItemId.BUNCH_OF_KEYS.id()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return (obj.getID() == 78 && obj.getX() == 448 && obj.getY() == 682)
				|| (obj.getID() == 76 && obj.getX() == 439 && obj.getY() == 694)
				|| (obj.getID() == 75 && obj.getX() == 463 && obj.getY() == 681)
				|| (obj.getID() == 77 && obj.getX() == 463 && obj.getY() == 676) || (obj.getID() == 79)
				|| (obj.getID() == 80 && obj.getX() == 459 && obj.getY() == 674)
				|| (obj.getID() == 81 && obj.getX() == 472 && obj.getY() == 674);
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 78 && obj.getX() == 448 && obj.getY() == 682) {
			if (player.getCache().hasKey("talked_alf") || player.getQuestStage(this) == -1) {
				player.message("you open the door");
				player.message("You go through the door");
				doDoor(obj, player);
			} else {
				Npc alf = ifnearvisnpc(player, NpcId.ALFONSE_THE_WAITER.id(), 10);
				if (alf != null) {
					npcsay(player, alf, "Hey you can't go through there, that's private");
				}
			}
		}
		else if (obj.getID() == 76 && obj.getX() == 439 && obj.getY() == 694) {
			Npc grubor = ifnearvisnpc(player, NpcId.GRUBOR.id(), 10);
			if (player.getQuestStage(this) == -1) {
				if (grubor != null) {
					npcsay(player, grubor, "Yes? what do you want?");
					int mem = multi(player, grubor, false, //do not send over
						"Would you like to have your windows refitting?",
						"I want to come in",
						"Do you want to trade?");
					if (mem == 0) {
						say(player, grubor, "Would you like to have your windows refitting?");
						npcsay(player, grubor, "Don't be daft, we don't have any windows");
					} else if (mem == 1) {
						say(player, grubor, "I want to come in");
						npcsay(player, grubor, "No, go away");
					} else if (mem == 2) {
						say(player, grubor, "Do you want to trade");
						npcsay(player, grubor, "No I'm busy");
					}
				}
				return;
			}
			if (player.getCache().hasKey("blackarm_mission")) {
				if (player.getCache().hasKey("talked_grubor")) {
					player.message("you open the door");
					player.message("You go through the door");
					doDoor(obj, player);
				} else {
					if (grubor != null) {
						npcsay(player, grubor, "Yes? what do you want?");
						int menu = multi(player, grubor, false, //do not send over
							"Rabbit's foot",
							"four leaved clover",
							"Lucky Horseshoe",
							"Black cat");
						if (menu == 1) {
							say(player, grubor, "Four leaved clover");
							npcsay(player, grubor, "Oh you're one of the gang are you",
								"Just a second I'll let you in");
							player.message("You here the door being unbarred");
							player.getCache().store("talked_grubor", true);
							return;
						}
						if (menu == 0) {
							say(player, grubor, "Rabbit's foot");
						} else if (menu == 2) {
							say(player, grubor, "Lucky Horseshoe");
						} else if (menu == 3) {
							say(player, grubor, "Black cat");
						}
						npcsay(player, grubor, "What are you on about",
							"Go away");
						return;
					}
				}
			} else {
				player.message("The door won't open");
			}
		}
		else if (obj.getID() == 75 && obj.getX() == 463 && obj.getY() == 681) {
			Npc garv = ifnearvisnpc(player, NpcId.GARV.id(), 12);
			if (player.getCache().hasKey("garv_door") || player.getQuestStage(this) == -1) {
				player.message("you open the door");
				player.message("You go through the door");
				doDoor(obj, player);
				return;
			}
			if (garv != null) {
				npcsay(player, garv, "Where do you think you're going?");
				if (isBlackArmGang(player) && player.getCache().hasKey("hq_impersonate")) {
					garvInspectDialogue(player, garv);
				}
			}
		}
		else if (obj.getID() == 77 && obj.getX() == 463 && obj.getY() == 676) {
			if (player.getCache().hasKey("talked_grip") || player.getQuestStage(this) == -1) {
				Npc grip = ifnearvisnpc(player, NpcId.GRIP.id(), 15);
				// grip exits if he is not in combat and player opens from inside the room
				boolean moveGrip = (player.getY() <= 675 && grip != null && grip.getY() <= 675 && !grip.isChasing());
				player.message("you open the door");
				player.message("You go through the door");
				if (moveGrip) {
					grip.walk(463, 675);
				}
				doDoor(obj, player);
				if (moveGrip) {
					removeReturnEventIfPresent(player);
					player.getWorld().getServer().getGameEventHandler().add(
						new SingleEvent(player.getWorld(), null,
							config().GAME_TICK * 2,
							"Heroes Quest Grip through door", DuplicationStrategy.ALLOW_MULTIPLE) {
							@Override
							public void action() {
							grip.teleport(463, 676);
						}
					});
				}
			} else {
				player.message("You can't get through the door");
				player.message("You need to speak to grip first");
			}
		}
		else if (obj.getID() == 79) { // strange panel - 11
			player.playSound("secretdoor");
			player.message("You just went through a secret door");
			doDoor(obj, player, 11);
		}
		else if (obj.getID() == 80 && obj.getX() == 459 && obj.getY() == 674) {
			player.message("The door is locked");
			say(player, null, "This room isn't a lot of use on it's own",
				"Maybe I can get extra help from the inside somehow",
				"I wonder if any of the other players have found a way in");
		}
		else if (obj.getID() == 81 && obj.getX() == 472 && obj.getY() == 674) {
			player.message("The door is locked");
		}
	}

	@Override
	public boolean blockUseBound(Player player, GameObject obj, Item item) {
		return obj.getID() == 80 || obj.getID() == 81;
	}

	@Override
	public void onUseBound(Player player, GameObject obj, Item item) {
		if (obj.getID() == 80) {
			if (item.getCatalogId() == ItemId.MISCELLANEOUS_KEY.id()) {
				thinkbubble(item);
				player.message("You unlock the door");
				player.message("You go through the door");
				doDoor(obj, player);
			}
		}
		else if (obj.getID() == 81) {
			if (item.getCatalogId() == ItemId.BUNCH_OF_KEYS.id()) {
				player.message("You open the door");
				player.message("You go through the door");
				doDoor(obj, player);
			}
		}

	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == GRIPS_CUPBOARD_OPEN || obj.getID() == GRIPS_CUPBOARD_CLOSED
				|| obj.getID() == CANDLESTICK_CHEST_OPEN || obj.getID() == CANDLESTICK_CHEST_CLOSED;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		Npc guard = ifnearvisnpc(player, NpcId.GUARD_PIRATE.id(), 10);
		Npc grip = ifnearvisnpc(player, NpcId.GRIP.id(), 15);
		if (obj.getID() == GRIPS_CUPBOARD_OPEN || obj.getID() == GRIPS_CUPBOARD_CLOSED) {
			if (command.equalsIgnoreCase("open") || command.equalsIgnoreCase("search")) {
				if (guard != null) {
					npcsay(player, guard, "I don't think Mr Grip will like you opening that up",
						"That's his drinks cabinet");
					int menu = multi(player, guard,
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
								gripReturnEvent.set(new SingleEvent(player.getWorld(), null,
									config().GAME_TICK * 1000,
									"Heroes Quest Delayed Return Grip", DuplicationStrategy.ALLOW_MULTIPLE) {
									@Override
									public void action() {
										if (grip != null && grip.getY() <= 675)
											grip.teleport(463, 677);
									}
								});
								player.getWorld().getServer().getGameEventHandler().add(gripReturnEvent.get());
							}
							npcsay(player, grip, "Hey what are you doing there",
								"That's my drinks cabinet get away from it");
						} else {
							if (command.equalsIgnoreCase("open")) {
								openCupboard(obj, player, GRIPS_CUPBOARD_OPEN);
							} else {
								player.message("You find a bottle of whisky in the cupboard");
								give(player, ItemId.DRAYNOR_WHISKY.id(), 1);
							}
						}
					}
				} else {
					player.message("The guard is busy at the moment");
				}
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, player, GRIPS_CUPBOARD_CLOSED);
			}
		}
		else if (obj.getID() == CANDLESTICK_CHEST_OPEN || obj.getID() == CANDLESTICK_CHEST_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openGenericObject(obj, player, CANDLESTICK_CHEST_OPEN, "You open the chest");
			} else if (command.equalsIgnoreCase("close")) {
				closeGenericObject(obj, player, CANDLESTICK_CHEST_CLOSED, "You close the chest");
			} else {
				if (!player.getCarriedItems().hasCatalogID(ItemId.CANDLESTICK.id(), Optional.empty())
					&& (player.getCache().hasKey("grip_keys") || player.getQuestStage(this) == -1)) {
					give(player, ItemId.CANDLESTICK.id(), 2);
					mes("You find two candlesticks in the chest");
					delay(3);
					mes("So that will be one for you");
					delay(3);
					mes("And one to the person who killed grip for you");
					delay(3);
					if (player.getQuestStage(this) == 1) {
						player.updateQuestStage(this, 2);
					}
					if (!player.getCache().hasKey("looted_grip") && player.getQuestStage(this) >= 1) {
						player.getCache().store("looted_grip", true);
					}
				} else {
					player.message("The chest is empty");
				}
			}
		}

	}

	private void removeReturnEventIfPresent(Player player) {
		if (gripReturnEvent.get() != null) {
			player.getWorld().getServer().getGameEventHandler().remove(gripReturnEvent.get());
			gripReturnEvent.set(null);
		}
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GRIP.id()) {
			GroundItem keys = new GroundItem(player.getWorld(), ItemId.BUNCH_OF_KEYS.id(), n.getX(), n.getY(), 1, null);
			keys.setAttribute("fromGrip", true);
			player.getWorld().registerItem(keys);
			if (!player.getCache().hasKey("killed_grip") && player.getQuestStage(this) >= 1) {
				player.getCache().store("killed_grip", true);
			}
			removeReturnEventIfPresent(player);
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.GRIP.id();
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		return n.getID() == NpcId.GRIP.id();
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return n.getID() == NpcId.GRIP.id() && !player.getLocation().inHeroQuestRangeRoom();
	}

	@Override
	public boolean blockPlayerRangeNpc(Player player, Npc n) {
		return n.getID() == NpcId.GRIP.id() && !player.getLocation().inHeroQuestRangeRoom();
	}

	@Override
	public void onSpellNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GRIP.id() && !player.getLocation().inHeroQuestRangeRoom()) {
			say(player, null, "I can't attack the head guard here",
					"There are too many witnesses to see me do it",
					"I'd have the whole of Brimhaven after me",
					"Besides if he dies I want to have the chance of being promoted");
			player.message("Maybe you need another player's help");
		}
	}

	@Override
	public void onPlayerRangeNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GRIP.id() && !player.getLocation().inHeroQuestRangeRoom()) {
			say(player, null, "I can't attack the head guard here",
				"There are too many witnesses to see me do it",
				"I'd have the whole of Brimhaven after me",
				"Besides if he dies I want to have the chance of being promoted");
			player.message("Maybe you need another player's help");
		}
	}

	@Override
	public void onAttackNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GRIP.id()) {
			if (!player.getLocation().inHeroQuestRangeRoom()) {
				say(player, null, "I can't attack the head guard here",
					"There are too many witnesses to see me do it",
					"I'd have the whole of Brimhaven after me",
					"Besides if he dies I want to have the chance of being promoted");
				player.message("Maybe you need another player's help");
			}
		}

	}

}
