package jxsource.xml.parser;

class Attr {
		private AttrToken name;
		private String attrName;
		private AttrToken value;
		
	Attr(AttrToken name, AttrToken value) {
		this.name = name;
		this.value = value;
		attrName = name.getValue().toString().toLowerCase();
	}
	
	AttrToken getName() {
		return name;
	}
	String getAttrName() {
		return attrName;
	}
	AttrToken getValue() {
		return value;
	}
	void delete() {
		StringBuffer data = name.getElement().data;
		if(value == null) {
			data.delete(name.getPos(), name.getPos()+name.getValue().length());
		} else {
			data.delete(name.getPos(), value.getPos()+value.getValue().length());			
		}
	}
	public String toString() {
		String s = "Attr: "+name.getValue().toString()+"("+Integer.toString(name.getPos())+")";
		if(value != null) {
			s += "="+value.getValue().toString()+"("+Integer.toString(value.getPos())+")";
		}
		return s;
	}
}
