package jxsource.xml.dom;

import jxsource.xml.parser.Token;

public class Text extends Node{
	Text() {
		super();
		super.setNodeComplete(true);
	}
	
	Text(Token token, Element parent) {
		super(token, parent);
		super.setNodeComplete(true);
	}
	
	public String getText() {
		return me.toString();
	}
	
	@Override
	public String toString() {
		return me.toString();
	}

}
