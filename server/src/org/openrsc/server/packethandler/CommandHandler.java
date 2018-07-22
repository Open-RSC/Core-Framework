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
import org.openrsc.server.entityhandling.defs.GameObjectDef;
import org.openrsc.server.entityhandling.defs.ItemDef;
import org.openrsc.server.event.ChangePasswordEvent;
import org.openrsc.server.event.DelayedEvent;
import org.openrsc.server.event.ShutdownEvent;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ErrorLog;
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
    private static String badSyntaxPrefix = Config.PREFIX + "Invalid Syntax - Usage: ::";
    
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
	
	public void handleCommand(String cmd, final String[] args, final Player owner) 
	{
		for (int index = 0; index < args.length - 1; index++) 
		{
			args[index] = args[index].replace("-", " ");
			args[index] = args[index].replace("_", " ");
		}
		
		owner.setLastCommand(System.currentTimeMillis());
		if ((cmd.equalsIgnoreCase("coords")) && (owner.isMod() || owner.isDev())) 
		{
            Player p = args.length > 0 ? 
                        World.getPlayer(DataConversions.usernameToHash(args[0])) :
                        owner;
            
            if(p != null)
                owner.sendMessage(Config.PREFIX + "is at X: " + owner.getLocation().getX() + ", Y: " + owner.getLocation().getY());
            else
                owner.sendMessage(Config.PREFIX + "Invalid name");
		}
        else // Show online players
		if (cmd.equalsIgnoreCase("online") && owner.isMod()) 
		{
			StringBuilder sb = new StringBuilder();
			synchronized (World.getPlayers()) 
			{
				EntityList<Player> players = World.getPlayers();
				sb.append("@gre@There are currently ").append(players.size()).append(" player(s) online.\n\n");
				for (Player p : players) 
				{
					Point loc = p.getLocation();
					if (owner.isSub())
						sb.append("@whi@").append(p.getUsername()).append(loc.inWilderness() ? " @red@".concat("Wilderness").concat("\n") : "\n");
					else
					if (owner.isMod())
						sb.append("@whi@").append(p.getUsername()).append(" @yel@(").append(loc).append(")").append(loc.inWilderness() ? " @red@".concat(loc.getDescription().concat("\n")) : "\n");	
				}
			}
			owner.getActionSender().sendScrollableAlert(sb.toString());
		}
        else // toggle invisibility
		if (cmd.equalsIgnoreCase("invisible") && (owner.isMod() || owner.isDev())) 
		{
			owner.invisible = !owner.invisible;
			owner.sendMessage(Config.PREFIX + "You are now " + (owner.invisible ? "invisible" : "visible"));
			if (owner.invisible)
			for (Player x : owner.getViewArea().getPlayersInView())
			x.removeWatchedPlayer(owner);
		} 
        else // leave CTF event
		if (cmd.equalsIgnoreCase("leavectf") && owner.getLocation().inCtf())
		{
			owner.removeFromCtf(owner);
			owner.sendAlert("You have been removed from CTF");
		}
        else // use global chat
		if (cmd.equalsIgnoreCase("say") || cmd.equalsIgnoreCase("s")) 
		{
			if (owner.getPrivacySetting(4)) 
			{
				if (!World.global && !owner.isMod())
					owner.sendMessage(Config.PREFIX + "Global Chat is currently disabled");
				else 
				if (World.muted && !owner.isMod())
					owner.sendMessage(Config.PREFIX + "The world is muted");				
				else 
				if (owner.getMuted() > 0)
					owner.sendMessage(Config.PREFIX + "You are muted");		
				else
				if (System.currentTimeMillis() - owner.getLastGlobal() < 10000 && !owner.isMod())
					owner.sendMessage(Config.PREFIX + "There's a 10 second delay using Global Chat");
				else 
				{	
					owner.setLastGlobal(System.currentTimeMillis());
					String message = "";
					for (int i = 0; i < args.length; i++) 
					{
						message = message += args[i] + " ";
					}
						
					Logger.log(new GlobalLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), message, DataConversions.getTimeStamp()));
					synchronized (World.getPlayers()) 
					{
						for (Player p : World.getPlayers()) 
						{
							if (owner.isAdmin())
								p.sendNotification("#adm#@yel@" + owner.getUsername() + ":@whi@ " + message);
							else
							if (owner.isMod())
								p.sendNotification("#mod#@whi@" + owner.getUsername() + ":@whi@ " + message);
							else
							if (owner.isDev())
								p.sendNotification("#dev#@red@" + owner.getUsername() + ":@whi@ " + message);
							else
							if (owner.isEvent())
								p.sendNotification("#eve#@eve@" + owner.getUsername() + ":@whi@ " + message);
							else 
							if (!p.getIgnoreList().contains(owner.getUsernameHash()) && p.getPrivacySetting(4) == true || owner.isMod())
								p.sendGlobalMessage(owner.getUsernameHash(), owner.getGroupID() == 4 ? (owner.isSub() ? 5 : 4) : owner.getGroupID(), message);
						}						
					}
				}
			} 
			else
				owner.sendMessage(Config.PREFIX + "You cannot use Global Chat as you have it disabled");
		} 
        else // Send an alert to a owner
        if (cmd.equalsIgnoreCase("alert") && owner.isMod()) 
        {
            String message = "";
            if (args.length > 0) 
            {
                Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
                if (p != null)
                {
                    for (int i = 1; i < args.length; i++)
                    message += args[i] + " ";
                    p.sendAlert((owner.getStaffName()) + ":@whi@ " + message);
                    owner.sendMessage(Config.PREFIX + "Alerted " + p.getUsername());
                    Logger.log(new GenericLog(owner.getUsername() + " alerted " + p.getUsername() +": " + message, DataConversions.getTimeStamp()));
                }
                else
                    owner.sendMessage(Config.PREFIX + "Invalid name");
            } 
			else
                owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name] [message]");	
		} 
		else
        if (cmd.equalsIgnoreCase("iplimit") && owner.isAdmin())
        {
            if(args.length != 1)
            {
                owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [amount]");
                return;
            }
            try
            {
                Config.MAX_LOGINS_PER_IP = Integer.parseInt(args[0]);
                for(Player p : World.getPlayers())
                {
                    p.sendNotification(Config.PREFIX + "Max logins per IP has been set to: " + Config.MAX_LOGINS_PER_IP );
                }
                Logger.log(new GenericLog(owner.getUsername() + " has set MAX_LOGINS_PER_IP to " + Config.MAX_LOGINS_PER_IP, DataConversions.getTimeStamp()));
            }
            catch(NumberFormatException e)
            {
                owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [amount]");
                return;
            }
        }
        else // Give a owner a skull
		if (cmd.equalsIgnoreCase("skull") && (owner.isAdmin() || owner.isMod())) 
		{
            if(args.length == 0)
            {
                owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
                return;
            }
            
            Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
            if (p != null)
            {
                p.addSkull(1200000);
                p.sendMessage(Config.PREFIX + "You have been healed by an admin");
                owner.sendMessage(Config.PREFIX + "Skulled: " + p.getUsername());
                Logger.log(new GenericLog(owner.getUsername() + " skulled " + p.getUsername(), DataConversions.getTimeStamp()));
            }
            else
            {
                owner.sendMessage(Config.PREFIX + "Invalid name");	
            }
		} 
		else // Heal a owner
		if (cmd.equalsIgnoreCase("heal") && owner.isAdmin()) 
		{
            Player p = args.length > 0 ? 
                        World.getPlayer(DataConversions.usernameToHash(args[0])) :
                        owner;
            
            if(p != null)
            {
                p.setCurStat(3, p.getMaxStat(3));
                p.sendStat(3);
                p.sendMessage(Config.PREFIX + "You have been healed by an admin");
                owner.sendMessage(Config.PREFIX + "Healed: " + p.getUsername());
                Logger.log(new GenericLog(owner.getUsername() + " healed " + p.getUsername(), DataConversions.getTimeStamp()));
            }
            else
                owner.sendMessage(Config.PREFIX + "Invalid name");
		} 
		else // Toggle global chat
		if(cmd.equalsIgnoreCase("global") && owner.isMod()) 
		{
			World.global = !World.global;
			synchronized (World.getPlayers()) 
			{
				for (Player p : World.getPlayers()) 
				{
					p.sendNotification(Config.PREFIX + "Global Chat has been " + (World.global ? "enabled" : "disabled") + " by " + owner.getStaffName());
				}
			}
		} 
		else // Toggle if dueling is allowed
		if(cmd.equalsIgnoreCase("dueling") && owner.isMod())
		{
			World.dueling = !World.dueling;
			synchronized (World.getPlayers()) 
			{
				for (Player p : World.getPlayers()) 
				{
					p.sendNotification(Config.PREFIX + "Dueling has been " + (World.dueling ? "enabled" : "disabled") + " by " + owner.getStaffName());
				}
			}	
		} 
		else // Mute world
		if (cmd.equalsIgnoreCase("muted") && owner.isAdmin()) 
		{
			World.muted = !World.muted;
			synchronized (World.getPlayers()) 
			{
				for (Player p : World.getPlayers()) 
				{
					p.sendNotification(Config.PREFIX + "World Mute has been " + (World.muted ? "enabled" : "disabled") + " by " + owner.getStaffName());
				}
			}			
		} 
        else // Fatigue owner
        if (cmd.equalsIgnoreCase("fatigue") && owner.isMod()) 
        {
            if(args.length == 0)
            {
                owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
                return;
            }
            
            Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
            if (p != null) 
            {
                try
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
                }
                catch(NumberFormatException e)
                {
                    owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [player] [amount]");
                    return;
                }
                owner.sendMessage(Config.PREFIX + p.getUsername() + "'s fatigue has been set to " + ((p.getFatigue() / 25) * 100 / 750) + "%");
                Logger.log(new GenericLog(owner.getUsername() + " set " + p.getUsername() + "'s fatigue to " + ((p.getFatigue() / 25) * 100 / 750) + "%", DataConversions.getTimeStamp()));
            } 
            else
            {
                owner.sendMessage(Config.PREFIX + "Invalid name");	
            }
        }
        else // Show a owner's IP address
		if (cmd.equalsIgnoreCase("ip") && owner.isAdmin()) 
		{
            Player p = args.length > 0 ? 
                        World.getPlayer(DataConversions.usernameToHash(args[0])) :
                        owner;
            
            if(p != null)
            {
				long requestee = owner.getUsernameHash();
				//p.requestLocalhost(requestee);
                owner.sendMessage(Config.PREFIX + p.getUsername() + " IP address: " + p.getIP());
				Logger.log(new GenericLog(owner.getUsername() + " requested " + p.getUsername() + "'s IP", DataConversions.getTimeStamp()));
            }
            else
                owner.sendMessage(Config.PREFIX + "Invalid name");
		} 
		else // Show info about a owner
		if (cmd.equalsIgnoreCase("info") && owner.isMod()) 
		{
            Player p = args.length > 0 ? 
                        World.getPlayer(DataConversions.usernameToHash(args[0])) :
                        owner;
            
            if(p != null)
            {
				owner.sendAlert(p.getUsername() + " (" + p.getStatus() + ") at " + owner.getLocation().toString() + " (" + owner.getLocation().getDescription() + ") % % Logged in: " + (DataConversions.getTimeStamp() - owner.getLastLogin()) + " seconds % % Last moved: " + (int)((System.currentTimeMillis() - owner.getLastMoved()) / 1000) + " % % Fatigue: " + ((p.getFatigue() / 25) * 100 / 750) + " % %Busy: " + (p.isBusy() ? "true" : "false"), true);
            }
            else
                owner.sendMessage(Config.PREFIX + "Invalid name");
		} 
		else // Kick a owner
		if (cmd.equalsIgnoreCase("kick") && owner.isMod()) 
		{
			if (args.length != 1) 
			{
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}	
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null) 
			{
				if (p.isDueling() && !owner.isAdmin())
					owner.sendMessage(Config.PREFIX + "You cannot kick players who are dueling");	
				else 
				{				
					World.unregisterEntity(p);
					owner.sendMessage(Config.PREFIX + p.getUsername() + " has been kicked");
					Logger.log(new GenericLog(owner.getUsername() + " kicked " + p.getUsername(), DataConversions.getTimeStamp()));
				}
			}
		}
		else // Ban a owner
		if (cmd.equalsIgnoreCase("ban") && (owner.isMod() || owner.isDev())) 
		{
			if (args.length != 1) 
			{
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
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
				Logger.log(new GenericLog(owner.getUsername() + " banned " + DataConversions.hashToUsername(DataConversions.usernameToHash(args[0])), DataConversions.getTimeStamp()));
				owner.sendMessage(Config.PREFIX + DataConversions.hashToUsername(DataConversions.usernameToHash(args[0])) + " has been banned");
			} 
		}
		else // Unban a owner
		if (cmd.equalsIgnoreCase("unban") && (owner.isMod() || owner.isDev())) 
		{
			if (args.length != 1) 
			{
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}
			
			ServerBootstrap.getDatabaseService().submit(new Player.BanTransaction(DataConversions.usernameToHash(args[0]), false));			
			Logger.log(new GenericLog(owner.getUsername() + " unbanned " + DataConversions.hashToUsername(DataConversions.usernameToHash(args[0])), DataConversions.getTimeStamp()));
			owner.sendMessage(Config.PREFIX + DataConversions.hashToUsername(DataConversions.usernameToHash(args[0])) + " has been unbanned");				
		}
		else // Mute a owner
		if (cmd.equalsIgnoreCase("mute") && (owner.isMod() || owner.isDev())) 
		{
			if (args.length != 1) 
			{
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null) 
			{
				p.mute(0);										
				Logger.log(new GenericLog(owner.getUsername() + " muted " + p.getUsername(), DataConversions.getTimeStamp()));
				ServerBootstrap.getDatabaseService().submit(new Player.MuteTransaction(DataConversions.usernameToHash(args[0]), true));
				owner.sendMessage(Config.PREFIX + p.getUsername() + " has been muted");	
			}
		} 
		else // Unmute a owner
		if (cmd.equalsIgnoreCase("unmute") && (owner.isMod() || owner.isDev())) 
		{
			if (args.length != 1) 
			{
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null) 
			{
				p.unmute();										
				Logger.log(new GenericLog(owner.getUsername() + " unmuted " + p.getUsername(), DataConversions.getTimeStamp()));
				ServerBootstrap.getDatabaseService().submit(new Player.MuteTransaction(DataConversions.usernameToHash(args[0]), false));
				owner.sendMessage(Config.PREFIX + p.getUsername() + " has been unmuted");	
			}		
		}
		else // spawn/remove an NPC
		if (cmd.equalsIgnoreCase("npc") && (owner.isAdmin() || owner.isDev())) 
		{
			if (args.length == 0) 
			{
				for (Npc n : World.getZone(owner.getX(), owner.getY()).getNpcsAt(owner.getX(), owner.getY())) 
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
				catch(NumberFormatException ex)
                {
                    owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [duration]");
                    return;
                }
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
                    catch(NumberFormatException ex)
                    {
                        owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [duration]");
                        return;
                    }
                }
				if (EntityHandler.getNpcDef(id) != null) 
				{
					final Npc n = new Npc(id, owner.getX(), owner.getY(), owner.getX() - 2, owner.getX() + 2, owner.getY() - 2, owner.getY() + 2);
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
					owner.sendMessage(Config.PREFIX + "Invalid ID");
				}
			}
		} 
		else // Teleport
		if ((cmd.equalsIgnoreCase("teleport") || cmd.equalsIgnoreCase("tp")) && (owner.isMod() || owner.isDev() || owner.isEvent())) 
		{
			owner.resetLevers();
			if (args.length == 0) 
			{
				owner.teleport = !owner.teleport;
				owner.sendMessage(Config.PREFIX + "Single click teleport " + (owner.teleport ? "enabled" : "disabled"));
			} 
			else 
			if (args.length == 1) 
			{
				if(!EntityHandler.getTeleportManager().containsTeleport(args[0]))
				{
					owner.sendMessage(Config.PREFIX + "Teleport location \"" + args[0] + "\" does not exist");
					owner.sendMessage(Config.PREFIX + "hint: you can add it via the website");
				}
				else
				{
					owner.teleport(EntityHandler.getTeleportManager().getTeleport(args[0]), false);
				}
			} 
			else if (args.length == 2) 
			{
				if (World.withinWorld(Integer.parseInt(args[0]), Integer.parseInt(args[1])))
				{
					owner.teleport(Integer.parseInt(args[0]), Integer.parseInt(args[1]), false);
				}
            }
			else if (args.length == 3) 
			{
				if (World.withinWorld(Integer.parseInt(args[0]), Integer.parseInt(args[1])))
				{
                    try
                    {
                        owner.teleport(Integer.parseInt(args[0]), Integer.parseInt(args[1]), false);
                    }
                    catch(NumberFormatException e)
                    {
                        owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y]");
                        return;
                    }
				}
			}	
		} 
        else // Show appearance change screen
        if((cmd.equalsIgnoreCase("appearance")) && (owner.isAdmin()))
        {
            Player p = args.length > 0 ? 
                        World.getPlayer(DataConversions.usernameToHash(args[0])) :
                        owner;
            
            if(p != null)
            {
                String confirmMessage = owner.getUsername() + " has been sent the change appearance screen";
                p.setChangingAppearance(true);
                p.getActionSender().sendAppearanceScreen();
                owner.sendMessage(Config.PREFIX + confirmMessage);
                Logger.log(new GenericLog(confirmMessage + " by " + owner.getUsername(), DataConversions.getTimeStamp()));	
            }
            else
                owner.sendMessage(Config.PREFIX + "Invalid name");
		}
        else // Summon a owner
		if (cmd.equalsIgnoreCase("summon") && (owner.isMod() || owner.isDev())) 
		{
			if (args.length != 1) 
			{
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null) 
			{
                if(p.getGroupID() >= 4)
                {
                    if (p.getLocation().inCtf())
                        owner.sendMessage(Config.PREFIX + "You cannot summon players who are in CTF");
                    else
                    if (p.isDueling() && !owner.isAdmin())
                        owner.sendMessage(Config.PREFIX + "You cannot summon players who are dueling");
                    else 
                    if (owner.getLocation().inWilderness() && !owner.isAdmin())
                        owner.sendMessage(Config.PREFIX + "You cannot summon players into the wilderness");
                    else 
                    {
                        String summonMessage = owner.getUsername() + " summoned " + p.getUsername() + " to " + "(" + p.getX() + ", " + p.getY() + ")";
                        p.setReturnPoint();
                        p.teleport(owner.getX(), owner.getY(), false);
                        owner.sendMessage(Config.PREFIX + summonMessage);
                        Logger.log(new GenericLog(summonMessage, DataConversions.getTimeStamp()));					
                    }
                }
                else
                {
                    owner.sendMessage(Config.PREFIX + "Staff members can not be summoned");
                }
			} 
			else
			{
				owner.sendMessage(Config.PREFIX + "Invalid name");
			}
		} 
		else // Return a owner to where they were before summoning
		if (cmd.equalsIgnoreCase("return") && (owner.isMod() || owner.isDev())) 
		{
			if (args.length != 1) 
			{
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}
			
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
			if (p != null) 
			{
                if(p.getGroupID() >= 4)
                {
                    if (p.wasSummoned()) 
                    {
                        String returnMessage = owner.getUsername() + " returned " + p.getUsername() + " to " + " (" + p.getX() + ", " + p.getY() + ")";
                        p.setSummoned(false);
                        p.teleport(p.getReturnX(), p.getReturnY(), false);
                        owner.sendMessage(Config.PREFIX + returnMessage);
                        Logger.log(new GenericLog(returnMessage, DataConversions.getTimeStamp()));
                    } 
                    else
                    {
                        owner.sendMessage(Config.PREFIX + p.getUsername() + " has no return point set");
                    }
                }
                else
                {
                    owner.sendMessage(Config.PREFIX + "Staff members can not be summoned");
                }
			} 
			else
			{
				owner.sendMessage(Config.PREFIX + "Invalid name");
			}
		} 
		else // Jail a owner
		if (cmd.equalsIgnoreCase("jail") && (owner.isMod() || owner.isDev() || owner.isEvent())) 
		{
			if (args.length != 1) 
			{
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
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
                        owner.sendMessage(Config.PREFIX + p.getUsername() + " has been jailed");
                        p.sendAlert("You have been jailed.");
                    }
                    else
                    {
                        owner.sendMessage(Config.PREFIX + p.getUsername() + " is already in jail");
                        return;
                    }
                } 
                else
                {
                    owner.sendMessage(Config.PREFIX + "Staff members can not be jailed");
                    return;
                }
            }
			else
			{
				owner.sendMessage(Config.PREFIX + "Invalid name");
                return;
			}
		} 
		else // Release a owner from jail
		if (cmd.equalsIgnoreCase("release") && (owner.isMod() || owner.isDev() || owner.isEvent())) 
		{
			if (args.length != 1) 
			{
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
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
                        owner.sendMessage(Config.PREFIX + p.getUsername() + " has been released from jail.");
                    }
                    else
                    {
                        owner.sendMessage(Config.PREFIX + p.getUsername() + " is not in jail");
                    }
                } 
                else
                {
                    owner.sendMessage(Config.PREFIX + "Staff members can not be released");
                    return;
                }
            }
			else
			{
				owner.sendMessage(Config.PREFIX + "Invalid name");
                return;
			}
		} 
		else // Go to a owner's location
		if ((cmd.equalsIgnoreCase("goto") || cmd.equalsIgnoreCase("tpto") || cmd.equalsIgnoreCase("teleportto")) && (owner.isMod() || owner.isDev())) 
		{
			if (args.length != 1) 
			{
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}
            
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			
            if(p != null)
            {
                owner.teleport(p.getX(), p.getY(), false);
                Logger.log(new GenericLog(owner.getUsername() + " went to " + p.getUsername() + " (" + p.getX() + ", " + p.getY() + ")", DataConversions.getTimeStamp()));
            }
 			else
			{
				owner.sendMessage(Config.PREFIX + "Invalid name");
			}
		} 
		else // Restart server
		if ((cmd.equalsIgnoreCase("restart") || cmd.equalsIgnoreCase("update")) && (owner.isAdmin() || owner.isDev()))
		{
			String message = "";
			if (args.length > 0) {
				for (String s : args)
					message += (s + " ");
				message = message.substring(0, message.length() - 1);
			}
			World.getWorld().getEventPump().submit(new ShutdownEvent(true, message));
		}
		else // spawn an item
		if (cmd.equalsIgnoreCase("item") && owner.isAdmin())
		{
			if (args.length < 1 || args.length > 2)
			{
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount]");
			}
			else 
			{
                try
                {
                    int id = Integer.parseInt(args[0]);
                    ItemDef itemDef = EntityHandler.getItemDef(id);
                    if (EntityHandler.getItemDef(id) != null) 
                    {
                        long amount = 1;
                        if (args.length == 2)
                            amount = Long.parseLong(args[1]);

                        if(itemDef.isStackable())
                        {
                            InvItem invItem = new InvItem(id, amount);
                            owner.getInventory().add(invItem);
                        }
                        else
                        {
                            for(int i = 0; i < amount; i++)
                            {
                                InvItem invItem = new InvItem(id, amount);
                                owner.getInventory().add(invItem);
                            }
                        }
                        owner.sendInventory();
                        Logger.log(new GenericLog(owner.getUsername() + " spawned " + amount + " " + EntityHandler.getItemDef(id).name, DataConversions.getTimeStamp()));
                    } 
                    else
                    {
                        owner.sendMessage(Config.PREFIX + "Invalid ID");
                    }
                }
                catch (NumberFormatException e)
                {
                    owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount]");
                    return;
                }
			}
		}
        else // Spawn or remove an object
		if (cmd.equalsIgnoreCase("object") && (owner.isAdmin() || owner.isDev()))
		{
            if(args.length == 0)
            {
                owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " create [object_id] [direction]" + (owner.isAdmin() ? " [from_database] eg. '::object create 1 0 true'" : "") + " OR");
                owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " delete" + (owner.isAdmin() ? " [from_database] eg. '::object delete true'" : ""));
                return;
            }
            
            if(args[0].equalsIgnoreCase("create"))
            {
                if(args.length <= 1)
                {
                    owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " create [object_id] [direction]" + (owner.isAdmin() ? " [from_database] eg. '::object create 1 true'" : "") + " OR");
                    return;
                }
                try
                {
                    int object_id           = Integer.parseInt(args[1]);
                    GameObjectDef objectDef = EntityHandler.getGameObjectDef(object_id);
                    int direction           = args.length >= 3 ? Integer.parseInt(args[2]) : 0;
                    
                    if(objectDef == null)
                    {
						owner.sendMessage(Config.PREFIX + "Invalid ID");
                        return;
                    }
                    
                    World.registerEntity(new GameObject(owner.getLocation(), object_id, direction, 0));
                    owner.sendMessage(Config.PREFIX + "Created " + objectDef.getName());
                    
                    if(owner.isAdmin() && args.length >= 4)
                    {
                        boolean sql = Boolean.parseBoolean(args[3]);
                        if(sql)
                        {
							try {
								World.getWorldLoader().writeQuery("INSERT INTO `spawn_object` (`object`, `x`, `y`, `direction`) VALUES ('" + object_id + "', '" + owner.getX() + "', '" + owner.getY() + "', '" + direction + "')");
                                owner.sendMessage(Config.PREFIX + "Object added to database");
                                Logger.log(new GenericLog(owner.getUsername() + " added object to database. id: " + object_id + ", direction: " + direction + ", location: " + owner.getLocation(), DataConversions.getTimeStamp()));
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                        }
                    }
                }
                catch(NumberFormatException e)
                {
                    owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " create [object_id] [direction]" + (owner.isAdmin() ? " [from_database] eg. '::object create 1 true'" : "") + " OR");
                    return;
                }
            }
            else
            if(args[0].equalsIgnoreCase("delete"))
            {
               GameObject o = World.getZone(owner.getX(), owner.getY()).getObjectAt(owner.getX(), owner.getY());
               if(o == null)
               {
                   owner.sendMessage(Config.PREFIX + "There is no object at your current location.");
                   return;
               }
               else
               {
                   World.unregisterEntity(o);
                   owner.sendMessage(Config.PREFIX + "Removed " + o.getGameObjectDef().getName());
               }
               
                if(owner.isAdmin() && args.length >= 2)
                {
                    boolean sql = Boolean.parseBoolean(args[1]);

                    if(sql)
                    {
                        try {
                            World.getWorldLoader().writeQuery("DELETE FROM `spawn_object` WHERE `x` = '" + owner.getX() + "' AND `y` = '" + owner.getY() + "'");
                            owner.sendMessage(Config.PREFIX + "Object removed from database");
                            Logger.log(new GenericLog(owner.getUsername() + " remvoed object from database. id: " + o.getID() + ", location: " + owner.getLocation(), DataConversions.getTimeStamp()));
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
            else
            {
                owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " create [object_id] [direction]" + (owner.isAdmin() ? " [from_database] eg. '::object create 1 0 true'" : "") + " OR");
                owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " delete" + (owner.isAdmin() ? " [from_database] eg. '::object delete true'" : ""));
                return;
            }
		} 
        else // Wipe owner's inventory.
		if (cmd.equalsIgnoreCase("wipeinventory") && owner.isAdmin()) 
		{
            if(args.length == 0)
            {
                owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
                return;
            }
            
            Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));            
            if(p != null)
            {
                for (InvItem i : p.getInventory().getItems()) {
                    if (p.getInventory().get(i).isWielded()) {
                        p.getInventory().get(i).setWield(false);
                        p.updateWornItems(i.getWieldableDef().getWieldPos(), p.getPlayerAppearance().getSprite(i.getWieldableDef().getWieldPos()));
                    }	
                }
                p.getInventory().getItems().clear();
                p.sendInventory();
                p.sendMessage(Config.PREFIX + "Your inventory has been wiped by an admin");
                owner.sendMessage(Config.PREFIX + "Wiped inventory of " + p.getUsername());
                Logger.log(new GenericLog(owner.getUsername() + " wiped the inventory of " + p.getUsername(), DataConversions.getTimeStamp()));
            }
            else
                owner.sendMessage(Config.PREFIX + "Invalid name");
		}
        else // Wipe owner's bank
        if (cmd.equalsIgnoreCase("wipebank") && owner.isAdmin())
        {
            if(args.length == 0)
            {
                owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
                return;
            }
            
            Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));            
            if(p != null)
            {
                p.getBank().getItems().clear();
                p.sendMessage(Config.PREFIX + "Your bank has been wiped by an admin");
                owner.sendMessage(Config.PREFIX + "Wiped bank of " + p.getUsername());
                Logger.log(new GenericLog(owner.getUsername() + " wiped the bank of " + p.getUsername(), DataConversions.getTimeStamp()));
            }
            else
                owner.sendMessage(Config.PREFIX + "Invalid name");
        }
        else // Kill a owner
        if (cmd.equalsIgnoreCase("kill") && owner.isAdmin())
        {
			if (args.length == 1)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
                return;
            }
            
            Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
            if (p == null)
            {
                owner.sendMessage(Config.PREFIX + "Invalid name");
                return;
            }
            
            p.setLastDamage(99);
            p.setHits(p.getHits() - 99);
            ArrayList<Player> playersToInform = new ArrayList<Player>();
            playersToInform.addAll(owner.getViewArea().getPlayersInView());
            playersToInform.addAll(p.getViewArea().getPlayersInView());
            for (Player i : playersToInform)
                i.informOfModifiedHits(p);
            p.sendStat(3);
            if (p.getHits() <= 0)
            {
                p.killedBy(owner, false);
                p.sendMessage(Config.PREFIX + "You have been killed by an admin");
                owner.sendMessage(Config.PREFIX + "Killed " + p.getUsername());
                Logger.log(new GenericLog(owner.getUsername() + " killed [command] " + p.getUsername(), DataConversions.getTimeStamp()));
            }
            else
            {
                owner.sendMessage(Config.PREFIX + "Could not kill " + p.getUsername());
                Logger.log(new ErrorLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), owner.getUsername() + " unable to kill [command] " + p.getUsername(), DataConversions.getTimeStamp()));
            }
        }
        else // Damage a owner.
        if (cmd.equalsIgnoreCase("damage") && owner.isAdmin())
        {
			if (args.length < 2)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name] [amount]");
                return;
            }
            try
            {
                int damage = Integer.parseInt(args[1]);
                Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
                if (p != null) {
                    p.setLastDamage(damage);
                    p.setHits(p.getHits() - damage);
                    ArrayList<Player> playersToInform = new ArrayList<Player>();
                    playersToInform.addAll(owner.getViewArea().getPlayersInView());
                    playersToInform.addAll(p.getViewArea().getPlayersInView());
                    for(Player i : playersToInform)
                        i.informOfModifiedHits(p);
                    p.sendStat(3);
                    if (p.getHits() <= 0)
                        p.killedBy(owner, false);
                    
                    p.sendMessage(Config.PREFIX + "You have been taken " + damage + " damage from an admin");
                    owner.sendMessage(Config.PREFIX + "Damaged " + p.getUsername() + " " + damage + " hits");
                    Logger.log(new GenericLog(owner.getUsername() + " damaged [" + damage + "] " + p.getUsername(), DataConversions.getTimeStamp()));
                }
            }
            catch (NumberFormatException e)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name] [amount]");
                return;
            }
		}
        else // set each of the specified players stats
        if (cmd.equalsIgnoreCase("stats") && owner.isAdmin())
        {
            Player p = args.length > 1 ? 
                        World.getPlayer(DataConversions.usernameToHash(args[0])) :
                        owner;
            
            if(p != null)
            {
                try
                {
                    int levelArg    = args.length > 1 ? 1 : 0;
                    int level       = Integer.parseInt(args[levelArg]);
                    
                    if (level > 99) 
                        level = 99;
                    if (level < 1) 
                        level = 1;
                    
                    for (Skill skill : Skill.values()) {
                        p.setCurStat(skill.ordinal(), level);
                        p.setMaxStat(skill.ordinal(), level);
                        p.setExp(skill.ordinal(), Formulae.lvlToXp(level));
                    }
                    p.setCombatLevel(Formulae.getCombatlevel(p.getMaxStats()));
                    p.sendStats();
                    p.sendMessage(Config.PREFIX + "All of your stats have been set to level " + level + " by an admin");
                    owner.sendMessage(Config.PREFIX + "All of " + p.getUsername() + "'s stats have been set to level " + level);
                }
                catch(NumberFormatException e)
                {
                    owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name] [level]");
                    return;
                }
            }
            else
            {
                owner.sendMessage(Config.PREFIX + "Invalid name");
                return;
            }
		}
        else // summon all players in the world
        if (cmd.equalsIgnoreCase("summonall") && owner.isAdmin())
        {
			if (args.length == 0) {
				synchronized (World.getPlayers()) {
					for (Player p : World.getPlayers()) {
						p.setReturnPoint();
						p.resetLevers();
						p.teleport(owner.getX(), owner.getY(), true);
					}
				}
                owner.sendMessage(Config.PREFIX + "All players summoned");
			} else if (args.length == 2) {
				int width = -1;
				int height = -1;
				try {
					width = Integer.parseInt(args[0]);
					height = Integer.parseInt(args[1]);
				}
                catch(NumberFormatException e)
                {
                    owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] - supplied dimensions were invalid");
                    return;
				}
                Random rand = DataConversions.getRandom();
                synchronized (World.getPlayers()) {
                    for (Player p : World.getPlayers()) {
                        if (p != owner) {
                            int x = rand.nextInt(width);
                            int y = rand.nextInt(height);
                            boolean XModifier = rand.nextInt(2) == 0;
                            boolean YModifier = rand.nextInt(2) == 0;
                            if (XModifier)
                                x = -x;
                            if (YModifier)
                                y = -y;
                            p.setReturnPoint();
                            p.resetLevers();
                            p.teleport(owner.getX() + x, owner.getY() + y, false);
                        }
                    }
                }
                owner.sendMessage(Config.PREFIX + "All players summoned");
			}	
		}
        else // return all players who have been summoned
        if(cmd.equalsIgnoreCase("returnall") && owner.isAdmin())
        {
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
            owner.sendMessage(Config.PREFIX + "All players summoned");
		}
        else
        if(cmd.equalsIgnoreCase("massitem") && owner.isAdmin())
        {
			if (args.length < 2)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount]");
				return;
			}
            
            try
            {
                int id = Integer.parseInt(args[0]);
                int amount = Integer.parseInt(args[1]);
                if (EntityHandler.getItemDef(id) != null) {
                    int x = 0;
                    int y = 0;
                    int baseX = owner.getX();
                    int baseY = owner.getY();
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
            }
            catch (NumberFormatException e)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount]");
                return;
            }
		}
        else // spawn NPCs who drop a specified item on death (only one NPC will drop the loot)
        if (cmd.equalsIgnoreCase("npcevent") && owner.isAdmin())
        {
			if (args.length < 4)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [npc_amount] [item_id] [item_amount]");
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
			} catch (NumberFormatException e)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [npc_amount] [item_id] [item_amount]");
				return;
			}
            
			int x = 0;
			int y = 0;
			int baseX = owner.getX();
			int baseY = owner.getY();
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
		}
        else // spawns multiple of a specific NPC type
        if (cmd.equalsIgnoreCase("massnpc") && owner.isAdmin())
        {
			if (args.length < 2)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount] [duration_minutes]");
				return;
			}
            
            try
            {
                int id = Integer.parseInt(args[0]);
                int amount = Integer.parseInt(args[1]);
                int duration = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
                if (EntityHandler.getNpcDef(id) != null) {
                    int x = 0;
                    int y = 0;
                    int baseX = owner.getX();
                    int baseY = owner.getY();
                    int nextX = 0;
                    int nextY = 0;
                    int dX = 0;
                    int dY = 0;
                    int minX = 0;
                    int minY = 0;
                    int maxX = 0;
                    int maxY = 0;
                    for(int i = 0; i < amount; i++)
                    {
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
                                World.getDelayedEventHandler().add(new SingleEvent(null, duration * 60000) {
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
            catch(NumberFormatException e)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount] [duration_minutes]");
                return;
            }
		}  
        else // Talk as another player
        if (cmd.equalsIgnoreCase("playertalk") && owner.isAdmin())
        {
			if (args.length < 2)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [name] [msg]");
				return;
			}
            
			String msg = "";
            
			for (int i = 1; i < args.length; i++)
				msg += args[i] + " ";
            
			Player p = World.getPlayer(DataConversions.usernameToHash(args[0]));
			if (p == null)
            {
				owner.sendMessage(Config.PREFIX + "Invalid name");
				return;
			}
			p.addMessageToChatQueue(msg);
            Logger.log(new GenericLog(owner.getUsername() + " said \"" + msg + "\" as " + p.getUsername(), DataConversions.getTimeStamp()));
		}
        else // Talk as an NPC
        if (cmd.equalsIgnoreCase("npctalk") && owner.isAdmin())
        {
            if(args.length < 2)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [msg]");
                return;
            }
            
            try
            {
                int npc_id      = Integer.parseInt(args[0]);

                String newStr = "";
                for (int i = 1; i < args.length; i++)
                    newStr = newStr += args[i] + " ";

                final Npc n = World.getNpc(npc_id, owner.getX() - 10, owner.getX() + 10, owner.getY() - 10, owner.getY() + 10);

                if (n != null)
                {
                    for (Player p : owner.getViewArea().getPlayersInView())
                        p.informOfNpcMessage(new ChatMessage(n, newStr, p));		
                }
                else
                {
                    owner.sendMessage(Config.PREFIX + "NPC could not be found");
                }
            }
            catch (NumberFormatException e)
            {
                
            }
		}
        else // modify a specific stat
        if (cmd.equalsIgnoreCase("stat") && owner.isAdmin())
        {
			if (args.length != 3)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [stat] [level] [user]");
                return;
            }
            try
			{
				byte stat = -1;
				if ((stat = (byte)statArray.indexOf(args[0])) != -1) {
					int level = Integer.parseInt(args[1]);
					if(level < 100 && level >= 1) {
						Player playerToEdit = owner;
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
							if (playerToEdit == owner)
								owner.sendMessage(Config.PREFIX + "You set your " + statArray.get(stat) + " to " + level);
							else {
								owner.sendMessage(Config.PREFIX + "Successfully edited " + playerToEdit.getUsername() + "'s " + statArray.get(stat) + " to " + level);
								playerToEdit.sendMessage(Config.PREFIX + owner.getUsername() + " has set your " + statArray.get(stat) + " to " + level);
							}
						} else
							owner.sendMessage(Config.PREFIX + "Invalid name");
					} else
						owner.sendMessage(Config.PREFIX + "Invalid level");
				} else
					owner.sendMessage(Config.PREFIX + "Invalid stat");
			}
            catch (NumberFormatException e)
            {
				owner.sendMessage(badSyntaxPrefix + cmd.toUpperCase() + " [stat] [level] [user]");
                return;
            }
		} else if (cmd.equalsIgnoreCase("smitenpc") && (owner.isAdmin() || owner.isDev())) {
			if (args.length == 2) {
				try {
					int id = Integer.parseInt(args[0]);
					Npc n = World.getNpc(id, owner.getX() - 10, owner.getX() + 10, owner.getY() - 10, owner.getY() + 10);
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
								n.killedBy(owner);
						} catch(Exception ex) {}
					}
				} catch(Exception e) {}
			} else
				owner.sendMessage(Config.PREFIX + "Invalid args: SMITENPC [ID] [DAMAGE]");
		} else if (cmd.equalsIgnoreCase("refreshchests") && owner.isAdmin()) {
			try {
				EntityHandler.setChestDefinitions(World.getWorldLoader().loadChestDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equalsIgnoreCase("refreshstalls") && owner.isAdmin()) {
			try {
				EntityHandler.setStallThievingDefinitions(World.getWorldLoader().loadStallThievingDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equalsIgnoreCase("refreshlockeddoors") && owner.isAdmin()) {
			try {
				EntityHandler.setPicklockDoorDefinitions(World.getWorldLoader().loadPicklockDoorDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equalsIgnoreCase("refreshpickpocket") && owner.isAdmin()) {
			try {
				EntityHandler.setPickPocketDefinitions(World.getWorldLoader().loadPickPocketDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equalsIgnoreCase("refreshwoodcut") && owner.isAdmin()) {
			try {
				EntityHandler.setWoodcutDefinitions(World.getWorldLoader().loadWoodcuttingDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equalsIgnoreCase("refreshfishing") && owner.isAdmin()) {
			try {
				EntityHandler.setFishingDefinitions(World.getWorldLoader().loadFishingDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(cmd.equalsIgnoreCase("refreshnpchandlers") && owner.isAdmin()) {
			try {
				World.getWorldLoader().loadNpcHandlers();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equalsIgnoreCase("refreshagility") && owner.isAdmin()) {
			try {
				EntityHandler.setAgilityCourseDefinitions(World.getWorldLoader().loadAgilityCourseDefinitions());
				EntityHandler.setAgilityDefinitions(World.getWorldLoader().loadAgilityDefinitons());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equalsIgnoreCase("refreshedibles") && owner.isAdmin()) {
			try {
				EntityHandler.setItemHealingDefinitions(World.getWorldLoader().loadItemEdibleHeals());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equalsIgnoreCase("refreshnpcs") && owner.isAdmin()) {
			for (Npc n : World.getNpcs())
				n.unconditionalRemove();
			try {
				World.getWorldLoader().loadNpcLocations();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equalsIgnoreCase("refreshcerters") && owner.isAdmin()) {
			try {
				EntityHandler.setCerterDefinitions(World.getWorldLoader().loadCerterDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equalsIgnoreCase("refreshherbs") && owner.isAdmin()) {
			try {
				EntityHandler.setHerbDefinitions(World.getWorldLoader().loadHerbDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equalsIgnoreCase("refreshunidentifiedherbs") && owner.isAdmin()) {
			try {
				EntityHandler.setUnidentifiedHerbDefinitions(World.getWorldLoader().loadUnidentifiedHerbDefinitions());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (cmd.equalsIgnoreCase("event")) {
			if (args.length > 1) {
				if (owner.isMod() || owner.isEvent()) {
					if (!World.eventRunning) {
						try {
							int low = Integer.parseInt(args[0]);
							int high = Integer.parseInt(args[1]);
							if (low <= high && low >= 3 && high <= 123) {
								World.eventLow = low;
								World.eventHigh = high;
								World.setEvent(owner.getX(), owner.getY());
								synchronized (World.getPlayers()) {
									for (Player p : World.getPlayers()) {
										p.sendNotification(Config.PREFIX + "Type @gre@::EVENT@whi@ to join the event!");
										if (owner.getLocation().inWilderness())
											p.sendNotification(Config.PREFIX + "@red@Warning:@whi@ This event is located in the wilderness!");									
										p.sendNotification(Config.PREFIX + "@yel@Level Restriction:@whi@ Level " + low + (low != high ? " to Level " + high : ""));
										p.sendNotification(Config.PREFIX + "An event has been set by " + owner.getStaffName());
									}
								}
							} else
								owner.sendMessage(Config.PREFIX + "Invalid level range");
						} catch(Exception e) {
							owner.sendMessage(Config.PREFIX + "Invalid level range");
						}							
					} else
						owner.sendMessage(Config.PREFIX + "There is already an event running!");
				} else
					owner.sendMessage(Config.PREFIX + "Invalid args! Syntax EVENT");
			} else {
				if (World.eventPoint != null) {
					if (!owner.getLocation().inWilderness() && !owner.isTrading() && !owner.isBusy() && !owner.accessingShop() && !owner.accessingBank() && !owner.isDueling()) {
						if(owner.getLocation().inBounds(792, 23, 794, 25))
						{
							owner.sendMessage(Config.PREFIX + "You cannot use ::event whilst being jailed.");
							return;
						}
						if (!World.joinEvent(owner))
							owner.sendMessage(Config.PREFIX + "You aren't eligible for this event");
					} else
						owner.sendMessage(Config.PREFIX + "You cannot enroll in this event right now");
				}
			}
		} else 
			if (cmd.equalsIgnoreCase("endevent") && (owner.isMod() || owner.isDev() || owner.isEvent())) 
			{
			if (World.eventRunning) {
				World.setEvent(-1, -1);
				synchronized (World.getPlayers()) {
					for (Player p : World.getPlayers())
						p.sendNotification(Config.PREFIX + "Event registration has been closed by " + owner.getStaffName());	
				}
			} else 
				owner.sendMessage(Config.PREFIX + "No event is currently running");
			} else if (cmd.equalsIgnoreCase("islandsafe") && (owner.isEvent() || owner.isMod())) {
				World.islandSafe = !World.islandSafe;
				owner.sendMessage(Config.PREFIX + "Safe mode " + (World.islandSafe ? "enabled" : "disabled"));
			} else if (cmd.equalsIgnoreCase("islandcombat") && (owner.isEvent() || owner.isMod())) {
				World.islandCombat = !World.islandCombat;
				owner.sendMessage(Config.PREFIX + "Combat " + (World.islandCombat ? "disabled" : "enabled"));
			} else if(cmd.equalsIgnoreCase("ipban") && owner.isAdmin()) {
			if (args.length != 1) {
				owner.sendMessage(Config.PREFIX + "Invalid args. Syntax: IPBAN [ip]");
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
				
				owner.sendMessage(Config.PREFIX + args[0] + " was successfully IP banned");
		} else if (cmd.equalsIgnoreCase("unipban") && owner.isAdmin()) {
			if (args.length != 1) {
				owner.sendMessage(Config.PREFIX + "Invalid args. Syntax: UNIPBAN [ip]");
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
				owner.sendMessage(Config.PREFIX + args[0] + " has been removed from the IP ban list");
		} else if (cmd.equalsIgnoreCase("time") || cmd.equalsIgnoreCase("date")) {
			owner.sendMessage(Config.PREFIX + Config.SERVER_NAME + "'s time/date is:@gre@ " + new java.util.Date().toString());
		} else if (cmd.equalsIgnoreCase("skiptutorial")) {
			if (owner.getLocation().onTutorialIsland())
			{
				for (InvItem i : owner.getInventory().getItems()) {
					if (owner.getInventory().get(i).isWielded()) {
						owner.getInventory().get(i).setWield(false);
						owner.updateWornItems(i.getWieldableDef().getWieldPos(), owner.getPlayerAppearance().getSprite(i.getWieldableDef().getWieldPos()));
					}	
				}
				owner.getInventory().getItems().clear();
				owner.sendInventory();			
				owner.getInventory().add(new InvItem(70, 1));
				owner.getInventory().add(new InvItem(1263, 1));
				owner.getInventory().add(new InvItem(156, 1));
				owner.getInventory().add(new InvItem(4, 1));
				owner.getInventory().add(new InvItem(87, 1));
				owner.getInventory().add(new InvItem(376, 1));
				owner.sendInventory();
				owner.teleport(122, 647, false);
			}
		} else if (cmd.equalsIgnoreCase("lottery")) {
	        if (World.lotteryRunning())
	                World.buyTicket(owner);
	        else
	                owner.sendMessage(Config.PREFIX + " There's no lottery running right now");
		} else if (cmd.equalsIgnoreCase("lotterypot")) {
	        World.getLotteryPot(owner);     
		} 
		else if(cmd.equalsIgnoreCase("godspells") && owner.isAdmin())
		{
			if(args.length != 1)
			{
				owner.sendMessage("Invalid Syntax - Usage: godspells [boolean] eg. '::godspells true'");
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
				owner.sendMessage("Invalid Syntax - Usage: godspells [boolean] eg. '::godspells true'");
				return;
			}
		}
		else if(cmd.equalsIgnoreCase("weakens") && owner.isAdmin())
		{
			if(args.length != 1)
			{
				owner.sendMessage("Invalid Syntax - Usage: weakens [boolean] eg. '::weakens true'");
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
				owner.sendMessage("Invalid Syntax - Usage: weakens [boolean] eg. '::weakens true'");
				return;
			}
		} else
        /*
         * Change password
         */
        if (cmd.equalsIgnoreCase("changepassword")) 
		{
			if (args.length != 1) 
			{
				owner.sendMessage(Config.PREFIX + "Invalid args. Syntax: CHANGEPASSWORD [new_password]");
				return;
			}
            
            // Check if a owner already has a password change event.
            ArrayList events = World.getDelayedEventHandler().getEvents();
            Iterator<DelayedEvent> iterator = events.iterator();
            while (iterator.hasNext()) {
				DelayedEvent event = iterator.next();
                
                if(!(event instanceof ChangePasswordEvent)) continue;
                
                if(event.belongsTo(owner)) {
                    owner.sendMessage(Config.PREFIX + "You have already initiated a password change.");
                    owner.sendMessage(Config.PREFIX + "Type ::confirmpassword [new_password] within 30 seconds to finish.");
                }
            }
			
			World.getDelayedEventHandler().add(new ChangePasswordEvent(owner, args[0]));
            owner.sendMessage(Config.PREFIX + "Password change initiated.");
            owner.sendMessage(Config.PREFIX + "Type ::confirmpassword [new_password] within 30 seconds to finish.");
		}
		else
        /*
         * Change password
         */
		if (cmd.equalsIgnoreCase("confirmpassword")) 
		{
			if (args.length != 1) 
			{
				owner.sendMessage(Config.PREFIX + "Invalid args. Syntax: CONFIRMPASSWORD [new_password]");
				return;
			}
            
            // Look for the existing password change event...
            ChangePasswordEvent originatingEvent = null;
            ArrayList events = World.getDelayedEventHandler().getEvents();
            Iterator<DelayedEvent> iterator = events.iterator();
            while (iterator.hasNext()) {
				DelayedEvent event = iterator.next();
                
                if(!(event instanceof ChangePasswordEvent)) continue;
                
                if(event.belongsTo(owner)) {
                    originatingEvent = (ChangePasswordEvent)event;
                    break;
                }
            }
            
            if(originatingEvent == null){
                owner.sendMessage(Config.PREFIX + "You have not initiated a password change.");
                owner.sendMessage(Config.PREFIX + "Type ::changepassword [new_password] to change your password.");
                return;
            }
            
            originatingEvent.confirmPassword(args[0]);
		}
		else
        if (cmd.equalsIgnoreCase("startlottery") && (owner.isMod() || owner.isDev() || owner.isEvent())) 
        {
            if (!World.lotteryRunning())
                if (args.length != 1)
                    owner.sendMessage(Config.PREFIX + " Invalid args. Syntax: STARTLOTTERY [price]");
                else
                    try {
                        World.startLottery(Integer.parseInt(args[0]));
                    } catch (Exception e) {}       
        } 
        else 
        if (cmd.equalsIgnoreCase("stoplottery") && (owner.isMod() || owner.isDev() || owner.isEvent())) 
        {
            if (World.lotteryRunning())
                World.stopLottery();
            else
                owner.sendMessage(Config.PREFIX + " There's no lottery running right now");
        }
    }
	public static final ArrayList<String> statArray = new ArrayList<String>(){{
		add("attack"); add("defense"); add("strength"); add("hits"); add("ranged"); add("prayer"); add("magic"); add("cooking"); add("woodcut"); add("fletching"); add("fishing"); add("firemaking"); add("crafting"); add("smithing"); add("mining"); add("herblaw"); add("agility"); add("thieving"); add("runecrafting");
	}};	
}