package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

@RestController
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return toItemDto(itemService.addItem(toItem(itemDto), ownerId));
    }

    @GetMapping
    public List<ItemDto> readAll(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        List<Item> items = itemService.getAllItems(ownerId);
        List<ItemDto> dtoItems = new ArrayList<>();
        for (Item item : items) {
            dtoItems.add(toItemDto(item));
        }
        return dtoItems;
    }

    @GetMapping("/{id}")
    public ItemDto readItemById(@PathVariable long id) {
        return toItemDto(itemService.getItemById(id));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(value = "X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return toItemDto(itemService.updateItem(toItem(itemDto), itemId, userId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(defaultValue = "") String text) {
        List<Item> items = itemService.searchItem(text);
        List<ItemDto> dtoItems = new ArrayList<>();
        if (items != null) {
            for (Item item : items) {
                dtoItems.add(toItemDto(item));
            }
        }
        return dtoItems;
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable long id) {
        itemService.deleteItem(id);
    }
}
