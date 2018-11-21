package jxsource.net.httpproxy;

import java.net.Socket;
import java.util.List;

import jxsource.net.httpproxy.socket.SocketAccessor;
import jxsource.net.httpproxy.socket.SocketAccessorManager;
import jxsource.net.httpproxy.trace.Log;
import jxsource.net.httpproxy.trace.TransferTrace;

public interface ProxyTransferProcessor {
	public final String WaitClientCall = "WaitClientCall";
	public final String ConnectRemoteServer = "ConnectRemoteServer";
	public final String WaitRemoteResponse = "WaitRemoteResponse";
	public final String ProcessEntity = "ProcessEntity";
	
	public void proc(Socket socket);
	public void reset();
	
	public Socket getRemoteServerSocket();
	public Socket getClientSocket();
	public SocketAccessorManager getSocketAccessorManager();
	public SocketAccessor getSocketAccessor();
	public RequestInfo getReceivedRequestInfo();
	public RequestInfo getToSendRequestInfo();
	public ResponseInfo getReceivedResponseInfo();
	public ResponseInfo getToSendResponseInfo();
	public TransferTrace getTrace();
	public boolean isFlagRemoteSocketError();
	public boolean isFlagStopTransactionLoop();
	public String getState();
}
