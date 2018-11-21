package jxsource.net.proxy.http.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DownloadRegistry {
	private static DownloadRegistry me;
	
	private Set<String> downloadUrls = Collections.synchronizedSet(new HashSet<String>());

	private DownloadRegistry() {}
	
	public static DownloadRegistry getInstance() {
		if(me == null) {
			me = new DownloadRegistry();
		}
		return me;
	}
	
	public void add(String url) {
		downloadUrls.add(url);
	}
	public boolean contains(String url) {
		return downloadUrls.contains(url);
	}
}
