package jxsource.xml.parser;

public class Token {
//	private int level;
	
	StringBuffer data;
	public Token(StringBuffer data) {
		this.data = data;
	}
	
	public String toString() {
		return data.toString();
	}
	public char[] toArray() {
		char[] chars = new char[data.length()];
		data.getChars(0, data.length(), chars, 0);
		return chars;
	}
	/**
	 * Utility Functions of Token
	 */
	
	/**
	 * Only ASCII SP handled. Not handled for other encoding
	 */
	protected int indexSp(int start) {
		for(int i=start; i<data.length(); i++) {
			char c = data.charAt(i);
			if(Utils.isSp(c)) {
				return i;
			}
		}
		return -1;
	}
	
}
