package jxsource.xml.parser;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class TokenTest {

	@Test
	public void test() {
		String s = "<input cC id=iD  D  value='sliekcs=' f />";
		StringBuffer sb = new StringBuffer();
		sb.append(s);
		Token token = new Token(sb);
		String target = new String(token.toArray());
		assertTrue(s.equals(target));
	}

	@Test
	public void testUTF8() {
		try {
			InputStream in = new FileInputStream("src/test/resource/chinese.html");
			byte[] b = new byte[1024*8];
			int k=0;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while((k=in.read(b)) != -1) {
				out.write(b,0,k);
				out.flush();
			}
			out.close();
			in.close();
			byte[] bytes = out.toByteArray();
			String src = new String(bytes,"UTF-8");
			in = new FileInputStream("src/test/resource/chinese.html");
			InputStreamReader r = new InputStreamReader(in, "UTF-8");
			char[] cbuf = new char[1024*8];
			k=0;
			StringBuffer sb = new StringBuffer();
			while((k=r.read(cbuf)) != -1) {
					sb.append(cbuf,0,k);
			}
			in.close();
			Token t = new Token(sb);
			
			assertTrue(t.toString().equals(src));
			assertTrue(new String(t.toArray()).equals(src));
			// if run as mvn install
			assertFalse(new String(t.toArray()).equals(new String(bytes)));
			// if run as test in eclipse
			// assertTrue(new String(t.toArray()).equals(new String(bytes)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
