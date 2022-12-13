package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User addUser(User user);

    User getUserById(long id);

    List<User> getAllUsers();

    User updateUser(User user, long userId);

    void deleteUser(long id);
}
