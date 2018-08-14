package org.openrsc.server.packethandler.web;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.net.WebPacket;

public class TeleportLocationMutation
	implements
		WebPacketHandler
{
	private enum OpCode
	{
		ADD_NEW_LOCATION,
		REMOVE_LOCATION
	}
	private final static int REPLY_SUCCESS = 3;
	private final static int REPLY_LOCATION_NOT_FOUND = 1;
	private final static int REPLY_INVALID_COORDINATES = 1;
	private final static int REPLY_ALIAS_IN_USE = 2;
	
	private void addNewLocation(IoSession session, String alias, int x, int y)
	{
		if(EntityHandler.getTeleportManager().containsTeleport(alias))
		{
			sendReply(session, REPLY_ALIAS_IN_USE);
		}
		else
		{
			if(World.withinWorld(x, y))
			{
				EntityHandler.getTeleportManager().addTeleport(alias, x, y);
				sendReply(session, REPLY_SUCCESS);
				for(Player player : World.getPlayers())
				{
					if(player.isAdmin() || player.isSuperMod() || player.isDev())
					{
						player.sendMessage(Config.getPrefix() + "A new teleport location has been created: \"" + alias + "\"");
					}
				}
			}
			else
			{
				sendReply(session, REPLY_INVALID_COORDINATES);
			}	
		}
	}

	private void removeLocation(IoSession session, String alias)
	{
		if(!EntityHandler.getTeleportManager().containsTeleport(alias))
		{
			sendReply(session, REPLY_LOCATION_NOT_FOUND);
		}
		else
		{
			EntityHandler.getTeleportManager().removeTeleport(alias);
			sendReply(session, REPLY_SUCCESS);
		}
	}
	
	@Override
	public void handlePacket(IoSession session, WebPacket p)
	{
		switch(OpCode.values()[p.readByte()])
		{
		case ADD_NEW_LOCATION:
			addNewLocation(session, p.readString(p.readByte()), p.readShort(), p.readShort());
			break;
		case REMOVE_LOCATION:
			removeLocation(session, p.readString(p.readByte()));
			break;
		}
	}

	@Override
	public void sendReply(IoSession session, int reply)
	{
		session.write(Integer.valueOf(reply));
	}

}
