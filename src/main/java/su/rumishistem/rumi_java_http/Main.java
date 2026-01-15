package su.rumishistem.rumi_java_http;

import su.rumishistem.rumi_java_http.Type.Request;
import su.rumishistem.rumi_java_http.Type.Response;
import su.rumishistem.rumi_java_http.Type.RouteEntry;
import su.rumishistem.rumi_java_http.Type.StreamingWriter;

/**
 * デバッグ用
 */
public class Main {
	public static void main(String[] args) throws InterruptedException {
		RumiJavaHTTP http = new RumiJavaHTTP(8080);

		http.set_route("/", new RouteEntry() {
			@Override
			public Response run(Request r) {
				StringBuilder sb = new StringBuilder();
				sb.append("〜〜〜〜RumiJavaHTTPライブラリ〜〜〜〜\n");
				sb.append("/					：これ\n");
				sb.append("/streaming		：ストリーミングテスト\n");

				return new Response(200, sb.toString().getBytes(), "text/plain; charset=UTF-8");
			}
		});

		http.set_route("/streaming", new RouteEntry() {
			@Override
			public Response run(Request r) {
				StreamingWriter s = r.get_streaming(200, "text/plain; charset=UTF-8");
				s.write("ストリームテスト\n".getBytes());
				s.write("にゃんにゃんこゃーんこにゃーん\n".getBytes());
				s.close();
				return null;
			}
		});

		http.start();
	}
}
