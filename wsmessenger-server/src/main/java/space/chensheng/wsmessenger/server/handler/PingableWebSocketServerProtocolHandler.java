package space.chensheng.wsmessenger.server.handler;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class PingableWebSocketServerProtocolHandler extends WebSocketServerProtocolHandler{

	public PingableWebSocketServerProtocolHandler(String websocketPath) {
		super(websocketPath);
	}
	
	public PingableWebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize) {
		super(websocketPath, subprotocols, allowExtensions, maxFrameSize);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
		if (frame instanceof PongWebSocketFrame) {
			out.add(frame.retain());
		} else {
			super.decode(ctx, frame, out);
		}
	}
}
