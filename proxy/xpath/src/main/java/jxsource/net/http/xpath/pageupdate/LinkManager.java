package jxsource.net.http.xpath.pageupdate;

import java.util.HashMap;
import java.util.Map;

public class LinkManager {
	
	private static LinkManager mgr;
	// key: id, value: host
	private Map<String,byte[]> links = new HashMap<String,byte[]>();
	private int id = 0;
	
	private LinkManager() {
		
	}
	
	public static LinkManager getInstance() {
		if(mgr == null) {
			mgr = new LinkManager();
		}
		return mgr;
	}

	public String createLink(byte[] src) {
		String link = Integer.toString(id++);
		links.put(link,src);
		return link;
	}
	
	public byte[] getLink(String link) {
		return links.get(link);
	}
}
