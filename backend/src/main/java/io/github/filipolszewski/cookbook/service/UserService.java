package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.user.UserCreateRequest;
import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.mapper.UserMapper;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.model.enumeration.Role;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserSummaryResponse registerUser(UserCreateRequest request) {

        if(userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("User with this address email is already registered!");
        }

        User user = userMapper.toEntity(request);
        user.setRole(Role.USER);
        // Hash password
        return userMapper.toSummary(userRepository.save(user));
    }

}
