package io.github.williansch.quarkussocial.rest;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.github.williansch.quarkussocial.domain.model.User;
import io.github.williansch.quarkussocial.domain.repository.UserRepository;
import io.github.williansch.quarkussocial.rest.dto.ResponseError;
import io.github.williansch.quarkussocial.rest.dto.UserDto;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    

    @Inject
    UserRepository userRepository;

    @Inject
    Validator validator;

    @POST
    @Transactional
    public Response createUser(UserDto userDto) {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        if (!violations.isEmpty()) {
            return ResponseError.createFromViolations(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATS);
        }
        User user = new User(userDto);
        userRepository.persist(user);
        return Response
                .status(Response.Status.CREATED.getStatusCode())
                .entity(user)
                .build();
    }

    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") Long id) {
        return userRepository.findById(id);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, UserDto userDto) {
        User user = userRepository.findById(id);
        if (user == null) {
            return Response.status(404).build();
        }
        user.setName(userDto.getName());
        user.setAge(userDto.getAge());
        userRepository.persist(user);
        return Response.ok(user).build();
    }
    
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            return Response.status(404).build();
        }
        userRepository.delete(user);
        return Response.noContent().build();
    }

    @GET
    public List<User> listAllUsers() {
        return userRepository.findAll().list();
    }
    
}
