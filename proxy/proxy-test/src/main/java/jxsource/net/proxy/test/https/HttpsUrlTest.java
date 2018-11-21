package jxsource.net.proxy.test.https;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpConnection;

public class HttpsUrlTest {

	public void run() {
		
		try {
			URL url = new URL("https://www.fidelity.com");
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
			
			conn.connect();
			Map<String,List<String>>headers =conn.getHeaderFields();
			for(String name: headers.keySet()) {
				System.out.println("--- "+name);
				for(String value: headers.get(name)) {
					System.out.println("\t"+value);
				}
			}
			InputStream in = url.openStream();
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line=r.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new HttpsUrlTest().run();
	}

}
