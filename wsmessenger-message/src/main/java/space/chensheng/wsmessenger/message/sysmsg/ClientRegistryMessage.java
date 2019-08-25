package space.chensheng.wsmessenger.message.sysmsg;

import space.chensheng.wsmessenger.message.component.WsMessage;

public class ClientRegistryMessage extends WsMessage {
    private String clientId;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
