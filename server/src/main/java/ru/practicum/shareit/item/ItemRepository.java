package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> getItemByIdAndOwnerId(long id, long ownerId);

    List<Item> getItemsByOwnerIdOrderById(long ownerId, Pageable pageable);

    @Query("SELECT item " +
           "FROM Item item " +
           "WHERE (LOWER(item.name) LIKE %:text% OR LOWER(item.description) LIKE %:text%) " +
           "   AND item.available = true " +
           "ORDER BY item.id")
    List<Item> findContainingText(@Param("text") String text, Pageable pageable);

    List<Item> getItemsByItemRequest_IdOrderById(long itemRequestId);

    List<Item> getItemsByItemRequestIsInOrderById(Collection<ItemRequest> itemRequest);
}
