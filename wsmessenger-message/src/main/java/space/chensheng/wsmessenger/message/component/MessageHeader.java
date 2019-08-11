package space.chensheng.wsmessenger.message.component;

import space.chensheng.wsmessenger.message.util.MessageIdGenerator;

public class MessageHeader extends ByteableBean {
	@MessageOptions(order = 1)
	private String messageClass;
	
	@MessageOptions(order = 2)
	private short protocolVersion;
	
	@MessageOptions(order = 3)
	private long messageId;
	
	@MessageOptions(order = 4)
	private long createTime;
	
	@MessageOptions(order = 5)
	private boolean needResponse;
	
	@MessageOptions(order = 6, describeLastStrLen = true)
	private String senderId;
	
	public MessageHeader() {
		protocolVersion = 1;
		messageId = MessageIdGenerator.generate();
		createTime = System.currentTimeMillis();
	}

	public short getProtocolVersion() {
		return protocolVersion;
	}

	public long getMessageId() {
		return messageId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public boolean isNeedResponse() {
		return needResponse;
	}

	public void setNeedResponse(boolean needResponse) {
		this.needResponse = needResponse;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getMessageClass() {
		return messageClass;
	}

	void setMessageClass(String messageClass) {
		this.messageClass = messageClass;
	}

}
