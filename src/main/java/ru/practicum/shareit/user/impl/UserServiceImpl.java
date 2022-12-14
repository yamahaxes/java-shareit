package ru.practicum.shareit.user.impl;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ShareItUtils;

import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ModelMapper<User, UserDto> userModelMapper;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userModelMapper.mapFromDto(userDto);

        return userModelMapper.mapToDto(
                repository.save(user)
        );
    }

    @Override
    public UserDto get(long id) {
        existsUserByUserIdOrThrow(id);

        return userModelMapper.mapToDto(
                repository.getReferenceById(id)
        );
    }

    @Override
    public UserDto patch(UserDto userDto) {
        existsUserByUserIdOrThrow(userDto.getId());

        User user = userModelMapper.mapFromDto(userDto);
        User userTarget = repository.getReferenceById(userDto.getId());

        ShareItUtils.copyNotNullProperties(user, userTarget);

        return userModelMapper.mapToDto(
                repository.save(userTarget)
        );
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll()
                .stream()
                .map(userModelMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void existsUserByUserIdOrThrow(long userId) {
        if (!repository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }
    }
}
