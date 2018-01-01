package jxsource.util.io.record;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestRecordTest {
	
	class Value {
		
	}
	
	TestRecord tr;
	
	@Before
	public void init() {
		tr = new TestRecord(0, "id", new Value());
	}

	@Test
	public void testCol() {
		TestRecord tr = new TestRecord(0, "id", new Value());
		assertTrue(tr.getColName(0).equals("Id"));
		assertTrue(tr.getColName(1).equals("Name"));
		assertTrue(tr.getColName(2).equals("Value"));
		assertTrue(tr.getColIndex("Id") == 0);
		assertTrue(tr.getColIndex("Name") == 1);
		assertTrue(tr.getColIndex("Value") == 2);
	}
	
	@Test
	public void testValue() {
		TestRecord tr = new TestRecord(0, "id", new Value());
		assertTrue(((Integer)tr.getValue(0)) == 0);
		assertTrue(((String)tr.getValue(1)).equals("id"));
		assertTrue(tr.getValue(2) instanceof Value);		
		assertTrue(((Integer)tr.getValue("Id")) == 0);
		assertTrue(((String)tr.getValue("Name")).equals("id"));
		assertTrue(tr.getValue("Value") instanceof Value);		
	}
	
}
