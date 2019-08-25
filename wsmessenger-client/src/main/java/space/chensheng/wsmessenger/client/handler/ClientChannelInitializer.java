package space.chensheng.wsmessenger.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import space.chensheng.wsmessenger.client.NettyClient;
import space.chensheng.wsmessenger.client.component.ClientContext;
import space.chensheng.wsmessenger.client.component.ClientContextable;
import space.chensheng.wsmessenger.common.component.Messenger;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.common.util.WsMessengerConstants;
import space.chensheng.wsmessenger.message.component.WsMessage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ClientChannelInitializer extends ChannelInitializer<Channel> implements ClientContextable {
	private ClientContext clientContext;
	
	private NettyClient nettyClient;
	
	private Messenger<WsMessage> messenger;
	
	private TaskExecutor taskExecutor;
	
	public ClientChannelInitializer(ClientContext clientContext, NettyClient nettyClient, Messenger<WsMessage> messenger, TaskExecutor taskExecutor) {
	    if (clientContext == null) {
	    	throw new NullPointerException("clientContext may not be null");
	    }
	    
	    this.clientContext = clientContext;
	    this.nettyClient = nettyClient;
	    this.messenger = messenger;
	    this.taskExecutor = taskExecutor;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		pipeline.addLast("idleStateHandler", createIdleStateHandler());
		
		pipeline.addLast("httpClientCodec", createHttpClientCodec());
		
		pipeline.addLast("chunkedWriteHandler", createChunkedWriteHandler());
		
		pipeline.addLast("httpObjectAggregator", createHttpObjectAggregator());
		
		pipeline.addLast("webSocketClientHandler", createWebSocketClientProtocolHandler());
		
		pipeline.addLast("handshakeCompletedHandler", createHandshakeCompletedHandler());
		
		pipeline.addLast("wsMessageHandler", createWsMessageHandler());
		
		pipeline.addLast("heartbeatHandler", createHeartbeatHandler());
	}
	
	private ChannelHandler createIdleStateHandler() {
		return new IdleStateHandler(getClientContext().getHeartbeatSeconds(), 0, 0);
	}
	
	private ChannelHandler createHttpClientCodec() {
		return new HttpClientCodec();
	}
	
	private ChannelHandler createChunkedWriteHandler() {
		return new ChunkedWriteHandler();
	}
	
	private ChannelHandler createHttpObjectAggregator() {
		return new HttpObjectAggregator(clientContext.getMaxContentLen());
	}
	
	private ChannelHandler createWebSocketClientProtocolHandler() {
		URI serverUri = null;
		try {
			serverUri = new URI(getClientContext().getServerUrl());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(getClientContext().getServerUrl() + " is not correct format");
		}
		
		HttpHeaders customHeaders = new DefaultHttpHeaders();
		if (clientContext.getClientHeaders() != null) {
			for (Map.Entry<String, String> entry : clientContext.getClientHeaders().entrySet()) {
			    customHeaders.add(entry.getKey(), entry.getValue());
            }
		}
        customHeaders.add(WsMessengerConstants.CUSTOM_HEADER_CLIENT_ID, getClientContext().getClientId());
		
		WebSocketClientProtocolHandler wsHandler = new PingableWebSocketClientProtocolHandler(serverUri, WebSocketVersion.V13, 
				null, false, customHeaders, clientContext.getMaxFramePayloadLen());
		return wsHandler;
	}
	
	private ChannelHandler createHandshakeCompletedHandler() {
		return new HandshakeCompletedHandler(nettyClient, taskExecutor);
	}
	
	private ChannelHandler createWsMessageHandler() {
		return new WsMessageHandler(messenger, taskExecutor);
	}
	
	private ChannelHandler createHeartbeatHandler() {
		return new HeartbeatHandler(clientContext);
	}

	@Override
	public ClientContext getClientContext() {
		return clientContext;
	}
}
