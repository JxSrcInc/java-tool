package jxsource.net.http.xpath.pageupdate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import jxsource.xml.dom.Document;
import jxsource.xml.dom.DocumentBuilder;
import jxsource.xml.dom.Element;
import jxsource.xml.dom.Node;
import jxsource.xml.parser.Parser;

import org.apache.log4j.Logger;

public class TextEntityStringFactory {
	private Logger logger = Logger.getLogger(TextEntityStringFactory.class);
	private String[] availableCharsetNames = Constants.DefaultCharsetNamesForText;

	public static TextEntityStringFactory getInstance() {
		return new TextEntityStringFactory();
	}

	// reset String[] availableCharsetNames
	public void setAvailableCharsetNames(String... names) {
		availableCharsetNames = new String[names.length];
		for (int i = 0; i < names.length; i++) {
			availableCharsetNames[i] = names[i];
		}
	}

	// reset String[] availableCharsetNames
	public void loadAllCharsets() {
		availableCharsetNames = Charset.availableCharsets().keySet()
				.toArray(new String[0]);
	}

	/**
	 * HttpEntityString will identify the Charset for the parameter entityBytes
	 * by matching all Charset specified by availableCharsetNames the parameter
	 * charset is null. If no matched, then this.charset is set to null.
	 * 
	 * When Charset is determined, the parameter entityBytes will convert to
	 * String using the determined Charset. If no Charset matched, the
	 * entityBytes will convert to String using "new String(entityBytes)"
	 * constructor.
	 * 
	 * @param charset
	 * @param entityBytes
	 */
	public TextEntityString create(Charset charset, final byte[] entityBytes) {
		TextEntityString textEntityString = new TextEntityString();
		if (charset == null) {
			charset = searchCharset(entityBytes);
			if(charset == null) {
				charset = Charset.forName("UTF-8");
			}
		}
		textEntityString.setCharset(charset);
		String value = new String(entityBytes, charset);
		textEntityString.setValue(value);
		textEntityString.setValid(true);
		byte[] bytes = value.getBytes(charset);
		textEntityString.setEntityBytes(bytes);
		return textEntityString;
	}

	private Charset searchCharset(byte[] entityBytes) {
		Charset charset = null;
		try {
			InputStream in = new ByteArrayInputStream(entityBytes);
			Document doc = new DocumentBuilder().buildeDoc(new Parser(in));
			for (Node node : doc.getNodesByName("meta")) {
				Element meta = (Element) node;
				if (meta.getAttribute("http-equiv") != null 
						&& meta.getAttribute("http-equiv").toLowerCase().equals("content-type")) {
					String contentType = meta.getAttribute("content");
					int i = contentType.indexOf(';');
					String mimeType = contentType;
					charset = null;
					if (i != -1) {
						mimeType = contentType.substring(0, i).trim();
						i = contentType.indexOf('=');
						String charsetName = contentType.substring(i + 1);
						charset = Charset.forName(charsetName);
					}

				}
			}
		} catch (Exception e) {
			logger.debug("Error when determine Charset.", e);			
		}
		return charset;
	}

	private Charset validateCharset(byte[] entityBytes, String charsetName,
			TextEntityString textEntityString) {
		Charset charset = Charset.forName(charsetName);
		return validateCharset(entityBytes, charset, textEntityString);
	}

	/**
	 * Convert byte[] to StringEntity using parameter charset as Charset and
	 * then convert StringEntity to byte[]. If two byte[] arrays are identical,
	 * then charset is a valid Charset for the byte[].
	 * 
	 * @param entityBytes
	 *            byte[]
	 * @param charset
	 *            Charset
	 * @return Charset if charset can correctly convert parameter entityBytes to
	 *         a String, otherwise return null
	 */
	private Charset validateCharset(byte[] entityBytes, Charset charset,
			TextEntityString textEntityString) {
		try {
			textEntityString.setValue(new String(entityBytes, charset));

			byte[] d = textEntityString.getValue().getBytes(charset);
			if (d.length != entityBytes.length) {
				return null;
			} else {
				for (int i = 0; i < d.length; i++) {
					if (d[i] != entityBytes[i]) {
						return null;
					}
				}
				return charset;
			}
		} catch (Exception e) {
			return null;
		}
	}

}
