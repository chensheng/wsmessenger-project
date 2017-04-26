package space.chensheng.wsmessenger.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import space.chensheng.wsmessenger.client.NettyClient;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;

public class HandshakeCompletedHandler extends ChannelInboundHandlerAdapter{
	private static final Logger logger = LoggerFactory.getLogger(HandshakeCompletedHandler.class);
	
	private TaskExecutor taskExecutor;
	
	private NettyClient nettyClient;
	
	public HandshakeCompletedHandler(NettyClient nettyClient, TaskExecutor taskExecutor) {
		if (nettyClient == null) {
			throw new NullPointerException("nettyClient may not be null");
		}
		if (taskExecutor == null) {
			throw new NullPointerException("taskExecutor may not be null");
		}
		this.taskExecutor = taskExecutor;
		this.nettyClient = nettyClient;
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {
			logger.info("netty client complete handshake!!!");
			taskExecutor.executeTask(new Runnable() {

				@Override
				public void run() {
					nettyClient.onConnected();
				}
				
			});
		}
		super.userEventTriggered(ctx, evt);
	}
}
