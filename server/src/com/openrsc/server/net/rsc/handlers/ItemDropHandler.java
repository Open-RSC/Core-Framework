package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Server;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.GenericLog;
import com.openrsc.server.util.rsc.DataConversions;

public final class ItemDropHandler implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		if (player.getStatus() == Action.DROPPING_GITEM) {
			return;
		}
		player.resetAll();
		final int idx = (int) p.readShort();
		int amount = p.readInt();

		if (idx < 0 || idx >= player.getInventory().size()) {
			player.setSuspiciousPlayer(true);
			return;
		}
		final Item item = player.getInventory().get(idx);

		if (item == null) {
			player.setSuspiciousPlayer(true);
			return;
		}
		if (amount <= 0) {
			return;
		}

		if (item.getDef().isStackable()) {
			if (amount > item.getAmount()) {
				amount = item.getAmount();
			}
		} else {
			if (amount > player.getInventory().countId(item.getID())) {
				amount = player.getInventory().countId(item.getID());
			}
		}

		final int finalAmount = amount;

		Server.getServer().getEventHandler().add(new DelayedEvent(player, 0, "Player Drop Item") {
			int dropTickCount = 0;

			@Override
			public void run() {
				setDelay(500);
				if (dropTickCount > 20) {
					dropTickCount = 0;
					stop();
					return;
				}
				dropTickCount++;
				if (owner.finishedPath()) {
					stop();
					if (item.getDef().isStackable()) {
						dropStackable(player, item, finalAmount);
					} else {
						dropUnstackable(player, item, finalAmount);
					}
				}
			}
		});

	}

	public void dropStackable(final Player player, final Item item, final int amount) {
		if (!item.getDef().isStackable()) {
			throw new IllegalArgumentException("Item must be stackable when passed on to dropStackable()");
		}

		player.setStatus(Action.DROPPING_GITEM);

		if (!player.getInventory().contains(item) || player.getStatus() != Action.DROPPING_GITEM) {
			player.setStatus(Action.IDLE);
			return;
		}
		if (PluginHandler.getPluginHandler().blockDefaultAction("Drop", new Object[]{player, item})) {
			return;
		}
		if (player.getInventory().remove(item.getID(), amount) > -1) {
			GroundItem groundItem = new GroundItem(item.getID(), player.getX(), player.getY(), amount,
				player);
			ActionSender.sendSound(player, "dropobject");
			World.getWorld().registerItem(groundItem, 188000);
			GameLogging.addQuery(new GenericLog(player.getUsername() + " dropped " + item.getDef().getName() + " x"
				+ DataConversions.numberFormat(groundItem.getAmount()) + " at " + player.getLocation().toString()));
			player.setStatus(Action.IDLE);
		}
	}

	public void dropUnstackable(final Player player, final Item item, final int amount) {
		if (item.getDef().isStackable()) {
			throw new IllegalArgumentException("Item must be unstackable when passed on to dropUnstackable()");
		}

		player.setStatus(Action.DROPPING_GITEM);
		Server.getServer().getEventHandler().add(new DelayedEvent(player, 500, "Player Drop Unstackable") {
			int dropCount = 0;

			public void run() {
				if (!owner.getInventory().contains(item) || owner.getStatus() != Action.DROPPING_GITEM) {
					matchRunning = false;
					player.setStatus(Action.IDLE);
					return;
				}
				if (owner.hasMoved()) {
					this.stop();
					player.setStatus(Action.IDLE);
					return;
				}
				if (dropCount >= amount) {
					matchRunning = false;
					player.setStatus(Action.IDLE);
					return;
				}
				if (!player.getInventory().hasItemId(item.getID())) {
					player.message("You don't have the entered amount to drop");
					matchRunning = false;
					player.setStatus(Action.IDLE);
					return;
				}
				ActionSender.sendSound(owner, "dropobject");
				if (PluginHandler.getPluginHandler().blockDefaultAction("Drop", new Object[]{player, item})) {
					stop();
					player.setStatus(Action.IDLE);
					return;
				}
				if (owner.getInventory().remove(item) > -1) {
					GroundItem groundItem = new GroundItem(item.getID(), owner.getX(), owner.getY(), amount,
						owner);
					World.getWorld().registerItem(groundItem, 188000);
					GameLogging.addQuery(new GenericLog(owner.getUsername() + " dropped " + item.getDef().getName()
						+ " at " + owner.getLocation().toString()));
					dropCount++;
					if (amount > 1)
						player.message("Dropped " + dropCount + "/" + amount);
				}

			}
		});
	}
}
