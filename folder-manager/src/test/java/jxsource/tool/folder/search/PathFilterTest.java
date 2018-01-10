package jxsource.tool.folder.search;

import java.io.File;
import java.util.List;

import org.junit.Test;

import jxsource.tool.folder.search.action.CollectionAction;
import jxsource.tool.folder.search.filter.Filter;
import jxsource.tool.folder.search.filter.PathFilter;
import jxsource.tool.folder.search.filter.PathMatcher;
import jxsource.tool.folder.search.hamcrestMatcher.MatcherFactory;
import jxsource.tool.folder.search.match.Match;
import jxsource.tool.folder.search.match.MatchFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
//	public void noFilterTest() {
//		String root = "./target";
//		SysSearchEngin engin = new SysSearchEngin();
//		CollectionAction ca = new CollectionAction();
//		ca.setUrl(root);
//		engin.addAction(ca);
//		engin.setFilter(new PathFilter("**/*.xml,*.java"));
//		engin.search(new File(root));
//		assertThat(root, is(ca.getUrl()));
//		List<JFile> files = ca.getFiles();
//		// every item has property "ext" which may have value "java" or "class"
//		assertThat(files, everyItem(hasProperty("ext", MatcherFactory.createStringOrMatcher("java, xml"))));
//	}

//	@Test
//	public void testNoAccept() {
//		Match[] matches = MatchFactory.createPathMatch("**/*.java, *.xml");
//		SysFile f = new SysFile(new File("./src"));
//		PathMatcher pfp = new PathMatcher(matches);
//		assertThat(pfp.start(f), is(Filter.ACCEPT));
//	}


}
