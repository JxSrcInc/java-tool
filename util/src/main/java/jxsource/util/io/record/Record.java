package jxsource.util.io.record;


public abstract class Record<T> {
	// static works here because RecordBase is abstract
	// So static colNames associated to each derived class
	protected String[] colNames;
	protected T[] values;
	
	public String[] getColNames() {
		return colNames;
	}
	public String getColName(int i) {
		return colNames[i];
	}
	public int getColIndex(String colName) {
		for(int i=0; i<colNames.length; i++) {
			if(colNames[i].equals(colName)) {
				return i;
			}
		}
		throw new RuntimeException(this.getClass().getName()+": No colName for "+colName+". ColNames="+printColNames());
	}
	public T[] getValues() {
		return values;
	}
	public T getValue(int i) {
		return values[i];
	}
	public T getValue(String colName) {
		return values[getColIndex(colName)];
	}
	public String printColNames() {
		if(colNames == null) {
			return "null";
		} else {
			String s = "";
			for(String str: colNames) {
				s += '['+str+']';
			}
			return s;
		}
	}
}
