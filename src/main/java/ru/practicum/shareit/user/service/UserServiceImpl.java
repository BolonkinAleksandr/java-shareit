package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.ValidationException;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User addUser(User user) {
        log.info("add user {}", user);
        emailValidation(user);
        return userStorage.addUser(user);
    }

    @Override
    public User getUserById(long id) {
        log.info("get user with id={}", id);
        return userStorage.getUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("get all users");
        return userStorage.getAllUsers();
    }

    @Override
    public User updateUser(User user, long userId) {
        user.setId(userId);
        log.info("update user with id={}", userId);
        emailValidation(user);
        return userStorage.updateUser(user);
    }

    @Override
    public void deleteUser(long id) {
        log.info("delete user with id={}", id);
        userStorage.deleteUser(id);
    }

    private void emailValidation(User user) {
        for (User user1 : userStorage.getAllUsers()) {
            if (user1.getEmail().equals(user.getEmail())) {
                throw new ValidationException("user with email " + user.getEmail() + " already exist");
            }
        }
    }
}
