package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.pageapleCreator.PageableCreater;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void findAllByOtherUsers() {
        var user = new User(1L, "user", "email@mail.ru");
        var user2 = new User(2L, "user2", "email2@mail.ru");
        userRepository.save(user);
        userRepository.save(user2);
        var itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now(), null);
        itemRequestRepository.save(itemRequest);
        Pageable pageable = Pageable.ofSize(10);
        Page<ItemRequest> itemRequests = itemRequestRepository.findAllByOtherUsers(2L, pageable);
        List<ItemRequest> requests = itemRequests.toList();
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(itemRequest.getId(), requests.get(0).getId());
        assertEquals(itemRequest.getDescription(), requests.get(0).getDescription());
    }
}
