package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> getItemByIdAndOwnerId(long id, long ownerId);

    List<Item> getItemsByOwnerId(long ownerId);

    @Query("SELECT item " +
           "FROM Item item " +
           "WHERE (LOWER(item.name) LIKE %?1% OR LOWER(item.description) LIKE %?1%) " +
           "   AND item.available = true")
    List<Item> findContainingText(String text);

    List<Item> getItemsByItemRequest_Id(long itemRequest_id);
}
