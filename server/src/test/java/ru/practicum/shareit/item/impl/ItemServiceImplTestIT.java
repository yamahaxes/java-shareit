package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTestIT {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemService service;

    private Item item1;
    private Item item2;
    private Item item3;
    private User owner1;
    private User owner2;

    @BeforeEach
    void setUp() {
        owner1 = makeUser(0);
        owner2 = makeUser(1);

        owner1 = userRepository.save(owner1);
        owner2 = userRepository.save(owner2);


        item1 = makeItem(0);
        item1.setOwner(owner1);
        item2 = makeItem(1);
        item2.setOwner(owner2);
        item3 = makeItem(2);
        item3.setOwner(owner1);

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);
        item3 = itemRepository.save(item3);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getByUser() {

        List<ItemDto> itemDtos = service.getByUser(owner1.getId(), 0, 3);
        assertEquals(2, itemDtos.size());
        assertEquals(itemDtos.get(0).getId(), item1.getId());
        assertEquals(itemDtos.get(1).getId(), item3.getId());

        itemDtos = service.getByUser(owner2.getId(), 0, 3);
        assertEquals(1, itemDtos.size());
        assertEquals(itemDtos.get(0).getId(), item2.getId());
    }

    private Item makeItem(int i) {
        Item item = new Item();
        item.setAvailable(true);
        item.setName("item" + i);
        item.setDescription("item" + i);
        return item;
    }

    private User makeUser(int i) {
        User user = new User();
        user.setName("name" + i);
        user.setEmail("mail" + i + "@mail.com");
        return user;
    }


}