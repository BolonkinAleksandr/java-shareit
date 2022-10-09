package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Item item, long ownerId);

    List<Item> getAllItems(long id);

    Item getItemById(long id);

    Item updateItem(Item item, long id, long ownerId);

    List<Item> searchItem(String text);

    void deleteItem(long id);
}
