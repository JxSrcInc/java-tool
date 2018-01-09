package jxsource.tool.folder.search.match;

public class MultiMatch extends Match{

	Match[] matches;
	
	public Match[] getMatches() {
		return matches;
	}
	@Override
	void init(String match, boolean ignoreCase) {
		String[] array = match.split(",");
		matches = new Match[array.length];
		for(int i=0; i<matches.length; i++) {
			matches[i] = MatchFactory.createMatch(array[i].trim(), ignoreCase);
		}
	}

	@Override
	public boolean match(String nodeName) {
		for(Match match: matches) {
			if(match.match(nodeName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean match(String nodeName, String match) {
		// not use.
		return false;
	}

}
