package jxsource.net.proxy;

public interface ControllerManager {

	public Controller getController(UrlInfo urlInfo);
	// This function is not really used in application
	// Because application does not need to remove a controller when it is created
	// a controller life cycle will end when application terminates.
	public Controller removeController(UrlInfo urlInfo);
	public int getSize();

}
