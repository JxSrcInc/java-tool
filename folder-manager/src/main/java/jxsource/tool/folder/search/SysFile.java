package jxsource.tool.folder.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SysFile extends JFile{
	private static Logger log = LogManager.getLogger(SysFile.class);
	private File file;
	private InputStream in;
	public SysFile(File file) {
		setPath(file.getPath());
		setLength(file.length());
		setDirectory(file.isDirectory());
		this.file = file;
	}
	@Override
	public InputStream getInputStream() throws IOException {
		return (in = new FileInputStream(file));
	}
	@Override
	public void close() {
		if(in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("Error when closing "+file.getPath());
			} finally {
				in = null;
			}
		}
	}
	@Override
	protected void finalize() throws Throwable {
		close();
		file = null;
		super.finalize();
	}
	
}
