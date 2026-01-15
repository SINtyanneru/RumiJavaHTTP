package su.rumishistem.rumi_java_http.Type;

import io.netty.channel.ChannelHandlerContext;

public class Request {
	private ChannelHandlerContext ctx;
	private String path;

	public Request(ChannelHandlerContext ctx, String path) {
		this.ctx = ctx;
		this.path = path;
	}

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
}
