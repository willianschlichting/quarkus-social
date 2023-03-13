package io.github.williansch.quarkussocial.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.github.williansch.quarkussocial.domain.model.Follower;
import io.github.williansch.quarkussocial.domain.model.User;
import io.github.williansch.quarkussocial.domain.repository.FollowerRepository;
import io.github.williansch.quarkussocial.domain.repository.UserRepository;
import io.github.williansch.quarkussocial.rest.dto.FollowerDto;
import io.github.williansch.quarkussocial.rest.dto.FollowerResponseDto;
import io.github.williansch.quarkussocial.rest.dto.FollowersPeruSerResponseDto;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerDto followerDto) {

        if (userId.equals(followerDto.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself").build();
        }

        User user = userRepository.findById(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        User followerUser = userRepository.findById(followerDto.getFollowerId());

        if (followerUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!followerRepository.followers(followerUser, user)) {
            Follower entity = new Follower();
            entity.setUser(user);
            entity.setFollower(followerUser);
            followerRepository.persist(entity);
        }

        return Response.noContent().build();

    }


    @GET
    public Response listFolloweResponse(@PathParam("userId") Long userId) {

        User user = userRepository.findById(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Follower> followers = followerRepository.findByUser(userId);

        List<FollowerResponseDto> content = followers.stream().map(f -> FollowerResponseDto.fromEntity(f)).collect(Collectors.toList());

        FollowersPeruSerResponseDto responseEntity = new FollowersPeruSerResponseDto();
        responseEntity.setContent(content);
        responseEntity.setFollowersCount(content.size());

        return Response.ok(responseEntity).build();

    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
                
        followerRepository.deleteByFollowerAndUser(user.getId(), followerId);

        return Response.noContent().build();
    }
    
}
