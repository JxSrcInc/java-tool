/**
 * This java script handles AJAX process to server.
 */
var headId = "___HEADID___";

function saveInnerHTML(name, e) {
	var evtobj = window.event ? event : e; // distinguish between IE's explicit
											// event object (window.event) and
											// Firefox's implicit.
	var unicode = evtobj.charCode ? evtobj.charCode : evtobj.keyCode;
	if (unicode == 96) {
		var v = document.getElementsByTagName("html")[0].innerHTML;
		write(v, name);
	}
}

function replaceDoc(doc) {
	doc = replace(doc, "<", "&lt;");
	doc = replace(doc, ">", "&gt;");
	doc = replace(doc, "\"", "&quot;");
//	doc = replace(doc, " ", "&nbsp;&nbsp;&nbsp;&nbsp;");
//	doc = replace(doc, "\n", "<br/>");
	doc = newLine(doc);
	return doc;
}
function replace(str, src, target) {
	while(str.indexOf(src) != -1) {
		str = str.replace(src, target);
	}
	return str;
}
function newLine(str) {
	var lines = str.split("\n");
	var newStr = "";
	for(var i=0; i<lines.length; i++) {
		var l = lines[i];
		for(var k=0; k<l.length; k++) {
			if(l.charAt(k) == " ") {
				l = replace(l, " ", "&nbsp;&nbsp;&nbsp;");
			} else {
				break;
			}
		}
		newStr += l+"<br/>";
	}
	return newStr;
}

function getType(obj) {
	if (obj == null) {
		return "null";
	} else if (typeof obj == "number") {
		return "number";
	} else if (typeof obj == "boolean") {
		return "boolean";
	} else if (typeof obj == "string") {
		return "string";
	} else if (typeof obj == "object") {
		return "object";
	} else if (typeof obj == "undefined") {
		return "undefined";
	} else {
		return "unknown";
	}
}
/*
 * It makes an AJAX call to server
 * and displays the returned content in a separate window.
 */
function write(msg, name) {
	var xmlhttp;
	if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();
	} else {// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.open("POST", "/jxsource_save_innerhtml", true);
	xmlhttp.setRequestHeader("Frame-Name", name);
	xmlhttp.setRequestHeader("Head-Id", headId);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4) {
			if(xmlhttp.status == 200) {
				// display AJAX returns in a new window
				var w = window.open("");
				w.document.write(xmlhttp.responseText);
			} else {
				alert('Ajax failed: '+xmlhttp.status);
			}
		}
	};
	xmlhttp.send(msg);
}

function convert(obj) {
	var ret = "";
	if (typeof obj == "object" && obj.nodeType != null) {
		var type = obj.nodeType;
		ret = "";
		if (type == 1) {
			var name = getNodeType(obj);
			if (obj.nodeName.charAt(0) != "/") {
				ret += "<" + name;
				for ( var i = 0; i < obj.attributes.length; i++) {
					ret += getNodeType(obj.attributes[i]);
				}
				ret += ">";
				ret += "\n" + procChildren(obj);
				ret += "</" + name + ">";
			}
		} else if (type == 9) {
			ret += procChildren(obj);
		} else if (type == 3) {
			ret += getNodeType(obj);
		}
	} else {
		ret += getType(obj) + "\n";
	}
	return ret;
}

function procChildren(obj) {
	var children = obj.childNodes;
	var ret = "";
	for ( var i = 0; i < children.length; i++) {
		ret += convert(children.item(i));
	}
	return ret;
}

var contains = new Array("Margin", "Editable");
function excludeAttribute(name) {
	for ( var i = 0; i < contains.length; i++) {
		if (name.indexOf(contains[i]) != -1) {
			return true;
		}
	}
	return false;
}
function getNodeType(obj) {
	var type = obj.nodeType;
	if (type == 1) {
		// Element
		return obj.nodeName;
	} else if (type == 2) {
		// Attribute
		var v = obj.value;
		if (!excludeAttribute(obj.nodeName) && v != 'null' && v != '0'
				&& v != 'false' && v.length > 0) {
			return " " + obj.nodeName + "='" + v + "'";
		} else {
			return "";
		}
	} else if (type == 3) {
		// Text
		return obj.toString();
	} else if (type == 4) {
		return "CData";
	} else if (type == 5) {
		return "Entity Ref";
	} else if (type == 6) {
		return "Entity";
	} else if (type == 7) {
		return "Processing";
	} else if (type == 8) {
		return "Comment";
	} else if (type == 9) {
		return "Document";
	} else if (type == 10) {
		return "Document Type";
	} else if (type == 11) {
		return "Document Fragment";
	} else if (type == 12) {
		return "Notation";
	} else {
		return "Unknown";
	}
}