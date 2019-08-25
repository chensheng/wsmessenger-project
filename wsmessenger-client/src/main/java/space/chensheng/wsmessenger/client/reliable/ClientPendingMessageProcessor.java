package space.chensheng.wsmessenger.client.reliable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.chensheng.wsmessenger.client.component.ClientContext;
import space.chensheng.wsmessenger.common.component.Messenger;
import space.chensheng.wsmessenger.common.reliable.PendingMessageProcessor;
import space.chensheng.wsmessenger.message.component.WsMessage;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientPendingMessageProcessor implements PendingMessageProcessor<WsMessage> {
	private static final Logger logger = LoggerFactory.getLogger(ClientPendingMessageProcessor.class);
	
	private ConcurrentLinkedQueue<WsMessage> pendingMsgQueue = new ConcurrentLinkedQueue<WsMessage>();
	
	private Messenger<WsMessage> messenger;
	
	private ClientContext clientContext;
	
	private volatile boolean isProcessorAlive = true;
	
	ClientPendingMessageProcessor(ClientContext clientContext, Messenger<WsMessage> messenger) {
		if (clientContext == null) {
			throw new NullPointerException("clientContext may not be null");
		}
		
		if (messenger == null) {
			throw new NullPointerException("messenger may not be null");
		}
		
		this.clientContext = clientContext;
		this.messenger = messenger;
	}
	
	@Override
	public void deliverPendingMessages(String receiverId) {
		logger.info("Begin to deliver pending message");
		
		int msgCount = pendingMsgQueue.size();
		if (msgCount > 0) {
			WsMessage pendingMsg = pendingMsgQueue.poll();
			while (pendingMsg != null) {
				messenger.sendMessage(pendingMsg);
				pendingMsg = pendingMsgQueue.poll();
			}
		}
		
		logger.info("Finish to deliver {} pending message", msgCount);
	}

	@Override
	public void addPendingMessage(String receiverId, WsMessage message) {
		if (message == null) {
			return;
		}
		
		if (pendingMsgQueue.size() >= clientContext.getMaxPendingMsg()) {
			logger.error("Pending message count exceeds {}, new message will not be accepted. message:{}.",
					clientContext.getMaxPendingMsg(), message);
			return;
		}
		
		pendingMsgQueue.offer(message);
		logger.debug("Add pending message, {}", message);
	}

	@Override
	public void shutdown() {
		pendingMsgQueue.clear();
		isProcessorAlive = false;
	}

	@Override
	public boolean isShutdown() {
		return !isProcessorAlive;
	}
}
