package su.rumishistem.rumi_java_http;

import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import su.rumishistem.rumi_java_http.Type.*;

public class ServerHandler extends ChannelInboundHandlerAdapter{
	private RumiJavaHTTP rjh;
	private Request current_request;
	private ByteArrayOutputStream request_body;

	public ServerHandler(RumiJavaHTTP rjh) {
		this.rjh = rjh;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			//ヘッダー
			HttpRequest r = (HttpRequest)msg;
			System.out.println("[ INFO ] HTTP Request: " + r.uri());

			Map<String, String> url_param = new HashMap();
			request_body = new ByteArrayOutputStream();

			//メソッド
			HttpMethod netty_method = r.method();
			Method method = Method.GET;
			switch (netty_method.asciiName().toUpperCase().toString()) {
				case "GET":
					method = Method.GET;
					break;
				case "DELETE":
					method = Method.DELETE;
					break;
				case "POST":
					method = Method.POST;
					break;
				case "PATCH":
					method = Method.PATCH;
					break;
				default:
					throw new UnsupportedOperationException(netty_method.name());
			}

			//URL
			String path = r.uri();
			int param_index = path.indexOf('?');
			if (param_index >= 0) {
				String up = path.substring(param_index + 1);
				path = path.substring(0, param_index);

				//パラメーター
				for (String param:up.split("&")) {
					int index = param.indexOf('=');
					String key = URLDecoder.decode(param.substring(0, index), StandardCharsets.UTF_8);
					String value = URLDecoder.decode(param.substring(index + 1), StandardCharsets.UTF_8);
					url_param.put(key, value);
				}
			}

			current_request = new Request(
				ctx,
				method,
				path,
				url_param,
				request_body
			);
		} else if (msg instanceof HttpContent) {
			//ボディー
			HttpContent content = (HttpContent) msg;

			if (request_body.size() > rjh.get_request_body_limit()) {
				//TODO:サイズ超過
			}

			ByteBuf buffer = content.content();
			byte[] body = new byte[buffer.readableBytes()];
			buffer.readBytes(body);
			request_body.write(body);

			if (msg instanceof LastHttpContent) {
				//終了
				boolean exists_route = false;
				Response route_response = null;

				//ルートを見る
				for (Entry<RoutePath, RouteEntry> entry:rjh.get_route_table().entrySet()) {
					RouteResult result = entry.getKey().check(current_request.get_path(), current_request.get_method());
					if (result.success) {
						exists_route = true;
						route_response = entry.getValue().run(current_request);
						break;
					}
				}

				//ルートはあったｋか
				if (exists_route) {
					//応答はあるか(なければ放置)
					if (route_response == null) return;

					FullHttpResponse response = new DefaultFullHttpResponse(
						HttpVersion.HTTP_1_1,
						HttpResponseStatus.valueOf(route_response.code),
						ctx.alloc().buffer().writeBytes(route_response.body)
					);
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, route_response.mime_type);
					response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
					ctx.writeAndFlush(response);
				} else {
					//404
				}

				reset();
			}
		}
	}

	private void reset() {
		current_request = null;
		request_body = null;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
