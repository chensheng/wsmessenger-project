package space.chensheng.wsmessenger.common.executor;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import space.chensheng.wsmessenger.common.component.Retryable;
import space.chensheng.wsmessenger.common.component.Shutdownable;

public interface TaskExecutor extends Shutdownable {
	
	/**
	 * Execute task in executor thread pool. The task may be wait in queue if running task exceed thread pool size. 
	 * @param task
	 */
	void executeTask(Runnable task);
	
	/**
	 * Execute task in a specify thread to guarantee tasks executed in sequence.
	 * @param task
	 */
	void executeSequentialTask(Runnable task);
	
	/**
	 * Execute task after specify delay time.
	 * @param task
	 * @param delay milliseconds
	 * @param unit
	 * @return
	 */
	ScheduledFuture<?> scheduleTask(Runnable task, long delay, TimeUnit unit);
	
	/**
	 * Execute task interval at fixed rate.
	 * @param task
	 * @param initialDelay
	 * @param period
	 * @param unit
	 * @return
	 */
	ScheduledFuture<?> scheduleTaskAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit);
	
	/**
	 * Execute a task which will retry specify times until success.
	 * @param retryable
	 */
	void submitRetryable(Retryable retryable);
}
