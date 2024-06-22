package com.bandlab.assignment.repository;

import com.bandlab.assignment.dto.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments c WHERE p.id >= :start AND (c.id IN (SELECT c2.id FROM Comment c2 WHERE c2.post.id = p.id ORDER BY c2.createdAt DESC LIMIT 2) OR NOT EXISTS (SELECT c2.id FROM Comment c2 WHERE c2.post.id = p.id)) ORDER BY p.createdAt DESC")
    List<Post> getPosts(@Param("start") Long start, Pageable pageable);

}
