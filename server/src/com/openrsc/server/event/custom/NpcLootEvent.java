package com.openrsc.server.event.custom;

import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.util.rsc.DataConversions;

/****
 * Author: Kenix
 */

public class NpcLootEvent extends SingleEvent {
	private Point location;
	private int npcId;
	private int npcAmount;
	private int itemId;
	private int itemAmount;
	private int npcLifetime;
	private Npc lootNpc;

	public NpcLootEvent(Point location, int npcId, int npcAmount, int itemId) {
		this(location, npcId, npcAmount, itemId, 1, 10);
	}

	public NpcLootEvent(Point location, int npcId, int npcAmount, int itemId, int itemAmount) {
		this(location, npcId, npcAmount, itemId, itemAmount, 10);
	}

	public NpcLootEvent(Point location, int npcId, int npcAmount, int itemId, int itemAmount, int npcLifeTime) {
		super(null, 0, "NPC Loot Event");
		this.location = location;
		this.npcId = npcId;
		this.npcAmount = npcAmount;
		this.itemId = itemId;
		this.itemAmount = itemAmount;
		this.npcLifetime = npcLifeTime;
	}

	// TODO: Maybe should implement listener method from PlayerKilledNpcListener
	public void onLootNpcDeath(Player p, Npc n) {
		if(!n.equals(lootNpc)) {
			return;
		}

		String npcName  = n.getDef().getName();
		ItemDefinition itemDef = EntityHandler.getItemDef(itemId);

		if(itemDef.isStackable()) {
			World.getWorld().registerItem(new GroundItem(itemId, n.getX(), n.getY(), itemAmount, p));
		} else {
			for (int i = 0; i < itemAmount; i++) {
				World.getWorld().registerItem(new GroundItem(itemId, n.getX(), n.getY(), itemAmount, p));
			}
		}

		for (Player informee : World.getWorld().getPlayers())
			informee.message(CommandListener.messagePrefix + p.getUsername() + " has killed the special " + npcName + " and won: " + itemDef.getName() +  " x" + itemAmount);

		ActionSender.sendBox(p, "You have killed the special " + npcName + "! % Remember to loot your winnings of " + itemAmount + " " + itemDef.getName(),false);
		lootNpc = null;
		stop();
	}

	public void onLootNpcDeath(Npc n, Npc n2) {
		if(!n.equals(lootNpc)) {
			return;
		}

		String npcName  = n.getDef().getName();
		ItemDefinition itemDef = EntityHandler.getItemDef(itemId);

		if(itemDef.isStackable()) {
			World.getWorld().registerItem(new GroundItem(itemId, n.getX(), n.getY(), itemAmount, n2));
		} else {
			for (int i = 0; i < itemAmount; i++) {
				World.getWorld().registerItem(new GroundItem(itemId, n.getX(), n.getY(), itemAmount, n2));
			}
		}

		lootNpc = null;
		stop();
	}

	public void action() {
		int prizeIndex = DataConversions.random(0, npcAmount-1);
		int x = 0;
		int y = 0;
		int baseX = location.getX();
		int baseY = location.getY();
		int nextX = 0;
		int nextY = 0;
		int dX = 0;
		int dY = 0;
		int minX = 0;
		int minY = 0;
		int maxX = 0;
		int maxY = 0;
		for (int scanned = 0; scanned < npcAmount; scanned++) {
			if (dX < 0) {
				x -= 1;
				if (x == minX) {
					dX = 0;
					dY = nextY;
					if (dY < 0)
						minY -= 1;
					else
						maxY += 1;
					nextX = 1;
				}
			} else if (dX > 0) {
				x += 1;
				if (x == maxX) {
					dX = 0;
					dY = nextY;
					if (dY < 0)
						minY -=1;
					else
						maxY += 1;
					nextX = -1;
				}
			} else {
				if (dY < 0) {
					y -= 1;
					if (y == minY) {
						dY = 0;
						dX = nextX;
						if (dX < 0)
							minX -= 1;
						else
							maxX += 1;
						nextY = 1;
					}
				} else if (dY > 0) {
					y += 1;
					if (y == maxY) {
						dY = 0;
						dX = nextX;
						if (dX < 0)
							minX -= 1;
						else
							maxX += 1;
						nextY = -1;
					}
				} else {
					minY -= 1;
					dY = -1;
					nextX = 1;
				}
			}
			if(World.getWorld().withinWorld(baseX + x, baseY + y)) {
				if ((World.getWorld().getTile(new Point(baseX + x, baseY + y)).traversalMask & 64) == 0) {
					final Npc n = new Npc(npcId, baseX + x, baseY + y, baseX + x - 20, baseX + x + 20, baseY + y - 20, baseY + y + 20);
					n.setShouldRespawn(false);
					World.getWorld().registerNpc(n);
					Server.getServer().getEventHandler().add(new SingleEvent(null, npcLifetime * 60000, "NPC Loot Delayed Remover") {
						@Override
						public void action() {
							n.remove();
						}
					});
					if(scanned == prizeIndex) {
						n.addDeathListener(this);
						this.lootNpc = n;
					}
				}
			}
		}

		Server.getServer().getEventHandler().add(new SingleEvent(null, npcLifetime * 60000, "NPC Loot Stop Event") {
			@Override
			public void action() {
				lootNpc = null;
				stop();
			}
		});
	}
}
