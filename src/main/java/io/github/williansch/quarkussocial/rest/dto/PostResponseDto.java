package io.github.williansch.quarkussocial.rest.dto;

import java.util.Date;

import io.github.williansch.quarkussocial.domain.model.Post;
import lombok.Data;

@Data
public class PostResponseDto {

    private String text;
    private Date dateTime;

    public static PostResponseDto fromEntity(Post post) {
        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setText(post.getText());
        postResponseDto.setDateTime(post.getDateTime());
        return postResponseDto;
    }
    
}
