package jxsource.xml.debug;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import jxsource.xml.parser.Element;
import jxsource.xml.parser.Parser;
import jxsource.xml.parser.Token;

public class DebugParser {

	public static void testCustomHtml() {
		try {
			String s = "<html><head><script>as<df><<<<input id=\"abcd\" d  c = 'd' e='ddd' f =sdf /> >>>asdf </asdf>";
			s += "</script></head></html>";
			Parser html = new Parser(s.getBytes());
			Token t = null;
			while ((t = html.getNextToken()) != null) {
				System.out.println(t.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void rawData(InputStream in, String outFilePath) {
		try {
			Parser parser = new Parser(in);
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(outFilePath));
			while (true) {
				Token t = parser.getNextToken();
				if (t == null)
					break;
				out.write(t.toArray());
				out.flush();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void token(InputStream in, String outFilePath) {
		try {
			Parser parser = new Parser(in);
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(outFilePath));
			while (true) {
				Token t = parser.getNextToken();
				if (t == null)
					break;
				out.write(t.toArray());
				out.write('\n');
				out.flush();
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testAttr() {
		String s = "<input id=\"id's info\" value='\"sliekcs=\"'/>";
		StringBuffer sb = new StringBuffer();
		sb.append(s);
		Element e = new Element(sb);
		System.out.println(e.getAttribute("id"));
		System.out.println(e.getAttribute("value"));
	}

	public static void main(String[] args) {
		try {
//			DebugParser.testCustomHtml();

//			InputStream in = new URL("http://www.sina.com.cn").openStream();
//			InputStream in = new URL("http://finance.yahoo.com/q?s=C%26ql=1").openStream();
			InputStream in = new FileInputStream("src/test/resource/yahoo.html");
			DebugParser.rawData(in, "c:/temp/x.html");
//			DebugParser.token(in, "c:/temp/z.html");

			// DebugHtmlParser.testAttr();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
