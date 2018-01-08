package jxsource.tool.folder.search.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import jxsource.tool.folder.search.JFile;
import jxsource.tool.folder.search.match.AnyMatch;
import jxsource.tool.folder.search.match.Match;
import jxsource.tool.folder.search.template.ZipSearchTemplateTest;

public class PathFilterProcessor {
	private Logger log = Logger.getLogger(PathFilterProcessor.class);
	private Match[] matches;
	private String[] nodes;

	public PathFilterProcessor(Match[] matches) {
		
		this.matches = matches;
	}
	public PathFilterProcessor(Match[] matches, String[] nodes) {
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
		return recursiveProc(0, 0);
	}
	public int recursiveProc(int mIndex, int nIndex) {
		if (matches[mIndex] instanceof AnyMatch) {
			mIndex++;
			Match match = matches[mIndex];
			String node = nodes[nIndex];
			while (!match.match(node)) {
				if (endNodes(nIndex)) {
					// end of JFile path and no match
					return Filter.REJECT;
				} else {
					// match next node
					node = nodes[++nIndex];
				}
			}
		} else 
		if(!matches[mIndex].match(nodes[nIndex])) {
			return Filter.REJECT;
		}
		// Match[mIndex] matches String[nIndex]
		if(endNodes(nIndex)) {
			if(endMatches(mIndex)) {
				// final match
				log.debug("accept: "+matches[mIndex]+", "+nodes[nIndex]);
				return Filter.ACCEPT;
			} else {
				// JFile is a mid point of match path
				return Filter.PASS;
			}
		} else {
			if(endMatches(mIndex)) {
				// filter path points to directory?
				log.debug("accept: "+matches[mIndex]+", "+nodes[nIndex]);
				return Filter.ACCEPT;
			} else {
				return recursiveProc(mIndex+1, nIndex+1);
			}
		}
	}
}
