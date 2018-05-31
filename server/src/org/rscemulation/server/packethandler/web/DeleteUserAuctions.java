package org.rscemulation.server.packethandler.web;

import org.apache.mina.common.IoSession;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.model.auctions.Auction;
import org.rscemulation.server.net.WebPacket;
import org.rscemulation.server.util.DataConversions;

public class DeleteUserAuctions implements WebPacketHandler {

	@Override
	public void handlePacket(IoSession session, WebPacket packet) {
		long userHash = packet.readLong();
		for(Auction a : World.getWorld().getAuctionHouse().getAllAuctions()) {
			if(DataConversions.usernameToHash(a.getOwner()) == userHash) {
				World.getWorld().getAuctionHouse().removeAuction(a, -2);
			}
		}
		for(Player p : World.getWorld().getPlayers())
			if(p.isInAuctionHouse())
				p.getActionSender().repopulateAuctionHouse();
		/* for real done */
	}

	@Override
	public void sendReply(IoSession paramIoSession, int paramInt) {
		// TODO Auto-generated method stub
		
	}

}
