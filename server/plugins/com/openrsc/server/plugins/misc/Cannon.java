package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.rsc.impl.FireCannonEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.sleep;

public class Cannon implements ObjectActionListener,
	ObjectActionExecutiveListener, InvActionListener,
	InvActionExecutiveListener, InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener {

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
		player.getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id(), 1));
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
		player.getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id(), 1));
		player.getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id(), 1));

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
		player.getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id(), 1));
		player.getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id(), 1));
		player.getInventory().add(new Item(ItemId.DWARF_CANNON_BARRELS.id(), 1));

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
		player.getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id(), 1));
		player.getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id(), 1));
		player.getInventory().add(new Item(ItemId.DWARF_CANNON_BARRELS.id(), 1));
		player.getInventory().add(new Item(ItemId.DWARF_CANNON_FURNACE.id(), 1));

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
				sleep(1500);
				player.message("...you need to complete the dwarf cannon quest");
				return;
			}
			if (player.getLocation().inDwarfArea()) {
				player.message("it is not permitted to set up a cannon...");
				sleep(1500);
				player.message("...this close to the dwarf black guard");
				return;
			}
			if (player.getCache().hasKey("has_cannon")) {
				player.message("you cannot construct more than one cannon at a time...");
				sleep(1500);
				player.message("if you have lost your cannon ...");
				sleep(1500);
				player.message("...go and see the dwarf cannon engineer");
				return;
			}
			if (player.getViewArea().getGameObject(player.getLocation()) != null) {
				player.message("you can't set up the cannon here");
				return;
			}
			player.resetPath();
			player.setBusy(true);
			player.message("you place the cannon base on the ground");
			sleep(1500);
			player.getInventory().remove(ItemId.DWARF_CANNON_BASE.id(), 1);

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

			player.setBusy(false);
		}
	}

	private void addCannonStand(Player player, Item item, GameObject object) {
		if (item.getID() == ItemId.DWARF_CANNON_STAND.id() && object.getID() == 946) {
			player.setBusy(true);
			player.message("you add the stand");
			player.getInventory().remove(ItemId.DWARF_CANNON_STAND.id(), 1);

			player.getCache().set("cannon_stage", 2);
			player.getWorld().unregisterGameObject(object);
			GameObject cannonStand = new GameObject(player.getWorld(), object.getLocation(),
				cannonObjectIDs[1], 0, 0, player.getUsername());
			player.getWorld().registerGameObject(cannonStand);
			player.setBusy(false);
		} else {
			player.message("these parts don't seem to fit together");
		}
	}

	private void addCannonBarrels(Player player, Item item, GameObject object) {
		if (item.getID() == ItemId.DWARF_CANNON_BARRELS.id() && object.getID() == 947) {
			player.setBusy(true);
			player.message("you add the barrels");
			player.getInventory().remove(ItemId.DWARF_CANNON_BARRELS.id(), 1);

			player.getWorld().unregisterGameObject(object);
			GameObject cannonBarrels = new GameObject(player.getWorld(), object.getLocation(),
				cannonObjectIDs[2], 0, 0, player.getUsername());
			player.getWorld().registerGameObject(cannonBarrels);

			player.getCache().set("cannon_stage", 3);
			player.setBusy(false);
		} else {
			player.message("these parts don't seem to fit together");
		}
	}

	private void addCannonFurnace(Player player, Item item, GameObject object) {
		if (item.getID() == ItemId.DWARF_CANNON_FURNACE.id() && object.getID() == 948) {
			player.setBusy(true);
			player.message("you add the furnace");
			player.getInventory().remove(ItemId.DWARF_CANNON_FURNACE.id(), 1);

			player.getWorld().unregisterGameObject(object);
			GameObject cannonFurnace = new GameObject(player.getWorld(), object.getLocation(),
				cannonObjectIDs[3], 0, 0, player.getUsername());
			player.getWorld().registerGameObject(cannonFurnace);
			player.getCache().set("cannon_stage", 4);
			player.setBusy(false);
		} else {
			player.message("these parts don't seem to fit together");
		}
	}

	private void handleFire(Player p) {
		if (!p.getInventory().contains(new Item(ItemId.MULTI_CANNON_BALL.id()))) {
			p.message("you're out of ammo");
			return;
		} else if (p.isCannonEventActive()) {
			return;
		}
		FireCannonEvent cannonEvent = new FireCannonEvent(p.getWorld(), p);
		p.setCannonEvent(cannonEvent);
		p.getWorld().getServer().getGameEventHandler().add(cannonEvent);
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
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
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (inArray(obj.getID(), 946, 947, 948, 943) && !obj.getOwner().equals(player.getUsername())) {
			if (!command.equalsIgnoreCase("fire")) {
				player.message("you can't pick that up, the owners still around");
			} else {
				player.message("you can't fire this cannon...");
				sleep(1500);
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
	public boolean blockInvAction(Item item, Player player, String command) {
		return item.getID() == ItemId.DWARF_CANNON_BASE.id();
	}

	@Override
	public void onInvAction(Item item, Player player, String command) {
		if (item.getID() == ItemId.DWARF_CANNON_BASE.id()) {
			handleBase(player, item, command);
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 946) {
			return true;
		}
		if (obj.getID() == 947) {
			return true;
		}
		if (obj.getID() == 948) {
			return true;
		}
		return obj.getID() == 943 && item.getID() == ItemId.MULTI_CANNON_BALL.id();
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 946) {
			if (!obj.getOwner().equals(p.getUsername())) {
				p.message("you can only add this stand to your own base");
				return;
			}
			addCannonStand(p, item, obj);
		}
		if (obj.getID() == 947) {
			if (!obj.getOwner().equals(p.getUsername())) {
				p.message("you can only add the barrels to your own cannon");
				return;
			}
			addCannonBarrels(p, item, obj);
		}
		if (obj.getID() == 948) {
			if (!obj.getOwner().equals(p.getUsername())) {
				p.message("you can only add the furnace to your own cannon");
				return;
			}
			addCannonFurnace(p, item, obj);
		}
		if (obj.getID() == 943 && item.getID() == ItemId.MULTI_CANNON_BALL.id()) {
			p.message("the cannon loads automatically");
		}
	}
}
