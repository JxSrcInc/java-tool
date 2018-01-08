package jxsource.tool.folder.search.match;

public class LikeMatch extends Match{

	public boolean match(String nodeName, String math) {
		return nodeName.contains(match);
	}

}
