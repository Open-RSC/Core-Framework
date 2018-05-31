package org.rscemulation.server.packethandler;

import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.model.*;
import org.rscemulation.server.net.Packet;

import org.apache.mina.common.IoSession;
public class PrivacySettingHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			switch (p.readByte()) {
				case 0:
					player.setPrivacySetting(0, true);
				break;
				
				case 1:
					player.setPrivacySetting(1, true);
					synchronized (World.getPlayers()) {
						for (Player friend : World.getPlayers()) {
							if (friend.isFriendsWith(player.getUsernameHash()) && !player.isFriendsWith(friend.getUsernameHash()))
								friend.sendFriendUpdate(player.getUsernameHash(), (byte)1);
						}
					}
				break;
				
				case 2:
					player.setPrivacySetting(2, true);
				break;
				
				case 3:
					player.setPrivacySetting(3, true);
					break;
				case 4:
					player.setPrivacySetting(4, true);
				break;
				
				case 5:
					player.setPrivacySetting(0, false);
				break;
				
				case 6:
					player.setPrivacySetting(1, false);
					synchronized (World.getPlayers()) {
						for(Player friend : World.getPlayers()) {
							if(friend.isFriendsWith(player.getUsernameHash()) &&!player.isFriendsWith(friend.getUsernameHash())) {
								friend.sendFriendUpdate(player.getUsernameHash(), (byte)0);
							}
						}
					}
				break;
				
				case 7:
					player.setPrivacySetting(2, false);
				break;
				
				case 8:
					player.setPrivacySetting(3, false);
				break;
				
				case 9:
					player.setPrivacySetting(4, false);
				break;
			}
		}
	}
}