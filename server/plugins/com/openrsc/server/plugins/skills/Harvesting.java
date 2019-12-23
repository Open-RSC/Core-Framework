package com.openrsc.server.plugins.skills;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

public final class Harvesting implements ObjectActionListener,
	ObjectActionExecutiveListener {

	@Override
	public void onObjectAction(final GameObject object, String command,
							   Player player) {
		int retrytimes;
		// Harvest of Xmas Tree
		if (object.getID() == 1238) {
			player.playerServerMessage(MessageType.QUEST, "You attempt to grab a present...");
			retrytimes = 10;
			player.setBatchEvent(new BatchEvent(player.getWorld(), player, 1800, "Harvesting Xmas", retrytimes, true) {
				@Override
				public void action() {
					final Item present = new Item(ItemId.PRESENT.id());
					if (getProduce(1, 1)) {
						//check if the tree still has gifts
						GameObject obj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
						if (obj == null) {
							getOwner().playerServerMessage(MessageType.QUEST, "You fail to take from the tree");
							interrupt();
						} else {
							getOwner().getInventory().add(present);
							getOwner().playerServerMessage(MessageType.QUEST, "You get a nice looking present");
						}
						if (DataConversions.random(1, 1000) <= 3) {
							obj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
							int depletedId = 1239;
							interrupt();
							if (obj != null && obj.getID() == object.getID()) {
								GameObject newObject = new GameObject(getWorld(), object.getLocation(), depletedId, object.getDirection(), object.getType());
								getWorld().replaceGameObject(object, newObject);
								getWorld().delayedSpawnObject(obj.getLoc(), 600 * 1000);
							}
						}
					} else {
						getOwner().playerServerMessage(MessageType.QUEST, "You fail to take from the tree");
						if (getRepeatFor() > 1) {
							GameObject checkObj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
							if (checkObj == null) {
								interrupt();
							}
						}
					}
					if (!isCompleted()) {
						getOwner().playerServerMessage(MessageType.QUEST, "You attempt to grab a present...");
					}

				}
			});
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return command.equals("pluck") || (command.equals("collect") && obj.getID() == 1238);
	}

	private boolean getProduce(int reqLevel, int harvestingLevel) {
		return Formulae.calcGatheringSuccessful(reqLevel, harvestingLevel, 0);
	}
}
