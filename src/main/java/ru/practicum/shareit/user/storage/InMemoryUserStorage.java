package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private long id = 1;
    private HashMap<Long, User> users = new HashMap<>();

    private long generateNewId() {
        return id++;
    }

    @Override
    public User addUser(User user) {
        user.setId(generateNewId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>();
        for (User user : users.values()) {
            usersList.add(user);
        }
        return usersList;
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    @Override
    public User updateUser(User user) {
        User updatedUser = users.get(user.getId());
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        deleteUser(user.getId());
        users.put(updatedUser.getId(), updatedUser);
        return updatedUser;
    }
}
