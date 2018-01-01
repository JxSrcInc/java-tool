package jxsource.xml.parser;

public class Tag extends Token {

	private String tagName;
	private int posAttrStart;

	public Tag(StringBuffer data) {
		super(data);
		int i = indexSp(1);
		if (i > 0) {
			tagName = data.substring(1, i);
			posAttrStart = i+1;
		} else {
			if(data.charAt(data.length()-2) == '/') {
				// case: <tag/> -- single tag without attribute
				tagName = data.substring(1, data.length() - 2);
			} else {
				// case: <tag> and </tag>
				tagName = data.substring(1, data.length() - 1);
			}
		}
		if(tagName.length() == 0) {
			throw new RuntimeException("TagName cannot have a value: "+data.toString());
		}
	}
	
	public int getPosAttrStart() {
		return this.posAttrStart;
	}
	
	// in lower case -- html only 
	public String getName() {
		return tagName.toLowerCase();
	}
	/**
	 * 
	 * @return -- int: the pos of '/' in data if '/' follows '>' 
	 * 			or the pos of '>' in data if there is no '/' before '>'
	 */
	public int getEndPos() {
		for(int i=data.length()-2; i>0; i--) {
			if(!Utils.isSp(data.charAt(i))) {
				if(data.charAt(i) == '/')
					return i;
				else
					return i+1;
			}
		}
		return 0;
	}
	public boolean isSingle() {
		return data.charAt(getEndPos())=='/';
	}
	
}
