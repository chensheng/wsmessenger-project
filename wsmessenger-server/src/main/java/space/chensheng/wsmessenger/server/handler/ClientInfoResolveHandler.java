package space.chensheng.wsmessenger.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.chensheng.wsmessenger.common.util.JsonMapper;
import space.chensheng.wsmessenger.common.util.StringUtil;
import space.chensheng.wsmessenger.common.util.WsMessengerConstants;
import space.chensheng.wsmessenger.server.NettyServer;
import space.chensheng.wsmessenger.server.clientmng.ClientInfo;
import space.chensheng.wsmessenger.server.clientmng.ClientRegistry;
import space.chensheng.wsmessenger.server.util.ServerConstants;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Sharable
public class ClientInfoResolveHandler extends SimpleChannelInboundHandler<HttpObject>{
	private static final Logger logger = LoggerFactory.getLogger(ClientInfoResolveHandler.class);

    private NettyServer server;

    public ClientInfoResolveHandler(NettyServer server) {
        this.server = server;
    }
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            tryToRegisterClient(ctx, request);
		}
		
		if (msg instanceof ReferenceCounted) {
			ctx.fireChannelRead(((ReferenceCounted)msg).retain());
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	private void tryToRegisterClient(ChannelHandlerContext ctx, HttpRequest request) {
        Channel clientChannel = ctx.channel();
        ClientInfo clientInfo = this.resolveClientInfo(clientChannel, request);
        if (clientInfo == null) {
            logger.error("Close client whose id is null");
            ctx.close();
            return;
        }
        if (!server.needToValidateClient()) {
            ClientRegistry.getInstance().register(clientInfo, clientChannel);
            return;
        }

        server.getTaskExecutor().executeTask(() -> {
            if (server.validateClient(clientInfo)) {
                ClientRegistry.getInstance().register(clientInfo, clientChannel);
            } else {
                logger.error("Close invalid client {}", JsonMapper.nonEmptyMapper().toJson(clientInfo));
                ctx.close();
            }
        });
    }

	private ClientInfo resolveClientInfo(Channel clientChannel, HttpRequest request) {
        ClientInfo clientInfo = ClientRegistry.resolveClientInfo(clientChannel);
        if (clientInfo != null) {
            return clientInfo;
        }

        HttpHeaders headers = request.headers();
        this.doResolveClientId(headers, clientChannel);
        this.doResolveClientIp(headers, clientChannel);
        this.doResolveClientConnTime(headers, clientChannel);
        this.doResolveClientHeaders(headers, clientChannel);
        return ClientRegistry.resolveClientInfo(clientChannel);
    }

	private void doResolveClientId(HttpHeaders headers, Channel clientChannel) {
		String clientId = headers.get(WsMessengerConstants.CUSTOM_HEADER_CLIENT_ID);
		if (StringUtil.isNotEmpty(clientId)) {
			clientChannel.attr(ServerConstants.ATTR_CLIENT_ID).set(clientId);
		}
	}
	
	private void doResolveClientIp(HttpHeaders headers, Channel clientChannel) {
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
		}
	}
	
	private void doResolveClientConnTime(HttpHeaders headers, Channel clientChannel) {
		long connTime = System.currentTimeMillis();
		clientChannel.attr(ServerConstants.ATTR_CLIENT_CONN_TIME).set(connTime);
	}

	private void doResolveClientHeaders(HttpHeaders headers, Channel clientChannel) {
        Map<String, String> clientHeaders = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : headers.entries()) {
            clientHeaders.put(entry.getKey(), entry.getValue());
        }
        clientChannel.attr(ServerConstants.ATTR_CLIENT_HEADERS).set(clientHeaders);
    }
}
