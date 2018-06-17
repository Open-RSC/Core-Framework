package org.openrsc.server.packethandler;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.DoorDef;
import org.openrsc.server.entityhandling.defs.extras.PicklockDoorDefinition;
import org.openrsc.server.event.DelayedGenericMessage;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.event.ShortEvent;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.event.WalkToPointEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.eventLog;
import org.openrsc.server.model.AgilityHandler;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.GameObject;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Item;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Point;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.states.Action;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;

import com.rscdaemon.scripting.ScriptCache;
import com.rscdaemon.scripting.ScriptError;
import com.rscdaemon.scripting.ScriptVariable;
import com.rscdaemon.scripting.listener.UseWallObjectListener;
public class WallObjectAction implements PacketHandler {

	private final ScriptCache<UseWallObjectListener> scriptCache = new ScriptCache<>();
	
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			int pID = ((RSCPacket)p).getID();
			if (player.isBusy() && player.getStatus() == Action.IDLE) {
				player.resetPath();
				return;
			}
			player.resetAll();
			short x = p.readShort();
			short y = p.readShort();
			final GameObject object = World.getZone(x, y).getDoorAt(x, y);
			final int click = pID == 27 ? 0 : 1;
			if (object == null) {
				//LOG THIS
				return;
			}
			player.setStatus(Action.USING_DOOR);
			World.getDelayedEventHandler().add(new WalkToPointEvent(player, object.getLocation(), 1, false) {
				
				private void replaceGameObject(int newID, boolean open) {
					owner.sendSound(open ? "opendoor" : "closedoor", false);
					World.unregisterEntity(object);
					World.registerEntity(new GameObject(object.getLocation(), newID, object.getDirection(), object.getType()));
				}
				
				private void doDoor() {
					owner.sendSound("opendoor", false);
					World.registerEntity(new GameObject(object.getLocation(), 11, object.getDirection(), object.getType()));
					World.delayedSpawnObject(object.getLoc(), 1000);
				}
				
				public void arrived() {
					owner.resetPath();
					DoorDef def = object.getDoorDef();
					if(owner.isBusy() || owner.isRanging() || !owner.nextTo(object) || def == null || owner.getStatus() != Action.USING_DOOR) {
						return;
					}
					owner.resetAll();
					String command = (click == 0 ? def.getCommand1() : def.getCommand2()).toLowerCase();
					// No events should fire if there is an active script
					if(owner.getScript() != null)
					{
						return;
					}
					// Try to retrieve a script from the cache
					UseWallObjectListener script = scriptCache.get(object.getID());
					try
					{
						// If the script was found in the cache, try to run it
						if(script != null)
						{
							script = script.getClass().newInstance();
							script.Bind(ScriptVariable.OWNER, owner);
							script.Bind(ScriptVariable.DOOR_TARGET, object);
							owner.setScript(script);
							if(script.onWallObjectUsed(owner, object, click == 0 ? 0 : 1))
							{
								script.run();
								return;
							}
						}
						
						// If the script wasn't ran, search for one.
						for(UseWallObjectListener listener : World.getScriptManager().<UseWallObjectListener>getListeners(UseWallObjectListener.class))
						{
							script = listener.getClass().newInstance();
							script.Bind(ScriptVariable.OWNER, owner);
							script.Bind(ScriptVariable.DOOR_TARGET, object);
							owner.setScript(script);
							if(script.onWallObjectUsed(owner, object, click == 0 ? 0 : 1))
							{
								script.run();
								scriptCache.put(object.getID(), listener);
								return;
							}
						}
						if(script != null)
						{
							// If no script was found, manually clean up
							script.__internal_unbind_all();
							owner.setScript(null);
						}
					}
					catch(IllegalAccessException | InstantiationException e)
					{
						if(script != null)
						{
							script.__internal_unbind_all();
							owner.setScript(null);
						}
						throw (ScriptError)new ScriptError(script, e.getMessage()).initCause(e);
					}

					Point telePoint = EntityHandler.getObjectTelePoint(object.getLocation(), command);

					if(telePoint != null) {
						owner.teleport(telePoint.getX(), telePoint.getY(), false);
					}
					else if(AgilityHandler.doEvent(owner, object.getID())){}
					else {
						if (command.equals("pick lock")) {
							handlePickLock();
							return;
						}
						switch(object.getID()) {
							case 1:
								replaceGameObject(2, false);
							break;
							case 2:
								if (object.getX() == 94 && object.getY() == 2951)
								{
									if (player.npcKillCount < 20)
									{
										player.sendMessage("You must kill 20 NPC's prior to entering this chamber");
										return;
									}
									
									replaceGameObject(1, true);
								}
								else
								{
									replaceGameObject(1, true);
								}
							break;
							case 9:
								replaceGameObject(8, false);
								break;
							case 8:
								replaceGameObject(9, true);
								break;
							case 20:
							case 23:
								owner.sendMessage("The door is locked");
								break;
							case 51:
						        if (owner.getY() == 536) 
								{
									doDoor();
									owner.teleport(666, 535, false);
						        } 
								else
							    {
									doDoor();
									owner.teleport(666, 536, false);
								}
							break;
							case 55:
								// Falador custom doors.
								if (object.getX() == 269 && object.getY() == 557)
								{
									owner.sendMessage("You walk through the door.");
									if (owner.getX() == 269)
									{
										doDoor();
										owner.teleport(268, 557, false);
									}
									else
									{
										doDoor();
										owner.teleport(269, 557, false);
									}
									return;
								}
								
								if (object.getX() == 269 && object.getY() == 553)
								{
									owner.sendMessage("You walk through the door.");
									if (owner.getX() == 269)
									{
										doDoor();
										owner.teleport(268, 553, false);
									}
									else
									{
										doDoor();
										owner.teleport(269, 553, false);
									}
									return;
								}
								
								if (object.getX() == 269 && object.getY() == 549)
								{
									owner.sendMessage("You walk through the door.");
									if (owner.getX() == 269)
									{
										doDoor();
										owner.teleport(268, 549, false);
									}
									else
									{
										doDoor();
										owner.teleport(269, 549, false);
									}
									return;
								}
								
								if (object.getX() == 262 && object.getY() == 543)
								{
									if (owner.getSkillTotal() < 1600)
									{
										owner.sendMessage(Config.PREFIX + "Only members with a skill total of 1600 can enter here.");
										return;
									}
									
									owner.sendMessage("You walk through the door.");
									if (owner.getY() == 543)
									{
										doDoor();
										owner.teleport(262, 542, false);
									}
									else
									{
										doDoor();
										owner.teleport(262, 543, false);
									}
									return;
								}
								
								
								if (object.getX() == 261 && object.getY() == 541)
								{
									owner.sendMessage("You walk through the door.");
									if (owner.getX() == 261)
									{
										doDoor();
										owner.teleport(260, 541, false);
									}
									else
									{
										doDoor();
										owner.teleport(261, 541, false);
									}
									return;
								}
								
								if (object.getX() == 259 && object.getY() == 549)
								{
									owner.sendMessage("You walk through the door.");
									if (owner.getX() == 259)
									{
										doDoor();
										owner.teleport(258, 549, false);
									}
									else
									{
										doDoor();
										owner.teleport(259, 549, false);
									}
									return;								
								}
								
								if (object.getX() == 259 && object.getY() == 553)
								{
									owner.sendMessage("You walk through the door.");
									if (owner.getX() == 259)
									{
										doDoor();
										owner.teleport(258, 553, false);
									}
									else
									{
										doDoor();
										owner.teleport(259, 553, false);
									}
									return;								
								}
								
								
								/*
								 * End Falador 
								 * Doors.
								 */
								
								if (owner.getCurStat(14) >= 60)
								{
									if (owner.getY() == 3380)
									{
										doDoor();
										owner.teleport(268, 3381, false);
									}
									else
									{
										doDoor();
										owner.teleport(268, 3380, false);
									}
									return;
								}
								else
								{
									owner.setBusy(true);
									Npc dwarf = World.getNpc(owner.getX(), owner.getY(), 5);
									if (dwarf != null) {
										for (Player informee : dwarf.getViewArea().getPlayersInView())
											informee.informOfNpcMessage(new ChatMessage(dwarf, "Sorry only the top miners are allowed in there", owner));
									}
									World.getDelayedEventHandler().add(new ShortEvent(owner) {
										public void action() {
											owner.setBusy(false);
											owner.sendMessage("You need a mining of level 60 to enter");
										}
									});
								}
							break;
							
							//mourner HQ door
							//action: Open
							case 138:
						        if (owner.getY() == 572) 
								{
						         doDoor();
						         owner.teleport(633, 573, false);
								 owner.sendMessage("You go through the door");
						        }
								else
								{
									if(owner.getQuest(38) == null)
									{
										owner.sendMessage("The door is locked");
									}
									else
									{
										owner.setBusy(true);
										Npc mourner = World.getNpc(451, 634, 634, 573, 573); //change coords to npc location
										if(owner.getQuest(38) != null && owner.getQuest(38).getStage() >= 4)
										{
											mourner.blockedBy(owner);
											if(player.getInventory().wielding(802)) //wield check doctor's robe
											{
												if(mourner != null) 
												{
													for (Player informee : mourner.getViewArea().getPlayersInView())
													informee.informOfNpcMessage(new ChatMessage(mourner, "In you go doc", owner));										
												}
												World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
												{
													public void action()
													{
														owner.sendMessage("You go through the door");
														owner.teleport(633, 572, false);; //change coords
														owner.setBusy(false);
														mourner.unblock();
													}
												});
											}
											else
											{
												if(mourner != null) 
												{
													for (Player informee : mourner.getViewArea().getPlayersInView())
													informee.informOfNpcMessage(new ChatMessage(mourner, "Keep away from there", owner));										
												}
												World.getDelayedEventHandler().add(new DelayedQuestChat(owner, mourner, new String[] {"Why?"})
												{
													public void finished()
													{
														World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"Several mourners are ill with food poisoning", "We're waiting for a doctor"})
														{
															public void finished()
															{
																owner.setBusy(false);
																mourner.unblock();
															}
														});
													}
												});
											}
										}
										else
										{
											//if player isnt on correct stage do this
											owner.sendMessage("The door is locked");
											World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
											{
												public void action()
												{
													owner.sendMessage("Inside you can hear the mourners eating");
													World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
													{
														public void action()
														{
															owner.sendMessage("You need to distract them from their stew");
															owner.setBusy(false);
														}
													});
												}
											});
										}
									}
								}
							break;
							
							case 141:
								if (owner.getY() == 1513) 
								{
									doDoor();
									owner.teleport(633, 1512, false);
									owner.sendMessage("You go through the door");
						        } 
								else
								{
									doDoor();
									owner.teleport(633, 1513, false);
									owner.sendMessage("You go through the door");
								}
							break;
							
							//BlackArm Brimhaven Hideout
							//action: Open
							case 76:
						        if (owner.getY() == 693) 
								{
						         doDoor();
						         owner.teleport(439, 694, false);
								 owner.sendMessage("You go through the door");
						        }
								else
								{
									if(owner.getQuest(20) == null || owner.getQuest(20) != null && owner.getQuest(20).finished() || owner.getQuest(52) != null &&  owner.getQuest(52).finished())
									{
										owner.sendMessage("The door is locked");
										return;
									}
									else
									{
										owner.setBusy(true);
										Npc grubor = World.getNpc(255, 436, 440, 691, 697); //change coords to npc location
										if(owner.getQuest(20) != null && owner.getQuest(20).getStage() == 2)
										{
											grubor.blockedBy(owner);
											if(grubor != null) 
											{
												for (Player informee : grubor.getViewArea().getPlayersInView())
												informee.informOfNpcMessage(new ChatMessage(grubor, "Yes? What do you want?", owner));										
											}
											World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
											{
												public void action()
												{
													final String[] options107 = {"Four leaved clover", "Rabbit's foot", "Lucky Horseshoe", "Black cat"};
													owner.setBusy(false);
													owner.sendMenu(options107);
													owner.setMenuHandler(new MenuHandler(options107) 
													{
														public void handleReply(final int option, final String reply)
														{
															owner.setBusy(true);
															for(Player informee : owner.getViewArea().getPlayersInView())
															{
																informee.informOfChatMessage(new ChatMessage(owner, reply, grubor));
															}
															switch(option) 
															{
																case 0:
																	World.getDelayedEventHandler().add(new DelayedQuestChat(grubor, owner, new String[] {"Oh, you're one of the gang are you?", "Just a second I'll let you in"})
																	{
																		public void finished()
																		{
																			World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
																			{
																				public void action()
																				{
																					owner.incQuestCompletionStage(20);
																					owner.sendMessage("You hear the door being unbarred");
																					owner.setBusy(false);
																					grubor.unblock();
																				}
																			});
																		}
																	});
																break;
																case 1:
																case 2:
																case 3:
																	World.getDelayedEventHandler().add(new DelayedQuestChat(grubor, owner, new String[] {"What? Get out of here!"})
																	{
																		public void finished()
																		{
																			owner.setBusy(false);
																			grubor.unblock();
																		}
																	});
																break;
															}
														}
													});
												}
											});
										}
										else if(owner.getQuest(51) != null && owner.getQuest(51).finished() && owner.getQuest(20) != null && owner.getQuest(20).getStage() >= 3)
										{
											doDoor();
											owner.teleport(439, 693, false);
											owner.sendMessage("You go through the door");
											owner.setBusy(false);
										}
									}
								}
							break;
							
							//Phoenixgang Hideout door
							//action: Open
							case 78:
						        if (owner.getY() == 681) 
								{
						            doDoor();
									owner.teleport(448, 682, false);
									owner.sendMessage("You go through the door");
						        }
								else
								{
									if(owner.getQuest(20).getStage() >= 3 && owner.getQuest(52) != null && owner.getQuest(52).finished())
									{
										doDoor();
										owner.teleport(448, 681, false);
										owner.sendMessage("You go through the door");
									}
									else
									{
										owner.sendMessage("The door is locked");
									}
								}
							break;
							
							//Scarface Pete's Mansion Door
							//action: Open
							case 77:
						        if (owner.getY() == 680) 
								{
						         doDoor();
						         owner.teleport(463, 681, false);
								 owner.sendMessage("You go through the door");
						        }
								else
								{
									if(owner.getQuest(20) == null || owner.getQuest(20) != null &&  owner.getQuest(20).finished() || owner.getQuest(52) != null &&  owner.getQuest(52).finished())
									{
										owner.sendMessage("The door is locked");
										return;
									}
									else
									{
										owner.setBusy(true);
										Npc garv = World.getNpc(257, 459, 466, 681, 685);
										if(owner.getQuest(20) != null && owner.getQuest(20).getStage() == 4)
										{
											garv.blockedBy(owner);
											if(garv != null) 
											{
												for (Player informee : garv.getViewArea().getPlayersInView())
												informee.informOfNpcMessage(new ChatMessage(garv, "Where do you think you're going?", owner));										
											}
											if(owner.getInventory().wielding(196) && owner.getInventory().wielding(248) && owner.getInventory().wielding(230) && owner.getInventory().countId(573) > 0)
											{
												World.getDelayedEventHandler().add(new DelayedQuestChat(owner, garv, new String[] {"Hi, I'm Hartigen", "I've come to work here"})
												{
													public void finished()
													{
														World.getDelayedEventHandler().add(new DelayedQuestChat(garv, owner, new String[] {"So have you got your i.d paper?"})
														{
															public void finished()
															{
																owner.sendMessage("You show Garv Hartigen's ID paper");
																World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
																{
																	public void action()
																	{
																		World.getDelayedEventHandler().add(new DelayedQuestChat(garv, owner, new String[] {"You had better come in then", "Grip will want to talk to you"})
																		{
																			public void finished()
																			{
																				doDoor();
																				owner.setBusy(false);
																				garv.unblock();
																				owner.teleport(463, 680, false);
																				owner.sendMessage("You go through the door");
																			}
																		});
																	}
																});
															}
														});
													}
												});
											}
											else
											{
												World.getDelayedEventHandler().add(new DelayedQuestChat(owner, garv, new String[] {"Oh no where sorry"})
												{
													public void finished()
													{
														owner.setBusy(false);
														garv.unblock();
													}
												});
											}
										}
										else
										{
											if(garv != null) 
											{
												for (Player informee : garv.getViewArea().getPlayersInView())
												informee.informOfNpcMessage(new ChatMessage(garv, "Hey get away from there!", owner));										
											}
											World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
											{
												public void action()
												{
													owner.sendMessage("You need a way to get by this guard");
													owner.setBusy(false);
													garv.unblock();
												}
											});
										}
									}
								}
							break;
							
							//Guidor's house door
							//action: Open
							case 145:
						        if (owner.getX() == 82) 
								{
									doDoor();
									owner.teleport(83, 534, false);
									owner.sendMessage("You go through the door");
						        }
								else
								{
									owner.setBusy(true);
									Npc wife = World.getNpc(488, 83, 86, 532, 535);
									wife.blockedBy(owner);
									if (player.getInventory().wielding(807) && player.getInventory().wielding(808)) //check if player is wearing priest robes
									{
										doDoor();
										owner.teleport(82, 534, false);
										owner.sendMessage("Guidor's wife allows you to go in");
										owner.sendMessage("You go through the door");	
										owner.setBusy(false);
										wife.unblock();
									}
									else
									{
										if(wife != null) 
										{
											for (Player informee : wife.getViewArea().getPlayersInView())
											informee.informOfNpcMessage(new ChatMessage(wife, "Hey get away from there, my husband is very sick", owner));										
										}
										World.getDelayedEventHandler().add(new DelayedQuestChat(wife, owner, new String[] {"We are waiting for a priest to arrive"})
										{
											public void finished()
											{
												World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
												{
													public void action()
													{
														owner.sendMessage("Maybe you should dress up like a priest to get inside?");
														owner.setBusy(false);
														wife.unblock();
													}
												});
											}
										});
									}
								}
							break;
							
							//Elena's house door
							//action: Open
							case 152:
								Quest LC = owner.getQuest(38);
								Quest plagueCity = owner.getQuest(35);
								
								if (owner.getX() == 607) 
								{
									doDoor();
									owner.teleport(608, 573, false);
						        } 
								else 
								{
									if (LC == null)
									{
										if (plagueCity != null && plagueCity.finished())
										{
											doDoor();
											owner.teleport(607, 573, false);
										}
										else
										{
											owner.sendMessage("The door is locked");
										}
									}
									else if (LC != null)
									{
										doDoor();
										owner.teleport(607, 573, false);
									}
								}
							break;
							
							case 67: // Lost City Door								
								if (object.getX() == 116 && owner.getY() == 3537)
								{
									owner.sendMessage(Config.PREFIX + "Please use the other door");
									return;
								}
							
								if (owner.getY() >= 3539)
								{
									doDoor();
									owner.teleport(117, 3538);
									return;
								}
								
								Npc Doorman = World.getNpc(owner.getX(), owner.getY(), 5);
								owner.setBusy(true);
								Doorman.blockedBy(player);
								if (Doorman != null)
								{
										World.getDelayedEventHandler().add(new DelayedQuestChat(Doorman, owner, new String[] {"You cannot go through this door without paying the trading tax "}, true) {
										public void finished() {
										World.getDelayedEventHandler().add(new DelayedQuestChat(owner, Doorman, new String[] {" What do I need to pay? "}) {
											public void finished() {
												World.getDelayedEventHandler().add(new DelayedQuestChat(Doorman, owner, new String[] {"One diamond "}) {
													public void finished() {
														World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
															public void action() {
																final String[] options = { "Okay", "A diamond, are you crazy?", "I haven't brought any diamonds with me" };
																owner.setBusy(false);
																owner.sendMenu(options);
																owner.setMenuHandler(new MenuHandler(options)
																{
																	public void handleReply(final int option, final String reply) 
																	{
																		owner.setBusy(true);
																		for(Player informee : owner.getViewArea().getPlayersInView()) {
																			informee.informOfChatMessage(new ChatMessage(owner, reply, Doorman));
																		}
																		switch(option) 
																		{
																			case 0:
																				if (owner.getInventory().contains(161, 1))
																				{
																					World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
																						public void action() {
																							owner.sendMessage("You give the doorman a diamond.");
																							doDoor();
																							owner.getInventory().remove(161, 1);
																							owner.sendInventory();
																							owner.teleport(117, 3539);
																							owner.setBusy(false);
																							Doorman.unblock();
																						}
																					});
																				}
																				else
																				{
																					World.getDelayedEventHandler().add(new DelayedQuestChat(owner, Doorman, new String[] {"Hmm, it appears I do not have a diamond on me."}) {
																						public void finished() {
																							World.getDelayedEventHandler().add(new DelayedQuestChat(Doorman, owner, new String[] {"Sorry, I cannot let you through that door without one."}) {
																								public void finished()
																								{
																									owner.setBusy(false);
																									Doorman.unblock();	
																								}
																							});
																						}
																					});
																				}
																			break;
																			
																			case 1:
																				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
																					public void action() {
																						World.getDelayedEventHandler().add(new DelayedQuestChat(Doorman, owner, new String[] {"Crazy is what great items are for trade through that door."}) {
																							public void finished() {
																								Doorman.unblock();
																								player.setBusy(false);
																							}																					
																						});
																					}
																				});
																			break;
																			
																			case 2:
																				World.getDelayedEventHandler().add(new DelayedQuestChat(Doorman, owner, new String[] {"Sorry, I cannot let you through that door without one."}) {
																					public void finished()
																					{
																						owner.setBusy(false);
																						Doorman.unblock();	
																					}
																				});
																			break;
																		}
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
								}
							break;
							/*case 67: //Lost City Door
								Doorman doorman = (Doorman) World.executors.get(221);
								if(doorman != null) {
									doorman.setOwner(owner);
									doorman.setDoor(object);
									Npc doormanNpc;
									if(owner.getX() < 116) {
										doormanNpc = World.getNpc(221, 113, 115, 3536, 3541);
									} else if(owner.getY() > 3538) {
										doormanNpc = World.getNpc(221, 115, 118, 3539, 3541);
									} else {
										doormanNpc = World.getNpc(221, 116, 120, 3534, 3538);
									}
									if(doormanNpc != null) {
										doorman.setNpc(doormanNpc);
										doormanNpc.updateSprite(owner.getX(), owner.getY());
										owner.updateSprite(doormanNpc.getX(), doormanNpc.getY());
										World.eventPool.assignTask(doorman);
									}
								}
								break;*/
							case 193: //Rails for Dwarf Cannon
								owner.sendMessage("You search the railing");
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
									public void action() {
										owner.sendMessage("but find nothing of interest");
									}
								});
								break;
							case 181:	
							case 182:
							case 183:
							case 184:
							case 185:
							case 186:
								if(owner.isRailingFixed(object.getID())) {
									owner.sendMessage("you have already fixed this railing");
								} else {
									owner.sendMessage("You search the railing");
									World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
										public void action() {
											owner.sendMessage("one railing is broken and needs to be replaced");
											World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
												public void action() {
													if(owner.getInventory().countId(995) > 0) {
														String[] options = new String[] {"try to replace the railing", "leave it be"};
														owner.setMenuHandler(new MenuHandler(options) {
	
															@Override
															public void handleReply(int option,	String reply) {
																switch(option) {
																	case 0:
																		owner.getInventory().remove(995, 1);
																		owner.sendInventory();
																		owner.sendMessage("you attempt to replace the missing railing");
																		owner.sendMessage("you replace the railing with no problems");
																		owner.fixRailing(object.getID());
																}
																
															}
														});
														owner.sendMenu(options);
													} else {
														owner.sendMessage("you'll need to get a railing from lawgof to fix it");
													}
												}
											});
										}
									});
								}
								break;
							case 194: //Dwarf Cannon Door [QUID46]
								Quest dwarfCannon = owner.getQuest(46);
								if(dwarfCannon != null) {
									if(dwarfCannon.getStage() == 4) {
										doDoor();
										owner.teleport(owner.getX() > 604 ? 604 : 605, 468);
										owner.sendMessage("You go through the door");
									} else {
										owner.sendMessage("The door is locked");
									}
								} else {
									owner.sendMessage("The door is locked");
								}
								break;
							case 197: //Dwarf Cannon Door to the engineer [QUID46]
								Quest dwarfCannon2 = owner.getQuest(46);
								if(dwarfCannon2 != null) {
									if(dwarfCannon2.finished() || dwarfCannon2.getStage() > 4) {
										doDoor();
										owner.teleport(owner.getX() > 277 ? 277 : 278, owner.getY());
										owner.sendMessage("You go through the door");
									} else {
										owner.sendMessage("The door is locked");
									}
								} else {
									owner.sendMessage("The door is locked");
								}
								break;
								
							case 66: //Lost City Door
								Quest lostCity = owner.getQuest(19);
								if(lostCity != null) {
									if(lostCity.finished()) {
										if(owner.isWearing(509) && owner.getX() > 125) {
											owner.setBusy(true);
											World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"The world starts to shimmer", "You find yourself in different surroundings"}, 2500) {
												public void finished() {
													owner.teleport(127, 3518);
													owner.setBusy(false);
												}
											});
										} else {
											doDoor();
											owner.sendMessage("You go through the door");
											owner.teleport( owner.getX() < 126 ? 126 : 125, 686);
										}
									} else {
										if(lostCity.getStage() == 4) {
											if(owner.isWearing(509) && owner.getX() > 125) {
												owner.setBusy(true);
												World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"The world starts to shimmer", "You find yourself in different surroundings"}, 2500) {
													public void finished() {
														owner.teleport(127, 3518);
														owner.finishQuest(19);
														owner.sendMessage("@gre@You have completed The Lost City quest!");
														owner.sendMessage("@gre@You have gained 2 quest points!");
														owner.setBusy(false);
														Logger.log(new eventLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), DataConversions.getTimeStamp(), "<strong>" + owner.getUsername() + "</strong>" + " has completed the <span class=\"recent_quest\">Lost City</span> quest!"));
													}
												});

											} else {
												doDoor();
												owner.sendMessage("You go through the door");
												owner.teleport( owner.getX() < 126 ? 126 : 125, 686);
											}
										} else {
											doDoor();
											owner.sendMessage("You go through the door");
											owner.teleport( owner.getX() < 126 ? 126 : 125, 686);
										}
									}
								} else {
									// this should work as expected?
									doDoor();
									owner.sendMessage("You go through the door");
									owner.teleport( owner.getX() < 126 ? 126 : 125, 686);
								}
								break;
								
								/*
								 * Merlins Crystal,
								 * Grums Goldsmith Shop
								 * Location: Port Sarim.
								 */
								case 69:
									Quest mc = owner.getQuest(22);
									if (mc != null) {
										if (mc.getStage() == 6)
										{
											Npc beggar = World.getNpc(286, 275, 632, 275, 632);
											if (beggar == null) {
												owner.sendMessage("A beggar approaches you from behind.");
												beggar = new Npc(286, 275, 632, 275, 275, 632, 632, true);
												World.registerEntity(beggar, 50000);
											} 
											owner.sendMessage("Grum the goldsmith peers at the beggar.");
										} else 
											if (owner.getX() == 276)
											{
												doDoor();
												owner.sendMessage("You go through the door");
												owner.teleport( owner.getX() < 276 ? 276 : 277, 632);
											} else 
											if (owner.getX() == 277) {
												doDoor();
												owner.sendMessage("You go through the door");
												owner.teleport( owner.getX() < 277 ? 277 : 276, 632);
											}
									} else 
										if (owner.getX() == 276)
										{
											doDoor();
											owner.sendMessage("You go through the door");
											owner.teleport( owner.getX() < 276 ? 276 : 277, 632);
										} else 
										if (owner.getX() == 277) {
											doDoor();
											owner.sendMessage("You go through the door");
											owner.teleport( owner.getX() < 277 ? 277 : 276, 632);
										}
								break;
							
							case 19:
								Quest blackarm2 = owner.getQuest(51);
								Quest phoenix = owner.getQuest(52);
								if(blackarm2 != null) {
									if(phoenix != null) {
										if(phoenix.finished()) {
											owner.setBusy(true);
											owner.sendMessage("You hear the doors being unbarred");
											World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
												public void action() {
													doDoor();
													owner.teleport(owner.getX(), owner.getY() + (owner.getY() < 3370 ? 1 : -1));
													owner.sendMessage("You go through the door");
													owner.setBusy(false);
												}
											});
										} else if(blackarm2.finished()) {
											owner.sendMessage("The door is securely locked");
										} else {
											owner.sendMessage("The door is securely locked");
										}
									} else {
										if(blackarm2.finished()) {
											owner.sendMessage("The door is securely locked");
										} else {
											owner.sendMessage("The door is securely locked");
										}
									}
								} else {
									if(phoenix != null) {
										if(phoenix.finished()) {
											owner.setBusy(true);
											owner.sendMessage("You hear the doors being unbarred");
											World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
												public void action() {
													doDoor();
													owner.teleport(owner.getX(), owner.getY() + (owner.getY() < 3370 ? 1 : -1));
													owner.sendMessage("You go through the door");
													owner.setBusy(false);
												}
											});
										} else {
											owner.sendMessage("The door is securely locked");
										}
									}
								}
								break;
							case 39:
								Quest blackarm = owner.getQuest(51);
								if(blackarm != null) {
									if(blackarm.finished()) {
										owner.setBusy(true);
										owner.sendMessage("You hear the doors being unbarred");
										World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
											public void action() {
												doDoor();
												owner.teleport(owner.getX(), owner.getY() + (owner.getY() > 532 ? -1 : 1));
												owner.sendMessage("You go through the door");
												owner.setBusy(false);
											}
										});
									} else {
										owner.sendMessage("The door is securely locked");
									}
								} else {
									owner.sendMessage("The door is securely locked");
								}
								break;
							case 45: //Prince Ali's Door
								if(owner.getX() == 199) {
									doDoor();
									owner.teleport(owner.getX() - 1, owner.getY(), false);
									owner.sendMessage("You go through the door");
								} else {
									owner.sendMessage("The door is locked");
								}
								break;
							case 47: //Wydin's Grocery Back Room Door [QUEST]
								if(owner.getX() == 276) { //OUTSIDE
									if(!owner.isGroceryStoreEmployee()) {
										final Npc wydin = World.getNpc(129, 271, 277, 654, 659);
										if(wydin != null) {
											wydin.updateSprite(owner.getX(), owner.getY());
											owner.updateSprite(wydin.getX(), wydin.getY());
											wydin.blockedBy(owner);
											owner.setBusy(true);											
											World.getDelayedEventHandler().add(new DelayedQuestChat(wydin, owner, new String[] {"Heh you can't go in there", "Only employees of the grocery store can go in"}, true) {
												public void finished() {
													World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)  {
														public void action() {
															owner.setBusy(false);
															String[] options = {"Well can I get a job here?", "Sorry I didn't realise"};
															owner.sendMenu(options);
															owner.setMenuHandler(new MenuHandler(options) {
																public void handleReply(final int option, final String reply) {
																	owner.setBusy(true);
																	for(Player informee : owner.getViewArea().getPlayersInView()) {
																		informee.informOfChatMessage(new ChatMessage(owner, reply, wydin));
																	}
																	switch(option) {
																		case 0:
																			World.getDelayedEventHandler().add(new DelayedQuestChat(wydin, owner, new String[] {"Well you're keen I'll give you that", "Ok I'll give you a go", "Have you got your own apron?"}) {
																				public void finished() {
																					if(owner.getInventory().wielding(182)) {
																						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, wydin, new String[] {"Yes I have one right here"}) {
																							public void finished() {
																								World.getDelayedEventHandler().add(new DelayedQuestChat(wydin, owner, new String[] {"Wow you are well prepared, you're hired", "Go through to the back room and tidy up for me please"}) {
																									public void finished() {
																										owner.hire();
																										owner.setBusy(false);
																										wydin.unblock();
																									}
																								});
																							}
																						});
																					} else {
																						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, wydin, new String[] {"No"}) {
																							public void finished() {
																								World.getDelayedEventHandler().add(new DelayedQuestChat(wydin, owner, new String[] {"Well you can't work here unless you have an apron", "Health and safety regulations, you understand"}) {
																									public void finished() {
																										owner.setBusy(false);
																										wydin.unblock();
																									}
																								});
																							}
																						});
																					}
																				}
																			});
																			break;
																		case 1:
																			owner.setBusy(false);
																			wydin.unblock();
																	}
																}
															});
														}
													});
												}
											});
										} else {
											owner.sendMessage("@red@An error was encountered with 'Pirates Treasure' : Null ID 129");
											owner.sendMessage("@red@Contact Zilent for support");
										}
									} else {
										if(owner.getInventory().wielding(182)) {
											doDoor();
											owner.teleport(owner.getX() + 1, owner.getY(), false);
										} else {
											final Npc wydin = World.getNpc(129, 271, 277, 654, 659);
											if(wydin != null) {
												wydin.updateSprite(owner.getX(), owner.getY());
												owner.updateSprite(wydin.getX(), wydin.getY());
												wydin.blockedBy(owner);
												owner.setBusy(true);												
												World.getDelayedEventHandler().add(new DelayedQuestChat(wydin, owner, new String[] {"Can you put your apron on before going in there please"}, true) {
													public void finished() {
														owner.setBusy(false);
														wydin.unblock();
													}
												});
											}
										}
									}
								} else {
									doDoor();
									owner.teleport(owner.getX() - 1, owner.getY(), false);
								}
								break;
								// Plaguehouse Door
							case 123:
						        if (owner.getY() == 606) 
								{
						         doDoor();
						         owner.teleport(637, 605, false);
						        } 
								else 
								{
									owner.setBusy(true);
									World.getDelayedEventHandler().add(new SingleEvent(owner, 0)
									{
										public void action()
										{
											owner.sendMessage("The door won't open");
											World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
											{
												public void action()
												{
													owner.sendMessage("You notice a black cross on the door");
													World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
													{
														public void action()
														{
															Npc mourner = World.getNpc(445, 638, 638, 605, 605);
															if(mourner != null) 
															{
																for (Player informee : mourner.getViewArea().getPlayersInView())
																informee.informOfNpcMessage(new ChatMessage(mourner, "I'd stand away from there", owner));										
															}
															World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"That black cross means that house has been touched by the plague"})
															{
																public void finished()
																{
																	if(owner.getInventory().countId(775) > 0 && owner.getQuest(35) != null && owner.getQuest(35).getStage() >= 12 ) 
																	{
																		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, mourner, new String[] {"I have a warrant from Bravek to enter here"})
																		{
																			public void finished()
																			{
																				World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"This is highly irregular", "Please wait while I speak to the head mourner"})
																				{
																					public void finished()
																					{
																						owner.sendMessage("You wait until the mourner's back is turned and sneak into the building");
																						doDoor();
																						owner.teleport(637, 606, false);
																						owner.setBusy(false);
																						mourner.unblock();	
																					}
																				});
																			}
																		});
																	}
																	else
																	{
																		World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
																		{
																			public void action()
																			{
																				if(owner.getQuest(35) != null && owner.getQuest(35).getStage() == 7)
																				{
																					final String[] options107 = {"But I think a kidnap victim is in here", "I fear not a mere plague", "Thanks for the warning"};
																					owner.setBusy(false);
																					owner.sendMenu(options107);
																					owner.setMenuHandler(new MenuHandler(options107) 
																					{
																						public void handleReply(final int option, final String reply)
																						{
																							owner.setBusy(true);
																							for(Player informee : owner.getViewArea().getPlayersInView())
																							{
																								informee.informOfChatMessage(new ChatMessage(owner, reply, mourner));
																							}
																							switch(option) 
																							{
																								case 0:
																									World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"Sounds unlikely", "Even kidnappers wouldn't go in there", "Even if someone is in there", "They're probably dead by now", "You also don't have a clearance to go in there"})
																									{
																										public void finished()
																										{
																											World.getDelayedEventHandler().add(new DelayedQuestChat(owner, mourner, new String[] {"How do I get clearance?"})
																											{
																												public void finished()
																												{
																													World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"Well you'd need to apply to the head mourner", "Or I suppose Bravek the city warder", "I wouldn't get your hopes up though"})
																													{
																														public void finished()
																														{
																															owner.incQuestCompletionStage(35);
																															owner.setBusy(false);
																															mourner.unblock();	
																														}
																													});
																												}
																											});																					
																										}
																									});
																								break;
																								case 1:
																									World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"That's irrelevant", "You don't have a clearance to go in there"})
																									{
																										public void finished()
																										{
																											World.getDelayedEventHandler().add(new DelayedQuestChat(owner, mourner, new String[] {"How do I get clearance?"})
																											{
																												public void finished()
																												{
																													World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"Well you'd need to apply to the head mourner", "Or I suppose Bravek the city warder", "I wouldn't get your hopes up though"})
																													{
																														public void finished()
																														{
																															owner.incQuestCompletionStage(35);
																															owner.setBusy(false);
																															mourner.unblock();	
																														}
																													});
																												}
																											});
																										}
																									});
																								break;
																								case 2:
																									owner.setBusy(false);
																									mourner.unblock();
																								break;
																							}
																						}
																					});
																				}
																				else
																				{
																					owner.setBusy(false);
																					mourner.unblock();
																				}
																			}
																		});	
																	}	
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
							
							case 50:
						        if (owner.getY() == 611) 
								{
						        	doDoor();
						         	owner.teleport(635, 612, false);
						        } 
								else 
								{
									owner.setBusy(true);
									World.getDelayedEventHandler().add(new SingleEvent(owner, 0)
									{
										public void action()
										{
											owner.sendMessage("The door won't open");
											World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
											{
												public void action()
												{
													owner.sendMessage("You notice a black cross on the door");
													World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
													{
														public void action()
														{
															Npc mourner = World.getNpc(445, 636, 636, 612, 612);
															if(mourner != null) 
															{
																for (Player informee : mourner.getViewArea().getPlayersInView())
																informee.informOfNpcMessage(new ChatMessage(mourner, "I'd stand away from there", owner));										
															}
															World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"That black cross means that house has been touched by the plague"})
															{
																public void finished()
																{
																	if(owner.getInventory().countId(775) > 0 && owner.getQuest(35) != null && owner.getQuest(35).getStage() >= 12 ) 
																	{
																		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, mourner, new String[] {"I have a warrant from Bravek to enter here"})
																		{
																			public void finished()
																			{
																				World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"This is highly irregular", "Please wait while I speak to the head mourner"})
																				{
																					public void finished()
																					{
																						owner.sendMessage("You wait until the mourner's back is turned and sneak into the building");
																						doDoor();
																						owner.teleport(635, 611, false);
																						owner.setBusy(false);
																						mourner.unblock();	
																					}
																				});
																			}
																		});
																	}
																	else
																	{
																		World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
																		{
																			public void action()
																			{
																				if(owner.getQuest(35) != null && owner.getQuest(35).getStage() == 7)
																				{
																					final String[] options107 = {"But I think a kidnap victim is in here", "I fear not a mere plague", "Thanks for the warning"};
																					owner.setBusy(false);
																					owner.sendMenu(options107);
																					owner.setMenuHandler(new MenuHandler(options107) 
																					{
																						public void handleReply(final int option, final String reply)
																						{
																							owner.setBusy(true);
																							for(Player informee : owner.getViewArea().getPlayersInView())
																							{
																								informee.informOfChatMessage(new ChatMessage(owner, reply, mourner));
																							}
																							switch(option) 
																							{
																								case 0:
																									World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"Sounds unlikely", "Even kidnappers wouldn't go in there", "Even if someone is in there", "They're probably dead by now", "You also don't have a clearance to go in there"})
																									{
																										public void finished()
																										{
																											World.getDelayedEventHandler().add(new DelayedQuestChat(owner, mourner, new String[] {"How do I get clearance?"})
																											{
																												public void finished()
																												{
																													World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"Well you'd need to apply to the head mourner", "Or I suppose Bravek the city warder", "I wouldn't get your hopes up though"})
																													{
																														public void finished()
																														{
																															owner.incQuestCompletionStage(35);
																															owner.setBusy(false);
																															mourner.unblock();	
																														}
																													});
																												}
																											});																					
																										}
																									});
																								break;
																								case 1:
																									World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"That's irrelevant", "You don't have a clearance to go in there"})
																									{
																										public void finished()
																										{
																											World.getDelayedEventHandler().add(new DelayedQuestChat(owner, mourner, new String[] {"How do I get clearance?"})
																											{
																												public void finished()
																												{
																													World.getDelayedEventHandler().add(new DelayedQuestChat(mourner, owner, new String[] {"Well you'd need to apply to the head mourner", "Or I suppose Bravek the city warder", "I wouldn't get your hopes up though"})
																													{
																														public void finished()
																														{
																															owner.incQuestCompletionStage(35);
																															owner.setBusy(false);
																															mourner.unblock();	
																														}
																													});
																												}
																											});
																										}
																									});
																								break;
																								case 2:
																									owner.setBusy(false);
																									mourner.unblock();
																								break;
																							}
																						}
																					});
																				}
																				else
																				{
																					owner.setBusy(false);
																					mourner.unblock();
																				}
																			}
																		});	
																	}	
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
							
							//Family house Plague City
							case 122:
								if (owner.getY() < 569) 
								{
						         doDoor();
						         owner.teleport(645, 569, false);
						        } 
								else 
								{
									if(owner.getQuest(35) != null && owner.getQuest(35).getStage() >= 7)
									{
										owner.sendMessage("You go through the door");
										doDoor();
										owner.teleport(645, 568, false);
									}
									else
									{
										owner.setBusy(true);
										owner.sendMessage("The door won't open");
										World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
										{
											public void action()
											{
												Npc ted = World.getNpc(446, 643, 648, 564, 568);
												if(ted != null) 
												{
													for (Player informee : ted.getViewArea().getPlayersInView())
													informee.informOfNpcMessage(new ChatMessage(ted, "Go away we don't want any", owner));										
												}
												if(owner.getInventory().countId(768) > 0)
												{
													World.getDelayedEventHandler().add(new DelayedQuestChat(owner, ted, new String[] {"I have come to return a book from Jethick"})
													{
														public void finished()
														{
															World.getDelayedEventHandler().add(new DelayedQuestChat(ted, owner, new String[] {"Ok I guess you can come in then"})
															{
																public void finished()
																{
																	owner.getInventory().remove(768, 1);
																	owner.sendInventory();
																	owner.incQuestCompletionStage(35);
																	owner.sendMessage("You go through the door");
																	doDoor();
																	owner.teleport(645, 568, false);
																	owner.setBusy(false);
																	ted.unblock();	
																}
															});
														}
													});
												}
												else
												{
													owner.setBusy(false);
													ted.unblock();
												}
											}
										});
									}
								}
							break;
							
							//Bravek's office door Plague City
							case 121:
								if (owner.getX() < 648) 
								{
						         doDoor();
						         owner.teleport(648, 585, false);
						        } 
								else 
								{
									if(owner.getQuest(35) != null && owner.getQuest(35).getStage() >= 9)
									{
										doDoor();
										owner.teleport(647, 585, false);
									}
									else
									{
										owner.sendMessage("The door is locked");
									}
								}
							break;
							
							case 59: //Dragon Slayer Door
								Quest dragonslayer = owner.getQuest(17);
								if(dragonslayer != null) {
									if(dragonslayer.getStage() == 3 && !dragonslayer.finished()) {
										doDoor();
										owner.sendMessage("You open the door and go through");
										if(owner.getX() == 413) {
											owner.teleport(owner.getX() + 1, owner.getY(), false);
										} else {
											owner.teleport(owner.getX() - 1, owner.getY(), false);
										}
										
									} else {
										owner.sendMessage("The door is locked");
									}
								} else {
									owner.sendMessage("The door is locked");
								}
								break;
							case 112: // Fishing Guild Door
								if(object.getX() != 586 || object.getY() != 524) {
									break;
								}
								if(owner.getY() > 523) {
									if(owner.getCurStat(10) < 68) {
										owner.setBusy(true);
										Npc masterFisher = World.getNpc(368, 582, 588, 524, 527);
										if(masterFisher != null) {
											for(Player informee : masterFisher.getViewArea().getPlayersInView()) {
												informee.informOfNpcMessage(new ChatMessage(masterFisher, "Hello only the top fishers are allowed in here", owner));
											}
										}
										World.getDelayedEventHandler().add(new ShortEvent(owner) {
											public void action() {
												owner.setBusy(false);
												owner.sendMessage("You need a fishing level of 68 to enter");
											}
										});
									}
									else {
										doDoor();
										owner.teleport(586, 523, false);
									}
								}
								else {
									doDoor();
									owner.teleport(586, 524, false);
								}
								break;
								
								case 65:
								
								if(object.getX() != 268 || object.getY() != 3381) {
									break;
								}
								
								if(owner.getY() <= 3380) {
									if(owner.getCurStat(14) < 60) {
										owner.setBusy(true);
										Npc dwarf = World.getNpc(191, 265, 270, 3379, 3380);
										if(dwarf != null) {
											for(Player informee : dwarf.getViewArea().getPlayersInView()) {
												informee.informOfNpcMessage(new ChatMessage(dwarf, "Hello only the top miners are allowed in here", owner));
											}
										}
										World.getDelayedEventHandler().add(new ShortEvent(owner) {
											public void action() {
												owner.setBusy(false);
												owner.sendMessage("You need a mining level of 60 to enter");
											}
										});
									}
									else {
										doDoor();
										owner.teleport(268, 3381, false);
									}
								}
								else {
									doDoor();
									owner.teleport(268, 3380, false);
								}
								break;
							case 44: // Champion Guild
								if(object.getX() != 150 || object.getY() != 554) {
									return;
								}
								if(owner.getY() <= 553) {
									if(owner.getQuestPoints() > 31) {
										doDoor();
										owner.teleport(150, 554, false);										
									} else {
										final Npc guildmaster = World.getNpc(111, 148, 152, 554, 562);
										if(guildmaster != null) {
											if(!guildmaster.isBusy()) {
												owner.setBusy(true);
												guildmaster.blockedBy(owner);
												World.getDelayedEventHandler().add(new DelayedQuestChat(guildmaster, owner, new String[] {"You have not proved yourself worthy to enter here yet"}, true) {
													public void finished() {
														World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
															public void action() {
																owner.setBusy(false);
																guildmaster.unblock();
																owner.sendMessage("The door won't open - you need at least 32 Quest Points.");																
															}
														});
													}
												});
											}
										}
									}
								} else {
									doDoor();
									owner.teleport(150, 553, false);
								}
								break;							
							case 68: // Crafting Guild Door
								if(object.getX() != 347 || object.getY() != 601) {
									return;
								}
								if(owner.getY() <= 600) {
									if(owner.getCurStat(12) < 40) {
										owner.setBusy(true);
										Npc master = World.getNpc(231, 341, 349, 599, 612);
										if(master != null) {
											for(Player informee : master.getViewArea().getPlayersInView()) {
												informee.informOfNpcMessage(new ChatMessage(master, "Hello only the top crafters are allowed in here", owner));
											}
										}
										World.getDelayedEventHandler().add(new ShortEvent(owner) {
												public void action() {
													owner.setBusy(false);
													owner.sendMessage("You need a crafting level of 40 to enter");
												}
											});
									}
									else if(!owner.getInventory().wielding(191)) {
										Npc master = World.getNpc(231, 341, 349, 599, 612);
										if(master != null) {
											owner.informOfNpcMessage(new ChatMessage(master, "Where is your apron?", owner));
										}
									}
									else {
										doDoor();
										owner.teleport(347, 601, false);
									}
								}
								else {
									doDoor();
									owner.teleport(347, 600, false);
								}
								break;
							case 43: // Cooking Guild Door
								if(object.getX() != 179 || object.getY() != 488) {
									break;
								}
								if(owner.getY() >= 488) {
									if(owner.getCurStat(7) < 32) {
										owner.setBusy(true);
										Npc chef = World.getNpc(133, 176, 181, 480, 487);
										if(chef != null) {
											for(Player informee : chef.getViewArea().getPlayersInView()) {
												informee.informOfNpcMessage(new ChatMessage(chef, "Hello only the top cooks are allowed in here", owner));
											}
										}
										World.getDelayedEventHandler().add(new ShortEvent(owner) {
												public void action() {
													owner.setBusy(false);
													owner.sendMessage("You need a cooking level of 32 to enter");
												}
											});
									}
									else if(!owner.getInventory().wielding(192)) {
										Npc chef = World.getNpc(133, 176, 181, 480, 487);
										if(chef != null) {
											for(Player informee : chef.getViewArea().getPlayersInView()) {
												informee.informOfNpcMessage(new ChatMessage(chef, "Where is your chef's hat?", owner));
											}
										}
									}
									else {
										doDoor();
										owner.teleport(179, 487, false);
									}
								}
								else {
									doDoor();
									owner.teleport(179, 488, false);
								}
								break;
							case 83: // Velrak's door & Suits of Armour door & Dusty Key door (Taverly Dungeon)
								if (owner.getX() == 360 && (owner.getY() == 3428 || owner.getY() == 3427)) { //Velrak's Door
									owner.sendMessage("The door is locked shut");
								} else if (owner.getY() == 3332) { //Suits of Armour Door
									if (owner.getX() == 374) {
										final Npc armour =  World.getNpc(206, 373, 375, 3330, 3334);
										if (armour != null) {
											owner.sendMessage("Suddenly the suit of armour comes to life!");
											armour.setAggressive(owner);
										} else {
											doDoor();
											owner.teleport(373, 3332, false);
										}
									} else if(owner.getX() == 373) {
										doDoor();
										owner.teleport(374, 3332, false);					
									}
								} else if (owner.getY() == 3353) {
									if (owner.getX() == 354 || owner.getX() == 355)
										owner.sendMessage("The door is locked shut");
								}
								break;
							case 146: // Magic Guild Door
								if (object.getX() != 599 || object.getY() != 757)
									break;
								if (owner.getX() <= 598) {
									if(owner.getCurStat(6) < 66) {
										owner.setBusy(true);
										Npc wizard = World.getNpc(513, 596, 597, 755, 758);
										if (wizard != null) {
											for (Player informee : wizard.getViewArea().getPlayersInView()) {
												informee.informOfNpcMessage(new ChatMessage(wizard, "Hello only the top wizards are allowed in here", owner));
											}
										}
										World.getDelayedEventHandler().add(new ShortEvent(owner) {
											public void action() {
												owner.setBusy(false);
												owner.sendMessage("You need a magic level of 66 to enter");
											}
										});
									}
									else {
										doDoor();
										owner.teleport(599, 757, false);
									}
								}
								else {
									doDoor();
									owner.teleport(598, 757, false);
								}
								break;
						//Heroes Guild door
						//action: Open
						case 74:
							if (owner.getY() == 440) 
							{
								doDoor();
								owner.teleport(372, 441, false);
								owner.sendMessage("You go through the door");
							} 
							else 
							{
								if (owner.getQuest(20).finished() && owner.getQuestPoints() >= 56) 
								{
									doDoor();
									owner.teleport(372, 440, false);
									owner.sendMessage("You go through the door");
								} 
								else 
								{
									owner.sendMessage("You need to complete The Heroes Quest");
									owner.sendMessage("And have at least 56 Quest Points to access this area");
									return;
								}
							}
							break;
							case 22:
								if(object.getX() == 273 && object.getY() == 435) {//black knight fortress passage A
									owner.sendSound("secretdoor", false);
									doDoor();
									owner.sendMessage("You just went through a secret door");
									if(owner.getY() == 435) {
										owner.teleport(273, 434, false);
									} else {
										owner.teleport(273, 435, false);
									}
								} else if(object.getX() == 281 && object.getY() == 2325) {
									owner.sendSound("secretdoor", false);
									doDoor();
									owner.sendMessage("You just went through a secret door");
									if(owner.getY() == 2325) {
										owner.teleport(281, 2324, false);
									} else {
										owner.teleport(281, 2325, false);
									}
								} else if(object.getX() == 219 && object.getY() == 3282) {
									owner.sendSound("secretdoor", false);
									doDoor();
									owner.sendMessage("You just went through a secret door");
									if(owner.getX() <= 218) {
										owner.teleport(219, 3282, false);
									}
									else {
										owner.teleport(218, 3282, false);
									}
								} else if(object.getX() == 634 && object.getY() == 3303) {
									owner.sendSound("secretdoor", false);
									doDoor();
									owner.sendMessage("You just went through a secret door");
									owner.teleport(owner.getX(), owner.getY() + (owner.getY() == 3303 ? -1 : 1), false);
								} else {
									owner.sendMessage("Nothing interesting happens");
								}
								break;
							case 58: // Karamja -> cranador wall
								if((object.getX() != 406 || object.getY() != 3518) && (object.getX() != 405 || object.getY() != 3518)) {
									return;
								}
								if (owner.getY() == 3517) {
									doDoor();
									owner.teleport(owner.getX(), 3518);
									return;
								}
								Quest q = owner.getQuest(17);
								if(q != null) {
									if(q.finished()) {
										doDoor();
										if(owner.getY() <= 3517) {
											owner.teleport(owner.getX(), 3518, false);
										} else {
											owner.teleport(owner.getX(), 3517, false);
										}		
									} else {
										owner.sendMessage("You cannot find a way through");
									}
								} else {
									owner.sendMessage("You cannot find a way through");
								}
								break;
							case 101: // Woodcutting guild secret exit
								if(owner.getX() == 539) 
								{
									owner.sendMessage("You push your way through");
									owner.teleport(540, 445, false);
								}
								else
								if (owner.getX() == 540)
								{
									owner.sendMessage("You push your way through");
									owner.teleport(539, 445, false);	
								}
								else 
								{
									owner.sendMessage("You can't seem to get through");
								}
							break;
							
							//Phoenixgang secret back door
							//action: Push
							case 79:
						        if (owner.getX() == 456) 
								{
						            doDoor();
									owner.teleport(455, 679, false);
									owner.sendMessage("You go through the hidden door");
						        }
								else
								{
									if(owner.getQuest(20).getStage() >= 4 && owner.getQuest(52).finished())
									{
										doDoor();
										owner.teleport(456, 679, false);
										owner.sendMessage("You go through the hidden door");
									}
									else
									{
										owner.sendMessage("The hidden door is sealed shut");
									}
								}
							break;
							
							//Grip's officer door
							//action: Open
							case 81:
						        if (owner.getY() == 675) 
								{
						            doDoor();
									owner.teleport(463, 676, false);
									owner.sendMessage("You go through the door");
						        }
								else
								{
									doDoor();
									owner.teleport(463, 675, false);
									owner.sendMessage("You go through the door");
								}
							break;
							
							//Scarface Pete's Treasure room door
							//action: Open
							case 82:
						        if (owner.getX() == 471) 
								{
						            doDoor();
									owner.teleport(472, 674, false);
									owner.sendMessage("You go through the door");
						        }
								else
								{
									if(owner.getInventory().countId(583) > 0 && owner.getQuest(20).getStage() == 4)
									{
										doDoor();
										owner.teleport(471, 674, false);
										owner.sendMessage("You unlock the door and go through");
									}
									else
									{
										owner.sendMessage("The door is locked");
									}
								}
							break;
							
							//Old Mansion entrance door
							//action: Open
							case 80:
						        if (owner.getY() == 673) 
								{
						            doDoor();
									owner.teleport(459, 674, false);
									owner.sendMessage("You go through the door");
						        }
								else
								{
									if(owner.getQuest(20).getStage() >= 4 && owner.getQuest(52).finished() && owner.getInventory().countId(582) > 0)
									{
										doDoor();
										owner.teleport(459, 673, false);
										owner.sendMessage("You unlock the door and go through");
									}
									else
									{
										owner.sendMessage("The door is locked");
									}
								}
							break;
							
							case 38: // Black Knight Guard Door
								if(object.getX() == 271 && object.getY() == 441) {
									if(owner.getX() <= 270) { //black knight outer guard door
										if(!owner.getInventory().wielding(7) || !owner.getInventory().wielding(104)) {
											final Npc guard = World.getNpc(100, 263, 270, 438, 445);
											if(guard != null) {
												for(Player informee : guard.getViewArea().getPlayersInView()) {
													informee.informOfNpcMessage(new ChatMessage(guard, "Hey, only guards are allowed inside!", owner));
												}
												World.getDelayedEventHandler().add(new ShortEvent(owner) {
													public void action() {
														owner.sendMessage("I need some sort of disguise in order to sneak in.");
													}
												});
											} else {
												owner.sendMessage("Only guards are allowed inside.");
											}
											return;
										}
										doDoor();
										owner.teleport(271, 441, false);
									} else {
										doDoor();
										owner.teleport(270, 441, false);
									}
								} else if(object.getX() == 275 && object.getY() == 439) {
									if(owner.getX() < 275) {
										final Npc guard = World.getNpc(100, 271, 274, 435, 442);
										if(guard != null) {
											owner.setBusy(true);
											guard.blockedBy(owner);
											World.getDelayedEventHandler().add(new DelayedQuestChat(guard, owner, new String[] {"I wouldn't go in there if I woz you", "Those black knights are in an important meeting", "They said they'd kill anyone who went in there"}) {
												public void finished() {
													owner.setBusy(false);
													String[] options = new String[]{"I don't care, I'm going in anyway.", "Oh, I think I'll stay out here then."};
													owner.setMenuHandler(new MenuHandler(options) {
														public void handleReply(final int option, final String reply) {
															for(Player informee : owner.getViewArea().getPlayersInView()) {
																informee.informOfChatMessage(new ChatMessage(owner, reply, guard));
															}
															switch(option) {
																case 0:
																	doDoor();
																	owner.teleport(275, 439, false);
																	guard.unblock();
																	break;
																default:
																	guard.unblock();
															}
														}
													});
													owner.sendMenu(options);													
												}
											});
										} else {
											doDoor();
											owner.teleport(275, 439, false);
										}
									} else {
										doDoor();
										owner.teleport(274, 439, false);
									}
								}
								break;
							case 25: //Oil Can Maze 'Door G'
								if(owner.willDoorGOpen()) {
									doDoor();
									if(owner.getX() == 225) {
										owner.teleport(224, 3376, false);
									} else {
										owner.teleport(225, 3376, false);
									}
								} else {
									owner.sendMessage("The door is locked");
								}
								break;
							case 26: //Oil Can Maze 'Door F'
								if(owner.willDoorFOpen()) {
									doDoor();
									if(owner.getX() == 228) {
										owner.teleport(227, 3376, false);
									} else {
										owner.teleport(228, 3376, false);
									}
								} else {
									owner.sendMessage("The door is locked");
								}
								break;
							case 27: //Oil Can Maze 'Door B'
								if(owner.willDoorBOpen()) {
									doDoor();
									if(owner.getX() == 224) {
										owner.teleport(225, 3379, false);
									} else {
										owner.teleport(224, 3379, false);
									}
								} else {
									owner.sendMessage("The door is locked");
								}
								break;
							case 28: //Oil Can Maze 'Door D'
								if(owner.willDoorDOpen()) {
									doDoor();
									if(owner.getX() == 227) {
										owner.teleport(228, 3379, false);
									} else {
										owner.teleport(227, 3379, false);
									}
								} else {
									owner.sendMessage("The door is locked");
								}
								break;
							case 29: //Oil Can Maze 'Door I'
								if(owner.willDoorIOpen()) {
									doDoor();
									if(owner.getX() == 227) {
										owner.teleport(228, 3382, false);
									} else {
										owner.teleport(227, 3382, false);
									}
								} else {
									owner.sendMessage("The door is locked");
								}
								break;
							//case 30: //Oil Can Maze 'Door G' See below
								
							case 33: //Oil Can Maze 'Door C'
								if(owner.willDoorCOpen()) {
									doDoor();
									if(owner.getY() == 3381) {
										owner.teleport(226, 3380, false);
									} else {
										owner.teleport(226, 3381, false);
									}
								} else {
									owner.sendMessage("The door is locked");
								}
								break;
							case 31: //Oil Can Maze 'Door E'
								if(owner.willDoorEOpen()) {
									doDoor();
									if(owner.getY() == 3378) {
										owner.teleport(229, 3377, false);
									} else {
										owner.teleport(229, 3378, false);
									}
								} else {
									owner.sendMessage("The door is locked");
								}
								break;
							case 32: //Oil Can Maze 'Door A'
								if(owner.willDoorAOpen()) {
									doDoor();
									if(owner.getY() == 3381) {
										owner.teleport(223, 3380, false);
									} else {
										owner.teleport(223, 3381, false);
									}
								} else {
									owner.sendMessage("The door is locked");
								}
								break;
							case 36: // Draynor mansion front door
								if(object.getX() != 210 || object.getY() != 553) {
									return;
								}
								if(owner.getY() >= 553) {
									doDoor();
									owner.teleport(210, 552, false);
								}
								else {
									owner.sendMessage("The door is locked shut");
								}
								break;
							case 37: // Draynor mansion back door
								if(object.getX() == 199 && object.getY() == 551) {
									if(owner.getY() >= 551) {
										doDoor();
										owner.teleport(199, 550, false);
									}
									else {
										owner.sendMessage("The door is locked shut");
									}
                                    break;
								} 
                                
                                Quest tutorialIsland = owner.getQuest(100);
                                if (tutorialIsland != null)
                                {
                                    // Guide, first door.
                                    if (object.getX() == 222 && object.getY() == 743)
                                    {
                                        if (tutorialIsland.getStage() >= 1)
                                        {
                                            if (owner.getX() == 222)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(221, 743);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(222, 743);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the guide before proceeding through this door.");
                                        }	
                                    }
                                    else
                                    // Controls Guide, second door.
                                    if (object.getX() == 224 && object.getY() == 737)
                                    {
                                        if (tutorialIsland.getStage() >= 2)
                                        {
                                            if (owner.getY() == 737)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(224, 736);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(224, 737);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the controls guide before proceeding through this door.");
                                        }
                                    }
                                    else
                                    // Combat Instructor, third door.
                                    if (object.getX() == 220 || object.getY() == 727)
                                    {
                                        if (tutorialIsland.getStage() >= 5)
                                        {
                                            if (owner.getX() == 220)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(219, 727);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(220, 727);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the combat instructor before proceeding through this door.");	
                                        }
                                    }
                                    else
                                    // Cooking Instructor, fourth door.
                                    if (object.getX() == 212 && object.getY() == 729)
                                    {
                                        if (tutorialIsland.getStage() >= 8)
                                        {
                                            if (owner.getX() == 212)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(211, 729);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(212, 729);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the cooking instructor before proceeding through this door.");	
                                        }
                                    }
                                    else
                                    // Financial Advisor, fifth door.
                                    if (object.getX() == 206 && object.getY() == 730)
                                    {
                                        if (tutorialIsland.getStage() >= 9)
                                        {
                                            if (owner.getX() == 206)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(205, 730);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(206, 730);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the financial advisor before proceeding through this door.");	
                                        }
                                    }
                                    else
                                    // Fishing Instructor, sixth door.
                                    if (object.getX() == 201 && object.getY() == 734)
                                    {
                                        if (tutorialIsland.getStage() >= 11)
                                        {
                                            if (owner.getX() == 201)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(201, 734);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(201, 733);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the fishing instructor before proceeding through this door.");	
                                        }
                                    }
                                    else
                                    // Mining Instructor, seventh door.
                                    if (object.getX() == 198 && object.getY() == 746)
                                    {
                                        if (tutorialIsland.getStage() >= 15)
                                        {
                                            if (owner.getY() == 745)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(198, 746);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(198, 745);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the mining instructor before proceeding through this door.");	
                                        }
                                    }
                                    else
                                    // Bank Assistant, eight door.
                                    if (object.getX() == 204 && object.getY() == 752)
                                    {
                                        if (tutorialIsland.getStage() >= 16)
                                        {
                                            if (owner.getX() == 203)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(204, 752);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(203, 752);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the bank assistant before proceeding through this door.");	
                                        }
                                    }
                                    else
                                    // Quest Advisor, ninth door.
                                    if (object.getX() == 209 && object.getY() == 754)
                                    {
                                        if (tutorialIsland.getStage() >= 17)
                                        {
                                            if (owner.getY() == 753)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(209, 754);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(209, 753);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the quest advisor before proceeding through this door.");	
                                        }
                                    }
                                    else
                                    // Wilderness Guide, tenth door.
                                    if (object.getX() == 217 && object.getY() == 760)
                                    {
                                        if (tutorialIsland.getStage() >= 18)
                                        {
                                            if (owner.getX() == 216)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(217, 760);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(216, 760);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the wilderness guide before proceeding through this door.");	
                                        }
                                    }
                                    else
                                    // Magic Instructor, tenth door.
                                    if (object.getX() == 222 && object.getY() == 760)
                                    {
                                        if (tutorialIsland.getStage() >= 21)
                                        {
                                            if (owner.getX() == 221)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(222, 760);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(221, 760);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the magic instructor before proceeding through this door.");	
                                        }
                                    }

                                    else
                                    // Fatigue Expert, elevent door.
                                    if (object.getX() == 226 && object.getY() == 760)
                                    {
                                        if (tutorialIsland.getStage() >= 23)
                                        {
                                            if (owner.getX() == 225)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(226, 760);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(225, 760);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the fatigue instructor before proceeding through this door.");	
                                        }
                                    }
                                    else
                                    // Community Instructor, tenth door.
                                    if (object.getX() == 230 && object.getY() == 759)
                                    {
                                        if (tutorialIsland.getStage() >= 24)
                                        {
                                            if (owner.getY() == 758)
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(230, 759);
                                            }
                                            else
                                            {
                                                doDoor();
                                                owner.sendMessage("You go through the door.");
                                                owner.teleport(230, 758);
                                            }
                                        }
                                        else
                                        {
                                            owner.sendMessage(Config.PREFIX + "You must speak to the community instructor before proceeding through this door.");	
                                        }
                                    }
                                }
                                else
                                {
                                    owner.sendMessage(Config.PREFIX + "You must talk to the guide before proceeding through this door.");
                                }
                                break;
							case 57:
								if(owner.getX() == 259 & owner.getInventory().countId(200) > 0 && owner.getInventory().countId(375) > 0 && owner.getInventory().countId(268) > 0 && owner.getInventory().countId(340) > 0) {
									doDoor();
									owner.teleport(258, 3334, false);
									owner.getInventory().remove(new InvItem(200, 1));
									owner.getInventory().remove(new InvItem(375, 1));
									owner.getInventory().remove(new InvItem(268, 1));
									owner.getInventory().remove(new InvItem(340, 1));
									owner.sendInventory();
								} else {
									if(owner.getX() ==	258) {
										doDoor();
										owner.teleport(259, 3334, false);
									} else {
									 owner.sendMessage("The door won't open");
									}
								}
								break;	
								case 60: // Melzars maze (coming out only)
								if(owner.getX() > 337) {
									doDoor();
									owner.teleport(337, owner.getY(), false);
								}
								else {
									owner.sendMessage("The door is locked shut");
								}
								break;
								
								case 75:
									
									// Shield of Arrav door.
									Quest Shield_Of_Arrav = owner.getQuest(13);
									if (object.getX() == 148 && object.getY() == 533)
									{
										if (Shield_Of_Arrav != null && !Shield_Of_Arrav.finished() && Shield_Of_Arrav.getStage() < 3)
										{
											if (owner.getY() == 533)
											{
												doDoor();
												owner.teleport(148, 532);
											}
											else
											{
												doDoor();
												owner.teleport(148, 533);
											}
										}
										return;
									}
								break;
								
							case 102: //Melzar's maze red key doors
							case 103: //Melzar's maze orange key doors
							case 104: // Melzar's maze yellow key doors
							case 106: //Melzar's maze blue key doors
								owner.sendMessage("The door is locked");
							break;
							
							case 105: // Exit Melzar maze (zombie room)
							if(owner.getX() < 346){
							doDoor();
							owner.teleport(346, owner.getY(), false);
							} else {
							owner.sendMessage("The door is locked");			
							}							
							break;
							
							case 107: // Exit melzar maze (Melzar the mad room)
							if  (owner.getX() < 348){
							doDoor();
							owner.teleport(348, owner.getY(),false);
							}else {
							owner.sendMessage("The door is locked");
							}
							break;
							
							case 108: // Magenta key ( Melzar the mad room )
							owner.sendMessage("The door is locked");
							break;
							
							case 110: //Exit melzar maze ( Lesser room)
							if (owner.getX() < 349){
							doDoor();
							owner.teleport(349, owner.getY(), false);
							}else {
							owner.sendMessage("The door is locked");
							}
							break;
							
							case 128: //Black door (lessers)
							owner.sendMessage("The door is locked");
							break;
							
							case 129:// final escape Melzar maze (  base level )
							if(owner.getX() < 340){
							doDoor();
							owner.teleport(340, owner.getY(), false);
							owner.sendMessage("You go through the door");
							} else {
							owner.sendMessage("The door is locked");
							}
							break;
								
								
								
							case 30: // Locked Doors / Oil Can Maze 'Door H'
								if(owner.getY() < 3000 || !owner.willDoorHOpen()) {
									owner.sendMessage("The door is locked shut");
								} else {
									doDoor();
									if(owner.getY() == 3377) {
										owner.teleport(226, 3378, false);
									} else {
										owner.teleport(226, 3377, false);
									}
								}
								break;
								
							case 151 : // Search wall for Rogues purse
								owner.sendMessage("Small amounts of herb fungus are growing at the base of this cavern wall");
								Quest junglePotion = owner.getQuest(39);
								if(junglePotion != null) {
									if(junglePotion.getStage() == 4 && !junglePotion.finished() && owner.getInventory().countId(824) == 0) {
										World.registerEntity(new Item(823, 411, 3576, 1, owner));
									}
								}
								
							break;
							
							case 450: //East | West ardounge door
								doDoor();
								if(owner.getX() < 623) {
									owner.teleport(owner.getX() + 2, owner.getY(), false);
								} else if(owner.getX() > 623) {
									owner.teleport(owner.getX() - 2, owner.getY(), false);
								} else {
									//shouldn't happen, player will be stuck if it does...lol
								}
								owner.teleport(586, 523, false);
								break;
							case 91:
								doDoor();
								if(owner.getX() < 508) {
									owner.teleport(owner.getX() + 1, owner.getY(), false);
								} else if(owner.getX() > 507) {
									owner.teleport(owner.getX() - 1, owner.getY(), false);
								} else { }						
								break;
							case 116:
								doDoor();
								owner.sendMessage("You go through the door");
								if(owner.getY() == 2437) {
									owner.teleport(202, 2438, false);
								} else {
									owner.teleport(202, 2437, false);
								}
								break;
							default:
								owner.sendMessage("Nothing interesting happens.");
								break;
						}
					}
				}
				
				private void handlePickLock() {
					final PicklockDoorDefinition lockedDoor = EntityHandler.getPicklockDoorDefinition(object.getID());
					if(lockedDoor != null && owner.getStatus() == Action.IDLE) {
						owner.setStatus(Action.PICKLOCKING_DOOR);
						owner.setBusy(true);
						if(owner.getMaxStat(17) < lockedDoor.getLevel()) {
							owner.sendMessage("You do not have a high enough thieving level to unlock this.");
							owner.setBusy(false);
							owner.setStatus(Action.IDLE);
						} else {
							if(!lockedDoor.lockpickRequired() || owner.getInventory().countId(714) > 0) {
//								Bubble bubble = new Bubble(owner.getIndex(), 714);
								for(Player p : owner.getViewArea().getPlayersInView())
								{
									p.watchItemBubble(owner.getIndex(), 714);
//									p.informOfBubble(bubble);
								}
								owner.sendMessage("You attempt to pick the lock on the " + object.getDoorDef().name);
								World.getDelayedEventHandler().add(new ShortEvent(owner) {
									public void action() {
										if(Formulae.thievingFormula(owner.getMaxStat(17), lockedDoor.getLevel())) {
											owner.sendMessage("You sucessfully unlocked the " + object.getDoorDef().name);
											owner.sendSound("opendoor", false);
											owner.increaseXP(17, lockedDoor.getExperience(), 1);
											owner.sendStat(17);
											int newX = 0;
											int newY = 0;
											try {
												switch(object.getDirection()) {
													case 0:
														owner.teleport(owner.getX(), owner.getY() + (owner.getY() < object.getY() ? 1 : -1), false);
														break;
													case 1:
														owner.teleport(owner.getX() + (owner.getX() < object.getX() ? 1 : -1), owner.getY(), false);
														break;
													case 2:
														if(owner.getX() < object.getX()) {
															newX = 2;
														} else if(owner.getX() > object.getX()) {
															newX = -2;
														}
														if(owner.getY() < object.getY()) {
															newY = 2;
														} else if(owner.getY() > object.getY()) {
															newY = -2;
														}
														owner.teleport(owner.getX() + newX, owner.getY() + newY, false);
														break;
													case 3:
														if(owner.getX() < object.getX()) {
															newX = 2;
														} else if(owner.getX() > object.getX()) {
															newX = -2;
														}
														if(owner.getY() < object.getY()) {
															newY = 2;
														} else if(owner.getY() > object.getY()) {
															newY = -2;
														}
														owner.teleport(owner.getX() + newX, owner.getY() + newY, false);
														break;
												}
												doDoor();
											} catch(Exception exception) {
												exception.printStackTrace();
											}
										} else {
											owner.sendMessage("You fail to unlock the " + object.getDoorDef().name);
										}
										owner.setBusy(false);
										owner.setStatus(Action.IDLE);
									}
								});
							} else {
								owner.sendMessage("You need a lockpick to picklock this door");
								owner.setBusy(false);
								owner.setStatus(Action.IDLE);
							}
						}
					}
				}
			
			});
		}
	}
}