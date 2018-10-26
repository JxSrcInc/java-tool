package jxsource.playground.mockito;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

public class WorkerTest {
	@Test
	public void test() {
		FilterChain chain = mock(FilterChain.class);
		Response response = new Response();
		Request request = null;//new Request() {};
		new Worker().doFilter(chain, request, response);
		verify(chain).doFilter(argThat(new ResponseMatcher(response)));
	}
}
