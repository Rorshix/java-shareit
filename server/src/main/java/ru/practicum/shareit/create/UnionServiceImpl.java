package ru.practicum.shareit.create;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

@Service
@RequiredArgsConstructor
public class UnionServiceImpl implements UnionService {

    private final BookingStorage bookingRepository;
    private final UserStorage userRepository;
    private final ItemStorage itemRepository;
    private final ItemRequestStorage itemRequestRepository;

    @Override
    public void checkUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(User.class, "User id " + userId + " not found.");
        }
    }

    @Override
    public void checkItem(Long itemId) {

        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(Item.class, "Item id " + itemId + " not found.");
        }
    }

    @Override
    public void checkBooking(Long bookingId) {

        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException(Booking.class, "Booking id " + bookingId + " not found.");
        }
    }

    @Override
    public void checkRequest(Long requestId) {

        if (!itemRequestRepository.existsById(requestId)) {
            throw new NotFoundException(ItemRequest.class, "Request id " + requestId + " not found.");
        }
    }
}