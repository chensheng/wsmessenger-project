package space.chensheng.wsmessenger.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.chensheng.wsmessenger.common.component.Messenger;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.message.converter.NettyMessageConverter;

public class WsMessageHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame>{
    private static final Logger logger = LoggerFactory.getLogger(WsMessageHandler.class);
	
    private Messenger<WsMessage> messenger;
    
    private TaskExecutor taskExecutor;
    
    public WsMessageHandler(Messenger<WsMessage> messenger, TaskExecutor taskExecutor) {
    	if (messenger == null) {
			throw new NullPointerException("messenger may not be null");
		}
		if (taskExecutor == null) {
			throw new NullPointerException("taskExecutor may not be null");
		}
		this.messenger = messenger;
		this.taskExecutor = taskExecutor;
    }
    
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
		final WsMessage wsMessage = NettyMessageConverter.fromBinaryWebSocketFrame(msg);
		logger.debug("schedule task to process message {}.", wsMessage);
		taskExecutor.executeTask(new Runnable() {

			@Override
			public void run() {
				messenger.onMessage(wsMessage, null);
			}
			
		});
	}

}
