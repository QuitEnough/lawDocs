package com.yana.userservice.service;

import com.yana.userservice.dto.UserCreateRequest;
import com.yana.userservice.dto.UserResponse;
import com.yana.userservice.entity.Role;
import com.yana.userservice.entity.User;
import com.yana.userservice.exception.UserNotFoundException;
import com.yana.userservice.mapper.UserMapper;
import com.yana.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    @Override
    public void addUser(UserCreateRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    @Override
    public UserResponse getUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with given id not found"));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return null;
    }

    @Override
    public List<?> getUserByValue(String value) {

        Optional<User> userById = userRepository.findById(Long.getLong(value));
        Optional<User> userByEmail = userRepository.findByEmail(value);

        if (userById.isPresent()) {
            User user = userById.get();
            return List.of(userMapper.toUserResponse(user));
        }

        if (userByEmail.isPresent()) {
            User user = userByEmail.get();
            return List.of(userMapper.toUserResponse(user));
        } else {
            return List.of();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with given email is not found"));

        return userMapper.toUserDetails(user);
    }

}
