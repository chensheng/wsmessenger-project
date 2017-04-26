package space.chensheng.wsmessenger.common.reliable;

import space.chensheng.wsmessenger.common.component.MessengerContext;
import space.chensheng.wsmessenger.common.component.Shutdownable;

/**
 * A class assembles all messenger reliable functions.
 * @author sheng.chen
 */
public abstract class ReliableAssembler<T, R, C extends MessengerContext> implements Reliable<T, R>, Shutdownable {
	
	private WaitingMessageProcessor<T, R, C> waitingMessageProcessor;
	
	private PendingMessageProcessor<T> pendingMessageProcessor;
	
	public ReliableAssembler(WaitingMessageProcessor<T, R, C> waitingMessageProcessor, PendingMessageProcessor<T> pendingMessageProcessor) {
		this.waitingMessageProcessor = waitingMessageProcessor;
		this.pendingMessageProcessor = pendingMessageProcessor;
	}

	@Override
	public void processWaitingResponse(R respMsg) {
		waitingMessageProcessor.processWaitingResponse(respMsg);
	}

	@Override
	public void waitingResponse(T msg, String receiverId, WaitingCallback<T> callback, long timeout) {
		waitingMessageProcessor.submitWaiting(msg, receiverId, callback, timeout);
	}

	@Override
	public void waitingResponse(T msg, String clientId, WaitingCallback<T> callback) {
		waitingMessageProcessor.submitWaiting(msg, clientId, callback);
	}
	
	@Override
	public void addPendingMessage(String receiverId, T message) {
		this.pendingMessageProcessor.addPendingMessage(receiverId, message);
	}

	@Override
	public void deliverPendingMessages(String receiverId) {
		this.pendingMessageProcessor.deliverPendingMessages(receiverId);
	}

	@Override
	public void shutdown() {
		this.waitingMessageProcessor.shutdown();
		this.pendingMessageProcessor.shutdown();
	}

	@Override
	public boolean isShutdown() {
		return true;
	}
}
