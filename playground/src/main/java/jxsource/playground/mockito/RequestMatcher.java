package jxsource.playground.mockito;

import org.mockito.ArgumentMatcher;

public 	class RequestMatcher implements ArgumentMatcher<Request> {
	public boolean matches(Request arg0) {
		System.out.println(getClass().getSimpleName()+" arg0: "+arg0);
		return true;
	}
	
}

