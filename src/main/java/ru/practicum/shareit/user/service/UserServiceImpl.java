package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.validation.ValidationException;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public User addUser(User user) {
        log.info("add user {}", user);
        emailValidation(user);
        return userRepository.save(user);
    }

    @Override
    public User getUserById(long id) {
        log.info("get user with id={}", id);
        return userRepository.getReferenceById(id);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("get all users");
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public User updateUser(User user, long userId) {
        emailValidation(user);
        log.info("update user with id={}", userId);
        User oldUser = userRepository.getReferenceById(userId);
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        return userRepository.save(oldUser);
    }


    @Override
    public void deleteUser(long id) {
        log.info("delete user with id={}", id);
        userRepository.deleteById(id);
    }

    private void emailValidation(User user) {
        for (User user1 : userRepository.findAll()) {
            if (user1.getEmail().equals(user.getEmail())) {
                throw new ValidationException("user with email " + user.getEmail() + " already exist");
            }
        }
    }
}
