package ru.practicum.shareit.repository;

import java.util.List;

public interface Repository<T> {
    T create(T obj);

    T get(long id);

    List<T> getAll();

    T update(T obj);

    T patch(T obj);

    void delete(long id);

}
