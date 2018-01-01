package jxsource.util.io.record;

public class LineRecord extends Record<String>{
	private static String[] lineRecordColNames = new String[] {"Line"};

	public LineRecord(String value) {
		colNames = lineRecordColNames;
		values = new String[] {value};
	}

	@Override
	public String getValue(int i) {
		// TODO Auto-generated method stub
		return (String)super.getValue(i);
	}

	@Override
	public String getValue(String colName) {
		// TODO Auto-generated method stub
		return (String)super.getValue(colName);
	}
	
	@Override
	public String toString() {
		return getValue(0);
	}
}
