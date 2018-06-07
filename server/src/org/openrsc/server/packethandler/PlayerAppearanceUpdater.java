package org.openrsc.server.packethandler;

import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ExploitLog;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.PlayerAppearance;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Inventory;
import org.openrsc.server.net.Packet;

import org.apache.mina.common.IoSession;
public class PlayerAppearanceUpdater implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player != null) {
			if (!player.isChangingAppearance()) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "PlayerAppearanceUpdater (1)", DataConversions.getTimeStamp()));
				return;
			}
			player.setChangingAppearance(false);
			
			byte headGender = p.readByte();
			byte headType = p.readByte();
			byte bodyGender = p.readByte();
			
			p.readByte();
			
			int hairColour = (int)p.readByte();
			int topColour = (int)p.readByte();
			int trouserColour = (int)p.readByte();
			int skinColour = (int)p.readByte();
			
			int headSprite = headType + 1;
			int bodySprite = bodyGender + 1;

			PlayerAppearance appearance = new PlayerAppearance(hairColour, topColour, trouserColour, skinColour, headSprite, bodySprite);
			if (!appearance.isValid()) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "PlayerAppearanceUpdater (2)", DataConversions.getTimeStamp()));
				return;
			}
		
      		player.setMale(headGender == 1);
      		
      		if (player.isMale()) {
  	      		Inventory inv = player.getInventory();
  	      		for (int slot = 0;slot < inv.size();slot++) {
  	      			InvItem i = inv.get(slot);
  	      			if (i.isWieldable() && i.getWieldableDef().getWieldPos() == 1 && i.isWielded() && i.getWieldableDef().femaleOnly()) {
						i.setWield(false);
						player.updateWornItems(i.getWieldableDef().getWieldPos(), player.getPlayerAppearance().getSprite(i.getWieldableDef().getWieldPos()));
						player.sendUpdateItem(slot);
						break;
  	      			}
  	      		}
      		}
      		
      		int[] oldWorn = player.getWornItems();
      		int[] oldAppearance = player.getPlayerAppearance().getSprites();
      		player.setAppearance(appearance);
      		int[] newAppearance = player.getPlayerAppearance().getSprites();
      		
      		for (int i = 0; i < 12; i++)
      			if (oldWorn[i] == oldAppearance[i])
      				player.updateWornItems(i, newAppearance[i]);
		}
	}
}