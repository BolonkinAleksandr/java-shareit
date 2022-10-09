package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private long id = 0;
    private HashMap<Long, Item> items = new HashMap<>();

    private long generateNewId() {
        return ++id;
    }

    @Override
    public Item addItem(Item item) {
        item.setId(generateNewId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAllItems() {
        List<Item> itemsList = new ArrayList<>();
        for (Item item : items.values()) {
            itemsList.add(item);
        }
        return itemsList;
    }

    @Override
    public void deleteItem(long id) {
        items.remove(id);
    }

    @Override
    public Item updateItem(Item item) {
        Item updatedItem = items.get(item.getId());
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        deleteItem(item.getId());
        items.put(updatedItem.getId(), updatedItem);
        return updatedItem;
    }
}
