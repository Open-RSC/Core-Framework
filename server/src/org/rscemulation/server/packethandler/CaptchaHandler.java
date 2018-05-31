package org.rscemulation.server.packethandler;

import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.net.Packet;
import org.apache.mina.common.IoSession;
public class CaptchaHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) {
		Player player = (Player)session.getAttachment();
		try {
			if (player != null) {
				if (System.currentTimeMillis() - player.getLastSleep() > 500) {
					String s1 = player.getSleepString();
					String s2 = p.readString();
					if (!s1.equalsIgnoreCase("[RESET]") && s1.equalsIgnoreCase(s2)) {
						player.sendSuccess();
						player.setFatigue(player.getTemporaryFatigue());
						player.stopSleepEvent();
						player.sendFatigue();
						player.resetSleepCount();
						player.sendMessage("You wake up - feeling refreshed");
					} else if (s2.equalsIgnoreCase("escape")) {
						player.sendMessage("You wake up - still feeling tired");
						player.stopSleepEvent();
						player.sendFatigue();
					} else if (!s1.equalsIgnoreCase("[RESET]") && !s1.equalsIgnoreCase(s2) && !s2.equalsIgnoreCase("")) {
						player.sendFailure();
						player.increaseSleepCount();
						player.sleep();
					} else {
						player.sleep();
					}
					player.setLastSleep();
				}
			}
		} catch(Exception ex) {
			if (player != null) {
				System.out.println(player.getUsername() + " (" + player.getIP() + ") sent a null sleep string.");
			}
			ex.printStackTrace();
		}
	}
}
