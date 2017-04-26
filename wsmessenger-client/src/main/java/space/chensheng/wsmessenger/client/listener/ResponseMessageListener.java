package space.chensheng.wsmessenger.client.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import space.chensheng.wsmessenger.client.MessengerClient;
import space.chensheng.wsmessenger.message.sysmsg.ResponseMessage;

public class ResponseMessageListener extends ClientMessageListener<ResponseMessage> {
	private static final Logger logger = LoggerFactory.getLogger(ResponseMessageListener.class);
	
	@Override
	protected void onMessage(ResponseMessage message, MessengerClient client) {
		logger.debug("receive ResponseMessage {} from server", message);
		client.processWaitingResponse(message);
	}

}
