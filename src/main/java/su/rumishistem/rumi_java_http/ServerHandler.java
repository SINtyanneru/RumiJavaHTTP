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
import su.rumishistem.rumi_java_http.Tool.ExceptionToString;
import su.rumishistem.rumi_java_http.Type.*;
import su.rumishistem.rumi_java_http.Type.MimeType.StandardMimeType;

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

			Map<String, String> url_param = new HashMap<>();
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
				request_body,
				null
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

				//ルートを見る
				for (Entry<RoutePath, RouteEntry> entry:rjh.get_route_table().entrySet()) {
					RouteResult result = entry.getKey().check(current_request.get_path(), current_request.get_method());
					if (result.success) {
						exists_route = true;
						current_request.set_param(result.param);
						run_entry(entry.getValue(), ctx);
						break;
					}
				}

				//404
				if (!exists_route) {
					run_entry(get_error_page_entry(ErrorCode.PageNotFound, current_request.get_path()), ctx);
				}

				reset();
			}
		}
	}

	private void run_entry(RouteEntry entry, ChannelHandlerContext ctx) {
		Response route_response;

		try {
			route_response = entry.run(current_request);
		} catch (Exception ex) {
			//500
			try {
				current_request.set_ex(ex);
				route_response = get_error_page_entry(ErrorCode.InternalServerError, current_request.get_path()).run(current_request);
			} catch (Exception ex2) {
				//500自体がエラーを吐いた場合
				route_response = new RouteEntry() {
					@Override
					public Response run(Request r) {
						return new Response(500, "500 Internal Server Error(RJH)".getBytes(), StandardMimeType.Text.Plain);
					}
				}.run(current_request);
			}
		}

		if (route_response == null) return;

		FullHttpResponse response = new DefaultFullHttpResponse(
			HttpVersion.HTTP_1_1,
			HttpResponseStatus.valueOf(route_response.code),
			ctx.alloc().buffer().writeBytes(route_response.body)
		);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, route_response.mime_type.get_as_mimetype());
		response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		ctx.writeAndFlush(response);
	}

	private RouteEntry get_error_page_entry(ErrorCode error, String path) {
		if (rjh.get_error_table(error) != null) {
			for (Entry<String, RouteEntry> row:rjh.get_error_table(error)) {
				if (path.startsWith(row.getKey())) {
					return row.getValue();
				}
			}
		}

		switch (error) {
			case InternalServerError:
				return new RouteEntry() {
					@Override
					public Response run(Request r) {
						return new Response(500, ("500 Internal Server Error\n" + ExceptionToString.get(r.get_ex())).getBytes(), StandardMimeType.Text.Plain);
					}
				};
			case PageNotFound:
				return new RouteEntry() {
					@Override
					public Response run(Request r) {
						return new Response(404, "404 Page Not Found".getBytes(), StandardMimeType.Text.Plain);
					}
				};
			default:
				return new RouteEntry() {
					@Override
					public Response run(Request r) {
						return new Response(500, "えらー".getBytes(), StandardMimeType.Text.Plain);
					}
				};
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
