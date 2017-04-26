package space.chensheng.wsmessenger.common.listener;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MessageListenerManager<T extends MessageListener, M> {
	private Map<Class<?>, List<T>> listenerMap = new HashMap<Class<?>, List<T>>();
	
	/**
	 * Add listener to listen specify message
	 * @param listener
	 */
	public void add(T listener) {
		if (listener == null) {
			return;
		}
		
		Class<?> listenMsgClzz = listener.acceptableClass();
		if (listenMsgClzz == null) {
			return;
		}
	
		if (!listenerMap.containsKey(listenMsgClzz)) {
			listenerMap.put(listenMsgClzz, new LinkedList<T>());
		}
		listenerMap.get(listenMsgClzz).add(listener);
	}
	
	/**
	 * Add listeners to listen multiple messages.
	 * @param listeners
	 */
	public void add(List<T> listeners) {
		if (listeners == null) {
			return;
		}
		
		for (T listener : listeners) {
			this.add(listener);
		}
	}
	
	/**
	 * Find listeners that listen specify message.
	 * @param message
	 * @return listeners that listen {@code message}, or null if not listener found.
	 */
	public List<T> find(M message) {
		if (message == null) {
			return null;
		}
		
		List<T> foundList = listenerMap.get(message.getClass());
		if (foundList != null) {
			foundList = Collections.unmodifiableList(foundList);
		}
		return foundList;
	}
}
