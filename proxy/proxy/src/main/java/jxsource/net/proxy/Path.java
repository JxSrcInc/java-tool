package jxsource.net.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
/*
 * The root, the first char of path is '/' can be handled 
 * by using constuctor Path(String, boolean) with second parameter is true
 * 
 * The process will automatically detect the first char of path and
 * if it is '/', then root property will be set
 * 
 * If root, then '/' will always add to the returned toString()
 * no matter if which segment is removed.
 * 
 */
public class Path {
	private int currIndex;
	private List<String> segments = new ArrayList<String>();
	private String root = "";
	public Path(String path, boolean bRoot) {
		this(path);
		if(bRoot) {
			if(path.charAt(0)=='/') {
				root = "/";
			}
		}
	}
	public Path(String path) {
		StringTokenizer st = new StringTokenizer(path,"/");
		while(st.hasMoreTokens()) {
			segments.add(st.nextToken());
		}
	}
	
	public final List<String> getSegments() {
		return segments;
	}
	
	public String getSegment(int i) {
		return segments.get(i);
	}
	public String getNextSegment() {
		return getSegment(currIndex++);
	}
	public boolean hasNextSegment() {
		return (currIndex < segments.size());
	}
	public int length() {
		return segments.size();
	}
	/*
	 * NOTE: it is caller's responsibility to add '/' at the beginning or ending of toString() method
	 */
	@Override
	public String toString() {
		String path = root;
		for(int i=0; i<segments.size(); i++) {
			if(i > 0) {
				path += "/";
			}
			path += segments.get(i);
		}
		return path;
	}
	
	public int find(String segment) {
		for(int i=0; i<segments.size(); i++) {
			String s = segments.get(i);
			if(s.equals(segment)) {
				return i;
			}
		}
		return -1;
	}
	/*
	 * return the first match
	 */
	public int findStartWith(String segment) {
		for(int i=0; i<segments.size(); i++) {
			String s = segments.get(i);
			if(s.indexOf(segment) == 0) {
				return i;
			}
		}
		return -1;
	}

	public String remove(String segment) {
		int i = find(segment);
		if(i != -1) {
			return remove(i);
		} else {
			return null;
		}
	}
	public String removeStartWith(String segment) {
		int i = findStartWith(segment);
		if(i != -1) {
			return remove(i);
		} else {
			return null;
		}
	}
	
	private String remove(int i) {
		return segments.remove(i);
	}

}
