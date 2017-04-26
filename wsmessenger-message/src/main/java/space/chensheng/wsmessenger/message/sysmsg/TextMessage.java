package space.chensheng.wsmessenger.message.sysmsg;

import space.chensheng.wsmessenger.message.body.StringBody;
import space.chensheng.wsmessenger.message.component.WsMessage;

public class TextMessage extends WsMessage<StringBody> {
	public TextMessage() {
		this(null);
	}
	
	public TextMessage(String content) {
		super(new StringBody(content));
	}

}
