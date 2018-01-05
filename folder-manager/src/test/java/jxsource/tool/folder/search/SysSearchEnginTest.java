package jxsource.tool.folder.search.template;

import static org.junit.Assert.assertNotNull;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.tool.folder.search.filter.ExtFilter;
import jxsource.tool.folder.search.filter.FullNameFilter;
import jxsource.tool.folder.search.filter.NameFilter;

public class ZipSearchTemplateTest {
	private Logger log = LoggerFactory.getLogger(ZipSearchTemplateTest.class);
	ZipReportAssert zipReportAssert;
	
	@Before
	public void init() {
		zipReportAssert = new ZipReportAssert();
	}
	@Test
	@Ignore
	public void builderTest() {
		assertNotNull(ZipSearchTemplate.getBuilder());
	}
	
	@Test
	public void defaultTemplateTest() {
		ZipSearchTemplate zst = ZipSearchTemplate.getBuilder()
				.setZipReport(zipReportAssert).build();
		zst.run();
	}
	
	@Test
	public void extFilterTest() {
		ZipSearchTemplate zst = ZipSearchTemplate.getBuilder()
			.setZipFilter(new ExtFilter("class"))
			.setZipReport(zipReportAssert.setExt("class"))
			.build();
		zst.run();		
	}
	@Test
	public void fullnameFilterTest() {
		ZipSearchTemplate zst = ZipSearchTemplate.getBuilder()
				.setZipFilter(new FullNameFilter().add("Filter.class"))
				.setZipReport(zipReportAssert.setName("Filter")) // ZipReportAssert removes extension from name 
				.build();
			zst.run();		
	}
	@Test
	public void nameFilterTest() {
		ZipSearchTemplate zst = ZipSearchTemplate.getBuilder()
				.setZipFilter(new NameFilter().add("Filter"))
				.setZipReport(zipReportAssert.setName("Filter"))
				.build();
			zst.run();		
	}
	@Test
	public void ignoreCaseNameFilterTest() {
		ZipSearchTemplate zst = ZipSearchTemplate.getBuilder()
				.setZipFilter(new NameFilter().add("filter").setIgnoreCase(true))
				.setZipReport(zipReportAssert.setName("Filter"))
				.build();
			zst.run();		
	}	
	@Test
	public void likeNameFilterTest() {
		ZipSearchTemplate zst = ZipSearchTemplate.getBuilder()
				.setZipFilter(new NameFilter().add("Template").setLike(true))
				.setZipReport(zipReportAssert.setName("Template, ZipSearchTemplate, ZipSearchTemplateTest"))
				.build();
			zst.run();		
		log.error(zipReportAssert.getFound().toString());
		// found more than checked.
		assertThat(zipReportAssert.getFound().size(), greaterThanOrEqualTo(3));
	}	
	@Test
	public void pathFilterTest() {
		ZipSearchTemplate zst = ZipSearchTemplate.getBuilder()
				.setZipFilter(new NameFilter().add("Template").setLike(true))
				.setZipReport(zipReportAssert.setName("Template, ZipSearchTemplate, ZipSearchTemplateTest"))
				.build();
			zst.run();		
		log.debug(zipReportAssert.getFound().toString());
		// found more than checked.
		assertThat(zipReportAssert.getFound().size(), greaterThanOrEqualTo(3));
	}		
	
}
