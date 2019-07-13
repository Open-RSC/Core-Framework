package com.openrsc.server.event.custom;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.Shop;

public final class ShopRestockEvent extends DelayedEvent {

	private final Shop shop;

	public ShopRestockEvent(Shop shop) {
		super(null, shop.getRespawnRate(), "Shop Restock Event");
		this.shop = shop;
	}

	@Override
	public void run() {
		shop.restock();
	}

}
