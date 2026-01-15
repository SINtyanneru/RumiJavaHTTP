package su.rumishistem.rumi_java_http;

import java.util.*;
import java.util.regex.*;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import su.rumishistem.rumi_java_http.Type.*;

public class RumiJavaHTTP {
	private int port;
	private int request_body_limit = 1024 * 1024;//1MB
	private RumiJavaHTTP rjh;

	private Map<RoutePath, RouteEntry> route_table = new HashMap<>();

	/**
	 * RJLから分離して書き直したHTTPサーバー
	 * @param port ポート番号
	 */
	public RumiJavaHTTP(int port) {
		this.port = port;
		this.rjh = this;
	}

	/**
	 * クライアントから送信するデータの最大サイズを設定します
	 * @param limit 最大サイズ
	 */
	public void set_request_body_limit(int limit) {
		request_body_limit = limit;
	}

	/**
	 * ルーティングの設定をします
	 * @param path パス
	 * @param method メソッド(null OK)
	 * @param entry エントリーポイント
	 */
	public void set_route(String path, Method method, RouteEntry entry) {
		if (!path.startsWith("/")) path = "/" + path;
		if (path.endsWith("/")) path = path.substring(0, path.length() - 1);

		if (path.contains("*") || path.contains(":")) {
			String temp = path;

			//パスを正規表現に変換
			path = path.replace("/", "\\/");
			path = path.replaceAll(":([^/]+)", "([^/]+)");
			path = path.replace("*", "[^/]+");
			Pattern regex = Pattern.compile("^" + path + "$");

			//パラメーター名を記録
			List<String> param_name_list = new ArrayList<>();
			Matcher param_mtc = Pattern.compile(":([^/]+)").matcher(temp);
			while (param_mtc.find()) {
				String param_name = param_mtc.group(1);
				param_name_list.add(param_name);
			}

			route_table.put(new RoutePath(regex, method, param_name_list), entry);
		} else {
			route_table.put(new RoutePath(path, method), entry);
		}
	}

	/**
	 * サーバーを起動します。
	 * @throws InterruptedException 
	 */
	public void start() throws InterruptedException {
		EventLoopGroup boss = new NioEventLoopGroup(1);
		EventLoopGroup worker = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(boss, worker);
			b.channel(NioServerSocketChannel.class);
			b.childHandler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					p.addLast(new HttpServerCodec());
					p.addLast(new ChunkedWriteHandler());
					p.addLast(new ServerHandler(rjh));
				};
			});
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.childOption(ChannelOption.SO_KEEPALIVE, true);

			Channel ch = b.bind(port).sync().channel();
			System.out.println("[ INFO ] Start HTTP Server, Port:" + port);
			ch.closeFuture().sync();
		} finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}

	protected Map<RoutePath, RouteEntry> get_route_table() {
		return route_table;
	}

	protected long get_request_body_limit() {
		return request_body_limit;
	}
}
