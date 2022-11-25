package ru.practicum.shareit.user.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserRepositoryImpl implements UserRepository {

    private long uid = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        emailNotExists(user.getEmail(), uid);

        user.setId(uid++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(long id) {
        userExists(id);
        return users.get(id);
    }

    @Override
    public User patch(User user) {
        userExists(user.getId());
        emailNotExists(user.getEmail(), user.getId());

        User updatedUser = users.get(user.getId());
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }

        return updatedUser;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) {
        userExists(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    private void userExists(long id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException("User not found: id=" + id);
        }
    }

    private void emailNotExists(String email, long excludeId) {
        long count = users
                .values()
                .stream()
                .filter(user -> user.getEmail().equals(email) && user.getId() != excludeId)
                .count();
        if (count > 0) {
            throw new EntityAlreadyExistsException("Email is already taken: " + email);
        }
    }
}
