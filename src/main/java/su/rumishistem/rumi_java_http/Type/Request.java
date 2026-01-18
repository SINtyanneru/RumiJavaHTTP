package su.rumishistem.rumi_java_http.Type;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import su.rumishistem.rumi_java_http.Type.MimeType.MimeType;

public class Request {
	private ChannelHandlerContext ctx;
	private Method method;
	private String path;
	private Map<String, String> url_param;
	private ByteArrayOutputStream body;
	private Map<String, String> param;
	private Exception ex;
	private Map<String, String> header_list;

	public Request(ChannelHandlerContext ctx, Method method, String path, Map<String, String> url_param, ByteArrayOutputStream body, Map<String, String> param, Map<String, String> header_list) {
		this.ctx = ctx;
		this.method = method;
		this.path = path;
		this.url_param = url_param;
		this.body = body;
		this.param = param;
		this.header_list = header_list;
	}

	public void set_param(Map<String, String> param) {
		this.param = param;
	}

	public void set_ex(Exception ex) {
		this.ex = ex;
	}

	/**
	 * メソッドを取得します
	 * @return メソッド
	 */
	public Method get_method() {
		return method;
	}

	/**
	 * パスを返します
	 * @return パス
	 */
	public String get_path() {
		return path;
	}

	/**
	 * ストリーミングで応答します。
	 * 「「「絶対にreturn null;しろ。」」」
	 * @return
	 */
	public StreamingWriter get_streaming(int code, MimeType mime_type) {
		return new StreamingWriter(ctx, code, mime_type);
	}

	/**
	 * URLパラメーターを取得します。
	 * @param key キー
	 * @return 値
	 */
	public String get_url_param(String key) {
		return url_param.get(key);
	}

	/**
	 * パラメーターを取得します。
	 * @param key キー
	 * @return 値
	 */
	public String get_param(String key) {
		return param.get(key);
	}

	/**
	 * リクエストボディーを取得します。
	 * @return body
	 */
	public RequestBody get_body() {
		return new RequestBody(body);
	}

	public Exception get_ex() {
		return ex;
	}

	/**
	 * ヘッダーを取得します
	 * @param key キー
	 * @return 値
	 */
	public String get_header(String key) {
		return header_list.get(key.toUpperCase());
	}
}
