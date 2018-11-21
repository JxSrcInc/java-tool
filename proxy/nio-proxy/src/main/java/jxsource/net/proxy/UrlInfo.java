package jxsource.net.proxy;

import org.apache.log4j.Logger;

public class UrlInfo {
	private static Logger logger = Logger.getLogger(UrlInfo.class);
	// uri = <protocol>://<hostname.[:<port>][<path>]
	private String protocol;
	private String hostname;
	private int port;
	private String host; // <hostname>[:<port>]
	private String path; // exclude query and fragment

	public UrlInfo(String uri) {
		int i = uri.indexOf("://");
		if (i > 0) {
			// absolute URI
			protocol = uri.substring(0, i);
			// remove protocol
			uri = uri.substring(i + 3);
//		} else {
//			throw new RuntimeException("no protocol defined: "+uri);
		}
		host = uri;
		i = uri.indexOf('/');
		if (i > 0) {
			// truncate path
			host = uri.substring(0, i);
			path = uri.substring(i);
		} else {
			path = "/";
			host = null;
		}
		if(host != null) {
		i = host.indexOf(':');
		if (i > 0) {
			this.hostname = host.substring(0, i);
			this.port = Integer.parseInt(host.substring(i + 1));
		} else {
			this.hostname = host;
			if(protocol.toLowerCase().equals("http")) {
				port = 80;
			} else
			if(protocol.toLowerCase().equals("https")) {
				port = 443;
			} else {
				logger.warn("No default port for protocal: "+protocol);
			}
		}
		host = hostname+':'+port;
		}

	}

	public String getUri() {
		return toString();
	}

	public String getProtocol() {
		return protocol;
	}

	public String getHostName() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	// host = hostname:port
	public String getHost() {
		return host;
	}

	public String getPath() {
		return path;
	}

	public String toString() {
		String s = "";
		if (protocol != null) {
			s += protocol + "://" + hostname;
		}
		if (port != 0) {
			s += ":" + port;
		}
		s += path;
		return s;
	}

	/*
	 * hashCode and equals are only based on protocol, hostname and port 
	 * for it can be used as key for ConnectionManager
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + port;
		result = prime * result
				+ ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UrlInfo other = (UrlInfo) obj;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (port != other.port)
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		return true;
	}

}
