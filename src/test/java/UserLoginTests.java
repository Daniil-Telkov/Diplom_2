import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.stellarburgers.api.model.User;

import static org.junit.Assert.*;
import static ru.yandex.practicum.stellarburgers.api.config.APIConfig.*;
import static ru.yandex.practicum.stellarburgers.api.cridentials.UserCredentials.*;
import static ru.yandex.practicum.stellarburgers.api.utils.RequestUtils.sendDeleteRequest;
import static ru.yandex.practicum.stellarburgers.api.utils.RequestUtils.sendPostRequest;

public class UserLoginTests {
    User user = new User(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD, DEFAULT_USER_NAME);
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;

        sendPostRequest(USER_CREATE_API, user)
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK);
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
    public void loginWithExistingUserTest() {
        User LoginUser = new User(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD);
        Response loginResponse = sendPostRequest(USER_AUTH_API, LoginUser);

        Boolean loginResult = loginResponse
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("success");
        assertTrue(loginResult);

        String accessToken = loginResponse
                .then()
                .extract()
                .path("accessToken");
        assertNotNull(accessToken);

        String refreshToken = loginResponse
                .then()
                .extract()
                .path("refreshToken");
        assertNotNull(refreshToken);

        String responseEmail = loginResponse
                .then()
                .extract()
                .path("user.email");
        assertEquals(DEFAULT_USER_EMAIL, responseEmail);

        String responseName = loginResponse
                .then()
                .extract()
                .path("user.name");
        assertEquals(DEFAULT_USER_NAME, responseName);
    }

    @Test
    public void loginWithNotExistingUserTest() {
        String expectedMessage = "email or password are incorrect";
        User notExistingUser = new User(NOT_EXISTING_USER_EMAIL, DEFAULT_USER_PASSWORD);

        String responseMessageText = sendPostRequest(USER_AUTH_API, notExistingUser)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .extract()
                .path("message");
        assertEquals(expectedMessage, responseMessageText);
    }
}
