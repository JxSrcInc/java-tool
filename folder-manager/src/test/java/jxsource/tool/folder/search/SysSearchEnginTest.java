package jxsource.tool.folder.search;

import java.io.File;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import jxsource.tool.folder.search.action.CollectionAction;
import jxsource.tool.folder.search.action.FilePrintAction;
import jxsource.tool.folder.search.filter.ExtFilter;

public class SysSearchEnginTest {
//	public static Matcher<JFile> fileExt(Matcher<? super String> matcher) {
//	    return new FeatureMatcher<JFile, String>(matcher, "a String of length that", "length") {
//	        @Override
//	        protected boolean featureValueOf(JFile actual) {
//	            return actual.getName();
//	        }
//	    };
//	}
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
		System.out.println(ca);
		assertThat(root, is(ca.getUrl()));
		List<JFile> files = ca.getFiles();
		for(JFile f: files) {
			assertThat(f, hasExt("java, class"));			
		}
	}

}
