package com.example.highload;

import com.example.highload.feign.LoginServiceFeignClient;
import com.example.highload.mock.LoginServiceMock;
import com.example.highload.mock.WireMockConfig;
import com.example.highload.model.enums.RoleType;
import com.example.highload.model.inner.Role;
import com.example.highload.model.inner.User;
import com.example.highload.model.network.UserDto;
import com.example.highload.repos.RoleRepository;
import com.example.highload.repos.UserRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
@Testcontainers
@EnableConfigurationProperties
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = { WireMockConfig.class, LoginServiceMock.class})
public class UserServiceTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private WireMockServer mockLoginService;

    @Autowired
    private WireMockServer mockUserService;

    @Autowired
    private LoginServiceMock loginServiceMock;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginServiceFeignClient loginServiceFeignClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

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

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(userDto)
                        .when()
                        .post("/api/user/save")
                        .then()
                        .extract();
        Assertions.assertAll(
                () -> Assertions.assertEquals("admin_test_client1", response1.body().as(UserDto.class).getLogin()),
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode())
        );

        /*add existing user*/

        ExtractableResponse<Response> response2 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(userDto)
                        .when()
                        .post("/api/user/save")
                        .then()
                        .extract();
        Assertions.assertAll(
                () -> Assertions.assertEquals("User already exists!", response2.body().asString()),
                () -> Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response2.statusCode())
        );

        /* add user with wrong name (empty) */

        UserDto wrongUserDto = new UserDto();
        wrongUserDto.setLogin("");
        wrongUserDto.setPassword("-");
        wrongUserDto.setRole(RoleType.CLIENT);

        ExtractableResponse<Response> response3 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(wrongUserDto)
                        .when()
                        .post("/api/user/save")
                        .then()
                        .extract();
        Assertions.assertAll(
                () -> Assertions.assertTrue(response3.body().asString().contains("Request body validation failed! Validation failed for classes")),
                () -> Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response3.statusCode())
        );
    }

    //TODO: Fix
    @Test
    @Order(1)
    public void deleteUser() {
        // create user using repo

        Role clientRole = roleRepository.findByName(RoleType.CLIENT).orElseThrow();

        User user = new User();
        user.setLogin("admin_test_client3");
        user.setHashPassword("mock");
        user.setRole(clientRole);
        user.setIsActual(true);

        User userWithId = userRepository.save(user);

        // delete existing

        String id = userWithId.getId().toString();

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .when()
                        .post("/api/user/deleteId/" + id)
                        .then()
                        .extract();
        Assertions.assertAll(
                () -> Assertions.assertEquals("User deleted successfully", response1.body().asString()),
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode()),
                () -> Assertions.assertThrows(NoSuchElementException.class, () -> userRepository.findByLogin("admin_test_client3").orElseThrow())
        );

    }

    @Test
    @Order(2)
    public void deleteAllExpiredUserDeletedAccounts() {

        Role clientRole = roleRepository.findByName(RoleType.CLIENT).orElseThrow();

        // create logically deleted account using repo

        User user = new User();
        user.setLogin("admin_test_client6");
        user.setHashPassword("mock");
        user.setRole(clientRole);
        user.setIsActual(false);
        user.setWhenDeletedTime(LocalDateTime.now());

        userRepository.save(user);

        // delete with 0 days param

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .when()
                        .post("/api/user/deleteAllExpired/0")
                        .then()
                        .extract();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Successfully deleted", response1.body().asString()),
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode()),
                () -> Assertions.assertThrows(NoSuchElementException.class, () -> userRepository.findByLogin("admin_test_client6").orElseThrow())
        );

    }

    @Test
    void deactivateIdAPICorrect() {
        User user = userRepository.findByLogin(clientLogin).orElseThrow();
        Integer userId = user.getId();

        ExtractableResponse<Response> response =
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", "Bearer " + "mock")
                        .when()
                        .post("/api/user/deactivate/" + userId)
                        .then()
                        .extract();
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response.response().getStatusCode()),
                () -> {
                    User user2 = userRepository.findByLogin(clientLogin).orElseThrow();
                    Assertions.assertFalse(user2.getIsActual());
                }
        );
    }

}

