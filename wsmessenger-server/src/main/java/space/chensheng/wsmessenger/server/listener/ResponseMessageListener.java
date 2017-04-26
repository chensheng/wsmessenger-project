package space.chensheng.wsmessenger.server.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import space.chensheng.wsmessenger.message.sysmsg.ResponseMessage;
import space.chensheng.wsmessenger.server.MessengerServer;
import space.chensheng.wsmessenger.server.clientmng.ClientInfo;

public class ResponseMessageListener extends ServerMessageListener<ResponseMessage> {
	private static final Logger logger = LoggerFactory.getLogger(ResponseMessageListener.class);
	
	@Override
	protected void onMessage(ResponseMessage message, ClientInfo clientInfo, MessengerServer server) {
		logger.debug("receive ResposneMessage {} from client {}", message, clientInfo);
		server.processWaitingResponse(message);
	}
	
}
