import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.Getter;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierCreationTest {

    @Getter
    private static Courier existingCourier;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        createNewCourier();
    }

    // Создание курьера
    @Test
    public void createNewCourier() {

        Faker faker = new Faker();

        existingCourier = Courier.builder()
                .login(faker.name().username())
                .password(faker.internet().password(4, 12))
                .firstName(faker.name().firstName())
                .build();
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(existingCourier)
                        .post("/api/v1/courier");
        response.then().statusCode(201);
        response.then().body("ok", equalTo(true));

    }


    // Создание одинаковых курьеров
    @Test
    public void createIdenticalCouriers() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(existingCourier)
                        .post("/api/v1/courier");
        response.then().statusCode(409);
        response.then().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    // Создание курьера без логина
    @Test
    public void createCourierWithoutLogin(){
        Faker faker = new Faker();

        Courier courier = Courier.builder()

                .password(faker.internet().password(4, 12))
                .firstName(faker.name().firstName())
                .build();
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courier)
                        .post("/api/v1/courier");
        response.then().statusCode(400);
        response.then().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    // Создание курьера без пароля
    @Test
    public void createCourierWithoutPassword(){
        Faker faker = new Faker();

        Courier courier = Courier.builder()

                .login(faker.name().username())
                .firstName(faker.name().firstName())
                .build();
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courier)
                        .post("/api/v1/courier");
        response.then().statusCode(400);
        response.then().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}


