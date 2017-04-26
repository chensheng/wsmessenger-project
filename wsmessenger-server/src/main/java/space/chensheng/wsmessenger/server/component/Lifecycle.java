package space.chensheng.wsmessenger.server.component;

public interface Lifecycle {
	boolean start();
	
	boolean stop();
	
	boolean restart();
}
