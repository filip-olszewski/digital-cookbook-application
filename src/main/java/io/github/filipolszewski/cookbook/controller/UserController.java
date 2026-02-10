package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.user.UserPrivateProfileResponse;
import io.github.filipolszewski.cookbook.dto.user.UserPublicProfileResponse;
import io.github.filipolszewski.cookbook.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1 + "/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserPublicProfileResponse> getPublicUserProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getPublicUserProfile(username));
    }

    @GetMapping("/me")
    public ResponseEntity<UserPrivateProfileResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }
}
