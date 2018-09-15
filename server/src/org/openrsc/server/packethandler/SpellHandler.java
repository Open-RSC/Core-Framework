package org.openrsc.server.packethandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.SpellDef;
import org.openrsc.server.entityhandling.defs.extras.ItemSmeltingDef;
import org.openrsc.server.entityhandling.defs.extras.ReqOreDef;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.event.WalkToMobEvent;
import org.openrsc.server.event.WalkToPointEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ExploitLog;
import org.openrsc.server.logging.model.PickUpLog;
import org.openrsc.server.model.*;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.states.Action;
import org.openrsc.server.states.CombatState;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;
public class SpellHandler implements PacketHandler {
  
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			int pID = ((RSCPacket)p).getID();
			if (!player.canRange())
				return;
			if ((player.isBusy() && !player.inCombat()) || player.isRanging())
				return;
			if (player.isDueling() && player.getDuelSetting(1)) {
				player.sendMessage("Magic cannot be used during this duel!");
				return;
			}

			player.resetAllExceptDueling();
			int spellID = p.readShort();
			
			if (spellID < 0 || spellID >= 50) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "SpellHandler (1)", DataConversions.getTimeStamp()));
				return;
			}
			if (!canCast(player))
				return;
			SpellDef spell = EntityHandler.getSpellDef(spellID);

			if (player.getCurStat(6) < spell.getReqLevel()) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "SpellHandler (2)", DataConversions.getTimeStamp()));
				player.resetPath();
				return;
			}
			if (!Formulae.castSpell(spell, player.getCurStat(6), player.getMagicPoints())) {
				player.sendMessage("The spell fails! You may try again in 20 seconds");
				player.sendSound("spellfail", false);
				player.setSpellFail();
				player.resetPath();
				return;
			}
			switch(pID) {
				case 34: // Cast on self
					if (!player.isDueling()) {
						if (spell.getSpellType() == 0)
							handleCastOnSelf(player, spell, spellID);
					} else
						player.sendMessage("You can't do that during a duel!");					
					break;
				case 32: // Cast on player
					Player affectedPlayer = World.getPlayer(p.readShort());
					if (affectedPlayer != null) {
						byte rand = p.readByte();
						if (player.getLastSpellRandom() == rand) {
							player.incAutocastProtection();
							if (player.getAutocastProtection() > 4) {
								Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "Player used packet sending software", DataConversions.getTimeStamp()));
							}
						} else {
							player.setLastSpellRandom(rand);
							player.resetAutocastProtection();
						}
						if (affectedPlayer.getCombatTimer() != 0 && !affectedPlayer.inCombat() && affectedPlayer.getCombatTimer() - System.currentTimeMillis() > -2000)
							return;
						/*
						 * One vs One
						 * implementation.
						 * 
						 */
												
						if (player.getLocation().varrockWilderness() || affectedPlayer.getLocation().varrockWilderness() || Config.isPkMode())
						{
							if (affectedPlayer.inCombat() && player.getInCBWith() != affectedPlayer || player.inCombat() && !affectedPlayer.inCombat() && player.getInCBWith() != affectedPlayer)
							{
								player.sendMessage(Config.getPrefix() + "This player cannot be maged whilst in combat with another player.");
								player.resetPath();
								player.resetFollowing();
								return;
							}
						}
						
                        if ( affectedPlayer.isInvulnerable() /*affectedPlayer.isSuperMod() || affectedPlayer.isDev() || affectedPlayer.isEvent()*/)
                        {
                        	player.sendMessage(Config.getPrefix() + affectedPlayer.getUsername() + " is currently invulnerable!");
                        	player.resetFollowing();
                        	player.resetPath();
                        	return;
						}
                        
						if (player.getLocation().isInDMArena() && affectedPlayer != player.getInDMWith()) 
						{
							player.sendMessage(Config.getPrefix() + "You aren't in a Death Match with " + affectedPlayer.getUsername());
							player.resetFollowing();
							player.resetPath();															
							return;															
						}
                        
						if (player.isDMing() && player.getDMSetting(1)) 
						{
							player.sendMessage(Config.getPrefix() + "Magic cannot be used in this Death Match");
							player.resetFollowing();
							player.resetPath();															
							return;															
						}
                        
                        
						if (!player.withinRange(affectedPlayer, 5))
						{
							player.resetPath();
							player.resetFollowing();
						}
                        
                        if(spellID == 19)
                        {
                        	player.sendMessage("This spell can only be used on skeletons, zombies and ghosts");
                        	player.resetPath();
                        	player.resetFollowing();
                        	return;
                        }
                        
                        if (!Config.isAllowGodspells() && spellID == 25 && player.getLocation().inWilderness())
                        {
                        	player.sendMessage(Config.getPrefix() + "Iban blast is currently disabled.");
                        	player.resetPath();
                        	player.resetFollowing();
                        	return;
                        }
                        
                        if (spellID == 25 && !player.isWearing(1000))
                        {
                        	player.sendMessage(Config.getPrefix() + "You must be wielding the staff of iban in order to cast this spell");
                        	player.resetPath();
                        	player.resetFollowing();
                        	return;
                        }
                        
                        if (!Config.isAllowWeakens())
                        {
                        	if (isWeaken(spellID))
                        	{
                        		player.sendMessage(Config.getPrefix() + "Weakens are currently disabled");
                        		player.resetPath();
                        		player.resetFollowing();
                        		return;
                        	}
                        }
						
						int specialID = spellID - 33;
						
						if(specialID < 3 && specialID > -1) 
						{
	                        int[] capeIDs = { 1215, 1214, 1213 };
	                        int[] staffIDs = { 1217, 1218, 1216 };
							String[] gods = { "Guthix", "Saradomin", "Zamarok" };
							
	                        if(!player.isWearing(staffIDs[specialID])) 
	                        {
	                        	player.sendMessage("You cannot cast this spell without the Staff of "+gods[specialID]+".");
	                        	player.resetFollowing();
	                        	player.resetPath();
	                            return;
	                        }
	                        
	                        if (!player.isWearing(capeIDs[specialID]))
	                        {
	                        	player.sendMessage("You cannot cast this spell without the Cape of "+gods[specialID]+".");
	                        	player.resetFollowing();
	                        	player.resetPath();
	                            return;
	                        }
	                        
	                        if (!Config.isAllowGodspells() && player.getLocation().inWilderness())
	                        {
	                        	player.sendMessage(Config.getPrefix() + "God spells are currently disabled");
	                        	player.resetFollowing();
	                        	player.resetPath();
	                        	return;
	                        }
						}
                        handleCastOnPlayer(player, affectedPlayer, spellID);
					}
				break;
				
				case 36: // Cast on NPC
					Npc affectedNpc = World.getNpc(p.readShort());
					if (affectedNpc != null) {
						if (!affectedNpc.getDef().isAttackable()) {
							player.resetPath();
							player.sendMessage("I can't attack that");
							return;
						}
						switch (affectedNpc.getID()) {
							case 35:
							
							break;
							
							default:
								if (!player.isDueling()) {
									if (spell.getSpellType() == 2) {
										if (spellID != 19 || (spellID == 19 && affectedNpc.getDef().isUndead())) {
											if (spellID != 25 || player.getInventory().wielding(1000)) { //iban
												if (spellID != 33 || player.getInventory().wielding(1217) && player.getInventory().wielding(1215)) {//guthix
													if (spellID != 34 || player.getInventory().wielding(1218) && player.getInventory().wielding(1214)) {//saradomin
														if (spellID != 35 || player.getInventory().wielding(1216) && player.getInventory().wielding(1213)) //zamorak
															handleCastOnNPC(player, affectedNpc, spellID);
														else {
															if (!player.getInventory().wielding(1216))
																player.sendMessage("you must be wielding the staff of zamorak to cast this spell");	
															else
																player.sendMessage("You must be wearing the cape of zamorak to cast this spell");
														}
													} else {
														if (!player.getInventory().wielding(1218))
															player.sendMessage("you must be wielding the staff of saradomin to cast this spell");
														else
															player.sendMessage("You must be wearing the cape of saradomin to cast this spell");
													}
												} else {
													if (!player.getInventory().wielding(1217))
														player.sendMessage("you must be wielding the staff of guthix to cast this spell");
													else
														player.sendMessage("you must be wearing the cape of guthix to cast this spell");
												}
											} else
												player.sendMessage("you need the staff of iban to cast this spell");
										} else
											player.sendMessage("This spell can only be used on skeletons, zombies and ghosts");
									}
								} else
									player.sendMessage("You can't do that during a duel!");				
								break;
						}
					} else
						player.resetPath();
					break;
				case 31: // Cast on Inventory Item
					if (!player.isDueling()) {
						if (spell.getSpellType() == 3) {
							InvItem item = player.getInventory().get(p.readShort());
							if (item != null)
								handleInvItemCast(player, spell, spellID, item);							
							else
								player.resetPath();						
						}
					} else
						player.sendMessage("You can't do that during a duel!");
					break;
				case 35: // Cast on Door
					if (!player.isDueling())
						player.sendMessage("Nothing interesting happens");					
					else
						player.sendMessage("You can't do that during a duel!");				
					break;
				case 33: // Cast on Object
					if (!player.isDueling()) {
						if (spell.getSpellType() == 3) {
							int obj = p.readShort();
							handleGameObjectCast(player, spellID, obj);
						}
					} else
						player.sendMessage("You can't do that during a duel!");				
					break;
				case 37: // Cast on Ground Item
					if (player.isDueling()) {
						player.sendMessage("You can't do that during a duel!");
						return;
					}
					if (player.getLocation().isInDMArena()) {
						player.sendMessage(Config.getPrefix() + "You cannot do that during a Death Match");
						return;
					}					
					short x = p.readShort();
					short y = p.readShort();
					short id = p.readShort();
					handleItemCast(player, spell, spellID, x, y, id);
					break;
				case 38: // Cast on Ground
					if (player.isDueling()) {
						player.sendMessage("You can't do that during a duel!");
						return;
					}
					if (player.getLocation().isInDMArena()) {
						player.sendMessage(Config.getPrefix() + "You cannot do that during a Death Match");
						return;
					}					
					if (spell.getSpellType() == 6)
						handleGroundCast(player, spell, spellID);
					break;
			}
			player.sendInventory();
			player.sendStat(6);
		}
	}
	
	private void handleCastOnPlayer(final Player player, final Player affectedPlayer, final int spellID) {
			if (affectedPlayer.getStatus() != Action.FIGHTING_MOB && affectedPlayer.getCombatState() != CombatState.RUNNING)
				player.setFollowing(affectedPlayer);
			player.setStatus(Action.CASTING_MOB);
			World.getDelayedEventHandler().add(new WalkToMobEvent(player, affectedPlayer, 5) {
				public void arrived() {
					owner.resetPath();
					if (!TrajectoryHandler.isRangedBlocked(owner.getX(), owner.getY(), affectedPlayer.getX(), affectedPlayer.getY())) {
						SpellDef spell = EntityHandler.getSpellDef(spellID);
						for(int id : spell.requiredRunes.keySet()) {
							if(EntityHandler.getItemDef(id).isP2P() && owner.getLocation().inWilderness() && affectedPlayer.getLocation().inWilderness() && !World.isP2PWilderness()) {
									owner.sendMessage(Config.getPrefix() + "The wilderness state must be P2P in order to cast this on your opponent.");
									return;
								}
							}
						if (canCast(owner) && affectedPlayer.getHits() > 0 && owner.checkAttack(affectedPlayer, true) && owner.getStatus() == Action.CASTING_MOB) {
							if (checkAndRemoveRunes(owner, spell)) {
								if (!owner.isDueling())
									owner.setSkulledOn(affectedPlayer);
								owner.resetAllExceptDueling();
								int damage = 0;
								int spellStrength = 0;
								boolean weakened = false;
								GameObject godSpellObject = null;
								ArrayList<Player> playersToInform = new ArrayList<Player>();
								playersToInform.addAll(owner.getViewArea().getPlayersInView());
								playersToInform.addAll(affectedPlayer.getViewArea().getPlayersInView());
								Projectile projectile = null;
								switch(spellID) {
									case 1: // Confuse
										if ((int)(affectedPlayer.getMaxStat(0) * 0.95) < affectedPlayer.getAttack()) {
											affectedPlayer.setAttack((int)(affectedPlayer.getMaxStat(0) * 0.95));
											affectedPlayer.sendStat(0);
										}
										weakened = true;
									case 5: // Weaken
										if (!weakened) {
											if ((int)(affectedPlayer.getMaxStat(2) * 0.95) < affectedPlayer.getStrength()) {
												affectedPlayer.setStrength((int)(affectedPlayer.getMaxStat(2) * 0.95));
												affectedPlayer.sendStat(2);
											}
											weakened = true;
										}
									case 9: // Curse
										if(!weakened) {
											if ((int)(affectedPlayer.getMaxStat(1) * 0.95) < affectedPlayer.getDefense()) {
												affectedPlayer.setDefense((int)(affectedPlayer.getMaxStat(1) * 0.95));
												affectedPlayer.sendStat(1);
											}
											weakened = true;
										}
									case 42: // vulnerability
										if (!weakened) {
											if ((int)(affectedPlayer.getMaxStat(1) * 0.9) < affectedPlayer.getDefense()) {
												affectedPlayer.setDefense((int)(affectedPlayer.getMaxStat(1) * 0.9));
												affectedPlayer.sendStat(1);
											}
											weakened = true;
										}
									case 45: // Enfeeble
										if (!weakened) {
											if ((int)(affectedPlayer.getMaxStat(2) * 0.9) < affectedPlayer.getStrength()) {
												affectedPlayer.setStrength((int)(affectedPlayer.getMaxStat(2) * 0.9));
												affectedPlayer.sendStat(2);
											}
											weakened = true;
										}
									case 47: // Stun
										if (!weakened) {
											if ((int)(affectedPlayer.getMaxStat(0) * 0.9) < affectedPlayer.getAttack()) {
												affectedPlayer.setAttack((int)(affectedPlayer.getMaxStat(0) * 0.9));
												affectedPlayer.sendStat(0);
											}
											weakened = true;
										}
									
									case 25: // Iban blast
										if (!weakened)
											projectile = new Projectile(owner, affectedPlayer, 4);
									case 33: // Claws of Guthix
										if (projectile == null && !weakened) {
											if (World.getZone(affectedPlayer.getX(), affectedPlayer.getY()).getObjectAt(affectedPlayer.getX(), affectedPlayer.getY()) == null && World.getZone(affectedPlayer.getX(), affectedPlayer.getY()).getDoorAt(affectedPlayer.getX(), affectedPlayer.getY()) == null) {
												godSpellObject = new GameObject(affectedPlayer.getLocation(), 1142, 0, 0);
												World.registerEntity(godSpellObject);
												World.delayedRemoveObject(godSpellObject, 500);
											}
										}
									case 34: // Saradomin Strike
										if (projectile == null && godSpellObject == null && !weakened) {
											if (World.getZone(affectedPlayer.getX(), affectedPlayer.getY()).getObjectAt(affectedPlayer.getX(), affectedPlayer.getY()) == null && World.getZone(affectedPlayer.getX(), affectedPlayer.getY()).getDoorAt(affectedPlayer.getX(), affectedPlayer.getY()) == null) {
												godSpellObject = new GameObject(affectedPlayer.getLocation(), 1031, 0, 0);
												World.registerEntity(godSpellObject);
												World.delayedRemoveObject(godSpellObject, 500);
											}
										}
									case 35: //Flames of Zamorak
										if (projectile == null && godSpellObject == null && !weakened) {
											if (World.getZone(affectedPlayer.getX(), affectedPlayer.getY()).getObjectAt(affectedPlayer.getX(), affectedPlayer.getY()) == null && World.getZone(affectedPlayer.getX(), affectedPlayer.getY()).getDoorAt(affectedPlayer.getX(), affectedPlayer.getY()) == null) {
												godSpellObject = new GameObject(affectedPlayer.getLocation(), 1036, 0, 0);
												World.registerEntity(godSpellObject);
												World.delayedRemoveObject(godSpellObject, 500);
											}
										}
										spellStrength = EntityHandler.getSpellAggressiveLvl(spellID) + (owner.isCharged() ? 15 : 0);
									default:
										affectedPlayer.sendMessage("Warning! " + owner.getUsername() + " is shooting at you!");
										if (godSpellObject == null) {
											if (projectile == null)
												projectile = new Projectile(owner, affectedPlayer, 1);
											for (Player p : playersToInform)
												p.informOfProjectile(projectile);
										}
										if (!weakened) {
											if (spellStrength != 0)
												damage = Formulae.calcSpellHit(spellStrength);
											else
												damage = Formulae.calcSpellHit(EntityHandler.getSpellAggressiveLvl(spellID));
											affectedPlayer.setLastDamage(damage);
											affectedPlayer.setHits(affectedPlayer.getHits() - damage);
											affectedPlayer.sendStat(3);
											if (affectedPlayer.getHits() <= 0)
												affectedPlayer.killedBy(owner, owner.isDueling());
										}
										if (!weakened) {
											final ArrayList<Player> delayedInformants = playersToInform;
											World.getDelayedEventHandler().add(new SingleEvent(null, 500) {
												public void action() {
													for (Player p : delayedInformants)
														p.informOfModifiedHits(affectedPlayer);								
												}
											});
										}
										finalizeSpell(owner, spell, true);
										owner.sendInventory();
										owner.sendStat(6);
									break;
								}
							}
						}
					} else {
						owner.sendMessage("I can't get a clear shot from here");
						owner.resetFollowing();
					}
				}
			});
		//}
	}

	private void handleCastOnNPC(final Player player, final Npc affectedNpc, final int spellID) {
		player.setFollowing(affectedNpc);
		player.setStatus(Action.CASTING_MOB);
		World.getDelayedEventHandler().add(new WalkToMobEvent(player, affectedNpc, 5) {
			public void arrived() {
				owner.resetPath();
				if (!TrajectoryHandler.isRangedBlocked(owner.getX(), owner.getY(), affectedNpc.getX(), affectedNpc.getY())) {
					SpellDef spell = EntityHandler.getSpellDef(spellID);
					if (canCast(owner) && affectedNpc.getHits() > 0 && owner.checkAttack(affectedNpc, true) && owner.getStatus() == Action.CASTING_MOB) {
						if (checkAndRemoveRunes(owner, spell)) {
							owner.resetAllExceptDueling();
							int damage = 0;
							boolean weakened = false;
							int spellStrength = 0;
							GameObject godSpellObject = null;
							ArrayList<Player> playersToInform = new ArrayList<Player>();
							playersToInform.addAll(owner.getViewArea().getPlayersInView());
							playersToInform.addAll(affectedNpc.getViewArea().getPlayersInView());
							Projectile projectile = null;
							switch(spellID) {
							case 1: // Confuse
								if ((int)(affectedNpc.getAttack() * 0.95) < affectedNpc.getDef().getAtt()) {
									affectedNpc.curAttack = ((int)(affectedNpc.curAttack * 0.95));
								} else {
									player.sendMessage("Your opponent has already been weakend in attack.");
									return;
								}
								weakened = true;
							case 5: // Weaken
								if (!weakened) {
									if ((int)(affectedNpc.getStrength() * 0.95) < affectedNpc.getDef().getStr()) {
										affectedNpc.curStrength = ((int)(affectedNpc.curStrength * 0.95));
									} else {
										player.sendMessage("Your opponent has already been weakend in strength.");
										return;
									}
									weakened = true;
								}
							case 9: // Curse
								if(!weakened) {
									if ((int)(affectedNpc.getDefense() * 0.95) < affectedNpc.getDef().getDef()) {
										affectedNpc.curDefense = ((int)(affectedNpc.curDefense * 0.95));
									} else {
										player.sendMessage("Your opponent has already been weakend in defense.");
										return;
									}
									weakened = true;
								}
							case 42: // vulnerability
								if (!weakened) {
									if ((int)(affectedNpc.getDefense() * 0.9) < affectedNpc.getDef().getDef()) {
										affectedNpc.curDefense = ((int)(affectedNpc.curDefense * 0.9));
									} else {
										player.sendMessage("Your opponent has already been weakend in defense.");
										return;
									}
									weakened = true;
								}
							case 45: // Enfeeble
								if (!weakened) {
									if ((int)(affectedNpc.getStrength() * 0.9) < affectedNpc.getDef().getStr()) {
										affectedNpc.curStrength = ((int)(affectedNpc.curStrength * 0.9));
									} else {
										player.sendMessage("Your opponent has already been weakend in strength.");
										return;
									}
									weakened = true;
								}
							case 47: // Stun
								if (!weakened) {
									if ((int)(affectedNpc.getAttack() * 0.9) < affectedNpc.getDef().getAtt()) {
										affectedNpc.curAttack = ((int)(affectedNpc.curAttack * 0.9));
									} else {
										player.sendMessage("Your opponent has already been weakend in attack.");
										return;
									}
									weakened = true;
								}
								case 25: // Iban blast
									if (!weakened)
										projectile = new Projectile(owner, affectedNpc, 4);
								case 33: // Claws of Guthix
									if (godSpellObject == null && !weakened && projectile == null) {
										if (World.getZone(affectedNpc.getX(), affectedNpc.getY()).getObjectAt(affectedNpc.getX(), affectedNpc.getY()) == null && World.getZone(affectedNpc.getX(), affectedNpc.getY()).getDoorAt(affectedNpc.getX(), affectedNpc.getY()) == null) {
											godSpellObject = new GameObject(affectedNpc.getLocation(), 1142, 0, 0);
											World.registerEntity(godSpellObject);
											World.delayedRemoveObject(godSpellObject, 500);
										}
									}
									spellStrength = EntityHandler.getSpellAggressiveLvl(spellID) + (owner.isCharged() ? 15 : 0);
								case 34: // Saradomin Strike
									if (godSpellObject == null && !weakened && projectile == null) {
										if (World.getZone(affectedNpc.getX(), affectedNpc.getY()).getObjectAt(affectedNpc.getX(), affectedNpc.getY()) == null && World.getZone(affectedNpc.getX(), affectedNpc.getY()).getDoorAt(affectedNpc.getX(), affectedNpc.getY()) == null) {
											godSpellObject = new GameObject(affectedNpc.getLocation(), 1031, 0, 0);
											World.registerEntity(godSpellObject);										
											World.delayedRemoveObject(godSpellObject, 500);
										}
									}
									spellStrength = EntityHandler.getSpellAggressiveLvl(spellID) + (owner.isCharged() ? 15 : 0);
								case 35: //Flames of Zamorak
									if (godSpellObject == null && !weakened && projectile == null) {
										if (World.getZone(affectedNpc.getX(), affectedNpc.getY()).getObjectAt(affectedNpc.getX(), affectedNpc.getY()) == null && World.getZone(affectedNpc.getX(), affectedNpc.getY()).getDoorAt(affectedNpc.getX(), affectedNpc.getY()) == null) {
											godSpellObject = new GameObject(affectedNpc.getLocation(), 1036, 0, 0);
											World.registerEntity(godSpellObject);
											World.delayedRemoveObject(godSpellObject, 500);
										}
									}
									spellStrength = EntityHandler.getSpellAggressiveLvl(spellID) + (owner.isCharged() ? 15 : 0);
								default:
									boolean isNpcDead = false;
									if (godSpellObject == null) {
										if (projectile == null)
											projectile = new Projectile(owner, affectedNpc, 1);
										for (Player p : playersToInform)
											p.informOfProjectile(projectile);
									}
									if (!weakened) {
										if (spellStrength != 0)
											damage = Formulae.calcSpellHit(spellStrength);
										else
											damage = Formulae.calcSpellHit(EntityHandler.getSpellAggressiveLvl(spellID));
										int hitsRemaining = affectedNpc.getHits();
										if (damage > hitsRemaining)
											damage = hitsRemaining;
										affectedNpc.setLastDamage(damage);
										affectedNpc.updateKillStealing(owner, damage, 2);
										int newHp = affectedNpc.getHits() - damage;
										affectedNpc.setHits(newHp);
										if (newHp <= 0) {
											isNpcDead = true;
											affectedNpc.killedBy(owner);
										}
									}
								if (!isNpcDead && !affectedNpc.isBusy() && magerWithinBounds(affectedNpc, owner))
									affectedNpc.setAggressive(owner);									
								if (!weakened) {
									final ArrayList<Player> delayedInformants = playersToInform;
									World.getDelayedEventHandler().add(new SingleEvent(null, 500) {
										public void action() {
											for (Player p : delayedInformants)
												p.informOfModifiedHits(affectedNpc);
										}
									});
								}
								finalizeSpell(owner, spell, true);
								owner.sendInventory();
								owner.sendStat(6);
								if (affectedNpc.getID() == 196) {
									playersToInform.clear();
									owner.sendMessage("Your spell was reflected!");
									owner.setLastDamage(damage);
									owner.setHits(owner.getHits() - damage);
									owner.sendStat(3);
									if (owner.getHits() < 1)
										owner.killedBy(affectedNpc, false);
									playersToInform.addAll(owner.getViewArea().getPlayersInView());
									for (Player p : playersToInform)
										p.informOfModifiedHits(owner);				
								}
								break;
							}
						}
					}
				} else {
					owner.sendMessage("I can't get a clear shot from here");
					owner.resetFollowing();
				}
			}
		});
	}
	
	private void handleItemCast(Player player, final SpellDef spell, final int id, final short x, final short y, final short itemID) {
		player.setStatus(Action.CASTING_GITEM);
		World.getDelayedEventHandler().add(new WalkToPointEvent(player, Point.location(x, y), 5, true) {
			public void arrived() {
				owner.resetPath();
				Item affectedItem = World.getZone(x, y).getSpecificItemVisibleTo(x, y, itemID, owner);
				if (!canCast(owner) || affectedItem == null || owner.getStatus() != Action.CASTING_GITEM)
					return;
				owner.resetAllExceptDueling();
				switch(id) {
					case 16: // Telekinetic Grab
                        if(affectedItem.getID() == 1156 || affectedItem.getID() == 1289) // Bunny Ears and Scythe
                        {
							owner.sendMessage("I can't use telekeneic grab on this object");
							return;
                        }
						if (affectedItem.getLocation().isInSeersPartyHall()) {
							owner.sendMessage("Telekinetic grab cannot be used in here");
							return;
						}
						if (affectedItem.getLocation().inBounds(461, 672, 464, 675)) // Keys, Heroes quest.
						{
							owner.sendMessage("Telekinetic grab cannot be used in here.");
							return;
						}
						if (affectedItem.getLocation().inBounds(870, 155, 898, 179))
						{
							owner.sendMessage("Telekinetic grab cannot be used in here.");
							return;
						}
						if (TrajectoryHandler.isRangedBlocked(owner.getX(), owner.getY(), affectedItem.getX(), affectedItem.getY())) {
							owner.sendMessage("I can't see the object from here");
							return;
						}
						if (!checkAndRemoveRunes(owner, spell))
							return;
						if (affectedItem.getID() == 416)
							owner.setHasMap(true);
						owner.sendTeleBubble(location.getX(), location.getY(), true);
						for (Object o : owner.getWatchedPlayers().getAllEntities()) {
							Player p = ((Player)o);
							p.sendTeleBubble(location.getX(), location.getY(), true);
						}
						World.unregisterEntity(affectedItem);
						finalizeSpell(owner, spell, true);
						owner.getInventory().add(new InvItem(affectedItem.getID(), affectedItem.getAmount()));
						Logger.log(new PickUpLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), affectedItem.getX(), affectedItem.getY(), affectedItem.getID(), affectedItem.getAmount(), DataConversions.getTimeStamp()));
						break;
				}
				owner.sendInventory();
				owner.sendStat(6);
			}
		});
	}
	
	private void handleInvItemCast(Player player, SpellDef spell, int id, InvItem affectedItem) {
		switch(id) {
			case 3: // Enchant Sapphire Amulet
				if (affectedItem.getID() == 302) {
					if (!checkAndRemoveRunes(player, spell))
						return;
					player.getInventory().remove(affectedItem);
					player.getInventory().add(new InvItem(314));
					finalizeSpell(player, spell,true);
				} else
					player.sendMessage("Nothing interesting happens");
				break;
			case 10: // Low level Alchemy
                // Non tradable items can be alched.
				/*if (!affectedItem.getDef().isTradable())
				{
					player.sendMessage("This item cannot be alched.");
					return;
				}*/
				if (affectedItem.getID() == 10) {
					player.sendMessage("That's already made of gold!");
					return;
				}
				if (affectedItem.getDef().isNote()) {
					player.sendMessage("I can not perform alchemy on that object!");
					return;
				}
				if (!checkAndRemoveRunes(player, spell))
					return;
				if (player.getInventory().remove(affectedItem) > -1) {
					long value = (int)(affectedItem.getDef().getBasePrice() * 0.3D);
					if(affectedItem.getDef().isStackable()/* || affectedItem.getDef().isNote()*/)
						value = value * affectedItem.getAmount();
					player.getInventory().add(new InvItem(10, value)); // 30%
				}
				finalizeSpell(player, spell, true, "Alchemy spell successful");
				break;
			case 13: // Enchant Emerald Amulet
				if (affectedItem.getID() == 303) {
					if (!checkAndRemoveRunes(player, spell))
						return;
					player.getInventory().remove(affectedItem);
					player.getInventory().add(new InvItem(315));
					finalizeSpell(player, spell, true);
				} else
					player.sendMessage("Nothing interesting happens");
				break;
			case 21: // Superheat item
    			ItemSmeltingDef smeltingDef = affectedItem.getSmeltingDef();
  				if (smeltingDef == null) {
  					player.sendMessage("This spell can only be used on ore");
  					return;
  				}
      			for (ReqOreDef reqOre : smeltingDef.getReqOres()) {
      				if (player.getInventory().countId(reqOre.getId()) < reqOre.getAmount()) {
      					if (affectedItem.getID() == 151) {
      						smeltingDef = EntityHandler.getItemSmeltingDef(9999);
      						break;
      					}
      					player.sendMessage("You need " + reqOre.getAmount() + " " + EntityHandler.getItemDef(reqOre.getId()).getName() + " to smelt a " + affectedItem.getDef().getName() + ".");
      					return;
      				}
  				}
    			if (player.getCurStat(13) < smeltingDef.getReqLevel()) {
    				player.sendMessage("You need at least level-" + smeltingDef.getReqLevel() + " smithing to smelt " + affectedItem.getDef().getName().toLowerCase().replace(" ore", ""));
    				return;
    			}
				if (!checkAndRemoveRunes(player, spell))
					return;
  				InvItem bar = new InvItem(smeltingDef.getBarId());
  				if (player.getInventory().remove(affectedItem) > -1) {
		      		for (ReqOreDef reqOre : smeltingDef.getReqOres()) {
		      			for (int i = 0;i < reqOre.getAmount();i++)
		      				player.getInventory().remove(new InvItem(reqOre.getId()));
		      		}
		      		player.sendMessage("You make a bar of " + bar.getDef().getName().toLowerCase().replace(" bar", ""));
  					player.getInventory().add(bar);
  					player.increaseXP(Skills.SMITHING, smeltingDef.getExp());
  					player.sendStat(13);
  					player.sendInventory();
  				}
  				finalizeSpell(player, spell, false);
  				break;
			case 24: // Enchant lvl-3 ruby amulet
				if (affectedItem.getID() == 304) {
					if (!checkAndRemoveRunes(player, spell))
						return;
					player.getInventory().remove(affectedItem);
					player.getInventory().add(new InvItem(316));
					finalizeSpell(player, spell, true);
				} else 
					player.sendMessage("Nothing interesting happens");
				break;
			case 28: // High level alchemy
                // Non tradable items can be alched.
				/*if (!affectedItem.getDef().isTradable())
				{
					player.sendMessage("This item cannot be alched.");
					return;
				}*/
				if (affectedItem.getID() == 10) {
					player.sendMessage("That's already made of gold!");
					return;
				}
				if (affectedItem.getDef().isNote()) {
					player.sendMessage("I can not perform alchemy on that object!");
					return;
				}
				if (!checkAndRemoveRunes(player, spell))
					return;
				if (player.getInventory().remove(affectedItem) > -1) {
					long value = (int)(affectedItem.getDef().getBasePrice() * 0.6D);
					if(affectedItem.getDef().isStackable() /*|| affectedItem.getDef().isNote()*/)
						value = value * affectedItem.getAmount();
					player.getInventory().add(new InvItem(10, value)); // 60%
				}
				finalizeSpell(player, spell, true, "Alchemy spell successful");
				break;
			case 30: // Enchant lvl-4 diamond amulet
				if (affectedItem.getID() == 305) {
					if (!checkAndRemoveRunes(player, spell))
						return;
					player.getInventory().remove(affectedItem);
					player.getInventory().add(new InvItem(317));
					finalizeSpell(player, spell, true);
				} else
					player.sendMessage("Nothing interesting happens");
				break;
			case 43: // Enchant lvl-5 dragonstone amulet
				if (affectedItem.getID() == 610) {
					if (!checkAndRemoveRunes(player, spell))
						return;
					player.getInventory().remove(affectedItem);
					player.getInventory().add(new InvItem(522));
					finalizeSpell(player, spell, true);
				} else
					player.sendMessage("Nothing interesting happens");
				break;
		}
		if (affectedItem.isWielded()) {
			player.sendSound("click", false);
			affectedItem.setWield(false);
			player.updateWornItems(affectedItem.getWieldableDef().getWieldPos(), player.getPlayerAppearance().getSprite(affectedItem.getWieldableDef().getWieldPos()));
			player.sendEquipmentStats();
		}
	}
	
	private void handleGroundCast(Player player, SpellDef spell, int id) {
		switch(id) {			

		}
	}
	
	private void handleGameObjectCast(Player player, int spell, int obj) {
		if (player.isDueling()) {
			player.sendMessage("You can't do that during a duel!");
			return;
		}
		if (!checkAndRemoveRunes(player, EntityHandler.getSpellDef(spell)))
			return;
		switch(spell) {
			case 29:
				if (obj == 300) {
					player.getInventory().add(new InvItem(613,1));
					finalizeSpell(player, EntityHandler.getSpellDef(spell), true);
				}
				break;
			case 36:
				if (obj == 304) {
					player.getInventory().add(new InvItem(627,1));
					finalizeSpell(player, EntityHandler.getSpellDef(spell), true);
				}
				break;
			case 39:
				if (obj == 301) {
					player.getInventory().add(new InvItem(612,1));
					finalizeSpell(player, EntityHandler.getSpellDef(spell), true);					
				}
				break;
			case 41:
				if (obj == 303) {
					player.getInventory().add(new InvItem(626,1));
					finalizeSpell(player, EntityHandler.getSpellDef(spell), true);
				}
				break;
		}
	}	
	
	private void handleCastOnSelf(Player player, SpellDef spell, int id) {
		
		/*
		 * Bones to Bananas.
		 */
		if (id == 7)
		{
			if (!checkAndRemoveRunes(player, spell))
				return;
			
			Iterator<InvItem> inventory = player.getInventory().iterator();
			int boneCount = 0;
			while (inventory.hasNext()) {
				InvItem i = inventory.next();
				if (i.getID() == 20) {
					inventory.remove();
					boneCount++;
				}
			}
			
			for (int i = 0; i < boneCount; i++)
				player.getInventory().add(new InvItem(249));
			
			finalizeSpell(player, spell, true);
		}
		
		
		if (id != 48) {
			if (player.getLocation().wildernessLevel() > 20) {
				player.sendMessage("A magical force stops you from teleporting.");
				return;
			}
			
			/*
			 * Experimental Biohazard
			 * Quest.
			 */

			if (player.getInventory().containsAnyOf(809, 810, 811, 812))
			{				
				player.sendMessage("The vials break, you are going to have to get more.");
				player.getInventory().remove(809, -1);
				player.getInventory().remove(810, -1);
				player.getInventory().remove(811, -1);
				player.getInventory().remove(812, -1);
				player.sendInventory();
			}
			
			/*
			 * End Biohazard
			 * Quest
			 */
			
			if(player.getLocation().inCtf())
			{
				player.getActionSender().sendMessage("You cannot teleport while in CTF");
				return;
			}
			
			if (!checkAndRemoveRunes(player, spell))
				return;
			switch (id) {
				case 12: // Varrock
					player.teleport(122, 503, true);
				break;
				case 15: // Lumbridge
					player.teleport(118, 649, true);
				break;
				case 18: // Falador
					player.teleport(313, 550, true);
				break;
				case 22: // Camalot
						player.teleport(465, 456, true);
				break;
				case 26: // Ardougne
						player.teleport(585, 621, true);
				break;
				case 31: // Watchtower
					player.teleport(637, 2628, true);
				break;
				case 37: // Lost City
					player.teleport(131, 3544, true);
				break;
			}
			if (player.getInventory().contains(318)) {
				player.getInventory().remove(318, 1);
				while (player.getInventory().contains(318))
					player.getInventory().remove(318, 1);
				player.sendInventory();
			}
			finalizeSpell(player, spell, false);				
		} else {
			if (id == 48) {
				if (checkAndRemoveRunes(player, spell)) {
					if (World.getZone(player.getX(), player.getY()).getObjectAt(player.getX(), player.getY()) == null && World.getZone(player.getX(), player.getY()).getDoorAt(player.getX(), player.getY()) == null) {
						GameObject charge = new GameObject(player.getLocation(), 1147, 0, 0);
						World.registerEntity(charge);
						World.delayedRemoveObject(charge, 500);
					}
					player.sendMessage("@gre@You feel charged with magic power");
					player.setCharged();
					finalizeSpell(player, spell, true);
					World.getDelayedEventHandler().add(new SingleEvent(player, 420000) {
						public void action() {
							if (!owner.isCharged())
								owner.sendMessage("@red@Your magic charge fades");
						}
					});
				}
			}
		}
	}
	
	private static boolean canCast(Player player) {
		if (!player.canRange()) {
			player.resetPath();
			return false;
		}
		if (!player.castTimer()) {
			player.sendMessage("You need to wait " + player.getSpellWait() + " seconds before you can cast another spell");
			player.resetPath();
			return false;
		}
		return true;
	}
	
	private static boolean checkAndRemoveRunes(Player player, SpellDef spell) {
		for (Entry<Integer, Integer> e : spell.getRunesRequired()) {
			boolean skipRune = false;
			for (InvItem staff : getStaffs(e.getKey())) {
				if (player.getInventory().contains(staff)) {
					for (InvItem item : player.getInventory().getItems()) {
						if (item.equals(staff) && item.isWielded()) {
							skipRune = true;
							break;
						}
					}
				}
			}
			if (skipRune)
				continue;
			if (player.getInventory().countId(((Integer)e.getKey()).intValue()) < ((Integer)e.getValue()).intValue()) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "SpellHandler (1)", DataConversions.getTimeStamp()));
				return false;
			}
		}
      	for (Entry<Integer, Integer> e : spell.getRunesRequired()) {
			boolean skipRune = false;
			for (InvItem staff : getStaffs(e.getKey())) {
				if (player.getInventory().contains(staff)) {
					for (InvItem item : player.getInventory().getItems()) {
						if (item.equals(staff) && item.isWielded()) {
							skipRune = true;
							break;
						}
					}
				}
			}
			if (skipRune)
				continue;
			player.getInventory().remove(((Integer)e.getKey()).intValue(), ((Integer)e.getValue()).intValue());
      	}
		return true;
	}
	
	private void finalizeSpell(Player player, SpellDef spell, boolean message) {
		finalizeSpell(player, spell, message, "Cast spell successfully");
	}
	
	private void finalizeSpell(Player player, SpellDef spell, boolean message, String string) {
		if (player.getLocation().onTutorialIsland())
		{
			Quest tutorialIsland = player.getQuest(Quests.TUTORIAL_ISLAND);
			if (tutorialIsland != null)
			{
				if (tutorialIsland.getStage() == 19)
				{
					player.sendMessage("You have successfully cast a spell on the chicken.");
					player.sendMessage("Speak to the magic instructor for further instructions.");
					player.incQuestCompletionStage(Quests.TUTORIAL_ISLAND);
				}
			}
		}
		player.sendSound("spellok", false);
		if (message)
			player.sendMessage(string);
		player.increaseXP(Skills.MAGIC, spell.getExp());
		player.setCastTimer();
	}
	
	private boolean magerWithinBounds(Npc target, Player owner) {
		return (owner.getLocation().getX() < target.getLoc().maxX() && owner.getLocation().getX() > target.getLoc().minX() && owner.getLocation().getY() < target.getLoc().maxY() && owner.getLocation().getY() > target.getLoc().minY());
	}
	
	private static InvItem[] getStaffs(int runeID) {
		InvItem[] items = staffs.get(runeID);
		if (items == null)
			return new InvItem[0];
		return items;	
	}
	
	
	private static boolean isWeaken(int id) {
		return id == 1 || id == 5 || id == 9 || id == 42 || id == 45 || id == 47;
	}
	
	private static TreeMap<Integer, InvItem[]> staffs = new TreeMap<Integer, InvItem[]>();
	
	static {
		staffs.put(31, new InvItem[]{new InvItem(197), new InvItem(615), new InvItem(682)}); // Fire-Rune
		staffs.put(32, new InvItem[]{new InvItem(102), new InvItem(616), new InvItem(683)}); // Water-Rune
		staffs.put(33, new InvItem[]{new InvItem(101), new InvItem(617), new InvItem(684)}); // Air-Rune
		staffs.put(34, new InvItem[]{new InvItem(103), new InvItem(618), new InvItem(685)}); // Earth-Rune
	}
}