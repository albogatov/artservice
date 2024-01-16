package com.example.highload.order;

import com.example.highload.order.mapper.TagMapper;
import com.example.highload.order.mock.LoginServiceMock;
import com.example.highload.order.mock.UserServiceMock;
import com.example.highload.order.mock.WireMockConfig;
import com.example.highload.order.model.enums.OrderStatus;
import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.order.model.inner.Tag;
import com.example.highload.order.model.inner.User;
import com.example.highload.order.model.network.OrderDto;
import com.example.highload.order.model.network.ResponseDto;
import com.example.highload.order.model.network.TagDto;
import com.example.highload.order.repos.OrderRepository;
import com.example.highload.order.repos.ResponseRepository;
import com.example.highload.order.repos.TagRepository;
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
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

@Testcontainers
@EnableConfigurationProperties
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = { WireMockConfig.class, LoginServiceMock.class, UserServiceMock.class })
public class OrderServiceTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private WireMockServer mockLoginService;

    @Autowired
    private WireMockServer mockUserService;

    @Autowired
    private LoginServiceMock loginServiceMock;

    @Autowired
    private UserServiceMock userServiceMock;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private TagMapper tagMapper;
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
    }

    @AfterAll
    static void pgStop() {
        postgreSQLContainer.stop();
    }

    @Test
    @Order(1)
    public void saveTag() {

        String tagName = "Programmer";
        TagDto tagDto = new TagDto();
        tagDto.setName(tagName);

        ExtractableResponse<Response> response =
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", "Bearer " + "mock")
                        .and()
                        .body(tagDto)
                        .when()
                        .post("/api/order/tag/save")
                        .then()
                        .extract();
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response.response().getStatusCode()),
                () -> {
                    Tag tag = tagRepository.findByName(tagName).orElseThrow();
                    Assertions.assertEquals(tag.getName(), tagName);
                }
        );
    }

    @Test
    @Order(2)
    public void saveTagAlreadyExisting() {

        String tagName = "Programmer";
        TagDto tagDto = new TagDto();
        tagDto.setName(tagName);

        ExtractableResponse<Response> response =
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", "Bearer " + "mock")
                        .and()
                        .body(tagDto)
                        .when()
                        .post("/api/order/tag/save")
                        .then()
                        .extract();
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.response().getStatusCode())
        );
    }

    @Test
    @Order(3)
    public void getTags() {

        String tagName = "Programmer";
        TagDto tagDto = new TagDto();
        tagDto.setName(tagName);

        ExtractableResponse<Response> response =
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", "Bearer " + "mock")
                        .and()
                        .body(tagDto)
                        .when()
                        .get("/api/order/tag/all")
                        .then()
                        .extract();
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response.response().getStatusCode()),
                () -> {
                    int size = response.body().as(List.class).size();
                    Assertions.assertEquals(1, size);
                }
        );
    }

    @Test
    @Order(4)
    public void addOrder() {

        Tag tag = tagRepository.findByName("Programmer").orElseThrow();
        OrderDto orderDto = new OrderDto();
        orderDto.setDescription("1o");
        orderDto.setPrice(1);
        orderDto.setStatus(OrderStatus.OPEN);
        orderDto.setCreated(LocalDateTime.now());
        orderDto.setUserName("client1");
        orderDto.setUserId(1);
        orderDto.setTags(List.of(tagMapper.tagToDto(tag)));


        // save valid

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(orderDto)
                        .when()
                        .post("/api/order/client/save")
                        .then()
                        .extract();

        ClientOrder result = orderRepository.findById(1).orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Order saved", response1.body().asString()),
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode()),
                () -> Assertions.assertEquals("1o", result.getDescription())
        );

        // try save invalid

        orderDto.setDescription("");
        orderDto.setStatus(null);

        ExtractableResponse<Response> response2 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(orderDto)
                        .when()
                        .post("/api/order/client/save")
                        .then()
                        .extract();


        Assertions.assertAll(
                () -> Assertions.assertTrue(response2.body().asString().contains("Request body validation failed!")),
                () -> Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response2.statusCode())
        );
    }

    @Test
    @Order(5)
    public void updateOrder() {

        Tag tag = tagRepository.findByName("Programmer").orElseThrow();
        OrderDto orderDto = new OrderDto();
        orderDto.setDescription("1o");
        orderDto.setPrice(100);
        orderDto.setStatus(OrderStatus.OPEN);
        orderDto.setCreated(LocalDateTime.now());
        orderDto.setUserName("client1");
        orderDto.setUserId(1);
        orderDto.setId(1);
        orderDto.setTags(List.of(tagMapper.tagToDto(tag)));


        // save valid

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(orderDto)
                        .when()
                        .post("/api/order/client/update/" + orderDto.getId())
                        .then()
                        .extract();

        Assertions.assertAll(
                () -> Assertions.assertEquals(100, response1.body().as(OrderDto.class).getPrice()),
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode())
        );

        // try save invalid

        orderDto.setDescription("");
        orderDto.setStatus(null);

        ExtractableResponse<Response> response2 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(orderDto)
                        .when()
                        .post("/api/order/client/update/" + orderDto.getId())
                        .then()
                        .extract();


        Assertions.assertAll(
                () -> Assertions.assertTrue(response2.body().asString().contains("Request body validation failed!")),
                () -> Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response2.statusCode())
        );
    }

    @Test
    @Order(6)
    public void getAllUserOpenOrders() {


        // get all

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .when()
                        .get("/api/order/client/open/user/" + 1)
                        .then()
                        .extract();


        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode())
        );

        List<OrderDto> orderDtos = response1.body().jsonPath().getList(".", OrderDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, orderDtos.size()),
                () -> Assertions.assertEquals("1o", orderDtos.get(0).getDescription()),
                () -> Assertions.assertEquals(OrderStatus.OPEN, orderDtos.get(0).getStatus())
        );


    }

    @Test
    @Order(7)
    public void addTagsToOrder() {

        String tagName = "NewTag";
        TagDto tagDto = new TagDto();
        tagDto.setName(tagName);

        ExtractableResponse<Response> response =
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", "Bearer " + "mock")
                        .and()
                        .body(tagDto)
                        .when()
                        .post("/api/order/tag/save")
                        .then()
                        .extract();
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response.response().getStatusCode()),
                () -> {
                    Tag tag = tagRepository.findByName(tagName).orElseThrow();
                    Assertions.assertEquals(tag.getName(), tagName);
                }
        );


        // add existing

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(List.of(tagRepository.findByName(tagName).orElseThrow().getId()))
                        .when()
                        .post("/api/order/client/single/" + 1 + "/tags/add")
                        .then()
                        .extract();

        OrderDto orderDto = response1.body().as(OrderDto.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode()),
                () -> Assertions.assertEquals(2, orderDto.getTags().size()),
                () -> Assertions.assertEquals("NewTag", orderDto.getTags().get(0).getName())
        );

        // add not existing

        ExtractableResponse<Response> response2 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(List.of(999))
                        .when()
                        .post("/api/order/client/single/" + 1 + "/tags/add")
                        .then()
                        .extract();

        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response2.statusCode())
        );
    }

    @Test
    @Order(8)
    public void getAllOrdersByTags() {

        Tag tag1 = tagRepository.findByName("Programmer").orElseThrow();
        Tag tag3 = tagRepository.findByName("NewTag").orElseThrow();



        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(List.of(tag1.getId(), tag3.getId()))
                        .when()
                        .get("/api/order/client/all/tag")
                        .then()
                        .extract();

        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode())
        );

        List<OrderDto> orderDtos = response1.body().jsonPath().getList(".", OrderDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, orderDtos.size()),
                () -> Assertions.assertEquals("1o", orderDtos.get(0).getDescription())
        );
    }

    @Test
    @Order(9)
    public void getAllOpenOrdersByTags() {

        Tag tag1 = tagRepository.findByName("Programmer").orElseThrow();
        Tag tag2 = tagRepository.findByName("NewTag").orElseThrow();


        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(List.of(tag1.getId(), tag2.getId()))
                        .when()
                        .get("/api/order/client/open/tag")
                        .then()
                        .extract();

        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode())
        );

        List<OrderDto> orderDtos = response1.body().jsonPath().getList(".", OrderDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, orderDtos.size()),
                () -> Assertions.assertEquals("1o", orderDtos.get(0).getDescription())
        );

        ClientOrder order = orderRepository.findById(1).orElseThrow();
        order.setStatus(OrderStatus.CLOSED);
        orderRepository.save(order);

        ExtractableResponse<Response> response2 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(List.of(tag1.getId(), tag2.getId()))
                        .when()
                        .get("/api/order/client/open/tag")
                        .then()
                        .extract();

        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode())
                );

        List<OrderDto> orderDtos2 = response2.body().jsonPath().getList(".", OrderDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, orderDtos2.size())
        );
    }

    @Test
    @Order(10)
    public void addResponse() {

        // save valid

        ResponseDto responseDto = new ResponseDto();
        responseDto.setApproved(false);
        responseDto.setText("-");
        responseDto.setUserId(2);
        responseDto.setUserName("artist1");
        responseDto.setOrderId(1);

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .and()
                        .body(responseDto)
                        .when()
                        .post("/api/order/response/save")
                        .then()
                        .extract();

        List<com.example.highload.order.model.inner.Response> result = responseRepository
                .findAllByOrder_Id(1).orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Response added", response1.body().asString()),
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode()),
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertEquals("artist1", result.get(0).getUser().getLogin())
        );
    }

    @Test
    @Order(11)
    public void getAllByOrder() {


        // get all

        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .when()
                        .get("/api/order/response/all/order/" + 1 )
                        .then()
                        .extract();


        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode())
        );

        List<ResponseDto> responseDtos = response1.body().jsonPath().getList(".", ResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, responseDtos.size()),
                () -> Assertions.assertEquals("artist1", responseDtos.get(0).getUserName())
        );


    }


    @Test
    @Order(12)
    public void approveResponse() {


        ExtractableResponse<Response> response1 =
                given()
                        .header("Authorization", "Bearer " + "mock")
                        .header("Content-type", "application/json")
                        .when()
                        .post("/api/order/response/approve/" + 1)
                        .then()
                        .extract();


        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK.value(), response1.statusCode())
        );

        com.example.highload.order.model.inner.Response response = responseRepository.findById(1).orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertEquals(true, response.getIsApproved())
        );


    }
}

