package space.chensheng.wsmessenger.message.sysmsg;

import space.chensheng.wsmessenger.message.body.StringBody;
import space.chensheng.wsmessenger.message.component.WsMessage;

public class ClientRegistryMessage extends WsMessage<StringBody> {

	public ClientRegistryMessage() {
		this(null);
	}
	
	public ClientRegistryMessage(String clientId) {
		super(new StringBody(clientId));
	}

}
