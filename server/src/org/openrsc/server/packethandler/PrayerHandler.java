package org.openrsc.server.packethandler;

import org.openrsc.server.Config;
import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ExploitLog;
import org.openrsc.server.model.Player;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.PrayerDef;

import org.apache.mina.common.IoSession;
public class PrayerHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			int pID = ((RSCPacket)p).getID();
			int prayerID = (int)p.readByte();
			if (prayerID < 0 || prayerID >= 14) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "PrayerHandler (1)", DataConversions.getTimeStamp()));
				player.sendPrayers();
				return;
			}
			if (player.isDueling() && player.getDuelSetting(2)) {
				player.sendMessage("Prayer cannot be used during this duel!");
				player.sendPrayers();
				return;
			}
			if (player.isDMing() && player.getDMSetting(0)) {
				player.sendMessage(Config.PREFIX + "Prayer cannot be used in this Death Match");
				player.sendPrayers();
				return;
			}
			if (prayerID == 8 && player.getLocation().isInDMArena()) {
				player.sendMessage(Config.PREFIX + "Protect Item cannot be used in Death Match's");
				player.sendPrayers();
				return;
			}
			PrayerDef prayer = EntityHandler.getPrayerDef(prayerID);
			switch (pID) {
				case 22:
					if (player.getMaxStat(5) < prayer.getReqLevel()) {
						Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "PrayerHandler (2)", DataConversions.getTimeStamp()));
						break;
					}
					if (player.getCurStat(5) <= 0) {
						player.setPrayer(prayerID, false);
						player.sendMessage("You have run out of prayer points. Return to a church to recharge");
						break;
					}
					activatePrayer(player, prayerID);
				break;
				
				case 23:
					deactivatePrayer(player, prayerID);
				break;
			}
			player.sendPrayers();
		}
	}
	
	private final void activatePrayer(Player player, int prayerID) {
		try {
			if (!player.isPrayerActivated(prayerID)) {
				switch (prayerID) {
					case 11:
						deactivatePrayer(player, 5);
						deactivatePrayer(player, 2);
					break;
					
					case 5:
						deactivatePrayer(player, 2);
						deactivatePrayer(player, 11);
					break;
					
					case 2:
						deactivatePrayer(player, 5);
						deactivatePrayer(player, 11);
					break;
					
					case 10:
						deactivatePrayer(player, 4);
						deactivatePrayer(player, 1);
					break;
					
					case 4:
						deactivatePrayer(player, 10);
						deactivatePrayer(player, 1);
					break;
					
					case 1:
						deactivatePrayer(player, 10);
						deactivatePrayer(player, 4);
					break;
					
					case 9:
						deactivatePrayer(player, 3);
						deactivatePrayer(player, 0);
					break;
					
					case 3:
						deactivatePrayer(player, 9);
						deactivatePrayer(player, 0);
					break;
					
					case 0:
						deactivatePrayer(player, 9);
						deactivatePrayer(player, 3);
					break;
				}
				player.addPrayerDrain(prayerID);
				player.setPrayer(prayerID, true);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private final void deactivatePrayer(Player player, int prayerID) {
		if (player.isPrayerActivated(prayerID)) {
			player.removePrayerDrain(prayerID);
			player.setPrayer(prayerID, false);
		}
	}
}