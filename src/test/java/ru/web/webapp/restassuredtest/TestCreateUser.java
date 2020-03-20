package ru.web.webapp.restassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class TestCreateUser {

    private final String CONTEXT_PATH = "/web-app";
    private final String JSON = "application/json";

    @BeforeEach
    void setUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    void testCreateUser() {

        List<Map<String, Object>> userAddresses = new ArrayList<>();

        Map<String, Object> shippingAddress = new HashMap<>();
        shippingAddress.put("city", "Moscow");
        shippingAddress.put("country", "Russia");
        shippingAddress.put("streetName", "123 Street");
        shippingAddress.put("postalCode", "123100");
        shippingAddress.put("type", "shipping");

        Map<String, Object> billingAddress = new HashMap<>();
        billingAddress.put("city", "Moscow");
        billingAddress.put("country", "Russia");
        billingAddress.put("streetName", "123 Street");
        billingAddress.put("postalCode", "123100");
        billingAddress.put("type", "billing");

        userAddresses.add(shippingAddress);
        userAddresses.add(billingAddress);

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "Andrey");
        userDetails.put("lastName", "Gagovich");
        userDetails.put("email", "4yma545@gmail.com");
        userDetails.put("password", "123");
        userDetails.put("addresses", userAddresses);

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .body(userDetails)
                .when()
                .post("/users")

                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String userId = response.jsonPath().get("userId");

        assertNotNull(userId);
        assertEquals(30, userId.length());

        String bodyString = response.body().asString();
        try {
            JSONObject responseBodyJson = new JSONObject(bodyString);
            JSONArray addresses = responseBodyJson.getJSONArray("addresses");

            assertNotNull(addresses);
            assertEquals(2, addresses.length());

            String addressId = addresses.getJSONObject(0).getString("addressId");
            assertNotNull(addressId);
            assertEquals(30, addressId.length());
        } catch (JSONException e) {
            fail(e.getMessage());
        }
    }
}
