package jxsource.tool.folder.search;

public abstract class JFile {
	private String name;
	private String path;
	private long length;
	private boolean directory;
	public String getName() {
		return name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
		int i = path.lastIndexOf('\\');
		if(i > 2) {
			name = path.substring(i+1);
		} else {
			name = path;
		}
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public boolean isDirectory() {
		return directory;
	}
	public void setDirectory(boolean directory) {
		this.directory = directory;
	}
	public String getExt() {
		int i = name.lastIndexOf('.');
		return name.substring(i+1);
	}
	@Override
	public String toString() {
		return path + ',' + length;
	}
}
