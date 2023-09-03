package org.apache.coyote.http11.handler;

import static java.nio.file.Files.*;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequest.HttpRequestBuilder;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

	private static final LoginHandler HANDLER = new LoginHandler();

	@Test
	@DisplayName("endpoint가 login이면 true를 반환한다.")
	void isSupported() {
		final String plainRequest = String.join("\r\n",
			"GET /login?account=hong&password=password HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");
		final HttpRequest request = HttpRequestBuilder.from(plainRequest).build();

		final boolean supported = HANDLER.isSupported(request);

		assertThat(supported)
			.isTrue();
	}

	@Nested
	@DisplayName("/login endpoint로 접근 시 처리")
	class HandleTo {

		@Test
		@DisplayName("쿼리파람이 없는 경우 login.html을 반환한다")
		void resolveHtml() throws IOException {
			final String plainRequest = String.join("\r\n",
				"GET /login HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"",
				"");
			final String file = "/login.html";
			final HttpRequest request = HttpRequestBuilder.from(plainRequest).build();

			final HttpResponse actual = HANDLER.handleTo(request);

			final URL resource = getClass().getClassLoader().getResource("static" + file);
			final String expected = "HTTP/1.1 200 OK \r\n" +
				"Content-Type: text/html;charset=utf-8 \r\n" +
				"Content-Length: 3797 \r\n" +
				"\r\n" +
				new String(readAllBytes(new File(resource.getFile()).toPath()));

			assertThat(actual.buildResponse())
				.isEqualTo(expected);
		}

		@Test
		@DisplayName("Post메서드고, 로그인에 성공하는 경우 302 상태코드와 Location index.html을 반환한다.")
		void loginSuccess() {
			final String requestBody = "account=gugu&password=password";
			final String plainRequest = String.join("\r\n",
				"POST /login HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"");
			final HttpRequest request = HttpRequestBuilder.from(plainRequest)
				.body(requestBody)
				.build();

			final HttpResponse actual = HANDLER.handleTo(request);

			final String expected = "HTTP/1.1 302 Found \r\n" +
				"Content-Type: text/html;charset=utf-8 \r\n" +
				"Content-Length: 0 \r\n" +
				"Location: http://localhost:8080/index.html \r\n" +
				"\r\n";

			assertThat(actual.buildResponse())
				.isEqualTo(expected);
		}

		@Test
		@DisplayName("Post 메서드고, 로그인에 실패하는 경우 UnauthorizedException을 반환한다.")
		void loginFail() {
			final String requestBody = "account=gugu&password=invalid";
			final String plainRequest = String.join(System.lineSeparator(),
				"POST /login HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"Content-length: 80 ",
				"");
			final HttpRequest request = HttpRequestBuilder.from(plainRequest)
				.body(requestBody)
				.build();

			assertThatThrownBy(() -> HANDLER.handleTo(request))
				.isInstanceOf(UnauthorizedException.class);
		}
	}
}
