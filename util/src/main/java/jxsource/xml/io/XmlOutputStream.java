package jxsource.xml.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;

import jxsource.xml.dom.Document;
import jxsource.xml.dom.Element;
import jxsource.xml.dom.Node;
import jxsource.xml.dom.Text;

public class XmlOutputStream {

	Charset charset;
	
	public XmlOutputStream() {
	}

	public XmlOutputStream(Charset charset) {
		Charset.isSupported(charset.displayName());
		this.charset = charset;
	}

	public XmlOutputStream(String charsetName) {
		this(Charset.forName(charsetName));
	}

	public String getString(Document doc) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writeFormated(out, doc);
		out.close();
		return new String(out.toByteArray());
	}
	/*
	 * write formated document
	 */
	public void writeFormated(OutputStream out, Document doc)
			throws IOException {
		for (Node child : doc.getChildren()) {
			writeFormated(out, child, 0);
		}
	}

	public String getString(Node node) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writeFormated(out, node, 0);
		out.close();
		return new String(out.toByteArray());
	}
	/*
	 * write formated tree
	 */
	public void writeFormated(OutputStream out, Node node, int startLevel)
			throws IOException {
		byte[] intent = getIntent(node, startLevel);
		if (!node.getName().equals("Root")) {
			if ((node instanceof Element && ((Element) node).isEndTagElement())) {
				out.write(intent);
				out.write(getBytes(node));
				out.flush();
			} else {
				out.write(intent);
				out.write(getBytes(node));
				out.flush();
			}
		}
		if (node instanceof jxsource.xml.dom.Element) {
			for (Node child : ((jxsource.xml.dom.Element) node).getChildren()) {
				if (child instanceof Text) {
					String name = child.toString();
					if (name.trim().length() == 0) {
						continue;
					}
				}
				writeFormated(out, child, startLevel);
			}
		}
	}
	
	public void print(PrintStream p, Document doc) throws IOException{
		OutputStream out = new ByteArrayOutputStream();
		writeFormated(out, doc);
		p.print(new String(out.toString()));
		
	}

	public void print(PrintStream p, Node node) throws IOException{
		OutputStream out = new ByteArrayOutputStream();
		writeFormated(out, node, 0);
		p.print(new String(out.toString()));
		
	}

	/*
	 * write document without format change
	 */
	public void write(OutputStream out, Document doc) throws IOException {
		for (Node child : doc.getChildren()) {
			write(out, child);
		}
	}

	/*
	 * write tree withoug format change
	 */
	public void write(OutputStream out, Node node) throws IOException {
		OutputStreamWriter w = null;
		if(charset == null) {
			w = new OutputStreamWriter(out);
		} else {
			w = new OutputStreamWriter(out, charset);			
		}
		w.write(node.getToken().toArray());
		w.flush();
		// output children
		if (node instanceof Element) {
			for (Node child : ((Element) node).getChildren()) {
				write(out, child);
			}
		}
	}

	private byte[] getBytes(Node node) {
		if(charset != null) {
			return node.getToken().toString().getBytes(charset);
		} else {
			return node.getToken().toString().getBytes();
		}

	}

	private byte[] getIntent(Node node, int initIntent) {
		if (initIntent < 0) {
			return new byte[0];
		}
		int len = node.getLevel() - initIntent;
		if (len < 0) {
			len = 0;
		}
		byte[] intent = new byte[len + 1];
		intent[0] = '\n';
		for (int i = 1; i < intent.length; i++)
			intent[i] = ' ';
		return intent;
	}
}
