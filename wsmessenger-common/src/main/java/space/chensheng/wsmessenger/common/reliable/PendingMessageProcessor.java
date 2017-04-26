package space.chensheng.wsmessenger.common.reliable;

import space.chensheng.wsmessenger.common.component.Shutdownable;
/**
 * This is used to manage pending message of messenger.
 * @author sheng.chen
 */
public interface PendingMessageProcessor<T> extends Shutdownable {
	/**
	 * Deliver messages in pending queue to receiver when receiver available.
	 * @param receiverId the messenger client id when calling this method in messenger server, and null in messenger client.
	 */
	void deliverPendingMessages(String receiverId);
	
	/**
	 * Add message to pending queue when fail to send message because of receiver unavailable.
	 * @param receiverId the messenger client id when calling this method in messenger server, and null in messenger client.
	 * @param message
	 */
	void addPendingMessage(String receiverId, T message);
}
