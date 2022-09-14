package src.test.java.com.kevindubois.cameldemo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ResultsResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/results")
          .then()
             .statusCode(200)
             .body(is("Hello hello"));
    }

}