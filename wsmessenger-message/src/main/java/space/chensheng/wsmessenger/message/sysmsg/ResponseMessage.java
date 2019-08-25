package space.chensheng.wsmessenger.message.sysmsg;

import space.chensheng.wsmessenger.message.component.WsMessage;

public class ResponseMessage extends WsMessage {

	private long respMessageId;

	private boolean success;

    public long getRespMessageId() {
        return respMessageId;
    }

    public void setRespMessageId(long respMessageId) {
        this.respMessageId = respMessageId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
