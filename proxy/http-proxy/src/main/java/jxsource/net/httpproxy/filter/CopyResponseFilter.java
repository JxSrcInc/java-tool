package jxsource.net.httpproxy.filter;

import jxsource.net.httpproxy.RequestInfo;
import jxsource.net.httpproxy.RequestInfoFilter;
import jxsource.net.httpproxy.ResponseInfo;
import jxsource.net.httpproxy.ResponseInfoFilter;

public class CopyResponseFilter implements ResponseInfoFilter{

	public ResponseInfo filter(ResponseInfo responseInfo) {
		return responseInfo;
	}

}
