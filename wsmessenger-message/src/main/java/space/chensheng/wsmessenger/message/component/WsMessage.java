package space.chensheng.wsmessenger.message.component;

import space.chensheng.wsmessenger.common.util.JsonMapper;
import space.chensheng.wsmessenger.message.converter.SerializeType;

/**
 * The base message for developer to implement. The subclass should provide a non-argument constructor which is used to convert bytes to concrete message.
 * @author sheng.chen
 */

public abstract class WsMessage {
	private MessageHeader header;

	public WsMessage() {
		header = new MessageHeader();
		header.setMessageClass(this.getClass().getName());
		header.setSerializeType(SerializeType.JSON);
	}

    public MessageHeader getHeader() {
        return header;
    }

    public void setHeader(MessageHeader header) {
        this.header = header;
    }

    @Override
	public String toString() {
		return JsonMapper.nonEmptyMapper().toJson(this);
	}
}
