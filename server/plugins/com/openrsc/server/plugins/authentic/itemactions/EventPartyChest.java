package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Random;

public class EventPartyChest implements UseLocTrigger {
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		if (obj != player.getWorld().eventChest) {
			return false;
		}

		if (obj.getID() != 257 && obj.getID() != 247) {
			return false;
		}

		if (item.getDef(player.getWorld()).isUntradable() && !player.isAdmin()) {
			return false;
		}

		return true;
	}

	public boolean isWithinChestNotificationRange(Player player, GameObject chest, Point p) {
		return chest.getLocation().getX() + player.getWorld().eventChestRadius * 2 >= p.getX() &&
		chest.getLocation().getX() - player.getWorld().eventChestRadius * 2 <= p.getX() &&
		chest.getLocation().getY() + player.getWorld().eventChestRadius * 2 >= p.getY() &&
		chest.getLocation().getY() - player.getWorld().eventChestRadius * 2 <= p.getY();
	}

	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (player.getCarriedItems().remove(item) <= -1) {
			return;
		}

		try {
			ActionSender.sendMessage(player, null, MessageType.QUEST, "You place the item into the chest...", 0, null);
			for (Player p : player.getWorld().getPlayers()) {
				if (isWithinChestNotificationRange(player, obj, p.getLocation())) {
					ActionSender.sendMessage(p, null, MessageType.QUEST, player.getStaffName() + "@whi@ just dropped: @gre@" + item.getDef(player.getWorld()).getName() + (item.getAmount() > 1 ? " @whi@(" + DataConversions.numberFormat(item.getAmount()) + ")" : ""), 0, null);
				}
			}
		} catch (Exception e) {}

		player.getWorld().getServer().getGameEventHandler().add(new SingleEvent(player.getWorld(), player, DataConversions.random(1000,5000), "Event Chest Drop Delay") {
			@Override
			public void action() {
				final Random rand = DataConversions.getRandom();

				while (true) {
					final Point location = new Point(obj.getLocation().getX() + rand.nextInt(player.getWorld().eventChestRadius), obj.getLocation().getY() + rand.nextInt(player.getWorld().eventChestRadius));
					if ((getOwner().getWorld().getTile(location).traversalMask & 64) != 0) {
						continue;
					}

					getOwner().getWorld().registerItem(new GroundItem(getOwner().getWorld(), item.getCatalogId(), location.getX(), location.getY(), item.getAmount(), null, item.getNoted()));
					break;
				}
			}
		});
	}
}
