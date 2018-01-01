package jxsource.util.io.record;

public class RangFilter implements RecordFilter<Record> {

	private int start;
	private int end;
	private int pos;
	
	/*
	 * include start and end
	 */
	public RangFilter(int start, int end) {
		this.start = start;
		this.end = end;
		pos = 0;
	}
	public boolean accept(Record record) {
		pos++;
		if(pos >= start && pos <= end) {
			return true;
		} else {
			return false;
		}
	}

}
