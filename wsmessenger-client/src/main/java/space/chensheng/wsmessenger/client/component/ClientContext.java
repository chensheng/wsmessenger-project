package space.chensheng.wsmessenger.client.component;

import space.chensheng.wsmessenger.common.component.MessengerContext;
import space.chensheng.wsmessenger.common.component.PropOption;

public class ClientContext extends MessengerContext {
	private static final String DEFAULT_CONFIG_PATH = "/wsmessenger-client-default.properties";
	
	private static final String CUSTOMER_CONFIG_PATH = "/wsmessenger-client.properties";
	
	private String clientId;

	private String serverUrl;
	
	private int ioThreadPoolSize;
	
	private int heartbeatSeconds;
	
	private int heartbeatMaxFail;
	
	private int reconnectMillis;
	
	private int maxContentLen;
	
	private int maxFramePayloadLen;
	
    private int businessThreadPoolSize;
	
	private int retryTaskMaxSize;
	
	
	public ClientContext() {
		super(DEFAULT_CONFIG_PATH, CUSTOMER_CONFIG_PATH);
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getClientId() {
		return clientId;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public int getIoThreadPoolSize() {
		return ioThreadPoolSize;
	}

	public int getHeartbeatSeconds() {
		return heartbeatSeconds;
	}

	public int getHeartbeatMaxFail() {
		return heartbeatMaxFail;
	}

	public int getReconnectMillis() {
		return reconnectMillis;
	}

	public int getMaxContentLen() {
		return maxContentLen;
	}

	public int getMaxFramePayloadLen() {
		return maxFramePayloadLen;
	}

	public int getBusinessThreadPoolSize() {
		return businessThreadPoolSize;
	}

	public int getRetryTaskMaxSize() {
		return retryTaskMaxSize;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public void setIoThreadPoolSize(int ioThreadPoolSize) {
		this.ioThreadPoolSize = ioThreadPoolSize;
	}

	public void setHeartbeatSeconds(int heartbeatSeconds) {
		this.heartbeatSeconds = heartbeatSeconds;
	}

	public void setHeartbeatMaxFail(int heartbeatMaxFail) {
		this.heartbeatMaxFail = heartbeatMaxFail;
	}

	public void setReconnectMillis(int reconnectMillis) {
		this.reconnectMillis = reconnectMillis;
	}

	public void setMaxContentLen(int maxContentLen) {
		this.maxContentLen = maxContentLen;
	}

	public void setMaxFramePayloadLen(int maxFramePayloadLen) {
		this.maxFramePayloadLen = maxFramePayloadLen;
	}

	public void setBusinessThreadPoolSize(int businessThreadPoolSize) {
		this.businessThreadPoolSize = businessThreadPoolSize;
	}

	public void setRetryTaskMaxSize(int retryTaskMaxSize) {
		this.retryTaskMaxSize = retryTaskMaxSize;
	}
}
