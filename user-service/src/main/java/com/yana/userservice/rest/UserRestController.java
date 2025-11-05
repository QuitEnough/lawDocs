package com.yana.userservice.rest;

import com.yana.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserRestController {

    private final UserService userService;

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

}
