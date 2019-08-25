package space.chensheng.wsmessenger.message.converter;

import space.chensheng.wsmessenger.message.component.MessageHeader;
import space.chensheng.wsmessenger.message.component.WsMessage;

public interface SerializeStrategy {
    MessageHeader deserializeHeader(byte[] bytes);

    boolean supports(MessageHeader header);

    byte[] serialize(WsMessage message);

    WsMessage deserialize(byte[] bytes, String messageType);
}
