package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

public final class ItemWieldHandler implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {
		int pID = p.getID();
		int packetOne = OpcodeIn.ITEM_EQUIP.getOpcode();
		int packetTwo = OpcodeIn.ITEM_REMOVE_EQUIPPED.getOpcode();
		int packetThree = OpcodeIn.ITEM_EQUIP_FROM_BANK.getOpcode();
		int packetFour = OpcodeIn.ITEM_REMOVE_TO_BANK.getOpcode();

		if (player.isBusy() && !player.inCombat()) {
			return;
		}

		if (player.getDuel().isDuelActive() && player.getDuel().getDuelSetting(3)) {
			player.message("No extra items may be worn during this duel!");
			return;
		}

		if (pID == packetOne || pID == packetTwo)
			player.resetAllExceptDueling();
		else if (pID == packetThree || pID == packetFour)
			player.resetAllExceptBank();
		int idx = (int) p.readShort();

		Item item = null;

		if (pID == packetTwo && player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item loop;
			for (int i = 0; i < Equipment.slots; i++) {
				loop = player.getEquipment().get(i);
				if (loop != null && loop.getID() == idx) {
					item = loop;
					item.setWielded(true);
					break;
				}
			}
		} else if (pID != packetThree && p.getID() != packetFour){
			if (idx < 0 || idx >= 30) {
				player.setSuspiciousPlayer(true, "idx < 0 or idx >= 30");
				return;
			}
			item = player.getInventory().get(idx);
		} else if (pID == packetThree) {
			item = player.getBank().get(idx);
		} else if (pID == packetFour) {
			int wieldPos = player.getWorld().getServer().getEntityHandler().getItemDef(idx).getWieldPosition();
			item = player.getEquipment().get(wieldPos);
			if (item.getID() != idx)
				item = null;


		}

		if (item == null || !item.isWieldable(player.getWorld())) {
			player.setSuspiciousPlayer(true, "item null or not wieldable");
			return;
		}
		/*if (!player.getLocation().isMembersWild() && item.getDef().isMembersOnly()) { //Not authentic behavior
			player.message("Members objects can only be wield above the P2P Gate in wild: " + World.membersWildStart + " - "
						+ World.membersWildMax);
			return;
		}*/
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD && item.getDef(player.getWorld()).isMembersOnly()) {
			player.message("You need to be a member to use this object");
			return;
		}
		if (pID == packetOne) {
			if (!item.isWielded()) {
				if (player.getWorld().getServer().getPluginHandler().blockDefaultAction(
					"Wield", new Object[]{player, item})) {
					return;
				}
				player.getInventory().wieldItem(item, true);
			}
		} else if (pID == packetTwo) {
			if (item.isWielded()) {
				if (player.getWorld().getServer().getPluginHandler().blockDefaultAction(
					"UnWield", new Object[]{player, item}))
					return;
				player.getInventory().unwieldItem(item, true);
			}
		} else if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && pID == packetThree) {
			if (player.getWorld().getServer().getPluginHandler().blockDefaultAction(
				"Wield", new Object[]{player, item})) {
				return;
			}
			player.getBank().wieldItem(idx, true);
			ActionSender.showBank(player);
		} else if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && pID == packetFour) {
			if (player.getWorld().getServer().getPluginHandler().blockDefaultAction(
				"UnWield", new Object[]{player, item}))
				return;
			player.getBank().unwieldItem(item, true);
			ActionSender.showBank(player);
			ActionSender.sendEquipmentStats(player, item.getDef(player.getWorld()).getWieldPosition());
		}
	}
}
