package space.chensheng.wsmessenger.common.reliable;

import space.chensheng.wsmessenger.common.component.Messenger;

public interface ReliableMessenger<T, R> extends Messenger<T>{
	/**
	 * Send message to receiver by receiverId, and add message to pending queue when fail to send.
	 * @param message
	 * @param receiverId the messenger client id when calling this method in messenger server, and null when calling this method in messenger client.
	 */
	void sendMessageReliably(T message, String receiverId);
	
	/**
	 * Send message to receiver by receiverId, and waiting for receiver's response. Trigger callback.onFail when fail to send.
	 * @param message
	 * @param receiverId the messenger client id when calling this method in messenger server, and null when calling this method in messenger client.
	 * @param callback
	 */
	void sendWaitingMessage(T message, String receiverId, WaitingCallback<T> callback);
	
	/**
	 * Send message to receiver by receiverId, and waiting for receiver's response. Trigger callback.onFail when fail to send.
	 * @param message
	 * @param receiverId the messenger client id when calling this method in messenger server, and null when calling this method in messenger client.
	 * @param callback
	 * @param timeout milliseconds to wait for receiver's response
	 */
	void sendWaitingMessage(T message, String receiverId, WaitingCallback<T> callback, long timeout);
	
	/**
	 * Send message to receiver by receiverId, and waiting for receiver's response. 
	 * Add message to pending queue when fail to send.
	 * @param message
	 * @param receiverId the messenger client id when calling this method in messenger server, and null when calling this method in messenger client.
	 * @param callback
	 */
	void sendWaitingMessageReliably(T message, String receiverId, WaitingCallback<T> callback);
	
	/**
	 * Send message to receiver by receiverId, and waiting for receiver's response.
	 * Add message to pending queue when fail to send.
	 * @param message
	 * @param receiverId the messenger client id when calling this method in messenger server, and null when calling this method in messenger client.
	 * @param callback
	 * @param timeout milliseconds to wait for receiver's response
	 */
	void sendWaitingMessageReliably(T message, String receiverId, WaitingCallback<T> callback, long timeout);
	
	/**
	 * Send message to receiver by receiverId, and waiting for receiver's response.
	 * Retry 3 times until receiving success response.
	 * Add message to pending queue when fail to send.
	 * @param message
	 * @param receiverId the messenger client id when calling this method in messenger server, and null when calling this method in messenger client.
	 */
	void sendWaitingMessageReliablyWithRetry(T message, String receiverId);
	
	/**
	 * Send message to receiver by receiverId, and waiting for receiver's response.
	 * Retry specify times until receiving success response.
	 * Add message to pending queue when fail to send.
	 * @param message
	 * @param receiverId the messenger client id when calling this method in messenger server, and null when calling this method in messenger client.
	 * @param retry max retry times
	 */
	void sendWaitingMessageReliablyWithRetry(T message, String receiverId, int retry);
	
	/**
	 * Deliver messages in pending queue to receiver when receiver available.
	 * @param receiverId the messenger client id when calling this method in messenger server, and null in messenger client.
	 */
	void deliverPendingMessages(String receiverId);
	
	/**
	 * Process response message from receiver. In general, just remove the waiting information of the message.
	 * @param respMsg response message from receiver
	 */
	void processWaitingResponse(R respMsg);
}
