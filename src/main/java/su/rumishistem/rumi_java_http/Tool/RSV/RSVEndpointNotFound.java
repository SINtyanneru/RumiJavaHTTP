package su.rumishistem.rumi_java_http.Tool.RSV;

import su.rumishistem.rumi_java_http.Type.Request;
import su.rumishistem.rumi_java_http.Type.Response;
import su.rumishistem.rumi_java_http.Type.RouteEntry;

public class RSVEndpointNotFound implements RouteEntry{
	@Override
	public Response run(Request r) throws Exception {
		return RSVErrorCode.endpoint_not_found_request("こん");
	}
}
