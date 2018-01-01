package jxsource.xml.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class CharBuffer {
	Reader in;
	char[] cbuf = new char[1024*8];;
	int length;
	int pos;
	public CharBuffer(InputStream in) {
		this.in = new InputStreamReader(in);
	}

	public CharBuffer(InputStream in, Charset charset) {
		this.in = new InputStreamReader(in, charset);
	}

	public char getNextChar() throws ParserCompleteException {
		if(pos == length) {
			try {
				length = in.read(cbuf);
				if(length < 0) {
					in.close();
					throw new ParserCompleteException();
				}
				pos = 0;
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return cbuf[pos++];
	}
	
	public static void main(String[] args) {
		try {
			CharBuffer cb = new CharBuffer(new java.io.FileInputStream("d:/temp/x.html"));
			while(true) {
				System.out.print(cb.getNextChar());
			}
		} catch(ParserCompleteException pe) {
			System.out.println();
			System.out.println("....end");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
