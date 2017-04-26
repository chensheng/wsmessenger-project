package space.chensheng.wsmessenger.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import space.chensheng.wsmessenger.common.util.ExceptionUtil;
import space.chensheng.wsmessenger.server.clientmng.ClientInfo;
import space.chensheng.wsmessenger.server.clientmng.ClientRegistry;
import space.chensheng.wsmessenger.server.component.ServerContext;

public class HeartbeatHandler extends SimpleChannelInboundHandler<PongWebSocketFrame>{
	private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);
	
	private ServerContext serverContext;
	
	private int readIdleCount = 0;
	
	public HeartbeatHandler(ServerContext serverContext) {
		this.serverContext = serverContext;
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		
		if (evt == IdleStateEvent.READER_IDLE_STATE_EVENT) {
			readIdleCount++;
			logger.debug("read idle state event triggered. client {}. read idle count is {}.", ctx.channel().remoteAddress(), readIdleCount);
			if (readIdleCount > serverContext.getHeartbeatMaxFail()) {
				ClientInfo clientInfo = ClientRegistry.resolveClientInfo(ctx.channel());
				logger.error("read idle count exceed {}, cleint {} will be closed.", serverContext.getHeartbeatMaxFail(), clientInfo!=null?clientInfo.getClientIp():"unkown");
				ctx.close();
			}
			ctx.pipeline().writeAndFlush(new PingWebSocketFrame());
		}
		
		super.userEventTriggered(ctx, evt);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PongWebSocketFrame msg) throws Exception {
		logger.debug("receive PongWebSocketFrame from client {}.", ctx.channel().remoteAddress());
		readIdleCount = 0;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("an exception occur, cause:{}", ExceptionUtil.getExceptionDetails(cause));
	}
}
