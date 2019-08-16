package com.openrsc.server.net;

import com.openrsc.server.Server;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;

public class RSCPipelineFactory extends ThrottleFilter implements ChannelPipelineFactory {
	
	private IdleStateHandler idleStateHandler;
	private final Server server;
	
	public RSCPipelineFactory(Server server, Timer timer) {
		this.server = server;
		this.idleStateHandler = new IdleStateHandler(timer, 15, 15, 15);
	}

	public ChannelPipeline getPipeline() {
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("idleStateHandler", idleStateHandler);
		pipeline.addLast("decoder", new RSCProtocolDecoder());
		pipeline.addLast("encoder", new RSCProtocolEncoder());
		pipeline.addLast("timeout", (ChannelHandler) new io.netty.handler.timeout.IdleStateHandler(getServer().getConfig().CONNECTION_TIMEOUT, 0, 0));
		pipeline.addLast("handler", new RSCConnectionHandler(getServer()));
		return pipeline;
	}

	public Server getServer() {
		return server;
	}
}
