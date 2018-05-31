package org.rscemulation.server.packethandler.web;

import org.apache.mina.common.IoSession;
import org.rscemulation.server.net.WebPacket;

public abstract interface WebPacketHandler
{
  public abstract void handlePacket(IoSession paramIoSession, WebPacket paramWebPacket);

  public abstract void sendReply(IoSession paramIoSession, int paramInt);
}