package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    void getAllBookingsByUserTest() {
        User owner = new User(1, "userName", "email@mail.ru");
        User booker = new User(2, "userName2", "email2@mail.ru");
        userService.addUser(owner);
        userService.addUser(booker);
        Item item = new Item(1, "itemName", "description", true, null, null,
                null, null, null, null);
        itemService.addItem(item, 1);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.owner.id = :id", Item.class);
        Item itemOut = query.setParameter("id", owner.getId()).getSingleResult();
        assertThat(itemOut.getId(), notNullValue());
        assertThat(itemOut.getOwner().getName(), equalTo(owner.getName()));
        Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                itemService.getItemById(itemOut.getId(), userService.getUserById(owner.getId()).getId()),
                userService.getUserById(booker.getId()), Status.WAITING);
        bookingService.addBooking(booking, userService.getUserById(booker.getId()).getId());
        List<Booking> bookingList = bookingService.getAllBookingsByUser(State.valueOf("ALL"),
                userService.getUserById(booker.getId()).getId(), null, null);
        assertThat(bookingList.size(), equalTo(1));
        assertThat(bookingList.get(0), equalTo(bookingService.getById(1,
                userService.getUserById(owner.getId()).getId())));
    }
}
