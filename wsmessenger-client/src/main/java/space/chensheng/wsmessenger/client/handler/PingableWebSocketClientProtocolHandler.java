package space.chensheng.wsmessenger.client.handler;

import java.net.URI;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

public class PingableWebSocketClientProtocolHandler extends WebSocketClientProtocolHandler{

	public PingableWebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol,
			boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength) {
		super(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength);
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
