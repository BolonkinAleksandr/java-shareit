package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    ItemStorage itemStorage;
    UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Item addItem(Item item, long ownerId) {
        item.setOwner(userStorage.getUserById(ownerId));
        log.info("add item {}", item);
        if (userStorage.getUserById(ownerId) == null) {
            throw new NotFoundException("user with id=" + ownerId + "not found");
        }
        return itemStorage.addItem(item);
    }

    @Override
    public List<Item> getAllItems(long id) {
        log.info("get items by owner id={}", id);
        List<Item> items = itemStorage.getAllItems();
        List<Item> ownerItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner().getId() == id) {
                ownerItems.add(item);
            }
        }
        return ownerItems;
    }

    @Override
    public Item getItemById(long id) {
        log.info("get item by id={}", id);
        return itemStorage.getItemById(id);
    }

    @Override
    public Item updateItem(Item item, long id, long ownerId) {
        item.setId(id);
        log.info("update item {}", item);
        if (itemStorage.getItemById(id).getOwner().getId() != ownerId) {
            throw new NotFoundException("you're not owner!");
        }
        return itemStorage.updateItem(item);
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> items = itemStorage.getAllItems();
        List<Item> searchedItems = new ArrayList<>();
        if (text.equals("")) {
            return null;
        }
        for (Item item : items) {
            if ((item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                    item.getName().toLowerCase().contains(text.toLowerCase())) &&
                    item.getAvailable()) {
                searchedItems.add(item);
            }
        }
        return searchedItems;
    }

    @Override
    public void deleteItem(long id) {
        log.info("delete item with id={}", id);
        itemStorage.deleteItem(id);
    }
}
