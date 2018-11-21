package jxsource.net.httpproxy.socket;

import jxsource.net.httpproxy.Config;

public class SocketManagerHolder {
	private static final ThreadLocal<SocketManager> contextHolder = 
			new ThreadLocal<SocketManager>() {
        @Override protected SocketManager initialValue() {
            try {
				return Config.getInstance().createSocketManager();
			} catch (Exception e) {
				System.err.println("SocketManagerHolder: Fail to create SocketManager.");
				e.printStackTrace();
				return null;
			}
        }
	};
		
	public static SocketManager get() {
		return contextHolder.get();
	}

}
