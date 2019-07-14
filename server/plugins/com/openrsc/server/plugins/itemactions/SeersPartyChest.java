package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Random;

public class SeersPartyChest implements InvUseOnObjectExecutiveListener, InvUseOnObjectListener {
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		if(obj.getID() != 18 && obj.getID() != 17) {
			return false;
		}
		if(!obj.getLocation().isInSeersPartyHall()) {
			return false;
		}
		if(item.getDef().isUntradable() && !player.isAdmin()) {
			return false;
		}
		return true;
	}

	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
		if(player.getInventory().remove(item) <= -1) {
			return;
		}

		ActionSender.sendMessage(player, null, 0, MessageType.QUEST, "You place the item into the chest...", 0);
		Server.getServer().getEventHandler().add(new SingleEvent(player, DataConversions.random(0,5000), "Seers Party Hall Drop Delay", true) {
			@Override
			public void action() {
				Random rand = DataConversions.getRandom();
				boolean upstairs = owner.getLocation().isInSeersPartyHallUpstairs();

				while(true) {
					Point location = upstairs ?
						new Point(rand.nextInt(11) + 490, rand.nextInt(8) + 1408) :
						new Point(rand.nextInt(11) + 490, rand.nextInt(8) + 464);

					if ((World.getWorld().getTile(location).traversalMask & 64) != 0) {
						continue;
					}

					World.getWorld().registerItem(new GroundItem(item.getID(), location.getX(), location.getY(), item.getAmount(), (Player) null));
					break;
				}

				for (Player p : World.getWorld().getPlayers()) {
					if((upstairs && p.getLocation().isInSeersPartyHallUpstairs()) || (!upstairs && p.getLocation().isInSeersPartyHallDownstairs())) {
						ActionSender.sendMessage(p, null, 0, MessageType.QUEST, owner.getStaffName() + "@whi@ just dropped: @gre@" + item.getDef().getName() + (item.getAmount() > 1 ? " @whi@(" + DataConversions.numberFormat(item.getAmount()) + ")" : ""), 0);
					}
				}
			}
		});
	}

}
