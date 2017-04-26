package space.chensheng.wsmessenger.client.listener;

import space.chensheng.wsmessenger.client.MessengerClient;
import space.chensheng.wsmessenger.common.listener.LifecycleListener;

public abstract class ClientLifecycleListener implements LifecycleListener {
	public abstract void onClientConnect(MessengerClient client);
	
	public abstract void onClientStart(MessengerClient client);
	
	public abstract void onClientStop(MessengerClient client);
	
	public abstract void onClientRestart(MessengerClient client);
}
