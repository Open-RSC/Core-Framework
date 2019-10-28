package com.openrsc.server.event.custom;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.world.World;

public final class ShopRestockEvent extends DelayedEvent {

	private final Shop shop;

	public ShopRestockEvent(World world, Shop shop) {
		super(world, null, shop.getRespawnRate(), "Shop Restock Event");
		this.shop = shop;
	}

	@Override
	public void run() {
		shop.restock();
	}

}
