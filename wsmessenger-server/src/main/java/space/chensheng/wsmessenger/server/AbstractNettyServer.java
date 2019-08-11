package space.chensheng.wsmessenger.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.message.converter.NettyMessageConverter;
import space.chensheng.wsmessenger.server.clientmng.ClientInfo;
import space.chensheng.wsmessenger.server.clientmng.ClientRegistry;
import space.chensheng.wsmessenger.server.component.SenderCallback;
import space.chensheng.wsmessenger.server.component.ServerContext;

/**
 * A abstract class implements basic functions to accept client's connection built upon netty. 
 * @author sheng.chen
 */
public abstract class AbstractNettyServer implements NettyServer {
	private static final Logger logger = LoggerFactory.getLogger(AbstractNettyServer.class);
	
	private ServerBootstrap serverBootstrap;
	
	private EventLoopGroup acceptorLoopGroup;
	
	private EventLoopGroup ioLoopGroup;
	
	private Channel serverChannel;
	
	private GenericFutureListener<ChannelFuture> bindFutureListener = new BindFutureListener();

	
	private ServerContext serverContext;
	
	private TaskExecutor taskExecutor;
	
	/**
	 * 
	 * @param serverContext
	 * @param taskExecutor
	 * @throws NullPointerException if {@code serverContext} or {@code taskExecutor} is null
	 */
	public AbstractNettyServer(ServerContext serverContext, TaskExecutor taskExecutor) {
		if (serverContext == null) {
			throw new NullPointerException("serverContext may not be null");
		}
		if (taskExecutor == null) {
			throw new NullPointerException("taskExecutor may not be null");
		}
		this.serverContext = serverContext;
		this.taskExecutor = taskExecutor;
		
		initServer();
	}
	
	@Override
	public boolean start() {
		ChannelFuture bindFuture = serverBootstrap.bind(serverContext.getServerPort());
		bindFuture.addListener(bindFutureListener);
		return true;
	}
	
	@Override
	public boolean stop() {
		if (taskExecutor != null) {
			taskExecutor.shutdown();
			taskExecutor = null;
		}
		
		if (serverChannel != null) {
			serverChannel.close();
			serverChannel = null;
		}
		
		if (acceptorLoopGroup != null) {
			acceptorLoopGroup.shutdownGracefully();
			acceptorLoopGroup = null;
		}
		
		if (ioLoopGroup != null) {
			ioLoopGroup.shutdownGracefully();
			ioLoopGroup = null;
		}
		
		return true;
	}
	
	@Override
	public boolean restart() {
		return false;
	}
	
	@Override
	public ServerContext getServerContext() {
		return serverContext;
	}
	
	@Override
	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}
	
	private void initServer() {
		ChannelHandler channelHandler = createChannelHandler();
		if (channelHandler == null) {
			throw new NullPointerException("createChannelHandler() may not return null");
		}
		
		acceptorLoopGroup = new NioEventLoopGroup(serverContext.getAcceptorThreadSize());
		ioLoopGroup = new NioEventLoopGroup(serverContext.getIoThreadSize());
		
		serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(acceptorLoopGroup, ioLoopGroup)
		               .channel(NioServerSocketChannel.class)
		               .childHandler(channelHandler)
		               .option(ChannelOption.SO_BACKLOG, serverContext.getSoBacklog())
		               .childOption(ChannelOption.ALLOW_HALF_CLOSURE, serverContext.isAllowHalfClosure());
	}
	
	private class BindFutureListener implements GenericFutureListener<ChannelFuture> {

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				logger.info("success to start server at port {}", serverContext.getServerPort());
				serverChannel = future.channel();
				taskExecutor.executeTask(new Runnable() {

					@Override
					public void run() {
						onStarted();
					}
					
				});
			} else {
				logger.error("fail to bind port {}, cause {}.", serverContext.getServerPort(), future.cause());
				stop();
			}
		}
		
	}
	
	/**
	 * Send message to all connected clients.
	 * @param message
	 * @param callback
	 */
	protected void sendMessage(WsMessage<?> message, SenderCallback callback) {
		if (message == null) {
			return;
		}
		
		this.decorateMessage(message);
		ChannelGroup channelGroup = ClientRegistry.getInstance().getClientGroup();
	
		BinaryWebSocketFrame bwsFrame = NettyMessageConverter.toBinaryWebSocketFrame(message);
		ChannelGroupFuture sendFuture = channelGroup.writeAndFlush(bwsFrame);
		sendFuture.addListener(new GenericFutureListener<ChannelGroupFuture>() {

			@Override
			public void operationComplete(ChannelGroupFuture future) throws Exception {
				if (!future.isSuccess()) {
					logger.error("fail to send message to all clients", message);
					if (callback != null) {
						callback.onFail(message, null);
					}
				} else {
					logger.debug("success to send message {} to all clients", message);
					if (callback != null) {
						callback.onSuccess(message, null);
					}
				}
			}
			
		});
	}
	
	/**
	 * Send message to specify client by clientId.
	 * @param message
	 * @param clientId
	 * @param callback
	 */
	protected void sendMessage(WsMessage<?> message, String clientId, SenderCallback callback) {
		if (message == null) {
			return;
		}
		
		this.decorateMessage(message);
		
		Channel clientChannel = ClientRegistry.getInstance().findClient(clientId);
		if (clientChannel == null) {
			logger.error("fail to send message {} to client {}, cause client not found", message, clientId);
			if (callback != null) {
				callback.onFail(message, clientId);
			}
			return;
		}
		
		if (!clientChannel.isWritable()) {
			ClientInfo clientInfo = ClientRegistry.resolveClientInfo(clientChannel);
			logger.error("client {} is not writable, try to detegister it", clientInfo);
			ClientRegistry.getInstance().deregister(clientChannel);
			return;
		}
		
		BinaryWebSocketFrame bwsFrame = NettyMessageConverter.toBinaryWebSocketFrame(message);
		ChannelFuture sendFuture = clientChannel.pipeline().writeAndFlush(bwsFrame);
		sendFuture.addListener(new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				ClientInfo clientInfo = space.chensheng.wsmessenger.server.clientmng.ClientRegistry.resolveClientInfo(clientChannel);
				if (!future.isSuccess()) {
					logger.error("fail to send message {} to client {}, cause {}.", message, clientInfo, future.cause());
					if (callback != null) {
						callback.onFail(message, clientId);
					}
				} else {
					logger.debug("success to send message {} to client {}.", message, clientInfo);
					if (callback != null) {
						callback.onSuccess(message, clientId);
					}
				}
			}
			
		});
	}
	
	private void decorateMessage(WsMessage<?> msg) {
		msg.header().setSenderId(getServerContext().getServerId());
	}
}
