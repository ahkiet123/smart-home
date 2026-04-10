package fit.nlu.dapm.controller;

import fit.nlu.dapm.dto.ApiResponse;
import fit.nlu.dapm.dto.user.UpdateProfileRequest;
import fit.nlu.dapm.dto.user.UserProfileResponse;
import fit.nlu.dapm.security.SecurityUtil;
import fit.nlu.dapm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        Long userId = securityUtil.getCurrentUserId();
        UserProfileResponse response = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", response));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", null));
    }
}
