package space.chensheng.wsmessenger.client.component;

import space.chensheng.wsmessenger.message.component.WsMessage;

public interface SenderCallback {
	void onSuccess(WsMessage msg);
	
	void onFail(WsMessage msg);
	
	void onError(WsMessage msg);
}
