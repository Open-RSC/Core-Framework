package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.impl.projectile.FireCannonEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Cannon implements OpLocTrigger,
	OpInvTrigger,
	UseLocTrigger {

	public final static int[] cannonObjectIDs = {
		946, // Cannon Base
		947, // Cannon Stand
		948, // Cannon Barrels
		943 // Complete Cannon
	};

	/***
	 * Cannon cache values:
	 * x, y location.
	 * cannon_stage - 1-4 depending on how many parts have been added.
	 */

	private void pickupBase(Player player, GameObject object) {
		player.message("you pick up the cannon");
		player.message("it's really heavy");
		player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id(), 1));
		player.getWorld().unregisterGameObject(object);
		if (player.getCache().hasKey("has_cannon")) {
			player.getCache().remove("has_cannon");
			player.getCache().remove("cannon_stage");
			player.getCache().remove("cannon_x");
			player.getCache().remove("cannon_y");
		}
	}

	private void pickupStand(Player player, GameObject object) {
		player.message("you pick up the cannon");
		player.message("it's really heavy");
		player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id(), 1));
		player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id(), 1));

		player.getWorld().unregisterGameObject(object);
		if (player.getCache().hasKey("has_cannon")) {
			player.getCache().remove("has_cannon");
			player.getCache().remove("cannon_stage");
			player.getCache().remove("cannon_x");
			player.getCache().remove("cannon_y");
		}
	}

	private void pickupBarrels(Player player, GameObject object) {
		player.message("you pick up the cannon base");
		player.message("it's really heavy");
		player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id(), 1));
		player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id(), 1));
		player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BARRELS.id(), 1));

		player.getWorld().unregisterGameObject(object);
		if (player.getCache().hasKey("has_cannon")) {
			player.getCache().remove("has_cannon");
			player.getCache().remove("cannon_stage");
			player.getCache().remove("cannon_x");
			player.getCache().remove("cannon_y");
		}
	}

	private void pickupCannon(Player player, GameObject object) {
		player.message("you pick up the cannon");
		player.message("it's really heavy");
		player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id(), 1));
		player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id(), 1));
		player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BARRELS.id(), 1));
		player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_FURNACE.id(), 1));

		player.getWorld().unregisterGameObject(object);
		if (player.getCache().hasKey("has_cannon")) {
			player.getCache().remove("has_cannon");
			player.getCache().remove("cannon_x");
			player.getCache().remove("cannon_y");
		}
	}

	private void handleBase(Player player, Item item, String command) {
		if (command.equalsIgnoreCase("set down")) {
			if (player.getQuestStage(Quests.DWARF_CANNON) != -1) {
				player.message("you can't set up this cannon...");
				delay(3);
				player.message("...you need to complete the dwarf cannon quest");
				return;
			}
			if (player.getLocation().inDwarfArea()) {
				player.message("it is not permitted to set up a cannon...");
				delay(3);
				player.message("...this close to the dwarf black guard");
				return;
			}
			if (player.getCache().hasKey("has_cannon")) {
				player.message("you cannot construct more than one cannon at a time...");
				delay(3);
				player.message("if you have lost your cannon ...");
				delay(3);
				player.message("...go and see the dwarf cannon engineer");
				return;
			}
			if (player.getViewArea().getGameObject(player.getLocation()) != null) {
				player.message("you can't set up the cannon here");
				return;
			}

			// no cannon in KBD lair; most likely authentic, but no direct proof.
			if (player.getLocation().inBounds(562,3314,572,3332)) {
				player.message("you can't set up the cannon here");
				return;
			}

			player.resetPath();
			player.message("you place the cannon base on the ground");
			delay(3);
			if (player.getCarriedItems().remove(new Item(ItemId.DWARF_CANNON_BASE.id())) == -1) return;

			GameObject cannonBase = new GameObject(
				player.getWorld(),
				player.getLocation(),
				cannonObjectIDs[0],
				0,
				0,
				player.getUsername()
			);
			player.getWorld().registerGameObject(cannonBase);

			player.getCache().store("has_cannon", true);
			player.getCache().set("cannon_x", cannonBase.getX());
			player.getCache().set("cannon_y", cannonBase.getY());
			player.getCache().set("cannon_stage", 1);
		}
	}

	private void addCannonStand(Player player, Item item, GameObject object) {
		if (item.getCatalogId() == ItemId.DWARF_CANNON_STAND.id() && object.getID() == 946) {
			if (player.getCarriedItems().remove(new Item(ItemId.DWARF_CANNON_STAND.id())) == -1) return;
			player.message("you add the stand");

			player.getCache().set("cannon_stage", 2);
			player.getWorld().unregisterGameObject(object);
			GameObject cannonStand = new GameObject(player.getWorld(), object.getLocation(),
				cannonObjectIDs[1], 0, 0, player.getUsername());
			player.getWorld().registerGameObject(cannonStand);
		} else {
			player.message("these parts don't seem to fit together");
		}
	}

	private void addCannonBarrels(Player player, Item item, GameObject object) {
		if (item.getCatalogId() == ItemId.DWARF_CANNON_BARRELS.id() && object.getID() == 947) {
			if (player.getCarriedItems().remove(new Item(ItemId.DWARF_CANNON_BARRELS.id())) == -1) return;
			player.message("you add the barrels");

			player.getWorld().unregisterGameObject(object);
			GameObject cannonBarrels = new GameObject(player.getWorld(), object.getLocation(),
				cannonObjectIDs[2], 0, 0, player.getUsername());
			player.getWorld().registerGameObject(cannonBarrels);

			player.getCache().set("cannon_stage", 3);
		} else {
			player.message("these parts don't seem to fit together");
		}
	}

	private void addCannonFurnace(Player player, Item item, GameObject object) {
		if (item.getCatalogId() == ItemId.DWARF_CANNON_FURNACE.id() && object.getID() == 948) {
			if (player.getCarriedItems().remove(new Item(ItemId.DWARF_CANNON_FURNACE.id())) == -1) return;
			player.message("you add the furnace");

			player.getWorld().unregisterGameObject(object);
			GameObject cannonFurnace = new GameObject(player.getWorld(), object.getLocation(),
				cannonObjectIDs[3], 0, 0, player.getUsername());
			player.getWorld().registerGameObject(cannonFurnace);
			player.getCache().set("cannon_stage", 4);
		} else {
			player.message("these parts don't seem to fit together");
		}
	}

	private void handleFire(Player player) {
		if (!player.getCarriedItems().getInventory().contains(new Item(ItemId.MULTI_CANNON_BALL.id()))) {
			player.message("you're out of ammo");
			return;
		} else if (player.isCannonEventActive()) {
			return;
		}
		FireCannonEvent cannonEvent = new FireCannonEvent(player.getWorld(), player);
		player.setCannonEvent(cannonEvent);
		player.getWorld().getServer().getGameEventHandler().add(cannonEvent);
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 943 && !command.equalsIgnoreCase("fire")) {
			return true;
		} else if (obj.getID() == 943 && command.equalsIgnoreCase("fire")) {
			return true;
		} else if (obj.getID() == 946) {
			return true;
		} else if (obj.getID() == 947) {
			return true;
		} else return obj.getID() == 948;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (inArray(obj.getID(), 946, 947, 948, 943) && !obj.getOwner().equals(player.getUsername())) {
			if (!command.equalsIgnoreCase("fire")) {
				player.message("you can't pick that up, the owners still around");
			} else {
				player.message("you can't fire this cannon...");
				delay(3);
				player.message("...it doesn't belong to you");
			}
		} else if (!command.equalsIgnoreCase("fire") && player.getFatigue() >= player.MAX_FATIGUE)
			player.message("you arms are too tired to pick it up");
		else if (obj.getID() == 946)
			pickupBase(player, obj);
		else if (obj.getID() == 947)
			pickupStand(player, obj);
		else if (obj.getID() == 948)
			pickupBarrels(player, obj);
		else if (obj.getID() == 943 && !command.equalsIgnoreCase("fire"))
			pickupCannon(player, obj);
		else if (obj.getID() == 943 && command.equalsIgnoreCase("fire"))
			handleFire(player);
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.DWARF_CANNON_BASE.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.DWARF_CANNON_BASE.id()) {
			handleBase(player, item, command);
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == 946) {
			return true;
		}
		if (obj.getID() == 947) {
			return true;
		}
		if (obj.getID() == 948) {
			return true;
		}
		return obj.getID() == 943 && item.getCatalogId() == ItemId.MULTI_CANNON_BALL.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == 946) {
			if (!obj.getOwner().equals(player.getUsername())) {
				player.message("you can only add this stand to your own base");
				return;
			}
			addCannonStand(player, item, obj);
		}
		if (obj.getID() == 947) {
			if (!obj.getOwner().equals(player.getUsername())) {
				player.message("you can only add the barrels to your own cannon");
				return;
			}
			addCannonBarrels(player, item, obj);
		}
		if (obj.getID() == 948) {
			if (!obj.getOwner().equals(player.getUsername())) {
				player.message("you can only add the furnace to your own cannon");
				return;
			}
			addCannonFurnace(player, item, obj);
		}
		if (obj.getID() == 943 && item.getCatalogId() == ItemId.MULTI_CANNON_BALL.id()) {
			player.message("the cannon loads automatically");
		}
	}
}
