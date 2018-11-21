package jxsource.net.bridge.http;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class BridgeToRemoteConverterTest {

	RegexMatcher matcher;

	@Before
	public void init() {
		matcher = new RegexMatcher();
	}
	@Test
	public void testHost() throws IOException {
		matcher.setReplace(new BridgeToRemoteHostConverter());
		String result = matcher.matchAndReplace("bridge__https__host__www.google.com__bridge");
		assertTrue(result.equals("www.google.com"));
		result = matcher.matchAndReplace("bridge__https__host__www.google.com__bridge/asdf die bridge__https__host__www.as.s-d.com__port__324__bridge");
		assertTrue(result.equals("www.google.com/asdf die www.as.s-d.com:324"));
		 
	}
	@Test
	public void testHttps() throws IOException {
		matcher.setReplace(new BridgeToRemoteHttpsConverter());
		String result = matcher.matchAndReplace("bridge__https__host__www.google.com__bridge");
		assertTrue(result.equals("https://www.google.com"));
		result = matcher.matchAndReplace("bridge__https__host__www.google.com__bridge/asdf die bridge__https__host__www.as.s-d.com__port__324__bridge");
		assertTrue(result.equals("https://www.google.com/asdf die https://www.as.s-d.com:324"));
	}
	@Test
	public void testHttpHost() throws IOException {
		matcher.setReplace(new HttpBridgeToRemoteHttpsConverter());
		String src = "/VSTAG?LOG=1&VSVERSION=VS01.01&VSLID=http%3A//bridge__https__host__login.fidelity.com__bridge/ftgw/Fas/Fidelity/RtlCust/Login/Init%3FAuthRedUrl%3Dhttp%3A//bridge__https__host__oltx.fidelity.com__bridge/ftgw/fbc/ofsummary/defaultPage&VSMODULE=login&VSDESC=CustomerLogIn&VSEVENT=CallToAction&VSPAGE=HomePage&VSPGVER=VM_PROSPECT_B&VSVID=0.1519557593907418 [Host: www.fidelity.com, User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:37.0) Gecko/20100101 Firefox/37.0, Accept: image/png,image/*;q=0.8,*/*;q=0.5, Accept-Language: en-US,en;q=0.5, Accept-Encoding: gzip, deflate, Referer: https://www.fidelity.com/, Cookie: v1st=659E08FC8DA5BC27; MC=bFkFU0aAXL1JSG7sdKMVWzJQKwwSAlUXX7gKAiiHIAAqQgAVqjMGBAAKADIGBVVqbhMAAAAAAAAAAAAAAAAAP03; s_vi=[CS]v1|294A63838507B271-6000010B400852FA[CE]; PZN=\"E,M=eef23384a0d6b811dd99af9bc8b53daa77\"; s_pers=%20visitStart%3D1422457927125%7C1453993927125%3B%20s_dfa%3Dfidelitycom%7C1427395397148%3B%20gpv_c11%3DFid.com%2520web%257Clogin%257CAccount%2520Postion%2520Login%7C1433040147397%3B; mbox=PC#1427339937097-971257.17_39#1440814347|session#1433036995108-290548#1433040207|check#true#1433038407; cvi=p1=55175fb80a02288720002a420015aa33&p2=&p3=99&p4=&p5=&p6=&p7=55175fb80a02288720002a420015aa33&p8=; s_sess=%20s_cc%3Dtrue%3B%20s_sq%3D%3B; JSESSIONID=D1EE4708E2038475F648D7A9BDDA9E45, Connection: keep-alive]";
		String result = matcher.matchAndReplace(src);
		src = src.replace("http%3A//bridge__https__host__login.fidelity.com__bridge", "https://login.fidelity.com");
		src = src.replace("http%3A//bridge__https__host__oltx.fidelity.com__bridge", "https://oltx.fidelity.com");
		assertTrue(result.equals(src));
	}

}
