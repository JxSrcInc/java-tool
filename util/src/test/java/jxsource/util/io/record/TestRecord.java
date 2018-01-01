package jxsource.util.io.record;

public class TestRecord extends Record<Object>{
	private static String[] testRecordColNames = new String[] {"Id", "Name", "Value"};

	public TestRecord(int id, String name, Object value) {
		colNames = testRecordColNames;
		values = new Object[] {id, name, value};
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return values[0]+","+values[1]+","+values[2];
	}

}
