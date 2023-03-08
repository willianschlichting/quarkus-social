package io.github.williansch.quarkussocial.rest.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class PostDto {

    @NotBlank(message = "text is required")
    private String text;
}
