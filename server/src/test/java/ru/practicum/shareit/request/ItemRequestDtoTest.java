package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDto;

public class ItemRequestDtoTest {
    @Test
    void bookingToBookingDtoTest() throws Exception {
        List<Item> items = new ArrayList<>();
        var user = new User(1, "name", "email@mail.ru");
        var item = new Item(1, "name", "description", true, user,
                null, null, null, null, null);
        items.add(item);
        var itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now(), items);
        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
    }
}
