package ru.practicum.shareit.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;


@RestController
@RequestMapping(path = "/users")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return toUserDto(userService.addUser(toUser(userDto)));
    }

    @GetMapping("/{id}")
    public UserDto readUserById(@PathVariable long id) {
        return toUserDto(userService.getUserById(id));
    }

    @GetMapping
    public List<UserDto> readAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> dtoUsers = new ArrayList<>();
        for (User user : users) {
            dtoUsers.add(toUserDto(user));
        }
        return dtoUsers;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable long userId) {
        return toUserDto(userService.updateUser(toUser(userDto), userId));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
