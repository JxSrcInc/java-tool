package jxsource.playground.mockito;

public class Worker {
	public void doFilter(FilterChain chain, Request request, Response response) {
		if(request != null) {
			chain.doFilter(request, response);
		} else {
			chain.doFilter(request, new GZIPResponse());
		}
	}
}
