package space.chensheng.wsmessenger.server.listener;

import space.chensheng.wsmessenger.common.listener.MessageListener;
import space.chensheng.wsmessenger.common.util.ReflectUtil;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.server.MessengerServer;
import space.chensheng.wsmessenger.server.clientmng.ClientInfo;

public abstract class ServerMessageListener<T extends WsMessage<?>> implements MessageListener {
	
	@SuppressWarnings("unchecked")
	public void handleMessage(WsMessage<?> message, ClientInfo clientInfo, MessengerServer server) {
		this.onMessage((T) message, clientInfo, server);
	}
	
	public Class<?> acceptableClass() {
		Class<?> acceptableClass = ReflectUtil.findGenericType(this, ServerMessageListener.class, "T");
		return acceptableClass;
	}
	
	protected abstract void onMessage(T message, ClientInfo clientInfo, MessengerServer server);
}
