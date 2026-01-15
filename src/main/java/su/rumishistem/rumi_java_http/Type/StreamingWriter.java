package su.rumishistem.rumi_java_http.Type;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;

/**
 * ストリーム
 */
public class StreamingWriter {
	private ChannelHandlerContext ctx;
	private boolean is_close = false;

	/**
	 * ストリーム
	 * @param ctx ctx
	 * @param code ステータスコード
	 * @param mime_type MimeType
	 */
	public StreamingWriter(ChannelHandlerContext ctx, int code, String mime_type) {
		this.ctx = ctx;

		//ヘッダー部分
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(code));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, mime_type);
		response.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
		ctx.write(response);
	}

	/**
	 * ストリームに書き込みます。
	 * @param body 書き込むデータ
	 */
	public void write(byte[] body) {
		if (is_close) {
			throw new IllegalStateException("ストリームは既に閉じています。");
		}

		ByteBuf buffer = ctx.alloc().buffer();
		buffer.writeBytes(body);
		ctx.write(buffer);
	}

	/**
	 * flushします。
	 */
	public void flush() {
		if (is_close) return;
		ctx.flush();
	}

	/**
	 * ストリームを閉じます。
	 * 自動的にflush()されます。
	 */
	public void close() {
		if (is_close) return;
		is_close = true;
		ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
	}
}
