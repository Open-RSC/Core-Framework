/*
 * Copyright (C) RSCEmulation 2009-13 All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by RSCEmulation Team <dev@rscemulation.net>, January, 2013
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package org.rscemulation.server.packethandler;

import org.apache.mina.common.IoSession;
import org.rscemulation.server.Config;
import org.rscemulation.server.LoginResponse;
import org.rscemulation.server.ServerBootstrap;
import org.rscemulation.server.database.game.Login;
import org.rscemulation.server.net.Packet;
import org.rscemulation.server.net.RSCPacket;
import org.rscemulation.server.packetbuilder.RSCPacketBuilder;
import org.rscemulation.server.util.DataConversions;

/**
 * A message handler for logging a player in.  Before a player can log in, 
 * they have to be validated.  Validation has several phases including:
 * <ul>
 * 	<li>Verifying correct client version</li>
 * 	<li>Verifying correct session keys</li>
 * 	<li>Additional validation performed in a DatabaseService</li>
 * </ul>
 * 
 * @author Zilent
 * 
 * @version 1.1, 2/1/2013
 * 
 * @since 3.0
 * 
 */
public class PlayerLogin
	implements
		PacketHandler
{
	
	public void handlePacket(Packet p, IoSession session)
		throws
			Exception
	{
		p.skip(1);
		int clientVersion = p.readShort();
		if(clientVersion != Config.SERVER_VERSION)
		{
			session.write(new RSCPacketBuilder().setBare(true).addByte((byte)LoginResponse.CLIENT_NOT_UP_TO_DATE.ordinal()).toPacket());
			session.close();
			return;
		}
		RSCPacket loginPacket = DataConversions.decryptRSA(p.readBytes(p.readByte()));
		int[] sessionKeys = new int[4];
		for (int key = 0; key < sessionKeys.length; key++)
			sessionKeys[key] = loginPacket.readInt();
		long serverKey = (Long)session.getAttachment();
		
		if((serverKey >> 32) != sessionKeys[2] || (int)serverKey != sessionKeys[3])
		{
			session.write(new RSCPacketBuilder().setBare(true).addByte((byte)LoginResponse.SESSION_REJECTED.ordinal()).toPacket());
			session.close();
			return;
		}
		int clientWidth = loginPacket.readInt();
		String username = loginPacket.readString(20).trim();
		loginPacket.skip(1);
		String password = loginPacket.readString(20).trim();

		loginPacket.skip(1);
		ServerBootstrap.getDatabaseService().submit(new Login(username, password, session, clientWidth));
	}
}