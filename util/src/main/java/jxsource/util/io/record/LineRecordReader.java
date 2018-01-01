package jxsource.util.io.record;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LineRecordReader extends RecordReader<LineRecord>{
	private BufferedReader reader;
	public LineRecordReader(InputStream in) {
		reader = new BufferedReader(new InputStreamReader(in));
	}
	@Override
	public LineRecord getNext() throws IOException{
		String line = reader.readLine();
		if(line != null) {
			return new LineRecord(line);
		} else {
			return null;
		}
	}
	@Override
	public void close() {
		try {
			reader.close();
		} catch(IOException e) {}
	}
}
