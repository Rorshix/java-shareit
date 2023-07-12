package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.create.OnCreate;
import ru.practicum.shareit.create.OnUpdate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") Long userId) {
        return userService.getById(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Validated(OnCreate.class) UserDto user) {
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId,
                              @RequestBody @Validated(OnUpdate.class) UserDto user) {
        return userService.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.delete(userId);
    }

}
