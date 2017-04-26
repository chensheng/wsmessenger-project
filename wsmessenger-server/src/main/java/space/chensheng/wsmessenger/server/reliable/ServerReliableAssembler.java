package space.chensheng.wsmessenger.server.reliable;

import space.chensheng.wsmessenger.common.component.Messenger;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.server.component.ServerContext;
import space.chensheng.wsmessenger.common.reliable.ReliableAssembler;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.message.sysmsg.ResponseMessage;

public class ServerReliableAssembler extends ReliableAssembler<WsMessage<?>, ResponseMessage, ServerContext> {
	
	public ServerReliableAssembler(ServerContext serverContext, Messenger<WsMessage<?>> messenger, TaskExecutor taskExecutor) {
		super(new ServerWaitingMessageProcessor(serverContext, taskExecutor), new ServerPendingMessageProcessor(serverContext, messenger, taskExecutor));
	}
}
