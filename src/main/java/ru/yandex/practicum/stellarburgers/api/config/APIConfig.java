package ru.yandex.practicum.stellarburgers.api.config;

public class APIConfig {
    public static final String BASE_URL = "http://stellarburgers.nomoreparties.site";

    public static final String USER_CREATE_API = "/api/auth/register";
    public static final String USER_MODIFY_API = "/api/auth/user";
    public static final String USER_AUTH_API = "/api/auth/login";

    public static final String ORDER_CREATE_API = "/api/orders";
    public static final String ORDER_GET_API = "/api/orders";

    public static final String INGREDIENTS_GET_API = "/api/ingredients";
}
