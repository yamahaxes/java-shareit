package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTestJpa {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void getOtherUserRequests() {

        User requestor1 = new User();
        requestor1.setName("req1");
        requestor1.setEmail("req1@mail.com");
        requestor1 = userRepository.save(requestor1);

        User requestor2 = new User();
        requestor2.setName("req2");
        requestor2.setEmail("req2@mail.com");
        requestor2 = userRepository.save(requestor2);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("item request");
        itemRequest.setRequestor(requestor1);
        itemRequest = itemRequestRepository.save(itemRequest);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("item request 2");
        itemRequest2.setRequestor(requestor2);
        itemRequest2 = itemRequestRepository.save(itemRequest2);

        Pageable pageable = PageRequest.of(0,100);
        List<ItemRequest> requestList = itemRequestRepository
                .getOtherUserRequests(requestor1.getId(), pageable);
        assertEquals(1, requestList.size());
        assertEquals(itemRequest2.getId(), requestList.get(0).getId());

        requestList = itemRequestRepository
                .getOtherUserRequests(requestor2.getId(), pageable);
        assertEquals(1, requestList.size());
        assertEquals(itemRequest.getId(), requestList.get(0).getId());

    }
}