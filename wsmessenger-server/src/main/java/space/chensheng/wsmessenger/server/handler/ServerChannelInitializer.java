package space.chensheng.wsmessenger.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import space.chensheng.wsmessenger.common.component.Messenger;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.server.NettyServer;
import space.chensheng.wsmessenger.server.component.ServerContext;

public class ServerChannelInitializer extends ChannelInitializer<Channel>{
	private ServerContext serverContext;
	
	private ChannelHandler wsMessageHandler;
	
	private ChannelHandler handshakeCompletedHandler;
	
	private ChannelHandler clientInfoResolveHandler;
	
	public ServerChannelInitializer(ServerContext serverContext, NettyServer server, 
			TaskExecutor taskExecutor, Messenger<WsMessage> messenger) {
		this.serverContext = serverContext;
		
		wsMessageHandler = new WsMessageHandler(taskExecutor, messenger);
		handshakeCompletedHandler = new HandshakeCompletedHandler(server);
		clientInfoResolveHandler = new ClientInfoResolveHandler(server);
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		pipeline.addLast("idleStateHandler", createIdleStateHandler());
		
		pipeline.addLast("httpServerCodec", createHttpServerCodec());
		
		pipeline.addLast("chunkedWriteHandler", createChunkedWriteHandler());
		
		pipeline.addLast("httpObjectAggregator", createHttpObjectAggregator());
		
		pipeline.addLast("clientInfoResolveHandler", createClientInfoResolveHandler());
		
		pipeline.addLast("webSocketServerProtocolHandler", createWebSocketServerProtocolHandler());
		
		pipeline.addLast("handshakeCompletedHandler", createHandshakeCompletedHandler());
		
		pipeline.addLast("wsMessageHandler", createWsMessageHandler());
		
		pipeline.addLast("heartbeatHandler", createHeartbeatHandler());
	}
	
	private ChannelHandler createHandshakeCompletedHandler() {
		return handshakeCompletedHandler;
	}
	
	private ChannelHandler createWsMessageHandler() {
		return wsMessageHandler;
	}
	
	private ChannelHandler createClientInfoResolveHandler() {
		return clientInfoResolveHandler;
	}
	
	private ChannelHandler createHttpServerCodec() {
		return new HttpServerCodec();
	}
	
	private ChannelHandler createChunkedWriteHandler() {
		return new ChunkedWriteHandler();
	}
	
	private ChannelHandler createHttpObjectAggregator() {
		return new HttpObjectAggregator(serverContext.getMaxContentLen());
	}
	
	private ChannelHandler createWebSocketServerProtocolHandler() {
		return new  PingableWebSocketServerProtocolHandler(null, null, true, serverContext.getMaxFrameSize());
	}
	
	private ChannelHandler createIdleStateHandler() {
		return new IdleStateHandler(serverContext.getHeartbeatIntervalSeconds(), 0, 0);
	}
	
	private ChannelHandler createHeartbeatHandler() {
		return new HeartbeatHandler(serverContext);
	}
}
