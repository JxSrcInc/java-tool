package jxsource.tool.folder.search.template;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import jxsource.tool.folder.search.filter.ExtFilter;
import jxsource.tool.folder.search.filter.FullNameFilter;
import jxsource.tool.folder.search.filter.NameFilter;

public class ZipSearchTemplateTest {

	@Test
	@Ignore
	public void builderTest() {
		assertNotNull(ZipSearchTemplate.getBuilder());
	}
	
	@Test
	public void defaultTemplateTest() {
		ZipSearchTemplate.getBuilder().build().run();
	}
	
	@Test
	@Ignore
	public void zipFilterTest() {
		ZipSearchTemplate.getBuilder().setZipFilter(new ExtFilter("class")).build().run();		
	}
	@Test
	@Ignore
	public void filterNameTest() {
		ZipSearchTemplate.getBuilder().setZipFilter(new FullNameFilter().add("Filter.class")).build().run();				
	}
}
