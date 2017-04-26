package space.chensheng.wsmessenger.server.component;

import space.chensheng.wsmessenger.common.component.MessengerContext;

public class ServerContext extends MessengerContext {
	private static final String DEFAULT_CONFIG_PATH = "/wsmessenger-server-default.properties";
	
	private static final String CUSTOMER_CONFIG_PATH = "/wsmessenger-server.properties";
	
	private String serverId;
	
	private int serverPort;
	
	private int pendingClientMaxCount;
	
	private int pendingClientMaxMsg;
	
	private int pendingClientTimeoutMillis;
	
	private int pendingClientTimeoutCheckerIntervalMinutes;
	
	private int acceptorThreadSize;
	
	private int ioThreadSize;
	
	private int heartbeatIntervalSeconds;
	
	private int heartbeatMaxFail;
	
	private int soBacklog;
	
	private boolean allowHalfClosure;
	
	private int maxContentLen;
	
	private int maxFrameSize;
	
	private int businessThreadSize;
	
	private int retryTaskMaxSize;
	
	public ServerContext() {
		super(DEFAULT_CONFIG_PATH, CUSTOMER_CONFIG_PATH);
	}

	public String getServerId() {
		return serverId;
	}

	public int getServerPort() {
		return serverPort;
	}

	public int getPendingClientMaxCount() {
		return pendingClientMaxCount;
	}

	public int getPendingClientMaxMsg() {
		return pendingClientMaxMsg;
	}

	public int getPendingClientTimeoutMillis() {
		return pendingClientTimeoutMillis;
	}

	public int getPendingClientTimeoutCheckerIntervalMinutes() {
		return pendingClientTimeoutCheckerIntervalMinutes;
	}

	public int getAcceptorThreadSize() {
		return acceptorThreadSize;
	}

	public int getIoThreadSize() {
		return ioThreadSize;
	}

	public int getHeartbeatIntervalSeconds() {
		return heartbeatIntervalSeconds;
	}

	public int getHeartbeatMaxFail() {
		return heartbeatMaxFail;
	}

	public int getSoBacklog() {
		return soBacklog;
	}

	public boolean isAllowHalfClosure() {
		return allowHalfClosure;
	}

	public int getMaxContentLen() {
		return maxContentLen;
	}

	public int getMaxFrameSize() {
		return maxFrameSize;
	}

	public int getBusinessThreadSize() {
		return businessThreadSize;
	}

	public int getRetryTaskMaxSize() {
		return retryTaskMaxSize;
	}
}
