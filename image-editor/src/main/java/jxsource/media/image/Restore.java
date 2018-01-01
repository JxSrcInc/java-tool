package jxsource.media.image;

import java.io.File;
import java.io.IOException;

public class Restore {

	public static void main(String[] args) {
			File dir = new File("c:/temp/proxyFiles/jpeg");
			for(String path: dir.list()) {
				if(path.indexOf(".deleted") == path.length()-8) {
					File src = new File(dir, path);
					File dest = new File(dir, path.substring(0, path.length()-8));
					System.out.println("restore: "+src.renameTo(dest)+"\t"+dest);
				}
			}
	}
}
