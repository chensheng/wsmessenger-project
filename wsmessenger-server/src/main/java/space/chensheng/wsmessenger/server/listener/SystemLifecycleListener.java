package space.chensheng.wsmessenger.server.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import space.chensheng.wsmessenger.server.MessengerServer;
import space.chensheng.wsmessenger.server.clientmng.ClientInfo;

public class SystemLifecycleListener extends ServerLifecycleListener {
	private static final Logger logger = LoggerFactory.getLogger(SystemLifecycleListener.class);
	
	@Override
	public void onServerStart(MessengerServer server) {
		logger.debug("server start");
	}

	@Override
	public void onClientConnect(ClientInfo clientInfo, MessengerServer server) {
		logger.debug("client {} is connected", clientInfo);
		server.deliverPendingMessages(clientInfo.getClientId());
	}

	@Override
	public void onClientDisconnect(ClientInfo clientInfo, MessengerServer server) {
		logger.debug("client {} is disconnected", clientInfo);
	}

}
