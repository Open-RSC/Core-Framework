package org.rscemulation.server.model.auctions;

import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.World;

/**
 * TODO: Use creation timestamp as index maybe? Would vastly simplify indexing.
 * 
 * @author Zach Knight
 *
 */

public class Auction {
	private long price, amount;
	private int index, id;
	private String owner;
	private boolean sold;
	private boolean canceled;
	private long createdTimestamp;

	public Auction(InvItem item, String owner, long price, boolean sold, long created) {
		if (!sold)
			this.index = World.getWorld().getAuctionHouse().getAuctions().size();

		this.id = item.getID();
		this.amount = item.getAmount();
		this.price = price;
		this.owner = owner;
		this.sold = sold;
		this.setCanceled(false);
		this.setCreatedTimestamp(created);
	}

	public Auction(InvItem item, String owner, long price, boolean sold, boolean canceled, long created) {
		if (!sold && !canceled)
			this.index = World.getWorld().getAuctionHouse().getAuctions().size();

		this.id = item.getID();
		this.amount = item.getAmount();
		this.price = price;
		this.owner = owner;
		this.sold = sold;
		this.setCanceled(canceled);
		this.setCreatedTimestamp(created);
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getAmount() {
		return amount;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public long getPrice() {
		return price;
	}

	public void decIndex() {
		this.index--;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setOwner(String username) {
		this.owner = username;
	}

	public String getOwner() {
		return owner;
	}

	public boolean isSold() {
		return sold;
	}

	public void setSold(boolean sold) {
		this.sold = sold;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public long getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(long created) {
		this.createdTimestamp = created;
	}
}
