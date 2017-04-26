package space.chensheng.wsmessenger.common.component;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Retryable implements Delayed {
	private static final Logger logger = LoggerFactory.getLogger(Retryable.class);
	
	private static final int DEFAULT_MAX_RETRY = 3;
	
	private long delay;
	
	private long trigger;
	
	private int maxRetry;
	
	private int retry;
	
	private String description;
	
	public Retryable(long delay) {
		this(delay, DEFAULT_MAX_RETRY, "");
	}
	
	public Retryable(long delay, int maxRetry) {
		this(delay, maxRetry, "");
	}
	
	public Retryable(long delay, String description) {
		this(delay, DEFAULT_MAX_RETRY, description);
	}
	
	public Retryable(long delay, int maxRetry, String description) {
		this.delay = delay;
		this.trigger = System.currentTimeMillis() + delay;
		this.maxRetry = maxRetry;
		this.description = description;
	}
	
	public Retryable resetTrigger() {
		this.trigger = System.currentTimeMillis() + delay;
		return this;
	}
	
	public boolean exceedMaxRetry() {
		return retry >= maxRetry;
	}
	
	public int getMaxRetry() {
		return maxRetry;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean retry() {
		if (exceedMaxRetry()) {
			return false;
		}
		
		boolean result = false;
		try {
			result = doTask();
		} catch (Throwable e) {
			logger.error(e.toString());
		}
		retry += 1;
		return result;
	}
	
	protected abstract boolean doTask();
	
	
	@Override
	public long getDelay(TimeUnit unit) {
		long leftMills = trigger - System.currentTimeMillis();
		return unit.convert(leftMills, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public int compareTo(Delayed o) {
		Retryable other = (Retryable) o;
		int result;
		if (this.trigger > other.trigger) {
			result = 1;
		} else if (this.trigger < other.trigger) {
			result = -1;
		} else if (this.retry > other.retry) {
			result = 1;
		} else if (this.retry < other.retry) {
			result = -1;
		} else {
			result = 0;
		}
		return result;
	}
	
	@Override
	public String toString() {
		return String.format("RetryTask[description:%s, retry:%d, maxRetry:%d, trigger:%d]", 
				description, retry, maxRetry, trigger);
	}
}
