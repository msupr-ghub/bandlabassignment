package com.bandlab.assignment.controller;

import com.bandlab.assignment.api.request.AddCommentRequest;
import com.bandlab.assignment.api.request.CreatePostRequest;
import com.bandlab.assignment.api.response.GenericApiResponse;
import com.bandlab.assignment.dto.Comment;
import com.bandlab.assignment.dto.Post;
import com.bandlab.assignment.service.impl.PostService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/posts")
public class ApiController {

    private final PostService postService;

    @Autowired
    public ApiController(PostService postService) {
        this.postService = postService;
    }


    @PostMapping
    public ResponseEntity<Post> createPost(@RequestParam("image") MultipartFile imageFile,
                               @RequestParam("caption") String caption, @RequestParam("userId") Long userId){
        if (!isSupportedImageType(imageFile.getOriginalFilename())){
            GenericApiResponse<String> response = GenericApiResponse.<String>builder().message("Unsupported image type").build();
            return ResponseEntity.badRequest().build();
        }
        CreatePostRequest createPostRequest = new CreatePostRequest(caption, userId);
        try {
            return ResponseEntity.ok(postService.createPost(createPostRequest, imageFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("")
    public List<Post> getPosts(@RequestParam(value = "start", required = false) Long start, @RequestParam(value = "pageSize",required = false) Integer pageSize) {
        if (start == null) {
            start = 0L;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        return postService.getPosts(start, pageSize);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable("postId") Long id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<GenericApiResponse<String>> deletePost(@PathVariable("postId") Long id) {
        Optional<Post> post = postService.getPostById(id);
        if (post.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        postService.deletePost(id);
        return ResponseEntity.ok(GenericApiResponse.<String>builder().message("Post deleted successfully").build());

    }


    @PostMapping("/{postId}/comments")
    public GenericApiResponse<Comment> addComment(@PathVariable("postId") Long postId, @RequestBody @Valid AddCommentRequest addCommentRequest) {
        Optional<Post> post = postService.getPostById(postId);
        if(post.isEmpty()){
            return GenericApiResponse.<Comment>builder().message("Post not found").build();
        }
        addCommentRequest.setPostId(postId);
        Comment comment = postService.addComment(addCommentRequest);
        return GenericApiResponse.<Comment>builder().message("Comment added successfully").entity(comment).build();
    }

    @GetMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Comment> getComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        Optional<Comment> comment = postService.getCommentById(commentId);
        return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public GenericApiResponse<String> deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        Optional<Post> post = postService.getPostById(postId);
        if(post.isEmpty()){
            return GenericApiResponse.<String>builder().message("Post not found").build();
        }
        Optional<Comment> comment = postService.getCommentById(commentId);
        if (comment.isEmpty()){
            return GenericApiResponse.<String >builder().message("Comment not found").build();
        }
        postService.deleteComment(commentId);
        return GenericApiResponse.<String>builder().message("Comment deleted successfully").build();
    }

    private boolean isSupportedImageType(String name) {
        String imageType = name.substring(name.lastIndexOf(".")+1).toLowerCase();
        return imageType.equalsIgnoreCase("jpg") || imageType.equals("jpeg") || imageType.equals("png") || imageType.equals("bmp");

    }



}
