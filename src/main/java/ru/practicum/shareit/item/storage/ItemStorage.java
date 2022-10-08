package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(Item item);

    Item getItemById(long id);

    List<Item> getAllItems();

    void deleteItem(long id);

    Item updateItem(Item item);
}
