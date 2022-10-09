package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User getUserById(long id);

    List<User> getAllUsers();

    void deleteUser(long id);

    User updateUser(User user);
}
