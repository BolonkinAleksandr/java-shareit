package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> readUserById(@Positive @PathVariable long id) {
        log.info("Get user with userId={}", id);
        return userClient.readUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> readAllUsers() {
        log.info("Get users");
        return userClient.readAllUsers();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Updating user {}", userDto);
        return userClient.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        log.info("Deleting user by userId={}", userId);
        return userClient.delete(userId);
    }
}
