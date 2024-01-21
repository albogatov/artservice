package com.example.highload.notification;

import jakarta.websocket.WebSocketContainer;
import com.example.highload.notification.mock.LoginServiceMock;
import com.example.highload.notification.mock.ProfileServiceMock;
import com.example.highload.notification.mock.UserServiceMock;
import com.example.highload.notification.mock.WireMockConfig;
import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.network.ResponseDto;
import com.example.highload.notification.repos.NotificationRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@EnableConfigurationProperties
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = { WireMockConfig.class, LoginServiceMock.class, UserServiceMock.class, ProfileServiceMock.class })
public class NotificationServiceTest {

    @LocalServerPort
    private Integer port;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private WireMockServer mockLoginService;

    @Autowired
    private WireMockServer mockUserService;

    @Autowired
    private WireMockServer mockProfileService;

    @Autowired
    private LoginServiceMock loginServiceMock;

    @Autowired
    private UserServiceMock userServiceMock;

    @Autowired
    private ProfileServiceMock profileServiceMock;

    @Autowired
    private NotificationRepository notificationRepository;

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("highload")
            .withUsername("high_user")
            .withPassword("high_user");

    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
    ).waitingFor(Wait.forLogMessage(".*started.*\\n", 1));

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> r2dbcUrl());
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.consumer.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.consumer.value-deserializer", () -> {
            return "org.springframework.kafka.support.serializer.JsonDeserializer";
        });
    }

    @BeforeAll
    static void pgStart() {
        postgreSQLContainer.start();
//        kafka.withEmbeddedZookeeper()
//                .start();
    }

    @BeforeEach
    void setUp() throws IOException {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.defaultParser = Parser.JSON;
        LoginServiceMock.setupMockValidateResponse(mockLoginService);
        LoginServiceMock.setupMockGetLoginResponse(mockLoginService);
        UserServiceMock.setupMockLoginResponse(mockUserService);
        ProfileServiceMock.setupMockGetByUserId(mockProfileService);
    }

    @AfterAll
    static void pgStop() {
        postgreSQLContainer.stop();
//        kafka.stop();
    }

    @Test
    public void notificationTest() {
        KafkaTemplate kafkaTemplate = new KafkaTemplate<>(producerFactory());
        ResponseDto responseDto = new ResponseDto();
        responseDto.setId(1);
        responseDto.setUserId(1);
        responseDto.setOrderUserId(1);
        responseDto.setOrderId(1);
        responseDto.setApproved(false);
        responseDto.setUserName("artist1");
        responseDto.setOrderUserName("client1");
        responseDto.setText("mock");
        kafkaTemplate.send("notifications", responseDto);
        await()
                .pollInterval(Duration.ofSeconds(5))
                .atMost(30, SECONDS)
                .untilAsserted(() -> {
                    Mono<Notification> notification = notificationRepository.findAll().last();
                    Notification notification1 = notification.block();
                    Assertions.assertNotNull(notification1);
                    Assertions.assertEquals(false, notification1.getIsRead());
                });
    }


    static String r2dbcUrl() {
        return "r2dbc:postgresql://" + postgreSQLContainer.getHost() + ":" + postgreSQLContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT) + "/highload";
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

    public static ProducerFactory<String, ResponseDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "response");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}

