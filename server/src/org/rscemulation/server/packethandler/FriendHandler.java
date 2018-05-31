package org.rscemulation.server.packethandler;

import org.apache.mina.common.IoSession;
import org.rscemulation.server.logging.Logger;
import org.rscemulation.server.logging.model.PrivateMessageLog;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.net.Packet;
import org.rscemulation.server.net.RSCPacket;
import org.rscemulation.server.util.ChatFilter;
import org.rscemulation.server.util.DataConversions;

public class FriendHandler
	implements
		PacketHandler
{
	public void handlePacket(Packet p, IoSession session)
		throws
			Exception
	{
		Player player = (Player) session.getAttachment();
		if (player != null)
		{
			int pID = ((RSCPacket) p).getID();
			long user = player.getUsernameHash();
			long friend = p.readLong();
			Player myFriend = World.getPlayer(friend);

			switch (pID)
			{
				/// Add Friend
				case 44:
					if ((player.friendCount() >= 200) || (player.getFriendList().contains(friend)))
					{
						return;
					}
					player.addFriend(friend);
					if (myFriend == null)
					{
						return;
					}
					if ((player.isFriendsWith(friend))
						&& (myFriend.isFriendsWith(user)))
					{
						player.sendFriendUpdate(friend, (byte) 1);
						myFriend.sendFriendUpdate(user, (byte) 1);
						return;
					}
					if (myFriend.getPrivacySetting(1) != true)
						return;
				player.sendFriendUpdate(friend, (byte) 1);
			break;
		case 45:
				player.removeFriend(friend);
				if ((myFriend == null) ||
				(player.getPrivacySetting(1)) ||
				(!myFriend.isFriendsWith(player.getUsernameHash())))
				return;
				myFriend.sendFriendUpdate(user, (byte) 0);
			break;
		case 46:
				if ((player.ignoreCount() >= 200) ||
				(player.getIgnoreList().contains(friend)))
				return;
				player.addIgnore(friend);
			break;
		case 47:
				player.removeIgnore(friend);
				break;
		case 48:
				boolean avoidBlock = player.isMod();
				if ((myFriend == null)
					|| (!myFriend.loggedIn())
					|| ((!myFriend.getPrivacySetting(1)) && (((myFriend
							.getPrivacySetting(1)) || (!myFriend
							.isFriendsWith(user)))))
					|| ((!avoidBlock) && (myFriend.isIgnoring(user))))
				return;
			byte[] inData = p.getRemainingData();
			String data = ChatFilter.censor(DataConversions.byteToString(
					inData, 0, inData.length));
			if (data.length() > 150)
				return;
			inData = DataConversions.stringToByteArray(data);
			myFriend.sendPM(user, inData, player.getGroupID(), false);
			player.sendPM(myFriend.getUsernameHash(), inData, myFriend.getGroupID(), true);
			Logger.log(new PrivateMessageLog(user, player.getAccount(),
					player.getIP(), friend, myFriend.getAccount(), myFriend
							.getIP(), data, DataConversions.getTimeStamp()));
			}
		}
	}
}