package jxsource.net.bridge.http;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class RemoteHttpsToBridgeConverterTest {

	RegexMatcher matcher;

	@Before
	public void init() {
		matcher = new RegexMatcher();
	}
	@Test
	public void testSingle() {
		matcher.setReplace(new RemoteHttpsToBridgeConverter());
		String result = matcher.matchAndReplace("https://www.google.com");
		assertTrue(result.equals("bridge__https__host__www.google.com__bridge"));
		result = matcher.matchAndReplace("https://www.google.com:443");
		assertTrue(result.equals("bridge__https__host__www.google.com__bridge"));
		result = matcher.matchAndReplace("https://www.google-s.com:345");
		assertTrue(result.equals("bridge__https__host__www.google-s.com__port__345__bridge"));
	}

	@Test
	public void testComplex() {
		matcher.setReplace(new RemoteHttpsToBridgeConverter());
		String result = matcher.matchAndReplace("xyz https://www.google.com 1234");
		assertTrue(result.equals("xyz bridge__https__host__www.google.com__bridge 1234"));
		result = matcher.matchAndReplace("xyz https://www.google.com 1234 https://www.google-s.com:332 end");
		assertTrue(result.equals("xyz bridge__https__host__www.google.com__bridge 1234 bridge__https__host__www.google-s.com__port__332__bridge end"));
	}

}
