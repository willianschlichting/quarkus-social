package io.github.williansch.quarkussocial.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.github.williansch.quarkussocial.domain.model.Follower;
import io.github.williansch.quarkussocial.domain.model.User;
import io.github.williansch.quarkussocial.domain.repository.FollowerRepository;
import io.github.williansch.quarkussocial.domain.repository.UserRepository;
import io.github.williansch.quarkussocial.rest.dto.FollowerDto;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import static javax.ws.rs.core.Response.Status.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    public void setup() {
        User user = new User();
        user.setName("Willian");
        user.setAge(32);
        userRepository.persist(user);
        userId = user.getId();



        User follower = new User();
        follower.setName("Willian");
        follower.setAge(32);
        userRepository.persist(follower);
        followerId = follower.getId();

        Follower followerEntity = new Follower();
        followerEntity.setUser(user);
        followerEntity.setFollower(follower);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("Should return conflict when follower it's equal to user")
    @Order(1)
    public void saveUserAsFollower(){
        FollowerDto followerDto = new FollowerDto();
        followerDto.setFollowerId(userId);
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .body(followerDto)
        .when()
            .put()
        .then()
            .statusCode(CONFLICT.getStatusCode());
            
    }

    @Test
    @DisplayName("Should return not found when user doesn't exists")
    @Order(2)
    public void saveUserInexistent(){
        FollowerDto followerDto = new FollowerDto();
        followerDto.setFollowerId(userId);
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", 9999)
            .body(followerDto)
        .when()
            .put()
        .then()
            .statusCode(NOT_FOUND.getStatusCode());
            
    }

    @Test
    @DisplayName("Should return not found when follower doesn't exists")
    @Order(3)
    public void saveFollowerInexistent(){
        FollowerDto followerDto = new FollowerDto();
        followerDto.setFollowerId(Long.parseLong("999"));
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .body(followerDto)
        .when()
            .put()
        .then()
            .statusCode(NOT_FOUND.getStatusCode());
            
    }

    @Test
    @DisplayName("Should follow an user")
    @Order(4)
    public void followerUserTest(){
        FollowerDto followerDto = new FollowerDto();
        followerDto.setFollowerId(followerId);
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .body(followerDto)
        .when()
            .put()
        .then()
            .statusCode(NO_CONTENT.getStatusCode());
            
    }

    @Test
    @DisplayName("Should return not found when user doesn't exists")
    @Order(5)
    public void listFollowerUserNotExists() {
        given()
            .pathParam("userId", 999)
        .when()
            .get()
        .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should list followers from an user")
    @Order(6)
    public void listFollower() {
        Response response = given()
            .pathParam("userId", userId)
        .when()
            .get()
        .then()
            .statusCode(OK.getStatusCode())
            .extract().response();

        String count = response.jsonPath().getString("followersCount");
        List<Object> followersContent = response.jsonPath().getList("content");

        assertEquals("1", count);
        assertEquals(1, followersContent.size());
            
    }

    @Test
    @DisplayName("Should unfollow user")
    public void onUnfollowUser() {
        given()
            .pathParam("userId", userId)
            .queryParam("followerId", followerId)
        .when()
            .delete()
        .then()
            .statusCode(NO_CONTENT.getStatusCode());
        
    }
}