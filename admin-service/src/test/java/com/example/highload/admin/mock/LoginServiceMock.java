package com.example.highload.admin.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

@TestConfiguration
public class LoginServiceMock {
    public static void setupMockValidateResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/api/auth/validate"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(
                                copyToString(
                                        LoginServiceMock.class.getClassLoader().
                                                getResourceAsStream("payload/validate.txt"),
                                        defaultCharset()))));
    }

    public static void setupMockErrorValidateResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/api/auth/validate"))
                .willReturn(WireMock.aResponse()
                        .withStatus(503)
                        .withHeader("Content-type", "application/json")
                        .withBody(String.valueOf(Boolean.TRUE))));
    }

    public static void setupMockGetLoginResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/api/auth/get-login-from-token"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", String.valueOf(MediaType.TEXT_PLAIN))
                        .withBody(
                                copyToString(
                                        LoginServiceMock.class.getClassLoader().
                                                getResourceAsStream("payload/getLoginClient.txt"),
                                        defaultCharset()))));
    }

}
