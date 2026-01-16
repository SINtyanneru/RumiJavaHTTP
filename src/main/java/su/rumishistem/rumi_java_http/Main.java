package su.rumishistem.rumi_java_http;

import su.rumishistem.rumi_java_http.Type.Method;
import su.rumishistem.rumi_java_http.Type.Request;
import su.rumishistem.rumi_java_http.Type.Response;
import su.rumishistem.rumi_java_http.Type.RouteEntry;
import su.rumishistem.rumi_java_http.Type.StreamingWriter;
import su.rumishistem.rumi_java_http.Type.MimeType.StandardMimeType;

/**
 * デバッグ用
 */
public class Main {
	public static void main(String[] args) throws InterruptedException {
		RumiJavaHTTP http = new RumiJavaHTTP(8080);

		http.set_route("/", null, new RouteEntry() {
			@Override
			public Response run(Request r) {
				StringBuilder sb = new StringBuilder();
				sb.append("〜〜〜〜RumiJavaHTTPライブラリ〜〜〜〜\n");
				sb.append("/					：これ\n");
				sb.append("/streaming		：ストリーミングテスト\n");
				sb.append("/post				：POSTテスト\n");

				return new Response(200, sb.toString().getBytes(), StandardMimeType.Text.Plain);
			}
		});

		http.set_route("/streaming", null, new RouteEntry() {
			@Override
			public Response run(Request r) {
				StreamingWriter s = r.get_streaming(200, StandardMimeType.Text.Plain);
				s.write("ストリームテスト\n".getBytes());
				s.write("にゃんにゃんこゃーんこにゃーん\n".getBytes());
				s.close();
				return null;
			}
		});

		http.set_route("/post", Method.GET, new RouteEntry() {
			@Override
			public Response run(Request r) {
				StringBuilder sb = new StringBuilder();
				sb.append("<!DOCTYPE html>");
				sb.append("<HTML>");
				sb.append("	<HEAD>");
				sb.append("		<TITLE>POSTのテスト</TITLE>");
				sb.append("	</HEAD>");
				sb.append("	<BODY>");
				sb.append("		<FORM METHOD=\"POST\" ACTION=\"/post\">");
				sb.append("			<INPUT TYPE=\"TEXT\" NAME=\"NAME\" PLACEHOLDER=\"名前\"><BR>");
				sb.append("			<TEXTAREA NAME=\"FOX\" PLACEHOLDER=\"ひとこと\"></TEXTAREA><BR>");
				sb.append("			<BUTTON>送信</BUTTON>");
				sb.append("		</FORM>");
				sb.append("	</BODY>");
				sb.append("</HTML>");

				return new Response(200, sb.toString().getBytes(), StandardMimeType.Text.HTML);
			}
		});

		http.set_route("/post", Method.POST, new RouteEntry() {
			@Override
			public Response run(Request r) {
				return new Response(200, ("あなたの送ったデータ↓\n" + r.get_body().get_as_string()).getBytes(), StandardMimeType.Text.Plain);
			}
		});

		http.start();
	}
}
