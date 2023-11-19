package ru.yandex.practicum.stellarburgers.api.utils;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class RequestUtils {
    @Step("Отправка get-запроса")
    public static Response sendGetRequest(String api) {
        return given()
                .filters(new RequestLoggingFilter(), new ResponseLoggingFilter())
                .contentType(JSON)
                .get(api);
    }
    @Step("Отправка get-запроса с токеном авторизации")
    public static Response sendGetRequestWithAuthorization(String api, String accessToken) {
        return given()
                .filters(new RequestLoggingFilter(), new ResponseLoggingFilter())
                .header("Authorization", accessToken)
                .get(api);
    }
    @Step("Отправка post-запроса")
    public static Response sendPostRequest(String api, Object body) {
        return given()
                .filters(new RequestLoggingFilter(), new ResponseLoggingFilter())
                .contentType(JSON)
                .body(body)
                .post(api);
    }

    @Step("Отправка post-запроса с токеном авторизации")
    public static Response sendPostRequestWithAuthorization(String api, Object body, String accessToken) {
        return given()
                .filters(new RequestLoggingFilter(), new ResponseLoggingFilter())
                .header("Authorization", accessToken)
                .contentType(JSON)
                .body(body)
                .post(api);
    }

    @Step("Отправка delete-запроса")
    public static Response sendDeleteRequest(String api, String accessToken) {
        return given()
                .filters(new RequestLoggingFilter(), new ResponseLoggingFilter())
                .header("Authorization", accessToken)
                .delete(api);
    }

    @Step("Отправка patch-запроса")
    public static Response sendPatchRequest(String api, String accessToken, Object body) {
        return given()
                .filters(new RequestLoggingFilter(), new ResponseLoggingFilter())
                .contentType(JSON)
                .header("Authorization", accessToken)
                .and()
                .body(body)
                .patch(api);
    }
}
