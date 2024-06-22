package com.bandlab.assignment.service.impl;

import com.bandlab.assignment.api.request.AddCommentRequest;
import com.bandlab.assignment.api.request.CreatePostRequest;
import com.bandlab.assignment.dto.Comment;
import com.bandlab.assignment.dto.Post;
import com.bandlab.assignment.repository.CommentRepository;
import com.bandlab.assignment.repository.PostRepository;
import com.bandlab.assignment.service.StorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private PostRepository postRepository;

    private CommentRepository commentRepository;

    private StorageService storageService;


    @Autowired
    public PostService(PostRepository postRepository, CommentRepository commentRepository, StorageService storageService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.storageService = storageService;
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getPosts(Long start, Integer pageSize) {
        Pageable pageable = Pageable.ofSize(pageSize);
        return postRepository.getPosts(start, pageable);
    }

    public Post createPost(CreatePostRequest createPostRequest, MultipartFile imageFile) throws IOException {
        Post post = createPostRequest.toPost();
        String imageUrl = storageService.uploadFile(imageFile);
        post.setImageUrl(imageUrl);
        return postRepository.saveAndFlush(post);
    }

    @Transactional
    public boolean deletePost(Long id) {
        postRepository.deleteById(id);
        return true;
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public boolean deleteComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        comment.ifPresent(commentRepository::delete);
        commentRepository.flush();
        if (!commentRepository.existsById(commentId)){
            return true;
        }
        return false;
    }

    public Comment addComment(AddCommentRequest addCommentRequest) {
        Comment comment = addCommentRequest.toComment();
        comment.setPost(postRepository.findById(addCommentRequest.getPostId()).get());
        commentRepository.save(comment);
        return comment;
    }

}
