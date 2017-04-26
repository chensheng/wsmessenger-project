package space.chensheng.wsmessenger.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.GenericFutureListener;
import space.chensheng.wsmessenger.server.NettyServer;
import space.chensheng.wsmessenger.server.clientmng.ClientInfo;
import space.chensheng.wsmessenger.server.clientmng.ClientRegistry;

@Sharable
public class HandshakeCompletedHandler extends ChannelInboundHandlerAdapter{
	private NettyServer server;
	
	private ClientCloseListener clientCloseListener;
	
	public HandshakeCompletedHandler(NettyServer server) {
		this.server = server;
		clientCloseListener = new ClientCloseListener(server);
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
			Channel clientChannel = ctx.channel();
			clientChannel.closeFuture().addListener(clientCloseListener);
			
			ClientInfo clientInfo = ClientRegistry.resolveClientInfo(clientChannel);
			if (clientInfo != null) {
				server.getTaskExecutor().executeTask(new Runnable() {

					@Override
					public void run() {
						server.onClientConnect(clientInfo);
					}
					
				});
			}
		}
		super.userEventTriggered(ctx, evt);
	}

	private static class ClientCloseListener implements GenericFutureListener<ChannelFuture> {
		private NettyServer server;
		
		ClientCloseListener(NettyServer server) {
			this.server = server;
		}

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			ClientInfo clientInfo = ClientRegistry.resolveClientInfo(future.channel());
			if (future.isSuccess() && clientInfo != null) {
				server.getTaskExecutor().executeTask(new Runnable() {

					@Override
					public void run() {
						server.onClientDisconnect(clientInfo);
					}
					
				});
			}
		}
		
	}
}
