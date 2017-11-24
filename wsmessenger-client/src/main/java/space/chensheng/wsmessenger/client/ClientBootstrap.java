package space.chensheng.wsmessenger.client;

import java.util.LinkedList;
import java.util.List;

import space.chensheng.wsmessenger.client.listener.ClientLifecycleListener;
import space.chensheng.wsmessenger.client.listener.ClientMessageListener;
import space.chensheng.wsmessenger.client.listener.ResponseMessageListener;
import space.chensheng.wsmessenger.client.listener.SystemLifecycleListener;
import space.chensheng.wsmessenger.common.util.StringUtil;

/**
 * A tool to create {@link WsMessengerClient}.
 * @author sheng.chen
 */
public class ClientBootstrap {
	private List<ClientMessageListener<?>> msgListeners;
	
	private List<ClientLifecycleListener> lifecycleListeners;
	
	private String clientId;
	
	public ClientBootstrap() {
		msgListeners = new LinkedList<ClientMessageListener<?>>();
		lifecycleListeners = new LinkedList<ClientLifecycleListener>();
		
		this.initSystemListeners();
	}
	
	/**
	 * Build a {@link WsMessengerClient}
	 * @return
	 */
	public WsMessengerClient build() {
		WsMessengerClient client = new WsMessengerClient();
		if (StringUtil.isNotEmpty(clientId)) {
			client.setClientId(clientId);
		}
		client.addMessageListeners(msgListeners);
		client.addLifecycleListeners(lifecycleListeners);
		return client;
	}
	
	public ClientBootstrap setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}
	
	/**
	 * Add a listener to listen specify message. If multiple listeners listen the same message, 
	 * they will be notified in their add order.
	 * @param listener
	 * @return
	 */
	public ClientBootstrap addMessageListener(ClientMessageListener<?> listener) {
		if (listener != null) {
			msgListeners.add(listener);
		}
		return this;
	}
	
	/**
	 * Add listeners to listen messages.If multiple listeners listen the same message, 
	 * they will be notified in their add order.
	 * @param listeners
	 * @return
	 */
	public ClientBootstrap addMessageListeners(List<ClientMessageListener<?>> listeners) {
		if (listeners != null && !listeners.isEmpty()) {
			msgListeners.addAll(listeners);
		}
		return this;
	}
	
	/**
	 * Add a listener to listen client's lifecycle. If multiple listeners have been added, 
	 * they will be notified in their add order.
	 * @param listener
	 * @return
	 */
	public ClientBootstrap addLifecycleListener(ClientLifecycleListener listener) {
		if (listener != null) {
			lifecycleListeners.add(listener);
		}
		return this;
	}
	
	/**
	 * Add listeners to listen client's lifecycle. If multiple listeners have been added,
	 * they will be notified in their add order.
	 * @param listeners
	 * @return
	 */
	public ClientBootstrap addLifecycleListeners(List<ClientLifecycleListener> listeners) {
		if (listeners != null && !listeners.isEmpty()) {
			lifecycleListeners.addAll(listeners);
		}
		return this;
	}
	
	private void initSystemListeners() {
		this.addMessageListener(new ResponseMessageListener());
		this.addLifecycleListener(new SystemLifecycleListener());
	}
}
