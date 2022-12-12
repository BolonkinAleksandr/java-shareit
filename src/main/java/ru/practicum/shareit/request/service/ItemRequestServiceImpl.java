package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.pageapleCreator.PageableCreater;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private PageableCreater pageableCreater;
    private ItemService itemService;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository,
                                  PageableCreater pageableCreater, ItemService itemService) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.pageableCreater = pageableCreater;
        this.itemService = itemService;
    }

    @Transactional
    @Override
    public ItemRequest addRequest(ItemRequest itemRequest, long userId) {
        log.info("add booking {}", itemRequest);
        checkUserById(userId);
        itemRequest.setRequester(userRepository.getReferenceById(userId));
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getUserRequests(long userId) {
        log.info("get user requests with id {}", userId);
        checkUserById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        for (ItemRequest itemRequest : itemRequests) {
            itemRequest.setItemList(itemService.findItemsByRequestId(itemRequest.getId()));
        }
        return itemRequests;
    }

    @Override
    public List<ItemRequest> getNotUserRequests(long userId, Integer from, Integer size) {
        log.info("get not user requests with id {}", userId);
        checkUserById(userId);
        List<ItemRequest> itemRequests = new ArrayList<>();
        Pageable pageable = pageableCreater.doPageable(from, size);
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAllByOtherUsers(userId, pageable);
        for (ItemRequest itemRequest : itemRequestPage.getContent()) {
            if (itemRequest.getRequester().getId() != userId) {
                itemRequests.add(itemRequest);
            }
        }
        for (ItemRequest itemRequest : itemRequests) {
            itemRequest.setItemList(itemService.findItemsByRequestId(itemRequest.getId()));
        }
        return itemRequests;
    }

    @Override
    public ItemRequest getRequestById(long requestId, long userId) {

        log.info("get request with id {}", requestId);
        checkUserById(userId);
        checkRequestById(requestId);
        ItemRequest itemRequest = itemRequestRepository.getReferenceById(requestId);
        itemRequest.setItemList(itemService.findItemsByRequestId(requestId));
        return itemRequest;
    }

    private void checkUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("user with id=" + userId + " doesn't exist");
        }
    }

    private void checkRequestById(long requestId) {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new NotFoundException("request with id=" + requestId + " doesn't exist");
        }
    }
}
