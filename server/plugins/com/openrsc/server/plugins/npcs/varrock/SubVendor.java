package com.openrsc.server.plugins.npcs.varrock;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.sql.DatabaseConnection;

public class SubVendor implements TalkToNpcExecutiveListener, TalkToNpcListener, PickupListener, PickupExecutiveListener, InvActionListener, InvActionExecutiveListener {

	private final int SUB_VENDOR_FEMALE = 796;
	private final int SUB_VENDOR_MALE = 797;

	private final int GOLD_TOKEN = 2092;
	private final int PREMIUM_TOKEN = 2094;

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(!Constants.GameServer.SPAWN_SUBSCRIPTION_NPCS) return;
		if(n.getID() == SUB_VENDOR_FEMALE || n.getID() == SUB_VENDOR_MALE) {
			npcTalk(p, n, "Hello, I'm the subscription vendor",
					"How can I help you?");
			int menu = showMenu(p, n, "I want to exchange my gold token", "I want to exchange my premium token", "What are these tokens?", "I want to claim my teleport stones", "I want to change my death spawn location");
			if(menu == 0) {
				if(hasItem(p, GOLD_TOKEN)) {
					tokenExchangeTime(p, n, new Item(GOLD_TOKEN));
				} else {
					p.message("You don't have any gold tokens");
				}
			} else if(menu == 1) {
				if(hasItem(p, PREMIUM_TOKEN)) {
					tokenExchangeTime(p, n, new Item(PREMIUM_TOKEN));
				} else {
					p.message("You don't have any premium tokens");
				}
			} else if(menu == 2) {
				npcTalk(p, n, "There are two types of tokens",
						"Gold and Premium. We collect these tokens in exchange for subscription",
						"Being a subscriber gives you benefits such as",
						"Better XP rate, more idle time, slower fatigue rate.",
						"The premium token is a boost token, used together with gold",
						"it will give you the maximum subscriber benefits.");
			} else if(menu == 3) {
				if(p.getTeleportStones() > 0) {
					npcTalk(p, n, "Ok. You have " + p.getTeleportStones() + " stones to claim",
							"Do you want to claim them now?");
					int stoneMenu = showMenu(p, n, "Yes, I want to claim them now", "Not yet");
					if(stoneMenu == 0) {
						p.message("You claim your teleport stones...");
						addItem(p, 2107, p.getTeleportStones());
						p.setTeleportStones(0);
						Server.getPlayerDataProcessor().getDatabase().setTeleportStones(0, p.getOwner());
						npcTalk(p, n, "There you go, have a nice day!");
					}
				} else {
					npcTalk(p, n, "sorry, you have no teleport stones to claim");
				}
			} else if(menu == 4) {
				if(p.getDaysSubscriptionLeft() > 0) {
					npcTalk(p, n, "Yes " + (p.isMale() ? " Sir" : "Ma'am"));
					if(p.getCache().hasKey("death_location_x") && p.getCache().getInt("death_location_x") == 216) {
						npcTalk(p, n, "Your current death location is in Edgeville");
					} else {
						npcTalk(p, n, "Your current death location is in Lumbridge");
					}
					npcTalk(p, n, "Where would you like to change your death spawn to?",
							"The service is free of charge");
					int spawnMenu = showMenu(p, n, "Edgeville", "Lumbridge");
					if(spawnMenu == 0) {
						p.message("Your spawn location has been changed to Edgeville");
						p.getCache().set("death_location_x", 216);
						p.getCache().set("death_location_y", 461);
					} else if(spawnMenu == 1) {
						p.message("Your spawn location has been changed to Lumbridge");
						p.getCache().set("death_location_x", 122);
						p.getCache().set("death_location_y", 647);
					}
				} else {
					npcTalk(p, n, "Sorry, " + (p.isMale() ? " Sir" : "Ma'am") + "!", 
							"It looks like you are not a subscriber");
					p.message("You need to be a gold or premium subscriber for this feature");
				}
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == SUB_VENDOR_FEMALE || n.getID() == SUB_VENDOR_MALE) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if(i.getID() == GOLD_TOKEN && i.getX() == 119 && i.getY() == 516) {
			return true;
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if(!Constants.GameServer.SPAWN_SUBSCRIPTION_NPCS) return;
		if(i.getID() == GOLD_TOKEN && i.getX() == 119 && i.getY() == 516) {
			ActionSender.sendBox(p, 
					"@yel@Subscription Token %"
							+ "There are two types of tokens @yel@Gold @whi@and @cya@Premium % %"
							+ "@yel@Gold subscription token: %"
							+ "-Faster XP rates (+1x combat, +1x skilling) %"
							+ "-Slower Fatigue %"
							+ "-5+ minute extra standing idle time %"
							+ "-Gold colored name in global chat % %"
							+ "@red@COMBINED WITH GOLD TOKEN ONLY* %"
							+ "@cya@Premium subscription token: %"
							+ "-Faster XP rates (+1x combat, +0.5x skilling) %"
							+ "-0.75x+ batch rate %"
							+ "-Slower Fatigue %"
							+ "-15+ minute extra standing idle time %"
							+ "-Premium colored name in global chat % %"
							+ "Please visit our website in order to get your token(s): %"
							+ "www.openrsc.com/donate.php" , true);
		}
	}

	private void tokenExchangeTime(Player p, Npc n, Item i) {
		if(!Constants.GameServer.SPAWN_SUBSCRIPTION_NPCS) return;
		long subTime = 0;
		long now = System.currentTimeMillis() / 1000;
		switch(i.getID()) {
		case GOLD_TOKEN:
			if(removeItem(p, i.getID(), 1)) {
				p.message("You hand over your " + i.getDef().getName().toLowerCase());
				subTime = (86400 * 30);
				if(p.getDaysSubscriptionLeft() > 0) {
					p.setSubscriptionExpires(p.getSubscriptionExpires() + subTime);
				} else {
					p.setSubscriptionExpires(now + subTime);
				}
				DatabaseConnection.getDatabase().executeUpdate("UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` SET sub_expires='"+ p.getSubscriptionExpires()  +"', group_id=6 WHERE `id`='" + p.getDatabaseID() + "'");
				DatabaseConnection.getDatabase().executeUpdate("UPDATE `users` SET gold_time="+ p.getSubscriptionExpires()  +" WHERE `id`='" + p.getOwner() + "'");
				if(n != null)
					npcTalk(p, n, "Thank you! Your membership will last for " + p.getDaysSubscriptionLeft() + " days");
				p.message("Your membership is now active!");	
			} else {
				p.message("You don't have any premium tokens");
			}
			break;
		case PREMIUM_TOKEN:
			if(p.getDaysSubscriptionLeft() > 0) {
				if(removeItem(p, i.getID(), 1)) {
					p.message("You hand over your " + i.getDef().getName().toLowerCase());
					if(p.premiumSubDaysLeft() > 0) {
						p.setPremiumExpires(p.getPremiumExpires() + (86400 * 30));
					} else {
						p.setPremiumExpires(now + (86400 * 30));
					}
					DatabaseConnection.getDatabase().executeUpdate("UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` SET platinum_expires='"+ p.getPremiumExpires()  +"' WHERE `id`='" + p.getDatabaseID() + "'");
					DatabaseConnection.getDatabase().executeUpdate("UPDATE `users` SET premium_time="+ p.getPremiumExpires()  +" WHERE `id`='" + p.getOwner() + "'");
					npcTalk(p, n, "Thank you! Your premium membership will last for " + p.premiumSubDaysLeft() + " days");
					p.message("Your premium membership is now active!");	
				} else {
					p.message("You don't have any premium tokens");
				}
			} else {
				p.message("You need to have an active gold subscription time first");
				npcTalk(p, n, "To gain maximum subscriber benefits",
						"The premium token need to be used with a gold token");
			}
			break;
		} 
	}

	@Override
	public boolean blockInvAction(Item item, Player player) {
		if(item.getID() == GOLD_TOKEN || item.getID() == PREMIUM_TOKEN) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvAction(Item item, Player player) {
		if(!Constants.GameServer.SPAWN_SUBSCRIPTION_NPCS) return;
		if(item.getID() == GOLD_TOKEN || item.getID() == PREMIUM_TOKEN) {
			message(player, "You can exchange this token for subscription time");
			player.message("by talking to the vendors in Varrock Centre");
		}
	}
}
