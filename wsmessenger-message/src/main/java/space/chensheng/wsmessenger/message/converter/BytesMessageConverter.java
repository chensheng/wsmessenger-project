package space.chensheng.wsmessenger.message.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import space.chensheng.wsmessenger.common.util.ExceptionUtil;
import space.chensheng.wsmessenger.common.util.StringUtil;
import space.chensheng.wsmessenger.message.component.MessageHeader;
import space.chensheng.wsmessenger.message.component.WsMessage;

public class BytesMessageConverter {
	private static final Logger logger = LoggerFactory.getLogger(BytesMessageConverter.class);
	
	public static byte[] toBytes(WsMessage<?> message) {
		if (message == null) {
			throw new NullPointerException("message may not be null");
		}
		return message.toBytes();
	}

	public static WsMessage<?> fromBytes(byte[] bytes) {
		if (bytes == null) {
			throw new NullPointerException("bytes may not be null");
		}

		MessageHeader header = new MessageHeader();
		header.fromBytes(bytes);
		
		String messageClassName = header.getMessageClass();
		Class<?> messageClass = findMessageClass(messageClassName);
		if (messageClass == null) {
			return null;
		}
		
		return newMessage(bytes, messageClass);
	}
	
	private static Class<?> findMessageClass(String messageClassName) {
		if (StringUtil.isEmpty(messageClassName)) {
			logger.error("Fail to convert message which lacks of header: {}!", messageClassName);
			return null;
		}
		
		Class<?> messageClass = null;
		
		try {
			messageClass = Class.forName(messageClassName);
		} catch (ClassNotFoundException e) {
			logger.error("Could not find message class: {}.", messageClassName);
			return null;
		}
		
		if (!WsMessage.class.isAssignableFrom(messageClass)) {
			logger.error("Class {} is not a subclass of {}", messageClass, WsMessage.class.getName());
			return null;
		}
		
		return messageClass;
	}

	private static WsMessage<?> newMessage(byte[] bytes, Class<?> msgClzz) {
		try {
			WsMessage<?> msg = (WsMessage<?>) msgClzz.newInstance();
			msg.fromBytes(bytes);
			return msg;
		} catch (InstantiationException e) {
			logger.error(ExceptionUtil.getExceptionDetails(e));
		} catch (IllegalAccessException e) {
			logger.error(ExceptionUtil.getExceptionDetails(e));
		}
		return null;
	}
}
