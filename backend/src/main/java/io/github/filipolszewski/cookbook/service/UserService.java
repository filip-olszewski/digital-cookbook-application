package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.user.UserPublicProfileResponse;
import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.UserMapper;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.model.enumeration.Role;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final RecipeRepository recipeRepository;

    public UserPublicProfileResponse getPublicUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Could not find a user with username: " + username));
        int authoredRecipeCount = recipeRepository.getRecipeCountByAuthor(username);
        return userMapper.toPublicDetails(user, authoredRecipeCount);
    }
}
