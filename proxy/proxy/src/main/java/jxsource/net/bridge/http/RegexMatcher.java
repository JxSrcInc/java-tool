	package jxsource.net.bridge.http;

import java.util.List;

import jxsource.util.search.streamsearch.Index;
import jxsource.util.search.streamsearch.IndexResult;
import jxsource.util.search.streamsearch.RegexIndex;
import jxsource.util.search.streamsearch.RegexIndexFactory;

public class RegexMatcher {

	private RegexMatchReplace replace;
	private RegexIndexFactory factory = new RegexIndexFactory();
	
	public void setReplace(RegexMatchReplace replace) {
		this.replace = replace;
		factory.setReservedCacheSize(400);
		factory.addSearchContent(replace.getRegex());
	}

	public String matchAndReplace(String src) {
		factory.reset();
//		factory.setIn(new ByteArrayInputStream(src.getBytes()));
		IndexResult<char[]> result = null;
		StringBuilder str = new StringBuilder();
//		while ((result=factory.createIndex(src)) != null) {
		result=factory.createIndex(src);
			List<Index<char[]>> list = result.getList();
			int start = 0;
			for(Index<char[]> index: list) {
				int indexPos = (int)index.getPos();
				str.append(src.substring(start, indexPos));
				str.append(replace.replace(((RegexIndex)index).getStringContent()));
				start = indexPos+index.getLength();
			}
			if(start < src.length())
				str.append(src.substring(start));
//		}
	return str.toString();
	}
}
