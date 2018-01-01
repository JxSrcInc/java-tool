package jxsource.xml.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class Parser implements TokenSrc {

	private TokenFactory factory = new TokenFactory();
	private List<Token> tokens = new ArrayList<Token>();
	private CharBuffer cb;
	private StringBuffer work = new StringBuffer();
	private boolean complete = false;
	private String CData = "![cdata[";
	/*
	 * true when pointer is in <Tag>
	 */
	private boolean inTag = false;
	
	public Parser(byte[] data) {
		this(new ByteArrayInputStream(data));
	}

	public Parser(InputStream in) {
		cb = new CharBuffer(in);
	}
	
	public Parser(byte[] data, Charset charset) {
		this(new ByteArrayInputStream(data), charset);
	}

	public Parser(InputStream in, Charset charset) {
		cb = new CharBuffer(in, charset);
	}

	public Parser(byte[] data, String charsetName) {
		this(new ByteArrayInputStream(data), Charset.forName(charsetName));
	}

	public Parser(InputStream in, String charsetName) {
		cb = new CharBuffer(in, Charset.forName(charsetName));
	}

	public Token getNextToken() {
		if(tokens.size() > 0) {
			return tokens.remove(0);
		} else {
			if(complete) {
				return null;
			} else {
				parse();
				return getNextToken();
			}
		}
	}
	private void parse() {
		try {
			while (true) {
				char c = cb.getNextChar();
				switch (c) {
				case '\\': // esc char
					work.append(c);
					work.append(cb.getNextChar());
					break;
				case '<':
						putText(work);
						work = new StringBuffer();
						work.append(c);
						String tagName = getTagName();
						createTag(tagName);
						return;
				default:
					// Text
					work.append(c);
				}
			}
		} catch (ParserCompleteException e) {
			// parser process end
			complete = true;
		}
		if (inTag) {
			// file cannot end within a tag
			throw new NullPointerException("Incompleted HTML data: ");
		} else {
			// end with text
			putText(work);
		}
//		mergeScript();
	}
	
	private String getTagName() throws ParserCompleteException {
		while(true) {
			char c = cb.getNextChar();
			work.append(c);
			if(Utils.isSp(c) || c == '>') {
				return work.substring(1, work.length()-1);
			}
		}
	}

	private void createTag(String tagName) throws ParserCompleteException {
		if(tagName.equals("!--")) {
			createComment();
		} else 
		if(tagName.toLowerCase().equals("script")) {
			createScript();
		} else 
		if(tagName.toLowerCase().equals("style")) {
			createStyle();
		} else 
		if(tagName.toLowerCase().equals(CData)) {
				createCData();
		} else {
			createTag();
		}
	}
	
	private void createCData() throws ParserCompleteException {
		while(!work.substring(work.length()-CData.length(), work.length()).equals("]]")) {
				char c = cb.getNextChar();
				work.append(c);
		}
		putTag(work);
		work = new StringBuffer();		
	}

	
	/*
	 * Allow any character in comment
	 */
	private void createComment() throws ParserCompleteException {
		while(!work.substring(work.length()-3, work.length()).equals("-->")) {
				char c = cb.getNextChar();
				if(c == '\\') {
					work.append(c);
					work.append(cb.getNextChar());
				} else {
					work.append(c);
				}
			
		}
		putTag(work);
		work = new StringBuffer();		
	}

	/*
	 * Used for Element and Doctype
	 */
	private void createTag() throws ParserCompleteException {
		int countSingleQuot = 0;
		int countDoubleQuot = 0;
		if(work.charAt(work.length()-1) != '>') {
			boolean end = false;
			while (!end) {
				char c = cb.getNextChar();
				switch (c) {
				case '\\': // esc char
					work.append(c);
					work.append(cb.getNextChar());
					break;
				case '\'':
					if(countDoubleQuot%2 ==0)
						countSingleQuot ++;
					work.append(c);
					break;
				case '"':
					if(countSingleQuot%2 == 0)
						countDoubleQuot ++;
					work.append(c);
					break;
				case '>':
					work.append(c);
					if(countSingleQuot%2 == 0 && countDoubleQuot%2 ==0) {
						end = true;
					}
					break;
				default:
					work.append(c);
				}
			}
		}
		putTag(work);
		work = new StringBuffer();
	}
	
	// TODO: merge with createStyle()
	private void createScript() throws ParserCompleteException {
		// create <Script> tag
		createTag();
		// retrieve it as Element
		Element ele = (Element)tokens.get(tokens.size()-1);
		// if not single, create </Script> and text between two script tags if any 
		if(!ele.isSingle()) {
			StringBuffer sbScript = new StringBuffer();
			while (true) {
				char c = cb.getNextChar();
				switch (c) {
				case '\\': // esc char
					sbScript.append(c);
					sbScript.append(cb.getNextChar());
					
					break;
				case '<':
					work.append(sbScript);
					sbScript = new StringBuffer();
					sbScript.append(c);
					break;
				case '>':
					sbScript.append(c);
					if(sbScript.toString().toLowerCase().equals("</script>")) {
						putText(work);
						work = sbScript; 
						putTag(work);
						work = new StringBuffer();
						return;
					} else {
						work.append(sbScript);
						sbScript = new StringBuffer();
					}
					break;
				default:
					// Text
					sbScript.append(c);
				}
			}			
		}
	}

	// TODO: merge with createScript()
	private void createStyle() throws ParserCompleteException {
		// create <Script> tag
		createTag();
		// retrieve it as Element
		Element ele = (Element)tokens.get(tokens.size()-1);
		// if not single, create </Script> and text between two script tags if any 
		if(!ele.isSingle()) {
			StringBuffer sbScript = new StringBuffer();
			while (true) {
				char c = cb.getNextChar();
				switch (c) {
				case '\\': // esc char
					sbScript.append(c);
					sbScript.append(cb.getNextChar());
					
					break;
				case '<':
					work.append(sbScript);
					sbScript = new StringBuffer();
					sbScript.append(c);
					break;
				case '>':
					sbScript.append(c);
					if(sbScript.toString().toLowerCase().equals("</style>")) {
						putText(work);
						work = sbScript; 
						putTag(work);
						work = new StringBuffer();
						return;
					} else {
						work.append(sbScript);
						sbScript = new StringBuffer();
					}
					break;
				default:
					// Text
					sbScript.append(c);
				}
			}			
		}
	}

	private void putText(StringBuffer text) {
		if (text.length() > 0) {
			tokens.add(factory.createText(text));
		}
	}

	private void putTag(StringBuffer sb) {
		Tag tag = factory.createTag(sb);
		tokens.add(tag);
	}

	public static void main(String[] args) {
		try {
			InputStream in = new java.io.FileInputStream("C:\\dev\\eclipse\\httpproxy\\http-xpath\\t.html");
			OutputStreamWriter out = new OutputStreamWriter(
					new java.io.FileOutputStream("c:/temp/z.html"));
			Parser cb = new Parser(in);
			while(true) {
				Token t = cb.getNextToken();
				if(t == null) break;
				out.write(t.toArray());
				System.out.println(t.toArray());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
