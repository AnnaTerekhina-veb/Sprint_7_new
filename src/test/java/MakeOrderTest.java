import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import io.qameta.allure.junit4.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class MakeOrderTest {
    private Order newOrder;
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private String[] color;

    // Конструктор
    public MakeOrderTest(String firstName, String lastName, String address, String metroStation, String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }


    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        Faker faker = new Faker();
        return Arrays.asList(new Object[][]{
                {
                        faker.name().firstName(),
                        faker.name().lastName(),
                        faker.address().fullAddress(),
                        "Аннино",
                        faker.phoneNumber().phoneNumber(),
                        faker.number().numberBetween(1, 4),
                        "2023-12-01",
                        "Please take care of my scooter",
                        new String[]{"BLACK"}
                },

                {
                        faker.name().firstName(),
                        faker.name().lastName(),
                        faker.address().fullAddress(),
                        "Марьина роща",
                        faker.phoneNumber().phoneNumber(),
                        faker.number().numberBetween(1, 4),
                        "2023-12-02",
                        "Need a speedy delivery",
                        new String[]{"BLACK", "GREY"}
                }

        });
    }
    // Создаем заказ с указанием одного цвета и с указанием сразу 2х цветов
    @Test
    public void createNewOrder() {
        newOrder = Order.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .metroStation(metroStation)
                .phone(phone)
                .rentTime(rentTime)
                .deliveryDate(deliveryDate)
                .comment(comment)
                .color(color)
                .build();

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(newOrder)
                        .when()
                        .post("/api/v1/orders")
                        .then()
                        .statusCode(201)
                        .body("track", notNullValue())
                        .extract().response();

        System.out.println("Response: " + response.asString());
    }

    // Создаем заказ без указания цвета

    @Test
    public void createOrderWithoutColors() {

        newOrder = Order.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .metroStation(metroStation)
                .phone(phone)
                .rentTime(rentTime)
                .deliveryDate(deliveryDate)
                .comment(comment)
                .build();

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(newOrder)
                        .when()
                        .post("/api/v1/orders")
                        .then()
                        .statusCode(201)
                        .body("track", notNullValue())
                        .extract().response();
    }
}