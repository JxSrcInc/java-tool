package jxsource.util.io;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public class FileUtil {

	public static char[] loadCharArray(String filename, Charset charset)
			throws IOException {
		return loadCharArray(new File(filename), charset);
	}

	public static char[] loadCharArray(File file, Charset charset)
			throws IOException {
		char[] data = new char[0];
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), charset));
		char[] b = new char[1024 * 8];
		int i = 0;
		while ((i = r.read(b)) != -1) {
			char[] dest = new char[data.length + i];
			System.arraycopy(data, 0, dest, 0, data.length);
			System.arraycopy(b, 0, dest, data.length, i);
			data = dest;
		}
		r.close();
		return data;

	}

	public static char[] loadCharArray(String filename)
			throws IOException {
		return loadCharArray(new File(filename));
	}

	public static char[] loadCharArray(File file)
			throws IOException {
		char[] data = new char[0];
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		char[] b = new char[1024 * 8];
		int i = 0;
		while ((i = r.read(b)) != -1) {
			char[] dest = new char[data.length + i];
			System.arraycopy(data, 0, dest, 0, data.length);
			System.arraycopy(b, 0, dest, data.length, i);
			data = dest;
		}
		r.close();
		return data;

	}

	public static byte[] loadByteArray(String filename) throws IOException {
		return loadByteArray(new File(filename));
	}

	public static byte[] loadByteArray(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		byte[] data = loadByteArray(in);
		in.close();
		return data;
	}

	public static byte[] loadByteArray(URL url) throws IOException {
		InputStream in = url.openStream();
		byte[] data = loadByteArray(in);
		in.close();
		return data;
	}

	public static byte[] loadByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024 * 8];
		int i = 0;
		while ((i = in.read(b)) != -1) {
			out.write(b, 0, i);
			out.flush();
		}
		out.close();
		return out.toByteArray();

	}

}
