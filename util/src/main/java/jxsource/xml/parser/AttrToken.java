package jxsource.xml.parser;


/**
 * 
 * AttributeToken may take three values:
 * 	Attribute Name
 * 	Attribute Value
 * 	char "="
 *
 */
class AttrToken {
	/**
	 * start index in Token/Tag.data StringBuffer
	 */
	private int pos;
	/**
	 * value of Attr
	 */
	private StringBuffer value = new StringBuffer();
	private Element ele;
	/**
	 * index of this AttrToken in Element.tokens (List<AttrToken>) 
	 */
	private int index;
	
	AttrToken(Element ele) {
		this.ele = ele;
	}
/*	AttrToken(Element ele, StringBuffer value) {
		this.ele = ele;
		this.value = value;
	}
*/
	Element getElement() {
		return ele;
	}
	int getIndex() {
		return index;
	}

	void setIndex(int index) {
		this.index = index;
	}

	int getPos() {
		return pos;
	}

	void setPos(int pos) {
		this.pos = pos;
	}

	/*
	 * this is special method used in Element parser 
	 * when building AttrToken
	 */
	void append(char c) {
		value.append(c);
	}
	
	StringBuffer getValue() {
		return value;
	}
	
	String getStringValue() {
		String s = value.toString();
//		if(s.charAt(0) == '"' || s.charAt(0) == '\'') {
//			s = s.substring(1,s.length()-1);
//		}
		return s;
	}

	void setValue(StringBuffer value) {
		deleteValue();
		add(value);
	}

	void deleteValue() {
		ele.data.delete(pos, pos+this.value.length());		
	}
	
	// TODO: no quote handled
	void setValue(String value) {
		setValue(new StringBuffer(value));
	}
/*	
	void updateValue(String value) {
		StringBuffer data = ele.data;
		StringBuffer newValue = new StringBuffer();
		deleteValue();
		if(value != null) {
//			String _value = '"'+value+'"';
//			newValue.append(_value);
//			data.insert(pos, _value);
			newValue.append(value);
			data.insert(pos, value);
		}
		this.value = newValue;
	}
*/
	// TODO: no quote handled
	private void add(StringBuffer value) {
//		addValue += ' ';
//		int insertIndex = 0;
//		if(value.charAt(0) == '"' || value.charAt(0) == '\'') {
//			insertIndex = 1;
//		}
		this.value = value;
		ele.data.insert(pos, value);

	}
	public String toString() {
		return Integer.toString(pos)+"-"+value;
	}
}
