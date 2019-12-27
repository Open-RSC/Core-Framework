package com.openrsc.server.model.entity;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.content.party.PartyPlayer;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

import java.util.Objects;

public class GroundItem extends Entity {
	/**
	 * Amount (for stackables)
	 */
	private int amount;

	/**
	 * Location definition of the item
	 */
	private ItemLoc loc = null;

	/**
	 * Contains the player that the item belongs to, if any
	 */
	private long ownerUsernameHash;
	/**
	 * The time that the item was spawned
	 */
	private long spawnedTime = 0L;

	public GroundItem(final World world, final int id, final int x, final int y, final int amount, final Player owner) {
		this(world, id, x, y, amount, owner, System.currentTimeMillis());
	}

	public GroundItem(final World world, final int id, final int x, final int y, final int amount) {
		this(world, id, x, y, amount, null);
	}

	public GroundItem(final World world, final int id, final int x, final int y, final int amount, final long spawnTime) {
		this(world, id, x, y, amount, null, spawnTime);
	}

	public GroundItem(final World world, final int id, final int x, final int y, final int amount, final Player owner, final long spawnTime) {
		super(world);
		setID(id);
		setAmount(amount);
		this.ownerUsernameHash = owner == null ? 0 : owner.getUsernameHash();
		spawnedTime = spawnTime;
		setLocation(Point.location(x, y));
		if (owner != null && owner.getIronMan() >= IronmanMode.Ironman.id() && owner.getIronMan() <= IronmanMode.Transfer.id())
			this.setAttribute("isIronmanItem", true);
	}

	public GroundItem(final World world, final ItemLoc loc) {
		super(world);
		this.loc = loc;
		setID(loc.id);
		setAmount(loc.amount);
		spawnedTime = System.currentTimeMillis();
		setLocation(Point.location(loc.x, loc.y));
	}

	public boolean equals(final Entity o) {
		if (o instanceof GroundItem) {
			GroundItem item = (GroundItem) o;
			return item.getID() == getID() && item.getAmount() == getAmount()
				&& item.getSpawnedTime() == getSpawnedTime()
				&& (item.getOwnerUsernameHash() == getOwnerUsernameHash())
				&& item.getLocation().equals(getLocation());
		}
		return false;
	}

	public boolean isOn(final int x, final int y) {
		return x == getX() && y == getY();
	}

	public boolean belongsTo(final Player p) {
		if (p.getParty() != null) {
			for (Player p2 : getWorld().getPlayers()) {
				if (Objects.requireNonNull(p.getParty()).getPlayers().size() > 1 && p.getParty() != null && p.getParty() == p2.getParty()) {
					PartyPlayer p3 = p2.getParty().getLeader();
					if (p3.getShareLoot() > 0) {
						return true;
					}
				}
			}
		}
		return p.getUsernameHash() == ownerUsernameHash || ownerUsernameHash == 0;
	}

	public void remove() {
		if (!isRemoved() && loc != null && loc.getRespawnTime() > 0) {
			getWorld().getServer().getGameEventHandler().add(new GameTickEvent(getWorld(), null, loc.getRespawnTime(), "Respawn Ground Item") {
				public void run() {
					getWorld().registerItem(new GroundItem(getWorld(), loc));
					stop();
				}
			});
		}
		super.remove();
	}

	public boolean isInvisibleTo(final Player p) {
		if (belongsTo(p))
			return false;
		if (getDef().isMembersOnly() && !getWorld().getServer().getConfig().MEMBER_WORLD)
			return true;
		if (getDef().isUntradable())
			return true;
		if (!belongsTo(p) && p.getIronMan() >= IronmanMode.Ironman.id() && p.getIronMan() <= IronmanMode.Transfer.id())
			return true;

		// One minute and four seconds to show to all.
		return System.currentTimeMillis() - spawnedTime <= 64000;
	}

	@Override
	public String toString() {
		return "Item(" + this.getID() + ", " + this.amount + ") location = " + getLocation().toString();
	}

	public ItemDefinition getDef() {
		return getWorld().getServer().getEntityHandler().getItemDef(getID());
	}

	public ItemLoc getLoc() {
		return loc;
	}

	public long getOwnerUsernameHash() {
		return ownerUsernameHash;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		if (getDef() != null) {
			if (getDef().isStackable()) {
				this.amount = amount;
			} else {
				this.amount = 1;
			}
		}
	}

	private long getSpawnedTime() {
		return spawnedTime;
	}
}
