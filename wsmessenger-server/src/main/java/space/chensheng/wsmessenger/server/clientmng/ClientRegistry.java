package space.chensheng.wsmessenger.server.clientmng;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;
import space.chensheng.wsmessenger.common.util.RandomUtil;
import space.chensheng.wsmessenger.server.util.ServerConstants;

public class ClientRegistry {
	private static final Logger logger = LoggerFactory.getLogger(ClientRegistry.class);
	
	private static final ConcurrentHashMap<String, Channel> clients = new ConcurrentHashMap<String, Channel>();
	
	private static final ChannelGroup clientGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
	
	private static class ClientRegistryHolder {
		public static final ClientRegistry clientRegistryInstance = new ClientRegistry();
	}
	
	private ClientRegistry() {
		
	}
	
	public static ClientRegistry getInstance() {
		return ClientRegistryHolder.clientRegistryInstance;
	}
	
	public Channel findClient(String clientId) {
		if (clientId == null) {
			return null;
		}
		return clients.get(clientId);
	}
	
	public boolean register(String clientId, Channel channel) {
		if (clientId != null && channel != null) {
			channel.attr(ServerConstants.ATTR_CLIENT_ID).set(clientId);
			clients.put(clientId, channel);
			clientGroup.add(channel);
			logger.info("success to register client {}", clientId);
			return true;
		}

		return false;
	}
	
	public boolean deregister(Channel channel) {
		if (channel != null) {
			ClientInfo clientInfo = resolveClientInfo(channel);
			if (clientInfo == null) {
				logger.error("fail to deregister client, cause it has not been registered");
				return false;
			}
			return doDeregister(clientInfo.getClientId(), channel);
		}
		return false;
	}
	
	private boolean doDeregister(String clientId, Channel channel) {
		if (clientId != null && channel == clients.get(clientId)) {
			clients.remove(clientId);
			logger.info("success to deregister client {}", clientId);
		    return true;
		}
		logger.info("fail to deregister client {}, it may already reconnect.", clientId);
		return false;
	}
	
	public ChannelGroup getClientGroup() {
		return clientGroup;
	}
	
	public int getClientCount() {
		return clients.size();
	}
	
	public Channel getClient() {
		Collection<Channel> channels = clients.values();
		if(channels != null && channels.size() > 0) {
			int chosenIndex = RandomUtil.nextInt(0, channels.size());
		    for (int i = 0; i <= chosenIndex; i++) {
		    	Channel client = channels.iterator().next();
		    	if (i == chosenIndex) {
		    		return client;
		    	}
		    }
		}
		return null;
	}
	
	/**
	 * 
	 * @param channel
	 * @return null if channel is null or clientId of channel is null.
	 */
	public static ClientInfo resolveClientInfo(Channel channel) {
		if (channel == null) {
			return null;
		}
		
		Long connTime = channel.attr(ServerConstants.ATTR_CLIENT_CONN_TIME).get();
		String clientId = channel.attr(ServerConstants.ATTR_CLIENT_ID).get();
		String clientIp = channel.attr(ServerConstants.ATTR_CLIENT_IP).get();
		
		if (clientId == null) {
			logger.error("fail to resolve client info, cause clientId of channel is null.");
			return null;
		}
		
		ClientInfo result = new ClientInfo();
		result.setClientId(clientId);
		result.setConnTime(connTime != null ? connTime : 0l);
		result.setClientIp(clientIp);
		return result;
	}
}
