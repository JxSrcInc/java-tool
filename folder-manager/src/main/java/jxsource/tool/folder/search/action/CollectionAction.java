package jxsource.tool.folder.search.action;

import java.util.ArrayList;
import java.util.List;

import jxsource.tool.folder.search.JFile;

public class CollectionAction implements Action{
	private List<JFile> files = new ArrayList<JFile>();
	private String url = "Undefined";
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void proc(JFile f) {
		files.add(f);
	}
	public List<JFile> getFiles() {
		return files;
	}
	@Override
	public String toString() {
		String s = url +'\n';
		for(JFile f: files) {
			s += "\t"+f+'\n';
		}
		return s;
	}
}
