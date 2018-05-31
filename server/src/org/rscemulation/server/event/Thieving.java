package org.rscemulation.server.event;
/*
import org.rscemulation.server.Server;
import org.rscemulation.server.entityhandling.defs.GameObjectDef;
import org.rscemulation.server.StaticDataStorage;
import org.rscemulation.server.entityhandling.EntityHandler;
import org.rscemulation.server.entityhandling.defs.NPCDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemDropDef;
import org.rscemulation.server.entityhandling.locs.NPCLoc;
import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.util.Formulae;
import org.rscemulation.server.util.DataConversions;
import org.rscemulation.server.event.SingleEvent;
import org.rscemulation.server.event.DelayedEvent;
import org.rscemulation.server.event.FightEvent;
import org.rscemulation.server.states.Action;
import org.rscemulation.server.states.CombatState;
import org.rscemulation.server.model.*;
import org.apache.mina.core.session.IoSession;

import java.util.*;
*/
public class Thieving {/*
	private Player player;
	private GameObject object;
	private static Random random = new Random();
	private Npc affectedNpc;
	private Mob affectedMob;
	private int npcID;

	public Thieving(Player p, GameObject obj) {
		this.player = p;
		this.object = obj;
	}
	
	public Thieving(Player p, Npc np, Mob mb) {
		this.player = p;
		this.affectedNpc = np;
		this.affectedMob = mb;
		npcID = affectedNpc.getID();	
	}
	
	public boolean playerIsCaught() {
		boolean caught = false;	
		try {
			int[] guardians = Server.data().stallGuardians().get(object.getID());
			Npc stallKeeper = Server.requestWorldAccess().getNpc(Server.data().stallKeepers().get(object.getID()), player.getX() - 5, player.getX() + 5, player.getY() - 5, player.getY() + 5);
			if(stallKeeper != null) {
				if(!TrajectoryHandler.isRangedBlocked(player.getLocation(), stallKeeper.getLocation())) {
					caught = true;
					for(Player p : player.getViewArea().getPlayersInView()) {
						p.informOfNpcMessage(new ChatMessage(stallKeeper, "Guards! Guards! Help I'm being robbed!", player));
					}
					for(int guardian : guardians) {
						Npc currentGuardian = Server.requestWorldAccess().getNpc(guardian, player.getX() - 5, player.getX() + 5, player.getY() - 5, player.getY() + 5);
						if(currentGuardian != null) {
							currentGuardian.setAggressive(player);
						}
					}
				}
			}
			if(!caught)	{
				for(int guardian : guardians) {
					Npc currentGuardian = Server.requestWorldAccess().getNpc(guardian, player.getX() - 5, player.getX() + 5, player.getY() - 5, player.getY() + 5);
					if(currentGuardian != null) {
						if(!TrajectoryHandler.isRangedBlocked(player.getLocation(), currentGuardian.getLocation())) {
							currentGuardian.setAggressive(player);
							caught = true;
							break;
						}
					}
				}
			}
		} catch(Exception e) {
			player.setBusy(false);
			e.printStackTrace();
			player.setThieving(false);
			caught =  false;
		}
		return caught;
	}
	
	public static boolean chestsContains(int id) {
		return Server.data().chests().get(id) == null ? false : true;
	}
	
	private String [] Chats = {
			"Oi! Get your hands out of there -name-!", 
			"Hey thief! Get over here!", 
			"Trying to steal from me, hmm?", 
			"No one steals from me!", 
			"Take those hands off me, Thief!",
			"Are you trying to steal from me -name-?",
			"Dont you dare touch me!", 
			"Thief get back here now!",
			"Stealing won't get you anywhere.",
			"I'll get you, Thief!",
			"Die evil thief!",
			"You are going to pay for that",
			"Ill make you wish you were never born.",
			"-name- I am going to hurt you!",
			"-name- dont steal from me again, you'll regret it.",
			"Remove your filthy hands off me!",
			"A real man doesn't need to steal buddy."
	};
	
	private String [] caughtChats = {
		"Guards! Guards! Im being Robbed!",
		"Help Guards I am being Robbed please help!",
		"Someone help! My items are getting stolen!",
		"You'll wish you never did that, Thief!",
		"You are going to pay for that",
		"-name- how could you steal from me? Guards!",
		"-name- get your hands out of my stall!",
		"Hey -name- thats not yours!",
		"Dont steal from me -name- I'll whip your behind!",
		"Oi! -name- you deserve the death rope!"		
	};
	
	public void thieveChest() {
		try {
			if (object == null) {
				player.setThieving(false);
				player.setBusy(false);
			} else	if (object.getID() == 338) {
				player.getActionSender().sendMessagePointer(9);
				player.setThieving(false);
				player.setBusy(false);
			} else {
				if(Server.data().chests().get(object.getID()) != null) {
					final int[] chest = Server.data().chests().get(object.getID());
					if (player.getMaxStat(17) < chest[0]) {
						player.setThieving(false);
						player.setBusy(false);
						player.getActionSender().sendMessagePointer(10);
					} else {
						player.getActionSender().sendMessagePointer(0);
						player.setBusy(true);
						Server.requestWorldAccess().getDelayedEventHandler().add(new MiniEvent(player, 300){
							public void action() {
								owner.getActionSender().sendMessagePointer(1);
								Bubble bubble = new Bubble(player, 549);
								for(Player p : owner.getViewArea().getPlayersInView()) {
									p.informOfBubble(bubble);
								}
								Server.requestWorldAccess().getDelayedEventHandler().add(new MiniEvent(player, 1000){
									public void action() {
										owner.getActionSender().sendMessagePointer(2);
										Server.requestWorldAccess().getDelayedEventHandler().add(new MiniEvent(player, 1000){
											public void action() {
												owner.getActionSender().sendMessagePointer(3);
												Server.requestWorldAccess().registerGameObject(new GameObject(object.getLocation(), 339, object.getDirection(), object.getType()));
												Server.requestWorldAccess().delayedSpawnObject(object.getLoc(), 900);
												Server.requestWorldAccess().getDelayedEventHandler().add(new MiniEvent(player, 1200){
													public void action() {
														Server.requestWorldAccess().registerGameObject(new GameObject(object.getLocation(), 338, object.getDirection(), object.getType()));
														Server.requestWorldAccess().delayedSpawnObject(object.getLoc(), chest[2]);
														owner.getActionSender().sendMessagePointer(4);
														owner.incExp(17, chest[1], 1);
														owner.sendStat(17);
														for(InvItem item : Server.data().chestLoot().get(object.getID())) {
															owner.getInventory().add(item.getID(), item.getAmount());
														}
														owner.sendInventory();
														owner.setBusy(false);
														owner.setThieving(false);
													}
												});
											}
										});
									}
								});
							}
						});
					}
				} else {
					player.setThieving(false);
					player.setBusy(false);
					player.getActionSender().sendMessagePointer(5);
					System.out.println("Player " + player.getUsername() + " found a chest not added, ID: " + object.getID() + ", Coords: " + object.getLocation());
				}
			}
		} catch(Exception e) {
			player.setBusy(false);
			System.out.println(e.getMessage() + "\nStack Trace: " + e.getStackTrace() + "\nEnd of Stack Trace");
			player.setThieving(false);
		}
	}

	public void openThievedChest() {
		try {
			if (player.getCurStat(3) <= 2) {
				player.getActionSender().sendMessagePointer(6);
				player.setThieving(false);
				player.setBusy(false);
			} else {
				int damage = player.getCurStat(3) / 9;
				player.getActionSender().sendMessagePointer(7);	
				player.setLastDamage(damage);
				player.setCurStat(3, player.getCurStat(3) - damage);
				ArrayList<Player> playersToInform = new ArrayList<Player>();
				playersToInform.addAll(player.getViewArea().getPlayersInView());
				player.sendStat(3);
				for(Player p : playersToInform) {
					p.informOfModifiedHits(player);
				}
				Server.requestWorldAccess().getDelayedEventHandler().add(new MiniEvent(player, 1200){
					public void action(){
						owner.setThieving(false);
						owner.setBusy(false);
					}
				});
			}
		} catch(Exception e) {
			player.setBusy(false);
			System.out.println(e.getMessage() + "\nStack Trace: " + e.getStackTrace() + "\nEnd of Stack Trace");
			player.setThieving(false);
		}
	}
	
	public void thieveStall() {
		try {
			if (object != null) {
				if(Server.data().stalls().get(object.getID()) != null) {
					final int[] stall = Server.data().stalls().get(object.getID());
					if (player.getMaxStat(17) < stall[0]) {
						player.sendMessage("Sorry, you need a thieving level of " + stall[0] + " to steal from that");
						player.setThieving(false);
					} else {
						Bubble bubble = new Bubble(player, 609);
						for(Player p : player.getViewArea().getPlayersInView()) {
							p.informOfBubble(bubble);
						}
						player.sendMessage("You attempt to steal from the " + object.getGameObjectDef().name);	
						player.setBusy(true);
						Server.requestWorldAccess().getDelayedEventHandler().add(new ShortEvent(player) {
							public void action() {
								owner.setThieving(false);
								if (object == null) {
									owner.getActionSender().sendMessagePointer(8);
									owner.setThieving(false);
									owner.setBusy(false);
								} else {
									if(playerIsCaught()) {
										owner.setThieving(false);
										owner.setBusy(false);										
									} else if (!chanceFormulae(stall[0])) {
										owner.sendMessage("You failed to steal from the " + object.getGameObjectDef().name);
										owner.setThieving(false);
										owner.setBusy(false);
									} else {
										Server.requestWorldAccess().registerGameObject(new GameObject(object.getLocation(), 341, object.getDirection(), object.getType()));
										Server.requestWorldAccess().delayedSpawnObject(object.getLoc(), stall[2] * 1000);
										owner.sendMessage("You successfully thieved from the " + object.getGameObjectDef().name);
										owner.setThieving(false);
										owner.setBusy(false);
										if (object.getID() == 327) {
											InvItem loot = new InvItem(thieveGem(), 1);
											owner.getInventory().add(loot);
										} else {
											owner.getInventory().add(Server.data().stallLoot().get(object.getID()));
										}	
										owner.sendInventory();
										owner.incExp(17, stall[1], 1);
										owner.sendStat(17);

									}
								}
							}
						});
					}
				} else {
					player.getActionSender().sendMessagePointer(11);
					System.out.println("Player " + player.getUsername() + " found a stall not added, ID: " + object.getID() + ", Coords: " + object.getLocation());
					player.setThieving(false);
				}
			} else {
				player.setThieving(false);			
			 }
		 } catch(Exception e) {
			player.setBusy(false);
			e.printStackTrace();
			player.setThieving(false);
		 }
	}
	
	public void lockpick() {
		try {
			if (!player.isBusy() && !player.inCombat() && player != null && object != null) {
				player.setBusy(true);
				if(Server.data().picklockDoors().get(player.getLocation()) != null) {
					final int[] door = Server.data().picklockDoors().get(player.getLocation());
					if(player.getMaxStat(17) < door[1]) {
						player.getActionSender().sendMessagePointer(12);
						player.setThieving(false);
						player.setBusy(false);
					} else {
						Bubble bubble = new Bubble(player, 714);
						for(Player p : player.getViewArea().getPlayersInView()) {
							p.informOfBubble(bubble);
						}
						player.sendMessage("You attempt to pick the lock on the " + object.getDoorDef().name);
						Server.requestWorldAccess().getDelayedEventHandler().add(new ShortEvent(player) {
							public void action() {
								if(!chanceFormulae(door[0])) {
									owner.getActionSender().sendMessagePointer(13);
									owner.setThieving(false);
									owner.setBusy(false);
									return;
								} else {
									owner.sendMessage("You sucessfully unlocked the " + object.getDoorDef().name);
									player.sendSound("opendoor", false);
									Server.requestWorldAccess().registerGameObject(new GameObject(object.getLocation(), 11, object.getDirection(), object.getType()));
									Server.requestWorldAccess().delayedSpawnObject(object.getLoc(), 1000);
									owner.incExp(17, door[1], 1);
									owner.sendStat(17);
									owner.setThieving(false);
									owner.setBusy(false);
									owner.teleport(Server.data().picklockDoorsCoords().get(owner.getLocation()), false);
								}
							}
						});
					}
				} else {
					player.getActionSender().sendMessagePointer(14);
					System.out.println("Player " + player.getUsername() + " found a door(lockpick) not added, ID: " + object.getID() + ", Coords: " + object.getLocation());
					player.setThieving(false);
					player.setBusy(false);
				}
			}
		} catch(Exception e) {
			player.setBusy(false);
			System.out.println(e.getMessage() + "\nStack Trace: " + e.getStackTrace() + "\nEnd of Stack Trace");
			player.setThieving(false);
		}
	}
	
	public boolean chanceFormulae(int targetLv) {
		int chance [] = {27, 33, 35, 37, 40, 43, 47, 51, 54, 58, 62, 66, 71, 74, 78, 81, 84, 88, 93, 95};
		int maxLvl [] = {1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100};
		int diff = player.getMaxStat(17) - targetLv;	
		int index = 0;
		for(int i=0; i < maxLvl.length; i++)
			if (diff >= maxLvl[i] && diff < maxLvl[i] + 5)
				index = i;
		int Chance = (chance[index] < 27 ? 27 : chance[index]);
		return random.nextInt(100) < Chance;
	}
		
	public int thieveGem() {
		int gem = random.nextInt(100);
		if (gem > 60) {
			gem = 160;
		} else if (gem > 30) {
			gem = 159;
		} else if (gem > 10) {
			gem = 158;
		} else {
			gem = 157;
		}
		return gem;
	}
	
	public void getPickpocketLoot(int id) {
		int loot = random.nextInt(100);
		if(id == 342) {
			if(loot > 80) {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(342).get(0).getID(), Server.data().pickpocketLoot().get(342).get(0).getAmount()));
			} else if(loot > 40) {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(342).get(1).getID(), Server.data().pickpocketLoot().get(342).get(1).getAmount()));
			} else if(loot > 20) {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(342).get(2).getID(), Server.data().pickpocketLoot().get(342).get(2).getAmount()));
			} else {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(342).get(3).getID(), Server.data().pickpocketLoot().get(342).get(3).getAmount()));
			}
		} else if(id == 593 || id == 585 || id == 581 || id == 582 || id == 583 || id == 580) {
			if(loot > 80) {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(593).get(0).getID(), Server.data().pickpocketLoot().get(593).get(0).getAmount()));
			} else if(loot > 60) {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(593).get(1).getID(), Server.data().pickpocketLoot().get(593).get(1).getAmount()));
			} else if(loot > 50) {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(593).get(2).getID(), Server.data().pickpocketLoot().get(593).get(2).getAmount()));
			} else if(loot > 40) {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(593).get(3).getID(), Server.data().pickpocketLoot().get(593).get(3).getAmount()));
			} else if(loot >20) {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(593).get(4).getID(), Server.data().pickpocketLoot().get(593).get(4).getAmount()));
			} else {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(593).get(5).getID(), Server.data().pickpocketLoot().get(593).get(5).getAmount()));
			}
		} else if(id == 324) {
			if(loot > 95) {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(324).get(0).getID(), Server.data().pickpocketLoot().get(324).get(0).getAmount()));
			} else if(loot > 85) {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(324).get(1).getID(), Server.data().pickpocketLoot().get(324).get(1).getAmount()));
			} else {
				player.getInventory().add(new InvItem(Server.data().pickpocketLoot().get(324).get(2).getID(), Server.data().pickpocketLoot().get(324).get(2).getAmount()));
			}
		} else {
			for(InvItem item : Server.data().pickpocketLoot().get(id)) {
				player.getInventory().add(item.getID(), item.getAmount());
			}
		}
	}
	
	public void beginPickpocket() {
		try {
			if(Server.data().pickpocketNpcs().get(npcID) != null) {
				final int[] npc = Server.data().pickpocketNpcs().get(npcID);
				player.setFollowing(affectedMob);
				Server.requestWorldAccess().getDelayedEventHandler().add(new WalkToMobEvent(player, affectedMob, 1) {
					public void arrived(){
						if(!owner.isThieving()) {
							if(affectedMob.inCombat() || owner.isBusy() || affectedNpc == null || affectedNpc.inCombat()) {
								owner.resetPath();
							} else if (owner == null) {
								affectedNpc.unblock();
							} else if (owner.nextTo(affectedMob)) {
								owner.setThieving(true);
								owner.setBusy(true);
								if(owner.getCurStat(17) < npc[0]){
									owner.sendMessage("You must be at least "+npc[0]+" thieving to pick the "+affectedNpc.getDef().name+"'s pocket.");
									owner.setBusy(false);
									owner.setThieving(false);
								} else {
									Bubble bubble = new Bubble(player, 16);
									for(Player p : owner.getViewArea().getPlayersInView()) {
										p.informOfBubble(bubble);
									}
									owner.sendMessage("You attempt to pick the "+affectedNpc.getDef().name+"'s pocket...");
									affectedNpc.resetPath();
									Server.requestWorldAccess().getDelayedEventHandler().add(new ShortEvent(owner){
										public void action(){
											owner.setBusy(false);
											affectedNpc.setBusy(false);
											if(chanceFormulae(npc[0])) {
												owner.setThieving(false);
												owner.sendMessage("You sucessfully stole from the "+affectedNpc.getDef().name);
												getPickpocketLoot(npcID);
												owner.sendInventory();
												owner.incExp(17, npc[1], 1);
												owner.sendStat(17);
												owner.setBusy(false);
												affectedNpc.unblock();
											} else {
												owner.setThieving(false);
												owner.sendMessage("You fail to pick the "+affectedNpc.getDef().name+"'s pocket.");
												if(random.nextInt(10) > 2) {
													owner.setBusy(false);
													affectedNpc.unblock();
												} else {
													affectedNpc.resetPath();
													owner.setBusy(true);
													if(affectedNpc == null || affectedNpc.inCombat()) {
														owner.resetPath();
														owner.setBusy(false);
														owner.setThieving(false);
													} else if (owner == null) {
														affectedNpc.unblock();
													} else {
														owner.setBusy(false);
														affectedNpc.setAggressive(owner);
													}
												}
											}
										}
									});
								}
							}
						}
					}
				});
			} else {
				player.getActionSender().sendMessagePointer(16);
				player.setBusy(false);
				player.setThieving(false);			
			}
		} catch (Exception e) {
			player.setBusy(false);
			e.printStackTrace();
			player.setThieving(false);
		}
	}*/
}
