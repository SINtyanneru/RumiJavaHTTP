package su.rumishistem.rumi_java_http.Type;

public class Response {
	public int code;
	public byte[] body;
	public String mime_type;

	public Response(int code, byte[] body, String mime_type) {
		this.code = code;
		this.body = body;
		this.mime_type = mime_type;
	}
}
