package space.chensheng.wsmessenger.client;

import space.chensheng.wsmessenger.client.component.ClientContext;
import space.chensheng.wsmessenger.client.listener.ClientLifecycleListener;
import space.chensheng.wsmessenger.client.listener.ClientMessageListener;
import space.chensheng.wsmessenger.common.listener.LifecycleListenerManager;
import space.chensheng.wsmessenger.common.listener.MessageListenerManager;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.message.sysmsg.ResponseMessage;

import java.util.List;

/**
 * Messenger client which can send message to messenger server through websocket protocol built upon netty.
 * @author sheng.chen
 */
public class WsMessengerClient extends MessengerClient {
	private MessageListenerManager<ClientMessageListener<?>, WsMessage> msgListenerMgr = new MessageListenerManager<ClientMessageListener<?>, WsMessage>();
	
	private LifecycleListenerManager<ClientLifecycleListener> lifecycleListenerMgr = new LifecycleListenerManager<ClientLifecycleListener>();
	
	WsMessengerClient() {
	}

	WsMessengerClient(ClientContext clientContext) {
		super(clientContext);
	}
	
	@Override
	public void onMessage(WsMessage message, String senderId) {
		sendResponseIfNecessary(message);
		
		List<ClientMessageListener<?>> listeners = msgListenerMgr.find(message);
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		
		for (ClientMessageListener<?> listener : listeners) {
			listener.handleMessage(message, this);
		}
	}

	@Override
	public void onConnected() {
		List<ClientLifecycleListener> listeners = lifecycleListenerMgr.findListeners();
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		
		for (ClientLifecycleListener listener : listeners) {
			listener.onClientConnect(this);
		}
	}
	
	@Override
	public void onStart() {
		List<ClientLifecycleListener> listeners = lifecycleListenerMgr.findListeners();
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		
		for (ClientLifecycleListener listener : listeners) {
			listener.onClientStart(this);
		}
	}

	@Override
	public void onStop() {
		List<ClientLifecycleListener> listeners = lifecycleListenerMgr.findListeners();
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		
		for (ClientLifecycleListener listener : listeners) {
			listener.onClientStop(this);
		}
	}

	@Override
	public void onRestart() {
		List<ClientLifecycleListener> listeners = lifecycleListenerMgr.findListeners();
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		
		for (ClientLifecycleListener listener : listeners) {
			listener.onClientRestart(this);
		}
	}
	
	void addMessageListener(ClientMessageListener<?> listener) {
		msgListenerMgr.add(listener);
	}
	
	void addMessageListeners(List<ClientMessageListener<?>> listeners) {
		msgListenerMgr.add(listeners);
	}
	
	void addLifecycleListener(ClientLifecycleListener listener) {
		lifecycleListenerMgr.add(listener);
	}
	
	void addLifecycleListeners(List<ClientLifecycleListener> listeners) {
		lifecycleListenerMgr.add(listeners);
	}

	private void sendResponseIfNecessary(WsMessage message) {
		if (message.getHeader().isNeedResponse()) {
			ResponseMessage respMsg = new ResponseMessage();
			respMsg.setRespMessageId(message.getHeader().getMessageId());
			respMsg.setSuccess(true);
			sendMessage(respMsg);
		}
	}

}
