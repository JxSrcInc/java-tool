package jxsource.tool.folder.search.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
//import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;

import jxsource.tool.folder.search.JFile;
import jxsource.tool.folder.search.hamcrestMatcher.MatcherFactory;
import jxsource.tool.folder.search.match.AnyMatch;
import jxsource.tool.folder.search.match.Match;
import jxsource.tool.folder.search.match.MatchFactory;
import jxsource.tool.folder.search.match.MultiMatch;
import jxsource.tool.folder.search.match.RegexMatch;

public class MatchTest {

	@Test
	public void multiMatchTest() {
		Match[] match = MatchFactory.createPathMatch("**/folder/*.java, *.class");
		assertThat(match, arrayWithSize(3));
		assertThat(match[0], instanceOf(AnyMatch.class));
		assertThat(match[1], instanceOf(RegexMatch.class));
		assertThat(match[2], instanceOf(MultiMatch.class));
		MultiMatch multiMatch = (MultiMatch) match[2];
		assertThat(Arrays.asList(multiMatch.getMatches()), everyItem(hasProperty("match", MatcherFactory.createStringOrMatcher(".java, .class"))));
		assertThat(Arrays.asList(multiMatch.getMatches()), everyItem(hasProperty("match", MatcherFactory.createStringOrMatcher(".java, .class, extra"))));
		try {
			assertThat(Arrays.asList(multiMatch.getMatches()), everyItem(hasProperty("match", MatcherFactory.createStringOrMatcher(".java"))));
		} catch(AssertionError e) {
			// TODO: handle Throwable
			//e.printStackTrace();
		}
	}
}
