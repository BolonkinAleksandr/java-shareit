package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.pageapleCreator.PageableCreater;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemRequestServiceTest {
    @Mock
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    @Mock
    private final ItemRequestRepository itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    @Mock
    private final PageableCreater pageableCreater = Mockito.mock(PageableCreater.class);
    @Mock
    ItemService itemService = Mockito.mock(ItemService.class);

    ItemRequestService itemRequestService;

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, pageableCreater, itemService);
    }

    @Test
    void addRequestTest() {
        User user = new User(1, "name", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now(), null);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemRequestRepository.save(Mockito.any())).thenReturn(itemRequest);
        assertEquals(1L, itemRequestService.addRequest(itemRequest, 1).getId());
    }

    @Test
    void addRequestIncorrectUserTest() {
        User user = new User(1, "name", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now(), null);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            itemRequestService.addRequest(itemRequest, 2);
        });
        Assertions.assertEquals("user with id=2 doesn't exist", thrown.getMessage());
    }

    @Test
    void getUserRequestsTest() {
        User user = new User(1, "name", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now(), null);
        ItemRequest itemRequest2 = new ItemRequest(2, "description2", user, LocalDateTime.now(), null);
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        itemRequests.add(itemRequest2);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(Mockito.anyLong())).thenReturn(itemRequests);
        Mockito.when(itemRequestRepository.save(Mockito.any())).thenReturn(itemRequest);
        Assertions.assertEquals(2, itemRequestService.getUserRequests(1).size());
    }

    @Test
    void getUserRequestsIncorrectUserTest() {
        User user = new User(1, "name", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now(), null);
        ItemRequest itemRequest2 = new ItemRequest(2, "description2", user, LocalDateTime.now(), null);
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        itemRequests.add(itemRequest2);
        Mockito.when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(Mockito.anyLong())).thenReturn(itemRequests);
        Mockito.when(itemRequestRepository.save(Mockito.any())).thenReturn(itemRequest);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getUserRequests(2);
        });
        Assertions.assertEquals("user with id=2 doesn't exist", thrown.getMessage());
    }

    @Test
    void getNotUserRequestsTest() {
        User user = new User(1, "name", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now(), null);
        ItemRequest itemRequest2 = new ItemRequest(2, "description2", user, LocalDateTime.now(), null);
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        itemRequests.add(itemRequest2);
        Page<ItemRequest> page = new PageImpl<>(itemRequests);
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        items.add(item);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.findAllByOtherUsers(Mockito.anyLong(), Mockito.any())).thenReturn(page);
        Mockito.when(itemService.findItemsByRequestId(Mockito.anyLong())).thenReturn(items);
        Assertions.assertEquals(2, itemRequestService.getNotUserRequests(2, null, null).size());
    }

    @Test
    void getNotUserRequestsIncorrectUserTest() {
        User user = new User(1, "name", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now(), null);
        ItemRequest itemRequest2 = new ItemRequest(2, "description2", user, LocalDateTime.now(), null);
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        itemRequests.add(itemRequest2);
        Page<ItemRequest> page = new PageImpl<>(itemRequests);
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        items.add(item);
        Mockito.when(itemRequestRepository.findAllByOtherUsers(Mockito.anyLong(), Mockito.any())).thenReturn(page);
        Mockito.when(itemService.findItemsByRequestId(Mockito.anyLong())).thenReturn(items);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getNotUserRequests(2, null, null);
        });
        Assertions.assertEquals("user with id=2 doesn't exist", thrown.getMessage());
    }

    @Test
    void getRequestByIdTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item();
        List<Item> items = new ArrayList<>();
        items.add(item);
        ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now(), items);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.getReferenceById(Mockito.anyLong())).thenReturn(itemRequest);
        Mockito.when(itemService.findItemsByRequestId(Mockito.anyLong())).thenReturn(null);
        Assertions.assertEquals(1, itemRequestService.getRequestById(1, 1).getId());
    }

    @Test
    void getRequestByIdIncorrectUserTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item();
        List<Item> items = new ArrayList<>();
        items.add(item);
        ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now(), items);
        Mockito.when(itemRequestRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.getReferenceById(Mockito.anyLong())).thenReturn(itemRequest);
        Mockito.when(itemService.findItemsByRequestId(Mockito.anyLong())).thenReturn(null);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(1, 2);
        });
        Assertions.assertEquals("user with id=2 doesn't exist", thrown.getMessage());
    }

    @Test
    void getRequestByIdIncorrectRequestTest() {
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item();
        List<Item> items = new ArrayList<>();
        items.add(item);
        ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now(), items);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.getReferenceById(Mockito.anyLong())).thenReturn(itemRequest);
        Mockito.when(itemService.findItemsByRequestId(Mockito.anyLong())).thenReturn(null);
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(2, 1);
        });
        Assertions.assertEquals("request with id=2 doesn't exist", thrown.getMessage());
    }
}
