package space.chensheng.wsmessenger.message.sysmsg;

import space.chensheng.wsmessenger.message.body.ResponseBody;
import space.chensheng.wsmessenger.message.component.WsMessage;

public class ResponseMessage extends WsMessage<ResponseBody> {

	public ResponseMessage() {
		this(0, false);
	}
	
	public ResponseMessage(long respMessageId, boolean success) {
		super(new ResponseBody(respMessageId, success));
	}
	
}
