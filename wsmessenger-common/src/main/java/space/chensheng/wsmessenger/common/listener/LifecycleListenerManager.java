package space.chensheng.wsmessenger.common.listener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LifecycleListenerManager<T extends LifecycleListener> {
	private List<T> listenerList = new LinkedList<T>();
	
	/**
	 * Add a listener to listen lifecycle of server or client.
	 * @param listener
	 */
	public void add(T listener) {
		if (listener != null) {
			listenerList.add(listener);
		}
	}
	
	/**
	 * Add multiple listeners to listen lifecycle of server or client.
	 * @param listeners
	 */
	public void add(List<T> listeners) {
		if (listeners != null && !listeners.isEmpty()) {
			listenerList.addAll(listeners);
		}
	}
	
	/**
	 * Find listeners that listen lifecycle of server or client.
	 * @return listener list, or an empty list
	 */
	public List<T> findListeners() {
		return Collections.unmodifiableList(listenerList);
	}
}
