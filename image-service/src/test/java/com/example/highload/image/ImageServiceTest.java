package com.example.highload.image;

import com.example.highload.image.mapper.ImageMapper;
import com.example.highload.image.mock.*;
import com.example.highload.image.model.inner.Image;
import com.example.highload.image.repos.ImageRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.minio.MinioClient;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.parsing.Parser;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

import static io.jsonwebtoken.lang.Classes.getResourceAsStream;
import static io.restassured.RestAssured.given;

@Testcontainers
@EnableConfigurationProperties
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = { WireMockConfig.class, LoginServiceMock.class, UserServiceMock.class })
public class ImageServiceTest {

    @LocalServerPort
    private Integer port;

    @Value("${jwt.secret}")
    private String jwtSecret;

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


    @Container
    private static final MinIOContainer minIOContainer = new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z")
            .withExposedPorts(9000);

//    MinioClient minioClient = MinioClient
//            .builder()
//            .endpoint(minIOContainer.getS3URL())
//            .credentials(minIOContainer.getUserName(), minIOContainer.getPassword())
//            .build();

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
        registry.add("minio.url", minIOContainer::getS3URL);
        registry.add("minio.port", () -> minIOContainer.getMappedPort(9000));
    }

    @BeforeAll
    static void pgStart() {
        postgreSQLContainer.start();
        minIOContainer.configure();
        minIOContainer.start();
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
        minIOContainer.stop();
    }

    @Test
    @Order(1)
    public void addImagesToProfile() throws IOException {

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.addParam("files", "..file");
        RequestSpecification requestSpec = builder.build();

        String token = tokenProvider("artist1", "ARTIST");

        // add to artist profile

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + token)
                        .spec(requestSpec)
                        .contentType(String.valueOf(MediaType.MULTIPART_FORM_DATA))
                        .multiPart("files", new File(getClass().getClassLoader().getResource("payload/imageExample.jpg").getFile()))
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
                () -> Assertions.assertEquals(1, images.size())
        );

    }

    @Test
    @Order(2)
    public void changeMainImageOfProfile() throws IOException {

        String token = tokenProvider("artist1", "ARTIST");

        // add to artist profile

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + token)
                        .and()
                        .contentType(String.valueOf(MediaType.MULTIPART_FORM_DATA))
                        .multiPart("file", new File(getClass().getClassLoader().getResource("payload/imageExample.jpg").getFile()))
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

        String token = tokenProvider("artist1", "ARTIST");

        // remove from artist profile

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + token)
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

