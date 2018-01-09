package jxsource.tool.folder.search;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import jxsource.tool.folder.search.filter.PathMatcher;
import jxsource.tool.folder.search.match.Match;
import jxsource.tool.folder.search.match.MatchFactory;

public class PathMatcherTest {

	String path = "./target/classes/jxsource/tool/folder/search/JFile.class";
	@Test
	public void testNotMatch() {
		Match[] matches = MatchFactory.createPathMatch("a/b");
		String[] nodes = "b/a".split("/");
		PathMatcher pfp = new PathMatcher(matches, nodes);
		assertThat(pfp.recursiveProc(0, 0), is(PathMatcher.NotMatch));
		nodes = "a/c".split("/");
		pfp = new PathMatcher(matches, nodes);
		assertThat(pfp.recursiveProc(0, 0), is(PathMatcher.NotMatch));
		matches = MatchFactory.createPathMatch("**/a/b");
		nodes = "x/y/z/a/c".split("/");
		pfp = new PathMatcher(matches, nodes);
		assertThat(pfp.recursiveProc(0, 0), is(PathMatcher.NotMatch));
	}
	@Test
	public void testUnknown() {
		Match[] matches = MatchFactory.createPathMatch("**/*.class");
		String[] nodes = "./target/classes/jxsource/tool".split("/");
		PathMatcher pfp = new PathMatcher(matches, nodes);
		assertThat(pfp.recursiveProc(0, 0), is(PathMatcher.Unknown));
	}
	@Test
	public void testMath() {
		Match[] matches = MatchFactory.createPathMatch("**/*.class");
		String[] nodes = path.split("/");
		PathMatcher pfp = new PathMatcher(matches, nodes);
		assertThat(pfp.recursiveProc(0, 0), is(PathMatcher.Match));
	}
	@Test
	public void testUnderMath() {
		Match[] matches = MatchFactory.createPathMatch("**/a/*.class");
		String[] nodes = "x/y/z/a".split("/");
		PathMatcher pfp = new PathMatcher(matches, nodes);
		assertThat(pfp.recursiveProc(0, 0), is(PathMatcher.UnderMatch));
		matches = MatchFactory.createPathMatch("**/a/**/b/*.class");
		nodes = "x/y/z/a/m/n/b".split("/");
		pfp = new PathMatcher(matches, nodes);
		assertThat(pfp.recursiveProc(0, 0), is(PathMatcher.UnderMatch));
	}
	@Test
	public void testOverMath() {
		Match[] matches = MatchFactory.createPathMatch("**/a");
		String[] nodes = "x/y/z/a/*.class".split("/");
		PathMatcher pfp = new PathMatcher(matches, nodes);
		assertThat(pfp.recursiveProc(0, 0), is(PathMatcher.OverMatch));
		matches = MatchFactory.createPathMatch("**/a/**/b");
		nodes = "x/y/z/a/m/n/b/*.class".split("/");
		pfp = new PathMatcher(matches, nodes);
		assertThat(pfp.recursiveProc(0, 0), is(PathMatcher.OverMatch));
	}
	
}
