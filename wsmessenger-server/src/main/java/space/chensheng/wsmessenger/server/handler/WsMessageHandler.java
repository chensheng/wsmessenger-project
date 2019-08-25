package space.chensheng.wsmessenger.server.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import space.chensheng.wsmessenger.common.component.Messenger;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.message.converter.NettyMessageConverter;

@Sharable
public class WsMessageHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame>{
	private TaskExecutor taskExecutor;
	
	private Messenger<WsMessage> messenger;
	
	public WsMessageHandler(TaskExecutor taskExecutor, Messenger<WsMessage> messenger) {
		if (taskExecutor == null) {
			throw new NullPointerException("taskExecutor may not be null");
		}
		if (messenger == null) {
			throw new NullPointerException("messenger may not be null");
		}
		this.taskExecutor = taskExecutor;
		this.messenger = messenger;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
	    WsMessage a2dMessage = NettyMessageConverter.fromBinaryWebSocketFrame(msg);
		taskExecutor.executeTask(new Runnable() {

			@Override
			public void run() {
				messenger.onMessage(a2dMessage, a2dMessage.getHeader().getSenderId());
			}
			
		});
	}

}
