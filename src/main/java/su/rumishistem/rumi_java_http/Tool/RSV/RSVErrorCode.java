package su.rumishistem.rumi_java_http.Tool.RSV;

import java.util.LinkedHashMap;

import su.rumishistem.rsdf_java.RSDFEncoder;
import su.rumishistem.rumi_java_http.Type.Response;
import su.rumishistem.rumi_java_http.Type.MimeType.StandardMimeType;

public class RSVErrorCode {
	public static Response bad_request(String trace) {
		return new Response(400, gen("0x4000", "仕様通りではないリクエスト", trace), StandardMimeType.Application.RSDF);
	}

	public static Response husei_request(String trace) {
		return new Response(401, gen("0x4001", "不正なリクエスト", trace), StandardMimeType.Application.RSDF);
	}

	public static Response unauthorized_request(String trace) {
		return new Response(401, gen("0x4002", "認証エラー", trace), StandardMimeType.Application.RSDF);
	}

	public static Response permission_request(String trace) {
		return new Response(401, gen("0x4006", "権限エラー", trace), StandardMimeType.Application.RSDF);
	}

	public static Response conflict_request(String trace) {
		return new Response(409, gen("0x4003", "リクエストが衝突した", trace), StandardMimeType.Application.RSDF);
	}

	public static Response contents_not_found_request(String trace) {
		return new Response(404, gen("0x4004", " リクエストに応じれるコンテンツがない", trace), StandardMimeType.Application.RSDF);
	}

	public static Response endpoint_not_found_request(String trace) {
		return new Response(404, gen("0x4005", " エンドポイントが存在しない", trace), StandardMimeType.Application.RSDF);
	}

	public static Response overflow(String trace) {
		return new Response(413, gen("0x4007", "  オーバーフロー", trace), StandardMimeType.Application.RSDF);
	}

	public static Response system_error(String trace) {
		return new Response(500, gen("0x5000", "システムエラー", trace), StandardMimeType.Application.RSDF);
	}

	public static Response smtp_server_error(String trace) {
		return new Response(521, gen("0x6001", "SMTP間通信エラー", trace), StandardMimeType.Application.RSDF);
	}

	public static Response activitypub_server_error(String trace) {
		return new Response(521, gen("0x6002", "ActivityPub間通信エラー", trace), StandardMimeType.Application.RSDF);
	}

	public static Response renkei_server_error(String trace) {
		return new Response(521, gen("0x6003", "連携通信エラー", trace), StandardMimeType.Application.RSDF);
	}

	private static byte[] gen(String error_code, String message, String trace) {
		LinkedHashMap<String, Object> r = new LinkedHashMap<String, Object>();
		r.put("STATUS", false);
		r.put("ERROR", new LinkedHashMap<String, String>(){
			{
				put("CODE", error_code);
				put("MESSAGE", message);
				put("TRACE", trace);
			}
		});

		try {
			return RSDFEncoder.encode(r);
		} catch (Exception ex) {
			throw new RuntimeException("");
		}
	}
}
