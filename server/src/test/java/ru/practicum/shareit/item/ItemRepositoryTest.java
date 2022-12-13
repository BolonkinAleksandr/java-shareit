package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Test
    void search() {
        var user = new User(1, "user", "email@mail.ru");
        userRepository.save(user);
        var item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        var item2 = new Item(2, "name2", "231", true, user,
                null, null, null, null, null);
        itemRepository.save(item);
        itemRepository.save(item2);
        Pageable pageable = Pageable.ofSize(10);
        Page<Item> page = itemRepository.search("des", pageable);
        List<Item> items = page.toList();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
        assertEquals(item.getName(), items.get(0).getName());
        assertEquals(item.getDescription(), items.get(0).getDescription());
    }
}
