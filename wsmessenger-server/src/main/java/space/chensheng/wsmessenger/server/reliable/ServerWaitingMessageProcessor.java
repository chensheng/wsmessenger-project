package space.chensheng.wsmessenger.server.reliable;

import space.chensheng.wsmessenger.common.executor.TaskExecutor;
import space.chensheng.wsmessenger.common.reliable.WaitingMessageProcessor;
import space.chensheng.wsmessenger.message.component.WsMessage;
import space.chensheng.wsmessenger.message.sysmsg.ResponseMessage;
import space.chensheng.wsmessenger.server.component.ServerContext;

public class ServerWaitingMessageProcessor extends WaitingMessageProcessor<WsMessage, ResponseMessage, ServerContext>{

	ServerWaitingMessageProcessor(ServerContext messengerContext, TaskExecutor taskExecutor) {
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
