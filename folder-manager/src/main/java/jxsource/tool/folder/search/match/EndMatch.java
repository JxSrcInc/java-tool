package jxsource.tool.folder.search.match;

public class EndMatch extends Match{

	public boolean match(String nodeName, String math) {
		return nodeName.endsWith(match);
	}

}
