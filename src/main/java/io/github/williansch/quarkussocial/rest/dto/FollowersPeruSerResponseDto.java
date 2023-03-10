package io.github.williansch.quarkussocial.rest.dto;

import java.util.List;

import lombok.Data;

@Data
public class FollowersPeruSerResponseDto {

    private Integer followersCount;

    private List<FollowerResponseDto> content;
    
}
