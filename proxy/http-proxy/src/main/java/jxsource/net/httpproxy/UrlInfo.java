package jxsource.net.httpproxy;

public class UrlInfo {

	// uri = <protocol>://<hostname.[:<port>][<path>]
	private String protocol;
	private String hostname;
	private int port = 0;
	private String host; // <hostname>[:<port>]
	private String path; // exclude query and fragment
	
	private UrlInfo() {}

	public UrlInfo clone() {
		UrlInfo urlInfo = new UrlInfo();
		urlInfo.setHostname(hostname);
		urlInfo.setProtocol(protocol);
		urlInfo.setPort(port);
		urlInfo.setPath(path);
		return urlInfo;
	}
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setHost(String host) {
		int index = host.indexOf(':');
		if(index != -1) {
			hostname = host.substring(0, index);
			port = Integer.parseInt(host.substring(index+1));
		} else {
			hostname = host;
//			port = 80;
		}
	}
	public UrlInfo(String uri) {

		int i = uri.indexOf("://");
		if (i == 4 || i == 5 ) {
			// absolute URI
			protocol = uri.substring(0, i);
			// remove protocol
			uri = uri.substring(i + 3);
			host = uri;
			i = uri.indexOf('/');
			if (i > 0) {
				// truncate path
				host = uri.substring(0, i);
				path = uri.substring(i);
			} else {
				path = "/";
			}

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
					throw new RuntimeException("No default port for protocal: "+protocol);
				}
			}
			host = hostname+':'+port;
		} else {
			path = uri;
		}
	}

	public String getUri() {
		return toString();
	}

	public String getProtocol() {
//		if(protocol == null) {
//			protocol = "http";
//		}
		return protocol;
	}

	public String getHostName() {
		if(hostname == null) {
			throw new RuntimeException("UrlInfo Error: HostName is null.");
		}
		return hostname;
	}

	public int getPort() {
		if(port == 0) {
			if(getProtocol() == "http") {
				port = 80;
			} else {
				port = 443;
			}
		}
		return port;
	}

	// host = hostname:port
	public String getHost() {
		if(host == null) {
			host = getHostname()+':'+getPort();
		}
		return host;
	}

	public String getPath() {
		return path;
	}

	public String toString() {
		String s = getProtocol() + "://" + hostname;
		if("http".equals(getProtocol()) && port != 80 && port != 0) {
			s += ":" + getPort();
		} else
		if("https".equals(getProtocol()) && port != 443 && port != 0) {
			s += ":" + getPort();
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
