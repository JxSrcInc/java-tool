/*
 * JavaScript to trace xpath
 */
var defaultColor;
var selected;
var selectedGroup;
var selectedPath = "";
var trace = true;
var nodeBackgroundColor = "yellow";
var myWindow;

function setXPath(obj) {
	var path = obj.getAttribute("attrPath");
	selectedPath = path;
}
/*
 * called by onmousemove event.
 */
function setLeafBackgroundColor(obj) {
	if (!trace) {
		return;
	}
	var path = obj.getAttribute("attrPath");
	var showPath = selectedPath;
	// path is a child of showPath
	if ((path.indexOf(showPath) == 0 && path.length > showPath.length) ||
	// path is the first sibling of showPath
	(showPath.indexOf(path) == 0 && showPath.charAt(path.length) == '[') ||
	// path and showPath have different parent
	(path.indexOf(showPath) == -1 && showPath.indexOf(path) == -1)) {
		defaultColor = document.body.style.backgroundColor;
		if (selected != null) {
			selected.style.backgroundColor = defaultColor;
		}
		selected = obj;
		selectedPath = path;
		document.getElementById("jx_path").value = path;
		obj.style.backgroundColor = "red";
	}
}
function startTrace() {
	trace = true;
	selectedGroup.style.backgroundColor = defaultColor;
}
function stopTrace() {
	if (trace) {
		trace = false;
	}
}
function saveHtml() {
	var path = document.getElementById("jx_path");
// 	console.log(path.value);
	var result = getNodeByPath(document.body, path.value);
	// alert(result);
	if (result != null) {
//		console.log(document.getElementsByTagName("head")[0].innerHtml);
		// var innerHtml = document.getElementsByTagName("html")[0].innerHTML;
		var innerHtml = result.innerHTML; // selected.innerHTML;
		// alert(innerHtml);
		write(innerHtml, "unknown");
	} else {
		alert("Cannot find node for " + path.value);
	}
}
function getNodeByPath(node, path) {
	try {
		if (node.nodeType == 1)
			var attrPath = node.getAttribute("attrPath");
		if (attrPath != null && attrPath == path) {
			// alert("* "+attrPath);
			return node;
		}
		var childNodes = node.childNodes;
		if (childNodes != null) {
			for ( var j = 0; j < childNodes.length; j++) {
				var child = childNodes[j];
				if (child.nodeType == 1) {
					var found = getNodeByPath(child, path);
					if (found != null) {
						return found;
					}
				}
			}
		}
	} catch (e) {
		alert(e.name + ": " + e.message);
	}
	return null;
}
function setNodeBackgroupColor(path) {
	if (selectedGroup != null) {
		selectedGroup.style.backgroundColor = defaultColor;
	}
	findGroupObject(path.substring(1), null);
	selectedGroup.style.backgroundColor = nodeBackgroundColor;
}
function containsAttrPathChild(node) {
	var ok = false;
	try {
		var childNodes = node.childNodes;
		if (childNodes != null) {
			for ( var j = 0; j < childNodes.length && !ok; j++) {
				var child = childNodes[j];
				if (child.getAttribute("attrPath") == null) {
					ok = containsAttrPathChild(child);
				}
			}
		}
	} catch (e) {
		alert(e.name + ": " + e.message);
	}
	return ok;
}
function findGroupObject(path, node) {
	var i = path.indexOf("/");
	var item = path;
	var rest = "";
	if (i > 0) {
		item = path.substring(0, i);
		rest = path.substring(i + 1);
	}
	if (item == "html") {
		selectedGroup = document.getElementsByTagName("html")[0];
		return findGroupObject(rest, selectedGroup);
	} else {
		selectedGround = node;
		var childNodes = node.childNodes;
		var name = item;
		i = item.indexOf("[");
		var index = 1;
		if (i != -1) {
			name = item.substring(0, i);
			var k = item.indexOf("]");
			index = item.substring(i + 1, k);
		}
		var count = 0;
		for ( var j = 0; j < childNodes.length; j++) {
			var child = childNodes[j];
			if (node.nodeName.toLowerCase() == "table"
					&& child.nodeName.toLowerCase() == "tbody"
					&& name.toLowerCase() != "tbody") {
				// skip <tbody> tag because it may be added by browser like IE
				// but it is not in original html.
				selectedGroup = child;
				return findGroupObject(path, selectedGroup);
			} else if (child.nodeName.toLowerCase() == name.toLowerCase()) {
				count++;
				if (count == index) {
					selectedGroup = child;
					if (rest.length > 0) {
						return findGroupObject(rest, selectedGroup);
					} else {
						return selectedGroup;
					}
				}
			}
		}
	}
	return selectedGroup;
}
function updatePath(jx_path) {
	setNodeBackgroupColor(jx_path.value);
}
function mouseClick(obj) {
	var path = obj.getAttribute("attrPath");
	var pathSelected = selected.getAttribute("attrPath");
	if (path == pathSelected) {
		stopTraceAction();
		// stopTrace();
		// var jx_path = document.getElementById("jx_path");
		// var value = prompt("Trace(T)/New Path:", jx_path.value);
		// if (value == 'T' || value == 't') {
		// startTrace()
		// } else {
		// if (value && value != jx_path.value) {
		// jx_path.value = value;
		// setNodeBackgroupColor(value);
		// }
		// }
	}
}
function stopTraceAction() {
	stopTrace();
	var jx_path = document.getElementById("jx_path");
	var value = prompt("Trace(T)/New Path:", jx_path.value);
	if (value == 'T' || value == 't') {
		startTrace()
	} else {
		if (value && value != jx_path.value) {
			jx_path.value = value;
			setNodeBackgroupColor(value);
		}
	}
}

function keypress(event) {
	if (event.charCode == 96) {
		stopTraceAction();
		saveHtml();
		// var v = document.getElementsByTagName("html")[0].innerHTML;
		// write(v, "main");
	}
}