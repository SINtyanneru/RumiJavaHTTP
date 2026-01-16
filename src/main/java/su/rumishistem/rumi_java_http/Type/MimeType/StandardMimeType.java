package su.rumishistem.rumi_java_http.Type.MimeType;

import java.util.HashMap;

public class StandardMimeType {
	public class Application {
		public static final MimeType OctetStream = new MimeType("application", "octet-stream", new HashMap<>());
		public static final MimeType ZIP = new MimeType("application", "zip", new HashMap<>());
		public static final MimeType PDF = new MimeType("application", "pdf", new HashMap<>());

		public static final MimeType JSON = new MimeType("application", "json", new HashMap<>() {{ put("charset", "UTF-8"); }});
		public static final MimeType RSDF = new MimeType("application", "rsdf", new HashMap<>());
	}

	/**
	 * UTF-8
	 */
	public class Text {
		public static final MimeType Plain = new MimeType("text", "plain", new HashMap<>() {{ put("charset", "UTF-8"); }});
		public static final MimeType HTML = new MimeType("text", "html", new HashMap<>() {{ put("charset", "UTF-8"); }});
	}

	public class Image {
		public static final MimeType PNG = new MimeType("image", "png", new HashMap<>());
		public static final MimeType JPEG = new MimeType("image", "jpeg", new HashMap<>());
		public static final MimeType SVG = new MimeType("image", "svg+xml", new HashMap<>());
	}

	public class Video {
		public static final MimeType MP4 = new MimeType("video", "mp4", new HashMap<>());
	}
}
