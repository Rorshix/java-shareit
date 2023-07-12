package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    public final String error;
    public final String description;
}
