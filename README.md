# Wsmessenger Project
Wsmessenger is a long connection message middleware based on websocket protocl. It works well in high concurrency circumstances because it is built upon netty project. It can be used to create PUSH service for android app. It also can be used to create monitor tools for cluster web applications.

## Quit start guide
* [Import dependencies](#)

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

To create customized message type, you should import `wsmessenger-message`
```
<dependency>
  <groupId>space.chensheng.wsmessenger</groupId>
  <artifactId>wsmessenger-message</artifactId>
  <version>1.0.0</version>
</dependency>
```
