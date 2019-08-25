package space.chensheng.wsmessenger.message.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.chensheng.wsmessenger.common.util.ExceptionUtil;
import space.chensheng.wsmessenger.common.util.JsonMapper;
import space.chensheng.wsmessenger.message.component.EmptyWsMessage;
import space.chensheng.wsmessenger.message.component.MessageHeader;
import space.chensheng.wsmessenger.message.component.WsMessage;

import java.io.UnsupportedEncodingException;

public class JsonSerializeStrategy implements SerializeStrategy {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializeStrategy.class);

    private static final byte[] EMPTY_BYTES = new byte[0];

    private static final String DEFAULT_CHARSET = "UTF-8";

    @Override
    public MessageHeader deserializeHeader(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        String msgContent;
        try {
            msgContent = new String(bytes, DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error(ExceptionUtil.getExceptionDetails(e));
            return null;
        }

        EmptyWsMessage wsMessage = JsonMapper.nonEmptyMapper().fromJson(msgContent, EmptyWsMessage.class);
        if (wsMessage == null) {
            return null;
        }

        return wsMessage.getHeader();
    }

    @Override
    public boolean supports(MessageHeader header) {
        if (header == null) {
            return false;
        }

        if (header.getSerializeType() == null) {
            return true;
        }

        return header.getSerializeType() == SerializeType.JSON;
    }

    @Override
    public byte[] serialize(WsMessage message) {
        if (message == null) {
            return EMPTY_BYTES;
        }

        String msgContent = JsonMapper.nonEmptyMapper().toJson(message);
        if (msgContent == null) {
            return EMPTY_BYTES;
        }

        try {
            return msgContent.getBytes(DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error(ExceptionUtil.getExceptionDetails(e));
        }
        return EMPTY_BYTES;
    }

    @Override
    public WsMessage deserialize(byte[] bytes, String messageType) {
        if (bytes == null) {
            return null;
        }

        Class<?> msgType = null;
        try {
            msgType = Class.forName(messageType);
        } catch (ClassNotFoundException e) {
            logger.error(ExceptionUtil.getExceptionDetails(e));
            return null;
        }

        if (!WsMessage.class.isAssignableFrom(msgType)) {
            logger.error("message type {} is not sub class of {}", msgType, WsMessage.class);
            return null;
        }

        String msgContent;
        try {
            msgContent = new String(bytes, DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error(ExceptionUtil.getExceptionDetails(e));
            return null;
        }

        return (WsMessage) JsonMapper.nonEmptyMapper().fromJson(msgContent, msgType);
    }

}
