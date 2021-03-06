package space.chensheng.wsmessenger.server.util;

import io.netty.util.AttributeKey;

import java.util.Map;

public class ServerConstants {
	public static final AttributeKey<String> ATTR_CLIENT_ID = AttributeKey.valueOf("clientId");
	
	public static final AttributeKey<Long> ATTR_CLIENT_CONN_TIME = AttributeKey.valueOf("clientConnectTime");
	
	public static final AttributeKey<String> ATTR_CLIENT_IP = AttributeKey.valueOf("clientIp");

	public static final AttributeKey<Map<String, String>> ATTR_CLIENT_HEADERS = AttributeKey.valueOf("clientHeaders");
}
