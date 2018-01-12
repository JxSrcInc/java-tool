package jxsource.tool.folder.search.template;

import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import jxsource.tool.folder.search.filter.ExtFilter;
import jxsource.tool.folder.search.filter.FullNameFilter;
import jxsource.tool.folder.search.filter.NameFilter;

public class ZipSearchTemplateTest {
	private Logger log = LogManager.getLogger(ZipSearchTemplateTest.class);
	ZipReportAssert zipReportAssert;
	
	@Before
	public void init() {
		zipReportAssert = new ZipReportAssert();
	}
	@Test
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
	@Ignore
	public void fullnameFilterTest() {
		ZipSearchTemplate zst = ZipSearchTemplate.getBuilder()
				.setZipFilter(new FullNameFilter().add("Filter.class"))
				.setZipReport(zipReportAssert.setName("Filter")) // ZipReportAssert removes extension from name 
				.build();
			zst.run();		
	}
	@Test
	@Ignore
	public void nameFilterTest() {
		ZipSearchTemplate zst = ZipSearchTemplate.getBuilder()
				.setZipFilter(new NameFilter().add("Filter"))
				.setZipReport(zipReportAssert.setName("Filter"))
				.build();
			zst.run();		
	}
	@Test
	@Ignore
	public void ignoreCaseNameFilterTest() {
		ZipSearchTemplate zst = ZipSearchTemplate.getBuilder()
				.setZipFilter(new NameFilter().add("filter").setIgnoreCase(true))
				.setZipReport(zipReportAssert.setName("Filter"))
				.build();
			zst.run();		
	}	
	@Test
	@Ignore
	public void likeNameFilterTest() {
		ZipSearchTemplate zst = ZipSearchTemplate.getBuilder()
				.setZipFilter(new NameFilter().add("Template").setLike(true))
				.setZipReport(zipReportAssert.setName("Template, ZipSearchTemplate, ZipSearchTemplateTest"))
				.build();
			zst.run();		
		log.error(zipReportAssert.getFound());
		// found more than checked.
		assertThat(zipReportAssert.getFound().size(), greaterThanOrEqualTo(3));
	}	
	@Test
	@Ignore
	public void pathFilterTest() {
		ZipSearchTemplate zst = ZipSearchTemplate.getBuilder()
				.setZipFilter(new NameFilter().add("Template").setLike(true))
				.setZipReport(zipReportAssert.setName("Template, ZipSearchTemplate, ZipSearchTemplateTest"))
				.build();
			zst.run();		
		log.debug(zipReportAssert.getFound());
		// found more than checked.
		assertThat(zipReportAssert.getFound().size(), greaterThanOrEqualTo(3));
	}		
	
}
