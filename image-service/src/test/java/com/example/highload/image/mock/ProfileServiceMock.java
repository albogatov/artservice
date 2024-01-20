package com.example.highload.image.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

@TestConfiguration
public class ProfileServiceMock {
    public static void setupMockGetById(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/api/profile/core/single/1"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(
                                copyToString(
                                        ProfileServiceMock.class.getClassLoader().
                                                getResourceAsStream("payload/profileDto.json"),
                                        defaultCharset()))));
    }

    public static void setupMockSetNewMain(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/api/profile/core/single/1/image"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(
                                copyToString(
                                        ProfileServiceMock.class.getClassLoader().
                                                getResourceAsStream("payload/imageDto.json"),
                                        defaultCharset()))));
    }

}
