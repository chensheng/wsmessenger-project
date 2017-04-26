package space.chensheng.wsmessenger.server.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import space.chensheng.wsmessenger.common.component.Retryable;
import space.chensheng.wsmessenger.common.executor.RetryableExecutor;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.server.component.ServerContext;

public class ServerTaskExecutor implements TaskExecutor {
	private static final Logger logger = LoggerFactory.getLogger(ServerTaskExecutor.class);
	
	private ScheduledExecutorService businessExecutor;
	
	private ScheduledExecutorService sequentialExecutor;
	
	private RetryableExecutor retryableExecutor;
	
	
	public ServerTaskExecutor(ServerContext serverContext) {
		if (serverContext == null) {
			throw new NullPointerException("serverContext may not be null");
		}
		
		businessExecutor = Executors.newScheduledThreadPool(serverContext.getBusinessThreadSize());
		sequentialExecutor = Executors.newSingleThreadScheduledExecutor();
		retryableExecutor = new RetryableExecutor(serverContext.getRetryTaskMaxSize());
	}

	@Override
	public void executeTask(Runnable task) {
		if (!businessExecutor.isShutdown()) {
			businessExecutor.execute(task);
		} else {
			logger.error("businessExecutor is shutdown!");
		}
	}

	@Override
	public void executeSequentialTask(Runnable task) {
		if (!sequentialExecutor.isShutdown()) {
			sequentialExecutor.execute(task);
		} else {
			logger.error("sequentialExecutor is shutdown");
		}
	}

	@Override
	public ScheduledFuture<?> scheduleTask(Runnable task, long delay, TimeUnit unit) {
		if (!businessExecutor.isShutdown()) {
			return businessExecutor.schedule(task, delay, unit);
		}
		logger.error("businessExecutor is shutdown");
		return null;
	}

	@Override
	public ScheduledFuture<?> scheduleTaskAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
		if (!businessExecutor.isShutdown()) {
			return businessExecutor.scheduleAtFixedRate(task, initialDelay, period, unit);
		}
		logger.error("businessExecutor is shutdown");
		return null;
	}

	@Override
	public void submitRetryable(Retryable retryable) {
		if (!retryableExecutor.isShutdown()) {
			retryableExecutor.submit(retryable);
		} else {
			logger.error("retryableExecutor is shutdown");
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
