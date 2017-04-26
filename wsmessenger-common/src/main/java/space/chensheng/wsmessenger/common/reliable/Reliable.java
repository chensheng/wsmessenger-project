package space.chensheng.wsmessenger.common.reliable;

public interface Reliable<T, R> extends PendingMessageProcessor<T> {
	
	/**
	 * Process response message from receiver. In general, just remove the waiting information of the message.
	 * @param respMsg response message from receiver
	 */
	void processWaitingResponse(R respMsg);
	
	/**
	 * Add waiting information for message. Waiting thread will wait for receiver's response for the message, and then trigger callback method. 
	 * @param msg
	 * @param receiverId the messenger client id when calling this method in messenger server, and null in messenger client.
	 * @param callback
	 * @param timeout milliseconds to wait for receiver's response
	 */
	void waitingResponse(T msg, String receiverId, WaitingCallback<T> callback, long timeout);
	
	/**
	 * Add waiting information for message. Waiting thread will wait for receiver's response for the message, and then trigger callback method. 
	 * @param msg
	 * @param receiverId the messenger client id when calling this method in messenger server, and null in messenger client.
	 * @param callback
	 */
	void waitingResponse(T msg, String receiverId, WaitingCallback<T> callback);
}
