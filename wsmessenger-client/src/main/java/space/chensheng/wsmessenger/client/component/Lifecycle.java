package space.chensheng.wsmessenger.client.component;

public interface Lifecycle {
	boolean start();
	
	boolean stop();
	
	boolean restart();
	
	boolean isActive();
	
	void onStart();
	
	void onStop();
	
	void onRestart();
}
