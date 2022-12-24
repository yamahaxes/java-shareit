package ru.practicum.shareit.user.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceImplTestIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService service;

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void create() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("name@mail.com");

        userDto = service.create(userDto);
        List<User> users = userRepository.findAll();
        assertEquals(1, users.size());
        assertEquals(userDto.getId(), users.get(0).getId());

    }

    @Test
    void getAll() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("name");
        userDto1.setEmail("name@mail.com");

        UserDto userDto2 = new UserDto();
        userDto2.setName("name2");
        userDto2.setEmail("name2@mail.com");

        userDto1 = service.create(userDto1);
        userDto2 = service.create(userDto2);

        List<User> users  = userRepository.findAll();
        assertEquals(2, users.size());
        assertEquals(userDto1.getId(), users.get(0).getId());
        assertEquals(userDto2.getId(), users.get(1).getId());
    }
}