package jxsource.xml.parser;

class Utils {

	public static boolean isSp(char c) {
		return Character.isSpaceChar(c) ||
		 (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
	
	public static StringBuffer trimLeft(StringBuffer src) {
		int start = src.length()-1; 
		for(int i=0; i<src.length(); i++) {
			char c = src.charAt(i);
			if(!isSp(c)) {
				start = i;
				break;
			}
		}
		if(start < src.length() -1) {
			return new StringBuffer(src.subSequence(start, src.length()-1));
		} else {
			return new StringBuffer();
		}
	}
	
	public static StringBuffer trimRight(StringBuffer src) {
		int end = 0; 
		for(int i=src.length()-1; i>-1; i--) {
			char c = src.charAt(i);
			if(!isSp(c)) {
				end = i+1;
				break;
			}
		}
		if(end > 0) {
			return new StringBuffer(src.subSequence(0, end));
		} else {
			return new StringBuffer();
		}
	}
	
	public static StringBuffer trim(StringBuffer src) {
		return trimLeft(trimRight(src));
	}
}
