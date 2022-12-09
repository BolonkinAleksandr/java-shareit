package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final EntityManager em;
    private final UserService userService;

    @Test
    void add() {
        var user = new User(1, "userName", "email@mail.ru");
        userService.addUser(user);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        var userOut = query.setParameter("email", user.getEmail()).getSingleResult();
        assertThat(userOut.getId(), equalTo(user.getId()));
        assertThat(userOut.getName(), equalTo(user.getName()));
        assertThat(userOut.getEmail(), equalTo(user.getEmail()));
        var user2 = userService.getUserById(userOut.getId());
        assertThat(userOut.getId(), equalTo(user2.getId()));
        assertThat(userOut.getName(), equalTo(user2.getName()));
        assertThat(userOut.getEmail(), equalTo(user2.getEmail()));
    }
}
