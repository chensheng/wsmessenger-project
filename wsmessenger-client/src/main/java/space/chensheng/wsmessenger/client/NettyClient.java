package space.chensheng.wsmessenger.client;

import io.netty.channel.ChannelHandler;
import space.chensheng.wsmessenger.client.component.Lifecycle;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;

public interface NettyClient extends Lifecycle {
	/**
	 * Business thread will call this method once client success to connect server.
	 * Time consuming task can be proceeded here.
	 */
	void onConnected();
	
	/**
	 * Create channel handler to handle netty's message.
	 * @return
	 */
	ChannelHandler createChannelHandler();
	
	/**
	 * Get executor which is to execute business task.
	 * @return
	 */
	TaskExecutor getTaskExecutor();
}
