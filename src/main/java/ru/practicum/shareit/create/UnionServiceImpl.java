package ru.practicum.shareit.create;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class UnionServiceImpl implements UnionService {

    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final ItemRequestStorage itemRequestStorage;

    @Override
    public void checkUser(Long userId) {

        if (!userStorage.existsById(userId)) {
            throw new NotFoundException(User.class, "User id " + userId + " not found.");
        }
    }

    @Override
    public void checkItem(Long itemId) {

        if (!itemStorage.existsById(itemId)) {
            throw new NotFoundException(Item.class, "Item id " + itemId + " not found.");
        }
    }

    @Override
    public void checkBooking(Long bookingId) {

        if (!bookingStorage.existsById(bookingId)) {
            throw new NotFoundException(Booking.class, "Booking id " + bookingId + " not found.");
        }
    }

    @Override
    public void checkRequest(Long requestId) {

        if (!itemRequestStorage.existsById(requestId)) {
            throw new NotFoundException(ItemRequest.class, "Request id " + requestId + " not found.");
        }
    }

    @Override
    public PageRequest checkPageSize(Integer from, Integer size) {

        if (from == 0 && size == 0) {
            throw new ValidationException("\"size\" and \"from\"must be not equal 0");
        }

        if (size <= 0) {
            throw new ValidationException("\"size\" must be greater than 0");
        }

        if (from < 0) {
            throw new ValidationException("\"from\" must be greater than or equal to 0");
        }
        return PageRequest.of(from / size, size);
    }
}