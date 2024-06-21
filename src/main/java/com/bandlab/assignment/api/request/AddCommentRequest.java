package com.bandlab.assignment.api.request;

import com.bandlab.assignment.dto.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AddCommentRequest {
    private Long postId;
    @NotBlank
    private String content;
    @NotNull
    private Long creator;

    public Comment toComment() {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreator(creator);
        return comment;
    }
}
