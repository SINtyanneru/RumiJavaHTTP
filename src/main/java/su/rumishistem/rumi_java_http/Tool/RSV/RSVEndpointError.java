package su.rumishistem.rumi_java_http.Tool.RSV;

import su.rumishistem.rumi_java_http.Type.Request;
import su.rumishistem.rumi_java_http.Type.Response;
import su.rumishistem.rumi_java_http.Type.RouteEntry;

public class RSVEndpointError implements RouteEntry{
	@Override
	public Response run(Request r) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(r.get_ex().toString());
		sb.append("\n");

		for (StackTraceElement e:r.get_ex().getStackTrace()) {
			if (e.getClassName().startsWith("su.rumishistem.rumi_java_http.ServerHandler")) {
				break;
			}

			sb.append("\t");
			sb.append(e.toString());
			sb.append("\n");
		}

		return RSVErrorCode.system_error(sb.toString());
	}
}
