package org.apache.coyote.http11.headers;

public enum MimeType {

	HTML("text/html;charset=utf-8"),
	CSS("text/css;"),
	JS("text/css;");

	private final String value;

	MimeType(final String value) {
		this.value = value;
	}

	public static MimeType parseEndpoint(final String requestEndPoint) {
		int index = requestEndPoint.indexOf(".");
		final String fileExtension = requestEndPoint.substring(index + 1);
		return MimeType.valueOf(fileExtension.toUpperCase());
	}

	public String getValue() {
		return value;
	}
}
