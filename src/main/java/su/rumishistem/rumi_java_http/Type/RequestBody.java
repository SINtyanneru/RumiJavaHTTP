package su.rumishistem.rumi_java_http.Type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RequestBody {
	private ByteArrayOutputStream baos;

	public RequestBody(ByteArrayOutputStream baos) {
		this.baos = baos;
	}

	/**
	 * byte[]で全て取得します
	 * @return byte[]
	 */
	public byte[] get_all_byte() {
		byte[] body = baos.toByteArray();

		try {
			baos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return body;
	}

	/**
	 * UTF-8としてbyte[]を解釈し、取得します。
	 * @return String
	 */
	public String get_as_string() {
		return new String(get_all_byte(), StandardCharsets.UTF_8);
	}

	/**
	 * 文字コードを指定してbyte[]を解釈し、取得します。
	 * @param charset 文字コード
	 * @return String
	 */
	public String get_as_string(Charset charset) {
		return new String(get_all_byte(), charset);
	}
}
