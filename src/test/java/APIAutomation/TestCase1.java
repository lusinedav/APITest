package APIAutomation;

import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Created by LusineDavtyan on 5/20/19.
 */
public class TestCase1 {

    static String  key;
    static String username;
    static String password;

    @Test(priority = 0)
    public void signInTest() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", "api-automation");
        jsonObject.addProperty("password", "Lusin86");

        Response response = given().
                contentType(ContentType.JSON).
                body(jsonObject).
                when().
                post("https://api.picsart.com/users/signin.json");

//        response.getBody().asString();
        response.then().
                statusCode(200).
                body("username", is("api-automation"),
                        "key", isA(String.class),
                        "id", isA(Long.class),
                        "name", notNullValue(),
                        "email", is("api.automation@gmail.com"),
                        //"email", isA(String.class),
                        "is_verified", isA(Boolean.class),
                        "type", is("user"),
                        "notification_settings.in_app.challenges", is(true),
                        "devices[0].id", is("3da1f9b8-8700-4d09-949f-56f54ab955f5")
                );

    }

    @Test(priority = 1)
    public static void signUpTest() {
        JsonObject jsonObject = new JsonObject();
        String username = UUID.randomUUID().toString().substring(0, 9);
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", "Lusin86");
        jsonObject.addProperty("email", username + "@gmail.com");
        jsonObject.addProperty("provider", "picsart");

        Response response = given().
                contentType(ContentType.JSON).
                body(jsonObject).
                when().
                post("https://api.picsart.com/users/signup.json");
        System.out.println( "response is : " + response.getBody().asString());
        key = response.path("key");
        username = response.path("username");
        password = response.path("password");
        response.then().statusCode(200).body(
                "key", notNullValue(),
//                "name", is(username),
                "photo", is("https://cdn190.picsart.com/232804661007900.png"),
                "email", is(username + "@gmail.com"),
                "gender", is(""),
                "provider", is("picsart")
//                "registered", not(false),
//                "notification_settings.in_app",notNullValue(),
//                "notification_settings.push",notNullValue()
        );

    }

    @Test(priority = 2)
    public static void DeleteUserTest(){
        Response response = given().
                queryParam("key", key).
                contentType(ContentType.JSON).
                when().
                post("https://api.picsart.com/users/remove");
        System.out.println( "response is : " + response.getBody().asString());

        response.then().statusCode(200).body(
                "status", is("success")
        );

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

       response = given().
                contentType(ContentType.JSON).
                body(jsonObject).
                when().
                post("https://api.picsart.com/users/signin.json");
        System.out.println( "response is : " + response.getBody().asString());
        response.then().
                statusCode(200).
                body(
                        "status", is("error"),
                        "reason", is("username_or_password_incorrect"),
                        "message", is("Username or password incorrect")

                );
    }
}
