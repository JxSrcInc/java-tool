package jxsource.util.io.record;

import static org.junit.Assert.*;
import org.junit.Test;

public class RecordColTest {

	class Value {

	}

	int size = 100;
	TestRecord tr = new TestRecord(0, "id", new Value());
	LineRecord lr = new LineRecord("lineRecord");
	Runnable rLr = new Runnable() {
		public void run() {
			for (int i = 0; i < size; i++) {
				System.out.println("lr: "+i+","+lr.toString());
				try {
					Thread.sleep(10);
				} catch (Exception e) {}
			}
		}
	};
	Runnable rTr = new Runnable() {
		public void run() {
			for (int i = 0; i < size; i++) {
				System.out.println("tr: "+i+","+tr.toString());
				try {
					Thread.sleep(10);
				} catch (Exception e) {}
			}
		}
	};

	@Test
	public void test() {
		Thread tt = new Thread(rTr);
		Thread tl = new Thread(rLr);
		tt.start();
		tl.start();
		try {
			tl.join();
			tt.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
