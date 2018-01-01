package jxsource.util.io.record;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LineRecordReaderTest {
	 
	public static void main(String[] args) {
		try {
			InputStream in = new FileInputStream("src/test/resource/sample.txt");
			LineRecordReader lrr = new LineRecordReader(in);
			LineRecord lr;
			RangFilter filter = new RangFilter(1,6);
			TestFilter t = new TestFilter();
			ChainFilter ch = new ChainFilter(new RecordFilter[] {filter,t});
			while((lr=lrr.getNext(ch))!=null) {
				System.out.println(lr);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
