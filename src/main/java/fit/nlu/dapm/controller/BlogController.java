package fit.nlu.dapm.controller;

import fit.nlu.dapm.dto.ApiResponse;
import fit.nlu.dapm.dto.blog.BlogPostResponse;
import fit.nlu.dapm.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BlogPostResponse>>> getBlogPosts(Pageable pageable) {
        Page<BlogPostResponse> response = blogService.getBlogPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success("Blog posts retrieved", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogPostResponse>> getBlogPost(@PathVariable Long id) {
        BlogPostResponse response = blogService.getBlogPost(id);
        return ResponseEntity.ok(ApiResponse.success("Blog post retrieved", response));
    }
}

