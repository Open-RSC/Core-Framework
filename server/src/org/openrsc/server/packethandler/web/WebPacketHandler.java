package org.openrsc.server.packethandler.web;

import org.apache.mina.common.IoSession;
import org.openrsc.server.net.WebPacket;

public abstract interface WebPacketHandler
{
  public abstract void handlePacket(IoSession paramIoSession, WebPacket paramWebPacket);

  public abstract void sendReply(IoSession paramIoSession, int paramInt);
}