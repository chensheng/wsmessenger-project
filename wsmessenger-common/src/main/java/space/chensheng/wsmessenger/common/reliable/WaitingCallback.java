package space.chensheng.wsmessenger.common.reliable;

public interface WaitingCallback<T> {
	
	/**
	 * It is called when a waiting message's response is success. 
	 * @param message the message waiting for response
	 * @param receiverId the messenger client id when calling this method in messenger server, and null in messenger client
	 */
	void onSuccess(T message, String receiverId);
	
	/**
	 * It is called when a waiting message's response is fail.
	 * @param message the message waiting for response
	 * @param receiverId the messenger client id when calling this method in messenger server, and null in messenger client
	 */
	void onFail(T message, String receiverId);
	
	/**
	 * It is called when a waiting message is timeout.
	 * @param message the message waiting for response
	 * @param receiverId the messenger client id when calling this method in messenger server, and null in messenger client
	 */
	void onTimeout(T message, String receiverId);
}
