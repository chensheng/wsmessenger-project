package space.chensheng.wsmessenger.server;

import space.chensheng.wsmessenger.server.clientmng.ClientValidator;
import space.chensheng.wsmessenger.server.component.ServerContext;
import space.chensheng.wsmessenger.server.component.ServerContextCustomizer;
import space.chensheng.wsmessenger.server.listener.ResponseMessageListener;
import space.chensheng.wsmessenger.server.listener.ServerLifecycleListener;
import space.chensheng.wsmessenger.server.listener.ServerMessageListener;
import space.chensheng.wsmessenger.server.listener.SystemLifecycleListener;

import java.util.LinkedList;
import java.util.List;

/**
 * A tool to create {@link WsMessengerServer}.
 * @author sheng.chen
 */
public class ServerBootstrap {
	private List<ServerMessageListener<?>> msgListeners;
	
	private List<ServerLifecycleListener> lifecycleListeners;

	private ServerContextCustomizer serverContextCustomizer;

	private ClientValidator clientValidator;
	
	public ServerBootstrap() {
		msgListeners = new LinkedList<ServerMessageListener<?>>();
		lifecycleListeners = new LinkedList<ServerLifecycleListener>();
		
		this.initSystemListeners();
	}
	
	/**
	 * Build a {@link WsMessengerServer}
	 * @return
	 */
	public WsMessengerServer build() {
	    ServerContext serverContext = new ServerContext();
	    if (serverContextCustomizer != null) {
	        serverContextCustomizer.customize(serverContext);
        }
		WsMessengerServer server = new WsMessengerServer(serverContext);
		server.addMessageListeners(msgListeners);
		server.addLifecycleListeners(lifecycleListeners);
		server.setClientValidator(clientValidator);
		return server;
	}
	
	/**
	 * Add a listener to listen specify message. If multiple listeners listen the same message, 
	 * they will be notified in their add order.
	 * @param listener
	 * @return
	 */
	public ServerBootstrap addMessageListener(ServerMessageListener<?> listener) {
		if (listener != null) {
			msgListeners.add(listener);
		}
		return this;
	}
	
	/**
	 * Add listeners to listen messages.If multiple listeners listen the same message, 
	 * they will be notified in their add order.
	 * @param listeners
	 * @return
	 */
	public ServerBootstrap addMessageListeners(List<ServerMessageListener<?>> listeners) {
		if (listeners != null && !listeners.isEmpty()) {
			msgListeners.addAll(listeners);
		}
		return this;
	}
	
	/**
	 * Add a listener to listen server's lifecycle. If multiple listeners have been added, 
	 * they will be notified in their add order.
	 * @param listener
	 * @return
	 */
	public ServerBootstrap addLifecycleListener(ServerLifecycleListener listener) {
		if (listener != null) {
			lifecycleListeners.add(listener);
		}
		return this;
	}
	
	/**
	 * Add listeners to listen server's lifecycle. If multiple listeners have been added,
	 * they will be notified in their add order.
	 * @param listeners
	 * @return
	 */
	public ServerBootstrap addLifecycleListeners(List<ServerLifecycleListener> listeners) {
		if (listeners != null && !listeners.isEmpty()) {
			lifecycleListeners.addAll(listeners);
		}
		return this;
	}

	public ServerBootstrap setServerContextCustomizer(ServerContextCustomizer customizer) {
		this.serverContextCustomizer = customizer;
		return this;
	}

	public ServerBootstrap setClientValidator(ClientValidator clientValidator) {
		this.clientValidator = clientValidator;
		return this;
	}
	
	private void initSystemListeners() {
		this.addMessageListener(new ResponseMessageListener());
		this.addLifecycleListener(new SystemLifecycleListener());
	}
}
