package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;

public class PlayerAppearanceUpdater implements PacketHandler {

	public void handlePacket(Packet packet, Player player) throws Exception {

		if (!player.isChangingAppearance()) {
			player.setSuspiciousPlayer(true, "player update packet without changing appearance");
			return;
		}
		player.setChangingAppearance(false);
		byte headGender = packet.readByte();
		byte headType = packet.readByte();
		byte bodyGender = packet.readByte();

		packet.readByte(); // wtf is this?

		int hairColour = packet.readByte();
		int topColour = packet.readByte();
		int trouserColour = packet.readByte();
		int skinColour = packet.readByte();

		int playerMode1 = packet.readByte();
		int playerMode2 = packet.readByte();

		int headSprite = headType + 1;
		int bodySprite = bodyGender + 1;

		PlayerAppearance appearance = new PlayerAppearance(hairColour,
			topColour, trouserColour, skinColour, headSprite, bodySprite);
		if (!appearance.isValid()) {
			player.setSuspiciousPlayer(true, "player invalid appearance");
			return;
		}

		player.setMale(headGender == 1);

		if (player.isMale()) {
			if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
				Item top = player.getCarriedItems().getEquipment().get(1);
				if (top != null && top.getDef(player.getWorld()).isFemaleOnly()) {
					player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, top, UnequipRequest.RequestType.FROM_EQUIPMENT, false));
					ActionSender.sendEquipmentStats(player, 1);
				}
			} else {
				Inventory inv = player.getCarriedItems().getInventory();
				for (int slot = 0; slot < inv.size(); slot++) {
					Item i = inv.get(slot);
					if (i.isWieldable(player.getWorld()) && i.getDef(player.getWorld()).getWieldPosition() == 1
						&& i.isWielded() && i.getDef(player.getWorld()).isFemaleOnly()) {
						player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, i, UnequipRequest.RequestType.FROM_INVENTORY, false));
						ActionSender.sendInventoryUpdateItem(player, slot);
						break;
					}
				}
			}
		}
		int[] oldWorn = player.getWornItems();
		int[] oldAppearance = player.getSettings().getAppearance().getSprites();
		player.getSettings().setAppearance(appearance);
		int[] newAppearance = player.getSettings().getAppearance().getSprites();
		for (int i = 0; i < 12; i++) {
			if (oldWorn[i] == oldAppearance[i]) {
				player.updateWornItems(i, newAppearance[i]);
			}
		}

		if (player.getWorld().getServer().getConfig().CHARACTER_CREATION_MODE == 1) {
			if (player.getLastLogin() == 0L) {
				player.setIronMan(playerMode1);
				player.setOneXp(playerMode2 == 1);
			}
		}
	}
}
