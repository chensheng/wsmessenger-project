package space.chensheng.wsmessenger.common.executor;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import space.chensheng.wsmessenger.common.component.Retryable;
import space.chensheng.wsmessenger.common.component.Shutdownable;

/**
 * A task executor can retry task for specify times until success.
 * @author sheng.chen
 */
public class RetryableExecutor implements Shutdownable {
	private static final Logger logger = LoggerFactory.getLogger(RetryableExecutor.class);
	
	private DelayQueue<Retryable> taskQueue = new DelayQueue<Retryable>();
	
	private RetryLooper retryLooper = new RetryLooper();
	
	private Semaphore startLooperSemaphore = new Semaphore(1);
	
	private volatile boolean executorAlive = true;
	
	private int maxTaskSize = Integer.MAX_VALUE;
	
	public RetryableExecutor() {
		
	}
	
	/**
	 * 
	 * @param maxTaskSize the max task count in retry queue
	 */
	public RetryableExecutor(int maxTaskSize) {
		this.maxTaskSize = maxTaskSize;
	}
	
	/**
	 * Submit a retry task, and then it will be added to retry queue. 
	 * The task will be executed in FIFO order.
	 * @param task
	 */
	public void submit(Retryable task) {
		if (!checkSubmit(task)) {
			return;
		}
		
		startLooperInNeed();
		taskQueue.put(task.resetTrigger());
		logger.debug("A retry task has been added, {}", task);
	}
	
	@Override
	public void shutdown() {
		executorAlive = false;
		retryLooper.cancel();
		taskQueue.clear();
		
	}
	
	@Override
	public boolean isShutdown() {
		return !executorAlive;
	}
	
	private boolean checkSubmit(Retryable task) {
		if (task == null) {
			throw new NullPointerException("task may not be null");
		}
		
		if (task.exceedMaxRetry()) {
			logger.info("Task retry times exceed max retry time, it will not be retried again, {}", task);
			return false;
		}
		
		if (!executorAlive) {
			logger.info("RetryExecutor has been shutdown, new retry task will not be accepted.");
			return false;
		}
		
		if (taskQueue.size() >= maxTaskSize) {
			logger.info("Retry task size has exceeded max size {}, new task will not be accepted, {}", maxTaskSize, task);
			return false;
		}
		
		return true;
	}
	
	private void startLooperInNeed() {
		if (!retryLooper.isLooping()) {
			if (!startLooperSemaphore.tryAcquire()) {
				return;
			}
			try {
				if (!retryLooper.isLooping()) {
					retryLooper.start();
				}
			} finally {
				startLooperSemaphore.release();
			}
		}
	}
	
	private class RetryLooper extends Thread {
		private volatile boolean looping = false;
		
		public RetryLooper() {
			setName("RetryLopper-" + getId());
		}
		
		@Override
		public synchronized void start() {
			looping = true;
			super.start();
		}
		
		@Override
		public void run() {
			logger.info("Retry task looper started. {}", this);
			Retryable task;
			try {
				while (looping) {
					task = taskQueue.take();
					if (task != null) {
						logger.debug("begin to retry task, {}", task);
						boolean retrySuccess = task.retry();
						if (!retrySuccess) {
							logger.error("fail to retry task, {}", task);
							RetryableExecutor.this.submit(task);
						} else {
							logger.debug("success to retry task, {}", task);
						}
					}
				}
			} catch (InterruptedException e) {
				logger.error(e.toString());
			}
			logger.info("Retry task looper has been shutdown. {}", this);
		}
		
		public boolean isLooping() {
			return looping;
		}
		
		public void cancel() {
			logger.info("Try to cancel Retry task lopper. {}", this);
			looping = false;
		}
	}
}
