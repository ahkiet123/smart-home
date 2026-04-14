package fit.nlu.dapm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_posts")
@Getter
@Setter
public class BlogPost extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}

