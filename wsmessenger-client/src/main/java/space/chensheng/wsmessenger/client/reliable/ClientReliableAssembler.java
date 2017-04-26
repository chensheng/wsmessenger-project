package space.chensheng.wsmessenger.client.reliable;

import space.chensheng.wsmessenger.client.component.ClientContext;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.common.reliable.ReliableAssembler;
import space.chensheng.wsmessenger.common.reliable.ReliableMessenger;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.message.sysmsg.ResponseMessage;

public class ClientReliableAssembler extends ReliableAssembler<WsMessage<?>, ResponseMessage, ClientContext> {
	
	public ClientReliableAssembler(ClientContext clientContext, ReliableMessenger<WsMessage<?>, ResponseMessage> messenger, TaskExecutor taskExecutor) {
		super(new ClientWaitingMessageProcessor(clientContext, taskExecutor), new ClientPendingMessageProcessor(clientContext, messenger));
	}
	
}
