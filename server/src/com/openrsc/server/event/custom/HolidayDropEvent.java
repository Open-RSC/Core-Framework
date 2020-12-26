package com.openrsc.server.event.custom;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class HolidayDropEvent extends HourlyEvent  {
	private ArrayList<Integer> items;
	private String eventMessage;

	public HolidayDropEvent(final World world, final int lifeTime, final ArrayList<Integer> items) {
		this(world, lifeTime, 0, items, null);
	}

	public HolidayDropEvent(final World world, final int lifeTime, final int minute, final ArrayList<Integer> items) {
		this(world, lifeTime, minute, items, null);
	}

	private HolidayDropEvent(final World world, final int lifeTime, final int minute, final ArrayList<Integer> items, final String eventMessage) {
		super(world, lifeTime, minute,"Holiday Drop Event");
		this.items = items;
		this.eventMessage = eventMessage;
	}

	public void action() {
		int totalItemsDropped = 0;

		for (int y = 96; y < 870; ) { // Highest Y is 867 currently.
			for (int x = 1; x < 770; ) { // Highest X is 766 currently.

				final int traversal = getWorld().getTile(x, y).traversalMask;
				final boolean isBlocking = traversal != 0;

				if (!isBlocking) { // Nothing in the way.
					getWorld().registerItem(new GroundItem(getWorld(), getItems().get(DataConversions.random(0, getItems().size() - 1)), x, y, 1, (Player) null));
					totalItemsDropped++;
				}

				x += DataConversions.random(14, 28); // How much space between each along X axis
			}
			
			y += DataConversions.random(2, 5);
		}

		for (final Player p : getWorld().getPlayers()) {
			if(!p.isAdmin()) {
				continue;
			}

			p.playerServerMessage(MessageType.QUEST, getWorld().getServer().getConfig().MESSAGE_PREFIX + "Dropped " + totalItemsDropped + " of item IDs: " + StringUtils.join(getItems(), ", "));
		}

		if(getEventMessage() != null) {
			for (final Player p : getWorld().getPlayers()) {
				ActionSender.sendMessage(p, null, MessageType.QUEST, getEventMessage(), 0, null);
			}
		}
	}

	public ArrayList<Integer> getItems() {
		return items;
	}

	public static boolean isOccurring(Player player) {
		for (GameTickEvent event : player.getWorld().getServer().getGameEventHandler().getEvents().values()) {
			if (!(event instanceof HolidayDropEvent)) continue;
			return true;
		}
		return false;
	}

	private String getEventMessage() {
		return eventMessage;
	}
}
