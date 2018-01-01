package jxsource.xml.debug;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import jxsource.xml.dom.Element;
import jxsource.xml.dom.Node;
import jxsource.xml.dom.PathUtility;
import jxsource.xml.dom.XPathChooser;
import jxsource.xml.io.XmlOutputStream;
import jxsource.xml.parser.Parser;

public class DebugPathUtilities {

	public static void main(String[] args) {
//		String path = "/html/body/div[3]/div[4]/table[2]/tr[2]/td/table[4]/tr/td/table";
		String path = "/html/body/div[3]/div[4]/table[2]/tr[2]/td/table[4]/tr/td";
		try {
			InputStream in = new FileInputStream("src/test/resource/yahoo.html");
			Parser parser = new Parser(in);
			PathUtility util = new PathUtility();

			Node startNode = util.locateElement(parser, path);
			Element node = util.locateSubTree((Element)startNode, "/table");
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
