package org.openrsc.server.packethandler;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ExploitLog;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;
public class WieldHandler implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			int pID = ((RSCPacket)p).getID();
			if (player.isBusy() && !player.inCombat())
				return;
			if (player.isDueling() && player.getDuelSetting(3)) {
				player.sendMessage("No extra items may be worn during this duel!");
				return;
			}
			player.resetAllExceptDueling();
			int idx = (int)p.readShort();
			if (idx < 0 || idx >= 30) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "WieldHandler (1)", DataConversions.getTimeStamp()));
				return;
			}
			InvItem item = player.getInventory().get(idx);
			if (item == null || !item.isWieldable()) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "WieldHandler (2)", DataConversions.getTimeStamp()));
				return;
			}
			if (!player.canWield())
				return;
			player.setLastWield();
			switch (pID) {
				case 20:
					if (!item.isWielded()) {
						switch (item.getID()) {
							case 1213: // Zamorak Cape
								if (player.getInventory().wielding(1217)) {
									player.sendMessage("The power of Guthix prevents you from equipping this item.");
									break;
								} else if (player.getInventory().wielding(1218)) {
									player.sendMessage("The power of Saradomin prevents you from equipping this item.");
									break;
								}
								wieldItem(player, item);
								break;
							case 1216: // Staff of Zamorak
								if (player.getInventory().wielding(1214)) {
									player.sendMessage("The power of Saradomin prevents you from equipping this item.");
									break;
								} else if (player.getInventory().wielding(1215)) {
									player.sendMessage("The power of Guthix prevents you from equipping this item.");
									break;
								}
								wieldItem(player, item);
								break;
							case 1214: // Saradomin Cape
								if (player.getInventory().wielding(1216)) {
									player.sendMessage("The power of Zamorak prevents you from equipping this item.");
									break;
								} else if (player.getInventory().wielding(1217)) {
									player.sendMessage("The power of Guthix prevents you from equipping this item.");
									break;
								}
								wieldItem(player, item);
								break;
							case 1218: // Saradomin Staff
								if (player.getInventory().wielding(1213)) {
									player.sendMessage("The power of Zamorak prevents you from equipping this item.");
									break;
								} else if (player.getInventory().wielding(1215)) {
									player.sendMessage("The power of Guthix prevents you from equipping this item.");
									break;
								}
								wieldItem(player, item);
								break;
							case 1217: // Staff of Guthix
								if (player.getInventory().wielding(1214)) {
									player.sendMessage("The power of Saradomin prevents you from equipping this item.");
									break;
								} else if (player.getInventory().wielding(1213)) {
									player.sendMessage("The power of Zamorak prevents you from equipping this item.");
									break;
								}
								wieldItem(player, item);
								break;
							case 1215: //Guthix Cape
								if (player.getInventory().wielding(1218)) {
									player.sendMessage("The power of Saradomin prevents you from equipping this item.");
									break;
								} else if (player.getInventory().wielding(1216)) {
									player.sendMessage("The power of Zamorak prevents you from equipping this item.");
									break;
								}
								wieldItem(player, item);
								break;
                            case 401: // rune platebody
                                Quest q = player.getQuest(17);
                                if(q == null || !q.finished())
                                {
                                    player.sendMessage("you have not earned the right to wear this yet");
                                    player.sendMessage("you need to complete the dragon slayer quest");
                                    break;
                                }
								wieldItem(player, item);
                                break;
							default:
								wieldItem(player, item);
								break;
						}
					}
					break;
				case 21:
					if (item.isWielded())
						unWieldItem(player, item, true);
					break;
			}
			player.sendInventory();
			player.sendEquipmentStats();
		}
	}
	
	private void wieldItem(Player player, InvItem item) {
		
		if (player.isDueling())
		{
			switch(item.getID())
			{
				case 744:
				case 522:
				case 597:
				case 314:
				case 315:
				case 316:
				case 317:
					player.sendMessage(Config.PREFIX + "Switching amulets during dueling has been disabled");
				return;
			}
		}
				
		if (player.getLocation().inWilderness() && item.getDef().isP2P() && !World.isP2PWilderness()) 
		{
			player.sendMessage(Config.PREFIX + item.getDef().name + " is only weildable while the wilderness is P2P");
			return;
		}
		
		
		String youNeed = "";
		for (Entry<Integer, Integer> e : item.getWieldableDef().getStatsRequired()) {
			if (player.getMaxStat(e.getKey()) < e.getValue())
				youNeed += ((Integer)e.getValue()).intValue() + " " + Formulae.STAT_ARRAY[((Integer)e.getKey()).intValue()] + ", ";
		}
		if (!youNeed.equals("")) 
		{
			switch (item.getID())
			{		
				default:
					player.sendMessage("You must have at least " + youNeed.substring(0, youNeed.length() - 2) + " to use this item.");
				return;
			}
		}
	
		if (EntityHandler.getItemWieldableDef(item.getID()).femaleOnly() && player.isMale()) {
			player.sendMessage("It doesn't fit!");
			player.sendMessage("Perhaps I should get someone to adjust it for me");
			return;
		}
	  	ArrayList<InvItem> items = player.getInventory().getItems();
	  	for (InvItem i : items) {
	  		if (item.wieldingAffectsItem(i) && i.isWielded())
	  			unWieldItem(player, i, false);
	  	}
	  	item.setWield(true);
	  	player.sendSound("click", false);
	  	player.updateWornItems(item.getWieldableDef().getWieldPos(), item.getWieldableDef().getSprite());
	}
	
	private void unWieldItem(Player player, InvItem item, boolean sound) {
		item.setWield(false);
		if (sound)
			player.sendSound("click", false);
		player.updateWornItems(item.getWieldableDef().getWieldPos(), player.getPlayerAppearance().getSprite(item.getWieldableDef().getWieldPos()));
	}
}