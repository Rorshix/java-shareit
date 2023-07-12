package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public Collection<UserDto> getAll() {
        return UserMapper.toDto(userStorage.findAll());
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toDto(userStorage.findById(userId));
    }

    @Override
    public UserDto create(UserDto user) {
        if (userStorage.findByEmail(user.getEmail()) != null) {
            throw new ValidationException("This email is already exists.");
        }
        return UserMapper.toDto(userStorage.create(UserMapper.fromDto(user)));
    }

    @Override
    public UserDto update(Long userId, UserDto user) {
        if (userStorage.findByEmail(user.getEmail()) != null) {
            throw new ValidationException("This email is already exists.");
        }
        return UserMapper.toDto(userStorage.update(userId, UserMapper.fromDto(user)));
    }

    @Override
    public void delete(Long userId) {
        userStorage.remove(userId);
    }
}