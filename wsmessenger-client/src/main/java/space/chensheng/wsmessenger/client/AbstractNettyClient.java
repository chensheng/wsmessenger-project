package space.chensheng.wsmessenger.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.concurrent.GenericFutureListener;
import space.chensheng.wsmessenger.client.component.ClientContext;
import space.chensheng.wsmessenger.client.component.ClientContextable;
import space.chensheng.wsmessenger.client.component.SenderCallback;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.message.converter.NettyMessageConverter;

/**
 * A abstract class implements basic functions to communicate server built upon netty. 
 * @author sheng.chen
 */
public abstract class AbstractNettyClient implements NettyClient, ClientContextable {
	private static final Logger logger = LoggerFactory.getLogger(AbstractNettyClient.class);
	
	private ClientContext clientContext;
	
	private TaskExecutor taskExecutor;
	
	private URI serverUri;
	
	private Bootstrap bootstrap;
	
	private EventLoopGroup ioThreadPool;
	
	private Channel clientChannel;
	
	private AtomicBoolean connFlag = new AtomicBoolean(false);
	
	private Semaphore connSemaphore = new Semaphore(1);
	
	private Semaphore disconnSemaphore = new Semaphore(1);
	
	private GenericFutureListener<ChannelFuture> clientConnectListener = new ClientConnectListener();

	private GenericFutureListener<ChannelFuture> clientCloseListener = new ClientCloseListener();
	
	private Runnable reconnectTask = new ReconnectTask();
	
	/**
	 * 
	 * @param clientContext
	 * @param taskExecutor
	 * @throws NullPointerException if {@code clientContext} or {@code taskExecutor} is null
	 */
	public AbstractNettyClient(ClientContext clientContext, TaskExecutor taskExecutor) {
		if (clientContext == null) {
			throw new NullPointerException("clientContext may not be null");
		}
		
		if (taskExecutor == null) {
			throw new NullPointerException("taskExecutor may not be null");
		}
		
		this.clientContext = clientContext;
		this.serverUri = resolveServerURI();
		this.taskExecutor = taskExecutor;
		
		initClient();
	}
	
	/**
	 * Start netty client which will establish a long connection to netty server.
	 * It will return false if the client has already started 
	 * or another thread is trying to start this client. 
	 * This method is thread safe.
	 * @param true if success to  start, otherwise false
	 */
	@Override
	public boolean start() {
		if(isActive()) {
			logger.info("client has already been started, no need to start again!!!");
			return false;
		}
		
		if(!connSemaphore.tryAcquire()) {
			logger.info("another thread is doning start job");
			return false;
		}
		
		onStart();
		ChannelFuture connectFuture = bootstrap.connect(serverUri.getHost(), serverUri.getPort());
		connectFuture.addListener(clientConnectListener);
		
		return true;
	}
	
	/**
	 * Stop netty client. It will close the long connection to netty server.
	 * All resources will be released, including the business thread pool.
	 * This method is thread safe.
	 */
	@Override
	public boolean stop() {
		if (ioThreadPool == null) {
			return true;
		}
		
		if(!disconnSemaphore.tryAcquire()) {
			logger.info("another thread is doning stop job");
			return false;
		}
		
		try {
			onStop();
			if (clientChannel != null) {
				clientChannel.close();
				clientChannel = null;
			}
			
			if (ioThreadPool != null) {
				ioThreadPool.shutdownGracefully();
				ioThreadPool = null;
			}
			
			if (taskExecutor != null) {
				taskExecutor.shutdown();
			}
			
			connFlag.set(false);
			logger.info("success to stop netty client");
		} finally {
			disconnSemaphore.release();
		}
		
		return true;
	}
	
	/**
	 * Send message to netty server, and trigger the callback later.
	 * @param msg
	 * @param callback
	 */
	protected void sendMessage(WsMessage<?> msg, SenderCallback callback) {
		if (msg == null) {
			return;
		}
		
		if (clientChannel == null) {
			logger.error("client channel is not ready, msg will not be sent at this moment.");
			if (callback != null) {
				callback.onFail(msg);
			}
			return;
		}
		
		msg.header().setSenderId(getClientContext().getClientId());
		BinaryWebSocketFrame bwsFrame = NettyMessageConverter.toBinaryWebSocketFrame(msg);
		ChannelFuture sendFuture = clientChannel.writeAndFlush(bwsFrame);
		sendFuture.addListener(new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
					logger.info("fail to send message, cause:{}, message: {}", future.cause(), msg);
					if (callback != null) {
						callback.onFail(msg);
					}
				} else {
					logger.debug("success to send message: {}.", msg);
					if (callback != null) {
						callback.onSuccess(msg);
					}
				}
			}
			
		});
	}
	
	@Override
	public boolean isActive() {
		return connFlag.get();
	}
	
	@Override
	public boolean restart() {
		onRestart();
		throw new UnsupportedOperationException("netty client do not support restart");
	}
	
	@Override
	public ClientContext getClientContext() {
		return clientContext;
	}
	
	@Override
	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}
	
	private void initClient() {
		ChannelHandler channelHandler = this.createChannelHandler();
		if(channelHandler == null) {
			throw new NullPointerException("createChannelHandler() may not return null");
		}
		
		ioThreadPool = new NioEventLoopGroup(getClientContext().getIoThreadPoolSize());
		bootstrap = new Bootstrap().group(ioThreadPool).channel(NioSocketChannel.class).handler(channelHandler);
	    bootstrap.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 1024*1024);
	}
	
	private URI resolveServerURI() {
		try {
			URI uri = new URI(getClientContext().getServerUrl());
			return uri;
		} catch (URISyntaxException e) {
			logger.error(e.toString());
			throw new IllegalArgumentException(getClientContext().getServerUrl() + " is not correct format");
		}
	}
	
	private void doReconnect() {
		if (connFlag.get()) {
			logger.info("has already connected to server");
			return;
		}
		taskExecutor.scheduleTask(reconnectTask, getClientContext().getReconnectMillis(), TimeUnit.MILLISECONDS);
	}
	
	private class ClientConnectListener implements GenericFutureListener<ChannelFuture> {

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			connSemaphore.release();
			if (future.isSuccess()) {
				connFlag.set(true);
				logger.info("success to connect server {}.", serverUri);
				clientChannel = future.channel();
				clientChannel.closeFuture().addListener(clientCloseListener);
			} else {
				logger.error("fail to connect server {}, cause {}", serverUri, future.cause());
				doReconnect();
			}
		}
		
	}
	
	private class ClientCloseListener implements GenericFutureListener<ChannelFuture> {

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			connFlag.set(false);
			logger.info("The client was closed, cause {}", future.cause());
			doReconnect();
		}
		
	}
	
	private class ReconnectTask implements Runnable {
		
		@Override
		public void run() {
			start();
		}
	}
}
