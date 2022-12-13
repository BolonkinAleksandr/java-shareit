package ru.practicum.shareit.pageapleCreator;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NewException;

@Component
public class PageableCreater {

    public Pageable doPageable(Integer from, Integer size) {
        Pageable pageable;
        if (from == null && size == null) {
            pageable = PageRequest.of(0, 10);
        } else if (from < 0 || size < 0) {
            throw new NewException("incorrect parameters of pageable");
        } else {
            pageable = PageRequest.of(from / size, size);
        }
        return pageable;
    }
}