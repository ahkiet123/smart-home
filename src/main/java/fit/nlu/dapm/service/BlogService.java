package fit.nlu.dapm.service;

import fit.nlu.dapm.dto.blog.BlogPostResponse;
import fit.nlu.dapm.entity.BlogPost;
import fit.nlu.dapm.exception.ResourceNotFoundException;
import fit.nlu.dapm.repository.BlogPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BlogService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    public Page<BlogPostResponse> getBlogPosts(Pageable pageable) {
        return blogPostRepository.findAll(pageable).map(this::toResponse);
    }

    public BlogPostResponse getBlogPost(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found"));
        return toResponse(blogPost);
    }

    private BlogPostResponse toResponse(BlogPost blogPost) {
        BlogPostResponse response = new BlogPostResponse();
        response.setId(blogPost.getId());
        response.setTitle(blogPost.getTitle());
        response.setSummary(blogPost.getSummary());
        response.setContent(blogPost.getContent());
        response.setThumbnailUrl(blogPost.getThumbnailUrl());
        response.setAuthorName(blogPost.getAuthorName());
        response.setPublishedAt(blogPost.getPublishedAt());
        response.setCreatedAt(blogPost.getCreatedAt());
        response.setUpdatedAt(blogPost.getUpdatedAt());
        return response;
    }
}

