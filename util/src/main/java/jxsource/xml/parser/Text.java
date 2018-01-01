package jxsource.xml.parser;

public class Text extends Token{

	public Text(StringBuffer sb) {
		super(sb);
	}
	
	public void replace(String value) {
		this.data = new StringBuffer(value);
		
	}
	
	public String getName() {
		return "Text";
	}
	public boolean isSingle() {
		return true;
	}

}
