package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addRequest(ItemRequest itemRequest, long userId);

    List<ItemRequest> getUserRequests(long userId);

    List<ItemRequest> getNotUserRequests(long userId, Integer from, Integer size);

    ItemRequest getRequestById(long requestId, long userId);
}
