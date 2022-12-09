package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void getAllItemsTest() {
        User user = new User(1, "userName", "email@mail.ru");
        userService.addUser(user);
        TypedQuery<User> query = em.createQuery("SELECT u from User u where u.id = :id", User.class);
        User userOut = query.setParameter("id", user.getId()).getSingleResult();
        Item item = new Item(1, "itemName", "itemDescription", true, null, null,
                null, null, null, null);
        itemService.addItem(item, userOut.getId());
        TypedQuery<Item> query2 = em.createQuery("Select i from Item i where i.owner.id = :id", Item.class);
        Item itemOut = query2.setParameter("id", userOut.getId()).getSingleResult();
        assertThat(itemOut.getId(), notNullValue());
        assertThat(itemOut.getName(), equalTo(item.getName()));
        assertThat(itemOut.getDescription(), equalTo(item.getDescription()));
        assertThat(itemOut.getOwner().getName(), equalTo(userOut.getName()));
        List<Item> items = itemService.getAllItems(userOut.getId(), null, null);
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getOwner().getName(), equalTo(user.getName()));
    }
}
