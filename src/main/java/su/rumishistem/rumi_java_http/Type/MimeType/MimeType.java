package su.rumishistem.rumi_java_http.Type.MimeType;

import java.util.Map;
import java.util.Map.Entry;

import su.rumishistem.rumi_java_http.Tool.MimeTypeBuilder;

import java.util.Set;

public class MimeType {
	private String main_type;
	private String sub_type;
	private Map<String, String> args;

	protected MimeType(String main_type, String sub_type, Map<String, String> args) {
		this.main_type = main_type;
		this.sub_type = sub_type;
		this.args = args;
	}

	public static MimeTypeBuilder builder() {
		return new MimeTypeBuilder();
	}

	public String get_as_mimetype() {
		StringBuilder sb = new StringBuilder();
		sb.append(main_type);
		sb.append("/");
		sb.append(sub_type);

		Set<Entry<String, String>> args_set = args.entrySet();
		if (args_set.size() != 0) {
			sb.append(";");
			for (Entry<String, String> arg:args_set) {
				sb.append(arg.getKey());
				sb.append("=");
				sb.append(arg.getValue());
			}
		}

		return sb.toString();
	}
}
