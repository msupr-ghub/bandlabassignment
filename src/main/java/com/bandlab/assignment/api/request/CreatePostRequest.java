package com.bandlab.assignment.api.request;

import com.bandlab.assignment.dto.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreatePostRequest {
    private String caption;

    private Long userId;

    public Post toPost() {
        Post post = new Post();
        post.setCaption(caption);
        post.setUserId(userId);
        return post;
    }
}
