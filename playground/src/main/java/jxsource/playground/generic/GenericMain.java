package jxsource.playground.generic;

import java.util.ArrayList;
import java.util.List;

public class GenericMain {

	public static void main(String...args) {
		List<A> aSet = new ArrayList<>();
		A a = new A();
		AA aa = new AA();
		aSet.add(a);
		aSet.add(aa);
		
		List<AA> aaSet = new ArrayList<>();
		// compile error -> aaSet.add(a);
		aaSet.add(aa);
		
		/* Cannot initialize a generic List as <? extends E>
		 * If you initialize the list using <>()
		 * then you will get compile error when adding any element to the list
		 * which is consistent to the first statement. 
		 */
		// compile error -> Set<? extends A> axSet = new HashSet<? extends A>();
		List<? extends A> axSet = new ArrayList<A>();
		// compile error -> axSet.add(a);
		// compile error -> axSet.add(aa);
		
		/*
		 * But you can initialize the generic List with any specific List
		 */
		axSet = aSet;
		System.out.println(axSet.size());
		for(Object o: axSet) {
			System.out.println(o.getClass());
		}
		// compile error ->		axSet.add(a);
		// compile error ->		axSet.add(aa);

		axSet = aaSet;
		System.out.println(axSet.size());
		for(Object o: axSet) {
			System.out.println(o.getClass());
		}
		// compile error ->		axSet.add(a);
		// compile error ->		axSet.add(aa);
}

}
