package space.chensheng.wsmessenger.server.handler;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCounted;
import space.chensheng.wsmessenger.common.util.StringUtil;
import space.chensheng.wsmessenger.common.util.WsMessengerConstants;
import space.chensheng.wsmessenger.server.clientmng.ClientInfo;
import space.chensheng.wsmessenger.server.clientmng.ClientRegistry;
import space.chensheng.wsmessenger.server.util.ServerConstants;

@Sharable
public class ClientInfoResolveHandler extends SimpleChannelInboundHandler<HttpObject>{
	private static final Logger logger = LoggerFactory.getLogger(ClientInfoResolveHandler.class);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if (msg instanceof HttpRequest) {
			Channel clientChannel = ctx.channel();
			ClientInfo clientInfo = ClientRegistry.resolveClientInfo(clientChannel);
			
			if (clientInfo == null) {
				HttpRequest request = (HttpRequest) msg;
				HttpHeaders headers = request.headers();
				this.resolveClientId(headers, clientChannel);
				this.resolveClientIp(headers, clientChannel);
				this.resolveClientConnTime(headers, clientChannel);
				
				clientInfo = ClientRegistry.resolveClientInfo(clientChannel);
				if (clientInfo != null) {
					ClientRegistry.getInstance().register(clientInfo.getClientId(), clientChannel);
				}
			}
		}
		
		if (msg instanceof ReferenceCounted) {
			ctx.fireChannelRead(((ReferenceCounted)msg).retain());
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	private void resolveClientId(HttpHeaders headers, Channel clientChannel) {
		String clientId = headers.get(WsMessengerConstants.CUSTOM_HEADER_CLIENT_ID);
		
		if (StringUtil.isNotEmpty(clientId)) {
			clientChannel.attr(ServerConstants.ATTR_CLIENT_ID).set(clientId);
			logger.info("Success to resolve client {} whose id is {}", clientChannel, clientId);
		} else {
			logger.error("Fail to resolve client {} whose id is {}", clientChannel, clientId);
		}
	}
	
	private void resolveClientIp(HttpHeaders headers, Channel clientChannel) {
		String clientIp = null;
		
		clientIp = headers.get("X-Forwarded-For");
		
		if(StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {      
			clientIp = headers.get("Proxy-Client-IP");      
		}    
		
		if(StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = headers.get("WL-Proxy-Client-IP");
		}
		
		if(StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
			InetSocketAddress insocket = (InetSocketAddress) clientChannel.remoteAddress();
			clientIp = insocket.getAddress().getHostAddress();
		}
		
		if (StringUtil.isNotEmpty(clientIp) && !"unknown".equalsIgnoreCase(clientIp)) {
			clientChannel.attr(ServerConstants.ATTR_CLIENT_IP).set(clientIp);
			logger.info("Success to resolve client {} whose ip is {}", clientChannel, clientIp);
		} else {
			logger.error("Fail to resolve client {} whose ip is {}", clientChannel, clientIp);
		}
	}
	
	private void resolveClientConnTime(HttpHeaders header, Channel clientChannel) {
		long connTime = System.currentTimeMillis();
		clientChannel.attr(ServerConstants.ATTR_CLIENT_CONN_TIME).set(connTime);
	}
}
