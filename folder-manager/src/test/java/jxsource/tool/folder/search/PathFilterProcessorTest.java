package jxsource.tool.folder.search;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;

import org.junit.Test;

import jxsource.tool.folder.search.filter.Filter;
import jxsource.tool.folder.search.filter.PathFilterProcessor;
import jxsource.tool.folder.search.match.Match;
import jxsource.tool.folder.search.match.MatchFactory;

public class PathFilterProcessorTest {

	String path = "./target/classes/jxsource/tool/folder/search/JFile.class";
	@Test
	public void testStringArrayAccept() {
		Match[] matches = MatchFactory.createPathMatch("**/*.class");
		String[] nodes = path.split("/");
		PathFilterProcessor pfp = new PathFilterProcessor(matches, nodes);
		assertThat(pfp.recursiveProc(0, 0), is(Filter.ACCEPT));
	}
	@Test
	public void testIncompletePathReject() {
		Match[] matches = MatchFactory.createPathMatch("**/*.class");
		String[] nodes = "./target/classes/jxsource/tool".split("/");
		PathFilterProcessor pfp = new PathFilterProcessor(matches, nodes);
		assertThat(pfp.recursiveProc(0, 0), is(Filter.REJECT));
	}
	
	@Test
	public void testFile() {
		Match[] matches = MatchFactory.createPathMatch("**/*.class");
		SysFile f = new SysFile(new File(path));
		PathFilterProcessor pfp = new PathFilterProcessor(matches);
		assertThat(pfp.start(f), is(Filter.ACCEPT));
	}

	@Test
	public void testPass() {
		Match[] matches = MatchFactory.createPathMatch("**/tool");
		SysFile f = new SysFile(new File(path));
		PathFilterProcessor pfp = new PathFilterProcessor(matches);
		assertThat(pfp.start(f), is(Filter.ACCEPT));
	}

	@Test
	public void testFileReject() {
		Match[] matches = MatchFactory.createPathMatch("**/*.java");
		SysFile f = new SysFile(new File(path));
		PathFilterProcessor pfp = new PathFilterProcessor(matches);
		assertThat(pfp.start(f), is(Filter.REJECT));
	}
	@Test
	public void testMultiMatchAccept() {
		Match[] matches = MatchFactory.createPathMatch("**/*.java, *.class");
		SysFile f = new SysFile(new File(path));
		PathFilterProcessor pfp = new PathFilterProcessor(matches);
		assertThat(pfp.start(f), is(Filter.REJECT));
	}

}
