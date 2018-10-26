package jxsource.playground.mockito;

import org.mockito.ArgumentMatcher;

public 	class ResponseMatcher implements ArgumentMatcher<Response> {
	private boolean diff;
	private Response response;

	public ResponseMatcher(Response response) {
		this.response = response;
	}
	public boolean matches(Response arg0) {
		System.out.println(arg0);
		System.out.println("this: "+response.getClass().getName());
		System.out.println("arg0: "+arg0.getClass().getName());
		return response.getClass().getName().equals(arg0.getClass().getName());
	}
	
}

