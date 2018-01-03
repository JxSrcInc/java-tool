package jxsource.tool.folder.search.template;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class ZipSearchTemplateTest {

//	ZipSearchTemplateBuilder builder;
//	@Before
//	public void init() {
//		builder = ZipSearchTemplate.getBuilder();
//	}
	
	@Test
	public void builderTest() {
		assertNotNull(ZipSearchTemplate.getBuilder());
	}
	
	@Test
	public void defaultTemplateTest() {
		ZipSearchTemplate.getBuilder().build().run();
	}
}
