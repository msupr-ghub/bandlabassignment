package com.bandlab.assignment.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private String caption;
    private String imageUrl;
    @CreationTimestamp
    private Date createdAt;
    @OneToMany(mappedBy = "post" , fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Comment> comments;
}
