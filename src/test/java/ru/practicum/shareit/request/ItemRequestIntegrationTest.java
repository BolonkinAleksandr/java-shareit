package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService requestService;
    private final UserService userService;

    @Test
    void getUserRequestsTest() {
        User user = new User(1, "userName", "email@mail.ru");
        userService.addUser(user);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User userOut = query.setParameter("id", user.getId()).getSingleResult();
        ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now(), null);
        requestService.addRequest(itemRequest, user.getId());
        TypedQuery<ItemRequest> query2 = em.createQuery("Select ir from ItemRequest ir where ir.requester.id = :requester_id"
                , ItemRequest.class);
        ItemRequest ItemRequestOut = query2.setParameter("requester_id", userOut.getId()).getSingleResult();
        assertThat(ItemRequestOut.getId(), equalTo(itemRequest.getId()));
        assertThat(ItemRequestOut.getDescription(), equalTo(itemRequest.getDescription()));
        List<ItemRequest> itemRequests = requestService.getUserRequests(userOut.getId());
        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.get(0).getDescription(), equalTo(itemRequest.getDescription()));
    }
}