package org.openrsc.client;

import org.openrsc.client.entityhandling.EntityHandler;

public class Auction implements Comparable<Auction> {
	private long price, amount;
	private int id;
	private int index;
	private boolean owner;
	
	public Auction(int index, int id, long amount, long price, boolean owner) {
		this.id = id;
		this.amount = amount;
		this.price = price;
		this.index = index;
		this.owner = owner;
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

	public void setOwner(boolean b) {
		this.owner = b;
	}
	
	public boolean isOwner() {
		return owner;
	}
	
	public void setIndex(int i) {
		this.index = i;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void decIndex() {
		this.index--;
	}
	
	@Override
	public String toString() {
		return EntityHandler.getItemDef(getID()).getName();
	}

	@Override
	public int compareTo(Auction auction) {
		return toString().compareTo(auction.toString());
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Auction && ((Auction) o).getIndex() == this.getIndex())
			return true;
		return false;
	}
}
