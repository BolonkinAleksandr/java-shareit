package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTest {
    @Mock
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    UserService userService;

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void addUserTest() {
        User user = new User(1, "name", "email@mail.ru");
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        assertEquals(1, userService.addUser(user).getId());
    }

    @Test
    void addUserIncorrectEmailTest() {
        User user = new User(1, "name", "email@mail.ru");
        List<User> users = new ArrayList<>();
        users.add(user);
        User user2 = new User(2, "name2", "email@mail.ru");
        Mockito.when(userRepository.findAll()).thenReturn(users);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            userService.addUser(user2);
        });
        Assertions.assertEquals("user with email email@mail.ru already exist", thrown.getMessage());
    }

    @Test
    void getUserByIdTest() {
        User user = new User(1, "name", "email@mail.ru");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        Assertions.assertEquals(1, userService.getUserById(1).getId());
        Assertions.assertEquals("name", userService.getUserById(1).getName());
    }

    @Test
    void getAllUsersTest() {
        List<User> users = new ArrayList<>();
        users.add(new User(1, "name", "email@mail.ru"));
        users.add(new User(2, "name", "email2@mail.ru"));
        Mockito.when(userRepository.findAll()).thenReturn(users);
        Assertions.assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    void updateUserTest() {
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(1, "name2", "email2@mail.ru");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user2);
        Assertions.assertEquals("email2@mail.ru", userService.updateUser(user2, 1).getEmail());
        Assertions.assertEquals("name2", userService.updateUser(user2, 1).getName());
    }

    @Test
    void deleteUserTest() {
        User user = new User(1, "name", "email@mail.ru");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        userService.deleteUser(1);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }
}
