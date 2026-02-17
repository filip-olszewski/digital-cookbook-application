package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.user.UserPrivateProfileResponse;
import io.github.filipolszewski.cookbook.dto.user.UserPublicProfileResponse;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.UserMapper;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.repository.FavouriteRepository;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.repository.ReviewRepository;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import io.github.filipolszewski.cookbook.security.UserContext;
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;

    private final RecipeRepository recipeRepository;
    private final ReviewRepository reviewRepository;
    private final FavouriteRepository favouriteRepository;

    public UserPublicProfileResponse getPublicUserProfile(String username) {
        log.debug("Fetching public profile for username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Could not find a user with username: " + username));

        long recipeCount = recipeRepository.countByAuthorUsername(username);

        return userMapper.toPublicProfile(user, recipeCount);
    }

    public UserPrivateProfileResponse getMyProfile() {
        Long id = userContext.getCurrentUserId();
        log.debug("Fetching private profile for current user ID: {}", id);
        User currentUser = findUserById(id);

        long recipeCount = recipeRepository.countByAuthorId(id);
        long reviewCount = reviewRepository.countByUserId(id);
        long favouriteCount = favouriteRepository.countByUserId(id);

        return userMapper.toPrivateProfile(currentUser, recipeCount, reviewCount, favouriteCount);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(User.class, "id", id)));
    }
}