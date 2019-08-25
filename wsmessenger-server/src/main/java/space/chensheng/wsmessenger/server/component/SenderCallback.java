package space.chensheng.wsmessenger.server.component;

import space.chensheng.wsmessenger.message.component.WsMessage;

public interface SenderCallback {
	void onSuccess(WsMessage msg, String receiverId);
	
	void onFail(WsMessage msg, String receiverId);
	
}
