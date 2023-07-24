package ru.practicum.shareit.user.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.create.UnionService;
import ru.practicum.shareit.exception.WrongAccessException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userRepository;
    private final UnionService unionService;

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {

        User user = UserMapper.returnUser(userDto);
        userRepository.save(user);

        return UserMapper.returnUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, long userId) {

        User user = UserMapper.returnUser(userDto);
        user.setId(userId);

        unionService.checkUser(userId);
        User newUser = userRepository.findById(userId).get();

        if (user.getName() != null) {
            newUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            List<User> findEmail = userRepository.findByEmail(user.getEmail());

            if (!findEmail.isEmpty() && findEmail.get(0).getId() != userId) {
                throw new WrongAccessException("there is already a user with an email " + user.getEmail());
            }
            newUser.setEmail(user.getEmail());
        }

        userRepository.save(newUser);

        return UserMapper.returnUserDto(newUser);
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {

        unionService.checkUser(userId);
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long userId) {

        unionService.checkUser(userId);
        return UserMapper.returnUserDto(userRepository.findById(userId).get());
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {

        return UserMapper.returnUserDtoList(userRepository.findAll());
    }
}