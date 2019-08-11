package space.chensheng.wsmessenger.common.reliable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import space.chensheng.wsmessenger.common.component.MessengerContext;
import space.chensheng.wsmessenger.common.component.Shutdownable;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;

/**
 * This class is used to process message waiting for receiver's response. 
 * It will notify the {@code WaitingCallback} once receiving a response message from receive.
 * @author sheng.chen
 */
public abstract class WaitingMessageProcessor<T, R, C extends MessengerContext> implements Shutdownable {
	private static final Logger logger = LoggerFactory.getLogger(WaitingMessageProcessor.class);
	
	private ConcurrentHashMap<Long, WaitingInfo<T>> waitingMap = new ConcurrentHashMap<Long, WaitingInfo<T>>();
	
	private DelayQueue<WaitingInfo<T>> timeoutQueue = new DelayQueue<WaitingInfo<T>>();
	
	private WaitingTimeoutLooper timeoutLooper = new WaitingTimeoutLooper();
	
	private Semaphore startLooperSemaphore = new Semaphore(1);
	
	private volatile boolean isProcessorAlive = true;
	
	private C messengerContext;
	
	private TaskExecutor taskExecutor;
	
	/**
	 * 
	 * @param messengerContext
	 * @param taskExecutor
	 * @throws NullPointerException if {@code messengerConext} or {@code taskExecutor} is null
	 */
	public WaitingMessageProcessor(C messengerContext, TaskExecutor taskExecutor) {
		if (messengerContext == null) {
			throw new NullPointerException("messengerContext may not be null");
		}
		
		if (taskExecutor == null) {
			 throw new NullPointerException("taskExecutor may not be null");
		}
		
		this.messengerContext = messengerContext;
		this.taskExecutor = taskExecutor;
	}
	
	/**
	 * Add waiting information for message. Waiting thread will wait for receiver's response for the message, and then trigger callback method. 
	 * @param message
	 * @param receiverId the messenger client id when calling this method in messenger server, and null in messenger client.
	 * @param callback
	 */
	public void submitWaiting(T message, String receiverId, WaitingCallback<T> callback) {
		this.submitWaiting(message, receiverId, callback, messengerContext.getWaitingMsgTimeoutMillis());
	}
	
	/**
	 * Add waiting information for message. Waiting thread will wait for receiver's response for the message, and then trigger callback method. 
	 * @param message
	 * @param receiverId the messenger client id when calling this method in messenger server, and null in messenger client.
	 * @param callback
	 * @param timeout milliseconds to wait for receiver's response
	 */
	public void submitWaiting(T message, String receiverId, WaitingCallback<T> callback, long timeout) {
		if (message == null) {
			return;
		}
		
		if (!checkSubmit()) {
			return;
		}
		
		startLooperInNeed();
		WaitingInfo<T> info = new WaitingInfo<T>(message, receiverId, callback, timeout);
		waitingMap.put(this.resolveMessageId(message), info);
		timeoutQueue.offer(info);
		logger.debug("Waiting response for message {}", message);
	}
	
	/**
	 * Process response message from receiver. In general, just remove the waiting information of the message.
	 * @param respMsg response message from receiver
	 */
	public void processWaitingResponse(R respMsg) {
		if (respMsg == null) {
			return;
		}
		
		WaitingInfo<T> info = waitingMap.remove(this.resolverRespMessageId(respMsg));
		if (info == null) {
			return;
		}
		
		timeoutQueue.remove(info);
		if (info.getCallback() == null) {
			return;
		}
		
		if (this.isRespMessageSuccess(respMsg)) {
			taskExecutor.executeTask(new Runnable() {

				@Override
				public void run() {
					logger.debug("Receive success response for message {}", info.getMessage());
					info.getCallback().onSuccess(info.getMessage(), info.getReceiverId());
				}
				
			});
		} else {
			taskExecutor.executeTask(new Runnable() {

				@Override
				public void run() {
					logger.error("Receive fail response for message {}", info.getMessage());
					info.getCallback().onFail(info.getMessage(), info.getReceiverId());
				}
				
			});
		}
	}
	
	@Override
	public void shutdown() {
		isProcessorAlive = false;
		timeoutLooper.cancel();
		waitingMap.clear();
		timeoutQueue.clear();
	}
	
	@Override
	public boolean isShutdown() {
		return !isProcessorAlive;
	}
	
	/**
	 * Resolve messageId from the message.
	 * @param message
	 * @return messageId
	 */
	protected abstract long resolveMessageId(T message);
	
	/**
	 * Resolve the respMessageId from response message
	 * @param respMsg
	 * @return respMessageId
	 */
	protected abstract long resolverRespMessageId(R respMsg);
	
	/**
	 * Check whether the result of response message is success or not.
	 * @param respMsg
	 * @return
	 */
	protected abstract boolean isRespMessageSuccess(R respMsg);
	
	private boolean checkSubmit() {
		if (!isProcessorAlive) {
			logger.error("WaitingResponseExecutor is not alive.");
			return false;
		}
		
		if (waitingMap.size() >= messengerContext.getWaitingMsgMaxSize()) {
			logger.error("Waiting message size exceeds max waiting size {}", messengerContext.getWaitingMsgMaxSize());
			return false;
		}
		
		return true;
	}
	
	private void startLooperInNeed() {
		if (!timeoutLooper.isLooping()) {
			if (!startLooperSemaphore.tryAcquire()) {
				return;
			}
			try {
				if (!timeoutLooper.isLooping()) {
					timeoutLooper.start();
				}
			} finally {
				startLooperSemaphore.release();
			}
		}
	}
	
	private class WaitingTimeoutLooper extends Thread {
		private volatile boolean looping = false;
		
		WaitingTimeoutLooper() {
			setName("WaitingTimeoutLooper-" + this.getId());
		}
		
		@Override
		public synchronized void start() {
			looping = true;
			super.start();
		}
		
		@Override
		public void run() {
			logger.info("WaitingTimeoutLooper started...");
			try {
				while (looping) {
					WaitingInfo<T> info = timeoutQueue.take();
					if (info != null) {
					    waitingMap.remove(WaitingMessageProcessor.this.resolveMessageId(info.getMessage()));
						if (info.getCallback() != null) {
							taskExecutor.executeTask(new Runnable() {

								@Override
								public void run() {
									logger.debug("Waiting timeout for message {}", info.getMessage());
									info.getCallback().onTimeout(info.getMessage(), info.getReceiverId());
								}
								
							});
						}
					}
				}
			} catch (InterruptedException e) {
				logger.error(e.toString());
			}
			
			logger.info("WaitingTimeoutLooper shutdown...");
		}
		
		public boolean isLooping() {
			return looping;
		}
		
		public void cancel() {
			looping = false;
			this.interrupt();
		}
	}
}
