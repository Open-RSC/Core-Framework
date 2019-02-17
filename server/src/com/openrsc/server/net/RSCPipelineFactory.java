package com.openrsc.server.net;

import com.openrsc.server.Constants;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.util.Timer;

public class RSCPipelineFactory extends ThrottleFilter implements ChannelPipelineFactory {
	
	private IdleStateHandler idleStateHandler;
	
	public RSCPipelineFactory(Timer timer) {
		this.idleStateHandler = new IdleStateHandler(timer, 15, 15, 15);
	}

	public ChannelPipeline getPipeline() {
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("idleStateHandler", idleStateHandler);
		pipeline.addLast("decoder", new RSCProtocolDecoder());
		pipeline.addLast("encoder", new RSCProtocolEncoder());
		pipeline.addLast("timeout", (ChannelHandler) new io.netty.handler.timeout.IdleStateHandler(Constants.GameServer.CONNECTION_TIMEOUT, 0, 0));
		pipeline.addLast("handler", new RSCConnectionHandler());
		return pipeline;
	}

}
