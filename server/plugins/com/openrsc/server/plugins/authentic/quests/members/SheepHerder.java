package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.RestartableDelayedEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.event.rsc.SingleTickEvent;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.Path;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.HashMap;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class SheepHerder implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	UseNpcTrigger,
	UseLocTrigger {

	private static final int GATE = 443;
	private static final int CATTLE_FURNACE = 444;

	private final int BASE_TICK = 640;

	private static final HashMap<Npc, RestartableDelayedEvent> npcEventMap = new HashMap<Npc, RestartableDelayedEvent>();

	@Override
	public int getQuestId() {
		return Quests.SHEEP_HERDER;
	}

	@Override
	public String getQuestName() {
		return "Sheep Herder (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.SHEEP_HERDER.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("well done, you have completed the Plaguesheep quest");
		final QuestReward reward = Quest.SHEEP_HERDER.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.COUNCILLOR_HALGRIVE.id() || n.getID() == NpcId.FARMER_BRUMTY.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() ==  NpcId.FARMER_BRUMTY.id()) {
			switch (player.getQuestStage(this)) {
				case 2:
					say(player, n, "hello");
					npcsay(player, n, "hello adventurer",
						"be careful rounding up those sheep",
						"i don't think they've wandered far",
						"but if you touch them you'll become infected as well",
						"there should be a cattle prod in the barn",
						"you can use it to herd up the sheep");
					break;
				case -1:
					say(player, n, "hello there", "i'm sorry about your sheep");
					npcsay(player, n, "that's ok, it had to be done",
						"i just hope none of my other livestock becomes infected");
					break;
			}
		}
		else if (n.getID() == NpcId.COUNCILLOR_HALGRIVE.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "how are you?");
					npcsay(player, n, "I've been better");
					// do not send over
					int menu = multi(player, n, false, "What's wrong?",
						"That's life for you");
					if (menu == 0) {
						say(player, n, "What's wrong?");
						npcsay(player, n, "a plague has spread over west ardounge",
							"apparently it's reasonably contained",
							"but four infected sheep have escaped",
							"they're roaming free in and around east ardounge",
							"the whole city could be infected in days",
							"i need someone to gather the sheep",
							"herd them into a safe enclosure",
							"then kill the sheep",
							"their remains will also need to be disposed of safely in a furnace");
						int menu2 = multi(player, n, false, "I can do that for you",
							"That's not a job for me");
						if (menu2 == 0) {
							say(player, n, "i can do that for you");
							npcsay(player,
								n,
								"good, the enclosure is to the north of the city",
								"On farmer Brumty's farm",
								"the four sheep should still be close to it",
								"before you go into the enclosure",
								"make sure you have protective clothing on",
								"otherwise you'll catch the plague");
							say(player, n, "where do I get protective clothing?");
							npcsay(player,
								n,
								"Doctor Orbon wears it when trying to save the infected",
								"you'll find him in the chapel",
								"take this poisoned animal feed",
								"give it to the four sheep and they'll peacefully fall asleep");
							mes("The councillor gives you some sheep poison");
							delay(3);
							give(player, ItemId.POISONED_ANIMAL_FEED.id(), 1);
							player.updateQuestStage(getQuestId(), 1);
						} else if (menu2 == 1) {
							say(player, n, "that's not a job for me");
							npcsay(player, n, "fair enough, it's not nice work");
						}
					} else if (menu == 1) {
						say(player, n, "that's life for you");
					}
					break;
				case 1:
					npcsay(player, n,
						"please find those four sheep as soon as you can",
						"every second counts");
					if (!player.getCarriedItems().hasCatalogID(ItemId.POISONED_ANIMAL_FEED.id(), Optional.empty())) {
						say(player, n, "Some more sheep poison might be useful");
						mes("The councillor gives you some more sheep poison");
						delay(3);
						give(player, ItemId.POISONED_ANIMAL_FEED.id(), 1);
					}
					break;
				case 2:
					npcsay(player, n,
						"have you managed to dispose of those four sheep?");
					if (player.getCache().hasKey("plagueremain1st")
						&& player.getCache().hasKey("plagueremain2nd")
						&& player.getCache().hasKey("plagueremain3th")
						&& player.getCache().hasKey("plagueremain4th")) {
						say(player, n, "yes i have");
						player.getCache().remove("plague1st");
						player.getCache().remove("plague2nd");
						player.getCache().remove("plague3th");
						player.getCache().remove("plague4th");
						player.getCache().remove("plagueremain1st");
						player.getCache().remove("plagueremain2nd");
						player.getCache().remove("plagueremain3th");
						player.getCache().remove("plagueremain4th");
						player.sendQuestComplete(Quests.SHEEP_HERDER);
						give(player, ItemId.COINS.id(), 3100);
						npcsay(player, n, "here take one hundred coins to cover the price of your protective clothing");
						mes("halgrive gives you 100 coins");
						delay(3);
						npcsay(player, n, "and another three thousand for your efforts");
						mes("halgrive gives you another 3000 coins");
						delay(3);
					} else {
						say(player, n, "erm not quite");
						npcsay(player, n, "not quite's not good enough",
							"all four sheep must be captured, slain and their remains burnt");
						say(player, n, "ok i'll get to it");
						if (!player.getCarriedItems().hasCatalogID(ItemId.POISONED_ANIMAL_FEED.id(), Optional.empty())) {
							say(player, n, "Some more sheep poison might be useful");
							player.message("The councillor gives you some more sheep poison");
							give(player, ItemId.POISONED_ANIMAL_FEED.id(), 1);
						}
					}
					break;
				case -1:
					say(player, n, "hello again halgrive");
					npcsay(player, n, "well hello again traveller", "how are you");
					say(player, n, "good thanks and yourself?");
					npcsay(player, n,
						"much better now i don't have to worry about those sheep");
					break;
			}
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == GATE;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == GATE) {
			if (wearingProtectiveClothing(player) || !wearingProtectiveClothing(player) && player.getX() == 589) {
				openGatey(obj, player);
				if (player.getX() <= 588) {
					player.teleport(589, 541, false);
				} else {
					player.teleport(588, 540, false);
				}
			} else {
				mes("this is a restricted area");
				delay(3);
				mes("you cannot enter without protective clothing");
				delay(3);
			}
		}

	}

	public boolean wearingProtectiveClothing(Player player) {
		return player.getCarriedItems().getEquipment().hasEquipped(ItemId.PROTECTIVE_JACKET.id())
				&& player.getCarriedItems().getEquipment().hasEquipped(ItemId.PROTECTIVE_TROUSERS.id());
	}

	public void handleGateSounds(Player player) {
        player.playSound("opendoor");

	   player.getWorld().getServer().getGameEventHandler().add(new SingleTickEvent(player.getWorld(), player, 5, "Sheep Herder Gate Sounds") {
           @Override
           public void action() {
               player.playSound("opendoor");
           }
       });
    }

	public void openGatey(GameObject object, Player player) {
		handleGateSounds(player);
		player.message("you open the gate and walk through");
		player.getWorld().replaceGameObject(object,
			new GameObject(object.getWorld(), object.getLocation(), 442,
				object.getDirection(), object.getType()));
		player.getWorld().delayedSpawnObject(object.getLoc(), 3000);
	}

	private void sheepYell(Player player) {
		delay();
		player.message("@yel@:Baaaaaaaaa!!!");
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return DataConversions.inArray(new int[] {NpcId.FIRST_PLAGUE_SHEEP.id(), NpcId.SECOND_PLAGUE_SHEEP.id(),
				NpcId.THIRD_PLAGUE_SHEEP.id(), NpcId.FOURTH_PLAGUE_SHEEP.id()}, npc.getID());
	}

	@Override
	public void onUseNpc(Player player, final Npc plagueSheep, Item item) {
		if (plagueSheep.getID() == NpcId.FIRST_PLAGUE_SHEEP.id() || plagueSheep.getID() == NpcId.SECOND_PLAGUE_SHEEP.id()
			|| plagueSheep.getID() == NpcId.THIRD_PLAGUE_SHEEP.id() || plagueSheep.getID() == NpcId.FOURTH_PLAGUE_SHEEP.id()) {
			if (item.getCatalogId() == ItemId.CATTLE_PROD.id()) {
				if ((player.getCarriedItems().getEquipment().hasEquipped(ItemId.PROTECTIVE_TROUSERS.id()) && player.getCarriedItems().getEquipment()
					.hasEquipped(ItemId.PROTECTIVE_JACKET.id()))
					&& player.getQuestStage(getQuestId()) != -1) {
					if (plagueSheep.getLocation().inBounds(589, 543, 592, 548)) {
						player.message("The sheep is already in the pen");
						return;
					}
					player.message("you nudge the sheep forward");

					RestartableDelayedEvent npcEvent = npcEventMap.get(plagueSheep);
					//nudging outside of pen resets the timer
					if (npcEvent == null) {
						npcEvent = new RestartableDelayedEvent(player.getWorld(), player, 2 * BASE_TICK, "Sheep Herder Nudge Sheep") {
							int timesRan = 0;

							@Override
							public void run() {
								if (timesRan > 60) {
									plagueSheep.remove();
									stop();
									npcEventMap.remove(plagueSheep);
								}
								timesRan++;
							}

							@Override
							public void reset() {
								timesRan = 0;
							}
						};
						npcEventMap.put(plagueSheep, npcEvent);
						player.getWorld().getServer().getGameEventHandler().add(npcEvent);
					} else {
						npcEvent.reset();
					}
					NPCLoc location = plagueSheep.getLoc();
					switch (NpcId.getById(plagueSheep.getID())) {
						case FIRST_PLAGUE_SHEEP:
							//minX:576, maxX: 599. minY: 534, maxY: 566
							if (player.getY() >= 563) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											moveNpc(plagueSheep, 580, 558);
											return invokeNextState(2);
										});
										addState(1, () -> {
											walkMob(plagueSheep, new Point(location.startX(), location.startY()));
											return null;
										});

									}
								});
							} else if (player.getY() >= 559) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											moveNpc(plagueSheep, 585, 553);
											return invokeNextState(2);
										});
										addState(1, () -> {
											walkMob(plagueSheep, new Point(582,555), new Point(location.startX(), location.startY()));
											return null;
										});

									}
								});
							} else if (player.getY() <= 558 && player.getY() > 542) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											moveNpc(plagueSheep, 594, 538);
											return invokeNextState(2);
										});
										addState(1, () -> {
											walkMob(plagueSheep, new Point(587, 538), new Point(578, 546), new Point(location.startX(), location.startY()));
											return null;
										});

									}
								});
							} else if (player.getY() < 543) {
								sheepYell(player);
								player.message("the sheep jumps the gate into the enclosure");
								moveNpc(plagueSheep, 590, 546);
								return;
							}
							player.message("the sheep runs to the north");
							sheepYell(player);
							break;
						case SECOND_PLAGUE_SHEEP:
							// minX:576, maxX: 599. minY: 534, maxY: 566
							if (player.getY() >= 559) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											moveNpc(plagueSheep, 585, 553);
											return invokeNextState(2);
										});
										addState(1, () -> {
											walkMob(plagueSheep, new Point(582,555), new Point(location.startX(), location.startY()));
											return null;
										});

									}
								});
							} else if (player.getY() <= 558 && player.getY() > 542) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											moveNpc(plagueSheep, 585, 553);//intentionally add bug for authenticity
											return invokeNextState(2);
										});
										addState(1, () -> {
											moveNpc(plagueSheep, 594, 538);
											return invokeNextState(2);
										});
										addState(2, () -> {
											walkMob(plagueSheep, new Point(587, 538), new Point(581, 542), new Point(location.startX(), location.startY()));
											return null;
										});

									}
								});
							} else if (player.getY() < 543) {
								sheepYell(player);
								player.message("the sheep jumps the gate into the enclosure");
								moveNpc(plagueSheep, 590, 546);
								return;
							}
							player.message("the sheep runs to the north");
							sheepYell(player);
							break;
						case THIRD_PLAGUE_SHEEP:
							//minx:570, maxX:624, minY:527, maxY: 566
							if (plagueSheep.getX() > 618) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the east");
											moveNpc(plagueSheep, 614, 531);
											return invokeNextState(2);
										});
										addState(1, () -> {
											walkMob(plagueSheep, new Point(location.startX(), location.startY()));
											return null;
										});

									}
								});

							} else if (plagueSheep.getX() < 619
								&& plagueSheep.getX() > 612) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the east");
											moveNpc(plagueSheep, 604, 531);
											return invokeNextState(2);
										});
										addState(1, () -> {
											walkMob(plagueSheep, new Point(614, 531), new Point(location.startX(), location.startY()));
											return null;
										});

									}
								});
							} else if (plagueSheep.getX() < 613
								&& plagueSheep.getX() > 602) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the east");
											moveNpc(plagueSheep, 594, 531);
											return invokeNextState(2);
										});
										addState(1, () -> {
											walkMob(plagueSheep, new Point(604, 531), new Point(614, 531), new Point(location.startX(), location.startY()));
											return null;
										});

									}
								});
							} else if (plagueSheep.getX() < 603
								&& plagueSheep.getX() > 592) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the east");
											moveNpc(plagueSheep, 584, 531);
											return invokeNextState(2);
										});
										addState(1, () -> {
											walkMob(plagueSheep, new Point(585,532), new Point(589,532), new Point(594, 531), new Point(604, 531), new Point(614, 531), new Point(location.startX(), location.startY()));
											return null;
										});

									}
								});
							} else if (plagueSheep.getX() < 593
								&& plagueSheep.getX() > 582) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the southeast");
											moveNpc(plagueSheep, 579, 543);
											return invokeNextState(2);
										});
										addState(1, () -> {
											walkMob(plagueSheep, new Point(589,532), new Point(594, 531), new Point(604, 531), new Point(614, 531), new Point(location.startX(), location.startY()));
											return null;
										});

									}
								});
							} else if (plagueSheep.getX() < 586) {
								sheepYell(player);
								player.message("the sheep jumps the gate into the enclosure");
								moveNpc(plagueSheep, 590, 546);
								return;
							}
							sheepYell(player);
							break;
						case FOURTH_PLAGUE_SHEEP:
							//change db values. minX:581, maxX: 604. minY: 536, maxY: 606
							if (plagueSheep.getX() == 603
								&& plagueSheep.getY() < 589) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the south");
											moveNpc(plagueSheep, 603, 595);
											return invokeNextState(2);
										});
										addState(1, () -> {
//											plagueSheep.walkToEntityAStar(603, 595);
											walkMob(plagueSheep, new Point(location.startX(), location.startY()));
											return null;
										});

									}
								});
							} else if (plagueSheep.getY() > 589
								&& plagueSheep.getY() < 599) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the southeast");
											moveNpc(plagueSheep, 591, 603);
											return invokeNextState(2);
										});
										addState(1, () -> {
//											plagueSheep.walkToEntityAStar(603, 595);
											walkMob(plagueSheep, new Point(596, 603), new Point(598, 599), new Point(location.startX(), location.startY()));
											return null;
										});

									}
								});
								//walkMob(plagueSheep, new Point(596, 603), new Point(598, 599));
							} else if (plagueSheep.getY() > 598
								&& plagueSheep.getY() < 604) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs over the river to the northeast");
											moveNpc(plagueSheep, 587, 596);
											return invokeNextState(2);
										});
										addState(1, () -> {
//											plagueSheep.walkToEntityAStar(591, 603);
											walkMob(plagueSheep, new Point(589, 595), new Point(593, 595), new Point(595, 587));
											return null;
										});

									}
								});
								//walkMob(plagueSheep, new Point(589, 595), new Point(593, 595), new Point(595, 587));
							} else if (plagueSheep.getY() > 583
								&& plagueSheep.getY() < 588) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the north");
											moveNpc(plagueSheep, 588, 578);
											return invokeNextState(2);
										});
										addState(1, () -> {
//											plagueSheep.walkToEntityAStar(587, 596);
											walkMob(plagueSheep, new Point(594, 584), new Point(594, 586));
											return null;
										});

									}
								});
								//walkMob(plagueSheep, new Point(594, 584), new Point(594, 586));
							} else if (plagueSheep.getY() > 575
								&& plagueSheep.getY() < 585) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the north");
											moveNpc(plagueSheep, 588, 570);
											return invokeNextState(2);
										});
										addState(1, () -> {
//											plagueSheep.walkToEntityAStar(588, 578);
											walkMob(plagueSheep, new Point(594, 578), new Point(595, 578));
											return null;
										});

									}
								});
								//walkMob(plagueSheep, new Point(594, 578), new Point(595, 578));
							}  else if (plagueSheep.getY() > 567
								&& plagueSheep.getY() < 576) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the northeast");
											moveNpc(plagueSheep, 589, 562);
											return invokeNextState(2);
										});
										addState(1, () -> {
//											plagueSheep.walkToEntityAStar(588, 570);
											walkMob(plagueSheep, new Point(594, 567), new Point(595, 567));
											return null;
										});

									}
								});
								//walkMob(plagueSheep, new Point(594, 567), new Point(595, 567));
							} else if (plagueSheep.getY() > 565
								&& plagueSheep.getY() < 568) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the northeast");
											moveNpc(plagueSheep, 587, 552);
											return invokeNextState(2);
										});
										addState(1, () -> {
//											plagueSheep.walkToEntityAStar(589, 562);
											walkMob(plagueSheep, new Point(596, 567));
											return null;
										});

									}
								});
								//walkMob(plagueSheep, new Point(596, 567));
							} else if (plagueSheep.getY() > 551
								&& plagueSheep.getY() < 562) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the northeast");
											moveNpc(plagueSheep, 586, 547);
											return invokeNextState(2);
										});
										addState(1, () -> {
											walkMob(plagueSheep, new Point(598, 557));
											return null;
										});

									}
								});
							} else if (plagueSheep.getY() > 547
								&& plagueSheep.getY() < 552) {
								player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Plague Sheep") {
									public void init() {
										addState(0, () -> {
											player.message("the sheep runs to the northeast");
											moveNpc(plagueSheep, 586, 539);
											return invokeNextState(2);
										});
										addState(1, () -> {
//											plagueSheep.walkToEntityAStar(586, 547);
											walkMob(plagueSheep, new Point(588, 549));
											return null;
										});

									}
								});
								//walkMob(plagueSheep, new Point(588, 549));
							} else if (plagueSheep.getY() <= 547) {
								sheepYell(player);
								player.message("the sheep jumps the gate into the enclosure");
								moveNpc(plagueSheep, 590, 546);
								return;
							}
							sheepYell(player);
							break;
						default:
							break;

					}
				} else {
					mes("this sheep has the plague");
					delay(3);
					mes("you better not touch it");
					delay(3);
				}
			}
			else if (item.getCatalogId() == ItemId.POISONED_ANIMAL_FEED.id()) {
				if (plagueSheep.getLocation().inBounds(589, 543, 592, 548)) {
					if (plagueSheep.getID() == NpcId.FIRST_PLAGUE_SHEEP.id()) {
						if (player.getCache().hasKey("plagueremain1st")) {
							mes("You have already disposed of this sheep");
							delay(3);
							mes("Find a different sheep");
							delay(3);
							return;
						}
					} else if (plagueSheep.getID() == NpcId.SECOND_PLAGUE_SHEEP.id()) {
						if (player.getCache().hasKey("plagueremain2nd")) {
							mes("You have already disposed of this sheep");
							delay(3);
							mes("Find a different sheep");
							delay(3);
							return;
						}
					} else if (plagueSheep.getID() == NpcId.THIRD_PLAGUE_SHEEP.id()) {
						if (player.getCache().hasKey("plagueremain3th")) {
							mes("You have already disposed of this sheep");
							delay(3);
							mes("Find a different sheep");
							delay(3);
							return;
						}
					} else if (plagueSheep.getID() == NpcId.FOURTH_PLAGUE_SHEEP.id()) {
						if (player.getCache().hasKey("plagueremain4th")) {
							mes("You have already disposed of this sheep");
							delay(3);
							mes("Find a different sheep");
							delay(3);
							return;
						}
					}
					mes("you give the sheep poisoned sheep feed");
					delay(3);
					player.message("the sheep collapses to the floor and dies");
					plagueSheep.killedBy(player);
				} else {
					mes("you can't kill the sheep out here");
					delay(3);
					mes("you might spread the plague");
					delay(3);
				}
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == CATTLE_FURNACE;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == CATTLE_FURNACE) {
			if (DataConversions.inArray(new int[]{ItemId.PLAGUED_SHEEP_REMAINS_1.id(), ItemId.PLAGUED_SHEEP_REMAINS_2.id(),
					ItemId.PLAGUED_SHEEP_REMAINS_3.id(), ItemId.PLAGUED_SHEEP_REMAINS_4.id()}, item.getCatalogId())) {
				if (player.getQuestStage(this) != -1) {
					if (item.getCatalogId() == ItemId.PLAGUED_SHEEP_REMAINS_1.id()) {
						if (!player.getCache().hasKey("plagueremain1st")) {
							player.getCache().store("plagueremain1st", true);
							player.getCarriedItems().remove(new Item(ItemId.PLAGUED_SHEEP_REMAINS_1.id()));
						} else {
							mes("You need to kill this sheep yourself");
							delay(3);
							return;
						}
					} else if (item.getCatalogId() == ItemId.PLAGUED_SHEEP_REMAINS_2.id()) {
						if (!player.getCache().hasKey("plagueremain2nd")) {
							player.getCache().store("plagueremain2nd", true);
							player.getCarriedItems().remove(new Item(ItemId.PLAGUED_SHEEP_REMAINS_2.id()));
						} else {
							mes("You need to kill this sheep yourself");
							delay(3);
							return;
						}
					} else if (item.getCatalogId() == ItemId.PLAGUED_SHEEP_REMAINS_3.id()) {
						if (!player.getCache().hasKey("plagueremain3th")) {
							player.getCache().store("plagueremain3th", true);
							player.getCarriedItems().remove(new Item(ItemId.PLAGUED_SHEEP_REMAINS_3.id()));
						} else {
							mes("You need to kill this sheep yourself");
							delay(3);
							return;
						}
					} else if (item.getCatalogId() == ItemId.PLAGUED_SHEEP_REMAINS_4.id()) {
						if (!player.getCache().hasKey("plagueremain4th")) {
							player.getCache().store("plagueremain4th", true);
							player.getCarriedItems().remove(new Item(ItemId.PLAGUED_SHEEP_REMAINS_4.id()));
						} else {
							mes("You need to kill this sheep yourself");
							delay(3);
							return;
						}
					}
					mes("you put the sheep remains in the furnace");
					delay(3);
					mes("the remains burn to dust");
					delay(3);
				} else {
					mes("You have already completed this quest");
					delay(3);
				}
			} else {
				mes("Nothing interesting happens");
				delay(3);
			}
		}
	}

	public static void moveNpc(Mob n, int x, int y) {
		n.resetPath();
		n.setLocation(new Point(x, y), true);
	}

	public static void walkMob(Mob n, Point... waypoints) {
		n.getWorld().getServer().getGameEventHandler().submit(() -> {
			n.resetPath();
			Path path = new Path(n, Path.PathType.WALK_TO_POINT);
			for (Point point : waypoints) {
				path.addStep(point.getX(), point.getY());
			}
			path.finish();
			n.getWalkingQueue().setPath(path);
		}, "Walk Mob");
	}
}
