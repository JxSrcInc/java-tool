package jxsource.xml.debug;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.URL;
import java.util.Map;

import jxsource.xml.dom.Document;
import jxsource.xml.dom.DocumentBuilder;
import jxsource.xml.dom.Element;
import jxsource.xml.dom.Node;
import jxsource.xml.dom.NodeOperator;
import jxsource.xml.dom.TreeProcessor;
import jxsource.xml.io.XmlOutputStream;
import jxsource.xml.parser.Parser;

public class DebugDomBuilder {

	/*
	 * output original html
	 */
	public void raw(InputStream in, OutputStream out) {
		try {
			Parser parser = new Parser(in);
			DocumentBuilder docBuilder = new DocumentBuilder();
			Document doc = docBuilder.buildeDoc(parser);
			new XmlOutputStream().write(out, doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * output formated html
	 */
	public void format(InputStream in, OutputStream out) {
		try {
			Parser parser = new Parser(in);
			DocumentBuilder docBuilder = new DocumentBuilder();
			Document doc = docBuilder.buildeDoc(parser);
			new XmlOutputStream().writeFormated(out, doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * output each node in a line
	 */
	public void node(InputStream in, final OutputStream out) {
		try {
			TreeProcessor worker = new TreeProcessor();
			Parser parser = new Parser(in);
			final PrintStream w = new PrintStream(out);
			DocumentBuilder docBuilder = new DocumentBuilder();
			Document doc = docBuilder.buildeDoc(parser);
			worker.addProcessor(new NodeOperator() {
				public boolean proc(Node node) {
						String s = node.getToken().toString();
//						System.out.println(node.getRootPath());
//						System.out.println(s);
						w.println(s);
						w.flush();
						return true;
				}
			});
			worker.run(doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void attribute(InputStream in, final OutputStream out) {
		try {
			TreeProcessor worker = new TreeProcessor();
			Parser parser = new Parser(in);
			final PrintStream w = new PrintStream(out);
			DocumentBuilder docBuilder = new DocumentBuilder();
			Document doc = docBuilder.buildeDoc(parser);
			worker.addProcessor(new NodeOperator() {
				public boolean proc(Node node) {
					if(node instanceof Element) {
						Element ele = (Element) node;
						w.println("--- "+ele.getValue());
						ele.getAttribute("attrPath");
						Map<String,String> attrs = ele.getAttributes();
						for(String name: attrs.keySet()) {
							w.println(name + " = "+ attrs.get(name));
							w.flush();
						}
					}
					return true;
				}
			});
			worker.run(doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * output each node root-path in a line
	 */
	public void rootPath(InputStream in, final OutputStream out) {
		try {
			TreeProcessor worker = new TreeProcessor();
			Parser parser = new Parser(in);
			final PrintStream w = new PrintStream(out);
			DocumentBuilder docBuilder = new DocumentBuilder();
			Document doc = docBuilder.buildeDoc(parser);
			worker.addProcessor(new NodeOperator() {
				public boolean proc(Node node) {
						w.println(node.getRootPath());
						w.flush();
						return true;
				}
			});
			worker.run(doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		new DebugDomBuilder().debug();

	}
		
	public void debug() {
		try {

			InputStream in = new FileInputStream(
					"C:\\dev\\eclipse\\projects\\searchengine\\searchengine\\src\\test\\testdata\\barrick.html");
			in = new URL("http://finance.yahoo.com/q/hp?s=C+Historical+Prices").openStream();
//			in = new URL("http://ent.sina.com.cn/").openStream();
//			in = new FileInputStream("C:/temp/aspectj/trace_1338497962046/jxsource.util.aspectj.TraceTests.main_main.xml");
			OutputStream out = new FileOutputStream("C:/temp/z.html");
			format(in, new FileOutputStream("src/test/resource/yahoo.html"));
//			node(in, new FileOutputStream("C:/temp/raw.html"));
/*
			String inputFile = "C:/temp/x.html";
			attribute(new FileInputStream(inputFile), new FileOutputStream("C:/temp/attribute.html"));
			format(new FileInputStream(inputFile), new FileOutputStream("C:/temp/format.html"));
			raw(new FileInputStream(inputFile), new FileOutputStream("C:/temp/raw.html"));
			node(new FileInputStream(inputFile),new FileOutputStream("C:/temp/nodelist.txt"));
			rootPath(new FileInputStream(inputFile),new FileOutputStream("C:/temp/rootpath.txt"));
*/		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
