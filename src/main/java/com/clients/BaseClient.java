package com.clients;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static com.RestAssuredConstants.BASIC_REQUEST_SPECIFICATION;
import static io.restassured.http.ContentType.JSON;

public class BaseClient {

    protected String baseUrl = "http://localhost:9000/route/";

    /**
     * @return RequestSpecification containing
     * com.base URL,
     * JSON content type,
     * default filters
     */
    protected RequestSpecification getBaseSpec() {

        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
                .addRequestSpecification(BASIC_REQUEST_SPECIFICATION)
                .setContentType(JSON)
                .setBaseUri(baseUrl);
        return specBuilder
                .build();
    }
}