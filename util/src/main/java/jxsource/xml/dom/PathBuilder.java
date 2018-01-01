package jxsource.xml.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class PathBuilder {
	
	class PathNode {
		public PathNode() {}
		public PathNode(String name) {
			this.name = name;
			count = 1;
		}
		public String name;
		public int count;
		public boolean equals(PathNode other) {
			return (name.equals(other.name) && count == other.count);
		}
		public String toString() {
			String s = name;
			if(count > 1) {
				s += '['+Integer.toString(count)+']';
			}
			return s;
		}
	}
	Map<String, PathNode> closeNodes = new HashMap<String, PathNode>();
	List<PathNode> path = new ArrayList<PathNode>();
	List<PathNode> searchPath = new ArrayList<PathNode>();
	String root = "";
	String lastSiblingName;
	
	public void reset() {
		closeNodes.clear();
		lastSiblingName = null;
		path.clear();		
	}
	
	public void setSearchPath(String path) {
		if(path.charAt(0)=='/') {
			root = "/";
			path = path.substring(1);
		}
		StringTokenizer st = new StringTokenizer(path,"/");
		while(st.hasMoreTokens()) {
			String node = st.nextToken();
			PathNode pathNode = new PathNode();
			int i = node.indexOf('[');
			if(i > 0) {
				int k = node.indexOf(']');
				pathNode.name = node.substring(0,i);
				pathNode.count = Integer.parseInt(node.substring(i+1,k));
			} else {
				pathNode.name = node;
				pathNode.count = 1;
			}
			searchPath.add(pathNode);
		}
		reset();
	}

	public String buildPath(List<Node> nodes) {
		reset();
		// A. End nodes array?
		for(Node node: nodes) {
			// 1. read node
			String nodeName = node.getName();
			// B. close node?
			if(node.isNodeComplete()) {
				// D. Is the first close node?
				if(closeNodes.containsKey(nodeName)) {
					// 5. update count
					closeNodes.get(nodeName).count++;
				} else {
					// 6. save node info
					closeNodes.put(nodeName, new PathNode(nodeName));
				}
				lastSiblingName = nodeName;
			} else {
				PathNode pathNode = new PathNode(nodeName);
				// C. Has close siblings
				if(closeNodes.containsKey(nodeName)) {
					pathNode.count += closeNodes.get(nodeName).count;
				}
				closeNodes.clear();
				lastSiblingName = null;
				path.add(pathNode);
				// G. node on search path
				if(!isValidPath()) {
					return getPathValue(path);
				}
			}
		}
		// E. <close node map> empty?
		if(closeNodes.size() > 0) {
			path.add(closeNodes.get(lastSiblingName));
		}
		return getPathValue(path);
	}
	
	private boolean isValidPath() {
		if(path.size() > searchPath.size()) {
			return false;
		}
		for(int i=0; i<searchPath.size(); i++) {
			if(i<path.size()) {
				PathNode pNode = path.get(i);
				if(!pNode.equals(searchPath.get(i))) {
					return false;
				}
			}
		}
		return true;
	}
	private String getPathValue(List<PathNode> path) {
		String s = root;
		for(int i=0; i<path.size(); i++) {
			PathNode pathNode = path.get(i);
			if(i > 0) {
				s += '/';
			}
			s += pathNode.toString();
		}
		return s;
	}
	String getSearchPathValue() {
		return getPathValue(this.searchPath);
	}
	
	String getPathValue() {
		return getPathValue(this.path);
	}
	
	List<PathNode> getPath() {
		return path;
	}

	List<PathNode> getSearchPath() {
		return searchPath;
	}

}
