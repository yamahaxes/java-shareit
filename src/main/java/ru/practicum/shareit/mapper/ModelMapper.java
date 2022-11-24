package ru.practicum.shareit.mapper;

public interface ModelMapper<T, V> {
    T mapFromDto(V dto);

    V mapToDto(T entity);
}
