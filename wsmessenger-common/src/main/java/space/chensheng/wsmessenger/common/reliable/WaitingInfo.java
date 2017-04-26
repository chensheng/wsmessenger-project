package space.chensheng.wsmessenger.common.reliable;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

class WaitingInfo<T> implements Delayed {
    private long trigger;
    
    private String receiverId;
    
    private T message;
    
    private WaitingCallback<T> callback;
    
    WaitingInfo(T message, String receiverId, WaitingCallback<T> callback, long timeout) {
    	this.message = message;
    	this.receiverId = receiverId;
    	this.callback = callback;
    	this.trigger = System.currentTimeMillis() + timeout;
    }
    
    public T getMessage() {
    	return message;
    }
    
    public String getReceiverId() {
    	return receiverId;
    }
    
    public WaitingCallback<T> getCallback() {
    	return callback;
    }
    
	@Override
	public int compareTo(Delayed o) {
		WaitingInfo<?> other = (WaitingInfo<?>) o;
		int result = 0;
		if (trigger > other.trigger) {
			result = 1;
		} else if (trigger < other.trigger) {
			result = -1;
		} else {
			result = 0;
		}
		return result;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(trigger - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

}
