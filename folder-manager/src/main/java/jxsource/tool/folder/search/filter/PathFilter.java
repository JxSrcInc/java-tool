package jxsource.tool.folder.search.filter;

import jxsource.tool.folder.search.JFile;
import jxsource.tool.folder.search.match.Match;
import jxsource.tool.folder.search.match.MatchFactory;

public class PathFilter extends Filter{
	private Match[] matches;

	public PathFilter(String pathMatch) {
		matches = MatchFactory.createPathMatch(pathMatch);
	}
	@Override
	public int _accept(JFile file) {
		PathFilterProcessor pfp = new PathFilterProcessor(matches);
		return pfp.start(file);
	}

}
