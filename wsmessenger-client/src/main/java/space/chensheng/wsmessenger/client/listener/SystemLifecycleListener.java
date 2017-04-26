package space.chensheng.wsmessenger.client.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import space.chensheng.wsmessenger.client.MessengerClient;

public class SystemLifecycleListener extends ClientLifecycleListener {
	private static final Logger logger = LoggerFactory.getLogger(SystemLifecycleListener.class);
	
	@Override
	public void onClientConnect(MessengerClient client) {
		logger.debug("client success to connect to server");
		client.deliverPendingMessages(null);
	}

	@Override
	public void onClientStart(MessengerClient client) {
		logger.debug("client start");
	}

	@Override
	public void onClientStop(MessengerClient client) {
		logger.debug("client stop");
	}

	@Override
	public void onClientRestart(MessengerClient client) {
		logger.debug("client restart");
	}

}
