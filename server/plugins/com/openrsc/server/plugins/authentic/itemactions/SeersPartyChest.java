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

public class SeersPartyChest implements UseLocTrigger {
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		if(obj.getID() != 18 && obj.getID() != 17) {
			return false;
		}
		if(!obj.getLocation().isInSeersPartyHall()) {
			return false;
		}
		if(item.getDef(player.getWorld()).isUntradable() && !player.isAdmin()) {
			return false;
		}
		return true;
	}

	public void onUseLoc(Player player, GameObject obj, Item item) {
		if(player.getCarriedItems().remove(item) <= -1) {
			return;
		}

		ActionSender.sendMessage(player, null, MessageType.QUEST, "You place the item into the chest...", 0, null);
		final boolean upstairs = player.getLocation().isInSeersPartyHallUpstairs();
		for (Player p : player.getWorld().getPlayers()) {
			if((upstairs && p.getLocation().isInSeersPartyHallUpstairs()) || (!upstairs && p.getLocation().isInSeersPartyHallDownstairs())) {
				ActionSender.sendMessage(p, null, MessageType.QUEST, player.getStaffName() + "@whi@ just dropped: @gre@" + item.getDef(player.getWorld()).getName() + (item.getAmount() > 1 ? " @whi@(" + DataConversions.numberFormat(item.getAmount()) + ")" : ""), 0, null);
			}
		}

		player.getWorld().getServer().getGameEventHandler().add(new SingleEvent(player.getWorld(), player, DataConversions.random(1000,5000), "Seers Party Hall Drop Delay") {
			@Override
			public void action() {
				final Random rand = DataConversions.getRandom();

				while(true) {
					final Point location = upstairs ?
						new Point(rand.nextInt(11) + 490, rand.nextInt(8) + 1408) :
						new Point(rand.nextInt(11) + 490, rand.nextInt(8) + 464);

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
