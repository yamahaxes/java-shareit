package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> getByRequestor_Id(long requestorId, Sort sort);

    @Query("SELECT " +
           "itemRequest " +
           "FROM ItemRequest itemRequest " +
           "WHERE NOT itemRequest.requestor.id = ?1 ")
    List<ItemRequest> getOtherUserRequests(long userIdException, Pageable pageable);
}
