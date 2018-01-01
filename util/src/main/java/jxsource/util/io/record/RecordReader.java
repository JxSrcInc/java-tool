package jxsource.util.io.record;

import java.io.IOException;

public abstract class RecordReader<T extends Record> {
	
	public abstract T getNext() throws IOException;
	public abstract void close();
	public T getNext(RecordFilter filter) throws IOException{
		T record;
		while((record = getNext()) != null) {
			if(filter.accept(record)) {
				return record;
			}
		}
		return null;
	}
	
}
