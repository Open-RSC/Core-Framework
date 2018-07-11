package org.openrsc.server.packethandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.ServerBootstrap;
import org.openrsc.server.database.DefaultTransaction;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.extras.ItemEdibleDef;
import org.openrsc.server.entityhandling.defs.extras.ItemUnIdentHerbDef;
import org.openrsc.server.event.DelayedGenericMessage;
import org.openrsc.server.event.MiniEvent;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ExploitLog;
import org.openrsc.server.model.GameObject;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Path;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;
import org.openrsc.server.states.Action;
import org.openrsc.server.util.DataConversions;
public class InvActionHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		int itemID = (int)p.readShort();
		if (itemID < 0 || itemID >= player.getInventory().size()) {
			Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "InvActionHandler (1)", DataConversions.getTimeStamp()));
		} else {
			final InvItem item = player.getInventory().get(itemID);
			if (item == null || item.getDef().getCommand().equals("")) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "InvActionHandler (2)", DataConversions.getTimeStamp()));
			} else if (player.isBusy()) {
				} else if (!player.inCombat()){
				if (item.getDef().getCommand().equalsIgnoreCase("drink") && !player.canDrink())
					return;
				player.resetAllExceptDMing();
				if (item.isEdible())
					eat(player, item);
				else if (item.getDef().getCommand().equalsIgnoreCase("bury"))
					bury(player, item);
				else if (item.getDef().getCommand().equalsIgnoreCase("clean"))
					clean(player, item);
				else if (item.getDef().getCommand().equalsIgnoreCase("drink"))
					drink(player, item);
				else if (item.getDef().getCommand().equalsIgnoreCase("release"))
					release(player, item);
				else if (item.getDef().getCommand().equalsIgnoreCase("redeem"))
					redeem(player, item);
				else if (item.getDef().getCommand().equalsIgnoreCase("open"))
					open(player, item);
				else {
					switch (item.getID()) {
						/*case 1032: // Cannon Base
							if(player.getCannonStage() != -1) {
								player.sendMessage("You may only set up one cannon at a time");
							} else if(World.getTile(player.getLocation()).hasDoor() || World.getTile(player.getLocation()).hasGameObject()) {
								player.sendMessage("You may not set up your cannon here");
							} else {
								player.updateCannonX(player.getX());
								player.updateCannonY(player.getY());
								player.updateCannonStage(0);
								player.getInventory().remove(new InvItem(1032, 1));
								player.sendInventory();
								World.registerGameObject(new GameObject(player.getCannonX(), player.getCannonY(), 1119, 0, 0));
								player.sendMessage("you place the cannon base on the ground");
							}
							break;*/
						case 1056: // Nulodion's notes
							player.sendAlert("Ammo for the dwarf multi cannon must be made from steel bars% %The bars must be heated in a furnace and used with the cannon ball mould.% %Due to the cannon ball's extreme weight, only so many may be carried before one must rest", true);
						break;
							
						case 781: // Scruffy Note
								player.setBusy(true);
								World.getDelayedEventHandler().add(new DelayedGenericMessage(player, new String[] {"Got a bncket of nnlk", "Tlen qrind sorne lhoculate", "Vnith a pestal and rnortar", "ald the grourd dlocolate to tho milt", "fnales add 5cme snape gras5", "You guess it really says something slightly different"}, 2500) {
									public void finished() 
									{
										player.setBusy(false);
									}
								});
						break;
						
						case 30:  //Shield of Arrav book
							player.setBusy(true);
							player.setStatus(Action.READING_BOOK);
							World.getDelayedEventHandler().add(new DelayedGenericMessage(player, new String[] {"The shield of Arrav", "By a.R.Wright", "Arrav is probably the best known hero of the 4th age.", "One surviving artifact from the 4th age is a fabulous shield.", "This shield is believed to have once belonged to Arrav", "And is now indeed known as the shield of Arrav", "For 15 years it was the prize piece in the royal museum of Varrock.", "However in the year 143 of the 5th age", "A gang of thieves called the phoenix gang broke into the museum", "And stole the shield", "King Roald the VII put a 1200 gold reward on the return of the shield", "The thieves who stole the shield", "Have now become the most powerful crime gang in Varrock", "The reward for the return of the shield still stands."}, 2500) {
								public void finished() {
									Quest q = owner.getQuest(Config.Quests.SHIELD_OF_ARRAV);
									if (q != null)
										if (q.getStage() == 0)
											owner.incQuestCompletionStage(Config.Quests.SHIELD_OF_ARRAV);
									owner.setBusy(false);
									owner.setStatus(Action.IDLE);
								}
							});
						break;
						
						case 895: //Swamp Toad
							//896
							player.setBusy(true);
							World.getDelayedEventHandler().add(new DelayedGenericMessage(player, new String[] {"You pull the legs off the toad", "poor toad...at least they'll grow back"}, 2000) {
								public void finished() {
									owner.setBusy(false);
									owner.getInventory().remove(item);
									owner.getInventory().add(new InvItem(896, 1));
									owner.sendInventory();
								}
							});
							break;

						case 597: // Charged Dragonstone Amulet
							
								if (player.getLocation().inCtf())
									return;

								player.sendMessage("You rub the amulet");
								player.sendMessage("Where would you like to teleport to?");
								World.getDelayedEventHandler().add(new MiniEvent(player) {
									public void action() {
										String[] options = new String[]{"Edgeville", "Karamja", "Draynor village", "Al Kharid", "Seers Village", "Yanille"};
										owner.setMenuHandler(new MenuHandler(options) {
											public void handleReply(final int option, final String reply) {
												if (owner.isBusy() || owner.getInventory().get(item) == null)
													return;
												if (owner.getLocation().inWilderness()) {
													owner.sendMessage("A magical force stops you from teleporting");
													return;
												}
												owner.sendSound("spellok", false);
												switch (option) {
													case 0: // Edgeville
														owner.teleport(226, 447, true);
														break;
													case 1: // Karamja
														owner.teleport(360, 696, true);
														break;
													case 2: // Draynor village
														owner.teleport(214, 632, true);
														break;
													case 3: // Al Kharid
														owner.teleport(72, 696, true);
														break;
													case 4: // Seers Village
														owner.teleport(516, 460, true);
														break;
													case 5: // Yanille
														owner.teleport(587, 761, true);
													break;
													default:
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
												 * Bullshit
												 */
												
												if (owner.getInventory().contains(318)) {
													owner.getInventory().remove(318, 1);
													while (owner.getInventory().contains(318))
														owner.getInventory().remove(318, 1);
													owner.sendInventory();
												}
												if (DataConversions.random(0, 5) == 1 && owner.getInventory().remove(item) > -1) {
													owner.getInventory().add(new InvItem(522, 1));
													owner.sendInventory();
												}
											}
										});
										owner.sendMenu(options);
									}
								});
							break;
						case 387: //Disk of Returning
							
							if (player.getLocation().inWilderness()) {
								player.sendMessage("The disk doesn't seem to work here");
								return;
							}
							player.sendMessage("The disk starts to spin...");
							if (player.getX() == 1 && player.getY() == 3456)
							{
								World.getDelayedEventHandler().add(new MiniEvent(player) {
									public void action() {
										owner.resetPath();
										owner.teleport(owner.getReturnX(), owner.getReturnY(), true);
										owner.getInventory().remove(387);
										owner.sendInventory();
										owner.sendMessage("You find yourself back where you originally spun the disk.");
									}
								});
							}
							else
							{
								World.getDelayedEventHandler().add(new MiniEvent(player) {
									public void action() {
										owner.resetPath();
										owner.setReturnPoint();
										owner.teleport(1, 3456, true);
										owner.sendMessage("You find yourself in a black hole.");
									}
								});
							}
							//player.sendMessage("Disabled");
							break;
						case 1263: //Sleeping Bag
							if (item.getID() == 1263 && !player.inCombat())
							{
								if (System.currentTimeMillis() - player.getLastSleep() > 500)
								{
									player.sleep(true);
									showBubble(player, item);
								}
							} 
							else
							{
								player.sendMessage("You cannot do that whilst fighting!");
							}
							break;
							
						case 1291: // Body talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 332, 507));
							showBubble(player, item);
						break;
						case 1292: // Air talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 299, 586));
							showBubble(player, item);
						break;
						case 1293: // Water talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 175, 682));
							showBubble(player, item);
						break;
						case 1294: // Fire talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 51, 646));
							showBubble(player, item);
						break;
						case 1295: // Earth talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 85, 468));
							showBubble(player, item);
						break;
						case 1296: // Nature talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 480, 671));
							showBubble(player, item);
						break;
						case 1297: // Law talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 402, 539));
							showBubble(player, item);
						break;
						case 1298: // Cosmic talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 149, 3538));
							showBubble(player, item);
						break;
						case 1299: // Chaos talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 209, 394));
							showBubble(player, item);
						break;
						case 1300: // Death talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 164, 119));
							showBubble(player, item);
						break;
						case 1301: // Blood talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 254, 124));
							showBubble(player, item);
						break;
						case 1302: // Soul talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 496, 533));
							showBubble(player, item);
						break;
						case 1303: // Mind talisman
							player.sendMessage(locateAltar(player.getX(), player.getY(), 234, 474));
							showBubble(player, item);
						break;
						
						default:
							player.sendMessage("Nothing interesting happens");
						return;
					} 
				}
			}
		}
	}
	
	
	public static String locateAltar(int playerX, int playerY, int altarX, int altarY) {
		if (playerX > 1 && playerX < 807 && playerY > 1 && playerY < 935) {
			if ((altarX == 254 && altarY == 3676) || (altarX == 213 && altarY == 3678) || (altarX == 202 && altarY == 3632) || (altarX == 276 && altarY == 3677))
				return "The talisman pulls towards the ground.";
		}
		else
		if (playerX > 193 && playerX < 283 && playerY > 3630 && playerY < 3695) {
			if ((altarX == 251 && altarY == 106) || (altarX == 215 && altarY == 98) || (altarX == 173 && altarY == 679) || (altarX == 233 && altarY == 469) || (altarX == 194 && altarY == 451) || (altarX == 250 && altarY == 447) || (altarX == 292 && altarY == 475) || (altarX == 197 && altarY == 416) && (altarX == 241 && altarY == 388))
				return "The talisman pulls towards the sky.";
			else if ((altarX == 197 && altarY == 416) || (altarX == 241 && altarY == 388))
				return "The talisman pulls towards the sky.";
		}
		else
		{
			return "The talisman doesn't seem to work here.";
		}
						
			String string = "";
			
			if (Math.abs(playerY - altarY) > 0)
				string = (playerY < altarY ? "south" : "north");
			
			if (Math.abs(playerX - altarX) > 0)
				string += (string.equals("") ? "" : "-") + (playerX < altarX ? "west" : "east");
			
			return "The talisman pulls towards the " + string + "."; 
	}
	
	private void open(final Player player, final InvItem item)
	{
		if (item != null)
		{
			switch (item.getID())
			{
				/*
				 * Halloween Shit
				 */
				case 1358:
					player.sendMessage("You open the " + item.getDef().getName() + "...");
      				player.getInventory().remove(1358, 1);
					player.sendInventory();
					
					if (DataConversions.random(0, 80) != 13)
					{
						player.sendMessage("You've been tricked!");

						Npc Ghost = World.getNpc(4, player.getX(), player.getX(), player.getY(), player.getY());

						if (Ghost == null)
						{
							Ghost = new Npc(4, player.getX(), player.getY(), player.getX(), player.getX(), player.getY(), player.getY(), true);
							Ghost.setRespawn(false);
							World.registerEntity(Ghost, 15000);
							Ghost.setAggressive(player);
							player.teleport(Ghost.getX(), Ghost.getY());
						}
					}
					else
					{
	      				int[] randomLoot = new int[] { 1356, 1357 };
	      				final int loot = randomLoot [DataConversions.getRandom().nextInt(randomLoot.length)];
						
						InvItem halloweenitem = new InvItem(loot, 1);
						player.sendMessage("Inside you find a " + halloweenitem.getDef().getName());
						player.getInventory().add(halloweenitem);
						player.sendInventory();
						player.setBusy(false);
					}
				break;
				case 1353:
					player.sendMessage("Are you sure you wish to open this " + item.getDef().getName());
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							String[] menu_options = new String[]{"Yes", "I'll pass" };
							owner.setMenuHandler(new MenuHandler(menu_options) 
							{
								public void handleReply(final int option, final String reply) 
								{
									if (owner.isBusy() || owner.getInventory().get(item) == null)
										return;
									switch (option) 
									{
										case 0: // Pker Gear
											if (player.getInventory().canHold(10))
											{
												if (player.getInventory().remove(1353, 1) != -1) 
												{
													player.sendMessage("You pry open the crate...");
													player.getInventory().add(1452, 5);
													player.getInventory().add(1736, 5);
													player.getInventory().add(1737, 5);
													player.getInventory().add(1421, 5);
													player.getInventory().add(1707, 500);
													player.getInventory().add(1560, 10);
													player.getInventory().add(1654, 5);
													player.sendInventory();
												}
											}
											else
											{
												player.sendMessage("Please make at least 10 slots available before opening your" + item.getDef().name);
												return;
											}
										break;
										
										case 1: // Cancel, Intentionally empty...
											
										break;
										
										default:
											return;
									}
								}
							});
							owner.sendMenu(menu_options);
						}
					});	
				break;
				
				case 1354:
					player.sendMessage("Are you sure you wish to open this " + item.getDef().name);
					World.getDelayedEventHandler().add(new MiniEvent(player) {
						public void action() {
							String[] pouch_options = new String[]{"Yes", "I'll pass" };
							owner.setMenuHandler(new MenuHandler(pouch_options) 
							{
								public void handleReply(final int option, final String reply) 
								{
									if (owner.isBusy() || owner.getInventory().get(item) == null)
										return;
									switch (option) 
									{
										case 0: // Pker Gear
											if (player.getInventory().canHold(3))
											{
												if (player.getInventory().remove(1354, 1) != -1) 
												{
													player.sendMessage("You open the pouch...");
													player.getInventory().add(31, 15000);
													player.getInventory().add(33, 12000);
													player.getInventory().add(38, 3000);
													player.sendInventory();
												}
											}
											else
											{
												player.sendMessage("Please make at least 3 slots available before opening your" + item.getDef().name);
												return;
											}
										break;
										
										case 1: // Cancel, Intentionally empty...
											
										break;
										
										default:
											return;
									}
								}
							});
							owner.sendMenu(pouch_options);
						}
					});	
				break;
				
				case 796: // Mithril Seeds.
					if (player.getLocation().inWilderness() || World.getZone(player.getX(), player.getY()).getObjectAt(player.getX(), player.getY()) != null || World.getZone(player.getX(), player.getY()).getDoorAt(player.getX(), player.getY()) != null || (World.getZone(player.getX(), player.getY()).getPlayersAt(player.getX(), player.getY()).size() > 1))
					{
						player.sendMessage("You cannot plant a mithril seed here at this time");
						return;
					}
					else 
					{
						player.getInventory().remove(796, 1);
						player.sendInventory();							
						final GameObject tree = new GameObject(player.getLocation(), 490, 0, 0);
						World.registerEntity(tree);
						World.delayedRemoveObject(tree, 60000);
					}
				break;
				
				default:
					player.sendMessage("Nothing interesting happens.");
				break;
			}
		}
	}
	
	private final static class SubscriptionTransaction
		extends
			DefaultTransaction
	{
		private /** final */ static PreparedStatement SUBSCRIBE_PS, LOG_PS;
		
		static
		{
			SubscriptionTransaction dummy = new SubscriptionTransaction();
			try
			{
				Connection connection = dummy.getConnection();
				SUBSCRIBE_PS = connection.prepareStatement("UPDATE `users` SET `group_id` = CASE WHEN `group_id` = '4' THEN '5' ELSE `group_id` END, `sub_expires` = CASE WHEN `sub_expires` >= UNIX_TIMESTAMP() THEN `sub_expires` + 2592000 ELSE UNIX_TIMESTAMP() + 2592000 END, `character_limit` = `character_limit` + 1 WHERE `id` = ?");
				LOG_PS = connection.prepareStatement("INSERT INTO `"+Config.LOG_DB_NAME+"`.`game_redeem` (`user`, `account`, `time`, `ip`) VALUES (?, ?, ?, ?);");
			}
			catch(SQLException e)
			{
				throw (ExceptionInInitializerError)new ExceptionInInitializerError().initCause(e);
			}
		}
		
		private final long user;
		private final int account;
		private final int time;
		private final String ip;
		
		private SubscriptionTransaction()
		{
			this.user = 0;
			this.account = 0;
			this.time = 0;
			this.ip = null;
		}
		
		public SubscriptionTransaction(long user, int account, int time, String ip)
		{
			this.user = user;
			this.account = account;
			this.time = time;
			this.ip = ip;
		}
		
		@Override
		public String toString()
		{
		   return "\"RedeemSubscriptionCard\" {user=" + DataConversions.hashToUsername(user) + "}";
		}
		
		@Override
		public Integer call()
			throws
				SQLException
		{
			SUBSCRIBE_PS.setInt(1, account);
			SUBSCRIBE_PS.executeUpdate();
			LOG_PS.setLong(1, user);
			LOG_PS.setInt(2, account);
			LOG_PS.setInt(3, time);
			LOG_PS.setString(4, ip);
			LOG_PS.executeUpdate();
			return 0;
		}

		@Override
		public boolean retryOnFatalError()
		{
			return false;
		}
		
	}
	
	private void redeem(final Player player, final InvItem item) {
		player.sendMessage("Are you sure you would like to subscribe this account?");
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				String[] options = new String[]{"Yes, subscribe this account", "No, not at this time"};
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						if (owner.isBusy() || owner.getInventory().get(item) == null)
							return;
						switch (option) {
							case 0: // Yes, subscribe.
								if (player.getInventory().remove(1304, 1) != -1) {
									player.sendInventory();		
									showBubble(player, item);
									if (player.getSubscriptionExpires() > 0)
										player.setSubscriptionExpires(2592000 + player.getSubscriptionExpires());
									else
										player.setSubscriptionExpires(DataConversions.getTimeStamp() + 2592000);
									player.sendAlert("Thank you for subscribing to " + Config.SERVER_NAME + "!  Without subscribers we simply wouldn't be able to keep " + Config.SERVER_NAME + " up and running.  You have " + player.getDaysSubscriptionLeft() + " days remaining.");
									ServerBootstrap.getDatabaseService().submit(new SubscriptionTransaction(player.getUsernameHash(), player.getAccount(), DataConversions.getTimeStamp(), player.getIP()));
									player.updateGroupID(5);
								}
								break;
							default:
								return;
						}
					}
				});
				owner.sendMenu(options);
			}
		});	
	}
	
	private void release(Player player, final InvItem item)
	{
		if (player.isBusy())
			return;
		
		switch (item.getID())
		{
			case 799:
				Quest Biohazard_Quest = player.getQuest(Config.Quests.BIOHAZARD);
				if (Biohazard_Quest.getStage() == 2)
				{
					if (player.getSeedsUsed() > 0 && player.getLocation().inBounds(620, 583, 622, 589))
					{
						player.setBusy(true);
						World.getDelayedEventHandler().add(new DelayedGenericMessage(player, new String[] {"You open the cage", "The pigeons fly towards the watch tower", "They begin pecking at the bird feed", "The mourners are frantically trying to scare the pigeons away"}, 2000) 
						{
							public void finished() 
							{
								owner.incQuestCompletionStage(Config.Quests.BIOHAZARD);
								owner.setBusy(false);
							}
						});
					}
					else
					{
						player.sendMessage("The pigeons do not want to come out.");
						return;
					}
				}
				else
				{
					player.sendMessage("The pigeons do not want to come out.");
					return;
				}
			break;
		}
	}
	
	private void drink(Player player, final InvItem item) {
		if (player.canDrink()) {
				if (player.getLocation().wildernessLevel() > 0 && !World.isP2PWilderness() && item.getDef().isP2P())
				{
					player.sendMessage(Config.PREFIX + "You can only use this potion when the wilderness state is P2P");
					return;
				}
				player.setLastDrink();
				switch (item.getID()) {
					case 221: // Strength Potion - 4 dose
						useNormalPotion(player, item, 2, 10, 2, 222, 3);
						break;
					case 222: // Strength Potion - 3 dose
						useNormalPotion(player, item, 2, 10, 2, 223, 2);
						break;
					case 223: // Strength Potion - 2 dose
						useNormalPotion(player, item, 2, 10, 2, 224, 1);
						break;
					case 224: // Strength Potion - 1 dose
						useNormalPotion(player, item, 2, 10, 2, 465, 0);
						break;
					case 474: // Attack Potion - 3 dose
						useNormalPotion(player, item, 0, 10, 2, 475, 2);
						break;
					case 475: // Attack Potion - 2 dose
						useNormalPotion(player, item, 0, 10, 2, 476, 1);
						break;
					case 476: // Attack Potion - 1 dose
						useNormalPotion(player, item, 0, 10, 2, 465, 0);
						break;
					case 477: // stat restoration Potion - 3 dose
						useStatRestorePotion(player, item, 478, 2);
						break;
					case 478: // stat restoration Potion - 2 dose
						useStatRestorePotion(player, item, 479, 1);
						break;
					case 479: // stat restoration Potion - 1 dose
						useStatRestorePotion(player, item, 465, 0);
						break;
					case 480: // defense Potion - 3 dose
						useNormalPotion(player, item, 1, 10, 2, 481, 2);
						break;
					case 481: // defense Potion - 2 dose
						useNormalPotion(player, item, 1, 10, 2, 482, 1);
					break;
					case 482: // defense Potion - 1 dose
						useNormalPotion(player, item, 1, 10, 2, 465, 0);
						break;
					case 483: // restore prayer Potion - 3 dose
						usePrayerPotion(player, item, 484, 2);
						break;
					case 484: // restore prayer Potion - 2 dose
						usePrayerPotion(player, item, 485, 1);
						break;
					case 485: // restore prayer Potion - 1 dose
						usePrayerPotion(player, item, 465, 0);
						break;
					case 486: // Super attack Potion - 3 dose
						useNormalPotion(player, item, 0, 15, 4, 487, 2);
						break;
					case 487: // Super attack Potion - 2 dose
						useNormalPotion(player, item, 0, 15, 4, 488, 1);
						break;
					case 488: // Super attack Potion - 1 dose
						useNormalPotion(player, item, 0, 15, 4, 465, 0);
						break;
					case 489: // fishing Potion - 3 dose
						useFishingPotion(player, item, 490, 2);
						break;
					case 490: // fishing Potion - 2 dose
						useFishingPotion(player, item, 491, 1);
						break;
					case 491: // fishing Potion - 1 dose
						useFishingPotion(player, item, 465, 0);
						break;
					case 492: // Super strength Potion - 3 dose
						useNormalPotion(player, item, 2, 15, 4, 493, 2);
						break;
					case 493: // Super strength Potion - 2 dose
						useNormalPotion(player, item, 2, 15, 4, 494, 1);
						break;
					case 494: // Super strength Potion - 1 dose
						useNormalPotion(player, item, 2, 15, 4, 465, 0);
						break;
					case 495: // Super defense Potion - 3 dose
						useNormalPotion(player, item, 1, 15, 4, 496, 2);
						break;
					case 496: // Super defense Potion - 2 dose
						useNormalPotion(player, item, 1, 15, 4, 497, 1);
						break;
					case 497: // Super defense Potion - 1 dose
						useNormalPotion(player, item, 1, 15, 4, 465, 0);
						break;
					case 498: // ranging Potion - 3 dose
						useNormalPotion(player, item, 4, 10, 2, 499, 2);
						break;
					case 499: // ranging Potion
						useNormalPotion(player, item, 4, 10, 2, 500, 1);
						break;
					case 500: // ranging Potion
						useNormalPotion(player, item, 4, 10, 2, 465, 0);
						break;
					case 963: //Zamorak Potion
						useZamorakPotion(player, item, 964, 2);
						break;
					case 964: //Zamorak Potion
						useZamorakPotion(player, item, 965, 1);
						break;		
					case 965: //Zamorak Potion
						useZamorakPotion(player, item, 465, 0);
						break;
					case 566: //Cure poison Potion
						useCurePotion(player, item, 567, 2);
						break;
					case 567: //Cure poison Potion
						useCurePotion(player, item, 568, 1);
						break;
					case 568: //Cure poison Potion
						useCurePotion(player, item, 465, 0);
						break;						
					case 739: //Tea
						player.setStatus(Action.DRINKING);
						player.sendMessage("You drink the " + item.getDef().getName());
						World.getDelayedEventHandler().add(new MiniEvent(player) {
							public void action() {
								owner.sendMessage("It's nice and refreshing");
								owner.getInventory().remove(item);
								owner.sendInventory();
								owner.setStatus(Action.IDLE);
							}
						});
						showBubble(player, item);
						break;
					case 193: //Beer
						player.setStatus(Action.DRINKING);
						player.sendMessage("You drink the " + item.getDef().getName());
						World.getDelayedEventHandler().add(new MiniEvent(player) {
							public void action() {
								owner.sendMessage("You feel slightly dizzy.");
								owner.setCurStat(0, owner.getCurStat(0) - 2);
								owner.sendStat(0);
								if (owner.getCurStat(2) <= owner.getMaxStat(2)) {
									owner.setCurStat(2, owner.getCurStat(2) + 2);
									owner.sendStat(2);
								}
								owner.getInventory().remove(item);
								owner.getInventory().add(new InvItem(620));
								owner.sendInventory();
								owner.setStatus(Action.IDLE);
							}
						});
						showBubble(player, item);
						break;
					case 142: //Wine
						player.setStatus(Action.DRINKING);
						player.sendMessage("You drink the " + item.getDef().getName());
						World.getDelayedEventHandler().add(new MiniEvent(player) {
							public void action() {
								owner.sendMessage("You feel slightly dizzy.");
								owner.setCurStat(0, owner.getCurStat(0) - 2);
								int newHits = owner.getCurStat(3) + 10;
								if (newHits > owner.getMaxStat(3))
									newHits = owner.getMaxStat(3);
								owner.setCurStat(3, newHits);
								owner.sendStat(3);
								owner.sendStat(0);
								if (owner.getCurStat(2) <= owner.getMaxStat(2)) {
									owner.setCurStat(2, owner.getCurStat(2) + 2);
									owner.sendStat(2);
								}
								owner.getInventory().remove(item);
								owner.getInventory().add(new InvItem(140));
								owner.sendInventory();
								owner.setStatus(Action.IDLE);
							}
						});
						showBubble(player, item);
						break;
					case 501: //Wine of Zamorak
						player.setStatus(Action.DRINKING);
						player.sendMessage("You drink the " + item.getDef().getName());
						World.getDelayedEventHandler().add(new MiniEvent(player) {
							public void action() {
								owner.sendMessage("You feel slightly dizzy.");
								owner.setCurStat(0, owner.getCurStat(0) - 2);
								owner.sendStat(0);
								if (owner.getCurStat(2) <= owner.getMaxStat(2)) {
									owner.setCurStat(2, owner.getCurStat(2) + 2);
									owner.sendStat(2);
								}
								owner.getInventory().remove(item);
								owner.getInventory().add(new InvItem(140));
								owner.sendInventory();
								owner.setStatus(Action.IDLE);
							}
						});
						showBubble(player, item);
						break;
						case 830: //Greenmans Ale
						player.setStatus(Action.DRINKING);
						player.sendMessage("You drink the " + item.getDef().getName());
						World.getDelayedEventHandler().add(new MiniEvent(player) {
							public void action() {
								owner.sendMessage("It has a strange taste.");
								for (int stat = 0;stat < 3; stat++) {
									owner.setCurStat(stat, owner.getCurStat(stat) - 4);
									owner.sendStat(stat);
								}
								if (owner.getCurStat(15) <= owner.getMaxStat(15)) {
									owner.setCurStat(15, owner.getCurStat(15) + 1);
									owner.sendStat(15);
								}
								owner.getInventory().remove(item);
								owner.getInventory().add(new InvItem(620));
								owner.sendInventory();
								owner.setStatus(Action.IDLE);
							}
						});
						showBubble(player, item);
					break;
					
					case 268: //Mind Bomb
						player.setStatus(Action.DRINKING);
						player.sendMessage("You drink the " + item.getDef().getName());
						World.getDelayedEventHandler().add(new MiniEvent(player) {
							public void action() {
								owner.sendMessage("You feel very strange.");
								for (int stat = 0; stat < 3; stat++) {
									owner.setCurStat(stat, owner.getCurStat(stat) - 2);
									owner.sendStat(stat);
								}
								if (owner.getCurStat(6) <= owner.getMaxStat(6)) {
									owner.setCurStat(6, owner.getCurStat(6) + 3);
									owner.sendStat(6);
								}
								owner.getInventory().remove(item);
								owner.getInventory().add(new InvItem(620));
								owner.sendInventory();
								owner.setStatus(Action.IDLE);
							}
						});
						showBubble(player, item);
					break;
					
					case 269: //Dwarven Stout
						player.setStatus(Action.DRINKING);
						player.sendMessage("You drink the " + item.getDef().getName() + ".");
						World.getDelayedEventHandler().add(new MiniEvent(player) {
							public void action() {
								owner.sendMessage("It tastes foul.");
								for (int stat = 0; stat < 3; stat++) {
									owner.setCurStat(stat, owner.getCurStat(stat) - 2);
									owner.sendStat(stat);
								}
								if (owner.getCurStat(13) <= owner.getMaxStat(13)) {
									owner.setCurStat(13, owner.getCurStat(13) + 1);
									owner.sendStat(13);
								}
								if (owner.getCurStat(14) <= owner.getMaxStat(14)) {
									owner.setCurStat(14, owner.getCurStat(14) + 1);
									owner.sendStat(14);
								}
								owner.getInventory().remove(item);
								owner.getInventory().add(new InvItem(620));
								owner.sendInventory();
								owner.setStatus(Action.IDLE);
							}
						});
						showBubble(player, item);
						break;
					case 267: //Asgarnian Ale
						player.setStatus(Action.DRINKING);
						player.sendMessage("You drink the " + item.getDef().getName() + ".");
						World.getDelayedEventHandler().add(new MiniEvent(player) {
							public void action() {
								owner.sendMessage("You feel slightly reinvigorated");
								owner.sendMessage("And slightly dizzy too.");
								owner.setCurStat(0, owner.getCurStat(0) - 4);
								owner.sendStat(0);
								if (owner.getCurStat(2) <= owner.getMaxStat(2)) {
									owner.setCurStat(2, owner.getCurStat(2) + 2);
									owner.sendStat(2);
								}
								owner.getInventory().remove(item);
								owner.getInventory().add(new InvItem(620));
								owner.sendInventory();
								owner.setStatus(Action.IDLE);
							}
						});
						showBubble(player, item);
						break;
					case 829: //Dragon Bitter
						player.setStatus(Action.DRINKING);
						player.sendMessage("You drink the " + item.getDef().getName());
						World.getDelayedEventHandler().add(new MiniEvent(player) {
							public void action() {
								owner.sendMessage("You feel slightly dizzy");
								owner.setCurStat(0, owner.getCurStat(0) - 4);
								owner.sendStat(0);
								if (owner.getCurStat(2) <= owner.getMaxStat(2)) {
									owner.setCurStat(2, owner.getCurStat(2) + 2);
									owner.sendStat(2);
								}
								owner.getInventory().remove(item);
								owner.getInventory().add(new InvItem(620));
								owner.sendInventory();
								owner.setStatus(Action.IDLE);
							}
						});
						showBubble(player, item);
						break;
					default:
						player.sendMessage("Nothing interesting happens");
				}
			}
		}
	
	private void clean(Player player, final InvItem item) {
		ItemUnIdentHerbDef herb = item.getUnIdentHerbDef();
		if (herb != null) {
			if (player.getMaxStat(15) < herb.getLevelRequired())
				player.sendMessage("Your herblaw ability is not high enough to clean this herb");
			else {
				player.setBusy(true);
				World.getDelayedEventHandler().add(new MiniEvent(player) {
					public void action() {
						ItemUnIdentHerbDef herb = item.getUnIdentHerbDef();
						InvItem newItem = new InvItem(herb.getNewId());
						owner.getInventory().remove(item);
						owner.getInventory().add(newItem);
						owner.sendMessage("You clean the mud off the " + newItem.getDef().getName());
						owner.increaseXP(15, herb.getExp(), 1);
						owner.sendStat(15);
						owner.sendInventory();
						owner.setBusy(false);
					}
				});
			}
		}
	}
	
	private void bury(Player player, final InvItem item) {
		player.setBusy(true);
		player.sendMessage("You dig a hole in the ground");
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				//owner.resetPath();
				owner.sendMessage("You bury the " + item.getDef().getName());
				owner.getInventory().remove(item);
				switch(item.getID()) {
					case 20: // Bones
						owner.increaseXP(5, 15, 1);
						break;						
					case 604: // Bat Bones
						owner.increaseXP(5, 18, 1);
						break;
					case 413: // Big Bones
						owner.increaseXP(5, 50, 1);
						break;
					case 814: // Dragon Bones
						owner.increaseXP(5, 240, 1);
						break;
				}
				owner.sendStat(5);
				owner.sendInventory();
				owner.setBusy(false);
			}
		});
	}
	
	private void eat (Player player, final InvItem item)
	{
		if (player.canEat())
		{
			if (player.getLocation().inWilderness() && !World.isP2PWilderness() && item.getDef().isP2P())
			{
				player.sendMessage(Config.PREFIX + "This item is only edible when the wilderness state is P2P.");
				return;
			}
			
			player.setLastEat();
			player.setStatus(Action.EATING);
			player.sendSound("eat", false);
			
			final ItemEdibleDef def = EntityHandler.getItemEdibleHeals(item.getID());
			if(def != null) {
				player.sendMessage(def.getEatMessage().replaceAll("%item%", item.getDef().getName()));
				player.getInventory().remove(item);
				if (def.getReplacement() > -1)
					player.getInventory().add(new InvItem(def.getReplacement(), 1));
				player.sendInventory();
				if (player.getCurStat(3) < player.getMaxStat(3)) {
					World.getDelayedEventHandler().add(new SingleEvent(player, 100) {
						public void action() {
							owner.sendMessage(def.getHealMessage().replaceAll("%item%", item.getDef().getName()));
							int newHits = owner.getCurStat(3) + def.getHealth();
							if (newHits > owner.getMaxStat(3))
								newHits = owner.getMaxStat(3);
							owner.setCurStat(3, newHits);
							owner.sendStat(3);
							owner.setStatus(Action.IDLE);
						}
					});
				} else {
					player.setStatus(Action.IDLE);
				}
			}
		}
	}

	
	private void showBubble(Player player, final InvItem item) {
		//Bubble bubble = new Bubble(player.getIndex(), item.getID());
		for (Player p1 : player.getViewArea().getPlayersInView())
		{
			p1.watchItemBubble(player.getIndex(), item.getID());
			//p1.informOfBubble(bubble);
		}
	}
	
	private void useNormalPotion(Player player, final InvItem item, final int affectedStat, final int percentageIncrease, final int modifier, final int newItem, final int left) {
		player.setStatus(Action.DRINKING);
		if (!player.getLocation().inWilderness())
			showBubble(player, item);
		player.getInventory().remove(item);
		player.getInventory().add(new InvItem(newItem));
		player.sendMessage("You drink some of your " + item.getDef().getName());
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (left == 0)
					owner.sendMessage("You have finished your potion");
				else if (left == 1)
					owner.sendMessage("You have " + left + " dose of potion left");
				else
					owner.sendMessage("You have " + left + " doses of potion left");
				int baseStat = owner.getCurStat(affectedStat) > owner.getMaxStat(affectedStat) ? owner.getMaxStat(affectedStat) : owner.getCurStat(affectedStat);
				int newStat = baseStat + DataConversions.roundUp((owner.getMaxStat(affectedStat) / 100D) * percentageIncrease) + modifier;
				if (newStat > owner.getCurStat(affectedStat)) {
					owner.setCurStat(affectedStat, newStat);
					owner.sendStat(affectedStat);
				}
				owner.sendInventory();
				/*
				if (owner.getLocation().inWilderness()) {
					if (owner.getCurStat(0) > owner.getMaxStat(0)) {
						owner.setCurStat(0, owner.getMaxStat(0));
					}
					if (owner.getCurStat(1) > owner.getMaxStat(1)) {
						owner.setCurStat(1, owner.getMaxStat(1));
					}
					if ((long)owner.getCurStat(2) > (long)owner.getMaxStat(2) * 1.1 + 3) {
						owner.setCurStat(2, (owner.getMaxStat(2) + DataConversions.roundUp((owner.getMaxStat(2) / 100D) * 10) + 2));
					}
					if (owner.getCurStat(3) > owner.getMaxStat(3)) {
						owner.setCurStat(3, owner.getMaxStat(3));
					}
					if (owner.getCurStat(4) > owner.getMaxStat(4)) {
						owner.setCurStat(4, owner.getMaxStat(4));
					}
					owner.sendStats();
				}
				*/
				owner.setStatus(Action.IDLE);
			}
		});
	}
	
	private void usePrayerPotion(Player player, final InvItem item, final int newItem, final int left) {
		if (player.getDMSetting(3)) {
			player.sendMessage(Config.PREFIX + "Potions have been disabled in this Death Match");
			return;
		}		
		player.setStatus(Action.DRINKING);
		if (!player.getLocation().inWilderness())
			showBubble(player, item);		
		player.getInventory().remove(item);
		player.getInventory().add(new InvItem(newItem));
		player.sendMessage("You drink some of your " + item.getDef().getName());
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (left == 0)
					owner.sendMessage("You have finished your potion");
				else if (left == 1)
					owner.sendMessage("You have " + left + " dose of potion left");
				else
					owner.sendMessage("You have " + left + " doses of potion left");
				int newPrayer = owner.getCurStat(5) + 21;
				if (newPrayer > owner.getMaxStat(5))
					newPrayer = owner.getMaxStat(5);
				owner.setCurStat(5, newPrayer);
				owner.sendStat(5);
				owner.sendInventory();
				owner.setStatus(Action.IDLE);
			}
		});
	}
	
	private void useStatRestorePotion(Player player, final InvItem item, final int newItem, final int left) {
		if (player.getDMSetting(3)) {
			player.sendMessage(Config.PREFIX + "Potions have been disabled in this Death Match");
			return;
		}		
		player.setStatus(Action.DRINKING);
		if (!player.getLocation().inWilderness())
			showBubble(player, item);
		player.getInventory().remove(item);
		player.getInventory().add(new InvItem(newItem));
		player.sendMessage("You drink some of your " + item.getDef().getName());
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (left == 0)
					owner.sendMessage("You have finished your potion");
				else if (left == 1)
					owner.sendMessage("You have " + left + " dose of potion left");
				else
					owner.sendMessage("You have " + left + " doses of potion left");
				for (int i = 0;i < 18;i++) {
					if (i == 3 || i == 5)
						continue;
					int max = owner.getMaxStat(i);
					if (owner.getCurStat(i) < max) {
						owner.setCurStat(i, max);
						owner.sendStat(i);
					}
				}
				owner.sendInventory();
				owner.setStatus(Action.IDLE);
			}
		});
	}
	
	private void useFishingPotion(Player player, final InvItem item, final int newItem, final int left) {
		if (player.getDMSetting(3)) {
			player.sendMessage(Config.PREFIX + "Potions have been disabled in this Death Match");
			return;
		}		
		player.setStatus(Action.DRINKING);
		showBubble(player, item);
		player.getInventory().remove(item);
		player.getInventory().add(new InvItem(newItem));
		player.sendMessage("You drink some of your " + item.getDef().getName());
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (left == 0)
					owner.sendMessage("You have finished your potion");
				else if (left == 1)
					owner.sendMessage("You have " + left + " dose of potion left");
				else
					owner.sendMessage("You have " + left + " doses of potion left");
				owner.setCurStat(10, owner.getMaxStat(10) + 3);
				owner.sendStat(10);
				owner.sendInventory();
				owner.setStatus(Action.IDLE);
			}
		});
	}
	
	private void useZamorakPotion(Player player, final InvItem item, final int newItem, final int left) {
		if (player.getLocation().isInDMArena())
			player.sendMessage(Config.PREFIX + "You cannot drink Zamorak potions in the DM arena");
		else {
			player.setStatus(Action.DRINKING);
			if (!player.getLocation().inWilderness())
				showBubble(player, item);		
			player.getInventory().remove(item);
			player.getInventory().add(new InvItem(newItem));
			player.sendInventory();
			player.sendMessage("You drink some of your " + item.getDef().getName() + ".");
			World.getDelayedEventHandler().add(new MiniEvent(player) {
				public void action() {
					if (left == 0)
						owner.sendMessage("You have finished your potion");
					else if (left == 1)
						owner.sendMessage("You have " + left + " dose of potion left");
					else
						owner.sendMessage("You have " + left + " doses of potion left");
					int newAttack, newDefense, newStrength, hitDecrement;
					newAttack = (int)(owner.getMaxStat(0) * 1.2) + 1;
					newDefense = (int)(owner.getMaxStat(1) * 0.95);
					newStrength = (int)(owner.getMaxStat(2) * 1.2) + 1;
					hitDecrement = (int)(owner.getCurStat(3) - owner.getCurStat(3) * 0.8);
					if (newAttack > owner.getCurStat(0))
						owner.setCurStat(0, newAttack);
					if (newDefense < owner.getCurStat(1))
						owner.setCurStat(1, newDefense);
					if (newStrength > owner.getCurStat(2))
						owner.setCurStat(2, newStrength);
					owner.setLastDamage(hitDecrement);
					owner.setCurStat(3, owner.getCurStat(3) - hitDecrement);
					owner.sendStats();			
					ArrayList<Player> playersToInform = new ArrayList<Player>();
					playersToInform.addAll(owner.getViewArea().getPlayersInView());
					owner.sendMessage("@red@You feel stronger but you are affected by it...");
					for (Player p : playersToInform)
						p.informOfModifiedHits(owner);
					if (owner.getCurStat(3) <= 0)
						owner.killedBy(null, false);
					if (owner.getLocation().inWilderness()) {
						if (owner.getCurStat(0) > owner.getMaxStat(0)) {
							owner.setCurStat(0, owner.getMaxStat(0));
						}
						if (owner.getCurStat(1) > owner.getMaxStat(1)) {
							owner.setCurStat(1, owner.getMaxStat(1));
						}
						if ((long)owner.getCurStat(2) > (long)owner.getMaxStat(2) * 1.1 + 3) {
							owner.setCurStat(2, (owner.getMaxStat(2) + DataConversions.roundUp((owner.getMaxStat(2) / 100D) * 10) + 2));
						}
						if (owner.getCurStat(3) > owner.getMaxStat(3)) {
							owner.setCurStat(3, owner.getMaxStat(3));
						}
						if (owner.getCurStat(4) > owner.getMaxStat(4)) {
							owner.setCurStat(4, owner.getMaxStat(4));
						}
						owner.sendStats();
					}
					owner.setStatus(Action.IDLE);
				}
			});
		}
	}
	
	private void useCurePotion(Player player, final InvItem item, final int newItem, final int left) {
		if (player.getDMSetting(3)) {
			player.sendMessage(Config.PREFIX + "Potions have been disabled in this Death Match");
			return;
		}		
		player.setStatus(Action.DRINKING);
		player.getInventory().remove(item);
		player.getInventory().add(new InvItem(newItem));
		player.sendInventory();
		player.sendMessage("You drink some of your " + item.getDef().getName() + ".");
		World.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (left == 0)
					owner.sendMessage("You have finished your potion");
				else if (left == 1)
					owner.sendMessage("You have " + left + " dose of potion left");
				else
					owner.sendMessage("You have " + left + " doses of potion left");

				owner.setStatus(Action.IDLE);
				owner.curePoison();
			}
		});
	}		
}