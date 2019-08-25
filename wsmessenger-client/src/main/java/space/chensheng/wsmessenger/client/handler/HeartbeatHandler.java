package space.chensheng.wsmessenger.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.chensheng.wsmessenger.client.component.ClientContext;
import space.chensheng.wsmessenger.common.util.ExceptionUtil;

public class HeartbeatHandler extends SimpleChannelInboundHandler<PongWebSocketFrame>{
	private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);
	
	private ClientContext clientContext;
	
	private int readIdleCount = 0;
	
	HeartbeatHandler(ClientContext clientContext) {
		this.clientContext = clientContext;
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt == IdleStateEvent.READER_IDLE_STATE_EVENT) {
			readIdleCount++;
			if (readIdleCount > clientContext.getHeartbeatMaxFail()) {
				logger.debug("read idle count exceed {}, channel will be closed.", clientContext.getHeartbeatMaxFail());
				ctx.close();
			}
			ctx.pipeline().writeAndFlush(new PingWebSocketFrame());
		}
		
		super.userEventTriggered(ctx, evt);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PongWebSocketFrame msg) throws Exception {
		logger.debug("receive PongWebSocketFrame.");
		readIdleCount = 0;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("an exception occur, cause:{}", ExceptionUtil.getExceptionDetails(cause));
	}
}
