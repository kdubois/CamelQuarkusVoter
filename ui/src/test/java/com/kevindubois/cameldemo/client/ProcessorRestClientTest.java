package com.kevindubois.cameldemo.client;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;


@QuarkusTest
@QuarkusTestResource(MockRestServer.class)
public class ProcessorRestClientTest {
    @Test
    void testGetVotes(){
        given()
            .when().get("http://localhost:"+MockRestServer.WIREMOCK_PORT+"/getresults")
            .then()
            .statusCode(200)
            .body("$.size()", is(1),
                "[0].id", is(1),
                "[0].shortname", is("quarkus"),
                "[0].fullname", is("Quarkus"),
                "[0].counter", is(1)
            );

    }
}
