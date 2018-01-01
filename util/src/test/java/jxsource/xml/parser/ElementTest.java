package jxsource.xml.parser;

import org.junit.Before;
import org.junit.Test;

import jxsource.xml.parser.Element;
import static org.junit.Assert.*;
public class ElementTest {
	
	TestElement testElement;
	
	class TestElement extends Element {
		public TestElement(StringBuffer sb) {
			super(sb);
		}
		public void initialize() {
			super.initialize();
		}
		public void test() {
			
			System.out.println("01234567890123456789012345678901234567890123456789");
			for(int i=0; i<data.length();i++) {
				System.out.print(data.charAt(i));
			}
			System.out.println();
			
			for(String key: this.attrs.keySet()) {
				Attr attr = attrs.get(key);
				System.out.println(key+"="+attr.toString());
				if(key.equals("f")) {
					assertTrue(attr.toString().equals("Attr: f(37)"));
				} else
				if(key.equals("id")) {
					assertTrue(attr.toString().equals("Attr: id(10)=iD(13)"));
				} else
				if(key.equals("d")) {
					assertTrue(attr.toString().equals("Attr: D(17)"));
				} else
				if(key.equals("value")) {
					assertTrue(attr.toString().equals("Attr: value(20)='sliekcs='(26)"));
				} else
				if(key.equals("cc")) {
					assertTrue(attr.toString().equals("Attr: cC(7)"));
				} 
			}
			
		}
		
		public void testUpdate() {
			
			this.setAttribute("id", "newId");
			this.setAttribute("f", "new Value");
			this.setAttribute("Value", "=abd");
			
			System.out.println("01234567890123456789012345678901234567890123456789");
			for(int i=0; i<data.length();i++) {
				System.out.print(data.charAt(i));
			}
			System.out.println();
			
			for(String key: this.attrs.keySet()) {
				Attr attr = attrs.get(key);
				System.out.println(key+"="+attr.toString());
				if(key.equals("f")) {
					assertTrue(attr.toString().equals("Attr: f(38)=\"new Value\"(40)"));
				} else
				if(key.equals("id")) {
					assertTrue(attr.toString().equals("Attr: id(10)=\"newId\"(13)"));
				} else
				if(key.equals("value")) {
					assertTrue(attr.toString().equals("Attr: value(25)=\"=abd\"(31)"));
				} 
			}
			
		}
		public void testDelete() {
			
			this.deleteAttribute("f");
			this.deleteAttribute("id");
			
			System.out.println("01234567890123456789012345678901234567890123456789");
			String s = data.toString();
			System.out.println(s);
			assertTrue(s.indexOf("f=") < 0);
			assertTrue(s.indexOf("id=") < 0);
			
		}
		public void testInsert() {
			
			this.setAttribute("XXX","xxx");
			this.setAttribute("XYZ",null);
			
			System.out.println("01234567890123456789012345678901234567890123456789");
			String s = data.toString();
			System.out.println(s);
			
		}
	}
	
	@Before
	public void init() {
		String s = "<input cC id=iD  D  value='sliekcs=' f />";
		StringBuffer sb = new StringBuffer();
		sb.append(s);
		testElement = new TestElement(sb);
		testElement.initialize();
		
	}

	@Test
	public void AttrTest() {
		testElement.test();
	}
	@Test
	public void AttrUpdateTest() {
		testElement.testUpdate();
	}
	@Test
	public void AttrDeleteTest() {
		testElement.testDelete();
	}
	@Test
	public void AttrInsertTest() {
		testElement.testInsert();
	}


}
