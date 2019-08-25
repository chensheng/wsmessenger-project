package space.chensheng.wsmessenger.message.component;

import space.chensheng.wsmessenger.message.converter.SerializeType;
import space.chensheng.wsmessenger.message.util.MessageIdGenerator;

public class MessageHeader {
	private SerializeType serializeType = SerializeType.JSON;

	private String messageClass;

	private short protocolVersion;

	private long messageId;

	private long createTime;

	private boolean needResponse;

	private String senderId;
	
	public MessageHeader() {
		protocolVersion = 1;
		messageId = MessageIdGenerator.generate();
		createTime = System.currentTimeMillis();
	}

    public SerializeType getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    public String getMessageClass() {
        return messageClass;
    }

    public void setMessageClass(String messageClass) {
        this.messageClass = messageClass;
    }

    public short getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(short protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
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
}
