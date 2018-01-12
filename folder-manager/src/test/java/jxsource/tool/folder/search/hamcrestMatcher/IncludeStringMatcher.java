package jxsource.tool.folder.search.hamcrestMatcher;

import java.util.Arrays;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import jxsource.tool.folder.search.JFile;

public class IncludeStringMatcher extends BaseMatcher<String> {

	private String[] matchArray;
	
	public IncludeStringMatcher(String[] matchValue) {
		this.matchArray = matchValue;
	}
    public boolean matches(final Object item) {
    	if(item == null) {
    		return false;
    	}
    	for(String value: matchArray) {
    		if(item.toString().contains(value)) {
    			return true;
    		}
    	}
        return false;
     }
  
     public void describeTo(final Description description) {
        description.appendText("should return ").appendValue(Arrays.asList(matchArray));
     }

}
