package io.github.williansch.quarkussocial.rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.github.williansch.quarkussocial.rest.dto.UserDto;
import io.github.williansch.quarkussocial.rest.dto.error.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserResourceTest {

    @TestHTTPResource("/users")
    URL serviceApiUrl;

    @Test
    @DisplayName("Should create an user successfully")
    @Order(1)
    public void createUserTest() {

        UserDto userDto = new UserDto();
        userDto.setName("joao");
        userDto.setAge(32);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(userDto)
        .when()
            .post(serviceApiUrl)
        .then()
            .extract().response();

        assertEquals(CREATED.getStatusCode(), response.getStatusCode());
        
        assertNotNull(response.jsonPath().getString("id"));
        assertNotNull(response.jsonPath().getString("name"));
        assertEquals("joao", response.jsonPath().getString("name"));

    }

    @Test
    @DisplayName("Should resturn error when json is not valid")
    @Order(2)
    public void createUserValidationError() {

        UserDto userDto = new UserDto();

        userDto.setName(null);
        userDto.setAge(null);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(userDto)
        .when()
            .post(serviceApiUrl)
        .then()
            .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATS, response.getStatusCode());
        assertEquals("Validation error", response.jsonPath().getString("message"));
        List<Map<String, String>> errors = response.jsonPath().getList("errors");

        assertNotNull(errors);
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
        
    }

    @Test
    @DisplayName("Should list all users")
    @Order(3)
    public void listAllUsers() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get(serviceApiUrl)
        .then()
            .statusCode(200);
    }
    
}
