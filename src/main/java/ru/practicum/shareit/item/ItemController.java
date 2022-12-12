package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.item.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.mapper.ItemWithBookingMapper.toItemWithBookingDto;

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
    public List<ItemWithBookingDto> readAll(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        List<Item> items = itemService.getAllItems(ownerId, from, size);
        List<ItemWithBookingDto> dtoItems = new ArrayList<>();
        for (Item item : items) {
            dtoItems.add(toItemWithBookingDto(item));
        }
        return dtoItems;
    }

    @GetMapping("/{id}")
    public ItemWithBookingDto readItemById(@PathVariable long id,
                                           @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return toItemWithBookingDto(itemService.getItemById(id, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(value = "X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return toItemDto(itemService.updateItem(toItem(itemDto), itemId, userId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(defaultValue = "") String text,
                                    @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemService.searchItem(text, from, size);
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

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @RequestHeader(value = "X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId) {
        return toCommentDto(itemService.createComment(toComment(commentDto), itemId, userId));
    }
}
