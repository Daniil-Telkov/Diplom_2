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
import static ru.yandex.practicum.stellarburgers.api.utils.RequestUtils.*;

public class UserEditTests {
    User user = new User(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD, DEFAULT_USER_NAME);
    User updatedUser = new User(UPDATED_USER_EMAIL, UPDATED_USER_PASSWORD, UPDATED_USER_NAME);
    String accessToken;

    Boolean clearUpdatedUser = true;
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
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
    public void deleteModifiedUser() {
        if (clearUpdatedUser) {
            User updatedUser = new User(UPDATED_USER_EMAIL, UPDATED_USER_PASSWORD);
            accessToken = sendPostRequest(USER_AUTH_API, updatedUser)
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
        else {
            User createdUser = new User(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD);
            accessToken = sendPostRequest(USER_AUTH_API, createdUser)
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
    public void editUserDataTest() {
        Response editUserDataResponse = sendPatchRequest(USER_MODIFY_API, accessToken, updatedUser);

        Boolean editResult = editUserDataResponse
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("success");
        assertTrue(editResult);

        String updatedEmail = editUserDataResponse
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("user.email");
        assertEquals(UPDATED_USER_EMAIL, updatedEmail);

        String updatedName = editUserDataResponse
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("user.name");
        assertEquals(UPDATED_USER_NAME, updatedName);
    }

    @Test
    public void editUserDataWithoutAuthorizationTest() {
        clearUpdatedUser = false;
        String emptyAccessToken = "";
        String expectedMessage = "You should be authorised";

        String responseMessageText = sendPatchRequest(USER_MODIFY_API, emptyAccessToken, updatedUser)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .extract()
                .path("message");
        assertEquals(expectedMessage, responseMessageText);
    }
}
