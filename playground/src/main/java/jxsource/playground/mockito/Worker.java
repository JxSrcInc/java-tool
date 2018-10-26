package jxsource.playground.mockito;

public class Worker {
	public void doFilter(FilterChain chain, Request request, Response response) {
		if(request != null) {
			chain.doFilter(response);
		} else {
			chain.doFilter(new Response());
		}
	}
}
