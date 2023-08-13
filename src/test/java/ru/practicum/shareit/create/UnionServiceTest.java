package ru.practicum.shareit.create;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.user.UserStorage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UnionServiceTest {

    @Autowired
    private UnionService unionService;

    @MockBean
    private UserStorage userStorage;

    @MockBean
    private ItemStorage itemStorage;

    @MockBean
    private BookingStorage bookingStorage;

    @MockBean
    private ItemRequestStorage itemRequestStorage;

    @Test
    void checkUser() {
        when(userStorage.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> unionService.checkUser(1L));
    }

    @Test
    void checkItem() {
        when(itemStorage.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> unionService.checkItem(1L));
    }

    @Test
    void checkBooking() {
        when(bookingStorage.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> unionService.checkBooking(1L));
    }

    @Test
    void checkRequest() {
        when(itemRequestStorage.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> unionService.checkRequest(1L));
    }

    @Test
    void checkPageSize() {
        assertThrows(ValidationException.class, () -> unionService.checkPageSize(0,0));
        assertThrows(ValidationException.class, () -> unionService.checkPageSize(5,-5));
        assertThrows(ValidationException.class, () -> unionService.checkPageSize(5,0));
        assertThrows(ValidationException.class, () -> unionService.checkPageSize(-5,5));
    }
}