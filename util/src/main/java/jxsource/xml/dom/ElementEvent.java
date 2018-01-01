package jxsource.xml.dom;

public class ElementEvent {

	private final Element ele;
	
	public ElementEvent(Element ele) {
		this.ele = ele;
	}
	
	public Element getValue() {
		return ele;
	}
}
