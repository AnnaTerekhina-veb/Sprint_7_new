import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import io.qameta.allure.junit4.DisplayName;

public class LoginCourierTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        CourierCreationTest testInstance = new CourierCreationTest();
        testInstance.createNewCourier();
    }

    // Авторизация курьера

    @Test
    public void loginCourier() {
        Courier existingCourier = CourierCreationTest.getExistingCourier();

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(existingCourier)
                        .post("/api/v1/courier/login");
        response.then().statusCode(200);
        response.then().body("id", notNullValue());
    }

    // Авторизация курьера без логина

    @Test
    public void loginCourierWithoutLogin() {
        Courier courierWithoutLogin = Courier.builder()
                .password("1111")
                .build();

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courierWithoutLogin)
                        .post("/api/v1/courier/login");

        response.then().statusCode(400);
        response.then().body("message", equalTo("Недостаточно данных для входа"));
    }

    // Авторизация курьера без пароля

    @Test
    public void loginCourierWithoutPassword() {

        Courier courierWithoutPassword = Courier.builder()
                .login("Anna")
                .firstName("Test")
                .build();

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courierWithoutPassword)
                        .post("/api/v1/courier/login");

        response.then().statusCode(400);
        response.then().body("message", equalTo("Недостаточно данных для входа"));
    }

// Авторизация несуществующего курьера

    @Test
    public void loginCourierDoesNotExist(){

        Courier courierDoesNotExist = Courier.builder()
                .login("па33вы6аыва")
                .password("66963")
                .build();
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courierDoesNotExist)
                        .post("/api/v1/courier/login");
        response.then().statusCode(404);
        response.then().body("message", equalTo("Учетная запись не найдена"));
    }
}