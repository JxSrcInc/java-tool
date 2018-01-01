package jxsource.xml.parser;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

public class FileTest {
	
	public byte[] load(String filename) {
		try {
			InputStream in = new FileInputStream(filename);
			byte[] b = new byte[1024*8];
			int k=0;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while((k=in.read(b)) != -1) {
				out.write(b,0,k);
				out.flush();
			}
			out.close();
			in.close();
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	private void compare(byte[] src, byte[] target) {
		System.out.println(src.length+","+target.length);
		assertTrue(src.length == target.length);
		for(int i=0; i<src.length; i++) {
			assertTrue(src[i] == target[i]);
		}
	}

	@Test
	public void testProfile() {
		try {
			String filename = "src/test/resource/profile.html";
			byte[] src = load(filename);
			InputStream in = new FileInputStream(filename);
			Parser parser = new Parser(in);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Token t = null;
			while((t=parser.getNextToken()) != null) {
				out.write(t.toString().getBytes());
				out.flush();
			}
			out.close();
			compare(src, out.toByteArray());
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testUTF8Byts() {
		try {
			String filename = "src/test/resource/chinese.html";
			String src = new String(load(filename), "UTF-8");
			InputStream in = new FileInputStream(filename);
			Parser parser = new Parser(in, "UTF-8");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Token t = null;
			while((t=parser.getNextToken()) != null) {
				out.write(t.toString().getBytes("UTF-8"));
				out.flush();
			}
			out.close();
			compare(src.getBytes("UTF-8"), out.toByteArray());
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testUTF8String() {
		try {
			String filename = "src/test/resource/chinese.html";
			String src = new String(load(filename), "UTF-8");
			InputStream in = new FileInputStream(filename);
			Parser parser = new Parser(in, "UTF-8");
			Token t = null;
			String target = "";
			while((t=parser.getNextToken()) != null) {
				target += t.toString();
			}
			assertTrue(src.equals(target));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testGBKByts() {
		try {
			String filename = "src/test/resource/gbk.html";
			String src = new String(load(filename), "GBK");
			InputStream in = new FileInputStream(filename);
			Parser parser = new Parser(in, "GBK");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Token t = null;
			while((t=parser.getNextToken()) != null) {
				out.write(t.toString().getBytes("GBK"));
				out.flush();
			}
			out.close();
			compare(src.getBytes("GBK"), out.toByteArray());
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testGBKString() {
		try {
			String filename = "src/test/resource/gbk.html";
			String src = new String(load(filename), "GBK");
			InputStream in = new FileInputStream(filename);
			Parser parser = new Parser(in, "GBK");
			Token t = null;
			String target = "";
			while((t=parser.getNextToken()) != null) {
				target += t.toString();
			}
			assertTrue(src.equals(target));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testScript() {
		try {
			String filename = "src/test/resource/scripttest.html";
			byte[] src = load(filename);
			InputStream in = new FileInputStream(filename);
			Parser parser = new Parser(in);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Token t = null;
			while((t=parser.getNextToken()) != null) {
				out.write(t.toString().getBytes());
				out.flush();
			}
			out.close();
			compare(src, out.toByteArray());
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
