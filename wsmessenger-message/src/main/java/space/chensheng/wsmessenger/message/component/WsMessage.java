package space.chensheng.wsmessenger.message.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import space.chensheng.wsmessenger.common.util.JsonMapper;

/**
 * The base message for developer to implement. The subclass should provide a non-argument constructor which is used to convert bytes to concrete message.
 * @author sheng.chen
 */

public abstract class WsMessage<T extends MessageBody> extends ByteableBean {

	private MessageHeader header;

	private T body;
	
	/**
	 * 
	 * @param messageBody
	 * @throws NullPointerException if messageBody is null
	 */
	public WsMessage(T messageBody) {
		if (messageBody == null) {
			throw new NullPointerException("messageBody may not be null");
		}
		
		this.initHeader();
		this.body = messageBody;
	}
	
	public MessageHeader header() {
		return header;
	}
	
	public T body() {
		return body;
	}
	
	@Override
	public byte[] toBytes() {
		if (bytes == null) {
			byte[] headerBytes = header().toBytes();
			byte[] bodyBytes = body().toBytes();
			
			int totalBytesLen = headerBytes.length + bodyBytes.length;
			ByteBuf buf = Unpooled.buffer(totalBytesLen);
			buf.writeBytes(headerBytes);
			buf.writeBytes(bodyBytes);
			
			bytes = new byte[totalBytesLen];
			buf.readBytes(bytes);
		}
		
		return bytes;
	}
	
	@Override
	public byte[] fromBytes(byte[] bytes) {
		if (bytes == null) {
			throw new NullPointerException("bytes may not be null");
		}
		
		byte[] bodyBytes = header().fromBytes(bytes);
		return body().fromBytes(bodyBytes);
	}
	
	private void initHeader() {
		header = new MessageHeader();
		header.setMessageClass(this.getClass().getName());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("header:");
		sb.append(JsonMapper.nonEmptyMapper().toJson(header));
		sb.append(" ");
		sb.append("body:");
		sb.append(JsonMapper.nonEmptyMapper().toJson(body));
		return sb.toString();
	}
}
