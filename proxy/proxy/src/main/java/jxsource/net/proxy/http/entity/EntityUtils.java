package jxsource.net.proxy.http.entity;

public class EntityUtils {

	public static String convertUrlToFilename(String url) {
		return url.replaceAll("[/:?=#]", "_").substring(0, Math.min(url.length(), 100));
	}

	public static void main(String[] args) {
		System.out.println(EntityUtils.convertUrlToFilename("http://www.asdf.com:985/kks/iesc?a=bid#ids"));
	}
}
