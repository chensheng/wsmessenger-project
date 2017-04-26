package space.chensheng.wsmessenger.server.clientmng;

import space.chensheng.wsmessenger.common.util.JsonBean;

public class ClientInfo extends JsonBean {
	private String clientId;
	
	private String clientIp;
	
	private long connTime;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public long getConnTime() {
		return connTime;
	}

	public void setConnTime(long connTime) {
		this.connTime = connTime;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
}
