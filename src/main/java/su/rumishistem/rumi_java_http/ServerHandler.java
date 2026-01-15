package su.rumishistem.rumi_java_http;

import java.util.Map.Entry;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import su.rumishistem.rumi_java_http.Type.Request;
import su.rumishistem.rumi_java_http.Type.Response;
import su.rumishistem.rumi_java_http.Type.RouteEntry;
import su.rumishistem.rumi_java_http.Type.RoutePath;
import su.rumishistem.rumi_java_http.Type.RouteResult;

public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
	private RumiJavaHTTP rjh;

	public ServerHandler(RumiJavaHTTP rjh) {
		this.rjh = rjh;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest r) throws Exception {
		String path = r.uri();

		boolean exists_route = false;
		Request http_request = new Request(
			ctx,
			path
		);
		Response route_response = null;

		//ルートを見る
		for (Entry<RoutePath, RouteEntry> entry:rjh.get_route_table().entrySet()) {
			RouteResult result = entry.getKey().check(path);
			if (result.success) {
				exists_route = true;
				route_response = entry.getValue().run(http_request);
				break;
			}
		}

		//ルートはあったｋか
		if (exists_route) {
			//応答はあるか(なければ放置)
			if (route_response == null) return;

			FullHttpResponse response = new DefaultFullHttpResponse(
				r.getProtocolVersion(),
				HttpResponseStatus.valueOf(route_response.code),
				ctx.alloc().buffer().writeBytes(route_response.body)
			);
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, route_response.mime_type);
			response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
			ctx.writeAndFlush(response);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
