package jxsource.xml.dom;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import jxsource.xml.io.XmlOutputStream;
import jxsource.xml.parser.Parser;

import org.junit.Test;

public class CharsetTest {

	private byte[] load(String filename) {
		try {
			InputStream in = new FileInputStream(filename);
			byte[] b = new byte[1024*8];
			int k=0;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while((k=in.read(b)) != -1) {
				out.write(b,0,k);
				out.flush();
			}
			out.close();
			in.close();
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	@Test
	public void testDefault() {
		String filename = "src/test/resource/profile.html";
		test(filename, "profile_result");
	}
		
	@Test
	public void testUTF8() {
		String filename = "src/test/resource/chinese.html";
		test(filename, "chinese_result","UTF-8");
	}

	@Test
	public void testGBK() {
		String filename = "src/test/resource/gbk.html";
		test(filename, "gbk_result","GBK");
	}

	
	private void test(String filename, String saveFilename, String...charsetName) {
		byte[] _entityData = null;
		byte[] entityData = load(filename);
		try {
		InputStream in = new ByteArrayInputStream(entityData);
		Parser parser = null;
		XmlOutputStream writer = null;
		if(charsetName.length > 0) {
			parser = new Parser(in, Charset.forName(charsetName[0]));
			writer = new XmlOutputStream(Charset.forName(charsetName[0]));
		} else {
			parser = new Parser(in);
			writer = new XmlOutputStream();
		}
		Document doc = new DocumentBuilder().buildeDoc(parser);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.write(out, doc);
		_entityData = out.toByteArray();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		boolean ok = true;
		if(entityData.length != _entityData.length) {
			save(_entityData, saveFilename);
			assertTrue(false);
			ok = false;
		} else {
			for(int i=0; i<entityData.length; i++) {
				if(entityData[i] != _entityData[i]) {
					save(_entityData, saveFilename);
					assertTrue(false);
					ok = false;
					break;
				}
			}
		}
		if(ok) {
			save(_entityData,saveFilename);
		}
	}

	private void save(byte[] data, String filename) {
		try {
			FileOutputStream out1 = new FileOutputStream("src/test/resource/"+filename+".html");
			out1.write(data);
			out1.flush();
			out1.close();
			} catch(IOException ioe ) {
				ioe.printStackTrace();
			}		
	}
}
