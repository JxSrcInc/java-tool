package jxsource.net.proxy;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PathTest {

	Path path;
	private final String pathValue = "/context/path1/path2/path3";
	
	@Before
	public void init() {
		path = new Path(pathValue);
	}
		
	@Test
	public void sizeTest() {
		assertTrue(path.length() == 4);
	}
	@Test
	public void nextTest() {
		int count = 0;
		while(path.hasNextSegment()) {
			String s = path.getNextSegment();
			switch(count++) {
			case 0:
				assertTrue(s.equals("context"));
				break;
			case 1:
				assertTrue(s.equals("path1"));
				break;
			case 2:
				assertTrue(s.equals("path2"));
				break;
			case 3:
				assertTrue(s.equals("path3"));
				break;
			default:
				assertTrue("Invalide count.", false);
			}
		}
	}
	@Test
	public void removeTest() {
		String removed = path.remove("context");
		assertTrue(removed.equals("context"));
		assertTrue(path.toString().equals("path1/path2/path3"));
		removed = path.remove("path2");
		assertTrue(removed.equals("path2"));
		assertTrue(path.toString().equals("path1/path3"));
	}
	@Test
	public void removeWithTest() {
		String removed = path.removeStartWith("cont");
		assertTrue(removed.equals("context"));
		assertTrue(path.toString().equals("path1/path2/path3"));
		removed = path.removeStartWith("path");
		assertTrue(removed.equals("path1"));
		assertTrue(path.toString().equals("path2/path3"));
	}
	@Test
	public void rootTest() {
		path = new Path(pathValue,true);
		assertTrue(path.toString().equals(pathValue));
		String removed = path.removeStartWith("cont");
		assertTrue(removed.equals("context"));
		assertTrue(path.toString().equals("/path1/path2/path3"));
		removed = path.remove("path2");
		assertTrue(removed.equals("path2"));
		assertTrue(path.toString().equals("/path1/path3"));
	}

}
