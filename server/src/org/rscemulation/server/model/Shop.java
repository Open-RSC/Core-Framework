package org.rscemulation.server.model;

import org.rscemulation.server.event.DelayedEvent;
import org.rscemulation.server.util.DataConversions;

import java.util.*;

public class Shop {

	private static int MAX_SIZE = 40;
	
	private boolean general;
	private int id, sellModifier, buyModifier, minX, maxX, minY, maxY, respawnRate;
	private String greeting;
	private String[] options;
	private ArrayList<InvItem> items;
	private int[] equilibriumIds;
	private long[] equilibriumAmounts;
	private ArrayList<Player> players;
	private ArrayList<InvItem> shopItems = new ArrayList<InvItem>();
	
	public Shop(int id, boolean general, int sellModifier, int buyModifier, int minX, int maxX, int minY, int maxY, String greeting, String[] options, int respawnRate) {
		this.id = id;
		this.general = general;
		this.sellModifier = sellModifier;
		this.buyModifier = buyModifier;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.greeting = greeting;
		this.options = options;
		this.respawnRate = respawnRate;
		this.items = new ArrayList<InvItem>();
		this.players = new ArrayList<Player>();

	}
	
	public int getRespawnRate() {
		return respawnRate;
	}
	
	public int minX() {
		return minX;
	}
	
	public int maxX() {
		return maxX;
	}
	
	public int minY() {
		return minY;
	}
	
	public int maxY() {
		return maxY;
	}
	
	public boolean shouldStock(int id) {
		if (general)
			return true;
		for (int eqID : equilibriumIds) {
			if (eqID == id)
				return true;
		}
		return false;
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	public void removePlayer(Player player) {
		players.remove(player);
	}
	
	public void initRestock() {
		players = new ArrayList<Player>();
		final Shop shop = this;
		World.getDelayedEventHandler().add(new DelayedEvent(null, respawnRate) {
			//private int iterations = 0;
			public void run() {
				boolean changed = false;
				Iterator<InvItem> iterator = items.iterator();
				//iterations++;
				while (iterator.hasNext()) {
					InvItem shopItem = iterator.next();
					long eq = shop.getEquilibrium(shopItem.getID());
					if (shopItem.getAmount() < eq) {
						shopItem.setAmount(shopItem.getAmount() + 1);
						changed = true;
					}
				}
				
  				if (changed)
  					shop.updatePlayers();
  			}
  		});
	}
	
	public void initReduction() {
		players = new ArrayList<Player>();
		final Shop shop = this;
		World.getDelayedEventHandler().add(new DelayedEvent(null, 180000) {
			private int iterations = 0;
			public void run() {
				boolean changed = false;
				Iterator<InvItem> iterator = items.iterator();
				iterations++;
				while (iterator.hasNext()) {
					InvItem shopItem = iterator.next();
					long eq = shop.getEquilibrium(shopItem.getID());
					if ((iterations % 4 == 0) && shopItem.getAmount() > eq) {
						shopItem.setAmount(shopItem.getAmount() - 1);
						if (shopItem.getAmount() <= 0 && !DataConversions.inArray(equilibriumIds, shopItem.getID()))
							iterator.remove();
						changed = true;
					}
				}
  				if (changed)
  					shop.updatePlayers();
  			}
  		});
	}	
	
	public void updatePlayers() {
		Iterator<Player> iterator = players.iterator();
  		while (iterator.hasNext()) {
  			Player p = iterator.next();
  			if (!equals(p.getShop())) {
  				iterator.remove();
  				continue;
  			}
  			p.showShop(this);
  		}
	}
	
	public long getEquilibrium(int id) {
		for (int idx = 0; idx < equilibriumIds.length; idx++) {
			if (equilibriumIds[idx] == id)
				return equilibriumAmounts[idx];
		}
		return 0;
	}
	
	public InvItem getFirstById(int id) {
		for (int index = 0; index < items.size(); index++) {
			if (items.get(index).getID() == id)
				return items.get(index);
		}
		return null;
	}
	
	public void setEquilibrium() {
		equilibriumIds = new int[items.size()];
		equilibriumAmounts = new long[items.size()];
		for (int idx = 0;idx < items.size();idx++) {
			equilibriumIds[idx] = items.get(idx).getID();
			equilibriumAmounts[idx] = items.get(idx).getAmount();
		}
	}
	
	public int getID() {
		return id;
	}
	
	public String getGreeting() {
		return greeting;
	}
	
	public String[] getOptions() {
		return options;
	}
	
	public boolean withinShop(Point p) {
		return p.getX() >= minX && p.getX() <= maxX && p.getY() >= minY && p.getY() <= maxY;
	}
	
	public ArrayList<InvItem> getItems() {
		return items;
	}
	
	public boolean contains(InvItem i) {
		return items.contains(i);
	}
	
	public int add(InvItem item) {
		if (item.getAmount() <= 0)
			return -1;
		for (int i = 0; i < items.size(); i++) {
			if (item.equals(items.get(i))) {
				items.get(i).setAmount(items.get(i).getAmount() + item.getAmount());
				return i;
			}
		}
		items.add(item);
		return items.size() - 2;
	}
	
	public int remove(InvItem item) {
		Iterator<InvItem> iterator = items.iterator();
		for (int index = 0; iterator.hasNext(); index++) {
			InvItem i = iterator.next();
			if (item.getID() == i.getID()) {
				if (item.getAmount() < i.getAmount())
					i.setAmount(i.getAmount() - item.getAmount());
				else if(DataConversions.inArray(equilibriumIds, item.getID()))
					i.setAmount(0);
				else
					iterator.remove();
				return index;
			}
		}
		return -1;
	}
	
	public ListIterator<InvItem> iterator() {
		return items.listIterator();
	}
	
	public long countId(int id) {
		for (InvItem i : items) {
			if (i.getID() == id)
				return i.getAmount();
		}
		return 0;
	}
	
	public boolean full() {
		return items.size() >= MAX_SIZE;
	}
	
	public int size() {
		return items.size();
	}
	
	public boolean isGeneral() {
		return general;
	}
	
	public int getSellModifier() {
		return sellModifier;
	}
	
	public int getBuyModifier() {
		return buyModifier;
	}
	
	public int getRequiredSlots(List<InvItem> items) {
		int requiredSlots = 0;
		for (InvItem item : items) {
			if (items.contains(item))
				continue;
			requiredSlots++;
		}
		return requiredSlots;
	}
	
	public int getRequiredSlots(InvItem item) {
		return (items.contains(item) ? 0 : 1);
	}
	
	public boolean canHold(InvItem item) {
		return (MAX_SIZE - items.size()) >= getRequiredSlots(item);
	}
	
	public boolean canHold(ArrayList<InvItem> items) {
		return (MAX_SIZE - items.size()) >= getRequiredSlots(items);
	}
	
	public int getItemBuyPrice(int id) {
		int index = 0;
		for(InvItem item : items) {
			if(item.getID() == id) {
				return getPrice(index, true);
			}
			index++;
		}
		return 0;
	}
	
	public int getItemSellPrice(int id) {
		int index = 0;
		for(InvItem item : items) {
			if(item.getID() == id) {
				return getPrice(index, false);
			}
			index++;
		}
		return 0;
	}
	
	public int getPrice(int index, boolean buy) {
		if(index >= this.items.size() || index < 0) {
			return 0;
		}
		InvItem item = items.get(index);
		long amount = item.getAmount();
		int basePrice = item.getDef().getBasePrice();
		
		if(basePrice == 0) {
			return 0;
		}
		long equilibrium = 0;
		if(shouldStock(item.getID())) {
			for(int ind = 0; ind < items.size(); ind++) {
				if(items.get(ind).getID() == item.getID()) {
					equilibrium = items.get(ind).getAmount();
				}
			}
		} else {
			equilibrium = -2;
		}
		
		double percent = (equilibrium > 100 ? 0.01 : 0.05);
		
		int minItemValue = basePrice / 4;
		int maxItemValue = basePrice * 2;
		
		int price;
		amount += 1;
		if(buy) {
			price = basePrice;
			if(amount < equilibrium) {
				price += Math.round(price * (percent * (equilibrium - amount)));
			}
		} else {
			price = basePrice / 2;
			//System.out.println(price+" "+minItemValue+" "+maxItemValue+" "+equilibrium+" "+amount);
			if(amount > equilibrium) {
				price += Math.round(price *(percent * (amount - equilibrium)));
			}
		}
		if(price > maxItemValue) {
			price = maxItemValue;
		} else if(price < minItemValue) {
			price = minItemValue;
		}
		
		if(price == 0) {
			return buy ? 1 : 0;
		}
		return price;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Shop) {
			Shop shop = (Shop)o;
			return shop.getID() == id;
		}
		return false;
	}

	public void generateShopItems() {
		for(InvItem item : items) {
			this.shopItems.add(item);
		}
	}
}