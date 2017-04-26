# Wsmessenger Project
Wsmessenger is a long connection message middleware based on websocket protocl. It is built upon netty, so it can work well in high concurrency circumstances. It can be used to create PUSH service for android app. It also can be used to create monitor tools for cluster web applications.

## Quit start guide
* [Import dependencies](#import-dependencies)
* [Set up server side](#set-up-server-side)
* [Set up client side](#set-up-client-side)

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
`ServerBootstrap` is used to set up the server side.

Simply set up the server side.
```java
WsMessengerServer server = new ServerBootstrap().build();
server.start();
```

If you want to listen server's lifecycle and messages from client, listeners can be added when setting up server side. More informations about listeners will be follow.
```java
WsMessengerServer server = new ServerBootstrap()
            .addLifecycleListener(new MyServerLifecycleListener())
        		.addMessageListener(new MyTextMessageListener())
        		.build();
server.start();
```
