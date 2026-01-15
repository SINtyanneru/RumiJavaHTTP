package su.rumishistem.rumi_java_http.Type;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;

public class Request {
	private ChannelHandlerContext ctx;
	private Method method;
	private String path;
	private Map<String, String> url_param;
	private ByteArrayOutputStream body;

	public Request(ChannelHandlerContext ctx, Method method, String path, Map<String, String> url_param, ByteArrayOutputStream body) {
		this.ctx = ctx;
		this.method = method;
		this.path = path;
		this.url_param = url_param;
		this.body = body;
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
	public StreamingWriter get_streaming(int code, String mime_type) {
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
	 * リクエストボディーを取得します。
	 * @return body
	 */
	public RequestBody get_body() {
		return new RequestBody(body);
	}
}
