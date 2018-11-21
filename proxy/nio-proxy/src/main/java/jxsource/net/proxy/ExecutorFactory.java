package jxsource.net.proxy;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

public interface ExecutorFactory {

	public String getHost();
	public int getPort();
	public Executor createExecutor();
	public ServerSocketChannel createServerSocketChannel() throws IOException;

}
