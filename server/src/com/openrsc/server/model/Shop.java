package com.openrsc.server.model;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.PriceMismatchException;
import com.openrsc.server.util.rsc.MathUtil;

import java.util.ArrayList;
import java.util.Iterator;

// TODO: This class should use a thread safe container rather than synchronized blocks

public final class Shop {

	private final boolean general;
	private final int respawnRate, buyModifier, sellModifier, priceModifier;
	private final Item[] items;
	private final ArrayList<Item> shopItems = new ArrayList<Item>();
	private final ArrayList<Player> players = new ArrayList<Player>();
	public String area = "-null-";
	public int[] ownerIDs = null;

	public Shop(boolean general, int respawnRate, int buyModifier, int sellModifier, int priceModifier, Item... items) {
		this.general = general;
		this.respawnRate = respawnRate;
		this.buyModifier = buyModifier;
		this.sellModifier = sellModifier;
		this.priceModifier = priceModifier;
		this.items = items;
		for (Item item : items) {
			shopItems.add(new Item(item.getCatalogId(), item.getAmount())); // comparing the two later, CAN NOT use the same reference
		}
	}

	public Shop(Shop oldShop, String name, int... ids) {
		this.general = oldShop.general;
		this.respawnRate = oldShop.respawnRate;
		this.buyModifier = oldShop.buyModifier;
		this.sellModifier = oldShop.sellModifier;
		this.priceModifier = oldShop.priceModifier;
		this.items = oldShop.items;
		this.area = name;
		this.ownerIDs = ids;

		for (Item item : items) {
			shopItems.add(new Item(item.getCatalogId(), item.getAmount())); // comparing the two later, CAN NOT use the same reference
		}
	}

	public boolean addPlayer(Player player) {
		synchronized (players) {
			if (players.contains(player)) {
				return false;
			}

			return players.add(player);
		}
	}

	public void removePlayer(Player player) {
		synchronized (players) {
			if (!players.contains(player)) {
				return;
			}

			players.remove(player);
		}
	}

	public void restock() {
		synchronized (shopItems) {
			boolean updatePlayers = false;

			for (int i = 0; i < shopItems.size(); i++) {
				Item shopItem = shopItems.get(i);
				int amount = shopItem.getAmount();
				int delemitor = i - (items.length - 1); //check if the item if custom, or original shop item

				if (delemitor <= 0) { //its an original item
					if (amount < items[i].getAmount()) { //add item
						shopItem.getItemStatus().setAmount(++amount);
						updatePlayers = true;
					} else if (amount > items[i].getAmount()) {
						shopItem.getItemStatus().setAmount(--amount);
						updatePlayers = true;
					}
				} else { //its custom
					shopItem.getItemStatus().setAmount(--amount);

					if (amount <= 0) {
						shopItems.remove(i);
					}
					updatePlayers = true;
				}
			}

			if (updatePlayers) {
				updatePlayers();
			}
		}
	}

	public void addShopItem(Item item) {
		boolean has = false;
		synchronized (shopItems) {
			for (Item i : shopItems) {
				if (i.getCatalogId() == item.getCatalogId()) {
					int amount = i.getAmount() + item.getAmount();
					if (amount > Short.MAX_VALUE * 2 - 1) {
						amount = 0xffff;
					}
					i.getItemStatus().setAmount(amount);
					has = true;
					break;
				}
			}

			if (!has) {
				shopItems.add(item);
			}

			updatePlayers();
		}
	}

	public void removeShopItem(Item item) {
		synchronized (shopItems) {
			Iterator<Item> shopItem = shopItems.iterator();
			while (shopItem.hasNext()) {
				Item i = shopItem.next();
				if (i.getCatalogId() == item.getCatalogId()) {
					if (i.getAmount() - item.getAmount() <= 0) {
						boolean original = false;

						synchronized (items) {
							for (Item i2 : items) {
								if (i.getCatalogId() == i2.getCatalogId()) {
									original = true;
									break;
								}
							}
						}
						if (!original) {
							shopItem.remove();
						} else {
							i.getItemStatus().setAmount(0);
						}
					} else {
						i.getItemStatus().setAmount(i.getAmount() - item.getAmount());
					}
				}
			}

			updatePlayers();
		}
	}

	private void updatePlayers() {
		synchronized (players) {
			for (Player player : players) {
				ActionSender.showShop(player, this);
			}
		}
	}

	public int currentStock(Item item) {
		return getItemCount(item.getCatalogId());
	}

	public boolean canHoldItem(Item item) {
		return (40 - shopItems.size()) >= (shopItems.contains(item) ? 0 : 1)
			&& currentStock(item) < (Short.MAX_VALUE - Short.MIN_VALUE);
	}

	public int getItemBuyPrice(Player player, int itemID, int defaultPrice, int totalBought) throws PriceMismatchException {
		int worldUsesRetroPrices = player.getWorld().getServer().getConfig().USES_RETRO_STOCK_SENSITIVITY ? 1 : 0;
		int playerUsesRetroPrices = player.getClientVersion() <= 204 ? 1 : 0;
		int combinedFlag = (worldUsesRetroPrices << 1) + playerUsesRetroPrices;

		int desiredPrice, effectivePrice;
		desiredPrice = effectivePrice = 0;

		// obtain desired and effective prices;
		switch(combinedFlag) {
			case 3:
				// world and player use retro price
				effectivePrice = desiredPrice = calcItemBuyPrice(itemID, defaultPrice, totalBought, true);
				break;
			case 0:
				// world and player use modern price
				effectivePrice = desiredPrice = calcItemBuyPrice(itemID, defaultPrice, totalBought, false);
				break;
			case 2:
				// world uses retro price, player does not
				effectivePrice = calcItemBuyPrice(itemID, defaultPrice, totalBought, true);
				desiredPrice = calcItemBuyPrice(itemID, defaultPrice, totalBought, false);
				break;
			case 1:
				// world uses modern price, player does not
				effectivePrice = calcItemBuyPrice(itemID, defaultPrice, totalBought, false);
				desiredPrice = calcItemBuyPrice(itemID, defaultPrice, totalBought, true);
				break;
		}
		if (effectivePrice != desiredPrice) {
			throw new PriceMismatchException(desiredPrice, effectivePrice, "A difference in shop prices encountered");
		}

		return effectivePrice;
	}

	public int calcItemBuyPrice(int itemID, int defaultPrice, int totalBought, boolean retroCalc) {
		int buyOffset;
		if (retroCalc) {
			buyOffset = getRetroStockOffset(itemID);
		} else {
			buyOffset = getStockBuyOffset(itemID, totalBought);
		}
		int priceMod = buyModifier + buyOffset;
		if (priceMod < 10)
			priceMod = 10;
		return (priceMod * defaultPrice) / 100;
	}

	public int getItemSellPrice(Player player, int itemID, int defaultPrice, int totalRemoved) throws PriceMismatchException {
		int worldUsesRetroPrices = player.getWorld().getServer().getConfig().USES_RETRO_STOCK_SENSITIVITY ? 1 : 0;
		int playerUsesRetroPrices = player.getClientVersion() <= 204 ? 1 : 0;
		int combinedFlag = (worldUsesRetroPrices << 1) + playerUsesRetroPrices;

		int desiredPrice, effectivePrice;
		desiredPrice = effectivePrice = 0;

		// obtain desired and effective prices;
		switch(combinedFlag) {
			case 3:
				// world and player use retro price
				effectivePrice = desiredPrice = calcItemSellPrice(itemID, defaultPrice, totalRemoved, true);
				break;
			case 0:
				// world and player use modern price
				effectivePrice = desiredPrice = calcItemSellPrice(itemID, defaultPrice, totalRemoved, false);
				break;
			case 2:
				// world uses retro price, player does not
				effectivePrice = calcItemSellPrice(itemID, defaultPrice, totalRemoved, true);
				desiredPrice = calcItemSellPrice(itemID, defaultPrice, totalRemoved, false);
				break;
			case 1:
				// world uses modern price, player does not
				effectivePrice = calcItemSellPrice(itemID, defaultPrice, totalRemoved, false);
				desiredPrice = calcItemSellPrice(itemID, defaultPrice, totalRemoved, true);
				break;
		}
		if (effectivePrice != desiredPrice) {
			throw new PriceMismatchException(desiredPrice, effectivePrice, "A difference in shop prices encountered");
		}

		return effectivePrice;
	}

	public int calcItemSellPrice(int itemID, int defaultPrice, int totalRemoved, boolean retroCalc) {
		int sellOffset;
		if (retroCalc) {
			sellOffset = getRetroStockOffset(itemID);
		} else {
			sellOffset = getStockOffset(itemID, totalRemoved);
		}
		int priceMod = sellModifier + sellOffset;
		if (priceMod < 10)
			priceMod = 10;
		return (priceMod * defaultPrice) / 100;
	}

	public boolean shouldStock(int id) {
		if (general) {
			return true;
		}
		for (Item item : items) {
			if (item.getCatalogId() == id) {
				return true;
			}
		}
		return false;
	}

	public Item getShopItem(int index) {
		return shopItems.get(index);
	}

	public int getItemCount(int id) {
		int count = 0;

		synchronized (shopItems) {
			for (Item item : shopItems) {
				if (item.getCatalogId() == id) {
					count += item.getAmount();
				}
			}
		}

		return count;
	}

	public int getShopSize() {
		return shopItems.size();
	}

	public boolean isGeneral() {
		return general;
	}

	public int getRespawnRate() {
		return respawnRate;
	}

	public int getBuyModifier() {
		return buyModifier;
	}

	public int getSellModifier() {
		return sellModifier;
	}

	public int getPriceModifier() {
		return priceModifier;
	}

	private int getStockOffset(int itemID, int totalRemoved) {
		int baseStock = 1;
		for (Item item : items) {
			if (item.getCatalogId() == itemID) {
				baseStock = item.getAmount() + 1;
			}
		}
		int offset = (int) priceModifier * (baseStock - (getItemCount(itemID) + totalRemoved));
		if (offset < -100)
			offset = -100;
		else if (offset > 100)
			offset = 100;
		return offset;
	}

	private int getStockBuyOffset(int itemID, int totalRemoved) {
		int baseStock = 0;
		for (Item item : items) {
			if (item.getCatalogId() == itemID) {
				baseStock = item.getAmount();
			}
		}
		int offset = (int) priceModifier * (baseStock - (getItemCount(itemID) - totalRemoved));
		if (offset < -100)
			offset = -100;
		else if (offset > 100)
			offset = 100;
		return offset;
	}

	public int getRetroStockOffset(int itemID) {
		int amount = getItemCount(itemID);
		int baseAmount = getStock(itemID);
		return MathUtil.boundedNumber(baseAmount - amount, -127, 127);
	}


	public int getStock(int itemID) {
		for (Item item : items) {
			if (item.getCatalogId() == itemID) {
				return item.getAmount();
			}
		}
		return 0;
	}

	public int getFilteredSize(int maxID) {
		if (maxID <= ItemId.NOTHING.id()) {
			return this.getShopSize();
		} else {
			return (int)(this.shopItems.stream().filter(i -> i.getCatalogId() <= maxID).count());
		}
	}
}
