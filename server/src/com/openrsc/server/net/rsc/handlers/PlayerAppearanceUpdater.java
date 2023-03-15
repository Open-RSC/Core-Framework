package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.content.PlayerClass;
import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.PlayerAppearanceStruct;

public class PlayerAppearanceUpdater implements PayloadProcessor<PlayerAppearanceStruct, OpcodeIn> {

	public void process(PlayerAppearanceStruct payload, Player player) throws Exception {

		if (!player.isChangingAppearance()) {
			player.setSuspiciousPlayer(true, "player appearance packet without changing appearance");
			return;
		}

		byte headRestrictions = payload.headRestrictions;
		byte headType = payload.headType;
		byte bodyType = payload.bodyType;

		// Check to see if we've been sent a bearded lady
		if (!player.getConfig().ALLOW_BEARDED_LADIES
			&& (headType == 6 && bodyType == 4)) {
			player.setSuspiciousPlayer(true, "player attempted to create a bearded lady");
			ActionSender.sendAppearanceScreen(player);
			return;
		}

		boolean tutorialAppearance = player.getCache().hasKey("tutorial_appearance");
		if (tutorialAppearance)
			player.getCache().remove("tutorial_appearance");

		player.setChangingAppearance(false);

		// This value is always "2" and is not very useful.
		// I looked in the  v40 client deob, and the 4th byte is also always 2 there.
		// I looked in the v127 client deob, and the 4th byte is also always 2 there.
		// I looked in the v204 client deob, and the 4th byte is also always 2 there.
		// I looked in the v233 client deob, and the 4th byte is also always 2 there.
		byte mustEqual2 = payload.mustEqual2;
		if (mustEqual2 != 2) {
			player.setSuspiciousPlayer(true, "4th byte of player appearance packet wasn't equal to 2");
			return;
		}

		int hairColour = payload.hairColour;
		int topColour = payload.topColour;
		int trouserColour = payload.trouserColour;
		int skinColour = payload.skinColour;
		int ironmanMode = payload.ironmanMode; // custom protocol
		int isOneXp = payload.isOneXp; // custom protocol

		int headSprite = headType + 1;
		int bodySprite = bodyType + 1;

		PlayerAppearance appearance = new PlayerAppearance(hairColour,
			topColour, trouserColour, skinColour, headSprite, bodySprite);
		if (!appearance.isValid(player)) {
			player.setSuspiciousPlayer(true, "player invalid appearance");
			return;
		}

		player.setMale(headRestrictions == 1);

		if (player.isMale()) {
			if (player.getConfig().WANT_EQUIPMENT_TAB) {
				Item top = player.getCarriedItems().getEquipment().get(1);
				if (top != null && top.getDef(player.getWorld()).isFemaleOnly()) {
					if(!player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, top, UnequipRequest.RequestType.FROM_EQUIPMENT, false))) {
						player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, top, UnequipRequest.RequestType.FROM_BANK, false));
					}
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

		if (player.getLastLogin() == 0L || tutorialAppearance) {
			if (player.getConfig().USES_CLASSES) {
				new PlayerClass(player, payload.chosenClass).init();
				player.getWorld().getServer().getPlayerService().savePlayerMaxStats(player);
			}

			if (player.getConfig().USES_PK_MODE) {
				player.setPkMode(payload.pkMode);
				player.setPkChanges(2);
				ActionSender.sendGameSettings(player);
			}

			if (player.getConfig().CHARACTER_CREATION_MODE == 1) {
				player.setIronMan(ironmanMode);
				player.setOneXp(isOneXp == 1);
			}
		}
	}
}
