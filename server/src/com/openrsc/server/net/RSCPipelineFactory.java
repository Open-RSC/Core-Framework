package com.openrsc.server.net;
//package com.rscr.server.net;
//
//import org.jboss.netty.channel.ChannelPipeline;
//import org.jboss.netty.channel.ChannelPipelineFactory;
//import org.jboss.netty.channel.Channels;
//import org.jboss.netty.handler.timeout.IdleStateHandler;
//import org.jboss.netty.util.Timer;
//
//public class RSCPipelineFactory implements ChannelPipelineFactory {
//	
//	private IdleStateHandler idleStateHandler;
//	
//	public RSCPipelineFactory(Timer timer) {
////		this.idleStateHandler = new IdleStateHandler(timer, 15, 15, 15);
//	}
//	
//	public ChannelPipeline getPipeline() {
//		ChannelPipeline pipeline = Channels.pipeline();
////		pipeline.addLast("idleStateHandler", idleStateHandler);
//		pipeline.addLast("decoder", new RSCProtocolDecoder());
//		pipeline.addLast("encoder", new RSCProtocolEncoder());
//		pipeline.addLast("handler", new RSCConnectionHandler());
//		return pipeline;
//	}
//
//}
