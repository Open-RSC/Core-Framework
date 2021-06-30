package com.openrsc.server.plugins.authentic.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.impl.combat.AggroEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class BlackKnightsFortress implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	UseLocTrigger,
	OpBoundTrigger {
	/**
	 * GameObjects associated with this quest;
	 */
	private static final int LISTEN_GRILL = 148;
	private static final int DOOR_ENTRANCE = 38;
	private static final int HOLE = 154;

	private static final Point DOOR_LOCATION = Point.location(271, 441);
	private static final Point DOOR2_LOCATION = Point.location(275, 439);
	private static final Point DOOR3_LOCATION = Point.location(278, 443);

	@Override
	public int getQuestId() {
		return Quests.BLACK_KNIGHTS_FORTRESS;
	}

	@Override
	public String getQuestName() {
		return "Black knight's fortress";
	}

	@Override
	public int getQuestPoints() {
		return Quest.BLACK_KNIGHTS_FORTRESS.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Sir Amik hands you 2500 coins");
		give(player, ItemId.COINS.id(), 2500);
		player.message("Well done.You have completed the Black Knights fortress quest");
		final QuestReward reward = Quest.BLACK_KNIGHTS_FORTRESS.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		if (n.getID() == NpcId.SIR_AMIK_VARZE.id()) {
			handleSirAmikVarze(player, n);
		}
	}

	private void handleSirAmikVarze(final Player player, final Npc n) {
		switch (player.getQuestStage(this)) {
			case 0:
				npcsay(player, n, "I am the leader of the white knights of Falador",
					"Why do you seek my audience?");
				int menu = multi(player, n, "I seek a quest",
					"I don't I'm just looking around");
				if (menu == 0) {
					if (!player.getConfig().INFLUENCE_INSTEAD_QP && player.getQuestPoints() < 12) {
						npcsay(player, n,
							"Well I do have a task, but it is very dangerous",
							"and it's critical to us that no mistakes are made",
							"I couldn't possibly let an unexperienced quester like yourself go");
						player.message("You need 12 quest points to start this quest");
						return;
					}
					npcsay(player, n, "Well I need some spy work doing",
						"It's quite dangerous",
						"You will need to go into the Black Knight's fortress");
					int sub_menu = multi(player, n, "I laugh in the face of danger",
						"I go and cower in a corner at the first sign of danger");
					if (sub_menu == 0) {
						npcsay(player, n,
							"Well that's good",
							"Don't get too overconfident though",
							"You've come along just right actually",
							"All of my knights are known to the black knights already",
							"Subtlety isn't exactly our strong point");
						say(player, n, "So what needs doing?");
						npcsay(player, n,
							"Well the black knights have started making strange threats to us",
							"Demanding large amounts of money and land",
							"And threataning to invade Falador if we don't pay",
							"Now normally this wouldn't be a problem",
							"But they claim to have a powerful new secret weapon",
							"What I want you to do is get inside their fortress",
							"Find out what their secret weapon is",
							"And then sabotage it", "You will be well paid");
						say(player, n, "OK I'll give it a try");
						player.updateQuestStage(getQuestId(), 1);
					} else if (sub_menu == 1) {
						npcsay(player, n, "Err", "Well",
							"spy work does involve a little hiding in corners I suppose");
						int sub = multi(player, n,
							"Oh I suppose I'll give it a go then",
							"No I'm not convinced");

						if (sub == 0) {
							say(player, n, "So what needs doing?");
							npcsay(player, n,
								"Well the black knights have started making strange threats to us",
								"Demanding large amounts of money and land",
								"And threataning to invade Falador if we don't pay",
								"Now normally this wouldn't be a problem",
								"But they claim to have a powerful new secret weapon",
								"What I want you to do is get inside their fortress",
								"Find out what their secret weapon is",
								"And then sabotage it", "You will be well paid");
							say(player, n, "OK I'll give it a try");
							player.updateQuestStage(getQuestId(), 1);
						}
					}
				} else if (menu == 1) {
					npcsay(player, n, "Ok, don't break anything");
				}
				break;

			case 1:
				npcsay(player, n, "How's the mission going?");
				say(player, n,
					"I haven't managed to find what the secret weapon is yet.");
				break;
			case 2:
				npcsay(player, n, "How's the mission going?");

				say(player, n,
					"I've found out what the black knight's secret weapon is.",
					"It's a potion of invincibility.");

				npcsay(player, n,
					"That is bad news.",
					"If you can sabotage it somehow, you will be paid well.");

				break;
			case 3:
				say(player, n,
					"I have ruined the black knight's invincibilty potion.",
					"That should put a stop to your problem.");

				npcsay(player, n,
					"Yes we have just received a message from the black knights.",
					"Saying they withdraw their demands.",
					"Which confirms your story");

				say(player, n, "You said you were going to pay me");
				npcsay(player, n, "Yes that's right");
				player.sendQuestComplete(Quests.BLACK_KNIGHTS_FORTRESS);
				break;

			case -1:
				say(player, n, "Hello Sir Amik");
				npcsay(player, n, "Hello friend");
				break;
		}
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		switch (obj.getID()) {
			case HOLE:
				if (item.getCatalogId() == ItemId.CABBAGE.id() && player.getQuestStage(this) == 2) {
					if (player.getCarriedItems().remove(new Item(ItemId.CABBAGE.id())) != -1) {
						mes("You drop a cabbage down the hole.");
						delay(3);
						mes("The cabbage lands in the cauldron below.");
						delay(3);
						mes("The mixture in the cauldron starts to froth and bubble.");
						delay(3);
						mes("You hear the witch groan in dismay.");
						delay(3);
						say(player, null,
							"Right I think that's successfully sabotaged the secret weapon.");
						player.updateQuestStage(this, 3);
					}
				} else if (item.getCatalogId() == ItemId.SPECIAL_DEFENSE_CABBAGE.id() && player.getQuestStage(this) == 2) {
					mes("This is the wrong sort of cabbage!");
					delay(3);
					mes("You are meant to be hindering the witch.");
					delay(3);
					mes("Not helping her.");
					delay(3);
				} else {
					say(player, null, "Why would I want to do that?");
				}
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SIR_AMIK_VARZE.id();
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == HOLE;
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == LISTEN_GRILL;
	}

	@Override
	public void onOpLoc(final Player player, final GameObject obj, String command) {
		switch (obj.getID()) {
			case LISTEN_GRILL:
				if (player.getQuestStage(this) == 1) {
					Npc blackKnight = ifnearvisnpc(player, NpcId.BLACK_KNIGHT_FORTRESS.id(), 20);
					Npc witch = ifnearvisnpc(player, NpcId.WITCH_FORTRESS.id(), 20);
					Npc greldo = ifnearvisnpc(player, NpcId.GRELDO.id(), 20);
					if (witch == null || blackKnight == null || greldo == null) {
						return;
					}
					npcsay(player, blackKnight,
						"So how's the secret weapon coming along?");
					npcsay(player,
						witch,
						"The invincibility potion is almost ready",
						"It's taken me five years but it's almost ready",
						"Greldo the Goblin here",
						"Is just going to fetch the last ingredient for me",
						"It's a specially grown cabbage",
						"Grown by my cousin Helda who lives in Draynor Manor",
						"The soil there is slightly magical",
						"And it gives the cabbages slight magic properties",
						"Not to mention the trees",
						"Now remember Greldo only a Draynor Manor cabbage will do",
						"Don't get lazy and bring any old cabbage",
						"That would entirely wreck the potion");
					npcsay(player, greldo, "Yeth Mithreth");
					player.updateQuestStage(this, 2);
				} else {
					player.message("I can't hear much right now");
				}
				break;
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 38 && obj.getLocation().equals(DOOR_LOCATION)) {
			return true;
		}
		if (obj.getID() == 39 && obj.getLocation().equals(DOOR2_LOCATION)) {
			return true;
		}
		if (obj.getID() == 40 && obj.getLocation().equals(DOOR3_LOCATION)) {
			return true;
		}
		return false;
	}

	@Override
	public void onOpBound(final Player player, final GameObject obj, Integer click) {
		switch (obj.getID()) {
			case DOOR_ENTRANCE:
				if (obj.getLocation().equals(DOOR_LOCATION) && player.getX() <= 270) {
					if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.IRON_CHAIN_MAIL_BODY.id())
						&& player.getCarriedItems().getEquipment().hasEquipped(ItemId.MEDIUM_BRONZE_HELMET.id())) {
						doDoor(obj, player);
						player.teleport(271, 441, false);
					} else {
						final Npc guard = ifnearvisnpc(player, NpcId.GUARD_FORTRESS.id(), 20);
						if (guard != null) {
							npcsay(player, guard, "Heh you can't come in here",
								"This is a high security military installation");
							int option = multi(player, guard, "Yes but I work here", "Oh sorry", "So who does it belong to?");
							if (option == 0) {
								npcsay(player,
									guard,
									"Well this is the guards entrance",
									"And I might be new here",
									"But I can tell you're not a guard",
									"You're not even wearing proper guards uniform");
								int sub_menu = multi(player, guard, "Pleaasse let me in",
									"So what is this uniform?");
								if (sub_menu == 0) {
									npcsay(player, guard,
										"Go away, you're getting annoying");
								} else if (sub_menu == 1) {
									npcsay(player,
										guard,
										"Well you can see me wearing it",
										"It's iron chain mail and a medium bronze helmet");
								}
							} else if (option == 1) {
								npcsay(player, guard,
									"Don't let it happen again");
							} else if (option == 2) {
								npcsay(player, guard,
									"This fortress belongs to the order of black knights known as the Kinshra");
							}
						}
					}
				} else {
					doDoor(obj, player);
				}
				break;
			case 39:
				if (obj.getLocation().equals(DOOR2_LOCATION)
					&& player.getX() <= 274) {
					final Npc guard = ifnearvisnpc(player, NpcId.GUARD_FORTRESS.id(), 20);
					if (guard != null) {
						npcsay(player, guard,
							"I wouldn't go in there if I woz you",
							"Those black knights are in an important meeting",
							"They said they'd kill anyone who went in there");
						int option = multi(player, guard, "Ok I won't", "I don't care I'm going in anyway");
						if (option == 1) {
							doDoor(obj, player);
							Npc n = ifnearvisnpc(player, NpcId.BLACK_KNIGHT.id(), 7);
							if (n != null && !n.isChasing()) {
								n.setChasing(player);
								new AggroEvent(n.getWorld(), n, player);
							}
						}
					}
				} else if (obj.getLocation().equals(DOOR2_LOCATION) && player.getX() <= 275) {
					doDoor(obj, player);
				}
				break;
			case 40:
				if (obj.getLocation().equals(DOOR3_LOCATION)
						&& player.getY() <= 442) {
					Npc npc = ifnearvisnpc(player, 5, NpcId.BLACK_KNIGHT.id());
					int countNotAbleChase = 0;
					if (npc == null) {
						doDoor(obj, player);
					} else {
						if (!npc.isChasing()) {
							npc.setChasing(player);
							new AggroEvent(npc.getWorld(), npc, player);
						} else {
							doDoor(obj, player);
						}
					}
				}
				else if (obj.getLocation().equals(DOOR3_LOCATION) && player.getY() <= 443) {
					doDoor(obj, player);
				}
				break;
		}
	}
}
