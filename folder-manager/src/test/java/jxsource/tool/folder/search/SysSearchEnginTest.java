package jxsource.tool.folder.search;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.everyItem;
import java.io.File;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;

import jxsource.tool.folder.search.action.CollectionAction;
import jxsource.tool.folder.search.filter.ExtFilter;
import jxsource.tool.folder.search.filter.Filter;
import jxsource.tool.folder.search.filter.PathFilter;
import jxsource.tool.folder.search.hamcrestMatcher.MatcherFactory;
import jxsource.tool.folder.search.hamcrestMatcher.IncludeStringMatcher;

public class SysSearchEnginTest {
	private Matcher<JFile> hasExt(final String exts) {
		   return new BaseMatcher<JFile>() {
		      public boolean matches(final Object item) {
		         final JFile f = (JFile) item;
		         return exts.indexOf(f.getExt()) >= 0 || f.isDirectory();
		      }
		   
		      public void describeTo(final Description description) {
		         description.appendText("getName should return ").appendValue(exts);
		      }
		      public void describeMismatch(final Object item, final
		Description description) {
		         description.appendText("was").appendValue(((JFile) item).getName());
		     }
		   };
		}

	@Test
	public void test() {
		String root = ".";
		SysSearchEngin engin = new SysSearchEngin();
		CollectionAction ca = new CollectionAction();
		ca.setUrl(root);
		engin.addAction(ca);
		engin.setFilter(new ExtFilter("java, class"));
		engin.search(new File(root));
		assertThat(root, is(ca.getUrl()));
		List<JFile> files = ca.getFiles();
		assertThat(files, everyItem(hasProperty("ext", MatcherFactory.createIncludeStringMatcher("java, class"))));
		for(JFile f: files) {
			assertThat(f, hasExt("java, class"));			
		}
	}
	
	@Test
	public void includeFilterTest() {
		String root = ".";
		SysSearchEngin engin = new SysSearchEngin();
		CollectionAction ca = new CollectionAction();
		ca.setUrl(root);
		engin.addAction(ca);
		engin.setFilter(new PathFilter("**/src"));
		engin.search(new File(root));
		assertThat(root, is(ca.getUrl()));
		List<JFile> files = ca.getFiles();
		assertThat(files, everyItem(hasProperty("path", MatcherFactory.createIncludeStringMatcher("src"))));
	}

	@Test
	public void excludeFilterTest() {
		String root = ".";
		SysSearchEngin engin = new SysSearchEngin();
		CollectionAction ca = new CollectionAction();
		ca.setUrl(root);
		engin.addAction(ca);
		Filter filter = new PathFilter("**/src");
		filter.setExclude(true);
		engin.setFilter(filter);
		engin.search(new File(root));
		assertThat(root, is(ca.getUrl()));
		List<JFile> files = ca.getFiles();
		assertThat(files, everyItem(hasProperty("path", MatcherFactory.createExcludeStringMatcher("src"))));
	}


}
