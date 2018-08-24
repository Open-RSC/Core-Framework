package com.openrsc.server.plugins.misc;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.impl.FireCannonEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class Cannon implements ObjectActionListener,
		ObjectActionExecutiveListener, InvActionListener,
		InvActionExecutiveListener, InvUseOnObjectListener,
		InvUseOnObjectExecutiveListener {
	
	static int[] cannonItemIDs = { 1032, // Cannon Base
			1033, // Cannon Stand
			1034, // Cannon Barrels
			1035 // Cannon Furnace
	};

	public final static int[] cannonObjectIDs = { 946, // Cannon Base
			947, // Cannon Stand
			948, // Cannon Barrels
			943 // Complete Cannon
	};

	
	/***
	 * Cannon cache values:
	 * x, y location.
	 * cannon_stage - 1-4 depending on how many parts have been added.
	 */
	
	public final Item CANNON_BALL = new Item(1041);

	private void pickupBase(Player player, GameObject object) {
		player.message("you pick up the cannon");
		player.message("it's really heavy");
		player.getInventory().add(new Item(cannonItemIDs[0], 1));
		World.getWorld().unregisterGameObject(object);
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
		player.getInventory().add(new Item(cannonItemIDs[0], 1));
		player.getInventory().add(new Item(cannonItemIDs[1], 1));
		
		World.getWorld().unregisterGameObject(object);
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
		player.getInventory().add(new Item(cannonItemIDs[0], 1));
		player.getInventory().add(new Item(cannonItemIDs[1], 1));
		player.getInventory().add(new Item(cannonItemIDs[2], 1));
		
		World.getWorld().unregisterGameObject(object);
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
		player.getInventory().add(new Item(cannonItemIDs[0], 1));
		player.getInventory().add(new Item(cannonItemIDs[1], 1));
		player.getInventory().add(new Item(cannonItemIDs[2], 1));
		player.getInventory().add(new Item(cannonItemIDs[3], 1));
		
		World.getWorld().unregisterGameObject(object);
		if (player.getCache().hasKey("has_cannon")) {
			player.getCache().remove("has_cannon");
			player.getCache().remove("cannon_x");
			player.getCache().remove("cannon_y");
		}
	}
	
	private void handleBase(Player player, Item item) {
		if (item.getDef().getCommand().equalsIgnoreCase("set down")) {
			if (player.getQuestStage(Constants.Quests.DWARF_CANNON) != -1) {
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
			player.message(
					"you place the cannon base on the ground");
			sleep(1500);
			player.getInventory().remove(cannonItemIDs[0], 1);
			
			GameObject cannonBase = new GameObject(player.getLocation(),
					cannonObjectIDs[0], 0, 0, player.getUsername());
			World.getWorld().registerGameObject(cannonBase);
			
			player.getCache().store("has_cannon", true);
			player.getCache().set("cannon_x", cannonBase.getX());
			player.getCache().set("cannon_y", cannonBase.getY());
			player.getCache().set("cannon_stage", 1);
			
			player.setBusy(false);
		}
	}

	private void addCannonStand(Player player, Item item, GameObject object) {
		if (item.getID() == 1033 && object.getID() == 946) {
			player.setBusy(true);
			player.message("you add the stand");
			player.getInventory().remove(cannonItemIDs[1], 1);

			player.getCache().set("cannon_stage", 2);
			World.getWorld().unregisterGameObject(object);
			GameObject cannonStand = new GameObject(object.getLocation(),
					cannonObjectIDs[1], 0, 0, player.getUsername());
			World.getWorld().registerGameObject(cannonStand);
			player.setBusy(false);
		} else {
			player.message("these parts don't seem to fit together");
		}
	}

	private void addCannonBarrels(Player player, Item item, GameObject object) {
		if (item.getID() == 1034 && object.getID() == 947) {
			player.setBusy(true);
			player.message("you add the barrels");
			player.getInventory().remove(cannonItemIDs[2], 1);
			
			World.getWorld().unregisterGameObject(object);
			GameObject cannonBarrels = new GameObject(object.getLocation(),
					cannonObjectIDs[2], 0, 0, player.getUsername());
			World.getWorld().registerGameObject(cannonBarrels);

			player.getCache().set("cannon_stage", 3);
			player.setBusy(false);
		} else {
			player.message("these parts don't seem to fit together");
		}
	}

	private void addCannonFurnace(Player player, Item item, GameObject object) {
		if (item.getID() == 1035 && object.getID() == 948) {
			player.setBusy(true);
			player.message("you add the furnace");
			player.getInventory().remove(cannonItemIDs[3], 1);
			
			World.getWorld().unregisterGameObject(object);
			GameObject cannonFurnace = new GameObject(object.getLocation(),
					cannonObjectIDs[3], 0, 0, player.getUsername());
			World.getWorld().registerGameObject(cannonFurnace);
			player.getCache().set("cannon_stage", 4);
			player.setBusy(false);
		} else {
			player.message("these parts don't seem to fit together");
		}
	}

	private void handleFire(Player p) {
		if (!p.getInventory().contains(CANNON_BALL)) {
			p.message("you're out of ammo");
			return;
		} else if(p.isCannonEventActive()) {
			return;
		}
		FireCannonEvent cannonEvent = new FireCannonEvent(p);
		p.setCannonEvent(cannonEvent);
		Server.getServer().getGameEventHandler().add(cannonEvent);
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == 946) {
			return true;
		}
		if (obj.getID() == 947) {
			return true;
		}
		if (obj.getID() == 948) {
			return true;
		}
		if (obj.getID() == 943 && !command.equalsIgnoreCase("fire")) {
			return true;
		}
		if (obj.getID() == 943 && command.equalsIgnoreCase("fire")) {
			return true;
		}
		return false;
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
			return;
		}
		if (obj.getID() == 946) {
			pickupBase(player, obj);
		}
		if (obj.getID() == 947) {
			pickupStand(player, obj);
		}
		if (obj.getID() == 948) {
			pickupBarrels(player, obj);
		}
		if (obj.getID() == 943 && !command.equalsIgnoreCase("fire")) {
			pickupCannon(player, obj);
		}
		if (obj.getID() == 943 && command.equalsIgnoreCase("fire")) {
			handleFire(player);
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player player) {
		if (item.getID() == 1032) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvAction(Item item, Player player) {
		if (item.getID() == 1032) {
			handleBase(player, item);
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
		if (obj.getID() == 943 && item.getID() == 1041) {
			return true;
		}
		return false;
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
		if (obj.getID() == 943 && item.getID() == 1041) {
			p.message("the cannon loads automatically");
		}
	}
}