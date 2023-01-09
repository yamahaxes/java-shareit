package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable long id) {
        return userClient.getUser(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patch(@RequestBody UserDto userDto,
                         @PathVariable long id) {
        return userClient.patchUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userClient.deleteUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getUsers();
    }
}
