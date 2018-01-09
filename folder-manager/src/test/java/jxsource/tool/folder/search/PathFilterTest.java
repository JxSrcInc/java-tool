package jxsource.tool.folder.search;

import java.io.File;

import org.junit.Test;

import jxsource.tool.folder.search.filter.Filter;
import jxsource.tool.folder.search.filter.PathMatcher;
import jxsource.tool.folder.search.match.Match;
import jxsource.tool.folder.search.match.MatchFactory;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PathFilterTest {
	@Test
	public void testMatchAccept() {
		Match[] matches = MatchFactory.createPathMatch("**/*.class");
		SysFile f = new SysFile(new File("./target/classes/jxsource/tool/folder/search/JFile.class"));
		PathMatcher pfp = new PathMatcher(matches);
		assertThat(pfp.start(f), is(Filter.ACCEPT));
	}

	@Test
	public void testPass() {
		Match[] matches = MatchFactory.createPathMatch("**/*.class");
		SysFile f = new SysFile(new File("./target/classes/jxsource/tool"));
		PathMatcher pfp = new PathMatcher(matches);
		assertThat(pfp.start(f), is(Filter.PASS));
	}

	@Test
	public void testReject() {
		Match[] matches = MatchFactory.createPathMatch("**/*.java");
		SysFile f = new SysFile(new File("./target/classes/jxsource/tool/folder/search/JFile.class"));
		PathMatcher pfp = new PathMatcher(matches);
		assertThat(pfp.start(f), is(Filter.REJECT));
	}
//	@Test
//	public void testMultiMatchAccept() {
//		Match[] matches = MatchFactory.createPathMatch("**/*.java, *.class");
//		SysFile f = new SysFile(new File(path));
//		PathMatcher pfp = new PathMatcher(matches);
//		assertThat(pfp.start(f), is(Filter.REJECT));
//	}


}
