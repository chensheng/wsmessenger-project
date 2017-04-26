package space.chensheng.wsmessenger.server.listener;

import space.chensheng.wsmessenger.common.listener.LifecycleListener;
import space.chensheng.wsmessenger.server.MessengerServer;
import space.chensheng.wsmessenger.server.clientmng.ClientInfo;

public abstract class ServerLifecycleListener implements LifecycleListener {
	/**
	 * Business thread will call this method once server starts.
	 * Time consuming task can be proceeded here
	 * @param server
	 */
	public abstract void onServerStart(MessengerServer server);
	
	/**
	 * Business thread will call this method once a client connects.
	 * Time consuming task can be proceeded here.
	 * @param clientInfo
	 * @param server
	 */
	public abstract void onClientConnect(ClientInfo clientInfo, MessengerServer server);
	
	/**
	 * Business thread will call this method once a client disconnects.
	 * Time consuming task can be proceeded here.
	 * @param clientInfo
	 * @param server
	 */
	public abstract void onClientDisconnect(ClientInfo clientInfo, MessengerServer server);
}
