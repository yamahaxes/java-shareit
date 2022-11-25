package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto get(long id);

    UserDto patch(UserDto userDto);

    void delete(long id);

    List<UserDto> getAll();
}
