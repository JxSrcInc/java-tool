package jxsource.net.httpproxy.filter;

import jxsource.net.httpproxy.RequestInfo;
import jxsource.net.httpproxy.RequestInfoFilter;

public class CopyRequestFilter implements RequestInfoFilter{

	public RequestInfo filter(RequestInfo requestInfo) {
		return requestInfo;
	}

}
