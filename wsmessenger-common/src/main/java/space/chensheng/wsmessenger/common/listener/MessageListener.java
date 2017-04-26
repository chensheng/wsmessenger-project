package space.chensheng.wsmessenger.common.listener;

public interface MessageListener {
	/**
	 * 
	 * @return type of listening message
	 */
	Class<?> acceptableClass();
}
