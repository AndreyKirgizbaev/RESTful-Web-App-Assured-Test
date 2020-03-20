package ru.web.webapp.restassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersWebServiceEndpointTest {

    private final String CONTEXT_PATH = "/web-app";
    private final String EMAIL_ADDRESS = "4yma545@gmail.com";
    private final String JSON = "application/json";
    private static String authorizationHeader;
    private static String userId;
    private static List<Map<String, String>> addresses;

    @BeforeEach
    void setUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    @Order(1)
    void testUserLogin() {

        Map<String, String> loginDetails = new HashMap<>();
        loginDetails.put("email", EMAIL_ADDRESS);
        loginDetails.put("password", "123");

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .body(loginDetails)
                .when()
                .post("/login")

                .then()
                .statusCode(200)
                .extract()
                .response();

        authorizationHeader = response.header("Authorization");
        userId = response.header("UserID");

        assertNotNull(authorizationHeader);
        assertNotNull(userId);
    }

    @Test
    @Order(2)
    void testGetUserDetails() {

        Response response = given()
                .header("Authorization", authorizationHeader)
                .accept(JSON)
                .when()
                .pathParam("id", userId)
                .get("/users/{id}")

                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String userPublicId = response.jsonPath().getString("userId");
        String userEmail = response.jsonPath().getString("email");
        String userFirstName = response.jsonPath().getString("firstName");
        String userLastName = response.jsonPath().getString("lastName");

        addresses = response.jsonPath().getList("addresses");
        String addressId = addresses.get(0).get("addressId");

        assertNotNull(userPublicId);
        assertNotNull(userEmail);
        assertNotNull(userFirstName);
        assertNotNull(userLastName);

        assertEquals(EMAIL_ADDRESS, userEmail);
        assertEquals(2, addresses.size());
        assertEquals(30, addressId.length());
    }

    @Test
    @Order(3)
    void testUpdateUserDetails() {

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "Plag");
        userDetails.put("lastName", "Gagov");

        Response response = given()
                .header("Authorization", authorizationHeader)
                .contentType(JSON)
                .accept(JSON)
                .body(userDetails)
                .when()
                .pathParam("id", userId)
                .put("/users/{id}")

                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String userFirstName = response.jsonPath().getString("firstName");
        String userLastName = response.jsonPath().getString("lastName");

        List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");

        assertNotNull(userFirstName);
        assertNotNull(userLastName);

        assertEquals("Plag", userFirstName);
        assertEquals("Gagov", userLastName);
        assertEquals(addresses.size(), storedAddresses.size());
        assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
    }

    @Test
    @Order(4)
    void testDeleteUserDetails() {

        Response response = given()
                .header("Authorization", authorizationHeader)
                .accept(JSON)
                .when()
                .pathParam("id", userId)
                .delete("/users/{id}")

                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String operationResult = response.jsonPath().getString("operationResult");

        assertEquals("SUCCESS", operationResult);
    }
}
