package com.openrsc.server.model;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;

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
			shopItems.add(new Item(item.getID(), item.getAmount())); // comparing the two later, CAN NOT use the same refference
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
			shopItems.add(new Item(item.getID(), item.getAmount())); // comparing the two later, CAN NOT use the same refference
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
						shopItem.setAmount(++amount);
						updatePlayers = true;
					} else if (amount > items[i].getAmount()) {
						shopItem.setAmount(--amount);
						updatePlayers = true;
					}
				} else { //its custom
					shopItem.setAmount(--amount);

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
				if (i.getID() == item.getID()) {
					i.setAmount(i.getAmount() + item.getAmount());
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
				if (i.getID() == item.getID()) {
					if (i.getAmount() - item.getAmount() <= 0) {
						boolean original = false;

						synchronized (items) {
							for (Item i2 : items) {
								if (i.getID() == i2.getID()) {
									original = true;
									break;
								}
							}
						}
						if (!original) {
							shopItem.remove();
						} else {
							i.setAmount(0);
						}
					} else {
						i.setAmount(i.getAmount() - item.getAmount());
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

	public boolean canHoldItem(Item item) {
		return (40 - shopItems.size()) >= (shopItems.contains(item) ? 0 : 1);
	}

	public int getItemBuyPrice(int itemID, int defaultPrice, int totalBought) {
		int priceMod = buyModifier + getStockBuyOffset(itemID, totalBought);
		if (priceMod < 10)
			priceMod = 10;
		return (priceMod * defaultPrice) / 100;
	}

	public int getItemSellPrice(int itemID, int defaultPrice, int totalRemoved) {
		int priceMod = sellModifier + getStockOffset(itemID, totalRemoved);
		if (priceMod < 10)
			priceMod = 10;
		return (priceMod * defaultPrice) / 100;
	}

	public boolean shouldStock(int id) {
		if (general) {
			return true;
		}
		for (Item item : items) {
			if (item.getID() == id) {
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
				if (item.getID() == id) {
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
			if (item.getID() == itemID) {
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
			if (item.getID() == itemID) {
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


	public int getStock(int itemID) {
		for (Item item : items) {
			if (item.getID() == itemID) {
				return item.getAmount();
			}
		}
		return 0;
	}
}
