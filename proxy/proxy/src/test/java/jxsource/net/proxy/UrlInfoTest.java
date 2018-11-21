package jxsource.net.proxy;

import static org.junit.Assert.*;

import org.junit.Test;

public class UrlInfoTest {

	@Test
	public void test() {
		UrlInfo base = new UrlInfo("http://localhost/abd");
		UrlInfo comp = new UrlInfo("http://localhost");
		assertTrue(base.equals(comp));
		comp = new UrlInfo("http://localhost/dis");
		assertTrue(base.equals(comp));
		comp = new UrlInfo("http://localhost:80");
		assertTrue(base.equals(comp));
		comp = new UrlInfo("http://localhost:4");
		assertFalse(base.equals(comp));
		comp = new UrlInfo("http://localhost:4/abd");
		assertFalse(base.equals(comp));
		comp = new UrlInfo("https://localhost/");
		assertFalse(base.equals(comp));
		comp = new UrlInfo("http://other/abd");
		assertFalse(base.equals(comp));
	}
	
	@Test
	public void initTest() {
		try {
			new UrlInfo("/asd");
			new UrlInfo("/");
			assertTrue(true);
		} catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void portTest() {
		UrlInfo base = new UrlInfo("http://localhost:80/abd");
		assertTrue(base.toString().equals("http://localhost/abd"));
		base = new UrlInfo("http://localhost:180/abd");
		assertTrue(base.toString().equals("http://localhost:180/abd"));
		base = new UrlInfo("https://localhost:180/abd");
		assertTrue(base.toString().equals("https://localhost:180/abd"));
		base = new UrlInfo("https://localhost:443/abd");
		assertTrue(base.toString().equals("https://localhost/abd"));
	}

}
