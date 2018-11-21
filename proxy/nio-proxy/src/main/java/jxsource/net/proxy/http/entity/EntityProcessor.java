package jxsource.net.proxy.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import jxsource.net.proxy.Constants;
import jxsource.net.proxy.DefaultTransferQueue;
import jxsource.net.proxy.TransferQueue;
import jxsource.net.proxy.http.HttpConstants;
import jxsource.net.proxy.http.HttpHeaderUtils;
import jxsource.net.proxy.http.exception.EntityException;
import jxsource.net.proxy.http.exception.MessageHeaderException;
import jxsource.util.bytearray.ByteArray;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.log4j.Logger;

public interface EntityProcessor {
	final byte[] CRLF = ByteArray.CRLF;
	public EntityStatus processEntity(HttpMessage message, SocketChannel from, EntityDestinationChannel to) throws IOException;
}
