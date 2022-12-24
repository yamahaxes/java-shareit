package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTestJpa {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findContainingText() {

        User owner = new User();
        owner.setName("user");
        owner.setEmail("mail@mail.com");
        owner = userRepository.save(owner);

        Item item1 = new Item();
        item1.setOwner(owner);
        item1.setAvailable(true);
        item1.setDescription("item1 fgr");
        item1.setName("item1");
        item1 = itemRepository.save(item1);

        Item item2 = new Item();
        item2.setOwner(owner);
        item2.setAvailable(true);
        item2.setDescription("item2 sdf");
        item2.setName("item2");
        item2 = itemRepository.save(item2);

        Item item3 = new Item();
        item3.setOwner(owner);
        item3.setAvailable(false);
        item3.setDescription("item3 rty");
        item3.setName("item3");
        item3 = itemRepository.save(item3);

        Item item4 = new Item();
        item4.setOwner(owner);
        item4.setAvailable(true);
        item4.setDescription("Item4 ssdf");
        item4.setName("Item4");
        item4 = itemRepository.save(item4);

        Pageable pageable = PageRequest.of(0, 100);
        List<Item> itemList = itemRepository.findContainingText("item",
                pageable);

        assertEquals(3, itemList.size());
        itemList = itemRepository.findContainingText("item3",
                pageable);
        assertTrue(itemList.isEmpty());

        itemList = itemRepository.findContainingText("sdf",
                pageable);
        assertEquals(item2.getId(), itemList.get(0).getId());

    }

}