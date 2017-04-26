package space.chensheng.wsmessenger.message.body;

import space.chensheng.wsmessenger.message.component.MessageBody;

public class StringBody extends MessageBody {
	private String content;

	public StringBody(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
