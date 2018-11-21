package jxsource.net.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import jxsource.net.proxy.exception.ConnectStopException;
import jxsource.net.proxy.exception.LocalRequestException;
import jxsource.net.proxy.exception.RemoteConnectionException;
import jxsource.net.proxy.http.HttpExecutor;
import jxsource.net.proxy.http.exception.MessageHeaderException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

/*
 * BridgeServer must pass Local socket channel to Executor
 * and then pass the executor to WorkThread before starting work thread.
 */
public class WorkThread extends Thread {
	public static final String Local = "Local";
	public static final String Remote = "Remote";
	private Utils utils = new Utils();
	private Logger logger = Logger.getLogger(WorkThread.class);
	private Transaction executor;
	private int transactionCount = 0;
	private Selector selector;
//	private SocketChannel remoteChannel;
	private SocketChannel localChannel;

	// BridgeServer call this method to set Executor before start work thread
	public void setExecutor(Transaction executor) {
		this.executor = executor;
	}

	@Override
	public synchronized void start() {
		try {
			if (executor == null) {
				logger.error("executor is null.");
				return;
			} else {
				createSelector();
			}
			super.start();
		} catch (IOException e) {
			logger.error("Cannot start WorkThread", e);
		}
	}
	
	private String prefix() {
		return Thread.currentThread().getName()+" [WorkerThread] - ";
	}

	public void run() {
		boolean ready = true;
		logger.info("start transaction "
				+ (transactionCount++));
		while (ready) {
			try {
				int num = selector.select(Constants.SelectorWaitTime);
				if (num == 0) {
					String state = executor.getState();
					logger.warn("Selector time out - No request from local client. Stop thread. Executor = "+state);
					if(state.equals(Transaction.E_Wait)) {
						ready = false;
					} 
				}
				Iterator<SelectionKey> selectionKeys = selector.selectedKeys()
						.iterator();
				while (selectionKeys.hasNext()) {
					SelectionKey key = selectionKeys.next();
					selectionKeys.remove();
					if (key.isReadable()) {
						if (Local.equals(key.attachment())) {
							if(executor.getState().equals(Transaction.E_Wait)) {
								SocketChannel channel = (SocketChannel) key
									.channel();
								if (channel != localChannel) {
									// safety check
									logger.warn("Different local channels");
								}
								String executorState = executor.procLocalRequest();
								logger.debug("Local Read: Executor = "+executorState);
								if(executorState.equals(Transaction.E_LocalRequestRead)) {
									SocketChannel remoteChannel = executor.getRemoteChannel();
									try {
										if (remoteChannel.isConnected()) {
											logger.debug("remote connected");
											SelectionKey remoteKey = remoteChannel
												.register(selector,
														SelectionKey.OP_WRITE);
											if (remoteKey.attachment() == null) {
												remoteKey.attach(Remote);
											}
										} else {
											logger.debug("remote not connected");
											SelectionKey remoteKey = remoteChannel
												.register(selector,
														SelectionKey.OP_CONNECT);
											remoteKey.attach(Remote);
											UrlInfo urlInfo = executor
												.getConnectToUrlInfo();
											InetSocketAddress isa = new InetSocketAddress(
												urlInfo.getHostName(),
												urlInfo.getPort());
											remoteChannel.connect(isa);
										}
									} catch (Exception e) {
										logger.warn(
											"Error when prepair remote channel for "
													+ executor
															.getConnectToUrlInfo()
															.getUri(), e);
										if(!reconnect()) {
											ready = false;
										}
									}
								} else 
								if(executorState.equals(Transaction.E_Error)) {
									// executor fails on procLocalRequest() method and is at E_Error state
									// known reason: Local client closes connection -> Thread must close
									// 		because the event is not consumed and will be in SelectedKeys set again.
									// unknown reason?
									if(!(executor.getCatchedException() instanceof MessageHeaderException)) {
										logger.warn("proLocalRequest() Error.",executor.getCatchedException());
									}
									// close thread
									ready = false;
								} else {
									// safety check
									System.err.println(prefix()+"Invalid Executor state "+executor.getState());
									System.exit(1);
								}
							} else {
								// executor is not at E_Wait 
								// one possibility is remote channel is not ready for connection
								// but local read event still happens.
								// not clear why !!!
								// TODO: clean local channel? 			
//								System.err.println(prefix()+"Unexpected local channel read. Executor.state="+executor.getState());
//Thread.sleep(100);
//								ready = false;
			//					System.exit(1);
							}
						} else if (Remote.equals(key.attachment())) {
							// remote Read
							String executorState = executor.procRemoteResponse();
							logger.debug("Remote Read: Executor = "+executorState);
							if(executorState.equals(Transaction.E_TransComplete)) {
								executor.reset();
								// unregister remote channel.
								// the remote channel for next request must
								// re-register
								// because next request may have different
								// remote host.
								// TODO: should keep selector or create new onw?
								// Issue: To keep existing selector, key must be cleaned. But how?
								createSelector();
//								SelectionKey localkey = localChannel.register(
//										selector, SelectionKey.OP_READ);
//								localkey.attach(Local);
//								remoteChannel = null;
							} else 
							if(executorState.equals(Transaction.E_RemoteResponseRead)) {
								// executor get remote server response
								// but fail to complete transaction
								// it is possible that part of response data has been sent to local client
								// TODO: simple solution - close thread to force ending transaction
								ready = false;
							} else {
								// executor is at E_RemoteRequestWritten state
								// TODO: reconnect?
								System.err.println(prefix()+"Unexpected Local Read.\n"+executor.getMessage());
/*							if(!reconnect()) {
									ready = false;
								}
*/							}
						} // end Remote read
					} else if (key.isWritable()) {
						if (Remote.equals(key.attachment())) {
							
							String state = executor.procRemoteRequest();
							logger.debug("Remote Write: Executor = "+state);

							if(state.equals(Transaction.E_RemoteRequestWritten)) {
								SelectionKey remoteKey = executor.getRemoteChannel() //remoteChannel
										.register(selector,
												SelectionKey.OP_READ);
								remoteKey.attach(Remote);
							} else 
							if(state.equals(Transaction.E_RemoteChannelError)) {
								logger.error("RemoteConnection Error", executor.getCatchedException());
								if(!reconnect()) {
									ready = false;
								}
							} else {
								// Unexpected
								// TODO ??
								System.err.println(prefix()+"Unexpected procRemoteRequest() return: "+state);
								System.exit(1);
							}
							// end Remote write
						} else {
							// Not expected Local write
							System.err.println(prefix()+"Unexpected Local write event");
							System.exit(1);
						}
					} else if (key.isConnectable()) {
						if (Remote.equals(key.attachment())) {
							logger.debug("Remote Connectable: Executor = "+executor.getState());
							if(!executor.getState().equals(Transaction.E_LocalRequestRead)) {
								// safety check
								System.err.println(prefix()+"executor is not at state "+Transaction.E_LocalRequestRead);
								System.exit(1);								
							}
							SocketChannel remoteChannel = executor.getRemoteChannel();
							if (remoteChannel.isConnectionPending()) {
								try {
									logger.debug("remote connected="
											+ remoteChannel.finishConnect());
								} catch (Exception e) {
									logger.warn("Remote connection fails. "+executor.getMessage(), e);
									if(!reconnect()) {
										ready = false;
									}
								}
								SelectionKey remoteKey = remoteChannel
										.register(selector,
												SelectionKey.OP_WRITE);
								remoteKey.attach(Remote);
							} // else - connected. do nothing
						} else {
							// safety check
							System.err.println(prefix()+"Unexpected Local connectable");
							System.exit(1);
						}
					} else {
						// safety check
						System.err.println("Not-Read-Write-Connectable key "+key+", Executor = "+executor.getState());
						System.exit(1);
					}

				} // end while
				
			} catch (Exception e) {
				// Selector Exception
				logger.error("Selector error.", e);
				ready = false;
			}
		}
		stopThread();
	}

	private boolean reconnect() {
		logger.info("Reconnect "+executor.getConnectToUrlInfo());
		try {
			// release remote channel
			executor.prepairReconnect();
			createSelector();
			// executor must keep necessary request information 
			// for connecting to remote channel
			SocketChannel remoteChannel = executor.getRemoteChannel();
			if (remoteChannel.isConnected() || remoteChannel.isConnectionPending()) {
				System.err.println(prefix()+"executor/remoteChannel not ready for re-connect");
				return false;//System.exit(1);
			} else {
				logger.debug("not re-connected " + remoteChannel);
				SelectionKey remoteKey = remoteChannel.register(selector,
						SelectionKey.OP_CONNECT);
				remoteKey.attach(Remote);
				UrlInfo urlInfo = executor.getConnectToUrlInfo();
				InetSocketAddress isa = new InetSocketAddress(
						urlInfo.getHostName(), urlInfo.getPort());
				remoteChannel.connect(isa);
				return true;
			}
		} catch (Exception e) {
			logger.error("Reconnection fails.", e);
			return false;
		}

	}

	private void stopThread() {
		executor.destroy();
		utils.closeSocketChannel(localChannel);
		executor = null;
		logger.info("Stop");
	}

	private void createSelector() throws IOException {
		selector = Selector.open();
		// BridgeServer must pass LocalChannel to Executor
		localChannel = executor.getLocalChannel();
		SelectionKey key = localChannel.register(selector,
				SelectionKey.OP_READ);
		key.attach(Local);

	}
}
