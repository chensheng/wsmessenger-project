package space.chensheng.wsmessenger.server.reliable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.chensheng.wsmessenger.common.component.Messenger;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.common.reliable.PendingMessageProcessor;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.server.component.ServerContext;

import java.util.concurrent.*;
import java.util.function.Function;

public class ServerPendingMessageProcessor implements PendingMessageProcessor<WsMessage> {
	private static final Logger logger = LoggerFactory.getLogger(ServerPendingMessageProcessor.class);
	
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<WsMessage>> pendingMsgMap = new ConcurrentHashMap<String, ConcurrentLinkedQueue<WsMessage>>();
	
	private DelayQueue<TimeoutInfo> timeoutInfoQueue = new DelayQueue<TimeoutInfo>();
	
	private volatile boolean timeoutCheckerStarted = false;
	
	private Semaphore timeoutCheckerSemaphore = new Semaphore(1);
	
	private ServerContext serverContext;
	
	private Messenger<WsMessage> messenger;
	
	private TaskExecutor taskExecutor;
	
	ServerPendingMessageProcessor(ServerContext serverContext, Messenger<WsMessage> messenger, TaskExecutor taskExecutor) {
		if (serverContext == null) {
			throw new NullPointerException("serverContext may not be null");
		}
		
		if (messenger == null) {
			throw new NullPointerException("messenger may not be null");
		}
		
		if (taskExecutor == null) {
			throw new NullPointerException("taskExecutor may not be null");
		}
		
		this.serverContext = serverContext;
		this.messenger = messenger;
		this.taskExecutor = taskExecutor;
	}
	
	@Override
	public void deliverPendingMessages(String receiverId) {
		if (receiverId == null) {
			return;
		}
		
		String pendingId = createPendingId(receiverId);
		ConcurrentLinkedQueue<WsMessage> pendingMsgQueue = pendingMsgMap.remove(pendingId);
		if (pendingMsgQueue != null && pendingMsgQueue.size() > 0) {
			logger.info("start to deliver pending message to client {}", pendingId);
			int msgCount = pendingMsgQueue.size();
			WsMessage pendingMsg = pendingMsgQueue.poll();
			while (pendingMsg != null) {
				messenger.sendMessage(pendingMsg, receiverId);
				pendingMsg = pendingMsgQueue.poll();
			}
			pendingMsgQueue = null;
			logger.info("finish to deliver {} pending message to client {}.", msgCount, pendingId);
		}
	}

	@Override
	public void addPendingMessage(String receiverId, WsMessage message) {
		if (receiverId == null || message == null) {
			return;
		}
		
		String pendingId = createPendingId(receiverId);
		doAddMessage(pendingId, message);
	}

	@Override
	public void shutdown() {
		timeoutInfoQueue.clear();
		pendingMsgMap.clear();
	}

	@Override
	public boolean isShutdown() {
		return true;
	}
	
	private String createPendingId(String clientId) {
		return clientId;
	}
	
	private void doAddMessage(String pendingId, WsMessage msg) {
		startTimeoutCheckerInNeed();
		ConcurrentLinkedQueue<WsMessage> pendingMsgQueue = pendingMsgMap.computeIfAbsent(pendingId, new Function<String, ConcurrentLinkedQueue<WsMessage>>() {

			@Override
			public ConcurrentLinkedQueue<WsMessage> apply(String t) {
				if (pendingMsgMap.size() >= serverContext.getPendingClientMaxCount()) {
					logger.error("pending clients exceed {}, new pending client will not be accepted.", serverContext.getPendingClientMaxCount());
					return null;
				}
				timeoutInfoQueue.put(new TimeoutInfo(pendingId));
				return new ConcurrentLinkedQueue<WsMessage>();
			}
			
		});
		
		if (pendingMsgQueue == null) {
			return;
		}
		
		if (pendingMsgQueue.size() >= serverContext.getPendingClientMaxCount()) {
			logger.error("pending clients exceed {}, new pending client will not be accepted.", serverContext.getPendingClientMaxCount());
			return;
		}
		
		pendingMsgQueue.offer(msg);
		logger.info("add pending message for client {}, message:{}.", pendingId, msg);
	}
	
	private void startTimeoutCheckerInNeed() {
		if (!timeoutCheckerStarted) {
			if (!timeoutCheckerSemaphore.tryAcquire()) {
				return;
			}
			try {
				if (!timeoutCheckerStarted) {
					timeoutCheckerStarted = true;
					taskExecutor.scheduleTaskAtFixedRate(new TimeoutChecker(), 
							1, serverContext.getPendingClientTimeoutCheckerIntervalMinutes(), TimeUnit.MINUTES);
				}
			} finally {
				timeoutCheckerSemaphore.release();
			}
		}
	}
	
	private class TimeoutChecker implements Runnable {
		private static final int BATCH_SIZE = 50;
		
		@Override
		public void run() {
			int times = 0;
			int timeoutCount = 0;
			TimeoutInfo ti;
			logger.info("Pending client timeout checker begin checking.");
			while (times < BATCH_SIZE) {
				ti = timeoutInfoQueue.poll();
				if (ti != null) {
					ConcurrentLinkedQueue<WsMessage> timeoutMsgQueue = pendingMsgMap.remove(ti.getPendingId());
					if (timeoutMsgQueue != null) {
						timeoutMsgQueue.clear();
						timeoutMsgQueue = null;
					}
					timeoutCount++;
				}
				times++;
			}
			logger.info("Pending client timeout checker finish checking, {} pending clients were removed.", timeoutCount);
		}	
	}
	
	private class TimeoutInfo implements Delayed {
        private String pendingId;
		
		private long trigger;
		
		public TimeoutInfo(String pendingId) {
			this(pendingId, serverContext.getPendingClientTimeoutMillis());
		}
		
		public TimeoutInfo(String pendingId, long delay) {
			this.pendingId = pendingId;
			this.trigger = System.currentTimeMillis() + delay;
		}
		
		public String getPendingId() {
			return pendingId;
		}
		
		@Override
		public int compareTo(Delayed o) {
			TimeoutInfo other = (TimeoutInfo) o;
			int result = 0;
			if (this.trigger > other.trigger) {
				result = 1;
			} else if (this.trigger < other.trigger) {
				result = -1;
			} else {
				result = 0;
			}
			return result;
		}

		@Override
		public long getDelay(TimeUnit unit) {
			long leftMills = trigger - System.currentTimeMillis();
			return unit.convert(leftMills, TimeUnit.MILLISECONDS);
		}
		
	}
}
