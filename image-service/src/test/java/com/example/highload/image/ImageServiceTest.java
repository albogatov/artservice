package com.example.highload.image;

import com.example.highload.image.mapper.ImageMapper;
import com.example.highload.image.mock.*;
import com.example.highload.image.model.inner.Image;
import com.example.highload.image.model.inner.ImageObject;
import com.example.highload.image.model.network.ImageDto;
import com.example.highload.image.repos.ImageRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

@Testcontainers
@EnableConfigurationProperties
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = { WireMockConfig.class, LoginServiceMock.class, UserServiceMock.class })
public class ImageServiceTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private WireMockServer mockLoginService;

    @Autowired
    private WireMockServer mockUserService;

    @Autowired
    private WireMockServer mockOrderService;

    @Autowired
    private WireMockServer mockProfileService;

    @Autowired
    private LoginServiceMock loginServiceMock;

    @Autowired
    private UserServiceMock userServiceMock;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageMapper imageMapper;
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
        OrderServiceMock.setupMockGetById(mockOrderService);
        ProfileServiceMock.setupMockGetById(mockProfileService);
        ProfileServiceMock.setupMockSetNewMain(mockProfileService);
    }

    @AfterAll
    static void pgStop() {
        postgreSQLContainer.stop();
    }

    @Test
    @Order(1)
    public void addImagesToProfile() {

        ImageDto imageDto1 = new ImageDto();
        imageDto1.setUrl("first");

        ImageDto imageDto2 = new ImageDto();
        imageDto2.setUrl("second");

        List<ImageDto> imageDtoList = new ArrayList<>();
        imageDtoList.add(imageDto1);
        imageDtoList.add(imageDto2);


        // add to artist profile

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(imageDtoList)
                        .when()
                        .post("/api/image/add/profile")
                        .then()
                        .extract();

        String responseBody = response1.body().asString();
        Assertions.assertAll(
                () -> Assertions.assertEquals("Images added", response1.body().asString()),
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode())
        );

        List<Image> images = imageRepository.findAll().stream().toList();

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, images.size()),
                () -> Assertions.assertEquals("second", images.get(1).getUrl()),
                () -> Assertions.assertEquals("first", images.get(0).getUrl())
        );

    }

    @Test
    @Order(2)
    public void changeMainImageOfProfile() {


        ImageDto imageDto = new ImageDto();
        imageDto.setUrl("main");


        // add to artist profile

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(imageDto)
                        .when()
                        .post("/api/image/change/profile")
                        .then()
                        .extract();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Main image changed", response1.body().asString()),
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode())
        );

    }

    @Test
    @Order(3)
    public void removeImageForProfile() {

        // remove from artist profile

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .when()
                        .post("/api/image/remove/profile/" + 1)
                        .then()
                        .extract();


        Assertions.assertAll(
                () -> Assertions.assertEquals("Image removed", response1.body().asString()),
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode())
        );

    }
}

