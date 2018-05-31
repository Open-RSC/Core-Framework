package org.rscemulation.server.networking;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.rscemulation.server.Config;
import org.rscemulation.server.logging.Logger;
import org.rscemulation.server.logging.model.ConnectionLog;
import org.rscemulation.server.net.WebPacket;
import org.rscemulation.server.util.Pair;

public class WebConnectionHandler
	implements
		IoHandler
{
	
	private final BlockingQueue<Pair<IoSession, WebPacket>> messageQueue;
	
	public WebConnectionHandler(BlockingQueue<Pair<IoSession, WebPacket>> messageQueue) {
		this.messageQueue = messageQueue;
	}

	public void exceptionCaught(IoSession session, Throwable cause) {}

	public void messageReceived(IoSession session, Object message)
	{
		if (((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress().toString().equals(Config.SERVER_IP))
		{
			messageQueue.add(new Pair<IoSession, WebPacket>(session, (WebPacket)message));
		}

	}

	public void messageSent(IoSession session, Object message) {}
	public void sessionIdle(IoSession session, IdleStatus status) {}
	public final void sessionClosed(IoSession session) {}
	
	public void sessionCreated(IoSession session) {
		session.getFilterChain().addFirst("protocolFilter", new ProtocolCodecFilter(new WebCodecFactory()));
		Logger.log(new ConnectionLog(((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress()));
	}

	public void sessionOpened(IoSession session) {
		session.setIdleTime(IdleStatus.BOTH_IDLE, 30);
		session.setWriteTimeout(30);
	}
}