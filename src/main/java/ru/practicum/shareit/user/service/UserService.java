package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAll();

    UserDto getById(Long userId);

    UserDto create(UserDto user);

    UserDto update(Long userId, UserDto user);

    void delete(Long userId);
}