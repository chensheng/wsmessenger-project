package space.chensheng.wsmessenger.client.reliable;

import space.chensheng.wsmessenger.client.component.ClientContext;
import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.common.reliable.WaitingMessageProcessor;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.message.sysmsg.ResponseMessage;

public class ClientWaitingMessageProcessor extends WaitingMessageProcessor<WsMessage, ResponseMessage, ClientContext> {

	public ClientWaitingMessageProcessor(ClientContext messengerContext, TaskExecutor taskExecutor) {
		super(messengerContext, taskExecutor);
	}

	@Override
	protected long resolveMessageId(WsMessage message) {
		return message.getHeader().getMessageId();
	}

	@Override
	protected long resolverRespMessageId(ResponseMessage respMsg) {
		return respMsg.getRespMessageId();
	}

	@Override
	protected boolean isRespMessageSuccess(ResponseMessage respMsg) {
		return respMsg.isSuccess();
	}

}
