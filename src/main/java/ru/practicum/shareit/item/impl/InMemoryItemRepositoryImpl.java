package ru.practicum.shareit.item.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepositoryImpl implements ItemRepository {

    private final UserRepository userRepository;
    private final List<Item> items = new ArrayList<>();
    private int uid = 1;

    @Autowired
    public InMemoryItemRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Item create(Item item) {
        userRepository.get(item.getOwnerId());

        item.setId(uid++);
        items.add(item);

        return item;
    }

    @Override
    public Item get(long id) {
        return items.stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item not found."));
    }

    @Override
    public List<Item> getAll() {
        return items;
    }

    @Override
    public Item update(Item item) {
        return null;
    }

    @Override
    public Item patch(Item item) {
        Item patchedItem = items.stream()
                .filter(item1 -> item1.getId() == item.getId() && item1.getOwnerId() == item.getOwnerId())
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        if (item.getName() != null){
            patchedItem.setName(item.getName());
        }

        if (item.getDescription() != null){
            patchedItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null){
            patchedItem.setAvailable(item.getAvailable());
        }

        return patchedItem;
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public List<Item> getItemsByUser(long ownerId) {
        return items.stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        String lowerCaseText = text.toLowerCase();
        return items.stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(lowerCaseText)
                                || item.getDescription().toLowerCase().contains(lowerCaseText)))
                .collect(Collectors.toList());
    }
}
