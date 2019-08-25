package space.chensheng.wsmessenger.server.clientmng;

import space.chensheng.wsmessenger.common.util.JsonBean;

import java.util.Map;

public class ClientInfo extends JsonBean {
	private String clientId;
	
	private String clientIp;
	
	private long clientConnTime;

	private Map<String, String> clientHeaders;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

    public long getClientConnTime() {
        return clientConnTime;
    }

    public void setClientConnTime(long clientConnTime) {
        this.clientConnTime = clientConnTime;
    }

    public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

    public Map<String, String> getClientHeaders() {
        return clientHeaders;
    }

    public void setClientHeaders(Map<String, String> clientHeaders) {
        this.clientHeaders = clientHeaders;
    }
}
