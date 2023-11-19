import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.stellarburgers.api.model.Order;
import ru.yandex.practicum.stellarburgers.api.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.yandex.practicum.stellarburgers.api.config.APIConfig.*;
import static ru.yandex.practicum.stellarburgers.api.cridentials.UserCredentials.*;
import static ru.yandex.practicum.stellarburgers.api.model.Ingredients.getIngredientByNum;
import static ru.yandex.practicum.stellarburgers.api.utils.RequestUtils.*;

public class OrderGetTests {
    String accessToken;
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;

        List<String> ingredients = new ArrayList<>();
        String bun = getIngredientByNum(0);
        String main = getIngredientByNum(1);
        String sauce = getIngredientByNum(4);

        ingredients.add(bun);
        ingredients.add(main);
        ingredients.add(sauce);

        Order order = new Order(ingredients);
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

        // Создаём два заказа добавленному пользователю
        sendPostRequestWithAuthorization(ORDER_CREATE_API, order, accessToken)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("order.number", notNullValue());

        sendPostRequestWithAuthorization(ORDER_CREATE_API, order, accessToken)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("order.number", notNullValue());
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
    public void getUsersOrdersWithAuthorizationTest() {
        int expectedOrdersAmount = 2;
        List<String> UsersOrders = sendGetRequestWithAuthorization(ORDER_GET_API, accessToken)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .jsonPath().getList("orders._id");
        assertEquals(expectedOrdersAmount, UsersOrders.size());
    }

    @Test
    public void getUsersOrdersWithoutAuthorizationTest() {
        String expectedMessage = "You should be authorised";
        int expectedOrdersAmount = 2;
        sendGetRequest(ORDER_GET_API)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false));
    }
}
