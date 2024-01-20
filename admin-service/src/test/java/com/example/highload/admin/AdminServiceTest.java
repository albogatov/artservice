package com.example.highload.admin;

import com.example.highload.admin.feign.LoginServiceFeignClient;
import com.example.highload.admin.feign.UserServiceFeignClient;
import com.example.highload.admin.mock.LoginServiceMock;
import com.example.highload.admin.mock.UserServiceMock;
import com.example.highload.admin.mock.WireMockConfig;
import com.example.highload.admin.model.enums.RoleType;
import com.example.highload.admin.model.network.UserDto;
import com.example.highload.admin.repos.RoleRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@WireMockTest
@Testcontainers
@EnableConfigurationProperties
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = { WireMockConfig.class, LoginServiceMock.class, UserServiceMock.class})
public class AdminServiceTest {

    @LocalServerPort
    private Integer port;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private WireMockServer mockLoginService;

    @Autowired
    private WireMockServer mockUserService;

    @Autowired
    private LoginServiceMock loginServiceMock;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LoginServiceFeignClient loginServiceFeignClient;

    @Autowired
    private UserServiceFeignClient userServiceFeignClient;

    private static final String adminLogin = "admin1";
    private static final String adminPassword = "admin1";
    private static final String adminRole = "ADMIN";
    private static final String artistLogin = "artist1";
    private static final String artistPassword = "artist1";
    private static final String artistRole = "ARTIST";
    private static final String clientLogin = "client1";
    private static final String clientPassword = "client1";
    private static final String clientRole = "CLIENT";
    private static final String newClientLogin = "client2";
    private static final String newClientPassword = "client2";


    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("highload")
            .withUsername("high_user")
            .withPassword("high_user");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @BeforeAll
    static void pgStart() {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() throws IOException {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.defaultParser = Parser.JSON;
        LoginServiceMock.setupMockValidateResponse(mockLoginService);
        LoginServiceMock.setupMockGetLoginResponse(mockLoginService);
        UserServiceMock.setupMockLoginResponse(mockUserService);
        UserServiceMock.setupMockLoginAdminResponse(mockUserService);
        UserServiceMock.setupMockSaveUserResponse(mockUserService);
    }

    @AfterAll
    static void pgStop() {
        postgreSQLContainer.stop();
    }

    @Test
    public void addUser() {

        /*add correct user*/

        UserDto userDto = new UserDto();
        userDto.setLogin("admin_test_client1");
        userDto.setPassword("admin_test_client1");
        userDto.setRole(RoleType.CLIENT);

        String token = tokenProvider("admin1", "ADMIN");

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + token)
                        .header("Content-type", "application/json")
                        .and()
                        .body(userDto)
                        .when()
                        .post("/api/admin/add")
                        .then()
                        .extract();
        Assertions.assertAll(
                () -> Assertions.assertEquals("User added", response1.body().asString()),
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode())
        );
    }

    public String tokenProvider(String login, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(login)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

}

