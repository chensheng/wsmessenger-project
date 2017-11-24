package space.chensheng.wsmessenger.client.component;

import space.chensheng.wsmessenger.common.component.MessengerContext;
import space.chensheng.wsmessenger.common.component.PropOption;

public class ClientContext extends MessengerContext {
	private static final String DEFAULT_CONFIG_PATH = "/wsmessenger-client-default.properties";
	
	private static final String CUSTOMER_CONFIG_PATH = "/wsmessenger-client.properties";
	
	private String clientId;
	
	@PropOption(notNull = true)
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

}
