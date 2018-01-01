package jxsource.xml.io;

import jxsource.xml.parser.Token;
import jxsource.xml.parser.TokenSrc;

public abstract class XmlInputFilter implements TokenSrc{

	TokenSrc filter;
	
	public abstract Token getNextToken();
	
	public void setXmlInputStream(TokenSrc filter)
	{
		this.filter = filter;
	}
	
	public TokenSrc getXmlInputStream()
	{
		return filter;
	}
}