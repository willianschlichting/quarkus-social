package io.github.williansch.quarkussocial.rest.dto;

import io.github.williansch.quarkussocial.domain.model.Follower;
import lombok.Data;

@Data
public class FollowerResponseDto {
    private Long id;
    private String name;

    public static FollowerResponseDto fromEntity(Follower follower) {
        FollowerResponseDto response = new FollowerResponseDto();
        response.setId(follower.getId());
        response.setName(follower.getFollower().getName());
        return response;
    }
}
