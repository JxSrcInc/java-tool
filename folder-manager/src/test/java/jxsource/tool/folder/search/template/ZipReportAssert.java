package jxsource.tool.folder.search.template;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import jxsource.tool.folder.search.JFile;
import jxsource.tool.folder.search.util.ZipReport;
import jxsource.tool.folder.search.util.Util;

import static org.junit.Assert.*;

public class ZipReportAssert extends ZipReport {
	private static Logger log = LogManager.getLogger(ZipReportAssert.class);
	private String url;
	private String[] paths;
	private String[] names;
	private String[] exts;
	private List<String> found = new ArrayList<String>();
	
	private Matcher<String> contains(final String[] matchs) {
		return new BaseMatcher<String>() {
			public boolean matches(final Object obj) {
				String src = (String) obj;
				for (String match : matchs) {
						if(src.contains(match)) {
							found.add(src);
							return true;
						}
				}
				return false;
			}

			public void describeTo(final Description description) {
				description.appendText("contains should return ").appendValue(matchs);
			}
		};
	}

	public List<String> getFound() {
		return found;
	}
	public ZipReportAssert setPath(String multiPaths) {
		paths = Util.toArray(multiPaths);
		return this;
	}

	public ZipReportAssert setName(String multiNames) {
		names = Util.toArray(multiNames);
		return this;
	}

	public ZipReportAssert setExt(String multiExts) {
		exts = Util.toArray(multiExts);
		return this;
	}

	@Override
	public void report(String url, List<JFile> extractFiles) {
//		assertTrue(extractFiles.size() > 0);
		log.debug("find "+extractFiles.size()+" entries in "+url);
		for (JFile f : extractFiles) {
			if (exts != null) {
				assertThat(f.getExt(), contains(exts));
			}
			if (names != null) {
				// don't verify extension
				String name = f.getName();
				int i = name.indexOf('.');
				if (i > 0) {
					name = name.substring(0, i);
				}
				assertThat(name, contains(names));
			}
		}
	}

}
