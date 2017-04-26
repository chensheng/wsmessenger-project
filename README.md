# Wsmessenger Project
Wsmessenger is a long connection message middleware based on websocket protocl. It is built upon netty, so it can work well in high concurrency circumstances. It can be used to create PUSH service for android app. It also can be used to create monitor tools for cluster web applications.

## Quit start guide
* [Import dependencies](#import-dependencies)
* [Set up server side](#set-up-server-side)
* [Set up client side](#set-up-client-side)
* [Send message](#send-message)

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
