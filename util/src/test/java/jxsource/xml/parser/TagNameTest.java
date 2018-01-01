package jxsource.xml.parser;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TagNameTest {
	
		@Test
		public void test1() {
			Tag tag = new Tag(new StringBuffer("<tag>"));
			assertTrue(tag.getName().equals("tag"));
		}
		@Test
		public void test2() {
			Tag tag = new Tag(new StringBuffer("</tag>"));
			assertTrue(tag.getName().equals("/tag"));
		}
		@Test
		public void test3() {
			Tag tag = new Tag(new StringBuffer("<tag/>"));
			assertTrue(tag.getName().equals("tag"));
		}
		@Test
		public void test4() {
			Tag tag = new Tag(new StringBuffer("<tag id='is'>"));
			assertTrue(tag.getName().equals("tag"));
		}
		@Test
		public void test5() {
			Tag tag = new Tag(new StringBuffer("<tag id=\"value\"/>"));
			assertTrue(tag.getName().equals("tag"));
		}
}
