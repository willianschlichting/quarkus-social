package io.github.williansch.quarkussocial.rest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.github.williansch.quarkussocial.domain.model.Post;
import io.github.williansch.quarkussocial.domain.model.User;
import io.github.williansch.quarkussocial.domain.repository.FollowerRepository;
import io.github.williansch.quarkussocial.domain.repository.PostRepository;
import io.github.williansch.quarkussocial.domain.repository.UserRepository;
import io.github.williansch.quarkussocial.rest.dto.PostDto;
import io.github.williansch.quarkussocial.rest.dto.PostResponseDto;
import io.github.williansch.quarkussocial.rest.dto.error.ResponseError;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;

@Path("users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @Inject
    UserRepository userRepository;

    @Inject
    PostRepository postRepository;

    @Inject
    FollowerRepository followerRepository;

    @Inject
    Validator validator;

    @POST
    @Transactional
    public Response save(@PathParam("userId") Long userId, PostDto postDto) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(404).build();
        }

        Set<ConstraintViolation<PostDto>> violations = validator.validate(postDto);
        if (!violations.isEmpty()) {
            return ResponseError.createFromViolations(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATS);
        }

        Post post = new Post();
        post.setText(postDto.getText());
        post.setUser(user);

        postRepository.persist(post);
        return Response.ok(post).build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(404).build();
        }

        if (followerId == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("you forgot the header followerId")
                    .build();
        }

        User follower = userRepository.findById(followerId);

        if (follower == null) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("inexistent Foolower")
                    .build();
        }

        if(!followerRepository.followers(follower, user)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        List<Post> list = postRepository.find("user", Sort.by("dateTime", Direction.Descending), user).list();

        List<PostResponseDto> responses = list.stream().map(p -> PostResponseDto.fromEntity(p)).collect(Collectors.toList());
        return Response.ok(responses).build();
    }
    
}
