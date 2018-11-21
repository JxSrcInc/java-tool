package jxsource.net.bridge.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.net.proxy.http.entity.EntityModifier;
//import jxsource.util.string.RegexMatcher;

public class HttpsBridgeEntityModifier extends EntityModifier{
	private static Logger logger = Logger.getLogger(HttpsBridgeEntityModifier.class);
	private HttpHeaderUtils headerUtils = new HttpHeaderUtils();
	private BridgeContext bridgeContext;
	private String regex = "https://([\\w-]+\\.?)+(:\\d+)*";
	private Pattern p = Pattern.compile(regex);
//	private RegexMatcher matcher = new RegexMatcher();
	private BridgeFileManager bridgeFileManager = new BridgeFileManager();
	public HttpsBridgeEntityModifier() {
		// TODO Auto-generated constructor stub
	}
	public void setBridgeContext(BridgeContext bridgeContext) {
		this.bridgeContext = bridgeContext;
	}
	@Override
	protected byte[] updateEntity(byte[] entityBytes, HttpResponse response) {
		HttpRequest request = BridgeContextHolder.get().getRemoteRequest();
		headerUtils.setHttpMessage(response);
		String srcFilename = bridgeFileManager.getBridgeFilename(request, response, "src");
		bridgeFileManager.save(srcFilename, entityBytes);
		ContentType contentType = headerUtils.getContentType();
		// Is response's contentType appliable?
		// Determine the Charset (or unspecified) and 
		// create the String value of the entityData.
		String src = new String(entityBytes);
		byte[] modifiedBytes = update(src).getBytes();
		String modifiedFilename = bridgeFileManager.getBridgeFilename(request, response, "modified");
		bridgeFileManager.save(modifiedFilename, modifiedBytes);
		return modifiedBytes;
	}
	
	public String update(String src) {
		 Matcher m = p.matcher(src);
		 String newStr = "";
		 int pos = 0;
		 int last = 0;
		 if(m.lookingAt()) {
			 newStr = substitute(src, pos, 0, m.end(), newStr);	
			 pos = m.end();
		 }
		 while(m.find()) {
			 newStr = substitute(src, pos, m.start() , m.end(), newStr);
			 pos = m.end();
		 }
		 newStr += src.substring(pos);
		 return newStr;
	}
	
	private String substitute(String src, int init, int start, int end, String target) {
		if(start > init) {
			target += src.substring(init, start);
		}
		System.err.println(this.getClass()+": "+src.substring(start, end));
		String s = src.substring(start+8, end);
		if(s.indexOf(':') != -1) {
			s.replaceFirst(":", BridgeContext.symbolPort);
		}
		String bridge = "http://"+BridgeContext.prefixBridge+"https"+
				BridgeContext.symbolHost+s+BridgeContext.suffixBridge;
		System.err.println(this.getClass()+": "+bridge);
		target += bridge;
		return target;
	}


}
