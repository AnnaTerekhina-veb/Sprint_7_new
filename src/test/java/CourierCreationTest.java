import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierCreationTest {

    private List<Integer> createdCourierIds = new ArrayList<>();

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

   private int getCourierId(String login, String password) {
        return given()
                .header("Content-type", "application/json")
                .body("{\"login\":\"" + login + "\", \"password\":\"" + password + "\"}")
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    private void deleteCourier(int id) {
        given()
                .delete("/api/v1/courier/" + id)
                .then()
                .statusCode(200);
    }

    @After
    public void tearDown() {
        for (int id : createdCourierIds) {
            deleteCourier(id);
        }
        createdCourierIds.clear();
    }

    // Создание курьера

    @Test
    public void createNewCourier() {
        Faker faker = new Faker();

        String login = faker.name().username();
        String password = faker.internet().password(4, 12);
        String firstName = faker.name().firstName();

        Courier courier = Courier.builder()
                .login(login)
                .password(password)
                .firstName(firstName)
                .build();

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        int courierId = getCourierId(login, password);
        createdCourierIds.add(courierId);
    }

    // Создание одинаковых курьеров

    @Test
    public void createIdenticalCouriers() {
        Faker faker = new Faker();

        String login = faker.name().username();
        String password = faker.internet().password(4, 12);
        String firstName = faker.name().firstName();

        Courier courier = Courier.builder()
                .login(login)
                .password(password)
                .firstName(firstName)
                .build();

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        int courierId = getCourierId(login, password);
        createdCourierIds.add(courierId);


        given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
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
