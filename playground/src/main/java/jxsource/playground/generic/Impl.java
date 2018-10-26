package jxsource.playground.generic;

public class Impl implements AA{

	public void set(A a) {
		// TODO Auto-generated method stub
		
	}

	public void add(AA aa) {
		// TODO Auto-generated method stub
	}
	
	public static void main(String...args) {
		Impl i = new Impl();
		i.add(i);
		i.set(i);
	}

}
