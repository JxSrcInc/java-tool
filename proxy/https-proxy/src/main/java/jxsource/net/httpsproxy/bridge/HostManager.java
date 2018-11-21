package jxsource.net.httpsproxy.bridge;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

public class HostManager {
	private static Logger logger = Logger.getLogger(HostManager.class);
	private static HostManager me;
	private Set<String> hosts = new HashSet<String>();
	private HostManager() {
		String hostFile = System.getProperty("jxsource.net.proxy.https.bridge.hostFile");
		logger.debug("Remote Host File: "+hostFile);
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(hostFile)));
			String line = "";
			while((line=r.readLine()) != null) {
				hosts.add(line);
			}
			r.close();
		} catch(IOException e) {
			logger.error("Failed to load host file: "+hostFile, e);
		}
	}
	public static HostManager getInstance() {
		if(me == null) {
			me = new HostManager();
		}
		return me;
	}
	public void setHost(String host) {
		hosts.add(host);
	}
	public boolean contains(String host) {
		return hosts.contains(host);
	}
}
