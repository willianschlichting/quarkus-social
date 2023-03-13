package io.github.williansch.quarkussocial.rest;

import io.github.williansch.quarkussocial.domain.model.Follower;
import io.github.williansch.quarkussocial.domain.model.Post;
import io.github.williansch.quarkussocial.domain.model.User;
import io.github.williansch.quarkussocial.domain.repository.FollowerRepository;
import io.github.williansch.quarkussocial.domain.repository.PostRepository;
import io.github.williansch.quarkussocial.domain.repository.UserRepository;
import io.github.williansch.quarkussocial.rest.dto.PostDto;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static javax.ws.rs.core.Response.Status.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setup() {
        User user = new User();
        user.setName("Fulano");
        user.setAge(28);
        userRepository.persist(user);
        userId = user.getId();

        User userNotFollower = new User();
        userNotFollower.setName("Fulano");
        userNotFollower.setAge(28);
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        User userFollower = new User();
        userFollower.setName("Fulano");
        userFollower.setAge(28);
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);

        Post post = new Post();
        post.setText("This is a post");
        post.setUser(user);
        postRepository.persist(post);

    }

    @Test
    @DisplayName("should create a post for a user")
    @Order(1)
    public void createPostTest() {
        PostDto postDto = new PostDto();
        postDto.setText("novo post");

        given()
            .contentType(ContentType.JSON)
            .body(postDto)
            .pathParam("userId", userId)
        .when()
            .post()
        .then()
            .statusCode(CREATED.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    @Order(2)
    public void inexistentUserTest() {
        PostDto postDto = new PostDto();
        postDto.setText("novo post");

        given()
            .contentType(ContentType.JSON)
            .body(postDto)
            .pathParam("userId", 999)
        .when()
            .post()
        .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 when user doesn't exists")
    @Order(2)
    public void listPostUsersNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", 999)
        .when()
            .get()
        .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }


    @Test
    @DisplayName("Should return 400 when follower id header is not present")
    @Order(2)
    public void listPostUsersFollowerNotPresent() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
        .when()
            .get()
        .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .body(Matchers.is("you forgot the header followerId"));
    }

    @Test
    @DisplayName("Should return 400 when follower doesn't exists")
    @Order(2)
    public void listPostUsersFollowerNotExists() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .header("followerId", 999)
        .when()
            .get()
        .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .body(Matchers.is("inexistent Follower"));
    }


    @Test
    @DisplayName("Should return 403 when follower doesn't follow the user")
    @Order(2)
    public void listPostUsersFollowerNotFollow() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .header("followerId", userNotFollowerId)
        .when()
            .get()
        .then()
            .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    @DisplayName("Should list post users")
    @Order(3)
    public void listPostUsers() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .header("followerId", userFollowerId)
        .when()
            .get()
        .then()
            .statusCode(OK.getStatusCode())
            .body("size()", Matchers.is(1));
    }
    
}
