package jxsource.xml.parser;

import java.util.*;

/*
 * Quotes are automatically added to or removed from attribute values 
 * when calling getAttribute(), setAttribute() and getAttributes() methods.
 * 
 */
public class Element extends Tag {
	class Attrs {
		/**
		 * key: attribute name -- in lower case
		 */
		private Map<String, Attr> _attrs = new HashMap<String, Attr>();
		
		public void put(String name, Attr value) {
			_attrs.put(name.toLowerCase(), value);
		}

		public Attr get(String name) {
			return _attrs.get(name.toLowerCase());
		}

		public Attr remove(String name) {
			return _attrs.remove(name.toLowerCase());
		}
		
		public void clear() {
			_attrs.clear();
		}
		
		public Set<String> keySet() {
			return _attrs.keySet();
		}
		
		public Collection<Attr> values() 
		{
			return _attrs.values();
		}
	}
	private boolean initialized = false;
	Attrs attrs = new Attrs();
	// pointer of data used in parse()
	private int pos;
	// used to create attrs map. 
	List<AttrToken> attrTokens = new ArrayList<AttrToken>();
	private int DoubleQuote = 1;
	private int SingleQuote = -1;
	
	public Element(StringBuffer data) {
		super(data);
	}

	protected void initialize() {
		if(!initialized) {
			init();
		initialized = true;
		}
	}

	private void init() {
		// clear attrs if re-init
		attrs.clear();
		attrTokens.clear();
		parse();
		createAttrs();
	}

	public Map<String, String> getAttributes() {
		initialize();
		Map<String,String> attributes = new HashMap<String,String>();
		for(String name: attrs.keySet()) {
			/*
			 * Quote removed from return value -- see getAttribute() method;
			 */
			attributes.put(name, this.getAttribute(name));
		}
		return attributes;
	}
	public boolean isElementEndTag() {
		return data.charAt(1) == '/';
	}
	/*
	 * Quote removed from return value
	 */
	public String getAttribute(String name) {
		initialize();
		Attr attr = attrs.get(name);
		if(attr == null) {
			return null;
		}
		AttrToken value = attr.getValue();
		if(value != null) {
			String str = value.getStringValue();
			if(str.charAt(0) == '"' || str.charAt(0) == '\'') {
				str = str.substring(1, str.length()-1);
			}
			return str;
		} else {
			return null;
		}
	}
	
	public void deleteAttribute(String name) {
		initialize();
		attrs.get(name).delete();
	}

	public void setAttribute(String name, String value) {
		if(value != null && value.length() > 0 &&
				value.charAt(0) != '"' && value.charAt(0) != '\'') {
			value = '"' + value +'"';
		}
		initialize();
		Attr attr = attrs.get(name);
		StringBuffer sb = new StringBuffer();
		// number of chars changed when new attribute value is set.
		if(attr != null) {
			AttrToken at = attr.getValue();
			if(at != null) {
				sb.append(value);
				int index = at.getIndex();
//				if(index > 0) {
					AttrToken atEqual = attrTokens.get(index-1);
					if(atEqual.getValue().toString().equals("=")) {
						if(value != null) {
							at.setValue(value);
						} else {
							at.deleteValue();
							atEqual.deleteValue();
						}
					} // else - never happen

//				}
			} else {
				if(value != null) {
					// the existing attribute has no value
					AttrToken atName = attr.getName();
					int pos = atName.getPos();
					int insertPos = pos+atName.getValue().length();
					data.insert(insertPos, value);
					data.insert(insertPos, "=");
				} // else - do nothing
			}
		} else {
			// add new attribute
			appendAttribute(name, value);
//			String newAttr = ' '+name;
//			if(value != null) {
//				newAttr += "="+value+" ";
//			}
//			char[] chars = newAttr.toCharArray();
//			this.data.insert(this.getEndPos(), chars);
		}
		init();
	}
	
	private void appendAttribute(String name, String value) {
//		if(value != null && value.length() > 0 &&
//				value.charAt(0) != '"' && value.charAt(0) != '\'') {
//			value = '"' + value +'"';
//		}
		if(value != null) {
			data.insert(getEndPos(), " "+name+"="+value);
		} else {
			data.insert(getEndPos(), " "+name);			
		}
//		init();
	}
	
	public boolean isSingleElement() {
		return isSingle();
	}

	private boolean isAasignChar(int pos) {
		if((pos) >= attrTokens.size()) {
			return false;
		} else {
			return (attrTokens.get(pos).getValue().charAt(0) == '=');
		}
	}
	private void createAttrs() {
		int i=0;
		boolean isName = false;
		AttrToken name = null;
		while(i < this.attrTokens.size()) {
			AttrToken at = attrTokens.get(i);
			if(!isName) {
				name = at;
				isName = true;
				i++;
			} else {
				if(isAasignChar(i)) {
					// attribute has value
					addAttr(name, attrTokens.get(++i));
					isName = false;
					i++;
				} else {
					// attribute has no value
					// this AttrToken is a new attribute name
					addAttr(name, null);
					name = at;
					i++;
				}
			}
		}
		if(isName) {
			addAttr(name, null);
		}
	}

	private int getQuoteType(char c) {
		int type = 0;
		if(c == '"') {
			type = DoubleQuote;
		} else {
			type = SingleQuote;
		}
		return type;
	}

	/*
	 * In this method, each AttrToken builds by adding one char after another until 
	 * addAttrToken method is called.
	 */
	private void parse() {
		int countQuote = 0;
		// initial pars process;
		pos = this.getName().length() + 1;
		// clean SP
		this.moveToNextNoneSpChar();
		AttrToken attrToken = new AttrToken(this);
		attrToken.setPos(pos);
		int quoteType = 0;
		while (pos < data.length()) {
			char c = data.charAt(pos++);
			if(c == '\\') {
				// esc
				attrToken.append(c);
				attrToken.append(data.charAt(pos++));				
			} else if(c == '"' || c == '\'') {
				attrToken.append(c);
				if(countQuote == 0) {
					quoteType = getQuoteType(c);
					countQuote++;
				} else
				if(countQuote == 1 && quoteType == getQuoteType(c)) {
					countQuote++;
				}
				if(countQuote == 2) {
					countQuote = 0;
					attrToken = addAttrToken(attrToken);
					quoteType = 0;
				}
			} else if(c == '=' && countQuote != 1) {
				int save_pos = pos;
				attrToken = addAttrToken(attrToken);
				attrToken.setPos(save_pos-1);
				attrToken.append(c);
				attrToken = addAttrToken(attrToken);
			} else if(Utils.isSp(c)) {
				if(countQuote == 1) {
					attrToken.append(c);
				} else {
//					attrToken.append(c);
					attrToken = addAttrToken(attrToken);
				}
			} else {
				attrToken.append(c);
			}
		}
		StringBuffer sb = attrToken.getValue();
		CharSequence cs = null;
		int i = sb.lastIndexOf("/");
//		System.out.println("01234567890123456789012345678901234567890123456789");
//		System.out.println(data.toString());
		int _pos = 0;
		if(i < 0) {
			// not single element, i.e., not <element..../>
			cs = sb.subSequence(0, sb.length()-1);
			_pos = pos - cs.length() -1;
		} else {
			cs = sb.subSequence(0, i);
			_pos = pos - cs.length() - i;
		}
		
		if(cs.toString().trim().length() > 0) {
			AttrToken at = new AttrToken(this);
			for(int k=0; k<cs.length(); k++) {
				at.append(cs.charAt(k));
			}
			at.setPos(_pos);
			at.setIndex(attrTokens.size());
			attrTokens.add(at);
		}
		/*
		 * bug: the last attrToken is "="
		 * fix: replace '=' with ' '
		 */
		if(attrTokens.size() > 0) {
			AttrToken at = attrTokens.get(attrTokens.size()-1);
			if(at.getStringValue().equals("=")) {
				at.setValue(" ");
				attrTokens.remove(attrTokens.size()-1);
			}
		}
//		for(AttrToken _at: attrTokens) {
//			System.out.println("--> "+_at);
//		}
	}

	private AttrToken addAttrToken(AttrToken attrToken) {
		if(attrToken.getValue().length() > 0) {
			attrToken.setIndex(attrTokens.size());
			attrTokens.add(attrToken);
		}
		if(this.moveToNextNoneSpChar()) {
			attrToken = new AttrToken(this);	
			attrToken.setPos(pos);
		} else {
			attrToken = null;
		}
		return attrToken;
	}

	private boolean moveToNextNoneSpChar() {
		while ((pos+1) < data.length() && Utils.isSp(data.charAt(pos))) {
			pos++;
		}
		return !((pos) == data.length());
	}

	private void addAttr(AttrToken name, AttrToken value) {
		attrs.put(name.getValue().toString().toString(), new Attr(name, value));

	}

	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
//		sb.append("<a href=\"http://billing.finance.yahoo.com/realtime_quotes/signup?.src=quote&amp;.refer=nb\">");
		sb.append("<a>");
		Element e = new Element(sb);
		e.setAttribute("asdf", "sss");
		System.out.println(e);
		System.out.println(e.getName().equals("a"));
		System.out.println(e.getAttribute("href"));
	}
}
