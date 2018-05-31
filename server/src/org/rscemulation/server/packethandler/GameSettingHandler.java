package org.rscemulation.server.packethandler;
import org.apache.mina.common.IoSession;
import org.rscemulation.server.logging.Logger;
import org.rscemulation.server.logging.model.ExploitLog;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.net.Packet;
import org.rscemulation.server.util.DataConversions;
public class GameSettingHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if(player != null) {
			switch(p.readByte()) {
				case 0: //Camera Auto 'ON'
					player.setGameSetting(0, false);
					break;
				case 1://ONE(ortwo?) mouse buttons
					player.setGameSetting(1, false);
					break;
				case 2:  //Sound effects 'ON'
					player.setGameSetting(2, false);
					break;
				case 3: //Show Roofs 'ON'
					player.setGameSetting(3, false);
					break;
				case 4: //Auto Screenshot 'ON'
					player.setGameSetting(4, false);
					break;
				case 5: //Camera Auto 'OFF'
					player.setGameSetting(0, true);
					break;
				case 6: //TWO(orone?) mouse button(s)
					player.setGameSetting(1, true);
					break;
				case 7: //SOUND GREEN
					player.setGameSetting(2, true);
					break;
				case 8: //Show Roofs 'OFF'
					player.setGameSetting(3, true);
					break;
				case 9: //Auto Screenshot 'OFF'
					player.setGameSetting(4, true);
					break;
				case 10: //Combat Window 'ON'
					player.setCombatWindow(0);
					break;
				case 11: // Combat Window 'Fighting'
					player.setCombatWindow(1);
					break;
				case 12: //Combat Window 'OFF'
					player.setCombatWindow(2);
					break;
				case 13:
					player.setGameSetting(5, true);
					break;
				case 14:
					player.setGameSetting(5, false);
					break;
				default:
					Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "GameSettingHandler (1)", DataConversions.getTimeStamp()));
					break;
			}
		}
	}
}
