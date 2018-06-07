/*
 * Copyright (C) openrsc 2009-13 All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by openrsc Team <dev@openrsc.net>, January, 2013
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

package org.openrsc.server.packethandler;

import org.apache.mina.common.IoSession;
import org.openrsc.server.net.Packet;
import org.openrsc.server.packetbuilder.RSCPacketBuilder;
import org.openrsc.server.util.DataConversions;

/**
 * A message handler for generating a set of session keys.  This 
 * is generally the first message that a connecting player sends 
 * to the server.  It should only be sent once per login session.
 * 
 * @author Zilent
 * 
 * @version 1.1, 2/1/2013
 * 
 * @since 3.0
 *
 */
public class SessionRequest
	implements
		PacketHandler
{
	public void handlePacket(Packet p, IoSession session)
	{
		/// Make sure nothing's attached to the session
		/// If anything is attached, then drop the request
		/// While this could potentially happen 'on accident'
		/// this usually indicates someone is probing the 
		/// server for vulnerabilities.  (Not that they will 
		/// find any, but you may want to take action anyway.)
		if(session.getAttachment() != null)
		{
			throw new IllegalStateException("Unable to provide multiple session keys [" + session.getRemoteAddress() + "]");
		}
		@SuppressWarnings("unused")
		byte unused_remove_me = p.readByte();
		if("RSCE".equals(p.readString().trim()))
		{
			/// Generate a random key
			Long serverKey = DataConversions.getRandom().nextLong();
			
			/// Attach that key to the session
			session.setAttachment(serverKey);

			/// Send the key to the client
			session.write(new RSCPacketBuilder().setBare(true).addLong(serverKey).toPacket());
		}
	}
}