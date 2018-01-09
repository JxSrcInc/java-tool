package jxsource.tool.folder.search.match;

public class MatchFactory {
	public static Match createMatch(String value, boolean ignoreCase) {
		Match match;
		if(value.contains(",")) {
			match = new MultiMatch();
			match.init(value, ignoreCase);
		} else
		if(value.equals("**")) {
			match = new AnyMatch();
			match.init(value, ignoreCase);
		} else
		if(value.charAt(0) == '*') {
			if(value.charAt(value.length()-1) == '*') {
				match = new LikeMatch();
				match.init(value.substring(1, value.length()-1), ignoreCase);
			} else {
				match = new EndMatch();
				match.init(value.substring(1), ignoreCase);
			}
		} else 
		if(value.charAt(value.length()-1) == '*') {	
			match = new StartMatch();
			match.init(value.substring(0, value.length()-1), ignoreCase);
		} else {
			match = new RegexMatch();
			match.init(value, ignoreCase);
		}
		return match;
	}
	
	public static Match[] createPathMatch(String pathMatch) {
		return createPathMatch(pathMatch, false);
	}
	public static Match[] createPathMatch(String pathMatch, boolean ignoreCase) {
		String[] segments = pathMatch.split("/");
		Match[] pathMatchArray = new Match[segments.length];
		for(int i=0; i<segments.length; i++) {
			pathMatchArray[i] = createMatch(segments[i], ignoreCase);
		}
		return pathMatchArray;
	}
}
