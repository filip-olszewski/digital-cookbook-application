package io.github.filipolszewski.cookbook.security;

import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(User.class, "email", email)));

        return new CustomUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
