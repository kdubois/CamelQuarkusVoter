package com.kevindubois.cameldemo.producer;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class VoteProducerRouteTest {

    @Test
    void testRoute() {
        given()
                .when()
                .body("{name: quarkus}")
                .post("/favstack")                
                .then()
                .statusCode(200);        
    }
}
