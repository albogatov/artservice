package com.example.highload.image.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
public class WireMockConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockLoginService() {
        return new WireMockServer(80);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockUserService() {
        return new WireMockServer(81);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockOrderService() {
        return new WireMockServer(82);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockProfileService() {
        return new WireMockServer(83);
    }
}