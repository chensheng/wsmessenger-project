package space.chensheng.wsmessenger.common.component;

public interface Shutdownable {
	void shutdown();
	
	boolean isShutdown();
}
