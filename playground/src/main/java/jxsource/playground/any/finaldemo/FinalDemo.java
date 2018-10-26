package jxsource.playground.any.finaldemo;

public class FinalDemo {

	final Final getFinal() {
		return new Final();
	}
	public static void main(String...args) {
		FinalDemo demo = new FinalDemo();
		Final f = demo.getFinal();
		f.setValue("new value");
		System.out.println(f.getValue());
		f = new Final();
		System.out.println(f.getValue());
	}
}
