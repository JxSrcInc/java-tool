package jxsource.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileInputStreamFactory {

	private long skipSize;
	public final String filename;
	private boolean loadNewContent;
	
	public FileInputStreamFactory(String filename) {
		this.filename = filename;
	}
	public void setLoadNewContent(boolean loadNewContent) {
		this.loadNewContent = loadNewContent;
	}
	public long getSkipSize() {
		return skipSize;
	}
	public InputStream getFileInputStream() throws IOException {
		File f = new File(filename);
		FileInputStream in = new FileInputStream(f);
		if(loadNewContent) {
			in.skip(skipSize);
			skipSize = f.length();
		}
		return in;
	}
}
