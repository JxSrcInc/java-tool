package jxsource.tool.folder.search;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import jxsource.tool.folder.search.action.CollectionAction;
import jxsource.tool.folder.search.filter.ExtFilter;
import jxsource.tool.folder.search.filter.PathFilter;

public class SysSearchEnginTest {
	private Matcher<JFile> hasExt(final String exts) {
		   return new BaseMatcher<JFile>() {
		      public boolean matches(final Object item) {
		         final JFile f = (JFile) item;
		         return exts.indexOf(f.getExt()) >= 0 || f.isDirectory();//i == foo.getNumber();
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
		for(JFile f: files) {
			assertThat(f, hasExt("java, class"));			
		}
	}
	@Test
	public void pathFilterTest() {
		String root = ".";
		SysSearchEngin engin = new SysSearchEngin();
		CollectionAction ca = new CollectionAction();
		ca.setUrl(root);
		engin.addAction(ca);
		engin.setFilter(new PathFilter("**/*.class"));
		engin.search(new File(root));
		assertThat(root, is(ca.getUrl()));
		List<JFile> files = ca.getFiles();
		for(JFile f: files) {
			assertThat(f, hasExt("java"));			
		}
	}

}
