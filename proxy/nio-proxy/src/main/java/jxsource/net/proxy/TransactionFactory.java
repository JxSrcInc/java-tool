package jxsource.net.proxy;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

public interface TransactionFactory {

	public String getHost();
	public int getPort();
	public Transaction createExecutor();
	public ServerSocketChannel createServerSocketChannet() throws IOException;

}
