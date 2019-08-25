package space.chensheng.wsmessenger.server;

import io.netty.channel.ChannelHandler;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.server.clientmng.ClientInfo;
import space.chensheng.wsmessenger.server.component.Lifecycle;
import space.chensheng.wsmessenger.server.component.ServerContextable;

public interface NettyServer extends Lifecycle, ServerContextable {
	
	/**
	 * Business thread will call this method when a client is connected. 
	 * Time consuming task can be proceeded here 
	 * @param clientInfo
	 */
	void onClientConnect(ClientInfo clientInfo);
	
	/**
	 * Business thread will call this method when a client is disconnected.
	 * Time consuming task can be proceeded here.
	 * @param clientInfo
	 */
	void onClientDisconnect(ClientInfo clientInfo);
	
	/**
	 * Business thread will call this method when server is started.
	 * Time consuming task can be processed here.
	 */
	void onStarted();
	
	/**
	 * Get executor which is to execute business task.
	 * @return
	 */
	TaskExecutor getTaskExecutor();
	
	/**
	 * Create channel handler to handle netty's message.
	 * @return
	 */
	ChannelHandler createChannelHandler();

    /**
     * Whether validate client step is needed
     * @return true if need to validate client, otherwise return false
     */
	boolean needToValidateClient();

    /**
     *  Check whether client is valid
     * @param clientInfo
     * @return true if client is valid, otherwise return false
     */
	boolean validateClient(ClientInfo clientInfo);
}
