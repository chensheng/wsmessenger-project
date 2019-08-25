package space.chensheng.wsmessenger.message.sysmsg;

import space.chensheng.wsmessenger.message.component.WsMessage;

public class TextMessage extends WsMessage {
	private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
