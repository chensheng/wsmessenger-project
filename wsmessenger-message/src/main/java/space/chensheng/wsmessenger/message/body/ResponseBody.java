package space.chensheng.wsmessenger.message.body;

import space.chensheng.wsmessenger.message.component.MessageBody;

public class ResponseBody extends MessageBody {
	private long respMessageId;
	
	private boolean success;
	
	public ResponseBody(long respMessageId, boolean success) {
		this.respMessageId = respMessageId;
		this.success = success;
	}

	public long getRespMessageId() {
		return respMessageId;
	}

	public boolean isSuccess() {
		return success;
	}
}
