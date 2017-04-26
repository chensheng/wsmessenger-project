package space.chensheng.wsmessenger.client.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import space.chensheng.wsmessenger.client.component.ClientContext;
import space.chensheng.wsmessenger.common.component.Retryable;
import space.chensheng.wsmessenger.common.executor.RetryableExecutor;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;

public class ClientTaskExecutor implements TaskExecutor {
	private ScheduledExecutorService businessExecutor;
	
	private ScheduledExecutorService sequentialExecutor;
	
	private RetryableExecutor retryableExecutor;
	
	
	public ClientTaskExecutor(ClientContext clientContext) {
		if (clientContext == null) {
			throw new NullPointerException("clientContext may not be null");
		}
		
		businessExecutor = Executors.newScheduledThreadPool(clientContext.getBusinessThreadPoolSize());
		sequentialExecutor = Executors.newSingleThreadScheduledExecutor();
		retryableExecutor = new RetryableExecutor(clientContext.getRetryTaskMaxSize());
	}

	@Override
	public void executeTask(Runnable task) {
		if (!businessExecutor.isShutdown()) {
			businessExecutor.execute(task);
		}
	}

	@Override
	public void executeSequentialTask(Runnable task) {
		if (!sequentialExecutor.isShutdown()) {
			sequentialExecutor.execute(task);
		}
	}

	@Override
	public ScheduledFuture<?> scheduleTask(Runnable task, long delay, TimeUnit unit) {
		if (!businessExecutor.isShutdown()) {
			return businessExecutor.schedule(task, delay, unit);
		}
		return null;
	}

	@Override
	public ScheduledFuture<?> scheduleTaskAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
		if (!businessExecutor.isShutdown()) {
			return businessExecutor.scheduleAtFixedRate(task, initialDelay, period, unit);
		}
		return null;
	}

	@Override
	public void submitRetryable(Retryable retryable) {
		if (!retryableExecutor.isShutdown()) {
			retryableExecutor.submit(retryable);
		}
	}

	@Override
	public void shutdown() {
		businessExecutor.shutdown();
		sequentialExecutor.shutdown();
		retryableExecutor.shutdown();
	}

	@Override
	public boolean isShutdown() {
		return businessExecutor.isShutdown() && sequentialExecutor.isShutdown() && retryableExecutor.isShutdown();
	}

}
