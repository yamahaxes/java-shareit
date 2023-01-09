package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTestIT {

    @Autowired
    private ItemRequestServiceImpl service;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;


    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;
    private User requestor1;
    private User requestor2;

    @BeforeEach
    void setUp() {

        requestor1 = new User();
        requestor1.setName("user1");
        requestor1.setEmail("user1@mail.com");

        requestor2 = new User();
        requestor2.setName("user2");
        requestor2.setEmail("user2@mail.com");
        requestor1 = userRepository.save(requestor1);
        requestor2 = userRepository.save(requestor2);

        itemRequest1 = makeItemRequest(requestor1, LocalDateTime.now().minusDays(3));
        itemRequest2 = makeItemRequest(requestor2, LocalDateTime.now().minusDays(2));
        itemRequest3 = makeItemRequest(requestor2, LocalDateTime.now().minusDays(1));

        itemRequest1 = itemRequestRepository.save(itemRequest1);
        itemRequest2 = itemRequestRepository.save(itemRequest2);
        itemRequest3 = itemRequestRepository.save(itemRequest3);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void getUserRequests() {
        List<ItemRequestDtoResponse> requests = service.getUserRequests(requestor1.getId());
        assertEquals(1, requests.size());
        assertEquals(itemRequest1.getId(), requests.get(0).getId());

        requests = service.getUserRequests(requestor2.getId());

        assertEquals(2, requests.size());
        assertEquals(itemRequest3.getId(), requests.get(0).getId());
        assertEquals(itemRequest2.getId(), requests.get(1).getId());
    }



    private ItemRequest makeItemRequest(User requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(requestor);
        itemRequest.setDescription("itemrequest");
        itemRequest.setCreated(created);
        return itemRequest;
    }
}