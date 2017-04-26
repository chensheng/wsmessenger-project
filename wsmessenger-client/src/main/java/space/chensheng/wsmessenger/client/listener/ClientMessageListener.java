package space.chensheng.wsmessenger.client.listener;

import space.chensheng.wsmessenger.client.MessengerClient;
import space.chensheng.wsmessenger.common.listener.MessageListener;
import space.chensheng.wsmessenger.common.util.ReflectUtil;

import space.chensheng.wsmessenger.message.component.WsMessage;

public abstract class ClientMessageListener<T extends WsMessage<?>> implements MessageListener {
	
	@SuppressWarnings("unchecked")
	public void handleMessage(WsMessage<?> message, MessengerClient client) {
		this.onMessage((T) message, client);
	}
	
	public Class<?> acceptableClass() {
		Class<?> acceptableClass = ReflectUtil.findGenericType(this, ClientMessageListener.class, "T");
		return acceptableClass;
	}
	
	/**
	 * This is running in business thread pool.
	 * @param message
	 */
	protected abstract void onMessage(T message, MessengerClient client);
}
