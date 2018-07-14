package org.openrsc.server.model.auctions;

import java.util.ArrayList;
import java.util.List;

import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.World;

public class AuctionHouse {
	private List<Auction> auctions = new ArrayList<Auction>();
	private List<Auction> soldAuctions = new ArrayList<Auction>();
	private List<Auction> canceledAuctions = new ArrayList<Auction>();

	public List<Auction> getAuctions() {
		return auctions;
	}

	public List<Auction> getSoldAuctions() {
		return soldAuctions;
	}

	public List<Auction> getCanceledAuctions() {
		return canceledAuctions;
	}

	public List<Auction> getAllAuctions() {
		List<Auction> allAuctions = new ArrayList<Auction>();
		allAuctions.addAll(auctions);
		allAuctions.addAll(soldAuctions);
		allAuctions.addAll(canceledAuctions);
		return allAuctions;
	}

	public void addAuction(Auction auction, boolean isLoading) {
		if (auction.isSold())
			soldAuctions.add(auction);
		else if (auction.isCanceled())
			canceledAuctions.add(auction);
		else
			auctions.add(auction);
		if (!isLoading)
			World.getWorldLoader().addAuction(auction);
	}

	public void addAuction(Auction auction) {
		this.addAuction(auction, false);
	}

	public boolean removeAuction(Auction auction, long amount) {
		if (amount == -2) { /* TODO: web removal */

			for (Auction a : auctions)
				if (a.getIndex() == auction.getIndex() && !a.isSold()) {
					for (int i = a.getIndex(); i < World.getWorld().getAuctionHouse().getAuctions().size(); i++)
						World.getWorld().getAuctionHouse().getAuction(i).decIndex();
					World.getWorldLoader().deleteAuction(a);
					auctions.remove(a);
					return true;
				}

		}
		if (amount == auction.getAmount() || amount == -1) {
			for (Auction a : auctions)
				if (a.getIndex() == auction.getIndex() && !a.isSold()) {
					for (int i = a.getIndex(); i < World.getWorld().getAuctionHouse().getAuctions().size(); i++)
						World.getWorld().getAuctionHouse().getAuction(i).decIndex();

					World.getWorldLoader().deleteAuction(a);
					auctions.remove(a);
					if (amount != -1) {
						a.setSold(true);
						soldAuctions.add(a);
					} else {
						a.setCanceled(true);
						canceledAuctions.add(a);
					}
					World.getWorldLoader().addAuction(a);
					return true;
				}
		} else {
			World.getWorldLoader().deleteAuction(auction);
			auction.setAmount(auction.getAmount() - amount);
			World.getWorldLoader().addAuction(auction);
			addAuction(new Auction(new InvItem(auction.getID(), amount), auction.getOwner(), auction.getPrice(), true,
					auction.getCreatedTimestamp()));
			return true;
		}
		return false;
	}

	public Auction getAuction(int index) {
		return auctions.get(index);
	}
}
