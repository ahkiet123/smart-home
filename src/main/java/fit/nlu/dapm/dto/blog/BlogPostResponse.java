package fit.nlu.dapm.dto.blog;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BlogPostResponse {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String thumbnailUrl;
    private String authorName;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

