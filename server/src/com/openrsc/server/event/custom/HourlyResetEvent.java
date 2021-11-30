package com.openrsc.server.event.custom;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.Region;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static com.openrsc.server.plugins.Functions.changeloc;

public class HourlyResetEvent extends HourlyEvent  {
	private int actionedResets;
	private final String eventMessage;

	public HourlyResetEvent(final World world, final int lifeTime) {
		this(world, lifeTime, 0, null);
	}

	public HourlyResetEvent(final World world, final int lifeTime, final int minute) {
		this(world, lifeTime, minute, null);
	}

	private HourlyResetEvent(final World world, final int lifeTime, final int minute, final String eventMessage) {
		super(world, lifeTime, minute,"Hourly Scenery Reset Event");
		this.eventMessage = eventMessage;
	}

	public void action() {
		actionedResets = 0;
		for (final ConcurrentHashMap<Integer, Region> yRegionList : getWorld().getRegionManager().getRegions().values()) {
			for (final Region region : yRegionList.values()) {
				for (GameObject obj : new ArrayList<>(region.getGameObjects())) {
					if (obj.getType() == 0) {
						// only for scenery
						resetScenery(obj);
					}
				}
			}
		}

		for (final Player p : getWorld().getPlayers()) {
			if(!p.isAdmin()) {
				continue;
			}
			if (getWorld().getServer().getConfig().DEBUG)
				p.playerServerMessage(MessageType.QUEST, getWorld().getServer().getConfig().MESSAGE_PREFIX + "Automatic hourly reset done for " + actionedResets + " sceneries");
		}
	}

	private void resetScenery(GameObject gameObject) {
		Point objectCoordinates = Point.location(gameObject.getLoc().getX(), gameObject.getLoc().getY());
		final int initialObjectID = getWorld().getSceneryLoc(objectCoordinates);
		if (initialObjectID != gameObject.getID()) {
			if (initialObjectID != -1) {
				// world object from initial json
				final GameObject replaceObj = new GameObject(gameObject.getWorld(), gameObject.getLocation(), initialObjectID, gameObject.getDirection(), gameObject.getType());
				changeloc(gameObject, replaceObj);
			} else {
				// dynamic stuck object
				// unregister
				getWorld().unregisterGameObject(gameObject);
			}
			++actionedResets;
		}
	}

	public static boolean isOccurring(Player player) {
		for (GameTickEvent event : player.getWorld().getServer().getGameEventHandler().getEvents()) {
			if (!(event instanceof HourlyResetEvent)) continue;
			return true;
		}
		return false;
	}

	private String getEventMessage() {
		return eventMessage;
	}
}
