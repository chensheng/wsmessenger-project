package space.chensheng.wsmessenger.message.converter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import space.chensheng.wsmessenger.message.component.WsMessage;

public class NettyMessageConverter {
	public static WsMessage<?> fromByteBuf(ByteBuf buf) {
		if (buf == null) {
			throw new NullPointerException("buf may not be null");
		}
		
		int readableBytes = buf.readableBytes();
		byte[] bytes = new byte[readableBytes];
		buf.readBytes(bytes);
		
		return BytesMessageConverter.fromBytes(bytes);
	}
	
	public static ByteBuf toByteBuf(WsMessage<?> message) {
		byte[] bytes = BytesMessageConverter.toBytes(message);
		return Unpooled.copiedBuffer(bytes);
	}
	
	public static BinaryWebSocketFrame toBinaryWebSocketFrame(WsMessage<?> message) {
		ByteBuf buf = toByteBuf(message);
		return new BinaryWebSocketFrame(buf);
	}
	
	public static WsMessage<?> fromBinaryWebSocketFrame(BinaryWebSocketFrame frame) {
		if (frame == null) {
			throw new NullPointerException("frame may not be null");
		}
		return fromByteBuf(frame.content());
	}
}
