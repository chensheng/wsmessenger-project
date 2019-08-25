package space.chensheng.wsmessenger.client;

import io.netty.channel.ChannelHandler;
import space.chensheng.wsmessenger.client.component.ClientContext;
import space.chensheng.wsmessenger.client.component.SenderCallbackAdapter;
import space.chensheng.wsmessenger.client.executor.ClientTaskExecutor;
import space.chensheng.wsmessenger.client.handler.ClientChannelInitializer;
import space.chensheng.wsmessenger.client.reliable.ClientReliableAssembler;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.common.reliable.ReliableMessenger;
import space.chensheng.wsmessenger.common.reliable.WaitingCallback;
import space.chensheng.wsmessenger.common.reliable.WaitingMessageRetryable;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.message.sysmsg.ResponseMessage;

/**
 * A class implements basic functions to send message to messenger server through websocket protocol built upon netty. 
 * @author sheng.chen
 */
public abstract class MessengerClient extends AbstractNettyClient implements ReliableMessenger<WsMessage, ResponseMessage> {
	private ClientReliableAssembler reliableAssembler;
	
	public MessengerClient() {
		this(new ClientContext());
	}
	
	/**
	 * 
	 * @param clientContext
	 * @throws NullPointerException if {@code clientContext} is null
	 */
	public MessengerClient(ClientContext clientContext) {
		this(clientContext, new ClientTaskExecutor(clientContext));
	}
	
	/**
	 * 
	 * @param clientContext
	 * @param taskExecutor
	 * @throws NullPointerException if {@code clientContext} or {@code taskExecutor} is null
 	 */
	public MessengerClient(ClientContext clientContext, TaskExecutor taskExecutor) {
		super(clientContext, taskExecutor);
		reliableAssembler = new ClientReliableAssembler(clientContext, this, taskExecutor);
	}

	@Override
	public ChannelHandler createChannelHandler() {
		return new ClientChannelInitializer(getClientContext(), this, this, getTaskExecutor());
	}

	@Override
	public void sendMessage(WsMessage message) {
		super.sendMessage(message, null);	
	}
	
	@Override
	public void sendMessage(WsMessage message, String receiverId) {
		super.sendMessage(message, null);
	}
	
	@Override
	public void sendMessageReliably(WsMessage message, String receiverId) {
		super.sendMessage(message, new SenderCallbackAdapter() {
			@Override
			public void onFail(WsMessage msg) {
				reliableAssembler.addPendingMessage(receiverId, message);
			}
		});
	}
	
	@Override
	public void sendWaitingMessage(WsMessage message, String receiverId, WaitingCallback<WsMessage> callback) {
		if (message != null) {
			message.getHeader().setNeedResponse(true);
		}
		
		super.sendMessage(message, new SenderCallbackAdapter() {
			@Override
			public void onFail(WsMessage msg) {
				if (callback != null) {
					callback.onFail(msg, receiverId);
				}
			}
			
			@Override
			public void onSuccess(WsMessage msg) {
				reliableAssembler.waitingResponse(msg, receiverId, callback);
			}
		});
	}
	
	@Override
	public void sendWaitingMessage(WsMessage message, String receiverId, WaitingCallback<WsMessage> callback, long timeout) {
		if (message != null) {
			message.getHeader().setNeedResponse(true);
		}
		
		super.sendMessage(message, new SenderCallbackAdapter() {
			@Override
			public void onFail(WsMessage msg) {
				if (callback != null) {
					callback.onFail(msg, receiverId);
				}
			}
			
			@Override
			public void onSuccess(WsMessage msg) {
				reliableAssembler.waitingResponse(msg, receiverId, callback, timeout);
			}
		});
	}
	
	@Override
	public void sendWaitingMessageReliably(WsMessage message, String receiverId, WaitingCallback<WsMessage> callback) {
		if (message != null) {
			message.getHeader().setNeedResponse(true);
		}
		
		super.sendMessage(message, new SenderCallbackAdapter() {
			@Override
			public void onFail(WsMessage msg) {
				reliableAssembler.addPendingMessage(receiverId, msg);
			}
			
			@Override
			public void onSuccess(WsMessage msg) {
				reliableAssembler.waitingResponse(msg, receiverId, callback);
			}
		});
	}
	
	@Override
	public void sendWaitingMessageReliably(WsMessage message, String receiverId, WaitingCallback<WsMessage> callback, long timeout) {
		if (message != null) {
			message.getHeader().setNeedResponse(true);
		}
		
		super.sendMessage(message, new SenderCallbackAdapter() {
			@Override
			public void onFail(WsMessage msg) {
				reliableAssembler.addPendingMessage(receiverId, msg);
			}
			
			@Override
			public void onSuccess(WsMessage msg) {
				reliableAssembler.waitingResponse(msg, receiverId, callback, timeout);
			}
		});
	}
	
	@Override
	public void sendWaitingMessageReliablyWithRetry(WsMessage message, String receiverId) {
		TaskExecutor taskExecutor = getTaskExecutor();
		taskExecutor.submitRetryable(new WaitingMessageRetryable<WsMessage, ResponseMessage>(this, taskExecutor, message, receiverId));
	}
	
	@Override
	public void sendWaitingMessageReliablyWithRetry(WsMessage message, String receiverId, int retry) {
	    TaskExecutor taskExecutor = getTaskExecutor();
		taskExecutor.submitRetryable(new WaitingMessageRetryable<WsMessage, ResponseMessage>(this, taskExecutor, message, receiverId, retry));
	}
	
	@Override
	public void processWaitingResponse(ResponseMessage respMsg) {
		reliableAssembler.processWaitingResponse(respMsg);	
	}
	
	@Override
	public boolean stop() {
		reliableAssembler.shutdown();
		return super.stop();
	}
	
	@Override
	public void deliverPendingMessages(String receiverId) {	
		reliableAssembler.deliverPendingMessages(receiverId);	
	}
	
}
