package jxsource.net.bridge.http;

import org.apache.http.HttpRequest;

/*
 * class contains bridge information
 * format: http://[www.]bridge__[<protocol__host__]<host>[__port__<port>][.com]
 * Note: [www.] and [.com] should appear at the same time
 */
public class BridgeContext {
	public static final String prefixBridge = "bridge__";
	public static final String suffixBridge = "__bridge";
	public static final String symbolHost = "__host__";
	public static final String symbolPort = "__port__";

	// saved bridgeHost
	private String bridgeHost;
	// remote protocol -- either http or https
	private String remoteProtocol;
	// remote host in format host[:port]
	private String remoteHostname;
	private int remotePort;
	private HttpRequest remoteRequest;
	
	public HttpRequest getRemoteRequest() {
		return remoteRequest;
	}

	public void setRemoteRequest(HttpRequest remoteRequest) {
		this.remoteRequest = remoteRequest;
	}

	public String getBridgeHost() {
		return bridgeHost;
	}

	public void setBridgeHost(String bridgeHost) {
		try {
			this.bridgeHost = bridgeHost;
			int start = bridgeHost.indexOf(prefixBridge);
			int end = bridgeHost.indexOf(suffixBridge);
			bridgeHost = bridgeHost.substring(start + prefixBridge.length(),
					end);
			int hostIndex = bridgeHost.indexOf(symbolHost);
			if (hostIndex != -1) {
				// protocol is specified in bridge
				remoteProtocol = bridgeHost.substring(0, hostIndex);
				// remove <protocol>symbolHost from bridgeHost
				bridgeHost = bridgeHost.substring(hostIndex
						+ symbolHost.length());
			} else {
				// default remote protocol is http
				remoteProtocol = "http";
			}
			int portIndex = bridgeHost.indexOf(symbolPort);

			if (portIndex != -1) {
				remotePort = Integer.parseInt(bridgeHost.substring(portIndex
						+ symbolPort.length()));
				remoteHostname = bridgeHost.substring(0, portIndex);
			} else {
				remoteHostname = bridgeHost;
				if (remoteProtocol.equals("http")) {
					remotePort = 80;
				} else if (remoteProtocol.equals("https")) {
					remotePort = 443;
				}
			}
		} catch (Exception e) {
			this.bridgeHost = null;
			remoteProtocol = null;
			remoteHostname = null;
			remotePort = 0;
			if (bridgeHost != null) {
				throw new RuntimeException("Cannot create BridgeContext for "
						+ bridgeHost, e);
			}
		}

	}

	public String getRemoteHostname() {
		return remoteHostname;
	}

	public String getRemoteProtocol() {
		return remoteProtocol;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public String getRemoteHost() {
		if ("http".equals(remoteProtocol)) {
			if (remotePort == 80) {
				return remoteHostname;
			} else {
				return remoteHostname + ":" + Integer.toString(remotePort);
			}
		} else {
			// remoteProtocol is https
			if (remotePort == 443) {
				return remoteHostname;
			} else {
				return remoteHostname + ":" + Integer.toString(remotePort);
			}

		}
	}

	public String getRemoteAuthority() {
		return remoteProtocol + "://" + getRemoteHost();
	}

	@Override
	public String toString() {
		return "BridgeContext [getBridgeHost()=" + getBridgeHost()
				+ ", getRemoteProtocol()=" + getRemoteProtocol()
				+ ", getRemoteHostname()=" + getRemoteHostname()
				+ ", getRemotePort()=" + getRemotePort() + ", getRemoteHost()="
				+ getRemoteHost() + ", getRemoteAuthority()="
				+ getRemoteAuthority() + "]";
	}

}
