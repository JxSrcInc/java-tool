package jxsource.tool.folder.search.match;

public class StartMatch extends Match{

	public boolean match(String nodeName, String math) {
		return nodeName.startsWith(match);
	}

}
