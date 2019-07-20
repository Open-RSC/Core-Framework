package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;

public final class ItemWieldHandler implements PacketHandler {

	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {
		int pID = p.getID();
		int packetOne = OpcodeIn.ITEM_EQUIP.getOpcode();
		int packetTwo = OpcodeIn.ITEM_REMOVE_EQUIPPED.getOpcode();

		if (player.isBusy() && !player.inCombat()) {
			return;
		}

		if (player.getDuel().isDuelActive() && player.getDuel().getDuelSetting(3)) {
			player.message("No extra items may be worn during this duel!");
			return;
		}

		player.resetAllExceptDueling();
		int idx = (int) p.readShort();

		Item item = null;

		if (p.getID() == packetTwo && Constants.GameServer.WANT_EQUIPMENT_TAB) {
			for (Item loop : player.getEquipment().list) {
				if (loop != null && loop.getID() == idx) {
					item = loop;
					item.setWielded(true);
					break;
				}
			}
		} else {
			if (idx < 0 || idx >= 30) {
				player.setSuspiciousPlayer(true);
				return;
			}
			item = player.getInventory().get(idx);
		}

		if (item == null || !item.isWieldable()) {
			player.setSuspiciousPlayer(true);
			return;
		}
		/*if (!player.getLocation().isMembersWild() && item.getDef().isMembersOnly()) { //Not authentic behavior
			player.message("Members objects can only be wield above the P2P Gate in wild: " + World.membersWildStart + " - "
						+ World.membersWildMax);
			return;
		}*/
		if (!Constants.GameServer.MEMBER_WORLD && item.getDef().isMembersOnly()) {
			player.message("You need to be a member to use this object");
			return;
		}
		if (pID == packetOne) {
			if (!item.isWielded()) {
				if (PluginHandler.getPluginHandler().blockDefaultAction(
					"Wield", new Object[]{player, item})) {
					return;
				}
				player.getInventory().wieldItem(item, true);
			}
		} else if (pID == packetTwo) {
			if (item.isWielded()) {
				if (PluginHandler.getPluginHandler().blockDefaultAction(
					"UnWield", new Object[]{player, item}))
					return;
				player.getInventory().unwieldItem(item, true);
			}
		}
	}
}
