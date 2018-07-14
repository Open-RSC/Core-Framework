package org.openrsc.server.packethandler;

import org.apache.mina.common.IoSession;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;

// TODO: Add in a database row for handler ID 86
public class GeneralPurposeMessage
	implements
		PacketHandler
{

	@Override
	public void handlePacket(Packet p, IoSession session)
		throws
			Exception
	{
		long target = p.readLong();
		int stringCount = p.readInt();
		
		// max of 10 strings pls.
		if(stringCount < 10);
		{
			String[] strings = new String[stringCount];
			int i = 0;
			// read 80 character strings for all except the last one (which might be less than 80 characters)
			for(; i < stringCount - 1; ++i)
			{
				strings[i] = p.readString(80);
			}
			// Then read the last one
			strings[i] = p.readString();
			Player player = World.getPlayer(target);
			if(player != null)
			{
				player.onGeneralPurposeMessageReceived(strings);
			}
		}	
	}

}
