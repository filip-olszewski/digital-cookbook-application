package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.user.UserPrivateProfileResponse;
import io.github.filipolszewski.cookbook.dto.user.UserPublicProfileResponse;
import io.github.filipolszewski.cookbook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1 + "/users")
@Tag(name = "Users", description = "Endpoints for retrieving user profiles")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get public user profile", description = "Public endpoint to retrieve basic profile info for a given username.")
    @GetMapping("/{username}")
    public ResponseEntity<UserPublicProfileResponse> getPublicUserProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getPublicUserProfile(username));
    }

    @Operation(
        summary = "Get current user profile",
        description = "Retrieves the private profile info of the currently authenticated user. Requires a valid JWT.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/me")
    public ResponseEntity<UserPrivateProfileResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }
}