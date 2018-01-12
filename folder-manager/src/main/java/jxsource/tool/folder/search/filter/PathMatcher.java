package jxsource.tool.folder.search.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jxsource.tool.folder.search.JFile;
import jxsource.tool.folder.search.match.AnyMatch;
import jxsource.tool.folder.search.match.Match;

/**
 * A file path converts to String[] nodes
 * Match requirements are given as Match[] matches.
 * Each element of nodes compares with corresponding each element of matches
 * to determine if the file is
 * 1. NotMatch
 * 2. Unknown -- if there is AnyMatch("**") in matches
 * 3. Match
 * 4. UnderMatch
 * 5. OverMatch
 * 
 */
public class PathMatcher {
	public static final int NotMatch = -1;
	public static final int Unknown = 0;
	// match and matches.length == nodes.length
	public static final int Match = 1;
	// match and matches.lennth > nodes.length -- nodes is part of matches
	public static final int UnderMatch = 2;
	// match and matches.length < nodes.length -- nodes contains all elements of matches
	public static final int OverMatch = 3;
	private Logger log = LogManager.getLogger(PathMatcher.class);
	private Match[] matches;
	private String[] nodes;

	public PathMatcher(Match[] matches) {
		
		this.matches = matches;
	}
	public PathMatcher(Match[] matches, String[] nodes) {
		this.matches = matches;
		this.nodes = nodes;
	}

	private boolean endNodes(int nIndex) {
		return nIndex == nodes.length - 1;
	}
	private boolean endMatches(int mIndex) {
		return mIndex == matches.length - 1;
	}
	public int start(JFile file) {
		String path = file.getPath();
		StringTokenizer st = new StringTokenizer(path, "\\");
		List<String> list = new ArrayList<String>();
		while(st.hasMoreTokens()) {
			list.add(st.nextToken());
		}
		nodes = list.toArray(new String[list.size()]);
		switch(recursiveProc(0, 0)) {
		case NotMatch:
			return Filter.REJECT;
		case Unknown:
			if(file.isDirectory()) {
				return Filter.PASS;
			} else {
				return Filter.REJECT;
			}
		case Match:
			return Filter.ACCEPT;
		case UnderMatch:
			// JFile is directory on the match path
			return Filter.PASS;
		case OverMatch:
			// JFile is child (sub-directory or file) of match path 
			return Filter.ACCEPT;
		default:
			throw new RuntimeException("Unknown return value of PathMatch.recursiveProc");
	}
	}
	public int recursiveProc(int mIndex, int nIndex) {
		if (matches[mIndex] instanceof AnyMatch) {
			mIndex++;
			Match match = matches[mIndex];
			String node = nodes[nIndex];
			while (!match.match(node)) {
				if (endNodes(nIndex)) {
					// end of JFile path and no match
					return Unknown;
				} else {
					// match next node
					node = nodes[++nIndex];
				}
			}
		} else 
		if(!matches[mIndex].match(nodes[nIndex])) {
			return NotMatch;
		}
		// Match[mIndex] matches String[nIndex]
		if(!endMatches(mIndex) && !endNodes(nIndex)) {
			return recursiveProc(mIndex+1, nIndex+1);
		} else 
		if(endMatches(mIndex) && endNodes(nIndex)) {
			// final match
			return Match;
		} else
		if(!endMatches(mIndex)) {
			return UnderMatch;
		} else {
			// OverMatch
			return OverMatch;
		}
			
	}
}
