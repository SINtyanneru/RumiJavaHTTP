package su.rumishistem.rumi_java_http.Type;

import su.rumishistem.rumi_java_http.Type.MimeType.MimeType;

public class Response {
	public int code;
	public byte[] body;
	public MimeType mime_type;

	public Response(int code, byte[] body, MimeType mime_type) {
		this.code = code;
		this.body = body;
		this.mime_type = mime_type;
	}
}
