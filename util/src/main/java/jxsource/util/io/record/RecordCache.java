package jxsource.util.io.record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordCache<T extends Record> {

	private List<T> records = new ArrayList<T>();
	
	public void load(RecordReader<T> reader) throws IOException {
		T r;
		while((r=reader.getNext()) != null) {
			records.add(r);
		}
		reader.close();
	}
	public void load(RecordReader<T> reader, RecordFilter<T> filter) throws IOException {
		T r;
		while((r=reader.getNext(filter)) != null) {
			records.add(r);
		}
		reader.close();
	}

}
