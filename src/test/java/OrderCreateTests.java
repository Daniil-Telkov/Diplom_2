import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.stellarburgers.api.model.Order;
import ru.yandex.practicum.stellarburgers.api.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ru.yandex.practicum.stellarburgers.api.config.APIConfig.*;
import static ru.yandex.practicum.stellarburgers.api.cridentials.UserCredentials.*;
import static ru.yandex.practicum.stellarburgers.api.model.Ingredients.getIngredientByNum;
import static ru.yandex.practicum.stellarburgers.api.utils.RequestUtils.*;

public class OrderCreateTests {
    String accessToken;
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;

        User user = new User(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD, DEFAULT_USER_NAME);
        sendPostRequest(USER_CREATE_API, user)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        accessToken = sendPostRequest(USER_AUTH_API, user)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("accessToken");
    }

    @After
    public void deleteCreatedUser() {
        User createdUser = new User(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD);
        String accessToken = sendPostRequest(USER_AUTH_API, createdUser)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("accessToken");

        Boolean deleteResult = sendDeleteRequest(USER_MODIFY_API, accessToken)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_ACCEPTED)
                .extract()
                .path("success");
        assertTrue(deleteResult);
    }
    @Test
    public void createOrderWithAuthorizationTest() {
        List<String> ingredients = new ArrayList<>();
        String bun = getIngredientByNum(0);
        String main = getIngredientByNum(1);
        String sauce = getIngredientByNum(4);

        ingredients.add(bun);
        ingredients.add(main);
        ingredients.add(sauce);

        Order order = new Order(ingredients);

        Boolean createResult = sendPostRequestWithAuthorization(ORDER_CREATE_API, order, accessToken)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("order.number", notNullValue())
                .extract()
                .path("success");
        assertTrue(createResult);
    }

    @Test
    public void createOrderWithoutAuthorizationTest() {
        List<String> ingredients = new ArrayList<>();
        String bun = getIngredientByNum(0);
        String main = getIngredientByNum(1);
        String sauce = getIngredientByNum(4);

        ingredients.add(bun);
        ingredients.add(main);
        ingredients.add(sauce);

        Order order = new Order(ingredients);

        Boolean createResult = sendPostRequest(ORDER_CREATE_API, order)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("order.number", notNullValue())
                .extract()
                .path("success");
        assertTrue(createResult);
    }

    @Test
    public void createOrderWithEmptyIngredientsListTest() {
        String expectedMessage = "Ingredient ids must be provided";
        List<String> ingredients = new ArrayList<>();
        Order order = new Order(ingredients);

        String responseMessageText = sendPostRequest(ORDER_CREATE_API, order)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .path("message");
        assertEquals(expectedMessage, responseMessageText);
    }

    @Test
    public void createOrderWithNotExistingIngredientsHashTest() {
        String notExistingHash = "thisHashIsNotExists500";

        List<String> ingredients = new ArrayList<>();
        ingredients.add(notExistingHash);
        Order order = new Order(ingredients);

        sendPostRequest(ORDER_CREATE_API, order)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}
