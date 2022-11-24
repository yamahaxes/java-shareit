package ru.practicum.shareit.user.impl;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserModelMapperImpl userModelMapper;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userModelMapper.mapFromDto(userDto);
        return userModelMapper.mapToDto(
                repository.create(user)
        );
    }

    @Override
    public UserDto get(long id) {
        return userModelMapper.mapToDto(
                repository.get(id)
        );
    }

    @Override
    public UserDto patch(UserDto userDto) {
        User user = userModelMapper.mapFromDto(userDto);
        return userModelMapper.mapToDto(
                repository.patch(user)
        );
    }

    @Override
    public void delete(long id) {
        repository.delete(id);
    }

    @Override
    public List<UserDto> getAll() {
        return repository.getAll()
                .stream()
                .map(userModelMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
