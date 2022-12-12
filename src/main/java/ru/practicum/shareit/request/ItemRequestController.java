package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDto;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto addRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                     @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return toItemRequestDto(itemRequestService.addRequest(toItemRequest(itemRequestDto), userId));
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequest(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        List<ItemRequestDto> dtoItemRequests = new ArrayList<>();
        List<ItemRequest> itemRequests = itemRequestService.getUserRequests(userId);
        if (itemRequests != null) {
            for (ItemRequest itemRequest : itemRequests) {
                dtoItemRequests.add(toItemRequestDto(itemRequest));
            }
        }
        return dtoItemRequests;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getNotUserRequests(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                   @PositiveOrZero @RequestParam(required = false) Integer from,
                                                   @Positive @RequestParam(required = false) Integer size) {
        List<ItemRequestDto> dtoItemRequests = new ArrayList<>();
        List<ItemRequest> itemRequests = itemRequestService.getNotUserRequests(userId, from, size);
        for (ItemRequest itemRequest : itemRequests) {
            dtoItemRequests.add(toItemRequestDto(itemRequest));
        }
        return dtoItemRequests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable long requestId,
                                         @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return toItemRequestDto(itemRequestService.getRequestById(requestId, userId));
    }
}
