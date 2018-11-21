package jxsource.net.httpproxy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import jxsource.net.httpproxy.entity.EntityDestinationSocketManager;
import jxsource.net.httpproxy.entity.EntityProcessor;
import jxsource.net.httpproxy.socket.SocketAccessorManager;
import jxsource.net.httpproxy.socket.SocketAccessor;
import jxsource.net.httpproxy.socket.SocketManager;

public class Config {
	
	private final String Plain = "Plain";
	private final String SSL = "SSL";
	private final String ProxyHost = "proxyHost";
	private final String ProxyProt = "proxyPort";
	private final String ProxyServerSocketType = "proxyServerSocketType";
//	private final String RemoteServerSocketType = "remoteServerSocketType";
	private final String ProxyCleintToRemoteServerRequestInfoFilter = "proxyCleintToRemoteServerRequestInfoFilter";
	private final String RemoteServerToProxyCleintResponseInfoFilter = "remoteServerToProxyCleintResponseInfoFilter";
	private final String RemoteServerSocketFactory = "remoteServerSocketFactory";
	private final String RemoteServerSocketManager = "remoteServerSocketManager";
	private final String EntityDestinationSocketManager = "entityDestinationSocketManager";
	private final String EntityProcessor = "entityProcessor";
	private final String SocketAccessorManager = "socketAccessorManager";
	private final String SocketAccessor = "socketAccessor";
	
	private static Config me;
	private String configFile;
	Properties props = new Properties();

	private ServerSocket proxyServerSocket; // for proxy client
	private RequestInfoFilter proxyCleintToRemoteServerRequestInfoFilter;
	private ResponseInfoFilter remoteServerToProxyCleintResponseInfoFilter;
	private String socketFactory; // for remote server connection
	private EntityDestinationSocketManager entityDestinationSocketManager;
	private EntityProcessor entityProcessor;
	private SocketAccessorManager socketAccessorManager;
	
	public static Config getInstance() {
		if(me == null) {
			me = new Config();
			me.init();
		}
		return me;
	}
	
	public void init() {
		configFile = System.getProperty(Constants.defaultConfigFile);
		if(configFile == null) {
			configFile = Constants.defaultConfigFile;
		}
		try {
			props.load(new FileInputStream(configFile));
		} catch(IOException ioe) {
			System.err.println("Failed to load config file: "+configFile);
			ioe.printStackTrace();
			System.exit(1);
		}
		
		// Load all objects in initialization
		// because not many objects and to check existence of all defined classes
		String className = "";
		try {
			className = props.getProperty(ProxyCleintToRemoteServerRequestInfoFilter);
			proxyCleintToRemoteServerRequestInfoFilter = (RequestInfoFilter)this.createObject(className);
			className = props.getProperty(RemoteServerToProxyCleintResponseInfoFilter);
			remoteServerToProxyCleintResponseInfoFilter = (ResponseInfoFilter)this.createObject(className);
			className = props.getProperty(EntityDestinationSocketManager);
			entityDestinationSocketManager = (EntityDestinationSocketManager)this.createObject(className);
			className = props.getProperty(EntityProcessor);
			entityProcessor = (EntityProcessor)this.createObject(className);
			className = props.getProperty(SocketAccessorManager);
			socketAccessorManager = (SocketAccessorManager)this.createObject(className);
			socketFactory = props.getProperty(RemoteServerSocketFactory);
		} catch(Throwable e) {
			System.err.println("Failed to load class: "+className);
			e.printStackTrace();
			System.exit(2);			
		}
	}
	
	public ServerSocket createServerSocket() throws IOException {
		String type = props.getProperty(this.ProxyServerSocketType);
		if(SSL.equals(type)) {
			proxyServerSocket = SSLServerSocketFactory.getDefault().createServerSocket();
		} else {
			proxyServerSocket = ServerSocketFactory.getDefault().createServerSocket();
		}
		InetSocketAddress isa = new InetSocketAddress(
				props.getProperty(ProxyHost), 
				Integer.parseInt(props.getProperty(ProxyProt)));
		proxyServerSocket.bind(isa);
		return proxyServerSocket;
	}
	
	public SocketManager createSocketManager() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String className = props.getProperty(RemoteServerSocketManager);
		SocketManager socketManager = (SocketManager) createObject(className);
		socketManager.setSocketFactory(socketFactory);
		return socketManager;
	}
	
	public ProxyTransferProcessor createProxyClientHandler() {
		return new ProxyTransferProcessorImpl();
	}
	
	public RequestInfoFilter getProxyCleintToRemoteServerRequestInfoFilter() {
		return proxyCleintToRemoteServerRequestInfoFilter;
	}

	public ResponseInfoFilter getRemoteServerToProxyCleintResponseInfoFilter() {
		return remoteServerToProxyCleintResponseInfoFilter;
	}
	
	public String getProperty(String key) {
		return props.getProperty(key);
	}
	
	public EntityDestinationSocketManager getEntityDestinationSocketManager() {
		return entityDestinationSocketManager;
	}
	
	public EntityProcessor getEntityProcessor() {
		return entityProcessor;
	}
	
	public SocketAccessorManager getControllerManager() {
		return socketAccessorManager;
	}
	
	public SocketAccessor createSocketAccessor() {
		String className = props.getProperty(SocketAccessor);
		try {
			return (SocketAccessor)createObject(className);
		} catch (Exception e) {
			throw new RuntimeException("Fail to create SocketAccessor.",e);
		}
	}
	private Object createObject(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> cl = getClass().forName(className);
		return cl.newInstance();
	}
}
