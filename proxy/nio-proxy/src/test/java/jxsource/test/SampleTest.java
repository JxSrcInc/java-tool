package jxsource.test;

import java.lang.reflect.Method;

import jxsource.net.proxy.http.HttpUtils;
import jxsource.util.bytearray.ByteArray;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class) 
@PrepareForTest(Logger.class)
public class SampleTest {
	
	@Test
	public void testLogger() throws Exception {
		Logger logger = PowerMockito.mock(Logger.class);
		PowerMockito.mockStatic(Logger.class);
		PowerMockito.when(Logger.getLogger(SampleApp.class)).thenReturn(logger);
		PowerMockito.when(logger.isDebugEnabled()).thenReturn(false);
		SampleApp app = new SampleApp();
		app.run();
	}
}
