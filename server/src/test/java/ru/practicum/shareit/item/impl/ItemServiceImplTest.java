package ru.practicum.shareit.item.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoResponseInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;

    @Mock
    private ModelMapper<Item, ItemDto> itemModelMapper;
    @Mock
    private ModelMapper<Booking, BookingDtoResponseInfo> bookingMapper;
    @Mock
    private ModelMapper<Comment, CommentDto> commentMapper;

    @InjectMocks
    private ItemServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ItemServiceImpl(
                repository,
                userRepository,
                bookingRepository,
                commentRepository,
                userService,
                itemModelMapper,
                bookingMapper,
                commentMapper
        );
    }

    @Test
    void create() {
       ItemDto itemDto = makeItemDto(0);

       Item item = new Item();
       item.setId(itemDto.getId());
       item.setName(itemDto.getName());
       item.setDescription(itemDto.getDescription());

       when(itemModelMapper.mapFromDto(itemDto))
               .thenReturn(item);
       when(userRepository.getReferenceById(anyLong()))
               .thenReturn(new User());
       when(itemModelMapper.mapToDto(item))
               .thenReturn(itemDto);
       when(repository.save(item))
               .thenReturn(item);

       assertEquals(itemDto, service.create(itemDto, 1));
       verify(repository, times(1)).save(item);
    }

    @Test
    void create_whenUserNotFound_thenNotFoundException() {
        ItemDto itemDto = makeItemDto(0);

        doThrow(NotFoundException.class)
                .when(userService).existsUserByUserIdOrThrow(anyLong());

        assertThrows(NotFoundException.class,
                () -> service.create(itemDto, 100));
    }

    @Test
    void patch() {
        ItemDto itemDto = makeItemDto(1);
        itemDto.setDescription("patchedItem");

        Item itemFromRepo = makeItem(1);

        Item item = makeItem(1);
        item.setDescription(itemDto.getDescription());

        when(repository.getItemByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(Optional.of(itemFromRepo));
        when(itemModelMapper.mapFromDto(itemDto))
                .thenReturn(item);
        when(repository.save(itemFromRepo))
                .thenReturn(itemFromRepo);
        when(itemModelMapper.mapToDto(itemFromRepo))
                .thenReturn(itemDto);

        assertEquals(itemDto.getDescription(), service.patch(itemDto, 1).getDescription());
        verify(repository, times(1)).save(itemFromRepo);
    }

    @Test
    void patch_WhenItemNotFound_thenNotFoundException() {
        doThrow(NotFoundException.class)
                .when(repository).getItemByIdAndOwnerId(1,2);

        assertThrows(NotFoundException.class, () -> service.patch(makeItemDto(0),2));

    }

    @Test
    void get_whenUserNotOwner() {
        Item item = makeItem(0);
        User owner = new User();
        owner.setId(14L);
        item.setOwner(owner);

        ItemDto itemDto = makeItemDto(0);

        when(repository.existsById(any()))
                .thenReturn(true);
        when(repository.getReferenceById(anyLong()))
                .thenReturn(item);
        when(itemModelMapper.mapToDto(item))
                .thenReturn(itemDto);

        assertEquals(itemDto, service.get(1, 1));
        verify(bookingRepository, times(0)).getApprovedBookingsByItem_Ids(List.of(1L));

    }

    @Test
    void get_whenUserIsOwner() {
        Item item = makeItem(0);
        User owner = new User();
        owner.setId(14L);
        item.setOwner(owner);

        ItemDto itemDto = makeItemDto(0);

        when(repository.existsById(any()))
                .thenReturn(true);
        when(repository.getReferenceById(anyLong()))
                .thenReturn(item);
        when(itemModelMapper.mapToDto(item))
                .thenReturn(itemDto);
        when(bookingRepository.getApprovedBookingsByItem_Ids(anyList()))
                .thenReturn(new ArrayList<>());

        assertEquals(itemDto, service.get(14L, 1));
        verify(bookingRepository, times(1)).getApprovedBookingsByItem_Ids(List.of(1L));

    }

    @Test
    void get_whenWrongItemId_thenNotFoundException() {
        when(repository.existsById(any()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.get(1, 1));
    }

    @Test
    void getByUser() {

        User owner1 = new User();
        owner1.setId(1L);
        User owner2 = new User();
        owner2.setId(2L);

        Item item1 = makeItem(0);
        item1.setOwner(owner1);

        Item item2 = makeItem(1);
        item2.setOwner(owner2);

        ItemDto itemDto1 = makeItemDto(0);
        ItemDto itemDto2 = makeItemDto(1);

        when(repository.getItemsByOwnerId(anyLong(), any()))
                .thenReturn(List.of(item1, item2));
        when(itemModelMapper.mapToDto(item1))
                .thenReturn(itemDto1);
        when(itemModelMapper.mapToDto(item2))
                .thenReturn(itemDto2);
        when(bookingRepository.getApprovedBookingsByItem_Ids(anyList()))
                .thenReturn(new ArrayList<>());

        assertEquals(List.of(itemDto1, itemDto2), service.getByUser(1, 0, 3));
    }

    @Test
    void getByUser_whenWrongOwner_theNotFoundException() {
        doThrow(NotFoundException.class)
                .when(userService).existsUserByUserIdOrThrow(anyLong());

        assertThrows(NotFoundException.class, () -> service.getByUser(1, 0, 3));

    }

    @Test
    void search() {

        User owner = new User();
        owner.setId(10L);
        Item item = makeItem(1);
        item.setOwner(owner);

        ItemDto itemDto = makeItemDto(1);
        when(repository.findContainingText(anyString(), any()))
                .thenReturn(List.of(item));
        when(itemModelMapper.mapToDto(item))
                .thenReturn(itemDto);

        assertEquals(List.of(itemDto), service.search(1, "String", 0, 10));

    }

    @Test
    void createComment() {


        User author = new User();
        author.setName("name");

        CommentDto commentDto = new CommentDto();
        commentDto.setItemId(1L);
        commentDto.setText("comment");

        Item item = makeItem(1);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText(commentDto.getText());

        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(author);
        when(bookingRepository
                .countAllByBooker_IdAndItem_IdAndEndBeforeAndStatus(anyLong(),
                        anyLong(),
                        any(),
                        any()))
                .thenReturn(1L);

        when(repository.existsById(anyLong()))
                .thenReturn(true);
        when(repository.getReferenceById(anyLong()))
                .thenReturn(item);
        when(commentMapper.mapFromDto(commentDto))
                .thenReturn(comment);
        when(commentRepository.save(comment))
                .thenReturn(comment);
        when(commentMapper.mapToDto(comment))
                .thenReturn(commentDto);

        assertEquals(commentDto, service.createComment(1L, commentDto));
        verify(commentRepository, times(1)).save(comment);

    }

    @Test
    public void createComment_whenWrongUser_thenNotFoundException() {
        doThrow(NotFoundException.class)
                .when(userService).existsUserByUserIdOrThrow(anyLong());

        assertThrows(NotFoundException.class, () -> service.create(new ItemDto(), 1));

    }

    @Test
    public void createComment_whenWrongItem_thenNotFoundException() {
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(new User());

        when(repository.existsById(anyLong()))
                .thenReturn(false);

        CommentDto commentDto = new CommentDto();
        commentDto.setItemId(1L);

        assertThrows(NotFoundException.class,
                () -> service.createComment(anyLong(), commentDto));

    }

    @Test
    public void createComment_whenNotBooked_thenBadRequestException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setItemId(1L);

        User author = new User();

        when(userRepository.getReferenceById(any()))
                .thenReturn(author);
        when(repository.existsById(any()))
                .thenReturn(true);
        when(bookingRepository.countAllByBooker_IdAndItem_IdAndEndBeforeAndStatus(
                anyLong(),
                anyLong(),
                any(),
                any()
        )).thenReturn(0L);

        assertThrows(BadRequestException.class,
                () -> service.createComment(anyLong(), commentDto));
        verify(commentRepository, times(0)).save(any());
    }

    private ItemDto makeItemDto(int i) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(i + 1);
        itemDto.setRequestId(i + 10L);
        itemDto.setAvailable(true);
        itemDto.setDescription("item" + i);
        itemDto.setName("item" + i);

        return itemDto;
    }

    private Item makeItem(int i) {
        Item item = new Item();

        item.setId(i + 1L);
        item.setAvailable(true);
        item.setDescription("item" + i);
        item.setName("item" + i);

        return item;
    }
}