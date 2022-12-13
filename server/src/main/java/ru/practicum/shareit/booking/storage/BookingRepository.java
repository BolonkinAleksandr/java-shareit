package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findBookingsByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime localDateTime,
                                                                   Pageable pageable);

    Page<Booking> findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start,
                                                                                LocalDateTime end, Pageable pageable);

    Page<Booking> findBookingsByBookerOrderByStartDesc(User user, Pageable pageable);

    Page<Booking> findBookingsByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime localDateTime,
                                                                    Pageable pageable);

    Page<Booking> findBookingsByBookerAndStatusOrderByStartDesc(User user, Status status, Pageable pageable);

    Page<Booking> findBookingsByItem_OwnerOrderByStartDesc(User user, Pageable pageable);

    Page<Booking> findBookingsByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start,
                                                                                    LocalDateTime end, Pageable pageable);

    Page<Booking> findBookingsByItem_OwnerAndEndBeforeOrderByStartDesc(User user, LocalDateTime localDateTime,
                                                                       Pageable pageable);

    Page<Booking> findBookingsByItem_OwnerAndStatusOrderByStartDesc(User user, Status status, Pageable pageable);

    Booking findBookingByBookerAndItemAndEndBefore(User user, Item item, LocalDateTime localDateTime);

    List<Booking> findBookingsByItemOrderByStart(Item item);

    Page<Booking> findBookingsByItem_OwnerAndStartAfterOrderByStartDesc(User user, LocalDateTime localDateTime,
                                                                        Pageable pageable);

}
