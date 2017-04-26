package space.chensheng.wsmessenger.server;

import io.netty.channel.ChannelHandler;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.common.reliable.ReliableMessenger;
import space.chensheng.wsmessenger.common.reliable.WaitingCallback;
import space.chensheng.wsmessenger.common.reliable.WaitingMessageRetryable;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.message.sysmsg.ResponseMessage;
import space.chensheng.wsmessenger.server.component.SenderCallbackAdapter;
import space.chensheng.wsmessenger.server.component.ServerContext;
import space.chensheng.wsmessenger.server.executor.ServerTaskExecutor;
import space.chensheng.wsmessenger.server.handler.ServerChannelInitializer;
import space.chensheng.wsmessenger.server.reliable.ServerReliableAssembler;

/**
 * A class implements basic functions to send message to messenger client through websocket protocol built upon netty. 
 * @author sheng.chen
 */
public abstract class MessengerServer extends AbstractNettyServer implements ReliableMessenger<WsMessage<?>, ResponseMessage> {
	private ServerReliableAssembler reliableAssembler;
	
    public MessengerServer() {
    	this(new ServerContext());
    }
    
    /**
     * 
     * @param serverContext
     * @throws NullPointerException if {@code serverContext} is null
     */
    public MessengerServer(ServerContext serverContext) {
    	this(serverContext, new ServerTaskExecutor(serverContext));
    }
    
    /**
     * 
     * @param serverContext
     * @param taskExecutor
     * @throws NullPointerException if {@code serverContext} or {@code taskExecutor} is null
     */
	public MessengerServer(ServerContext serverContext, TaskExecutor taskExecutor) {
		super(serverContext, taskExecutor);
		reliableAssembler = new ServerReliableAssembler(serverContext, this, taskExecutor);
	}
	
	@Override
	public ChannelHandler createChannelHandler() {
		return new ServerChannelInitializer(getServerContext(), this, getTaskExecutor(), this);
	}
	
	@Override
	public void sendMessage(WsMessage<?> message) {
		super.sendMessage(message, null);
	}

	@Override
	public void sendMessage(WsMessage<?> message, String clientId) {
		super.sendMessage(message, clientId, null);
	}
	
	@Override
	public void sendMessageReliably(WsMessage<?> message, String clientId) {
		super.sendMessage(message, clientId, new SenderCallbackAdapter() {
			
			@Override
			public void onFail(WsMessage<?> msg, String receiverId) {
				reliableAssembler.addPendingMessage(receiverId, msg);
			}
			
		});
	}
	
	@Override
	public void sendWaitingMessage(WsMessage<?> message, String clientId, WaitingCallback<WsMessage<?>> callback, long timeout) {
		if (message != null) {
			message.header().setNeedResponse(true);
		}
		
		super.sendMessage(message, new SenderCallbackAdapter() {
			@Override
			public void onFail(WsMessage<?> msg, String receiverId) {
				if (callback != null) {
					callback.onFail(msg, receiverId);
				}
			}
			
			@Override
			public void onSuccess(WsMessage<?> msg, String receiverId) {
				reliableAssembler.waitingResponse(msg, receiverId, callback, timeout);
			}
		});
	}
	
	@Override
	public void sendWaitingMessage(WsMessage<?> message, String clientId, WaitingCallback<WsMessage<?>> callback) {
		if (message != null) {
			message.header().setNeedResponse(true);
		}
		
		super.sendMessage(message, new SenderCallbackAdapter() {
			@Override
			public void onFail(WsMessage<?> msg, String receiverId) {
				if (callback != null) {
					callback.onFail(msg, receiverId);
				}
			}
			
			@Override
			public void onSuccess(WsMessage<?> msg, String receiverId) {
				reliableAssembler.waitingResponse(msg, receiverId, callback);
			}
		});
	}
	
	@Override
	public void sendWaitingMessageReliably(WsMessage<?> message, String clientId, WaitingCallback<WsMessage<?>> callback) {
		if (message != null) {
			message.header().setNeedResponse(true);
		}
		
		super.sendMessage(message, new SenderCallbackAdapter() {
			@Override
			public void onFail(WsMessage<?> msg, String receiverId) {
				reliableAssembler.addPendingMessage(receiverId, msg);
			}
			
			@Override
			public void onSuccess(WsMessage<?> msg, String receiverId) {
				reliableAssembler.waitingResponse(msg, receiverId, callback);
			}
		});
	}
	
	@Override
	public void sendWaitingMessageReliably(WsMessage<?> message, String clientId, WaitingCallback<WsMessage<?>> callback, long timeout) {
		if (message != null) {
			message.header().setNeedResponse(true);
		}
		
		super.sendMessage(message, new SenderCallbackAdapter() {
			@Override
			public void onFail(WsMessage<?> msg, String receiverId) {
				reliableAssembler.addPendingMessage(receiverId, msg);
			}
			
			@Override
			public void onSuccess(WsMessage<?> msg, String receiverId) {
				reliableAssembler.waitingResponse(msg, receiverId, callback, timeout);
			}
		});
	}
	
	@Override
	public void sendWaitingMessageReliablyWithRetry(WsMessage<?> message, String clientId) {
		TaskExecutor taskExecutor = getTaskExecutor();
		taskExecutor.submitRetryable(new WaitingMessageRetryable<WsMessage<?>, ResponseMessage>(this,  taskExecutor, message, clientId));
	}
	
	@Override
	public void sendWaitingMessageReliablyWithRetry(WsMessage<?> message, String clientId, int retry) {
		TaskExecutor taskExecutor = getTaskExecutor();
		taskExecutor.submitRetryable(new WaitingMessageRetryable<WsMessage<?>, ResponseMessage>(this,  taskExecutor, message, clientId, retry));
	}
	
	@Override
	public void deliverPendingMessages(String clientId) {
		reliableAssembler.deliverPendingMessages(clientId);
	}
	
	@Override
	public void processWaitingResponse(ResponseMessage respMsg) {
		reliableAssembler.processWaitingResponse(respMsg);
	}
	
}
