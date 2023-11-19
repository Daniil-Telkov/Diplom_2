import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.stellarburgers.api.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.yandex.practicum.stellarburgers.api.config.APIConfig.*;
import static ru.yandex.practicum.stellarburgers.api.cridentials.UserCredentials.*;
import static ru.yandex.practicum.stellarburgers.api.utils.RequestUtils.sendDeleteRequest;
import static ru.yandex.practicum.stellarburgers.api.utils.RequestUtils.sendPostRequest;

public class UserCreateTests {
    User user = new User(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD, DEFAULT_USER_NAME);
    Boolean clearCreatedUser;
    @Before
    public void setUp() {
        clearCreatedUser = true;
        RestAssured.baseURI = BASE_URL;
    }

    @After
    public void deleteCreatedUser() {
        if (clearCreatedUser) {
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
    }
    @Test
    public void createUserTest() {
        Boolean createResult = sendPostRequest(USER_CREATE_API, user)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("success");
        assertTrue(createResult);
    }

    @Test
    public void createExistingUserTest() {
        String expectedMessage = "User already exists";

        sendPostRequest(USER_CREATE_API, user)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        String responseMessageText = sendPostRequest(USER_CREATE_API, user)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .extract()
                .path("message");
        assertEquals(expectedMessage, responseMessageText);
    }

    @Test
    public void createUserWithoutRequiredFieldTest() {
        clearCreatedUser = false;
        String expectedMessage = "Email, password and name are required fields";
        User user = new User(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD, "");

        String responseMessageText = sendPostRequest(USER_CREATE_API, user)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .extract()
                .path("message");
        assertEquals(expectedMessage, responseMessageText);
    }
}
