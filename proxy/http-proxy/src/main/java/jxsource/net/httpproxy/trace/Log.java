package jxsource.net.httpproxy.trace;

public class Log {
	public String name;
	public Object value;
	public Log(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	@Override
	public String toString() {
		return name+"-"+value;
	}
}
