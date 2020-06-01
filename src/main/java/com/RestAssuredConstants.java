package com;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

public class RestAssuredConstants {

    private static final LogConfig LOG_ALL_IF_VALIDATION_FAILS = LogConfig.logConfig()
            .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);

    public static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

    private static final RestAssuredConfig CONFIG = RestAssured.config()
            .logConfig(LOG_ALL_IF_VALIDATION_FAILS)
            .objectMapperConfig(
                    objectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper)
            );

    private static final Filter ALLURE_REST_ASSURED_FILTER = new AllureRestAssured()
            .setRequestTemplate("allure-rest-assured-filters/http-request.ftl")
            .setResponseTemplate("allure-rest-assured-filters/http-response.ftl");

    public static final RequestSpecification BASIC_REQUEST_SPECIFICATION = getDefaultRequestBuilder().build();


    private RestAssuredConstants() {
    }

    private static RequestSpecBuilder getDefaultRequestBuilder() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                .setConfig(CONFIG)
                .addFilter(ALLURE_REST_ASSURED_FILTER);

        return requestSpecBuilder;
    }
}
