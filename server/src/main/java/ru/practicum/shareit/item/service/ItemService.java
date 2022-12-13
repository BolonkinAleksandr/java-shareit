package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Item item, long ownerId);

    List<Item> getAllItems(long id, Integer from, Integer size);

    Item getItemById(long itemId, long userId);

    Item updateItem(Item item, long id, long ownerId);

    List<Item> searchItem(String text, Integer from, Integer size);

    void deleteItem(long id);

    Comment createComment(Comment comment, long itemId, long userId);

    List<Item> findItemsByRequestId(Long requestId);
}
