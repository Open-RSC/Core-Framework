package org.openrsc.server.packethandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.model.auctions.Auction;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.util.DataConversions;

public class AuctionHouseHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	@Override
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		Auction auction;
		int pID = ((RSCPacket) p).getID();
		long price = -1;
		if (!player.isInAuctionHouse()) {
			// player.setSuspiciousPlayer(true);
			player.resetAuctionHouse();
			return;
		}
		switch (pID) {
		case 87: /* create new auction */
			int itemID = p.readShort();
			long itemAmount = p.readLong();
			price = p.readLong();
			if (!EntityHandler.getItemDef(itemID).isTradable() || itemID == 10) {
				player.sendMessage(Config.getPrefix() + "This item cannot be sold in the auction house.");
				return;
			}
			if (itemAmount < 1 || price < 1 || itemID < 0) {
				player.sendMessage("Auction House Input error.  Please change your inputs and try again.");
				return;
			}
			int totalPersonalAuctions = 0;
			for (Auction a : world.getAuctionHouse().getAuctions())
				if (DataConversions.usernameToHash(a.getOwner()) == player.getUsernameHash())
					totalPersonalAuctions++;
			if (!player.isSuperMod())
				if ((player.isSub() && totalPersonalAuctions >= 10)
						|| (!player.isSub() && totalPersonalAuctions >= 5)) {
					player.sendMessage("You have exceeded the amount of auctions listed for this account.");
					return;
				}
			auction = new Auction(new InvItem(itemID, itemAmount), player.getUsername(), price, false,
					System.currentTimeMillis());
			if (EntityHandler.getItemDef(itemID).isStackable()) {
				if (player.getInventory().countId(itemID) < itemAmount) {
					player.sendMessage("You do not have enough of that item to make this auction.");
					return;
				}
				if (player.getInventory().contains(new InvItem(itemID, itemAmount))
						&& player.getInventory().remove(new InvItem(itemID, itemAmount)) > -1) {
					/* success! */
					// world.getWorldLoader().addAuction(auction);
					world.getAuctionHouse().addAuction(auction);
					for (Player p1 : World.getPlayers())
						if (p1.isInAuctionHouse())
							p1.getActionSender().addToAuctionHouse(auction);
					player.sendMessage("Your auction has been listed.");
				}
			} else {
				if (player.getInventory().countId(itemID) < itemAmount) {
					player.sendMessage("You do not have enough of that item to make this auction.");
					return;
				}
				if (player.getInventory().contains(new InvItem(itemID))
						&& player.getInventory().countId(itemID) >= itemAmount) {
					for (int i = 0; i < itemAmount; i++)
						if (player.getInventory().remove(new InvItem(itemID, 1)) == -1) {
							player.sendMessage("Failed to remove all of the items from inventory.");
							return;
						}
					/* success! */
					// world.getWorldLoader().addAuction(auction);
					world.getAuctionHouse().addAuction(auction);
					for (Player p1 : World.getPlayers())
						if (p1.isInAuctionHouse())
							p1.getActionSender().addToAuctionHouse(auction);
					player.sendMessage("Your auction has been listed.");
				}
			}
			// world.getWorldLoader().saveAuctionHouse();
			player.getActionSender().sendInventory();
			break;

		case 88: /* refresh */
			player.getActionSender().repopulateAuctionHouse();
			break;

		case 89: /* close */
			player.resetAuctionHouse();
			break;

		case 90: /* buy items */
			int auctionIndex = p.readShort();
			int auctionItemID = p.readInt();
			long auctionItemPriceEach = p.readLong();
			long amount = p.readLong();
			if (amount < 1 || auctionIndex < 0 || world.getAuctionHouse().getAuctions().size() == 0) {
				player.sendMessage("Please try again.");
				return;
			}
			auction = world.getAuctionHouse().getAuction(auctionIndex);
			if (auction == null) {
				player.sendMessage("Auction not found.");
				return;
			}
			if (auction.getPrice() != auctionItemPriceEach || auction.getID() != auctionItemID) {
				player.sendMessage("Please try again.");
				player.getActionSender().repopulateAuctionHouse();
				return;
			}
			if (auction.getAmount() < amount) {
				player.sendMessage("There is not enough of that item in the auction house to purchase.");
				return;
			}
			List<InvItem> items = new ArrayList<InvItem>();
			if (!EntityHandler.getItemDef(auction.getID()).isStackable())
				for (int i = 0; i < amount; i++)
					items.add(new InvItem(auction.getID()));
			else
				items.add(new InvItem(auction.getID()));
			/*
			 * if (player.getInventory().getRequiredSlots(items) > 30 -
			 * player.getInventory().size()) { player.getActionSender()
			 * .sendMessage("You do not have enough room in your inventory to complete the purchase."
			 * ); return; }
			 */
			price = auction.getPrice() * amount;
			if (player.getInventory().countId(10) < price) {
				player.getActionSender().sendMessage("You do not have enough coins to purchase that item.");
				return;
			}
			if (player.getInventory().remove(10, price) < 0) {
				player.getActionSender().sendMessage("You do not have enough coins to purchase that item.");
				return;
			}

			// world.getWorldLoader().deleteAuction(auction);
			world.getAuctionHouse().removeAuction(auction, amount);
			/*
			 * for(Player p1 : world.getPlayers()) if(p1.isInAuctionHouse())
			 * p1.getActionSender().removeFromAuctionHouse(auction, amount);
			 */
			Auction newAuction = new Auction(new InvItem(auction.getID(), amount), player.getUsername(), price, false,
					true, auction.getCreatedTimestamp());
			world.getAuctionHouse().addAuction(newAuction);

			for (Player p1 : World.getPlayers())
				if (p1.getUsernameHash() == DataConversions.usernameToHash(auction.getOwner())) {
					if (!p1.inCombat() && !p1.getLocation().inWilderness() && !p1.isInAuctionHouse()
							&& !p1.accessingBank() && !p1.accessingShop())
						p1.getActionSender().sendPendingAuctions();
					else {
						p1.getActionSender().sendMessage("You have sold " + amount + "x "
								+ EntityHandler.getItemDef(auction.getID()).getName() + " for " + price + "gp.");
						p1.getActionSender().sendMessage("Talk to the Auctioneer to collect your coins.");
					}
					break;
				}
			player.getActionSender().sendMessage("You have bought " + amount + "x "
					+ EntityHandler.getItemDef(auction.getID()).getName() + " for " + price + "gp.");
			player.getActionSender().sendMessage("Talk to the Auctioneer to collect your items.");

			// world.getWorldLoader().saveAuctionHouse();
			player.getActionSender().sendInventory();
			for (Player p1 : World.getPlayers())
				if (p1.isInAuctionHouse())
					p1.getActionSender().repopulateAuctionHouse();
			break;
		case 91: /* cancel */
			auctionIndex = p.readShort();
			if (auctionIndex < 0 || world.getAuctionHouse().getAuctions().size() == 0
					|| world.getAuctionHouse().getAuctions().size() <= auctionIndex) {
				player.getActionSender().sendMessage("Please try again.");
				return;
			}
			auction = world.getAuctionHouse().getAuction(auctionIndex);
			if (auction == null) {
				player.getActionSender().sendMessage("Auction not found.");
				return;
			}
			if (DataConversions.usernameToHash(auction.getOwner()) != player.getUsernameHash() && !player.isSuperMod()) {
				player.getActionSender().sendMessage("This auction does not belong to you.");
				return;
			}
			boolean modRemoved = DataConversions.usernameToHash(auction.getOwner()) != player.getUsernameHash()
					&& player.isSuperMod();
			/*
			 * for(Player p1 : world.getPlayers()) if(p1.isInAuctionHouse())
			 * p1.getActionSender().removeFromAuctionHouse(auction,
			 * auction.getAmount());
			 */
			// world.getWorldLoader().deleteAuction(auction);
			world.getAuctionHouse().removeAuction(auction, -1);
			for (Player p1 : World.getPlayers())
				if (p1.getUsernameHash() == DataConversions.usernameToHash(auction.getOwner())) {
					if (!p1.inCombat() && !p1.getLocation().inWilderness() && !p1.isInAuctionHouse()
							&& !p1.accessingBank() && !p1.accessingShop())
						p1.getActionSender().sendPendingAuctions();
					else {
						p1.sendMessage("Your auction listing for " + auction.getAmount() + "x "
								+ EntityHandler.getItemDef(auction.getID()).getName() + " has been canceled"
								+ (modRemoved ? " by a staff member" : "") + ".");
						p1.sendMessage("Talk to the Auctioneer to collect your items.");
					}
					break;
				}

			// world.getWorldLoader().saveAuctionHouse();
			for (Player p1 : World.getPlayers())
				if (p1.isInAuctionHouse())
					p1.getActionSender().repopulateAuctionHouse();
			break;
		default:
			return;
		}
	}
}
