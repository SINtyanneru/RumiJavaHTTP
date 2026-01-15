package su.rumishistem.rumi_java_http.Type;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoutePath {
	private String path = null;

	private boolean is_regex = false;
	private Pattern regex = null;
	private List<String> param_name_list = null;

	public RoutePath(Pattern regex, List<String> param_name_list) {
		this.is_regex = true;
		this.regex = regex;
		this.param_name_list = param_name_list;
	}

	public RoutePath(String path) {
		this.path = path;
	}

	public RouteResult check(String request_path) {
		if (!request_path.startsWith("/")) request_path = "/" + request_path;
		if (request_path.endsWith("/")) request_path = request_path.substring(0, request_path.length() - 1);

		RouteResult result = new RouteResult();

		if (is_regex) {
			Matcher mtc = regex.matcher(request_path);
			result.param = new HashMap<>();

			if (mtc.find()) {
				result.success = true;

				for (int i = 0; i < param_name_list.size(); i++) {
					String key = param_name_list.get(i);
					String value = mtc.group(i + 1);
					result.param.put(key, value);
				}
			} else {
				result.success = false;
			}
		} else {
			result.success = path.equals(request_path);
			result.param = new HashMap<>();
		}

		return result;
	}
}
