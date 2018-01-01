package jxsource.util.buffer.bytebuffer;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jxsource.util.buffer.bytebuffer.ByteArray;
import jxsource.util.buffer.bytebuffer.BufferOutputStreamDst;
import jxsource.util.buffer.bytebuffer.BufferOutputStreamFilter;

import org.junit.Before;
import org.junit.Test;

public class BufferOutputStreamFilterTest {

	ByteArrayOutputStream out;
	byte[] src = "12345678901234567890".getBytes();
	byte[] find = "234".getBytes();
	byte[] replace = "ABCD".getBytes();
	BufferOutputStreamDst obos;
	
	@Before
	public void init() {
		out = new ByteArrayOutputStream();
		obos = new BufferOutputStreamDst(out);
	}
	@Test
	public void removeOneWriteTest() throws IOException {
		BufferOutputStreamFilter modifier = 
				new BufferOutputStreamFilter(obos,find);
		modifier.write(src);
		modifier.close();
		assertTrue(ByteArray.equal(out.toByteArray(), "15678901567890".getBytes()));
	}
	@Test
	public void removeTwoWriteTest() throws IOException {
		BufferOutputStreamFilter modifier = 
				new BufferOutputStreamFilter(obos,find);
		modifier.write(src);
		modifier.write("1234567890123567890".getBytes());
		modifier.close();
		assertTrue(ByteArray.equal(out.toByteArray(), "156789015678901567890123567890".getBytes()));
	}
	@Test
	public void modifyOneWriteTest() throws IOException {
		BufferOutputStreamFilter modifier = 
				new BufferOutputStreamFilter(obos,find,replace);
		modifier.write(src);
		modifier.close();
		assertTrue(ByteArray.equal(out.toByteArray(), "1ABCD5678901ABCD567890".getBytes()));
	}
	@Test
	public void modifyTwoWriteTest() throws IOException {
		BufferOutputStreamFilter modifier = 
				new BufferOutputStreamFilter(obos,find,replace);
		modifier.write(src);
		modifier.write("1234567890123567890".getBytes());
		modifier.close();
//		System.out.println(new String(out.toByteArray()));
		assertTrue(ByteArray.equal(out.toByteArray(), "1ABCD5678901ABCD5678901ABCD567890123567890".getBytes()));
	}
	@Test
	public void changeFindTest() throws IOException {
		BufferOutputStreamFilter modifier = 
				new BufferOutputStreamFilter(obos,find,replace);
		modifier.write(src);
		modifier.setFind("789".getBytes());
		modifier.write(src);
		modifier.close();
		assertTrue(ByteArray.equal(out.toByteArray(), "1ABCD5678901ABCD567890123456ABCD0123456ABCD0".getBytes()));
	}
	@Test
	public void changeReplaceTest() throws IOException {
		BufferOutputStreamFilter modifier = 
				new BufferOutputStreamFilter(obos,find,replace);
		modifier.write(src);
		modifier.setReplace("ZYXWV".getBytes());
		modifier.write(src);
		modifier.close();
		assertTrue(ByteArray.equal(out.toByteArray(), "1ABCD5678901ABCD5678901ZYXWV5678901ZYXWV567890".getBytes()));
	}
	@Test
	public void changeBothTest() throws IOException {
		BufferOutputStreamFilter modifier = 
				new BufferOutputStreamFilter(obos,find,replace);
		modifier.write(src);
		modifier.setFind("1234567".getBytes());
		modifier.setReplace("ZYXWV".getBytes());
		modifier.write(src);
		modifier.close();
		assertTrue(ByteArray.equal(out.toByteArray(), "1ABCD5678901ABCD567890ZYXWV890ZYXWV890".getBytes()));
	}
	@Test
	public void trickyFindChangeTest() throws IOException {
		BufferOutputStreamFilter modifier = 
				new BufferOutputStreamFilter(obos,find,replace);
		modifier.write(src);
		modifier.setFind("012".getBytes());
		modifier.setReplace("ZYXWV".getBytes());
		modifier.write(src);
		modifier.close();
//		System.out.println(new String(out.toByteArray()));
		assertTrue(ByteArray.equal(out.toByteArray(), "1ABCD5678901ABCD56789ZYXWV3456789ZYXWV34567890".getBytes()));
	}
}
