package jxsource.xml.dom;

import jxsource.xml.parser.TokenFactory;

public class Document extends Element {

	Document()
	{
		super();
		me = new TokenFactory().createTag(defaultRoot);
		ele = (jxsource.xml.parser.Element)me;
//		me.setLevel(0);
//		xpath = getPath();
		doc = this;
	}
	
	public String getPath()
	{
		return "";
	}
	
	public int getLevel() {
		return -1;
	}
}
