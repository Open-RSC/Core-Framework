package com.openrsc.server.content.market.task;

import com.openrsc.server.content.market.Market;
import com.openrsc.server.content.market.MarketDatabase;
import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.DiscordSender;
import com.openrsc.server.net.rsc.ActionSender;

public class NewMarketItemTask extends MarketTask {

	private MarketItem newItem;
	private Player owner;

	public NewMarketItemTask(Player player, MarketItem item) {
		this.owner = player;
		this.newItem = item;
	}

	@Override
	public void doTask() {
		ItemDefinition def = EntityHandler.getItemDef(newItem.getItemID());

		if (newItem.getItemID() == 10 || def.isUntradable()) {
			ActionSender.sendBox(owner, "@red@[Auction House - Error] % @whi@ You cannot sell that item on auction house!", false);
			return;
		}
		if (newItem.getPrice() < 1) {
			ActionSender.sendBox(owner, "@red@[Auction House - Error] % @whi@ Price must be greater than zero", false);
			return;
		}
		if (newItem.getAmount() < 1) {
			ActionSender.sendBox(owner, "@red@[Auction House - Error] % @whi@ Amount must be greater than zero", false);
			return;
		}
		if (owner.getInventory().countId(newItem.getItemID()) < newItem.getAmount()) {
			return;
		}
		/*int feeCost = (int) (newItem.getPrice() * 0.025);
		if(feeCost < 5)
			feeCost = 5;
		
		if(feeCost >= 5) {
			if((!owner.getInventory().contains(new Item(10)) && (!owner.getBank().contains(new Item(10))))) {
				ActionSender.sendBox(owner, "@red@[Auction House - Error] % @whi@ You have no coins in your inventory or bank to cover the auction fee.", false);
				return;
			} else {
				if(owner.getInventory().countId(10) >= feeCost) {
					owner.getInventory().remove(10, feeCost);
				} else if(owner.getBank().countId(10) >= feeCost){
					owner.getBank().remove(10, feeCost);
				} else {
					ActionSender.sendBox(owner, "@red@[Auction House - Error] % @whi@ You don't have enough coins in your inventory or bank to cover the auction fee. " + (feeCost != 0 ? "% Your auction fee: @gre@" + feeCost + "gp" : "No fee was required."), false);
					return;
				}
			}
		}*/

		if (!def.isStackable())
			for (int i = 0; i < newItem.getAmount(); i++) owner.getInventory().remove(newItem.getItemID(), 1);
		else owner.getInventory().remove(newItem.getItemID(), newItem.getAmount());

		if (def.getOriginalItemID() != -1) newItem.setItemID(def.getOriginalItemID());

		if (MarketDatabase.add(newItem)) {
			//ActionSender.sendBox(owner, "@gre@[Auction House - Success] % @whi@ Auction has been listed % " + newItem.getAmount() + "x @yel@" + def.getName() + " @whi@for @yel@" + newItem.getPrice() + "gp % @whi@Completed auction fee: @gre@" + feeCost + "gp", false);
			ActionSender.sendBox(owner, "@gre@[Auction House - Success] % @whi@ Auction has been listed % " + newItem.getAmount() + "x @yel@" + def.getName() + " @whi@for @yel@" + newItem.getPrice() + "gp", false);
			DiscordSender.auctionAdd(newItem.getItemID(), newItem.getPrice(), newItem.getAmount(), newItem.getSellerName());
		} else {
			Item item = new Item(newItem.getItemID(), newItem.getAmount());
			if (item.getDef().isStackable()) {
				for (int i = 0; i < newItem.getAmount(); i++) {
					owner.getInventory().add(new Item(newItem.getItemID(), 1));
				}
			} else {
				owner.getInventory().add(new Item(newItem.getItemID(), newItem.getAmount()));
			}
			ActionSender.sendBox(owner, "@red@[Auction House - Error] % @whi@ Failed to add item to Auction. % Item(s) have been returned to your inventory.", false);
		}
		Market.getInstance().addRequestOpenAuctionHouseTask(owner);
	}
}
