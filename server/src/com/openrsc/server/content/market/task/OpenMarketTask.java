package com.openrsc.server.content.market.task;

import com.openrsc.server.content.market.Market;
import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.PacketBuilder;

import java.util.ArrayList;
import java.util.Iterator;

public class OpenMarketTask extends MarketTask {

	private Player owner;

	public OpenMarketTask(Player player) {
		this.owner = player;
	}

	public void doTask() {
		PacketBuilder pb = new PacketBuilder(132);
		pb.writeByte(0);
		owner.write(pb.toPacket());

		@SuppressWarnings("unchecked")
		ArrayList<MarketItem> items = (ArrayList<MarketItem>) Market.getInstance().getAuctionItems().clone();
		System.out.println("There's currently " + items.size() + " auctions. ");
		Iterator<MarketItem> iterator = items.iterator();

		int currentWritten = 0;
		AuctionPacketChunk chunk = new AuctionPacketChunk();
		while (iterator.hasNext()) {
			if (chunk.getChunkItemCount() >= 200) {
				currentWritten += chunk.getChunkItemCount();
				owner.write(chunk.toPacket());
				chunk.reset();
			}
			MarketItem item = iterator.next();
			chunk.addItem(item);
		}

		System.out.println(currentWritten + " : " + chunk.getChunkItemCount());
		if (!chunk.isFinished())
			owner.write(chunk.toPacket());
	}

	private class AuctionPacketChunk {
		private ArrayList<MarketItem> items = new ArrayList<>();
		private PacketBuilder builder = new PacketBuilder();

		private boolean finished = false;

		AuctionPacketChunk() {
			builder = new PacketBuilder();
			builder.setID(132);
			builder.writeByte(1);
		}

		public void reset() {
			setFinished(false);
			items.clear();
			builder = new PacketBuilder();
			builder.setID(132);
			builder.writeByte(1);
		}

		public void addItem(MarketItem item) {
			items.add(item);
		}

		int getChunkItemCount() {
			return items.size();
		}

		Packet toPacket() {
			builder.writeShort(items.size());
			for (MarketItem item : items) {
				builder.writeInt(item.getAuctionID());
				builder.writeInt(item.getItemID());
				builder.writeInt(item.getAmountLeft());
				builder.writeInt(item.getPrice());
				builder.writeByte(item.getSeller() == owner.getDatabaseID() ? 1 : 0);
				if (item.getSeller() != owner.getDatabaseID()) builder.writeString(item.getSellerName());
				builder.writeByte(item.getHoursLeft());
			}
			setFinished(true);
			return builder.toPacket();
		}

		public boolean isFinished() {
			return finished;
		}

		public void setFinished(boolean finished) {
			this.finished = finished;
		}

	}
}
