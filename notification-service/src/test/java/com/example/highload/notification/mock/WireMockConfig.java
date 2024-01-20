package com.example.highload.notification.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
public class WireMockConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockLoginService() {
        return new WireMockServer(4765);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockUserService() {
        return new WireMockServer(4764);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockProfileService() {
        return new WireMockServer(4763);
    }
}
