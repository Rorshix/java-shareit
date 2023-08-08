package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.create.UnionService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestStorage itemRequestStorage;

    @MockBean
    private ItemStorage itemStorage;

    @MockBean
    private UserStorage userStorage;

    @MockBean
    private UnionService unionService;


    private User firstUser;
    private User secondUser;
    private ItemRequest firstItemRequest;
    private ItemRequest secondItemRequest;
    private ItemRequestDto itemRequestDto;
    private Item item;

    @BeforeEach
    void beforeEach() {
        firstUser = User.builder()
                .id(1L)
                .name("Anna")
                .email("anna@yandex.ru")
                .build();

        secondUser = User.builder()
                .id(2L)
                .name("Tiana")
                .email("tiana@yandex.ru")
                .build();

        firstItemRequest = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest 1")
                .created(LocalDateTime.now())
                .build();

        secondItemRequest = ItemRequest.builder()
                .id(2L)
                .description("ItemRequest 2")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("screwdriver")
                .description("works well, does not ask to eat")
                .available(true)
                .owner(firstUser)
                .request(firstItemRequest)
                .build();

        itemRequestDto = ItemRequestDto.builder().description("ItemRequest 1").build();
    }

    @Test
    void addRequest() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(itemRequestStorage.save(any(ItemRequest.class))).thenReturn(firstItemRequest);

        ItemRequestDto itemRequestDtoTest = itemRequestService.addRequest(itemRequestDto, firstUser.getId());

        assertEquals(itemRequestDtoTest.getId(), firstItemRequest.getId());
        assertEquals(itemRequestDtoTest.getDescription(), firstItemRequest.getDescription());

        verify(itemRequestStorage, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getRequests() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(itemRequestStorage.findByRequesterIdOrderByCreatedAsc(anyLong())).thenReturn(List.of(firstItemRequest));
        when(itemStorage.findByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto itemRequestDtoTest = itemRequestService.getRequests(firstUser.getId()).get(0);

        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getName(), item.getName());
        assertEquals(itemRequestDtoTest.getItems().get(0).getDescription(), item.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getAvailable(), item.getAvailable());

        verify(itemRequestStorage, times(1)).findByRequesterIdOrderByCreatedAsc(anyLong());
    }

    @Test
    void getAllRequests() {
        when(unionService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10,10));
        when(itemRequestStorage.findByIdIsNotOrderByCreatedAsc(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstItemRequest)));
        when(itemStorage.findByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto itemRequestDtoTest = itemRequestService.getAllRequests(firstUser.getId(), 5, 10).get(0);

        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getName(), item.getName());
        assertEquals(itemRequestDtoTest.getItems().get(0).getDescription(), item.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getAvailable(), item.getAvailable());

        verify(itemRequestStorage, times(1)).findByIdIsNotOrderByCreatedAsc(anyLong(),any(PageRequest.class));
    }

    @Test
    void getRequestById() {
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(itemRequestStorage.existsById(anyLong())).thenReturn(true);
        when(itemRequestStorage.findById(anyLong())).thenReturn(Optional.ofNullable(firstItemRequest));
        when(itemStorage.findByRequestId(anyLong())).thenReturn(List.of(item));


        ItemRequestDto itemRequestDtoTest = itemRequestService.getRequestById(firstUser.getId(), firstItemRequest.getId());

        assertEquals(itemRequestDtoTest.getId(), firstItemRequest.getId());
        assertEquals(itemRequestDtoTest.getDescription(), firstItemRequest.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getRequestId(), firstUser.getId());

        verify(itemRequestStorage, times(1)).findById(anyLong());
    }

    @Test
    void addItemsToRequest() {
        when(itemStorage.findByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto itemRequestDtoTest = itemRequestService.addItemsToRequest(firstItemRequest);

        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getRequestId(), firstUser.getId());

        verify(itemStorage, times(1)).findByRequestId(anyLong());
    }
}