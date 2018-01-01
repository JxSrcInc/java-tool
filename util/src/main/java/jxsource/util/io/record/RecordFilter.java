package jxsource.util.io.record;

public interface RecordFilter<T extends Record> {
	public boolean accept(T record);
}
