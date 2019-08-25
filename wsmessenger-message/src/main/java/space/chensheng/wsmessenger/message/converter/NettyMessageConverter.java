package space.chensheng.wsmessenger.message.converter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.chensheng.wsmessenger.message.component.MessageHeader;
import space.chensheng.wsmessenger.message.component.WsMessage;

import java.util.ArrayList;
import java.util.List;

public class NettyMessageConverter {
    private static final Logger logger = LoggerFactory.getLogger(NettyMessageConverter.class);

	private static List<SerializeStrategy> serializeStrategies = new ArrayList<SerializeStrategy>();

	static {
	    serializeStrategies.add(new JsonSerializeStrategy());
    }

	public static WsMessage fromByteBuf(ByteBuf buf) {
		if (buf == null) {
			return null;
		}
		
		int readableBytes = buf.readableBytes();
		byte[] bytes = new byte[readableBytes];
		buf.readBytes(bytes);

		MessageHeader header = null;
		SerializeStrategy serializeStrategy = null;
		for (SerializeStrategy strategy : serializeStrategies) {
		    header = strategy.deserializeHeader(bytes);
		    if (strategy.supports(header)) {
		        serializeStrategy = strategy;
		        break;
            }
        }
		if (serializeStrategy == null) {
		    logger.error("Could not find message serialize strategy");
		    return null;
        }

		return serializeStrategy.deserialize(bytes, header.getMessageClass());
	}
	
	public static ByteBuf toByteBuf(WsMessage message) {
	    if (message == null) {
	        return null;
        }

	    SerializeStrategy serializeStrategy = null;
        for (SerializeStrategy strategy : serializeStrategies) {
            if (strategy.supports(message.getHeader())) {
                serializeStrategy = strategy;
                break;
            }
        }
        if (serializeStrategy == null) {
            logger.error("Could not find message serialize strategy for message {}", message);
            return null;
        }

		byte[] bytes = serializeStrategy.serialize(message);
		return Unpooled.copiedBuffer(bytes);
	}
	
	public static BinaryWebSocketFrame toBinaryWebSocketFrame(WsMessage message) {
		ByteBuf buf = toByteBuf(message);
		return new BinaryWebSocketFrame(buf);
	}
	
	public static WsMessage fromBinaryWebSocketFrame(BinaryWebSocketFrame frame) {
		if (frame == null) {
			throw new NullPointerException("frame may not be null");
		}
		return fromByteBuf(frame.content());
	}

}
