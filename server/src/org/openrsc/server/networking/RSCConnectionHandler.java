package org.openrsc.server.networking;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ConnectionLog;
import org.openrsc.server.model.Player;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.util.Pair;

public class RSCConnectionHandler
implements IoHandler
{
	
	private final BlockingQueue<Pair<IoSession, RSCPacket>> messageQueue;
		
	public RSCConnectionHandler(BlockingQueue<Pair<IoSession, RSCPacket>> messageQueue)
	{
		this.messageQueue = messageQueue;
	}

	public void exceptionCaught(IoSession session, Throwable cause)
	{
		session.close();
	}

	public void messageReceived(IoSession session, Object message)
	{
		/** Remove These Hacks */
		Player player = session.getAttachment() instanceof Player ? (Player)session.getAttachment() : null;
		if(player != null)
		{
			if ((session.isClosing()) || (player.destroyed()) || (player.isUnregistered()))
				return;
		}
		else
		{
			if(session.getAttachment() != null && !(session.getAttachment() instanceof Long))
			{
				return;
			}
		}
		/** LOL Remove These Hacks */
		
		RSCPacket p = (RSCPacket)message;
		if ((p.getID() > 1) && (p.getID() < 92)) { /* ?????? */
			if (player == null || player.addPacket(p))
				messageQueue.add(new Pair<IoSession, RSCPacket>(session, p));
		}
		else {
			session.close();
		}
	}

	public void messageSent(IoSession session, Object message) { }

	public final void sessionClosed(IoSession session)
	{
		Player player = (Player)session.getAttachment();
		if (player != null) {
			player.destroy(false);
		}
	}

	private final ConnectionLimiter limiter = new ConnectionLimiter(5, 400);
	/*private final static RSCPacket PROXY_DENIED;
	private final static RSCPacket ALLOW_LOGIN;
	
	static
	{
		RSCPacketBuilder builder = new RSCPacketBuilder();
		builder.setBare(true);
		builder.addByte((byte)1);
		PROXY_DENIED = builder.toPacket();
		
		builder = new RSCPacketBuilder();
		builder.setBare(true);
		builder.addByte((byte)0);
		ALLOW_LOGIN = builder.toPacket();

	}*/
	public void sessionCreated(IoSession session)
	{
		//InetSocketAddress ip = (InetSocketAddress)session.getRemoteAddress();
		Logger.log(new ConnectionLog(((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress()));
		if(!limiter.addConnection(((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress().toString()))
		{
			return;
		}
		
		session.getFilterChain().addFirst("protocolFilter", new ProtocolCodecFilter(new RSCCodecFactory()));
		
/*		try
		{
			String reply = "220 mail.openrsc.com ESMTP Postfix (Debian/GNU)";
			//proxycheck -vv -ddsthost:dstport -c chat::"waitstr" list-of-IPs
			// unfortunately, we have to test this on the linux box...safe? It looks like it should be. ok, I'll send you a command to run
			// we need the string returned by it.
			
			if(Runtime.getRuntime().exec("/scripts/proxycheck -dmail.openrsc.com:25 -c chat::\" + reply + \" -s " + ip.getHostName()).waitFor() != 0)
			{
				session.write(PROXY_DENIED);
				session.close();
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}*/
	}

	public void sessionIdle(IoSession session, IdleStatus status)
	{
		Player player = (Player)session.getAttachment();
		if (!player.destroyed())
			player.destroy(false);
		session.close();
	}

	public void sessionOpened(IoSession session)
	{
		session.setIdleTime(IdleStatus.BOTH_IDLE, 30);
		session.setWriteTimeout(30);
	}
}
