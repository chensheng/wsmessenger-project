# Wsmessenger Project
Wsmessenger is a long connection message middleware based on websocket protocl. It is built upon netty, so it can work well in high concurrency circumstances. It can be used to create PUSH service for android app. It also can be used to create monitor tools for web cluster.

## Quit start guide
* [Import dependencies](#import-dependencies)
* [Set up server side](#set-up-server-side)
* [Set up client side](#set-up-client-side)
* [Send message](#send-message)
* [Implement customized message](#implement-customized-message)
* [Listen message](#listen-message)
* [Listen lifecycle](#listen-lifecycle)
* [Advanced configuration](#advanced-configuration)

### Import dependencies
Here we use MAVEN to manage project's dependencies. 

For server side, you should import `wsmessenger-server`.
```
<dependency>
  <groupId>space.chensheng.wsmessenger</groupId>
  <artifactId>wsmessenger-server</artifactId>
  <version>1.0.0</version>
</dependency>
```

For client side, you should import `wsmessenger-client`.
```
<dependency>
  <groupId>space.chensheng.wsmessenger</groupId>
  <artifactId>wsmessenger-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

To create customized messages, you should import `wsmessenger-message`
```
<dependency>
  <groupId>space.chensheng.wsmessenger</groupId>
  <artifactId>wsmessenger-message</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Set up server side
`ServerBootstrap` is used to set up the server side. The server will listen `2046` port by default.

Simply set up the server side.
```java
WsMessengerServer server = new ServerBootstrap().build();
server.start();
```

If you want to listen server's lifecycle and received messages, listeners can be added when setting up the server side. 
```java
WsMessengerServer server = new ServerBootstrap()
            .addLifecycleListener(new MyServerLifecycleListener())
            .addMessageListener(new MyTextMessageListener())
            .build();
server.start();
```

### Set up client side
`ClientBoostrap` is used to set up the client side. Once client starts, it will establish a long connection to server through websocket protocol.

Before setting up the client side, you should create a config file `wsmessenger-client.properties` in your project. The config should specify `clientId` and `serverUrl`.
```
clientId=your-wsmessenger-client-id
serverUrl=ws://127.0.0.1:2046
```

Simply set up the client side.
```java
WsMessengerClient client = new ClientBootstrap().build();
client.start();
```

If you want to listen client's lifecycle and received messages, listeners can be added when setting up the client side.
```java
WsMessengerClient client = new ClientBootstrap()
            .addLifecycleListener(new MyClientLifecycleListener())
            .addMessageListener(new MyTextMessageListener())
            .build();
client.start();
```

### Send message
Server and client can comunicate with each other by sending message. 

In server side, `WsMessengerServer` is used to send message.
```java
TextMessage message = new TextMessage("This is a text message sent to client!");
server.sendMessage(message, clientId);
```

In client side, `WsMessengerClient` is used to send message.
```java
TextMessage message = new TextMessage("This is a text message sent to server!");
client.sendMessage(message, null);
```

###### Server side's sender APIs
API|description
---|---
sendMessage(WsMessage message)|Send  message to all connected clients.
sendMessage(WsMessage message, String clientId)|Send message to specific client.
sendMessageReliably(WsMessage message, String clientId)|Send message to specific client, and add message to pending queue if client is unavailable.
sendWaitingMessage(WsMessage message, String clientId, WaitingCallback callback)|Send message to specific client, and waiting for client's response. Trigger callback when receiving response or timeout.
sendWaitingMessage(WsMessage message, String clientId, WaitingCallback callback, long timeout)|Send message to specific client, and waiting for client's response in specific timeout milliseconds. Trigger callback when receiving response or timeout.
sendWaitingMessageReliably(WsMessage message, String clientId, WaitingCallback callback)|Send message to specific client, and waiting for client's response. Trigger callback when receiving response or timeout. Add message to pending queue if client is unavailable.
sendWaitingMessageReliably(WsMessage message, String clientId, WaitingCallback callback, long timeout)|Send message to specific client, and waiting for client's response in specific timeout milliseconds. Trigger callback when receiving response or timeout. Add message to pending queue if client is unavailable.
sendWaitingMessageReliablyWithRetry(WsMessage message, String clientId)|Send message to specific client, and waiting for client's response. Retry 3 times until receiving success response. Add message to pending queue if client is unavailable.
sendWaitingMessageReliablyWithRetry(WsMessage message, String clientId, int retry)|Send message to specific client, and waiting for client's response. Retry specific times until receiving success response. Add message to pending queue if client is unavailable.
	
###### Client side's sender APIs
API(serverId is always null)|description
---|---
sendMessage(WsMessage message)|send  message to server
sendMessage(WsMessage message, String serverId)|send message to server
sendMessageReliably(WsMessage message, String serverId)|Send message to server, and add message to pending queue if server is unavailable.
sendWaitingMessage(WsMessage message, String serverId, WaitingCallback callback)|Send message to server, and waiting for server's response. Trigger callback when receiving response or timeout.
sendWaitingMessage(WsMessage message, String serverId, WaitingCallback callback, long timeout)|Send message to server, and waiting for server's response in specific timeout milliseconds. Trigger callback when receiving response or timeout.
sendWaitingMessageReliably(WsMessage message, String serverId, WaitingCallback callback)|Send message to server, and waiting for server's response. Trigger callback when receiving response or timeout. Add message to pending queue if server is unavailable.
sendWaitingMessageReliably(WsMessage message, String serverId, WaitingCallback callback, long timeout)|Send message to server, and waiting for server's response in specific timeout milliseconds. Trigger callback when receiving response or timeout. Add message to pending queue if server is unavailable.
sendWaitingMessageReliablyWithRetry(WsMessage message, String serverId)|Send message to specific server, and waiting for server's response. Retry 3 times until receiving success response. Add message to pending queue if server is unavailable.
sendWaitingMessageReliablyWithRetry(WsMessage message, String serverId, int retry)|Send message to specific server, and waiting for server's response. Retry specific times until receiving success response. Add message to pending queue when server is unavailable.

### Implement customized message
Developer can implement customized message, and send it between server and client. To implement a customized message, you should use `WsMessage` and `MessageBody`. The following is an example:

Firstly, implement a customized message body.
```java
import space.chensheng.wsmessenger.message.component.MessageBody;

public class UserInfoMessageBody extends MessageBody {
    private int userId;
    
    private String userName;
    
    public UserInfoMessageBody(int userId, String userName) {
        this.userId = userId;
	this.userName = userName;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getUserName() {
        return userName;
    }
}
```

Secondly, implement a customized message with the customized message body.
```java
import space.chensheng.wsmessenger.message.component.WsMessage;
import your.package.to.UserInfoMessageBody;

public class UserInfoMessage extends WsMessage<UserInfoMessageBody> {
    //A non-argument constructor is required.
    public UserInfoMessage() {
        this(0, null);
    }
    
    public UserInfoMessage(int userId, String userName) {
        super(new UserInfoMessageBody(userId, userName));
    }
}
```

Finally, send the customized message.
```java
UserInfoMessage message = new UserInfoMessage(123, "wsmessenger");
client.sendMessage(message, null);
```

### Listen message
Message listeners can be added to listen specific message. Developer should implement your own `MessageListener`.

In server side, `ServerMessageListener` is used to implement. The following is an example listening `TextMessage`:
```java
public class MyTextMessageListener extends ServerMessageListener<TextMessage> {
    
    @Override
    protected void onMessage(TextMessage message, ClientInfo clientInfo, MessengerServer server) {
        String replyContent = "Hello client, i have received your text message " + message.body().getContent();
        TextMessage relyMessage = new TextMessage(replyContent);
	server.sendWaitingMessageReliablyWithRetry(replyMessage, clientInfo.getClientId());	
    }
    
}
```

In client side, `ClientMessageListener` is used to implement. The following is an example listening `TextMessage`:
```java
public class MyTextMessageListener extends ClientMessageListener<TextMessage> {

    @Override
    protected void onMessage(TextMessage message, MessengerClient client) {
        String replyContent = "Hello server, i have received your text message " + message.body().getContent();
        TextMessage relyMessage = new TextMessage(replyContent);
	client.sendWaitingMessageReliablyWithRetry(replyMessage, null);
    }

}
```

### Listen lifecycle
Lifecycle listeners can be added to listen server and client's lifecycles. Developer should implement your own `LifecycleListener`.

In server side, `ServerLifecycleListener` is used to implement. The following is an example:
```java
public class MyServerLifecycleListener extends ServerLifecycleListener {
	
    @Override
    public void onServerStart(MessengerServer server) {
        //server is started
    }
    
    @Override
    public void onClientConnect(ClientInfo clientInfo, MessengerServer server) {
        //client is connected
    }
    
    @Override
    public void onClientDisconnect(ClientInfo clientInfo, MessengerServer server) {
        //client is disconnected 
    }
    
}
```

In client side, `ClientLifecycleListener` is used to implement. The following is an example:
```java
public class MyClientLifecycleListener extends ClientLifecycleListener {

    @Override
    public void onClientConnect(MessengerClient client) {
        //success to connect server
    }
    
    @Override
    public void onClientStart(MessengerClient client) {
        //client is started
    }
    
    @Override
    public void onClientStop(MessengerClient client) {
        //client is stopped
    }
    
    @Override
    public void onClientRestart(MessengerClient client) {
        //client restarts
    }

}
```

## Advanced configuration
There are serveal advanced configurations for server and client.

In server side, you can create a config file named `wsmessenger-server.properties` in your project. It is optional. The following table shows configuration details.
Config property name|Config property value
-----|-----
serverId|Default is `wsmessenger-server`. The server's id. The `serverId` will be set to message when sending message to client.
serverPort|Default  is `2046`.  The port listened by server.
pendingClientMaxCount|Default is `100`. Server will keep messages in pending queue for unavailable clients. If unavailable client's size exceed max count, new messages will not keep for new unavailable clients.
pendingClientMaxMsg|Default is `100`. The max number of messages kept in pending queue for single unavailable client.
pendingClientTimeoutMillis|Default is `3600000`. Time unit is millisecond. If an unavailable client sitting in pending queue exceeds `pendingClientTimeoutMillis`, it will be removed from the pending queue.
pendingClientTimeoutCheckerIntervalMinutes|Default is `5`. Time unit is minute. The interval minutes to check whether unavailable clients is timeout or not.
maxPendingMsg|Default is `1000`. The total number of messages kept in pending queue for all unavailable clients.
acceptorThreadSize|Default is `1`. The number of threads  to accept connections from clients. One thread is enough.
ioThreadSize|Default is `4`. The number of threads to read io from clients.
businessThreadSize|Default is `8`. The number of threads to execute business tasks.
heartbeatIntervalSeconds|Default is `60`. Time unit is second. The interval seconds to send heartbeat to clients. 
heartbeatMaxFail|Default is `2`. The max fail times of heartbeat. The long connection to client will be closed if heartbeat fail specific times.
soBacklog|Default is `3074`. The same as socBacklog in TCP/IP.
allowHalfClosure|Default is `false`. The same as allowHalfClosure in TCP/IP
waitingMsgTimoutMillis|Default is `120000`. Time unit is millisecond. The timeout to wait receiver's response for message that needed response. If timeout to wait resposne, the timeout callback will be triggered.
waitingMsgMaxSize|Default is `1000`. The number of messages kept in waiting response queue. New waiting inforamtion will not be added to waiting queue if waiting size execceds the max size. 
maxContentLen|Default is `1048576`. Unit is byte. The max size of message.
maxFrameSize|Default is `1048576`. Unit is byte. The max size of message.
retryTaskMaxSize|Default if `1000`. The max number of retry tasks kept in retry queue. New retry task will be dropped if the retry queue execceds max size. 
