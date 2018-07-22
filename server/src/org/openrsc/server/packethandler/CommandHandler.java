package org.openrsc.server.packethandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import com.rscdaemon.scripting.Skill;
import com.runescape.entity.attribute.DropItemAttr;
import java.util.Iterator;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.ServerBootstrap;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.event.ChangePasswordEvent;
import org.openrsc.server.event.DelayedEvent;
import org.openrsc.server.event.ShutdownEvent;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.GenericLog;
import org.openrsc.server.logging.model.GlobalLog;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.GameObject;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Item;
import org.openrsc.server.model.Mob;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Point;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;
import org.openrsc.server.states.CombatState;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.EntityList;
import org.openrsc.server.util.Formulae;

public class CommandHandler implements PacketHandler 
{
	public void handlePacket(Packet p, IoSession session) throws Exception 
	{
		Player player = (Player)session.getAttachment();
		if (player != null) 
		{
			if (System.currentTimeMillis() - player.getLastCommand() < 1000 && !player.isAdmin())
			{
				player.sendMessage(Config.PREFIX + "There's a second delay between using commands");
			}
			else 
			{
				String s = new String(p.getData()).trim();
				int firstSpace = s.indexOf(" ");
				String cmd = s;
				String[] args = new String[0];
				
				if (firstSpace != -1) 
				{
					cmd = s.substring(0, firstSpace).trim();
					args = s.substring(firstSpace + 1).trim().split(" ");
				} 
				try 
				{
					handleCommand(cmd.toLowerCase(), args, player);
				} 
				catch(Exception e) {}
			}
		}
	}
	
	public void handleCommand(String cmd, final String[] args, final Player player) 
	{
		for (int index = 0; index < args.length - 1; index++) 
		{
			args[index] = args[index].replace("-", " ");
			args[index] = args[index].replace("_", " ");
		}
		
		player.setLastCommand(System.currentTimeMillis());
		if ((cmd.equals("coords")) && (player.isMod() || player.isDev())) 
		{
            Player p = args.length > 0 ? 
                        World.getPlayer(DataConversions.usernameToHash(args[0])) :
                        player;
            
            if(p == null)
                player.sendMessage(Config.PREFIX + "Invalid player");
            else
                player.sendMessage(Config.PREFIX + player.getLocation()+" ");
		}
        else
		if (cmd.equals("online") && player.isMod()) 
		{
			StringBuilder sb = new StringBuilder();
			synchronized (World.getPlayers()) 
			{
				EntityList<Player> players = World.getPlayers();
				sb.append("@gre@There are currently ").append(players.size()).append(" player(s) online.\n\n");
				for (Player p : players) 
				{
					Point loc = p.getLocation();
					if (player.isSub())
						sb.append("@whi@").append(p.getUsername()).append(loc.inWilderness() ? " @red@".concat("Wilderness").concat("\n") : "\n");
					else
					if (player.isMod())
						sb.append("@whi@").append(p.getUsername()).append(" @yel@(").append(loc).append(")").append(loc.inWilderness() ? " @red@".concat(loc.getDescription().concat("\n")) : "\n");	
				}
			}
			player.getActionSender().sendScrollableAlert(sb.toString());
		}
		else
		/*
		 * Set invisible
		 */
		if (cmd.equals("invisible") && (player.isMod() || player.isDev())) 
		{
			player.invisible = !player.invisible;
			player.sendMessage(Config.PREFIX + "You are now " + (player.invisible ? "invisible" : "visible"));
			if (player.invisible)
			for (Player x : player.getViewArea().getPlayersInView())
			x.removeWatchedPlayer(player);
		} 
        else // leave CTF event
		if (cmd.equalsIgnoreCase("leavectf") && player.getLocation().inCtf())
		{
			player.removeFromCtf(player);
			player.sendAlert("You have been removed from CTF");
		}
        else // use global chat
		if (cmd.equals("say") || cmd.equals("s")) 
		{
			if (player.getPrivacySetting(4)) 
			{
				if (!World.global && !player.isMod())
					player.sendMessage(Config.PREFIX + "Global Chat is currently disabled");
				else 
				if (World.muted && !player.isMod())
					player.sendMessage(Config.PREFIX + "The world is muted");				
				else 
				if (player.getMuted() > 0)
					player.sendMessage(Config.PREFIX + "You are muted");		
				else
				if (System.currentTimeMillis() - player.getLastGlobal() < 10000 && !player.isMod())
					player.sendMessage(Config.PREFIX + "There's a 10 second delay using Global Chat");
				else 
				{	
					player.setLastGlobal(System.currentTimeMillis());
					String message = "";
					for (int i = 0; i < args.length; i++) 
					{
						message = message += args[i] + " ";
					}
						
					Logger.log(new GlobalLog(player.getUsernameHash(), player.getAccount(), player.getIP(), message, DataConversions.getTimeStamp()));
					synchronized (World.getPlayers()) 
					{
						for (Player p : World.getPlayers()) 
						{
							if (player.isAdmin())
								p.sendNotification("#adm#@yel@" + player.getUsername() + ":@whi@ " + message);
							else
							if (player.isMod())
								p.sendNotification("#mod#@whi@" + player.getUsername() + ":@whi@ " + message);
							else
							if (player.isDev())
								p.sendNotification("#dev#@red@" + player.getUsername() + ":@whi@ " + message);
							else
							if (player.isEvent())
								p.sendNotification("#eve#@eve@" + player.getUsername() + ":@whi@ " + message);
							else 
							if (!p.getIgnoreList().contains(player.getUsernameHash()) && p.getPrivacySetting(4) == true || player.isMod())
								p.sendGlobalMessage(player.getUsernameHash(), player.getGroupID() == 4 ? (player.isSub() ? 5 : 4) : player.getGroupID(), message);
						}						
					}
				}
			} 
			else
				player.sendMessage(Config.PREFIX + "You cannot use Global Chat as you have it disabled");
		} 
        else 
        /*
         * Alert player
         */
        if (cmd.equals("alert") && player.isMod()) 
        {
            String message = "";
            if (args.length > 0) 
            {
                Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
                if (p != null)
                {
                    for (int i = 1; i < args.length; i++)
                    message += args[i] + " ";
                    p.sendAlert((player.getStaffName()) + ":@whi@ " + message);
                    player.sendMessage(Config.PREFIX + "Alert sent");
                    Logger.log(new GenericLog(player.getUsername() + " alerted " + p.getUsername() +": " + message, DataConversions.getTimeStamp()));
                }
                else
                    player.sendMessage(Config.PREFIX + "Invalid player");
            } 
			else
                player.sendMessage(Config.PREFIX + "Syntax: ALERT [name] [message]");	
		} 
		else
        if (cmd.equals("iplimit") && player.isAdmin())
        {
            if(args.length != 1)
            {
                player.sendMessage("Invalid Syntax - Usage: iplimit [amount]");
                return;
            }
            try
            {
                Config.MAX_LOGINS_PER_IP = Integer.parseInt(args[0]);
                for(Player p : World.getPlayers())
                {
                    p.sendNotification(Config.PREFIX + "Max logins per IP has been set to: " + Config.MAX_LOGINS_PER_IP );
                }
            }
            catch(Exception e)
            {
                player.sendMessage("Invalid Syntax - Usage: iplimit [amount]");
                return;
            }
        }
        else // Give a player a skull
		if (cmd.equals("skull")) 
		{
			if (args.length > 0 && player.isAdmin() || player.isMod()) 
			{
				Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
				
				if (p != null)
				{
					p.addSkull(1200000);
				}
				else
				{
					player.sendMessage(Config.PREFIX + "Invalid name");	
				}
			} 
			else
			{
				player.addSkull(1200000);
			}
		} 
		else // Heal a player
		if (cmd.equals("heal") && player.isAdmin()) 
		{
			if (args.length > 0) 
			{
				Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
				
				if (p != null) 
				{
					p.setCurStat(3, p.getMaxStat(3));
					p.sendStat(3);
				} 
				else
				{
					player.sendMessage(Config.PREFIX + "Invalid name");	
				}
			} 
			else 
			{
				player.setCurStat(3, player.getMaxStat(3));
				player.sendStat(3);
			}
		} 
		else 
		/*
		 * Toggle global
		 */
		if(cmd.equals("global") && player.isMod()) 
		{
			World.global = !World.global;
			synchronized (World.getPlayers()) 
			{
				for (Player p : World.getPlayers()) 
				{
					p.sendNotification(Config.PREFIX + "Global Chat has been " + (World.global ? "enabled" : "disabled") + " by " + player.getStaffName());
				}
			}
		} 
		else 
		/*
		 * Toggle dueling
		 */
		if(cmd.equals("dueling") && player.isMod())
		{
			World.dueling = !World.dueling;
			synchronized (World.getPlayers()) 
			{
				for (Player p : World.getPlayers()) 
				{
					p.sendNotification(Config.PREFIX + "Dueling has been " + (World.dueling ? "enabled" : "disabled") + " by " + player.getStaffName());
				}
			}	
		} 
		else 
		/*
		 * Mute world
		 */
		if (cmd.equals("muted") && player.isAdmin()) 
		{
			World.muted = !World.muted;
			synchronized (World.getPlayers()) 
			{
				for (Player p : World.getPlayers()) 
				{
					p.sendNotification(Config.PREFIX + "World Mute has been " + (World.muted ? "enabled" : "disabled") + " by " + player.getStaffName());
				}
			}			
		} 
		else
			/*
			 * Fatigue player
			 */
			if (cmd.equals("fatigue")) 
			{
				if (args.length > 0 && player.isMod()) 
				{
					Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
					if (p != null) 
					{
						int fatigue = args.length > 1 ? Integer.parseInt(args[1]) : 100;
						if(fatigue < 0)
						{
							fatigue = 0;
						}
						if(fatigue > 100)
						{
							fatigue = 100;
						}
						p.setFatigue((int)(18750 * (fatigue / 100.0D)));
						p.sendFatigue();
						player.sendMessage(Config.PREFIX + p.getUsername() + "'s fatigue has been set to " + ((p.getFatigue() / 25) * 100 / 750) + "%");
						Logger.log(new GenericLog(player.getUsername() + " set " + p.getUsername() + "'s fatigue to " + ((p.getFatigue() / 25) * 100 / 750) + "%", DataConversions.getTimeStamp()));
					} 
					else
					{
						player.sendMessage(Config.PREFIX + "Invalid name");	
					}
				} 
				else 
				{
					player.setFatigue(18750);
					player.sendFatigue();
				}
			}
		if (cmd.equals("ip") && player.isAdmin()) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: " + cmd.toUpperCase() + " [name]");
				return;
			}
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null) 
			{
				long requestee = player.getUsernameHash();
				p.requestLocalhost(requestee);
				Logger.log(new GenericLog(player.getUsername() + " requested " + p.getUsername() + "'s IP", DataConversions.getTimeStamp()));
			} 
			else
				player.sendMessage(Config.PREFIX + "Invalid name");
		} 
		else 
		if (cmd.equals("info") && player.isMod()) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: " + cmd.toUpperCase() + " [name]");
				return;
			}
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null) 
			{
				player.sendAlert(p.getUsername() + " (" + p.getStatus() + ") at " + player.getLocation().toString() + " (" + player.getLocation().getDescription() + ") % % Logged in: " + (DataConversions.getTimeStamp() - player.getLastLogin()) + " seconds % % Last moved: " + (int)((System.currentTimeMillis() - player.getLastMoved()) / 1000) + " % % Fatigue: " + ((p.getFatigue() / 25) * 100 / 750) + " % %Busy: " + (p.isBusy() ? "true" : "false"), true);
			} 
			else
				player.sendMessage(Config.PREFIX + "Invalid name");
		} 
		else 
		/*
		 * Kick player
		 */
		if (cmd.equals("kick") && player.isMod()) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: " + cmd.toUpperCase() + " [name]");
				return;
			}	
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null) 
			{
				if (p.isDueling() && !player.isAdmin())
					player.sendMessage(Config.PREFIX + "You cannot kick players who are dueling");	
				else 
				{				
					World.unregisterEntity(p);
					player.sendMessage(Config.PREFIX + p.getUsername() + " has been kicked");
					Logger.log(new GenericLog(player.getUsername() + " kicked " + p.getUsername(), DataConversions.getTimeStamp()));
				}
			}
		}
		else 
		/*
		 * Ban player
		 */
		if (cmd.equals("ban") && (player.isMod() || player.isDev())) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: " + cmd.toUpperCase() + " [name]");
				return;
			}
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null)
			{
				p.ban();
			}
			else 
			{
				ServerBootstrap.getDatabaseService().submit(new Player.BanTransaction(DataConversions.usernameToHash(args[0]), true));
				Logger.log(new GenericLog(player.getUsername() + " banned " + DataConversions.hashToUsername(DataConversions.usernameToHash(args[0])), DataConversions.getTimeStamp()));
				player.sendMessage(Config.PREFIX + DataConversions.hashToUsername(DataConversions.usernameToHash(args[0])) + " has been banned");
			} 
		}
		else 
		/*
		 * Unban player
		 */
		if (cmd.equals("unban") && (player.isMod() || player.isDev())) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: " + cmd.toUpperCase() + " [name]");
				return;
			}
			
			ServerBootstrap.getDatabaseService().submit(new Player.BanTransaction(DataConversions.usernameToHash(args[0]), false));			
			Logger.log(new GenericLog(player.getUsername() + " unbanned " + DataConversions.hashToUsername(DataConversions.usernameToHash(args[0])), DataConversions.getTimeStamp()));
			player.sendMessage(Config.PREFIX + DataConversions.hashToUsername(DataConversions.usernameToHash(args[0])) + " has been unbanned");				
		}
		else 
		if (cmd.equals("mute") && (player.isMod() || player.isDev())) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: " + cmd.toUpperCase() + " [name]");
				return;
			}
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null) 
			{
				p.mute(0);										
				Logger.log(new GenericLog(player.getUsername() + " muted " + p.getUsername(), DataConversions.getTimeStamp()));
				ServerBootstrap.getDatabaseService().submit(new Player.MuteTransaction(DataConversions.usernameToHash(args[0]), true));
				player.sendMessage(Config.PREFIX + p.getUsername() + " has been muted");	
			}
		} 
		else 
		/*
		 * Unmute player
		 * ::unmute <playername>
		 */
		if (cmd.equals("unmute") && (player.isMod() || player.isDev())) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: " + cmd.toUpperCase() + " [name]");
				return;
			}
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null) 
			{
				p.unmute();										
				Logger.log(new GenericLog(player.getUsername() + " unmuted " + p.getUsername(), DataConversions.getTimeStamp()));
				ServerBootstrap.getDatabaseService().submit(new Player.MuteTransaction(DataConversions.usernameToHash(args[0]), false));
				player.sendMessage(Config.PREFIX + p.getUsername() + " has been unmuted");	
			}		
		}
		else 
		/*
		 * Spawn NPC
		 * ::npc <id> 
		 */
		if (cmd.equals("npc") && (player.isAdmin() || player.isDev())) 
		{
			if (args.length == 0) 
			{
				for (Npc n : World.getZone(player.getX(), player.getY()).getNpcsAt(player.getX(), player.getY())) 
				{
					Mob opponent = n.getOpponent();
					
					if (opponent != null)
					{
						opponent.resetCombat(CombatState.ERROR);
					}
					
					n.resetCombat(CombatState.ERROR);
					World.unregisterEntity(n);
					n.remove();
				}
			} 
			else 
			{
				int id = -1;
				try 
				{
					id = Integer.parseInt(args[0]);
				} 
				catch(Exception ex) {}
				int duration = 0;
				if (args.length == 1)
				{
					duration = 60000;
				} 
				else 
				if (args.length == 2) 
				{
					try 
					{
						duration = Integer.parseInt(args[1]) * 60000;
					} 
					catch(Exception ex) {}
				}
				if (EntityHandler.getNpcDef(id) != null) 
				{
					final Npc n = new Npc(id, player.getX(), player.getY(), player.getX() - 2, player.getX() + 2, player.getY() - 2, player.getY() + 2);
					n.setRespawn(false);
					World.registerEntity(n);
					World.getDelayedEventHandler().add(new SingleEvent(null, duration) 
					{
						public void action() 
						{
							Mob opponent = n.getOpponent();
							
							if (opponent != null)
							{
								opponent.resetCombat(CombatState.ERROR);
							}
							
							n.resetCombat(CombatState.ERROR);
							n.remove();
						}
					});
				} 
				else
				{
					player.sendMessage(Config.PREFIX + "Invalid ID");
				}
			}
		} 
		else 
		/*
		 * Teleport single-click
		 */
		if ((cmd.equals("teleport") || cmd.equals("tp")) && (player.isMod() || player.isDev() || player.isEvent())) 
		{
			player.resetLevers();
			if (args.length == 0) 
			{
				player.teleport = !player.teleport;
				player.sendMessage(Config.PREFIX + "Single click teleport " + (player.teleport ? "enabled" : "disabled"));
			} 
			else 
			if (args.length == 1) 
			{
				if(!EntityHandler.getTeleportManager().containsTeleport(args[0]))
				{
					player.sendMessage(Config.PREFIX + "Teleport location \"" + args[0] + "\" does not exist");
					player.sendMessage(Config.PREFIX + "hint: you can add it via the website");
				}
				else
				{
					player.teleport(EntityHandler.getTeleportManager().getTeleport(args[0]), false);
				}
			} 
			else if (args.length == 2) 
			{
				if (World.withinWorld(Integer.parseInt(args[0]), Integer.parseInt(args[1])))
				{
					player.teleport(Integer.parseInt(args[0]), Integer.parseInt(args[1]), false);
				}
            }
			else if (args.length == 3) 
			{
				if (World.withinWorld(Integer.parseInt(args[0]), Integer.parseInt(args[1])))
				{
					player.teleport(Integer.parseInt(args[0]), Integer.parseInt(args[1]), false);
				}
			}	
		} 
        else if((cmd.equalsIgnoreCase("appearance")) && (player.isAdmin())) {
			if (args.length > 0) 
			{
				Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
				
				if (p != null) 
				{
                    p.setChangingAppearance(true);
                    p.getActionSender().sendAppearanceScreen();
				} 
				else
				{
					player.sendMessage(Config.PREFIX + "Invalid name");	
				}
			} 
			else 
			{
                player.setChangingAppearance(true);
                player.getActionSender().sendAppearanceScreen();
			}
			return;
		}
		else
		if (cmd.equals("summon") && (player.isMod() || player.isDev())) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: " + cmd.toUpperCase() + " [name]");
				return;
			}
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null) 
			{
                if(p.getGroupID() >= 4)
                {
                    if (p.getLocation().inCtf())
                        player.sendMessage(Config.PREFIX + "You cannot summon players who are in CTF");
                    else
                    if (p.isDueling() && !player.isAdmin())
                        player.sendMessage(Config.PREFIX + "You cannot summon players who are dueling");
                    else 
                    if (player.getLocation().inWilderness() && !player.isAdmin())
                        player.sendMessage(Config.PREFIX + "You cannot summon players into the wilderness");
                    else 
                    {
                        p.setReturnPoint();
                        p.teleport(player.getX(), player.getY(), false);
                        Logger.log(new GenericLog(player.getUsername() + " summoned " + p.getUsername() + " to " + "(" + p.getX() + ", " + p.getY() + ")", DataConversions.getTimeStamp()));					
                    }
                }
                else
                {
                    player.sendMessage(Config.PREFIX + "Staff members can not be summoned");
                }
			} 
			else
			{
				player.sendMessage(Config.PREFIX + "Invalid name");
			}
		} 
		else 
		if (cmd.equals("return") && (player.isMod() || player.isDev())) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: " + cmd.toUpperCase() + " [name]");
				return;
			}
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null) 
			{
                if(p.getGroupID() >= 4)
                {
                    if (p.wasSummoned()) 
                    {
                        p.setSummoned(false);
                        p.teleport(p.getReturnX(), p.getReturnY(), false);
                        Logger.log(new GenericLog(player.getUsername() + " returned " + p.getUsername() + " to " + " (" + p.getX() + ", " + p.getY() + ")", DataConversions.getTimeStamp()));
                    } 
                    else
                    {
                        player.sendMessage(Config.PREFIX + p.getUsername() + " has no return point set");
                    }
                }
                else
                {
                    player.sendMessage(Config.PREFIX + "Staff members can not be summoned");
                }
			} 
			else
			{
				player.sendMessage(Config.PREFIX + "Invalid name");
			}
		} 
		else 
		if (cmd.equals("jail") && (player.isMod() || player.isDev() || player.isEvent())) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: " + cmd.toUpperCase() + " [name]");
				return;
			}
            
            Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));

			if (p != null) 
			{
                if (p.getGroupID() >= 4) 
                {
                    if(!p.getLocation().isInJail())
                    {
                        p.teleport(793, 24, false);
                        player.sendMessage(Config.PREFIX + p.getUsername() + " has been jailed");
                        p.sendAlert("You have been jailed.");
                    }
                    else
                    {
                        player.sendMessage(Config.PREFIX + p.getUsername() + " is already in jail");
                    }
                } 
                else
                {
                    player.sendMessage(Config.PREFIX + "Staff members can not be jailed");
                }
            }
			else
			{
				player.sendMessage(Config.PREFIX + "Invalid name");
			}
		} 
		else 
		if (cmd.equals("release") && (player.isMod() || player.isDev() || player.isEvent())) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: " + cmd.toUpperCase() + " [name]");
				return;
			}
            
            Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
            
			if (p != null) 
			{
                if (p.getGroupID() >= 4) 
                {
                    if(p.getLocation().isInJail())
                    {
                        p.teleport(120, 648, false);
                        p.sendAlert("You have been released from jail.");
                        player.sendMessage(Config.PREFIX + p.getUsername() + " has been released from jail.");
                    }
                    else
                    {
                        player.sendMessage(Config.PREFIX + p.getUsername() + " is not in jail");
                    }
                } 
                else
                {
                    player.sendMessage(Config.PREFIX + "Staff members can not be released");
                }
            }
			else
			{
				player.sendMessage(Config.PREFIX + "Invalid name");
			}
		} 
		else 
		if ((cmd.equals("goto") || cmd.equals("tpto") || cmd.equals("teleportto")) && (player.isMod() || player.isDev())) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: " + cmd.toUpperCase() + " [name]");
				return;
			}
            
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
            if(p != null)
            {
                player.teleport(p.getX(), p.getY(), false);
                Logger.log(new GenericLog(player.getUsername() + " went to " + p.getUsername() + " (" + p.getX() + ", " + p.getY() + ")", DataConversions.getTimeStamp()));
            }
 			else
			{
				player.sendMessage(Config.PREFIX + "Invalid name");
			}
		} 
		else 
		if ((cmd.equals("restart") || cmd.equals("update")) && (player.isAdmin() || player.isDev()))
		{
			String message = "";
			if (args.length > 0) {
				for (String s : args)
					message += (s + " ");
				message = message.substring(0, message.length() - 1);
			}
			World.getWorld().getEventPump().submit(new ShutdownEvent(true, message));
		}
		else 
		if (cmd.equals("item") && player.isAdmin())
		{
			if (args.length < 1 || args.length > 2)
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: ITEM [id] [amount]");
			}
			else 
			{
				int id = Integer.parseInt(args[0]);
				if (EntityHandler.getItemDef(id) != null) 
				{
					long amount = 1;
					if (args.length == 2 && EntityHandler.getItemDef(id).isStackable())
					{
						amount = Long.parseLong(args[1]);
					}
					InvItem i = new InvItem(id, amount);
					player.getInventory().add(i);
					player.sendInventory();
					Logger.log(new GenericLog(player.getUsername() + " spawned " + amount + " " + EntityHandler.getItemDef(id).name, DataConversions.getTimeStamp()));
				} 
				else
				{
					player.sendMessage(Config.PREFIX + "Invalid ID");
				}
			}
		} 
		if (cmd.equals("object") && (player.isAdmin() || player.isDev()))
		{
			switch (args.length) {
				case 0: // Remove Object (from both in-game and database)
					GameObject o = World.getZone(player.getX(), player.getY()).getObjectAt(player.getX(), player.getY());
					if (o != null) {
						World.unregisterEntity(o);
						try {
							World.getWorldLoader().writeQuery("DELETE FROM `spawn_object` WHERE `x` = '" + player.getX() + "' AND `y` = '" + player.getY() + "'");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				break;
					
				case 1: // Create Object (default direction)
					if (EntityHandler.getGameObjectDef(Integer.parseInt(args[0])) != null)
						World.registerEntity(new GameObject(player.getLocation(), Integer.parseInt(args[0]), 0, 0));
					else	
						player.sendMessage(Config.PREFIX + "Invalid ID");
				break;
					
				case 2: // Create Object (custom direction OR saving object to database)
					boolean SQL = args[1].equalsIgnoreCase("true");
					if (EntityHandler.getGameObjectDef(Integer.parseInt(args[0])) != null) {
						if (SQL) {
							try {
								World.getWorldLoader().writeQuery("INSERT INTO `spawn_object` (`object`, `x`, `y`, `direction`) VALUES ('" + Integer.parseInt(args[0]) + "', '" + player.getX() + "', '" + player.getY() + "', '" + 0 + "')");
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							player.sendMessage(Config.PREFIX + "Added object " + Integer.parseInt(args[0]) + " to database");
						}
						World.registerEntity(new GameObject(player.getLocation(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), 0));
					} else
						player.sendMessage(Config.PREFIX + "Invalid ID");
				break;
				
				case 3: // Create Object (custom direction AND saving object to database)
					SQL = args[2].equalsIgnoreCase("true");
					if (EntityHandler.getGameObjectDef(Integer.parseInt(args[0])) != null) {
						if (SQL) {
							try {
								World.getWorldLoader().writeQuery("INSERT INTO `spawn_object` (`object`, `x`, `y`, `direction`) VALUES ('" + Integer.parseInt(args[0]) + "', '" + player.getX() + "', '" + player.getY() + "', '" + Integer.parseInt(args[1]) + "')");
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							player.sendMessage(Config.PREFIX + "Added object " + Integer.parseInt(args[0]) + " to database");
						}
						World.registerEntity(new GameObject(player.getLocation(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), 0));
					} else
						player.sendMessage(Config.PREFIX + "Invalid ID");
				break;
			}
		} 
		else 
		if (cmd.equals("door") && (player.isAdmin() || player.isDev()))
		{
			int id = -1, direction = 0;
			boolean sql;
			try {
				if (args.length == 0) {
					GameObject o = World.getZone(player.getX(), player.getY()).getDoorAt(player.getX(), player.getY());
					if (o != null) {
						World.unregisterEntity(o);
						World.getWorldLoader().writeQuery("DELETE FROM `spawn_object` WHERE `x` = '" + player.getX() + "' AND `y` = '" + player.getY() + "' AND `type` = '1'");
					}					
				} else {
					id = Integer.parseInt(args[0]);
					if (args[0] != null && EntityHandler.getDoorDef(id) == null)
						player.sendMessage(Config.PREFIX + "Invalid ID");
					else {
						GameObject o = World.getZone(player.getX(), player.getY()).getDoorAt(player.getX(), player.getY());
						if (o != null)
							World.unregisterEntity(o);					
						switch(args.length) {
							case 1:
								World.registerEntity(new GameObject(player.getLocation(), id, 0, 1));
								break;
							case 2:
								try {
									direction = Integer.parseInt(args[1]);
									World.registerEntity(new GameObject(player.getLocation(), id, direction, 1));
								} catch(Exception e) {
									sql = args[1].equalsIgnoreCase("true");
									if (sql) {
										player.sendMessage("Object written to MySQL");
										World.registerEntity(new GameObject(player.getLocation(), id, 0, 1));
									} else
										player.sendMessage(Config.PREFIX + "Invalid args!");
								}
								break;
							case 3:
								try {
									direction = Integer.parseInt(args[1]);
									sql = args[2].equalsIgnoreCase("true");
									World.registerEntity(new GameObject(player.getLocation(), id, direction, 1));
									if (sql)
										World.getWorldLoader().writeQuery("INSERT INTO `spawn_object` (`object`, `type`, `x`, `y`, `direction`) VALUES ('" + id + "', '1', '" + player.getX() + "', '" + player.getY() + "', '" + direction + "')");
								} catch(Exception e) {
									player.sendMessage(Config.PREFIX + "Invalid direction");
								}
						}
					}
				}
			} catch(Exception e) {
				player.sendMessage(Config.PREFIX + "Invalid ID");
			}
		} 
		else
		if (cmd.equals("state"))
		{
			System.out.println(World.wildernessP2P);
		}
		else
		if (cmd.equals("wipeinventory") && player.isMod()) 
		{
			for (InvItem i : player.getInventory().getItems()) {
				if (player.getInventory().get(i).isWielded()) {
					player.getInventory().get(i).setWield(false);
					player.updateWornItems(i.getWieldableDef().getWieldPos(), player.getPlayerAppearance().getSprite(i.getWieldableDef().getWieldPos()));
				}	
			}
			player.getInventory().getItems().clear();
			player.sendInventory();
		} else if (cmd.equals("wipebank") && player.isMod())
			player.getBank().getItems().clear();			
		else if (cmd.equals("kill") && player.isAdmin()) {
			if (args.length != 1)
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: KILL [user]");
			else {
				Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
				if (p != null) {
					p.setLastDamage(99);
					p.setHits(p.getHits() - 99);
					ArrayList<Player> playersToInform = new ArrayList<Player>();
					playersToInform.addAll(player.getViewArea().getPlayersInView());
					playersToInform.addAll(p.getViewArea().getPlayersInView());
					for (Player i : playersToInform)
						i.informOfModifiedHits(p);
					p.sendStat(3);
					if (p.getHits() <= 0)
						p.killedBy(player, false);						
				}
			}
		} else if (cmd.equals("damage") && player.isAdmin()) {
			if (args.length != 2)
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: DAMAGE [user] [amount]");
			else {
				int damage = Integer.parseInt(args[1]);
				Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
				if (p != null) {
					p.setLastDamage(damage);
					p.setHits(p.getHits() - damage);
					ArrayList<Player> playersToInform = new ArrayList<Player>();
					playersToInform.addAll(player.getViewArea().getPlayersInView());
					playersToInform.addAll(p.getViewArea().getPlayersInView());
					for(Player i : playersToInform)
						i.informOfModifiedHits(p);
					p.sendStat(3);
					if (p.getHits() <= 0)
						p.killedBy(player, false);	
				}
			}
		} else if (cmd.equals("stats") && player.isAdmin()) {
			int level = 99;
			if (args.length > 0) {
				try {
					level = Integer.parseInt(args[0]);
				} catch (Exception e) { }
			}
			
			if (level > 255) {
				level = 255;
			}
			if (level < 1) {
				level = 1;
			}
			
			for (Skill skill : Skill.values()) {
				player.setCurStat(skill.ordinal(), level);
				player.setMaxStat(skill.ordinal(), level);
				player.setExp(skill.ordinal(), Formulae.lvlToXp(level));
			}
			player.setCombatLevel(Formulae.getCombatlevel(player.getMaxStats()));
			player.sendStats();
			player.sendMessage(Config.PREFIX + "Set all stats to level " + level + ".");
		} else if (cmd.equals("summonall") && player.isAdmin()){
			if (args.length == 0) {
				synchronized (World.getPlayers()) {
					for (Player p : World.getPlayers()) {
						p.setReturnPoint();
						p.resetLevers();
						p.teleport(player.getX(), player.getY(), true);
					}
				}
			} else if (args.length == 2) {
				int width = -1;
				int height = -1;
				try {
					width = Integer.parseInt(args[0]);
					height = Integer.parseInt(args[1]);
				} catch(Exception ex) {
					player.sendMessage(Config.PREFIX + "Invalid dimensions");
				}
				if (width > 0 && height > 0) {
					Random rand = new Random(System.currentTimeMillis());
					synchronized (World.getPlayers()) {
						for (Player p : World.getPlayers()) {
							if (p != player) {
								int x = rand.nextInt(width);
								int y = rand.nextInt(height);
								boolean XModifier = (rand.nextInt(2) == 0 ? false : true);
								boolean YModifier = (rand.nextInt(2) == 0 ? false : true);
								if (XModifier)
									x = -x;
								if (YModifier)
									y = -y;
								p.setReturnPoint();
								p.resetLevers();
								p.teleport(player.getX() + x, player.getY() + y, false);
							}
						}
					}
				}
			}	
		} else if(cmd.equals("returnall") && player.isAdmin()) {
			synchronized (World.getPlayers()) {
				for (Player p : World.getPlayers()) {
					if (p != null) {
						if (p.wasSummoned()) {
							p.setSummoned(false);
							p.teleport(p.getReturnX(), p.getReturnY(), false);
						}
					}
				}
			}
		} else if (cmd.equals("massitem") && player.isAdmin()) {
			if (args.length != 2) {
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: MASSITEM [id] [amount]");
				return;
			}
			int id = Integer.parseInt(args[0]);
			int amount = Integer.parseInt(args[1]);
			if (EntityHandler.getItemDef(id) != null) {
				int x = 0;
				int y = 0;
				int baseX = player.getX();
				int baseY = player.getY();
				int nextX = 0;
				int nextY = 0;
				int dX = 0;
				int dY = 0;
				int minX = 0;
				int minY = 0;
				int maxX = 0;
				int maxY = 0;
				int scanned = 0;
				while (scanned < amount) {
					scanned++;
					if (dX < 0) {
						x -= 1;
						if (x == minX) {
							dX = 0;
							dY = nextY;
							if (dY < 0)
								minY -= 1;
							else
								maxY += 1;
							nextX = 1;
						}
					} else if (dX > 0) {
						x += 1;
						if (x == maxX) {
							dX = 0;
							dY = nextY;
							if (dY < 0)
								minY -=1;
							else
								maxY += 1;
							nextX = -1;
						}
					} else {
						if (dY < 0) {
							y -= 1;
							if (y == minY) {
								dY = 0;
								dX = nextX;
								if (dX < 0)
									minX -= 1;
								else
									maxX += 1;
								nextY = 1;
							}
						} else if (dY > 0) {
							y += 1;
							if (y == maxY) {
								dY = 0;
								dX = nextX;
								if (dX < 0)
									minX -= 1;
								else
									maxX += 1;
								nextY = -1;
							}
						} else {
							minY -= 1;
							dY = -1;
							nextX = 1;
						}
					}
					if (!((baseX + x) < 0 || (baseY + y) < 0 || ((baseX + x) >= World.MAX_WIDTH) || ((baseY + y) >= World.MAX_HEIGHT))) {
						if ((World.mapValues[baseX + x][baseY + y] & 64) == 0)
							World.registerEntity(new Item(id, baseX + x, baseY + y, amount, (Player[])null));
					}
				}
			}
		} else if (cmd.equals("npcevent") && player.isAdmin()) {
			if (args.length < 1) {
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: npcevent [npc_id] [npc_amount] [item_id] [item_amount]");
				return;
			}
			int npcID, npcAmt = 0, random = 0;
			InvItem item = null;
			try {
				npcID = Integer.parseInt(args[0]);
				npcAmt = Integer.parseInt(args[1]);
				int id = Integer.parseInt(args[2]);
				int amount = args.length > 2 ? Integer.parseInt(args[3]) : 1;
				item = new InvItem(id, amount);
				random = DataConversions.random(0, npcAmt);
			} catch (Exception e) {
				player.sendMessage(Config.PREFIX + "Error parsing command.");
				return;
			}
			int x = 0;
			int y = 0;
			int baseX = player.getX();
			int baseY = player.getY();
			int nextX = 0;
			int nextY = 0;
			int dX = 0;
			int dY = 0;
			int minX = 0;
			int minY = 0;
			int maxX = 0;
			int maxY = 0;
			int scanned = -1;
			while (scanned < npcAmt) {
				scanned++;
				if (dX < 0) {
					x -= 1;
					if (x == minX) {
						dX = 0;
						dY = nextY;
						if (dY < 0)
							minY -= 1;
						else
							maxY += 1;
						nextX = 1;
					}
				} else if (dX > 0) {
					x += 1;
					if (x == maxX) {
						dX = 0;
						dY = nextY;
						if (dY < 0)
							minY -=1;
						else
							maxY += 1;
						nextX = -1;
					}
				} else {
					if (dY < 0) {
						y -= 1;
						if (y == minY) {
							dY = 0;
							dX = nextX;
							if (dX < 0)
								minX -= 1;
							else
								maxX += 1;
							nextY = 1;
						}
					} else if (dY > 0) {
						y += 1;
						if (y == maxY) {
							dY = 0;
							dX = nextX;
							if (dX < 0)
								minX -= 1;
							else
								maxX += 1;
							nextY = -1;
						}
					} else {
						minY -= 1;
						dY = -1;
						nextX = 1;
					}
				}
				if (!((baseX + x) < 0 || (baseY + y) < 0 || ((baseX + x) >= World.MAX_WIDTH) || ((baseY + y) >= World.MAX_HEIGHT))) {
					if ((World.mapValues[baseX + x][baseY + y] & 64) == 0) {
						final Npc n = new Npc(npcID, baseX + x, baseY + y, baseX + x - 20, baseX + x + 20, baseY + y - 20, baseY + y + 20);
						
						if (scanned == random) {
							DropItemAttr attr = new DropItemAttr(n, item);
							n.addAttr(attr);
						}
						
						n.setRespawn(false);
						World.registerEntity(n);
						World.getDelayedEventHandler().add(new SingleEvent(null, 120000 /* 2 minutes */) {
							public void action() {
								Mob opponent = n.getOpponent();
								if (opponent != null)
									opponent.resetCombat(CombatState.ERROR);
								n.resetCombat(CombatState.ERROR);
								n.remove();
							}
						});
					}
				}
			}
		} else if (cmd.equals("massnpc") && player.isAdmin()) {
			if (args.length != 1) {
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: NPC [id]");
				return;
			}
			int id = Integer.parseInt(args[0]);
			if (EntityHandler.getNpcDef(id) != null) {
				int x = 0;
				int y = 0;
				int baseX = player.getX();
				int baseY = player.getY();
				int nextX = 0;
				int nextY = 0;
				int dX = 0;
				int dY = 0;
				int minX = 0;
				int minY = 0;
				int maxX = 0;
				int maxY = 0;
				int scanned = 0;
				while (scanned < 400) {
					scanned++;
					if (dX < 0) {
						x -= 1;
						if (x == minX) {
							dX = 0;
							dY = nextY;
							if (dY < 0)
								minY -= 1;
							else
								maxY += 1;
							nextX = 1;
						}
					} else if (dX > 0) {
						x += 1;
						if (x == maxX) {
							dX = 0;
							dY = nextY;
							if (dY < 0)
								minY -=1;
							else
								maxY += 1;
							nextX = -1;
						}
					} else {
						if (dY < 0) {
							y -= 1;
							if (y == minY) {
								dY = 0;
								dX = nextX;
								if (dX < 0)
									minX -= 1;
								else
									maxX += 1;
								nextY = 1;
							}
						} else if (dY > 0) {
							y += 1;
							if (y == maxY) {
								dY = 0;
								dX = nextX;
								if (dX < 0)
									minX -= 1;
								else
									maxX += 1;
								nextY = -1;
							}
						} else {
							minY -= 1;
							dY = -1;
							nextX = 1;
						}
					}
					if (!((baseX + x) < 0 || (baseY + y) < 0 || ((baseX + x) >= World.MAX_WIDTH) || ((baseY + y) >= World.MAX_HEIGHT))) {
						if ((World.mapValues[baseX + x][baseY + y] & 64) == 0) {
							final Npc n = new Npc(id, baseX + x, baseY + y, baseX + x - 20, baseX + x + 20, baseY + y - 20, baseY + y + 20);
							n.setRespawn(false);
							World.registerEntity(n);
							World.getDelayedEventHandler().add(new SingleEvent(null, 60000) {
								public void action() {
									Mob opponent = n.getOpponent();
									if (opponent != null)
										opponent.resetCombat(CombatState.ERROR);
									n.resetCombat(CombatState.ERROR);
									n.remove();
								}
							});
						}
					}
				}
			}
		}  
		else if (cmd.equals("playertalk") && player.isAdmin()) {
			if (args.length < 2) {
				player.sendMessage(Config.PREFIX + "Invalid syntax. ::PLAYERTALK [player] [msg]");
				return;
			}
			String msg = "";
			for (int i = 1; i < args.length; i++) {
				msg += args[i] + " ";
			}
			Player pl = World.getPlayer(DataConversions.usernameToHash(args[0]));
			if (pl == null) {
				player.sendMessage(Config.PREFIX + "Invalid Player");
				return;
			}
			pl.addMessageToChatQueue(msg);
		} else if (cmd.equals("npctalk") && player.isAdmin()) {
			String newStr = "";
			for (int i = 1; i < args.length; i++)
				newStr = newStr += args[i] + " ";
					
			final Npc n = World.getNpc(Integer.parseInt(args[0]), player.getX() - 10, player.getX() + 10, player.getY() - 10, player.getY() + 10);
			
			if (n != null) {
				for (Player p : player.getViewArea().getPlayersInView())
					p.informOfNpcMessage(new ChatMessage(n, newStr, p));		
			} else
				player.sendMessage(Config.PREFIX + "Invalid NPC");
		} if (cmd.equals("stat") && player.isAdmin()) {
			if (args.length < 2 || args.length > 3)
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: STAT [stat] [level] [user]");
			else {
				byte stat = -1;
				if ((stat = (byte)statArray.indexOf(args[0])) != -1) {
					int level = Integer.parseInt(args[1]);
					if(level < 100 && level >= 1) {
						Player playerToEdit = player;
						if(args.length == 3) {
							playerToEdit = World.getPlayer(DataConversions.usernameToHash(args[2]));
						}
						if(playerToEdit != null) {
							if (level == 1) {
								playerToEdit.setExp(stat, 0);
								playerToEdit.setCurStat(stat, 1);
								playerToEdit.setMaxStat(stat, 1);
							} else {
								playerToEdit.setExp(stat, Formulae.experienceArray[level - 2]);
								playerToEdit.setCurStat(stat, level);
								playerToEdit.setMaxStat(stat, Formulae.experienceToLevel((int) playerToEdit.getExp(stat)));
							}
							playerToEdit.setCombatLevel(Formulae.getCombatlevel(playerToEdit.getMaxStats()));
							playerToEdit.sendStats();
							if (playerToEdit == player)
								player.sendMessage(Config.PREFIX + "You set your " + statArray.get(stat) + " to " + level);
							else {
								player.sendMessage(Config.PREFIX + "Successfully edited " + playerToEdit.getUsername() + "'s " + statArray.get(stat) + " to " + level);
								playerToEdit.sendMessage(Config.PREFIX + player.getUsername() + " has set your " + statArray.get(stat) + " to " + level);
							}
						} else
							player.sendMessage(Config.PREFIX + "Invalid name");
					} else
						player.sendMessage(Config.PREFIX + "Invalid level");
				} else
					player.sendMessage(Config.PREFIX + "Invalid stat");
			}
		} else if (cmd.equals("smitenpc") && (player.isAdmin() || player.isDev())) {
			if (args.length == 2) {
				try {
					int id = Integer.parseInt(args[0]);
					Npc n = World.getNpc(id, player.getX() - 10, player.getX() + 10, player.getY() - 10, player.getY() + 10);
					if (n != null) {
						try {
							int damage = Integer.parseInt(args[1]);
							n.setLastDamage(damage);
							n.setHits(n.getHits() - damage);
							for (Player p : n.getViewArea().getPlayersInView())
								p.informOfModifiedHits(n);
							GameObject sara = new GameObject(n.getLocation(), 1031, 0, 0);
							World.registerEntity(sara);
							World.delayedRemoveObject(sara, 600);
							if (n.getHits() < 1)
								n.killedBy(player);
						} catch(Exception ex) {}
					}
				} catch(Exception e) {}
			} else
				player.sendMessage(Config.PREFIX + "Invalid args: SMITENPC [ID] [DAMAGE]");
		} else if (cmd.equals("refreshchests") && player.isAdmin()) {
			try {
				EntityHandler.setChestDefinitions(World.getWorldLoader().loadChestDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("refreshstalls") && player.isAdmin()) {
			try {
				EntityHandler.setStallThievingDefinitions(World.getWorldLoader().loadStallThievingDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("refreshlockeddoors") && player.isAdmin()) {
			try {
				EntityHandler.setPicklockDoorDefinitions(World.getWorldLoader().loadPicklockDoorDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("refreshpickpocket") && player.isAdmin()) {
			try {
				EntityHandler.setPickPocketDefinitions(World.getWorldLoader().loadPickPocketDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("refreshwoodcut") && player.isAdmin()) {
			try {
				EntityHandler.setWoodcutDefinitions(World.getWorldLoader().loadWoodcuttingDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("refreshfishing") && player.isAdmin()) {
			try {
				EntityHandler.setFishingDefinitions(World.getWorldLoader().loadFishingDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(cmd.equals("refreshnpchandlers") && player.isAdmin()) {
			try {
				World.getWorldLoader().loadNpcHandlers();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("refreshagility") && player.isAdmin()) {
			try {
				EntityHandler.setAgilityCourseDefinitions(World.getWorldLoader().loadAgilityCourseDefinitions());
				EntityHandler.setAgilityDefinitions(World.getWorldLoader().loadAgilityDefinitons());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("refreshedibles") && player.isAdmin()) {
			try {
				EntityHandler.setItemHealingDefinitions(World.getWorldLoader().loadItemEdibleHeals());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("refreshnpcs") && player.isAdmin()) {
			for (Npc n : World.getNpcs())
				n.unconditionalRemove();
			try {
				World.getWorldLoader().loadNpcLocations();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("refreshcerters") && player.isAdmin()) {
			try {
				EntityHandler.setCerterDefinitions(World.getWorldLoader().loadCerterDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("refreshherbs") && player.isAdmin()) {
			try {
				EntityHandler.setHerbDefinitions(World.getWorldLoader().loadHerbDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("refreshunidentifiedherbs") && player.isAdmin()) {
			try {
				EntityHandler.setUnidentifiedHerbDefinitions(World.getWorldLoader().loadUnidentifiedHerbDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equals("event")) {
			if (args.length > 1) {
				if (player.isMod() || player.isEvent()) {
					if (!World.eventRunning) {
						try {
							int low = Integer.parseInt(args[0]);
							int high = Integer.parseInt(args[1]);
							if (low <= high && low >= 3 && high <= 123) {
								World.eventLow = low;
								World.eventHigh = high;
								World.setEvent(player.getX(), player.getY());
								synchronized (World.getPlayers()) {
									for (Player p : World.getPlayers()) {
										p.sendNotification(Config.PREFIX + "Type @gre@::EVENT@whi@ to join the event!");
										if (player.getLocation().inWilderness())
											p.sendNotification(Config.PREFIX + "@red@Warning:@whi@ This event is located in the wilderness!");									
										p.sendNotification(Config.PREFIX + "@yel@Level Restriction:@whi@ Level " + low + (low != high ? " to Level " + high : ""));
										p.sendNotification(Config.PREFIX + "An event has been set by " + player.getStaffName());
									}
								}
							} else
								player.sendMessage(Config.PREFIX + "Invalid level range");
						} catch(Exception e) {
							player.sendMessage(Config.PREFIX + "Invalid level range");
						}							
					} else
						player.sendMessage(Config.PREFIX + "There is already an event running!");
				} else
					player.sendMessage(Config.PREFIX + "Invalid args! Syntax EVENT");
			} else {
				if (World.eventPoint != null) {
					if (!player.getLocation().inWilderness() && !player.isTrading() && !player.isBusy() && !player.accessingShop() && !player.accessingBank() && !player.isDueling()) {
						if(player.getLocation().inBounds(792, 23, 794, 25))
						{
							player.sendMessage(Config.PREFIX + "You cannot use ::event whilst being jailed.");
							return;
						}
						if (!World.joinEvent(player))
							player.sendMessage(Config.PREFIX + "You aren't eligible for this event");
					} else
						player.sendMessage(Config.PREFIX + "You cannot enroll in this event right now");
				}
			}
		} else 
			if (cmd.equals("endevent") && (player.isMod() || player.isDev() || player.isEvent())) 
			{
			if (World.eventRunning) {
				World.setEvent(-1, -1);
				synchronized (World.getPlayers()) {
					for (Player p : World.getPlayers())
						p.sendNotification(Config.PREFIX + "Event registration has been closed by " + player.getStaffName());	
				}
			} else 
				player.sendMessage(Config.PREFIX + "No event is currently running");
			} else if (cmd.equals("islandsafe") && (player.isEvent() || player.isMod())) {
				World.islandSafe = !World.islandSafe;
				player.sendMessage(Config.PREFIX + "Safe mode " + (World.islandSafe ? "enabled" : "disabled"));
			} else if (cmd.equals("islandcombat") && (player.isEvent() || player.isMod())) {
				World.islandCombat = !World.islandCombat;
				player.sendMessage(Config.PREFIX + "Combat " + (World.islandCombat ? "disabled" : "enabled"));
			} else if(cmd.equals("ipban") && player.isAdmin()) {
			if (args.length != 1) {
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: IPBAN [ip]");
				return;
			}
				new Thread(
					new Runnable()
					{
						@Override
						public final void run()
						{
							try {
								Runtime.getRuntime().exec("IPTABLES -A INPUT -s " + args[0] + " -j DROP");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				).start();
				
				player.sendMessage(Config.PREFIX + args[0] + " was successfully IP banned");
		} else if (cmd.equals("unipban") && player.isAdmin()) {
			if (args.length != 1) {
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: UNIPBAN [ip]");
				return;
			}
				new Thread(
						new Runnable()
						{
							@Override
							public final void run()
							{
								try {
									Runtime.getRuntime().exec("IPTABLES -D INPUT -s " + args[0] + " -j ACCEPT");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					).start();	
				player.sendMessage(Config.PREFIX + args[0] + " has been removed from the IP ban list");
		} else if (cmd.equals("time") || cmd.equals("date")) {
			player.sendMessage(Config.PREFIX + Config.SERVER_NAME + "'s time/date is:@gre@ " + new java.util.Date().toString());
		} else if (cmd.equals("skiptutorial")) {
			if (player.getLocation().onTutorialIsland())
			{
				for (InvItem i : player.getInventory().getItems()) {
					if (player.getInventory().get(i).isWielded()) {
						player.getInventory().get(i).setWield(false);
						player.updateWornItems(i.getWieldableDef().getWieldPos(), player.getPlayerAppearance().getSprite(i.getWieldableDef().getWieldPos()));
					}	
				}
				player.getInventory().getItems().clear();
				player.sendInventory();			
				player.getInventory().add(new InvItem(70, 1));
				player.getInventory().add(new InvItem(1263, 1));
				player.getInventory().add(new InvItem(156, 1));
				player.getInventory().add(new InvItem(4, 1));
				player.getInventory().add(new InvItem(87, 1));
				player.getInventory().add(new InvItem(376, 1));
				player.sendInventory();
				player.teleport(122, 647, false);
			}
		} else if (cmd.equals("lottery")) {
	        if (World.lotteryRunning())
	                World.buyTicket(player);
	        else
	                player.sendMessage(Config.PREFIX + " There's no lottery running right now");
		} else if (cmd.equals("lotterypot")) {
	        World.getLotteryPot(player);     
		} 
		else if(cmd.equals("godspells") && player.isAdmin())
		{
			if(args.length != 1)
			{
				player.sendMessage("Invalid Syntax - Usage: godspells [boolean] eg. '::godspells true'");
				return;
			}
			try
			{
				Config.ALLOW_GODSPELLS = Boolean.parseBoolean(args[0]);
				for(Player p : World.getPlayers())
				{
					p.sendNotification(Config.PREFIX + "Godspells have been " + (Config.ALLOW_GODSPELLS ? "enabled" : "disabled"));
				}
			}
			catch(Exception e)
			{
				player.sendMessage("Invalid Syntax - Usage: godspells [boolean] eg. '::godspells true'");
				return;
			}
		}
		else if(cmd.equals("weakens") && player.isAdmin())
		{
			if(args.length != 1)
			{
				player.sendMessage("Invalid Syntax - Usage: weakens [boolean] eg. '::weakens true'");
				return;
			}
			try
			{
				Config.ALLOW_WEAKENS = Boolean.parseBoolean(args[0]);
				for(Player p : World.getPlayers())
				{
					p.sendNotification(Config.PREFIX + "Weaken spells have been " + (Config.ALLOW_WEAKENS ? "enabled" : "disabled"));
				}
			}
			catch(Exception e)
			{
				player.sendMessage("Invalid Syntax - Usage: weakens [boolean] eg. '::weakens true'");
				return;
			}
		} else
        /*
         * Change password
         */
        if (cmd.equals("changepassword")) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: CHANGEPASSWORD [new_password]");
				return;
			}
            
            // Check if a player already has a password change event.
            ArrayList events = World.getDelayedEventHandler().getEvents();
            Iterator<DelayedEvent> iterator = events.iterator();
            while (iterator.hasNext()) {
				DelayedEvent event = iterator.next();
                
                if(!(event instanceof ChangePasswordEvent)) continue;
                
                if(event.belongsTo(player)) {
                    player.sendMessage(Config.PREFIX + "You have already initiated a password change.");
                    player.sendMessage(Config.PREFIX + "Type ::confirmpassword [new_password] within 30 seconds to finish.");
                }
            }
			
			World.getDelayedEventHandler().add(new ChangePasswordEvent(player, args[0]));
            player.sendMessage(Config.PREFIX + "Password change initiated.");
            player.sendMessage(Config.PREFIX + "Type ::confirmpassword [new_password] within 30 seconds to finish.");
		}
		else
        /*
         * Change password
         */
		if (cmd.equals("confirmpassword")) 
		{
			if (args.length != 1) 
			{
				player.sendMessage(Config.PREFIX + "Invalid args. Syntax: CONFIRMPASSWORD [new_password]");
				return;
			}
            
            // Look for the existing password change event...
            ChangePasswordEvent originatingEvent = null;
            ArrayList events = World.getDelayedEventHandler().getEvents();
            Iterator<DelayedEvent> iterator = events.iterator();
            while (iterator.hasNext()) {
				DelayedEvent event = iterator.next();
                
                if(!(event instanceof ChangePasswordEvent)) continue;
                
                if(event.belongsTo(player)) {
                    originatingEvent = (ChangePasswordEvent)event;
                    break;
                }
            }
            
            if(originatingEvent == null){
                player.sendMessage(Config.PREFIX + "You have not initiated a password change.");
                player.sendMessage(Config.PREFIX + "Type ::changepassword [new_password] to change your password.");
                return;
            }
            
            originatingEvent.confirmPassword(args[0]);
		}
		else
        if (cmd.equals("startlottery") && (player.isMod() || player.isDev() || player.isEvent())) 
        {
            if (!World.lotteryRunning())
                if (args.length != 1)
                    player.sendMessage(Config.PREFIX + " Invalid args. Syntax: STARTLOTTERY [price]");
                else
                    try {
                        World.startLottery(Integer.parseInt(args[0]));
                    } catch (Exception e) {}       
        } 
        else 
        if (cmd.equals("stoplottery") && (player.isMod() || player.isDev() || player.isEvent())) 
        {
            if (World.lotteryRunning())
                World.stopLottery();
            else
                player.sendMessage(Config.PREFIX + " There's no lottery running right now");
        }
    }
	public static final ArrayList<String> statArray = new ArrayList<String>(){{
		add("attack"); add("defense"); add("strength"); add("hits"); add("ranged"); add("prayer"); add("magic"); add("cooking"); add("woodcut"); add("fletching"); add("fishing"); add("firemaking"); add("crafting"); add("smithing"); add("mining"); add("herblaw"); add("agility"); add("thieving"); add("runecrafting");
	}};	
}