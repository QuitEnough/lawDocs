package com.yana.userservice.rest;

import com.yana.userservice.repository.UserRepository;
import com.yana.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public Object getUserByIdOrEmail(@RequestParam(required = false) Long id,
                                     @RequestParam(required = false) String email) {
        if (id != null) {
            return userService.getUserById(id);
        }
        if (email != null) {
            return userService.getUserByEmail(email);
        }

        return List.of();
    }

    @GetMapping("/{userId}/exists")
    public Boolean userExists(@PathVariable Long userId) {
        return userRepository.existsById(userId);
    }

}
