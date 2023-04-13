package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@Builder
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private Long requestId;
    private List<CommentDto> comments;
}
