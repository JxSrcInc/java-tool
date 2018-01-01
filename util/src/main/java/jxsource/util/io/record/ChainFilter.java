package jxsource.util.io.record;

public class ChainFilter implements RecordFilter<Record>{
	RecordFilter<Record>[] filters;
	public ChainFilter(RecordFilter<Record>[] filters) {
		this.filters = filters;
	}
	public boolean accept(Record record) {
		for(RecordFilter<Record> filter: filters) {
			if(!filter.accept(record)) {
				return false;
			}
		}
		return true;
	}
}
