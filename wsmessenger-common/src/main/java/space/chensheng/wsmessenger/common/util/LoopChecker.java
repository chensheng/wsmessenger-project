package space.chensheng.wsmessenger.common.util;

public abstract class LoopChecker {
	private long timeoutMillis;
	
	public LoopChecker(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}
	
	public boolean check() {
		long startTime = System.currentTimeMillis();
		while(true) {
			if(System.currentTimeMillis() - startTime > timeoutMillis) {
				break;
			}
			
			if(condition()) {
				return true;
			}
		}
		return false;
	}
	
	protected abstract boolean condition();
}
