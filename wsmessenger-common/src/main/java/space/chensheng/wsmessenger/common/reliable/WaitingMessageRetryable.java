package space.chensheng.wsmessenger.common.reliable;

import space.chensheng.wsmessenger.common.component.Retryable;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;

public class WaitingMessageRetryable<T, R> extends Retryable {
	private ReliableMessenger<T, R> messenger;
	
	private TaskExecutor taskExecutor;
	
	private T message;
	
	private WaitingCallback<T> waitingCallback;
	
	private String receiverId;
	
	public WaitingMessageRetryable(ReliableMessenger<T, R> messenger, TaskExecutor taskExecutor, T message, String receiverId) {
		super(1000);
		this.message = message;
		this.messenger = messenger;
		this.taskExecutor = taskExecutor;
		this.waitingCallback = new RetryWaitingCallback();
		this.receiverId = receiverId;
	}
	
	public WaitingMessageRetryable(ReliableMessenger<T, R> messenger, TaskExecutor taskExecutor, T message, String receiverId, int retry) {
		super(1000, retry);
		this.message = message;
		this.messenger = messenger;
		this.taskExecutor = taskExecutor;
		this.waitingCallback = new RetryWaitingCallback();
		this.receiverId = receiverId;
	}

	@Override
	protected boolean doTask() {
		messenger.sendWaitingMessageReliably(message, receiverId, waitingCallback);
		return true;
	}

	private class RetryWaitingCallback implements WaitingCallback<T> {

		@Override
		public void onSuccess(T sentMsg, String receiverId) {
		}

		@Override
		public void onFail(T sentMsg, String receiverId) {
			taskExecutor.submitRetryable(WaitingMessageRetryable.this);
		}

		@Override
		public void onTimeout(T sentMsg, String receiverId) {
			taskExecutor.submitRetryable(WaitingMessageRetryable.this);
		}
		
	}
}
