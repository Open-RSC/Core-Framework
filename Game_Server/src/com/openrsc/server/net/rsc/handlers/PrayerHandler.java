package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.external.PrayerDef;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

public class PrayerHandler implements PacketHandler {

	private boolean activatePrayer(Player player, int prayerID) {
		if (!player.getPrayers().isPrayerActivated(prayerID)) {
			if (prayerID == 11) {
				deactivatePrayer(player, 5);
				deactivatePrayer(player, 2);
			} else if (prayerID == 5) {
				deactivatePrayer(player, 2);
				deactivatePrayer(player, 11);
			} else if (prayerID == 2) {
				deactivatePrayer(player, 5);
				deactivatePrayer(player, 11);
			} else if (prayerID == 10) {
				deactivatePrayer(player, 4);
				deactivatePrayer(player, 1);
			} else if (prayerID == 4) {
				deactivatePrayer(player, 10);
				deactivatePrayer(player, 1);
			} else if (prayerID == 1) {
				deactivatePrayer(player, 10);
				deactivatePrayer(player, 4);
			} else if (prayerID == 9) {
				deactivatePrayer(player, 3);
				deactivatePrayer(player, 0);
			} else if (prayerID == 3) {
				deactivatePrayer(player, 9);
				deactivatePrayer(player, 0);
			} else if (prayerID == 0) {
				deactivatePrayer(player, 9);
				deactivatePrayer(player, 3);
			} else if (prayerID == 6 || prayerID == 7) {
				//TODO:
			}
			player.getPrayers().setPrayer(prayerID, true);
			return true;
		}
		return false;
	}

	private boolean deactivatePrayer(Player player, int prayerID) {
		if (player.getPrayers().isPrayerActivated(prayerID)) {
			player.getPrayers().setPrayer(prayerID, false);
			if (prayerID == 6 || prayerID == 7) {
				//TODO:
			}
			return true;
		}
		return false;
	}

	public void handlePacket(Packet p, Player player) throws Exception {
		int pID = p.getID();
		int prayerID = (int) p.readByte();
		if (prayerID < 0 || prayerID >= 14) {
			player.setSuspiciousPlayer(true, "prayer id < 0 or prayer id >= 14");
//			ActionSender.sendPrayers(player);TODO
			return;
		}
		if (player.getDuel().isDuelActive() && player.getDuel().getDuelSetting(2)) {
			player.message("Prayers cannot be used during this duel!");
//			ActionSender.sendPrayers(player);
			return;
		}
		if (prayerID == 8 && player.isIronMan(2)) {
			player.message("Ultimate Iron Mens, cannot protect items");
			player.getPrayers().resetPrayers();
			return;
		}

		PrayerDef prayer = player.getWorld().getServer().getEntityHandler().getPrayerDef(prayerID);
		int packetOne = OpcodeIn.PRAYER_ACTIVATED.getOpcode();
		int packetTwo = OpcodeIn.PRAYER_DEACTIVATED.getOpcode();
		if (pID == packetOne) {
			if (player.getSkills().getMaxStat(5) < prayer.getReqLevel()) {
				player.setSuspiciousPlayer(true, "max stat prayer < req level");
				player.message("Your prayer ability is not high enough to use this prayer");
			}
			if (player.getSkills().getLevel(5) <= 0) {
				player.getPrayers().setPrayer(prayerID, false);
				player.message("You have run out of prayer points. Return to a church to recharge");
			}
			activatePrayer(player, prayerID);
		} else if (pID == packetTwo) {
			deactivatePrayer(player, prayerID);
		}
	}
}
