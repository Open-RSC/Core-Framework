package com.openrsc.server.model.entity;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.content.party.PartyPlayer;
import com.openrsc.server.event.DelayedEvent;
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
	 * Is item noted?
	 */
	private boolean noted;

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
	private long spawnedTime;

	public GroundItem(final World world, final int id, final int x, final int y, final int amount, final Player owner) {
		this(world, id, x, y, amount, owner, System.currentTimeMillis());
	}

	public GroundItem(final World world, final int id, final int x, final int y, final int amount) {
		this(world, id, x, y, amount, null);
	}

	public GroundItem(final World world, final int id, final int x, final int y, final int amount, final long spawnTime) {
		this(world, id, x, y, amount, null, spawnTime);
	}

	public GroundItem(final World world, final int id, final int x, final int y, final int amount, final Player owner, final boolean noted) {
		this(world, id, x, y, amount, owner, System.currentTimeMillis(), noted);
	}

	public GroundItem(final World world, final int id, final int x, final int y, final int amount, final Player owner, final long spawnTime) {
		this(world, id, x, y, amount, owner, spawnTime, false);
	}

	public GroundItem(final World world, final int id, final int x, final int y, final int amount, final Player owner, final long spawnTime, final boolean noted) {
		super(world, EntityType.GROUND_ITEM);
		setID(id);
		setNoted(noted);
		setAmount(amount);
		this.ownerUsernameHash = owner == null ? 0 : owner.getUsernameHash();
		spawnedTime = spawnTime;
		trySetLocation(Point.location(x, y));
		if (owner != null) {
			if (owner.getIronMan() == IronmanMode.Transfer.id()) {
				// disallow everyone from picking up transfer ironman items
				this.setAttribute("isTransferIronmanItem", true);
			}
		}
	}

	public GroundItem(final World world, final ItemLoc loc) {
		super(world, EntityType.GROUND_ITEM);
		this.loc = loc;
		setID(loc.id);
		setAmount(loc.amount);
		spawnedTime = System.currentTimeMillis();
		trySetLocation(Point.location(loc.x, loc.y));
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

	public void trySetLocation(Point point) {
		if (getWorld().getServer().getConfig().RESTRICT_ITEM_ID <= ItemId.NOTHING.id()
			|| this.getID() <= getWorld().getServer().getConfig().RESTRICT_ITEM_ID) {
			setLocation(point);
		}
	}

	public boolean isOn(final int x, final int y) {
		return x == getX() && y == getY();
	}

	public boolean belongsTo(final Player player) {
		if (player.getParty() != null) {
			for (Player p2 : getWorld().getPlayers()) {
				if (Objects.requireNonNull(player.getParty()).getPlayers().size() > 1 && player.getParty() != null && player.getParty() == p2.getParty()) {
					PartyPlayer p3 = p2.getParty().getLeader();
					if (p3.getShareLoot() > 0) {
						return true;
					}
				}
			}
		}

		// If the player is an ironman,
		if (player.getIronMan() != IronmanMode.None.id()) {
			// This attribute should only be there if the loot pile was dropped by someone that was killed by a mob
			long killedByMobOwner = getAttribute("killedByMob", -1L);
			if (killedByMobOwner != -1L) {
				// Can only pick it up if it's their own loot pile
				return killedByMobOwner == player.getUsernameHash();
			}
		}

		return player.getUsernameHash() == ownerUsernameHash || ownerUsernameHash == 0;
	}

	public void remove() {
		if (getWorld().getServer().getConfig().RESTRICT_ITEM_ID <= ItemId.NOTHING.id()
			|| this.getID() <= getWorld().getServer().getConfig().RESTRICT_ITEM_ID) {
			if (!isRemoved() && loc != null && loc.getRespawnTime() > 0) {
				getWorld().getServer().getGameEventHandler().add(new DelayedEvent(getWorld(), null, loc.getRespawnTime() * 1000, "Respawn Ground Item") {
					public void run() {
						getWorld().registerItem(new GroundItem(getWorld(), loc));
						stop();
					}
				});
			}
			super.remove();
		}
	}

	public boolean isInvisibleTo(final Player player) {
		if (belongsTo(player))
			return false;
		if (getDef().isMembersOnly() && !getWorld().getServer().getConfig().MEMBER_WORLD)
			return true;
		if (getDef().isUntradable())
			return true;
		if (getAttribute("killerHash", -1L) == player.getUsernameHash())
			return false;
		if (getID() > player.getClientLimitations().maxItemId)
			return true;
		// If the killedByMob attribute exists, this means that the pile was dropped when another player was killed by a mob.
		// The ironman should be able to see it, but still not pick it up.
		if (player.getIronMan() != IronmanMode.None.id() && getAttribute("killedByMob", -1L) != -1) {
			return false;
		}

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

	public boolean getNoted() {
		return noted;
	}

	public void setNoted(final boolean noted) {
		this.noted = noted;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		if (getDef() != null) {
			if (getDef().isStackable() || getNoted()) {
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
