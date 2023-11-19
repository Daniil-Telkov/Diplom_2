package ru.yandex.practicum.stellarburgers.api.model;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import static ru.yandex.practicum.stellarburgers.api.config.APIConfig.INGREDIENTS_GET_API;
import static ru.yandex.practicum.stellarburgers.api.utils.RequestUtils.sendGetRequest;

public class Ingredients {
    @Step("Получение списка доступных ингредиентов")
    public static Response getIngredientsList () {
        return sendGetRequest(INGREDIENTS_GET_API);
    }

    @Step("Получение ингредиента по порядковому номеру из списка")
    public static String getIngredientByNum (int ingredientNum) {
        return getIngredientsList().
                then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path(String.format("data[%s]._id", ingredientNum));
    }
}
