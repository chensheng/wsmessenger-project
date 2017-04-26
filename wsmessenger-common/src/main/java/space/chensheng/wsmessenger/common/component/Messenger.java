package space.chensheng.wsmessenger.common.component;

public interface Messenger<T> {
	/**
	 * Business thread will call this method when a message received.
	 * @param message the message received
	 * @param senderId the messenger client id when calling this method in messenger server, and null when calling this method in messenger client.
	 */
	void onMessage(T message, String senderId);
	
	/**
	 * In messenger server, this means to send message to all messenger clients.
	 * In messenger client, this means to send message to messenger server.
	 * @param message
	 */
	void sendMessage(T message);
	
	/**
	 * Send message to receiver by receiverId.
	 * @param message
	 * @param receiverId the messenger client id when calling this method in messenger server, and null when calling this method in messenger client.
	 */
	void sendMessage(T message, String receiverId);
	
}
