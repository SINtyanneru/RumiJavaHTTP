package su.rumishistem.rumi_java_http.Tool;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionToString {
	public static String get(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		return sw.toString();
	}
}
