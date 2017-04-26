package space.chensheng.wsmessenger.server.component;

import space.chensheng.wsmessenger.message.component.WsMessage;

public class SenderCallbackAdapter implements SenderCallback {

	@Override
	public void onSuccess(WsMessage<?> msg, String receiverId) {
	}

	@Override
	public void onFail(WsMessage<?> msg, String receiverId) {
	}
}
