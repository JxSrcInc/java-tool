package jxsource.xml.io;

import jxsource.xml.parser.Element;
import jxsource.xml.parser.Token;

/**
 * 
 * A Context is a sequence of tokens, which 
 * 	1. contains at least two tokens, i.e., cannot be in format <element/>
 *  2. starts with token <element> 
 * 	3. ends with token </element> which has the same Token.getLevel() as the start token <element>
 * 
 * The caller must move the cursor of input stream, which is under XmlInputFilter, 
 * 	to the first token after <element> before using this filter
 * 
 * The filter returns all tokens until (and including) </element> through getNextToken(). 
 * 
 * After returning </element>, the filter returns null,
 * 	and the cursor of input stream is at the first token after </element>
 *
 * Assumption: the data in the input stream must be valid at least tag level.
 * 
 */
public class ElementFilter extends XmlInputFilter { 
	
	private String[] skipElements = new String[0];
	private boolean skip = false;
	private String skipElement;
	private Token t;
	
	public ElementFilter(String...skipElements) {
		this.skipElements = skipElements;
	}
	public Token getNextToken()
	{
		do {
			t = filter.getNextToken();
		} while(skip());
		return t;
	}
	
	private boolean skip()
	{
		if(t == null) {
			return false;
		} else
		if(!(t instanceof Element)) {
			return skip;
		} else {
			Element ele = (Element) t;
			if(!skip){
				for(String name: skipElements) {
					if(name.equals(ele.getName())) {
						if(!ele.isSingleElement()) {
							skip = true;
							skipElement = name;
						} 
						return true;
					} else
					if(name.equals(ele.getName().substring(1))) {
						// end-tag
						return true;
					}
				}
				return false;
			} else {
				if(skipElement.equals(ele.getName().substring(1))) {
					// end-tag of skipElement
					skip =false;
					skipElement = "";
				}
				return true;
			}
		}
	}
}
