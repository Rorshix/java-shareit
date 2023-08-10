package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.WrongAccessException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserStorage userStorage;

    private User firstUser;

    private User secondUser;

    private UserDto firstUserDto;

    private UserDto secondUserDto;

    @BeforeEach
    void beforeEach() {
        firstUser = User.builder()
                .id(1L)
                .name("Anna")
                .email("anna@yandex.ru")
                .build();

        firstUserDto = UserMapper.returnUserDto(firstUser);

        secondUser = User.builder()
                .id(2L)
                .name("Tiana")
                .email("tiana@yandex.ru")
                .build();

        secondUserDto = UserMapper.returnUserDto(secondUser);
    }

    @Test
    void addUser() {
        when(userStorage.save(any(User.class))).thenReturn(firstUser);

        UserDto userDtoTest = userService.addUser(firstUserDto);

        assertEquals(userDtoTest.getId(), firstUserDto.getId());
        assertEquals(userDtoTest.getName(), firstUserDto.getName());
        assertEquals(userDtoTest.getEmail(), firstUserDto.getEmail());

        verify(userStorage, times(1)).save(firstUser);
    }

    @Test
    void updateUser() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(userStorage.findByEmail(anyString())).thenReturn(List.of(firstUser));
        when(userStorage.save(any(User.class))).thenReturn(firstUser);

        firstUserDto.setName("Sofia");
        firstUserDto.setEmail("Sofia@yandex.ru");

        UserDto userDtoUpdated = userService.updateUser(firstUserDto, 1L);

        assertEquals(userDtoUpdated.getName(), firstUserDto.getName());
        assertEquals(userDtoUpdated.getEmail(), firstUserDto.getEmail());

        verify(userStorage, times(1)).findByEmail(firstUser.getEmail());
        verify(userStorage, times(1)).save(firstUser);
    }

    @Test
    void updateUser_wrongEmail() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(userStorage.findByEmail(anyString())).thenReturn(List.of(firstUser));

        firstUserDto.setEmail("");
        assertThrows(WrongAccessException.class, () -> userService.updateUser(firstUserDto, 2L));
    }

    @Test
    void deleteUser() {
        when(userStorage.existsById(anyLong())).thenReturn(true);

        userService.deleteUser(1L);

        verify(userStorage, times(1)).deleteById(1L);
    }

    @Test
    void getUserById() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(firstUser));

        UserDto userDtoTest = userService.getUserById(1L);

        assertEquals(userDtoTest.getId(), firstUserDto.getId());
        assertEquals(userDtoTest.getName(), firstUserDto.getName());
        assertEquals(userDtoTest.getEmail(), firstUserDto.getEmail());

        verify(userStorage, times(1)).findById(1L);
    }

    @Test
    void getAllUsers() {
        when(userStorage.findAll()).thenReturn(List.of(firstUser, secondUser));

        List<UserDto> userDtoList = userService.getAllUsers();

        assertEquals(userDtoList, List.of(firstUserDto, secondUserDto));

        verify(userStorage, times(1)).findAll();
    }
}