package ru.practicum.shareit.user.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;
    @Mock
    private ModelMapper<User, UserDto> userModelMapper;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void create() {

        service.create(new UserDto());
        verify(repository, times(1)).save(any());
    }

    @Test
    void get() {
        when(repository.existsById(any()))
                .thenReturn(true);
        service.get(1);
        verify(repository, times(1)).getReferenceById(any());
    }

    @Test
    void get_whenWrongUser_thenNotFoundException() {
        when(repository.existsById(any()))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> service.get(1));
    }

    @Test
    void patch() {
        when(repository.existsById(any()))
                .thenReturn(true);
        when(userModelMapper.mapFromDto(any()))
                .thenReturn(new User());
        when(repository.getReferenceById(anyLong()))
                .thenReturn(new User());

        service.patch(new UserDto());

        verify(repository, times(1)).save(any());
    }

    @Test
    void patch_whenWrongUser_thenNotFoundException() {
        when(repository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> service.patch(new UserDto()));
    }

    @Test
    void delete() {
        service.delete(1);
        verify(repository, times(1)).deleteById(anyLong());
    }

    @Test
    void getAll() {
        service.getAll();
        verify(repository, times(1)).findAll();
    }
}