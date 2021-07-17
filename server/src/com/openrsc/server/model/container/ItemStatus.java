package com.openrsc.server.model.container;

public class ItemStatus {
	private int catalogId;
	private int amount;
	private boolean noted;
	private boolean wielded;
	private int durability;

	public ItemStatus() {
		this.catalogId = -1;
		this.amount = 0;
		this.noted = false;
		this.wielded = false;
		this.durability = 100;
	}

	public int getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(int catalogId) {
		this.catalogId = catalogId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		if (amount < 0) {
			amount = 0;
		}
		this.amount = amount;
	}

	public boolean getNoted() {
		return noted;
	}

	public void setNoted(boolean noted) {
		this.noted = noted;
	}

	public boolean isWielded() { return wielded; }

	public void setWielded(boolean wielded) { this.wielded = wielded; }

	public int getDurability() {
		return durability;
	}

	public void setDurability(int durability) {
		if (durability < 0) {
			durability = 0;
		} else if (durability > 100) {
			durability = 100;
		}
		this.durability = durability;
	}
}
