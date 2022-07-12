import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class AuthorizationTest {

    public static Response response;

    @Test
    public void validTokenTest() {
        response = given().get(Constants.CURRENCY_LAYER_URL + Constants.LIVE + Constants.TOKEN);
        System.out.println(response.asString());
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("source", equalTo("USD"));
    }

    @Test
    public void missingTokenTest() {
        response = given().get(Constants.CURRENCY_LAYER_URL + Constants.LIVE + null);
        System.out.println(response.asString());
        response.then().statusCode(401);
        response.then().body("message", containsString("No API key"));
    }

    @Test
    public void invalidTokenTest() {
        response = given().get(Constants.CURRENCY_LAYER_URL + Constants.LIVE + "apikey=j0mzIM0RbLYpLmBRYGeM6VdrsMyhpmbzi");
        System.out.println(response.asString());
        response.then().statusCode(401);
        response.then().body("message", containsString("Invalid authentication"));
    }

}


