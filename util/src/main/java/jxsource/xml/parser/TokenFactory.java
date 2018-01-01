package jxsource.xml.parser;

public class TokenFactory {
	
	public Text createText(String value) {
		return createText(new StringBuffer(value));
	}
	public Text createText(StringBuffer sb) {
		return new Text(sb);
	}
	public Tag createTag(String value) {
		return createTag(new StringBuffer(value));
	}
	public Tag createTag(StringBuffer sb) {
		Tag tag = new Tag(sb);
		String name = tag.getName().toLowerCase();
		if(name.indexOf("![cdata[")==0) {
			return new CDATA(sb);
		} else if(name.indexOf("!--") == 0) {
			return new Comment(sb);
		} else if(name.equals("!doctype")) {
			return new Doctype(sb);
		} else {
			return new Element(sb);
		}
	}

}
