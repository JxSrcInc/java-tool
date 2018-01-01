package jxsource.xml.debug;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import jxsource.xml.dom.Node;
import jxsource.xml.dom.XPathChooser;
import jxsource.xml.io.XmlOutputStream;
import jxsource.xml.parser.Parser;


public class DebugXPathChooser {

	public static void main(String[] args) {
//		String path = "/html/body/div[3]/div[4]/table[2]/tr[2]/td/table[4]/tr/td/table";
		String path = "/html/body/div[3]";
		try {
			InputStream in = new FileInputStream("src/test/resource/yahoo.html");
			Parser parser = new Parser(in);
			XPathChooser builder = new XPathChooser(parser);
			builder.searchNode(path);
			int status = builder.getStatus();
			System.out.println(status);
			builder.searchNode(path);
			status = builder.getStatus();
			System.out.println(status);
			Node node = builder.getFoundNode();
			System.out.println(node);
			if(node != null) {
				OutputStream out = new FileOutputStream("c:/temp/xpathChooser.html");
				new XmlOutputStream().write(out, node);
				out.close();

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
