package space.chensheng.wsmessenger.client.reliable;

import space.chensheng.wsmessenger.common.reliable.WaitingCallback;
import space.chensheng.wsmessenger.message.component.WsMessage;

public class WaitingCallbackAdapter implements WaitingCallback<WsMessage<?>> {

	@Override
	public void onSuccess(WsMessage<?> message, String receiverId) {
	}

	@Override
	public void onFail(WsMessage<?> message, String receiverId) {
	}

	@Override
	public void onTimeout(WsMessage<?> message, String receiverId) {
	}

}
