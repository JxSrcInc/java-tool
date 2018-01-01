package jxsource.util.buffer.charbuffer;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.junit.Before;
import org.junit.Test;

public class BufferWriterFilterTest {

	ByteArrayOutputStream out;
	String src = "12345678901234567890";
	char[] find = "234".toCharArray();
	char[] replace = "ABCD".toCharArray();
	BufferWriterDst obos;
	
	@Before
	public void init() {
		out = new ByteArrayOutputStream();
		obos = new BufferWriterDst(new OutputStreamWriter(out));
	}
	@Test
	public void removeOneWriteTest() throws IOException {
		BufferWriterFilter modifier = 
				new BufferWriterFilter(obos,find);
		modifier.write(src);
		modifier.close();
		assertTrue(out.toString().equals("15678901567890"));
	}
	@Test
	public void removeTwoWriteTest() throws IOException {
		BufferWriterFilter modifier = 
				new BufferWriterFilter(obos,find);
		modifier.write(src);
		modifier.write("1234567890123567890".toCharArray());
		modifier.close();
		assertTrue(out.toString().equals("156789015678901567890123567890"));
	}
	@Test
	public void modifyOneWriteTest() throws IOException {
		BufferWriterFilter modifier = 
				new BufferWriterFilter(obos,find,replace);
		modifier.write(src);
		modifier.close();
		assertTrue(out.toString().equals("1ABCD5678901ABCD567890"));
	}
	@Test
	public void modifyTwoWriteTest() throws IOException {
		BufferWriterFilter modifier = 
				new BufferWriterFilter(obos,find,replace);
		modifier.write(src);
		modifier.write("1234567890123567890".toCharArray());
		modifier.close();
//		System.out.println(new String(out.toByteArray()));
		assertTrue(out.toString().equals("1ABCD5678901ABCD5678901ABCD567890123567890"));
	}
	@Test
	public void changeFindTest() throws IOException {
		BufferWriterFilter modifier = 
				new BufferWriterFilter(obos,find,replace);
		modifier.write(src);
		modifier.setFind("789".toCharArray());
		modifier.write(src);
		modifier.close();
		assertTrue(out.toString().equals("1ABCD5678901ABCD567890123456ABCD0123456ABCD0"));
	}
	@Test
	public void changeReplaceTest() throws IOException {
		BufferWriterFilter modifier = 
				new BufferWriterFilter(obos,find,replace);
		modifier.write(src);
		modifier.setReplace("ZYXWV".toCharArray());
		modifier.write(src);
		modifier.close();
		assertTrue(out.toString().equals("1ABCD5678901ABCD5678901ZYXWV5678901ZYXWV567890"));
	}
	@Test
	public void changeBothTest() throws IOException {
		BufferWriterFilter modifier = 
				new BufferWriterFilter(obos,find,replace);
		modifier.write(src);
		modifier.setFind("1234567".toCharArray());
		modifier.setReplace("ZYXWV".toCharArray());
		modifier.write(src);
		modifier.close();
		assertTrue(out.toString().equals("1ABCD5678901ABCD567890ZYXWV890ZYXWV890"));
	}
	@Test
	public void trickyFindChangeTest() throws IOException {
		BufferWriterFilter modifier = 
				new BufferWriterFilter(obos,find,replace);
		modifier.write(src);
		modifier.setFind("012".toCharArray());
		modifier.setReplace("ZYXWV".toCharArray());
		modifier.write(src);
		modifier.close();
//		System.out.println(new String(out.toByteArray()));
		assertTrue(out.toString().equals("1ABCD5678901ABCD56789ZYXWV3456789ZYXWV34567890"));
	}
}
