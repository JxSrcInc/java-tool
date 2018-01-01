package jxsource.util.io;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileInputStreamFactoryTest {
	File file;
	FileInputStreamFactory factory;
	@Before
	public void init() {
		file = new File("temp");
		System.out.println("Create: "+file.getAbsolutePath());
		factory = new FileInputStreamFactory(file.getPath());
		factory.setLoadNewContent(true);
	}
	@After
	public void end() {
		System.out.println("Delete: "+file.getAbsolutePath());
		file.delete();
	}
	private void add(String msg) throws IOException {
		FileOutputStream out = new FileOutputStream(file, true);
		out.write(msg.getBytes());
		out.flush();
		out.close();
	}
	private String get() throws IOException {
		InputStream in = factory.getFileInputStream();
		byte[] data = FileUtil.loadByteArray(in);
		String msg = new String(data);
		in.close();
		return msg;
	}
	@Test
	public void test() throws IOException {
		add("1234567890");
		assertTrue(factory.getSkipSize()==0);
		assertTrue(get().equals("1234567890"));
		add("ABCDEFGHIJKLMN");
		assertTrue(factory.getSkipSize()==10);
		assertTrue(get().equals("ABCDEFGHIJKLMN"));
	}
}
