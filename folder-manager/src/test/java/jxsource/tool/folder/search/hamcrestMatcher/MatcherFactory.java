package jxsource.tool.folder.search.hamcrestMatcher;

import java.util.Arrays;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import jxsource.tool.folder.search.JFile;
import jxsource.tool.folder.search.util.Util;

public class MatcherFactory {
	/**
	 * convert String matchValue to String[] using ',' as separator
	 * 
	 * @param matchValue
	 * @return Matcher<String> which matches any String in the converted String[]
	 */
	public static Matcher<String> createIncludeStringMatcher(String matchValue) {
		String[] array = Util.toArray(matchValue);
		return new IncludeStringMatcher(array);
	}

	/**
	 * convert String matchValue to String[] using ',' as separator
	 * 
	 * @param matchValue
	 * @return Matcher<String> which matches no String in the converted String[]
	 */
	public static Matcher<String> createExcludeStringMatcher(String matchValue) {
		String[] array = Util.toArray(matchValue);
		return new ExcludeStringMatcher(array);
	}

}
