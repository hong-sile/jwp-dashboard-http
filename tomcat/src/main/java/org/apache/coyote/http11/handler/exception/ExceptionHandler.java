package org.apache.coyote.http11.handler.exception;

import java.io.IOException;

import org.apache.coyote.http11.response.HttpResponse;

public interface ExceptionHandler {

	boolean isSupported(final Exception exception);

	HttpResponse handleTo() throws IOException;
}
