package ru.practicum.shareit.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.UserRepository;

@Repository
public class UserDBStorageImpl{

    private final UserRepository userRepository;

    @Autowired
    public UserDBStorageImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
