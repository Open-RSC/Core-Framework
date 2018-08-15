package org.openrsc.server.packethandler;
import org.openrsc.server.Config;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ExploitLog;
import org.openrsc.server.logging.model.eventLog;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;
import org.openrsc.server.states.Action;
import org.apache.mina.common.IoSession;

import java.util.Arrays;
import java.util.Random;

import org.openrsc.server.entityhandling.defs.extras.*;
import org.openrsc.server.event.*;
import org.openrsc.server.model.*;

public class InvUseOnObject implements PacketHandler {

	int bird_seeds = 0;
	int bucket = 0;

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			int pID = ((RSCPacket)p).getID();
			if (player.isBusy()) {
				player.resetPath();
				return;
			}
			player.resetAllExceptDMing();
			final int x = p.readShort();
			final int y = p.readShort();
			GameObject object = World.getZone(x, y).getObjectAt(x, y);
			GameObject door = World.getZone(x, y).getDoorAt(x, y);
			InvItem item;
			switch (pID) {
			case 63: // Use Item on Door
				int dir = p.readByte();
				item = player.getInventory().get(p.readShort());
				if (door == null || door.getType() == 0 || item == null) {
					Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "InvUseOnObject (1)", DataConversions.getTimeStamp()));
					return;
				}
				handleDoor(player, x, y, door, dir, item);
				break;
			case 58: // Use Item on GameObject
				item = player.getInventory().get(p.readShort());
				final int batch = p.readShort();
				if (object == null || object.getType() == 1 || item == null) {
					Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "InvUseOnObject (2)", DataConversions.getTimeStamp()));
					return;
				}
				handleObject(player, x, y, object, item, batch);
				break;
			}
		}

	}

	private void handleObject(final Player player, int x, int y, final GameObject object, final InvItem item, final int batch) {
		player.setStatus(Action.USING_INVITEM_ON_OBJECT);
		World.getDelayedEventHandler().add(new WalkToObjectEvent(player, object, false) {
			public void arrived() {
				owner.resetPath();
				if (owner.isBusy() || owner.isRanging() || !owner.getInventory().contains(item) || !owner.nextTo(object) || !World.entityExists(object) || owner.getStatus() != Action.USING_INVITEM_ON_OBJECT)
					return;
				owner.resetAllExceptDMing();
				String[] options;

				switch (object.getID()) {
				case 294:
					if (item.getID() == 603)
					{
						owner.sendMessage("You spray the bee hive with insect repellant");
						owner.sendMessage("The bees fly away");
						if (bucket == 0)
							bucket++;

					}
					else
						if (item.getID() == 21 && bucket > 0)
						{
							owner.sendMessage("You obtain some wax");
							owner.getInventory().add(605, 1);
							owner.getInventory().remove(21, 1);
							bucket--;
							owner.sendMessage("The bees seem to have come back");
							owner.sendInventory();
						}
						else
						{
							owner.sendMessage("Maybe I should use some insect repellant first");
							return;
						}

					break;

				case 447:  //Soil for Plague City
					if(item.getID() == 50) {
						owner.setBusy(true);
						Quest plagueCity1 = owner.getQuest(Quests.PLAGUE_CITY);
						if(plagueCity1 != null) {
							if(plagueCity1.getStage() == 2) {
								owner.sendMessage("you pour the water onto the soil");
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
									public void action() {
										owner.sendMessage("the soil softens slightly");
										owner.setBucketsUsedOnSoil(owner.getBucketsUsedOnSoil() + 1);
										owner.getInventory().remove(50, 1);
										owner.getInventory().add(21, 1);
										owner.sendInventory();
										if(owner.getBucketsUsedOnSoil() == 4) {
											World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
												public void action() {
													owner.sendMessage("the soil is soft enough to dig into");
													owner.incQuestCompletionStage(Quests.PLAGUE_CITY);
													owner.setBusy(false);
												}
											});
										} else {
											owner.setBusy(false);
										}
									}
								});

							} else {
								owner.sendMessage("Nothing interesting happens");
								owner.setBusy(false);
							}
						} else {
							owner.sendMessage("Nothing interesting happens");
							owner.setBusy(false);
						}
					} else if(item.getID() == 211) {
						owner.setBusy(true);
						Quest plagueCity1 = owner.getQuest(Quests.PLAGUE_CITY);
						if(plagueCity1 != null) {
							if(owner.getQuest(Quests.PLAGUE_CITY).getStage() >= 3) {
								World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"you dig deep into the soft soil", "Suddenly it crumbles away", "you fall through", "and land in the sewer", "Edmond follows you down the hole"}, 2500) {
									public void finished() {
										if (plagueCity1.getStage() >= 4)
										{
											owner.teleport(621, 3415);
											owner.setBusy(false);
										}
										else
										{
											owner.teleport(621, 3415);
											owner.setBusy(false);
											owner.incQuestCompletionStage(Quests.PLAGUE_CITY);
										}
									}
								});
							} else {
								owner.setBusy(false);
								owner.sendMessage("The soil is too hard to dig through");
							}
						} else {
							owner.setBusy(false);
							owner.sendMessage("The soil is too hard to dig through");
						}
					} else {
						owner.sendMessage("Nothing interesting happens");
					}
					break;

				case 502:
					if (item.getID() == 801)
					{
						Quest Biohazard_Quest = owner.getQuest(Quests.BIOHAZARD);
						if (Biohazard_Quest.getStage() == 3)
						{
							World.getDelayedEventHandler().add(new DelayedGenericMessage(player, new String[] { "You place the rotten apples in the pot", "They quickly dissolve in to the stew", "That wasn't very nice" }, 2000)
							{
								public void finished() 
								{
									owner.incQuestCompletionStage(Quests.BIOHAZARD);
									owner.getInventory().remove(801, 1);
									owner.sendInventory();
									owner.setBusy(false);
								}
							});
						}
						else
						{
							owner.sendMessage("I think there is enough in there");
							owner.setBusy(false);
						}
					}
					break;

				case 494:		      				
					if (item.getID() == 800)
					{
						Quest Biohazard_Quest = owner.getQuest(Quests.BIOHAZARD);
						if (Biohazard_Quest.getStage() == 2 && player.getSeedsUsed() < 1)
						{
							owner.setBusy(true);
							World.getDelayedEventHandler().add(new DelayedGenericMessage(player, new String[] { "You throw a handful of seeds onto the watch tower", "The mourners do not seem to notice" }, 2000)
							{
								public void finished() 
								{
									owner.setSeedsUsed(owner.getSeedsUsed() + 1);
									owner.getInventory().remove(800, 1);
									owner.sendInventory();
									owner.setBusy(false);
								}
							});
						}
						else
						{
							owner.sendMessage("Nothing interesting happens.");
							owner.setBusy(false);
							return;
						}
					}
					break;

				case 457:
					if (item.getID() == 780)
					{
						World.registerEntity(new GameObject(object.getLocation(), 181, object.getDirection(), object.getType()));
						World.delayedSpawnObject(object.getLoc(), 1000);
						owner.teleport(637, 3448);
						owner.sendMessage("You go through the gate.");
					}
					break;

				case 449: // Plague City, use rope on the sewer.
					if (item.getID() == 237 && owner.getQuest(Quests.PLAGUE_CITY) != null && owner.getQuest(Quests.PLAGUE_CITY).getStage() == 4)
					{
						owner.sendMessage("You tie one end of the rope to the sewer pipe's grill and hold the other end in your hand.");
						owner.getInventory().remove(237, 1);
						owner.sendInventory();
						owner.incQuestCompletionStage(Quests.PLAGUE_CITY);
					}
					else
					{
						owner.sendMessage("Nothing interesting happens.");
						return;
					}
					break;

				case 350:
					/*
					 * Fishing Contest garlic use on pipe
					 */
					if (object.getX() == 569 && object.getY() == 488)
					{
						if(item.getID() == 218)
						{
							Quest Fishing_Contest = owner.getQuest(Quests.FISHING_CONTEST);
							switch(Fishing_Contest.getStage()) 
							{
							case 1:
								owner.incQuestCompletionStage(Quests.FISHING_CONTEST);
								owner.sendMessage("You place the garlic inside the pipe");
								owner.getInventory().remove(218, 1);
								owner.sendInventory();
								break;

							default:
								owner.sendMessage("Nothing interesting happens.");
							}
						}
					}
					break;

				case 355: // Fishing Contest red vine worms.
				case 219:
				case 220:
					if (item.getID() == 211)
					{
						owner.sendMessage("You dig up some red vine worms from the vine.");
						owner.getInventory().add(715, 3);
						owner.sendInventory();
					}
					break;

				case 226:
				case 232:
					if(item.getID() == 410) {
						if(owner.getInventory().countId(419) > 3) {
							if(owner.getInventory().countId(168) > 0) {
								owner.sendMessage("You nail the plank across the hole");
								owner.getInventory().remove(new InvItem(419, 4));
								owner.getInventory().remove(new InvItem(410, 1));
								owner.sendInventory();
								owner.applyShipPatch(object.getID());
							} else {
								owner.sendMessage("I need a hammer to drive in the nails");
							}
						} else {
							owner.sendMessage("I need nails to repair this");
						}
					} else {
						owner.sendMessage("Nothing interesting happens");
					}
					break;

				case 504:
					Quest Biohazard_Quest = owner.getQuest(Quests.BIOHAZARD);
					if (item.getID() == 803 && Biohazard_Quest.getStage() >= 4)
					{
						World.registerEntity(new GameObject(object.getLocation(), 504, object.getDirection(), object.getType()));
						World.delayedSpawnObject(object.getLoc(), 1000);
						owner.sendMessage("You go through the gate");
						owner.teleport(630, 1514);
					}
					else
					{
						owner.sendMessage("The gate appears to be locked");
						return;
					}
					break;

					/*case 1119: //Cannon base
		      				if(item.getID() == 1034 || item.getID() == 1035) {
		      					owner.sendMessage("These parts don't fit together");
		      				} else if(item.getID() == 1033) {
		      					if(owner.getCannonX() != object.getX() || owner.getCannonY() != object.getY()) {
		      						owner.sendMessage("This isn't your cannon!");
		      					} else {
		      						owner.getInventory().remove(new InvItem(1033));
		      						owner.sendInventory();
		      						World.unregisterGameObject(object);
		      						World.registerGameObject(new GameObject(player.getCannonX(), player.getCannonY(), 1120, 0, 0));
		      						owner.updateCannonStage(1);
		      						owner.sendMessage("you add the stand");
		      					}
		      				} else {
		      					owner.sendMessage("Nothing interesting happens");
		      				}
		      				break;
		      			case 1120:
		      				if(item.getID() == 1035) {
		      					owner.sendMessage("These parts don't fit together");
		      				} else if(item.getID() == 1034) {
		      					if(owner.getCannonX() != object.getX() || owner.getCannonY() != object.getY()) {
		      						owner.sendMessage("This isn't your cannon!");
		      					} else {
		      						owner.getInventory().remove(new InvItem(1034));
		      						owner.sendInventory();
		      						World.unregisterGameObject(object);
		      						World.registerObject(new GameObject(player.getCannonX(), player.getCannonY(), 1121, 0, 0));
		      						owner.updateCannonStage(2);
		      						owner.sendMessage("you add the barrels");
		      					}
		      				} else {
		      					owner.sendMessage("Nothing interesting happens");
		      				}
		      				break;
		      			case 1121:
		      				if(item.getID() == 1035) {
		      					if(owner.getCannonX() != object.getX() || owner.getCannonY() != object.getY()) {
		      						owner.sendMessage("This isn't your cannon!");
		      					} else {
		      						owner.getInventory().remove(new InvItem(1035));
		      						owner.sendInventory();
		      						World.unregisterGameObject(object);
		      						World.registerObject(new GameObject(player.getCannonX(), player.getCannonY(), 1118, 0, 0));
		      						owner.updateCannonStage(3);
		      						owner.sendMessage("you add the furnace");
		      					}
		      				} else {
		      					owner.sendMessage("Nothing interesting happens");
		      				}
		      				break;*/
				case 134:	//Draynor Manor Compost Heap
					if (item.getID() == 211)
					{
						owner.setBusy(true);
						owner.sendMessage("You dig through the compost heap.");
						World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
							public void action() {
								Quest q = owner.getQuest(Quests.ERNEST_THE_CHICKEN);
								if(q != null) 
								{
									if(q.getStage() > 0 && !q.finished() && !owner.getInventory().contains(212)) 
									{
										owner.sendMessage("You find a small key");
										owner.getInventory().add(new InvItem(212, 1));
										owner.sendInventory();
									} 
									else 
									{
										owner.sendMessage("You find nothing of interest");
									}
								} 
								else 
								{
									owner.sendMessage("You find nothing of interest");
								}
							}
						});
						owner.setBusy(false);
					}
					break;

				case 77: //Drainpipe containing Silverlight Key 3
					if (item.getID() == 50) {
						if (owner.getQuestCompletionStage(Quests.DEMON_SLAYER) == 2) {
							if (!owner.getInventory().contains(new InvItem(51, 1))) {
								owner.setBusy(true);
								owner.getInventory().remove(new InvItem(50, 1));
								owner.getInventory().add(new InvItem(21, 1));
								owner.sendInventory();
								owner.sendMessage("You pour the liquid down the drain");
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1250) {
									public void action() {
										owner.sendMessage("Ok I think I've washed the key down into the sewer");
										World.getDelayedEventHandler().add(new SingleEvent(owner, 1250) {
											public void action() {
												owner.sendMessage("I'd better go down and get it before someone else finds it");
												owner.setBusy(false);
												World.registerEntity(new Item(51, 117, 3295, 1, owner));//, 59000));
											}
										});
									}
								});
							} else
								owner.sendMessage("I already have Sir Prysin's key");
						} else
							owner.sendMessage("I have no reason to do that");
					} else
						owner.sendMessage("Nothing interesting happens");
					break;
				case 40: //Lumbridge Coffin [QUEST]
					if (item.getID() == 412) {
						Quest q = owner.getQuest(Quests.THE_RESTLESS_GHOST);
						if (q != null) {
							if (q.getStage() == 2 && !q.finished()) {
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
									public void action() {
										owner.sendMessage("You put the skull in the coffin");
										owner.getInventory().remove(new InvItem(412, 1));
										owner.sendInventory();
										World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
											public void action() {
												owner.sendMessage("The ghost vanishes");
												Npc ghost = World.getNpc(15, 103, 110, 670, 676);
												if (ghost != null) {
													World.unregisterEntity(ghost);
													ghost.remove();
												}
												World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
													public void action() {
														owner.sendMessage("You think you hear a faint voice in the air");
														World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
															public void action() {
																owner.sendMessage("Thank you");
																World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
																	public void action() {
																		owner.finishQuest(Quests.THE_RESTLESS_GHOST);
																		owner.sendMessage("You have completed the restless ghost quest");
																		owner.incQuestExp(Skills.PRAYER, 562);
																		owner.sendStat(5);
																		owner.sendMessage("You have gained @gre@1@whi@ quest point!");
																		Logger.log(new eventLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), DataConversions.getTimeStamp(), "<strong>" + owner.getUsername() + "</strong>" + " has completed the <span class=\"recent_quest\">Restless Ghost</span> quest!"));
																	}
																});
															}
														});
													}
												});
											}
										});
									}
								});
							} else
								owner.sendMessage("Nothing interesting happens");
						} else
							owner.sendMessage("Nothing interesting happens");
					} else
						owner.sendMessage("Nothing interesting happens");
					break;
				case 52: // Grain On Hopper (Draynor)
					if (item.getID() == 29) { // Wheat
						if (!owner.isGrainInDraynorHopper()) {
							owner.sendMessage("You put the grain in the hopper");
							owner.getInventory().remove(new InvItem(29, 1));
							owner.sendInventory();
							owner.grainInDraynorHopper(true);
						} else
							owner.sendMessage("There is already grain in the hopper.");
					} else
						owner.sendMessage("Nothing interesting happens");
					break;
				case 173: // Grain On Hopper (Cooking Guild)
					if(item.getID() == 29) { // Wheat
						if (!owner.isGrainInCookingGuildHopper()) {
							owner.sendMessage("You put the grain in the hopper");
							owner.getInventory().remove(new InvItem(29, 1));
							owner.sendInventory();
							owner.grainInCookingGuildHopper(true);
						} else
							owner.sendMessage("There is already grain in the hopper.");
					} else
						owner.sendMessage("Nothing interesting happens");
					break;
				case 154: // Cabbage down the hole in Black Knight's Fortress [QUEST]
					if (item.getID() == 18 && owner.getQuestCompletionStage(Quests.BLACK_KNIGHTS_FORTRESS) == 2) {
						owner.getInventory().remove(new InvItem(18, 1));
						owner.sendInventory();
						owner.sendMessage("You drop a cabbage down the hold.");
						World.getDelayedEventHandler().add(new MiniEvent(owner) {
							public void action() {
								owner.sendMessage("The cabbage lands in the cauldron below.");
								World.getDelayedEventHandler().add(new MiniEvent(owner) {
									public void action() {
										owner.sendMessage("The mixture in the cauldron starts to froth and bubble.");
										World.getDelayedEventHandler().add(new MiniEvent(owner) {
											public void action() {
												owner.sendMessage("You hear the witch groan in dismay.");
												World.getDelayedEventHandler().add(new MiniEvent(owner) {
													public void action() {
														for (Player informee : owner.getViewArea().getPlayersInView())
															informee.informOfChatMessage(new ChatMessage(owner, "Right I think that's successfully sabotaged the secret weapon.", owner));
														owner.incQuestCompletionStage(Quests.BLACK_KNIGHTS_FORTRESS);																
													}
												});
											}
										});
									}
								});
							}
						});
					}
					break;
				case 462: //leafless tree 1
					if (item.getID() == 237) {
						owner.setBusy(true);
						owner.sendMessage("You tie the rope off to the tree...");
						World.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								owner.setBusy(false);
								owner.teleport(662,467,false);
								owner.sendMessage("and land on the next island.");
							}
						});
					}
					break;

				case 463: //leafless tree 2
					if (item.getID() == 237) {
						owner.setBusy(true);
						owner.sendMessage("You tie the rope off to the tree...");
						World.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								owner.setBusy(false);
								owner.teleport(659,471,false);
								owner.sendMessage("and land on the next island.");
							}
						});
					}
					break;

				case 482: //leafless tree 3
					if (item.getID() == 237) {
						owner.setBusy(true);
						owner.sendMessage("You tie the rope off to the tree...");
						World.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								owner.setBusy(false);
								owner.teleport(659, 3304,false);
								owner.sendMessage("and rappel down to the waterfall below.");
							}
						});
					}
					break;

				case 282: // Fountain of Heroes
					if (item.getID() == 522) {
						owner.sendMessage("You dip the amulet in the fountain...");
						owner.setBusy(true);
						World.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								owner.sendMessage("you can now rub this amulet to teleport");
								World.getDelayedEventHandler().add(new ShortEvent(owner) {
									public void action() {
										World.getDelayedEventHandler().add(new ShortEvent(owner) {
											public void action() {
												owner.sendMessage("Though using it too much means you will need to recharge it");
												World.getDelayedEventHandler().add(new ShortEvent(owner) {
													public void action() {
														if (owner.getInventory().remove(item) > -1) {
															owner.sendMessage("It now also means you can find more gems while mining");
															owner.getInventory().add(new InvItem(597));
															owner.sendInventory();
														}
														owner.setBusy(false);
													}
												});
											}
										});
									}
								});
							}
						});
						break;
					}
				case 2: // Well
				case 466: // Well
				case 814: // Well
				case 48: // Sink
				case 26: // Fountain
				case 1130: // Fountain
					if (item.getID() == 341) 
					{
						owner.sendSound("filljug", false);
						showBubble();
						owner.sendMessage("You fill the bowl from the " + object.getGameObjectDef().getName());
						owner.getInventory().remove(new InvItem(341, 1));
						owner.getInventory().add(new InvItem(342, 1));
						owner.sendInventory();
						break;
					}
					if (!itemId(new int[]{21, 140, 465}) && !itemId(Formulae.potionsUnfinished) && !itemId(Formulae.potions1Dose) && !itemId(Formulae.potions2Dose) && !itemId(Formulae.potions3Dose)) {
						owner.sendMessage("Nothing interesting happens");
						return;
					}
					if (owner.getInventory().remove(item) > -1) {
						showBubble();
						owner.sendSound("filljug", false);
						switch(item.getID()) {
						case 21:
							owner.getInventory().add(new InvItem(50));
							break;
						case 140:
							owner.getInventory().add(new InvItem(141));
							break;
						default:
							owner.getInventory().add(new InvItem(464));
							break;
						}
						owner.sendInventory();
					}
					break;
				case 97: // Fire
				case 11:
				case 119: //Cook's range
					if(!owner.isQuestFinished(Quests.COOKS_ASSISTANT) && object.isOn(131,660)) {
						owner.setBusy(true);
						Npc chef = World.getNpc(7, 131, 137, 659, 665);
						if (chef != null) {
							for (Player informee : chef.getViewArea().getPlayersInView()) {
								informee.informOfNpcMessage(new ChatMessage(chef, "Hey! Who said you could use that?", owner));
							}
						}
						World.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								owner.setBusy(false);
							}
						});
					} else {
						if (item.getID() == 591) // Lava Eel
						{
							owner.setBusy(true);
							showBubble();
							owner.sendSound("cooking", false);
							owner.sendMessage("You cook the lava eel on the " + object.getGameObjectDef().getName());
							World.getDelayedEventHandler().add(new ShortEvent(owner) {
								public void action() {
									if (owner.getInventory().remove(item) > -1) {
										owner.sendMessage("The lava eel is now nicely cooked");
										owner.getInventory().add(new InvItem(590, 1));
										owner.sendInventory();
									}
									owner.setBusy(false);
								}
							});
						}
						else
							if (item.getID() == 622) { // Seaweed (Glass)
								owner.setBusy(true);
								showBubble();
								owner.sendSound("cooking", false);
								owner.sendMessage("You put the seaweed on the  " + object.getGameObjectDef().getName());
								World.getDelayedEventHandler().add(new ShortEvent(owner) {
									public void action() {
										if (owner.getInventory().remove(item) > -1) {
											owner.sendMessage("The seaweed burns to ashes");
											owner.getInventory().add(new InvItem(624, 1));
											owner.sendInventory();
										}
										owner.setBusy(false);
									}
								});
							} else if (item.getID() == 132) { // Cooked Meat
								owner.setBusy(true);
								showBubble();
								owner.sendSound("cooking", false);
								owner.sendMessage("You cook the Cooked Meat on the " + object.getGameObjectDef().getName());
								World.getDelayedEventHandler().add(new ShortEvent(owner) {
									public void action() {
										if (owner.getInventory().remove(item) > -1) {
											owner.sendMessage("You burn the meat");
											owner.getInventory().add(new InvItem(134, 1));
											owner.sendInventory();
										}
										owner.setBusy(false);
									}
								});
							} else {
								owner.setCancelBatch(false);
								cookLoop();
							}
					}
					break;
				case 274:
				case 435:
				case 491: // Range
					if (item.getID() == 591) // Lava Eel
					{
						owner.setBusy(true);
						showBubble();
						owner.sendSound("cooking", false);
						owner.sendMessage("You cook the lava eel on the " + object.getGameObjectDef().getName());
						World.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								if (owner.getInventory().remove(item) > -1) {
									owner.sendMessage("The lava eel is now nicely cooked");
									owner.getInventory().add(new InvItem(590, 1));
									owner.sendInventory();
								}
								owner.setBusy(false);
							}
						});
					}
					else
						if (item.getID() == 622) { // Seaweed (Glass)
							owner.setBusy(true);
							showBubble();
							owner.sendSound("cooking", false);
							owner.sendMessage("You put the seaweed on the  " + object.getGameObjectDef().getName());
							World.getDelayedEventHandler().add(new ShortEvent(owner) {
								public void action() {
									if (owner.getInventory().remove(item) > -1) {
										owner.sendMessage("The seaweed burns to ashes");
										owner.getInventory().add(new InvItem(624, 1));
										owner.sendInventory();
									}
									owner.setBusy(false);
								}
							});
						} else if (item.getID() == 132) { // Cooked Meat
							owner.setBusy(true);
							showBubble();
							owner.sendSound("cooking", false);
							owner.sendMessage("You cook the Cooked Meat on the " + object.getGameObjectDef().getName());
							World.getDelayedEventHandler().add(new ShortEvent(owner) {
								public void action() {
									if (owner.getInventory().remove(item) > -1) {
										owner.sendMessage("You burn the meat");
										owner.getInventory().add(new InvItem(134, 1));
										owner.sendInventory();
									}
									owner.setBusy(false);
								}
							});								
						} else {
							owner.setCancelBatch(false);
							cookLoop();
						}
					break;
				case 118:
				case 813: // Furnace
					if(item.getID() == 171 && owner.getInventory().contains(1057)) { //Cannon ball smithing
						if(!owner.isQuestFinished(Quests.DWARF_CANNON)) {
							owner.sendMessage("You need to have finished Dwarf Cannon in order to make cannon balls");
							return;
						}
						else if (owner.getCurStat(13) < 35) {
							owner.sendMessage("You need a smithing level of 35 to smelt this.");
							return;
						}
						owner.setBusy(true);
						showBubble();
						World.getDelayedEventHandler().add(new SingleEvent(owner, 0) {
							public void action() {
								InvItem ball = new InvItem(1041);
								if (owner.getInventory().remove(item) > -1)
									owner.sendInventory();
								owner.sendMessage("You heat the steel bar into a liquid state");
								World.getDelayedEventHandler().add(new SingleEvent(owner, 2000) {
									public void action() {
										owner.sendMessage("and pour it into your cannon ball mould");
										World.getDelayedEventHandler().add(new SingleEvent(owner, 2000) {
											public void action() {
												owner.sendMessage("you then leave it to cool for a short while");
												World.getDelayedEventHandler().add(new SingleEvent(owner, 2000) {
													public void action() {
														{
															owner.getInventory().add(ball);
															owner.sendMessage("It's very heavy");
															owner.increaseXP(Skills.SMITHING, 25);
															owner.sendStat(13);
															owner.sendInventory();
														}
														owner.setBusy(false);
													}
												});
											}
										});
									}
								});
							}
						});
					}
					else if (item.getID() == 172) {

						// Gold Bar (Crafting)
						World.getDelayedEventHandler().add(new MiniEvent(owner) {
							public void action() {
								owner.sendMessage("What would you like to make?");
								String[] options = new String[]{"Ring", "Necklace", "Amulet"};
								owner.setMenuHandler(new MenuHandler(options) {
									public void handleReply(int option, String reply) {
										if (owner.isBusy() || option < 0 || option > 2)
											return;
										final int[] moulds = {293, 295, 294};
										final int[] gems = {-1, 164, 163, 162, 161, 523};
										String[] options = {"Gold", "Sapphire", "Emerald", "Ruby", "Diamond", "Dragonstone"};
										final int craftType = option;
										if (owner.getInventory().countId(moulds[craftType]) < 1) {
											owner.sendMessage("You need a " + EntityHandler.getItemDef(moulds[craftType]).getName() + " to make a " + reply);
											return;
										}
										owner.sendMessage("What type of " + reply + " would you like to make?");
										owner.setMenuHandler(new MenuHandler(options) {
											public void handleReply(int option, String reply) {
												if (owner.isBusy() || option < 0 || option > 5)
													return;
												if (option != 0 && owner.getInventory().countId(gems[option]) < 1) {
													owner.sendMessage("You don't have a " + reply);
													return;
												}
												ItemCraftingDef def = EntityHandler.getCraftingDef((option * 3) + craftType);
												if (def == null) {
													owner.sendMessage("Nothing interesting happens");
													return;
												}
												if(owner.getCurStat(12) < def.getReqLevel()) {
													owner.sendMessage("You need at crafting level of " + def.getReqLevel() + " to make this");
													return;
												}
												if (owner.getInventory().remove(item) > -1 && (option == 0 || owner.getInventory().remove(gems[option], 1) > -1)) {
													showBubble();
													InvItem result = new InvItem(def.getItemID(), 1);
													owner.sendMessage("You make a " + result.getDef().getName());
													owner.getInventory().add(result);
													owner.increaseXP(Skills.CRAFTING, def.getExp());
													owner.sendStat(12);
													owner.sendInventory();
												}
											}
										});
										owner.sendMenu(options);
									}
								});
								owner.sendMenu(options);
							}
						});
					} else if (item.getID() == 384) {

						// Silver Bar (Crafting)
						World.getDelayedEventHandler().add(new MiniEvent(owner) {
							public void action() {
								owner.sendMessage("What would you like to make?");
								String[] options = new String[]{"Holy Symbol of Saradomin", "UnHoly Symbol of Zamorak"};
								owner.setMenuHandler(new MenuHandler(options) {
									public void handleReply(int option, String reply) {
										if (owner.isBusy() || option < 0 || option > 1)
											return;
										int[] moulds = {386, 1026};
										int[] results = {44, 1027};
										if (owner.getInventory().countId(moulds[option]) < 1) {
											owner.sendMessage("You need a " + EntityHandler.getItemDef(moulds[option]).getName() + " to make a " + reply);
											return;
										}
										if (option == 0 && owner.getCurStat(12) < 16) {
											owner.sendMessage("You need a crafting level of 16 to make this");
											return;
										}
										else if(option == 1 && owner.getCurStat(12) < 17) {
											owner.sendMessage("You need a crafting level of 17 to make this");
											return;
										}
										if (owner.getInventory().remove(item) > -1) {
											showBubble();
											InvItem result = new InvItem(results[option]);
											owner.sendMessage("You make a " + result.getDef().getName());
											owner.getInventory().add(result);
											owner.increaseXP(Skills.CRAFTING, 200);
											owner.sendStat(12);
											owner.sendInventory();
										}
									}
								});
								owner.sendMenu(options);
							}
						});
					} else if (item.getID() == 625) {

						// Sand (Glass)
						if (player.getInventory().countId(624) < 1) {
							owner.sendMessage("You need some soda ash to mix the sand with");
							return;
						}
						owner.setBusy(true);
						showBubble();
						owner.sendMessage("You put the seaweed and the soda ash in the furnace.");
						World.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								if (player.getInventory().remove(624, 1) > -1 && player.getInventory().remove(item) > -1) {
									owner.sendMessage("It mixes to make some molten glass");
									owner.getInventory().add(new InvItem(623, 1));
									owner.getInventory().add(new InvItem(21, 1));
									owner.increaseXP(Skills.CRAFTING, 80);
									owner.sendStat(12);
									owner.sendInventory();
								}
								owner.setBusy(false);
							}
						});
					} else {

						// Smelting Ore
						ItemSmeltingDef smeltingDef = item.getSmeltingDef();
						if (smeltingDef == null) {
							owner.sendMessage("Nothing interesting happens");
							return;
						}
						for (ReqOreDef reqOre : smeltingDef.getReqOres()) {
							if (owner.getInventory().countId(reqOre.getId()) < reqOre.getAmount()) {
								if (item.getID() == 151) {
									smeltingDef = EntityHandler.getItemSmeltingDef(9999);
									break;
								}
								owner.sendMessage("You need " + reqOre.getAmount() + " " + EntityHandler.getItemDef(reqOre.getId()).getName() + " to smelt a " + item.getDef().getName() + ".");
								return;
							}
						}
						if (owner.getCurStat(13) < smeltingDef.getReqLevel()) {
							owner.sendMessage("You need a smithing level of " + smeltingDef.getReqLevel() + " to smelt this.");
							return;
						}

						final ItemSmeltingDef def = smeltingDef;
						BatchedEvent smeltEvent = new BatchedEvent(owner, 1500) {
							public void doAction() {
								action();
							}
							public void run() {
								owner.setBusy(true);
								showBubble();
								owner.sendMessage("You smelt the " + item.getDef().getName().toLowerCase().replace(" ore", "") + " in the furnace");
								InvItem bar = new InvItem(def.getBarId());
								if (owner.getInventory().remove(item) > -1) {
									for (ReqOreDef reqOre : def.getReqOres()) {
										for (int i = 0; i < reqOre.getAmount(); i++)
											owner.getInventory().remove(new InvItem(reqOre.getId()));
									}
									if(bar.getID() == 170)
									{
										boolean ironOreSuccess = DataConversions.getRandom().nextBoolean();
										if(ironOreSuccess) {
											owner.getInventory().add(bar);
											owner.sendMessage("You retrieve a bar of " + bar.getDef().getName().toLowerCase().replace(" bar", ""));
											owner.increaseXP(Skills.SMITHING, def.getExp());
											owner.sendStat(13);
											owner.sendInventory();                                                
										} else {
											owner.sendMessage("The ore is too impure and you fail to refine it");
											owner.sendInventory();
										}
									}
									else {
										owner.getInventory().add(bar);
										owner.sendMessage("You retrieve a bar of " + bar.getDef().getName().toLowerCase().replace(" bar", ""));
										owner.increaseXP(Skills.SMITHING, def.getExp());
										owner.sendStat(13);
										owner.sendInventory();
									}
								}
								if (Config.getSkillLoopMode() == 0 || batch == 0 ||
										owner.getCancelBatch() || owner.getInventory().full() ||
										!owner.getInventory().contains(item.getID()) ||
										owner.getLocation().distanceToObject(object) > 1) {
									owner.setBusy(false);
									this.stop();
								}
							}
							public int calculateActionAttempts() {
								if (batch > 0) {
									return owner.getInventory().getEmptySlots();
								}
								return 1;
							}
						};
						World.getDelayedEventHandler().add(smeltEvent);
					}
					break;

				case 177: // Doric's Anvil
					if (owner.getQuestCompletionStage(Quests.DORICS_QUEST) != 1) {
						Npc doric = World.getNpc(144, 323, 327, 487, 492);
						if (doric != null && !doric.isBusy()) {
							if (doric.getX() != owner.getX() || doric.getY() != owner.getY()) {
								doric.updateSprite(owner.getX(), owner.getY());
								owner.updateSprite(doric.getX(), doric.getY());
							}
							for (Player informee : owner.getViewArea().getPlayersInView())
								informee.informOfNpcMessage(new ChatMessage(doric, "Heh who said you could use that?", owner));
						} else
							owner.sendMessage("I should get permission before using this");
						break;
					}
				case 50: // Anvil
					int minSmithingLevel = Formulae.minSmithingLevel(item.getID());
					if(item.getID() == 1276 || item.getID() == 1277) {
						if (owner.getCurStat(13) < 60)
							owner.sendMessage("You need a smithing level of 99 to join the dragon shield halves");
						else if(owner.getInventory().countId(1277) < 1 || owner.getInventory().countId(1276) < 1)
							owner.sendMessage("You need both halves of the dragon shield in order to join the halves");										
						else if(owner.getInventory().countId(168) < 1)
							owner.sendMessage("You need a hammer to forge the shield");								
						else {
							//									Bubble shieldCreation = new Bubble(owner.getIndex(), 1278);
							showBubble();									
							for(Player p : owner.getViewArea().getPlayersInView())
							{
								p.watchItemBubble(owner.getIndex(), 1278);
								//										p.informOfBubble(shieldCreation);
							}
							owner.sendMessage("You join the halves to create a new dragon shield");
							owner.getInventory().remove(new InvItem(1276, 1));
							owner.getInventory().remove(new InvItem(1277, 1));
							owner.getInventory().add(new InvItem(1278, 1));
							owner.sendInventory();
							return;
						}
					}
					if (minSmithingLevel < 0) {
						owner.sendMessage("Nothing interesting happens");
						return;
					}
					if (owner.getInventory().countId(168) < 1) {
						owner.sendMessage("You need a hammer to work the metal with");
						return;
					}
					if (owner.getCurStat(13) < minSmithingLevel) {
						owner.sendMessage("You need a smithing level of " + minSmithingLevel + " to use this type of bar");
						return;
					}
					if(item.getID() == 171) {
						options = new String[] {"Make Weapon", "Make Armour", "Make Missle Heads", "Make Nails", "Cancel"};
						owner.setMenuHandler(new MenuHandler(options) {
							public void handleReply(int option, String reply) {
								if(owner.isBusy())
									return;
								String[] options;
								switch(option) {
								case 0:
									owner.sendMessage("Choose a type of weapon to make");
									options = new String[]{"Dagger", "Throwing Knife", "Sword", "Axe", "Mace"};
									owner.setMenuHandler(new MenuHandler(options) {
										public void handleReply(int option, String reply) {
											if(owner.isBusy())
												return;
											String[] options;
											switch(option) {
											case 0:
												handleSmithing(item.getID(), 0);
												break;
											case 1:
												handleSmithing(item.getID(), 1);
												break;
											case 2:
												owner.sendMessage("What sort of sword do you want to make?");
												options = new String[]{"Short Sword", "Long Sword", "Scimitar", "2-handed Sword"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if(owner.isBusy()) {
															return;
														}
														switch(option) {
														case 0:
															handleSmithing(item.getID(), 2);
															break;
														case 1:
															handleSmithing(item.getID(), 3);
															break;
														case 2:
															handleSmithing(item.getID(), 4);
															break;
														case 3:
															handleSmithing(item.getID(), 5);
															break;
														default:
															return;
														}
													}
												});
												owner.sendMenu(options);
												break;
											case 3:
												owner.sendMessage("What sort of axe do you want to make?");
												options = new String[]{"Hatchet", "Pickaxe", "Battle Axe"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if(owner.isBusy())
															return;
														switch(option) {
														case 0:
															handleSmithing(item.getID(), 6);
															break;
														case 1:
															handleSmithing(item.getID(), 7);
															break;
														case 2:
															handleSmithing(item.getID(), 8);
															break;
														default:
															return;
														}
													}
												});
												owner.sendMenu(options);
												break;
											case 4:
												handleSmithing(item.getID(), 9);
												break;
											default:
												return;
											}
										}
									});
									owner.sendMenu(options);
									break;
								case 1:
									owner.sendMessage("Choose a type of armour to make");
									options = new String[]{"Helmet", "Shield", "Armour"};
									owner.setMenuHandler(new MenuHandler(options) {
										public void handleReply(int option, String reply) {
											if(owner.isBusy())
												return;
											switch(option) {
											case 0:
												owner.sendMessage("What sort of helmet do you want to make?");
												options = new String[]{"Medium Helmet", "Large Helmet"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if(owner.isBusy())
															return;
														switch(option) {
														case 0:
															handleSmithing(item.getID(), 10);
															break;
														case 1:
															handleSmithing(item.getID(), 11);
															break;
														default:
															return;
														}
													}
												});
												owner.sendMenu(options);
												break;
											case 1:
												owner.sendMessage("What sort of shield do you want to make?");
												options = new String[]{"Square Shield", "Kite Shield"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if (owner.isBusy())
															return;
														switch (option) {
														case 0:
															handleSmithing(item.getID(), 12);
															break;
														case 1:
															handleSmithing(item.getID(), 13);
															break;
														default:
															return;
														}
													}
												});
												owner.sendMenu(options);
												break;
											case 2:
												owner.sendMessage("What sort of armour do you want to make?");
												options = new String[]{"Chain Mail Body", "Plate Mail Body", "Plate Mail Legs", "Plated Skirt"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if(owner.isBusy())
															return;
														switch(option) {
														case 0:
															handleSmithing(item.getID(), 14);
															break;
														case 1:
															handleSmithing(item.getID(), 15);
															break;
														case 2:
															handleSmithing(item.getID(), 16);
															break;
														case 3:
															handleSmithing(item.getID(), 17);
															break;
														default:
															return;
														}
													}
												});
												owner.sendMenu(options);
												break;
											default:
												return;
											}
										}
									});
									owner.sendMenu(options);
									break;//unlock the door
								case 2:
									options = new String[]{"Make 10 Arrow Heads", "Make 50 Arrow Heads (5 bars)", "Forge Dart Tips"};
									owner.setMenuHandler(new MenuHandler(options) {
										public void handleReply(int option, String reply) {
											if (owner.isBusy())
												return;
											switch (option) {
											case 0:
												handleSmithing(item.getID(), 18);
												break;
											case 1:
												handleSmithing(item.getID(), 19);
												break;
											case 2:
												handleSmithing(item.getID(), 20);
												break;
											default:
												return;
											}
										}
									});
									owner.sendMenu(options);
									break;
								case 3:
									if (owner.getCurStat(13) < 34) {
										owner.sendMessage("You need at smithing level of 34 to make this");
										return;
									}
									if (owner.getInventory().countId(171) < 1) {
										owner.sendMessage("You don't have enough bars to make this");
										return;
									}
									owner.sendSound("anvil", false);
									owner.getInventory().remove(new InvItem(171, 1));
									//	      										Bubble bubble = new Bubble(owner.getIndex(), item.getID());
									for (Player p : owner.getViewArea().getPlayersInView())
									{
										p.watchItemBubble(owner.getIndex(), item.getID());
										//	      											p.informOfBubble(bubble);
									}
									owner.sendMessage("You hammer the metal into some nails");
									owner.getInventory().add(new InvItem(419, 2));
									owner.increaseXP(Skills.SMITHING, Formulae.getSmithingExp(171, 1));
									owner.sendStat(13);
									owner.sendInventory();
									break;
								default:
									return;
								}
							}
						});
					} else {
						options = new String[]{"Make Weapon", "Make Armour", "Make Missile Heads", "Cancel"};
						owner.setMenuHandler(new MenuHandler(options) {
							public void handleReply(int option, String reply) {
								if(owner.isBusy())
									return;
								String[] options;
								switch(option) {
								case 0:
									owner.sendMessage("Choose a type of weapon to make");
									options = new String[]{"Dagger", "Throwing Knife", "Sword", "Axe", "Mace"};
									owner.setMenuHandler(new MenuHandler(options) {
										public void handleReply(int option, String reply) {
											if(owner.isBusy())
												return;
											String[] options;
											switch(option) {
											case 0:
												handleSmithing(item.getID(), 0);
												break;
											case 1:
												handleSmithing(item.getID(), 1);
												break;
											case 2:
												owner.sendMessage("What sort of sword do you want to make?");
												options = new String[]{"Short Sword", "Long Sword", "Scimitar", "2-handed Sword"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if(owner.isBusy()) {
															return;
														}
														switch(option) {
														case 0:
															handleSmithing(item.getID(), 2);
															break;
														case 1:
															handleSmithing(item.getID(), 3);
															break;
														case 2:
															handleSmithing(item.getID(), 4);
															break;
														case 3:
															handleSmithing(item.getID(), 5);
															break;
														default:
															return;
														}
													}
												});
												owner.sendMenu(options);
												break;
											case 3:
												owner.sendMessage("What sort of axe do you want to make?");
												options = new String[]{"Hatchet", "Pickaxe", "Battle Axe"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if(owner.isBusy())
															return;
														switch(option) {
														case 0:
															handleSmithing(item.getID(), 6);
															break;
														case 1:
															handleSmithing(item.getID(), 7);
															break;
														case 2:
															handleSmithing(item.getID(), 8);
															break;
														default:
															return;
														}
													}
												});
												owner.sendMenu(options);
												break;
											case 4:
												handleSmithing(item.getID(), 9);
												break;
											default:
												return;
											}
										}
									});
									owner.sendMenu(options);
									break;
								case 1:
									owner.sendMessage("Choose a type of armour to make");
									options = new String[]{"Helmet", "Shield", "Armour"};
									owner.setMenuHandler(new MenuHandler(options) {
										public void handleReply(int option, String reply) {
											if(owner.isBusy())
												return;
											switch(option) {
											case 0:
												owner.sendMessage("What sort of helmet do you want to make?");
												options = new String[]{"Medium Helmet", "Large Helmet"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if(owner.isBusy())
															return;
														switch(option) {
														case 0:
															handleSmithing(item.getID(), 10);
															break;
														case 1:
															handleSmithing(item.getID(), 11);
															break;
														default:
															return;
														}
													}
												});
												owner.sendMenu(options);
												break;
											case 1:
												owner.sendMessage("What sort of shield do you want to make?");
												options = new String[]{"Square Shield", "Kite Shield"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if (owner.isBusy())
															return;
														switch (option) {
														case 0:
															handleSmithing(item.getID(), 12);
															break;
														case 1:
															handleSmithing(item.getID(), 13);
															break;
														default:
															return;
														}
													}
												});
												owner.sendMenu(options);
												break;
											case 2:
												owner.sendMessage("What sort of armour do you want to make?");
												options = new String[]{"Chain Mail Body", "Plate Mail Body", "Plate Mail Legs", "Plated Skirt"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if(owner.isBusy())
															return;
														switch(option) {
														case 0:
															handleSmithing(item.getID(), 14);
															break;
														case 1:
															handleSmithing(item.getID(), 15);
															break;
														case 2:
															handleSmithing(item.getID(), 16);
															break;
														case 3:
															handleSmithing(item.getID(), 17);
															break;
														default:
															return;
														}
													}
												});
												owner.sendMenu(options);
												break;
											default:
												return;
											}
										}
									});
									owner.sendMenu(options);
									break;//unlock the door
								case 2:
									options = new String[]{"Make 10 Arrow Heads", "Make 50 Arrow Heads (5 bars)", "Forge Dart Tips"};
									owner.setMenuHandler(new MenuHandler(options) {
										public void handleReply(int option, String reply) {
											if (owner.isBusy())
												return;
											switch (option) {
											case 0:
												handleSmithing(item.getID(), 18);
												break;
											case 1:
												handleSmithing(item.getID(), 19);
												break;
											case 2:
												handleSmithing(item.getID(), 20);
												break;
											default:
												return;
											}
										}
									});
									owner.sendMenu(options);
									break;
								default:
									return;
								}
							}
						});	
					}

					owner.sendMenu(options);
					break;
				case 121: // Spinning Wheel
					switch (item.getID()) {
					case 145: // Wool
						owner.sendMessage("You spin the sheeps wool into a nice ball of wool");
						World.getDelayedEventHandler().add(new MiniEvent(owner) {
							public void action() {
								if (owner.getInventory().remove(item) > -1) {
									owner.getInventory().add(new InvItem(207, 1));
									owner.increaseXP(Skills.CRAFTING, 10);
									owner.sendStat(12);
									owner.sendInventory();
								}
								owner.setBusy(false);
							}
						});
						break;
					case 675: // Flax
						if (owner.getCurStat(12) < 10) {
							owner.sendMessage("You need a crafting level of 10 to spin flax");
							return;
						}
						owner.sendMessage("You make the flax into a bow string");
						World.getDelayedEventHandler().add(new MiniEvent(owner) {
							public void action() {
								if (owner.getInventory().remove(item) > -1) {
									owner.getInventory().add(new InvItem(676, 1));
									owner.increaseXP(Skills.CRAFTING, 60);
									owner.sendStat(12);
									owner.sendInventory();
								}
								owner.setBusy(false);
							}
						});
						break;
					default:
						owner.sendMessage("Nothing interesting happens");
						return;
					}
					owner.setBusy(true);
					showBubble();
					owner.sendSound("mechanical", false);
					break;

				case 248: // Crystal key chest
					if(item.getID() == 525) 
					{
						owner.setBusy(true);
						owner.sendMessage("You use the crystal key to unlock the chest.");
						owner.getInventory().remove(525, 1);
						owner.sendInventory();
						World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) 
						{
							int[] randomLoot = new int[] { 517, 535, 542, 526, 523, 525, 369, 408 };
							final int loot = randomLoot [DataConversions.getRandom().nextInt(randomLoot.length)];

							public void action() 
							{
								InvItem item = new InvItem(loot, 1);
								owner.sendMessage("Inside you find a " + item.getDef().getName());
								owner.getInventory().add(item);
								owner.sendInventory();
								owner.setBusy(false);
							}
						});
					}
					else
					{
						owner.sendMessage("Nothing interesting happens.");
					}
					break;

					/*owner.sendMessage("You use the key to unlock the chest");
	      				owner.setBusy(true);
	      				showBubble();
	      				World.getDelayedEventHandler().add(new ShortEvent(owner) {
	      					public void action() {
	      						if (owner.getInventory().remove(item) > -1) {
	      							owner.getInventory().add(new InvItem(542, 1));
	      							List<InvItem> loot = Formulae.getcrystalKeyChestLoot();
									for (InvItem i : loot) {
										if (i.getAmount() > 1 && !i.getDef().isStackable()) {
											for (int x = 0; x < i.getAmount(); x++)
												owner.getInventory().add(new InvItem(i.getID(), 1));
										} else {
											owner.getInventory().add(i);
										}
									}
									owner.sendInventory();
	      						}
	      						owner.setBusy(false);
	      					}
	      				});
					 */

				case 187:
					if(item.getID() == 382) {
						Quest q = owner.getQuest(Quests.PIRATES_TREASURE);
						if (q != null) {
							if (q.getStage() == 1) {
								owner.setBusy(true);
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
									public void action() {
										owner.sendMessage("You unlock the chest");
										owner.getInventory().remove(382, 1);
										owner.sendInventory();
										World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
											public void action() {
												owner.sendMessage("All that is in the chest is a message");
												World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
													public void action() {
														owner.sendMessage("You take the message from the chest");
														World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
															public void action() {
																owner.setBusy(false);
																owner.sendMessage("It says dig just behind the south bench in the park");
																owner.incQuestCompletionStage(Quests.PIRATES_TREASURE);
															}
														});
													}
												});
											}
										});
									}
								});
							} else
								owner.sendMessage("Nothing interesting happens.");
						} else
							owner.sendMessage("Nothing interesting happens.");
					} else
						owner.sendMessage("Nothing interesting happens.");
					break;
				case 188: //Flower for the Pirate's Treasure [QUEST]
					if (item.getID() == 211) {
						Quest q = owner.getQuest(Quests.PIRATES_TREASURE);
						if (q != null) {
							if (q.getStage() == 2 && !q.finished()) {
								final Npc wyson = World.getNpc(116, 285, 296, 545, 551);
								if (wyson != null && !wyson.isBusy() && !wyson.isAggressive()) {
									for (Player informee : owner.getViewArea().getPlayersInView())
										informee.informOfNpcMessage(new ChatMessage(wyson, "Hey leave off my flowers", owner));
									wyson.setAggressive(owner);
								} else {
									owner.setBusy(true);
									World.getDelayedEventHandler().add(new SingleEvent(owner, 2000) {
										public void action() {
											owner.sendMessage("You dig a hole in the ground");
											World.getDelayedEventHandler().add(new SingleEvent(owner, 2000) {
												public void action() {
													owner.sendMessage("You find a little bag of treasure");
													World.getDelayedEventHandler().add(new SingleEvent(owner, 2000) {
														public void action() {
															owner.sendMessage("Well done you have completed the Pirate's Treasure quest");
															owner.sendMessage("@gre@You have gained 2 quest points!");
															owner.finishQuest(Quests.PIRATES_TREASURE);
															owner.getInventory().add(new InvItem(10, 450));
															owner.getInventory().add(new InvItem(283, 1));
															owner.getInventory().add(new InvItem(163, 1));
															owner.sendInventory();
															owner.setBusy(false);
															Logger.log(new eventLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), DataConversions.getTimeStamp(), "<strong>" + owner.getUsername() + "</strong>" + " has completed the <span class=\"recent_quest\">Pirate's Treasure</span> quest!"));
														}
													});
												}
											});
										}
									});
								}
							} else
								owner.sendMessage("Nothing interesting happens");
						} else
							owner.sendMessage("Nothing interesting happens");
					} else
						owner.sendMessage("Nothing interesting happens");
					break;
				case 302: // Sandpit
					if (item.getID() != 21) {
						owner.sendMessage("Nothing interesting happens");
						return;
					}
					owner.sendMessage("You fill the bucket with sand");
					owner.setBusy(true);
					showBubble();
					World.getDelayedEventHandler().add(new MiniEvent(owner) {
						public void action() {
							if (owner.getInventory().remove(item) > -1) {
								owner.getInventory().add(new InvItem(625, 1));
								owner.sendInventory();
							}
							owner.setBusy(false);
						}
					});
					break;
				case 86: //Draynor Manor Pirhana Fountain
					owner.setBusy(true);
					if(item.getID() == 176) {
						String[] poured = owner.killedFish() ? new String[] {"You pour the fish food into the fountain"} : new String[] {"You pour the fish food into the fountain", "You see the pirhanas eating the food", "The pirhanas seem hungrier than ever"};
						World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, poured, 1500) {
							public void finished() {
								owner.getInventory().remove(new InvItem(176, 1));
								owner.sendInventory();
								owner.setBusy(false);
							}
						});
					} else if(item.getID() == 178) {
						String[] poured = owner.killedFish() ? new String[] {"You pour the poisoned fish food into the fountain"} : new String[] {"You pour the poisoned fish food into the fountain", "You see the pirhanas eating the food", "The pirhanas drop dead and float to the surface"};
						World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, poured, 1500) {
							public void finished() {
								owner.getInventory().remove(new InvItem(178, 1));
								owner.sendInventory();
								owner.setKilledFish(true);
								owner.setBusy(false);
							}
						});
					} else {
						owner.sendMessage("Nothing interesting happens");
						owner.setBusy(false);
					}
					break;
				case 179: // Potters Wheel
					if (item.getID() != 243) {
						owner.sendMessage("Nothing interesting happens");
						return;
					}
					owner.sendMessage("What would you like to make?");
					options = new String[]{"Pot", "Pie Dish", "Bowl", "Cancel"};
					owner.setMenuHandler(new MenuHandler(options) {
						public void handleReply(int option, String reply) {
							if (owner.isBusy())
								return;
							int reqLvl, exp;
							InvItem result;
							switch (option) {
							case 0:
								result = new InvItem(279, 1);
								reqLvl = 1;
								exp = 50;
								break;
							case 1:
								result = new InvItem(278, 1);
								reqLvl = 4;
								exp = 100;
								break;
							case 2:
								result = new InvItem(340, 1);
								reqLvl = 7;
								exp = 100;
								break;
							default:
								owner.sendMessage("Nothing interesting happens");
								return;
							}
							if(owner.getCurStat(12) < reqLvl) {
								owner.sendMessage("You need a crafting level of " + reqLvl + " to make this");
								return;
							}
							if (owner.getInventory().remove(item) > -1) {
								showBubble();
								owner.sendMessage("You make a " + result.getDef().getName());
								owner.getInventory().add(result);
								owner.increaseXP(Skills.CRAFTING, exp);
								owner.sendStat(12);
								owner.sendInventory();
							}
						}
					});
					owner.sendMenu(options);
					break;
				case 178: // Potters Oven
					int reqLvl, xp, resultID;
					switch (item.getID()) {
					case 279: // Pot
						resultID = 135;
						reqLvl = 1;
						xp = 7;
						break;
					case 278: // Pie Dish
						resultID = 251;
						reqLvl = 4;
						xp = 15;
						break;
					case 340: // Bowl
						resultID = 341;
						reqLvl = 7;
						xp = 15;
						break;
					default:
						owner.sendMessage("Nothing interesting happens");
						return;
					}
					if (owner.getCurStat(12) < reqLvl) {
						owner.sendMessage("You need a crafting level of " + reqLvl + " to make this");
						return;
					}
					final InvItem result = new InvItem(resultID, 1);
					final int exp = xp;
					final boolean fail = Formulae.crackPot(reqLvl, owner.getCurStat(12));
					showBubble();
					owner.sendMessage("You place the " + item.getDef().getName() + " in the oven");
					owner.setBusy(true);
					World.getDelayedEventHandler().add(new ShortEvent(owner) {
						public void action() {
							if (owner.getInventory().remove(item) > -1) {
								if (fail)
									owner.sendMessage("The " + result.getDef().getName() + " cracks in the oven, you throw it away.");
								else {
									owner.sendMessage("You take out the " + result.getDef().getName());
									owner.getInventory().add(result);
									owner.increaseXP(Skills.CRAFTING, exp);
									owner.sendStat(12);
								}
								owner.sendInventory();
							}
							owner.setBusy(false);
						}
					});
					break;
				case 17:
				case 18:
					if (!item.getDef().isTradable())
					{
						owner.sendMessage("You cannot use " + item.getDef().getName() + " with this chest");
						return;
					}
					Random rand = DataConversions.getRandom();
					if (item.getID() == 1355 || item.getID() == 1353 || item.getID() == 1354)
					{
						player.sendMessage("You cannot use this item here.");
						return;
					}
					else
						if (owner.getInventory().remove(item) > -1) {
							owner.sendInventory();
							owner.sendMessage("You place the item into the chest...");

							World.getDelayedEventHandler().add(new SingleEvent(owner, rand.nextInt(5000)) {
								public void action() {
									Random rand = DataConversions.getRandom();
									Item drop = new Item(item.getID(), rand.nextInt(9) + 492, rand.nextInt(8) + 1408, (item.getDef().isStackable() ? item.getAmount() : 1), (Player[])null);
									World.registerEntity(drop);

									synchronized (World.getPlayers()) {
										for (Player p : World.getPlayers()) {
											if (p.getLocation().isInSeersPartyHall())
												p.sendNotification(Config.getPrefix() + owner.getStaffName() + "@whi@ just dropped: @gre@" + item.getDef().getName() + (item.getAmount() > 1 ? " @whi@(" + DataConversions.number_format("" + item.getAmount()) + ")" : ""));
										}
									}									
								}
							});
						}
					break;
				case 236:
					if(owner.getQuestCompletionStage(Quests.DRUIDIC_RITUAL) == 1) {
						if (item.getID() == 133) {
							owner.getInventory().remove(new InvItem(item.getID(), 1));
							owner.sendMessage("You dip the " + item.getDef().getName() + " in the cauldron");
							owner.getInventory().add(new InvItem(508, 1));
							owner.sendInventory();
						} else {
							int[] items = {502, 503, 504};
							int[] receive = {505, 506, 507};
							int index = Arrays.binarySearch(items, item.getID());
							if (index >= 0) {
								owner.getInventory().remove(new InvItem(item.getID(), 1));
								owner.sendInventory();
								owner.sendMessage("You dip the " + item.getDef().getName() + " in the cauldron");
								owner.getInventory().add(new InvItem(receive[index], 1));
								owner.sendInventory();
							}
						}
					}
					break;
				case 287:
					if (item.getID() == 606) { // Excalibur
						Quest merlinsQuest = owner.getQuest(Quests.MERLINS_CRYSTAL);
						if (merlinsQuest != null) {
							if (merlinsQuest.getStage() == 7 && !merlinsQuest.finished()) {
								owner.sendMessage("You shatter the crystal with Excalibur");
								owner.teleport(463, 3280);
							} else
								owner.sendMessage("Nothing interesting happens");
						} else
							owner.sendMessage("Nothing interesting happens");
					}
					break;

				case 182: //Luthas' Banana Box
					if (item.getID() == 249) { //Banana
						if (owner.hasBananaJob()) {
							if (owner.isJobFinished())
								owner.sendMessage("The crate is already full.");
							else {
								owner.sendMessage("You put a banana in the crate.");
								owner.putBananaInCrate();
								owner.getInventory().remove(new InvItem(249, 1));
								owner.sendInventory();
							}
						} else
							owner.sendMessage("I have no reason to do that.");
						break;
					} else if(item.getID() == 318) {
						if (owner.hasBananaJob()) {
							if (!owner.rumInKaramjaCrate()) {
								owner.sendMessage("You stash the rum in the crate.");
								owner.getInventory().remove(new InvItem(318, 1));
								owner.sendInventory();
								owner.putRumInCrate();
							} else
								owner.sendMessage("I see no reason to do that.");
						} else
							owner.sendMessage("I have no reason to do that.");
						break;
					}
				default:
					owner.sendMessage("Nothing interesting happens.");
					return;
				}
			}

			private void cookLoop() {
				if (owner.getCancelBatch())
					return;
				final ItemCookingDef cookingDef = item.getCookingDef();
				if (cookingDef == null) {
					owner.sendMessage("Nothing interesting happens");
					return;
				}
				if (owner.getInventory().countId(item.getID()) < 1)
					return;
				if (owner.getCurStat(7) < cookingDef.getReqLevel()) {
					owner.sendMessage("You need a cooking level of " + cookingDef.getReqLevel() + " to cook this");
					return;
				}
				owner.setBusy(true);
				showBubble();
				owner.sendSound("cooking", false);
				owner.sendMessage("You cook the " + item.getDef().getName() + " on the " + object.getGameObjectDef().getName());
				World.getDelayedEventHandler().add(new ShortEvent(owner) {
					public void action() {
						InvItem cookedFood = new InvItem(cookingDef.getCookedId());
						if (owner.getInventory().remove(item) > -1) {
							if (!Formulae.burnFood(item.getID(), owner.getCurStat(7))) {
								owner.getInventory().add(cookedFood);
								owner.sendMessage("The " + item.getDef().getName() + " is now nicely cooked");
								owner.increaseXP(7, cookingDef.getExp());
								owner.sendStat(7);
							} else {
								owner.getInventory().add(new InvItem(cookingDef.getBurnedId()));
								owner.sendMessage("You accidently burn the " + item.getDef().getName());
							}
							owner.sendInventory();
						}
						owner.setBusy(false);
						if (Config.getSkillLoopMode() == 2 && batch == 2 && !owner.getCancelBatch() && owner.getInventory().contains(item.getID()) && owner.getLocation().distanceToObject(object) < 2) {
							World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
								public void action() {
									cookLoop();
								}
							});
						}
					}
				});
			}

			private void handleSmithing(int barID, int toMake) {
				ItemSmithingDef def = EntityHandler.getSmithingDef((Formulae.getBarType(barID) * 21) + toMake);
				if (def == null) {
					owner.sendMessage("Nothing interesting happens");
					return;
				}
				if (owner.getCurStat(13) < def.getRequiredLevel()) {
					owner.sendMessage("You need at smithing level of " + def.getRequiredLevel() + " to make this");
					return;
				}
				if (owner.getInventory().countId(barID) < def.getRequiredBars()) {
					owner.sendMessage("You don't have enough bars to make this");
					return;
				}
				owner.sendSound("anvil", false);
				for (int x = 0;x < def.getRequiredBars();x++)
					owner.getInventory().remove(new InvItem(barID, 1));
				if (item.getID() != 1276 && item.getID() != 1277) {
					//					Bubble bubble = new Bubble(owner.getIndex(), item.getID());
					for(Player p : owner.getViewArea().getPlayersInView())
					{
						p.watchItemBubble(owner.getIndex(), item.getID());
						//							p.informOfBubble(bubble);
					}
				}
				if (EntityHandler.getItemDef(def.getItemID()).isStackable()) {
					owner.sendMessage("You hammer the metal into some " + EntityHandler.getItemDef(def.getItemID()).getName());
					owner.getInventory().add(new InvItem(def.getItemID(), def.getAmount()));
				} else {
					owner.sendMessage("You hammer the metal into a " + EntityHandler.getItemDef(def.getItemID()).getName());
					for(int x = 0;x < def.getAmount();x++)
						owner.getInventory().add(new InvItem(def.getItemID(), 1));
				}
				owner.increaseXP(Skills.SMITHING, Formulae.getSmithingExp(barID, def.getRequiredBars()));
				owner.sendStat(13);
				owner.sendInventory();
			}

			private boolean itemId(int[] ids) {
				return DataConversions.inArray(ids, item.getID());
			}

			private void showBubble() {
				//				Bubble bubble = new Bubble(owner.getIndex(), item.getID());
				for (Player p : owner.getViewArea().getPlayersInView())
				{
					p.watchItemBubble(owner.getIndex(), item.getID());
					//					p.informOfBubble(bubble);
				}
			}
		});
	}

	private void handleDoor(final Player player, int x, int y, final GameObject object, final int dir, final InvItem item) {
		player.setStatus(Action.USING_INVITEM_ON_DOOR);
		World.getDelayedEventHandler().add(new WalkToObjectEvent(player, object, false) {

			private void doDoor() {
				owner.sendSound("opendoor", false);
				World.registerEntity(new GameObject(object.getLocation(), 11, object.getDirection(), object.getType()));
				World.delayedSpawnObject(object.getLoc(), 1000);
			}

			public void arrived() {
				owner.resetPath();
				if (owner.isBusy() || owner.isRanging() || !owner.getInventory().contains(item) || !World.entityExists(object) || owner.getStatus() != Action.USING_INVITEM_ON_DOOR)
					return;
				owner.resetAllExceptDMing();
				switch(object.getID()) {
				case 20: //Phoenix Gang Weapon Cache Door
					if(item.getID() == 48) {
						owner.sendMessage("You go through the door");
						doDoor();
						if(owner.getY() > 531)
							owner.teleport(owner.getX(), owner.getY() - 1);
						else
							owner.teleport(owner.getX(), owner.getY() + 1);
					} else {
						owner.sendMessage("Nothing interesting happens");
					}
					break;
				case 218: // Web
					ItemWieldableDef def = item.getWieldableDef();
					if ((def == null || def.getWieldPos() != 4) && item.getID() != 13) {
						owner.sendMessage("Nothing interesting happens.");
						return;
					}
					if (owner.getX() == 220) {
						owner.sendMessage("You cut your way through the web");
						owner.teleport(219, 130, false);
					} else if(owner.getX() == 219) {
						owner.sendMessage("You cut your way through the web");
						owner.teleport(220, 130, false);
					}
					break;
				case 219: // Web
					ItemWieldableDef def2 = item.getWieldableDef();
					if((def2 == null || def2.getWieldPos() != 4) && item.getID() != 13) {
						owner.sendMessage("Nothing interesting happens.");
						return;
					}
					if(owner.getX() == 236) {
						owner.sendMessage("You cut your way through the web");
						owner.teleport(237, 130, false);
					}
					else if(owner.getX() == 237) {
						owner.sendMessage("You cut your way through the web");
						owner.teleport(236, 130, false);
					}
					break;

				case 24: // Web
					ItemWieldableDef def3 = item.getWieldableDef();
					if ((def3 == null || def3.getWieldPos() != 4) && item.getID() != 13) {
						owner.sendMessage("Nothing interesting happens");
						return;
					}
					owner.sendMessage("You try to destroy the web");
					owner.setBusy(true);
					World.getDelayedEventHandler().add(new ShortEvent(owner) {
						public void action() {
							if (Formulae.cutWeb()) {
								owner.sendMessage("You slice through the web");
								World.unregisterEntity(object);
								World.delayedSpawnObject(object.getLoc(), 15000);
							} else
								owner.sendMessage("You fail to cut through it");
							owner.setBusy(false);
						}
					});
					break;
				case 23: // Giant place near barb village
					if (!itemId(new int[]{99})) {
						owner.sendMessage("Nothing interesting happens");
						return;
					}
					owner.sendMessage("You unlock the door and go through it");
					doDoor();
					if (owner.getY() <= 484)
						owner.teleport(owner.getX(), 485, false);
					else
						owner.teleport(owner.getX(), 484, false);
					break;
				case 60: // Melzars maze
					if (!itemId(new int[]{421})) {
						owner.sendMessage("Nothing interesting happens");
						return;
					}
					owner.sendMessage("You unlock the door and go through it");
					doDoor();
					if (owner.getX() <= 337)
						owner.teleport(338, owner.getY(), false);
					break;
				case 102: // Melzars maze -- First floor RED KEY
					if(item.getID() == 390 && owner.getInventory().remove(new InvItem(390, 1)) > -1) {
						owner.sendMessage("You unlock the door and go through it");
						owner.sendMessage("Your red key has gone!");
						doDoor();
						owner.sendInventory();
						if (owner.getX() < 349)
							owner.teleport(owner.getX() + 1, owner.getY(), false);
						else 
							owner.teleport(owner.getX() - 1, owner.getY(), false);			
					} else {
						owner.sendMessage("Nothing interesting happens");
					}
					break;
				case 103: // Melzars maze --  Second Floor -- ORANGE KEY
					if (item.getID() == 391 && owner.getInventory().remove(new InvItem(391,1)) > -1) {
						owner.sendMessage("You unlock the door and go through it");
						owner.sendMessage("Your orange key has gone!");
						doDoor();
						if (owner.getX() < 345)
							owner.teleport(owner.getX() + 1, owner.getY(), false);
						else
							owner.teleport(owner.getX() - 1, owner.getY(), false);
					} else{
						owner.sendMessage("Nothing interesting happens");
					}
					break;
				case 104: // Melzars maze --  Third floor Yellow key
					if (item.getID() == 392 && owner.getInventory().remove(new InvItem(392,1)) > -1) {
						owner.sendMessage("You unlock the door and go through it");
						owner.sendMessage("Your yellow key has gone!");
						doDoor();
						if(object.getX() == 341) {
							if (owner.getX() == 341) {
								owner.teleport(owner.getX() - 1, owner.getY(), false);
							} else {
								owner.teleport(owner.getX() + 1, owner.getY(), false);
							}
						} else {
							if (owner.getY() == 2520) {
								owner.teleport(owner.getX(), owner.getY() -1, false);
							} else {
								owner.teleport(owner.getX(), owner.getY() + 1, false);
							}
						}
					}
					else
						owner.sendMessage("Nothing interesting happens");
					break;

				case 106: // Blue door in Melzar's maze
					if(item.getID() == 393 && owner.getInventory().remove(new InvItem(393, 1)) > -1) {
						owner.sendMessage("You unlock the door and go through it");
						owner.sendMessage("Your blue key has gone!");
						doDoor();
						if(owner.getX() < 346)
							owner.teleport(owner.getX() + 1, owner.getY(), false);
						else
							owner.teleport(owner.getX() -1, owner.getY(), false);
					}
					else{
						owner.sendMessage("Nothing interesting happens");
					}
					break;
				case 108: // Magenta door in Melzar's maze
					if (item. getID() == 394 && owner.getInventory().remove(new InvItem(394, 1)) > -1){
						owner.sendMessage("You unlock the door and go through it");
						owner.sendMessage("Your magenta key has gone!");
						doDoor();
						if (owner.getY() == 3462)
							owner.teleport(owner.getX(), owner.getY() - 1, false);
						else
							owner.teleport(owner.getX(), owner.getY() + 1, false);
					} else{
						owner.sendMessage("Nothing interesting happens");
					}
					break;
				case 128: // Black door key ( lesser room )
					if (item.getID() == 395 && owner.getInventory().remove(new InvItem(395,1)) > -1){
						owner.sendMessage("You unlock the door and go through it");
						owner.sendMessage("Your black key has gone!");
						doDoor();
						if (owner.getY() == 3460)
							owner.teleport(owner.getX(), owner.getY() - 1, false);
						else
							owner.teleport(owner.getX(), owner.getY() + 1, false);
					} else{
						owner.sendMessage("Nothing interesting happens");
					}
					break;











				case 83:
					if (item.getID() == 595 && owner.getX() == 360) {
						if (owner.getY() == 3427) {
							//								Bubble bubble = new Bubble(owner.getIndex(), 595);
							for (Player p : owner.getViewArea().getPlayersInView())
							{
								p.watchItemBubble(owner.getIndex(), 595);
								//									p.informOfBubble(bubble);
							}
							owner.sendMessage("You unlock the door and go through it");
							doDoor();
							owner.teleport(360, 3428, false);
						} else if (owner.getY() == 3428) {
							//								Bubble bubble = new Bubble(owner.getIndex(), 595);
							for (Player p : owner.getViewArea().getPlayersInView())
							{
								p.watchItemBubble(owner.getIndex(), 595);
								//									p.informOfBubble(bubble);
							}
							owner.sendMessage("You unlock the door and go through it");
							doDoor();
							owner.teleport(360, 3427, false);
						}
					} else if (item.getID() == 596 && owner.getY() == 3353) {
						if (owner.getX() == 354) {
							for (Player p : owner.getViewArea().getPlayersInView())
							{
								p.watchItemBubble(owner.getIndex(), 596);
							}
							owner.sendMessage("You unlock the door and go through it");
							doDoor();
							owner.teleport(355, 3353, false);
						} else if (owner.getX() == 355) {
							for (Player p : owner.getViewArea().getPlayersInView())
							{
								p.watchItemBubble(owner.getIndex(), 596);
							}
							owner.sendMessage("You unlock the door and go through it");
							doDoor();
							owner.teleport(354, 3353, false);
						}
					}
					break;
				case 35: //Draynor Manor Skeleton Closet
					if(item.getID() == 212) {
						doDoor();
						owner.sendMessage("You unlock the door");
						owner.sendMessage("You go through the door");							
						if(owner.getX() == 212) {
							owner.teleport(211, 545, false);
						}
						else {
							owner.teleport(212, 545, false);
						}
					} else {
						owner.sendMessage("Nothing interesting happens");
					}
					break;
				case 45: //Prince Ali Rescue Door
					if(item.getID() == 242) {
						if(owner.getX() == 198) {
							Quest q = owner.getQuest(Quests.PRINCE_ALI_RESCUE);
							if(q != null) {
								switch(q.getStage()) {
								case 0:
								case 1:
								case 2:
									owner.sendMessage("You must disable the guard and tie up Keli first!");
									break;
								case 3:
									owner.sendMessage("You must tie up Keli first!");
									break;
								case 4:
									Npc ladyKeli = World.getNpc(123, 194, 198, 637, 642);
									if(ladyKeli == null) {
										//												Bubble bubble = new Bubble(owner.getIndex(), 242);
										for (Player p : owner.getViewArea().getPlayersInView())
										{
											p.watchItemBubble(owner.getIndex(), 242);
											//													p.informOfBubble(bubble);
										}
										owner.sendMessage("You unlock the door and go through it");
										doDoor();
										owner.teleport(199, 640, false);
									} else {
										owner.sendMessage("You must tie up Keli first!");
									}
									break;
								case 5:
									owner.sendMessage("You have already completed this quest!");
								}
							} else {
								owner.sendMessage("Nothing interesting happens");
							}
						} else {
							doDoor();
							owner.teleport(198, 640, false);
						}
					} else {
						owner.sendMessage("Nothing interesting happens");
					}
					break;				


				default:
					owner.sendMessage("Nothing interesting happens");
					return;
				}
				owner.sendInventory();
			}

			private boolean itemId(int[] ids) {
				return DataConversions.inArray(ids, item.getID());
			}
		});
	}
}
