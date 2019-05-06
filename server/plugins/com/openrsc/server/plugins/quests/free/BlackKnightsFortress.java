package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.event.rsc.impl.combat.AggroEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import java.util.Collection;

public class BlackKnightsFortress implements QuestInterface, TalkToNpcListener,
	ObjectActionListener, ObjectActionExecutiveListener,
	TalkToNpcExecutiveListener, InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener, WallObjectActionExecutiveListener,
	WallObjectActionListener {
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
		return Constants.Quests.BLACK_KNIGHTS_FORTRESS;
	}

	@Override
	public String getQuestName() {
		return "Black knight's fortress";
	}
	
	@Override
	public boolean isMembers() {
		return false;
	}
	
	@Override
	public void handleReward(Player p) {
		p.message("Sir Amik hands you 2500 coins");
		addItem(p, ItemId.COINS.id(), 2500);
		p.message("Well done. You have completed the Black Knights Fortress quest");
		incQuestReward(p, Quests.questData.get(Quests.BLACK_KNIGHTS_FORTRESS), true);
		p.message("@gre@You haved gained 3 quest points!");
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == NpcId.SIR_AMIK_VARZE.id()) {
			handleSirAmikVarze(p, n);
		}
	}

	private void handleSirAmikVarze(final Player p, final Npc n) {
		switch (p.getQuestStage(this)) {
			case 0:
				npcTalk(p, n, "I am the leader of the white knights of Falador",
					"Why do you seek my audience?");
				int menu = showMenu(p, n, "I seek a quest",
					"I don't I'm just looking around");
				if (menu == 0) {
					if (p.getQuestPoints() < 12) {
						npcTalk(p, n,
							"Well I do have a task, but it is very dangerous",
							"and it's critical to us that no mistakes are made",
							"I couldn't possibly let an unexperienced quester like yourself go");
						p.message("You need 12 quest points to start this quest");
						return;
					}
					npcTalk(p, n, "Well I need some spy work doing",
						"It's quite dangerous",
						"You will need to go into the Black Knight's fortress");
					int sub_menu = showMenu(p, n, "I laugh in the face of danger",
						"I go and cower in a corner at the first sign of danger");
					if (sub_menu == 0) {
						npcTalk(p, n,
							"Well that's good",
							"Don't get too overconfident though",
							"You've come along just right actually",
							"All of my knights are known to the black knights already",
							"Subtlety isn't exactly our strong point");
						playerTalk(p, n, "So, what needs doing?");
						npcTalk(p, n,
							"Well the black knights have started making strange threats to us",
							"Demanding large amounts of money and land",
							"And threataning to invade Falador if we don't pay",
							"Now normally this wouldn't be a problem",
							"But they claim to have a powerful new secret weapon",
							"What I want you to do is get inside their fortress",
							"Find out what their secret weapon is",
							"And then sabotage it", "You will be well paid");
						playerTalk(p, n, "Ok I'll give it a try");
						p.updateQuestStage(getQuestId(), 1);
					} else if (sub_menu == 1) {
						npcTalk(p, n, "Er", "well",
							"spy work does involve a little hiding in corners I suppose");
						int sub = showMenu(p, n,
							"Oh I suppose I'll give it a go then",
							"No I'm not convinced");

						if (sub == 0) {
							playerTalk(p, n, "So, what needs doing?");
							npcTalk(p, n,
								"Well the black knights have started making strange threats to us",
								"Demanding large amounts of money and land",
								"And threataning to invade Falador if we don't pay",
								"Now normally this wouldn't be a problem",
								"But they claim to have a powerful new secret weapon",
								"What I want you to do is get inside their fortress",
								"Find out what their secret weapon is",
								"And then sabotage it", "You will be well paid");
							playerTalk(p, n, "Ok I'll give it a try");
							p.updateQuestStage(getQuestId(), 1);
						}
					}
				} else if (menu == 1) {
					npcTalk(p, n, "Ok, don't break anything");
				}
				break;

			case 1:
				npcTalk(p, n, "How's the mission going?");
				playerTalk(p, n,
					"I haven't managed to find what the secret weapon is yet.");
				break;
			case 2:
				npcTalk(p, n, "How's the mission going?");

				playerTalk(p, n,
					"I have found out what the Black Knights' secret weapon is.",
					"It's a potion of invincibility.");

				npcTalk(p, n,
					"That is bad news.",
					"If you can sabotage it somehow, you will be paid well.");

				break;
			case 3:
				playerTalk(p, n,
					"I have ruined the black knight's invincibility potion.",
					"That should put a stop to your problem.");

				npcTalk(p, n,
					"Yes we have just recieved a message from the black knights.",
					"Saying they withdraw their demands.",
					"Which confirms your story");

				playerTalk(p, n, "You said you were going to pay me");
				npcTalk(p, n, "Yes that's right");
				p.sendQuestComplete(Quests.BLACK_KNIGHTS_FORTRESS);
				break;

			case -1:
				playerTalk(p, n, "Hello Sir Amik");
				npcTalk(p, n, "Hello friend");
				break;
		}
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
		switch (obj.getID()) {
			case HOLE:
				if (item.getID() == ItemId.CABBAGE.id() && player.getQuestStage(this) == 2) {
					if (removeItem(player, ItemId.CABBAGE.id(), 1)) {
						message(player,
							"You drop a cabbage down the hole.",
							"The cabbage lands in the cauldron below.",
							"The mixture in the cauldron starts to froth and bubble.",
							"You hear the witch groan in dismay.");
						playerTalk(player, null,
							"Right I think that's successfully sabotaged the secret weapon.");
						player.updateQuestStage(this, 3);
					}
				} else if (item.getID() == ItemId.SPECIAL_DEFENSE_CABBAGE.id() && player.getQuestStage(this) == 2) {
					message(player,
						"This is the wrong sort of cabbage!",
						"You are meant to be hindering the witch.",
						"Not helping her.");
				} else {
					playerTalk(player, null, "Why would I want to do that?");
				}
				break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.SIR_AMIK_VARZE.id();
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
									   Player player) {
		return obj.getID() == HOLE;
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
									 Player player) {
		return obj.getID() == LISTEN_GRILL;
	}

	@Override
	public void onObjectAction(final GameObject obj, String command,
							   final Player player) {
		switch (obj.getID()) {
			case LISTEN_GRILL:
				if (player.getQuestStage(this) == 1) {
					Npc blackKnight = getNearestNpc(player, NpcId.BLACK_KNIGHT_FORTRESS.id(), 20);
					Npc witch = getNearestNpc(player, NpcId.WITCH_FORTRESS.id(), 20);
					Npc greldo = getNearestNpc(player, NpcId.GRELDO.id(), 20);
					if (witch == null || blackKnight == null || greldo == null) {
						return;
					}
					npcTalk(player, blackKnight,
						"So how's the secret weapon coming along?");
					npcTalk(player,
						witch,
						"The invincibility potion is almost ready",
						"It's taken me five years but it's almost ready",
						"Greldo the Goblin here",
						"Is just going to fetch the last ingredient for me",
						"It's a specially grown cabbage",
						"Grown by my cousin Helda who lives in Draynor Manor",
						"The soil there is slightly magical",
						"And gives the cabbages slight magic properties",
						"Not to mention the trees",
						"Now remember Greldo only a Draynor Manor cabbage will do",
						"Don't get lazy and bring any old cabbage",
						"That would entirely wreck the potion");
					npcTalk(player, greldo, "Yeth Mithreth");
					player.updateQuestStage(this, 2);
				} else {
					player.message("I can't hear much right now");
				}
				break;
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
										 Player player) {
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
	public void onWallObjectAction(final GameObject obj, Integer click,
								   final Player player) {
		switch (obj.getID()) {
			case DOOR_ENTRANCE:
				if (obj.getLocation().equals(DOOR_LOCATION) && player.getX() <= 270) {
					if (player.getInventory().wielding(ItemId.IRON_CHAIN_MAIL_BODY.id())
						&& player.getInventory().wielding(ItemId.MEDIUM_BRONZE_HELMET.id())) {
						doDoor(obj, player);
						player.teleport(271, 441, false);
					} else {
						final Npc guard = getNearestNpc(player, NpcId.GUARD_FORTRESS.id(), 20);
						if (guard != null) {
							guard.resetPath();
							guard.face(player);
							player.face(guard);
							npcTalk(player, guard, "Heh, you can't come in here",
								"This is a high security military installation");
							int option = showMenu(player, guard, "Yes but I work here", "Oh sorry", "So who does it belong to?");
							if (option == 0) {
								npcTalk(player,
									guard,
									"Well this is the guards entrance",
									"And I might be new here",
									"But I can tell you're not a guard",
									"You're not even wearing proper guards uniform");
								int sub_menu = showMenu(player, guard, "Pleaasse let me in",
									"So what is this uniform?");
								if (sub_menu == 0) {
									npcTalk(player, guard,
										"Go away, you're getting annoying");
								} else if (sub_menu == 1) {
									npcTalk(player,
										guard,
										"Well you can see me wearing it",
										"It's iron chain mail and a medium bronze helmet");
								}
							} else if (option == 1) {
								npcTalk(player, guard,
									"Don't let it happen again");
							} else if (option == 2) {
								npcTalk(player, guard,
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
					final Npc guard = getNearestNpc(player, NpcId.GUARD_FORTRESS.id(), 20);
					if (guard != null) {
						guard.resetPath();
						guard.face(player);
						player.face(guard);
						npcTalk(player, guard,
							"I wouldn't go in there if I woz you",
							"Those black knights are in an important meeting",
							"They said they'd kill anyone who went in there");
						int option = showMenu(player, guard, "Ok I won't", "I don't care I'm going in anyway");
						if (option == 1) {
							doDoor(obj, player);
							Npc n = Functions.getNearestNpc(player, NpcId.BLACK_KNIGHT.id(), 7);
							if (!n.isChasing()) {
								n.setChasing(player);
								new AggroEvent(n, player);
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
					Collection<Npc> npcs = Functions.getNpcsInArea(player, 5, NpcId.BLACK_KNIGHT.id());
					int countNotAbleChase = 0;
					if (npcs.size() == 0) {
						doDoor(obj, player);
					} else {
						for (Npc n : npcs) {
							if (!n.isChasing()) {
								n.setChasing(player);
								new AggroEvent(n, player);
							} else {
								countNotAbleChase++;
							}
						}
						if (countNotAbleChase >= npcs.size()) {
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
