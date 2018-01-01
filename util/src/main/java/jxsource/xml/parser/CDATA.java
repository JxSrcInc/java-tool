package jxsource.xml.parser;

public class CDATA extends Tag{

	public CDATA(StringBuffer sb) {
		super(sb);
	}
	
	public String getName() {
		return "CDATA";
	}
	public boolean isSingle() {
		return true;
	}

}
