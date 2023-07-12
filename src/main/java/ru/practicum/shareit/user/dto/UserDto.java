package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.create.OnCreate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    @NotNull(groups = OnCreate.class)
    @NotBlank(groups = OnCreate.class)
    String name;
    @NotNull(groups = OnCreate.class)
    @Email(groups = OnCreate.class)
    String email;
}