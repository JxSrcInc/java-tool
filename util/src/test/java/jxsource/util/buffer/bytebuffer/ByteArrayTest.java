package jxsource.util.buffer.bytebuffer;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import jxsource.util.buffer.bytebuffer.ByteArray;

public class ByteArrayTest {
	byte[] src = "1234567890abcdefgABCDEFG".getBytes();
	ByteArray byteArray;
	
	@Before
	public void init() {
		byteArray = new ByteArray();
		byteArray.append(src);
	}
	@Test
	public void removeTest() throws IOException {
		byte[] removed = byteArray.remove(10, 7);
		assertTrue(ByteArray.equal(removed, "abcdefg".getBytes()));
		assertTrue(byteArray.equals("1234567890ABCDEFG".getBytes()));
		removed = byteArray.remove(10);
		assertTrue(ByteArray.equal(removed, "1234567890".getBytes()));
		assertTrue(byteArray.equals("ABCDEFG".getBytes()));
		removed = byteArray.remove(byteArray.length());
		assertTrue(byteArray.length() == 0);
	}
	@Test
	public void removeAdditionalTest() throws IOException {
		src = "12345678901234567890abc".getBytes();
		init();
		byte[] removed = byteArray.remove(10);
		assertTrue(ByteArray.equal(removed, "1234567890".getBytes()));
		removed = byteArray.remove(10);
		assertTrue(ByteArray.equal(removed, "1234567890".getBytes()));
		removed = byteArray.remove(10);
		assertTrue(ByteArray.equal(removed, "abc".getBytes()));
	}
	@Test
	public void fillinTest() throws IOException {
		byte[] dst = new byte[10];
		assertTrue(byteArray.fillin(dst)==10);
		assertTrue(ByteArray.equal(dst, "1234567890".getBytes()));
		dst = new byte[200];
		assertTrue(byteArray.fillin(dst)==src.length);
		byte[] tmp = new byte[src.length];
		System.arraycopy(dst, 0, tmp, 0, tmp.length);
		assertTrue(ByteArray.equal(tmp, src));
	}
	@Test
	public void indexOfTest() throws IOException {
		src = "1234567890#@1234567890abc".getBytes();
		init();
		byte[] param = "#@".getBytes();
		int i = byteArray.indexOf(param);
		assertTrue(i == 10);
		byte[] data = byteArray.remove(i+param.length);
		assertTrue(ByteArray.equal(data, "1234567890#@".getBytes()));
	}
/*	@Test
	public void indexOfWhiteSpaceTest() throws IOException {
		src = "1234567890  1234567890\t\n\r   1234567890".getBytes();
		init();
		WhiteSpace sp = new WhiteSpace();
		int i = byteArray.indexOf(sp);
		assertTrue(i == 10);
		assertTrue(sp.getLength() == 2);
		byte[] data = byteArray.remove(i);
		assertTrue(ByteArray.equal(data, "1234567890".getBytes()));
		byteArray.remove(sp.getLength());
		
		i = byteArray.indexOf(sp);
		assertTrue(i == 10);
		assertTrue(sp.getLength() == 6);
		data = byteArray.remove(i);
		assertTrue(ByteArray.equal(data, "1234567890".getBytes()));
		byteArray.remove(sp.getLength());

		i = byteArray.indexOf(sp);
		assertTrue(i == -1);
		data = byteArray.getArray();
		assertTrue(ByteArray.equal(data, "1234567890".getBytes()));
		byteArray.remove(sp.getLength());
	}

	@Test
	public void indexOfWhiteSpaceEndTest() throws IOException {
		src = "1234567890\n ".getBytes();
		init();
		WhiteSpace sp = new WhiteSpace();
		int i = byteArray.indexOf(sp);
		assertTrue(i == 10);
		assertTrue(sp.getLength() == 2);
		byte[] data = byteArray.remove(i);
		assertTrue(ByteArray.equal(data, "1234567890".getBytes()));
		byteArray.remove(sp.getLength());
	}

	@Test
	public void indexOfWhiteSpaceStartTest() throws IOException {
		src = "  1234567890".getBytes();
		init();
		WhiteSpace sp = new WhiteSpace();
		int i = byteArray.indexOf(sp);
		assertTrue(i == 0);
		byteArray.remove(sp.getLength());
		byte[] data = byteArray.getArray();
		assertTrue(ByteArray.equal(data, "1234567890".getBytes()));
	}
*/	

	@Test
	public void insertTest() throws IOException {
		ByteArray ba = new ByteArray();
		ba.append("1234567890".getBytes());
		ba.insert(5, "ABCDE".getBytes());
		String src = ba.toString();
		assertTrue(src.equals("12345ABCDE67890"));
	}
	@Test
	public void insertEndTest() throws IOException {
		ByteArray ba = new ByteArray();
		ba.append("1234567890".getBytes());
		ba.insert(10, "ABCDE".getBytes());
		String src = ba.toString();
		assertTrue(src.equals("1234567890ABCDE"));
	}
	@Test
	public void insertExceptionTest() {
		ByteArray ba = new ByteArray();
		ba.append("1234567890".getBytes());
		try {
			ba.insert(11, "ABCDE".getBytes());
			assertTrue(false);
		} catch(Exception e) {
			assert(e.getMessage().equals("Insert index is out of ByteArray limit."));
		}
	}
	@Test
	public void replaceFirstTest() throws IOException {
		ByteArray ba = new ByteArray();
		ba.append("1234567890".getBytes());
		ba.replaceFirst("12345".getBytes(), "ABCDE".getBytes());
		String src = ba.toString();
		assertTrue(src.equals("ABCDE67890"));
	}

	@Test
	public void replaceAllTest() throws IOException {
		ByteArray ba = new ByteArray();
		ba.append("12345678901234567890".getBytes());
		ba.replaceAll("67890".getBytes(), "ABCDE".getBytes());
		String src = ba.toString();
		assertTrue(src.equals("12345ABCDE12345ABCDE"));
	}
	@Test
	public void replaceSecondTest() throws IOException {
		ByteArray ba = new ByteArray();
		ba.append("12345678901234567890".getBytes());
		ba.replace("67890".getBytes(), "ABCDE".getBytes(), 12);
		String src = ba.toString();
		assertTrue(src.equals("123456789012345ABCDE"));
	}
	@Test 
	public void startIndexOfTest() {
		ByteArray ba = new ByteArray();
		ba.append("12345678901234567890".getBytes());
		int i = ba.indexOf("123".getBytes(), 7);
		assertTrue(i == 10);
	}
	@Test
	public void replaceTest() {
		ByteArray ba = new ByteArray();
		ba.append("12345678901234567890".getBytes());
		ba.replace("ABCD".getBytes(), 3, 10);
		assertTrue(ba.equals("123ABCD4567890".getBytes()));
	}
}
