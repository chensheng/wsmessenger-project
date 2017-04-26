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
sendMessage(WsMessage message)|send  message to all connected clients
sendMessage(WsMessage message, String clientId)|send message to specific client

###### Client side's sender APIs
API|description(serverId can be null)
---|---
sendMessage(WsMessage message)|send  message to server
sendMessage(WsMessage message, String serverId)|send message to server
