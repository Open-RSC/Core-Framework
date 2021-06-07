package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.openrsc.server.plugins.Functions.*;

public class TheHolyGrail implements QuestInterface, TalkNpcTrigger,
	OpBoundTrigger,
	OpInvTrigger,
	KillNpcTrigger, TakeObjTrigger,
	OpLocTrigger {

	@Override
	public int getQuestId() {
		return Quests.THE_HOLY_GRAIL;
	}

	@Override
	public String getQuestName() {
		return "The Holy Grail (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.THE_HOLY_GRAIL.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the holy grail quest");
		final QuestReward reward = Quest.THE_HOLY_GRAIL.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
	}

	/**
	 * NPCS: #275 King Arthur - NPC HANDLED IN MERLINS CRYSTAL QUEST FILE. #287 is Merlin trapped
	 * Merlin should be the one of library (393)
	 */
	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.MERLIN_LIBRARY.id(), NpcId.BLACK_KNIGHT_TITAN.id(), NpcId.UNHAPPY_PEASANT.id(),
				NpcId.FISHERMAN.id(), NpcId.FISHER_KING.id(), NpcId.KING_PERCIVAL.id(), NpcId.HAPPY_PEASANT.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.MERLIN_LIBRARY.id()) {
			switch (player.getQuestStage(Quests.THE_HOLY_GRAIL)) {
				case 1:
				case 2:
				case 3:
					say(
						player,
						n,
						"Hello King Arthur has sent me on a quest for the holy grail",
						"He thought you could offer some assistance");
					npcsay(player, n, "Ah yes the holy grail",
						"That is a powerful artifact indeed",
						"Returning it here would help Camelot a lot",
						"Due to its nature the holy grail is likely to reside in a holy place");
					say(player, n, "Any suggestions?");
					npcsay(player,
						n,
						"I believe there is a holy island somewhere not far away",
						"I'm not entirely sure",
						"I spent too long inside that crystal",
						"Anyway go and talk to someone over there",
						"I suppose you could also try speaking to Sir Galahad",
						"He returned from the quest many years after everyone else",
						"He seems to know something about it",
						"but he can only speak about those experiences cryptically");
					if (player.getQuestStage(this) == 1) {
						setQuestStage(player, this, 2);
					}
					int menu = multi(player, n, false, //do not send over
						"Thankyou for the advice",
						"Where can I find Sir Galahad?");
					if (menu == 0) {
						say(player, n, "Thankyou for the advice");
					} else if (menu == 1) {
						say(player, n, "Where can I find Sir Galahad");
						npcsay(player,
							n,
							"Galahad now lives a life of religious contemplation",
							"He lives somewhere west of McGrubors Wood");
					}
					break;
				case -1:
					npcsay(player, n, "hello I'm working on a new spell",
						"To turn people into hedgehogs");
					break;
			}
		}
		else if (n.getID() == NpcId.BLACK_KNIGHT_TITAN.id()) {
			npcsay(player, n, "I am the black knight titan",
				"You must pass through me before you can continue in this realm");
			int menu = multi(player, n, "Ok, have at ye oh evil knight",
				"Actually I think I'll run away");
			if (menu == 0) {
				delay(2);
				n.setChasing(player);
			}
		}
		else if (n.getID() == NpcId.UNHAPPY_PEASANT.id()) {
			npcsay(player, n, "Woe is me", "Our crops are all failing",
				"How shall I feed myself this winter?");
		}
		else if (n.getID() == NpcId.FISHERMAN.id()) {
			npcsay(player, n, "Hi - I don't get many visitors here");
			int menu = multi(player, n, "How's the fishing?",
				"Any idea how to get into the castle?",
				"Yes well this place is a dump");
			if (menu == 0) {
				npcsay(player, n, "Not amazing",
					"Not many fish can live in this gungey stuff",
					"I remember when this was a pleasant river",
					"Teaming with every sort of fish");
			} else if (menu == 1) {
				npcsay(player, n, "why thats easy",
					"just ring one of the bells outside");
				say(player, n, "I didn't see any bells");
				npcsay(player, n, "You must be blind then",
					"There's always bells there when I go to the castle");
				addobject(ItemId.BELL.id(), 1, 421, 30, player);
			} else if (menu == 2) {
				npcsay(player, n, "This place used to be very beautiful",
					"However as our king grows old and weak",
					"the land seems to be dying too");
			}
		}
		else if (n.getID() == NpcId.FISHER_KING.id()) {
			npcsay(player, n, "Ah you got inside at last",
				"You spent all that time fumbling around outside",
				"I thought you'd never make it here");
			if (player.getQuestStage(this) == 3) {
				player.updateQuestStage(this, 4);
			}
			int menu = multi(player, n,
				"How did you know what I have been doing?",
				"I seek the holy grail", "You don't look too well");
			if (menu == 0) {
				npcsay(player, n, "Oh I can see what is happening in my realm",
					"I have sent clues to help you get here",
					"Such as the fisherman", "And the crone");
				int mm = multi(player, n, "I seek the holy grail",
					"You don't look too well",
					"Do you mind if I have a look around?");
				if (mm == 0) {
					npcsay(player,
						n,
						"Ah excellent, a knight come to seek the holy grail",
						"Maybe now our land can be restored to it's former glory",
						"At the moment the grail cannot be removed from the castle",
						"legend has it a questing knight will one day",
						"Work out how to restore our land",
						"then he will claim the grail as his prize");
					say(player, n, "Any ideas how I can restore the land?");
					npcsay(player, n, "None at all");
					int m = multi(player, n, false, //do not send over
						"You don't look to well",
						"Do you mind if I have a look around?");
					if (m == 0) {
						say(player, n, "You don't look too well");
						npcsay(player, n, "Nope I don't feel so good either",
							"I fear my life is running short",
							"Alas my son and heir is not here",
							"I am waiting for my son to return to this castle",
							"If you could find my son that would be a great weight off my shoulders");
						say(player, n, "Who is your son?");
						npcsay(player, n, "He is known as Percival",
							"I believe he is a knight of the round table");
						say(player, n, "I shall go and see if I can find him");
					} else if (m == 1) {
						say(player, n, "Do you mind if I have a look around?");
						npcsay(player, n, "No not at all, be my guest");
					}
				} else if (mm == 1) {
					npcsay(player, n, "Nope I don't feel so good either",
						"I fear my life is running short",
						"Alas my son and heir is not here",
						"I am waiting for my son to return to this castle",
						"If you could find my son that would be a great weight off my shoulders");
					say(player, n, "Who is your son?");
					npcsay(player, n, "He is known as Percival",
						"I believe he is a knight of the round table");
					say(player, n, "I shall go and see if I can find him");
				} else if (mm == 2) {
					npcsay(player, n, "No not at all, be my guest");
				}
			} else if (menu == 1) {
				npcsay(player,
					n,
					"Ah excellent, a knight come to seek the holy grail",
					"Maybe now our land can be restored to it's former glory",
					"At the moment the grail cannot be removed from the castle",
					"legend has it a questing knight will one day",
					"Work out how to restore our land",
					"then he will claim the grail as his prize");
				say(player, n, "Any ideas how I can restore the land?");
				npcsay(player, n, "None at all");
				int m = multi(player, n, false, //do not send over
					"You don't look to well",
					"Do you mind if I have a look around?");
				if (m == 0) {
					say(player, n, "You don't look too well");
					npcsay(player, n, "Nope I don't feel so good either",
						"I fear my life is running short",
						"Alas my son and heir is not here",
						"I am waiting for my son to return to this castle",
						"If you could find my son that would be a great weight off my shoulders");
					say(player, n, "Who is your son?");
					npcsay(player, n, "He is known as Percival",
						"I believe he is a knight of the round table");
					say(player, n, "I shall go and see if I can find him");
				} else if (m == 1) {
					say(player, n, "Do you mind if I have a look around?");
					npcsay(player, n, "No not at all, be my guest");
				}
			} else if (menu == 2) {
				npcsay(player, n, "Nope I don't feel so good either",
					"I fear my life is running short",
					"Alas my son and heir is not here",
					"I am waiting for my son to return to this castle",
					"If you could find my son that would be a great weight off my shoulders");
				say(player, n, "Who is your son?");
				npcsay(player, n, "He is known as Percival",
					"I believe he is a knight of the round table");
				say(player, n, "I shall go and see if I can find him");
			}
		}
		else if (n.getID() == NpcId.KING_PERCIVAL.id()) {
			npcsay(player,
				n,
				"You missed all the excitement",
				"I got here and agreed to take over duties as king here",
				"Then before my eyes the most miraculous changes occured here",
				"Grass and trees were growing outside before our very eyes",
				"Thankyou very much for showing me the way home");
		}
		else if (n.getID() == NpcId.HAPPY_PEASANT.id()) {
			npcsay(player, n, "Oh happy day",
				"suddenly our crops are growing again",
				"It'll be a bumper harvest this year");
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == 117 || obj.getID() == 116;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 117) {
			if (player.getQuestStage(this) >= 1
				&& atQuestStage(player, Quests.MERLINS_CRYSTAL, -1)
				|| player.getQuestStage(this) == -1) {
				doDoor(obj, player);
			} else {
				player.message("The door won't open");
			}
		}
		if (obj.getID() == 116) {
			player.message("You go through the door");
			doDoor(obj, player);
			if (player.getCarriedItems().getInventory().countId(ItemId.MAGIC_WHISTLE.id()) != 2
				&& (player.getQuestStage(Quests.THE_HOLY_GRAIL) >= 3 || player
				.getQuestStage(Quests.THE_HOLY_GRAIL) == -1)) {
				addobject(ItemId.MAGIC_WHISTLE.id(), 1, 204, 2440, player);
				addobject(ItemId.MAGIC_WHISTLE.id(), 1, 204, 2440, player);
			}
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.MAGIC_WHISTLE.id() || item.getCatalogId() == ItemId.BELL.id() || item.getCatalogId() == ItemId.MAGIC_GOLDEN_FEATHER.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.MAGIC_WHISTLE.id()) {
			if (player.getLocation().inBounds(490, 652, 491, 653)) { // SQUARE PLOT
				if (player.getQuestStage(this) == 5 || player.getQuestStage(this) == -1) {
					player.teleport(492, 18, false);
				} else {
					player.teleport(396, 18, false);
				}
			} else if (player.getLocation().inBounds(388, 4, 427, 40)) { // 1st
				// ISLAND
				player.teleport(490, 651, false);
			} else if (player.getLocation().inBounds(484, 4, 523, 40)
				|| player.getLocation().inBounds(511, 976, 519, 984)
				|| player.getLocation().inBounds(511, 1920, 518, 1925)) { // 2nd
				// ISLAND
				// -
				// 2nd
				// floor
				// -
				// top
				// floor
				// castle.
				player.teleport(490, 651, false);
			} else {
				mes("The whistle makes no noise");
				delay(3);
				mes("It will not work in this location");
				delay(3);
			}
		}
		else if (item.getCatalogId() == ItemId.BELL.id()) {
			player.message("Ting a ling a ling");
			if (player.getLocation().inBounds(411, 27, 425, 40)) {
				if (DataConversions.getRandom().nextBoolean()) {
					Npc maiden = ifnearvisnpc(player, NpcId.MAIDEN.id(), 5);
					if (maiden != null) {
						npcsay(player, maiden, "welcome to the grail castle",
							"you should come inside",
							"It's cold out there");
					}
				}
				player.message("Somehow you are now inside the castle");
				player.teleport(420, 35, false);
			}
		} //Prod sack = 328, 446
		else if (item.getCatalogId() == ItemId.MAGIC_GOLDEN_FEATHER.id()) {
			int x = player.getLocation().getX();
			int y = player.getLocation().getY();
			int sX = 328;
			int sY = 446;
			int pX = x - sX;
			int pY = y - sY;
			if (player.getQuestStage(this) == -1) {
				player.message("nothing interesting happens");
			} else if (Math.abs(pY) > Math.abs(pX) && y <= sY) {
				player.message("the feather points south");
			} else if (Math.abs(pX) > Math.abs(pY) && x > sX) {
				player.message("the feather points east");
			} else if (x < sX) {
				player.message("the feather points west");
			} else if (Math.abs(pY) > Math.abs(pX) && y >= sY) {
				player.message("the feather points north");
			} else {
				// TODO we may or may not need this.
			}
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.BLACK_KNIGHT_TITAN.id();
	}

	@Override
	public void onKillNpc(Player player, Npc npc) {
		if (npc.getID() == NpcId.BLACK_KNIGHT_TITAN.id()) {
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.EXCALIBUR.id())) {
				player.getWorld().registerItem(
					new GroundItem(player.getWorld(), ItemId.BIG_BONES.id(), npc.getX(), npc.getY(), 1, player));
				npc.remove();
				player.message("Well done you have defeated the black knight titan");
				player.teleport(414, 11, false);
			} else {
				// should remove the original black knight, make dialogue if there is some black
				// titan and add a new one
				Npc otherTitan = ifnearvisnpc(player, NpcId.BLACK_KNIGHT_TITAN.id(), 5);
				if (otherTitan == null) {
					Npc newTitan = addnpc(player.getWorld(), NpcId.BLACK_KNIGHT_TITAN.id(), 413, 11);
					npc.remove();
				} else {
					npc.teleport(413, 11);
					npc.getSkills().setLevel(Skill.HITS.id(), npc.getDef().hits);
					npcsay(player, otherTitan, "You can't defeat me little man",
						"I'm invincible!");
					npc.killed = false;
				}
				player.message("Maybe you need something more to beat the titan");
			}
		}
	}

	/**
	 * playerTalk(p,n, "You feel that the grail shouldn't be moved");
	 * playerTalk(p,n,
	 * "You must complete some task here before you are worthy");
	 */
	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.HOLY_GRAIL.id() && i.getX() == 418 && i.getY() == 1924) {
			mes("You feel that the grail shouldn't be moved");
			delay(3);
			mes("You must complete some task here before you are worthy");
			delay(3);
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return i.getID() == ItemId.HOLY_GRAIL.id() && i.getX() == 418 && i.getY() == 1924;
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 408;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 408) {
			if (player.getQuestStage(this) == 4) {
				mes("You hear muffled noises from the sack");
				delay(3);
				player.message("You open the sack");
				Npc percival = addnpc(player.getWorld(), NpcId.SIR_PERCIVAL.id(), 328, 446, (int)TimeUnit.SECONDS.toMillis(64));
				npcsay(player, percival, "Wow thankyou",
					"I could hardly breathe in there");
				int menu = multi(player, percival,
					"How did you end up in a sack?",
					"Come with me, I shall make you a king",
					"Your father wishes to speak to you");
				if (menu == 0) {
					npcsay(player,
						percival,
						"It's a little embarrassing really",
						"After going on a long and challenging quest",
						"to retrieve the boots of arkaneeses",
						"defeating many powerful enemies on the way",
						"I fell into a goblin trap",
						"I've been kept as a slave here for the last 3 months",
						"a day or so ago, they decided it was a fun game",
						"To put me in this sack",
						"Then they forgot about me",
						"I'm now very hungry and my bones feel very stiff");
					int menu2 = multi(player, percival,
						"Come with me, I shall make you a king",
						"Your father wishes to speak to you");
					if (menu2 == 0) {
						npcsay(player, percival, "What are you talking about?",
							"The king of where?");
						say(player, percival,
							"Your father is apparently someone called the fisher king");
						beHisHeir(player, percival);
					} else if (menu2 == 1) {
						npcsay(player, percival,
							"My father? you have spoken to him recently?");
						beHisHeir(player, percival);
					}
				} else if (menu == 1) {
					npcsay(player, percival, "What are you talking about?",
						"The king of where?");
					say(player, percival,
						"Your father is apparently someone called the fisher king");
					beHisHeir(player, percival);
				} else if (menu == 2) {
					npcsay(player, percival,
						"My father? you have spoken to him recently?");
					beHisHeir(player, percival);
				}
			} else {
				player.message("nothing interesting happens");
			}
		}
	}

	private void beHisHeir(Player player, Npc percival) {
		say(player, percival,
				"He is dying and wishes you to be his heir");
		npcsay(player, percival, "I have been told that before",
			"I have not been able to find that castle again though");
		say(player, percival,
			"Well I do have the means to get us there - a magic whistle");
		if (player.getCarriedItems().hasCatalogID(ItemId.MAGIC_WHISTLE.id(), Optional.of(false))) {
			mes("You give a whistle to Sir Percival");
			delay(3);
			mes("You tell sir Percival what to do with the whistle");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.MAGIC_WHISTLE.id()));
			npcsay(player, percival, "Ok I will see you there then");
			player.updateQuestStage(this, 5);
		} else {
			say(player, percival, "I will just go and get you one");
		}
	}
}
