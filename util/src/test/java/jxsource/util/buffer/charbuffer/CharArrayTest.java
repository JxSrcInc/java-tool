package jxsource.util.buffer.charbuffer;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class CharArrayTest {
	char[] src = "1234567890abcdefgABCDEFG".toCharArray();
	CharArray charArray;
	
	@Before
	public void init() {
		charArray = new CharArray();
		charArray.append(src);
	}
	@Test
	public void removeTest() throws IOException {
		char[] removed = charArray.remove(10, 7);
		assertTrue(CharArray.equal(removed, "abcdefg".toCharArray()));
		assertTrue(charArray.equals("1234567890ABCDEFG".toCharArray()));
		removed = charArray.remove(10);
		assertTrue(CharArray.equal(removed, "1234567890".toCharArray()));
		assertTrue(charArray.equals("ABCDEFG".toCharArray()));
		removed = charArray.remove(charArray.length());
		assertTrue(charArray.length() == 0);
	}
	@Test
	public void removeAdditionalTest() throws IOException {
		src = "12345678901234567890abc".toCharArray();
		init();
		char[] removed = charArray.remove(10);
		assertTrue(CharArray.equal(removed, "1234567890".toCharArray()));
		removed = charArray.remove(10);
		assertTrue(CharArray.equal(removed, "1234567890".toCharArray()));
		removed = charArray.remove(10);
		assertTrue(CharArray.equal(removed, "abc".toCharArray()));
	}
	@Test
	public void fillinTest() throws IOException {
		char[] dst = new char[10];
		assertTrue(charArray.fillin(dst)==10);
		assertTrue(CharArray.equal(dst, "1234567890".toCharArray()));
		dst = new char[200];
		assertTrue(charArray.fillin(dst)==src.length);
		char[] tmp = new char[src.length];
		System.arraycopy(dst, 0, tmp, 0, tmp.length);
		assertTrue(CharArray.equal(tmp, src));
	}
	@Test
	public void indexOfTest() throws IOException {
		src = "1234567890#@1234567890abc".toCharArray();
		init();
		char[] param = "#@".toCharArray();
		int i = charArray.indexOf(param);
		assertTrue(i == 10);
		char[] data = charArray.remove(i+param.length);
		assertTrue(CharArray.equal(data, "1234567890#@".toCharArray()));
	}
	@Test
	public void insertTest() throws IOException {
		CharArray ba = new CharArray();
		ba.setIncreasesize(4);
		ba.append("1234567890".toCharArray());
		ba.insert(5, "ABCDE".toCharArray());
		String src = ba.toString();
		assertTrue(src.equals("12345ABCDE67890"));
	}
	@Test
	public void insertEndTest() throws IOException {
		CharArray ba = new CharArray();
		ba.append("1234567890".toCharArray());
		ba.insert(10, "ABCDE".toCharArray());
		String src = ba.toString();
		assertTrue(src.equals("1234567890ABCDE"));
	}
	@Test
	public void insertExceptionTest() {
		CharArray ba = new CharArray();
		ba.append("1234567890".toCharArray());
		try {
			ba.insert(11, "ABCDE".toCharArray());
			assertTrue(false);
		} catch(Exception e) {
			assert(e.getMessage().equals("Insert index is out of CharArray limit."));
		}
	}
	@Test
	public void replaceFirstTest() throws IOException {
		CharArray ba = new CharArray();
		ba.append("1234567890".toCharArray());
		ba.replaceFirst("12345".toCharArray(), "ABCDE".toCharArray());
		String src = ba.toString();
		assertTrue(src.equals("ABCDE67890"));
	}

	@Test
	public void replaceAllTest() throws IOException {
		CharArray ba = new CharArray();
		ba.append("12345678901234567890".toCharArray());
		ba.replaceAll("67890".toCharArray(), "ABCDE".toCharArray());
		String src = ba.toString();
		assertTrue(src.equals("12345ABCDE12345ABCDE"));
	}
	@Test
	public void replaceSecondTest() throws IOException {
		CharArray ba = new CharArray();
		ba.append("12345678901234567890".toCharArray());
		ba.replace("67890".toCharArray(), "ABCDE".toCharArray(), 12);
		String src = ba.toString();
		assertTrue(src.equals("123456789012345ABCDE"));
	}
	@Test 
	public void startIndexOfTest() {
		CharArray ba = new CharArray();
		ba.append("12345678901234567890".toCharArray());
		int i = ba.indexOf("123".toCharArray(), 7);
		assertTrue(i == 10);
	}
	@Test
	public void replaceTest() {
		CharArray ba = new CharArray();
		ba.append("12345678901234567890".toCharArray());
		ba.replace("ABCD".toCharArray(), 3, 10);
		assertTrue(ba.equals("123ABCD4567890".toCharArray()));
	}
}
