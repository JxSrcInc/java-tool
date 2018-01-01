package jxsource.util.io.record;

public class TestFilter implements RecordFilter{

	int pos = 0;
	
	public boolean accept(Record record) {
		pos++;
		return pos%2 == 0;
	}

}
