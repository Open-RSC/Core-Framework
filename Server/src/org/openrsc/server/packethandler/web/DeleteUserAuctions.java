package org.openrsc.server.packethandler.web;

import org.apache.mina.common.IoSession;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.model.auctions.Auction;
import org.openrsc.server.net.WebPacket;
import org.openrsc.server.util.DataConversions;

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
