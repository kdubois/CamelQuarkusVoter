package com.kevindubois.cameldemo;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class RestToCamelRouteTest {

    @Test
    void testRoute() {
        given()
                .body("{\"name\": \"quarkus\"}")
                .header("Content-Type", "application/json")
                .when()
                .post("/favstack")
                .then()
                .statusCode(200);
    }
}
