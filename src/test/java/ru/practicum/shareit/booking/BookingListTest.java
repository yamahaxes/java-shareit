package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingListTest {

    @Mock
    private BookingRepository repository;

    @InjectMocks
    private BookingList bookingList;

    @Test
    void getByBookerId_whenStateAll() {
        bookingList.getByBookerId("ALL", 1, PageRequest.of(1, 100));
        verify(repository, times(1)).getByBooker_Id(anyLong(), any());
    }

    @Test
    void getByBookerId_whenStateCurrent() {
        bookingList.getByBookerId("CURRENT", 1, PageRequest.of(1, 100));
        verify(repository, times(1)).getByBooker_IdAndDateBetweenStartAndEnd(anyLong(), any(), any());
    }

    @Test
    void getByBookerId_whenStatePast() {
        bookingList.getByBookerId("PAST", 1, PageRequest.of(1, 100));
        verify(repository, times(1)).getByBooker_IdAndEndBefore(anyLong(), any(), any());
    }

    @Test
    void getByBookerId_whenStateFuture() {
        bookingList.getByBookerId("FUTURE", 1, PageRequest.of(1, 100));
        verify(repository, times(1)).getByBooker_IdAndStartAfter(anyLong(), any(), any());
    }

    @Test
    void getByBookerId_whenStateWaiting() {
        bookingList.getByBookerId("WAITING", 1, PageRequest.of(1, 100));
        verify(repository, times(1)).getByBooker_IdAndStatusEquals(anyLong(), any(), any());
    }

    @Test
    void getByBookerId_whenStateRejected() {
        bookingList.getByBookerId("REJECTED", 1, PageRequest.of(1, 100));
        verify(repository, times(1)).getByBooker_IdAndStatusEquals(anyLong(), any(), any());
    }

    @Test
    void getByBookerId_whenUnknownStateThrow() {
        assertThrows(BadRequestException.class,
                () -> bookingList.getByBookerId("", 1, PageRequest.of(1, 100)));
    }


    @Test
    void getByItem_Owner_Id_whenStateALL() {
        bookingList.getByItem_Owner_Id("ALL", 1, PageRequest.of(0,100));
        verify(repository, times(1)).getByItem_Owner_Id(anyLong(), any());
    }

    @Test
    void getByItem_Owner_Id_whenStateCurrent() {
        bookingList.getByItem_Owner_Id("CURRENT", 1, PageRequest.of(0,100));
        verify(repository, times(1)).getByItem_Owner_idAndBetweenStartAndEnd(anyLong(), any(), any());
    }

    @Test
    void getByItem_Owner_Id_whenStatePast() {
        bookingList.getByItem_Owner_Id("PAST", 1, PageRequest.of(0,100));
        verify(repository, times(1)).getByItem_Owner_idAndEndBefore(anyLong(), any(), any());
    }

    @Test
    void getByItem_Owner_Id_whenStateFuture() {
        bookingList.getByItem_Owner_Id("FUTURE", 1, PageRequest.of(0,100));
        verify(repository, times(1)).getByItem_Owner_idAndStartAfter(anyLong(), any(), any());
    }

    @Test
    void getByItem_Owner_Id_whenStateWaiting() {
        bookingList.getByItem_Owner_Id("WAITING", 1, PageRequest.of(0,100));
        verify(repository, times(1)).getByItem_Owner_idAndStatusEquals(anyLong(), any(), any());
    }

    @Test
    void getByItem_Owner_Id_whenStateRejected() {
        bookingList.getByItem_Owner_Id("REJECTED", 1, PageRequest.of(0,100));
        verify(repository, times(1)).getByItem_Owner_idAndStatusEquals(anyLong(), any(), any());
    }

    @Test
    void getByItem_Owner_Id_whenUnknownStateThrow() {
        assertThrows(BadRequestException.class,
                () -> bookingList.getByItem_Owner_Id("", 1, PageRequest.of(0,100)));
    }
}