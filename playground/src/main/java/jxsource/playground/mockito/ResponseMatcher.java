package jxsource.playground.mockito;

import org.mockito.ArgumentMatcher;

public 	class ResponseMatcher implements ArgumentMatcher<Response> {
	private Response response;

	public ResponseMatcher(Response response) {
		this.response = response;
	}
	public boolean matches(Response arg0) {
		System.out.println(getClass().getSimpleName()+" this: "+response.getClass().getName());
		System.out.println(getClass().getSimpleName()+" arg0: "+arg0.getClass().getName());
		return response.getClass().getName().equals(arg0.getClass().getName()) ||
				arg0.getClass().getSimpleName().equals("GZIPResponse");
	}
	
}

