package com.clients;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.Builder;

import static io.restassured.RestAssured.given;

public class RoutingServiceClient extends BaseClient {

    private static final String SEED_PATH_PARAMETER = "seed";
    private static final String SEED_PATH = "{" + SEED_PATH_PARAMETER + "}";

    @Builder
    public RoutingServiceClient() {
    }

    protected RequestSpecification getSpec() {
        return getBaseSpec();
    }

    @Step
    public ValidatableResponse getRoutingService(String seedValue) {
        return given()
                .urlEncodingEnabled(false)
                .spec(getSpec())
                .pathParam(SEED_PATH_PARAMETER, seedValue)
                .when()
                .get(SEED_PATH)
                .then();
    }
}
