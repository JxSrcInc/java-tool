package jxsource.tool.folder.search;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFile extends JFile{
	private ZipInputStream zis;
	public ZipFile(ZipEntry zipEntry, ZipInputStream zis) {
		setPath(zipEntry.getName());
		setLength(zipEntry.getSize());
		setDirectory(zipEntry.isDirectory());
		this.zis = zis;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return zis;
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	protected void finalize() throws Throwable {
		zis = null;
		super.finalize();
	}
	
}
