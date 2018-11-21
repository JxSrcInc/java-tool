package jxsource.net.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SocketUtils {
	private Logger logger = Logger.getLogger(SocketUtils.class);
	public void closeChannel(Socket socket) {
		logger.info("shutdown: "+socket);
		try {
			socket.close();
		} catch (Exception e) {}					
	}
	public void writeByteArrayToChannel(byte[] data, Socket socket) throws IOException {
		OutputStream out = socket.getOutputStream();
		out.write(data);
		out.flush();;
	}

}
