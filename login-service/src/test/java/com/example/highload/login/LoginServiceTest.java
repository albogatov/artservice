package com.example.highload.login;

import com.example.highload.login.model.inner.User;
import com.example.highload.login.model.network.JwtRequest;
import com.example.highload.login.model.network.JwtResponse;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetailsFactory;
import org.springframework.boot.ssl.SslBundleRegistry;
import com.example.highload.login.repos.RoleRepository;
import com.example.highload.login.repos.UserRepository;
import com.example.highload.login.security.util.JwtTokenUtil;
import com.example.highload.login.service.LoginService;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

@Testcontainers
@EnableConfigurationProperties
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginServiceTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    public LoginService loginService;
    @Autowired
    public JwtTokenUtil jwtTokenUtil;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public RoleRepository roleRepository;

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
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    static void pgStop() {
        postgreSQLContainer.stop();
    }

    private String getToken(String login, String password, String role) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(new JwtRequest(login, password, role))
                .when()
                .post("/api/auth/login")
                .then()
                .extract().body().as(JwtResponse.class).getToken();
    }

    private ExtractableResponse<Response> getResponse(String postUrl, String login, String password, String role) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(new JwtRequest(login, password, role))
                .when()
                .post(postUrl)
                .then()
                .extract();
    }

    @Test
    @Order(1)
    void authAdminCorrect() {
        String adminJwt = loginService.login(adminLogin, adminPassword, adminRole);
        User user = userRepository.findByLogin(adminLogin).orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(() -> jwtTokenUtil.getLoginFromToken(adminJwt)),
                () -> Assertions.assertEquals(jwtTokenUtil.getLoginFromToken(adminJwt), user.getLogin()),
                () -> Assertions.assertTrue(jwtTokenUtil.getRoleFromJwtToken(adminJwt).contains(user.getRole().getName().toString()))
        );
    }

    @ParameterizedTest
    @MethodSource("loginProvider")
    @Order(2)
    void authAdminBadLogin(String login) {
        Assertions.assertThrows(BadCredentialsException.class, () ->
                loginService.login(login, adminPassword, adminRole)
        );
    }

    @ParameterizedTest
    @MethodSource("passwordProvider")
    @Order(3)
    void authAdminBadPassword(String password) {
        Assertions.assertThrows(BadCredentialsException.class, () ->
                loginService.login(adminLogin, password, adminRole)
        );
    }

    @ParameterizedTest
    @MethodSource("userProvider")
    @Order(4)
    void authRESTCorrect(String login, String password, String role) {
        ExtractableResponse<Response> response = getResponse("/api/auth/login", login, password, role);
        User user = userRepository.findByLogin(login).orElseThrow();
        String tokenResponse = response.body().as(JwtResponse.class).getToken();
        Assertions.assertAll(
                () -> Assertions.assertEquals(jwtTokenUtil.getLoginFromToken(tokenResponse), user.getLogin()),
                () -> Assertions.assertTrue(jwtTokenUtil.getRoleFromJwtToken(tokenResponse).contains(user.getRole().getName().toString())),
                () -> Assertions.assertEquals(response.response().getStatusCode(), HttpStatus.OK.value())
        );
    }

    @ParameterizedTest
    @MethodSource("userProvider")
    @Order(5)
    void authRESTBad(String login, String password, String role) {
        ExtractableResponse<Response> response = getResponse("/api/auth/login", login + "1", password, role);
        Assertions.assertEquals(response.response().getStatusCode(), HttpStatus.UNAUTHORIZED.value());
    }

    private static Stream<String> loginProvider() {
        return Stream.of(
                clientLogin,
                artistLogin,
                "Bogatov",
                "client2",
                adminLogin + "1"
        );
    }

    private static Stream<String> passwordProvider() {
        return Stream.of(
                clientPassword,
                artistPassword,
                "ABCDEFG",
                "client2",
                adminPassword + "1"
        );
    }

    private static Stream<?> userProvider() {
        return Stream.of(
                Arguments.of(adminLogin, adminPassword, adminRole),
                Arguments.of(artistLogin, artistPassword, artistRole),
                Arguments.of(clientLogin, clientPassword, clientRole)
        );
    }

}

