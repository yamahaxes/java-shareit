package ru.practicum.shareit.mapper;

import java.util.List;

public interface ModelMapperList<T, V> extends ModelMapper<T, V> {
    List<T> mapFromDto(List<V> dtoList);

    List<V> mapToDto(List<T> entities);
}
