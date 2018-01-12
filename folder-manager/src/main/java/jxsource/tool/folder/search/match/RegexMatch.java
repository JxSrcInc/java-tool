package jxsource.tool.folder.search.match;

public class RegexMatch extends Match{

	public boolean match(String nodeName, String math) {
		// it implements as equals right now
		//TODO: need refine to support complete regex
		return nodeName.matches(math);
	}

}
