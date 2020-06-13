package jxsource.playground.generic;

/*
 * You cannot initialize a generic type variable using new operator
 * in a generic class or generic method.
 * but you can initialize it as null
 */
public class GenericClassAndMethod<E extends A> {
	
	E e;
	
	GenericClassAndMethod() {
		// compile error -> e = new A();
		// compile error -> e = new AA();
		e = null;
	}
	
	E get() {
		// compile error -> return new A();
		// compile error -> return new AA();
		return null;
	}
	
	public <T extends A> T create(final String type) {
		// compile error -> return new A();
		// compile error -> return new AA();
		return null;
	}
}
