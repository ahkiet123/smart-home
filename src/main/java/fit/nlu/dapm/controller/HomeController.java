package fit.nlu.dapm.controller;

import fit.nlu.dapm.dto.ApiResponse;
import fit.nlu.dapm.dto.home.HomeRequest;
import fit.nlu.dapm.dto.home.HomeResponse;
import fit.nlu.dapm.security.SecurityUtil;
import fit.nlu.dapm.service.HomeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/homes")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<HomeResponse>> createHome(@Valid @RequestBody HomeRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        HomeResponse response = homeService.createHome(userId, request);
        return new ResponseEntity<>(ApiResponse.success("Home created", response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HomeResponse>>> getHomes(Pageable pageable) {
        Long userId = securityUtil.getCurrentUserId();
        Page<HomeResponse> response = homeService.getHomes(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Homes retrieved", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HomeResponse>> getHome(@PathVariable Long id) {
        HomeResponse response = homeService.getHome(id);
        return ResponseEntity.ok(ApiResponse.success("Home retrieved", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HomeResponse>> updateHome(@PathVariable Long id, @Valid @RequestBody HomeRequest request) {
        HomeResponse response = homeService.updateHome(id, request);
        return ResponseEntity.ok(ApiResponse.success("Home updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHome(@PathVariable Long id) {
        homeService.deleteHome(id);
        return ResponseEntity.ok(ApiResponse.success("Home deleted", null));
    }
}
