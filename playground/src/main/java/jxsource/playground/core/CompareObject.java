package jxsource.playground.core;

public class CompareObject {

	class A {
		String str;
		public A(String str) {
			this.str = str;
		}
	}
	class B {
		String str;
		public B(String str) {
			this.str = str;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
//			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((str == null) ? 0 : str.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			B other = (B) obj;
//			if (!getOuterType().equals(other.getOuterType()))
//				return false;
			if (str == null) {
				if (other.str != null)
					return false;
			} else if (!str.equals(other.str))
				return false;
			return true;
		}
		private CompareObject getOuterType() {
			return CompareObject.this;
		}
		
	}
	
	class C {
		String str;
		public C(String str) {
			this.str = str;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			C other = (C) obj;
//			if (!getOuterType().equals(other.getOuterType()))
//				return false;
			if (str == null) {
				if (other.str != null)
					return false;
			} else if (!str.equals(other.str))
				return false;
			return true;
		}
		private CompareObject getOuterType() {
			return CompareObject.this;
		}
		
	}

	public static void main(String...args) {
		A a = new CompareObject().new A("A");
		A b = new CompareObject().new A("A");
		System.out.println(a.equals(b));
		B c = new CompareObject().new B("A");
		B d = new CompareObject().new B("A");
		System.out.println(c.equals(d));
		C e = new CompareObject().new C("A");
		C f = new CompareObject().new C("A");
		System.out.println(e.hashCode()+","+f.hashCode()+","+e.equals(f));
	}
}
